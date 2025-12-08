pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'Java-21'
    }
    
    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/husainkarim/e-commerce-platform.git'
            }
        }

        stage('Install & Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Start Frontend Server') {
            steps {
                sh 'pkill -f "ng serve" || true'       // stop old frontend
                sh 'nohup sh -c "cd frontend && npm start --host 0.0.0.0 --port 4200" &'       // run new build
            }
        }

        stage('Build Backend Services') {
            steps {
                dir('backend') {
                    sh 'make jar'
                    sh 'make build'
                }
            }
        }

        stage('Deploy Backend') {
            steps {
                dir('backend') {
                    sh 'make down'
                    sh 'make up'
                }
            }
        }
    }
}
