package com.fiuady.compustore.android.compustore;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Kuro on 28/04/2017.
 */

public class DialogOrderSaleList extends DialogFragment {

    private static String ARG_TAG = "com.fiuady.compustore.android.compustore.dialogOrderSaleList.tag";

    public static String ARG_TITLE = "com.fiuady.compustore.android.compustore.dialogOrderSaleList.title";
    public static String ARG_MESSAGE = "com.fiuady.compustore.android.compustore.dialogOrderSaleList.message";
    public static String ARG_BTN_POSITIVE = "com.fiuady.compustore.android.compustore.dialogOrderSaleList.btnpositive";
    public static String ARG_CALENDAR = "com.fiuady.compustore.android.compustore.dialogOrderSaleList.producidlist";

    public static String ARG_SAVE_DATA = "com.fiuady.compustore.android.compustore.dialogOrderSaleList.savedata";
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

    private String tag;
    private String title;
    private String message;
    private String positiveTxt;
    private Bundle savedData;

    private Calendar calendar;

    private Button positiveBtn;

    Inventory inventory;

    static DialogOrderSaleList newInstance (String tag, Bundle args)
    {
        args.putString(ARG_TAG, tag);
        DialogOrderSaleList dialog = new DialogOrderSaleList();
        dialog.setArguments(args);
        return dialog;
    }

    public Bundle getSavedData() {
        return savedData;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_order_sale_list,null);
        positiveBtn = (Button)v.findViewById(R.id.dialog_orderSaleList_positiveBtn);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Bundle args = getArguments();
        tag = args.getString(ARG_TAG);
        title = args.getString(ARG_TITLE);
        message = args.getString(ARG_MESSAGE);
        positiveTxt = args.getString(ARG_BTN_POSITIVE);
        savedData = args.getBundle(ARG_SAVE_DATA);
        calendar = (Calendar)args.getSerializable(ARG_CALENDAR);

        inventory = new Inventory(getContext());


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

        if(calendar!=null)
        {
            RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.dialog_orderSaleList_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            ArrayList<Order> orders = inventory.getOrdersByMonthSale(calendar);
            OrderAdapter adapter = new OrderAdapter(orders, getContext());
            recyclerView.setAdapter(adapter);
        }


        builder.setView(v);
        return builder.create();
    }










    private class OrderHolder extends RecyclerView.ViewHolder
    {

        private TextView txtClient;
        private TextView txtStatus;
        private TextView txtDate;
        private TextView txtTotal;
        private Order order;

        public OrderHolder (View itemView)
        {
            super(itemView);

            txtClient=(TextView)itemView.findViewById(R.id.orderSaleList_client_text);
            txtStatus=(TextView)itemView.findViewById(R.id.orderSaleList_status_text);
            txtDate=(TextView)itemView.findViewById(R.id.orderSaleList_date_text);
            txtTotal=(TextView)itemView.findViewById(R.id.orderSaleList_total_text);

        }

        public void bindOrder(Order order)
        {
            this.order = order;
            txtClient.setText(order.getCustomer().getFullName());
            txtStatus.setText(order.getOrderStatus().getDescription());
            txtDate.setText(formatter.format(order.getCalendar().getTime()));
            txtTotal.append(String.valueOf(inventory.getTotalByOrder(order)));
        }



        public TextView getTxtClient() {
            return txtClient;
        }

        public Order getOrder() {
            return order;
        }
    }


    private class OrderAdapter extends RecyclerView.Adapter<OrderHolder> {
        private ArrayList<Order> orders;
        private Context context;

        public OrderAdapter(ArrayList<Order> orders, Context context) {
            this.orders = orders;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final OrderHolder holder, int position) {
            holder.bindOrder(this.orders.get(position));
        }

        @Override
        public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.order_sale_list_item, parent, false);
            return new OrderHolder(view);
        }

        @Override
        public int getItemCount() {
            return this.orders.size();
        }
    }













}
