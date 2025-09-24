package dev.aniketkadam.annapoorna.reel;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import org.springframework.stereotype.Service;

@Service
public class ReelMapper {

    private final ObjectMapper mapper = new ObjectMapper();

    public FoodReelRequest stringToFoodReelRequest(String reelString) throws OperationNotPermittedException {
        // convert string to object
        try {
            return mapper.readValue(reelString, FoodReelRequest.class);
        } catch (Exception e) {
            throw new OperationNotPermittedException("Invalid food data format. Please check your request.");
        }
    }
}
