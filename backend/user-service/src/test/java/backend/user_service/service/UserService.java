package backend.user_service.service;

import org.springframework.stereotype.Service;

import backend.user_service.model.User;
import backend.user_service.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createNewUser(User user) {
        // Simple business logic: ensures the username is lowercase before saving
        user.setName(user.getName().toLowerCase());
        return userRepository.save(user);
    }
}