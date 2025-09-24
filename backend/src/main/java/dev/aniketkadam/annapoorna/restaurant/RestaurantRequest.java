package dev.aniketkadam.annapoorna.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequest {

    @NotEmpty(message = "Restaurant name is required")
    @NotBlank(message = "Restaurant name is required")
    private String name;
    @NotEmpty(message = "Restaurant description is required")
    @NotBlank(message = "Restaurant description is required")
    private String description;
}
