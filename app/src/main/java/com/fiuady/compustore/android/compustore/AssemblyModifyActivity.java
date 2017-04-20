package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.fiuady.compustore.db.Assembly;
import com.fiuady.compustore.db.AssemblyProducts;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Product;

import java.util.ArrayList;
import java.util.List;

public class AssemblyModifyActivity extends AppCompatActivity implements DialogConfirm.DialogConfirmListener, DialogNumberPicker.DialogNumberPickerListener {
    public static String EXTRA_ASSEMBLY_ID = "com.fiuady.compustore.android.compustore.assemblyModifyActivity.extraProductId";
    public static final int CODE_SEARCH_PRODUCT = 1;
    private Inventory inventory;
    private Context context;
    private Intent intent;
    private int extraAssemblyId;
    private Assembly assembly;
    private EditText etDescription;
    private RecyclerView recyclerView;
    private AssemblyProductsAdapter adapter;
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.AssemblyInsertActivity.dialogsavedataadapterposition";
    private static String dialogTagDelete = "com.fiuady.compustore.android.compustore.AssemblyInsertActivity.dialogtagdelete";
    private static String dialogTagQty = "com.fiuady.compustore.android.compustore.AssemblyInsertActivity.dialogQty";

    private static String KEY_PRODUCT_LIST_ID = "com.fiuady.compustore.android.compustore.AssemblyInsertActivity.ProductListId";
    private static String KEY_PRODUCT_LIST_QTY = "com.fiuady.compustore.android.compustore.AssemblyInsertActivity.ProductListQty";

