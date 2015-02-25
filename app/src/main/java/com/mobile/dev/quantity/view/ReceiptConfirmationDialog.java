package com.mobile.dev.quantity.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mobile.dev.quantity.R;
import com.mobile.dev.quantity.util.QuantityDictionay;

/**
 * Created by Luis.Cariño on 17/02/2015.
 */
public class ReceiptConfirmationDialog extends DialogFragment {

    // Use this instance of the interface to deliver action events
    ReceiptConfirmationListener mListener;

    //variables to bundle
    private static final String ARG_TOTAL = "total";


    public ReceiptConfirmationDialog(){}


    public static ReceiptConfirmationDialog newInstance(String total) {
        ReceiptConfirmationDialog dialog = new ReceiptConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TOTAL, total);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //get layout inflater
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View dialogRootView = layoutInflater.inflate(R.layout.layout_receipt_dialog, null);
        builder.setView(dialogRootView);

        //set references to fragment dialog ui elements
        final EditText editTextEmail = (EditText)dialogRootView.findViewById(R.id.editTextEmail);


        //configure dialog elements
        builder.setTitle(R.string.cash_dialog_title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onReceiptDialogPositiveClick(editTextEmail.getText().toString());
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ReceiptConfirmationListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ReceiptConfirmationListener");
        }
    }

    public interface ReceiptConfirmationListener{
        public void onReceiptDialogPositiveClick(String email);
    }
}
