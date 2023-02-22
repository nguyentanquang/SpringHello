import * as ec2 from 'aws-cdk-lib/aws-ec2'
import { Construct } from 'constructs';

interface StackProps {
  prefix: string
  cidr: string
}

/**
 * Creates a new custom VPC
 *
 * @param  {cdk.Construct} scope stack application scope
 * @param  {StackProps} props props needed to create the resource
 *
 */
export class CustomVPC {
  public readonly vpc: ec2.IVpc

  constructor(scope: Construct, props: StackProps) {
    this.vpc = new ec2.Vpc(scope, `${props.prefix}-vpc`, {
      maxAzs: 1, // RDS requires at least 2
      cidr: props.cidr, // the ip address block of the vpc e.g. '172.22.0.0/16'
      enableDnsHostnames: true,
      enableDnsSupport: true,
      natGateways: 0, // expensive -- we don't need that, yet (we have no PRIVATE subnets)
      subnetConfiguration: [
        {
          cidrMask: 22,
          name: `${props.prefix}-public-`,
          subnetType: ec2.SubnetType.PUBLIC,
        }
      ],
    })
    
  }
}

wget https://downloads.apache.org/tomcat/tomcat-9/v9.0.40/bin/apache-tomcat-9.0.40.tar.gz
# Lấy các tham số đầu vào
path=${1:-.}
group=${2:-.}
owner=${3:-.}
mode=${4:-.}

# Kiểm tra xem path là folder hay file
if [ -d "$path" ]; then
  # Nếu là folder, tạo folder
  mkdir -p $path
else
  # Nếu là file, lấy path chứa file và tạo folder
  dir=$(dirname $path)
  mkdir -p $dir
  touch $path
fi

# Thiết lập group, owner, mode cho folder hoặc file
[ "$group" != "." ] && chgrp $group $dir
[ "$owner" != "." ] && chown $owner $dir
[ "$mode" != "." ] && chmod $mode $dỉ

old_word="word1"
old_values=($old_word word2)
new_values=(replacement1 replacement2)

result=$(cat file.txt)
for i in "${!old_values[@]}"; do
    result=$(echo "$result" | sed "s/${old_values[i]}/${new_values[i]}/g")
done
echo "$result" > output.txt

<VirtualHost *:443>
  ServerName $host_name
  SSLEngine on
  SSLCertificateFile "$ssl_name"
  SSLCertificateKeyFile "$ssl_key"
  ProxyPass /path ajp://localhost:2342
  ProxyPass /path1 ajp://localhost:8123
</VirtualHost>

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
