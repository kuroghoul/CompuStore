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
import com.fiuady.compustore.db.Assembly;
import com.fiuady.compustore.db.Inventory;


import java.util.List;

public class AssembliesActivity extends AppCompatActivity {
    private Inventory inventory;
    private RecyclerView recyclerView;
    private AssembliesActivity.AssemblyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inventory = new Inventory(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assemblies);

        recyclerView=(RecyclerView)findViewById(R.id.assembly_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new AssembliesActivity.AssemblyAdapter(inventory.getAllAssemblies(), this);
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
                                case R.id.menu1:
                                    Toast.makeText(AssembliesActivity.this, item.getTitle().toString() +" "+ holder.getTxtDescription().getText(), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.menu2:
                                    Toast.makeText(AssembliesActivity.this, item.getTitle().toString() +" "+holder.getTxtDescription().getText(), Toast.LENGTH_SHORT).show();
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





















}
