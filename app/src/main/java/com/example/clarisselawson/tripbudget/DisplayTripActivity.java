package com.example.clarisselawson.tripbudget;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clarisselawson.tripbudget.Database.TripDBHelper;

public class DisplayTripActivity extends AppCompatActivity {

    private String DATABASE_NAME = "tripDB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip);

       EditText destination = (EditText) findViewById(R.id.edit_trip_destination);
        //TextView duration = (TextView) findViewById(R.id.edit_trip_duration);
       EditText totalBudget = (EditText) findViewById(R.id.edit_trip_budget);


        TripDBHelper myDb = new TripDBHelper(this, DATABASE_NAME, null, 1);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int tripId = extras.getInt("id");

            Cursor rs = myDb.getTrip(tripId);
            rs.moveToFirst();

            if (rs != null) {

                String dest = rs.getString(rs.getColumnIndex(TripDBHelper.TRIPS_COLUMN_DESTINATION));
                Integer strt = rs.getInt(rs.getColumnIndex(TripDBHelper.TRIPS_COLUMN_START));
                Integer fnsh = rs.getInt(rs.getColumnIndex(TripDBHelper.TRIPS_COLUMN_FINISH));
                float total = rs.getFloat(rs.getColumnIndex(TripDBHelper.TRIPS_COLUMN_TOTAL_BUDGET));


                if (!rs.isClosed()) {
                    rs.close();
                }

                destination.setText((CharSequence) dest);
                destination.setFocusableInTouchMode(true);
                destination.setClickable(true);

                /*duration.setText((CharSequence) strt);
                duration.setFocusableInTouchMode(true);
                duration.setClickable(false);*/

                totalBudget.setText(Float.toString(total));
                totalBudget.setFocusableInTouchMode(true);
                totalBudget.setClickable(true);

            }

        }
    }
}