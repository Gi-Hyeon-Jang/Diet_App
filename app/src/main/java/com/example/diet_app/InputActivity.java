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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class InputActivity extends AppCompatActivity {

    private RadioGroup radioGroupLocation;
    private EditText editTextFoodName, editTextSideDish, editTextReview, editTextDateTime, editTextCost;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private Button buttonSelectImage;
    private String imagePath;
    private ActivityResultLauncher<String> imagePickerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        radioGroupLocation = findViewById(R.id.radioGroupLocation);
        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextSideDish = findViewById(R.id.editTextSideDish);
        editTextReview = findViewById(R.id.editTextReview);
        editTextDateTime = findViewById(R.id.editTextDateTime);
        editTextCost = findViewById(R.id.editTextCost);

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
                    Toast.makeText(InputActivity.this, "이미지 경로: " + imagePath, Toast.LENGTH_SHORT).show();
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(InputActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        int selectedRadioButtonId = radioGroupLocation.getCheckedRadioButtonId();

        if (selectedRadioButtonId == -1) {
            // No radio button selected
            Toast.makeText(this, "장소를 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String location = selectedRadioButton.getText().toString();
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

