package com.example.myapplication.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.models.Day;
import com.example.myapplication.models.User;
import com.example.myapplication.services.DatabaseService;
import com.google.firebase.database.collection.LLRBNode;

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

        DatabaseService databaseService = DatabaseService.getInstance();

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotalCalories = itemView.findViewById(R.id.tvTotalCalories);
        }

        public void bind(Day day, OnItemClickListener listener) {
            tvDate.setText(day.getDate().toString()); // Adjust date formatting as needed
            tvTotalCalories.setText("Total Calories: " + day.getSumcal());

            itemView.setOnClickListener(v -> listener.onItemClick(day));

            int sumCal = day.getSumcal();

            databaseService.getUser(databaseService.getCurrentUserId(), new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    int cal_on_day = user.getDailycal();

                    if(sumCal < cal_on_day)
                        itemView.setBackgroundColor(Color.parseColor("#87FF0000"));
                    else
                        itemView.setBackgroundColor(Color.parseColor("#9238FF00"));
                }

                @Override
                public void onFailed(Exception e) {

                }
            });

        }
    }
}
