package backend.user_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import backend.user_service.model.User;
import backend.user_service.repository.UserRepository;
import backend.user_service.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User inputUser;

    @BeforeEach
    void setUp() {
        inputUser = new User(
            "1L",
            "TestUser123",
            "test@test.com",
            "password",
            "USER",
            "avatar.png"
        );
    }

    @Test
    void whenCreateNewUser_thenUsernameIsLowercasedAndSaved() {

        // Mock save() to return whatever it receives
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User actualUser = userService.createNewUser(inputUser);

        // Assert
        assertEquals("testuser123", actualUser.getName());
        assertEquals("test@test.com", actualUser.getEmail());
    }
}
