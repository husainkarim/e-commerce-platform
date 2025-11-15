package backend.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRole {
    @NotBlank(message = "User ID is required")
    String userId;
    @NotBlank(message = "Role is required")
    String role;
}
