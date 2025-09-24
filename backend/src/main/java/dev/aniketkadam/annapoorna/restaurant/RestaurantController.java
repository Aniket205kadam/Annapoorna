package dev.aniketkadam.annapoorna.restaurant;

import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class RestaurantController {

    private final RestaurantService service;

    @PostMapping
    public ResponseEntity<String> addRestaurant(
            Authentication connectedUser,
            @RequestPart("restaurant") String restaurantString,
            @RequestPart("address") String addressString,
            @RequestPart("image") MultipartFile image
    ) throws OperationNotPermittedException {
        return ResponseEntity.ok(service.addRestaurant(connectedUser, restaurantString, addressString, image));
    }

}
