package backend.media_service.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.imageio.ImageIO;

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

        InputStream serviceAccount;

        // Use env variable if set (e.g., in Jenkins/Docker)
        String gcpKeyPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (gcpKeyPath != null && !gcpKeyPath.isEmpty()) {
            serviceAccount = new FileInputStream(gcpKeyPath);
        } else {
            // Fallback for local development or tests
            serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
            if (serviceAccount == null) {
                throw new IOException("Firebase service account key not found. Set GOOGLE_APPLICATION_CREDENTIALS or place serviceAccountKey.json in resources.");
            }
        }

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
        // Read original image
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("Invalid image format or unsupported colorspace");
        }

        // Convert to RGB if necessary
        BufferedImage rgbImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgbImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        // Compress to 70% quality
        byte[] compressedBytes = ImageCompressionUtil.compress(rgbImage, 0.7f);

        String fileName = "images/" + file.getOriginalFilename() + UUID.randomUUID() + ".jpg";

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/jpeg")
                .build();

        storage.create(blobInfo, compressedBytes);

        return "https://firebasestorage.googleapis.com/v0/b/"
        + bucketName
        + "/o/"
        + fileName.replace("/", "%2F")
        + "?alt=media";
    }

    public boolean deleteFileByUrl(String fileUrl) {
        try {
            // Extract the part after /o/ and before ?
            String encodedPath = fileUrl.substring(fileUrl.indexOf("/o/") + 3, fileUrl.indexOf("?"));
            // URL decode it
            String filePath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.name());

            BlobId blobId = BlobId.of(bucketName, filePath);
            return storage.delete(blobId);
        } catch (java.io.UnsupportedEncodingException | java.lang.IllegalArgumentException | java.lang.NullPointerException | java.lang.StringIndexOutOfBoundsException e) {
            System.out.println("Failed to delete file due to invalid URL or encoding: " + e.getMessage());
            return false;
        } catch (com.google.cloud.storage.StorageException e) {
            System.out.println("Failed to delete file from storage: " + e.getMessage());
            return false;
        }
    }
}
