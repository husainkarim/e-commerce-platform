pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'Java-21'
    }
    
    stages {

        // stage('Checkout Code') {
        //     steps {
        //         git branch: 'main', url: 'https://github.com/husainkarim/e-commerce-platform.git'
        //     }
        // }

        stage('Clean Workspace') {
            steps {
                echo 'Cleaning up previous build artifacts...'
                
                // Define the service directories you need to clean
                def serviceDirs = ['user-service', 'product-service', 'media-service', 'api-gateway'] // <-- Adjust this list to match your actual service folders
                
                // Iterate through each service folder and remove the 'target' directory
                dir('backend') {
                    script {
                        for (dir in serviceDirs) {
                            sh "rm -rf ${dir}/target || true" // '|| true' ensures the pipeline doesn't fail if the directory doesn't exist
                            echo "Cleaned target directory in: ${dir}"
                        }
                    }
                }
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
                    // 3. Install dependencies and run Angular unit tests using a headless browser.
                    // This runs inside the pipeline's agent (Jenkins container).
                    sh 'npm install'
                    sh 'npm test -- --watch=false --browsers=ChromeHeadless'
                }
            }
        }

        stage('Deploy Application') {
            steps {
                dir('backend') {
                    // 4. Build and start all services (Backend JARs + Frontend image)
                    sh 'make down'
                    sh 'make build'  
                    sh 'make up'     
                }
            }
        }
        
        stage('E2E Testing') {
            steps {
                // 5. Test the whole system once all containers are running and stable.
                sh 'sleep 30' // Give services time to boot (Kafka, DB, Backend)
                dir('frontend') {
                    // Assuming you use Cypress/Playwright and the npm script is defined
                    sh 'npm run e2e' 
                }
            }
        }
    }
}