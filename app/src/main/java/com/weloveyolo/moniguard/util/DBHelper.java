package com.weloveyolo.moniguard.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AppDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String CREATE_TABLE_MESSAGE = "CREATE TABLE IF NOT EXISTS message (" +
//                "mid INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "residentId INTEGER," +
//                "content TEXT," +
//                "createdAt TEXT," +
//                "type INTEGER)";
        String CREATE_TABLE_MESSAGE = "CREATE TABLE IF NOT EXISTS message (" +
                "mid INTEGER PRIMARY KEY AUTOINCREMENT," +
                "content TEXT," +
                "type INTEGER," +
                "cameraId INTEGER," +
                "createdAt TEXT," +
                "residentId INTEGER)";

        db.execSQL(CREATE_TABLE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS message");
        onCreate(db);
    }
}
