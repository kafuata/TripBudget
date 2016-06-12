package com.example.clarisselawson.tripbudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class DisplayTripActivity extends AppCompatActivity {

    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip);

        TextView destination = (TextView) findViewById(R.id.edit_trip_destination);
        TextView totalBudget = (TextView) findViewById(R.id.edit_trip_budget);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            trip = extras.getParcelable("trip");

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


    public  void addSpent(View v){
        Intent intent = new Intent(this, EditSpentActivity.class);;
        intent.putExtra("trip", trip);

        startActivity(intent);
    }
}