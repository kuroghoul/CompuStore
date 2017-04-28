package com.fiuady.compustore.android.compustore;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuro on 27/04/2017.
 */

public class DialogProductList extends DialogFragment {
    private static String ARG_TAG = "com.fiuady.compustore.android.compustore.dialogproductlist.tag";

    public static String ARG_TITLE = "com.fiuady.compustore.android.compustore.dialogproductlist.title";
    public static String ARG_MESSAGE = "com.fiuady.compustore.android.compustore.dialogproductlist.message";
    public static String ARG_BTN_POSITIVE = "com.fiuady.compustore.android.compustore.dialogproductlist.btnpositive";
    public static String ARG_PRODUCT_ID_LIST = "com.fiuady.compustore.android.compustore.dialogproductlist.producidlist";
    public static String ARG_PRODUCT_QTY_LIST = "com.fiuady.compustore.android.compustore.dialogproductlist.productqtylist";
    public static String ARG_QTY_TAG = "com.fiuady.compustore.android.compustore.dialogproductlist.qtytag";
    public static String ARG_SAVE_DATA = "com.fiuady.compustore.android.compustore.dialogproductlist.savedata";

    private String tag;
    private String title;
    private String message;
    private String positiveTxt;
    private Bundle savedData;
    private ArrayList<Integer> productsId;
    private ArrayList<Integer> productsQty;
    private String qtyTag;

    private Button positiveBtn;

    Inventory inventory;



    static DialogProductList newInstance (String tag, Bundle args)
    {
        args.putString(ARG_TAG, tag);
        DialogProductList dialog = new DialogProductList();
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
        final View v = inflater.inflate(R.layout.dialog_product_list,null);
        positiveBtn = (Button)v.findViewById(R.id.dialog_product_list_positiveBtn);
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
        productsId = args.getIntegerArrayList(ARG_PRODUCT_ID_LIST);
        productsQty = args.getIntegerArrayList(ARG_PRODUCT_QTY_LIST);
        qtyTag = args.getString(ARG_QTY_TAG);
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

        if(productsId!=null)
        {
            RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.dialog_product_list_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            ArrayList<Product> products = new ArrayList<Product>();
            for (int productId : productsId)
            {
                products.add(inventory.getProductById(productId));
            }
            if(productsQty!=null)
            {
                for (int i=0;i<products.size();i++)
                {
                    products.get(i).setQty(productsQty.get(i));
                }
            }

            ProductsAdapter adapter = new ProductsAdapter(products, getContext());
            recyclerView.setAdapter(adapter);
        }


        builder.setView(v);
        return builder.create();

    }














    private class ProductHolder extends RecyclerView.ViewHolder
    {
        private TextView txtDescription;
        private TextView txtCategory;
        private TextView txtPrice;
        private TextView txtQtyTag;
        private TextView txtQty;
        private TextView options;
        private int id;
        private Product product;

        public ProductHolder (View itemView)
        {
            super(itemView);
            txtDescription=(TextView)itemView.findViewById(R.id.product_description_text);
            txtCategory=(TextView)itemView.findViewById(R.id.product_category_text);
            txtPrice=(TextView)itemView.findViewById(R.id.product_price_text);
            txtQty=(TextView)itemView.findViewById(R.id.product_qty_text);
            txtQtyTag=(TextView)itemView.findViewById(R.id.product_qty_tag);
            options=(TextView)itemView.findViewById(R.id.product_options);
        }

        public void bindProduct(Product product)
        {
            this.product = product;
            id = product.getId();
            txtDescription.setText(product.getDescription());
            txtCategory.setText(product.getProductCategory().getDescription());
            txtPrice.setText(Integer.toString(product.getPrice()));
            txtQty.setText(Integer.toString(product.getQty()));
            if(qtyTag!=null)
            {
                txtQtyTag.setText(qtyTag);
            }

        }


        public int getId() {
            return id;
        }

        public Product getProduct() {
            return product;
        }

        public TextView getTxtDescription() {
            return txtDescription;
        }

        public TextView getTxtCategory() {
            return txtCategory;
        }

        public TextView getTxtPrice() {
            return txtPrice;
        }

        public TextView getTxtQty() {
            return txtQty;
        }

        public TextView getTxtQtyTag() {
            return txtQtyTag;
        }

        public TextView getOptions() {
            return options;
        }
    }




    private class ProductsAdapter extends RecyclerView.Adapter<DialogProductList.ProductHolder>
    {
        private ArrayList<Product> products;
        private Context context;
        public ProductsAdapter(ArrayList<Product> products, Context context) {
            this.products = products;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final DialogProductList.ProductHolder holder, final int position) {
            holder.bindProduct(products.get(position));

        }

        @Override
        public DialogProductList.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.product_list_item_simple, parent, false);
            return new DialogProductList.ProductHolder(view);
        }

        @Override
        public int getItemCount() {
            return this.products.size();
        }
    }













}
