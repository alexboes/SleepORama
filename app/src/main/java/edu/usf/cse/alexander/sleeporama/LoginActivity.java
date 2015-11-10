package edu.usf.cse.alexander.sleeporama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;

import edu.usf.cse.android.db.ExternDBHelper;
import edu.usf.cse.android.db.SleepDBManager;

public class LoginActivity extends AppCompatActivity {

    private SleepDBManager dbm;
    private ExternDBHelper edbh;
    Response.Listener<JSONObject> checkLoginResponseListener;
    Response.Listener<JSONObject> createUserResponseListener;
    Response.Listener<JSONArray> retrieveUserSessionsResponseListener;
    Response.Listener<JSONArray> retrieveUserDatapointsResponseListener;
    Response.ErrorListener checkLoginErrorListener;
    boolean loginButtonClicked;
    boolean registerButtonClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButtonClicked = false;

        edbh = new ExternDBHelper(this);

        checkLoginResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String response = edbh.parseAndReturnValue(jsonObject);
                switch(response){
                    case "TRUE" :
                        login(true);
                        break;
                    case "FALSE" :
                        loginButtonClicked = false;
                        break;
                    default:
                        loginButtonClicked = false;
                        break;
                }
            }
        };

        createUserResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String response = edbh.parseAndReturnValue(jsonObject);
                Log.d("Personal", response);
                switch(response){
                    case "TRUE" :
                        login(true);
                        break;
                    case "FALSE" :
                        registerButtonClicked = false;
                        break;
                    default :
                        registerButtonClicked = false;
                }
            }
        };

        retrieveUserSessionsResponseListener =  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("Personal", "Sessions Retrieved");
                edbh.parseAndStoreSessions(jsonArray, dbm);
            }
        };

        retrieveUserDatapointsResponseListener =  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("Personal", "Datapoints Retrieved");
                edbh.parseAndStoreDatapoints(jsonArray, dbm);
            }
        };

        checkLoginErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Personal", volleyError.toString());
                loginButtonClicked = false;
            }
        };

        dbm = new SleepDBManager(this);
        try {
            dbm.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button login = (Button) this.findViewById(R.id.Login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!loginButtonClicked){
                    if(((EditText) findViewById(R.id.username)).getText().toString().equals("debug")){
                        login(true);
                    }
                    else {
                        String username = dbm.getUsername();
                        if (((EditText) findViewById(R.id.username)).getText().toString().equals(username)) {
                            String password = dbm.getPassword();
                            if(((EditText) findViewById(R.id.password)).getText().toString().equals(password)){
                                login(false);
                            }
                        } else {
                            loginButtonClicked = true;
                            edbh.checkLogin(((EditText) findViewById(R.id.username)).getText().toString(), ((EditText) findViewById(R.id.password)).getText().toString(), checkLoginResponseListener, checkLoginErrorListener);
                        }
                    }
                }
            }
        });

        Button register = (Button) this.findViewById(R.id.Register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!registerButtonClicked){
                    String username = dbm.getUsername();
                    if(!(((EditText) findViewById(R.id.username)).getText().toString().equals(username))){
                        Log.d("Personal", "Check1");
                        registerButtonClicked = true;
                        edbh.createUser(((EditText) findViewById(R.id.username)).getText().toString(), ((EditText) findViewById(R.id.password)).getText().toString(), createUserResponseListener, checkLoginErrorListener);
                    }
                }
            }
        });
    }

    public void login(boolean differentUser) {
        Intent intent = new Intent(this, MainActivity.class);
        if(differentUser) {
            dbm.updatePreference(1, ((EditText) findViewById(R.id.username)).getText().toString());
            dbm.updatePreference(2, ((EditText) findViewById(R.id.password)).getText().toString());
            dbm.deleteAllSessions();
            dbm.deleteAllDatapoints();
            edbh.retrieveUserSessions(((EditText) findViewById(R.id.username)).getText().toString(), retrieveUserSessionsResponseListener, checkLoginErrorListener);
            edbh.retrieveUserDatapoints(((EditText) findViewById(R.id.username)).getText().toString(), retrieveUserDatapointsResponseListener, checkLoginErrorListener);
        }
        startActivity(intent);
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
}


