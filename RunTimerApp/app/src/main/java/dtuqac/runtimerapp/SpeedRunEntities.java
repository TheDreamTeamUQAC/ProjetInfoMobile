package dtuqac.runtimerapp;

import java.sql.Time;
import java.util.Date;
import java.util.List;


/**
 * Created by Tommy Duperré on 2017-03-04.
 */


/*********************************************************************
Classe SpeedRunEntity
 *********************************************************************/
class SpeedRunEntity {
    public int Id;
    public String GameName;
    public String CategoryName;
    public Boolean UsesEmulator;
    public Date OffSet;
    public List<Attempt> AttemptHistory;
    public List<SplitDefinition> SpeedRunSplits;

    public SpeedRunEntity(int id, String gameName, String categoryName, Boolean usesEmulator, Date offSet) {
        Id = id;
        GameName = gameName;
        CategoryName = categoryName;
        UsesEmulator = usesEmulator;
        OffSet = offSet;
    }
}

/*********************************************************************
 Classe Attempt lorsqu'on fait exécute une speedrun afind e sauvegarder les données
 *********************************************************************/

class Attempt{
    public int Id;
    public int SpeedRunId;
    public Date TimeStarted;
    public Date TimeEnded;
    public Boolean IsBestAttempt;
    public List<Split> Splits;

    public Attempt(int id, int speedRunId, Date timeStarted, Date timeEnded, Boolean isBestAttempt) {
        Id = id;
        SpeedRunId = speedRunId;
        TimeStarted = timeStarted;
        TimeEnded = timeEnded;
        IsBestAttempt = isBestAttempt;
    }
}

/*********************************************************************
 Classe Split afin de garder tous les temps des segments séparément lors d'un Attempt
 *********************************************************************/

class Split{
    public int Id;
    public int IdAttempt;
    public int IdSplitDefinition;
    public Date Duration;
    public Date SplitTime;

    public Split(int id, int idAttempt, int idSplitDefinition, Date duration, Date splitTime) {
        Id = id;
        IdAttempt = idAttempt;
        IdSplitDefinition = idSplitDefinition;
        Duration = duration;
        SplitTime = splitTime;
    }
}

/*********************************************************************
 Classe SplitDefinition pour garder une définition basique des split/segments
 *********************************************************************/

class SplitDefinition{
    public int Id;
    public int SpeedRunId;
    public String SplitName;
    public boolean IsBestSegment;

    public SplitDefinition(int id, int speedRunId, String splitName, boolean isBestSegment) {
        Id = id;
        SpeedRunId = speedRunId;
        SplitName = splitName;
        IsBestSegment = isBestSegment;
    }
}
