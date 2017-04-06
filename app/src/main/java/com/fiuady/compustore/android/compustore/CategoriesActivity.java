package com.fiuady.compustore.android.compustore;


import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.ProductCategory;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements DialogNewCategory.DialogNewCategoryListener {
    private Inventory inventory;

    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog) {
        Toast.makeText(CategoriesActivity.this, "Aqui se agrega la categoria", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog) {
        Toast.makeText(CategoriesActivity.this, "Operación cancelada", Toast.LENGTH_SHORT).show();
    }

    private DialogNewCategory dialogNewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        inventory = new Inventory(this);
        dialogNewCategory = new DialogNewCategory();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        recyclerView=(RecyclerView)findViewById(R.id.categories_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new ProductCategoryAdapter(inventory.getAllProductCategories(), this);
        recyclerView.setAdapter(adapter);

    }



    private  class ProductCategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView txtDescription;
        private TextView txtId;
        private TextView options;
        public ProductCategoryHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            txtId=(TextView) itemView.findViewById(R.id.productcategory_id_text);
            txtDescription=(TextView)itemView.findViewById(R.id.productcategory_text);
            options= (TextView)itemView.findViewById(R.id.productcategory_options);
        }

        public void bindProductCategory(ProductCategory productCategory)
        {
            txtDescription.setText(productCategory.getDescription());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(CategoriesActivity.this, "Categoria " + txtId.getText() + " " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
        }

        public TextView getOptions()
        {
            return options;
        }

        public TextView getTxtId() {
            return txtId;
        }

        public TextView getTxtDescription() {
            return txtDescription;
        }
    }



    private class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryHolder>
    {
        private List<ProductCategory> categories;
        private Context context;
        public ProductCategoryAdapter(List<ProductCategory> categories, Context context) {
            this.categories = categories;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final ProductCategoryHolder  holder, int position) {
            holder.bindProductCategory(categories.get(position));
            holder.getTxtId().setText(Integer.toString(position+1));
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
                                    Toast.makeText(CategoriesActivity.this, item.getTitle().toString() +" "+ holder.getTxtDescription().getText(), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.menu2:
                                    Toast.makeText(CategoriesActivity.this, item.getTitle().toString() +" "+holder.getTxtDescription().getText(), Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.menu_action_addonly,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.addProductCategory)
        {
            dialogNewCategory.show(getSupportFragmentManager(), "tag");
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

}
