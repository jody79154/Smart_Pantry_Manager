package com.example.smartpantry.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNav = findViewById(R.id.bottomNav);
        BottomNavigationHelper.setup(this, bottomNav, R.id.nav_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_settings);
    }
}
