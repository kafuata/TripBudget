package com.example.clarisselawson.tripbudget;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clarisselawson.tripbudget.adapter.SpentAdapter;
import com.example.clarisselawson.tripbudget.database.DBHelper;
import com.example.clarisselawson.tripbudget.listener.SwipeCardListener;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class DisplayTripActivity extends AppCompatActivity {

    private int REQUEST_CREATE_SPENT = 0;
    private int REQUEST_UPDATE_SPENT = 1;

    private Trip trip;
    private ArrayList<Spent> allSpents;

    private HashMap<Long, ArrayList<Spent>> spentsByDate;
    private ArrayList<RecyclerView> recyclerViews;
    private ArrayList<SpentAdapter> spentAdapters;
    private ArrayList<Long> sortedGroupTimestamps;

    private DBHelper myDb;

    TextView destination;
    TextView totalBudget;
    TextView spentTotal;
    ImageView tripImage;

    LinearLayout tripContainer;
    LayoutInflater inflater;

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

            initViews();
            displayTripDetails();
        }
    }

    public void initViews() {
        tripContainer = (LinearLayout) findViewById(R.id.trip_spents_container);
        inflater = LayoutInflater.from(getApplicationContext());

        destination = (TextView) findViewById(R.id.edit_trip_destination);
        totalBudget = (TextView) findViewById(R.id.trip_budget);
        spentTotal = (TextView) findViewById(R.id.spent_total);
        tripImage = (ImageView) findViewById(R.id.trip_image);

        try {
            FileInputStream input = getApplicationContext().openFileInput(String.valueOf(trip.getId()));
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            tripImage.setImageBitmap(bitmap);
            input.close();
        } catch (Exception e) {
        }

        //ScrollView scrollView = (ScrollView) findViewById(R.id.trip_spents_scrollview);
        //scrollView.requestDisallowInterceptTouchEvent(true);
    }

    private void displaySpentGroups() {
        recyclerViews = new ArrayList<>();
        spentAdapters = new ArrayList<>();

        spentsByDate = groupSpentsByDate();
        sortedGroupTimestamps = new ArrayList<>(spentsByDate.keySet());
        Collections.sort(sortedGroupTimestamps);

        // supprimer tout le précédent contenu
        tripContainer.removeAllViews();

        int spentGroupIndex = 0;
        for (Long timestamp : sortedGroupTimestamps) {
            SpentAdapter spentAdapter = new SpentAdapter(spentsByDate.get(timestamp), this);

            LinearLayout recyclerContainer = (LinearLayout) inflater.inflate(R.layout.spentgroup, null, false);
            RecyclerView recyclerView = (RecyclerView) recyclerContainer.findViewById(R.id.spent_recyclerView);

            TextView title = (TextView) inflater.inflate(R.layout.spentgroup_title, null, false);
            title.setText(Util.formatSpentGroupTitle(new Date(timestamp)));

            tripContainer.addView(title);
            tripContainer.addView(recyclerContainer);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(spentAdapter);

            spentAdapters.add(spentAdapter);
            recyclerViews.add(recyclerView);

            setupSwipeListeners(spentGroupIndex++);
        }
    }

    private void setupSwipeListeners(final int spentGroupIndex) {
        Long timestamp = sortedGroupTimestamps.get(spentGroupIndex);
        final ArrayList<Spent> spents = spentsByDate.get(timestamp);
        RecyclerView recyclerView = recyclerViews.get(spentGroupIndex);
        final SpentAdapter spentAdapter = spentAdapters.get(spentGroupIndex);

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
                                    Spent spent = spents.get(position);
                                    myDb.deleteSpent(spent.getId());
                                    spents.remove(position);
                                    allSpents.remove(spent);
                                    spentAdapter.notifyItemRemoved(position);
                                }
                                spentAdapter.notifyDataSetChanged();
                                displayTripDetails();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                Intent intent = new Intent(getApplicationContext(), EditSpentActivity.class);
                                int position = reverseSortedPositions[0];
                                Spent spent = spents.get(position);
                                intent.putExtra("trip", trip);
                                intent.putExtra("spent", spent);
                                intent.putExtra("position", allSpents.indexOf(spent));
                                startActivityForResult(intent, REQUEST_UPDATE_SPENT);
                            }
                        });

        recyclerView.addOnItemTouchListener(spentCardTouchListener);
    }

    public HashMap<Long, ArrayList<Spent>> groupSpentsByDate() {
        HashMap<Long, ArrayList<Spent>> map = new HashMap<>();

        for (Spent spent: allSpents) {
            Date date = spent.getDate();

            if (!map.containsKey(date.getTime())) {
                map.put(date.getTime(), new ArrayList<Spent>());
            }

            map.get(date.getTime()).add(spent);
        }

        return map;
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

        displaySpentGroups();
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

        displayTripDetails();
    }
}