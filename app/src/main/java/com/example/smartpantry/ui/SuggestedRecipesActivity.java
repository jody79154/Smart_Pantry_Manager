package com.example.smartpantry.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.PantryDao;
import com.example.smartpantry.data.RecipeDao;
import com.example.smartpantry.matching.RecipeMatcher;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.ExpiryHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private PantryDao pantryDao;
    private RecipeDao recipeDao;
    private RecipeAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyState;
    private TextView tvSummary;
    private TextView tvEmptyHint;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        pantryDao = new PantryDao(this);
        recipeDao = new RecipeDao(this);
        recyclerView = findViewById(R.id.recipeRecycler);
        emptyState = findViewById(R.id.emptyState);
        tvSummary = findViewById(R.id.tvSummary);
        tvEmptyHint = findViewById(R.id.tvEmptyHint);
        bottomNav = findViewById(R.id.bottomNav);

        adapter = new RecipeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        BottomNavigationHelper.setup(this, bottomNav, R.id.nav_suggestions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_suggestions);
        loadSuggestions();
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }

    private void loadSuggestions() {
        List<Recipe> suggested;
        boolean pantryEmpty = true;
        int expiredCount = 0;
        try {
            List<PantryItem> pantry = pantryDao.getAll();
            pantryEmpty = pantry.isEmpty();
            LocalDate today = LocalDate.now();
            for (PantryItem item : pantry) {
                if (ExpiryHelper.isExpired(item.getExpiryDate(), today)) {
                    expiredCount++;
                }
            }
            suggested = RecipeMatcher.findMakeable(recipeDao.getAll(), pantry, today);
        } catch (SQLiteException e) {
            suggested = new ArrayList<>();
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_LONG).show();
        }
        adapter.setRecipes(suggested);

        boolean empty = suggested.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvSummary.setVisibility(empty ? View.GONE : View.VISIBLE);
        String expiredNote = expiredCount == 0 ? "" : getResources().getQuantityString(
                R.plurals.expired_not_used, expiredCount, expiredCount);
        tvEmptyHint.setText(withNote(getString(pantryEmpty
                ? R.string.empty_recipes_hint_no_pantry : R.string.empty_recipes_hint_no_match), expiredNote));
        if (!empty) {
            tvSummary.setText(withNote(getResources().getQuantityString(
                    R.plurals.recipes_ready, suggested.size(), suggested.size()), expiredNote));
        }
    }

    private String withNote(String text, String note) {
        return note.isEmpty() ? text : text + "\n" + note;
    }
}
