package com.example.smartpantry.matching;

import com.example.smartpantry.matching.IngredientConversions.Measure;
import com.example.smartpantry.matching.UnitConverter.Unit;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import com.example.smartpantry.util.ExpiryHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RecipeMatcher {

    private static final double TOLERANCE = 1e-9;

    private RecipeMatcher() {
    }

    public static List<Recipe> findMakeable(List<Recipe> recipes, List<PantryItem> pantry) {
        return findMakeable(recipes, pantry, null);
    }

    public static List<Recipe> findMakeable(List<Recipe> recipes, List<PantryItem> pantry, LocalDate today) {
        List<Recipe> makeable = new ArrayList<>();
        if (recipes == null) {
            return makeable;
        }
        Map<String, Double> available = buildAvailability(pantry, today);
        for (Recipe recipe : recipes) {
            if (canMake(recipe, available)) {
                makeable.add(recipe);
            }
        }
        return makeable;
    }

    public static boolean canMake(Recipe recipe, List<PantryItem> pantry) {
        return canMake(recipe, pantry, null);
    }

    public static boolean canMake(Recipe recipe, List<PantryItem> pantry, LocalDate today) {
        return canMake(recipe, buildAvailability(pantry, today));
    }

    private static boolean canMake(Recipe recipe, Map<String, Double> available) {
        if (recipe == null || recipe.getIngredients().isEmpty()) {
            return false;
        }

        // Recipe matching
        Map<String, Double> required = new HashMap<>();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            Unit unit = UnitConverter.parse(ingredient.getUnit());
            String name = IngredientNormalizer.normalize(ingredient.getName());
            if (unit == null || name.isEmpty() || ingredient.getQuantity() <= 0) {
                return false;
            }
            Measure measure = IngredientConversions.toComparable(name, ingredient.getQuantity(), unit);
            required.merge(measure.getKey(), measure.getAmount(), Double::sum);
        }

        for (Map.Entry<String, Double> need : required.entrySet()) {
            Double have = available.get(need.getKey());
            if (have == null || have + TOLERANCE < need.getValue()) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, Double> buildAvailability(List<PantryItem> pantry, LocalDate today) {
        Map<String, Double> available = new HashMap<>();
        if (pantry == null) {
            return available;
        }
        for (PantryItem item : pantry) {
            if (today != null && ExpiryHelper.isExpired(item.getExpiryDate(), today)) {
                continue;
            }
            Unit unit = UnitConverter.parse(item.getUnit());
            String name = IngredientNormalizer.normalize(item.getName());
            if (unit == null || name.isEmpty() || item.getQuantity() <= 0) {
                continue;
            }
            Measure measure = IngredientConversions.toComparable(name, item.getQuantity(), unit);
            available.merge(measure.getKey(), measure.getAmount(), Double::sum);
        }
        return available;
    }
}
