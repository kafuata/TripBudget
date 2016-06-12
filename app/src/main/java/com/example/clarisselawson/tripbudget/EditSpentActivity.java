package com.example.clarisselawson.tripbudget;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.clarisselawson.tripbudget.database.DBHelper;
import com.example.clarisselawson.tripbudget.logger.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.util.Calendar;
import java.util.Date;

public class EditSpentActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static String TAG = "EditSpentActivity";

    private EditText libelleView;
    private EditText amountView;
    private Spinner categoryView;
    private Button dateView;

    private DBHelper myDb;

    private Date date;
    private Trip trip;

    private DatePickerDialog datePickerDialog;

    // id of the spent being edited
    // -1 for new spents
    private int spentId = -1;

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_spent);

        myDb = DBHelper.getInstance(getApplicationContext());
        date = new Date();

        initViews();
        fillFormFields();
        initDatePicker();
    }

    private void fillFormFields() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            spentId = extras.getInt("id", -1);
            trip = extras.getParcelable("trip");

            if (trip == null) {
                Toast.makeText(getApplicationContext(), "No trip selected!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        if (spentId < 0) {
            guessLibelleFromPlace();
        } else {
            Spent spent = myDb.getSpent(spentId);
            if (spent == null) {
                Toast.makeText(getApplicationContext(), "Spent " + spentId + "not found. Creating a new one.", Toast.LENGTH_SHORT).show();
            } else {
                libelleView.setText(spent.getLibelle());
                amountView.setText(Float.toString(spent.getAmount()));
                categoryView.setSelection(spent.getCategory());
                date = spent.getDate();
            }
        }

        dateView.setText(Util.formatDate(date));
    }

    private void guessLibelleFromPlace() {
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            return;
        }

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                if (!likelyPlaces.getStatus().isSuccess()) {
                    Log.e(TAG, likelyPlaces.getStatus().getStatusMessage());
                }
                if (likelyPlaces.getCount() > 0 && libelleView.getText().toString().isEmpty()) {
                    PlaceLikelihood placeLikelihood = likelyPlaces.get(0);
                    String name = placeLikelihood.getPlace().getName().toString();
                    libelleView.setText(name);
                    libelleView.setSelection(0, name.length());
                }
                likelyPlaces.release();
            }
        });
    }

    private void initViews() {
        dateView = (Button) findViewById(R.id.spent_date);
        libelleView = (EditText) findViewById(R.id.spent_libelle);
        amountView = (EditText) findViewById(R.id.spent_amount);

        categoryView = (Spinner) findViewById(R.id.spent_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Spent.CATEGORIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryView.setAdapter(adapter);
    }

    private void initDatePicker() {
        dateView.setOnClickListener(this);

        Calendar calender = Calendar.getInstance();
        calender.setTime(date);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = Util.displayDateOnButton(dateView, year, monthOfYear, dayOfMonth);
            }
        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if (view == dateView) {
            datePickerDialog.show();
        }
    }

    public void saveSpent(View view) {
        String libelle;
        float amount;
        int category;

        libelle = Util.getDefault(libelleView.getText().toString(), "[Sans titre]");
        amount = Float.parseFloat(Util.getDefault(amountView.getText().toString(), "0"));
        category = categoryView.getSelectedItemPosition();

        Spent spent = new Spent(spentId, libelle, amount, category, date, trip);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("spent", spent);

        if (spentId < 0) {
            // create new Spent
            long newId = myDb.insertSpent(spent);
            if (newId != -1) {
                spent.setId(newId);
                Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
            }
        } else {
            // update existing Spent
            if (myDb.updateSpent(spentId, spent) == 1) {
                resultIntent.putExtra("position", getIntent().getExtras().getInt("position"));
                Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
            }
        }

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void cancelSpentEdition(View view) {
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google API connexion failed: " + connectionResult.getErrorMessage());
    }
}