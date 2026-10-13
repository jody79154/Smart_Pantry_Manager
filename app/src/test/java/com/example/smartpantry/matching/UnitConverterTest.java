package com.example.smartpantry.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.matching.UnitConverter.Unit;
import com.example.smartpantry.util.InputValidator;

import org.junit.Test;

public class UnitConverterTest {

    private static final double DELTA = 1e-9;

    @Test
    public void commonSpellingsAreRecognised() {
        assertEquals(Unit.GRAM, UnitConverter.parse("g"));
        assertEquals(Unit.GRAM, UnitConverter.parse("Grams"));
        assertEquals(Unit.KILOGRAM, UnitConverter.parse(" KG "));
        assertEquals(Unit.KILOGRAM, UnitConverter.parse("kilos"));
        assertEquals(Unit.MILLILITRE, UnitConverter.parse("ml"));
        assertEquals(Unit.MILLILITRE, UnitConverter.parse("Millilitres"));
        assertEquals(Unit.LITRE, UnitConverter.parse("l"));
        assertEquals(Unit.LITRE, UnitConverter.parse("liters"));
        assertEquals(Unit.TEASPOON, UnitConverter.parse("tsp"));
        assertEquals(Unit.TEASPOON, UnitConverter.parse("Teaspoons"));
        assertEquals(Unit.TABLESPOON, UnitConverter.parse("tbsp"));
        assertEquals(Unit.TABLESPOON, UnitConverter.parse("tablespoon"));
        assertEquals(Unit.TABLESPOON, UnitConverter.parse("tbsp."));
        assertEquals(Unit.CUP, UnitConverter.parse("cups"));
    }

    @Test
    public void itemPieceAndWholeAreTheSameUnit() {
        assertEquals(Unit.ITEM, UnitConverter.parse("item"));
        assertEquals(Unit.ITEM, UnitConverter.parse("piece"));
        assertEquals(Unit.ITEM, UnitConverter.parse("Pieces"));
        assertEquals(Unit.ITEM, UnitConverter.parse("whole"));
        assertEquals(Unit.ITEM, UnitConverter.parse("pcs"));
        assertEquals(Unit.ITEM, UnitConverter.parse("cloves"));
    }

    @Test
    public void unknownOrEmptyUnitsReturnNull() {
        assertNull(UnitConverter.parse(null));
        assertNull(UnitConverter.parse(""));
        assertNull(UnitConverter.parse("   "));
        assertNull(UnitConverter.parse("bananas"));
        assertNull(UnitConverter.parse("oz"));
    }

    @Test
    public void everyUnitOfferedByTheFormIsRecognised() {
        for (String unit : InputValidator.UNITS) {
            assertNotNull(unit, UnitConverter.parse(unit));
        }
    }

    @Test
    public void massConvertsToGrams() {
        assertEquals(1000, UnitConverter.toBase(1, Unit.KILOGRAM), DELTA);
        assertEquals(250, UnitConverter.toBase(0.25, Unit.KILOGRAM), DELTA);
        assertEquals(2.5, UnitConverter.toBase(2.5, Unit.GRAM), DELTA);
    }

    @Test
    public void volumeConvertsToMillilitres() {
        assertEquals(2000, UnitConverter.toBase(2, Unit.LITRE), DELTA);
        assertEquals(15, UnitConverter.toBase(1, Unit.TABLESPOON), DELTA);
        assertEquals(5, UnitConverter.toBase(1, Unit.TEASPOON), DELTA);
        assertEquals(240, UnitConverter.toBase(1, Unit.CUP), DELTA);
        assertEquals(UnitConverter.toBase(1, Unit.TABLESPOON), UnitConverter.toBase(3, Unit.TEASPOON), DELTA);
    }

    @Test
    public void countsStayAsItems() {
        assertEquals(3, UnitConverter.toBase(3, Unit.ITEM), DELTA);
    }

    @Test
    public void unitsInTheSameCategoryAreCompatible() {
        assertTrue(UnitConverter.isCompatible(Unit.GRAM, Unit.KILOGRAM));
        assertTrue(UnitConverter.isCompatible(Unit.MILLILITRE, Unit.TEASPOON));
        assertTrue(UnitConverter.isCompatible(Unit.TABLESPOON, Unit.LITRE));
        assertTrue(UnitConverter.isCompatible(UnitConverter.parse("piece"), UnitConverter.parse("whole")));
    }

    @Test
    public void unitsInDifferentCategoriesAreIncompatible() {
        assertFalse(UnitConverter.isCompatible(Unit.GRAM, Unit.MILLILITRE));
        assertFalse(UnitConverter.isCompatible(Unit.ITEM, Unit.GRAM));
        assertFalse(UnitConverter.isCompatible(Unit.CUP, Unit.ITEM));
        assertFalse(UnitConverter.isCompatible(Unit.GRAM, null));
        assertFalse(UnitConverter.isCompatible(null, null));
    }
}
