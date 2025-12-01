FROM jenkins/jenkins:lts

USER root

# Install Node.js 20 (LTS)
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs

# Install Docker inside the Jenkins container
RUN apt-get install -y docker.io

# Allow Jenkins user to use Docker
RUN usermod -aG docker jenkins

USER jenkins
