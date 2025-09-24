package dev.aniketkadam.annapoorna.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
* FileServiceImpl class use Cloudinary to upload photos and videos.
* */

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final Cloudinary cloudinary;

    /**
    * @param file -> image of restaurant
    * @return Map<String, Object> containing details of the uploaded asset:
    *           - public_id: Unique identifier for the asset (Useful for delete the file)
    *           - url: HTTP URL of the asset
    *           - secure_url: HTTPs URL of the asset
    * */
    @Override
    public Map uploadRestaurantImage(MultipartFile file) throws OperationNotPermittedException {
        try {
            return cloudinary.uploader()
                    .upload(
                            file.getBytes(),
                            Map.of("folder", "annapoorna/restaurants", "resource_type", "image")
                    );
        } catch (Exception e) {
            throw new OperationNotPermittedException("Restaurant image upload could not be completed. Please try again.");
        }
    }

    @Override
    public Map uploadFoodImage(MultipartFile file) throws OperationNotPermittedException {
        try {
            System.out.println("File content type: " + file.getContentType());
            return cloudinary.uploader()
                    .upload(
                            file.getBytes(),
                            Map.of("folder", "annapoorna/foods", "resource_type", "image")
                    );
        } catch (Exception e) {
            throw new OperationNotPermittedException("Food image upload could not be completed. Please try again. ERROR:[" + e.getMessage() + "]");
        }
    }

    @Override
    public Map uploadFoodReel(MultipartFile file) throws OperationNotPermittedException {
        try {
            return cloudinary.uploader()
                    .upload(
                            file.getBytes(),
                            Map.of(
                                    "folder", "annapoorna/food-reels",
                                    "resource_type", "video",
                                    "eager", List.of(Map.of("duration", 90)) // only accept 90s video, if not then save only first 90s
                            )
                    );
        } catch (Exception e) {
            throw new OperationNotPermittedException("Food reel video upload could not be completed. Please try again.");
        }
    }

    /**
     * @param publicId
     * @return Map<String, String> containing result of deleting asset
     *          - result: which contents two value "ok" and "not found"
     */
    @Override
    public Map deleteFileByPublicId(@NonNull String publicId) throws OperationNotPermittedException {
        try {
            return cloudinary.uploader()
                    .destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new OperationNotPermittedException("Failed to delete the file with publicId: " + publicId);
        }
    }

}
