package com.feifei.testv3;

/*
    Methods for accessing local database
    Can be tricky, (prone to errors if null entries are accessed) Watch tutorial if you
        wanna edit. You need to know how to access the database info properly
    Updating database info also takes a while to sync to android studio. Restart all softwares
        nalang to make sure the change is noticed.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    // open a connection to database
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    // close current connection to database
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    // grabbing and parsing of information from database
    public ArrayList<User_Subject> getData() {
        ArrayList<User_Subject> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM UserSubjects", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User_Subject newsubject = new User_Subject(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getInt(3), cursor.getInt(4));
            list.add(newsubject);
            //Log.d("Yeet", cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
}
