package com.mobile.dev.quantity.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mobile.dev.quantity.R;
import com.mobile.dev.quantity.util.Validations;


/**
 * This class handles the dialog for creditcard confirmation
 * Created by Luis.Cariño on 18/02/2015.
 */
public class CreditCardConfirmationDialog extends DialogFragment{
    private View dialogRootView;
    private EditText editTextCardNumber;
    private EditText editTextMonth;
    private EditText editTextYear;
    private EditText editTextCVV;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //get layout inflater
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        dialogRootView = layoutInflater.inflate(R.layout.layout_credit_card_dialog, null);
        editTextCardNumber = (EditText) dialogRootView.findViewById(R.id.editTextCardNumber);
        editTextMonth = (EditText) dialogRootView.findViewById(R.id.editTextMonth);
        editTextYear = (EditText) dialogRootView.findViewById(R.id.editTextYear);
        editTextCVV = (EditText) dialogRootView.findViewById(R.id.editTextCVV);
        builder.setView(dialogRootView);


        //configure dialog elements
        builder.setTitle(R.string.dialog_card_title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isValid()) {

                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();

    }

    private boolean isValid() {
        if (Validations.validate(Validations.VALIDATION_CC_NUMBER,
                Validations.getStringNullable(editTextCardNumber.getText().toString()))
                && Validations.validate(Validations.VALIDATION_CC_NUMBER,
                Validations.getStringNullable(editTextCardNumber.getText().toString()))) {
            return true;
        }
        return false;
    }

}
