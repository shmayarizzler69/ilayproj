package com.example.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.models.Day;

import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayViewHolder> {

    private List<Day> dayList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Day day);
    }

    public DaysAdapter(List<Day> dayList, OnItemClickListener listener) {
        this.dayList = dayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_item, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = dayList.get(position);
        holder.bind(day, listener);
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvTotalCalories;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotalCalories = itemView.findViewById(R.id.tvTotalCalories);
        }

        public void bind(Day day, OnItemClickListener listener) {
            tvDate.setText(day.getDate().toString()); // Adjust date formatting as needed
            tvTotalCalories.setText("Total Calories: " + day.getSumcal());

            itemView.setOnClickListener(v -> listener.onItemClick(day));
        }
    }
}
