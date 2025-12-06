pipeline {
    // 1. Agent should specify where Docker tools are available, or use the node configured with Docker capabilities
    agent any

    // 2. Tools should be configured globally in Jenkins, and simply referenced here.
    tools {
        // These tools will be automatically added to the PATH for subsequent shell steps.
        maven 'Maven-3.9'
        jdk 'Java-21' 
    }

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['local', 'prod'], description: 'Select environment')
    }

    environment {
        // DOCKER_HOST is usually not needed when binding the host socket via docker-compose, 
        // but keeping it here for safety if required by a build agent.
        DOCKER_HOST = "unix:///var/run/docker.sock"
        // Define the expected path for the Docker executable to mitigate PATH issues (Exit Code 127)
        PATH = "/usr/bin:/usr/local/bin:${env.PATH}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/husainkarim/e-commerce-platform.git'
            }
        }

        /* -----------------------
           BACKEND BUILD, TEST & DEPLOY
        ------------------------*/
        stage('Build & Test Backend') {
            steps {
                // Use 'dir' to change directory and ensure correct context for 'make' and 'docker compose'
                dir('backend') { 
                    // 3. Clean and Package the JAR (The 'make jar' command is assumed to do this)
                    sh 'make jar' 

                    // 4. Build Docker Images (no hyphen for modern V2 Compose)
                    // The image build should happen *before* tests.
                    sh 'docker compose build'
                }
            }
        }
        
        stage('Test Backend Microservices') {
            steps {
                // Run integration tests using Docker Compose
                dir('backend') {
                    // This command should start the backend, database, and run tests,
                    // exiting with an error code if tests fail.
                    sh 'docker compose run --rm backend-service mvn test' 
                }
                // 5. Post-step to publish JUnit results for Jenkins reporting
                junit '**/backend/target/surefire-reports/*.xml' 
            }
        }

        /* -----------------------
           FRONTEND BUILD & TEST
        ------------------------*/
        stage('Test Angular Frontend') {
            steps {
                dir('frontend') {
                    // 6. Use 'npm ci' for clean, consistent CI installs
                    sh 'npm ci' 
                    // 7. Test command: The exit code must fail the stage if tests fail.
                    sh 'npm run test -- --watch=false --browsers=ChromeHeadless'
                }
                // Post-step to publish test results (assuming Karma is configured to output JUnit XML)
                junit '**/frontend/junit.xml' 
            }
        }
        
        stage('Build Angular Frontend') {
            steps {
                dir('frontend') {
                    // Build the production assets
                    sh 'npm run build -- --configuration=production'
                    // TODO: Add step to build frontend Docker image here if applicable
                }
            }
        }

        /* -----------------------
           DEPLOYMENT
        ------------------------*/
        stage('Deploy Application') {
            when {
                // 8. Only deploy if the build result is stable/success
                expression { 
                    return currentBuild.result == null || currentBuild.result == 'SUCCESS' 
                }
            }
            steps {
                // 9. Use the DEPLOY_ENV parameter to select the deployment file/strategy
                script {
                    def composeFile = "docker-compose-${params.DEPLOY_ENV}.yml"
                    echo "Deploying to ${params.DEPLOY_ENV} using ${composeFile}..."

                    // Deploy the application using the environment-specific compose file
                    sh "docker compose -f backend/${composeFile} up -d"
                }
            }
        }
    }

    /* -----------------------
       NOTIFICATIONS
    ------------------------*/
    post {
        always {
            // 10. Clean up Docker containers after every run
            sh 'docker compose down'
        }
        success {
            echo "✅ Build Success! App deployed to ${params.DEPLOY_ENV}."
            // slackSend channel: '#builds', message: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
        unstable { // Tests failed, but code built.
            echo "⚠️ Build Unstable: Tests Failed. Deployment Skipped."
        }
        failure {
            echo "❌ Build Failed! Check logs for errors."
            // Rollback strategy goes here.
            // slackSend channel: '#builds', message: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
    }
}