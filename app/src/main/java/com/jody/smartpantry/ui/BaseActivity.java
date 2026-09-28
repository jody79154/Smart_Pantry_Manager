package com.jody.smartpantry.ui;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jody.smartpantry.R;

public abstract class BaseActivity extends AppCompatActivity {

    // Shared bottom nav
    protected void setupBottomNavigation(BottomNavigationView bottomNavigation, int selectedItemId) {
        bottomNavigation.setSelectedItemId(selectedItemId);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == selectedItemId) {
                return true;
            }
            Class<? extends BaseActivity> destination = destinationFor(itemId);
            if (destination == null) {
                return false;
            }
            Intent intent = new Intent(this, destination);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    private Class<? extends BaseActivity> destinationFor(int itemId) {
        if (itemId == R.id.nav_pantry) {
            return PantryListActivity.class;
        }
        if (itemId == R.id.nav_recipes) {
            return SuggestedRecipesActivity.class;
        }
        if (itemId == R.id.nav_settings) {
            return SettingsActivity.class;
        }
        return null;
    }
}
