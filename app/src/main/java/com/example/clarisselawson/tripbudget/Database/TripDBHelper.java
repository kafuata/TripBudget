package com.example.clarisselawson.tripbudget.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by clarisselawson on 05/06/16.
 */
public class TripDBHelper extends SQLiteOpenHelper {

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
                "create table trips " +
                        "(id integer primary key, destination String, start Integer, finish Integer, total float)"
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

        long rowTrip = db.insert("trips", null, values);
        return true;
    }

    public Cursor getTrip(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from trips where id="+id+"", null );
        return res;
    }

    public boolean updateTrip (Integer id, String destination, Date start, Date finish, float total)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("destination", destination);
        values.put("start", start.getTime());
        values.put("start", finish.getTime());
        values.put("total", total);
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
