package com.example.clarisselawson.tripbudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clarisselawson.tripbudget.adapter.SpentAdapter;
import com.example.clarisselawson.tripbudget.database.DBHelper;
import com.example.clarisselawson.tripbudget.listener.SwipeCardListener;

import java.util.ArrayList;

public class DisplayTripActivity extends AppCompatActivity {

    private int REQUEST_CREATE_SPENT = 0;
    private int REQUEST_UPDATE_SPENT = 1;

    private Trip trip;
    private ArrayList<Spent> allSpents;
    private RecyclerView recyclerView;
    private SpentAdapter spentAdapter;

    private DBHelper myDb;

    TextView destination;
    TextView totalBudget;
    TextView spentTotal;
    ImageView tripImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            trip = extras.getParcelable("trip");

            if (trip == null) {
                // TODO: error message or throw
                Toast.makeText(getApplicationContext(), "Trip to display is invalid!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            myDb = DBHelper.getInstance(getApplicationContext());
            allSpents = myDb.getAllSpentForTrip(trip);

            spentAdapter = new SpentAdapter(allSpents, this);
            recyclerView = (RecyclerView) findViewById(R.id.spent_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(spentAdapter);
            setupSwipeListeners();

            initViews();
            displayTripDetails();
        }
    }

    public void initViews() {
        destination = (TextView) findViewById(R.id.edit_trip_destination);
        totalBudget = (TextView) findViewById(R.id.edit_trip_budget);
        spentTotal = (TextView) findViewById(R.id.spent_total);
        tripImage = (ImageView) findViewById(R.id.trip_image);
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
                                displayTripDetails();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                Intent intent = new Intent(getApplicationContext(), EditSpentActivity.class);
                                int position = reverseSortedPositions[0];
                                intent.putExtra("trip", trip);
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

    private void displayTripDetails() {
        float totalSpent = 0;
        for (Spent spent: allSpents) {
            totalSpent += spent.getAmount();
        }

        destination.setText(trip.getDestination());
        totalBudget.setText(Float.toString(trip.getBudget())+ "€");
        spentTotal.setText(String.valueOf(totalSpent)+ "€");
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
        displayTripDetails();
    }
}