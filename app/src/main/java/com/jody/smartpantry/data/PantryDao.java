package com.jody.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.jody.smartpantry.model.PantryItem;
import java.util.ArrayList;
import java.util.List;

public class PantryDao {

    private final DatabaseHelper databaseHelper;

    public PantryDao(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(String name, double quantity, String unit, String expiryDate) {
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues values = valuesFor(name, quantity, unit, expiryDate);
            return db.insert(DatabaseHelper.TABLE_PANTRY_ITEMS, null, values);
        } catch (SQLiteException e) {
            return -1L;
        }
    }

    public int update(long id, String name, double quantity, String unit, String expiryDate) {
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues values = valuesFor(name, quantity, unit, expiryDate);
            return db.update(
                    DatabaseHelper.TABLE_PANTRY_ITEMS,
                    values,
                    DatabaseHelper.COLUMN_PANTRY_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (SQLiteException e) {
            return 0;
        }
    }

    public int delete(long id) {
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            return db.delete(
                    DatabaseHelper.TABLE_PANTRY_ITEMS,
                    DatabaseHelper.COLUMN_PANTRY_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (SQLiteException e) {
            return 0;
        }
    }

    public PantryItem findById(long id) {
        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            try (Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PANTRY_ITEMS,
                    null,
                    DatabaseHelper.COLUMN_PANTRY_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null)) {
                if (cursor.moveToFirst()) {
                    return itemFrom(cursor);
                }
                return null;
            }
        } catch (SQLiteException e) {
            return null;
        }
    }

    public List<PantryItem> findAll() {
        List<PantryItem> items = new ArrayList<>();
        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            try (Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PANTRY_ITEMS,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_PANTRY_NAME + " COLLATE NOCASE ASC")) {
                while (cursor.moveToNext()) {
                    items.add(itemFrom(cursor));
                }
            }
        } catch (SQLiteException e) {
            return new ArrayList<>();
        }
        return items;
    }

    private ContentValues valuesFor(String name, double quantity, String unit, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PANTRY_NAME, name.trim());
        values.put(DatabaseHelper.COLUMN_PANTRY_QUANTITY, quantity);
        values.put(DatabaseHelper.COLUMN_PANTRY_UNIT, unit.trim());
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            values.putNull(DatabaseHelper.COLUMN_PANTRY_EXPIRY);
        } else {
            values.put(DatabaseHelper.COLUMN_PANTRY_EXPIRY, expiryDate.trim());
        }
        return values;
    }

    private PantryItem itemFrom(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_NAME));
        double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_QUANTITY));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_UNIT));
        int expiryIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_EXPIRY);
        String expiryDate = cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex);
        return new PantryItem(id, name, quantity, unit, expiryDate);
    }
}

