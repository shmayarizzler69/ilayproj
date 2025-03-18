package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Adapters.DaysAdapter;
import com.example.myapplication.models.Day;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DaysListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DaysAdapter adapter;
    private List<Day> dayList;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_list);

        recyclerView = findViewById(R.id.recyclerViewDays);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayList = new ArrayList<>();
        adapter = new DaysAdapter(dayList, day -> {
            // Open a new activity to show details
            Intent intent = new Intent(DaysListActivity.this, DayDetailActivity.class);
            intent.putExtra("day", day);
            startActivity(intent);
        });

        databaseService = DatabaseService.getInstance();

        // Set the OnClickListener for the return button
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(DaysListActivity.this, AfterLoginMain.class);
            startActivity(intent);
            finish();
        });

        // Initial data load
        loadDays();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this activity
        loadDays();
    }

    private void loadDays() {
        String currentUserId = AuthenticationService.getInstance().getCurrentUserId();

        if (currentUserId != null) {
            databaseService.getAllDays(currentUserId, new DatabaseService.DatabaseCallback<List<Day>>() {
                @Override
                public void onCompleted(List<Day> days) {
                    dayList.clear();
                    dayList.addAll(days);
                    // Sort the list by date
                    Collections.sort(dayList, (d1, d2) -> d2.getDate().compareTo(d1.getDate()));

                    // Notify the adapter of data changes
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(DaysListActivity.this, "Failed to load days", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show();
        }
    }
}
