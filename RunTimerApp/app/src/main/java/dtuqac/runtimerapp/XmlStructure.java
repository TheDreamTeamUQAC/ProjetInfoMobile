package dtuqac.runtimerapp;

import java.util.Date;
import java.util.List;


/**
 * Created by Tommy Duperré on 2017-03-04.
 */

public class XmlStructure {
    public String GameName;
    public String CategoryName;
    public Boolean UsesEmulator;
    public List<Attempt> AttemptHistory;


    public XmlStructure(String gameName, String categoryName, Boolean usesEmulator, List<Attempt> attemptHistory) {
        GameName = gameName;
        CategoryName = categoryName;
        UsesEmulator = usesEmulator;
        AttemptHistory = attemptHistory;
    }
}

class Attempt{
    public int Id;
    public Date TimeStarted;
    public Date TimeEnded;
    //private Date ElapsedTime;
    //private int AttempCount
    public List<Segment> Segments;

    public Attempt(int id, Date timeStarted, Date timeEnded, List<Segment> segments) {
        Id = id;
        TimeStarted = timeStarted;
        TimeEnded = timeEnded;
        Segments = segments;
    }
}

class Segment{
    public String Name;
    public List<SplitTime> SegmentHistory;

    public Segment(String name, List<SplitTime> segmentHistory) {
        Name = name;
        SegmentHistory = segmentHistory;
    }
}

class SplitTime{
    public SplitTime(Date timeElapsed) {
        TimeElapsed = timeElapsed;
    }

    public Date TimeElapsed;
}
