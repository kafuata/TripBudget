package com.example.clarisselawson.tripbudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clarisselawson.tripbudget.Database.TripDBHelper;

public class CreateTripActivity extends AppCompatActivity {

    private  TripDBHelper myDb ;
    private  String DATABASE_NAME = "tripDB";
    TextView destination ;
    TextView total;
    TextView duration;
    float transport = 0;
    float feed = 0;
    float outing = 0;
    float extra = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        destination = (TextView) findViewById(R.id.trip_destination);
        duration = (TextView) findViewById(R.id.trip_duration);
        total = (TextView) findViewById(R.id.trip_budget);


        myDb = new TripDBHelper(this, DATABASE_NAME, null, 1);
    }


        public void run(View view)
        {
            if(myDb.insertTrip(destination.getText().toString(),duration.getText().toString(),Float.parseFloat(total.getText().toString()),transport,feed,outing,extra)){
                        Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                    }

                    else{
                        Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }

}
