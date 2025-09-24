package dev.aniketkadam.annapoorna.food;

import dev.aniketkadam.annapoorna.common.BaseAuditingEntity;
import dev.aniketkadam.annapoorna.file.MediaAsset;
import dev.aniketkadam.annapoorna.reel.FoodReel;
import dev.aniketkadam.annapoorna.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "food_items")
public class FoodItem extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    private double rating;
    private Long reviewCount;

    @Column(nullable = false)
    private BigDecimal originalPrice; // Original price of the food item
    private BigDecimal discountedPrice; // Price after applying discount
    private BigDecimal discountPercentage; // Discount percentages

    @Column(nullable = false)
    private String category;
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    private Integer preparationTimeMinutes; // minutes

    // labels
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isSpicy;

    // customization
    private String customizableOptions; // comma-separated list

    // nutritional information
    private Integer calories; // kcal
    private Double protein; // in grams
    private Double carbs; // in grams
    private Double fat; // in grams
    private String allergens; // comma-separated list

    private Boolean isAvailable = true; // Can the restaurant currently serve this item
    private String availabilitySchedule; // "10:00-22:00"


    @OneToMany
    @JoinTable(
            name = "food_items_images",
            joinColumns = @JoinColumn(name = "food_item_id"),
            inverseJoinColumns = @JoinColumn(name = "media_asset_id")
    )
    private List<MediaAsset> imageUrls = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL)
    private List<FoodFeedBack> feedBacks = new ArrayList<>();
}
