package com.example.clarisselawson.tripbudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.clarisselawson.tripbudget.adapter.TripAdapter;
import com.example.clarisselawson.tripbudget.database.TripDBHelper;
import com.example.clarisselawson.tripbudget.listener.SwipeCardListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private ArrayList<Trip> allTrips;
    TripDBHelper myDb;

    private int REQUEST_CREATE_TRIP = 0;
    private int REQUEST_UPDATE_TRIP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new TripDBHelper(this, getString(R.string.db_name), null, R.integer.db_version);
        allTrips = myDb.getAllTrips();

        tripAdapter = new TripAdapter(allTrips, this);
        recyclerView = (RecyclerView) findViewById(R.id.trip_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(tripAdapter);
        setupSwipeListeners();
    }

    private void setupSwipeListeners() {
        SwipeCardListener tripCardTouchListener =
                new SwipeCardListener(recyclerView,
                        new SwipeCardListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    myDb.deleteTrip(allTrips.get(position).getId());
                                    allTrips.remove(position);
                                    tripAdapter.notifyItemRemoved(position);
                                }

                                tripAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                Intent intent = new Intent(getApplicationContext(), EditTripActivity.class);
                                int position = reverseSortedPositions[0];
                                intent.putExtra("trip", allTrips.get(position));
                                intent.putExtra("position", position);
                                startActivityForResult(intent, REQUEST_UPDATE_TRIP);
                            }
                        });

        recyclerView.addOnItemTouchListener(tripCardTouchListener);
    }

    public void newTrip(View v) {
        Intent intent = new Intent(this, EditTripActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_TRIP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        Trip trip = data.getExtras().getParcelable("trip");
        if (requestCode == REQUEST_CREATE_TRIP) {
            allTrips.add(trip);
        }
        if (requestCode == REQUEST_UPDATE_TRIP) {
            allTrips.set(data.getExtras().getInt("position"), trip);
        }
        tripAdapter.notifyDataSetChanged();
    }
}


