package com.example.myapplication.screens;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import com.example.myapplication.Adapters.UserAdapter;
import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

// מסך מחיקת משתמשים - מאפשר למנהל לראות את כל המשתמשים ולמחוק אותם
public class DeleteUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList, filteredList;
    private DatabaseReference databaseReference;
    private ValueEventListener listener;
    private SearchView searchView;
    private Toolbar toolbar;
    private ImageButton backButton;

    // פונקציה שמופעלת כשהמסך נפתח בפעם הראשונה - מכינה את הרשימה ואת תיבת החיפוש
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        initializeViews();
        
        // Set up toolbar and back button
        setupToolbar();

        // Initialize the RecyclerView and List
        userList = new ArrayList<>();
        filteredList = new ArrayList<>();
        userAdapter = new UserAdapter(this, filteredList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        // Fetch users and display them
        fetchUsers();

        // Set up search functionality
        setupSearchView();
    }

    // פונקציה שמאתחלת את כל הרכיבים במסך
    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        toolbar = findViewById(R.id.toolbar);
        backButton = findViewById(R.id.backButton);
    }

    // פונקציה שמגדירה את הסרגל העליון וכפתור החזרה
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(DeleteUserActivity.this, AdminPage.class);
            startActivity(intent);
            finish(); // Close this activity
        });
    }

    // פונקציה שמביאה את כל המשתמשים מהמסד נתונים
    private void fetchUsers() {
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                filteredList.clear();
                filteredList.addAll(userList);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DeleteUserActivity.this, "Failed to load users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // פונקציה שמגדירה את תיבת החיפוש ואת אופן החיפוש
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No need to handle submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });
    }

    // פונקציה שמסננת את המשתמשים לפי טקסט החיפוש
    private void filterUsers(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            for (User user : userList) {
                if (user.getId().toLowerCase().contains(query.toLowerCase()) ||
                        user.getFname().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }
        userAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        databaseReference.removeEventListener(listener);
        super.onDestroy();
    }
}