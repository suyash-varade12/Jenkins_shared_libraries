def call() {
    stage('Dotnet Publish') {
        steps {
            sh "dotnet publish -c Release -o out"
        }
    }
}
