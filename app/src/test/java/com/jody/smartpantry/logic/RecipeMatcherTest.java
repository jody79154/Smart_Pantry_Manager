package com.jody.smartpantry.logic;

import com.jody.smartpantry.model.PantryItem;
import com.jody.smartpantry.model.Recipe;
import com.jody.smartpantry.model.RecipeIngredient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecipeMatcherTest {

    private static long nextId = 1;

    private PantryItem pantryItem(String name, double quantity, String unit) {
        return new PantryItem(nextId++, name, quantity, unit, null);
    }

    private RecipeIngredient requirement(String name, double quantity, String unit) {
        return new RecipeIngredient(nextId++, 1, name, quantity, unit);
    }

    private Recipe recipe(RecipeIngredient... ingredients) {
        return new Recipe(1, "Test Recipe", "Steps", Arrays.asList(ingredients));
    }

    @Test
    public void exactCompleteMatch_recipeQualifies() {
        Recipe recipe = recipe(
                requirement("tomato", 3, "item"),
                requirement("pasta", 200, "g"));
        List<PantryItem> pantry = Arrays.asList(
                pantryItem("tomato", 3, "item"),
                pantryItem("pasta", 200, "g"));

        assertTrue(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void oneMissingIngredient_recipeExcluded() {
        Recipe recipe = recipe(
                requirement("tomato", 3, "item"),
                requirement("pasta", 200, "g"));
        List<PantryItem> pantry = Collections.singletonList(pantryItem("tomato", 3, "item"));

        assertFalse(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void insufficientQuantity_recipeExcluded() {
        Recipe recipe = recipe(requirement("tomato", 3, "item"));
        List<PantryItem> pantry = Collections.singletonList(pantryItem("tomato", 2, "item"));

        assertFalse(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void pantryQuantityGreaterThanRequired_recipeQualifies() {
        Recipe recipe = recipe(requirement("tomato", 3, "item"));
        List<PantryItem> pantry = Collections.singletonList(pantryItem("tomato", 10, "item"));

        assertTrue(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void caseDifferences_stillMatch() {
        Recipe recipe = recipe(requirement("Tomato", 2, "item"));
        List<PantryItem> pantry = Collections.singletonList(pantryItem("TOMATO", 2, "item"));

        assertTrue(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void extraWhitespace_stillMatches() {
        Recipe recipe = recipe(requirement("  tomato ", 2, "item"));
        List<PantryItem> pantry = Collections.singletonList(pantryItem("tomato   ", 2, "item"));

        assertTrue(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void singularPluralDifferences_stillMatch() {
        Recipe recipe = recipe(requirement("tomato", 2, "item"));
        List<PantryItem> pantry = Collections.singletonList(pantryItem("tomatoes", 2, "item"));

        assertTrue(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void compatibleUnitConversion_recipeQualifies() {
        Recipe recipe = recipe(requirement("olive oil", 15, "ml"));
        List<PantryItem> pantry = Collections.singletonList(pantryItem("olive oil", 1, "tbsp"));

        assertTrue(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void incompatibleUnits_recipeExcluded() {
        Recipe recipe = recipe(requirement("flour", 200, "g"));
        List<PantryItem> pantry = Collections.singletonList(pantryItem("flour", 200, "ml"));

        assertFalse(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void unrecognisedUnit_recipeExcludedRatherThanCrashing() {
        Recipe recipe = recipe(requirement("flour", 200, "g"));
        List<PantryItem> pantry = Collections.singletonList(pantryItem("flour", 3, "bags"));

        assertFalse(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void duplicatePantryQuantitiesCombineToSufficient() {
        Recipe recipe = recipe(requirement("flour", 300, "g"));
        List<PantryItem> pantry = Arrays.asList(
                pantryItem("flour", 100, "g"),
                pantryItem("flour", 0.1, "kg"),
                pantryItem("flour", 100, "g"));

        assertTrue(RecipeMatcher.matches(recipe, pantry));
    }

    @Test
    public void emptyPantry_noRecipesMatch() {
        List<Recipe> recipes = Arrays.asList(
                recipe(requirement("tomato", 1, "item")),
                recipe(requirement("egg", 2, "item")));

        List<Recipe> matches = RecipeMatcher.findMatchingRecipes(recipes, new ArrayList<>());

        assertTrue(matches.isEmpty());
    }

    @Test
    public void multipleIngredientRecipeRequiresEveryIngredient() {
        Recipe recipe = recipe(
                requirement("chicken breast", 300, "g"),
                requirement("bell pepper", 1, "item"),
                requirement("onion", 1, "item"),
                requirement("soy sauce", 30, "ml"),
                requirement("garlic", 2, "item"));

        List<PantryItem> fullPantry = Arrays.asList(
                pantryItem("chicken breast", 300, "g"),
                pantryItem("bell pepper", 1, "item"),
                pantryItem("onion", 1, "item"),
                pantryItem("soy sauce", 30, "ml"),
                pantryItem("garlic", 2, "item"));
        assertTrue(RecipeMatcher.matches(recipe, fullPantry));

        List<PantryItem> missingGarlic = Arrays.asList(
                pantryItem("chicken breast", 300, "g"),
                pantryItem("bell pepper", 1, "item"),
                pantryItem("onion", 1, "item"),
                pantryItem("soy sauce", 30, "ml"));
        assertFalse(RecipeMatcher.matches(recipe, missingGarlic));
    }

    @Test
    public void decimalQuantities_handledCorrectly() {
        Recipe recipe = recipe(requirement("rice", 1.5, "kg"));
        List<PantryItem> sufficientPantry = Collections.singletonList(pantryItem("rice", 1500, "g"));
        List<PantryItem> insufficientPantry = Collections.singletonList(pantryItem("rice", 1499.9, "g"));

        assertTrue(RecipeMatcher.matches(recipe, sufficientPantry));
        assertFalse(RecipeMatcher.matches(recipe, insufficientPantry));
    }
}
