package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Customer;
import com.fiuady.compustore.db.Order;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.OrderStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrdersActivity extends AppCompatActivity implements DialogDatePicker.DialogDateListener, DialogChangeOrderStatus.DialogChangeOrderStatusListener{

    private static String dialogTagStateBack= "com.fiuady.compustore.android.compustore.ordersactivity.dialogStateBack";
    private static String dialogTagStateForward= "com.fiuady.compustore.android.compustore.ordersactivity.dialogStateForward";

    private Inventory inventory;
    private RecyclerView recyclerView;
    private OrdersActivity.OrderAdapter adapter;
    private ArrayList<Order> orders;

    private TextView textViewFromTag;
    private TextView textViewFromDate;
    private Calendar calendarFromDate;
    private TextView textViewUntilTag;
    private TextView textViewUntilDate;
    private Calendar calendarUntilDate;
    private CheckBox calendarCheckbox;
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

    private Spinner spinnerOS;
    private CustomSpinnerAdapter spinnerOSAdapter;
    private ArrayList<OrderStatus> orderStatusList;
    private ArrayList<StateVO> orderStatusVOList;

    private Spinner spinnerC;
    private ArrayList<Customer> customers;
    private ArrayAdapter<String> spinnerCAdapter;

    private Spinner spinnerSort;
    private final static String[] spinnerSortStrings = {"Reciente", "Antiguo"};
    private Inventory.SortDB selectedSort;

    private static String dialogTagDelete = "com.fiuady.compustore.android.compustore.ordersactivity.dialogtagdelete";
    private static String dialogDateTagFrom = "com.fiuady.compustore.android.compustore.ordersactivity.dialogDateTagFrom";
    private static String dialogDateTagUntil = "com.fiuady.compustore.android.compustore.ordersactivity.dialogDateTagUntil";
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.ordersactivity.dialogsavedataadapterposition";

    private static String KEY_SPINNER_BOOLEAN = "com.fiuady.compustore.android.compustore.ordersactivity.spinnerboolean";
    private static String KEY_CALENDAR_FROM = "com.fiuady.compustore.android.compustore.ordersactivity.calendarfrom";
    private static String KEY_CALENDAR_UNTIL = "com.fiuady.compustore.android.compustore.ordersactivity.calendaruntil";
    public static int CODE_INSERT_ORDER = 1;
    public static int CODE_MODIFY_ORDER = 2;

    private  Order newestOrder;
    private  Order oldestOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inventory = new Inventory(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        calendarCheckbox = (CheckBox)findViewById(R.id.ordersactivity_datepicker_chkbox);
        textViewFromTag = (TextView)findViewById(R.id.ordersactivity_datepicker_fromTag);
        textViewFromDate = (TextView)findViewById(R.id.ordersactivity_datepicker_fromDate);
        textViewUntilTag = (TextView)findViewById(R.id.ordersactivity_datepicker_untilTag);
        textViewUntilDate = (TextView)findViewById(R.id.ordersactivity_datepicker_untilDate);
        selectedSort = Inventory.SortDB.Ascending;
        newestOrder = inventory.getNewestOrder();
        oldestOrder = inventory.getOldestOrder();
        calendarCheckbox.setChecked(true);
        if(oldestOrder!=null)
        {
            if(calendarFromDate==null)
            {
                calendarFromDate = (Calendar)oldestOrder.getCalendar().clone();
            }
            textViewFromDate.setText(formatter.format(calendarFromDate.getTime()));
            textViewFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Bundle minmaxDate = new Bundle();
                    minmaxDate.putSerializable(DialogDatePicker.MIN_DATE, (Calendar)oldestOrder.getCalendar().clone());
                    minmaxDate.putSerializable(DialogDatePicker.MAX_DATE, calendarUntilDate);

                    DialogDatePicker dialogDatePicker = DialogDatePicker.newInstance(dialogDateTagFrom,calendarFromDate.get(Calendar.YEAR), calendarFromDate.get(Calendar.MONTH), calendarFromDate.get(Calendar.DAY_OF_MONTH), minmaxDate);
                    dialogDatePicker.show(getSupportFragmentManager(), "oldestDatePicker");
                }
            });
        }
        if(newestOrder!=null)
        {
            if(calendarUntilDate==null)
            {
                calendarUntilDate = (Calendar)newestOrder.getCalendar().clone();
            }
            textViewUntilDate.setText(formatter.format(calendarUntilDate.getTime()));
            textViewUntilDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bundle minmaxDate = new Bundle();
                    minmaxDate.putSerializable(DialogDatePicker.MIN_DATE, calendarFromDate);
                    minmaxDate.putSerializable(DialogDatePicker.MAX_DATE, (Calendar)newestOrder.getCalendar().clone());


                    DialogDatePicker dialogDatePicker = DialogDatePicker.newInstance(dialogDateTagUntil,calendarUntilDate.get(Calendar.YEAR), calendarUntilDate.get(Calendar.MONTH), calendarUntilDate.get(Calendar.DAY_OF_MONTH), minmaxDate);
                    dialogDatePicker.show(getSupportFragmentManager(), "newesttDatePicker");
                }
            });
        }





        calendarCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                {
                    textViewFromDate.setEnabled(false);
                    textViewUntilDate.setEnabled(false);
                    textViewFromTag.setEnabled(false);
                    textViewUntilTag.setEnabled(false);
                }
                else
                {
                    textViewFromDate.setEnabled(true);
                    textViewUntilDate.setEnabled(true);
                    textViewFromTag.setEnabled(true);
                    textViewUntilTag.setEnabled(true);
                }
                searchOrders();
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.order_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        orders = inventory.getAllOrders();
        orderStatusList = inventory.getAllOrderStatus();
        adapter = new OrdersActivity.OrderAdapter(orders, this);
        recyclerView.setAdapter(adapter);



        orderStatusVOList = new ArrayList<StateVO>();
        StateVO defaultVO = new StateVO();
        defaultVO.setTitle("Estados");
        defaultVO.setSelected(false);
        orderStatusVOList.add(defaultVO);
        for (OrderStatus orderStatus : orderStatusList)
        {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(orderStatus.getDescription());
            stateVO.setSelected(true);
            stateVO.setOrderStatus(orderStatus);
            orderStatusVOList.add(stateVO);
        }

        spinnerOS = (Spinner)findViewById(R.id.ordersactivity_orderStatus_spinner);
        spinnerOSAdapter = new CustomSpinnerAdapter(this, R.layout.spinner_item_checkbox, orderStatusVOList);
        spinnerOS.setAdapter(spinnerOSAdapter);


        customers = inventory.getAllCustomers();
        String allCustomersFilter = "Todos";
        ArrayList<String> arraySpinnerList = new ArrayList<String>();
        arraySpinnerList.add(allCustomersFilter);
        for (Customer customer : customers)
        {
            arraySpinnerList.add(customer.getFullName());
        }
        String[] arraySpinnerStrings = new String [arraySpinnerList.size()];
        arraySpinnerList.toArray(arraySpinnerStrings);
        spinnerCAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_small, arraySpinnerStrings);


        spinnerC = (Spinner)findViewById(R.id.ordersactivity_customer_spinner);
        spinnerC.setAdapter(spinnerCAdapter);
        spinnerC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchOrders();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSort = (Spinner)findViewById(R.id.ordersactivity_order_sortSpinner);
        spinnerSort.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item_small, spinnerSortStrings));
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

                searchOrders();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean chkState[] = new boolean[orderStatusVOList.size()];

        for (int i=0; i<orderStatusVOList.size();i++)
        {
            chkState[i] = orderStatusVOList.get(i).isSelected();
        }
        outState.putBooleanArray(KEY_SPINNER_BOOLEAN, chkState);
        outState.putSerializable(KEY_CALENDAR_FROM, calendarFromDate);
        outState.putSerializable(KEY_CALENDAR_UNTIL, calendarUntilDate);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean chkState[]=savedInstanceState.getBooleanArray(KEY_SPINNER_BOOLEAN);
        for (int i=0; i<chkState.length;i++)
        {
            orderStatusVOList.get(i).setSelected(chkState[i]);
        }
        calendarFromDate = (Calendar)savedInstanceState.getSerializable(KEY_CALENDAR_FROM);
        calendarUntilDate= (Calendar)savedInstanceState.getSerializable(KEY_CALENDAR_UNTIL);
        textViewFromDate.setText(formatter.format(calendarFromDate.getTime()));
        textViewUntilDate.setText(formatter.format(calendarUntilDate.getTime()));
        searchOrders();

    }

    private class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private TextView txtClient;
        private TextView txtStatus;
        private TextView txtDate;
        private TextView options;
        private String fullName;
        private String changelog;

        public OrderHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            txtClient=(TextView)itemView.findViewById(R.id.order_client_text);
            txtStatus=(TextView)itemView.findViewById(R.id.order_status_text);
            txtDate=(TextView)itemView.findViewById(R.id.order_date_text);
            options=(TextView)itemView.findViewById(R.id.order_options);

        }

        public void bindOrder(Order order)
        {
            txtClient.setText(order.getCustomer().getFullName());
            txtStatus.setText(order.getOrderStatus().getDescription());
            txtDate.setText(formatter.format(order.getCalendar().getTime()));
            changelog = order.getChangeLog();
        }


        @Override
        public void onClick(View v) {
            Toast.makeText(OrdersActivity.this, changelog, Toast.LENGTH_SHORT).show();
        }

        public TextView getTxtClient() {
            return txtClient;
        }

        public TextView getOptions() {
            return options;
        }
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrdersActivity.OrderHolder> {
        private ArrayList<Order> orders1;
        private Context context;

        public OrderAdapter(ArrayList<Order> orders, Context context) {
            this.orders1 = orders;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final OrdersActivity.OrderHolder holder, int position) {
            holder.bindOrder(orders1.get(position));

            holder.getOptions().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, holder.getOptions());
                    popup.setGravity(Gravity.END);

                    popup.inflate(R.menu.menu_popup_ram);
                    if(!orders1.get(holder.getAdapterPosition()).getOrderStatus().isEditable())
                    {
                        //popup.getMenu().removeItem(R.id.ram_modify);
                        popup.getMenu().findItem(R.id.ram_modify).setEnabled(false);
                    }
                    if(orders1.get(holder.getAdapterPosition()).getOrderStatus().getPrevious().isEmpty())
                    {
                        popup.getMenu().findItem(R.id.ram_state_back).setEnabled(false);
                    }
                    if(orders1.get(holder.getAdapterPosition()).getOrderStatus().getNext().isEmpty())
                    {
                        popup.getMenu().findItem(R.id.ram_state_forward).setEnabled(false);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.ram_state_back:{
                                    if(item.isEnabled()) {
                                        Bundle args = new Bundle();
                                        Bundle save = new Bundle();
                                        save.putInt(dialogSaveDataAdapterPosition, holder.getAdapterPosition());
                                        args.putString(DialogChangeOrderStatus.ARG_TITLE, "Regresar estado a:");
                                        args.putString(DialogChangeOrderStatus.ARG_BTN_POSITIVE, "Aceptar");
                                        args.putString(DialogChangeOrderStatus.ARG_BTN_NEGATIVE, "Cancelar");
                                        args.putBundle(DialogChangeOrderStatus.ARG_SAVE_DATA, save);
                                        args.putIntegerArrayList(DialogChangeOrderStatus.ARG_SP_ARRAYLIST_ORDER_iD, orders1.get(holder.getAdapterPosition()).getOrderStatus().getPrevious());

                                        DialogChangeOrderStatus dialogChangeOrderStatus = DialogChangeOrderStatus.newInstance(dialogTagStateBack, args);
                                        dialogChangeOrderStatus.show(getSupportFragmentManager(), dialogTagStateBack);
                                    }
                                    break;}
                                case R.id.ram_state_forward:{
                                    if(item.isEnabled()) {
                                        Bundle args = new Bundle();
                                        Bundle save = new Bundle();
                                        save.putInt(dialogSaveDataAdapterPosition, holder.getAdapterPosition());
                                        args.putString(DialogChangeOrderStatus.ARG_TITLE, "Avanzar estado a:");
                                        args.putString(DialogChangeOrderStatus.ARG_BTN_POSITIVE, "Aceptar");
                                        args.putString(DialogChangeOrderStatus.ARG_BTN_NEGATIVE, "Cancelar");
                                        args.putBundle(DialogChangeOrderStatus.ARG_SAVE_DATA, save);
                                        args.putIntegerArrayList(DialogChangeOrderStatus.ARG_SP_ARRAYLIST_ORDER_iD, orders1.get(holder.getAdapterPosition()).getOrderStatus().getNext());

                                        DialogChangeOrderStatus dialogChangeOrderStatus = DialogChangeOrderStatus.newInstance(dialogTagStateForward, args);
                                        dialogChangeOrderStatus.show(getSupportFragmentManager(), dialogTagStateForward);
                                    }
                                    break;}
                                case R.id.ram_modify:{
                                    if(!item.isEnabled())
                                    {
                                        Toast.makeText(OrdersActivity.this, "No se puede modificar un pedido fuera del estado pendiente" + holder.getTxtClient().getText(), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(OrdersActivity.this, OrdersModifyActivity.class);
                                        intent.putExtra(OrdersModifyActivity.EXTRA_ORDER_ID, orders1.get(holder.getAdapterPosition()).getId());
                                        startActivityForResult(intent, CODE_MODIFY_ORDER);
                                    }
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
        public OrdersActivity.OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.order_list_item, parent, false);
            return new OrdersActivity.OrderHolder(view);
        }

        @Override
        public int getItemCount() {
            return orders1.size();
        }
    }

    public void searchOrders(){
        orders.clear();
        //if(spinnerC.getSelectedItem().toString().equals("Todos"))
        //{
        //    for (StateVO orderStatusVO : orderStatusVOList)
        //    {
        //        if(orderStatusVO.isSelected())
        //        {
        //            ArrayList<Order> newOrders =inventory.getOrdersByOrderStatus(orderStatusVO.getOrderStatus());
        //            for (Order order : newOrders)
        //            {
        //                orders.add(order);
        //            }
        //        }
//
        //    }
        //}
        String selectedCustomer = spinnerC.getSelectedItem().toString();
        ArrayList<OrderStatus> selectedOrderStatus = new ArrayList<OrderStatus>();



        for (StateVO orderStatusVO : orderStatusVOList)
        {
            if (orderStatusVO.isSelected())
            {
                selectedOrderStatus.add(orderStatusVO.getOrderStatus());
            }
        }

        if(selectedCustomer.equals("Todos"))
        {
            if (calendarCheckbox.isChecked())
            {
                orders = inventory.getOrdersByCustomerAndOrderStatus2(null, selectedOrderStatus, calendarFromDate, calendarUntilDate, selectedSort );
            }
            else
            {
                orders = inventory.getOrdersByCustomerAndOrderStatus2(null, selectedOrderStatus, null, null, selectedSort);
            }

        }
        else
        {
            for (Customer customer : customers)
            {
                if (customer.getFullName().equals(selectedCustomer)) {
                    if (calendarCheckbox.isChecked()) {
                        orders = inventory.getOrdersByCustomerAndOrderStatus2(customer, selectedOrderStatus, calendarFromDate, calendarUntilDate, selectedSort);
                    }
                    else{
                        orders = inventory.getOrdersByCustomerAndOrderStatus2(customer, selectedOrderStatus, null, null, selectedSort);
                    }
                    break;
                }
            }
        }

        //else {
        //    for (Customer customer : customers) {
        //        if (customer.getFullName().equals(spinnerC.getSelectedItem().toString())) {
        //            for (StateVO orderStatusVO : orderStatusVOList) {
        //                if (orderStatusVO.isSelected()) {
        //                    ArrayList<Order> newOrders = inventory.getOrdersByCustomerAndOrderStatus(customer, orderStatusVO.getOrderStatus());
        //                    for (Order order : newOrders) {
        //                        orders.add(order);
        //                    }
        //                }
//
        //            }
        //        }
        //    }
        //}

        adapter = new OrdersActivity.OrderAdapter(orders,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_addonly,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.addItemToDb){
            Intent intent = new Intent(this, OrdersInsertActivity.class);
            startActivityForResult(intent, CODE_INSERT_ORDER);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CODE_INSERT_ORDER && resultCode == RESULT_OK)
        {
            Calendar currentDate = Calendar.getInstance();
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH);
            int day = currentDate.get(Calendar.DAY_OF_MONTH);
            calendarUntilDate.set(year, month, day);
            newestOrder = inventory.getNewestOrder();
            textViewUntilDate.setText(formatter.format(calendarUntilDate.getTime()));
            searchOrders();
        }
        else if (requestCode == CODE_MODIFY_ORDER && resultCode == RESULT_OK)
        {
            Toast.makeText(OrdersActivity.this, "Orden modificada con éxito", Toast.LENGTH_SHORT).show();
        }

    }

    private class CustomSpinnerAdapter extends ArrayAdapter<StateVO>
    {
        private Context mContext;
        private ArrayList<StateVO> listState;
        private CustomSpinnerAdapter myAdapter;
        private boolean isFromView = false;

        public CustomSpinnerAdapter(Context context, int resource, List<StateVO> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.listState = (ArrayList<StateVO>) objects;
            this.myAdapter = this;
        }

        @Override
        public int getPosition(@Nullable StateVO item) {
            return super.getPosition(item);
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }



        public View getCustomView(final int position, View convertView,
                                  ViewGroup parent) {

            final StateVOHolder holder;

            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item_checkbox, null);
            holder = new StateVOHolder(convertView);
            holder.bindStateVo(listState.get(position));

            if(position == 0)
            {
                holder.mCheckBox.setVisibility(View.GONE);
            }
            //convertView.setTag(holder);

            return convertView;
        }


    }

    private class StateVOHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;

        public StateVOHolder (View itemView)
        {
            mTextView = (TextView)itemView.findViewById(R.id.spinner_item_checkbox_txt);
            mCheckBox = (CheckBox)itemView.findViewById(R.id.spinner_item_chekbox_btn);
        }

        public void bindStateVo (final StateVO stateVO)
        {
            mTextView.setText(stateVO.getTitle());
            if(stateVO.getOrderStatus() == null)
            {
                stateVO.setSelected(false);
            }
            else
            {
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCheckBox.setChecked(!mCheckBox.isChecked());
                    }
                });
            }
            mCheckBox.setChecked(stateVO.isSelected());

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    stateVO.setSelected(isChecked);
                        searchOrders();
                }
            });
        }
    }

    public class StateVO {
        private String title;
        private boolean selected;
        private OrderStatus orderStatus;
        //private Inventory.CustomerFilters customerFilter;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public OrderStatus getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }


    @Override
    public void onDialogDateSet(DialogFragment dialog, String tag, int year, int month, int day) {
        if (tag.equals(dialogDateTagFrom))
        {
            calendarFromDate.set(year, month, day);
            textViewFromDate.setText(formatter.format(calendarFromDate.getTime()));
        }
        else if (tag.equals(dialogDateTagUntil))
        {
            calendarUntilDate.set(year, month, day);
            textViewUntilDate.setText(formatter.format(calendarUntilDate.getTime()));
        }
        searchOrders();
    }

    @Override
    public void onDialogOrderStatusPositiveClick(String tag, android.support.v4.app.DialogFragment dialog, OrderStatus orderStatus) {

        EditText changelogTxt = (EditText)dialog.getDialog().findViewById(R.id.dialog_change_order_status_changelog);
            Bundle save = ((DialogChangeOrderStatus)dialog).getSavedData();
            int position = save.getInt(dialogSaveDataAdapterPosition);

            switch (inventory.modifyOrderState(orders.get(position), orderStatus.getId(), changelogTxt.getText().toString()))
            {
                case InvalidOrderStatus:
                    Toast.makeText(OrdersActivity.this, "Estado no válido", Toast.LENGTH_SHORT).show();
                    break;
                case InvalidChangelog:
                    Toast.makeText(OrdersActivity.this, "Texto no válido", Toast.LENGTH_SHORT).show();
                    break;
                case Ok:
                    Toast.makeText(OrdersActivity.this, "Estado de la orden modificado con éxito", Toast.LENGTH_SHORT).show();

                    for(StateVO stateVO : orderStatusVOList)
                    {
                        if((stateVO.getOrderStatus()==null)? false : (stateVO.getOrderStatus().getId() == orderStatus.getId()))
                        {
                            if(stateVO.isSelected())
                            {
                                orders.get(position).setOrderStatus(orderStatus);
                                orders.get(position).setChangeLog(inventory.getOrderById(orders.get(position).getId()).getChangeLog());
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                searchOrders();
                            }
                            break;
                        }
                    }

                    dialog.dismiss();
                    break;
            }


    }

    @Override
    public void onDialogOrderStatusNegativeClick(String tag, android.support.v4.app.DialogFragment dialog, OrderStatus orderStatus) {
        dialog.dismiss();
    }
}





