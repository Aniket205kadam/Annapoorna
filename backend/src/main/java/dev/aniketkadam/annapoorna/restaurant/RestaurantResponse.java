package dev.aniketkadam.annapoorna.restaurant;

import dev.aniketkadam.annapoorna.file.MediaAssetResponse;
import dev.aniketkadam.annapoorna.user.UserResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {

    private String id;
    private String name;
    private String description;
    private Double rating;
    private MediaAssetResponse mediaAsset;
    private RestaurantAddress address;
    private UserResponse owner;
}
