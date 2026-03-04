package backend.product_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;

import backend.product_service.controller.ProductController;
import backend.product_service.service.KafkaService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@TestPropertySource(properties = {
    "spring.kafka.listener.auto-startup=false",
	"spring.data.mongodb.uri=mongodb://localhost:27017/testdb",
})
class ProductServiceApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@MockBean
	private KafkaService kafkaService;

	@Test
	void contextLoads() {
		assertThat(applicationContext).isNotNull();
	}

	@Test
	void shouldLoadProductServiceApplication() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("productServiceApplication")).isTrue();
	}

	@Test
	void shouldLoadProductController() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("productController")).isTrue();
	}

	@Test
	void shouldLoadKafkaService() {
		assertThat(applicationContext).isNotNull();
		KafkaService service = applicationContext.getBean(KafkaService.class);
		assertThat(service).isNotNull();
	}

	@Test
	void shouldLoadProductRepository() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("productRepository")).isTrue();
	}

	@Test
	void shouldLoadSellerRepository() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("sellerRepository")).isTrue();
	}

	@Test
	void shouldLoadOrderedRepository() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("orderedRepository")).isTrue();
	}

	@Test
	void shouldApplicationStartSuccessfully() {
		assertThat(applicationContext).isNotNull();
	}
}

