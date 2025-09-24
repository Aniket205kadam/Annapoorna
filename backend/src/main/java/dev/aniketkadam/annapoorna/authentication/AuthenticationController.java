package dev.aniketkadam.annapoorna.authentication;

import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import dev.aniketkadam.annapoorna.exception.RefreshTokenException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegistrationRequest registrationRequest,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws OperationNotPermittedException {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(service.register(registrationRequest, httpRequest, httpResponse));
    }

    @PatchMapping("/verification/{email}")
    public ResponseEntity<Void> sendVerificationCode(
            @PathVariable("email") String email
    ) throws MessagingException {
        service.sendVerificationCodeOnEmail(email);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/verification/{email}/{verification-code}")
    public ResponseEntity<Boolean> validateVerificationCode(
            @PathVariable("email") String email,
            @PathVariable("verification-code") String verificationCode
    ) throws OperationNotPermittedException, MessagingException {
        return ResponseEntity
                .ok(service.emailVerification(email, verificationCode));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshAccessToken(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws RefreshTokenException {
        return ResponseEntity.ok(service.refreshAccessToken(httpRequest, httpResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody @Valid AuthenticationRequest authenticationRequest,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        return ResponseEntity.ok(service.login(authenticationRequest, httpRequest, httpResponse));
    }

    @PostMapping("/signup/google")
    public ResponseEntity<AuthenticationResponse> signupWithGoogle(
            @RequestBody @Valid OAuthRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws GeneralSecurityException, IOException, OperationNotPermittedException {
        return ResponseEntity.ok(service.signupWithGoogle(request, httpRequest, httpResponse));
    }

    @PatchMapping("/password-reset/{email}")
    public ResponseEntity<Boolean> resetPasswordWithEmail(
            @PathVariable("email") @NotEmpty @NotBlank String email
    ) throws MessagingException {
        return ResponseEntity.ok(service.resetPasswordWithEmail(email));
    }

    @PatchMapping("/password/change")
    public ResponseEntity<Boolean> changePassword(
            @RequestParam("uid") String userId,
            @RequestParam("token") String token,
            @RequestBody  Map<String, String> request
    ) throws OperationNotPermittedException {
        return ResponseEntity.ok(service.changePassword(userId, token, request));
    }

}
