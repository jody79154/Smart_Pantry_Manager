package com.jody.smartpantry.logic;

import com.jody.smartpantry.model.PantryItem;
import com.jody.smartpantry.model.Recipe;
import com.jody.smartpantry.model.RecipeIngredient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class RecipeMatcher {

    // Absorbs floating-point noise from decimal entry without rounding a genuine shortage up to a pass.
    private static final BigDecimal EPSILON = new BigDecimal("0.0001");

    private RecipeMatcher() {
    }

    public static List<Recipe> findMatchingRecipes(List<Recipe> recipes, List<PantryItem> pantryItems) {
        Map<String, BigDecimal> pantryTotals = totalsForPantry(pantryItems);
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (matches(recipe, pantryTotals)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    public static boolean matches(Recipe recipe, List<PantryItem> pantryItems) {
        return matches(recipe, totalsForPantry(pantryItems));
    }

    private static boolean matches(Recipe recipe, Map<String, BigDecimal> pantryTotals) {
        Map<String, BigDecimal> required = totalsForIngredients(recipe.getIngredients());
        for (Map.Entry<String, BigDecimal> requirement : required.entrySet()) {
            BigDecimal available = pantryTotals.get(requirement.getKey());
            if (available == null) {
                return false;
            }
            if (available.add(EPSILON).compareTo(requirement.getValue()) < 0) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, BigDecimal> totalsForPantry(List<PantryItem> pantryItems) {
        Map<String, BigDecimal> totals = new HashMap<>();
        for (PantryItem item : pantryItems) {
            accumulate(totals, item.getName(), item.getQuantity(), item.getUnit());
        }
        return totals;
    }

    private static Map<String, BigDecimal> totalsForIngredients(List<RecipeIngredient> ingredients) {
        Map<String, BigDecimal> totals = new HashMap<>();
        for (RecipeIngredient ingredient : ingredients) {
            accumulate(totals, ingredient.getIngredientName(), ingredient.getQuantity(), ingredient.getUnit());
        }
        return totals;
    }

    // Same normalized name + same unit category combine into one total; different categories
    // (or an unrecognised unit) are kept in separate buckets so they can never satisfy each other.
    private static void accumulate(Map<String, BigDecimal> totals, String rawName, double quantity, String unit) {
        UnitConverter.UnitCategory category = UnitConverter.categoryOf(unit);
        String bucket = category != null
                ? category.name()
                : "unrecognised:" + (unit == null ? "" : unit.trim().toLowerCase(Locale.US));
        String key = IngredientNormalizer.normalize(rawName) + "|" + bucket;

        BigDecimal baseQuantity = category != null
                ? UnitConverter.toBaseQuantity(quantity, unit)
                : BigDecimal.valueOf(quantity);

        totals.merge(key, baseQuantity, BigDecimal::add);
    }
}
