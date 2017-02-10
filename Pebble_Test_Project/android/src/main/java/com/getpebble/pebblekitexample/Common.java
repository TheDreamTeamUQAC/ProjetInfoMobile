package com.getpebble.pebblekitexample;

import android.content.Context;
import android.os.Vibrator;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Tommy Duperré on 2017-02-06.
 */

public final class Common {
    //Effectuer la vibration du téléphone pour x milisecondes
    private Common(){}

    public static void ExecVibration(Context ctx, int _miliseconds)
    {
        Vibrator v = (Vibrator) ctx.getSystemService(VIBRATOR_SERVICE);
        // Vibrate for x milliseconds
        v.vibrate(_miliseconds);
    }


}
