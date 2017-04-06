package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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



public class ProductsActivity extends AppCompatActivity{

    private Inventory inventory;
    private RecyclerView recyclerView;
    private ProductsActivity.ProductsAdapter adapter;

    private List<Product> products;
    private List<ProductCategory> productCategories;
    private Spinner spinner;
    private String allCategoryFilter;
    private Context context;

    private EditTextSearch searchText;
    private ImageButton searchButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        this.context = this;

        inventory = new Inventory(this);
        products = inventory.getAllProducts();
        productCategories = inventory.getAllProductCategories();

        recyclerView=(RecyclerView)findViewById(R.id.products_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allCategoryFilter = getString(R.string.all_productscategory_filter);
        ArrayList<String> arraySpinnerList = new ArrayList<String>();
        arraySpinnerList.add(allCategoryFilter);
        for (ProductCategory category : productCategories)
        {
            arraySpinnerList.add(category.getDescription());
        }
        String[] arraySpinnerStrings = new String [arraySpinnerList.size()];
        arraySpinnerList.toArray(arraySpinnerStrings);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arraySpinnerStrings);

        spinner=(Spinner)findViewById(R.id.spinner_filter_by_category);
        spinner.setAdapter(spinnerAdapter);

        //spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //    @Override
        //    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //        String selectedItem = parent.getSelectedItem().toString();
        //        if (selectedItem == allCategoryFilter)
        //        {
        //            products = inventory.getAllProducts();
        //        }
        //        else {
        //            ProductCategory category = inventory.getProductCategoryByDescription(selectedItem);
        //            products = inventory.getProductsFilterByCategory(category);
        //        }
        //    }
//
        //    @Override
        //    public void onNothingSelected(AdapterView<?> parent) {
//
        //    }
        //});


        searchText=(EditTextSearch) findViewById(R.id.edit_text_product_search);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    searchText.clearFocus();
                    InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                    Toast.makeText(ProductsActivity.this, "HOLA!!!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                searchText.clearFocus();
                return false;
            }
        });

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    hideKeyboard(context, v);
                }
            }
        });

        searchButton=(ImageButton)findViewById(R.id.product_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusableInTouchMode(true);
                v.requestFocus();
                v.clearFocus();
                v.setFocusableInTouchMode(false);
                hideKeyboard(context, v);

                String selectedItem = spinner.getSelectedItem().toString();
                if (selectedItem == allCategoryFilter)
                {
                    products = inventory.getAllProducts();
                }
                else {
                    ProductCategory category = inventory.getProductCategoryByDescription(selectedItem);
                    products = inventory.getProductsFilterByCategory(category, searchText.getText().toString());
                }

                if(products.isEmpty())
                {
                    Toast.makeText(ProductsActivity.this, "No se encontraron coincidencias", Toast.LENGTH_LONG).show();
                }
                adapter = new ProductsActivity.ProductsAdapter(products, context);
                recyclerView.setAdapter(adapter);



            }
        });





    }
    private void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
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

        public ProductHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            txtDescription=(TextView)itemView.findViewById(R.id.product_description_text);
            txtCategory=(TextView)itemView.findViewById(R.id.product_category_text);
            txtPrice=(TextView)itemView.findViewById(R.id.product_price_text);
            txtQty=(TextView)itemView.findViewById(R.id.product_qty_text);
            options=(TextView)itemView.findViewById(R.id.product_options);
        }

        public void bindProduct(Product product)
        {

            txtDescription.setText(product.getDescription());
            txtCategory.setText(product.getProductCategory().getDescription());
            txtPrice.setText(Integer.toString(product.getPrice()));
            txtQty.setText(Integer.toString(product.getQty()));
        }

        @Override
        public void onClick(View v) {

            Toast.makeText(ProductsActivity.this, "Producto: " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
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

    private class ProductsAdapter extends RecyclerView.Adapter<ProductsActivity.ProductHolder>
    {
        private List<Product> products;
        private Context context;
        public ProductsAdapter(List<Product> products, Context context) {
            this.products = products;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final ProductsActivity.ProductHolder holder, int position) {
            holder.bindProduct(products.get(position));

            holder.getOptions().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(context, holder.getOptions());

                    popup.inflate(R.menu.menu_options_me);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    Toast.makeText(ProductsActivity.this, item.getTitle().toString() +" "+ holder.getTxtDescription().getText(), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.menu2:
                                    Toast.makeText(ProductsActivity.this, item.getTitle().toString() +" "+holder.getTxtDescription().getText(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });

        }

        @Override
        public ProductsActivity.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.product_list_item, parent, false);
            return new ProductsActivity.ProductHolder(view);
        }

        @Override
        public int getItemCount() {
            return products.size();
        }
    }


}
