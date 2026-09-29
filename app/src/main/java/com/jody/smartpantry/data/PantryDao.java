package com.jody.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jody.smartpantry.model.PantryItem;
import java.util.ArrayList;
import java.util.List;

public class PantryDao {

    private final DatabaseHelper databaseHelper;

    public PantryDao(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = valuesFor(name, quantity, unit, expiryDate);
        return db.insert(DatabaseHelper.TABLE_PANTRY_ITEMS, null, values);
    }

    public int update(long id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = valuesFor(name, quantity, unit, expiryDate);
        return db.update(
                DatabaseHelper.TABLE_PANTRY_ITEMS,
                values,
                DatabaseHelper.COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int delete(long id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_PANTRY_ITEMS,
                DatabaseHelper.COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public PantryItem findById(long id) {
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
    }

    public List<PantryItem> findAll() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_PANTRY_ITEMS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_PANTRY_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                items.add(itemFrom(cursor));
            }
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
