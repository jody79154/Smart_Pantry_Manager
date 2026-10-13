package com.example.smartpantry.matching;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class UnitConverter {

    public enum Category {
        MASS, VOLUME, COUNT
    }

    public enum Unit {
        GRAM(Category.MASS, 1),
        KILOGRAM(Category.MASS, 1000),
        MILLILITRE(Category.VOLUME, 1),
        LITRE(Category.VOLUME, 1000),
        TEASPOON(Category.VOLUME, 5),
        TABLESPOON(Category.VOLUME, 15),
        CUP(Category.VOLUME, 240),
        ITEM(Category.COUNT, 1);

        private final Category category;
        private final double baseFactor;

        Unit(Category category, double baseFactor) {
            this.category = category;
            this.baseFactor = baseFactor;
        }

        public Category getCategory() {
            return category;
        }

        public double getBaseFactor() {
            return baseFactor;
        }
    }

    private static final Map<String, Unit> ALIASES = new HashMap<>();

    static {
        register(Unit.GRAM, "g", "gm", "gms", "gram", "grams");
        register(Unit.KILOGRAM, "kg", "kgs", "kilo", "kilos", "kilogram", "kilograms");
        register(Unit.MILLILITRE, "ml", "mls", "millilitre", "millilitres", "milliliter", "milliliters");
        register(Unit.LITRE, "l", "ltr", "litre", "litres", "liter", "liters");
        register(Unit.TEASPOON, "tsp", "tsps", "teaspoon", "teaspoons");
        register(Unit.TABLESPOON, "tbsp", "tbsps", "tbs", "tablespoon", "tablespoons");
        register(Unit.CUP, "cup", "cups");
        register(Unit.ITEM, "item", "items", "piece", "pieces", "pc", "pcs", "whole",
                "unit", "units", "each", "clove", "cloves");
    }

    private UnitConverter() {
    }

    public static Unit parse(String raw) {
        if (raw == null) {
            return null;
        }
        String key = raw.trim().toLowerCase(Locale.ROOT);
        if (key.endsWith(".")) {
            key = key.substring(0, key.length() - 1);
        }
        return ALIASES.get(key);
    }

    public static boolean isCompatible(Unit first, Unit second) {
        return first != null && second != null && first.getCategory() == second.getCategory();
    }

    public static double toBase(double quantity, Unit unit) {
        return quantity * unit.getBaseFactor();
    }

    private static void register(Unit unit, String... names) {
        for (String name : names) {
            ALIASES.put(name, unit);
        }
    }
}
