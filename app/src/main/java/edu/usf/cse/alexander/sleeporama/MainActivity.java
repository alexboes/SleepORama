// hello
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

        import org.achartengine.ChartFactory;
        import org.achartengine.GraphicalView;
        import org.achartengine.model.XYMultipleSeriesDataset;
        import org.achartengine.model.XYSeries;
        import org.achartengine.renderer.XYMultipleSeriesRenderer;
        import org.achartengine.renderer.XYSeriesRenderer;

        import java.sql.SQLException;
        import java.util.Calendar;

        import edu.usf.cse.android.db.SleepDBManager;

public class MainActivity extends AppCompatActivity {
    private SleepDBManager dbm;
    private GraphicalView mChart1;
    private XYMultipleSeriesDataset mDataset1 = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer1 = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries1;
    private XYSeriesRenderer mCurrentRenderer1;
    private long sessionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbm = new SleepDBManager(this);
        try {
            dbm.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sessionID = getMaximumSessionID();

            String username = "";
            Bundle b = getIntent().getExtras();
            if (b != null) {
                username = b.getString("EXTRA_MESSAGE");
            }
            TextView t = (TextView) findViewById(R.id.welcome);
            t.setText("Welcome, " + username);


        Button startSleep = (Button) this.findViewById(R.id.startSleep);
        startSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                sendSessionID(dbm.createSession(c.toString()));
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
                Log.d("Personal", "Check1");
                if(sessionID > 1) {
                    sessionID--;
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
            mChart1 = ChartFactory.getCubeLineChartView(this, mDataset1, mRenderer1, 0.3f);
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
    }

    private void setChart1toSessionValues(){
        mCurrentSeries1.clear();
        Cursor mCursor = null;
        double count = 0;
        try {
            mCursor = dbm.getAllSessionDatapoints(sessionID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(mCursor != null){
            while(!(mCursor.isAfterLast())) {
                mCurrentSeries1.add(count, mCursor.getDouble(2));
                count = count + 1.0;
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