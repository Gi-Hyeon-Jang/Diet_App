package com.example.diet_app;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private PlaceDBManager placeDBManager;
    private TextView caloriesView;
    private Spinner radioGroupLocation;
    private EditText editTextSideDish, editTextReview, editTextDateTime, editTextCost;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private Button buttonSelectImage;
    private String imagePath;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private AutoCompleteTextView editTextFoodName;
//    private Button confirmButton, viewTotalButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placeDBManager = new PlaceDBManager(this);
        radioGroupLocation = findViewById(R.id.radioGroupLocation);
        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextSideDish = findViewById(R.id.editTextSideDish);
        editTextReview = findViewById(R.id.editTextReview);
        editTextDateTime = findViewById(R.id.editTextDateTime);
        editTextCost = findViewById(R.id.editTextCost);
        caloriesView= findViewById(R.id.caloriesView);

        final String[] selectedPlace = { "selectedPlace" };
//        placeDBManager.insertPlace("ABCDE");
//        placeDBManager.insertFood("ABCDE","DEF",369,1234);
//        placeDBManager.insertFood("ABCDE","JIK",147,5678);
//        placeDBManager.insertPlace("FGHIJ");
//        placeDBManager.insertFood("FGHIJ","ABC",852,4398);
//        placeDBManager.insertFood("FGHIJ","LMN",401,23901);

        Cursor myCursor = placeDBManager.getAllPlaces();
        ArrayAdapter<String> newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        if (myCursor != null && myCursor.moveToFirst()) {
            do {
                String place = myCursor.getString(myCursor.getColumnIndex(PlaceDBManager.COLUMN_PLACE_NAME));
                newAdapter.add(place); // Add the place to the ArrayAdapter
            } while (myCursor.moveToNext());
            Log.d("MainActivity", "All places: " + newAdapter.getCount());
        } else {
            Log.d("MainActivity", "No places found");
        }

        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radioGroupLocation.setAdapter(newAdapter);

        radioGroupLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPlace[0] = parent.getItemAtPosition(position).toString();

                // Fetch the foods for the selected place
                Cursor foodCursor = placeDBManager.getAllFoodsForPlace(selectedPlace[0]);
                if (foodCursor != null && foodCursor.moveToFirst()) {
                    do {
                        // Get the data from the Cursor
                        String foodName = foodCursor.getString(foodCursor.getColumnIndex(PlaceDBManager.COLUMN_FOOD_NAME));
                        int calories = foodCursor.getInt(foodCursor.getColumnIndex(PlaceDBManager.COLUMN_CALORIES));
                        int cost = foodCursor.getInt(foodCursor.getColumnIndex(PlaceDBManager.COLUMN_COST));

                        // Log the data
                        Log.d("FoodData", "Food Name: " + foodName + ", Calories: " + calories + ", Cost: " + cost);
                    } while (foodCursor.moveToNext());
                } else {
                    Log.d("FoodData", "No data found");
                }
                String[] from = new String[]{PlaceDBManager.COLUMN_FOOD_NAME};
                int[] to = new int[]{android.R.id.text1};
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        foodCursor,
                        from,
                        to,
                        0) {
                    @Override
                    public CharSequence convertToString(Cursor cursor) {
                        int index = cursor.getColumnIndex(PlaceDBManager.COLUMN_FOOD_NAME);
                        return cursor.getString(index);
                    }
                };
                editTextFoodName.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Cursor foodCursor = placeDBManager.getAllFoodsForPlace(selectedPlace[0]);
        if (foodCursor != null && foodCursor.moveToFirst()) {
            do {
                // Get the data from the Cursor
                String foodName = foodCursor.getString(foodCursor.getColumnIndex(PlaceDBManager.COLUMN_FOOD_NAME));
                int calories = foodCursor.getInt(foodCursor.getColumnIndex(PlaceDBManager.COLUMN_CALORIES));
                int cost = foodCursor.getInt(foodCursor.getColumnIndex(PlaceDBManager.COLUMN_COST));

                // Log the data
                Log.d("FoodData", "Food Name: " + foodName + ", Calories: " + calories + ", Cost: " + cost);
            } while (foodCursor.moveToNext());
        } else {
            Log.d("FoodData", "No data found");
        }
        String[] from = new String[]{PlaceDBManager.COLUMN_FOOD_NAME};
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_dropdown_item_1line,
                foodCursor,
                from,
                to,
                0);
        editTextFoodName.setAdapter(adapter);


        // When a food is selected, get its calories and cost and fill them into the appropriate views
        editTextFoodName.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int caloriesIndex = cursor.getColumnIndex(PlaceDBManager.COLUMN_CALORIES);
            int costIndex = cursor.getColumnIndex(PlaceDBManager.COLUMN_COST);
            if (caloriesIndex == -1 || costIndex == -1) {
                Toast.makeText(this, "Column does not exist in Cursor", Toast.LENGTH_LONG).show();
            } else {
                int calories = cursor.getInt(caloriesIndex);
                int cost = cursor.getInt(costIndex);
                caloriesView.setText(String.valueOf(calories));
                editTextCost.setText(String.valueOf(cost));
            }
        });
        editTextDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDatabase();
            }
        });

        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // ActivityResultLauncher 초기화
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri != null) {
                    imagePath = getRealPathFromURI(uri);
                    // 이미지 경로를 저장할 수 있습니다.
                    // imagePath를 데이터베이스에 저장하거나 사용할 수 있습니다.
                    // 여기에서는 imagePath 값을 토스트 메시지로 표시합니다.
                    Toast.makeText(MainActivity.this, "이미지 경로: " + imagePath, Toast.LENGTH_SHORT).show();
                }
            }
            private String getRealPathFromURI(Uri contentUri) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);

                if (cursor == null) {
                    return contentUri.getPath(); // 일반적인 경우
                } else {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);
                    cursor.close();
                    return path;
                }
            }
        });
