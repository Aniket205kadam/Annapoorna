package dev.aniketkadam.annapoorna.reel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodReelRepository extends JpaRepository<FoodReel, String> {
}
