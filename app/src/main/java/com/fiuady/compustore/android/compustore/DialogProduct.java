package com.fiuady.compustore.android.compustore;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.ProductCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuro on 06/04/2017.
 */

public class DialogProduct extends DialogFragment {
    private static String ARG_TAG = "com.fiuady.compustore.android.compustore.dialogproduct.tag";

    public static String ARG_TITLE = "com.fiuady.compustore.android.compustore.dialogproduct.title";
    public static String ARG_MESSAGE = "com.fiuady.compustore.android.compustore.dialogproduct.message";
    public static String ARG_BTN_POSITIVE = "com.fiuady.compustore.android.compustore.dialogproduct.btnpositive";
    public static String ARG_BTN_NEGATIVE = "com.fiuady.compustore.android.compustore.dialogproduct.btnnegative";
    public static String ARG_SAVE_DATA = "com.fiuady.compustore.android.compustore.dialogproduct.savedata";
    public static String ARG_ET_DESCRIPTION = "com.fiuady.compustore.android.compustore.dialogproduct.description";
    public static String ARG_SP_CATEGORY = "com.fiuady.compustore.android.compustore.dialogproduct.category";
    public static String ARG_ET_PRICE = "com.fiuady.compustore.android.compustore.dialogproduct.price";



    public interface DialogProductListener { //Este listener sirve para enviar información al activity host de este dialog
        public void onDialogPositiveClick(String tag, DialogFragment dialog);
        public void onDialogNegativeClick(String tag, DialogFragment dialog);
    }
    private Inventory inventory;
    private List<ProductCategory> productCategories;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner spinner;

    private String tag;
    private String et_description;
    private String sp_category;
    private String et_price;
    private String title;
    private String message;
    private String positiveTxt;
    private String negativeTxt;
    private Bundle savedData;
    //private NumberPicker qty;
    private DialogProductListener dpListener;

    static DialogProduct newInstance (String tag, Bundle args)
    {
        Bundle bundle = args;
        bundle.putString(ARG_TAG, tag);
        DialogProduct dialog = new DialogProduct();
        dialog.setArguments(bundle);
        return dialog;
    }

    public Bundle getSavedData() {
        return savedData;
    }

    @Override
    public void onAttach(Context context) { //Este sirve para obligar al activity que llame al dialogo a implementar la interface anterior
        super.onAttach(context);
        Activity activity = (Activity)context;
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dpListener = (DialogProductListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " debe de implementar el DialogProductListener de la clase DialogProduct");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        tag = args.getString(ARG_TAG);
        et_description = args.getString(ARG_ET_DESCRIPTION);
        sp_category = args.getString(ARG_SP_CATEGORY);
        et_price = args.getString(ARG_ET_PRICE);
        title = args.getString(ARG_TITLE);
        message = args.getString(ARG_MESSAGE);
        positiveTxt = args.getString(ARG_BTN_POSITIVE);
        negativeTxt = args.getString(ARG_BTN_NEGATIVE);
        savedData = args.getBundle(ARG_SAVE_DATA);

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

        Button positiveBtn =(Button)v.findViewById(R.id.dialog_add_product_positivebtn);
        Button negativeBtn =(Button)v.findViewById(R.id.dialog_add_product_negativebtn);
        EditText description = (EditText)v.findViewById(R.id.dialog_add_product_description);
        EditText price = (EditText)v.findViewById(R.id.dialog_add_product_price);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpListener.onDialogPositiveClick(tag, DialogProduct.this);
            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpListener.onDialogNegativeClick(tag, DialogProduct.this);
            }
        });

        if(et_description!=null)
        {
            description.append(et_description);
        }
        if(sp_category!=null)
        {
            spinner.setSelection(spinnerAdapter.getPosition(sp_category));
        }
        if(et_price!=null)
        {
            price.append(et_price);
        }
        if(title!=null)
        {
            builder.setTitle(title);
        }
        if(message!=null)
        {
            builder.setMessage(message);
        }
        if(positiveTxt!=null)
        {
            positiveBtn.setText(positiveTxt);
        }
        if(negativeTxt!=null)
        {
            negativeBtn.setText(negativeTxt);
        }
        //qty = (NumberPicker)v.findViewById(R.id.dialog_add_product_qty);
        //qty.setMinValue(0);
        //qty.setMaxValue(99);

        builder.setView(v);

        return builder.create();

    }




}
