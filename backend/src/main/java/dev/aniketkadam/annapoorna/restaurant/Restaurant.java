package dev.aniketkadam.annapoorna.restaurant;

import dev.aniketkadam.annapoorna.common.BaseAuditingEntity;
import dev.aniketkadam.annapoorna.file.MediaAsset;
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
    private Double rating; // average rating of all foodItems

    // restaurant image details
    @OneToOne
    @JoinColumn(name = "image_id")
    private MediaAsset image;

    // Address fields
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private RestaurantAddress address;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // seller who own this restaurant

    @OneToMany(mappedBy = "restaurant")
    private List<FoodItem> menu = new ArrayList<>();
}
