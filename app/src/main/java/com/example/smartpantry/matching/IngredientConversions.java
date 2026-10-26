package com.example.smartpantry.matching;

import com.example.smartpantry.matching.UnitConverter.Unit;

import java.util.HashMap;
import java.util.Map;

public final class IngredientConversions {

    public static final class Measure {
        private final String key;
        private final double amount;

        Measure(String key, double amount) {
            this.key = key;
            this.amount = amount;
        }

        public String getKey() {
            return key;
        }

        public double getAmount() {
            return amount;
        }
    }

    private static final Map<String, Double> GRAMS_PER_ML = new HashMap<>();
    private static final Map<String, Double> GRAMS_PER_ITEM = new HashMap<>();

    static {
        // Grams per millilitre
        density("flour", 0.53);
        density("sugar", 0.84);
        density("butter", 0.95);
        density("salt", 1.2);
        density("milk", 1.03);
        density("cream", 1.0);
        density("yogurt", 1.03);
        density("water", 1.0);
        density("vegetable stock", 1.0);
        density("canned tomato", 1.0);
        density("olive oil", 0.91);
        density("vegetable oil", 0.92);
        density("soy sauce", 1.2);
        density("honey", 1.42);
        density("mayonnaise", 0.95);
        density("oat", 0.34);
        density("rice", 0.8);
        density("cheese", 0.47);
        density("cinnamon", 0.53);
        density("curry powder", 0.45);
        density("black pepper", 0.46);
        density("baking soda", 0.92);
        density("cornstarch", 0.53);

        // Grams per item
        perItem("egg", 50);
        perItem("onion", 150);
        perItem("potato", 170);
        perItem("carrot", 60);
        perItem("tomato", 120);
        perItem("banana", 120);
        perItem("lemon", 70);
        perItem("lime", 45);
        perItem("avocado", 150);
        perItem("bell pepper", 150);
        perItem("spring onion", 15);
        perItem("garlic", 5);
        perItem("bread", 30);
        perItem("tortilla", 40);
    }

    private IngredientConversions() {
    }

    public static boolean hasDensity(String normalizedName) {
        return GRAMS_PER_ML.containsKey(normalizedName);
    }

    public static boolean hasItemWeight(String normalizedName) {
        return GRAMS_PER_ITEM.containsKey(normalizedName);
    }

    public static Measure toComparable(String normalizedName, double quantity, Unit unit) {
        double base = UnitConverter.toBase(quantity, unit);
        switch (unit.getCategory()) {
            case VOLUME:
                Double density = GRAMS_PER_ML.get(normalizedName);
                if (density != null) {
                    return new Measure(massKey(normalizedName), base * density);
                }
                break;
            case COUNT:
                Double itemWeight = GRAMS_PER_ITEM.get(normalizedName);
                if (itemWeight != null) {
                    return new Measure(massKey(normalizedName), base * itemWeight);
                }
                break;
            default:
                return new Measure(massKey(normalizedName), base);
        }
        return new Measure(normalizedName + "|" + unit.getCategory(), base);
    }

    private static String massKey(String normalizedName) {
        return normalizedName + "|" + UnitConverter.Category.MASS;
    }

    private static void density(String name, double gramsPerMl) {
        GRAMS_PER_ML.put(name, gramsPerMl);
    }

    private static void perItem(String name, double grams) {
        GRAMS_PER_ITEM.put(name, grams);
    }
}
