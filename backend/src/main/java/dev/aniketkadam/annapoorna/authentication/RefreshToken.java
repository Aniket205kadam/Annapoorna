package dev.aniketkadam.annapoorna.authentication;

import dev.aniketkadam.annapoorna.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(length = 800, nullable = false, updatable = false)
    private String token;
    private String deviceInfo;
    private Boolean revoked;
    private Boolean isExpired;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
