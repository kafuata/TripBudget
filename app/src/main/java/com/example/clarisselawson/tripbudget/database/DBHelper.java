package com.example.clarisselawson.tripbudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by clarisselawson on 11/06/16.
 */
public abstract class DBHelper<T> extends SQLiteOpenHelper {
    protected String tableName;

    public DBHelper(String tableName, Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.tableName = tableName;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    public boolean insertRow (ContentValues values)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(tableName, null, values);
        return true;
    }

    public T getRow(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + tableName + " WHERE id = ?", new String[] { String.valueOf(id)});

        if (res == null) {
            return null;
        } else {
            res.moveToFirst();
            return cursorToObject(res);
        }
    }

    public boolean updateRow (int id, ContentValues values)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(tableName, values, "id = ?", new String[] { Integer.toString(id) } );

        return true;
    }

    public Integer deleteRow (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
                tableName,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<T> getAllRows()
    {
        ArrayList<T> allSpent = new ArrayList<T>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + tableName, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            allSpent.add(cursorToObject(res));
            res.moveToNext();
        }
        return allSpent;
    }

    protected abstract T cursorToObject(Cursor cursor);
}