//        confirmButton.setOnClickListener(v -> {
//            long date = System.currentTimeMillis();
//            String foodName = editTextFoodName.getText().toString();
//            int calories = Integer.parseInt(caloriesView.getText().toString());
//            double cost = Double.parseDouble(editTextCost.getText().toString());
//            dietDBManager.insertDietRecord(date, foodName, calories, cost);
//        });

        // Provide options for the user to view their total calories and cost for a given date or month
//        viewTotalButton.setOnClickListener(v -> {
//            // This is just a placeholder. You'll need to replace "year", "month", and "day" with the actual date
//            int year = 2022, month = 1, day = 1;
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(year, month, day);
//            long date = calendar.getTimeInMillis();
//            Cursor cursor = dietDBManager.getTotalCaloriesAndCostForDate(date);
//            if (cursor.moveToFirst()) {
//                int totalCalories = cursor.getInt(0);
//                double totalCost = cursor.getDouble(1);
//                // Display the total calories and cost to the user
//                // This is just a placeholder. You'll need to replace it with your actual implementation
//                Toast.makeText(this, "Total calories: " + totalCalories + ", Total cost: " + totalCost, Toast.LENGTH_LONG).show();
//            }
//        });

    }
    private void selectImage() {
        // 이미지 선택 Intent 호출
        imagePickerLauncher.launch("image/*");
    }


    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = monthOfYear;
                selectedDay = dayOfMonth;

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;

                        // 선택한 날짜와 시간을 EditText에 표시
                        String dateTime = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d", selectedYear, selectedMonth + 1, selectedDay, selectedHour, selectedMinute);
                        editTextDateTime.setText(dateTime);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void saveDataToDatabase() {
        Spinner spinnerLocation = findViewById(R.id.radioGroupLocation);

        if (spinnerLocation.getSelectedItem() == null) {
            // No item selected
            Toast.makeText(this, "장소를 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String location = spinnerLocation.getSelectedItem().toString();
        String foodName = editTextFoodName.getText().toString();
        String sideDish = editTextSideDish.getText().toString();
        String review = editTextReview.getText().toString();
        String cost = editTextCost.getText().toString();

        // SQLite 데이터베이스에 데이터 저장
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("location", location);
        values.put("food_name", foodName);
        values.put("side_dish", sideDish);
        values.put("review", review);
        values.put("cost", cost);
        values.put("image_path", imagePath);
        values.put("meal_datetime", getFormattedDateTime());

        long newRowId = database.insert("daily_diet", null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "데이터 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "데이터 저장에 성공했습니다.", Toast.LENGTH_SHORT).show();
        }

        dbHelper.close();
    }

    private String getFormattedDateTime() {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d", selectedYear, selectedMonth + 1, selectedDay, selectedHour, selectedMinute);
    }
}