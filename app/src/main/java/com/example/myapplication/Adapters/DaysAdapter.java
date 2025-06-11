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
import com.example.myapplication.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// מתאם (אדפטר) לתצוגת רשימת הימים - אחראי על הצגת הימים בצורה מסודרת לפי שבועות, כולל כותרות וחישוב קלוריות
public class DaysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;  // קוד עבור כותרת שבוע
    private static final int TYPE_DAY = 1;     // קוד עבור תצוגת יום

    // ממשק להעברת לחיצות על יום ספציפי למסך הראשי
    public interface OnDayClickListener {
        void onDayClick(Day day);
    }

    private List<Object> items; // רשימה שמכילה או כותרות (מחרוזות) או ימים
    private final OnDayClickListener listener;

    // בנאי - מקבל רשימת ימים ופונקציה שתטפל בלחיצות על ימים
    public DaysAdapter(List<Day> days, OnDayClickListener listener) {
        this.listener = listener;
        this.items = new ArrayList<>();
        organizeByWeeks(days);
    } //

    // פונקציה שמארגנת את הימים לפי שבועות ומוסיפה כותרות מתאימות
    private void organizeByWeeks(List<Day> days) {
        items.clear();
        if (days.isEmpty()) return;

        // Sort days by date
        days.sort((d1, d2) -> d2.getDate().compareTo(d1.getDate()));

        Calendar calendar = Calendar.getInstance();
        int currentWeek = -1;

        for (Day day : days) {
            calendar.set(day.getDate().getYear(), day.getDate().getMonth() - 1, day.getDate().getDay());
            int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
            
            if (weekNumber != currentWeek) {
                currentWeek = weekNumber;
                String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                items.add("Week " + weekNumber + " - " + month);
            }
            items.add(day);
        }
    }

    // פונקציה שיוצרת את התצוגה המתאימה - או כותרת שבוע או תצוגת יום
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_week_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_day, parent, false);
            return new DayViewHolder(view);
        }
    }

    // פונקציה שממלאת את התצוגה בנתונים - או כותרת או פרטי היום
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((String) items.get(position));
        } else if (holder instanceof DayViewHolder) {
            ((DayViewHolder) holder).bind((Day) items.get(position));
        }
    }

    // פונקציה שמחזירה את סוג התצוגה - כותרת או יום
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_DAY;
    }

    // פונקציה שמעדכנת את הרשימה עם ימים חדשים
    public void updateData(List<Day> days) {
        organizeByWeeks(days);
        notifyDataSetChanged();
    }

    // מחלקה פנימית שמטפלת בתצוגת כותרת שבוע
    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvWeekHeader;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvWeekHeader = itemView.findViewById(R.id.tvWeekHeader);
        }

        // פונקציה שמציגה את כותרת השבוע
        void bind(String headerText) {
            tvWeekHeader.setText(headerText);
        }
    }

    // מחלקה פנימית שמטפלת בתצוגת יום בודד
    class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;        // כותרת היום
        private final TextView tvDate;         // תאריך היום
        private final TextView tvCalories;     // סך הקלוריות ביום
        private final TextView tvGoalIndicator; // סמן האם עמדנו ביעד הקלוריות
        private final DatabaseService databaseService;

        // בנאי - מאתחל את כל רכיבי התצוגה ומגדיר מה קורה בלחיצה על יום
        DayViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDayTitle);
            tvDate = itemView.findViewById(R.id.tvDayDate);
            tvCalories = itemView.findViewById(R.id.tvDayCalories);
            tvGoalIndicator = itemView.findViewById(R.id.tvGoalIndicator);
            databaseService = DatabaseService.getInstance();

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Object item = items.get(position);
                    if (item instanceof Day) {
                        listener.onDayClick((Day) item);
                    }
                }
            });
        }

        // פונקציה שממלאת את תצוגת היום בכל הפרטים ובודקת אם עמדנו ביעד הקלוריות
        void bind(Day day) {
            tvTitle.setText(day.getTitle());
            tvDate.setText(day.getDate().toString());
            tvCalories.setText(String.format(Locale.getDefault(), "%d cal", day.getSumcal()));

            // Get user's daily calorie goal and update the indicator
            String userId = databaseService.getCurrentUserId();
            if (userId != null) {
                databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public void onCompleted(User user) {
                        int dailyGoal = user.getDailycal();
                        int sumCal = day.getSumcal();

                        if (sumCal > dailyGoal) {
                            itemView.setBackgroundColor(Color.parseColor("#1AFF0000")); // Light red with alpha
                            tvGoalIndicator.setText("✗");
                            tvGoalIndicator.setTextColor(Color.RED);
                            // Send notification for exceeding the goal
                            String title = "Calorie Alert";
                            String message = "You've consumed " + sumCal + " calories today, which is over your daily goal of " + dailyGoal + " calories.";
                            NotificationHelper.sendNotification(itemView.getContext(), title, message);
                        } else {
                            itemView.setBackgroundColor(Color.parseColor("#1A00FF00")); // Light green with alpha
                            tvGoalIndicator.setText("✓");
                            tvGoalIndicator.setTextColor(Color.GREEN);
                            // Send congratulatory notification
                            String title = "Goal Achieved!";
                            String message = "Great job! You're within your daily calorie goal of " + dailyGoal + " calories!";
                            NotificationHelper.sendNotification(itemView.getContext(), title, message);
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        // Handle error
                        tvGoalIndicator.setText("");
                        itemView.setBackgroundColor(Color.TRANSPARENT);
                    }
                });
            }
        }
    }
}
