package dtuqac.runtimerapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import dtuqac.runtimerapp.SpeedRunEntity;
import dtuqac.runtimerapp.ActiveSpeedrun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//import com.getpebble.android.kit.PebbleKit;
//import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
//import com.getpebble.android.kit.util.PebbleDictionary;

public class TimerActivity extends AppCompatActivity {

    private TimerClass MonTimer;

    private static final UUID WATCHAPP_UUID = UUID.fromString("6456a937-1e6d-40cf-a871-6545ea853727");

    private static final int
            KEY_BUTTON = 0,
            MESSAGE_TIME = 43,
            BUTTON_UP = 0,
            BUTTON_SELECT = 1,
            BUTTON_DOWN = 2;

    private Handler handler = new Handler();
   // private PebbleDataReceiver appMessageReciever;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        MonTimer = new TimerClass();

        if (ActiveSpeedrun.getInstance().IsInitialized())
        {
            LoadSplits();
        }
    }

    public void LoadSplits()
    {
        //Récupère la liste des splits time
        int PBID = ActiveSpeedrun.getInstance().GetPersonnalBestID();
        List<Split> PBSplits = ActiveSpeedrun.getInstance().GetSplitsByAttemptID(PBID);

        //Récupère la liste des splits name
        List<SplitDefinition> SplitsList = ActiveSpeedrun.getInstance().GetActiveSpeedrun().getSpeedRunSplits();

        //Map la liste dans le listview
        TimerSplit_Adapter ListAdapter = new TimerSplit_Adapter(this, SplitsList, PBSplits);

        ListView lv = (ListView) findViewById(R.id.Liste_Splits);

        lv.setAdapter(ListAdapter);

        int x = 0;
        x++;

        /*
        ListView lv = (ListView) findViewById(R.id.Liste_Splits);

        List<SplitDefinition> SplitsList = ActiveSpeedrun.getInstance().GetActiveSpeedrun().getSpeedRunSplits();

        for (SplitDefinition item : SplitsList)
        {
            String test = item.getSplitName();
        }

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
*/

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
/*
    //region PebbleControl

    @Override
    protected void onResume() {
        super.onResume();

        // Define AppMessage behavior
        if(appMessageReciever == null) {
            appMessageReciever = new PebbleDataReceiver(WATCHAPP_UUID) {

                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                    // Always ACK
                    PebbleKit.sendAckToPebble(context, transactionId);

                    // What message was received?
                    if(data.getInteger(KEY_BUTTON) != null) {
                        // KEY_BUTTON was received, determine which button
                        final int button = data.getInteger(KEY_BUTTON).intValue();

                        // Update UI on correct thread
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                switch(button) {
                                    case BUTTON_UP:
                                        whichButtonView.setText("UP");
                                        fctCommunes.ExecVibration(getApplicationContext(),500);
                                        break;
                                    case BUTTON_SELECT:
                                        whichButtonView.setText("SELECT");
                                        fctCommunes.ExecVibration(getApplicationContext(),500);
                                        break;
                                    case BUTTON_DOWN:
                                        whichButtonView.setText("DOWN");
                                        fctCommunes.ExecVibration(getApplicationContext(),500);
                                        break;
                                    default:
                                        Toast.makeText(getApplicationContext(), "Unknown button: " + button, Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                        });
                    }
                }
            };

            // Add AppMessage capabilities
            PebbleKit.registerReceivedDataHandler(this, appMessageReciever);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        //Open app on pebble
        if(PebbleKit.isWatchConnected(getApplicationContext())) {
            PebbleKit.startAppOnPebble(getApplicationContext(), WATCHAPP_UUID);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Close app on pebble
        PebbleKit.closeAppOnPebble(getApplicationContext(),WATCHAPP_UUID);

        // Unregister AppMessage reception
        if(appMessageReciever != null) {
            unregisterReceiver(appMessageReciever);
            appMessageReciever = null;
        }
    }

    //endregion
*/
}
