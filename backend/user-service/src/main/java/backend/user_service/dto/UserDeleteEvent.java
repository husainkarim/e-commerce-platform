package backend.user_service.dto;

public class UserDeleteEvent {
    private String userId;
    public UserDeleteEvent(String userId) {
        this.userId = userId;
    }
}
