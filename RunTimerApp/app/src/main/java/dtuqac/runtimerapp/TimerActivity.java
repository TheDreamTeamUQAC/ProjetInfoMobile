package dtuqac.runtimerapp;

import android.app.LauncherActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

public class TimerActivity extends AppCompatActivity {

    private TimerClass MonTimer;
    private int CurrentSplitIndex = 0;

    private static final UUID WATCHAPP_UUID = UUID.fromString("6456a937-1e6d-40cf-a871-6545ea853727");

    private static final int
            KEY_BUTTON = 0,
            MESSAGE_TIME = 43,
            BUTTON_UP = 0,
            BUTTON_SELECT = 1,
            BUTTON_DOWN = 2;

    private Handler handler = new Handler();
    private PebbleDataReceiver appMessageReciever;


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

        final ListView lv = (ListView) findViewById(R.id.Liste_Splits);
        /*
        AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),"Salut",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        lv.setOnItemSelectedListener(selectedListener);*/

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),"Click item : " + position ,Toast.LENGTH_SHORT).show();
                //HighlightListItem(position);

                for (int i=0; i<lv.getCount();i++)
                {
                    parent.getChildAt(i).setBackgroundResource(R.color.light_grey);
                }

                parent.getChildAt(position).setBackgroundColor(Color.BLUE);

               // lv.setItemChecked(position,true);

                //TODO: À retenir: L'objet retourné est un SplitDefinition
                //Object temp = parent.getItemAtPosition(position);

            }
        };
        lv.setOnItemClickListener(itemClickListener);
    }

    public void TestButtonClick(View view)
    {

        ListView lv = (ListView) findViewById(R.id.Liste_Splits);
        lv.performItemClick(view,CurrentSplitIndex++,1);
        //lv.setSelection(CurrentSplitIndex);

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
    }

    private void HighlightListItem(int position)
    {
        ListView lv = (ListView) findViewById(R.id.Liste_Splits);
        TimerSplit_Adapter timerSplit_adapter = (TimerSplit_Adapter)lv.getAdapter();
        timerSplit_adapter.SetSelectecItem(position);
        //lv.setAdapter(timerSplit_adapter);
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

                //Si il y a des splits, highlight le current split
                if (ActiveSpeedrun.getInstance().IsInitialized())
                {
                    ListView lv = (ListView) findViewById(R.id.Liste_Splits);

                }
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

        //TODO envoyer le nom du premier split
        EnvoyerMessagePebble("Reset time");
    }

    private void PollTimer()
    {
        final TextView MainTimeView = (TextView)findViewById(R.id.main_timer_label);
        final TextView SmallTimeView = (TextView)findViewById(R.id.small_timer_label);

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

                    EnvoyerMessagePebble("Etape 1 \n" + MainTimer + SmallTimer);
                }
                handler.postDelayed(this, 10);
            }
        });

    }

    private void EnvoyerMessagePebble(String s) {
        //Communiquer le temps à la pebble
        PebbleDictionary comm = new PebbleDictionary();

        comm.addString(MESSAGE_TIME, s);
        PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, comm);
    }

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
                                        //TODO Rien...
                                        //whichButtonView.setText("UP");
                                        //fctCommunes.ExecVibration(getApplicationContext(),500);
                                        ResetTimer(null);
                                        Toast.makeText(getBaseContext(),"UP",Toast.LENGTH_LONG).show();
                                        break;
                                    case BUTTON_SELECT:
                                        //TODO Start/Pause
                                        //whichButtonView.setText("SELECT");
                                        //fctCommunes.ExecVibration(getApplicationContext(),500);
                                        PauseTimer(null);
                                        Toast.makeText(getBaseContext(),"SELECT",Toast.LENGTH_LONG).show();
                                        break;
                                    case BUTTON_DOWN:
                                        //TODO Split
                                        //whichButtonView.setText("DOWN");
                                        //fctCommunes.ExecVibration(getApplicationContext(),500);
                                        StartTimer(null);
                                        Toast.makeText(getBaseContext(),"DOWN",Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        Toast.makeText(getBaseContext(), "Unknown button: " + button, Toast.LENGTH_SHORT).show();
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
        if(PebbleKit.isWatchConnected(getBaseContext())) {
            PebbleKit.startAppOnPebble(getBaseContext(), WATCHAPP_UUID);

            EnvoyerMessagePebble("Instructions bouttons:\n" +
                    "Haut: Reset\n"+
                    "Milieu: Pause\n" +
                    "Bas: Start/Split");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Close app on pebble
        PebbleKit.closeAppOnPebble(getBaseContext(),WATCHAPP_UUID);

        // Unregister AppMessage reception
        if(appMessageReciever != null) {
            unregisterReceiver(appMessageReciever);
            appMessageReciever = null;
        }
    }

}
