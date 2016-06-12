package com.example.clarisselawson.tripbudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.clarisselawson.tripbudget.adapter.SpentAdapter;
import com.example.clarisselawson.tripbudget.database.SpentDBHelper;
import com.example.clarisselawson.tripbudget.listener.SwipeCardListener;

import java.util.ArrayList;

public class DisplayTripActivity extends AppCompatActivity {

    private int REQUEST_CREATE_SPENT = 0;
    private int REQUEST_UPDATE_SPENT = 1;

    private Trip trip;
    private ArrayList<Spent> allSpents;
    private RecyclerView recyclerView;
    private SpentAdapter spentAdapter;

    private SpentDBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip);

        TextView destination = (TextView) findViewById(R.id.edit_trip_destination);
        TextView totalBudget = (TextView) findViewById(R.id.edit_trip_budget);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            trip = extras.getParcelable("trip");

            if (trip == null) {
                // TODO: error message or throw
                return;
            }
            destination.setText(trip.getDestination());
            destination.setFocusableInTouchMode(true);
            destination.setClickable(true);

            totalBudget.setText(Float.toString(trip.getBudget()));
            totalBudget.setFocusableInTouchMode(true);
            totalBudget.setClickable(true);

            myDb = new SpentDBHelper(this, getString(R.string.db_name), null, R.integer.db_version);
            allSpents = myDb.getAllSpentForTrip(trip);

            spentAdapter = new SpentAdapter(allSpents, this);
            recyclerView = (RecyclerView) findViewById(R.id.spent_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(spentAdapter);
            setupSwipeListeners();
        }
    }

    private void setupSwipeListeners() {
        SwipeCardListener spentCardTouchListener =
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
                                    myDb.deleteSpent(allSpents.get(position).getId());
                                    allSpents.remove(position);
                                    spentAdapter.notifyItemRemoved(position);
                                }
                                spentAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                Intent intent = new Intent(getApplicationContext(), EditSpentActivity.class);
                                int position = reverseSortedPositions[0];
                                intent.putExtra("spent", allSpents.get(position));
                                intent.putExtra("position", position);
                                startActivityForResult(intent, REQUEST_UPDATE_SPENT);
                            }
                        });

        recyclerView.addOnItemTouchListener(spentCardTouchListener);
    }

    public  void addSpent(View v){
        Intent intent = new Intent(this, EditSpentActivity.class);;
        intent.putExtra("trip", trip);

        startActivityForResult(intent, REQUEST_CREATE_SPENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        Spent spent = data.getExtras().getParcelable("spent");
        if (requestCode == REQUEST_CREATE_SPENT) {
            allSpents.add(spent);
        }
        if (requestCode == REQUEST_UPDATE_SPENT) {
            allSpents.set(data.getExtras().getInt("position"), spent);
        }
        spentAdapter.notifyDataSetChanged();
    }
}