package com.fiuady.compustore.android.compustore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Product;

import java.util.ArrayList;
import java.util.List;

public class AssemblyInsertActivity extends AppCompatActivity implements DialogConfirm.DialogConfirmListener{

    public static final int CODE_SEARCH_PRODUCT = 1;
    private static String KEY_PRODUCT_LIST_ID = "com.fiuady.compustore.android.compustore.AssemblyInsertActivity.ProductListId";
    private static String KEY_PRODUCT_LIST_QTY = "com.fiuady.compustore.android.compustore.AssemblyInsertActivity.ProductListQty";
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.AssemblyInsertActivity.dialogsavedataadapterposition";
    private static String dialogTagDelete = "com.fiuady.compustore.android.compustore.AssemblyInsertActivity.dialogtagdelete";

    Inventory inventory;

    private RecyclerView recyclerView;
    private AssemblyInsertActivity.ProductsAdapter adapter;
    private ArrayList<Product> productList;
    private ArrayList<Integer> productListId; // Used to restore the recyclerView on rotation
    private ArrayList<Integer> productQty;
    private Context context;


    @Override
    public void onDialogConfirmPositiveClick(DialogFragment dialog) {
        Bundle save = ((DialogConfirm)dialog).getSavedData();
        int position = save.getInt(dialogSaveDataAdapterPosition);
        productList.remove(position);
        productListId.remove(position);
        productQty.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, productList.size());
        Toast.makeText(AssemblyInsertActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDialogConfirmNegativeClick(DialogFragment dialog) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assembly_insert);
        context = this;
        inventory = new Inventory(this);
        productList = new ArrayList<Product>();
        productListId = new ArrayList<>();
        productQty = new ArrayList<>();



        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AssemblyInsertActivity.this, AssemblySearchProductActivity.class);
                intent.putIntegerArrayListExtra(AssemblySearchProductActivity.EXTRA_IGNORE_PRODUCT_ID, productListId);
                startActivityForResult(intent, CODE_SEARCH_PRODUCT);

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });



        recyclerView=(RecyclerView)findViewById(R.id.assemblyProducts_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AssemblyInsertActivity.ProductsAdapter(productList, this);
        recyclerView.setAdapter(adapter);
    }










    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView txtDescription;
        private TextView txtCategory;
        private TextView txtPrice;
        private TextView txtQty;
        private TextView options;
        private int id;

        public ProductHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            txtDescription=(TextView)itemView.findViewById(R.id.product_description_text);
            txtCategory=(TextView)itemView.findViewById(R.id.product_category_text);
            txtPrice=(TextView)itemView.findViewById(R.id.product_price_text);
            txtQty=(TextView)itemView.findViewById(R.id.product_qty_text);
            options=(TextView)itemView.findViewById(R.id.product_options);

            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(context, v);

                    popup.inflate(R.menu.menu_options_me);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.me_modify:{

                                    break;}
                                case R.id.me_delete: {
                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());
                                    DialogConfirm confirmDelete = DialogConfirm.newInstance("¿Seguro que desea retirar este producto del ensamble?", "Si", "No", save);
                                    confirmDelete.show(getSupportFragmentManager(), dialogTagDelete);
                                    break;}
                            }


                            return false;
                        }
                    });
                    popup.show();
                }
            });

        }

        public void bindProduct(Product product)
        {
            id = product.getId();
            txtDescription.setText(product.getDescription());
            txtCategory.setText(product.getProductCategory().getDescription());
            txtPrice.setText(Integer.toString(product.getPrice()));
            txtQty.setText(Integer.toString(product.getQty()));
        }

        @Override
        public void onClick(View v) {

            Toast.makeText(AssemblyInsertActivity.this, "Producto: " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
        }

        public int getId() {
            return id;
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

        public TextView getOptions() {
            return options;
        }
    }

    private class ProductsAdapter extends RecyclerView.Adapter<AssemblyInsertActivity.ProductHolder>
    {
        private List<Product> products;
        private Context context;
        public ProductsAdapter(List<Product> products, Context context) {
            this.products = products;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final AssemblyInsertActivity.ProductHolder holder, final int position) {
            holder.bindProduct(products.get(position));

        }

        @Override
        public AssemblyInsertActivity.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.product_list_item, parent, false);
            return new AssemblyInsertActivity.ProductHolder(view);
        }

        @Override
        public int getItemCount() {
            return products.size();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Integer> newProductListId = new ArrayList<>();
        for (Product product : productList)
        {
            newProductListId.add(product.getId());
        }
        outState.putIntegerArrayList(KEY_PRODUCT_LIST_ID, newProductListId);
        outState.putIntegerArrayList(KEY_PRODUCT_LIST_QTY, productQty);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        productListId.clear();
        productList.clear();
        productListId = savedInstanceState.getIntegerArrayList(KEY_PRODUCT_LIST_ID);
        productQty = savedInstanceState.getIntegerArrayList(KEY_PRODUCT_LIST_QTY);
        if (productList != null) {
            for (Integer id : productListId) {
                productList.add(inventory.getProductById(id));
            }

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CODE_SEARCH_PRODUCT && resultCode== Activity.RESULT_OK)
        {
            int id = data.getIntExtra(AssemblySearchProductActivity.EXTRA_PRODUCT_ID, -1);
            if ( id!= -1)
            {
                productList.add(inventory.getProductById(id));
                productListId.add(id);
                productQty.add(1); //Qty by default
                adapter.notifyDataSetChanged();

            }

            Toast.makeText(AssemblyInsertActivity.this, "Hola! resultCode OK" + id, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_assemblies_insert,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.assembly_menu_insert_save) {
            EditText assemblyDescription = (EditText) findViewById(R.id.assemblyInsertActivity_description);
            switch (inventory.insertAssembly(assemblyDescription.getText().toString(), productList, productQty))
            {
                case InvalidDescription:
                    Toast.makeText(AssemblyInsertActivity.this, "Descripción Inválida", Toast.LENGTH_SHORT).show();
                    break;
                case DuplicateDescription:
                    Toast.makeText(AssemblyInsertActivity.this, "Ya existe un ensamble con este nombre", Toast.LENGTH_SHORT).show();
                    break;
                case Ok:
                    Toast.makeText(AssemblyInsertActivity.this, "Ensamble agregado con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }


    }
}
