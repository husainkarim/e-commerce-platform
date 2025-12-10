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
# 2. Install Google 
#---------------------------------------
# 2a. Add Google's GPG key
RUN curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /etc/apt/keyrings/google-archive.gpg

# 2b. Add the Google Chrome repository to APT sources
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/google-archive.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
    | tee /etc/apt/sources.list.d/google-chrome.list > /dev/null

# 2c. Update APT package index to include the new Google repo
RUN apt-get update

# 2d. Install Chrome Stable (This will now succeed)
RUN apt-get install -y google-chrome-stable

# Set the CHROME_BIN environment variable for Karma
ENV CHROME_BIN=/usr/bin/google-chrome

#---------------------------------------
# 3. Install Docker Engine + CLI + Compose (Existing)
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
# 4. Install NodeJS 20 (Existing)
#---------------------------------------
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

# 5. Clean up the cache to keep the image small (NEW)
RUN apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

#---------------------------------------
# 6. Configure Jenkins user for Docker access (Existing)
#---------------------------------------
# IMPORTANT: Replace 991 with your host Docker GID if needed
RUN groupadd -g 991 docker || true && \
    usermod -aG docker jenkins

# Switch back to Jenkins user
USER jenkins
WORKDIR /var/jenkins_home
EXPOSE 8080