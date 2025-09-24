package dev.aniketkadam.annapoorna.restaurant;

import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import dev.aniketkadam.annapoorna.file.FileService;
import dev.aniketkadam.annapoorna.file.MediaAsset;
import dev.aniketkadam.annapoorna.file.MediaAssetRepository;
import dev.aniketkadam.annapoorna.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantMapper mapper;
    private final FileService fileService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantAddressRepository addressRepository;
    private final MediaAssetRepository assetRepository;

    @Transactional
    public String addRestaurant(
            @NonNull Authentication connectedUser,
            @NonNull String restaurantString,
            @NonNull String addressString,
            @NonNull MultipartFile image
    ) throws OperationNotPermittedException {
        User currentUser = (User) connectedUser.getPrincipal();

        // Upload restaurant image on server resource
        Map imageProperties = fileService.uploadRestaurantImage(image);
        // Persists image details in media_assets table
        MediaAsset asset = MediaAsset.builder()
                .publicId((String) imageProperties.get("public_id"))
                .url((String) imageProperties.get("url"))
                .secureUrl((String) imageProperties.get("secure_url"))
                .build();
        MediaAsset savedAsset = assetRepository.save(asset);

        // Map objects from string to desired object
        RestaurantRequest restaurantRequest = mapper.stringToRestaurantRequest(restaurantString);
        RestaurantAddressRequest addressRequest = mapper.stringToRestaurantAddressRequest(addressString);

        // Persists address of the restaurant
        RestaurantAddress address = RestaurantAddress.builder()
                .street(addressRequest.getStreet())
                .postalCode(addressRequest.getPostalCode())
                .city(addressRequest.getCity())
                .state(addressRequest.getState())
                .country(addressRequest.getCountry())
                .latitude(addressRequest.getLatitude())
                .longitude(addressRequest.getLongitude())
                .build();
        RestaurantAddress savedAddress = addressRepository.save(address);

        // Persists restaurant details and return the ID
        Restaurant restaurant = Restaurant.builder()
                .name(restaurantRequest.getName())
                .image(savedAsset)
                .description(restaurantRequest.getDescription())
                .address(savedAddress)
                .owner(currentUser)
                .build();
        return restaurantRepository.save(restaurant).getId();
    }
}
