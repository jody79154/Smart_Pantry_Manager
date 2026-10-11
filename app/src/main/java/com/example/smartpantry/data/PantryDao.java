package com.example.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class PantryDao {

    private static final String ORDER_BY = "name COLLATE NOCASE ASC, id ASC";

    private final DatabaseHelper helper;

    public PantryDao(Context context) {
        this.helper = DatabaseHelper.getInstance(context);
    }

    public long insert(PantryItem item) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(DatabaseHelper.TABLE_PANTRY, null, toValues(item));
    }

    public int update(PantryItem item) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(DatabaseHelper.TABLE_PANTRY, toValues(item),
                DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    public int delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_PANTRY,
                DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public PantryItem getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_PANTRY, null,
                DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null)) {
            return cursor.moveToFirst() ? fromCursor(cursor) : null;
        }
    }

    public List<PantryItem> getAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<PantryItem> items = new ArrayList<>();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_PANTRY, null,
                null, null, null, null, ORDER_BY)) {
            while (cursor.moveToNext()) {
                items.add(fromCursor(cursor));
            }
        }
        return items;
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NAME, item.getName());
        values.put(DatabaseHelper.COL_QUANTITY, item.getQuantity());
        values.put(DatabaseHelper.COL_UNIT, item.getUnit());
        if (item.getExpiryDate() == null) {
            values.putNull(DatabaseHelper.COL_EXPIRY);
        } else {
            values.put(DatabaseHelper.COL_EXPIRY, item.getExpiryDate());
        }
        return values;
    }

    private PantryItem fromCursor(Cursor cursor) {
        int expiryIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPIRY);
        return new PantryItem(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT)),
                cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex));
    }
}
