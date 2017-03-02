package dtuqac.runtimerapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.SystemClock;
import java.util.concurrent.TimeUnit;


public class TimerActivity extends AppCompatActivity {

    private long StartTime = 0;
    private long CurrentTime = 0;
    private long RealElapsedTime = 0;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        runTimer();
    }

    public void StartTimer(View view)
    {
        if (!running)
        {
            running = true;
            StartTime = SystemClock.elapsedRealtime();

            final Button StartButton = (Button)findViewById(R.id.start_button);
            StartButton.setText("Split");
        }
    }

    public void PauseTimer(View view)
    {
        if (running)
        {
            running = false;
        }
        else if (!running && StartTime != 0)
        {
            long NewCurrentTime = SystemClock.elapsedRealtime();
            long PausedTime = NewCurrentTime - CurrentTime;

            StartTime = StartTime + PausedTime;
            running = true;
        }
    }

    public void ResetTimer(View view)
    {
        running = false;
        StartTime = 0;

        final Button StartButton = (Button)findViewById(R.id.start_button);
        StartButton.setText("Start");
        final TextView MainTimeView = (TextView)findViewById(R.id.main_timer_label);
        final TextView SmallTimeView = (TextView)findViewById(R.id.small_timer_label);
        MainTimeView.setText("0");
        SmallTimeView.setText(".00");
    }

    private void runTimer()
    {
        final TextView MainTimeView = (TextView)findViewById(R.id.main_timer_label);
        final TextView SmallTimeView = (TextView)findViewById(R.id.small_timer_label);
        //java text view associated with the xml one
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                if (running)
                {
                    CurrentTime = SystemClock.elapsedRealtime();
                    RealElapsedTime = CurrentTime - StartTime;

                    long Mils = RealElapsedTime;
                    long Secondes = TimeUnit.MILLISECONDS.toSeconds(RealElapsedTime);
                    long Minutes = TimeUnit.SECONDS.toMinutes(Secondes);
                    long Heures = TimeUnit.MINUTES.toHours(Minutes);

                    String SmallTimer = String.format(".%02d", (Mils/10) % 100 );
                    String MainTimer = String.format("%d", Secondes);

                    if (Minutes > 0)
                    {
                        MainTimer = String.format("%d:%02d", Minutes % 60, Secondes % 60);
                    }
                    if (Heures > 0)
                    {
                        MainTimer = String.format("%d:%02d:%02d", Heures, Minutes % 60, Secondes % 60);
                    }

                    SmallTimeView.setText(SmallTimer);
                    MainTimeView.setText(MainTimer);
                }

                handler.postDelayed(this, 10);
            }
        });
    }

}
