package com.example.smartpantry.data;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

public final class RecipeSeeder {

    private RecipeSeeder() {
    }

    public static void seed(SQLiteDatabase db) {
        if (DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE_RECIPES) > 0) {
            return;
        }
        for (Recipe recipe : RecipeCatalog.all()) {
            ContentValues recipeValues = new ContentValues();
            recipeValues.put(DatabaseHelper.COL_NAME, recipe.getName());
            recipeValues.put(DatabaseHelper.COL_STEPS, recipe.getSteps());
            long recipeId = db.insertOrThrow(DatabaseHelper.TABLE_RECIPES, null, recipeValues);

            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COL_RECIPE_ID, recipeId);
                values.put(DatabaseHelper.COL_INGREDIENT_NAME, ingredient.getName());
                values.put(DatabaseHelper.COL_QUANTITY, ingredient.getQuantity());
                values.put(DatabaseHelper.COL_UNIT, ingredient.getUnit());
                db.insertOrThrow(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, values);
            }
        }
    }
}
