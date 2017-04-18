package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Assembly;
import com.fiuady.compustore.db.Inventory;


import java.util.List;

public class AssembliesActivity extends AppCompatActivity implements DialogConfirm.DialogConfirmListener {

    private static String dialogTagModify = "com.fiuady.compustore.android.compustore.assembliesactivity.dialogtagmodify";
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.assembliesactivity.dialogsavedataadapterposition";
    private static String dialogTagDelete = "com.fiuady.compustore.android.compustore.assembliesactivity.dialogtagdelete";
    private static String dialogTagInsert = "com.fiuady.compustore.android.compustore.assembliesactivity.dialogtaginsert";
    private static String dialogTagAddStock = "com.fiuady.compustore.android.compustore.assembliesactivity.dialogAddStock";


    private Inventory inventory;
    private RecyclerView recyclerView;
    List<Assembly> assemblies;
    private AssembliesActivity.AssemblyAdapter adapter;

    @Override
    public void onDialogConfirmPositiveClick(DialogFragment dialog) {
        Bundle save = ((DialogConfirm)dialog).getSavedData();
        switch (inventory.deleteAssembly(assemblies.get(save.getInt(dialogSaveDataAdapterPosition))))
        {
            case AlreadyInUse:
                Toast.makeText(AssembliesActivity.this, getString(R.string.assembliesActivity_delete_alreadyInUse), Toast.LENGTH_SHORT).show();
                break;
            case Ok:
                Toast.makeText(AssembliesActivity.this, getString(R.string.assembliesActivity_delete_ok).replace("#assembly#", assemblies.get(save.getInt(dialogSaveDataAdapterPosition)).getDescription()), Toast.LENGTH_SHORT).show();
                //refreshRecyclerView();
                break;
        }
        dialog.dismiss();
    }

    @Override
    public void onDialogConfirmNegativeClick(DialogFragment dialog) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inventory = new Inventory(this);
        setContentView(R.layout.activity_assemblies);

        recyclerView=(RecyclerView)findViewById(R.id.assembly_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        assemblies = inventory.getAllAssemblies();
        adapter = new AssembliesActivity.AssemblyAdapter(assemblies, this);
        recyclerView.setAdapter(adapter);

    }



    private class AssemblyHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private TextView txtDescription;
        private TextView options;

        public AssemblyHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            txtDescription=(TextView)itemView.findViewById(R.id.assembly_description_text);
            options=(TextView)itemView.findViewById(R.id.assembly_options);

        }

        public void bindAssembly(Assembly assembly)
        {

            txtDescription.setText(assembly.getDescription());



        }

        @Override
        public void onClick(View v) {
            Toast.makeText(AssembliesActivity.this, "Ensamble: " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
        }


        public TextView getTxtDescription() {
            return txtDescription;
        }


        public TextView getOptions() {
            return options;
        }
    }

    private class AssemblyAdapter extends RecyclerView.Adapter<AssembliesActivity.AssemblyHolder>
    {
        private List<Assembly> assemblies;
        private Context context;
        public AssemblyAdapter(List<Assembly> assemblies, Context context) {
            this.assemblies = assemblies;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final AssembliesActivity.AssemblyHolder holder, int position) {
            holder.bindAssembly(assemblies.get(position));

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
                                    Toast.makeText(AssembliesActivity.this, item.getTitle().toString() +" "+ holder.getTxtDescription().getText(), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.me_delete:

                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, holder.getAdapterPosition());
                                    DialogConfirm dialogConfirm = DialogConfirm.newInstance( "Eliminar ensamble", "Eliminar", "Cancelar", save);
                                    dialogConfirm.show(getSupportFragmentManager(), dialogTagDelete);
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
        public AssembliesActivity.AssemblyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.assembly_list_item, parent, false);
            return new AssembliesActivity.AssemblyHolder(view);
        }

        @Override
        public int getItemCount() {
            return assemblies.size();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_assemblies,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()== R.id.assembly_menu_insert)
        {
            Intent intent = new Intent(this, AssemblyInsertActivity.class);
            startActivity(intent);

            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

}
