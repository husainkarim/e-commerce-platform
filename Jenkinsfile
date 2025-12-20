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

        stage('Backend Tests') {
            steps {
                // Change the directory to the root of the services
                dir('backend/user-service') {
                    sh 'mvn clean verify'
                }
                dir('backend/product-service') {
                    sh 'mvn clean verify'
                }
                dir('backend/media-service') {
                    sh 'mvn clean verify'
                }
                dir('backend/api-gateway') {
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Install & Build Backend') {
            steps {
                dir('backend') {
                    // 2. Build the final JAR after tests pass
                    sh 'make jar'
                }
            }
        }
        
        stage('Frontend Tests') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm test -- --watch=false --browsers=ChromeHeadlessNoSandbox'
                }
            }
        }

        stage('Deploy Application') {
            steps {
                dir('backend') withCredentials([
                    string(credentialsId: 'MONGODB_URI', variable: 'MONGODB_URI'),
                    string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                    string(credentialsId: 'KEYSTORE_PASSWORD', variable: 'KEYSTORE_PASSWORD')
                ]) {
                    // 4. Build and start all services (Backend JARs + Frontend image)
                    sh 'make down'
                    sh 'make build'  
                    sh 'make up'     
                }
            }
        }
    }

    post {
        success {
            mail to: 'husain.akarim@gmail.com',
                 subject: 'Build Success ✔',
                 body: 'The Jenkins build succeeded.'
        }
        failure {
            mail to: 'husain.akarim@gmail.com',
                 subject: 'Build Failed ❌',
                 body: 'The Jenkins build has failed — please check logs.'
        }
    }
}