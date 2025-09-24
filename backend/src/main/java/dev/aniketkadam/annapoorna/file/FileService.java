package dev.aniketkadam.annapoorna.file;

import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {

    Map uploadRestaurantImage(MultipartFile file) throws OperationNotPermittedException;

    Map uploadFoodImage(MultipartFile file) throws OperationNotPermittedException;

    Map uploadFoodReel(MultipartFile file) throws OperationNotPermittedException;

    Map deleteFileByPublicId(String publicId) throws OperationNotPermittedException;
}
