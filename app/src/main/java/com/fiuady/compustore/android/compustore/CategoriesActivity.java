package com.fiuady.compustore.android.compustore;


import android.content.Context;
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
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.ProductCategory;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements DialogCategory.DialogCategoryListener, DialogConfirm.DialogConfirmListener {
    private Inventory inventory;
    private DialogCategory dialogAddCategory;

    private RecyclerView recyclerView;
    private ProductCategoryAdapter adapter;
    private List<ProductCategory> categories;

    private static String dialogTagModify = "com.fiuady.compustore.android.compustore.categoriesactivity.dialogtagmodify";
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.categoriesactivity.dialogsavedataadapterposition";
    private static String dialogTagDelete = "com.fiuady.compustore.android.compustore.categoriesactivity.dialogtagdelete";
    private static String dialogTagInsert = "com.fiuady.compustore.android.compustore.categoriesactivity.dialogtaginsert";


    @Override
    public void onDialogConfirmPositiveClick(DialogFragment dialog) {

        Bundle save = ((DialogConfirm)dialog).getSavedData();
        switch (inventory.deleteProductCategory(categories.get(save.getInt(dialogSaveDataAdapterPosition))))
        {
            case AlreadyInUse:
                Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_delete_alreadyInUse), Toast.LENGTH_SHORT).show();
                break;
            case Ok:
                Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_delete_ok).replace("#category#", categories.get(save.getInt(dialogSaveDataAdapterPosition)).getDescription()), Toast.LENGTH_SHORT).show();
                refreshRecyclerView();
                break;
        }
        dialog.dismiss();
    }

    @Override
    public void onDialogConfirmNegativeClick(DialogFragment dialogFragment) {

    }

    @Override
    public void onDialogCategoryPositiveClick(String tag, android.support.v4.app.DialogFragment dialog) {
        EditText descriptionET = (EditText) dialog.getDialog().findViewById(R.id.dialog_add_category_description);
        String description = descriptionET.getText().toString();

        if(tag.equals(dialogTagInsert)){
            switch (inventory.insertProductCategory(description))
            {
                case InvalidDescription:
                    Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_insert_invalidDescription), Toast.LENGTH_SHORT).show();
                    break;
                case DuplicateDescription:
                    Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_insert_duplicateDescription), Toast.LENGTH_SHORT).show();
                    break;
                case Ok:
                    Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_insert_ok), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    refreshRecyclerView();
                    break;
            }
        }
        else if(tag.equals(dialogTagModify))
        {
            Bundle save = ((DialogCategory)dialog).getSavedData();
            ProductCategory oldCategory = categories.get(save.getInt(dialogSaveDataAdapterPosition));
            if (descriptionET.getText().toString().equals(oldCategory.getDescription()))
            {
                dialog.dismiss();
                Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_modify_noChanges), Toast.LENGTH_SHORT).show();
            }
            else {
                ProductCategory newCategory = new ProductCategory(oldCategory);
                newCategory.setDescription(descriptionET.getText().toString());
                switch (inventory.modifyProductCategory(newCategory)) {
                    case InvalidDescription:
                        Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_modify_invalidDescription), Toast.LENGTH_SHORT).show();
                        break;
                    case InvalidId:
                        Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_modify_invalidId), Toast.LENGTH_SHORT).show();
                        break;
                    case DuplicateDescription:
                        Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_modify_duplicateDescription), Toast.LENGTH_SHORT).show();
                        break;
                    case Ok:
                        Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_modify_ok), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        refreshRecyclerView();
                        break;
                }
            }
        }

    }

    @Override
    public void onDialogCategoryNegativeClick(String tag, android.support.v4.app.DialogFragment dialog) {
        //No hace falta checar cuál diálogo es, en cualquier caso queremos hacer dismiss() al dialogo abierto
        //al presionar el boton negativo/cancelar
        dialog.dismiss();
        Toast.makeText(CategoriesActivity.this, getString(R.string.categoriesActivity_dialog_cancel_dismiss), Toast.LENGTH_SHORT).show();
    }

    private void refreshRecyclerView()
    {
        categories = inventory.getAllProductCategories();
        adapter = new ProductCategoryAdapter(categories, this);
        recyclerView.setAdapter(adapter);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        inventory = new Inventory(this);
        Bundle args = new Bundle();
        args.putString(DialogCategory.ARG_TITLE, getString(R.string.dialogCategory_insert_title));
        args.putString(DialogCategory.ARG_BTN_POSITIVE, getString(R.string.dialogCategory_insert_positivebtn));
        args.putString(DialogCategory.ARG_BTN_NEGATIVE, getString(R.string.dialogCategory_insert_negativebtn));

        dialogAddCategory = DialogCategory.newInstance(dialogTagInsert, args);
        dialogAddCategory.setCancelable(false);

        //dialogAddCategory.setDialogNewCategoryListener(new DialogCategory.DialogCategoryListener() {
        //    @Override
        //    public void onDialogCategoryPositiveClick(DialogFragment dialog) {
        //        EditText descriptionET = (EditText) dialog.getDialog().findViewById(R.id.dialog_add_category_description);
        //            String description = descriptionET.getText().toString();
//
        //            switch (inventory.insertProductCategory(description))
        //            {
        //                case InvalidDescription:
        //                    Toast.makeText(CategoriesActivity.this, "Descripción no válida", Toast.LENGTH_SHORT).show();
        //                    break;
        //                case DuplicateDescription:
        //                    Toast.makeText(CategoriesActivity.this, "Ya existe una categoría con la descripción que intenta agregar", Toast.LENGTH_SHORT).show();
        //                    break;
        //                case Ok:
        //                    Toast.makeText(CategoriesActivity.this, "Categoría agregada con éxito", Toast.LENGTH_SHORT).show();
        //                    dialog.dismiss();
        //                    refreshRecyclerView();
        //                    break;
        //            }
        //    }
//
        //    @Override
        //    public void onDialogCategoryNegativeClick(DialogFragment dialog) {
        //        dialog.dismiss();
        //        Toast.makeText(CategoriesActivity.this, "Operación cancelada", Toast.LENGTH_SHORT).show();
        //    }
        //});


        recyclerView=(RecyclerView)findViewById(R.id.categories_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categories = inventory.getAllProductCategories();
        adapter = new ProductCategoryAdapter(categories, this);
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
                                case R.id.me_modify:
                                    Bundle args = new Bundle();
                                    args.putString(DialogCategory.ARG_ET_DESCRIPTION, categories.get(holder.getAdapterPosition()).getDescription());
                                    args.putString(DialogCategory.ARG_BTN_POSITIVE, getString(R.string.dialogCategory_modify_positivebtn));
                                    args.putString(DialogCategory.ARG_BTN_NEGATIVE, getString(R.string.dialogCategory_modify_negativebtn));
                                    args.putString(DialogCategory.ARG_TITLE, getString(R.string.dialogCategory_modify_title));
                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, holder.getAdapterPosition());
                                    args.putBundle(DialogCategory.ARG_SAVE_DATA, save);

                                    DialogCategory modifyCategory = DialogCategory.newInstance(dialogTagModify, args);
                                    modifyCategory.setCancelable(false);
                                    modifyCategory.show(getSupportFragmentManager(),dialogTagModify);



                                    break;
                                case R.id.me_delete:
                                {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(dialogSaveDataAdapterPosition, holder.getAdapterPosition());
                                    DialogConfirm confirmDelete = DialogConfirm.newInstance( getString(R.string.dialogConfirm_category_delete_message).replace("#category#",categories.get(holder.getAdapterPosition()).getDescription()) , getString(R.string.dialogConfirm_Category_delete_positivebtn),getString(R.string.dialogConfirm_Category_delete_negativebtn),bundle);
                                    confirmDelete.setCancelable(false);
                                    confirmDelete.show(getSupportFragmentManager(),dialogTagDelete);
                                    //alert = new AlertDialog.Builder(
                                    //        CategoriesActivity.this);
                                    //alert.setTitle("¡Atención!");
                                    //alert.setMessage("¿Está seguro de querer eliminar \""+holder.getTxtDescription().getText()+"\"?");
                                    //alert.setCancelable(false);
                                    //alert.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
//
                                    //    @Override
                                    //    public void onClick(DialogInterface dialog, int which) {
                                    //                switch (inventory.deleteProductCategory(categories.get(holder.getAdapterPosition())))
                                    //                {
                                    //                    case AlreadyInUse:
                                    //                        Toast.makeText(CategoriesActivity.this, "No se puede eliminar una categoría que está actualmente asignada a un producto", Toast.LENGTH_SHORT).show();
                                    //                        break;
                                    //                    case Ok:
                                    //                        Toast.makeText(CategoriesActivity.this, "Categoría eliminada con éxito", Toast.LENGTH_SHORT).show();
                                    //                        refreshRecyclerView();
                                    //                        break;
                                    //                }
                                    //        dialog.dismiss();
//
                                    //    }
                                    //});
                                    //alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//
                                    //    @Override
                                    //    public void onClick(DialogInterface dialog, int which) {
//
                                    //        dialog.dismiss();
                                    //    }
                                    //});
                                    //alert.show();

                                }
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
            dialogAddCategory.show(getSupportFragmentManager(), dialogTagInsert);
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
