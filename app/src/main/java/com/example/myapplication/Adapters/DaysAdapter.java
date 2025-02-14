package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.models.Day;
import com.example.myapplication.screens.DayDetailActivity;

import java.util.ArrayList;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayViewHolder> {

    private Context context;
    private ArrayList<Day> daysList;

    public DaysAdapter(Context context, ArrayList<Day> daysList) {
        this.context = context;
        this.daysList = daysList;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = daysList.get(position);
        holder.dateTextView.setText(day.getDate().toString());
        holder.sumCalTextView.setText("Calories: " + day.getSumcal());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DayDetailActivity.class);
            intent.putExtra("dayId", day.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, sumCalTextView;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            sumCalTextView = itemView.findViewById(R.id.sumCalTextView);
        }
    }
}
