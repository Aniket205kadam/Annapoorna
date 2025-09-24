package dev.aniketkadam.annapoorna.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import dev.aniketkadam.annapoorna.file.MediaAssetResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FoodMapper {

    private final ObjectMapper mapper = new ObjectMapper();

    public FoodRequest stringToFoodRequest(String foodString) throws OperationNotPermittedException {
        // convert string to object
        try {
            return mapper.readValue(foodString, FoodRequest.class);
        } catch (JsonProcessingException e) {
            throw new OperationNotPermittedException("Invalid food data format. Please check your request.");
        }
    }

    public FoodItem fromFoodRequestToFoodItem(FoodRequest foodRequest) {
        return FoodItem.builder()
                .name(foodRequest.getName())
                .description(foodRequest.getDescription())
                .originalPrice(foodRequest.getOriginalPrice())
                .discountedPrice(foodRequest.getDiscountedPrice())
                .discountPercentage(foodRequest.getDiscountPercentage())
                .category(foodRequest.getCategory())
                .tags(foodRequest.getTags())
                .preparationTimeMinutes(foodRequest.getPreparationTimeMinutes())
                .isVegetarian(foodRequest.getIsVegetarian())
                .isVegan(foodRequest.getIsVegan())
                .isSpicy(foodRequest.getIsSpicy())
                .customizableOptions(foodRequest.getCustomizableOptions())
                .calories(foodRequest.getCalories())
                .protein(foodRequest.getProtein())
                .carbs(foodRequest.getCarbs())
                .fat(foodRequest.getFat())
                .allergens(foodRequest.getAllergens())
                .availabilitySchedule(foodRequest.getAvailabilitySchedule())
                .build();
    }

    public FoodItemResponse fromFoodItemTOFoodItemResponse(FoodItem foodItem) {
        List<MediaAssetResponse> imageUrls = foodItem.getImageUrls().stream()
                .map(imageUrl ->
                        MediaAssetResponse.builder()
                                .id(imageUrl.getId())
                                .url(imageUrl.getUrl())
                                .secureUrl(imageUrl.getSecureUrl())
                                .build()
                ).toList();
        List<String> category = Arrays.stream(foodItem.getCategory().split(",")).toList();

        return FoodItemResponse.builder()
                .id(foodItem.getId())
                .name(foodItem.getName())
                .description(foodItem.getDescription())
                .rating(foodItem.getRating())
                .imageUrls(imageUrls)
                .restaurantId(foodItem.getRestaurant().getId())
                .originalPrice(foodItem.getOriginalPrice())
                .discountedPrice(foodItem.getDiscountedPrice())
                .discountPercentage(foodItem.getDiscountPercentage())
                .category(category)
                .tags(foodItem.getTags())
                .preparationTimeMinutes(foodItem.getPreparationTimeMinutes())
                .isVegetarian(foodItem.getIsVegetarian())
                .isVegan(foodItem.getIsVegan())
                .isSpicy(foodItem.getIsSpicy())
                .customizableOptions(null)
                .nutrients(Nutrients.builder()
                        .calories(foodItem.getCalories() + " kcal")
                        .protein(foodItem.getProtein() + "grams")
                        .fat(foodItem.getFat() + "grams")
                        .carbs(foodItem.getCarbs() + "grams")
                        .allergens(Arrays.stream(foodItem.getAllergens().split(",")).toList())
                        .build())
                .isAvailable(foodItem.getIsAvailable())
                .availabilitySchedule(foodItem.getAvailabilitySchedule())
                .build();
    }
}
