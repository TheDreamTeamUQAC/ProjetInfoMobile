package dtuqac.runtimerapp;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Tommy Duperré on 2017-03-04.
 */


/*********************************************************************
Classe SpeedRunEntity
 *********************************************************************/
class SpeedRunEntity {
    private int Id;
    private String GameName;
    private String CategoryName;
    private Boolean UsesEmulator;
    private Date OffSet;
    private List<Attempt> AttemptHistory;
    private List<SplitDefinition> SpeedRunSplits;

    //region Getter and Setter

    public void setId(int id) {
        Id = id;
    }

    public void setGameName(String gameName) {
        GameName = gameName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public void setUsesEmulator(Boolean usesEmulator) {
        UsesEmulator = usesEmulator;
    }

    public void setOffSet(Date offSet) {
        OffSet = offSet;
    }

    public int getId() {
        return Id;
    }

    public String getGameName() {
        return GameName;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public Boolean getUsesEmulator() {
        return UsesEmulator;
    }

    public Date getOffSet() {
        return OffSet;
    }

    public List<Attempt> getAttemptHistory() {
        return AttemptHistory;
    }

    public List<SplitDefinition> getSpeedRunSplits() {
        return SpeedRunSplits;
    }
    //endregion

    public SpeedRunEntity(int id, String gameName, String categoryName, Boolean usesEmulator, Date offSet) {
        Id = id;
        GameName = gameName;
        CategoryName = categoryName;
        UsesEmulator = usesEmulator;
        OffSet = offSet;
        AttemptHistory= new ArrayList<>();
        SpeedRunSplits = new ArrayList<>();
    }

    public void addAttempt(Attempt attempt){
        AttemptHistory.add(attempt);
    }
    public void addSplitDefinition(SplitDefinition splitDefinition){
        SpeedRunSplits.add(splitDefinition);
    }

}

/*********************************************************************
 Classe Attempt lorsqu'on fait exécute une speedrun afind e sauvegarder les données
 *********************************************************************/

class Attempt{
    private int Id;
    private int SpeedRunId;
    private Date TimeStarted;
    private Date TimeEnded;
    private Boolean IsBestAttempt;
    private List<Split> Splits;

    //region Getter and Setter

    public void setId(int id) {
        Id = id;
    }

    public void setSpeedRunId(int speedRunId) {
        SpeedRunId = speedRunId;
    }

    public void setTimeStarted(Date timeStarted) {
        TimeStarted = timeStarted;
    }

    public void setTimeEnded(Date timeEnded) {
        TimeEnded = timeEnded;
    }

    public void setBestAttempt(Boolean bestAttempt) {
        IsBestAttempt = bestAttempt;
    }

    public int getId() {

        return Id;
    }

    public int getSpeedRunId() {
        return SpeedRunId;
    }

    public Date getTimeStarted() {
        return TimeStarted;
    }

    public Date getTimeEnded() {
        return TimeEnded;
    }

    public Boolean getBestAttempt() {
        return IsBestAttempt;
    }

    public List<Split> getSplits() {
        return Splits;
    }

    //endregion

    public Attempt(int id, int speedRunId, Date timeStarted, Date timeEnded, Boolean isBestAttempt) {
        Id = id;
        SpeedRunId = speedRunId;
        TimeStarted = timeStarted;
        TimeEnded = timeEnded;
        IsBestAttempt = isBestAttempt;
        Splits = new ArrayList<>();
    }

    public void addSplit(Split split)
    {
        Splits.add(split);
    }
}

/*********************************************************************
 Classe Split afin de garder tous les temps des segments séparément lors d'un Attempt
 *********************************************************************/

class Split{
    private int Id;
    private int IdAttempt;
    private int IdSplitDefinition;
    private Date Duration;
    private Date SplitTime;

    //region Getter and Setter

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getIdAttempt() {
        return IdAttempt;
    }

    public void setIdAttempt(int idAttempt) {
        IdAttempt = idAttempt;
    }

    public int getIdSplitDefinition() {
        return IdSplitDefinition;
    }

    public void setIdSplitDefinition(int idSplitDefinition) {
        IdSplitDefinition = idSplitDefinition;
    }

    public Date getDuration() {
        return Duration;
    }

    public void setDuration(Date duration) {
        Duration = duration;
    }

    public Date getSplitTime() {
        return SplitTime;
    }

    public void setSplitTime(Date splitTime) {
        SplitTime = splitTime;
    }

    //endregion

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
    private int Id;
    private int SpeedRunId;
    private String SplitName;
    private boolean IsBestSegment;

    //region Getter and Starter

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getSpeedRunId() {
        return SpeedRunId;
    }

    public void setSpeedRunId(int speedRunId) {
        SpeedRunId = speedRunId;
    }

    public String getSplitName() {
        return SplitName;
    }

    public void setSplitName(String splitName) {
        SplitName = splitName;
    }

    public boolean isBestSegment() {
        return IsBestSegment;
    }

    public void setBestSegment(boolean bestSegment) {
        IsBestSegment = bestSegment;
    }

    //endregion

    public SplitDefinition(int id, int speedRunId, String splitName, boolean isBestSegment) {
        Id = id;
        SpeedRunId = speedRunId;
        SplitName = splitName;
        IsBestSegment = isBestSegment;
    }
}
