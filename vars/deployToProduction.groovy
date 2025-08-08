def call(String toolName, String repositoryName, String cluster, String serviceName) {
    stage('Deploy to Production') {
        when {
            allOf {
                expression { params.deployToProd == true }
                expression { currentBuild.result == null }
            }
        }
        steps {
            echo "Deploying to production..."
            script {
                def lowerToolName = toolName.toLowerCase()
                def dockerImageName = "patseer.service.${lowerToolName}:${BUILD_NUMBER}"
                def PdockerImageName = "patseer.service.${lowerToolName}_arm:${BUILD_NUMBER}"
                def PregistryImageName = "${env.LOCAL_REGISTRY}/${PdockerImageName}"
                def REGION = 'us-east-1'
                def FAMILY = sh(script: 'sed -n \'s/.*"family": "\\(.*\\)",/\\1/p\' taskdef.json', returnStdout: true).trim()
                sh 'env'
                sh 'aws configure list'
                def REPOSITORY_URI = sh(script: "aws ecr describe-repositories --repository-names ${repositoryName} --region ${REGION} | jq -r '.repositories[].repositoryUri'", returnStdout: true).trim()
                sh "docker tag ${PregistryImageName} ${REPOSITORY_URI}:v_${BUILD_NUMBER}"
                sh "aws ecr get-login-password --region ${REGION} | docker login --username AWS --password-stdin 801436036474.dkr.ecr.us-east-1.amazonaws.com"
                sh "docker push ${REPOSITORY_URI}:v_${BUILD_NUMBER}"
                sh "sed -e \"s;%BUILD_NUMBER%;${BUILD_NUMBER};g\" -e \"s;%REPOSITORY_URI%;${REPOSITORY_URI};g\" taskdef.json > ${FAMILY}-v_${BUILD_NUMBER}.json"
                sh "aws ecs register-task-definition --family ${FAMILY} --cli-input-json file://${WORKSPACE}/${FAMILY}-v_${BUILD_NUMBER}.json --region ${REGION}"
                def REVISION = sh(script: "aws ecs describe-task-definition --task-definition ${FAMILY} --query 'taskDefinition.revision'", returnStdout: true).trim()
                sh "aws ecs update-service --cluster ${cluster} --region ${REGION} --service ${serviceName} --task-definition ${FAMILY}:${REVISION} --desired-count 1"
                echo 'THANKYOU FROM PATSEER ;-)'
            }
        }
    }
}
