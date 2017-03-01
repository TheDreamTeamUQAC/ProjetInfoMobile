package dtuqac.runtimerapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;

public class Timer extends AppCompatActivity {

    private int MonTimer = 0;
    private  boolean running;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        runTimer();
    }

    public void StartTimer(View view)
    {
        running = true;
    }

    public void PauseTimer(View view)
    {
        running = false;
    }

    public void ResetTimer(View view)
    {
        running = false;
        MonTimer = 0;
    }

    private void runTimer() {
        final TextView timeView = (TextView)findViewById(R.id.main_timer_text);
        final TextView smalltimeView = (TextView)findViewById(R.id.small_timer_text);
        //java text view associated with the xml one
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int onehundredth = MonTimer;
                int tendth = MonTimer / 10;

                String smalltime = String.format(".%d%d",tendth, onehundredth);
                smalltimeView.setText(smalltime);
/*
                int hours = MonTimer / 3600;
                int minutes = (MonTimer % 3600) / 60;
                int secs = MonTimer % 60;
                String time = String.format("%d:%02d:%02d",
                        hours, minutes, secs, ".", tendth, onehundredth);
                timeView.setText(time);*/
                if (running) {
                    MonTimer++;
                }
                handler.postDelayed(this, 10);
            }
        });
    }




}
