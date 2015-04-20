package com.mobile.dev.quantity.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.mobile.dev.quantity.R;
import com.mobile.dev.quantity.util.QuantityDictionay;
import com.mobile.dev.quantity.util.Validations;

/**
 * Created by Luis.Cariño on 17/02/2015.
 */
public class CashConfirmationDialog extends DialogFragment {

    // Use this instance of the interface to deliver action events
    CashConfirmationListener mListener;
    Button button;
    EditText editTextEmail;
    CheckBox chkBox;
    EditText editTextCantidad;
    boolean pago_correcto = false;

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
        editTextCantidad = (EditText)dialogRootView.findViewById(R.id.editTextCantidad);
        final TextView textViewTotal = (TextView) dialogRootView.findViewById(R.id.textViewTotal);
        final TextView textViewCambio = (TextView) dialogRootView.findViewById(R.id.textViewCambio);
        editTextEmail = (EditText)dialogRootView.findViewById(R.id.editTextEmail);
        chkBox = (CheckBox) dialogRootView.findViewById(R.id.checkBoxEmail);
        chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextEmail.setVisibility(View.VISIBLE);
                } else {
                    editTextEmail.setVisibility(View.GONE);
                }
            }
        });

        //get argument value passed to this instance
        if(getArguments().containsKey(ARG_TOTAL))
            textViewTotal.setText("TOTAL: $ " + getArguments().get(ARG_TOTAL).toString());

        textViewCambio.setTextColor(Color.RED);
        textViewCambio.setText(String.format( "FALTANTE: $ " + getArguments().get(ARG_TOTAL).toString()));

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
                if(s.length() > 0) {
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

                    if (change >= 0) {
                        textViewCambio.setTextColor(Color.BLACK);
                        textViewCambio.setText(String.format( "CAMBIO: $ %.2f", change ));
                        pago_correcto = true;
                    } else {
                        textViewCambio.setTextColor(Color.RED);
                        textViewCambio.setText(String.format( "FALTANTE: $ %.2f", Math.abs(change)));
                        pago_correcto = false;
                    }

                }else{
                    textViewCambio.setText("CAMBIO:");
                }
            }
        });

        //configure dialog elements
        builder.setTitle(R.string.cash_dialog_title)
                .setPositiveButton("ACEPTAR", null)
                .setNegativeButton("CANCELAR", null);

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
        public void onCashDialogPositiveClick(String email);
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            button = d.getButton(Dialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (pago_correcto) {
                        if (chkBox.isChecked()) {
                            if (Validations.validate(Validations.VALIDATION_MAIL, editTextEmail.getText().toString())) {
                                mListener.onCashDialogPositiveClick(editTextEmail.getText().toString());
                                dismiss();
                            } else {
                                editTextEmail.setError(getResources().getString(R.string.error_dialog_mail));
                            }
                        } else {
                            if (editTextCantidad.getText().toString().trim().length() == 0) {
                                editTextCantidad.setError(getResources().getString(R.string.error_dialog_no_pago));
                            } else {
                                mListener.onCashDialogPositiveClick("");
                                dismiss();
                            }
                        }
                    } else {
                        editTextCantidad.setError(getResources().getString(R.string.error_dialog_pago_incorrecto));
                    }
                }
            });
        }
    }
}
