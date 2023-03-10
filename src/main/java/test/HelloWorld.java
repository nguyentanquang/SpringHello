stages:          # List of stages for jobs, and their order of execution
  - build
  - tranfer-war
  - deploy-war

build-job:
  stage: build
  image: maven:3.8.3-openjdk-17
  script:
    - echo "Compiling the code..."
    - mvn clean install
    - mv ./target/*.war $CI_PROJECT_DIR/SAMPLE_TEST.war
    - echo "Compile complete."
  artifacts:
    paths:
      - SAMPLE_TEST.war

tranfer-war-job:
  stage: tranfer-war
  image: python:latest
  before_script:
    - pip install awscli
    - aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
    - aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
    - aws configure set region $AWS_DEFAULT_REGION
  script:
    - echo "Getting the war file..."
    - ls -l $CI_PROJECT_DIR
    - aws s3 cp $CI_PROJECT_DIR/SAMPLE_TEST.war s3://cdk-hnb659fds-assets-800329615455-us-east-1/
  dependencies:
    - build-job

deploy-war-job:
  stage: deploy-war
  image: python:latest
  variables:
    AWS_ACCESS_KEY_ID: $AWS_ACCESS_KEY_ID
    AWS_SECRET_ACCESS_KEY: $AWS_SECRET_ACCESS_KEY
    AWS_DEFAULT_REGION: $AWS_DEFAULT_REGION
    SSM_COMMAND: "sudo su - && /opt/apache-tomcat-test/bin/shutdown.sh && aws s3 cp s3://cdk-hnb659fds-assets-800329615455-us-east-1/SAMPLE_TEST.war /opt/apache-tomcat-test/webapps/ && /opt/apache-tomcat-test/bin/startup.sh"
    SSM_INSTANCE_IDS: "i-0b995cf10a186cefd"
  before_script:
    - pip install awscli
  script:
    - aws ssm send-command --document-name "AWS-RunShellScript" --parameters commands="$SSM_COMMAND" --instance-ids "$SSM_INSTANCE_IDS"
  dependencies:
    - tranfer-war-job

import boto3
import time

# Khởi tạo các client cho Elastic Load Balancer và EC2
elbv2_client = boto3.client('elbv2')
ec2_client = boto3.client('ec2')

# ID của instance EC2
instance_id = 'i-0123456789abcdef'

# ID của target group
target_group_arn = 'arn:aws:elasticloadbalancing:us-west-2:111122223333:targetgroup/my-target-group/1234567890abcdef'

# Loại bỏ instance EC2 khỏi target group
elbv2_client.deregister_targets(
    TargetGroupArn=target_group_arn,
    Targets=[
        {
            'Id': instance_id,
        },
    ]
)

# Chờ đợi cho đến khi không còn request nào trên instance EC2
while True:
    response = elbv2_client.describe_target_health(
        TargetGroupArn=target_group_arn,
        Targets=[
            {
                'Id': instance_id,
            },
        ]
    )
    target_health = response['TargetHealthDescriptions'][0]['TargetHealth']
    if target_health['State'] == 'draining':
        if target_health['RequestCount'] == 0:
            break
        else:
            time.sleep(10)
    else:
        raise Exception('Instance is not in draining state')

# Khởi động lại Tomcat trên instance EC2
# (sử dụng Lambda function hoặc kết nối đến instance EC2 để khởi động lại Tomcat)

# Đăng ký lại instance EC2 vào target group
elbv2_client.register_targets(
    TargetGroupArn=target_group_arn,
    Targets=[
        {
            'Id': instance_id,
        },
    ]
)

import psutil

service_name = "myservice"

# Tìm kiếm các process có tên chứa service_name
processes = list(filter(lambda p: service_name in p.name(), psutil.process_iter()))

# Kiểm tra xem có ít nhất một process match với service_name hay không
if len(processes) > 0:
    print(f"Service {service_name} đã start thành công!")
else:
    print(f"Service {service_name} chưa start hoặc start không thành công.")

import boto3
import time

elbv2 = boto3.client('elbv2')
ec2 = boto3.client('ec2')
ssm = boto3.client('ssm')

def remove_instance_from_target_group(target_group_arn, instance_id):
    elbv2.deregister_targets(
        TargetGroupArn=target_group_arn,
        Targets=[{
            'Id': instance_id,
        }],
    )
    
    return True

def restart_tomcat_service(instance_id, tomcat_service_name):
    response = ssm.send_command(
        InstanceIds=[instance_id],
        DocumentName='AWS-RunShellScript',
        Parameters={
            'commands': [
                f'sudo service {tomcat_service_name} restart'
            ]
        }
    )
    
    command_id = response['Command']['CommandId']
    
    return command_id

def add_instance_to_target_group(target_group_arn, instance_id):
    elbv2.register_targets(
        TargetGroupArn=target_group_arn,
        Targets=[{
            'Id': instance_id,
        }],
    )
    
    return True

def lambda_handler(event, context):
    target_group_arn = 'arn:aws:elasticloadbalancing:us-west-2:123456789012:targetgroup/my-target-group/abcdef1234567890'
    instance_id = 'i-01234567890abcdef'
    tomcat_service_name = 'tomcat8'
    max_attempts = 30
    drain_timeout_seconds = 300
    
    attempts = 0
    
    while True:
        instance_state = get_instance_state(target_group_arn, instance_id)
        
        if instance_state == 'InService':
            set_instance_health(target_group_arn, instance_id, 'Unhealthy')
            
            while attempts < max_attempts:
                time.sleep(drain_timeout_seconds / max_attempts)
                
                if check_instance_requests(instance_id):
                    break
                else:
                    attempts += 1
            
            if attempts == max_attempts:
                return {
                    'statusCode': 500,
                    'body': 'Instance did not complete requests in time'
                }
            
            restart_tomcat_service(instance_id, tomcat_service_name)
            
            new_instance_id = add_instance_to_target_group(target_group_arn)
            
            break
        
        elif instance_state == 'Initial':
            time.sleep(5)
            continue
        
        else:
            return {
                'statusCode': 500,
                'body': f'Instance in unexpected state: {instance_state}'
            }
        
    return {
        'statusCode': 200,
        'body': f'Instance {instance_id} removed and {new_instance_id} added to Target Group'
    }

import sys
import json

def get_item_value(json_file, item_path):
    with open(json_file) as f:
        data = json.load(f)
        item_path_list = item_path.split(".")
        for item in item_path_list:
            data = data.get(item, {})
        return data

json_file = sys.argv[1]
item_path = sys.argv[2]
result = get_item_value(json_file, item_path)
print(result)


#!/bin/bash
#!/bin/bash
import json

def get_json_value(file_path, field_name):
    with open(file_path) as f:
        data = json.load(f)
        return data.get(field_name)
function parse_json {
  local json_file=$1
  local field_name=$2
  local value=$(grep -oE "\"$field_name\":\"[^\"]+\"" $json_file | awk -F":" '{print $2}' | tr -d '"')
  echo "Value of $field_name is: $value"
}
# Hàm để lấy giá trị item cấp sâu trong JSON
get_json_value() {
  local json=$1
  local item=$2
  local IFS='.'
  local -a keys=($item)
  local value=''
  
  for key in "${keys[@]}"; do
    if [[ $json =~ \"$key\":\"(.*?)\", ]]; then
      value=${BASH_REMATCH[1]}
      json=${json#*\"$key\":\"$value\",}
    elif [[ $json =~ \"$key\":(.*?), ]]; then
      value=${BASH_REMATCH[1]}
      json=${json#*\"$key\":$value,}
    fi
  done
  
  echo "$value"
}

# Sử dụng hàm
json='{"a": {"b": {"c": "value"}}}'
item="a.b.c"
value=$(get_json_value "$json" "$item")

echo "Giá trị của item 

#!/bin/bash

# Function to find a value of an item in the config file
function find() {
  local item=$1
  local value=$(grep $item config | cut -d ':' -f2)
  echo $value
}

# Create the config file with name-value pairs
echo "item1:value1" > config
echo "item2:value2" >> config
echo "item3:value3" >> config

# Call the find function and pass in the item name as an argument
result=$(find "item2")
echo "Value of item2: $result"


function parse_json {
  local json_file=$1
  local field_name=$2
  local value=$(grep -oE "\"$field_name\":\"[^\"]+\"" $json_file | awk -F":" '{print $2}' | tr -d '"')
  echo "Value of $field_name is: $value"
}

parse_json "file.json" "field_name"

if [ "$(lsb_release -rs | cut -d. -f1)" == "446" ]; then
  yum install -y postgreffsql93
  yum remove -y postgresfffql-libs
fi
Lệnh lsb_release -rs sẽ trả về phiên bản hệ điều hành của bạn và cut -d. -f1 sẽ cắt chuỗi để chỉ giữ lại phần số nguyên đầu tiên của phiên bản.

if yum list installed | grep 'oracle-7' > /dev/null; then
  echo "oracle-7 package is installed"
else
  echo "oracle-7 package is not installed"
fi
