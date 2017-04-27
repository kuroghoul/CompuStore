package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class OrderSearchAssemblyActivity extends AppCompatActivity {
    public static String EXTRA_ASSEMBLY_ID = "com.fiuady.compustore.android.compustore.ordersearchassemblyactivity.extraAssemblyId";
    public static String EXTRA_IGNORE_ASSEMBLY_ID = "com.fiuady.compustore.android.compustore.ordersearchassemblyactivity.extraIgnoreAssemblyId";
    private Inventory inventory;
    private RecyclerView recyclerView;
    private OrderSearchAssemblyActivity.AssemblyAdapter adapter;
    private ArrayList<Assembly> assemblies;
    private ArrayList<Integer> assembliesIgnoreId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assemblies);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        assembliesIgnoreId=getIntent().getIntegerArrayListExtra(EXTRA_IGNORE_ASSEMBLY_ID);
        inventory = new Inventory(this);

        recyclerView=(RecyclerView)findViewById(R.id.assembly_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assemblies = inventory.getAllAssemblies();
        adapter = new OrderSearchAssemblyActivity.AssemblyAdapter(assemblies, this);
        recyclerView.setAdapter(adapter);
    }












    private class AssemblyHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private TextView txtDescription;
        private TextView options;
        private int id;

        public AssemblyHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            txtDescription=(TextView)itemView.findViewById(R.id.assembly_description_text);
            options=(TextView)itemView.findViewById(R.id.assembly_options);

        }

        public void bindAssembly(Assembly assembly)
        {
            id = assembly.getId();
            txtDescription.setText(assembly.getDescription());
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(OrderSearchAssemblyActivity.this, options);

                    popup.inflate(R.menu.menu_popup_a);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.a_addToAssembly:
                                    setResult(RESULT_OK, new Intent().putExtra(EXTRA_ASSEMBLY_ID, id));
                                    finish();
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
        public void onClick(View v) {
            Toast.makeText(OrderSearchAssemblyActivity.this, "Ensamble: " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
        }


        public TextView getTxtDescription() {
            return txtDescription;
        }


        public TextView getOptions() {
            return options;
        }
    }

    private class AssemblyAdapter extends RecyclerView.Adapter<OrderSearchAssemblyActivity.AssemblyHolder>
    {
        private ArrayList<Assembly> newassemblies;
        private Context context;
        public AssemblyAdapter(ArrayList<Assembly> assemblies, Context context) {
            newassemblies = new ArrayList<>();
            for(Assembly assembly : assemblies)
            {
                if (!assembliesIgnoreId.contains(assembly.getId()))
                {
                    newassemblies.add(assembly);
                }
            }
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final OrderSearchAssemblyActivity.AssemblyHolder holder, int position) {
            holder.bindAssembly(newassemblies.get(position));
        }

        @Override
        public OrderSearchAssemblyActivity.AssemblyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.assembly_list_item, parent, false);
            return new OrderSearchAssemblyActivity.AssemblyHolder(view);
        }

        @Override
        public int getItemCount() {
            return newassemblies.size();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
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
