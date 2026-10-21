package com.example.smartpantry.ui;

import android.app.Activity;
import android.content.Intent;

import com.example.smartpantry.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public final class BottomNavigationHelper {

    private BottomNavigationHelper() {
    }

    public static void setup(Activity activity, BottomNavigationView navigation, int selectedId) {
        navigation.setSelectedItemId(selectedId);
        navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) {
                return true;
            }
            if (id == R.id.nav_pantry) {
                Intent intent = new Intent(activity, PantryListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                if (selectedId != R.id.nav_pantry) {
                    activity.finish();
                }
                return true;
            }
            Class<?> target = id == R.id.nav_suggestions
                    ? SuggestedRecipesActivity.class
                    : SettingsActivity.class;
            activity.startActivity(new Intent(activity, target));
            if (selectedId != R.id.nav_pantry) {
                activity.finish();
            }
            return true;
        });
    }
}
