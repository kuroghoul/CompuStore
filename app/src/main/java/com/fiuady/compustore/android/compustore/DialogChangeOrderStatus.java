package com.fiuady.compustore.android.compustore;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.OrderStatus;

import java.util.ArrayList;

/**
 * Created by Kuro on 27/04/2017.
 */

public class DialogChangeOrderStatus extends DialogFragment{

    private static String ARG_TAG = "com.fiuady.compustore.android.compustore.dialogproduct.tag";

    public static String ARG_TITLE = "com.fiuady.compustore.android.compustore.dialogproduct.title";
    public static String ARG_MESSAGE = "com.fiuady.compustore.android.compustore.dialogproduct.message";
    public static String ARG_BTN_POSITIVE = "com.fiuady.compustore.android.compustore.dialogproduct.btnpositive";
    public static String ARG_BTN_NEGATIVE = "com.fiuady.compustore.android.compustore.dialogproduct.btnnegative";
    public static String ARG_SAVE_DATA = "com.fiuady.compustore.android.compustore.dialogproduct.savedata";
    public static String ARG_SP_ARRAYLIST_ORDER_iD = "com.fiuady.compustore.android.compustore.dialogproduct.description";

    public interface DialogChangeOrderStatusListener { //Este listener sirve para enviar información al activity host de este dialog
        public void onDialogOrderStatusPositiveClick(String tag, DialogFragment dialog, OrderStatus orderStatus);
        public void onDialogOrderStatusNegativeClick(String tag, DialogFragment dialog, OrderStatus orderStatus);
    }

    private String title;
    private String message;
    private String positiveTxt;
    private String negativeTxt;

    private Bundle savedData;
    private String tag;

    private Inventory inventory;


    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;

    private DialogChangeOrderStatusListener mListener;



    static DialogChangeOrderStatus newInstance(String tag, Bundle args)
    {
        args.putString(tag, ARG_TAG);
        DialogChangeOrderStatus dialog = new DialogChangeOrderStatus();
        dialog.setArguments(args);
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
            mListener = (DialogChangeOrderStatusListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " debe de implementar el DialogChangeOrderStatusListener de la clase DialogChangeOrderStatus");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        tag = args.getString(ARG_TAG);
        savedData = args.getBundle(ARG_SAVE_DATA);
        title = args.getString(ARG_TITLE);
        message = args.getString(ARG_MESSAGE);
        positiveTxt = args.getString(ARG_BTN_POSITIVE);
        negativeTxt = args.getString(ARG_BTN_NEGATIVE);



        inventory = new Inventory(this.getContext());



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_change_order_status,null);


        spinner = (Spinner)v.findViewById(R.id.dialog_change_order_status_spinner);
        EditText changelogTxt = (EditText)v.findViewById(R.id.dialog_change_order_status_changelog);

        final ArrayList<OrderStatus> orderStatusList = new ArrayList<OrderStatus>();
        ArrayList<String> orderStatusSpinnerList = new ArrayList<String>();

        if(args.getIntegerArrayList(ARG_SP_ARRAYLIST_ORDER_iD)!=null) {
            for (Integer orderId : args.getIntegerArrayList(ARG_SP_ARRAYLIST_ORDER_iD)) {
                orderStatusList.add(inventory.getOrderStatusById(orderId));
            }
            for(OrderStatus orderStatus1 : orderStatusList)
            {
                orderStatusSpinnerList.add(orderStatus1.getDescription());
            }
            String[] arraySpinnerStrings = new String[orderStatusSpinnerList.size()];
            orderStatusSpinnerList.toArray(arraySpinnerStrings);
            spinnerAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, arraySpinnerStrings);
            spinner.setAdapter(spinnerAdapter);
        }








        Button positiveBtn = (Button)v.findViewById(R.id.dialog_change_order_status_ok);
        Button negativeBtn = (Button)v.findViewById(R.id.dialog_change_order_status_cancel);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onDialogOrderStatusPositiveClick(tag, DialogChangeOrderStatus.this, orderStatusList.get(spinner.getSelectedItemPosition()));
            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogOrderStatusNegativeClick(tag, DialogChangeOrderStatus.this, orderStatusList.get(spinner.getSelectedItemPosition()));
            }
        });

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
        builder.setView(v);
        return builder.create();
    }
}
