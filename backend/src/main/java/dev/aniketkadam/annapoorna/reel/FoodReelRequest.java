package dev.aniketkadam.annapoorna.reel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodReelRequest {

    private String title;
    private String description;
}
