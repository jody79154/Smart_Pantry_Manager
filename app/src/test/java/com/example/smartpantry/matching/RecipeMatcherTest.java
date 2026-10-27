package com.example.smartpantry.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.data.RecipeCatalog;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeMatcherTest {

    private static PantryItem pantry(String name, double quantity, String unit) {
        return new PantryItem(0, name, quantity, unit, null);
    }

    private static PantryItem pantry(String name, double quantity, String unit, String expiryDate) {
        return new PantryItem(0, name, quantity, unit, expiryDate);
    }

    private static RecipeIngredient need(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    private static Recipe recipe(String name, RecipeIngredient... ingredients) {
        return new Recipe(0, name, "1. Mix.\n2. Cook.\n3. Serve.", Arrays.asList(ingredients));
    }

    private static List<PantryItem> pantryOf(PantryItem... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    private static Recipe catalogRecipe(String name) {
        for (Recipe recipe : RecipeCatalog.all()) {
            if (recipe.getName().equals(name)) {
                return recipe;
            }
        }
        throw new AssertionError("Missing catalog recipe: " + name);
    }

    @Test
    public void exactCompleteMatchIsSuggested() {
        Recipe pancakes = recipe("Pancakes", need("flour", 200, "g"), need("milk", 300, "ml"), need("egg", 2, "item"));
        List<PantryItem> items = pantryOf(pantry("flour", 200, "g"), pantry("milk", 300, "ml"), pantry("egg", 2, "item"));
        assertTrue(RecipeMatcher.canMake(pancakes, items));
        assertEquals(1, RecipeMatcher.findMakeable(Arrays.asList(pancakes), items).size());
    }

    @Test
    public void oneMissingIngredientRejectsTheRecipe() {
        Recipe pancakes = recipe("Pancakes", need("flour", 200, "g"), need("milk", 300, "ml"), need("egg", 2, "item"));
        List<PantryItem> items = pantryOf(pantry("flour", 500, "g"), pantry("milk", 1, "l"));
        assertFalse(RecipeMatcher.canMake(pancakes, items));
        assertTrue(RecipeMatcher.findMakeable(Arrays.asList(pancakes), items).isEmpty());
    }

    @Test
    public void insufficientQuantityRejectsTheRecipe() {
        Recipe omelette = recipe("Omelette", need("egg", 3, "item"), need("cheese", 50, "g"));
        assertFalse(RecipeMatcher.canMake(omelette, pantryOf(pantry("egg", 2, "item"), pantry("cheese", 50, "g"))));
        assertFalse(RecipeMatcher.canMake(omelette, pantryOf(pantry("egg", 3, "item"), pantry("cheese", 49.9, "g"))));
    }

    @Test
    public void pantryQuantityGreaterThanRequiredIsAccepted() {
        Recipe omelette = recipe("Omelette", need("egg", 3, "item"), need("cheese", 50, "g"));
        assertTrue(RecipeMatcher.canMake(omelette, pantryOf(pantry("egg", 12, "item"), pantry("cheese", 500, "g"))));
    }

    @Test
    public void caseDifferencesAreIgnored() {
        Recipe omelette = recipe("Omelette", need("Egg", 3, "item"), need("CHEESE", 50, "g"));
        assertTrue(RecipeMatcher.canMake(omelette, pantryOf(pantry("eGG", 3, "ITEM"), pantry("cheese", 50, "G"))));
    }

    @Test
    public void extraWhitespaceIsIgnored() {
        Recipe pasta = recipe("Pasta", need("olive oil", 2, "tbsp"), need("spaghetti", 200, "g"));
        List<PantryItem> items = pantryOf(pantry("   Olive    Oil  ", 2, " tbsp "), pantry("\tspaghetti ", 200, "g"));
        assertTrue(RecipeMatcher.canMake(pasta, items));
    }

    @Test
    public void singularAndPluralNamesMatchEachWay() {
        Recipe salad = recipe("Salad", need("tomato", 2, "item"), need("onion", 1, "item"));
        assertTrue(RecipeMatcher.canMake(salad, pantryOf(pantry("tomatoes", 2, "item"), pantry("onions", 1, "item"))));

        Recipe salsa = recipe("Salsa", need("tomatoes", 2, "item"), need("onions", 1, "item"));
        assertTrue(RecipeMatcher.canMake(salsa, pantryOf(pantry("tomato", 2, "item"), pantry("onion", 1, "item"))));
    }

    @Test
    public void compatibleUnitsAreConvertedBeforeComparing() {
        Recipe cake = recipe("Cake", need("flour", 500, "g"), need("milk", 250, "ml"), need("oil", 1, "tbsp"));
        assertTrue(RecipeMatcher.canMake(cake, pantryOf(pantry("flour", 0.5, "kg"), pantry("milk", 0.25, "l"), pantry("oil", 3, "tsp"))));
        assertTrue(RecipeMatcher.canMake(cake, pantryOf(pantry("flour", 1, "kg"), pantry("milk", 2, "cup"), pantry("oil", 15, "ml"))));
        assertFalse(RecipeMatcher.canMake(cake, pantryOf(pantry("flour", 0.4, "kg"), pantry("milk", 2, "cup"), pantry("oil", 15, "ml"))));
        assertFalse(RecipeMatcher.canMake(cake, pantryOf(pantry("flour", 1, "kg"), pantry("milk", 2, "cup"), pantry("oil", 2, "tsp"))));
        assertFalse(RecipeMatcher.canMake(cake, pantryOf(pantry("flour", 1, "kg"), pantry("milk", 1, "cup"), pantry("oil", 15, "ml"))));

        Recipe toast = recipe("Toast", need("bread", 2, "item"));
        assertTrue(RecipeMatcher.canMake(toast, pantryOf(pantry("bread", 2, "piece"))));
        assertTrue(RecipeMatcher.canMake(toast, pantryOf(pantry("bread", 2, "whole"))));
    }

    @Test
    public void incompatibleUnitCategoriesAreRejectedWhenThereIsNoConversionData() {
        Recipe pasta = recipe("Pasta", need("spaghetti", 200, "g"));
        assertFalse(RecipeMatcher.canMake(pasta, pantryOf(pantry("spaghetti", 1000, "ml"))));
        assertFalse(RecipeMatcher.canMake(pasta, pantryOf(pantry("spaghetti", 10, "item"))));

        Recipe soup = recipe("Soup", need("stock", 500, "ml"));
        assertFalse(RecipeMatcher.canMake(soup, pantryOf(pantry("stock", 1, "kg"))));
        assertFalse(RecipeMatcher.canMake(soup, pantryOf(pantry("stock", 3, "item"))));
    }

    @Test
    public void duplicatePantryEntriesAreCombined() {
        Recipe omelette = recipe("Omelette", need("egg", 3, "item"));
        assertFalse(RecipeMatcher.canMake(omelette, pantryOf(pantry("egg", 2, "item"))));
        assertTrue(RecipeMatcher.canMake(omelette, pantryOf(pantry("egg", 1, "item"), pantry("eggs", 2, "item"))));

        Recipe bread = recipe("Bread", need("flour", 1, "kg"));
        assertTrue(RecipeMatcher.canMake(bread, pantryOf(pantry("flour", 600, "g"), pantry("Flour", 0.4, "kg"))));
    }

    @Test
    public void emptyPantryMatchesNothing() {
        Recipe toast = recipe("Toast", need("bread", 1, "item"));
        assertFalse(RecipeMatcher.canMake(toast, new ArrayList<>()));
        assertTrue(RecipeMatcher.findMakeable(RecipeCatalog.all(), new ArrayList<>()).isEmpty());
        assertTrue(RecipeMatcher.findMakeable(RecipeCatalog.all(), null).isEmpty());
    }

    @Test
    public void multipleIngredientRecipeNeedsEveryIngredient() {
        Recipe stew = recipe("Stew",
                need("beef", 500, "g"), need("onion", 2, "item"), need("carrot", 3, "item"),
                need("stock", 500, "ml"), need("salt", 1, "tsp"));
        List<PantryItem> items = pantryOf(pantry("beef", 500, "g"), pantry("onion", 2, "item"),
                pantry("carrot", 3, "item"), pantry("stock", 500, "ml"));
        assertFalse(RecipeMatcher.canMake(stew, items));

        items.add(pantry("salt", 1, "tsp"));
        assertTrue(RecipeMatcher.canMake(stew, items));

        items.remove(0);
        assertFalse(RecipeMatcher.canMake(stew, items));
    }

    @Test
    public void decimalQuantitiesAreSupported() {
        Recipe guacamole = recipe("Guacamole", need("onion", 0.5, "item"), need("salt", 0.5, "tsp"));
        assertTrue(RecipeMatcher.canMake(guacamole, pantryOf(pantry("onion", 0.5, "item"), pantry("salt", 0.5, "tsp"))));
        assertTrue(RecipeMatcher.canMake(guacamole, pantryOf(pantry("onion", 1.25, "item"), pantry("salt", 2.5, "tsp"))));
        assertFalse(RecipeMatcher.canMake(guacamole, pantryOf(pantry("onion", 0.4, "item"), pantry("salt", 0.5, "tsp"))));
    }

    @Test
    public void documentedAliasesCountAsTheSameIngredient() {
        Recipe stirFry = recipe("Stir fry", need("spring onion", 2, "item"), need("bell pepper", 1, "item"),
                need("vegetable oil", 1, "tbsp"));
        List<PantryItem> items = pantryOf(pantry("Scallions", 2, "item"), pantry("capsicum", 1, "item"),
                pantry("sunflower oil", 1, "tbsp"));
        assertTrue(RecipeMatcher.canMake(stirFry, items));
    }

    @Test
    public void ingredientListedTwiceInARecipeCannotDoubleCountThePantry() {
        Recipe doubled = recipe("Doubled", need("flour", 100, "g"), need("flours", 100, "g"));
        assertFalse(RecipeMatcher.canMake(doubled, pantryOf(pantry("flour", 150, "g"))));
        assertTrue(RecipeMatcher.canMake(doubled, pantryOf(pantry("flour", 200, "g"))));
    }

    @Test
    public void recipeWithNoIngredientsIsNeverSuggested() {
        assertFalse(RecipeMatcher.canMake(recipe("Nothing"), pantryOf(pantry("flour", 1, "kg"))));
    }

    @Test
    public void unusablePantryRowsAreIgnored() {
        Recipe toast = recipe("Toast", need("bread", 2, "item"));
        assertFalse(RecipeMatcher.canMake(toast, pantryOf(pantry("bread", 2, "bananas"))));
        assertFalse(RecipeMatcher.canMake(toast, pantryOf(pantry("bread", 0, "item"))));
        assertFalse(RecipeMatcher.canMake(toast, pantryOf(pantry("   ", 2, "item"))));
    }

    @Test
    public void floatingPointRoundingDoesNotRejectAnExactAmount() {
        Recipe halfSet = recipe("Half set", need("papaya", 0.8, "item"));
        assertTrue(RecipeMatcher.canMake(halfSet, pantryOf(pantry("papaya", 0.1, "item"), pantry("papaya", 0.7, "item"))));
        assertFalse(RecipeMatcher.canMake(halfSet, pantryOf(pantry("papaya", 0.1, "item"), pantry("papaya", 0.6, "item"))));
    }

    @Test
    public void findMakeableKeepsOnlyQualifyingRecipesInOriginalOrder() {
        Recipe toast = recipe("Toast", need("bread", 2, "item"));
        Recipe omelette = recipe("Omelette", need("egg", 3, "item"));
        Recipe eggToast = recipe("Egg toast", need("egg", 1, "item"), need("bread", 1, "item"));
        List<PantryItem> items = pantryOf(pantry("bread", 2, "item"), pantry("egg", 1, "item"));

        List<Recipe> result = RecipeMatcher.findMakeable(Arrays.asList(toast, omelette, eggToast), items);
        assertEquals(2, result.size());
        assertEquals("Toast", result.get(0).getName());
        assertEquals("Egg toast", result.get(1).getName());
    }

    @Test
    public void catalogRecipeWithFourOfFiveIngredientsIsNotSuggested() {
        Recipe pasta = catalogRecipe("Tomato Pasta");
        assertEquals(5, pasta.getIngredients().size());

        List<PantryItem> items = pantryOf(pantry("spaghetti", 200, "g"), pantry("canned tomatoes", 400, "g"),
                pantry("garlic", 2, "item"), pantry("olive oil", 2, "tbsp"));
        assertFalse(RecipeMatcher.canMake(pasta, items));
        assertFalse(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Tomato Pasta"));

        items.add(pantry("salt", 1, "tsp"));
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Tomato Pasta"));

        items.remove(items.size() - 1);
        assertFalse(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Tomato Pasta"));
    }

    @Test
    public void kilogramsSatisfyARecipeThatNeedsTablespoons() {
        Recipe sweetTea = recipe("Sweet tea", need("sugar", 1, "tbsp"));
        assertTrue(RecipeMatcher.canMake(sweetTea, pantryOf(pantry("sugar", 1, "kg"))));
        assertTrue(RecipeMatcher.canMake(sweetTea, pantryOf(pantry("sugar", 13, "g"))));
        assertFalse(RecipeMatcher.canMake(sweetTea, pantryOf(pantry("sugar", 12, "g"))));
    }

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 28);

    private static List<PantryItem> eggsOnToastPantry(String saltExpiry) {
        return pantryOf(pantry("egg", 3, "item"), pantry("bread", 2, "item"),
                pantry("butter", 10, "g"), pantry("salt", 1, "tsp", saltExpiry));
    }

    @Test
    public void expiredIngredientRemovesTheRecipesThatNeedIt() {
        assertFalse(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(),
                eggsOnToastPantry("2026-10-27"), TODAY), "Scrambled Eggs on Toast"));
        assertFalse(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(),
                eggsOnToastPantry("2020-01-01"), TODAY), "Scrambled Eggs on Toast"));
    }

    @Test
    public void ingredientsThatAreNotExpiredAreStillUsed() {
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(),
                eggsOnToastPantry("2026-10-28"), TODAY), "Scrambled Eggs on Toast"));
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(),
                eggsOnToastPantry("2026-10-29"), TODAY), "Scrambled Eggs on Toast"));
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(),
                eggsOnToastPantry(null), TODAY), "Scrambled Eggs on Toast"));
    }

    @Test
    public void onlyTheFreshRowsOfAnIngredientCount() {
        Recipe bread = recipe("Bread", need("flour", 200, "g"));
        assertFalse(RecipeMatcher.canMake(bread,
                pantryOf(pantry("flour", 100, "g", "2026-12-01"), pantry("flour", 500, "g", "2026-10-01")), TODAY));
        assertTrue(RecipeMatcher.canMake(bread,
                pantryOf(pantry("flour", 250, "g", "2026-12-01"), pantry("flour", 500, "g", "2026-10-01")), TODAY));
        assertTrue(RecipeMatcher.canMake(bread,
                pantryOf(pantry("flour", 100, "g", "2026-12-01"), pantry("flour", 100, "g")), TODAY));
    }

    @Test
    public void recipeComesBackWhenAFreshReplacementIsAdded() {
        List<PantryItem> items = eggsOnToastPantry("2026-10-20");
        assertFalse(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items, TODAY), "Scrambled Eggs on Toast"));
        items.add(pantry("salt", 1, "tsp", "2027-06-01"));
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items, TODAY), "Scrambled Eggs on Toast"));
    }

    @Test
    public void expiryIsIgnoredWhenNoDateIsGiven() {
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(),
                eggsOnToastPantry("2020-01-01")), "Scrambled Eggs on Toast"));
    }

    @Test
    public void unreadableExpiryDatesAreNotTreatedAsExpired() {
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(),
                eggsOnToastPantry("not a date"), TODAY), "Scrambled Eggs on Toast"));
    }

    @Test
    public void saltInGramsSatisfiesCatalogRecipesThatNeedTeaspoons() {
        List<PantryItem> items = pantryOf(pantry("egg", 3, "item"), pantry("bread", 2, "item"),
                pantry("butter", 10, "g"), pantry("salt", 3, "g"));
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Scrambled Eggs on Toast"));

        items.set(3, pantry("salt", 2.9, "g"));
        assertFalse(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Scrambled Eggs on Toast"));

        items.set(3, pantry("salt", 1, "tsp"));
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Scrambled Eggs on Toast"));
        items.set(3, pantry("salt", 0.003, "kg"));
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Scrambled Eggs on Toast"));
    }

    @Test
    public void catalogPancakesAreSuggestedWhenSugarAndOilAreStoredByWeightOrVolume() {
        List<PantryItem> items = pantryOf(pantry("flour", 1, "kg"), pantry("milk", 1, "l"),
                pantry("egg", 2, "item"), pantry("sugar", 1, "kg"), pantry("vegetable oil", 0.5, "l"));
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Simple Pancakes"));

        items.set(3, pantry("sugar", 5, "g"));
        assertFalse(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Simple Pancakes"));
    }

    @Test
    public void cupsAndGramsOfTheSameIngredientAreComparedAndCombined() {
        Recipe bread = recipe("Bread", need("flour", 200, "g"));
        assertTrue(RecipeMatcher.canMake(bread, pantryOf(pantry("flour", 2, "cup"))));
        assertFalse(RecipeMatcher.canMake(bread, pantryOf(pantry("flour", 1, "cup"))));
        assertTrue(RecipeMatcher.canMake(bread, pantryOf(pantry("flour", 100, "g"), pantry("flour", 1, "cup"))));
        assertTrue(RecipeMatcher.canMake(recipe("Cups", need("flour", 1, "cup")), pantryOf(pantry("flour", 0.2, "kg"))));
    }

    @Test
    public void countedIngredientsAreComparedWithWeights() {
        Recipe mash = recipe("Mash", need("potato", 800, "g"));
        assertTrue(RecipeMatcher.canMake(mash, pantryOf(pantry("potato", 5, "item"))));
        assertFalse(RecipeMatcher.canMake(mash, pantryOf(pantry("potato", 4, "item"))));

        Recipe soup = recipe("Soup", need("potato", 4, "item"));
        assertTrue(RecipeMatcher.canMake(soup, pantryOf(pantry("potato", 1, "kg"))));
        assertFalse(RecipeMatcher.canMake(soup, pantryOf(pantry("potato", 600, "g"))));

        Recipe omelette = recipe("Omelette", need("egg", 3, "item"));
        assertTrue(RecipeMatcher.canMake(omelette, pantryOf(pantry("egg", 150, "g"))));
        assertFalse(RecipeMatcher.canMake(omelette, pantryOf(pantry("egg", 149, "g"))));
    }

    @Test
    public void catalogMashedPotatoesAcceptPotatoesCountedInItems() {
        List<PantryItem> items = pantryOf(pantry("potato", 5, "item"), pantry("milk", 1, "cup"),
                pantry("butter", 100, "g"), pantry("salt", 2, "tsp"));
        assertTrue(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Mashed Potatoes"));

        items.set(0, pantry("potato", 3, "item"));
        assertFalse(containsName(RecipeMatcher.findMakeable(RecipeCatalog.all(), items), "Mashed Potatoes"));
    }

    @Test
    public void ingredientsWithoutConversionDataStayStrict() {
        Recipe pasta = recipe("Pasta", need("spaghetti", 200, "g"));
        assertFalse(RecipeMatcher.canMake(pasta, pantryOf(pantry("spaghetti", 5, "cup"))));
        Recipe mystery = recipe("Mystery", need("dragon fruit", 2, "tbsp"));
        assertFalse(RecipeMatcher.canMake(mystery, pantryOf(pantry("dragon fruit", 1, "kg"))));
        assertTrue(RecipeMatcher.canMake(mystery, pantryOf(pantry("dragon fruit", 6, "tsp"))));
        assertFalse(RecipeMatcher.canMake(mystery, pantryOf(pantry("dragon fruit", 4, "tsp"))));
    }

    private static boolean containsName(List<Recipe> recipes, String name) {
        for (Recipe recipe : recipes) {
            if (recipe.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
