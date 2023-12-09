package com.example.diet_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaceDBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Places.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_PLACE = "Place";
    private static final String TABLE_FOOD = "Food";

    public static final String COLUMN_PLACE_NAME = "placeName";
    public static final String COLUMN_FOOD_NAME = "foodName";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_COST = "cost";

    public PlaceDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPlaceTable = "CREATE TABLE " + TABLE_PLACE + "("
                + COLUMN_PLACE_NAME + " TEXT PRIMARY KEY)";
        db.execSQL(createPlaceTable);
        String createFoodTable = "CREATE TABLE " + TABLE_FOOD + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PLACE_NAME + " TEXT,"
                + COLUMN_FOOD_NAME + " TEXT,"
                + COLUMN_CALORIES + " INTEGER,"
                + COLUMN_COST + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_PLACE_NAME + ") REFERENCES " + TABLE_PLACE + "(" + COLUMN_PLACE_NAME + "))";
        db.execSQL(createFoodTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        onCreate(db);
    }

    public void insertPlace(String placeName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_NAME, placeName);
        db.insert(TABLE_PLACE, null, values);
    }

    public void insertFood(String placeName, String foodName, int calories, int cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_NAME, placeName);
        values.put(COLUMN_FOOD_NAME, foodName);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_COST, cost);
        db.insert(TABLE_FOOD, null, values);
    }

    public Cursor getAllFoodsForPlace(String placeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"_id", COLUMN_PLACE_NAME, COLUMN_FOOD_NAME, COLUMN_CALORIES, COLUMN_COST};
        return db.query(TABLE_FOOD, columns, COLUMN_PLACE_NAME + "=?", new String[]{placeName}, null, null, null);
    }
    public Cursor getMatchingFoodsForPlace(String place, String str) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FOOD + " WHERE " + COLUMN_PLACE_NAME + " = ? AND " + COLUMN_FOOD_NAME + " LIKE ?";
        return db.rawQuery(query, new String[]{place, "%" + str + "%"});
    }
    public void deletePlace(String placeName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLACE, COLUMN_PLACE_NAME + "=?", new String[]{placeName});
    }

    public void deleteFood(String placeName, String foodName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOOD, DATABASE_NAME + "=? AND " + COLUMN_FOOD_NAME + "=?", new String[]{placeName, foodName});
    }
    public Cursor getAllPlaces() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PLACE, new String[]{COLUMN_PLACE_NAME}, null, null, null, null, null);
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLACE);
        db.execSQL("DELETE FROM " + TABLE_FOOD);
    }
}