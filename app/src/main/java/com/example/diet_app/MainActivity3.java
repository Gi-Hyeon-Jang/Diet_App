package com.example.diet_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

    public class MainActivity3 extends AppCompatActivity {
        private DBHelper dbHelper;
        private SQLiteDatabase database;

        private TextView totalCaloriesView,totalCostView;
        private Button selectDateForCaloriesButton;
        private Button selectDateForCostButton;
        private int selectedYear;
        private int selectedMonth;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main3);

            dbHelper = new DBHelper(this);
            database = dbHelper.getReadableDatabase();

            selectDateForCaloriesButton = findViewById(R.id.select_date_for_calories_button);
            selectDateForCaloriesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(true);
                }
            });

            selectDateForCostButton = findViewById(R.id.select_date_for_cost_button);
            selectDateForCostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(false);
                }
            });
        }

        private void showDatePickerDialog(final boolean isForCalories) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            selectedYear = year;
                            selectedMonth = month + 1; // Month is 0-indexed
                            if (isForCalories) {
                                analyzeCalorieForSelectedMonth();
                            } else {
                                analyzeCostForSelectedMonth();
                            }
                        }
                    },
                    Calendar.getInstance().get(Calendar.YEAR), // Initial year selection
                    Calendar.getInstance().get(Calendar.MONTH), // Initial month selection
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH) // Initial day selection
            );
            datePickerDialog.show();
        }


        private void analyzeCalorieForSelectedMonth() {
            String selectedYearMonth = String.format("%04d-%02d", selectedYear, selectedMonth);

            // Query to get total calories for selected month
            Cursor cursorCalories = database.rawQuery("SELECT SUM(calorie) AS total_calories FROM daily_diet WHERE strftime('%Y-%m', meal_datetime) = ?", new String[]{selectedYearMonth});

            // Iterate over the results and display them
            if (cursorCalories.moveToFirst()) {
                int totalCalories = cursorCalories.getInt(cursorCalories.getColumnIndex("total_calories"));

                totalCaloriesView=findViewById(R.id.total_calories_text_view);
            }
            cursorCalories.close();
        }

        private void analyzeCostForSelectedMonth() {
            String selectedYearMonth = String.format("%04d-%02d", selectedYear, selectedMonth);

            // Query to get total cost for selected month per type
            Cursor cursorCost = database.rawQuery("SELECT type, SUM(cost) AS total_cost FROM daily_diet WHERE strftime('%Y-%m', meal_datetime) = ? GROUP BY type", new String[]{selectedYearMonth});

            // Iterate over the results and display them
            if (cursorCost.moveToFirst()) {
                do {
                    int type = cursorCost.getInt(cursorCost.getColumnIndex("type"));
                    int totalCost = cursorCost.getInt(cursorCost.getColumnIndex("total_cost"));

                    totalCostView=findViewById(R.id.total_cost_text_view);
                } while (cursorCost.moveToNext());
            }
            cursorCost.close();
        }


}