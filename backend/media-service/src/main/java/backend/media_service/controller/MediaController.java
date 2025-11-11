package backend.media_service.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.media_service.model.Media;
import backend.media_service.repository.MediaRepository;
import backend.media_service.service.FileStorageService;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaRepository mediaRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public MediaController(MediaRepository mediaRepository, FileStorageService fileStorageService) {
        this.mediaRepository = mediaRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadMedia(@RequestParam("file") MultipartFile file, @RequestParam("productId") String productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                System.out.println("File is empty");
                response.put("message", "File is empty");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (file.getSize() > 2 * 1024 * 1024) {
                System.out.println("File too large");
                response.put("message", "File too large (max 2MB)");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            String fileUrl = fileStorageService.uploadCompressedImage(file);
            Media media = new Media();
            media.setImagePath(fileUrl);
            media.setProductId(productId);
            mediaRepository.save(media);
            response.put("message", "File uploaded successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            System.out.println("File upload failed: " + e.getMessage());
            response.put("message", "File upload failed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // get the images by product id
    @GetMapping("/getImagesByProductId")
    public ResponseEntity<Map<String, Object>> getMediaByProductId(@RequestParam("productId") String productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            var mediaList = mediaRepository.findByProductId(productId);
            response.put("images", mediaList);
            response.put("message", "Images fetched successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Failed to fetch images: " + e.getMessage());
            response.put("message", "Failed to fetch images");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
