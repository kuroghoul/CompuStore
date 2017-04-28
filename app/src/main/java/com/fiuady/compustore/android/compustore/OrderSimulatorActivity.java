package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Customer;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Order;
import com.fiuady.compustore.db.OrderStatus;
import com.fiuady.compustore.db.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrderSimulatorActivity extends AppCompatActivity {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.processByCustomerActivity.dialogsavedataadapterposition";
    static String dialogProductListTag = "com.fiuady.compustore.android.compustore.productsactivity.dialogProductListTag";

    private RecyclerView recyclerView;
    private OrderSimulatorActivity.OrderAdapter adapter;

    private Spinner spinnerC;
    private ArrayList<Customer> customers;
    private ArrayAdapter<String> spinnerCAdapter;
    private ArrayList<Order> ordersList;
    private ArrayList<OrderStatus> pendiente;

    private Spinner spinnerSort;
    private final static String[] spinnerSortStrings = {"Reciente", "Antiguo"};
    private Inventory.SortDB selectedSort;


    private Inventory inventory;

    private Button simulateBtn;
    private Button clearBtn;
    private RadioGroup filterGroup;
    private RadioButton customersRadBtn;
    private RadioButton dateRadBtn;
    private RadioButton totalSaleRadBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_simulator);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        inventory = new Inventory(this);
        pendiente = new ArrayList<OrderStatus>();
        pendiente.add(inventory.getOrderStatusById(0));
        simulateBtn = (Button) findViewById(R.id.orderSimulatorActivity_simulate_button);
        clearBtn = (Button) findViewById(R.id.orderSimulatorActivity_clear_button);
        selectedSort = Inventory.SortDB.Ascending;
        filterGroup = (RadioGroup)findViewById(R.id.orderSimulatorActivity_filter_radioGroup);
        customersRadBtn = (RadioButton)findViewById(R.id.orderSimulatorActivity_customers_radio);
        dateRadBtn = (RadioButton)findViewById(R.id.orderSimulatorActivity_date_radio);
        totalSaleRadBtn = (RadioButton)findViewById(R.id.orderSimulatorActivity_totalSale_radio);


        customers = inventory.getAllCustomers();
        ArrayList<String> arraySpinnerList = new ArrayList<String>();
        for (Customer customer : customers)
        {
            arraySpinnerList.add(customer.getFullName());
        }
        String[] arraySpinnerStrings = new String [arraySpinnerList.size()];
        arraySpinnerList.toArray(arraySpinnerStrings);
        spinnerCAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_small, arraySpinnerStrings);



        spinnerC = (Spinner)findViewById(R.id.orderSimulatorActivity_customers_spinner);
        spinnerC.setAdapter(spinnerCAdapter);

        spinnerSort = (Spinner)findViewById(R.id.orderSimulatorActivity_sort_spinner);
        spinnerSort.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item_small, spinnerSortStrings));

        ordersList = inventory.getOrdersByCustomerAndOrderStatus2(customers.get(spinnerC.getSelectedItemPosition()), pendiente, null, null, Inventory.SortDB.Descending);
        recyclerView=(RecyclerView)findViewById(R.id.orderSimulatorActivity_orders_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(ordersList, this);
        recyclerView.setAdapter(adapter);




    simulateBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (filterGroup.getCheckedRadioButtonId())
            {
                case R.id.orderSimulatorActivity_customers_radio:{
                    ordersList = inventory.getOrdersByCustomerAndOrderStatus2(customers.get(spinnerC.getSelectedItemPosition()), pendiente, null, null, null);
                    adapter = new OrderAdapter(ordersList, OrderSimulatorActivity.this);
                    recyclerView.setAdapter(adapter);
                    break;}
                case R.id.orderSimulatorActivity_date_radio:{
                    ordersList = inventory.getOrdersByOrderStatus(inventory.getOrderStatusById(0), selectedSort);
                    adapter = new OrderAdapter(ordersList, OrderSimulatorActivity.this);
                    recyclerView.setAdapter(adapter);
                    break;}
                case R.id.orderSimulatorActivity_totalSale_radio:{
                    spinnerC.setEnabled(false);
                    spinnerSort.setEnabled(false);
                    break;}
            }
        }
    });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItem().equals("Antiguo"))
                {
                    selectedSort = Inventory.SortDB.Ascending;
                }
                else if(parent.getSelectedItem().equals("Reciente"))
                {
                    selectedSort = Inventory.SortDB.Descending;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        filterGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.orderSimulatorActivity_customers_radio:{
                        spinnerC.setEnabled(true);
                        spinnerSort.setEnabled(false);

                        break;}
                    case R.id.orderSimulatorActivity_date_radio:{
                        spinnerC.setEnabled(false);
                        spinnerSort.setEnabled(true);
                        break;}
                    case R.id.orderSimulatorActivity_totalSale_radio:{
                        spinnerC.setEnabled(false);
                        spinnerSort.setEnabled(false);
                        break;}
                }
            }
        });
    clearBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ordersList.clear();
            adapter.notifyDataSetChanged();
        }
    });
        filterGroup.check(R.id.orderSimulatorActivity_customers_radio);

    }














    private class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private TextView txtClient;
        private TextView txtStatus;
        private TextView txtDate;
        private Order order;



        public OrderHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            txtClient=(TextView)itemView.findViewById(R.id.order_client_text);
            txtStatus=(TextView)itemView.findViewById(R.id.order_status_text);
            txtDate=(TextView)itemView.findViewById(R.id.order_date_text);


        }

        public void bindOrder(Order order)
        {
            this.order = order;
            txtClient.setText(order.getCustomer().getFullName());
            txtStatus.setText(order.getOrderStatus().getDescription());
            txtDate.setText(formatter.format(order.getCalendar().getTime()));
        }


        @Override
        public void onClick(View v) {

            ArrayList<Integer> productsIdList = new ArrayList<Integer>();
            for (Product product :inventory.getAllProducts())
            {
                productsIdList.add(product.getId());
            }

            Bundle args = new Bundle();
            Bundle save = new Bundle();
            save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());

            args.putString(DialogProductList.ARG_TITLE,"Productos");
            args.putString(DialogProductList.ARG_BTN_POSITIVE,"Ok");
            args.putBundle(DialogProductList.ARG_SAVE_DATA, save);
            args.putIntegerArrayList(DialogProductList.ARG_PRODUCT_ID_LIST, productsIdList);

            DialogProductList dialogProductList = DialogProductList.newInstance(dialogProductListTag, args);
            dialogProductList.show(getSupportFragmentManager(), dialogProductListTag);
        }

        public TextView getTxtClient() {
            return txtClient;
        }

        public Order getOrder() {
            return order;
        }
    }



    private class OrderAdapter extends RecyclerView.Adapter<OrderSimulatorActivity.OrderHolder> {
        private ArrayList<Order> orders1;
        private Context context;

        public OrderAdapter(ArrayList<Order> orders, Context context) {
            this.orders1 = orders;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final OrderSimulatorActivity.OrderHolder holder, int position) {
            holder.bindOrder(orders1.get(position));

        }

        @Override
        public OrderSimulatorActivity.OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.order_list_item_simple, parent, false);
            return new OrderSimulatorActivity.OrderHolder(view);
        }

        @Override
        public int getItemCount() {
            return orders1.size();
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
