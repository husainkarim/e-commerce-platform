package backend.media_service.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
@Profile("!test")
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream serviceAccount = classLoader.getResourceAsStream("serviceAccountKey.json");

        if (serviceAccount == null) {
            throw new IOException("Firebase service account key not found. Set GOOGLE_APPLICATION_CREDENTIALS or place serviceAccountKey.json in resources.");
        }

        FirebaseOptions options = FirebaseOptions.builder()
            .setProjectId("e-commerce-platform-5461b") 
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setStorageBucket("e-commerce-platform-5461b.firebasestorage.app") 
            .build();

        return FirebaseApp.initializeApp(options);
    }
}
