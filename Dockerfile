FROM jenkins/jenkins:lts

# Switch to root for installations
USER root

# ----------------------------------------
# 1. Install Basic Required Tools & Docker Client
# ----------------------------------------
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
    curl \
    wget \
    make \
    unzip \
    zip \
    build-essential \
    # Install Docker client/CLI:
    docker.io \
    git \
    ca-certificates \
    gnupg \
    lsb-release

# ----------------------------------------
# 2. Install NodeJS 20
# ----------------------------------------
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y nodejs

# ----------------------------------------
# 3. Install Docker Compose v2 (as a plugin for 'docker compose')
# ----------------------------------------
RUN DOCKER_CLI_PLUGINS_DIR=/usr/local/lib/docker/cli-plugins && \
    mkdir -p ${DOCKER_CLI_PLUGINS_DIR} && \
    curl -SL https://github.com/docker/compose/releases/download/v2.23.1/docker-compose-linux-x86_64 \
    -o ${DOCKER_CLI_PLUGINS_DIR}/docker-compose && \
    chmod +x ${DOCKER_CLI_PLUGINS_DIR}/docker-compose

# ----------------------------------------
# 4. Configure Jenkins User and Cleanup
# ----------------------------------------
# Add Jenkins user to the Docker group
# (This grants permission to the mounted /var/run/docker.sock)
RUN groupadd -g 999 docker || true && \
    usermod -aG docker jenkins && \
    # Final cleanup of package lists
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Switch back to Jenkins user
USER jenkins

# Set working directory
WORKDIR /var/jenkins_home

# Expose Jenkins port
EXPOSE 8080