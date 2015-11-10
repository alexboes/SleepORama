package edu.usf.cse.alexander.sleeporama;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class AvailableBLEDevicesActivity extends ListActivity{

    private ArrayList<BluetoothDevice> bleDevices;
    private BleWrapper mBleWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_available_bledevices);
        bleDevices =  getIntent().getExtras().getParcelableArrayList("devices.list");

        ArrayAdapter<BluetoothDevice> adapter = new ArrayAdapter<BluetoothDevice>(this, R.layout.list_layout, R.id.text1, bleDevices);

        setListAdapter(adapter);
    }

    /*When an item on the list is clicked, connect to that device*/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //MainActivity main = new MainActivity();
        String selected = ((TextView) v.findViewById(R.id.text1)).getText().toString();
        Log.i("THIS IS MY ITEM", selected);
        Intent mainClass = new Intent(this, BluetoothConnectActivity.class);
        mainClass.putExtra("MacAddress", selected);
        startActivity(mainClass);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_available_bledevices, menu);
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
}