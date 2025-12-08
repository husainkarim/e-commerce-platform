FROM jenkins/jenkins:lts

#---------------------------------------
# Switch to root for installations
#---------------------------------------
USER root

# Set environment variables for non-interactive commands
ENV DEBIAN_FRONTEND=noninteractive

# 1. Install necessary tools and initial dependencies (Existing)
RUN apt-get update && \
    apt-get install -y ca-certificates curl gnupg lsb-release git unzip wget make sudo

#---------------------------------------
# 2. Install Google Chrome Headless Dependencies (NEW)
#---------------------------------------
# Install common packages required for Chrome Headless to run on Debian/Ubuntu base
RUN apt-get update && \
    apt-get install -yq --no-install-recommends \
        libasound2 libatk1.0-0 libc6 libcairo2 libcups2 libdbus-1-3 \
        libexpat1 libfontconfig1 libglib2.0-0 libgtk-3-0 libnss3 \
        libx11-6 libxcomposite1 libxcursor1 libxdamage1 libxext6 libxfixes3 \
        libxi6 libxrandr2 libxrender1 libxtst6 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
    
# 3. Download and Install Google Chrome Stable (NEW)
RUN wget -q -O /tmp/google-chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    dpkg -i /tmp/google-chrome.deb || apt-get install -f -y && \
    rm /tmp/google-chrome.deb

# Set the CHROME_BIN environment variable for Karma/Angular CLI
ENV CHROME_BIN=/usr/bin/google-chrome

#---------------------------------------
# 4. Install Docker Engine + CLI + Compose (Existing)
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
# 5. Install NodeJS 20 (Existing)
#---------------------------------------
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

# 6. Clean up the cache to keep the image small (NEW)
RUN apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

#---------------------------------------
# 7. Configure Jenkins user for Docker access (Existing)
#---------------------------------------
# IMPORTANT: Replace 991 with your host Docker GID if needed
RUN groupadd -g 991 docker || true && \
    usermod -aG docker jenkins

# Switch back to Jenkins user
USER jenkins
WORKDIR /var/jenkins_home
EXPOSE 8080