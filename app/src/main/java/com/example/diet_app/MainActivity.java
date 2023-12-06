package com.example.diet_app;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PlaceDBManager placeDBManager;
    private DietDBManager dietDBManager;
    private AutoCompleteTextView foodNameInput;
    private TextView caloriesView, costView;
    private Button confirmButton, viewTotalButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placeDBManager = new PlaceDBManager(this);
        dietDBManager = new DietDBManager(this);

        foodNameInput = findViewById(R.id.foodNameInput);
        caloriesView = findViewById(R.id.caloriesView);
        costView = findViewById(R.id.costView);
        confirmButton = findViewById(R.id.confirmButton);
        viewTotalButton = findViewById(R.id.viewTotalButton);


        placeDBManager.insertPlace("상록원1-2층");
        placeDBManager.insertFood("상록원1-2층","라면",580,3800);
        String selectedPlace = "selectedPlace";
        Cursor foodCursor = placeDBManager.getAllFoodsForPlace(selectedPlace);
        String[] from = new String[]{PlaceDBManager.COLUMN_FOOD_NAME};
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line, foodCursor, from, to, 0);
        foodNameInput.setAdapter(adapter);

        // When a food is selected, get its calories and cost and fill them into the appropriate views
        foodNameInput.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int calories = cursor.getInt(cursor.getColumnIndex(PlaceDBManager.COLUMN_CALORIES));
            int cost = cursor.getInt(cursor.getColumnIndex(PlaceDBManager.COLUMN_COST));
            caloriesView.setText(String.valueOf(calories));
            costView.setText(String.valueOf(cost));
        });

            confirmButton.setOnClickListener(v -> {
                long date = System.currentTimeMillis();
                String foodName = foodNameInput.getText().toString();
                int calories = Integer.parseInt(caloriesView.getText().toString());
                double cost = Double.parseDouble(costView.getText().toString());
                dietDBManager.insertDietRecord(date, foodName, calories, cost);
            });

            // Provide options for the user to view their total calories and cost for a given date or month
            viewTotalButton.setOnClickListener(v -> {
                // This is just a placeholder. You'll need to replace "year", "month", and "day" with the actual date
                int year = 2022, month = 1, day = 1;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                long date = calendar.getTimeInMillis();
                Cursor cursor = dietDBManager.getTotalCaloriesAndCostForDate(date);
                if (cursor.moveToFirst()) {
                    int totalCalories = cursor.getInt(0);
                    double totalCost = cursor.getDouble(1);
                    // Display the total calories and cost to the user
                    // This is just a placeholder. You'll need to replace it with your actual implementation
                    Toast.makeText(this, "Total calories: " + totalCalories + ", Total cost: " + totalCost, Toast.LENGTH_LONG).show();
                }
            });

        }
    }