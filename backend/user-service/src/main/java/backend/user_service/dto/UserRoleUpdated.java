package backend.user_service.dto;

public class UserRoleUpdated {
    private String userId;
    private String newRole;

    public UserRoleUpdated(String userId, String newRole) {
        this.userId = userId;
        this.newRole = newRole;
    }
}
