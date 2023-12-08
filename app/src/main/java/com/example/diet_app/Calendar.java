package com.example.diet_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Calendar extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView listViewDailyData;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        listViewDailyData = findViewById(R.id.listViewDailyData);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택한 날짜에 해당하는 데이터 조회 및 표시
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                showDataForSelectedDate(selectedDate);
            }
        });
    }
    private void showDataForSelectedDate(String selectedDate) {
        // 데이터베이스에서 선택한 날짜에 해당하는 데이터를 조회
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] projection = {"location", "food_name", "meal_datetime"};
        String selection = "meal_datetime LIKE ?";
        String[] selectionArgs = {selectedDate + "%"};

        Cursor cursor = database.query("food_reviews", projection, selection, selectionArgs, null, null, null);
        List<String> dataForSelectedDate = new ArrayList<>();

        while (cursor.moveToNext()) {
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
            String mealDateTime = cursor.getString(cursor.getColumnIndexOrThrow("meal_datetime"));

            // 데이터를 리스트에 추가
            String data = mealDateTime + " - " + location + " - " + foodName;
            dataForSelectedDate.add(data);
        }

        cursor.close();
        dbHelper.close();

        // 리스트뷰에 데이터 표시
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataForSelectedDate);
        listViewDailyData.setAdapter(adapter);
    }
}