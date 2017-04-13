package com.fiuady.compustore.android.compustore;

/**
 * Created by Kuro on 12/04/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Kuro on 11/04/2017.
 */

public class DialogConfirm extends DialogFragment {

    public static final String MESSAGE = "com.fiuady.compustore.android.compustore.dialogConfirm.message";
    public static final String POSITIVE = "com.fiuady.compustore.android.compustore.dialogConfirm.positive";
    public static final String NEGATIVE = "com.fiuady.compustore.android.compustore.dialogConfirm.negative";
    public static final String SAVEDDATA = "com.fiuady.compustore.android.compustore.dialogConfirm.saveddata";

    private DialogConfirmListener mListener;

    public interface DialogConfirmListener {
        void onDialogConfirmPositiveClick(DialogFragment dialog);
        void onDialogConfirmNegativeClick(DialogFragment dialog);
    }

    public DialogConfirm() {
        mListener = null;
    }

    static DialogConfirm newInstance (String message, String positiveBtn, String negativeBtn, Bundle savedData)
    {
        DialogConfirm dialogConfirm = new DialogConfirm();
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE, message);
        bundle.putString(POSITIVE, positiveBtn);
        bundle.putString(NEGATIVE, negativeBtn);
        bundle.putBundle(SAVEDDATA, savedData);
        dialogConfirm.setArguments(bundle);
        return dialogConfirm;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(args.getString(MESSAGE))
                .setPositiveButton(args.getString(POSITIVE), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mListener!=null) {
                            mListener.onDialogConfirmPositiveClick(DialogConfirm.this);
                        }
                    }
                })
                .setNegativeButton(args.getString(NEGATIVE), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mListener!=null) {
                            mListener.onDialogConfirmNegativeClick(DialogConfirm.this);
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (DialogConfirmListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe de implementar DialogConfirmListener para que detecte los clicks del dialogo");
        }
    }

    public Bundle getSavedData ()
    {
        return getArguments().getBundle(SAVEDDATA);
    }
}
