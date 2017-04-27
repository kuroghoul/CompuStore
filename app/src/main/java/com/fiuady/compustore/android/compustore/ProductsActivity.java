package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.support.v4.app.DialogFragment;
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
import android.widget.EditText;
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


public class ProductsActivity extends AppCompatActivity implements DialogProduct.DialogProductListener, DialogConfirm.DialogConfirmListener, DialogNumberPicker.DialogNumberPickerListener {

    private static String dialogTagModify = "com.fiuady.compustore.android.compustore.productsactivity.dialogtagmodify";
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.productsactivity.dialogsavedataadapterposition";
    private static String dialogSaveDataLayoutPosition = "com.fiuady.compustore.android.compustore.productsactivity.dialogsavedatalayoutposition";
    private static String dialogTagDelete = "com.fiuady.compustore.android.compustore.productsactivity.dialogtagdelete";
    private static String dialogTagInsert = "com.fiuady.compustore.android.compustore.productsactivity.dialogtaginsert";
    private static String dialogTagAddStock = "com.fiuady.compustore.android.compustore.productsactivity.dialogAddStock";

    private static String KEY_SPINNER_POSITION = "com.fiuady.compustore.android.compustore.productsactivity.spinnerposition";
    private static String KEY_FLAG_RESTORESEARCH = "com.fiuady.compustore.android.compustore.productsactivity.restoresearch";

    private Inventory inventory;
    private RecyclerView recyclerView;
    private ProductsActivity.ProductsAdapter adapter;

    private List<Product> products;
    private List<ProductCategory> productCategories;
    private Spinner spinner;
    private String allCategoryFilter;
    ArrayAdapter<String> spinnerAdapter;
    private Context context;

    private EditTextSearch searchText;
    private ImageButton searchButton;

    private DialogProduct dialogInsertProduct;

    boolean Flag_restoreSearch;

    int maxQty = 9999;


