package dev.aniketkadam.annapoorna.restaurant;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantAddressRequest {

    @NotEmpty(message = "Street is required")
    @NotBlank(message = "Street is required")
    private String street;
    @NotEmpty(message = "City is required")
    @NotBlank(message = "City is required")
    private String city;
    @NotEmpty(message = "Postal code is required")
    @NotBlank(message = "Postal code is required")
    private String postalCode;
    @NotEmpty(message = "State is required")
    @NotBlank(message = "State is required")
    private String state;
    @NotEmpty(message = "Country is required")
    @NotBlank(message = "Country is required")
    private String country;
    @NotEmpty(message = "Latitude is required")
    @NotBlank(message = "Latitude is required")
    private Double latitude;
    @NotEmpty(message = "Longitude is required")
    @NotBlank(message = "Longitude is required")
    private Double longitude;
}
