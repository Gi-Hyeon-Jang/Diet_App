package com.example.diet_app;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.DietViewHolder> {
    private Cursor cursor;

    public DietAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public DietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item, parent, false);
        return new DietViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DietViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
            String mealDatetime = cursor.getString(cursor.getColumnIndex("meal_datetime"));

            holder.foodNameTextView.setText(foodName);
            holder.mealDatetimeTextView.setText(mealDatetime);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class DietViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView mealDatetimeTextView;

        public DietViewHolder(@NonNull View itemView) {
            super(itemView);

            foodNameTextView = itemView.findViewById(R.id.food_name);
            mealDatetimeTextView = itemView.findViewById(R.id.meal_datetime);
        }
    }
}