# Stage 1: Build the Angular application
FROM node:20-alpine AS build
WORKDIR /app

# Tell Puppeteer NOT to download Chromium during npm ci
ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true
ENV NODE_OPTIONS="--max-old-space-size=2048"

COPY package.json package-lock.json .npmrc ./

# Add high timeout for the Nexus proxy
RUN npm ci --no-audit --no-fund --fetch-timeout=600000

COPY . .
RUN npx ng build --configuration=production

# Stage 2: Serve the application using a lightweight server
FROM nginx:alpine
# Copy the custom Nginx configuration file
# This replaces the default Nginx config
COPY nginx.conf /etc/nginx/nginx.conf

# Copy the built files from the build stage to the Nginx static directory
# Make sure the source path is correct: it should be /app/dist/your-app-name if you are using Angular 16+
# For most setups, this works:
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
# If the path is /app/dist/my-app, use: COPY --from=build /app/dist/my-app /usr/share/nginx/html

# Nginx default port is 80
EXPOSE 80

# The base image already runs Nginx by default, so no CMD needed
