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
                    // 1. Install necessary dependencies for Chrome Headless on Debian/Ubuntu base images
                    sh 'apt-get update && apt-get install -yq gconf-service libasound2 libatk1.0-0 libc6 libcairo2 libcups2 libdbus-1-3 libexpat1 libfontconfig1 libgcc1 libgconf-2-4 libgdk-pixbuf2.0-0 libglib2.0-0 libgtk-3-0 libnspr4 libpango-1.0-0 libpangocairo-1.0-0 libstdc++6 libx11-6 libx11-xcb1 libxcb1 libxcomposite1 libxcursor1 libxdamage1 libxext6 libxfixes3 libxi6 libxrandr2 libxrender1 libxss1 libxtst6 ca-certificates fonts-liberation libappindicator1 libnss3 lsb-release xdg-utils wget'
                    
                    // 2. Install Google Chrome (replace with curl/wget method if needed)
                    sh 'wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb'
                    sh 'dpkg -i google-chrome-stable_current_amd64.deb || true'
                    sh 'apt-get install -f -y' // Fix broken dependencies
                    
                    // 3. Run tests
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