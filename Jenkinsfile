pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'Java-21'
    }

    environment {
        GCP_KEY_FILE = '/var/jenkins_home/keys/serviceAccountKey.json'
        NEXUS_URL = "nexus:8081"
        NEXUS_PUSH_REGISTRY = "nexus:5000" // For pushing your images
        NEXUS_PULL_REGISTRY = "nexus:5001" // For pulling base images
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Initialize GitHub Status') {
            steps {
                // Set status to PENDING as soon as the build starts
                withCredentials([string(credentialsId: 'github-token', variable: 'TOKEN')]) {
                    sh """
                        curl -H "Authorization: token ${TOKEN}" \
                             -H "Content-Type: application/json" \
                             -X POST \
                             -d '{"state": "pending", "context": "Jenkins CI/SafeZone", "description": "Build is in progress...", "target_url": "${env.BUILD_URL}"}' \
                             https://api.github.com/repos/husainkarim/e-commerce-platform/statuses/${env.GIT_COMMIT}
                    """
                }
            }
        }
        // backend tests
        // stage('Backend Tests') {
        //     stages {
        //         stage('User Service') {
        //             steps {
        //                 dir('backend/user-service') {
        //                     sh 'mvn clean verify'
        //                 }
        //             }
        //         }
        //         stage('Product Service') {
        //             steps {
        //                 dir('backend/product-service') {
        //                     sh 'mvn clean verify'
        //                 }
        //             }
        //         }
        //         stage('Media Service') {
        //             steps {
        //                 dir('backend/media-service') {
        //                     sh 'mvn clean verify'
        //                 }
        //             }
        //         }
        //         stage('Order Service') {
        //             steps {
        //                 dir('backend/order-service') {
        //                     sh 'mvn clean verify'
        //                 }
        //             }
        //         }
        //         stage('API Gateway') {
        //             steps {
        //                 dir('backend/api-gateway') {
        //                     sh 'mvn clean verify'
        //                 }
        //             }
        //         }
        //     }
        // }
        // frontend tests
        // stage('Frontend Tests') {
        //     steps {
        //         dir('frontend') {
        //             sh 'npm ci'
        //             sh 'npm test -- --watch=false --browsers=ChromeHeadlessNoSandbox --code-coverage'
        //         }
        //     }
        // }
        // SonarQube analysis for both backend and frontend
        stage('SonarQube Analysis') {
            steps {
                // 'SonarQube' must match the name you give in Jenkins Global Configuration
                withSonarQubeEnv('SonarQube') { 
                    script {
                        // 1. Backend Microservices Analysis
                        def services = ['user-service', 'product-service', 'media-service', 'order-service', 'api-gateway']
                        services.each { service ->
                            dir("backend/${service}") {
                                sh "mvn clean package"  // compile & run tests + generate jacoco
                                sh "mvn sonar:sonar -Dsonar.projectKey=${service} -Dsonar.projectName=${service}"
                            }
                        }

                        // 2. Frontend Analysis
                        dir('frontend') {
                            // Ensure coverage is generated before this if you want it in Sonar
                            // Use npx to run the scanner without manual installation
                            sh "npm install sonar-scanner --save-dev --registry=https://registry.npmjs.org/"
    
                            sh "node_modules/.bin/sonar-scanner \
                                -Dsonar.projectKey=ecommerce-frontend \
                                -Dsonar.sources=src \
                                -Dsonar.javascript.lcov.reportPaths=coverage/frontend/lcov.info \
                                -Dsonar.typescript.lcov.reportPaths=coverage/frontend/lcov.info \
                                -Dsonar.host.url=${SONAR_HOST_URL} \
                                -Dsonar.login=${SONAR_AUTH_TOKEN}"
                        }
                    }
                }
            }
        }
        // This stage will wait for SonarQube to process the analysis and return the quality gate status via webhook
        stage("Quality Gate") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    // Jenkins will pause here until SonarQube sends a webhook back
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Publish Artifacts to Nexus') {
            steps {
                configFileProvider([configFile(fileId: 'my-nexus-settings', variable: 'MAVEN_SETTINGS')]) {
                    script {
                        def services = ['user-service', 'product-service', 'media-service', 'order-service', 'api-gateway']
                        services.each { service ->
                            dir("backend/${service}") {
                                // Requirement #3: Artifact Publishing
                                // Requirement #5: Versioning (retrieved from pom.xml)
                                sh "mvn -s ${MAVEN_SETTINGS} deploy -DskipTests"
                            }
                        }
                    }
                }
            }
        }

        stage('Docker Build & Push to Nexus') {
            steps {
                script {
                    // 1. Login to the PULLING registry first so the 'FROM' command works
                    docker.withRegistry("http://${NEXUS_PULL_REGISTRY}", 'nexus-credentials-id') {
                        
                        // 2. Wrap the build/push in the PUSHING registry context
                        docker.withRegistry("http://${NEXUS_PUSH_REGISTRY}", 'nexus-credentials-id') {
                            
                            def services = ['user-service', 'product-service', 'media-service', 'order-service', 'api-gateway']
                            services.each { service ->
                                dir("backend/${service}") {
                                    // This build now has access to nexus:5001 for the 'FROM' line
                                    // and will tag it for nexus:5000 for the 'push'
                                    def appImage = docker.build("${NEXUS_PUSH_REGISTRY}/${service}:${env.BUILD_NUMBER}")
                                    appImage.push()
                                    appImage.push("latest")
                                }
                            }

                            dir('frontend') {
                                def feImage = docker.build("${NEXUS_PUSH_REGISTRY}/ecommerce-frontend:${env.BUILD_NUMBER}")
                                feImage.push()
                                feImage.push("latest")
                            }
                        }
                    }
                }
            }
        }
        //
        stage('Install & Build & Deploy Application') {
            steps {
                dir('backend') {
                    withCredentials([
                        string(credentialsId: 'MONGODB_URI', variable: 'MONGODB_URI'),
                        string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                        string(credentialsId: 'KEYSTORE_PASSWORD', variable: 'KEYSTORE_PASSWORD'),
                        file(credentialsId: 'media-service-gcp-key', variable: 'GCP_KEY_FILE')
                    ]) {
                        sh '''
                            # 1. Fix Firebase: Copy the secret file into the resources folder before building the JAR
                            cp "$GCP_KEY_FILE" media-service/src/main/resources/serviceAccountKey.json
                            
                            # 2. Fix Kafka: Clean up the "accidental directory" if it exists from a failed run
                            # This prevents the [Errno 21] error
                            if [ -d "../kafka-config.properties" ]; then
                                rm -rf "../kafka-config.properties"
                            fi
                            export MONGODB_URI=$MONGODB_URI
                            export JWT_SECRET=$JWT_SECRET
                            export KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD
                            make jar
                            make down
                            make up
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs() // Good: Prevents disk space issues and "dirty" builds
        }
        success {
            withCredentials([string(credentialsId: 'github-token', variable: 'TOKEN')]) {
                sh """
                    curl -H "Authorization: token ${TOKEN}" \
                         -H "Content-Type: application/json" \
                         -X POST \
                         -d '{"state": "success", "context": "Jenkins CI/SafeZone", "description": "Build Succeeded!", "target_url": "${env.BUILD_URL}"}' \
                         https://api.github.com/repos/husainkarim/e-commerce-platform/statuses/${env.GIT_COMMIT}
                """
            }
            
            mail to: 'husain.akarim@gmail.com',
                 subject: "SUCCESS: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                 body: "Great news! The build passed all quality checks. Review it here: ${env.BUILD_URL}"
        }
        failure {
            withCredentials([string(credentialsId: 'github-token', variable: 'TOKEN')]) {
                sh """
                    curl -H "Authorization: token ${TOKEN}" \
                         -H "Content-Type: application/json" \
                         -X POST \
                         -d '{"state": "failure", "context": "Jenkins CI/SafeZone", "description": "Build Failed!", "target_url": "${env.BUILD_URL}"}' \
                         https://api.github.com/repos/husainkarim/e-commerce-platform/statuses/${env.GIT_COMMIT}
                """
            }
            
            mail to: 'husain.akarim@gmail.com',
                 subject: "FAILURE: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                 body: "The build or security scan failed. Please check the logs immediately: ${env.BUILD_URL}"
        }
    }
}
