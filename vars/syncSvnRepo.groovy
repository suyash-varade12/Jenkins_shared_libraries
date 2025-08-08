def call() {
    stage('Sync SVN Repository') {
        agent { label "SVN" }
        steps {
            script {
                sh 'svnsync synchronize file:///mnt/svn_disk/important_repos/PatseerApp'
            }
        }
    }
}
