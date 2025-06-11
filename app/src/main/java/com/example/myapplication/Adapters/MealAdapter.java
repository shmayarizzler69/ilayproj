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

// מתאם (אדפטר) לתצוגת רשימת הארוחות - אחראי על הצגת הארוחות ברשימה, כולל פרטי הארוחה והאוכל שנאכל בה
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private ArrayList<Meal> meals;             // רשימת הארוחות להצגה
    private Context context;                   // הקשר האפליקציה - נדרש לפעולות מערכת
    private OnDeleteMealListener deleteMealListener; // מאזין למחיקת ארוחה
    private DatabaseService databaseService;   // שירות מסד הנתונים

    // ממשק למחיקת ארוחה - מאפשר למסך הראשי לטפל במחיקת ארוחה
    public interface OnDeleteMealListener {
        void onDeleteMeal(Meal meal);
    }

    // בנאי - מקבל את כל הנתונים הנדרשים להצגת הארוחות
    public MealAdapter(Context context, ArrayList<Meal> meals, OnDeleteMealListener deleteMealListener) {
        this.context = context;
        this.meals = meals;
        this.deleteMealListener = deleteMealListener;
        this.databaseService = DatabaseService.getInstance();
    }

    // פונקציה שיוצרת תצוגה חדשה של ארוחה
    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    // פונקציה שממלאת את תצוגת הארוחה בכל הפרטים ומגדירה את כפתור המחיקה
    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);

        // הצגת פרטי הארוחה
        holder.tvMealDetails.setText(meal.getDetail());
        
        // בניית רשימת האוכל שנאכל בארוחה
        StringBuilder foodList = new StringBuilder();
        for (String food : meal.getFood()) {
            foodList.append("- ").append(food).append("\n");
        }
        holder.tvFoods.setText(foodList.toString());

        // הגדרת פעולת המחיקה בלחיצה על הכפתור
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteMealListener != null) {
                deleteMealListener.onDeleteMeal(meal);

                // מחיקת הארוחה ממסד הנתונים
                databaseService.deleteMeal(meal, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void result) {
                        meals.remove(meal);  // הסרת הארוחה מהרשימה
                        notifyDataSetChanged();  // עדכון התצוגה
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        exception.printStackTrace();
                    }
                });
            }
        });
    }

    // פונקציה שמחזירה את מספר הארוחות ברשימה
    @Override
    public int getItemCount() {
        return meals.size();
    }

    // מחלקה פנימית שמחזיקה את רכיבי התצוגה של ארוחה בודדת
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealDetails;    // פרטי הארוחה (שעה, סוג וכו')
        TextView tvFoods;         // רשימת האוכל בארוחה
        Button btnDelete;         // כפתור למחיקת הארוחה

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMealDetails = itemView.findViewById(R.id.tvMealDetails);
            tvFoods = itemView.findViewById(R.id.tvFoods);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}