package com.example.diet_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
        private Button goToMain;
        private int selectedYear;
        private int selectedMonth;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main3);

            dbHelper = new DBHelper(this);
            database = dbHelper.getReadableDatabase();
            totalCaloriesView = findViewById(R.id.total_calories_text_view);
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
            goToMain=findViewById(R.id.return_to_home);
            goToMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                    startActivity(intent);
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

                totalCaloriesView = findViewById(R.id.total_calories_text_view);
                totalCaloriesView.setText("Total Calories: "+totalCalories+"Kcal");
            }
            cursorCalories.close();
        }

        private void analyzeCostForSelectedMonth() {
            String selectedYearMonth = String.format("%04d-%02d", selectedYear, selectedMonth);

            // Query to get total cost for selected month per type
            Cursor cursorCost = database.rawQuery("SELECT type, SUM(cost) AS total_cost FROM daily_diet WHERE strftime('%Y-%m', meal_datetime) = ? GROUP BY type", new String[]{selectedYearMonth});

            TextView costType1 = findViewById(R.id.total_cost_text_view);
            TextView costType2 = findViewById(R.id.total_cost_text_view2);
            TextView costType3 = findViewById(R.id.total_cost_text_view3);
            TextView costType4 = findViewById(R.id.total_cost_text_view4);
            costType1.setText("");
            costType2.setText("");
            costType3.setText("");
            costType4.setText("");
            // Iterate over the results and display them
            if (cursorCost.moveToFirst()) {
                do {
                    int type = cursorCost.getInt(cursorCost.getColumnIndex("type"));
                    int totalCost = cursorCost.getInt(cursorCost.getColumnIndex("total_cost"));

                    switch (type) {
                        case 1:
                            costType1.setText("Total Morning Cost: " + totalCost+" 원");
                            break;
                        case 2:
                            costType2.setText("Total Lunch Cost: "+ totalCost+" 원");
                            break;
                        case 3:
                            costType3.setText("Total Dinner Cost: " + totalCost+" 원");
                            break;
                        case 4:
                            costType4.setText("Total Beverage Cost: " + totalCost+" 원");
                            break;
                    }
                } while (cursorCost.moveToNext());
            }
            cursorCost.close();
        }


}