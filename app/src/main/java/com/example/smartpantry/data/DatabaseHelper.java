package com.example.smartpantry.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smart_pantry.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";
    public static final String COL_STEPS = "steps";
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_INGREDIENT_NAME = "ingredient_name";

    private static final String CREATE_PANTRY =
            "CREATE TABLE pantry_items ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "quantity REAL NOT NULL CHECK (quantity > 0), "
                    + "unit TEXT NOT NULL, "
                    + "expiry_date TEXT)";

    private static final String CREATE_RECIPES =
            "CREATE TABLE recipes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL UNIQUE, "
                    + "steps TEXT NOT NULL)";

    private static final String CREATE_RECIPE_INGREDIENTS =
            "CREATE TABLE recipe_ingredients ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "recipe_id INTEGER NOT NULL, "
                    + "ingredient_name TEXT NOT NULL, "
                    + "quantity REAL NOT NULL CHECK (quantity > 0), "
                    + "unit TEXT NOT NULL, "
                    + "FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)";

    private static final String CREATE_RECIPE_INGREDIENTS_INDEX =
            "CREATE INDEX idx_recipe_ingredients_recipe ON recipe_ingredients(recipe_id)";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PANTRY);
        db.execSQL(CREATE_RECIPES);
        db.execSQL(CREATE_RECIPE_INGREDIENTS);
        db.execSQL(CREATE_RECIPE_INGREDIENTS_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry_items");
        onCreate(db);
    }
}
