package com.example.clarisselawson.tripbudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.clarisselawson.tripbudget.adapter.TripAdapter;
import com.example.clarisselawson.tripbudget.database.TripDBHelper;
import com.example.clarisselawson.tripbudget.listener.SwipeTripCardListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String DATABASE_NAME = "tripDB";
    private RecyclerView recyclerView;

    private ArrayList<Trip> allTrips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TripDBHelper myDb = new TripDBHelper(this, DATABASE_NAME, null, 1);
        allTrips = myDb.getAllTrips();

        final TripAdapter tripAdapter = new TripAdapter(allTrips, this);
        recyclerView = (RecyclerView) findViewById(R.id.trip_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(tripAdapter);
        setupSwipeListeners(tripAdapter);
    }

    private void setupSwipeListeners(final TripAdapter tripAdapter) {
        SwipeTripCardListener tripCardTouchListener =
                new SwipeTripCardListener(recyclerView,
                        new SwipeTripCardListener.SwipeListener() {
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
                                    allTrips.remove(position);
                                    tripAdapter.notifyItemRemoved(position);
                                }
                                tripAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                int position = reverseSortedPositions[0];
                                int tripId = allTrips.get(position).getId();

                                Intent intent = new Intent(getApplicationContext(), EditTripActivity.class);
                                Bundle dataBundle = new Bundle();
                                dataBundle.putInt("id", tripId);

                                intent.putExtras(dataBundle);
                                startActivity(intent);
                            }
                        });

        recyclerView.addOnItemTouchListener(tripCardTouchListener);
    }

    public void newTrip(View v) {
        Intent intent = new Intent(this, EditTripActivity.class);
        startActivity(intent);
    }
}


