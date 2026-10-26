package com.example.smartpantry.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.data.RecipeCatalog;
import com.example.smartpantry.matching.IngredientConversions.Measure;
import com.example.smartpantry.matching.UnitConverter.Unit;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import org.junit.Test;

public class IngredientConversionsTest {

    private static final double DELTA = 1e-9;

    @Test
    public void weightsAreExpressedInGrams() {
        Measure measure = IngredientConversions.toComparable("flour", 1, Unit.KILOGRAM);
        assertEquals("flour|MASS", measure.getKey());
        assertEquals(1000, measure.getAmount(), DELTA);
    }

    @Test
    public void volumesOfKnownIngredientsBecomeGrams() {
        Measure tablespoon = IngredientConversions.toComparable("honey", 1, Unit.TABLESPOON);
        assertEquals("honey|MASS", tablespoon.getKey());
        assertEquals(15 * 1.42, tablespoon.getAmount(), DELTA);

        Measure cup = IngredientConversions.toComparable("flour", 1, Unit.CUP);
        assertEquals(240 * 0.53, cup.getAmount(), DELTA);
    }

    @Test
    public void countsOfKnownIngredientsBecomeGrams() {
        Measure eggs = IngredientConversions.toComparable("egg", 2, Unit.ITEM);
        assertEquals("egg|MASS", eggs.getKey());
        assertEquals(100, eggs.getAmount(), DELTA);
    }

    @Test
    public void unknownIngredientsKeepTheirOwnUnitCategory() {
        assertEquals("widget|VOLUME", IngredientConversions.toComparable("widget", 1, Unit.TABLESPOON).getKey());
        assertEquals("widget|COUNT", IngredientConversions.toComparable("widget", 1, Unit.ITEM).getKey());
        assertEquals("widget|MASS", IngredientConversions.toComparable("widget", 1, Unit.GRAM).getKey());
        assertEquals(15, IngredientConversions.toComparable("widget", 1, Unit.TABLESPOON).getAmount(), DELTA);
        assertNotEquals(IngredientConversions.toComparable("widget", 1, Unit.GRAM).getKey(),
                IngredientConversions.toComparable("widget", 1, Unit.TABLESPOON).getKey());
    }

    @Test
    public void volumeOnlyIngredientsKeepCountsSeparate() {
        assertTrue(IngredientConversions.hasDensity("milk"));
        assertFalse(IngredientConversions.hasItemWeight("milk"));
        assertEquals("milk|COUNT", IngredientConversions.toComparable("milk", 2, Unit.ITEM).getKey());
    }

    @Test
    public void everyCatalogIngredientMeasuredByVolumeHasADensity() {
        for (Recipe recipe : RecipeCatalog.all()) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                Unit unit = UnitConverter.parse(ingredient.getUnit());
                String name = IngredientNormalizer.normalize(ingredient.getName());
                if (unit.getCategory() == UnitConverter.Category.VOLUME) {
                    assertTrue(recipe.getName() + " / " + name, IngredientConversions.hasDensity(name));
                }
            }
        }
    }

    @Test
    public void everyCatalogIngredientMeasuredByCountHasAnItemWeightExceptWhereItIsOnlyEverCounted() {
        for (Recipe recipe : RecipeCatalog.all()) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                Unit unit = UnitConverter.parse(ingredient.getUnit());
                String name = IngredientNormalizer.normalize(ingredient.getName());
                if (unit.getCategory() == UnitConverter.Category.COUNT) {
                    assertTrue(recipe.getName() + " / " + name, IngredientConversions.hasItemWeight(name));
                }
            }
        }
    }
}
