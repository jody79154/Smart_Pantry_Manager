package com.jody.smartpantry.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class IngredientNormalizer {

    // Common regional/alternate names mapped to one canonical form.
    private static final Map<String, String> ALIASES = new HashMap<>();
    static {
        ALIASES.put("capsicum", "bell pepper");
        ALIASES.put("aubergine", "eggplant");
        ALIASES.put("courgette", "zucchini");
        ALIASES.put("spring onion", "scallion");
        ALIASES.put("coriander", "cilantro");
        ALIASES.put("garbanzo bean", "chickpea");
        ALIASES.put("garbanzo beans", "chickpea");
        ALIASES.put("chickpeas", "chickpea");
    }

    private IngredientNormalizer() {
    }

    public static String normalize(String rawName) {
        if (rawName == null) {
            return "";
        }
        String cleaned = rawName.trim().toLowerCase(Locale.US).replaceAll("\\s+", " ");
        String alias = ALIASES.get(cleaned);
        if (alias != null) {
            cleaned = alias;
        }
        return singularize(cleaned);
    }

    private static String singularize(String phrase) {
        if (phrase.isEmpty()) {
            return phrase;
        }
        String[] words = phrase.split(" ");
        words[words.length - 1] = singularizeWord(words[words.length - 1]);
        return String.join(" ", words);
    }

    private static String singularizeWord(String word) {
        if (word.endsWith("ies") && word.length() > 3) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("oes") && word.length() > 3) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ses") && word.length() > 3) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("s") && !word.endsWith("ss") && word.length() > 1) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}
