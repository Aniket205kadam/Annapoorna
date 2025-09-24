package dev.aniketkadam.annapoorna.authentication;

import dev.aniketkadam.annapoorna.user.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthRequest {

    @NotEmpty(message = "Phone number is required")
    @NotBlank(message = "Phone number is required")
    private String phone;
    @NotNull(message = "Role is required")
    private RoleName role;
    @NotEmpty(message = "Token is required")
    @NotBlank(message = "Token is required")
    private String token;
}
