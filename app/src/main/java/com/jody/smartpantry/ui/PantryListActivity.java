package com.jody.smartpantry.ui;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jody.smartpantry.R;

public class PantryListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        setupBottomNavigation(bottomNavigation, R.id.nav_pantry);

        FloatingActionButton fabAddIngredient = findViewById(R.id.fab_add_ingredient);
        fabAddIngredient.setOnClickListener(v -> AddEditIngredientActivity.startForNewItem(this));
    }
}
