package dtuqac.runtimerapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by Tommy Duperré on 2017-03-02.
 */

public class PopupMessage {
    static Context popupContext;

    PopupMessage(Context ctx){
        popupContext = ctx;
    }

    private String m_Text;

    //Popup texte
    public void AskString(String _nomFenetre){
        AlertDialog.Builder builder = new AlertDialog.Builder(popupContext);
        builder.setTitle(_nomFenetre);

        // Set up the input
        final EditText input = new EditText(popupContext);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
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
