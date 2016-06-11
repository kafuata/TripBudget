package com.example.clarisselawson.tripbudget;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clarisselawson.tripbudget.database.TripDBHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTripActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static String TAG = "EditTripActivity";
    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;

    // http://stackoverflow.com/a/13824556
    private static final LatLngBounds EARTH = new LatLngBounds(
            new LatLng(-84.9, -180), // top left corner of map
            new LatLng(84.9, 180) // bottom right corner
            );

    private TripDBHelper myDb;
    private String DATABASE_NAME = "tripDB";

    private EditText destinationView;
    private EditText budgetView;
    private Button startView;
    private Button finishView;

    private Date startDate;
    private Date finishDate;
    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog finishDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    // id of the trip being edited
    // -1 for new trips
    private int tripId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        /*SQLiteDatabase db = myDb.getWritableDatabase();
        myDb.onUpgrade(db, 1, 2);*/
        myDb = new TripDBHelper(this, DATABASE_NAME, null, 1);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
        startDate = finishDate = new Date();

        findViewsById();
        fillTripFields();
        initDatePickers();
        setupPlaceAutocomplete();
    }

    private void setupPlaceAutocomplete() {
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.trip_destination);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, EARTH, null);
        mAutocompleteView.setAdapter(mAdapter);
    }

    private void fillTripFields() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tripId = extras.getInt("id");
        }

        if (tripId >= 0) {
            Trip trip = myDb.getTrip(tripId);
            if (trip == null) {
                Toast.makeText(getApplicationContext(), "Trip " + tripId + "not found. Creating a new one.", Toast.LENGTH_SHORT).show();
            } else {
                destinationView.setText(trip.getDestination());
                budgetView.setText(Float.toString(trip.getBudget()));

                startDate = trip.getStartDate();
                finishDate = trip.getFinishDate();
            }
        }

        startView.setText(formatDate(startDate));
        finishView.setText(formatDate(finishDate));
    }

    private void findViewsById() {
        startView = (Button) findViewById(R.id.trip_start_date);
        finishView = (Button) findViewById(R.id.trip_finish_date);
        destinationView = (EditText) findViewById(R.id.trip_destination);
        budgetView = (EditText) findViewById(R.id.trip_budget);
    }

    private void initDatePickers() {
        startView.setOnClickListener(this);
        finishView.setOnClickListener(this);

        Calendar calender = Calendar.getInstance();

        calender.setTime(startDate);
        startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startDate = onNewDate(startView, year, monthOfYear, dayOfMonth);
            }
        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));


        calender.setTime(finishDate);
        finishDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                finishDate = onNewDate(startView, year, monthOfYear, dayOfMonth);
            }

        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
    }

    private String formatDate(Date date) {
        return dateFormatter.format(date.getTime());
    }
    private Date onNewDate(Button button, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Date date = calendar.getTime();

        button.setText(formatDate(date));
        return date;
    }

    @Override
    public void onClick(View view) {
        if (view == startView) {
            startDatePickerDialog.show();
        } else if (view == finishView) {
            finishDatePickerDialog.show();
        }
    }

    public void saveTrip(View view) {
        String destination;
        float budget;

        destination = destinationView.getText().toString();
        budget = Float.parseFloat(budgetView.getText().toString());

        if (tripId < 0) {
            // create new trip
            if (myDb.insertTrip(destination, startDate, finishDate, budget)) {
                Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
            }
        } else {
            // update existing trip
            if (myDb.updateTrip(tripId, destination, startDate, finishDate, budget)) {
                Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
            }
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void cancelTripEdition(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            Log.i(TAG, "Place details received: " + place.getName());
            places.release();
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
