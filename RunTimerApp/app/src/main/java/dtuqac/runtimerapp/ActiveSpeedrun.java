package dtuqac.runtimerapp;

import android.content.Context;

import java.util.List;

import dtuqac.runtimerapp.SpeedRunEntity;

/**
 * Created by François on 2017-03-14.
 */

class ActiveSpeedrun {

    private SpeedRunEntity Run;
    private int AddedAttempts;

    private static final ActiveSpeedrun ourInstance = new ActiveSpeedrun();

    static ActiveSpeedrun getInstance() {
        return ourInstance;
    }

    private ActiveSpeedrun() {
    }

    public void SetCurrentSpeedrun(SpeedRunEntity _Run)
    {
        Run = _Run; AddedAttempts =0;
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
            if (a.getIsBestAttempt())
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

    public int GetSpeedrunID()
    {
        return Run.getId();
    }

    public List<SplitDefinition> GetSplitDefinition()
    {
        return Run.getSpeedRunSplits();
    }

    public void AddSplitDefinition(SplitDefinition NewSplitDef)
    {
        Run.addSplitDefinition(NewSplitDef);

        //TODO: This is really bad
        //Pour sauver du temps, on considere qu'on ajoute juste a la fin. Donc on ajoute un extra split bidon a tous les attempts
        for (Attempt a : Run.getAttemptHistory())
        {
            Split LastSplit = a.getSplits().get(a.getSplits().size() - 1);
            Split temp = new Split(LastSplit.getId() + 1, a.getId(), NewSplitDef.getId(), LastSplit.getDuration(), LastSplit.getSplitTime(), false);
            a.addSplit(temp);
        }
    }

    public void AddAttempt(Attempt _NewAttempt)
    {
        if (_NewAttempt.getIsBestAttempt())
        {
            //le new attempt est le personnal best, met les autre attempts a false
            for (Attempt a : Run.getAttemptHistory())
            {
                a.setBestAttempt(false);
            }
        }

        Run.addAttempt(_NewAttempt);
        AddedAttempts++;
    }

    public int GetNumberOfAddedAttemps(){
        return AddedAttempts;
    }

    public void UpdateSplitDefinition(int id, String NewName)
    {
        Run.getSpeedRunSplits().get(id).setSplitName(NewName);
    }

    public void UpdateSplitTime(int id, CustomTime NewTime)
    {
        for (Attempt a : Run.getAttemptHistory())
        {
            if (a.getIsBestAttempt())
            {
                a.getSplits().get(id).setSplitTime(NewTime);
            }
        }
    }

    public void SaveInstance(Context _ctx){
        SGBD db = new SGBD(_ctx);
        db.SaveInstance(Run);
    }

    public int GetNextAttemptId(Context _ctx){
        SGBD db = new SGBD(_ctx);
        return db.getNextAttemptId() + AddedAttempts;
    }
}
