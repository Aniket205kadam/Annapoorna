package dev.aniketkadam.annapoorna.restaurant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aniketkadam.annapoorna.exception.OperationNotPermittedException;
import org.springframework.stereotype.Service;

@Service
public class RestaurantMapper {

    private final ObjectMapper mapper = new ObjectMapper();

    public RestaurantRequest stringToRestaurantRequest(String restaurantString) throws OperationNotPermittedException {
        // convert string to object
        try {
            return mapper.readValue(restaurantString, RestaurantRequest.class);
        } catch (JsonProcessingException e) {
            throw new OperationNotPermittedException("Invalid restaurant data format. Please check your request.");
        }
    }

    public RestaurantAddressRequest stringToRestaurantAddressRequest(String addressString) throws OperationNotPermittedException {
        // convert string to object
        try {
            return mapper.readValue(addressString, RestaurantAddressRequest.class);
        } catch (JsonProcessingException e) {
            throw new OperationNotPermittedException("Invalid restaurant data format. Please check your request.");
        }
    }
}
