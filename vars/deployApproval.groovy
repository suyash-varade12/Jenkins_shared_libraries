def call() {
    stage('Deploy approval') {
        when {
            allOf {
                expression { params.deployToProd == true }
                expression { currentBuild.result == null }
            }
        }
        steps {
            script {
                timeout(time: 7, unit: 'DAYS') {
                    input message: "Deploy to production? Press 'Proceed' to continue or 'Abort' to stop."
                }
            }
        }
    }
}
