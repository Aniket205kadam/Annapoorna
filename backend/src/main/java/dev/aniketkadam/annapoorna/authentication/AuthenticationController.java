package dev.aniketkadam.annapoorna.authentication;

import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import dev.aniketkadam.annapoorna.exception.RefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegistrationRequest registrationRequest,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(service.register(registrationRequest, httpRequest));
    }

    @PatchMapping("/verification/{email}")
    public ResponseEntity<Void> sendVerificationCode(
            @PathVariable("email") String email
    ) {
        service.sendVerificationCodeOnEmail(email);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/verification/{email}/{verification-code}")
    public ResponseEntity<Boolean> validateVerificationCode(
            @PathVariable("email") String email,
            @PathVariable("verification-code") String verificationCode
    ) throws OperationNotPermittedException {
        return ResponseEntity
                .ok(service.emailVerification(email, verificationCode));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshAccessToken(
            @RequestBody Map<String, Object> request
    ) throws RefreshTokenException {
        return ResponseEntity.ok(service.refreshAccessToken(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody @Valid AuthenticationRequest authenticationRequest,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(service.login(authenticationRequest, httpRequest));
    }

}
