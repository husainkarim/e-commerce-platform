package backend.media_service.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

import backend.media_service.util.ImageCompressionUtil;

@Service
public class FileStorageService {

    private final Storage storage;
    private final String bucketName = "social-network-d4ea8.appspot.com";

    public FileStorageService() throws IOException {
        ClassPathResource resource = new ClassPathResource(
            "serviceAccountKey.json"
        );
        InputStream serviceAccount = resource.getInputStream();

        GoogleCredentials credentials = GoogleCredentials.fromStream(
            serviceAccount
        ).createScoped(
            Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform")
        );

        this.storage = StorageOptions.newBuilder()
            .setCredentials(credentials)
            .build()
            .getService();
    }

    public String uploadCompressedImage(MultipartFile file) throws IOException {
        BufferedImage img = ImageIO.read(file.getInputStream());

        // Compress to 70% quality
        byte[] compressedBytes = ImageCompressionUtil.compress(img, 0.7f);

        String fileName = "images/" + file.getOriginalFilename() + UUID.randomUUID() + ".jpg";

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/jpeg")
                .build();

        storage.create(blobInfo, compressedBytes);

        return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
    }
}
