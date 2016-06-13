package com.example.clarisselawson.tripbudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.clarisselawson.tripbudget.R;
import com.example.clarisselawson.tripbudget.Spent;
import com.example.clarisselawson.tripbudget.Trip;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by clarisselawson on 11/06/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String SPENT_TABLE = "spent";
    public static final String SPENT_COLUMN_ID = "id";
    public static final String SPENT_COLUMN_LIBELLE = "libelle";
    public static final String SPENT_COLUMN_AMOUNT = "amount";
    public static final String SPENT_COLUMN_CATEGORY = "category";
    public static final String SPENT_COLUMN_DATE = "date";
    public static final String SPENT_COLUMN_tripID = "tripID";

    public static final String TRIPS_TABLE = "trips";
    public static final String TRIPS_COLUMN_ID = "id";
    public static final String TRIPS_COLUMN_DESTINATION = "destination";
    public static final String TRIPS_COLUMN_START = "start";
    public static final String TRIPS_COLUMN_TOTAL_BUDGET = "total";
    public static final String TRIPS_COLUMN_FINISH = "finish";

    private static DBHelper sInstance;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext(), context.getString(R.string.db_name), null, R.integer.db_version);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TRIPS_TABLE +
                        "(id INTEGER PRIMARY KEY, destination STRING, start INTEGER, finish INTEGER, total FLOAT)"
        );
        db.execSQL(
                "CREATE TABLE " + SPENT_TABLE +
                        "(id INTEGER PRIMARY KEY, libelle STRING, amount FLOAT, category INTEGER, date INTEGER, tripID INTEGER, " +
                        " FOREIGN KEY(tripID) REFERENCES trip(id))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SPENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRIPS_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SPENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRIPS_TABLE);
        onCreate(db);
    }

    public long insertRow(String tableName, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(tableName, null, values);
    }

    public Cursor getRow(String tableName, long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + tableName + " WHERE id = ?", new String[]{String.valueOf(id)});

        if (res == null) {
            return null;
        } else {
            res.moveToFirst();
            return res;
        }
    }

    public int updateRow(String tableName, long id, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(tableName, values, "id = ?", new String[]{Long.toString(id)});
    }

    public int deleteRow(String tableName, Long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
                tableName,
                "id = ? ",
                new String[]{Long.toString(id)});
    }

    public Cursor getAllRows(String tableName) {
        return getAllRows(tableName, "1 = 1");
    }

    public Cursor getAllRows(String tableName, String whereClause) {

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + tableName + " WHERE " + whereClause, null);
    }

    // Spent API =========================================

    public long insertSpent(Spent spent) {
        return insertRow(SPENT_TABLE, getValues(spent));
    }

    public Spent getSpent(long id) {
        return cursorToSpent(getRow(SPENT_TABLE, id));
    }

    public int updateSpent(Spent spent) {
        return updateRow(SPENT_TABLE, spent.getId(), getValues(spent));
    }

    public int deleteSpent(long id) {
        return deleteRow(SPENT_TABLE, id);
    }

    public ArrayList<Spent> getAllSpent() {
        return cursorToSpents(getAllRows(SPENT_TABLE));
    }

    public ArrayList<Spent> getAllSpentForTrip(Trip trip) {
        ArrayList<Spent> rows = cursorToSpents(getAllRows(SPENT_TABLE, "tripID = " + trip.getId()));
        for (Spent row : rows) {
            row.setTrip(trip);
        }
        return rows;
    }

    private ArrayList<Spent> cursorToSpents(Cursor cursor) {

        ArrayList<Spent> list = new ArrayList<Spent>();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            list.add(cursorToSpent(cursor));
            cursor.moveToNext();
        }
        return list;
    }

    protected Spent cursorToSpent(Cursor cursor) {
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

    private ContentValues getValues(Spent spent) {
        ContentValues values = new ContentValues();

        values.put("libelle", spent.getLibelle());
        values.put("amount", spent.getAmount());
        values.put("category", spent.getCategory());
        values.put("date", spent.getDate().getTime());
        values.put("tripID", spent.getTripId());

        return values;
    }

    // Trip API =================================
    public long insertTrip(Trip trip) {
        return insertRow(TRIPS_TABLE, getValues(trip));
    }

    public Trip getTrip(long id) {
        return cursorToTrip(getRow(TRIPS_TABLE, id));
    }

    public int updateTrip(Trip trip) {
        return updateRow(TRIPS_TABLE, trip.getId(), getValues(trip));
    }

    public int deleteTrip(long id) {
        return deleteRow(TRIPS_TABLE, id);
    }

    public ArrayList<Trip> getAllTrips() {
        return cursorToTrips(getAllRows(TRIPS_TABLE));
    }

    private ArrayList<Trip> cursorToTrips(Cursor cursor) {

        ArrayList<Trip> list = new ArrayList<Trip>();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            list.add(cursorToTrip(cursor));
            cursor.moveToNext();
        }
        return list;
    }

    protected Trip cursorToTrip(Cursor cursor) {
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

