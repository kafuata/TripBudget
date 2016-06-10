package com.example.clarisselawson.tripbudget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clarisselawson.tripbudget.Database.TripDBHelper;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Locale.FRANCE;

public class CreateTripActivity extends AppCompatActivity implements View.OnClickListener {

    private TripDBHelper myDb;
    private String DATABASE_NAME = "tripDB";
    EditText destination;
    EditText total;
    Date start;
    Date finish;

    private EditText fromDateEtxt;
    private EditText toDateEtxt;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getCountries());
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.trip_destination);
        textView.setAdapter(adapter);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);

        findViewsById();

        setDateTimeField();

        myDb = new TripDBHelper(this, DATABASE_NAME, null, 1);

        /*SQLiteDatabase db = myDb.getWritableDatabase();
        myDb.onUpgrade(db, 1, 2);*/

        EditText editText = (EditText) findViewById(R.id.trip_destination);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    handled = true;
                }
                return handled;
            }
        });


    }


    private void findViewsById() {
        fromDateEtxt = (EditText) findViewById(R.id.trip_start_date);


        toDateEtxt = (EditText) findViewById(R.id.trip_finish_date);

    }


    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onClick(View view) {
        if (view == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if (view == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }

    public void createTrip(View view) {

        try {
            String start_var = (fromDateEtxt.getText().toString());
            String finish_var = (toDateEtxt.getText().toString());

            start = dateFormatter.parse(start_var);
            finish = dateFormatter.parse(finish_var);

        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("E11111111111", e.toString());
        }

        destination = (EditText) findViewById(R.id.trip_destination);
        total = (EditText) findViewById(R.id.trip_budget);

        if (myDb.insertTrip(destination.getText().toString(), start, finish, Float.parseFloat(total.getText().toString()))) {
            Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void cancelTrip(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private static String[] getCountries() {
        return new String[]
                {"Belgium", "France", "Italy", "Germany", "Spain"};
    }

}
