pipeline {
    agent none  

    environment {
        REPO_URL = "https://github.com/Fredericsonn/sirius-back.git"
        REMOTE_USER = "eco"
    }

    stages {

        stage('Load Secrets from Vault') {
            steps {
                script {
                    withVault([
                        vaultSecrets: [[
                            path: "jenkins/backend/${ENV}",
                            secretValues: [
                                [envVar: 'REMOTE_HOST',          vaultKey: 'REMOTE_HOST'],
                                [envVar: 'REGISTRY_URL',         vaultKey: 'REGISTRY_URL'],
                                [envVar: 'IMAGE_NAME',           vaultKey: 'IMAGE_NAME'],
                                [envVar: 'BRANCH',               vaultKey: 'BRANCH'],
                                [envVar: 'DATABASE_URL',         vaultKey: 'DATABASE_URL'],
                                [envVar: 'DATABASE_USER',        vaultKey: 'DATABASE_USER'],
                                [envVar: 'DATABASE_PASSWORD',    vaultKey: 'DATABASE_PASSWORD']
                            ]
                        ]]
                    ]) {
                        env.REMOTE_HOST        = REMOTE_HOST
                        env.REGISTRY_URL       = REGISTRY_URL
                        env.IMAGE_NAME         = IMAGE_NAME
                        env.BRANCH             = BRANCH
                        env.DATABASE_URL       = DATABASE_URL
                        env.DATABASE_USER      = DATABASE_USER
                        env.DATABASE_PASSWORD  = DATABASE_PASSWORD
                    }
                }
            }
        }

        stage('Cloning the repository') {
            agent { docker { image 'ecotracer/back-agent' } }
            steps {
                git branch: "${BRANCH}", url: "${env.REPO_URL}"
            }
        }

        stage("Build") {
            agent { docker { image 'ecotracer/back-agent' } }
            steps {
                withEnv([
                    "DATABASE_URL=${env.DATABASE_URL}",
                    "DATABASE_USER=${env.DATABASE_USER}",
                    "DATABASE_PASSWORD=${env.DATABASE_PASSWORD}"
                ]) {
                    sh 'mvn clean package'
                }
            }
        }

        stage("Archive artifact") {
            agent { docker { image 'ecotracer/back-agent' } }
            steps {
                archiveArtifacts artifacts: "target/*.jar,Dockerfile", allowEmptyArchive: false
            }
        }

        stage("Building Docker Image") {
            agent { 
                docker { 
                    image 'ecotracer/dind'
                    args '--user root --restart always -v /var/run/docker.sock:/var/run/docker.sock --entrypoint=""'
                } 
            }
            steps {
                sh 'docker build -t ${REGISTRY_URL}/${IMAGE_NAME} .'
            }
        }

        stage("Pushing Image to the registry") {
            agent { 
                docker { 
                    image 'ecotracer/dind'
                    args '--user root --restart always -v /var/run/docker.sock:/var/run/docker.sock --entrypoint=""'
                } 
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'registry', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh '''
                        docker login ${REGISTRY_URL} -u $USERNAME -p $PASSWORD
                        docker push ${REGISTRY_URL}/${IMAGE_NAME}
                    '''
                }
            }
        }

        stage("Deploy to the back server") {
            agent { 
                docker { 
                    image 'ecotracer/dind'
                    args '--user root --restart always -v /var/run/docker.sock:/var/run/docker.sock --entrypoint=""'
                } 
            }
            steps {
                sshagent(['creds']) { 
                    sh """
                        ssh -o StrictHostKeyChecking=no "${env.REMOTE_USER}"@"${REMOTE_HOST}" "docker stop backend || true && docker rm backend || true"
                        ssh -o StrictHostKeyChecking=no "${env.REMOTE_USER}"@"${REMOTE_HOST}" "docker rmi -f ${REGISTRY_URL}/${IMAGE_NAME} && docker run -d --name backend -p 8080:8080 -e DATABASE_URL=${DATABASE_URL} -e DATABASE_USER=${DATABASE_USER} -e DATABASE_PASSWORD=${DATABASE_PASSWORD} ${REGISTRY_URL}/${IMAGE_NAME}"
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment completed successfully!'
        }
        failure {
            echo 'Deployment failed. Please check the logs.'
        }
    }
}
