import { bootstrapApplication } from '@angular/platform-browser';
// import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { getStorage, provideStorage } from '@angular/fire/storage';
import { environment } from './environments/environment';
import { initializeApp, provideFirebaseApp } from '@angular/fire/app';

const appConfig = {
  providers: [
    provideFirebaseApp(() => initializeApp(environment.firebase)), // Use the factory for Firebase App
    provideStorage(() => getStorage()),           // Storage can often use the arrow function directly
  ]
};


bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
