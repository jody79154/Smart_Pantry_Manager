package com.jody.smartpantry.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jody.smartpantry.R;
import com.jody.smartpantry.data.PantryDao;
import com.jody.smartpantry.logic.IngredientValidator;
import com.jody.smartpantry.model.PantryItem;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    public static final long NO_ITEM_ID = -1L;

    private static final String[] UNIT_OPTIONS = {
            "g", "kg", "ml", "l", "tsp", "tbsp", "item"
    };

    public static void startForNewItem(Context context) {
        context.startActivity(new Intent(context, AddEditIngredientActivity.class));
    }

    public static void startForEdit(Context context, long itemId) {
        Intent intent = new Intent(context, AddEditIngredientActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        context.startActivity(intent);
    }

    private PantryDao pantryDao;
    private long itemId = NO_ITEM_ID;

    private TextInputEditText inputName;
    private TextInputEditText inputQuantity;
    private AutoCompleteTextView inputUnit;
    private TextInputEditText inputExpiry;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputLayout layoutUnit;
    private TextInputLayout layoutExpiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        pantryDao = new PantryDao(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        inputName = findViewById(R.id.input_name);
        inputQuantity = findViewById(R.id.input_quantity);
        inputUnit = findViewById(R.id.input_unit);
        inputExpiry = findViewById(R.id.input_expiry);

        layoutName = findViewById(R.id.layout_name);
        layoutQuantity = findViewById(R.id.layout_quantity);
        layoutUnit = findViewById(R.id.layout_unit);
        layoutExpiry = findViewById(R.id.layout_expiry);

        inputUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, UNIT_OPTIONS));

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ITEM_ID);
        if (itemId != NO_ITEM_ID) {
            loadExistingItem();
        }

        findViewById(R.id.button_save).setOnClickListener(v -> onSaveClicked());
    }

    private void loadExistingItem() {
        PantryItem item = pantryDao.findById(itemId);
        if (item == null) {
            Toast.makeText(this, R.string.error_item_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_edit_ingredient);
        }
        inputName.setText(item.getName());
        inputQuantity.setText(formatQuantity(item.getQuantity()));
        inputUnit.setText(item.getUnit(), false);
        inputExpiry.setText(item.getExpiryDate());
        showDeleteAction();
    }

    private void showDeleteAction() {
        findViewById(R.id.button_delete).setVisibility(android.view.View.VISIBLE);
        findViewById(R.id.button_delete).setOnClickListener(v -> confirmDelete());
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> performDelete())
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void performDelete() {
        pantryDao.delete(itemId);
        Toast.makeText(this, R.string.feedback_item_deleted, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void onSaveClicked() {
        clearErrors();

        String name = textOf(inputName);
        String quantityText = textOf(inputQuantity);
        String unit = textOf(inputUnit);
        String expiry = textOf(inputExpiry);

        IngredientValidator.ValidationResult result =
                IngredientValidator.validate(name, quantityText, unit, expiry);

        if (!result.isValid()) {
            showErrors(result);
            return;
        }

        double quantity = Double.parseDouble(quantityText);

        if (itemId == NO_ITEM_ID) {
            long newId = pantryDao.insert(name, quantity, unit, expiry);
            if (newId == -1L) {
                Toast.makeText(this, R.string.error_database_generic, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.feedback_item_added, Toast.LENGTH_SHORT).show();
        } else {
            int rowsUpdated = pantryDao.update(itemId, name, quantity, unit, expiry);
            if (rowsUpdated == 0) {
                Toast.makeText(this, R.string.error_database_generic, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.feedback_item_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void clearErrors() {
        layoutName.setError(null);
        layoutQuantity.setError(null);
        layoutUnit.setError(null);
        layoutExpiry.setError(null);
    }

    private void showErrors(IngredientValidator.ValidationResult result) {
        layoutName.setError(result.getNameError());
        layoutQuantity.setError(result.getQuantityError());
        layoutUnit.setError(result.getUnitError());
        layoutExpiry.setError(result.getExpiryError());
    }

    private String textOf(android.widget.TextView view) {
        return view.getText() == null ? "" : view.getText().toString().trim();
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }
}

