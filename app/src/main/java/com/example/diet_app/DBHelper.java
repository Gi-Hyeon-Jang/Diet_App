package com.example.diet_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "daily_diet.db";
    private static final int DATABASE_VERSION = 6;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE daily_diet (" +
                "id INTEGER PRIMARY KEY," +
                "type INTEGER,"+
                "location TEXT," +
                "food_name TEXT," +
                "calorie TEXT," +
                "review TEXT," +
                "cost TEXT," +
                "image_path TEXT," +
                "meal_datetime TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 버전 업그레이드 시 필요한 작업 수행
        // (현재는 간단히 테이블을 삭제하고 다시 생성하는 방식을 사용)
        db.execSQL("DROP TABLE IF EXISTS daily_diet");
        onCreate(db);
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE daily_diet ADD COLUMN image_path TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE daily_diet ADD COLUMN meal_datetime DATETIME");
        }
    }
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM daily_diet");
    }
}

