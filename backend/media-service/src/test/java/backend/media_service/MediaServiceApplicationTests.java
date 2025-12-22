package backend.media_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import backend.media_service.service.FileStorageService;

@SpringBootTest
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
// @TestPropertySource(properties = {
//     // Also explicitly disable listeners to be safe
//     "spring.kafka.listener.auto-startup=false", 
// 	"spring.data.mongodb.uri=mongodb://localhost:27017/testdb",
// })
@ActiveProfiles("test")
class MediaServiceApplicationTests {

	@MockBean
    private FileStorageService fileStorageService;

	@Test
	void contextLoads() {
	}

}
