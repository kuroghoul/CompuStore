package com.fiuady.compustore.android.compustore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fiuady.compustore.R;

/**
 * Created by Kuro on 03/04/2017.
 */

public class DialogNewCategory extends DialogFragment{

    public interface DialogNewCategoryListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    DialogNewCategoryListener dncListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dncListener = (DialogNewCategoryListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_add_category,null);
        builder.setView(v).setMessage("Agregar nueva categoria").setPositiveButton(R.string.dialog_add_button, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                //Probablemente se haga de esta manera:
                //CategoriesActivity manda a llamar una instancia de este dialogo
                //pero hay que pasarle algún  de boundle para recuperar datos.
                //El objetivo es que el dialogo no interactue directamente con la base de datos
                //sino que categoriesActivity lo haga con la información que reciba de este dialogo.
                //Esto es tanto para el boton positivo como el negativo
                dncListener.onDialogPositiveClick(DialogNewCategory.this);


            }
        }).setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dncListener.onDialogNegativeClick(DialogNewCategory.this);
            }
        });
        return builder.create();

    }
}
