package com.example.myapplication.screens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Adapters.DaysAdapter;
import com.example.myapplication.models.Day;
import com.example.myapplication.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class DaysListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DaysAdapter adapter;
    private ArrayList<Day> daysList;

    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        daysList = new ArrayList<>();
        adapter = new DaysAdapter(this, daysList);
        recyclerView.setAdapter(adapter);

        databaseService = DatabaseService.getInstance();

        String currentUserId = databaseService.getCurrentUserId();
        databaseService.fetchDays(currentUserId, new DatabaseService.DatabaseCallback<List<Day>>() {
            @Override
            public void onCompleted(List<Day> days) {
                daysList.clear();
                daysList.addAll(days);
                daysList.sort((d1, d2) -> d1.getDate().asDate().compareTo(d2.getDate().asDate())); // Sort by date
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }
}