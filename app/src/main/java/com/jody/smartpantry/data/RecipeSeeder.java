package com.jody.smartpantry.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import java.util.Arrays;
import java.util.List;

public class RecipeSeeder {

    private static final class SeedIngredient {
        final String name;
        final double quantity;
        final String unit;

        SeedIngredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    private static final class SeedRecipe {
        final String name;
        final String steps;
        final List<SeedIngredient> ingredients;

        SeedRecipe(String name, String steps, SeedIngredient... ingredients) {
            this.name = name;
            this.steps = steps;
            this.ingredients = Arrays.asList(ingredients);
        }
    }

    private static SeedIngredient ing(String name, double quantity, String unit) {
        return new SeedIngredient(name, quantity, unit);
    }

    private static final SeedRecipe[] RECIPES = {
            new SeedRecipe("Tomato Pasta",
                    "1. Boil the pasta until al dente.\n2. Saute garlic in olive oil.\n3. Add chopped tomato and simmer.\n4. Toss pasta through the sauce and season with salt.",
                    ing("tomato", 3, "item"), ing("pasta", 200, "g"), ing("garlic", 2, "item"),
                    ing("olive oil", 15, "ml"), ing("salt", 2, "g")),

            new SeedRecipe("Scrambled Eggs",
                    "1. Whisk eggs with milk.\n2. Melt butter in a pan.\n3. Cook eggs gently, stirring, and season with salt.",
                    ing("egg", 3, "item"), ing("milk", 30, "ml"), ing("butter", 10, "g"), ing("salt", 1, "g")),

            new SeedRecipe("Veggie Omelette",
                    "1. Whisk eggs with milk.\n2. Dice bell pepper and onion, cook until soft.\n3. Pour in egg mixture and cook until set.",
                    ing("egg", 3, "item"), ing("bell pepper", 1, "item"), ing("onion", 1, "item"), ing("milk", 20, "ml")),

            new SeedRecipe("Grilled Cheese Sandwich",
                    "1. Butter one side of each bread slice.\n2. Layer cheese between the unbuttered sides.\n3. Grill until golden and the cheese melts.",
                    ing("bread", 2, "item"), ing("cheese", 60, "g"), ing("butter", 10, "g")),

            new SeedRecipe("Chicken Stir Fry",
                    "1. Slice chicken breast and vegetables.\n2. Stir-fry chicken until cooked through.\n3. Add bell pepper, onion and garlic.\n4. Finish with soy sauce.",
                    ing("chicken breast", 300, "g"), ing("bell pepper", 1, "item"), ing("onion", 1, "item"),
                    ing("soy sauce", 30, "ml"), ing("garlic", 2, "item")),

            new SeedRecipe("Vegetable Soup",
                    "1. Dice carrot, potato, onion and celery.\n2. Simmer in vegetable stock until tender.\n3. Season to taste.",
                    ing("carrot", 2, "item"), ing("potato", 2, "item"), ing("onion", 1, "item"),
                    ing("vegetable stock", 500, "ml"), ing("celery", 1, "item")),

            new SeedRecipe("Rice and Beans",
                    "1. Cook rice according to package instructions.\n2. Saute onion and garlic.\n3. Add black beans and simmer.\n4. Serve beans over rice.",
                    ing("rice", 200, "g"), ing("black beans", 400, "g"), ing("onion", 1, "item"), ing("garlic", 2, "item")),

            new SeedRecipe("Banana Pancakes",
                    "1. Mash banana and whisk with egg and milk.\n2. Stir in flour and sugar.\n3. Cook spoonfuls of batter on a hot pan until golden.",
                    ing("flour", 200, "g"), ing("banana", 2, "item"), ing("egg", 2, "item"),
                    ing("milk", 150, "ml"), ing("sugar", 20, "g")),

            new SeedRecipe("Greek Salad",
                    "1. Chop cucumber, tomato and red onion.\n2. Combine with cubed feta cheese.\n3. Dress with olive oil.",
                    ing("cucumber", 1, "item"), ing("tomato", 2, "item"), ing("feta cheese", 100, "g"),
                    ing("olive oil", 15, "ml"), ing("red onion", 1, "item")),

            new SeedRecipe("Tuna Salad",
                    "1. Drain the canned tuna.\n2. Mix with mayonnaise and finely chopped onion.\n3. Finish with a squeeze of lemon.",
                    ing("canned tuna", 150, "g"), ing("mayonnaise", 30, "g"), ing("onion", 1, "item"), ing("lemon", 1, "item")),

            new SeedRecipe("Spaghetti Bolognese",
                    "1. Brown the ground beef with onion and garlic.\n2. Add chopped tomato and simmer into a sauce.\n3. Serve over cooked pasta.",
                    ing("pasta", 250, "g"), ing("ground beef", 300, "g"), ing("tomato", 4, "item"),
                    ing("onion", 1, "item"), ing("garlic", 2, "item")),

            new SeedRecipe("Mushroom Risotto",
                    "1. Saute onion and mushroom.\n2. Add rice and toast briefly.\n3. Stir in vegetable stock gradually until creamy.\n4. Finish with parmesan cheese.",
                    ing("rice", 200, "g"), ing("mushroom", 150, "g"), ing("onion", 1, "item"),
                    ing("vegetable stock", 600, "ml"), ing("parmesan cheese", 50, "g")),

            new SeedRecipe("Vegetable Curry",
                    "1. Saute onion, then add potato and carrot.\n2. Stir in curry powder.\n3. Pour in coconut milk and simmer until vegetables are tender.",
                    ing("potato", 2, "item"), ing("carrot", 2, "item"), ing("coconut milk", 400, "ml"),
                    ing("curry powder", 10, "g"), ing("onion", 1, "item")),

            new SeedRecipe("Chicken Soup",
                    "1. Simmer chicken breast in chicken stock.\n2. Add carrot, celery and onion.\n3. Cook until vegetables are tender and chicken is cooked through.",
                    ing("chicken breast", 200, "g"), ing("carrot", 2, "item"), ing("celery", 1, "item"),
                    ing("onion", 1, "item"), ing("chicken stock", 500, "ml")),

            new SeedRecipe("Fruit Smoothie",
                    "1. Add banana, strawberry and milk to a blender.\n2. Sweeten with honey.\n3. Blend until smooth.",
                    ing("banana", 1, "item"), ing("strawberry", 100, "g"), ing("milk", 200, "ml"), ing("honey", 15, "g")),

            new SeedRecipe("Avocado Toast",
                    "1. Toast the bread.\n2. Mash avocado with a squeeze of lemon and a pinch of salt.\n3. Spread over the toast.",
                    ing("bread", 2, "item"), ing("avocado", 1, "item"), ing("lemon", 1, "item"), ing("salt", 1, "g")),

            new SeedRecipe("Classic Pancakes",
                    "1. Whisk flour, egg, milk and sugar into a smooth batter.\n2. Melt butter in a pan.\n3. Cook spoonfuls of batter until golden on both sides.",
                    ing("flour", 200, "g"), ing("egg", 2, "item"), ing("milk", 250, "ml"),
                    ing("sugar", 20, "g"), ing("butter", 20, "g")),

            new SeedRecipe("Baked Sweet Potato",
                    "1. Pierce the sweet potato and bake until soft.\n2. Split open and top with butter.\n3. Season with salt.",
                    ing("sweet potato", 2, "item"), ing("butter", 10, "g"), ing("salt", 2, "g")),

            new SeedRecipe("Lentil Soup",
                    "1. Saute onion, garlic and carrot.\n2. Add lentils and vegetable stock.\n3. Simmer until the lentils are soft.",
                    ing("lentils", 200, "g"), ing("onion", 1, "item"), ing("carrot", 2, "item"),
                    ing("garlic", 2, "item"), ing("vegetable stock", 500, "ml")),

            new SeedRecipe("Caprese Salad",
                    "1. Slice tomato and mozzarella cheese.\n2. Layer with fresh basil.\n3. Drizzle with olive oil.",
                    ing("tomato", 3, "item"), ing("mozzarella cheese", 150, "g"), ing("basil", 10, "g"),
                    ing("olive oil", 15, "ml")),
    };

    private RecipeSeeder() {
    }

    public static void seed(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            for (SeedRecipe recipe : RECIPES) {
                ContentValues recipeValues = new ContentValues();
                recipeValues.put(DatabaseHelper.COLUMN_RECIPE_NAME, recipe.name);
                recipeValues.put(DatabaseHelper.COLUMN_RECIPE_STEPS, recipe.steps);
                long recipeId = db.insertOrThrow(DatabaseHelper.TABLE_RECIPES, null, recipeValues);

                for (SeedIngredient ingredient : recipe.ingredients) {
                    ContentValues ingredientValues = new ContentValues();
                    ingredientValues.put(DatabaseHelper.COLUMN_RI_RECIPE_ID, recipeId);
                    ingredientValues.put(DatabaseHelper.COLUMN_RI_INGREDIENT_NAME, ingredient.name);
                    ingredientValues.put(DatabaseHelper.COLUMN_RI_QUANTITY, ingredient.quantity);
                    ingredientValues.put(DatabaseHelper.COLUMN_RI_UNIT, ingredient.unit);
                    db.insertOrThrow(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static int recipeCount() {
        return RECIPES.length;
    }
}
