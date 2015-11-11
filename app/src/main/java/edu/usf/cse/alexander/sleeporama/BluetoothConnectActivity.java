package edu.usf.cse.alexander.sleeporama;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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

import java.util.ArrayList;
import java.util.List;


public class BluetoothConnectActivity extends Activity implements View.OnClickListener {

    private int count = 0;
    public BleWrapper mBleWrapper = null;
    private static final String LOGTAG = "BLETEST";
    private mSensorState mState;
    private String[] gattList = new String[8];
    private TextView topView, hrView;
    private String selected;
    private BluetoothDevice device;

    private ArrayList<BluetoothDevice> bleDevices = new ArrayList<BluetoothDevice>();

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

        View showSC = findViewById(R.id.services_characteristics);
        showSC.setOnClickListener(this);

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
                        Log.d(LOGTAG, "uiNewValueForCharasteristic");
                        break;
                }

                Log.d(LOGTAG, "uiNewValueForCharasteristic");

                for (byte b : rawValue) {
                    Log.d(LOGTAG, "Val: " + b);

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
                topView.setText("Heart Rate");
                Toast.makeText(getApplicationContext(), "Reading sensor data...", Toast.LENGTH_LONG).show();
                testButton();

                break;

            case R.id.services_characteristics:
                Intent newIntent = new Intent(this, ServicesCharacteristicsActivity.class);
                newIntent.putExtra("services", gattList);
                //intent.putParcelableArrayListExtra("device.list", bleDevices);
                startActivity(newIntent);
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
        BluetoothGattCharacteristic HR, BT;


        if(!mBleWrapper.isConnected()){
            return;
        }

        Log.d(LOGTAG, "testButton: Reading Heart Rate");
        gatt = mBleWrapper.getGatt();

        //Enabling notifications for Heart Rate service
        HR = gatt.getService(BleDefinedUUIDs.Service.HEART_RATE).getCharacteristic(BleDefinedUUIDs.Characteristic.HEART_RATE_MEASUREMENT);
        mBleWrapper.setNotificationForCharacteristic(HR, true);
        mBleWrapper.requestCharacteristicValue(HR);
        mState=mSensorState.HR_READ;
        //Maybe?
        Log.d(LOGTAG, "END");

    }

}