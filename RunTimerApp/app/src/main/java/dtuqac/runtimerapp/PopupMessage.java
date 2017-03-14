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

    //Popup texte
    public void AjouterSpeedRunPopup(String _nomFenetre){
        AlertDialog.Builder builder = new AlertDialog.Builder(popupActivity);
        builder.setTitle(_nomFenetre);

        // Set up the input
        final EditText input = new EditText(popupActivity);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);
        builder.setIcon(android.R.drawable.ic_dialog_info);

        // Set up the buttons
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SGBD db = new SGBD(popupActivity);
                if (!db.speedRunExiste(input.getText().toString())){
                    //TODO Écrire une speedrun avec les information
                    SpeedRunEntity tmp = new SpeedRunEntity(
                            0,
                            input.getText().toString(),
                            "Test",
                            false,
                            new Date()
                    );

                    db.addSpeedRun(tmp);
                }
                else {
                    //Avertir que l'entrée bd existe déjà
                    Toast.makeText(popupActivity,"La speedrun \"" + input.getText().toString() + "\" existe déjà!",Toast.LENGTH_LONG).show();
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

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

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
