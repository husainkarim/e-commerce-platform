package backend.user_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @NotBlank
    @Size(min = 3, max = 20)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 30)
    @JsonIgnore
    private String password;

    @NotNull
    private String role;

    @NotBlank
    private String avatar;

    // public String getId() { return id; }
    // public String getName() { return name; }
    // public String getEmail() { return email; }
    // public String getPassword() { return password; }
    // public Role getRole() { return role; }
    // public String getAvatar() { return avatar; }
    // public void setName(String name) { this.name = name; }
    // public void setEmail(String email) { this.email = email; }
    // public void setPassword(String password) { this.password = password; }
    // public void setRole(Role role) { this.role = role; }
    // public void setAvatar(String avatar) { this.avatar = avatar; }
}
