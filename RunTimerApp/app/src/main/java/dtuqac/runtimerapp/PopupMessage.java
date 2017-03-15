package dtuqac.runtimerapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by Tommy Duperré on 2017-03-02.
 */

public class PopupMessage {
    private Activity popupActivity;

    PopupMessage(Activity act){
        popupActivity = act;
    }

    public void ConfirmerSuppressionPopup(String _nomFenetre, final int _idSpeedRun){
        ConfirmerSuppressionPopup(_nomFenetre,_idSpeedRun,false);
    }

    public void ConfirmerSuppressionPopup(String _nomFenetre, final int _idSpeedRun, final Boolean _recreer){
        AlertDialog.Builder builder = new AlertDialog.Builder(popupActivity);
        builder.setTitle(_nomFenetre);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        // Set up the buttons
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SGBD db = new SGBD(popupActivity);
                db.deleteSpeedRun(_idSpeedRun);
                if(_recreer)
                {
                    //fileWorker.EcrireFichier(_nomFichier);
                }
                ((LoadActivity)popupActivity).ChargerEntreesListView();
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
