def call() {
    stage('Restore Dependencies') {
        steps {
            script {
                env.HOME = sh(script: 'echo ~', returnStdout: true).trim()
                sh "dotnet restore"
            }
        }
    }
}
