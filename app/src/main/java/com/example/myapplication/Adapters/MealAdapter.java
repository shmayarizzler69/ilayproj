package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Meal;
import com.example.myapplication.services.DatabaseService;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private ArrayList<Meal> meals;
    private Context context;
    private OnDeleteMealListener deleteMealListener;
    private DatabaseService databaseService;

    // Interface for delete action
    public interface OnDeleteMealListener {
        void onDeleteMeal(Meal meal);
    }

    // Constructor to initialize the adapter with necessary data
    public MealAdapter(Context context, ArrayList<Meal> meals, OnDeleteMealListener deleteMealListener) {
        this.context = context;
        this.meals = meals;
        this.deleteMealListener = deleteMealListener;
        this.databaseService = DatabaseService.getInstance(); // Initialize the DatabaseService instance
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom meal item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);

        // Set the meal details in the UI
        holder.tvMealDetails.setText(meal.getDetail());
        StringBuilder foodList = new StringBuilder();
        for (String food : meal.getFood()) {
            foodList.append("- ").append(food).append("\n");
        }
        holder.tvFoods.setText(foodList.toString());

        // Set up the delete button functionality
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteMealListener != null) {
                // Notify listener for meal deletion
                deleteMealListener.onDeleteMeal(meal);

                // After deletion, update Firebase
                String userId = "your_user_id"; // Replace this with the actual user ID logic
                databaseService.deleteMealFromFirebase(meal, userId, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void result) {
                        // Successfully deleted the meal from Firebase
                        meals.remove(meal);  // Remove the meal from the list
                        notifyDataSetChanged();  // Notify the adapter to refresh the view
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        // Handle failure (e.g., show a Toast)
                        exception.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();  // Return the total number of meals
    }

    // ViewHolder class to hold references to the UI elements for each meal item
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealDetails, tvFoods;
        Button btnDelete;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMealDetails = itemView.findViewById(R.id.tvMealDetails);
            tvFoods = itemView.findViewById(R.id.tvFoods);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
