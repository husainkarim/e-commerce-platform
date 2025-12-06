FROM jenkins/jenkins:lts

# Switch to root for installations
USER root

# ----------------------------------------
# Install basic required tools
# ----------------------------------------
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    wget \
    make \
    unzip \
    zip \
    build-essential \
    docker.io \
    git \
    ca-certificates \
    gnupg \
    lsb-release \
 && rm -rf /var/lib/apt/lists/*

# ----------------------------------------
# Install NodeJS 20
# ----------------------------------------
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
 && apt-get install -y nodejs \
 && rm -rf /var/lib/apt/lists/*

# ----------------------------------------
# Install Docker Compose v2
# ----------------------------------------
RUN curl -SL https://github.com/docker/compose/releases/download/v2.23.1/docker-compose-linux-x86_64 \
    -o /usr/local/bin/docker-compose \
 && chmod +x /usr/local/bin/docker-compose

# ----------------------------------------
# Add Jenkins user to Docker group
# ----------------------------------------
RUN groupadd -g 999 docker || true \
 && usermod -aG docker jenkins

# ----------------------------------------
# Optional: allow Jenkins to run sudo without password (not needed if using Docker group)
# ----------------------------------------
# RUN echo "jenkins ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

# Switch back to Jenkins user
USER jenkins

# Set working directory
WORKDIR /var/jenkins_home

# Expose Jenkins port
EXPOSE 8080
