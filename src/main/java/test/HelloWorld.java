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
