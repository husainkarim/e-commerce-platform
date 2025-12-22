package backend.media_service.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @Bean
    FirebaseApp firebaseApp() throws IOException {

        InputStream serviceAccount;

        // Use env variable if set (e.g., in Jenkins/Docker)
        String gcpKeyPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (gcpKeyPath != null && !gcpKeyPath.isEmpty()) {
            serviceAccount = new FileInputStream(gcpKeyPath);
        } else {
            // Fallback for local development or tests
            serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
            if (serviceAccount == null) {
                throw new IOException("Firebase service account key not found. Set GOOGLE_APPLICATION_CREDENTIALS or place serviceAccountKey.json in resources.");
            }
        }

        FirebaseOptions options = FirebaseOptions.builder()
            .setProjectId("social-network-d4ea8") 
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setStorageBucket("social-network-d4ea8.appspot.com") 
            .build();

        return FirebaseApp.initializeApp(options);
    }
}
