package dev.aniketkadam.annapoorna.food;

import dev.aniketkadam.annapoorna.file.MediaAssetResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItemResponse {

    private String id;
    private String name;
    private String description;
    private double rating;
    private Long reviewCount;

    private List<MediaAssetResponse> imageUrls = new ArrayList<>();
    private String restaurantId;

    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private BigDecimal discountPercentage;

    private List<String> category;
    private List<String> tags = new ArrayList<>();
    private Integer preparationTimeMinutes;

    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isSpicy;

    private Map<String, Object> customizableOptions;

    private Nutrients nutrients;

    private Boolean isAvailable;
    private String availabilitySchedule;
}
