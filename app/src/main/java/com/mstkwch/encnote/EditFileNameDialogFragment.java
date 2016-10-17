package com.mstkwch.encnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by m.wati on 2016/09/14.
 */
public class EditFileNameDialogFragment extends DialogFragment {

    public interface EditFileNameDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    EditFileNameDialogListener listener;
    EditText editFilename;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditFileNameDialogFragment thisDialog = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setMessage("File Name?");

        View vw = inflater.inflate(R.layout.dialog_edit_filename, null);

        //ここでvw.findViewByIdが使える
        editFilename = (EditText) vw.findViewById(R.id.edtFilename);
        builder.setView(vw);

        // Add action buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //String theFileName =
                listener.onDialogPositiveClick(thisDialog);
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(thisDialog);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(Constants.LOG_TAG, "EditFileNameDialogFragment.onAttach start.");
        try {
            listener = (EditFileNameDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement EditFileNameDialogListener");
        }

    }

    public String getFilename() {
        return this.editFilename.getText().toString();
    }

}
