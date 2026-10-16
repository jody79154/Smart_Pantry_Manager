package com.example.smartpantry.util;

import com.example.smartpantry.model.RecipeIngredient;

public final class IngredientFormatter {

    private IngredientFormatter() {
    }

    public static String format(RecipeIngredient ingredient) {
        String quantity = QuantityFormatter.format(ingredient.getQuantity());
        if ("item".equals(ingredient.getUnit())) {
            return quantity + " \u00d7 " + ingredient.getName();
        }
        return quantity + " " + ingredient.getUnit() + " " + ingredient.getName();
    }
}
