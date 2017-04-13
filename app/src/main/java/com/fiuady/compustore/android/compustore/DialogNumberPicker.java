package com.fiuady.compustore.android.compustore;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.fiuady.compustore.R;

/**
 * Created by Kuro on 13/04/2017.
 */

public class DialogNumberPicker extends DialogFragment {

    public static final String MESSAGE = "com.fiuady.compustore.android.compustore.dialogNumberPicker.message";
    public static final String POSITIVE = "com.fiuady.compustore.android.compustore.dialogNumberPicker.positive";
    public static final String NEGATIVE = "com.fiuady.compustore.android.compustore.dialogNumberPicker.negative";
    public static final String MIN = "com.fiuady.compustore.android.compustore.dialogNumberPicker.minimum";
    public static final String MAX = "com.fiuady.compustore.android.compustore.dialogNumberPicker.maximum";
    public static final String SAVEDDATA = "com.fiuady.compustore.android.compustore.dialogNumberPicker.saveddata";

    private DialogNumberPicker.DialogNumberPickerListener mListener;

    public interface DialogNumberPickerListener {
        void onDialogNumberPickerPositiveClick(DialogFragment dialog, int value);
        void onDialogNumberPickerNegativeClick(DialogFragment dialog, int value);
    }

    public DialogNumberPicker() {
        mListener = null;
    }

    static DialogNumberPicker newInstance (String message, String positiveBtn, String negativeBtn, int min, int max, Bundle savedData)
    {
        DialogNumberPicker dialogNumberPicker = new DialogNumberPicker();
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE, message);
        bundle.putString(POSITIVE, positiveBtn);
        bundle.putString(NEGATIVE, negativeBtn);
        bundle.putInt(MIN, min);
        bundle.putInt(MAX, max);
        bundle.putBundle(SAVEDDATA, savedData);

        dialogNumberPicker.setArguments(bundle);
        return dialogNumberPicker;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_number_picker,null);
        Button positiveBtn = (Button)v.findViewById(R.id.dialog_number_picker_positivebtn);
        Button negativeBtn= (Button)v.findViewById(R.id.dialog_number_picker_negativebtn);
        final NumberPicker numberPicker = (NumberPicker)v.findViewById(R.id.dialog_number_picker_np);
        numberPicker.setMinValue(args.getInt(MIN));
        numberPicker.setMaxValue(args.getInt(MAX));
        numberPicker.setWrapSelectorWheel(false);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null) {
                    mListener.onDialogNumberPickerPositiveClick(DialogNumberPicker.this, numberPicker.getValue());
                }
            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null) {
                    mListener.onDialogNumberPickerNegativeClick(DialogNumberPicker.this, numberPicker.getValue());
                }
            }
        });

        builder.setMessage(args.getString(MESSAGE));
        builder.setView(v);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (DialogNumberPicker.DialogNumberPickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe de implementar DialogNumberPickerListener para que detecte los clicks del dialogo");
        }
    }

    public Bundle getSavedData ()
    {
        return getArguments().getBundle(SAVEDDATA);
    }

}
