package backend.media_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class MediaServiceApplicationTests {

    @Test
    void contextLoads() {
        // Ensures Spring context starts without real Mongo/Kafka
    }
}
