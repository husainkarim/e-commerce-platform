package backend.media_service.config;

import static org.mockito.Mockito.mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.google.firebase.FirebaseApp;

@TestConfiguration
public class FirebaseTestConfig {

    @Bean
    public FirebaseApp firebaseApp() {
        // Return a mock instead of real FirebaseApp
        return mock(FirebaseApp.class);
    }
}
