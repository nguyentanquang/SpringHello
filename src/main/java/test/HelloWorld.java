import boto3

def check_directory(instance_id, directory_path):
    # Create EC2 client
    ec2 = boto3.client('ec2')

    # Describe instances
    response = ec2.describe_instances(InstanceIds=[instance_id])

    # Get instance
    instance = response['Reservations'][0]['Instances'][0]

    # Get volume ids
    volume_ids = [v['Ebs']['VolumeId'] for v in instance['BlockDeviceMappings']]

    # Create EC2 resource
    ec2_resource = boto3.resource('ec2')

    # Get volumes
    volumes = ec2_resource.volumes.filter(VolumeIds=volume_ids)

    # Check each mount target
    for volume in volumes:
        for mount_target in volume.attachments[0]['Volume']['Attachments']:
            if mount_target['Device'] == directory_path:
                return True

    return False
