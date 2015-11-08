// hello its jake
package edu.usf.cse.alexander.sleeporama;

        import android.content.Intent;
        import android.database.Cursor;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import com.android.volley.Response;
        import com.android.volley.VolleyError;

        import org.achartengine.ChartFactory;
        import org.achartengine.GraphicalView;
        import org.achartengine.model.XYMultipleSeriesDataset;
        import org.achartengine.model.XYSeries;
        import org.achartengine.renderer.XYMultipleSeriesRenderer;
        import org.achartengine.renderer.XYSeriesRenderer;
        import org.json.JSONObject;

        import java.sql.SQLException;
        import java.util.Calendar;
        import java.util.TimeZone;

        import edu.usf.cse.android.db.ExternDBHelper;
        import edu.usf.cse.android.db.SleepDBManager;

public class MainActivity extends AppCompatActivity {
    private SleepDBManager dbm;
    private GraphicalView mChart1;
    private XYMultipleSeriesDataset mDataset1 = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer1 = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries1;
    private XYSeriesRenderer mCurrentRenderer1;
    private long sessionID;
    private ExternDBHelper edbh;
    private Response.Listener<JSONObject> createSessionResponseListener;
    private Response.ErrorListener createSessionErrorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Personal", "Main Activity");

        edbh = new ExternDBHelper(this);

        createSessionResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String response = edbh.parseAndReturnValue(jsonObject);
                switch(response){
                    case "TRUE" :
                        Log.d("Personal", "Success");
                        break;
                    case "FAlSE" :
                        Log.d("Personal", "Already Used");
                        break;
                    default :
                        Log.d("Personal", "Error1");
                        break;
                }
            }
        };

        createSessionErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Personal", volleyError.toString());
            }
        };

        dbm = new SleepDBManager(this);
        try {
            dbm.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sessionID = getMaximumSessionID();

        String username = dbm.getUsername();
        TextView welc = (TextView) findViewById(R.id.welcome);
        welc.setText("Welcome, " + username);

        ((TextView) findViewById(R.id.date)).setText("Date: " + dbm.getDate(sessionID));

        Button startSleep = (Button) this.findViewById(R.id.startSleep);
        startSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d = Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime().toGMTString();
                long temp = dbm.createSession(d);
                edbh.createSession(dbm.getUsername(), temp, d, createSessionResponseListener, createSessionErrorListener);
                sendSessionID(temp);
            }
        });

        Button logout = (Button) this.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBack();
            }
        });

        Button prev = (Button) this.findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sessionID > 1) {
                    sessionID--;
                    ((TextView) findViewById(R.id.date)).setText("Date: " + dbm.getDate(sessionID));
                    setChart1toSessionValues();
                }
            }
        });

        Button next = (Button) this.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sessionID < getMaximumSessionID()) {
                    sessionID++;
                    ((TextView) findViewById(R.id.date)).setText("Date: " + dbm.getDate(sessionID));
                    setChart1toSessionValues();
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        LinearLayout charts = (LinearLayout) this.findViewById(R.id.charts);
        if(mChart1 == null){
            initChart();
            mChart1 = ChartFactory.getCubeLineChartView(this, mDataset1, mRenderer1, 0);
            setChart1toSessionValues();
            charts.addView(mChart1);
        } else {
            mChart1.repaint();
        }
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

    private void initChart(){
        mCurrentSeries1 = new XYSeries("Accelerometer Data");
        mDataset1.addSeries(mCurrentSeries1);
        mCurrentRenderer1 = new XYSeriesRenderer();
        mRenderer1.addSeriesRenderer(mCurrentRenderer1);
        mRenderer1.setPanEnabled(false, false);
        mRenderer1.setZoomEnabled(false, false);
    }

    private void setChart1toSessionValues(){
        mCurrentSeries1.clear();
        Cursor mCursor = null;
        try {
            mCursor = dbm.getAllSessionDatapoints(sessionID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.d("Personal", "" + sessionID + " Count: " + mCursor.getCount());
        if(mCursor != null){
            while(!(mCursor.isAfterLast())) {
                mCurrentSeries1.add(mCursor.getLong(2), mCursor.getDouble(3));
                mCursor.moveToNext();
            }
        }
        mChart1.repaint();
    }

    private long getMaximumSessionID (){
        Cursor mCursor = dbm.getAllSessions();
        return mCursor.getCount();
    }
}