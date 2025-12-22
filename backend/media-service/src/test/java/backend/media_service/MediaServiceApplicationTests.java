package backend.media_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import backend.media_service.config.FirebaseTestConfig;

@SpringBootTest
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@Import(FirebaseTestConfig.class)
@ActiveProfiles("test")
class MediaServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
