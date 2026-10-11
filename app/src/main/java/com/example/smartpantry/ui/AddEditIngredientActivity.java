package com.example.smartpantry.ui;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.PantryDao;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.InputValidator;
import com.example.smartpantry.util.QuantityFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";
    private static final long NO_ID = -1;

    private PantryDao pantryDao;
    private long itemId = NO_ID;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputLayout layoutExpiry;
    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private TextInputEditText etExpiry;
    private Spinner spinnerUnit;
    private TextView tvUnitError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        pantryDao = new PantryDao(this);
        bindViews();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        setupUnitSpinner();
        clearErrorOnEdit(layoutName, etName);
        clearErrorOnEdit(layoutQuantity, etQuantity);
        clearErrorOnEdit(layoutExpiry, etExpiry);
        layoutExpiry.setEndIconOnClickListener(v -> showDatePicker());

        // Save button
        findViewById(R.id.btnSave).setOnClickListener(v -> saveIngredient());

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ID);
        if (itemId != NO_ID) {
            toolbar.setTitle(R.string.title_edit_ingredient);
            loadExistingItem(savedInstanceState == null);
        }
    }

    private void bindViews() {
        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        layoutExpiry = findViewById(R.id.layoutExpiry);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        tvUnitError = findViewById(R.id.tvUnitError);
    }

    private void setupUnitSpinner() {
        List<String> options = new ArrayList<>();
        options.add(getString(R.string.unit_placeholder));
        options.addAll(Arrays.asList(InputValidator.UNITS));
        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);
    }

    private void loadExistingItem(boolean fillFields) {
        PantryItem item;
        try {
            item = pantryDao.getById(itemId);
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (item == null) {
            Toast.makeText(this, R.string.item_missing, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (fillFields) {
            etName.setText(item.getName());
            etQuantity.setText(QuantityFormatter.format(item.getQuantity()));
            spinnerUnit.setSelection(Arrays.asList(InputValidator.UNITS).indexOf(item.getUnit()) + 1);
            etExpiry.setText(item.getExpiryDate() == null ? "" : item.getExpiryDate());
        }
    }

    private void saveIngredient() {
        String rawName = textOf(etName);
        String rawQuantity = textOf(etQuantity);
        String rawExpiry = textOf(etExpiry);
        String unit = selectedUnit();

        String nameError = InputValidator.validateName(rawName);
        String quantityError = InputValidator.validateQuantity(rawQuantity);
        String unitError = InputValidator.validateUnit(unit);
        String expiryError = InputValidator.validateExpiry(rawExpiry);

        layoutName.setError(nameError);
        layoutQuantity.setError(quantityError);
        layoutExpiry.setError(expiryError);
        tvUnitError.setText(unitError);
        tvUnitError.setVisibility(unitError == null ? View.GONE : View.VISIBLE);

        if (nameError != null) {
            etName.requestFocus();
            return;
        }
        if (quantityError != null) {
            etQuantity.requestFocus();
            return;
        }
        if (unitError != null) {
            spinnerUnit.requestFocus();
            return;
        }
        if (expiryError != null) {
            etExpiry.requestFocus();
            return;
        }

        String name = InputValidator.cleanName(rawName);
        PantryItem item = new PantryItem(
                itemId == NO_ID ? 0 : itemId,
                name,
                InputValidator.parseQuantity(rawQuantity),
                unit,
                InputValidator.normalizeExpiry(rawExpiry));

        try {
            if (itemId == NO_ID) {
                if (pantryDao.insert(item) == -1) {
                    Toast.makeText(this, R.string.error_save, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(this, getString(R.string.item_added, name), Toast.LENGTH_SHORT).show();
            } else {
                if (pantryDao.update(item) == 0) {
                    Toast.makeText(this, R.string.item_missing, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                Toast.makeText(this, getString(R.string.item_updated, name), Toast.LENGTH_SHORT).show();
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.error_save, Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }

    private void showDatePicker() {
        LocalDate date = LocalDate.now();
        String current = InputValidator.normalizeExpiry(textOf(etExpiry));
        if (current != null && InputValidator.validateExpiry(current) == null) {
            try {
                date = LocalDate.parse(current);
            } catch (DateTimeParseException ignored) {
                date = LocalDate.now();
            }
        }
        new DatePickerDialog(this, (picker, year, month, day) ->
                etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private String selectedUnit() {
        if (spinnerUnit.getSelectedItemPosition() <= 0) {
            return "";
        }
        return String.valueOf(spinnerUnit.getSelectedItem());
    }

    private String textOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString();
    }

    private void clearErrorOnEdit(TextInputLayout layout, TextInputEditText field) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
