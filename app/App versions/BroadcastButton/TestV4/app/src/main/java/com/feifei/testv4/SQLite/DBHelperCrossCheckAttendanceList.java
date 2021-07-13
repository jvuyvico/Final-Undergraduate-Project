package com.feifei.testv4.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelperCrossCheckAttendanceList extends  SQLiteOpenHelper{

    public DBHelperCrossCheckAttendanceList(Context context){
        super(context, "DummyAttendanceRecord.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table comparerecordsdetails(course TEXT, student TEXT, attendanceclass TEXT, date TEXT, status TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists comparerecordsdetails");
        onCreate(db);
    }

    public Boolean insertuserdata(String course, String student, String attendanceclass, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("course", course);
        contentValues.put("student", student);
        contentValues.put("attendanceclass", attendanceclass);
        contentValues.put("date", date);
        contentValues.put("status", status);
        long result = db.insert("comparerecordsdetails", null, contentValues);
        if (result == -1) {
            return false;
        }
        else {
            Log.d("Inside DBhelper", "Added");

            return true;
        }
    }
/*
    public Boolean updateuserdata(String course, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("course", course);
        contentValues.put("date", date);
        contentValues.put("status", status);
        long result = db.update("comparerecordsdetails", contentValues, );
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }
    */

    public Cursor getdata(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from comparerecordsdetails", null);
        return cursor;
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from comparerecordsdetails");
    }

}
