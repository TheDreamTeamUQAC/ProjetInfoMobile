package dtuqac.runtimerapp;

import android.os.SystemClock;
import java.util.concurrent.TimeUnit;
import dtuqac.runtimerapp.ActiveSpeedrun;

/**
 * Created by Francois on 2017-03-01.
 */

public final class TimerClass
{
    //TODO: SplitsFile, CurrentComparison, CurrentSplit, etc.

    private long StartTime = 0;
    private long CurrentTime = 0;
    private long RealElapsedTime = 0;
    private boolean Running;

    private long Miliseconds = 0;
    private long Secondes = 0;
    private long Minutes = 0;
    private long Heures = 0;

    public long GetMiliseconds(){ return Miliseconds; }
    public long GetSecondes(){ return Secondes; }
    public long GetMinutes(){ return Minutes; }
    public long GetHeures(){ return Heures; }
    public boolean IsRunning(){ return Running; }

    public void StartTimer()
    {
        Running = true;
        StartTime = SystemClock.elapsedRealtime();
    }

    public void PauseTimer()
    {
        if (Running) //On Pause
        {
            Running = false;
        }
        else
        {
            UnpauseTimer();
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
        Running = false;
        StartTime = 0;
        Miliseconds = 0;
        Secondes = 0;
        Minutes = 0;
        Heures = 0;
    }

    public void ReportTime()
    {
        if (Running)
        {
            CurrentTime = SystemClock.elapsedRealtime();
            RealElapsedTime = CurrentTime - StartTime;

            Miliseconds = RealElapsedTime;
            Secondes = TimeUnit.MILLISECONDS.toSeconds(RealElapsedTime);
            Minutes = TimeUnit.SECONDS.toMinutes((Secondes));
            Heures = TimeUnit.MINUTES.toHours(Minutes);
        }
    }
}
