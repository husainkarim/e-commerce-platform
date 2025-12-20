package backend.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
        // Just ensures Spring context starts without real Mongo/Kafka
    }
}
