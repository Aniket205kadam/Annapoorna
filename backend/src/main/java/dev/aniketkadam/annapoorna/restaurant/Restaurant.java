package dev.aniketkadam.annapoorna.restaurant;

import dev.aniketkadam.annapoorna.common.BaseAuditingEntity;
import dev.aniketkadam.annapoorna.food.FoodItem;
import dev.aniketkadam.annapoorna.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "restaurants")
public class Restaurant extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(length = 300)
    private String description;
    private String imageUrl;

    // Address fields
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;

    private Double rating; // average rating of all foodItems

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // seller who own this restaurant

    private List<FoodItem> menu = new ArrayList<>();
}
