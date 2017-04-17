package dtuqac.runtimerapp;

import android.content.Context;

import java.util.List;

import dtuqac.runtimerapp.SpeedRunEntity;

/**
 * Created by François on 2017-03-14.
 */

class ActiveSpeedrun {

    private SpeedRunEntity Run;

    private static final ActiveSpeedrun ourInstance = new ActiveSpeedrun();

    static ActiveSpeedrun getInstance() {
        return ourInstance;
    }

    private ActiveSpeedrun() {
    }

    public void SetCurrentSpeedrun(SpeedRunEntity _Run)
    {
        Run = _Run;
    }

    public SpeedRunEntity GetActiveSpeedrun()
    {
        return Run;
    }

    public boolean IsInitialized()
    {
        if (Run != null)
        {
            return true;
        }
        return false;
    }

    //Retourne l'ID de l'attempt qui est le personnal best
    public int GetPersonnalBestID()
    {
        for (Attempt a : Run.getAttemptHistory())
        {
            if (a.getBestAttempt())
            {
                return a.getId();
            }
        }
        return 0;
    }

    public String GetGameName()
    {
        return Run.getGameName();
    }

    public String GetCategoryName()
    {
        return Run.getCategoryName();
    }

    public List<Split> GetSplitsByAttemptID(int _AttemptID)
    {
        List<Split> SplitsList;

        for (Attempt a : Run.getAttemptHistory())
        {
            if (a.getId() == _AttemptID)
            {
                return a.getSplits();
            }
        }
        return null;
    }

    public void AddSplitDefinition(String _SplitName, CustomTime _SplitTime)
    {
       // Run.addSplitDefinition();
    }


    public void AddAttempt(Attempt _NewAttempt)
    {
        if (_NewAttempt.getBestAttempt())
        {
            //le new attempt est le personnal best, met les autre attempts a false
            for (Attempt a : Run.getAttemptHistory())
            {
                a.setBestAttempt(false);
            }
        }

        Run.addAttempt(_NewAttempt);
    }

    public void SaveInstance(Context _ctx){
        SGBD db = new SGBD(_ctx);
        db.SaveInstance(Run);
    }
}
