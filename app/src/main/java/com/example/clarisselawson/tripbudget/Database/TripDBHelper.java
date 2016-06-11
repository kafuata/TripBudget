package com.example.clarisselawson.tripbudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.clarisselawson.tripbudget.Trip;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by clarisselawson on 05/06/16.
 */
public class TripDBHelper extends SQLiteOpenHelper {

    public static final String TRIPS_COLUMN_ID = "id";
    public static final String TRIPS_COLUMN_DESTINATION = "destination";
    public static final String TRIPS_COLUMN_START = "start";
    public static final String TRIPS_COLUMN_TOTAL_BUDGET = "total";
    public static final String TRIPS_COLUMN_FINISH = "finish";

    public TripDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE trips " +
                        "(id INTEGER PRIMARY KEY, destination STRING, start INTEGER, finish INTEGER, total FLOAT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS trips");
        onCreate(db);
    }

    public boolean insertTrip (String destination, Date start, Date finish, float total)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("destination", destination);
        values.put("start", start.getTime());
        values.put("finish", finish.getTime());
        values.put("total", total);

        db.insert("trips", null, values);
        return true;
    }

    public Trip getTrip(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM trips WHERE id = ?", new String[] { String.valueOf(id)});

        if (res == null) {
            return null;
        } else {
            res.moveToFirst();
            return cursorToTrip(res);
        }
    }

    public boolean updateTrip (Integer id, String destination, Date start, Date finish, float total)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("destination", destination);
        values.put("start", start.getTime());
        values.put("finish", finish.getTime());
        values.put("total", total);
        db.update("trips", values, "id = ?", new String[] { Integer.toString(id) } );

        return true;
    }

    public Integer deleteTrip (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("trips",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<Trip> getAllTrips()
    {
        ArrayList<Trip> allTrips = new ArrayList<Trip>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM trips", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            allTrips.add(cursorToTrip(res));
            res.moveToNext();
        }
        return allTrips;
    }

    private Trip cursorToTrip(Cursor cursor) {
        return new Trip(
                cursor.getInt(cursor.getColumnIndex(TRIPS_COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(TRIPS_COLUMN_DESTINATION)),
                cursor.getFloat(cursor.getColumnIndex(TRIPS_COLUMN_TOTAL_BUDGET)),
                new Date(cursor.getLong(cursor.getColumnIndex(TRIPS_COLUMN_START))),
                new Date(cursor.getLong(cursor.getColumnIndex(TRIPS_COLUMN_FINISH)))
        );
    }
}
