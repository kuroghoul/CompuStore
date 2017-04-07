package com.fiuady.compustore.android.compustore;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.ProductCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuro on 06/04/2017.
 */

public class DialogNewProduct extends DialogFragment {
    private Inventory inventory;
    private List<ProductCategory> productCategories;
    ArrayAdapter<String> spinnerAdapter;
    Spinner spinner;
    NumberPicker qty;





    public interface DialogNewProductListener{ //Este listener sirve para enviar información al activity host de este dialog
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    DialogNewProduct.DialogNewProductListener dncListener;

    @Override
    public void onAttach(Context context) { //Este sirve para obligar al activity que llame al dialogo a implementar la interface anterior
        super.onAttach(context);
        Activity activity = (Activity)context;
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dncListener = (DialogNewProduct.DialogNewProductListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inventory = new Inventory(this.getContext());
        productCategories = inventory.getAllProductCategories();
        ArrayList<String> arraySpinnerList = new ArrayList<String>();
        for (ProductCategory category : productCategories)
        {
            arraySpinnerList.add(category.getDescription());
        }
        String[] arraySpinnerStrings = new String [arraySpinnerList.size()];
        arraySpinnerList.toArray(arraySpinnerStrings);
        spinnerAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, arraySpinnerStrings);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_add_product,null);

        spinner=(Spinner)v.findViewById(R.id.dialog_add_product_spinner_categories);
        spinner.setAdapter(spinnerAdapter);

        qty = (NumberPicker)v.findViewById(R.id.dialog_add_product_qty);
        qty.setMinValue(0);
        qty.setMaxValue(99);

        EditText price = (EditText)v.findViewById(R.id.dialog_add_product_price);
        price.setText("0");


        builder.setView(v).setMessage("Agregar nuevo producto")
                .setPositiveButton(R.string.dialog_add_button, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dncListener.onDialogPositiveClick(DialogNewProduct.this);
                    }
                }).setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dncListener.onDialogNegativeClick(DialogNewProduct.this);
            }
        });
        return builder.create();

    }



}
