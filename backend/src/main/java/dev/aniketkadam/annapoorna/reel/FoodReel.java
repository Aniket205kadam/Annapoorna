package dev.aniketkadam.annapoorna.reel;

import dev.aniketkadam.annapoorna.common.BaseAuditingEntity;
import dev.aniketkadam.annapoorna.file.MediaAsset;
import dev.aniketkadam.annapoorna.food.FoodItem;
import dev.aniketkadam.annapoorna.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "reels")
public class FoodReel extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "media_asset_id", nullable = false)
    private MediaAsset mediaAsset;
    @OneToOne
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;
    @Column(nullable = false)
    private String title;
    private Long durationInSecond;
    @Column(nullable = false)
    private String description;

    private Integer viewsCount = 0;
    private Integer likeCount = 0;
    @ElementCollection
    @Column(name = "liked_user_id")
    private List<String> likedUsers = new ArrayList<>();

    @OneToMany
    private List<Comment> comments;
}
