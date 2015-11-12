package edu.usf.cse.android.db;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Alex on 11/4/2015.
 */
public class ExternDBHelper {
    private Context context;
    private String ip;

    public ExternDBHelper(Context c, SleepDBManager dbm){
        context = c;
        ip = dbm.getIP();
    }

    public void checkLogin(String username, String password, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        Log.d("Personal", "http://" + ip + "/SleepORama/checkLogin.php?username=" + username + "&password=" + password);
        JsonObjectRequest request = new JsonObjectRequest("http://" + ip + "/SleepORama/checkLogin.php?username=" + username + "&password=" + password, null, responseListener, errorListener);
        Volley.newRequestQueue(context).add(request);
        return;
    }

    public void createUser(String username, String password, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        JsonObjectRequest request = new JsonObjectRequest("http://" + ip + "/SleepORama/createUser.php?username=" + username + "&password=" + password, null, responseListener, errorListener);
        Volley.newRequestQueue(context).add(request);
        return;
    }

    public void createSession(String username, long session_id, String date, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        String d = date.replace(' ', '_');
        JsonObjectRequest request = new JsonObjectRequest("http://" + ip + "/SleepORama/createSession.php?username=" + username + "&sessionid=" + session_id + "&date=" + d, null, responseListener, errorListener);
        Volley.newRequestQueue(context).add(request);
        return;
    }

    public void createDatapoint(String username, long session_id, long milliseconds, double datapoint, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        JsonObjectRequest request = new JsonObjectRequest("http://" + ip + "/SleepORama/createDatapoint.php?username=" + username + "&sessionid=" + session_id + "&milliseconds=" + milliseconds + "&datapoint=" + datapoint, null, responseListener, errorListener);
        Volley.newRequestQueue(context).add(request);
        return;
    }

    public void retrieveUserSessions(String username, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener){
        JsonArrayRequest request = new JsonArrayRequest("http://" + ip + "/SleepORama/retrieveUserSessions.php?username=" + username, responseListener, errorListener);
        Volley.newRequestQueue(context).add(request);
        return;
    }

    public void retrieveUserDatapoints(String username, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener){
        JsonArrayRequest request = new JsonArrayRequest("http://" + ip + "/SleepORama/retrieveUserDatapoints.php?username=" + username, responseListener, errorListener);
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

    public void parseAndStoreSessions(JSONArray response, SleepDBManager dbm){
        JSONObject result;
        String d;
        try{
            for(int i = 0; i < response.length(); i++){
                result = response.getJSONObject(i);
                d = result.getString("date");
                dbm.createSession(d.replace('_',' '));
            }
        }
        catch(JSONException je) {
            Log.d("Personal", je.toString());
        }
    }

    public void parseAndStoreDatapoints(JSONArray response, SleepDBManager dbm){
        JSONObject result;
        Long sid;
        Long m;
        Double d;
        try{
            for(int i = 0; i < response.length(); i++){
                result = response.getJSONObject(i);
                sid = result.getLong("session_id");
                m = result.getLong("milliseconds");
                d = result.getDouble("datapoint");
                dbm.createDatapoint(sid, m, d);
            }
        }
        catch(JSONException je) {
            Log.d("Personal", je.toString());
        }
    }

    public void updateIP(SleepDBManager dbm){
        ip = dbm.getIP();
    }
}
