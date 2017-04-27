package com.fiuady.compustore.android.compustore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Product;
import com.fiuady.compustore.db.ProductCategory;

import java.util.ArrayList;
import java.util.List;

public class AssemblySearchProductActivity extends AppCompatActivity {

    private static String dialogTagModify = "com.fiuady.compustore.android.compustore.assemblySearchProductActivity.dialogtagmodify";
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.AssemblySearchProductActivity.dialogsavedataadapterposition";
    private static String dialogTagDelete = "com.fiuady.compustore.android.compustore.assemblySearchProductActivity.dialogtagdelete";
    private static String dialogTagInsert = "com.fiuady.compustore.android.compustore.assemblySearchProductActivity.dialogtaginsert";
    private static String dialogTagAddStock = "com.fiuady.compustore.android.compustore.assemblySearchProductActivity.dialogAddStock";

    public static String EXTRA_PRODUCT_ID = "com.fiuady.compustore.android.compustore.assemblySearchProductActivity.extraProductId";
    public static String EXTRA_IGNORE_PRODUCT_ID = "com.fiuady.compustore.android.compustore.assemblySearchProductActivity.extraIgnoreProductId";

    private static String KEY_SPINNER_POSITION = "com.fiuady.compustore.android.compustore.assemblySearchProductActivity.spinnerposition";
    private static String KEY_FLAG_RESTORESEARCH = "com.fiuady.compustore.android.compustore.assemblySearchProductActivity.restoresearch";

    private Inventory inventory;
    private RecyclerView recyclerView;
    private AssemblySearchProductActivity.ProductsAdapter adapter;

    private List<Product> products;
    private ArrayList<Integer> productsIgnoreId;
    private List<ProductCategory> productCategories;
    private Spinner spinner;
    private String allCategoryFilter;
    ArrayAdapter<String> spinnerAdapter;
    private Context context;

    private EditTextSearch searchText;
    private ImageButton searchButton;

    //private DialogProduct dialogInsertProduct;

    boolean Flag_restoreSearch;

    private static int maxQty = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        productsIgnoreId=getIntent().getIntegerArrayListExtra(EXTRA_IGNORE_PRODUCT_ID);

        this.context = this;
        //Bundle args = new Bundle();
        //args.putString(DialogCategory.ARG_TITLE, getString(R.string.dialogProduct_insert_title));
        //args.putString(DialogCategory.ARG_BTN_POSITIVE, getString(R.string.dialogProduct_insert_positivebtn));
        //args.putString(DialogCategory.ARG_BTN_NEGATIVE, getString(R.string.dialogProduct_insert_negativebtn));

        //dialogInsertProduct = DialogProduct.newInstance(dialogTagInsert, args);


        inventory = new Inventory(this);
        products = inventory.getAllProducts();
        productCategories = inventory.getAllProductCategories();

        recyclerView = (RecyclerView) findViewById(R.id.products_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allCategoryFilter = getString(R.string.all_productscategory_filter);
        ArrayList<String> arraySpinnerList = new ArrayList<String>();
        arraySpinnerList.add(allCategoryFilter);
        for (ProductCategory category : productCategories) {
            arraySpinnerList.add(category.getDescription());
        }
        String[] arraySpinnerStrings = new String[arraySpinnerList.size()];
        arraySpinnerList.toArray(arraySpinnerStrings);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arraySpinnerStrings);

        spinner = (Spinner) findViewById(R.id.spinner_filter_by_category);
        spinner.setAdapter(spinnerAdapter);


        searchText = (EditTextSearch) findViewById(R.id.edit_text_product_search);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchText.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                    searchProducts();
                    return true;
                }
                searchText.clearFocus();
                return false;
            }
        });

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(context, v);
                }
            }
        });

        searchButton = (ImageButton) findViewById(R.id.product_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusableInTouchMode(true);
                v.requestFocus();
                v.clearFocus();
                v.setFocusableInTouchMode(false);
                hideKeyboard(context, v);

                searchProducts();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SPINNER_POSITION, spinner.getSelectedItemPosition());
        outState.putBoolean(KEY_FLAG_RESTORESEARCH, Flag_restoreSearch);
        if (Flag_restoreSearch)
        {
            refreshRecyclerView();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        spinner.setSelection(savedInstanceState.getInt(KEY_SPINNER_POSITION));
        Flag_restoreSearch = savedInstanceState.getBoolean(KEY_FLAG_RESTORESEARCH);
        if (Flag_restoreSearch)
        {
            refreshRecyclerView();
        }
    }

    private void refreshRecyclerView() {
        String selectedItem = spinner.getSelectedItem().toString();
        if (selectedItem.equals(allCategoryFilter) && searchText.getText().toString().trim().equals("")) {
            products = inventory.getAllProducts();
        } else {
            if (selectedItem.equals(allCategoryFilter)) {
                products = inventory.getProductsFilterByCategory(null, searchText.getText().toString());
            }
            ProductCategory category = inventory.getProductCategoryByDescription(selectedItem);
            products = inventory.getProductsFilterByCategory(category, searchText.getText().toString());
        }

        adapter = new AssemblySearchProductActivity.ProductsAdapter(products, this);
        recyclerView.setAdapter(adapter);
        Flag_restoreSearch = true;

    }

    private void searchProducts() {
        refreshRecyclerView();
        if (products.isEmpty()) {
            Toast.makeText(AssemblySearchProductActivity.this, getString(R.string.productsActivity_search_noResults), Toast.LENGTH_LONG).show();
        }
    }

    private void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                    popup.inflate(R.menu.menu_popup_a);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.a_addToAssembly:{
                                    setResult(Activity.RESULT_OK, new Intent().putExtra(EXTRA_PRODUCT_ID, id));
                                    finish();
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

            Toast.makeText(AssemblySearchProductActivity.this, "Producto: " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
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












    private class ProductsAdapter extends RecyclerView.Adapter<AssemblySearchProductActivity.ProductHolder>
    {
        private List<Product> productsNew;
        private Context context;
        public ProductsAdapter(List<Product> products, Context context) {
            productsNew = new ArrayList<>();
            this.context = context;
            for (Product product : products)
            {
                if (!productsIgnoreId.contains(product.getId()))
                {
                    productsNew.add(product);
                }
            }
        }

        @Override
        public void onBindViewHolder(final AssemblySearchProductActivity.ProductHolder holder, final int position) {
            holder.bindProduct(productsNew.get(position));
        }


        @Override
        public AssemblySearchProductActivity.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.product_list_item, parent, false);
            return new AssemblySearchProductActivity.ProductHolder(view);
        }

        @Override
        public int getItemCount() {
            return productsNew.size();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
}