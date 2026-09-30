package com.jody.smartpantry.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jody.smartpantry.model.Recipe;
import com.jody.smartpantry.model.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;

public class RecipeDao {

    private final DatabaseHelper databaseHelper;

    public RecipeDao(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }

    public List<Recipe> findAll() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_RECIPE_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_NAME));
                String steps = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_STEPS));
                recipes.add(new Recipe(id, name, steps, findIngredients(db, id)));
            }
        }
        return recipes;
    }

    public Recipe findById(long recipeId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPES,
                null,
                DatabaseHelper.COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, null)) {
            if (!cursor.moveToFirst()) {
                return null;
            }
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_NAME));
            String steps = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_STEPS));
            return new Recipe(recipeId, name, steps, findIngredients(db, recipeId));
        }
    }

    private List<RecipeIngredient> findIngredients(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                null,
                DatabaseHelper.COLUMN_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, DatabaseHelper.COLUMN_RI_ID + " ASC")) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RI_ID));
                String ingredientName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RI_INGREDIENT_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RI_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RI_UNIT));
                ingredients.add(new RecipeIngredient(id, recipeId, ingredientName, quantity, unit));
            }
        }
        return ingredients;
    }
}
