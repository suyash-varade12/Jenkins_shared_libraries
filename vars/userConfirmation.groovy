def call() {
    stage('User Confirmation') {
        when { expression { params.deployToProd == false } }
        steps {
            script {
                echo 'Deployment to production aborted by user'
                currentBuild.result = 'SUCCESS'
            }
        }
    }
}
