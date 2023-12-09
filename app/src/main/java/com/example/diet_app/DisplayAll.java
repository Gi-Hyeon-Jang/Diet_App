package com.example.diet_app;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class DisplayAll extends AppCompatActivity {
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    private TextView locationTextView, foodNameTextView, reviewTextView, costTextView, mealDatetimeTextView,calorieTextView,typeTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all);
        imageView = findViewById(R.id.imageView);
        locationTextView = findViewById(R.id.location);
        foodNameTextView = findViewById(R.id.food_name);
        typeTextView=findViewById(R.id.typeView);
        reviewTextView = findViewById(R.id.review);
        costTextView = findViewById(R.id.cost);
        mealDatetimeTextView = findViewById(R.id.meal_datetime);
        calorieTextView = findViewById(R.id.calorieView);
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        int id = getIntent().getIntExtra("id", -1);
        cursor = database.query("daily_diet", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
            String mealDatetime = cursor.getString(cursor.getColumnIndex("meal_datetime"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            String calorie = cursor.getString(cursor.getColumnIndex("calorie"));
            String review= cursor.getString(cursor.getColumnIndex("review"));
            String cost = cursor.getString(cursor.getColumnIndex("cost"));
            String imagePath = cursor.getString(cursor.getColumnIndex("image_path"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String typeString = mapTypeToString(type);
            locationTextView.setText("장소: "+location);
            foodNameTextView.setText("먹은 음식: "+foodName);
            reviewTextView.setText("리뷰: "+review);
            costTextView.setText("가격: "+cost+" 원");
            mealDatetimeTextView.setText("식사 시각: "+mealDatetime);
            calorieTextView.setText("칼로리: "+calorie);
            typeTextView.setText(typeString);
            if (imagePath != null) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            }
        }

    }
    private String mapTypeToString(int type) {
        switch (type) {
            case 1:
                return "조식";
            case 2:
                return "중식";
            case 3:
                return "석식";
            case 4:
                return "음료";
            default:
                return ""; // Default value if no match
        }
    }
}