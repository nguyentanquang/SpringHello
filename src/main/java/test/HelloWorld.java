class WebACLStack(core.Stack):
    def __init__(self, scope: core.Construct, id: str, **kwargs) -> None:
        super().__init__(scope, id, **kwargs)

        # Định nghĩa một IPSet để lưu các giá trị User-Agent không cho phép
        ip_set = waf.CfnIPSet(self, "UserAgentIPSet",
            addresses=["toinf"],
            ip_address_version="IPV4",
            scope=waf.Scope.REGIONAL,
            name="UserAgentIPSet",
            description="List of user agents to block"
        )

        # Định nghĩa một rule để kiểm tra User-Agent
        user_agent_rule = waf.CfnRule(self, "UserAgentRule",
            name="UserAgentRule",
            scope=waf.Scope.REGIONAL,
            priority=1,
            statement=waf.CfnRule.StatementProperty(
                byte_match_statement=waf.CfnRule.ByteMatchStatementProperty(
                    search_string="toinf",
                    search_string_base64=None,
                    field_to_match=waf.CfnRule.FieldToMatchProperty(
                        single_header=waf.CfnRule.SingleHeaderProperty(
                            name="User-Agent"
                        )
                    ),
                    positional_constraint="EXACTLY",
                    text_transformations=[
                        waf.CfnRule.TextTransformationProperty(
                            priority=0,
                            type="NONE"
                        )
                    ]
                )
            )
        )

        # Định nghĩa một rule group và thêm rule vào đó
        rule_group = waf.CfnWebACL.RuleProperty(
            name="UserAgentRule",
            priority=1,
            statement=waf.CfnWebACL.StatementOneProperty(
                byte_match_statement=waf.CfnWebACL.ByteMatchStatementProperty(
                    search_string="toinf",
                    search_string_base64=None,
                    field_to_match=waf.CfnWebACL.FieldToMatchProperty(
                        single_header=waf.CfnWebACL.SingleHeaderProperty(
                            name="User-Agent"
                        )
                    ),
                    positional_constraint="EXACTLY",
                    text_transformations=[
                        waf.CfnWebACL.TextTransformationProperty(
                            priority=0,
                            type="NONE"
                        )
                    ]
                ),
                action=waf.CfnWebACL.RuleActionProperty(
                    block={}
                )
            )
        )

        # Định nghĩa một Web ACL và thêm rule group vào đó
        web_acl = waf.CfnWebACL(self, "WebACL",
            default_action=waf.CfnWebACL.DefaultActionProperty(
                allow={}
            ),
            scope=waf.Scope.REGIONAL,
            name="WebACL",
            description="Web ACL to block requests with user agent 'toinf'",
            rules=[rule_group],
            visibility_config=waf.CfnWebACL.VisibilityConfigProperty(
                cloud_watch_metrics_enabled=True,
                sampled_requests
import boto3

def lambda_handler(event, context):
    instance_id = 'i-0123456789abcdef' # replace with your instance ID
    folder_path = '/path/to/folder' # replace with your folder path

    # Create an SSM client
    ssm = boto3.client('ssm')

    # Send the command to the instance and get the command ID
    response = ssm.send_command(
        InstanceIds=[instance_id],
        DocumentName='AWS-RunShellScript',
        Parameters={
            'commands': [
                f'[ -d {folder_path} ] && echo True || echo False'
            ]
        }
    )
    command_id = response['Command']['CommandId']

    # Get the command output
    output = ''
    while output == '':
        response = ssm.get_command_invocation(
            CommandId=command_id,
            InstanceId=instance_id
        )
        output = response.get('StandardOutputContent', '').strip()

    if output == 'True':
        print(f'The folder {folder_path} exists on instance {instance_id}')
    else:
        print(f'The folder {folder_path} does not exist on instance {instance_id}')

import boto3

# Create a boto3 Lambda client
lambda_client = boto3.client('lambda')

# Define the input payload for the Lambda function
payload = {'key1': 'value1', 'key2': 'value2'}

# Invoke the Lambda function
response = lambda_client.invoke(
    FunctionName='my-lambda-function',
    InvocationType='RequestResponse',
    Payload=json.dumps(payload)
)

# Print the response from the Lambda function
print(response['Payload'].read().decode())
