pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'Java-21'
    }

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['local','prod'], description: 'Select environment')
    }

    environment {
        DOCKER_HOST = "unix:///var/run/docker.sock"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/husainkarim/e-commerce-platform.git'
            }
        }

        /* -----------------------
           BACKEND BUILD & TEST
        ------------------------*/
        stage('Build Backend Microservices') {
            steps {
                sh '''
                    cd backend
                    make jar
                    docker compose build
                '''
            }
        }

        /* -----------------------
           FRONTEND BUILD & TEST
        ------------------------*/
        stage('Build Angular Frontend') {
            steps {
                sh '''
                    cd frontend
                    npm install
                    npm run test -- --watch=false --browsers=ChromeHeadless
                    npm run build --prod
                '''
            }
        }

        /* -----------------------
           DEPLOYMENT
        ------------------------*/
        stage('Deploy to Local Server') {
            when {
                expression { params.DEPLOY_ENV == 'local' }
            }
            steps {
                sh '''
                    echo "Deploying with Docker Compose..."
                    docker compose down
                    docker compose up -d
                '''
            }
        }
    }

    /* -----------------------
       NOTIFICATIONS
    ------------------------*/
    post {
        success {
            echo "Build Success!"
            // Slack example
            // slackSend channel: '#builds', message: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
        failure {
            echo "Build Failed!"
            // slackSend channel: '#builds', message: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
    }
}
