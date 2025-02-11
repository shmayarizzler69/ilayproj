package Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Day;

import java.util.ArrayList;
import java.util.Calendar;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final Context context;
    private final ArrayList<Day> days;
    private final int daysInMonth;

    public CalendarAdapter(Context context, ArrayList<Day> days, int daysInMonth) {
        this.context = context;
        this.days = days;
        this.daysInMonth = daysInMonth;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_meal_page, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        if (position < daysInMonth) {
            Day day = days.get(position);

            // Display date
            holder.dayText.setText(String.valueOf(position + 1));

            // Update progress bar and calories
            holder.progressBar.setProgress(day.getSumcal());
            holder.calorieText.setText(day.getSumcal() + " Cal");

            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setVisibility(View.INVISIBLE); // Hide empty cells
        }
    }

    @Override
    public int getItemCount() {
        return 42; // 6 rows x 7 columns (standard grid for calendar)
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayText, calorieText;
        ProgressBar progressBar;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            calorieText = itemView.findViewById(R.id.calorieText);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}