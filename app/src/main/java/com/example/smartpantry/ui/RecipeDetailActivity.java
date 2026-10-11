package com.example.smartpantry.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.google.android.material.appbar.MaterialToolbar;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
