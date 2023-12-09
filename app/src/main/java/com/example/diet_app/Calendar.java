package com.example.diet_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Button buttonHome,buttonAnalysis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        buttonHome = findViewById(R.id.buttonHome);
        buttonAnalysis = findViewById(R.id.buttonAnalysis);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //화면전환
                Intent intent = new Intent(Calendar.this, MainActivity.class);
                startActivity(intent);
            }
        });
        buttonAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //화면전환
                Intent intent = new Intent(Calendar.this, MainActivity3.class);
                startActivity(intent);
            }
        });

        calendarView = findViewById(R.id.calendarView);


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

        String[] projection = {"location", "food_name", "meal_datetime", "cost"};
        String selection = "meal_datetime LIKE ?";
        String[] selectionArgs = {selectedDate + "%"};

        Cursor cursor = database.query("daily_diet", projection, selection, selectionArgs, null, null, null);

        List<String> dataForSelectedDate = new ArrayList<>();

        while (cursor.moveToNext()) {
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
            String mealDateTime = cursor.getString(cursor.getColumnIndexOrThrow("meal_datetime"));
            String cost = cursor.getString(cursor.getColumnIndexOrThrow("cost"));

            // 데이터를 리스트에 추가
            String data = mealDateTime + " - " + location + " - " + foodName + " - " + cost;
            dataForSelectedDate.add(data);
        }

        cursor.close();
        dbHelper.close();

        // 리스트뷰에 데이터 표시
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataForSelectedDate);
        ListView listViewDailyData=findViewById(R.id.listViewDailyData);
        listViewDailyData.setAdapter(adapter);
    }
}