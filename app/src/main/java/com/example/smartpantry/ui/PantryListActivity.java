package com.example.smartpantry.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.PantryDao;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.SettingsStore;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class PantryListActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private PantryDao pantryDao;
    private SettingsStore settings;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyState;
    private BottomNavigationView bottomNav;
    private AlertDialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        pantryDao = new PantryDao(this);
        settings = new SettingsStore(this);
        recyclerView = findViewById(R.id.pantryRecycler);
        emptyState = findViewById(R.id.emptyState);
        bottomNav = findViewById(R.id.bottomNav);

        adapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        BottomNavigationHelper.setup(this, bottomNav, R.id.nav_pantry);

        // Add button
        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        adapter.setExpiryAlerts(settings.isExpiryAlertsEnabled(), settings.getExpiryWarningDays());
        loadItems();
    }

    @Override
    protected void onDestroy() {
        if (deleteDialog != null && deleteDialog.isShowing()) {
            deleteDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        deleteDialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, item.getName()))
                .setPositiveButton(R.string.delete_confirm, (dialog, which) -> deleteItem(item))
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void deleteItem(PantryItem item) {
        try {
            int rows = pantryDao.delete(item.getId());
            int message = rows > 0 ? R.string.item_deleted : R.string.item_already_deleted;
            Snackbar.make(recyclerView, getString(message, item.getName()), Snackbar.LENGTH_SHORT)
                    .setAnchorView(bottomNav)
                    .show();
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_LONG).show();
        }
        loadItems();
    }

    private void loadItems() {
        List<PantryItem> items;
        try {
            items = pantryDao.getAll();
        } catch (SQLiteException e) {
            items = new ArrayList<>();
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_LONG).show();
        }
        adapter.setItems(items);

        boolean empty = items.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
