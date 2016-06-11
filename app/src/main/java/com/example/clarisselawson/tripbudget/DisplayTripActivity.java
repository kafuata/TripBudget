package com.example.clarisselawson.tripbudget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.clarisselawson.tripbudget.database.TripDBHelper;

public class DisplayTripActivity extends AppCompatActivity {

    private String DATABASE_NAME = "tripDB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip);

        EditText destination = (EditText) findViewById(R.id.edit_trip_destination);
        EditText totalBudget = (EditText) findViewById(R.id.edit_trip_budget);


        TripDBHelper myDb = new TripDBHelper(this, DATABASE_NAME, null, 1);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int tripId = extras.getInt("id");
            Trip trip = myDb.getTrip(tripId);

            if (trip != null) {
                destination.setText(trip.getDestination());
                destination.setFocusableInTouchMode(true);
                destination.setClickable(true);

                totalBudget.setText(Float.toString(trip.getBudget()));
                totalBudget.setFocusableInTouchMode(true);
                totalBudget.setClickable(true);
            }
        }
    }
}