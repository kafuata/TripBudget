package com.example.clarisselawson.tripbudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.clarisselawson.tripbudget.Trip;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by clarisselawson on 05/06/16.
 */
public class TripDBHelper extends DBHelper<Trip> {

    public static final String TRIPS_COLUMN_ID = "id";
    public static final String TRIPS_COLUMN_DESTINATION = "destination";
    public static final String TRIPS_COLUMN_START = "start";
    public static final String TRIPS_COLUMN_TOTAL_BUDGET = "total";
    public static final String TRIPS_COLUMN_FINISH = "finish";

    public TripDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super("trips", context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + tableName +
                        "(id INTEGER PRIMARY KEY, destination STRING, start INTEGER, finish INTEGER, total FLOAT)"
        );
    }

    public long insertTrip (Trip trip)
    {
        return insertRow(getValues(trip));
    }

    public Trip getTrip(long id){
        return getRow(id);
    }

    public int updateTrip (long id, Trip trip)
    {
        return updateRow(id, getValues(trip));
    }

    public int deleteTrip (long id)
    {
        return deleteRow(id);
    }

    public ArrayList<Trip> getAllTrips()
    {
        return getAllRows();
    }

    @Override
    protected Trip cursorToObject(Cursor cursor) {
        return new Trip(
                cursor.getInt(cursor.getColumnIndex(TRIPS_COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_DESTINATION)),
                cursor.getFloat(cursor.getColumnIndex(TRIPS_COLUMN_TOTAL_BUDGET)),
                new Date(cursor.getLong(cursor.getColumnIndex(TRIPS_COLUMN_START))),
                new Date(cursor.getLong(cursor.getColumnIndex(TRIPS_COLUMN_FINISH)))
        );
    }

    private ContentValues getValues(Trip trip) {
        ContentValues values = new ContentValues();

        values.put("destination", trip.getDestination());
        values.put("start", trip.getStartDate().getTime());
        values.put("finish", trip.getFinishDate().getTime());
        values.put("total", trip.getBudget());

        return values;
    }
}
