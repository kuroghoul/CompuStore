package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Product;


import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private Inventory inventory;
    private RecyclerView recyclerView;
    private ProductsActivity.ProductsAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        inventory = new Inventory(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        recyclerView=(RecyclerView)findViewById(R.id.products_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new ProductsActivity.ProductsAdapter(inventory.getAllProducts(), this);
        recyclerView.setAdapter(adapter);

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
