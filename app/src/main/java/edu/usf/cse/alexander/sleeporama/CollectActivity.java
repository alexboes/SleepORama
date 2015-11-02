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

import java.sql.SQLException;

import edu.usf.cse.android.db.SleepDBManager;

public class CollectActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private long sessionID;
    private SleepDBManager dbm;
    private double[] data = new double[10000];
    private double average = 0.0;
    private int minicount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        Bundle bundle = getIntent().getExtras();
        sessionID = bundle.getLong("sessionID");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        dbm = new SleepDBManager(this);
        try {
            dbm.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button endSleep = (Button) this.findViewById(R.id.doneButton);
        endSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Personal", "Hello");
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
                    dbm.createDatapoint(sessionID, average);
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
