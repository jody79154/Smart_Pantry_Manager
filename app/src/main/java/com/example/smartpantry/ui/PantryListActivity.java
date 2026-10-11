package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PantryListActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        bottomNav = findViewById(R.id.bottomNav);
        BottomNavigationHelper.setup(this, bottomNav, R.id.nav_pantry);

        // Add button
        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_pantry);
    }
}
