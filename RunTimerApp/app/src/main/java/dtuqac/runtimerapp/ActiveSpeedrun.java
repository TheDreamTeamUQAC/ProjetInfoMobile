package dtuqac.runtimerapp;

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


}
