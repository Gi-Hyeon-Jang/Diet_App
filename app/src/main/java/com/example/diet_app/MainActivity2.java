package com.example.diet_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity2 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;
    private DietAdapter adapter;
    private Button goToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        cursor = database.query("daily_diet", null, null, null, null, null, null);

        adapter = new DietAdapter(cursor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        goToMain=findViewById(R.id.return_home);
        goToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ImageButton button_moveCalendar = findViewById(R.id.button_moveCalendar);
        button_moveCalendar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity2.this, Calendar.class);
                startActivity(intent);
            }


        });
    }
}