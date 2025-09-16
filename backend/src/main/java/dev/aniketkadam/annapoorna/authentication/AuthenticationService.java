package dev.aniketkadam.annapoorna.authentication;

import dev.aniketkadam.annapoorna.email.EmailService;
import dev.aniketkadam.annapoorna.email.EmailTemplateName;
import dev.aniketkadam.annapoorna.email.EmailVerificationRequest;
import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import dev.aniketkadam.annapoorna.exception.RefreshTokenException;
import dev.aniketkadam.annapoorna.security.JwtService;
import dev.aniketkadam.annapoorna.user.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest, HttpServletRequest httpRequest) throws OperationNotPermittedException {
        Role role = roleRepository.findByName(registrationRequest.getRole())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(registrationRequest.getRole())
                        .build()));

        // first check email is verified
        Optional<VerificationCode> verificationCodes = verificationCodeRepository.findTopByEmailOrderByCreatedAtDesc(registrationRequest.getEmail());
        if (verificationCodes.isEmpty() || verificationCodes.get().getValidatedAt() == null) {
            throw new OperationNotPermittedException("Please verify your email before signing up.");
        }

        // save user
        User user = User.builder()
                .firstname(registrationRequest.getFirstname())
                .lastname(registrationRequest.getLastname())
                .email(registrationRequest.getEmail())
                .phone(registrationRequest.getPhone())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(role)
                .enabled(true)
                .build();
        User savedUser = userRepository.save(user);

        // generate Refresh-token and Access-token
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", savedUser.getEmail());
        final String accessToken = jwtService.generateAccessToken(claims, savedUser);
        final String refreshToken = jwtService.generateRefreshToken(claims, savedUser);

        // persist refresh-token in database
        refreshTokenRepository.save(RefreshToken.builder()
                        .token(refreshToken)
                        .user(savedUser)
                        .deviceInfo(getDeviceInfo(httpRequest))
                        .isExpired(false)
                        .revoked(false)
                .build());

        return AuthenticationResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String getDeviceInfo(HttpServletRequest httpRequest) {
        String userAgent = httpRequest.getHeader("User-Agent"); // Browser or App info
        String ipAddress = httpRequest.getRemoteAddr(); // IP address
        return userAgent + " | IP: " + ipAddress;
    }

    public void sendVerificationCodeOnEmail(@NotNull String email) throws MessagingException {
        String newToken = generateAndSaveActivationToken(email);
        // send email
        emailService.sendEmail(EmailVerificationRequest.builder()
                        .to(email)
                        .emailTemplate(EmailTemplateName.EMAIL_VERIFICATION)
                        .verificationCode(newToken)
                        .subject("Account activation")
                        .build());
    }

    private String generateAndSaveActivationToken(String email) {
        String generatedToken = generateActivationToken(6);
        VerificationCode verificationCode = VerificationCode.builder()
                .code(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10)) // generated verification code only valid for 10 minutes
                .email(email)
                .build();
        return verificationCodeRepository.save(verificationCode).getCode();
    }

    private String generateActivationToken(int length) {
        String characters = "0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIdx = random.nextInt(characters.length());
            code.append(characters.charAt(randomIdx));
        }
        return code.toString();
    }

    @Transactional
    public Boolean emailVerification(String email, String code) throws OperationNotPermittedException, MessagingException {
        VerificationCode verificationCode = verificationCodeRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new EntityNotFoundException("Verification code is not found with code and email!"));
        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            // new verification code send on the register email
            sendVerificationCodeOnEmail(email);
            throw new OperationNotPermittedException("Verification code expired! We've sent a new one to your email. Please check your inbox.");
        }
        if (verificationCode.getValidatedAt() != null) {
            throw new OperationNotPermittedException("A new token has been sent! The previous one is already validated.");
        }
        verificationCode.setValidatedAt(LocalDateTime.now());
        verificationCodeRepository.save(verificationCode);
        return code.equals(verificationCode.getCode());
    }

    @Transactional
    public AuthenticationResponse refreshAccessToken(Map<String, Object> request) throws RefreshTokenException {
        final String token = (String) request.get("refreshToken");
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found or invalid."));
        User currentUser = refreshToken.getUser();
        // check if refresh token is expired or revoked
        if (refreshToken.getRevoked() || refreshToken.getIsExpired() || !jwtService.isValidToken(refreshToken.getToken(), currentUser)) {
            // mark refresh token as expired
            refreshToken.setRevoked(true);
            refreshToken.setIsExpired(true);
            refreshTokenRepository.save(refreshToken);
            throw new RefreshTokenException("Refresh token expired or revoked.");
        }
        // Generate Access token
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", currentUser.getEmail());
        final String newAccessToken = jwtService.generateAccessToken(claims, currentUser);

        return AuthenticationResponse.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getName())
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest, HttpServletRequest httpRequest) {
        User currentUser = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + authenticationRequest.getEmail()));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(currentUser.getEmail(), authenticationRequest.getPassword()));

        // After authentication generate Refresh-token & Access-token
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", currentUser.getEmail());
        final String refreshToken = jwtService.generateRefreshToken(claims, currentUser);
        final String accessToken = jwtService.generateAccessToken(claims, currentUser);

        // persists refresh token in database
        refreshTokenRepository.save(RefreshToken.builder()
                        .token(refreshToken)
                        .user(currentUser)
                        .deviceInfo(getDeviceInfo(httpRequest))
                        .isExpired(false)
                        .revoked(false)
                .build());

        return AuthenticationResponse.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getName())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }
}
