package com.example.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Adapters.DaysAdapter;
import com.example.myapplication.models.Day;
import com.example.myapplication.services.AuthenticationService;
import com.example.myapplication.services.DatabaseService;
import com.example.myapplication.utils.SharedPreferencesUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DaysListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DaysAdapter adapter;
    private List<Day> dayList;
    private List<Day> filteredDayList;
    private DatabaseService databaseService;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private View errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_list);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Days");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewDays);
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.emptyStateText);
        errorView = findViewById(R.id.errorView);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayList = new ArrayList<>();
        filteredDayList = new ArrayList<>();
        adapter = new DaysAdapter(filteredDayList, this::onDayClicked, SharedPreferencesUtil.getUser(this));
        recyclerView.setAdapter(adapter);

        // Initialize services
        databaseService = DatabaseService.getInstance();

        // Setup FAB for adding new day
        FloatingActionButton fab = findViewById(R.id.fabAddDay);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(DaysListActivity.this, AddDayActivity.class);
            startActivity(intent);
        });

        // Initial data load
        loadDays();
    }

    private void onDayClicked(Day day) {
        Intent intent = new Intent(DaysListActivity.this, DayDetailActivity.class);
        intent.putExtra("day", day);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.days_list_menu, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterDays(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDays(newText);
                return true;
            }
        });
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterDays(String query) {
        if (query.isEmpty()) {
            filteredDayList.clear();
            filteredDayList.addAll(dayList);
        } else {
            filteredDayList.clear();
            filteredDayList.addAll(dayList.stream()
                    .filter(day -> day.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                            day.getDescription().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        adapter.updateData(filteredDayList);
        updateEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDays();
    }

    private void loadDays() {
        showLoading();
        String currentUserId = AuthenticationService.getInstance().getCurrentUserId();

        if (currentUserId != null) {
            databaseService.getAllDays(currentUserId, new DatabaseService.DatabaseCallback<List<Day>>() {
                @Override
                public void onCompleted(List<Day> days) {
                    hideLoading();
                    dayList.clear();
                    dayList.addAll(days);
                    filteredDayList.clear();
                    filteredDayList.addAll(days);
                    adapter.updateData(filteredDayList);
                    updateEmptyState();
                }

                @Override
                public void onFailed(Exception e) {
                    hideLoading();
                    showError(e.getMessage());
                }
            });
        } else {
            hideLoading();
            showError("User not authenticated!");
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        errorView.setVisibility(View.VISIBLE);
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", v -> loadDays())
                .show();
    }

    private void updateEmptyState() {
        if (filteredDayList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
