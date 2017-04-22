package dtuqac.runtimerapp;

import java.util.ArrayList;
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
    private CustomTime OffSet;
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

    public void setOffSet(CustomTime offSet) {
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

    public CustomTime getOffSet() {
        return OffSet;
    }

    public List<Attempt> getAttemptHistory() {
        return AttemptHistory;
    }

    public List<SplitDefinition> getSpeedRunSplits() {
        return SpeedRunSplits;
    }
    //endregion

    public SpeedRunEntity(int id, String gameName, String categoryName, Boolean usesEmulator, CustomTime offSet) {
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
    private CustomTime TimeStarted;
    private CustomTime TimeEnded;
    private Boolean IsBestAttempt;
    private List<Split> Splits;

    //region Getter and Setter

    public void setId(int id) {
        Id = id;
    }

    public void setSpeedRunId(int speedRunId) {
        SpeedRunId = speedRunId;
    }

    public void setTimeStarted(CustomTime timeStarted) {
        TimeStarted = timeStarted;
    }

    public void setTimeEnded(CustomTime timeEnded) {
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

    public CustomTime getTimeStarted() {
        return TimeStarted;
    }

    public CustomTime getTimeEnded() {
        return TimeEnded;
    }

    public Boolean getIsBestAttempt() {
        return IsBestAttempt;
    }

    public List<Split> getSplits() {
        return Splits;
    }

    //endregion

    public Attempt(int id, int speedRunId, CustomTime timeStarted, CustomTime timeEnded, Boolean isBestAttempt) {
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
    private CustomTime Duration;
    private CustomTime SplitTime;
    private Boolean IsBestSegment;

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

    public CustomTime getDuration() {
        return Duration;
    }

    public void setDuration(CustomTime duration) {
        Duration = duration;
    }

    public CustomTime getSplitTime() {
        return SplitTime;
    }

    public void setSplitTime(CustomTime splitTime) {
        SplitTime = splitTime;
    }

    public Boolean getIsBestSegment() {
        return IsBestSegment;
    }

    public void setIsBestSegment(Boolean bestSegment) {
        IsBestSegment = bestSegment;
    }

    //endregion

    public Split(int id, int idAttempt, int idSplitDefinition, CustomTime duration, CustomTime splitTime, Boolean isBestSegment) {
        Id = id;
        IdAttempt = idAttempt;
        IdSplitDefinition = idSplitDefinition;
        Duration = duration;
        SplitTime = splitTime;
        IsBestSegment = isBestSegment;
    }
}

/*********************************************************************
 Classe SplitDefinition pour garder une définition basique des split/segments
 *********************************************************************/

class SplitDefinition{
    private int Id;
    private int SpeedRunId;
    private String SplitName;

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

    //endregion

    public SplitDefinition(int id, int speedRunId, String splitName) {
        Id = id;
        SpeedRunId = speedRunId;
        SplitName = splitName;
    }
}
