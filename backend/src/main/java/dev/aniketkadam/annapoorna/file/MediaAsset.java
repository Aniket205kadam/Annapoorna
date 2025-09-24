package dev.aniketkadam.annapoorna.file;

import dev.aniketkadam.annapoorna.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "media_assets")
public class MediaAsset extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false, updatable = false, unique = true)
    private String publicId;
    @Column(nullable = false, updatable = false, unique = true, length = 800)
    private String url;
    @Column(nullable = false, updatable = false, unique = true, length = 800)
    private String secureUrl;
}
