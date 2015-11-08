package edu.usf.cse.alexander.sleeporama;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Calendar;

import edu.usf.cse.android.db.ExternDBHelper;
import edu.usf.cse.android.db.SleepDBManager;

public class CollectActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private long sessionID;
    private SleepDBManager dbm;
    private double[] data = new double[10000];
    private double average = 0.0;
    private int minicount = 0;
    private long milliseconds;
    private ExternDBHelper edbh;
    private Response.Listener<JSONObject> createDatapointResponseListener;
    private Response.ErrorListener createDatapointErrorListener;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        Bundle bundle = getIntent().getExtras();
        sessionID = bundle.getLong("sessionID");
        Calendar c = Calendar.getInstance();
        milliseconds = c.getTimeInMillis();

        edbh = new ExternDBHelper(this);

        createDatapointResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String response = edbh.parseAndReturnValue(jsonObject);
                switch(response){
                    case "TRUE" :
                        Log.d("Personal", "ExternalDB Datapoint Transaction Executed");
                        break;
                    case "False" :
                        Log.d("Personal", "Already Exists");
                        break;
                    default :
                        Log.d("Personal", "Error");
                }
            }
        };

        createDatapointErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Personal", volleyError.toString());
            }
        };

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        dbm = new SleepDBManager(this);
        try {
            dbm.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        username = dbm.getUsername();

        Button endSleep = (Button) this.findViewById(R.id.doneButton);
        endSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNothing();
            }
        });
    }

    //This should send it back to the previous activity
    public void sendMessage(View view) {
        // Do something in response to button
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            synchronized(this){
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                double r = Math.sqrt(x*x + y*y + z*z);
                average += r/100;
                minicount++;
                if(minicount == 100){
                    minicount = 0;
                    long temp = Calendar.getInstance().getTimeInMillis();
                    dbm.createDatapoint(sessionID, (temp - milliseconds), average);
                    edbh.createDatapoint(username, sessionID, (temp - milliseconds), average, createDatapointResponseListener, createDatapointErrorListener);
                    average = 0.0;
                }
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void sendNothing() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
