package com.example.clarisselawson.tripbudget.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by clarisselawson on 05/06/16.
 */
public class TripDBHelper extends SQLiteOpenHelper {

    public static final String TRIPS_COLUMN_DESTINATION = "destination";
    

    public TripDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table trips " +
                        "(id integer primary key, destination String, duration String,total float, " +
                        "transport float, feed float, outing float, extra float, tripImage blob)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS trips");
        onCreate(db);

    }


    public boolean insertTrip (String destination, String duration, float total, float transport, float feed, float outing, float extra)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("destination", destination);
        values.put("duration", duration);
        values.put("total", total);
        values.put("transport", transport);
        values.put("feed", feed);
        values.put("outing", outing);
        values.put("extra", extra);
        long rowTrip = db.insert("trips", null, values);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from trips where id="+id+"", null );
        return res;
    }

    public boolean updateTrip (Integer id, String destination, String duration, float total, float transport, float feed, float outing, float extra)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("destination", destination);
        values.put("duration", duration);
        values.put("total", total);
        values.put("transport", transport);
        values.put("feed", feed);
        values.put("outing", outing);
        values.put("extra", extra);
        db.update("trips", values, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteTrip (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("trips",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllTrips()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from trips", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(TRIPS_COLUMN_DESTINATION)));
            res.moveToNext();
        }
        return array_list;
    }

}
