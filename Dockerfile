FROM jenkins/jenkins:lts

USER root

# Install Node.js 20 (LTS)
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs

# Install essential build tools
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    wget \
    unzip \
    zip \
    docker.io \
    make

# Install Docker inside the Jenkins container
RUN apt-get install -y docker.io

# Install docker-compose plugin
RUN curl -SL https://github.com/docker/compose/releases/download/v2.23.1/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose \
    && chmod +x /usr/local/bin/docker-compose

# Allow Jenkins user to use Docker
RUN usermod -aG docker jenkins

USER jenkins
