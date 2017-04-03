package com.fiuady.compustore.android.compustore;

import android.app.DialogFragment;
import android.app.FragmentManager;
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

import org.w3c.dom.Text;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements DialogNewCategory.DialogNewCategoryListener {

    private Inventory inventory;




    private DialogNewCategory dialogNewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inventory = new Inventory(getApplicationContext());
        dialogNewCategory = new DialogNewCategory();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        recyclerView=(RecyclerView)findViewById(R.id.categories_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new ProductCategoryAdapter(inventory.getAllProductCategories());
        recyclerView.setAdapter(adapter);

    }
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Toast.makeText(CategoriesActivity.this, "Aqui se agrega la categoria", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(CategoriesActivity.this, "Operación cancelada", Toast.LENGTH_SHORT).show();
    }

    private class ProductCategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView txtDescription;
        private TextView txtId;
        public ProductCategoryHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            txtId=(TextView) itemView.findViewById(R.id.productcategory_id_text);
            txtDescription=(TextView)itemView.findViewById(R.id.productcategory_text);
        }

        public void bindProductCategory(ProductCategories productCategory)
        {
            txtId.setText(Integer.toString(productCategory.getId()));
            txtDescription.setText(productCategory.getDescription());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(CategoriesActivity.this, "Categoria " + txtId.getText() + " " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_categories,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.addProductCategory)
        {

            //Toast.makeText(CategoriesActivity.this, "Add Category", Toast.LENGTH_SHORT).show();
            dialogNewCategory.show(getFragmentManager(), "tag");
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

}
