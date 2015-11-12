package edu.usf.cse.alexander.sleeporama;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



import android.bluetooth.BluetoothGattCharacteristic;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.appdatasearch.GetRecentContextCall;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.usf.cse.android.db.ExternDBHelper;
import edu.usf.cse.android.db.SleepDBManager;


public class BluetoothConnectActivity extends Activity implements View.OnClickListener, SensorEventListener {

    private int count = 0;
    public BleWrapper mBleWrapper = null;
    private static final String LOGTAG = "BLETEST";
    private mSensorState mState;
    private String[] gattList = new String[8];
    private TextView topView, hrView;
    private String selected;
    private BluetoothDevice device;

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

    private boolean reading;

    private ArrayList<BluetoothDevice> bleDevices = new ArrayList<BluetoothDevice>();

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

    private enum mSensorState {IDLE, HR_ENABLE, HR_READ
    }


    @Override
    /** LIFECYCLE METHOD
     * This function is called when the applications first starts
     * and it initialize things that need to be initialized on startup
     * and only once.
     *
     */
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);

        reading = false;
        Bundle bundle = getIntent().getExtras();
        sessionID = bundle.getLong("sessionID");

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

        edbh = new ExternDBHelper(this, dbm);

        username = dbm.getUsername();

        selected = getIntent().getStringExtra("MacAddress");
        System.out.println("SHOWING SELECTED VALUE" + selected);
        topView = (TextView) findViewById(R.id.HR);
        hrView = (TextView) findViewById(R.id.HRtext);

        hrView.setText("---");

        View scanButton = findViewById(R.id.action_scan);
        scanButton.setOnClickListener(this);


        View connectButton = findViewById(R.id.action_conect);
        connectButton.setOnClickListener(this);

        View stopButton = findViewById(R.id.action_stop);
        stopButton.setOnClickListener(this);

        View readButton = findViewById(R.id.read_button);
        readButton.setOnClickListener(this);

        mBleWrapper = new BleWrapper(this, new BleWrapperUiCallbacks.Null() {

            @Override
            /**
             *
             * This code posts a toast message box whenever it finds a device
             * Easy way to indicate when a device is found and display information
             * about the device
             *
             */
            public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record)

            {

                String msg = "uiDeviceFound: " + device.getName() + ", " + rssi + ", "
                        + record.toString();

                //Adding device to list
                if(!bleDevices.contains(device))
                    bleDevices.add(device);

                updateTV(hrView, "Devices Found: " + bleDevices.size());
                //MIO FUSE MAC ADDRESS - E0:CA:23:D4:52:12
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "uiDeviceFound: " + msg);
                String dName = device.getName();

                //if (!dName.isEmpty() && device.getName().equals("Lala-FUSE") == true) {
                boolean status;
                //device.getAddress().toString();
                  //                if (selected != null) {
                    //    connectDevice(selected);
                      //  if (mBleWrapper.connect(selected)) {
                        //    Log.d("DEBUG", "CONNECTION SUCCESSFUL" + device.getAddress().toString());
                          //  Toast.makeText(getApplicationContext(), "Connection Succesful to " + device.getName(), Toast.LENGTH_LONG).show();
                            // }

                       //if (status == false){

                       //     Log.d("DEBUG", "uiDeviceFound: Connection problem");
                        //}

            //    }
              //      }
            }//close uiDeviceFound method

            @Override
            public void uiDeviceConnected(BluetoothGatt gatt, BluetoothDevice device){

                Log.d(LOGTAG, "uiDeviceConnected: State = " + mBleWrapper.getAdapter().getState());
                // showToast("Connected to " + device.getName());

            }

            //            @Override
            //            public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device){
            //
            //                Log.d(LOGTAG, "uiDeviceConnected: State = " + mBleWrapper.getAdapter().getState());
            //                mBleWrapper.disconnect();
            //            }


            @Override
                /*

                For now, rather than simply scrolling through the service list and printing
                out the service UUIDs, this method will be resolving them into human-readable names
                and printing those out in the logcat.

                 */
            public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device, List<BluetoothGattService> services) {

                BluetoothGattCharacteristic c;

                    /* Cycle through each element in the service list. For each service converts the
                     UUID to a String value and passes it to resolveUuid method in BleNamesResolver
                     class*/


                for (BluetoothGattService service : services) {

                    String serviceName = BleNamesResolver.resolveUuid(service.getUuid().toString());
                    gattList[count] += serviceName + "\n";
                    count++;
                    Log.d("SERVICE", serviceName);
                    mBleWrapper.getCharacteristicsForService(service);
                }

                Log.d(LOGTAG, "uiAvailableServices: Enabling services");
                c = gatt.getService(BleDefinedUUIDs.Service.HEART_RATE).getCharacteristic(BleDefinedUUIDs.Characteristic.HEART_RATE_MEASUREMENT);
                mBleWrapper.writeDataToCharacteristic(c, new byte[]{0x01});
                mState= mSensorState.HR_ENABLE;
                Log.d("STATUS", "THIS IS THE MSTATE " + mState);

            }

            @Override
            public void uiCharacteristicForService(BluetoothGatt gatt,
                                                   BluetoothDevice device,
                                                   BluetoothGattService service,
                                                   List<BluetoothGattCharacteristic> chars)
            {
                super.uiCharacteristicForService(gatt, device, service, chars);
                for (BluetoothGattCharacteristic c : chars)
                {
                    String charName = BleNamesResolver.resolveCharacteristicName(c.getUuid().toString());
                    gattList [count] += "Characteristic: " + charName + "\n";
                //    count++;
                    Log.d("CHARACTERISTIC", charName);
                }
            }


            @Override
            //*This method lets me do stuff with the sensor once its enabled. This we modify as we want it for what we need*//*
            public void uiSuccessfulWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String description) {

                BluetoothGattCharacteristic c;
                Log.d(LOGTAG, "uiSuccessfulWrite");
                super.uiSuccessfulWrite(gatt, device, service, ch, description);
                Log.d(LOGTAG, "mState is now " + mState);


                switch (mState) {
                    case HR_ENABLE:
                        Log.d(LOGTAG, "uiSuccessfulWrite: Reading Heart Rate");
                        Log.d(LOGTAG, "uiSuccessfulWrite: Successfully enabled Heart Rate " );
                        c = gatt.getService(BleDefinedUUIDs.Service.HEART_RATE).getCharacteristic(BleDefinedUUIDs.Characteristic.HEART_RATE_MEASUREMENT);
                        c = gatt.getService(BleDefinedUUIDs.Service.BATTERY).getCharacteristic(BleDefinedUUIDs.Characteristic.BATTERY_LEVEL);
                        mBleWrapper.requestCharacteristicValue(c);
                        mState = mSensorState.HR_READ;
                        break;

                    case HR_READ:
                           Log.d(LOGTAG, "uiSuccessfulWrite: state = HR_READ");
                           c = gatt.getService(BleDefinedUUIDs.Service.BATTERY).getCharacteristic(BleDefinedUUIDs.Characteristic.BATTERY_LEVEL);
                           mBleWrapper.requestCharacteristicValue(c);
                           break;

                    default:
                        Log.d(LOGTAG, "mState default");
                        break;
                }
            }

            @Override
            public void uiFailedWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String description) {

                super.uiFailedWrite(gatt, device, service, ch, description);
                Log.d(LOGTAG, "uiFailedWrite");
            }

            @Override
            public void uiNewValueForCharacteristic(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
                                                    BluetoothGattCharacteristic ch, String strValue, int intValue, byte[] rawValue, String timestamp) {

                super.uiNewValueForCharacteristic(gatt, device, service, ch, strValue, intValue, rawValue, timestamp);
                switch (mState){

                    case HR_READ:
                        Log.d("Personal", "uiNewValueForCharasteristic");
                        break;
                }

                Log.d(LOGTAG, "uiNewValueForCharasteristic");

                for (byte b : rawValue) {
                    Log.d("Personal", "Val: " + b);

                   // Log.d("test", "THIS IS MY HR" + rawValue[1]);

                }
                updateHR(rawValue[1]);
                //RunOnUI hrView.setText(rawValue[1]);
            }

            @Override
            public void uiGotNotification(	BluetoothGatt gatt,
                                              BluetoothDevice device,
                                              BluetoothGattService service,
                                              BluetoothGattCharacteristic characteristic)
            {
                super.uiGotNotification(gatt, device, service, characteristic);
                String ch = BleNamesResolver.resolveCharacteristicName(characteristic.getUuid().toString());

                Log.d(LOGTAG,  "uiGotNotification: " + ch);
            }
        });

        if (mBleWrapper.checkBleHardwareAvailable() == false) {
            Toast.makeText(this, "No BLE-compatible hardware detected",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void connectDevice(String selected) {

        if (mBleWrapper.connect(selected)) {

            Log.d("DEBUG", "CONNECTION SUCCESSFUL" );
            //Log.d("DEBUG", "CONNECTION SUCCESSFUL" + device.getAddress().toString());
            //Toast.makeText(getApplicationContext(), "Connection Succesful to " + device.getName(), Toast.LENGTH_LONG).show();
        }
    }

    public void updateHR(byte b) {

        final byte updateHR = b;
        Long temp = Calendar.getInstance().getTimeInMillis();
        dbm.createHeartrate(sessionID, temp - milliseconds, (long) updateHR);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hrView.setText("" + updateHR);

            }
        });
    }

    public void updateTV(final TextView tv, final String msg){
        //TextView tvUpdate = tv;
        //String msgUpdate = msg;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(msg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent enableBtIntent;

        //check for Bluetooth enabled on each resume
        if (mBleWrapper.isBtEnabled() == false) {
            //Bluetooth is not enabled. Request to user to turn it on
            enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivity(enableBtIntent);
            finish();
        }

        //init ble wrapper
        mBleWrapper.initialize();
    }

    @Override
    protected void onPause() {

        super.onPause();

        mBleWrapper.diconnect();
        mBleWrapper.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    @Override
        /*
        Once the button is clicked call on the following method.
         */
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.action_scan:
                topView.setText("Scanning");
                mBleWrapper.startScanning();
                break;

            case R.id.action_stop:
                mBleWrapper.stopScanning();
                Intent intent = new Intent(this, AvailableBLEDevicesActivity.class);
                intent.putParcelableArrayListExtra("devices.list", bleDevices);
                startActivity(intent);
                break;

            case R.id.read_button:
                if(!reading) {
                    reading = true;
                    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
                    topView.setText("Heart Rate");
                    ((Button) findViewById(R.id.read_button)).setText("STOP SLEEPING");
                    Toast.makeText(getApplicationContext(), "Reading sensor data...", Toast.LENGTH_LONG).show();
                    testButton();
                }
                else {
                    sendNothing();
                }

                break;

            case R.id.action_conect:

                if(selected != null) {
                    connectDevice(selected);
                    Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Select a device to connect to", Toast.LENGTH_LONG).show();

                break;

            default:
                break;
        }
    }

    private void testButton(){

        BluetoothGatt gatt;
        BluetoothGattCharacteristic HR;


        if(!mBleWrapper.isConnected()){
            return;
        }

        Log.d("Personal", "testButton: Reading Heart Rate");
        gatt = mBleWrapper.getGatt();

        //Enabling notifications for Heart Rate service
        HR = gatt.getService(BleDefinedUUIDs.Service.HEART_RATE).getCharacteristic(BleDefinedUUIDs.Characteristic.HEART_RATE_MEASUREMENT);
        mBleWrapper.setNotificationForCharacteristic(HR, true);
        mBleWrapper.requestCharacteristicValue(HR);
        mState=mSensorState.HR_READ;
        //Maybe?
        Log.d("Personal", "END");
        milliseconds = Calendar.getInstance().getTimeInMillis();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);

    }

    private void sendNothing() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}