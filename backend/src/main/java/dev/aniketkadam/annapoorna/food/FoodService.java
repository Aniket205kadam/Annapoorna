package dev.aniketkadam.annapoorna.food;

import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import dev.aniketkadam.annapoorna.file.FileService;
import dev.aniketkadam.annapoorna.file.MediaAsset;
import dev.aniketkadam.annapoorna.file.MediaAssetRepository;
import dev.aniketkadam.annapoorna.reel.FoodReel;
import dev.aniketkadam.annapoorna.reel.FoodReelRepository;
import dev.aniketkadam.annapoorna.reel.FoodReelRequest;
import dev.aniketkadam.annapoorna.reel.ReelMapper;
import dev.aniketkadam.annapoorna.restaurant.Restaurant;
import dev.aniketkadam.annapoorna.restaurant.RestaurantRepository;
import dev.aniketkadam.annapoorna.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodService {

    private final FileService fileService;
    private final MediaAssetRepository assetRepository;
    private final FoodMapper foodMapper;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final ReelMapper reelMapper;
    private final FoodReelRepository reelRepository;

    @Transactional
    public FoodItemResponse addFoodInMenu(
            @NonNull Authentication connectedUser,
            @NonNull String foodString,
            String reelString,
            MultipartFile reel,
            @NonNull MultipartFile[] images
    ) throws OperationNotPermittedException {
        FoodRequest foodRequest = foodMapper.stringToFoodRequest(foodString);

        User currentUser = (User) connectedUser.getPrincipal();
        Restaurant restaurant = restaurantRepository.findById(foodRequest.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with Id: " + foodRequest.getRestaurantId()));
        // check current-user is owner of this restaurant
        if (!currentUser.getId().equals(restaurant.getOwner().getId())) {
            throw new OperationNotPermittedException("You are not permitted to add menu in the restaurant!");
        }

        List<MediaAsset> uploadedImages = new ArrayList<>();
        try {
            // Upload food images
            for (MultipartFile image : images) {
                Map imageResponse = fileService.uploadFoodImage(image);
                MediaAsset asset = MediaAsset.builder()
                        .publicId((String) imageResponse.get("public_id"))
                        .url((String) imageResponse.get("url"))
                        .secureUrl((String) imageResponse.get("secure_url"))
                        .build();
                MediaAsset savedMediaAsset = assetRepository.save(asset);
                uploadedImages.add(savedMediaAsset);
            }
            FoodItem foodItem = foodMapper.fromFoodRequestToFoodItem(foodRequest);
            foodItem.setIsAvailable(true);
            foodItem.setRestaurant(restaurant);
            foodItem.setImageUrls(uploadedImages);
            FoodItem savedFood = foodItemRepository.save(foodItem);

            // Upload food reel
            if (reel != null) {
                boolean isReelUploaded = uploadFoodReel(connectedUser, savedFood.getId(), reelString, reel);
                if (!isReelUploaded) {
                    throw new OperationNotPermittedException("Failed to upload the reel of the food");
                }
            }
            return foodMapper.fromFoodItemTOFoodItemResponse(savedFood);
        } catch (Exception e) {
            // before throw the error first remove all the files which is stored during this call
            uploadedImages.forEach(image -> {
                Boolean isDeleted = removeFileFromServer(image.getPublicId());
                if (Boolean.FALSE.equals(isDeleted)) {
                    log.error("Failed to remove the file with public_id: [{}]. Please remove manually!", image.getPublicId());
                }
            });
            //todo -> also remove the reel
            throw new OperationNotPermittedException(
                    (e.getMessage() == null || e.getMessage().isBlank())
                            ? "Failed to upload the food details!"
                            : e.getMessage()
            );
        }
    }

    @Transactional
    public Boolean uploadFoodReel(Authentication connectedUser, String foodId, String reelString, MultipartFile video) throws OperationNotPermittedException {
        User currentUser = (User) connectedUser.getPrincipal();
        FoodItem foodItem = foodItemRepository.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food item is not found with Id: " + foodId));

        if (!foodItem.getRestaurant().getOwner().getId().equals(currentUser.getId())) {
            throw new OperationNotPermittedException("You are not permitted to add menu in the restaurant!");
        }

        Map reelResponse = fileService.uploadFoodReel(video);
        MediaAsset asset = MediaAsset.builder()
                .publicId((String) reelResponse.get("public_id"))
                .url((String) reelResponse.get("url"))
                .secureUrl((String) reelResponse.get("secure_url"))
                .build();
        MediaAsset reelAsset = assetRepository.save(asset);

        FoodReelRequest reelRequest = reelMapper.stringToFoodReelRequest(reelString);
        FoodReel foodReel = FoodReel.builder()
                .mediaAsset(reelAsset)
                .foodItem(foodItem)
                .title(reelRequest.getTitle())
                .description(reelRequest.getDescription())
                .durationInSecond((Long) reelResponse.get("duration"))
                .build();
        FoodReel savedReel = reelRepository.save(foodReel);
        return savedReel.getId() != null;
    }

    private Boolean removeFileFromServer(String publicId) {
        try {
            Map response = fileService.deleteFileByPublicId(publicId);
            return response.get("result").equals("ok");
        } catch (Exception e) {
            return false;
        }
    }
}
