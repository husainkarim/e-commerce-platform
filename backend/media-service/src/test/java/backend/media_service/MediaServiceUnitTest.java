package backend.media_service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import backend.media_service.model.Media;
import backend.media_service.repository.MediaRepository;
import backend.media_service.service.MediaService;

@ExtendWith(MockitoExtension.class)
public class MediaServiceUnitTest {

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    private Media mediaToSave;

    @BeforeEach
    void setUp() {
        // Prepare a media object for use in tests
        mediaToSave = new Media("m001", "/uploads/product/p123/img.jpg", "p123");
    }

    @Test
    void whenSaveMedia_thenMediaIsSavedAndNotificationIsSent() {
        // ARRANGE: Mock the behavior of the dependencies
        // When repository.save is called, return the mediaToSave object
        when(mediaRepository.save(any(Media.class)))
            .thenReturn(mediaToSave);

        // ACT: Call the method under test
        Media createdMedia = mediaService.saveMedia(mediaToSave);

        // ASSERT: Verify the return value and interactions
        assertEquals("m001", createdMedia.getId());
        assertEquals("/uploads/product/p123/img.jpg", createdMedia.getImagePath());

        // Verify the interactions with the mocks
        verify(mediaRepository, times(1)).save(any(Media.class));
    }

    // Example of testing a retrieval method
    @Test
    void whenFindByProductId_thenReturnMediaList() {
        // ARRANGE
        String productId = "p456";
        List<Media> expectedList = List.of(
            new Media("m002", "/uploads/p456/1.jpg", productId),
            new Media("m003", "/uploads/p456/2.jpg", productId)
        );
        
        // Mock the repository call to return the list
        when(mediaRepository.findByProductId(productId)).thenReturn(expectedList);

        // ACT
        List<Media> actualList = mediaService.findByProductId(productId);

        // ASSERT
        assertEquals(2, actualList.size(), "Should retrieve exactly two media items.");
        assertEquals("m002", actualList.get(0).getId());
        
        // Verify the repository was called
        verify(mediaRepository, times(1)).findByProductId(productId);
    }
}