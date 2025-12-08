pipeline {
    agent any
    
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
                sh 'cd frontend && npm start &'       // run new build
            }
        }

        stage('Build Backend Services') {
            steps {
                sh 'docker compose -f backend/docker-compose.yml build'
            }
        }

        stage('Deploy Backend') {
            steps {
                sh 'docker compose -f backend/docker-compose.yml down'
                sh 'docker compose -f backend/docker-compose.yml up -d'
            }
        }
    }
}
