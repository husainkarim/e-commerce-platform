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
            parallel {
                stage('User Service') {
                    steps {
                        dir('backend/user-service') {
                            sh 'mvn clean verify'
                        }
                    }
                }
                stage('Product Service') {
                    steps {
                        dir('backend/product-service') {
                            sh 'mvn clean verify'
                        }
                    }
                }
                stage('Media Service') {
                    steps {
                        dir('backend/media-service') {
                            sh 'mvn clean verify'
                        }
                    }
                }
                stage('API Gateway') {
                    steps {
                        dir('backend/api-gateway') {
                            sh 'mvn clean verify'
                        }
                    }
                }
            }
        }

        stage('Install & Build Backend') {
            steps {
                dir('backend') {
                    sh 'make jar'
                }
            }
        }

        stage('Frontend Tests') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm test -- --watch=false --browsers=ChromeHeadlessNoSandbox'
                }
            }
        }

        stage('Deploy Application') {
            when {
                branch 'main'
            }
            steps {
                dir('backend') {
                    withCredentials([
                        string(credentialsId: 'mongo-uri-prod', variable: 'MONGODB_URI'),
                        string(credentialsId: 'gateway-jwt-secret', variable: 'JWT_SECRET'),
                        string(credentialsId: 'gateway-keystore-password', variable: 'KEYSTORE_PASSWORD')
                    ]) {
                        sh '''
                            export MONGODB_URI=$MONGODB_URI
                            export JWT_SECRET=$JWT_SECRET
                            export KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD

                            make down
                            make build
                            make up
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
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
