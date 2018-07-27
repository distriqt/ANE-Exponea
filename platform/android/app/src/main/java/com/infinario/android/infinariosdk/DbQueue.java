package com.infinario.android.infinariosdk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This file has been created by igi on 1/13/15.
 */
public class DbQueue {
    private SQLiteDatabase db;
    private DbHelper dbHelper;
    private String[] allColumns = { Contract.COLUMN_ID, Contract.COLUMN_COMMAND, Contract.COLUMN_RETRIES};
    private Object lockAccess;
    private int openCounter;

    public DbQueue(Context context) {
        dbHelper = new DbHelper(context);
        lockAccess = new Object();
        openCounter = 0;
    }

    public boolean schedule(Command command) {
        synchronized (lockAccess){
            openDatabase();
            try{
                ContentValues values = new ContentValues();
                values.put(Contract.COLUMN_COMMAND, command.toString());
                return -1 < db.insert(Contract.TABLE_COMMANDS, null, values);
            } finally {
                closeDatabase();
            }
        }
    }

    public List<Request> pop(Integer limit) {
        Cursor cursor = db.query(Contract.TABLE_COMMANDS, allColumns, null, null, null, null, Contract.COLUMN_ID + " ASC", limit.toString());
        List<Request> requests = new ArrayList<>();

        while (cursor.moveToNext()) {
            try {
                requests.add(new Request(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)));
            } catch (JSONException e) {
                db.delete(Contract.TABLE_COMMANDS, Contract.COLUMN_ID + " = " + cursor.getInt(0), null);
            }
        }

        cursor.close();

        return requests;
    }

    public List<Request> pop() {
        synchronized (lockAccess){
            openDatabase();
            try{
                return pop(Contract.DEFAULT_LIMIT);
            } finally {
                closeDatabase();
            }
        }
    }

    public boolean isEmpty() {
        synchronized (lockAccess){
            openDatabase();
            try{
                Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + Contract.TABLE_COMMANDS, null);
                boolean result = !(cursor.moveToFirst() && cursor.getInt(0) > 0);
                cursor.close();
                return result;
            } finally {
                closeDatabase();
            }
        }

    }

    public void clear(Set<Integer> delete) {
        synchronized (lockAccess){
            openDatabase();
            try{
                db.delete(Contract.TABLE_COMMANDS, Contract.COLUMN_ID + " IN (" + TextUtils.join(", ", delete) + ")", null);
            } finally {
                closeDatabase();
            }
        }
    }

    private void openDatabase(){
        synchronized (lockAccess){
            openCounter++;
            if (openCounter == 1){
                db = dbHelper.getWritableDatabase();
            }
        }
    }

    private void closeDatabase(){
        synchronized (lockAccess){
            openCounter--;
            if (openCounter == 0){
                db.close();
            }
        }
    }
}
