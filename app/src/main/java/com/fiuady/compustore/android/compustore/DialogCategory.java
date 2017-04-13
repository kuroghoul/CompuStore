package com.fiuady.compustore.android.compustore;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fiuady.compustore.R;

/**
 * Created by Kuro on 03/04/2017.
 */

public class DialogCategory extends DialogFragment{
    private static String ARG_TAG = "com.fiuady.compustore.android.compustore.dialogcategory.tag";

    public static String ARG_TITLE = "com.fiuady.compustore.android.compustore.dialogcategory.title";
    public static String ARG_MESSAGE = "com.fiuady.compustore.android.compustore.dialogcategory.message";
    public static String ARG_BTN_POSITIVE = "com.fiuady.compustore.android.compustore.dialogcategory.btnpositive";
    public static String ARG_BTN_NEGATIVE = "com.fiuady.compustore.android.compustore.dialogcategory.btnnegative";
    public static String ARG_SAVE_DATA = "com.fiuady.compustore.android.compustore.dialogcategory.savedata";
    public static String ARG_ET_DESCRIPTION = "com.fiuady.compustore.android.compustore.dialogcategory.description";

    public interface DialogCategoryListener {
        //Este listener sirve para enviar información al activity host de este dialog
        void onDialogCategoryPositiveClick(String tag, DialogFragment dialog);
        void onDialogCategoryNegativeClick(String tag, DialogFragment dialog);
    }

    private DialogCategoryListener dncListener;
    private String tag;
    private String et_description;
    private String title;
    private String message;
    private String positiveTxt;
    private String negativeTxt;

    private Bundle savedData;

    static DialogCategory newInstance (String tag, Bundle args)
    {
        Bundle bundle = args;
        bundle.putString(ARG_TAG, tag);
        DialogCategory dialog = new DialogCategory();
        dialog.setArguments(bundle);
        return dialog;
    }

    public DialogCategory() {
        this.dncListener = null;
    }

    public Bundle getSavedData(){
        return savedData;
    }

    @Override
    public void onAttach(Context context) { //Este sirve para obligar al activity que llame al dialogo a implementar la interface anterior
        super.onAttach(context);
        Activity activity = (Activity)context;
        try {
            dncListener = (DialogCategoryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " debe de implementar el DialogCategoryListener de la clase DialogCategory");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        tag = args.getString(ARG_TAG);
        et_description = args.getString(ARG_ET_DESCRIPTION);
        title = args.getString(ARG_TITLE);
        message = args.getString(ARG_MESSAGE);
        positiveTxt = args.getString(ARG_BTN_POSITIVE);
        negativeTxt = args.getString(ARG_BTN_NEGATIVE);

        savedData = args.getBundle(ARG_SAVE_DATA);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_add_category, null);

        Button positiveBtn =(Button) v.findViewById(R.id.dialog_add_category_btnADdd);
        Button negativeBtn= (Button) v.findViewById(R.id.dialog_add_category_btncancel);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dncListener.onDialogCategoryPositiveClick(tag, DialogCategory.this);
                    }
                });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dncListener.onDialogCategoryNegativeClick(tag, DialogCategory.this);
            }
        });
        if(et_description!=null)
        {
            ((EditText)v.findViewById(R.id.dialog_add_category_description)).append(et_description);
        }
        if(message!=null){
            builder.setMessage(message);
        }
        if(title!=null){
            builder.setTitle(title);
        }
        if(positiveTxt!=null){
            positiveBtn.setText(positiveTxt);
        }
        if(negativeTxt!=null){
            negativeBtn.setText(negativeTxt);
        }
        builder.setView(v);
        return builder.create();

    }


    //Este método que intenté agregar no funciona completamente
    //La intención era poder utilizarlo como se utiliza un "setOnClickListener"
    //Y poder declararle a este diálogo un listener a través de una clase anónima
    //Aparentemente funciona, sin embargo, al rotar, el "dncListener" se pierte y por tanto se vuelve nulo
    //Y tendría que reasignarlo en el onCreate del activity host del diálogo
    //Pero se perdería entonces el sentido de haber usado una clase anónima
    //Además de que en algunos casos se puede hacer uso de variables locales y en el onCreate
    //No necesariamente se tendría acceso a ellas

    //public void setDialogCategoryListener (DialogCategoryListener listener)
    //{
    //    this.dncListener = listener;
    //}



}
