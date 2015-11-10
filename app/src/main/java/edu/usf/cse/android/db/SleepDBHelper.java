package edu.usf.cse.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Alex on 10/20/2015.
 */
public class SleepDBHelper extends SQLiteOpenHelper {
    private static final String CREATE_PREFERENCES_TABLE =
            "create table preferences(" +
            "_id integer primary key autoincrement, " +
            "information text not null)";
    private static final String CREATE_SESSIONS_TABLE =
            "create table sessions(" +
            "_id integer primary key, " +
            "date text not null)";
    private static final String CREATE_DATAPOINTS_TABLE =
            "create table datapoints(" +
            "_id integer primary key autoincrement, " +
            "session_id integer not null, " +
            "milliseconds integer not null, " +
            "datapoint real not null)";

    private static final String DB_NAME = "SleepORama";
    private static final int DB_VERSION = 1;

    public SleepDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PREFERENCES_TABLE);
        db.execSQL(CREATE_SESSIONS_TABLE);
        db.execSQL(CREATE_DATAPOINTS_TABLE);
        ContentValues values = new ContentValues();
        values.put("information", "null");
        db.insert("preferences", null, values);
        values = new ContentValues();
        values.put("information", "null");
        db.insert("preferences", null, values);
        values = new ContentValues();
        values.put("date", "null");
        db.insert("sessions", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS preferences");
        db.execSQL("DROP TABLE IF EXISTS sessions");
        db.execSQL("DROP TABLE IF EXISTS datapoints");
        onCreate(db);
    }
}
