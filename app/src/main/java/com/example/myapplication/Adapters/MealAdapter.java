package com.example.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.models.Meal;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private ArrayList<Meal> mealList;
    private OnMealClickListener onMealClickListener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public MealAdapter(ArrayList<Meal> mealList, OnMealClickListener onMealClickListener) {
        this.mealList = mealList;
        this.onMealClickListener = onMealClickListener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mealTypeTextView, caloriesTextView;

        MealViewHolder(View itemView) {
            super(itemView);
            mealTypeTextView = itemView.findViewById(R.id.mealTypeTextView);
            caloriesTextView = itemView.findViewById(R.id.caloriesTextView);
        }

        void bind(Meal meal) {
            mealTypeTextView.setText(meal.getDetail());
            caloriesTextView.setText("Calories: " + meal.getCal());

            itemView.setOnClickListener(v -> onMealClickListener.onMealClick(meal));
        }
    }
}
