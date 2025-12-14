# Stage 1: Build the Angular application
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
# Note: Ensure the build path here matches the path in the COPY command below
RUN npm run build -- --configuration=production

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
