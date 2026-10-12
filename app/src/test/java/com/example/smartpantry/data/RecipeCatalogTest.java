package com.example.smartpantry.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import com.example.smartpantry.util.InputValidator;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RecipeCatalogTest {

    private final List<Recipe> recipes = RecipeCatalog.all();

    @Test
    public void catalogContainsExactlyTwentyRecipes() {
        assertEquals(20, recipes.size());
    }

    @Test
    public void recipeNamesAreUniqueIgnoringCase() {
        Set<String> names = new HashSet<>();
        for (Recipe recipe : recipes) {
            assertTrue("Duplicate name: " + recipe.getName(),
                    names.add(recipe.getName().toLowerCase(Locale.ROOT)));
        }
    }

    @Test
    public void everyRecipeHasNameStepsAndIngredients() {
        for (Recipe recipe : recipes) {
            assertFalse(recipe.getName().trim().isEmpty());
            assertTrue(recipe.getName() + " needs at least 3 steps",
                    recipe.getSteps().split("\n").length >= 3);
            int count = recipe.getIngredients().size();
            assertTrue(recipe.getName() + " has " + count + " ingredients", count >= 3 && count <= 8);
        }
    }

    @Test
    public void everyIngredientIsValid() {
        List<String> units = Arrays.asList(InputValidator.UNITS);
        for (Recipe recipe : recipes) {
            Set<String> seen = new HashSet<>();
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                String where = recipe.getName() + " / " + ingredient.getName();
                assertEquals(where, ingredient.getName().trim().toLowerCase(Locale.ROOT), ingredient.getName());
                assertTrue(where, ingredient.getQuantity() > 0);
                assertTrue(where + " unit " + ingredient.getUnit(), units.contains(ingredient.getUnit()));
                assertTrue(where + " listed twice", seen.add(ingredient.getName()));
            }
        }
    }

    @Test
    public void catalogCoversVariedUnitsAndDecimalQuantities() {
        Set<String> unitsUsed = new HashSet<>();
        boolean hasDecimal = false;
        boolean hasLargeRecipe = false;
        for (Recipe recipe : recipes) {
            hasLargeRecipe |= recipe.getIngredients().size() >= 6;
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                unitsUsed.add(ingredient.getUnit());
                hasDecimal |= ingredient.getQuantity() != Math.floor(ingredient.getQuantity());
            }
        }
        assertTrue(unitsUsed.containsAll(Arrays.asList("g", "ml", "tsp", "tbsp", "item")));
        assertTrue(hasDecimal);
        assertTrue(hasLargeRecipe);
    }
}
