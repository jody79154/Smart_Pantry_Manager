package com.example.smartpantry.ui;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.RecipeDao;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import com.example.smartpantry.util.IngredientFormatter;
import com.google.android.material.appbar.MaterialToolbar;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";
    private static final long NO_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, NO_ID);
        Recipe recipe = null;
        if (recipeId != NO_ID) {
            try {
                recipe = new RecipeDao(this).getById(recipeId);
            } catch (SQLiteException e) {
                Toast.makeText(this, R.string.error_database, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        if (recipe == null) {
            Toast.makeText(this, R.string.recipe_missing, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        showRecipe(recipe);
    }

    private void showRecipe(Recipe recipe) {
        StringBuilder ingredients = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (ingredients.length() > 0) {
                ingredients.append('\n');
            }
            ingredients.append("\u2022 ").append(IngredientFormatter.format(ingredient));
        }

        ((TextView) findViewById(R.id.tvRecipeName)).setText(recipe.getName());
        ((TextView) findViewById(R.id.tvIngredients)).setText(ingredients);
        ((TextView) findViewById(R.id.tvSteps)).setText(recipe.getSteps());
    }
}
