package dev.aniketkadam.annapoorna.food;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodRequest {
    private String name;
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private BigDecimal discountPercentage;
    private String category;
    private List<String> tags;
    private Integer preparationTimeMinutes;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isSpicy;
    private String customizableOptions;
    private Integer calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private String allergens;
    private String availabilitySchedule;

    private String restaurantId;
}
