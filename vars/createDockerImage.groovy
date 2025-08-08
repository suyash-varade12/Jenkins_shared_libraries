def call(String toolName, String localRegistry) {
    stage('Create Docker Image') {
        steps {
            script {
                def lowerToolName = toolName.toLowerCase()
                def dockerImageName = "patseer.service.${lowerToolName}:${BUILD_NUMBER}"
                def PdockerImageName = "patseer.service.${lowerToolName}_arm:${BUILD_NUMBER}"
                sh "docker build -t ${dockerImageName} ."
                def registryImageName = "${localRegistry}/${dockerImageName}"
                def PregistryImageName = "${localRegistry}/${PdockerImageName}"
                sh "docker tag ${dockerImageName} ${registryImageName}"
                sh "docker push ${registryImageName}"
                sh "docker buildx build --load --platform=linux/arm64 -t ${PdockerImageName} ."
                sh "docker tag ${PdockerImageName} ${PregistryImageName}"
                sh "docker push ${PregistryImageName}"
                sh "ssh root@192.168.1.19 'docker ps -a --filter \"name=patseer.service.${lowerToolName}\" --format \"{{.ID}}\" | xargs -r docker stop || true && docker ps -a --filter \"name=patseer.service.${lowerToolName}\" --format \"{{.ID}}\" | xargs -r docker rm || true'"
                sh """
                ssh root@192.168.1.19 \\
                  "docker run -d -p 42780:5000 --env ASPNETCORE_ENVIRONMENT=Staging0-112 \\
                  --name=patseer.service.${lowerToolName} --restart=always ${registryImageName}"
                """
            }
        }
    }
}
