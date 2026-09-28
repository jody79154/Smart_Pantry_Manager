package com.jody.smartpantry.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.jody.smartpantry.R;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    public static final long NO_ITEM_ID = -1L;

    public static void startForNewItem(Context context) {
        context.startActivity(new Intent(context, AddEditIngredientActivity.class));
    }

    public static void startForEdit(Context context, long itemId) {
        Intent intent = new Intent(context, AddEditIngredientActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ITEM_ID);
        if (itemId != NO_ITEM_ID && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_edit_ingredient);
        }
    }
}
