package backend.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"jwt.secret=testsecretkey",
	"server.ssl.key-store-password=testkeystorepassword"
})
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
