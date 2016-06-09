package com.example.clarisselawson.tripbudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.clarisselawson.tripbudget.Database.TripDBHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String DATABASE_NAME = "tripDB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TripDBHelper mydb = new TripDBHelper(this, DATABASE_NAME, null, 1);
        ArrayList allTrips = mydb.getAllTrips();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, allTrips);

        ListView view = (ListView) findViewById(R.id.listView1);
        view.setAdapter(arrayAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                // TODO Auto-generated method stub
                int id_To_Search = arg2 + 1;

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);

                Intent intent = new Intent(getApplicationContext(),DisplayTripActivity.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
    }

    public void newTrip(View v) {
        Intent intent = new Intent(this, CreateTripActivity.class);
        startActivity(intent);

    }
}


