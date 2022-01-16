package com.example.wang.project2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class myDB extends SQLiteOpenHelper {
    private static final String DB_NAME= "db_name";
    private  static final String TABLE_NAME1 = "user";
    private  static final String TABLE_NAME2 = "comment";
    private static final int DB_VERSION = 1;

    public myDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE1 = "CREATE TABLE if not exists "
                + TABLE_NAME1
                + " (name TEXT PRIMARY KEY, password TEXT, icon BLOB)";
        sqLiteDatabase.execSQL(CREATE_TABLE1);
        String CREATE_TABLE2 = "CREATE TABLE "
                + TABLE_NAME2
                + " (date TEXT PRIMARY KEY, name TEXT,speak TEXT,num INTEGER,zan INTEGER)";
        sqLiteDatabase.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int ii) {

    }





}
