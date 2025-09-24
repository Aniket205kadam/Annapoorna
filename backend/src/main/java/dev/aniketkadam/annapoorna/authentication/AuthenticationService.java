package dev.aniketkadam.annapoorna.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import dev.aniketkadam.annapoorna.email.EmailService;
import dev.aniketkadam.annapoorna.email.EmailTemplateName;
import dev.aniketkadam.annapoorna.email.EmailVerificationRequest;
import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import dev.aniketkadam.annapoorna.exception.RefreshTokenException;
import dev.aniketkadam.annapoorna.security.JwtService;
import dev.aniketkadam.annapoorna.user.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

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
    private final UserMapper userMapper;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${application.security.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;
    @Value("${application.client.user-frontend-url}")
    private String frontendUrl;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws OperationNotPermittedException {
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

        // put refresh token in HttpOnly, secure cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .sameSite("Strict")
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return AuthenticationResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getName())
                .accessToken(accessToken)
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
                        .subject("Complete your signup - verify yor email")
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
    public AuthenticationResponse refreshAccessToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws RefreshTokenException {
        // extract refresh-token from cookie
        String token = Arrays.stream(Optional.ofNullable(httpRequest.getCookies()).orElse(new Cookie[0]))
                .filter(c -> c.getName().equals("refreshToken"))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElseThrow(() -> new RefreshTokenException("No refresh token found in cookies"));

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
                .build();
    }

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
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

        // put refresh token on cookie which more secure
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .sameSite("Strict")
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return AuthenticationResponse.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getName())
                .accessToken(accessToken)
                .build();
    }

    @Transactional
    public AuthenticationResponse signupWithGoogle(OAuthRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws GeneralSecurityException, IOException, OperationNotPermittedException {
        String token = request.getToken();
        GoogleIdToken idToken = verifyGoogleToken(token);
        if (idToken == null) {
            throw new OperationNotPermittedException("Failed to retrieve your Google account information.");
        }
        GoogleIdToken.Payload googleUser = idToken.getPayload();
        User user = userMapper.fromGoogleUserToAnnapoornaUser(googleUser);
        Role role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(request.getRole())
                        .build()));
        user.setEnabled(true);
        user.setRole(role);
        user.setPhone(request.getPhone());
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

        // put refresh token on cookie which more secure
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .sameSite("Strict")
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return AuthenticationResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getName())
                .accessToken(accessToken)
                .build();
    }

    private GoogleIdToken verifyGoogleToken(String token) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        return verifier.verify(token);
    }

    @Transactional
    public Boolean resetPasswordWithEmail(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        PasswordResetToken savedToken = passwordResetTokenRepository.save(PasswordResetToken.builder()
                .userId(user.getId())
                .token(UUID.randomUUID().toString() + UUID.randomUUID())
                .used(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build());
        // link for reset the password
        String restLink = frontendUrl + "/rest-password?uid=" + user.getId() + "&token=" + savedToken.getToken();
        // send password rest email
        emailService.sendRestPasswordEmail(user.getName(), user.getEmail(), restLink);
        return true;
    }

    @Transactional
    public Boolean changePassword(String userId, String token, Map<String, String> request) throws OperationNotPermittedException {
        final String newPassword = request.get("password");
        if (newPassword.isEmpty()) {
            throw new OperationNotPermittedException("Not found the new password.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with user Id: " + userId));
        PasswordResetToken passwordToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid token!"));

        if (!user.getId().equals(passwordToken.getUserId())
                || passwordToken.getExpiresAt().isBefore(LocalDateTime.now())
                || Boolean.TRUE.equals(passwordToken.getUsed())) {
            throw new OperationNotPermittedException("Invalid or expired token.");
        }

        passwordToken.setUsed(true);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.save(passwordToken);
        return true;
    }
}
