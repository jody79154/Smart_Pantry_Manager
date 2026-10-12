package com.example.smartpantry.data;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDao {

    private final DatabaseHelper helper;

    public RecipeDao(Context context) {
        this.helper = DatabaseHelper.getInstance(context);
    }

    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE_RECIPES);
    }

    public List<Recipe> getAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Map<Long, List<RecipeIngredient>> ingredientsByRecipe = new HashMap<>();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null,
                null, null, null, null, "recipe_id ASC, id ASC")) {
            while (cursor.moveToNext()) {
                long recipeId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_ID));
                ingredientsByRecipe.computeIfAbsent(recipeId, key -> new ArrayList<>())
                        .add(ingredientFromCursor(cursor));
            }
        }

        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_RECIPES, null,
                null, null, null, null, "name COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                List<RecipeIngredient> ingredients = ingredientsByRecipe.get(id);
                recipes.add(new Recipe(id,
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STEPS)),
                        ingredients == null ? new ArrayList<>() : ingredients));
            }
        }
        return recipes;
    }

    public Recipe getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] args = {String.valueOf(id)};

        List<RecipeIngredient> ingredients = new ArrayList<>();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null,
                DatabaseHelper.COL_RECIPE_ID + " = ?", args, null, null, "id ASC")) {
            while (cursor.moveToNext()) {
                ingredients.add(ingredientFromCursor(cursor));
            }
        }

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_RECIPES, null,
                DatabaseHelper.COL_ID + " = ?", args, null, null, null)) {
            if (!cursor.moveToFirst()) {
                return null;
            }
            return new Recipe(id,
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STEPS)),
                    ingredients);
        }
    }

    private RecipeIngredient ingredientFromCursor(Cursor cursor) {
        return new RecipeIngredient(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INGREDIENT_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT)));
    }
}
