package com.example.diet_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

public class DietDBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Diet.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_DIET_RECORD = "DietRecord";

    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_FOOD_NAME = "foodName";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_COST = "cost";

    public DietDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDietRecordTable = "CREATE TABLE " + TABLE_DIET_RECORD + "("
                + COLUMN_DATE + " INTEGER,"
                + COLUMN_FOOD_NAME + " TEXT,"
                + COLUMN_CALORIES + " INTEGER,"
                + COLUMN_COST + " REAL)";
        db.execSQL(createDietRecordTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIET_RECORD);
        onCreate(db);
    }

    public void insertDietRecord(long date, String foodName, int calories, double cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_FOOD_NAME, foodName);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_COST, cost);
        db.insert(TABLE_DIET_RECORD, null, values);
    }

    public Cursor getTotalCaloriesAndCostForDate(long date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_CALORIES + "), SUM(" + COLUMN_COST + ") FROM " + TABLE_DIET_RECORD
                + " WHERE " + COLUMN_DATE + "=?";
        return db.rawQuery(query, new String[]{Long.toString(date)});
    }

    public Cursor getTotalCaloriesAndCostForMonth(int year, int month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        long monthStart = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long monthEnd = calendar.getTimeInMillis();
        String query = "SELECT SUM(" + COLUMN_CALORIES + "), SUM(" + COLUMN_COST + ") FROM " + TABLE_DIET_RECORD
                + " WHERE " + COLUMN_DATE + ">=? AND " + COLUMN_DATE + "<?";
        return db.rawQuery(query, new String[]{Long.toString(monthStart), Long.toString(monthEnd)});
    }
}
