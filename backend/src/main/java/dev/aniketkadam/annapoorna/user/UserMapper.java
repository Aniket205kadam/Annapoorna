package dev.aniketkadam.annapoorna.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;

    public User fromGoogleUserToAnnapoornaUser(GoogleIdToken.Payload googleUser) {
        String sub = googleUser.getSubject();
        Optional<User> existingUser = userRepository.findByGoogleId(sub);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        String[] fullName = ((String) googleUser.get("name")).split(" ");
        String firstname = "";
        String lastname = "";
        if (fullName.length >= 2) {
            firstname = fullName[0];
            lastname = fullName[1];
        } else {
            firstname = fullName[0];
            lastname = "";
        }

        return User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(googleUser.getEmail())
                .googleId(sub)
                .build();
    }
}
