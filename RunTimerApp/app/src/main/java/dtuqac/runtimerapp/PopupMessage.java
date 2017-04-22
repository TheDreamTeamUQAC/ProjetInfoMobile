package dtuqac.runtimerapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.CheckBox;
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

    public void AjouterSpeedRunPopup(String _nomFenetre){
        AlertDialog.Builder builder = new AlertDialog.Builder(popupActivity);
        builder.setTitle(_nomFenetre);
        builder.setIcon(android.R.drawable.ic_input_add);
        builder.setView(R.layout.dialog_addspeedrun);

        // Set up the buttons
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SGBD db = new SGBD(popupActivity);

                SpeedRunEntity newSpeedRun = new SpeedRunEntity(
                        0,
                        ((EditText)((AlertDialog)dialog).findViewById(R.id.txtNomSpeedRun)).getText().toString(),
                        ((EditText)((AlertDialog)dialog).findViewById(R.id.txtCategorie)).getText().toString(),
                        ((CheckBox)((AlertDialog)dialog).findViewById(R.id.chkEmulator)).isChecked(),
                        new CustomTime("00:00:00.00")
                );
                db.addSpeedRun(newSpeedRun);


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
