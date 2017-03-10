package dtuqac.runtimerapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TimerActivity extends AppCompatActivity {

    private TimerClass MonTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        MonTimer = new TimerClass();
    }


    public void LoadSplits(View view)
    {
        ListView lv = (ListView) findViewById(R.id.Liste_Splits);

        // Create the item mapping
        String[] from = new String[] { "title", "description" };
        int[] to = new int[] { R.id.title, R.id.description };

        List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("title", "First title"); // This will be shown in R.id.title
        map.put("description", "description 1"); // And this in R.id.description
        fillMaps.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "Second title");
        map.put("description", "description 2");
        fillMaps.add(map);

        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.split_list_item, from, to);
        lv.setAdapter(adapter);

        //lv.setAdapter(fillMaps);
    }

    public void StartTimer(View view)
    {
        if(!MonTimer.IsRunning())
        {
            if (MonTimer.GetMiliseconds() != 0) //Cas ou on est en pause et qu'on clique sur split
            {
                MonTimer.UnpauseTimer();
            }
            else
            {
                MonTimer.StartTimer();
                PollTimer();
                final Button StartButton = (Button)findViewById(R.id.start_button);
                StartButton.setText("Split");
            }
        }
        else
        {
            //TODO: MonTimer.Split();
        }
    }

    public void PauseTimer(View view)
    {
        MonTimer.PauseTimer();
    }

    public void ResetTimer(View view)
    {
        MonTimer.ResetTimer();

        final Button StartButton = (Button)findViewById(R.id.start_button);
        StartButton.setText("Start");
        final TextView MainTimeView = (TextView)findViewById(R.id.main_timer_label);
        final TextView SmallTimeView = (TextView)findViewById(R.id.small_timer_label);
        MainTimeView.setText("0");
        SmallTimeView.setText(".00");
    }

    private void PollTimer()
    {
        final TextView MainTimeView = (TextView)findViewById(R.id.main_timer_label);
        final TextView SmallTimeView = (TextView)findViewById(R.id.small_timer_label);
        //java text view associated with the xml one

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                if (MonTimer.IsRunning())
                {
                    MonTimer.ReportTime();

                    String SmallTimer = String.format(".%02d", (MonTimer.GetMiliseconds()/10) % 100 );
                    String MainTimer = String.format("%d", MonTimer.GetSecondes());

                    if (MonTimer.GetMinutes() > 0)
                    {
                        MainTimer = String.format("%d:%02d", MonTimer.GetMinutes() % 60, MonTimer.GetSecondes() % 60);
                    }
                    if (MonTimer.GetHeures() > 0)
                    {
                        MainTimer = String.format("%d:%02d:%02d", MonTimer.GetHeures(), MonTimer.GetMinutes() % 60, MonTimer.GetSecondes() % 60);
                    }

                    SmallTimeView.setText(SmallTimer);
                    MainTimeView.setText(MainTimer);
                }
                handler.postDelayed(this, 10);
            }
        });

    }

}
