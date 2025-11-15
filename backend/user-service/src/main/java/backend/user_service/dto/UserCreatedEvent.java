package backend.user_service.dto;

public class UserCreatedEvent {
    private String userId;
    private String email;
    private String role;
    public UserCreatedEvent(String userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
}

