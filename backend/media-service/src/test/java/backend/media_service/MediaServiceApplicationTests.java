package backend.media_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import backend.media_service.config.FirebaseTestConfig;
import backend.media_service.config.FileStorageTestConfig;

import backend.media_service.service.FileStorageService;

import org.springframework.boot.test.mock.mockito.MockBean;

import backend.media_service.config.FirebaseConfig;

@SpringBootTest
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@Import(value = {FirebaseTestConfig.class, FileStorageTestConfig.class})
@ActiveProfiles("test")
class MediaServiceApplicationTests {

	@MockBean
    private FileStorageService fileStorageService;

	@MockBean
	private FirebaseConfig firebaseConfig;

	@Test
	void contextLoads() {
	}

}
