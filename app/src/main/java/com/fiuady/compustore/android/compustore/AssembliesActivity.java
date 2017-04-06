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

import java.util.List;

public class AssembliesActivity extends AppCompatActivity {
    private Inventory inventory;
    private RecyclerView recyclerView;
    private AssembliesActivity.ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assemblies);


        inventory = new Inventory(getApplicationContext());



        recyclerView=(RecyclerView)findViewById(R.id.products_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new ProductsActivity.ProductsAdapter(inventory.getAllProducts());
        recyclerView.setAdapter(adapter);

    }



    private class AssemblyHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView txtName;
        private TextView txtCategory;


        public AssemblyHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            txtName=(TextView) itemView.findViewById(R.id.assembly_name_text);
            txtCategory=(TextView)itemView.findViewById(R.id.assembly_category_text);

        }

        public void bindProduct(Product product)
        {

            txtName.setText(Assemblies.getDescription());
            txtCategory.setText(product.getProductCategory().getDescription());

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
