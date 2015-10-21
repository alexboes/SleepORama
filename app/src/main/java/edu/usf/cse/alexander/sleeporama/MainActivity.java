package edu.usf.cse.alexander.sleeporama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import edu.usf.cse.android.db.SleepDBManager;

public class MainActivity extends AppCompatActivity {
    private SleepDBManager dbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbm = new SleepDBManager(this);

        Button startSleep = (Button) findViewById(R.id.startSleep);
        startSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                sendSessionID(dbm.createSession(c.toString()));
            }
        });

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBack();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void sendSessionID(long sid){
        Intent intent = new Intent(this, CollectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("sessionID", sid);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void sendBack(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
