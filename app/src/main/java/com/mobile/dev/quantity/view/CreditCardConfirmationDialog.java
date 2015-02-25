package com.mobile.dev.quantity.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.mobile.dev.quantity.R;


/**
 * This class handles the dialog for creditcard confirmation
 * Created by Luis.Cariño on 18/02/2015.
 */
public class CreditCardConfirmationDialog extends DialogFragment{


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //get layout inflater
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View dialogRootView = layoutInflater.inflate(R.layout.layout_credit_card_dialog, null);
        builder.setView(dialogRootView);


        //configure dialog elements
        builder.setTitle(R.string.dialog_card_title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();

    }

}
