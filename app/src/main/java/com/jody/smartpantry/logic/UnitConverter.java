package com.jody.smartpantry.logic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class UnitConverter {

    public enum UnitCategory {
        MASS, VOLUME, COUNT
    }

    // Base unit per category: grams for mass, millilitres for volume, item for count.
    private static final Map<String, UnitCategory> CATEGORY_BY_UNIT = new HashMap<>();
    private static final Map<String, Double> FACTOR_TO_BASE = new HashMap<>();

    static {
        register("g", UnitCategory.MASS, 1.0);
        register("gram", UnitCategory.MASS, 1.0);
        register("grams", UnitCategory.MASS, 1.0);
        register("kg", UnitCategory.MASS, 1000.0);
        register("kilogram", UnitCategory.MASS, 1000.0);
        register("kilograms", UnitCategory.MASS, 1000.0);

        register("ml", UnitCategory.VOLUME, 1.0);
        register("milliliter", UnitCategory.VOLUME, 1.0);
        register("milliliters", UnitCategory.VOLUME, 1.0);
        register("millilitre", UnitCategory.VOLUME, 1.0);
        register("millilitres", UnitCategory.VOLUME, 1.0);
        register("l", UnitCategory.VOLUME, 1000.0);
        register("liter", UnitCategory.VOLUME, 1000.0);
        register("liters", UnitCategory.VOLUME, 1000.0);
        register("litre", UnitCategory.VOLUME, 1000.0);
        register("litres", UnitCategory.VOLUME, 1000.0);
        register("tsp", UnitCategory.VOLUME, 5.0);
        register("teaspoon", UnitCategory.VOLUME, 5.0);
        register("teaspoons", UnitCategory.VOLUME, 5.0);
        register("tbsp", UnitCategory.VOLUME, 15.0);
        register("tablespoon", UnitCategory.VOLUME, 15.0);
        register("tablespoons", UnitCategory.VOLUME, 15.0);

        register("item", UnitCategory.COUNT, 1.0);
        register("items", UnitCategory.COUNT, 1.0);
        register("piece", UnitCategory.COUNT, 1.0);
        register("pieces", UnitCategory.COUNT, 1.0);
        register("whole", UnitCategory.COUNT, 1.0);
    }

    private static void register(String unit, UnitCategory category, double factorToBase) {
        CATEGORY_BY_UNIT.put(unit, category);
        FACTOR_TO_BASE.put(unit, factorToBase);
    }

    private UnitConverter() {
    }

    /** Returns the unit's category, or null if the unit isn't recognised. */
    public static UnitCategory categoryOf(String unit) {
        if (unit == null) {
            return null;
        }
        return CATEGORY_BY_UNIT.get(unit.trim().toLowerCase(Locale.US));
    }

    /** Converts a quantity into its category's base unit. Caller must check categoryOf() first. */
    public static BigDecimal toBaseQuantity(double quantity, String unit) {
        String key = unit.trim().toLowerCase(Locale.US);
        Double factor = FACTOR_TO_BASE.get(key);
        if (factor == null) {
            throw new IllegalArgumentException("Unrecognised unit: " + unit);
        }
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(factor));
    }
}
