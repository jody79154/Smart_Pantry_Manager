package com.jody.smartpantry.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jody.smartpantry.R;
import com.jody.smartpantry.data.PantryDao;
import com.jody.smartpantry.model.PantryItem;

public class PantryListActivity extends BaseActivity implements PantryAdapter.Listener {

    private PantryDao pantryDao;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);
        pantryDao = new PantryDao(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        setupBottomNavigation(bottomNavigation, R.id.nav_pantry);

        recyclerView = findViewById(R.id.recycler_pantry);
        emptyStateText = findViewById(R.id.text_empty_pantry);
        adapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddIngredient = findViewById(R.id.fab_add_ingredient);
        fabAddIngredient.setOnClickListener(v -> AddEditIngredientActivity.startForNewItem(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        var items = pantryDao.findAll();
        adapter.submitList(items);
        boolean isEmpty = items.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEditClicked(PantryItem item) {
        AddEditIngredientActivity.startForEdit(this, item.getId());
    }

    @Override
    public void onDeleteClicked(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    pantryDao.delete(item.getId());
                    Toast.makeText(this, R.string.feedback_item_deleted, Toast.LENGTH_SHORT).show();
                    refreshList();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}

