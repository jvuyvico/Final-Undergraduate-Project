package com.feifei.testv4.SQLite;

/*
    Methods for accessing local database
    Can be tricky, (prone to errors if null entries are accessed) Watch tutorial if you
        wanna edit. You need to know how to access the database info properly
    Updating database info also takes a while to sync to android studio. Restart all softwares
        nalang to make sure the change is noticed.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.feifei.testv4.Classes.Attendance_Data;
import com.feifei.testv4.Classes.Scan_Data;
import com.feifei.testv4.Classes.User_Subject;
import com.feifei.testv4.Classes.User_SubjectExtended;

import java.util.ArrayList;

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
    /*** Subjects ***/
    public ArrayList<User_Subject> getData() {
        ArrayList<User_Subject> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM UserSubjects", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User_Subject newsubject = new User_Subject(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getString(5));
            list.add(newsubject);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public boolean insertData(User_Subject userSubject) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Subject", userSubject.getSubject());
        contentValues.put("Section", userSubject.getSection());
        contentValues.put("Days", userSubject.getDays());
        contentValues.put("TimeStart", userSubject.getTimestart());
        contentValues.put("TimeEnd", userSubject.getTimeend());
        contentValues.put("UUID", userSubject.getUuid());
        long result = database.insert("UserSubjects", null, contentValues);
        if (result==-1){
            return false;
        } else {
            return true;
        }
    }

    public boolean insertExtendedData(User_SubjectExtended userSubject) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Subject", userSubject.getSubject());
        contentValues.put("Section", userSubject.getSection());
        contentValues.put("Days", userSubject.getDays());
        contentValues.put("TimeStart", userSubject.getTimestart());
        contentValues.put("TimeEnd", userSubject.getTimeend());
        contentValues.put("UUID", userSubject.getUuid());
        contentValues.put("Major", userSubject.getMajor());
        contentValues.put("Minor", userSubject.getMinor());
        long result = database.insert("UserSubjects", null, contentValues);
        if (result==-1){
            return false;
        } else {
            return true;
        }
    }

    public void deleteData(String subject) {
        Cursor cursor = database.rawQuery("SELECT * FROM UserSubjects where Subject = ?" , new String[]{subject});
        if (cursor.getCount() > 0) {
            database.delete("UserSubjects", "Subject=?", new String[]{subject});
        }
    }

    public void deleteAllData(){
        database.execSQL("delete from UserSubjects");
    }
    /*** Subjects ***/

    /*** Credentials***/
    String temp="";
    public String getAdminUsername() {
        Cursor cursor = database.rawQuery("SELECT * FROM Credentials", null);
        cursor.moveToFirst();
        temp = cursor.getString(1);
        cursor.close();
        return temp;
    }
    public String getAdminPassword() {
        Cursor cursor = database.rawQuery("SELECT * FROM Credentials", null);
        cursor.moveToFirst();
        temp = cursor.getString(2);
        cursor.close();
        return temp;
    }
    public String getStudentUsername() {
        Cursor cursor = database.rawQuery("SELECT * FROM Credentials", null);
        cursor.moveToFirst();
        cursor.moveToNext();
        if(cursor.isAfterLast()){
            temp = "none ";
        } else {
            temp = cursor.getString(1);
        }
        cursor.close();
        return temp;
    }
    public String getStudentNumber() {
        Cursor cursor = database.rawQuery("SELECT * FROM Credentials", null);
        cursor.moveToFirst();
        cursor.moveToNext();
        if(cursor.isAfterLast()){
            temp = "none";
        } else {
            temp = cursor.getString(2);
        }
        cursor.close();
        return temp;
    }

    public void insertStudentData(String username, String studentnumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Identifier", "Student");
        contentValues.put("Username", username);
        contentValues.put("Field2", studentnumber);
        database.insert("Credentials", null, contentValues);
    }

    public void updateStudentData(String username, String studentnumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Identifier", "Student");
        contentValues.put("Username", username);
        contentValues.put("Field2", studentnumber);
        database.update("Credentials", contentValues, "Identifier = ?", new String[]{"Student"});
    }
    /*** Credentials***/

    /*** Scan Data ***/
    public ArrayList<Scan_Data> getScanData() {
        ArrayList<Scan_Data> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM ScanData", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Scan_Data newScanData = new Scan_Data(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2));
            list.add(newScanData);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public boolean insertScanData(Scan_Data scanData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("UUID", scanData.getUuid());
        contentValues.put("Time", scanData.getTime());
        contentValues.put("RSSI", scanData.getRssi());
        long result = database.insert("ScanData", null, contentValues);
        if (result==-1){
            return false;
        } else {
            return true;
        }
    }
    /*** Scan Data ***/

    /*** Attendance Data ***/
    public ArrayList<Attendance_Data> getAttendanceData() {
        ArrayList<Attendance_Data> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM AttendanceData", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Attendance_Data newAttendanceData = new Attendance_Data(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5), cursor.getString(6) );
            list.add(newAttendanceData);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public boolean insertAttendanceData(Attendance_Data attendanceData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Subject", attendanceData.getSubject());
        contentValues.put("Status", attendanceData.getStatus());
        contentValues.put("UUID", attendanceData.getUuid());
        contentValues.put("Major", attendanceData.getMajor());
        contentValues.put("Minor", attendanceData.getMinor());
        contentValues.put("Date", attendanceData.getDate());
        contentValues.put("Time", attendanceData.getTime());

        long result = database.insert("AttendanceData", null, contentValues);
        if (result==-1){
            return false;
        } else {
            return true;
        }
    }
    /*** Attendance Data ***/

    /*** Ping Data ***/
    public ArrayList<Integer> getPings() {
        ArrayList<Integer> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Pings", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int status = cursor.getInt(1);
            list.add(status);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public boolean insertPing(int number, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Number", number);
        contentValues.put("Status", status);

        long result = database.insert("Pings", null, contentValues);
        if (result==-1){
            return false;
        } else {
            return true;
        }
    }

    public void clearPings() {
        database.execSQL("DROP TABLE IF EXISTS Pings");


        database.execSQL("CREATE TABLE \"Pings\" (\n" +
                "\t\"Number\"\tTEXT NOT NULL,\n" +
                "\t\"Status\"\tINTEGER NOT NULL DEFAULT 0\n" +
                ")");

    }
    /*** Ping Data ***/
}
