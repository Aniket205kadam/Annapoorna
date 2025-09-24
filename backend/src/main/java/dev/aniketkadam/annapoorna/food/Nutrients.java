package dev.aniketkadam.annapoorna.food;

import lombok.*;

import java.util.List;

@Builder
public record Nutrients(
        String calories,
        String protein,
        String carbs,
        String fat,
        List<String> allergens
) {
}
