package com.example.smartpantry.data;

import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RecipeCatalog {

    private RecipeCatalog() {
    }

    public static List<Recipe> all() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(recipe("Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Whisk the eggs with the salt.\n3. Melt the butter in a pan over low heat.\n4. Stir the eggs gently until just set.\n5. Spoon over the toast and serve.",
                ing("egg", 3, "item"), ing("bread", 2, "item"), ing("butter", 10, "g"), ing("salt", 0.5, "tsp")));

        recipes.add(recipe("Tomato Pasta",
                "1. Boil the spaghetti in salted water until tender.\n2. Fry the crushed garlic in the olive oil for one minute.\n3. Add the canned tomato and simmer for 10 minutes.\n4. Drain the pasta and toss it through the sauce.",
                ing("spaghetti", 200, "g"), ing("canned tomato", 400, "g"), ing("garlic clove", 2, "item"),
                ing("olive oil", 2, "tbsp"), ing("salt", 1, "tsp")));

        recipes.add(recipe("Simple Pancakes",
                "1. Whisk the flour, sugar, milk and eggs into a smooth batter.\n2. Heat a little oil in a frying pan.\n3. Pour in a small ladle of batter and cook until bubbles form.\n4. Flip and cook the other side until golden.",
                ing("flour", 200, "g"), ing("milk", 300, "ml"), ing("egg", 2, "item"),
                ing("sugar", 1, "tbsp"), ing("vegetable oil", 1, "tbsp")));

        recipes.add(recipe("Cheese Omelette",
                "1. Beat the eggs with the salt.\n2. Melt the butter in a non-stick pan.\n3. Pour in the eggs and let the base set.\n4. Sprinkle over the cheese, fold the omelette and serve.",
                ing("egg", 3, "item"), ing("cheese", 50, "g"), ing("butter", 10, "g"), ing("salt", 0.5, "tsp")));

        recipes.add(recipe("Vegetable Fried Rice",
                "1. Cook the rice and let it cool.\n2. Grate the carrot and slice the spring onion.\n3. Heat the oil and scramble the eggs in a wok.\n4. Add the carrot and rice and fry for 4 minutes.\n5. Stir in the soy sauce and spring onion and serve.",
                ing("rice", 200, "g"), ing("egg", 2, "item"), ing("carrot", 1, "item"),
                ing("spring onion", 2, "item"), ing("soy sauce", 2, "tbsp"), ing("vegetable oil", 1, "tbsp")));

        recipes.add(recipe("Chicken Stir-Fry",
                "1. Slice the chicken, pepper and onion.\n2. Heat the oil in a wok and fry the chicken until cooked through.\n3. Add the vegetables and garlic and fry for 4 minutes.\n4. Pour in the soy sauce, toss and serve.",
                ing("chicken breast", 300, "g"), ing("bell pepper", 1, "item"), ing("onion", 1, "item"),
                ing("soy sauce", 2, "tbsp"), ing("garlic clove", 2, "item"), ing("vegetable oil", 1, "tbsp")));

        recipes.add(recipe("Lemon Garlic Rice",
                "1. Cook the rice in salted water and drain.\n2. Melt the butter and fry the crushed garlic for one minute.\n3. Stir in the rice and the juice of the lemon.\n4. Season and serve warm.",
                ing("rice", 200, "g"), ing("butter", 20, "g"), ing("garlic clove", 2, "item"),
                ing("lemon", 1, "item"), ing("salt", 0.5, "tsp")));

        recipes.add(recipe("Potato Soup",
                "1. Peel and dice the potatoes and onion.\n2. Soften the onion in the butter.\n3. Add the potato and stock and simmer for 20 minutes.\n4. Mash or blend to the texture you like and season with salt.",
                ing("potato", 4, "item"), ing("onion", 1, "item"), ing("vegetable stock", 500, "ml"),
                ing("butter", 20, "g"), ing("salt", 1, "tsp")));

        recipes.add(recipe("Chickpea Curry",
                "1. Soften the chopped onion and garlic in the oil.\n2. Stir in the curry powder for 30 seconds.\n3. Add the canned tomato and chickpeas.\n4. Simmer for 15 minutes and serve with rice or bread.",
                ing("chickpea", 400, "g"), ing("canned tomato", 400, "g"), ing("onion", 1, "item"),
                ing("garlic clove", 3, "item"), ing("curry powder", 2, "tsp"), ing("vegetable oil", 1, "tbsp")));

        recipes.add(recipe("Banana Oat Bites",
                "1. Heat the oven to 180 C.\n2. Mash the bananas in a bowl.\n3. Stir in the oats and honey.\n4. Spoon onto a lined tray and bake for 15 minutes.",
                ing("banana", 2, "item"), ing("oat", 100, "g"), ing("honey", 1, "tbsp")));

        recipes.add(recipe("Mashed Potatoes",
                "1. Peel the potatoes and cut them into chunks.\n2. Boil in salted water until soft, then drain.\n3. Mash with the butter and warm milk.\n4. Season with salt and serve.",
                ing("potato", 800, "g"), ing("milk", 100, "ml"), ing("butter", 40, "g"), ing("salt", 1, "tsp")));

        recipes.add(recipe("Tuna Mayo Sandwich",
                "1. Drain the canned tuna.\n2. Mix the tuna with the mayonnaise.\n3. Spread over two slices of bread.\n4. Top with the remaining slices and cut in half.",
                ing("canned tuna", 120, "g"), ing("bread", 4, "item"), ing("mayonnaise", 2, "tbsp")));

        recipes.add(recipe("French Toast",
                "1. Whisk the eggs, milk, sugar and cinnamon in a shallow dish.\n2. Soak each slice of bread for 30 seconds per side.\n3. Melt the butter in a pan.\n4. Fry the bread until golden on both sides.",
                ing("bread", 4, "item"), ing("egg", 2, "item"), ing("milk", 100, "ml"),
                ing("sugar", 1, "tbsp"), ing("butter", 10, "g"), ing("cinnamon", 1, "tsp")));

        recipes.add(recipe("Simple Guacamole",
                "1. Halve the avocados and scoop out the flesh.\n2. Mash roughly with a fork.\n3. Stir in the finely chopped onion and tomato.\n4. Add the lime juice and salt and mix well.",
                ing("avocado", 2, "item"), ing("lime", 1, "item"), ing("onion", 0.5, "item"),
                ing("tomato", 1, "item"), ing("salt", 0.5, "tsp")));

        recipes.add(recipe("Tomato Soup",
                "1. Soften the chopped onion and garlic in the olive oil.\n2. Add the canned tomato and stock.\n3. Simmer for 20 minutes.\n4. Blend until smooth and serve hot.",
                ing("canned tomato", 800, "g"), ing("onion", 1, "item"), ing("vegetable stock", 500, "ml"),
                ing("olive oil", 1, "tbsp"), ing("garlic clove", 2, "item")));

        recipes.add(recipe("Honey Yogurt Parfait",
                "1. Spoon half of the yogurt into a glass.\n2. Add half of the oats and drizzle with honey.\n3. Repeat with the remaining yogurt, oats and honey.\n4. Serve straight away.",
                ing("yogurt", 250, "g"), ing("honey", 2, "tbsp"), ing("oat", 50, "g")));

        recipes.add(recipe("Cheese Quesadilla",
                "1. Butter one side of each tortilla.\n2. Place one tortilla butter-side down in a pan.\n3. Cover with the cheese and top with the second tortilla.\n4. Cook until golden on both sides and cut into wedges.",
                ing("tortilla", 2, "item"), ing("cheese", 100, "g"), ing("butter", 10, "g")));

        recipes.add(recipe("Spaghetti Bolognese",
                "1. Fry the chopped onion and garlic in the oil.\n2. Add the ground beef and brown it well.\n3. Pour in the canned tomato and simmer for 25 minutes.\n4. Boil the spaghetti, drain and serve with the sauce.",
                ing("ground beef", 300, "g"), ing("spaghetti", 200, "g"), ing("canned tomato", 400, "g"),
                ing("onion", 1, "item"), ing("garlic clove", 2, "item"), ing("olive oil", 1, "tbsp")));

        recipes.add(recipe("Egg Fried Noodles",
                "1. Cook the noodles and drain.\n2. Heat the oil and scramble the eggs in a wok.\n3. Add the noodles and soy sauce and toss for 3 minutes.\n4. Top with sliced spring onion.",
                ing("noodle", 200, "g"), ing("egg", 2, "item"), ing("soy sauce", 2, "tbsp"),
                ing("spring onion", 2, "item"), ing("vegetable oil", 1, "tbsp")));

        recipes.add(recipe("Honey Roasted Carrots",
                "1. Heat the oven to 200 C.\n2. Cut the carrots into batons.\n3. Toss with the olive oil, honey and salt.\n4. Roast for 30 minutes, turning once.",
                ing("carrot", 500, "g"), ing("olive oil", 2, "tbsp"), ing("honey", 1, "tbsp"), ing("salt", 0.5, "tsp")));

        return recipes;
    }

    private static Recipe recipe(String name, String steps, RecipeIngredient... ingredients) {
        return new Recipe(0, name, steps, Arrays.asList(ingredients));
    }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }
}
