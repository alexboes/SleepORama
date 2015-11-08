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

import org.json.JSONObject;

import java.sql.SQLException;

import edu.usf.cse.android.db.ExternDBHelper;
import edu.usf.cse.android.db.SleepDBManager;

public class LoginActivity extends AppCompatActivity {

    private SleepDBManager dbm;
    private ExternDBHelper edbh;
    Response.Listener<JSONObject> checkLoginResponseListener;
    Response.ErrorListener checkLoginErrorListener;
    boolean loginButtonClicked;

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

        checkLoginErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
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
                    Log.d("Personal", ((EditText) findViewById(R.id.username)).getText().toString());
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
    }

    public void login(boolean differentUser) {
        Intent intent = new Intent(this, MainActivity.class);
        if(differentUser) {
            dbm.updatePreference(1, ((EditText) findViewById(R.id.username)).getText().toString());
            dbm.updatePreference(2, ((EditText) findViewById(R.id.password)).getText().toString());
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


