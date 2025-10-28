export interface FirebaseConfig {
  projectId: string;
  appId: string;
  storageBucket: string;
  apiKey: string;
  authDomain: string;
  messagingSenderId: string;
  measurementId?: string;
}

export interface Environment {
  production: boolean;
  firebase: FirebaseConfig;
  MY_CUSTOM_VAR?: string;
}
