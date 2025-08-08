def call() {
    stage('Trigger Other Job') {
        agent { label 'Service_Deploy' }
        steps {
            build job: 'PatseerServices'
        }
    }
}