    private static int maxQty = 99;
    private ArrayList<Product> products;
    private ArrayList<AssemblyProducts> assemblyProducts;
    private ArrayList<Integer> productListId;
    @Override
    public void onDialogConfirmPositiveClick(DialogFragment dialog) {
        Bundle save = ((DialogConfirm)dialog).getSavedData();
        int position = save.getInt(dialogSaveDataAdapterPosition);
        products.remove(position);
        assemblyProducts.remove(position);
        //productListId.remove(position);
        //productQty.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, products.size());
        Toast.makeText(AssemblyModifyActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogConfirmNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNumberPickerPositiveClick(DialogFragment dialog, int value) {
        Bundle save = ((DialogNumberPicker)dialog).getSavedData();
        int position = save.getInt(dialogSaveDataAdapterPosition);
        AssemblyProducts newAssemblyProduct = assemblyProducts.get(position);
        newAssemblyProduct.setQty(value);
    }

    @Override
    public void onDialogNumberPickerNegativeClick(DialogFragment dialog, int value) {
        dialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assembly_insert);
        context = this;
        inventory = new Inventory(this);
        intent = getIntent();
        extraAssemblyId = intent.getIntExtra(EXTRA_ASSEMBLY_ID, -1);

        if (extraAssemblyId!=-1)
        {
            assembly = inventory.getAssemblyById(extraAssemblyId);
            assemblyProducts = inventory.getAssemblyProductsById(assembly.getId());
            etDescription = (EditText)findViewById(R.id.assemblyInsertActivity_description);
            etDescription.append(assembly.getDescription());
            productListId = new ArrayList<Integer>();


            //products = inventory.getAllAssemblyProducts(extraAssemblyId);
            products = new ArrayList<Product>();
            for (AssemblyProducts aP : assemblyProducts)
            {
                products.add(inventory.getProductById(aP.getProductId()));
                productListId.add(aP.getProductId());
            }

            adapter = new AssemblyProductsAdapter(assemblyProducts, this);
            recyclerView = (RecyclerView)findViewById(R.id.assemblyProducts_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);


            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

            Intent intent = new Intent(AssemblyModifyActivity.this, AssemblySearchProductActivity.class);
            intent.putIntegerArrayListExtra(AssemblySearchProductActivity.EXTRA_IGNORE_PRODUCT_ID, productListId);
            startActivityForResult(intent, CODE_SEARCH_PRODUCT);

                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    //        .setAction("Action", null).show();
                }
            });

        }
        else
        {
            finish();
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Integer> newProductListId = new ArrayList<>();
        ArrayList<Integer> newProductListQty = new ArrayList<>();
        for (AssemblyProducts aP : assemblyProducts)
        {
            newProductListId.add(aP.getProductId());
            newProductListQty.add(aP.getQty());
        }
        outState.putIntegerArrayList(KEY_PRODUCT_LIST_ID, newProductListId);
        outState.putIntegerArrayList(KEY_PRODUCT_LIST_QTY, newProductListQty);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        assemblyProducts.clear();
        productListId.clear();
        productListId = savedInstanceState.getIntegerArrayList(KEY_PRODUCT_LIST_ID);
        ArrayList<Integer> productListQty = savedInstanceState.getIntegerArrayList(KEY_PRODUCT_LIST_QTY);

        if (productListId.size() == productListQty.size())
        {
            for (int i=0 ; i<(productListId.size());i++)
            {
                assemblyProducts.add(new AssemblyProducts(assembly.getId(), productListId.get(i),productListQty.get(i)));
            }

        }
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
                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());
                                    String message = getString(R.string.dialogNumberPicker_products_addStock_message);
                                    String positivetxt = getString(R.string.dialogNumberPicker_products_addStock_positivebtn);
                                    String negativetxt = getString(R.string.dialogNumberPicker_products_addStock_negativebtn);
                                    int min = products.get(getAdapterPosition()).getQty();
                                    int max = maxQty;


                                    DialogNumberPicker dialogNumberPicker = DialogNumberPicker.newInstance(message, positivetxt, negativetxt, min, max, save);
                                    dialogNumberPicker.show(getSupportFragmentManager(),dialogTagQty);
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

        public void bindProduct(AssemblyProducts assemblyProduct)
        {
            Product product = inventory.getProductById(assemblyProduct.getProductId());
            id = product.getId();
            txtDescription.setText(product.getDescription());
            txtCategory.setText(product.getProductCategory().getDescription());
            txtPrice.setText(Integer.toString(product.getPrice()));
            txtQty.setText(Integer.toString(product.getQty()));
        }

        @Override
        public void onClick(View v) {

            Toast.makeText(AssemblyModifyActivity.this, "Producto: " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
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

    private class AssemblyProductsAdapter extends RecyclerView.Adapter<AssemblyModifyActivity.ProductHolder>
    {
        private ArrayList<AssemblyProducts> products;
        private Context context;
        public AssemblyProductsAdapter(ArrayList<AssemblyProducts> products, Context context) {
            this.products = products;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final AssemblyModifyActivity.ProductHolder holder, final int position) {
            holder.bindProduct(products.get(position));

        }

        @Override
        public AssemblyModifyActivity.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.product_list_item, parent, false);
            return new AssemblyModifyActivity.ProductHolder(view);
        }

        @Override
        public int getItemCount() {
            return products.size();
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
            Assembly oldAssembly = assembly;
            Assembly newAssembly = new Assembly(oldAssembly);
            newAssembly.setDescription(assemblyDescription.getText().toString());




            switch (inventory.modifyAssembly(newAssembly, assemblyProducts)) {
                case InvalidDescription:
                    Toast.makeText(AssemblyModifyActivity.this, "Descripción Inválida", Toast.LENGTH_SHORT).show();
                    break;
                case DuplicateDescription:
                    Toast.makeText(AssemblyModifyActivity.this, "Ya existe un ensamble con este nombre", Toast.LENGTH_SHORT).show();
                    break;
                case Ok:
                    Toast.makeText(AssemblyModifyActivity.this, "Ensamble agregado con éxito", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==CODE_SEARCH_PRODUCT &&
                resultCode == AssemblySearchProductActivity.RESULT_OK)
        {
            int id = data.getIntExtra(AssemblySearchProductActivity.EXTRA_PRODUCT_ID, -1);
            if(id!=-1) {
                products.add(inventory.getProductById(id));
                productListId.add(id);
                assemblyProducts.add(new AssemblyProducts(assembly.getId(), id, 1));
                adapter.notifyDataSetChanged();
            }
        }


    }
}
