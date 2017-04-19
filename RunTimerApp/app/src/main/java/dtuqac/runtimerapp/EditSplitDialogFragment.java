package dtuqac.runtimerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by François on 2017-04-19.
 */

public class EditSplitDialogFragment extends DialogFragment {

    private Bundle info;

    public interface EditSplitDialogListener
    {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    EditSplitDialogListener mListener;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EditSplitDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstancesState)
    {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();


        final View vi = inflater.inflate(R.layout.dialog_editsplit, null);
        info = getArguments();

        //Set les textboxes
        EditText txtSplitName = (EditText) vi.findViewById(R.id.txtSplitName);
        EditText txtSplitTime = (EditText) vi.findViewById(R.id.txtSplitTime);
        String test1 = info.getString("splitdef");
        String test2 = info.getString("splittime");
        txtSplitName.setText(info.getString("splitdef"));
        txtSplitTime.setText(info.getString("splittime"));

        builder.setView(vi);

        //Set les controles
        builder.setMessage("Modifier")
                .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        EditText txtSplitName = (EditText) vi.findViewById(R.id.txtSplitName);
                        EditText txtSplitTime = (EditText) vi.findViewById(R.id.txtSplitTime);
                        info.putString("splitdef", txtSplitName.getText().toString());
                        info.putString("splittime", txtSplitTime.getText().toString());

                        mListener.onDialogPositiveClick(EditSplitDialogFragment.this);
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(EditSplitDialogFragment.this);
                    }
                });

        return builder.create();
    }

}
