package com.example.clarisselawson.tripbudget;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.clarisselawson.tripbudget.database.SpentDBHelper;

import java.util.Calendar;
import java.util.Date;

public class EditSpentActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText libelleView;
    private EditText amountView;
    private Spinner categoryView;
    private Button dateView;

    private SpentDBHelper myDb;

    private Date date;
    private Trip trip;

    private DatePickerDialog datePickerDialog;

    // id of the spent being edited
    // -1 for new spents
    private int spentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_spent);

        /*SQLiteDatabase db = myDb.getWritableDatabase();
        myDb.onUpgrade(db, 1, 2);*/
        myDb = new SpentDBHelper(this, getString(R.string.db_name), null, 1);

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
                goToTrip();
            }
        }

        if (spentId >= 0) {
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

        libelle = libelleView.getText().toString();
        amount = Float.parseFloat(amountView.getText().toString());
        category = categoryView.getSelectedItemPosition();

        if (spentId < 0) {
            // create new Spent
            if (myDb.insertSpent(libelle, amount, category, date, trip)) {
                Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
            }
        } else {
            // update existing Spent
            if (myDb.updateSpent(spentId, libelle, amount, category, date, trip)) {
                Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
            }
        }

        goToTrip();
    }

    public void cancelSpentEdition(View view) {
        goToTrip();
    }

    public void goToTrip() {
        Intent intent = new Intent(getApplicationContext(), DisplayTripActivity.class);
        intent.putExtra("trip", trip);
        startActivity(intent);
    }
}