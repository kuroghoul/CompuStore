package com.fiuady.compustore.android.compustore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Product;
import com.fiuady.compustore.db.ProductCategory;

import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private Inventory inventory;
    private RecyclerView recyclerView;
    private ProductsActivity.ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);


        inventory = new Inventory(getApplicationContext());



        recyclerView=(RecyclerView)findViewById(R.id.products_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new ProductsActivity.ProductsAdapter(inventory.getAllProducts());
        recyclerView.setAdapter(adapter);

    }






    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView txtDescription;
        private TextView txtCategory;
        private TextView txtPrice;
        private TextView txtQty;

        public ProductHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            txtDescription=(TextView) itemView.findViewById(R.id.product_description_text);
            txtCategory=(TextView)itemView.findViewById(R.id.product_category_text);
            txtPrice=(TextView)itemView.findViewById(R.id.product_price_text);
            txtQty=(TextView)itemView.findViewById(R.id.product_qty_text);
        }

        public void bindProduct(Product product)
        {

            txtDescription.setText(product.getDescription());
            txtCategory.setText(product.getProductCategory().getDescription());
            txtPrice.setText(product.getPrice());
            txtQty.setText(product.getQty());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(ProductsActivity.this, "Producto: " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    private class ProductsAdapter extends RecyclerView.Adapter<ProductsActivity.ProductHolder>
    {
        private List<Product> products;
        public ProductsAdapter(List<Product> products) {
            this.products = products;
        }

        @Override
        public void onBindViewHolder(ProductsActivity.ProductHolder holder, int position) {
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


}
