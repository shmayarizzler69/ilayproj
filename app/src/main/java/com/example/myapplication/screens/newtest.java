package com.example.myapplication.screens;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.services.DatabaseService;

public class newtest extends AppCompatActivity {

    Spinner spinnertype;  // Spinner for sizes
    RadioGroup RGFood;
    RadioButton rbDairy, rbVegetarian, rbVegan, rbMeat;
    String food;

    EditText etVenueName, etDate, etDress, etTime, etAdress, etCity;
    String stVenueName, stDate, stDress, stTime, stAdress, stCity;
    Button btnAddEvent;

    private DatabaseService databaseService;
    private String selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtest);

        databaseService = DatabaseService.getInstance();

        initViews();
        btnAddEvent.setOnClickListener(this);
    }

    private void initViews() {
        btnAddEvent = findViewById(R.id.btnAddItem);

        spinnertype = findViewById(R.id.spItemTYpe);
        // Size spinner

        etVenueName = findViewById(R.id.etVenue);
        etDate = findViewById(R.id.etEventDate);
        etDress = findViewById(R.id.etDressCode);
        etTime = findViewById(R.id.etTime);
        etAdress = findViewById(R.id.etAdress);
        etCity = findViewById(R.id.etCity);
        RGFood = findViewById(R.id.RGFood);
        rbDairy = findViewById(R.id.rbDairy);
        rbVegetarian = findViewById(R.id.rbVegetarian);
        rbVegan = findViewById(R.id.rbVegan);
        rbMeat = findViewById(R.id.rbMeat);


        // Set listener to show or hide size spinner based on item type

    }

    @Override
    public void onClick(View view) {

        if (view == btnAddEvent) {
            selectedType = spinnertype.getSelectedItem().toString();
            stDress = etDress.getText().toString() + "";
            stVenueName = etVenueName.getText().toString() + "";
            stDate = etDate.getText().toString() + "";
            stTime = etTime.getText().toString() + "";
            stAdress = etAdress.getText().toString() + "";
            stCity = etCity.getText().toString() + "";


            int selectedId = RGFood.getCheckedRadioButtonId();
            if (selectedId == rbDairy.getId()) {
                food = "Dairy";
            }
            else if (selectedId == rbVegetarian.getId()) {
                food = "Vegetarian";
            }
            else if (selectedId == rbVegan.getId()) {
                food = "vegan";
            }


            else if (selectedId == rbMeat.getId()) {
                food = "Meat";
            }




            if (TextUtils.isEmpty(stDate)) {
                Toast.makeText(addEVENT.this, "Please enter The date", Toast.LENGTH_SHORT).show();
                return;
            }


            String itemid = databaseService.generateEventId();

            //  public Event(String type, String date, String hour, String venue, String address, String city, String dresscode, String status, ArrayList < String > menutype, ArrayList < User > users)

            Event newEvent = new Event(itemid, selectedType, stDate, stTime, stVenueName, stAdress, stCity, stDress,"new", food,null);
            databaseService.createNewEvent(newEvent, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    // handle completion

                    Log.d("TAG", "Event added successfully");
                    Toast.makeText(addEVENT.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    Intent goReg = new Intent(addEVENT.this, AfterLoginMain.class);
                    startActivity(goReg);



                }

                @Override
                public void onFailed(Exception e) {
                    // handle failure
                    Log.e("TAG", "Failed to add Event", e);
                    Toast.makeText(addEVENT.this, "Failed to add Item", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}