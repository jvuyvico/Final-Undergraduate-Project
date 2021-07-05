package com.feifei.testv4.SQLite;

/*
    Method for selecting and opening a local database
    Enter name of desired database in DATABASE_NAME. This database should be
        stored in app/src/main/java/assets/database folders
 */

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "DummyDB.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
