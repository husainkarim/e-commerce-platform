package backend.media_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import backend.media_service.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@TestConfiguration
public class FileStorageTestConfig {

    @Bean
    @Primary
    public FileStorageService fileStorageService() {
        try {
            return new FileStorageService() {
                @Override
                public String uploadCompressedImage(MultipartFile file) {
                    return "https://dummy-url.com/image.jpg";
                }

                @Override
                public boolean deleteFileByUrl(String fileUrl) {
                    return true;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test FileStorageService", e);
        }
    }
}
