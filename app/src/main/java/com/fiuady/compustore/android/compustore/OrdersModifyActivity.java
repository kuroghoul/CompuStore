package com.fiuady.compustore.android.compustore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Assembly;
import com.fiuady.compustore.db.Customer;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Order;
import com.fiuady.compustore.db.OrderAssemblies;

import java.util.ArrayList;

public class OrdersModifyActivity extends AppCompatActivity implements DialogNumberPicker.DialogNumberPickerListener, DialogConfirm.DialogConfirmListener {
    public static String EXTRA_ORDER_ID = "com.fiuady.compustore.android.compustore.ordersmodifyactivity.extraOrderId";
    private int extraOrderId;
    private Intent intent;
    private Inventory inventory;
    private Order order;



    public static final int CODE_SEARCH_ASSEMBLY = 1;
    private static String KEY_ASSEMBLY_LIST_ID = "com.fiuady.compustore.android.compustore.ordersmodifyactivity.AssemblyListId";
    private static String KEY_ASSEMBLY_LIST_QTY = "com.fiuady.compustore.android.compustore.ordersmodifyactivity.AssemblyListQty";
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.ordersmodifyactivity.dialogsavedataadapterposition";
    private static String dialogTagDelete = "com.fiuady.compustore.android.compustore.ordersmodifyactivity.dialogtagdelete";
    private static String dialogTagQty = "com.fiuady.compustore.android.compustore.ordersmodifyactivity.dialogQty";

    private RecyclerView recyclerView;
    private OrdersModifyActivity.AssemblyAdapter adapter;

    private Spinner spinnerC;
    private ArrayList<Customer> customers;
    private Customer currentCustomer;
    private ArrayList<Integer> customersId;
    private ArrayAdapter<String> spinnerCAdapter;


    private ArrayList<OrderAssemblies> orderAssemblies;


    private ArrayList<Integer> assemblyListId;
    private ArrayList<Assembly> assemblyList;
    private ArrayList<Integer> assembliesQty;

