# Stage 1: Build the Angular application
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build -- --output-path=./dist --configuration=production

# Stage 2: Serve the application using a lightweight server
FROM nginx:alpine
# Copy the built files from the build stage to the Nginx static directory
COPY --from=build /app/dist /usr/share/nginx/html
# Nginx default port is 80, but we'll expose 4200 in Compose for consistency
EXPOSE 80
