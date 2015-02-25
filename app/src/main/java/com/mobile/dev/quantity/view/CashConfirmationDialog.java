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
public class CashConfirmationDialog extends DialogFragment {

    // Use this instance of the interface to deliver action events
    CashConfirmationListener mListener;

    //variables to bundle
    private static final String ARG_TOTAL = "total";


    public CashConfirmationDialog(){}


    public static CashConfirmationDialog newInstance(String total) {
        CashConfirmationDialog dialog = new CashConfirmationDialog();
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
        View dialogRootView = layoutInflater.inflate(R.layout.layout_cash_dialog, null);
        builder.setView(dialogRootView);

        //set references to fragment dialog ui elements
        final EditText editTextCantidad = (EditText)dialogRootView.findViewById(R.id.editTextCantidad);
        final TextView textViewTotal = (TextView) dialogRootView.findViewById(R.id.textViewTotal);
        final TextView textViewCambio = (TextView) dialogRootView.findViewById(R.id.textViewCambio);

        //get argument value passed to this instance
        if(getArguments().containsKey(ARG_TOTAL))
            textViewTotal.setText("TOTAL : $"+getArguments().get(ARG_TOTAL).toString());

        //set text change listener to show on textview
        editTextCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //check if input is not null
                if(s.length()>0) {
                    //parsing string values and make subtraction
                    Double total,
                            payment = null,
                            change;
                    try {
                        payment = Double.valueOf(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        QuantityDictionay.debugLog("Exception thrown parsing payment to double");
                    }
                    total = Double.valueOf(getArguments().get(ARG_TOTAL).toString());
                    change = payment - total;
                    //set result to view
                    textViewCambio.setText("CAMBIO: $" + String.valueOf(change));
                }else{
                    textViewCambio.setText("CAMBIO:");
                }
            }
        });

        //configure dialog elements
        builder.setTitle(R.string.cash_dialog_title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onCashDialogPositiveClick(editTextCantidad.getText().toString());
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
            mListener = (CashConfirmationListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface CashConfirmationListener{
        public void onCashDialogPositiveClick(String message);
    }
}
