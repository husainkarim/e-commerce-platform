const fs = require('fs');
const dotenv = require('dotenv');

// Load environment variables from .env file
dotenv.config();

const environmentTarget = process.env.NODE_ENV || 'development'; // Default to development if not set

const firebaseConfig = {
  projectId: process.env.FIREBASE_PROJECT_ID,
  appId: process.env.FIREBASE_APP_ID,
  storageBucket: process.env.FIREBASE_STORAGE_BUCKET,
  apiKey: process.env.FIREBASE_API_KEY,
  authDomain: process.env.FIREBASE_AUTH_DOMAIN,
  messagingSenderId: process.env.FIREBASE_MESSAGING_SENDER_ID,
  measurementId: process.env.FIREBASE_MEASUREMENT_ID,
  // Add any other Firebase config properties from your .env
};

// You might also want to include other vars from .env
const otherEnvVars = {
  MY_CUSTOM_VAR: process.env.MY_CUSTOM_VAR
};

// Define the content for environment.ts (for development)
const environmentDevContent = `
import { Environment } from './environment.interface';

export const environment: Environment = {
  production: false,
  firebase: ${JSON.stringify(firebaseConfig, null, 2)},
  // Include other variables
  MY_CUSTOM_VAR: "${otherEnvVars.MY_CUSTOM_VAR}",
};
`;

// Define the content for environment.prod.ts (for production)
// In a real scenario, you'd likely have a separate .env.production file
// or use different values for some variables. For simplicity, we'll
// reuse for now, but mark production as true.
const environmentProdContent = `
import { Environment } from './environment.interface';

export const environment: Environment = {
  production: true,
  firebase: ${JSON.stringify(firebaseConfig, null, 2)},
  // Include other variables
  MY_CUSTOM_VAR: "${otherEnvVars.MY_CUSTOM_VAR}",
};
`;

// Write to src/environments/environment.ts
const environmentPath = './src/environments/environment.ts';
fs.writeFile(environmentPath, environmentDevContent, function (err) {
  if (err) {
    console.log(err);
  }
  console.log(`Successfully generated ${environmentPath}`);
});

// Write to src/environments/environment.prod.ts
const environmentProdPath = './src/environments/environment.prod.ts';
fs.writeFile(environmentProdPath, environmentProdContent, function (err) {
  if (err) {
    console.log(err);
  }
  console.log(`Successfully generated ${environmentProdPath}`);
});
