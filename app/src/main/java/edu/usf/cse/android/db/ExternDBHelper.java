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
    private Context context;
    private final static String ipv4 = "192.232.176.248";

    public ExternDBHelper(Context c){
        context = c;
    }

    public void checkLogin(String username, String password, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        JsonObjectRequest request = new JsonObjectRequest("http://" + ipv4 + "/SleepORama/checkLogin.php?username=" + username + "&password=" + password, null, responseListener, errorListener);
        Volley.newRequestQueue(context).add(request);
        return;
    }

    public void createSession(String username, long session_id, String date, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        String d = date.replace(' ', '_');
        JsonObjectRequest request = new JsonObjectRequest("http://" + ipv4 + "/SleepORama/createSession.php?username=" + username + "&sessionid=" + session_id + "&date=" + d, null, responseListener, errorListener);
        Volley.newRequestQueue(context).add(request);
        return;
    }

    public void createDatapoint(String username, long session_id, long milliseconds, double datapoint, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        JsonObjectRequest request = new JsonObjectRequest("http://" + ipv4 + "/SleepORama/createDatapoint.php?username=" + username + "&sessionid=" + session_id + "&milliseconds=" + milliseconds + "&datapoint=" + datapoint, null, responseListener, errorListener);
        Volley.newRequestQueue(context).add(request);
        return;
    }

    public String parseAndReturnValue(JSONObject response){
        JSONTokener theTokener = new JSONTokener(response.toString());
        JSONObject result;
        try{
            result = (JSONObject) theTokener.nextValue();
            if(result != null){
                return result.getString("value");
            }
            else {
                return "Value error";
            }
        }
        catch(JSONException je){
            return "JSON error";
        }
    }
}
