package dtuqac.runtimerapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class TimerActivity extends AppCompatActivity {

    private TimerClass MonTimer;
    private int CurrentSplitIndex = 0;
    private Attempt CurrentAttempt;
    private String DernierTempsPebble;

    private CustomTime LastSplitTime;

    private static final UUID WATCHAPP_UUID = UUID.fromString("6456a937-1e6d-40cf-a871-6545ea853727");

    private static final int
            KEY_BUTTON = 0,
            MESSAGE_TIME = 43,
            MESSAGE_MENU = 44,
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MonTimer = new TimerClass();
        DernierTempsPebble = "";
        LastSplitTime = new CustomTime(0,0,0,0);

        TextView txt = (TextView)findViewById(R.id.txtListViewVide);
        if (ActiveSpeedrun.getInstance().IsInitialized())
        {
            //Set le title
            getSupportActionBar().setTitle(ActiveSpeedrun.getInstance().GetGameName() + " - " + ActiveSpeedrun.getInstance().GetCategoryName());
            if (ActiveSpeedrun.getInstance().GetSplitDefinition().size() > 0) {
                txt.setVisibility(View.INVISIBLE);
                LoadSplits();
            }
            else {
                txt.setVisibility(View.VISIBLE);
                txt.setText("Aucun split dans cette SpeedRun.");
            }
        }
        else{
            //Montrer qu'il n'y a aucun split à charger
            txt.setVisibility(View.VISIBLE);
            txt.setText("Aucune SpeedRun sélectionnée. Choisir une SpeedRun dans Gérer.");
        }



        final ListView lv = (ListView) findViewById(R.id.Liste_Splits);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Reset le background de tous les éléments
                for (int i=lv.getFirstVisiblePosition(); i<=lv.getLastVisiblePosition();i++)
                {
                    parent.getChildAt(i).setBackgroundResource(R.color.light_grey);
                }

                if (position >= lv.getCount()) // Si on est au dernier split
                {
                    FinishRun();
                    return;
                }

                if (position != -1) //Position est setté à -1 quand on reset
                {
                    parent.getChildAt(position).setBackgroundColor(Color.GRAY);
                }

                //À retenir: L'objet retourné est un SplitDefinition
                //Object temp = parent.getItemAtPosition(position);

            }
        };
        lv.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                if(ActiveSpeedrun.getInstance().GetNumberOfAddedAttemps()>0)
                {
                    PopupMessage popupMaker = new PopupMessage(TimerActivity.this);
                    popupMaker.SauvegarderAttempt("Un essai a été enregistré: voulez-vous l'enregistrer?");
                    //finish() dans le popup
                }
                else{
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void TestButtonClick(View view)
    {
        ActiveSpeedrun.getInstance().SaveInstance(this);
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
                    lv.performItemClick(view,CurrentSplitIndex,1);

                    //Get l'ID du dernier attempt
                    CurrentAttempt = new Attempt(ActiveSpeedrun.getInstance().GetNextAttemptId(TimerActivity.this),ActiveSpeedrun.getInstance().GetSpeedrunID(), new CustomTime(0,0,0,0), new CustomTime(0,0,0,0), false);
                }
            }
        }
        else
        {
            Split();

            final ListView lv = (ListView) findViewById(R.id.Liste_Splits);

            if (CurrentSplitIndex + 3  >= lv.getLastVisiblePosition())
            {
                lv.smoothScrollToPosition(CurrentSplitIndex + 3);
            }

            if (CurrentSplitIndex <= lv.getCount()) //Clique sur le prochain split pour l'highlight
            {
                CurrentSplitIndex++;
                lv.performItemClick(view,CurrentSplitIndex,1);
            }

            if(CurrentSplitIndex < lv.getCount()){
                EnvoyerMessagePebble();
            }

        }
        EnvoyerSplitPebble();
    }

    private void Split()
    {
        //Report le Split Time dans l'attempt
        CustomTime SegmentTime; //TODO: Gestion des segments (IsBestSegment, Sauver le segment time, etc)
        CustomTime SplitTime = new CustomTime((int)MonTimer.GetHeures(), (int)MonTimer.GetMinutes() % 60, (int)MonTimer.GetSecondes() % 60, (int)MonTimer.GetMiliseconds() % 100);
        Split MonSplit = new Split(CurrentSplitIndex,CurrentAttempt.getId(),CurrentAttempt.getId(),SplitTime,SplitTime,false); //TODO: fixer les ID des splits
        
        //CustomTime SplitTime = new CustomTime((int)MonTimer.GetHeures(), (int)MonTimer.GetMinutes() % 60, (int)MonTimer.GetSecondes() % 60, (int)MonTimer.GetMiliseconds() % 100);
        //CustomTime SegmentTime = new CustomTime(SplitTime.soustraire(LastSplitTime).ToMiliseconds());
        //Split MonSplit = new Split(-1,CurrentAttempt.getId(),ActiveSpeedrun.getInstance().GetSplitDefinition().get(CurrentSplitIndex).getId(),SegmentTime,SplitTime,false); //TODO: fixer les ID des splits

        //Split(int id, int idAttempt, int idSplitDefinition, CustomTime duration, CustomTime splitTime, Boolean isBestSegment)

        CurrentAttempt.addSplit(MonSplit);

        //Set le Split Time sur l'item du list view
        List<Split> SplitsListe = new LinkedList<Split>();
        SplitsListe.addAll(CurrentAttempt.getSplits());

        //Récupère la liste des splits time
        int PBID = ActiveSpeedrun.getInstance().GetPersonnalBestID();
        List<Split> PBSplits = ActiveSpeedrun.getInstance().GetSplitsByAttemptID(PBID);

        for (int i = CurrentSplitIndex + 1; i < PBSplits.size() ;i++)
        {
            SplitsListe.add(PBSplits.get(i));
        }

        ListView lv = (ListView) findViewById(R.id.Liste_Splits);
        //Refresh la liste
        ((TimerSplit_Adapter) lv.getAdapter()).refreshSplits(SplitsListe);

        LastSplitTime = SplitTime;
    }

    private void FinishRun()
    {
        MonTimer.StopTimer();

        //Check si l'attempt est un PB
        int PBID = ActiveSpeedrun.getInstance().GetPersonnalBestID();
        List<Split> PBSplits = ActiveSpeedrun.getInstance().GetSplitsByAttemptID(PBID);
        CustomTime PBLastSplit = PBSplits.get(PBSplits.size() - 1).getSplitTime();

        if (PBLastSplit.IsGreaterThan(CurrentAttempt.getTimeEnded()))
        {
            CurrentAttempt.setBestAttempt(true);
        }

        CurrentAttempt.setTimeEnded(new CustomTime((int)MonTimer.GetHeures(), (int)MonTimer.GetMinutes() % 60, (int)MonTimer.GetSecondes() % 60, (int)MonTimer.GetMiliseconds() % 100));

        ActiveSpeedrun.getInstance().AddAttempt(CurrentAttempt);

        Toast.makeText(getBaseContext(),"Fini" ,Toast.LENGTH_LONG).show();
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

        //TODO envoyer le nom du premier split // men rappel pu lol

        final ListView lv = (ListView) findViewById(R.id.Liste_Splits);
        //Reset le background de tous les éléments
        for (int i=0; i<lv.getCount();i++)
        {
            lv.getChildAt(i).setBackgroundResource(R.color.light_grey);
        }

        CurrentSplitIndex = 0;
        LastSplitTime = new CustomTime(0,0,0,0);
        EnvoyerMessagePebble();
        EnvoyerSplitPebble();
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

                    if(!DernierTempsPebble.equals(MainTimer)){
                        DernierTempsPebble = MainTimer;
                        EnvoyerMessagePebble();
                    }
                }
                handler.postDelayed(this, 10);
            }
        });

    }

    private void EnvoyerMessagePebble() {
        if(PebbleKit.isWatchConnected(getBaseContext())) {
            int secondes = (int)MonTimer.GetSecondes()% 60 - LastSplitTime.getSecondes();
            int minutes = (int)MonTimer.GetMinutes()% 60 - LastSplitTime.getMinutes();
            int heures = (int)MonTimer.GetHeures() - LastSplitTime.getHeures();

            if(secondes < 0){
                minutes--;
                secondes = 60 + secondes;
            }
            if(minutes<0)
            {
                heures--;
                minutes = 60 - minutes;
            }

            String s = "Split: " + String.format("%02d:%02d:%02d", heures, minutes, secondes) + "\n" +
                    "Total: "+String.format("%02d:%02d:%02d", MonTimer.GetHeures(), MonTimer.GetMinutes() % 60, MonTimer.GetSecondes() % 60);

            //Communiquer le temps à la pebble
            PebbleDictionary comm = new PebbleDictionary();

            comm.addString(MESSAGE_TIME, s);
            PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, comm);
        }
    }

    private void EnvoyerSplitPebble() {
        if(PebbleKit.isWatchConnected(getBaseContext())) {
            //On ajuste l'interface sur la pebble
            String txtAEnvoyer = "";

            try {
                txtAEnvoyer = ((SplitDefinition) ((ListView) findViewById(R.id.Liste_Splits)).getAdapter().getItem(CurrentSplitIndex)).getSplitName();
            }
            catch (Exception e){
                txtAEnvoyer = "Speed Run Terminée!";
            }

            if(txtAEnvoyer.length() <= 14){
                txtAEnvoyer = "\n" + txtAEnvoyer;
            }

            //Communiquer le temps à la pebble
            PebbleDictionary comm = new PebbleDictionary();

            comm.addString(MESSAGE_MENU, txtAEnvoyer);
            PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, comm);
        }
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
                                        ResetTimer(null);
                                        break;
                                    case BUTTON_SELECT:
                                        //TODO Start/Pause
                                        PauseTimer(null);
                                        break;
                                    case BUTTON_DOWN:
                                        //TODO Split
                                        StartTimer(null);
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
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //Close app on pebble
        PebbleKit.closeAppOnPebble(getBaseContext(),WATCHAPP_UUID);

        // Unregister AppMessage reception
        if(appMessageReciever != null) {
            unregisterReceiver(appMessageReciever);
            appMessageReciever = null;
        }

        MonTimer.PauseTimer();
    }

}
