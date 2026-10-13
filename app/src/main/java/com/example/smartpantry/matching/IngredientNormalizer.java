package com.example.smartpantry.matching;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class IngredientNormalizer {

    private static final Set<String> UNCHANGED = new HashSet<>();
    private static final Map<String, String> IRREGULAR_PLURALS = new HashMap<>();
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        UNCHANGED.add("molasses");

        IRREGULAR_PLURALS.put("leaves", "leaf");
        IRREGULAR_PLURALS.put("loaves", "loaf");
        IRREGULAR_PLURALS.put("halves", "half");
        IRREGULAR_PLURALS.put("knives", "knife");
        IRREGULAR_PLURALS.put("cookies", "cookie");
        IRREGULAR_PLURALS.put("brownies", "brownie");
        IRREGULAR_PLURALS.put("smoothies", "smoothie");
        IRREGULAR_PLURALS.put("veggies", "veggie");
        IRREGULAR_PLURALS.put("pies", "pie");
        IRREGULAR_PLURALS.put("chilies", "chili");
        IRREGULAR_PLURALS.put("chillies", "chilli");

        alias("spring onion", "scallion", "green onion");
        alias("bell pepper", "capsicum", "sweet pepper");
        alias("zucchini", "courgette");
        alias("eggplant", "aubergine");
        alias("arugula", "rocket");
        alias("chickpea", "garbanzo", "garbanzo bean", "chick pea");
        alias("chili", "chilli");
        alias("garlic", "garlic clove");
        alias("flour", "plain flour", "all purpose flour");
        alias("sugar", "caster sugar", "granulated sugar", "white sugar");
        alias("baking soda", "bicarbonate of soda", "bicarb");
        alias("cornstarch", "cornflour");
        alias("cream", "double cream", "heavy cream");
        alias("milk", "whole milk", "full cream milk");
        alias("yogurt", "yoghurt", "plain yogurt", "natural yogurt", "plain yoghurt", "natural yoghurt");
        alias("ground beef", "minced beef", "beef mince");
        alias("canned tomato", "tinned tomato");
        alias("canned tuna", "tinned tuna");
        alias("vegetable oil", "sunflower oil", "canola oil", "cooking oil");
        alias("olive oil", "extra virgin olive oil");
        alias("black pepper", "ground black pepper");
    }

    private IngredientNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String text = raw.toLowerCase(Locale.ROOT)
                .replaceAll("\\([^)]*\\)", " ")
                .replace('-', ' ')
                .replaceAll("[^\\p{L} ]", " ")
                .trim()
                .replaceAll("\\s+", " ");
        if (text.isEmpty()) {
            return "";
        }

        int lastSpace = text.lastIndexOf(' ');
        String head = lastSpace < 0 ? "" : text.substring(0, lastSpace + 1);
        String phrase = head + singularize(text.substring(lastSpace + 1));
        return ALIASES.getOrDefault(phrase, phrase);
    }

    static String singularize(String word) {
        if (UNCHANGED.contains(word)) {
            return word;
        }
        String irregular = IRREGULAR_PLURALS.get(word);
        if (irregular != null) {
            return irregular;
        }
        if (word.length() <= 3) {
            return word;
        }
        if (word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("oes") || word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("xes") || word.endsWith("sses") || word.endsWith("zzes")) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ss") || word.endsWith("us") || word.endsWith("is")) {
            return word;
        }
        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    private static void alias(String canonical, String... variants) {
        for (String variant : variants) {
            ALIASES.put(variant, canonical);
        }
    }
}
