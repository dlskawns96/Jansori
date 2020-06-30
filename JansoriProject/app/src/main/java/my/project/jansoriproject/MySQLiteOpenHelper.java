package com.a.term_timetable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { // Set the DB style
        String sql = "create table course (" +
                "_id integer, " +
                "title text, " +
                "prof text, " +
                "day integer, " +
                "startTime text, " +
                "endTime text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists course";
        db.execSQL(sql);
        onCreate(db);
    }
}