package backend.user_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import backend.user_service.model.User;
import backend.user_service.repository.UserRepository;
import backend.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    // 1. Mock the dependency (the database interaction layer)
    @Mock
    private UserRepository userRepository;

    // 2. Inject the mocks into the class we want to test
    @InjectMocks
    private UserService userService;
    
    // Test data setup
    private User inputUser;
    private User expectedSavedUser;

    @BeforeEach
    void setUp() {
        // Setup a user object before each test
// Corrected Java syntax:
        inputUser = new User("1L", "TestUser123", "test@test.com", "password", "USER", "avatar.png");
        expectedSavedUser = new User("1L", "testuser123", "test@test.com", "password", "USER", "avatar.png"); // Note: username is lowercased
    }

    @Test
    void whenCreateNewUser_thenUsernameIsLowercasedAndSaved() {
        // ARRANGE (Mocking Behavior)
        // Tell Mockito what to do when userService calls userRepository.save()
        // We expect it to be called with the lowercased username.
        when(userRepository.save(expectedSavedUser))
            .thenReturn(expectedSavedUser);

        // ACT (Call the method under test)
        User actualUser = userService.createNewUser(inputUser);

        // ASSERT (Verify the results)
        // 1. Check if the returned object is correct
        assertEquals("testuser123", actualUser.getName(), 
                     "The username should be lowercased after processing.");
        assertEquals(expectedSavedUser.getId(), actualUser.getId());
        
        // 2. (Optional but good practice) Verify that the mock was actually called
        // verify(userRepository, times(1)).save(any(User.class));
    }
}