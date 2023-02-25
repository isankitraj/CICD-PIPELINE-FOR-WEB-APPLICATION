def Build(){
    echo "Building the application..."
    withCredentials([usernamePassword(credentialsId: 'dockerhub-id-pass', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
        sh 'docker login -u $DOCKER_USER -p $DOCKER_PASS'
        sh "docker build -t mywebsite:${env.TAG} ."
        sh "docker tag mywebsite:${env.TAG} ankitraz/mywebsite:${env.TAG}"
        /* if you want to push to dockerhub you need to build image with your repo name and tag.
        otherwise you"ll have to tag image manually like i did above.*/
        sh "docker push ankitraz/mywebsite:${env.TAG}"
    }
}

def Test(){
    echo "Testing the application..."
}

def Deploy(){
    echo "Deploying the application..."
    def cd = "cd /root/new"
    def dockerCmd = "TAG=${env.TAG} docker-compose up -d"
    
    sshagent(['github-ssh-key']) {
        //copying docker-compose.yml file to remote server
        sh "scp docker-compose.yml root@64.227.108.131:/root/new"
        sh "ssh -o StrictHostKeyChecking=no root@64.227.108.131 '${cd};${dockerCmd}'"
        
    }
}

return this