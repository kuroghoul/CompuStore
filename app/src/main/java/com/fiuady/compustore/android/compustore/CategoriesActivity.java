package com.fiuady.compustore.android.compustore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.ProductCategories;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private Inventory inventory;

    private class ProductCategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView txtDescription;
        public ProductCategoryHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            txtDescription=(TextView)itemView.findViewById(R.id.productcategory_text);
        }

        public void bindProductCategory(ProductCategories productCategory)
        {
            txtDescription.setText(productCategory.getDescription());
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryHolder>
    {
        private List<ProductCategories> categories;
        public ProductCategoryAdapter(List<ProductCategories> categories) {
            this.categories = categories;
        }

        @Override
        public void onBindViewHolder(ProductCategoryHolder holder, int position) {
            holder.bindProductCategory(categories.get(position));
        }

        @Override
        public ProductCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.productcategory_list_item, parent, false);
            return new ProductCategoryHolder(view);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }
    }


    private RecyclerView recyclerView;
    private ProductCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        recyclerView=(RecyclerView)findViewById(R.id.categories_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inventory = new Inventory(getApplicationContext());

        adapter = new ProductCategoryAdapter(inventory.getAllProductCategories());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_categories,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.addProductCategory)
        {
            Toast.makeText(CategoriesActivity.this, "Add Category", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

}