    FloatingActionButton fab;
    private static int maxQty = 999;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_insert);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        intent = getIntent();
        extraOrderId = intent.getIntExtra(EXTRA_ORDER_ID,-1);
        if(extraOrderId!=-1)
        {
            inventory = new Inventory(this);
            order = inventory.getOrderById(extraOrderId);


            assemblyList = new ArrayList<Assembly>();
            assemblyListId = new ArrayList<Integer>();
            assembliesQty = new ArrayList<Integer>();
            orderAssemblies = inventory.getOrderAssembliesById(order.getId());

            for(OrderAssemblies orderAssembly : orderAssemblies)
            {
                assemblyList.add(inventory.getAssemblyById(orderAssembly.getAssembly().getId()));
                assemblyListId.add(orderAssembly.getAssembly().getId());
                assembliesQty.add(orderAssembly.getQty());
            }





            customers = inventory.getAllCustomers();
            ArrayList<String> arraySpinnerList = new ArrayList<String>();
            for (Customer customer : customers)
            {
                arraySpinnerList.add(customer.getFullName());
            }
            String[] arraySpinnerStrings = new String [arraySpinnerList.size()];
            arraySpinnerList.toArray(arraySpinnerStrings);
            spinnerCAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_small, arraySpinnerStrings);


            spinnerC = (Spinner)findViewById(R.id.orders_insert_spinner_customers);
            spinnerC.setAdapter(spinnerCAdapter);
            spinnerC.setEnabled(false);
            currentCustomer=order.getCustomer();

            spinnerC.setSelection(spinnerCAdapter.getPosition(order.getCustomer().getFullName()));

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(OrdersModifyActivity.this, OrderSearchAssemblyActivity.class);
                    intent.putIntegerArrayListExtra(OrderSearchAssemblyActivity.EXTRA_IGNORE_ASSEMBLY_ID, assemblyListId);
                    startActivityForResult(intent, CODE_SEARCH_ASSEMBLY);
                }
            });

            recyclerView=(RecyclerView)findViewById(R.id.assemblyProducts_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AssemblyAdapter(assemblyList, assembliesQty, this);
            recyclerView.setAdapter(adapter);

        }
        else
        {
            finish();
        }

    }

























    private class OrderAssemblyHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView txtDescription;
        private TextView txtQty;
        private TextView options;

        public OrderAssemblyHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            txtDescription=(TextView)itemView.findViewById(R.id.assembly_list_order_item_description);
            txtQty=(TextView)itemView.findViewById(R.id.assembly_list_order_item_qty);
            options=(TextView)itemView.findViewById(R.id.assembly_list_order_item_options);
        }

        public void bindOrderAssembly(Assembly assembly, int qty)
        {
            txtDescription.setText(assembly.getDescription());
            txtQty.setText(Integer.toString(qty));
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(OrdersModifyActivity.this, v);

                    popup.inflate(R.menu.menu_options_me);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.me_modify:{
                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());
                                    String message = "Seleccionar cantidad";
                                    String positivetxt = "Guardar";
                                    String negativetxt = "Cancelar";
                                    int min = 1;
                                    int max = maxQty;


                                    DialogNumberPicker dialogNumberPicker = DialogNumberPicker.newInstance(message, positivetxt, negativetxt, min, max, save);
                                    dialogNumberPicker.show(getSupportFragmentManager(),dialogTagQty);
                                    break;}
                                case R.id.me_delete:{

                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());
                                    DialogConfirm dialogConfirm = DialogConfirm.newInstance( "Eliminar ensamble", "Eliminar", "Cancelar", save);
                                    dialogConfirm.show(getSupportFragmentManager(), dialogTagDelete);
                                    break;}
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
            Toast.makeText(OrdersModifyActivity.this, "Ensamble: " + txtDescription.getText(), Toast.LENGTH_SHORT).show();
        }

        public TextView getTxtDescription() {
            return txtDescription;
        }


        public TextView getOptions() {
            return options;
        }
    }




    private class AssemblyAdapter extends RecyclerView.Adapter<OrdersModifyActivity.OrderAssemblyHolder>
    {
        private ArrayList<Assembly> assemblies;
        private ArrayList<Integer> qtys;
        private Context context;

        public AssemblyAdapter(ArrayList<Assembly> assemblies, ArrayList<Integer> qtys, Context context) {
            this.assemblies = assemblies;
            this.qtys = qtys;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final OrdersModifyActivity.OrderAssemblyHolder holder, int position) {
            holder.bindOrderAssembly(assemblies.get(position), qtys.get(position));
        }

        @Override
        public OrdersModifyActivity.OrderAssemblyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.assembly_list_orders_item, parent, false);
            return new OrdersModifyActivity.OrderAssemblyHolder(view);
        }

        @Override
        public int getItemCount() {
            return assemblies.size();
        }
    }













    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //ArrayList<Integer> newAssemblyListId = new ArrayList<>();
        //for (Assembly assembly1: assemblyList)
        //{
        //    newAssemblyListId.add(assembly1.getId());
        //}
        outState.putIntegerArrayList(KEY_ASSEMBLY_LIST_ID, assemblyListId);
        outState.putIntegerArrayList(KEY_ASSEMBLY_LIST_QTY, assembliesQty);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        assemblyList.clear();
        //assemblyListId.clear();
        //assembliesQty.clear();
        assemblyListId = savedInstanceState.getIntegerArrayList(KEY_ASSEMBLY_LIST_ID);
        assembliesQty = savedInstanceState.getIntegerArrayList(KEY_ASSEMBLY_LIST_QTY);
        for(Integer id : assemblyListId)
        {
            assemblyList.add(inventory.getAssemblyById(id));
        }
        adapter = new AssemblyAdapter(assemblyList, assembliesQty, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CODE_SEARCH_ASSEMBLY && resultCode== Activity.RESULT_OK)
        {
            int id = data.getIntExtra(OrderSearchAssemblyActivity.EXTRA_ASSEMBLY_ID, -1);
            if ( id!= -1)
            {
                assemblyList.add(inventory.getAssemblyById(id));
                assemblyListId.add(id);
                assembliesQty.add(1); //Qty by default
                adapter.notifyDataSetChanged();

            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_orders_insert,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.orders_menu_insert_save)
        {
            ArrayList<OrderAssemblies> newOrderAssemblies = new ArrayList<OrderAssemblies>();
            for(int i=0; i<assemblyList.size();i++)
            {
                newOrderAssemblies.add(new OrderAssemblies(order, assemblyList.get(i),assembliesQty.get(i)));
            }
            switch (inventory.modifyOrder(order, newOrderAssemblies))
            {
                case InvalidOrderStatus:
                    Toast.makeText(OrdersModifyActivity.this, "Order Status no válido", Toast.LENGTH_SHORT).show();
                    break;
                case InvalidOrderAssemblies:
                    Toast.makeText(OrdersModifyActivity.this, "Lista de ensambles inválida " , Toast.LENGTH_SHORT).show();
                    break;
                case Ok:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
            return true;
        }
        else if(item.getItemId()==android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDialogNumberPickerPositiveClick(DialogFragment dialog, int value) {
        Bundle save = ((DialogNumberPicker)dialog).getSavedData();
        int position = save.getInt(dialogSaveDataAdapterPosition);
        assembliesQty.set(position,value);
        adapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    @Override
    public void onDialogNumberPickerNegativeClick(DialogFragment dialog, int value) {
        dialog.dismiss();
    }

    @Override
    public void onDialogConfirmPositiveClick(DialogFragment dialog) {

        Bundle save = ((DialogConfirm)dialog).getSavedData();
        int position = save.getInt(dialogSaveDataAdapterPosition);
        assemblyList.remove(position);
        assemblyListId.remove(position);
        assembliesQty.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, assemblyList.size());
        Toast.makeText(OrdersModifyActivity.this, "Ensamble eliminado", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onDialogConfirmNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
