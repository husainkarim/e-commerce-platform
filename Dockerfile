FROM jenkins/jenkins:lts

#---------------------------------------
# Switch to root for installations
#---------------------------------------
USER root

# Install necessary tools
RUN apt-get update && \
    apt-get install -y ca-certificates curl gnupg lsb-release git unzip wget make sudo

#---------------------------------------
# Install Docker Engine + CLI + Compose
#---------------------------------------
RUN install -m 0755 -d /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
    https://download.docker.com/linux/debian $(lsb_release -cs) stable" \
    | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && \
    apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Ensure docker-compose is accessible
RUN mkdir -p /usr/libexec/docker/cli-plugins && \
    ln -s /usr/libexec/docker/cli-plugins/docker-compose /usr/local/bin/docker-compose || true

#---------------------------------------
# Install NodeJS 20
#---------------------------------------
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

#---------------------------------------
# Configure Jenkins user for Docker access
#---------------------------------------
# IMPORTANT: Replace 991 with your host Docker GID if needed
RUN groupadd -g 991 docker || true && \
    usermod -aG docker jenkins

# Switch back to Jenkins user
USER jenkins
WORKDIR /var/jenkins_home
EXPOSE 8080
