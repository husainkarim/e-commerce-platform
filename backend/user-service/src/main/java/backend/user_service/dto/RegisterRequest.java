package backend.user_service.dto;

import backend.user_service.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "User name is required")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    private String name;
    @Email
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    private String password;
    @NotBlank(message = "User type is required")
    private Role userType; // client or seller
    @NotBlank(message = "Avatar is required")
    private String avatar;

    // public String getName() { return name; }
    // public String getEmail() { return email; }
    // public String getPassword() { return password; }
    // public Role getUserType() { return userType; }
    // public String getAvatar() { return avatar; }
}
