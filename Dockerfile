FROM jenkins/jenkins:lts

# Switch to root for installations
USER root

# Install basic required tools
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    make \
    unzip \
    zip \
    build-essential \
    sudo

# ----------------------------------------
# Install NodeJS 20
# ----------------------------------------
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

# ----------------------------------------
# Install Docker + give access to Jenkins
# ----------------------------------------
RUN apt-get install -y docker.io && \
    usermod -aG docker jenkins

# ----------------------------------------
# Install Docker Compose v2
# ----------------------------------------
RUN curl -SL https://github.com/docker/compose/releases/download/v2.23.1/docker-compose-linux-x86_64 \
    -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# OPTIONAL — allow sudo without password (so Jenkins can use `sudo docker`)
RUN echo "jenkins ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

# Switch back to Jenkins user
USER jenkins
