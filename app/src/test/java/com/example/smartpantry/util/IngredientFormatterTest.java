package com.example.smartpantry.util;

import static org.junit.Assert.assertEquals;

import com.example.smartpantry.model.RecipeIngredient;

import org.junit.Test;

public class IngredientFormatterTest {

    @Test
    public void measuredIngredientsShowQuantityUnitAndName() {
        assertEquals("200 g spaghetti", IngredientFormatter.format(new RecipeIngredient("spaghetti", 200, "g")));
        assertEquals("2 tbsp olive oil", IngredientFormatter.format(new RecipeIngredient("olive oil", 2, "tbsp")));
    }

    @Test
    public void countedIngredientsUseAMultiplicationSign() {
        assertEquals("3 \u00d7 egg", IngredientFormatter.format(new RecipeIngredient("egg", 3, "item")));
    }

    @Test
    public void decimalQuantitiesKeepTheirFraction() {
        assertEquals("0.5 \u00d7 onion", IngredientFormatter.format(new RecipeIngredient("onion", 0.5, "item")));
        assertEquals("0.5 tsp salt", IngredientFormatter.format(new RecipeIngredient("salt", 0.5, "tsp")));
    }
}
