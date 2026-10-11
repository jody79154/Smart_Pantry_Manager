package com.example.smartpantry.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        bottomNav = findViewById(R.id.bottomNav);
        BottomNavigationHelper.setup(this, bottomNav, R.id.nav_suggestions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_suggestions);
    }
}
