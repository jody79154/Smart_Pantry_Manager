package com.jody.smartpantry.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jody.smartpantry.R;
import com.jody.smartpantry.data.RecipeDao;
import com.jody.smartpantry.model.Recipe;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    public static void start(Context context, long recipeId) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1L);
        Recipe recipe = new RecipeDao(this).findById(recipeId);

        if (recipe == null) {
            Toast.makeText(this, R.string.error_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView textName = findViewById(R.id.text_recipe_name);
        TextView textSteps = findViewById(R.id.text_recipe_steps);
        textName.setText(recipe.getName());
        textSteps.setText(recipe.getSteps());

        RecyclerView recyclerIngredients = findViewById(R.id.recycler_ingredients);
        recyclerIngredients.setLayoutManager(new LinearLayoutManager(this));
        RecipeIngredientAdapter ingredientAdapter = new RecipeIngredientAdapter();
        recyclerIngredients.setAdapter(ingredientAdapter);
        ingredientAdapter.submitList(recipe.getIngredients());
    }
}

