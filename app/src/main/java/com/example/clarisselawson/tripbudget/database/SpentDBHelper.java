package com.example.clarisselawson.tripbudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.clarisselawson.tripbudget.Spent;
import com.example.clarisselawson.tripbudget.Trip;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by clarisselawson on 11/06/16.
 */
public class SpentDBHelper extends DBHelper<Spent> {

    public static final String SPENT_COLUMN_ID = "id";
    public static final String SPENT_COLUMN_LIBELLE = "libelle";
    public static final String SPENT_COLUMN_AMOUNT = "amount";
    public static final String SPENT_COLUMN_CATEGORY = "category";
    public static final String SPENT_COLUMN_DATE = "date";
    public static final String SPENT_COLUMN_tripID = "tripID";

    private Context context;

    public SpentDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super("spent", context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + tableName +
                        "(id INTEGER PRIMARY KEY, libelle STRING, amount FLOAT, category INTEGER, date INTEGER, tripID INTEGER, " +
                        " FOREIGN KEY(tripID) REFERENCES trip(id))"
        );
    }

    public long insertSpent (String libelle, float amount, int category, Date date, Trip trip)
    {
        return insertRow(getValues(libelle, amount, category, date, trip));
    }

    public Spent getSpent(long id){
        return getRow(id);
    }

    public int updateSpent (long id, String libelle, float amount, int category, Date date, Trip trip)
    {
        return updateRow(id, getValues(libelle, amount, category, date, trip));
    }

    public int deleteSpent (long id)
    {
        return deleteRow(id);
    }

    public ArrayList<Spent> getAllSpent()
    {
        return getAllRows();
    }

    public ArrayList<Spent> getAllSpentForTrip(Trip trip)
    {
        ArrayList<Spent> rows = getAllRows("tripID = " + trip.getId());
        for(Spent row: rows) {
            row.setTrip(trip);
        }
        return rows;
    }

    @Override
    protected Spent cursorToObject(Cursor cursor) {
        int tripID = cursor.getInt((cursor.getColumnIndex(SPENT_COLUMN_tripID)));

        return new Spent(
                cursor.getInt(cursor.getColumnIndex(SPENT_COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(SPENT_COLUMN_LIBELLE)),
                cursor.getFloat(cursor.getColumnIndex(SPENT_COLUMN_AMOUNT)),
                cursor.getInt(cursor.getColumnIndex(SPENT_COLUMN_CATEGORY)),
                new Date(cursor.getLong(cursor.getColumnIndex(SPENT_COLUMN_DATE))),
                tripID
        );
    }

    private ContentValues getValues(String libelle, float amount, int category, Date date, Trip trip) {
        ContentValues values = new ContentValues();

        values.put("libelle", libelle);
        values.put("amount", amount);
        values.put("category", category);
        values.put("date", date.getTime());
        values.put("tripID", trip.getId());

        return values;
    }
}
