package dtuqac.runtimerapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by Tommy Duperré on 2017-03-02.
 */

public class PopupMessage {
    private Activity popupActivity;

    PopupMessage(Activity act){
        popupActivity = act;
    }

    //Popup texte
    public void AjouterFichierPopup(String _nomFenetre){
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
                GestionFichier fileWorker = new GestionFichier(popupActivity);
                if (!fileWorker.FichierExiste(input.getText().toString())){
                    //Écrire un fichier vide
                    fileWorker.EcrireFichier(input.getText().toString(),"");
                }
                else {
                    //Supprimer le fichier existant
                    fileWorker.SupprimerFichier(input.getText().toString());
                }
                ((LoadActivity)popupActivity).ChargerFichiersListView();
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

    public void ConfirmerSuppressionPopup(String _nomFenetre, final String _nomFichier){
        AlertDialog.Builder builder = new AlertDialog.Builder(popupActivity);
        builder.setTitle(_nomFenetre);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        // Set up the buttons
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GestionFichier fileWorker = new GestionFichier(popupActivity);
                fileWorker.SupprimerFichier(_nomFichier);
                ((LoadActivity)popupActivity).ChargerFichiersListView();
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
