package com.example.smartpantry.matching;

import static org.junit.Assert.assertEquals;

import com.example.smartpantry.data.RecipeCatalog;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import org.junit.Test;

public class IngredientNormalizerTest {

    private static void assertNormalizes(String expected, String... inputs) {
        for (String input : inputs) {
            assertEquals("input: " + input, expected, IngredientNormalizer.normalize(input));
        }
    }

    @Test
    public void caseAndWhitespaceAreIgnored() {
        assertNormalizes("tomato", "Tomato", "  TOMATO ", "tomato\t");
        assertNormalizes("olive oil", "Olive   Oil", "  olive oil  ");
    }

    @Test
    public void simplePluralsAreSingularized() {
        assertNormalizes("tomato", "tomatoes");
        assertNormalizes("potato", "potatoes");
        assertNormalizes("egg", "eggs");
        assertNormalizes("onion", "onions");
        assertNormalizes("banana", "bananas");
        assertNormalizes("avocado", "avocados");
        assertNormalizes("pea", "peas");
        assertNormalizes("chickpea", "chickpeas");
        assertNormalizes("noodle", "noodles");
        assertNormalizes("oat", "oats");
    }

    @Test
    public void irregularAndSpecialPluralsAreHandled() {
        assertNormalizes("berry", "berries");
        assertNormalizes("strawberry", "strawberries");
        assertNormalizes("peach", "peaches");
        assertNormalizes("radish", "radishes");
        assertNormalizes("sandwich", "sandwiches");
        assertNormalizes("bay leaf", "bay leaves");
        assertNormalizes("cookie", "cookie", "cookies");
        assertNormalizes("chili", "chilies", "chili");
    }

    @Test
    public void wordsThatLookPluralAreLeftAlone() {
        assertNormalizes("molasses", "molasses");
        assertNormalizes("hummus", "hummus");
        assertNormalizes("couscous", "couscous");
        assertNormalizes("asparagus", "asparagus");
        assertNormalizes("watercress", "watercress");
    }

    @Test
    public void onlyTheLastWordIsSingularized() {
        assertNormalizes("red onion", "red onions");
        assertNormalizes("sweet potato", "Sweet Potatoes");
        assertNormalizes("chicken breast", "chicken breasts");
    }

    @Test
    public void documentedAliasesMapToOneName() {
        assertNormalizes("spring onion", "scallion", "Scallions", "green onions", "spring onions");
        assertNormalizes("bell pepper", "capsicum", "Bell Peppers", "sweet pepper");
        assertNormalizes("zucchini", "courgette", "courgettes");
        assertNormalizes("eggplant", "aubergine");
        assertNormalizes("chickpea", "garbanzo beans", "chick peas", "Chickpeas");
        assertNormalizes("garlic", "garlic clove", "garlic cloves", "garlic");
        assertNormalizes("flour", "plain flour", "all-purpose flour", "All Purpose Flour");
        assertNormalizes("yogurt", "yoghurt", "Plain Yogurt", "natural yoghurt");
        assertNormalizes("ground beef", "minced beef", "beef mince", "Ground Beef");
        assertNormalizes("canned tomato", "tinned tomatoes", "canned tomatoes");
        assertNormalizes("vegetable oil", "sunflower oil", "cooking oil");
        assertNormalizes("milk", "whole milk", "full cream milk");
        assertNormalizes("chili", "chilli", "chillies");
    }

    @Test
    public void punctuationAndBracketedNotesAreRemoved() {
        assertNormalizes("tomato", "Tomato (large)", "tomato!", "tomato.");
        assertNormalizes("flour", "all-purpose flour");
    }

    @Test
    public void emptyAndSymbolOnlyInputGivesEmptyString() {
        assertNormalizes("", null, "", "   ", "123", "---");
    }

    @Test
    public void normalizingTwiceChangesNothing() {
        for (Recipe recipe : RecipeCatalog.all()) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                String once = IngredientNormalizer.normalize(ingredient.getName());
                assertEquals(ingredient.getName(), once, IngredientNormalizer.normalize(once));
            }
        }
    }

    @Test
    public void everyCatalogIngredientNormalizesToSomething() {
        for (Recipe recipe : RecipeCatalog.all()) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                assertEquals(false, IngredientNormalizer.normalize(ingredient.getName()).isEmpty());
            }
        }
    }
}
