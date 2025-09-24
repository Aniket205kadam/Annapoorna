package dev.aniketkadam.annapoorna.food;

import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/foods")
@PreAuthorize("hasRole('SELLER')")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService service;

    @PostMapping
    public ResponseEntity<FoodItemResponse> addFoodInMenu(
            Authentication connectedUser,
            @RequestPart(name = "food") String foodString,
            @RequestPart(name = "reel", required = false) String reelString,
            @RequestPart(name = "video", required = false) MultipartFile reel,
            @RequestPart(name = "images") MultipartFile ...images
    ) throws OperationNotPermittedException {
        return ResponseEntity.ok(service.addFoodInMenu(connectedUser, foodString, reelString, reel, images));
    }
}
