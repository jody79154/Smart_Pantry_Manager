package com.jody.smartpantry.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jody.smartpantry.R;
import com.jody.smartpantry.data.PantryDao;
import com.jody.smartpantry.data.RecipeDao;
import com.jody.smartpantry.logic.RecipeMatcher;
import com.jody.smartpantry.model.Recipe;

public class SuggestedRecipesActivity extends BaseActivity implements RecipeAdapter.Listener {

    private PantryDao pantryDao;
    private RecipeDao recipeDao;
    private RecipeAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        pantryDao = new PantryDao(this);
        recipeDao = new RecipeDao(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        setupBottomNavigation(bottomNavigation, R.id.nav_recipes);

        recyclerView = findViewById(R.id.recycler_recipes);
        emptyStateText = findViewById(R.id.text_empty_recipes);
        adapter = new RecipeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSuggestions();
    }

    private void refreshSuggestions() {
        var pantryItems = pantryDao.findAll();
        var allRecipes = recipeDao.findAll();
        var matches = RecipeMatcher.findMatchingRecipes(allRecipes, pantryItems);

        adapter.submitList(matches);
        boolean isEmpty = matches.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        RecipeDetailActivity.start(this, recipe.getId());
    }
}

