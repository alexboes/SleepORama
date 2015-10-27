package edu.usf.cse.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by Alex on 10/20/2015.
 */
public class SleepDBManager {
    private Context context;
    private SQLiteDatabase db;
    private SleepDBHelper dbHelper;

    public SleepDBManager(Context context){
        this.context = context;
    }

    public void open() throws SQLException {
        dbHelper = new SleepDBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues createPreferencesValues(String information) {
        ContentValues values = new ContentValues();
        values.put("information", information);
        return values;
    }

    private ContentValues createSessionsValues(String date){
        ContentValues values = new ContentValues();
        values.put("date", date);
        return values;
    }

    private ContentValues createDatapointsValues(long session_id, double datapoint){
        ContentValues values = new ContentValues();
        values.put("session_id", session_id);
        values.put("datapoint", datapoint);
        return values;
    }

    public long createPreference(String information){
        ContentValues initialValues = createPreferencesValues(information);
        return db.insert("preferences", null, initialValues);
    }

    public long createSession(String date){
        ContentValues initialValues = createSessionsValues(date);
        return db.insert("sessions", null, initialValues);
    }

    public long createDatapoint(Long session_id, double datapoint){
        ContentValues initialValues = createDatapointsValues(session_id, datapoint);
        return db.insert("datapoints", null, initialValues);
    }

    public boolean updatePreference(long p_id, String information){
        ContentValues updateValues = createPreferencesValues(information);
        return db.update("preferences", updateValues, "_id=" + p_id, null) > 0;
    }

    public boolean updateSession(long session_id, String date){
        ContentValues updateValues = createSessionsValues(date);
        return db.update("sessions", updateValues, "_id=" + session_id, null) > 0;
    }

    public boolean updateDatapoint(long d_id, long session_id, double datapoint){
        ContentValues updateValues = createDatapointsValues(session_id, datapoint);
        return db.update("datapoints", updateValues, "_id=" + d_id, null) > 0;
    }

    public boolean deletePreference(long p_id){
        return db.delete("preferences", "_id=" + p_id, null) > 0;
    }

    public boolean deleteSession(long session_id){
        return db.delete("sessions", "_id=" + session_id, null) > 0;
    }

    public boolean deleteDatapoint(long d_id){
        return db.delete("datapoints", "_id=" + d_id, null) > 0;
    }

    public Cursor getAllSessionDatapoints(long session_id) throws SQLException{
        Cursor mCursor = db.query(true, "datapoints", new String[] {"_id", "session_id", "datapoint"}, "session_id=" + session_id, null, null, null, null, null);
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getAllSessions() {
        return db.query("sessions", new String[]{"_id", "date"}, null, null, null, null, null);
    }
}
