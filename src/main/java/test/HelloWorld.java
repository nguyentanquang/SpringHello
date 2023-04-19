from aws_cdk import (
    # Duration,
    Stack,
    # aws_sqs as sqs,
    aws_iam as iam,
    aws_codebuild as codebuild,
    aws_codecommit as codecommit,
    aws_codepipeline as codepipeline,
    aws_codepipeline_actions as codepipeline_actions
    
)
from constructs import Construct

class CodeBuildStack(Stack):

    def __init__(self, scope: Construct, construct_id: str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)

        # Tạo CodeCommit repository
        repository = codecommit.Repository.from_repository_name(
            self, 'MyCodeCommitRepository', 'project-a')
        # Định nghĩa vai trò
        # build_role = iam.Role(
        #     self, 'MyBuildRole',
        #     assumed_by=iam.ServicePrincipal('codebuild.amazonaws.com'),
        #     description='Role for CodeBuild',
        #     role_name='MyBuildRole'
        # )

        # # Gán quyền truy cập cho vai trò
        # build_role.add_managed_policy(
        #     iam.ManagedPolicy.from_aws_managed_policy_name(
        #         'AmazonS3ReadOnlyAccess'
        #     )
        # )
        # Tạo CodeBuild project
        build_project = codebuild.Project(
            self,
            "MyProject1",
            source=codebuild.Source.code_commit(
                repository=repository,
                branch_or_ref="master",
            ),
            build_spec=codebuild.BuildSpec.from_object({
                'version': '0.2',
                'phases': {
                    'build': {
                        'commands': [
                            'mvn clean install',
                            'aws s3 cp target/*.war s3://cdk-hnb659fds-assets-800329615455-ap-southeast-1/build/project-a.war'
                        ]
                    }
                }
            })
        )

        # Create pipeline
        pipeline = codepipeline.Pipeline(
            self, "Pipeline",
            pipeline_name="MyPipeline"
        )

        # Add source stage
        source_output = codepipeline.Artifact()
        source_stage = pipeline.add_stage(
            stage_name="Source",
            actions=[
                codepipeline_actions.CodeCommitSourceAction(
                    action_name="CodeCommit_Source",
                    repository=codecommit.Repository.from_repository_name(
                        self, "CodeCommitRepo",
                        repository_name="project-a"
                    ),
                    branch="master",
                    output=source_output
                )
            ]
        )

        # Add build stage
        build_output = codepipeline.Artifact()
        build_stage = pipeline.add_stage(
            stage_name="Build",
            actions=[
                codepipeline_actions.CodeBuildAction(
                    action_name="Build",
                    project=build_project,
                    input=source_output,
                    outputs=[build_output]
                )
            ]
        )
