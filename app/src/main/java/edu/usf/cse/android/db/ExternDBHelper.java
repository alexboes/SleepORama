package edu.usf.cse.android.db;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Alex on 11/4/2015.
 */
public class ExternDBHelper {
    private String returnString = "";
    private Context context;

    public ExternDBHelper(Context c){
        context = c;
    }

    public String checkLogin(String username, String password){
        returnString = "";
        Log.d("Personal", "Check1");
        JsonObjectRequest request = new JsonObjectRequest("http://192.236.124.29/SleepORama/checkLogin.php?username=" + username + "&password=" + password, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        JSONTokener theTokener = new JSONTokener(jsonObject.toString());
                        JSONObject result;
                        try{
                            result = (JSONObject) theTokener.nextValue();
                            if(result != null)
                            {
                                returnString = result.getString("value");
                                Log.d("Personal", returnString);
                            }
                        }
                        catch(JSONException e){
                            returnString = "JSON Error";
                            Log.d("Personal", "JSON Error");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("Personal", "Connection Error");
                    }
                }
        );
        Volley.newRequestQueue(context).add(request);
        return returnString;
    }
}
