package dtuqac.runtimerapp;

import android.os.Handler;
import android.os.SystemClock;
import java.util.concurrent.TimeUnit;

/**
 * Created by Francois on 2017-03-01.
 */

public final class TimerClass
{
    private long StartTime = 0;
    private long CurrentTime = 0;
    private long RealElapsedTime = 0;
    private boolean Running;

    private long Miliseconds = 0;
    private long Secondes = 0;
    private long Minutes = 0;
    private long Heures = 0;

    public void StartTimer()
    {
        Running = true;
        RunTimer();
    }

    public void PauseTimer()
    {
        if (Running)
        {
            Running = false;
        }
    }

    public void UnpauseTimer()
    {
        if (!Running && StartTime != 0)
        {
            //Recupere le temps qu'on a passe en pause
            long NewCurrentTime = SystemClock.elapsedRealtime();
            long PausedTime = NewCurrentTime - CurrentTime;

            //Ajoute le au temps de depart pour repartir ou on etait
            StartTime = StartTime + PausedTime;
            Running = true;
        }
    }

    public void ResetTimer()
    {
        StartTime = 0;
        Running = false;
    }

    private void RunTimer()
    {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //TODO: Mettre le code du runTimer()
            }
        });
    }


}