    @Override
    public void onDialogConfirmPositiveClick(DialogFragment dialog) {
        Bundle save = ((DialogConfirm)dialog).getSavedData();
        int position = save.getInt(dialogSaveDataAdapterPosition);
        //int layoutPosition = save.getInt(dialogSaveDataLayoutPosition);
        switch (inventory.deleteProduct(products.get(position)))
        {
            case AlreadyInUse:
                Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_delete_alreadyInUse), Toast.LENGTH_SHORT).show();
                break;
            case Ok:
                Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_delete_ok).replace("#product#", products.get(save.getInt(dialogSaveDataAdapterPosition)).getDescription()), Toast.LENGTH_SHORT).show();
                products.remove(position);
                //recyclerView.removeViewAt(layoutPosition);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, products.size());
                //refreshRecyclerView();
                break;
        }
        dialog.dismiss();
    }

    @Override
    public void onDialogConfirmNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onDialogPositiveClick(String tag, android.support.v4.app.DialogFragment dialog) {

        EditText description = (EditText)dialog.getDialog().findViewById(R.id.dialog_add_product_description);
        EditText price = (EditText)dialog.getDialog().findViewById(R.id.dialog_add_product_price);
        Spinner categories = (Spinner)dialog.getDialog().findViewById(R.id.dialog_add_product_spinner_categories);

        //MEJORAR CUANDO INVESTIGUE COMO DAR FORMATO CURRENCY AL EDITTEXT
        //EL PLAN ES QUE EL USUARIO ESCRIBA EL PRECIO EN FLOTANTE/DOBLE
        //Y A LA FUNCIÓN InsertProduct PASARLE ESTE NÚMERO Y QUE SE ENCARGUE DE LA CONVERSIÓN A ENTERO
        //DE LA MISMA MANERA PASARIA CON getProduct, CREARÍA UN PRODUCTO CON FLOTANTE/DOBLE ENCARGANDOSE
        //DE LA CONVERSIÓN, PUES EN LA BASE DE DATOS SE MANEJA COMO ENTEROS

        //ADEMAS, ESTA LINEA ES PARA CUANDO EL USUARIO NO PONGA PRECIO, SE AGREGUE AUTOMÁTICAMENTE CON 0
        //DE ESTA MANERA EVITAMOS CREAR UN OBJETO product SIN TENER UN PRICE = NULL
        if (tag.equals(dialogTagInsert))
        {
            if(price.getText().toString().equals(""))
            {
                price.setText("0");
            }
            switch (inventory.insertProduct(description.getText().toString(),
                    Integer.valueOf(price.getText().toString()),
                    inventory.getProductCategoryByDescription(categories.getSelectedItem().toString())))
            {
                case InvalidDescription:
                    Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_insert_invalidDescription), Toast.LENGTH_SHORT).show();
                    break;
                case InvalidPrice:
                    Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_insert_invalidPrice), Toast.LENGTH_SHORT).show();
                    break;
                case InvalidCategory:
                    Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_insert_invalidCategory), Toast.LENGTH_SHORT).show();
                    break;
                case DuplicateDescription:
                    Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_insert_duplicateDescription), Toast.LENGTH_SHORT).show();
                    break;
                case Ok:
                    dialog.dismiss();
                    refreshRecyclerView();
                    Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_insert_ok), Toast.LENGTH_SHORT).show();
                    break;

            }
        }
        else if (tag.equals(dialogTagModify))
        {
            Bundle save = ((DialogProduct)dialog).getSavedData();
            Product oldProduct = products.get(save.getInt(dialogSaveDataAdapterPosition));
            if (description.getText().toString().equals(oldProduct.getDescription())
                    && price.getText().toString().equals(Integer.toString(oldProduct.getPrice()))
                    && categories.getSelectedItem().toString().equals(oldProduct.getProductCategory().getDescription()))
            {
                dialog.dismiss();
                Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_noChanges), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Product newProduct = new Product(oldProduct);
                newProduct.setDescription(description.getText().toString());
                newProduct.setPrice(Integer.parseInt(price.getText().toString()));
                newProduct.setProductCategory(inventory.getProductCategoryByDescription(categories.getSelectedItem().toString()));
                switch (inventory.modifyProduct(newProduct))
                {
                    case InvalidDescription:
                        Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_invalidDescription), Toast.LENGTH_SHORT).show();
                        break;
                    case InvalidCategory:
                        Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_invalidCategory), Toast.LENGTH_SHORT).show();
                        break;
                    case InvalidPrice:
                        Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_invalidPrice), Toast.LENGTH_SHORT).show();
                        break;
                    case InvalidId:
                        Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_invalidPrice), Toast.LENGTH_SHORT).show();
                    case DuplicateDescription:
                        Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_duplicateDescription), Toast.LENGTH_SHORT).show();
                        break;
                    case Ok:
                        dialog.dismiss();
                        refreshRecyclerView();
                        Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_ok), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

    }

    @Override
    public void onDialogNegativeClick(String tag, android.support.v4.app.DialogFragment dialog) {
        Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_dialog_cancel_dismiss) , Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onDialogNumberPickerPositiveClick(DialogFragment dialog, int value) {
        Bundle save = ((DialogNumberPicker)dialog).getSavedData();
        int position = save.getInt(dialogSaveDataAdapterPosition);
        Product newProduct = products.get(position);
        newProduct.setQty(value);


        switch (inventory.modifyProduct(newProduct))
        {
            case InvalidDescription:
                Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_invalidDescription), Toast.LENGTH_SHORT).show();
                break;
            case InvalidCategory:
                Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_invalidCategory), Toast.LENGTH_SHORT).show();
                break;
            case InvalidPrice:
                Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_invalidPrice), Toast.LENGTH_SHORT).show();
                break;
            case InvalidId:
                Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_invalidPrice), Toast.LENGTH_SHORT).show();
            case DuplicateDescription:
                Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_modify_duplicateDescription), Toast.LENGTH_SHORT).show();
                break;
            case Ok:
                dialog.dismiss();
                refreshRecyclerView();
                Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_addStock_ok), Toast.LENGTH_SHORT).show();
                break;
        }
        dialog.dismiss();
    }

    @Override
    public void onDialogNumberPickerNegativeClick(DialogFragment dialog, int value) {
        dialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.context = this;
        Bundle args = new Bundle();
        args.putString(DialogCategory.ARG_TITLE, getString(R.string.dialogProduct_insert_title));
        args.putString(DialogCategory.ARG_BTN_POSITIVE, getString(R.string.dialogProduct_insert_positivebtn));
        args.putString(DialogCategory.ARG_BTN_NEGATIVE, getString(R.string.dialogProduct_insert_negativebtn));

        dialogInsertProduct = DialogProduct.newInstance(dialogTagInsert, args);
        //dialogInsertProduct.setArguments(dialogBundle);
        //dialogInsertProduct = DialogCategory.newInstance(dialogBundle);

        //dialogInsertProduct.getBuilder().setTitle("HOLA!!!");

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
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arraySpinnerStrings);

        spinner=(Spinner)findViewById(R.id.spinner_filter_by_category);
        spinner.setAdapter(spinnerAdapter);


        searchText=(EditTextSearch) findViewById(R.id.edit_text_product_search);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    searchText.clearFocus();
                    InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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

                searchProducts();
            }
        });





    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SPINNER_POSITION, spinner.getSelectedItemPosition());
        outState.putBoolean(KEY_FLAG_RESTORESEARCH, Flag_restoreSearch);

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

    private void refreshRecyclerView ()
    {
        String selectedItem = spinner.getSelectedItem().toString();
        if (selectedItem.equals(allCategoryFilter) && searchText.getText().toString().trim().equals(""))
        {
            products = inventory.getAllProducts();
        }
        else {
            if (selectedItem.equals(allCategoryFilter)) {
                products = inventory.getProductsFilterByCategory(null, searchText.getText().toString());
            }
            ProductCategory category = inventory.getProductCategoryByDescription(selectedItem);
            products = inventory.getProductsFilterByCategory(category, searchText.getText().toString());
        }


        adapter = new ProductsActivity.ProductsAdapter(products, context);
        recyclerView.setAdapter(adapter);
        Flag_restoreSearch = true;

    }
    private void searchProducts()
    {
        refreshRecyclerView();
        if(products.isEmpty())
        {
            Toast.makeText(ProductsActivity.this, getString(R.string.productsActivity_search_noResults), Toast.LENGTH_LONG).show();
        }
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

                    popup.inflate(R.menu.menu_popup_ame);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.ame_addStock:{
                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());

                                    String message = getString(R.string.dialogNumberPicker_products_addStock_message);
                                    String positivetxt = getString(R.string.dialogNumberPicker_products_addStock_positivebtn);
                                    String negativetxt = getString(R.string.dialogNumberPicker_products_addStock_negativebtn);
                                    int min = products.get(getAdapterPosition()).getQty();
                                    int max = maxQty;


                                    DialogNumberPicker dialogNumberPicker = DialogNumberPicker.newInstance(message, positivetxt, negativetxt, min, max, save);
                                    dialogNumberPicker.show(getSupportFragmentManager(),dialogTagAddStock);
                                    break;}
                                case R.id.ame_modify:{
                                    Bundle args = new Bundle();
                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());

                                    args.putString(DialogProduct.ARG_ET_DESCRIPTION,products.get(getAdapterPosition()).getDescription());
                                    args.putString(DialogProduct.ARG_SP_CATEGORY,products.get(getAdapterPosition()).getProductCategory().getDescription());
                                    args.putString(DialogProduct.ARG_ET_PRICE,Integer.toString(products.get(getAdapterPosition()).getPrice()));
                                    args.putString(DialogProduct.ARG_TITLE,getString(R.string.dialogProduct_modify_title));
                                    args.putString(DialogProduct.ARG_BTN_POSITIVE,getString(R.string.dialogCategory_modify_positivebtn));
                                    args.putString(DialogProduct.ARG_BTN_NEGATIVE,getString(R.string.dialogProduct_modify_negativebtn));
                                    args.putBundle(DialogProduct.ARG_SAVE_DATA, save);

                                    DialogProduct modifyProduct = DialogProduct.newInstance(dialogTagModify, args);
                                    modifyProduct.show(getSupportFragmentManager(), dialogTagModify);
                                    break;}
                                case R.id.ame_delete: {
                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());
                                    save.putInt(dialogSaveDataLayoutPosition, getLayoutPosition());
                                    DialogConfirm confirmDelete = DialogConfirm.newInstance(getString(R.string.dialogConfirm_products_delete_title).replace("#product#", products.get(getAdapterPosition()).getDescription()), getString(R.string.dialogConfirm_products_delete_positivebtn), getString(R.string.dialogConfirm_products_delete_negativebtn), save);
                                    confirmDelete.show(getSupportFragmentManager(), dialogTagDelete);
                                    break;
                                }
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

            Toast.makeText(ProductsActivity.this, "Producto: " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
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

    private class ProductsAdapter extends RecyclerView.Adapter<ProductsActivity.ProductHolder>
    {
        private List<Product> products;
        private Context context;
        public ProductsAdapter(List<Product> products, Context context) {
            this.products = products;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final ProductsActivity.ProductHolder holder, final int position) {
            holder.bindProduct(products.get(position));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_addonly,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()== R.id.addItemToDb)
        {
            dialogInsertProduct.show(getSupportFragmentManager(), dialogTagInsert);
            return true;
        }
        else if(item.getItemId()==android.R.id.home)
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
