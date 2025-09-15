package dev.aniketkadam.annapoorna.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @NotEmpty(message = "Please provide your email address to proceed!")
    @NotBlank(message = "Please provide your email address to proceed!")
    private String email;
    @NotEmpty(message = "Please provide your password to proceed!")
    @NotBlank(message = "Please provide your password to proceed!")
    private String password;
}
