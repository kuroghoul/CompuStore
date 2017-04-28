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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Customer;
import com.fiuady.compustore.db.Inventory;


import java.util.ArrayList;
import java.util.List;

public class CustomersActivity extends AppCompatActivity implements DialogConfirm.DialogConfirmListener{

    private Inventory inventory;
    private RecyclerView recyclerView;
    private ImageButton searchButton;
    private CustomersActivity.CustomersAdapter adapter;
    private ArrayList<Customer> customers;
    private EditTextSearch searchEText;
    private Spinner spinner;
    private CustomSpinnerAdapter spinnerAdapter;
    final private String customerFilters[] = {"Select Filters", "Nombre", "Apellido", "Dirección", "Teléfono", "E-mail"};
    private ArrayList<StateVO> customerFiltersList;
    boolean Flag_restoreSearch;

    private static String dialogTagDelete = "com.fiuady.compustore.android.compustore.customersactivity.dialogtagdelete";
    private static String dialogSaveDataAdapterPosition = "com.fiuady.compustore.android.compustore.customersactivity.dialogsavedataadapterposition";
    private static String KEY_SPINNER_BOOLEAN = "com.fiuady.compustore.android.compustore.customersactivity.spinnerboolean";
    private static String KEY_FLAG_RESTORESEARCH = "com.fiuady.compustore.android.compustore.customersactivity.restoresearch";
    private static int CODE_INSERT_CUSTOMER = 1;
    private static int CODE_MODIFY_CUSTOMER = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        inventory = new Inventory(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView=(RecyclerView)findViewById(R.id.customers_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //customers = inventory.getAllCustomers();
        //adapter = new CustomersActivity.CustomersAdapter(customers);
        //recyclerView.setAdapter(adapter);
        Flag_restoreSearch = false;
        searchEText = (EditTextSearch)findViewById(R.id.et_customers_search);
        searchEText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    searchEText.clearFocus();
                    InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchEText.getWindowToken(), 0);
                    searchCustomers();
                    return true;
                }
                searchEText.clearFocus();
                return false;

            }
        });


        customerFiltersList = new ArrayList<>();
        ArrayList<Inventory.CustomerFilters> enumCustomerFilters = new ArrayList<>();
        enumCustomerFilters.add(Inventory.CustomerFilters.Default);
        enumCustomerFilters.add(Inventory.CustomerFilters.FirstName);
        enumCustomerFilters.add(Inventory.CustomerFilters.LastName);
        enumCustomerFilters.add(Inventory.CustomerFilters.Address);
        enumCustomerFilters.add(Inventory.CustomerFilters.Phone);
        enumCustomerFilters.add(Inventory.CustomerFilters.Email);

        for (int i = 0 ; i<enumCustomerFilters.size(); i++)
        {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(customerFilters[i]);
            stateVO.setCustomerFilter(enumCustomerFilters.get(i));
            stateVO.setSelected(true);
            customerFiltersList.add(stateVO);
        }
        spinner = (Spinner)findViewById(R.id.spinner_filter_customers);
        spinnerAdapter = new CustomSpinnerAdapter(CustomersActivity.this, R.layout.spinner_item_checkbox, customerFiltersList);
        spinner.setAdapter(spinnerAdapter);


        searchButton=(ImageButton)findViewById(R.id.imgbtn_customers_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusableInTouchMode(true);
                v.requestFocus();
                v.clearFocus();
                v.setFocusableInTouchMode(false);
                hideKeyboard(CustomersActivity.this, v);

                searchCustomers();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        boolean chkState[] = new boolean[customerFiltersList.size()];

        for (int i=0; i<customerFiltersList.size();i++)
        {
            chkState[i] = customerFiltersList.get(i).isSelected();
        }
        outState.putBooleanArray(KEY_SPINNER_BOOLEAN, chkState);
        outState.putBoolean(KEY_FLAG_RESTORESEARCH, Flag_restoreSearch);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean chkState[]=savedInstanceState.getBooleanArray(KEY_SPINNER_BOOLEAN);
        for (int i=0; i<chkState.length;i++)
        {
            customerFiltersList.get(i).setSelected(chkState[i]);
        }
        Flag_restoreSearch = savedInstanceState.getBoolean(KEY_FLAG_RESTORESEARCH);
        if(Flag_restoreSearch)
        {
            searchCustomers();
        }
    }

    private void refreshRecyclerView ()
    {
        ArrayList<Inventory.CustomerFilters> customerFiltersEnum= new ArrayList<Inventory.CustomerFilters>();
        for (StateVO stateVO : customerFiltersList)
        {
            if(stateVO.isSelected())
            {
                customerFiltersEnum.add(stateVO.getCustomerFilter());
            }

        }

        customers = inventory.searchCustomersWithFilter(customerFiltersEnum, searchEText.getText().toString());

        adapter = new CustomersActivity.CustomersAdapter(customers);
        recyclerView.setAdapter(adapter);
        Flag_restoreSearch = true;

    }
    private void searchCustomers()
    {
        refreshRecyclerView();
        Flag_restoreSearch = true;
        if(customers.isEmpty())
        {
            Toast.makeText(CustomersActivity.this, getString(R.string.productsActivity_search_noResults), Toast.LENGTH_LONG).show();
        }
    }
    private void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            if(stateVO.getCustomerFilter()== Inventory.CustomerFilters.Default)
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
                    if (Flag_restoreSearch)
                    {
                        searchCustomers();
                    }


                }
            });
        }
    }

    public class StateVO {
        private String title;
        private boolean selected;
        private Inventory.CustomerFilters customerFilter;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Inventory.CustomerFilters getCustomerFilter() {
            return customerFilter;
        }

        public void setCustomerFilter(Inventory.CustomerFilters customerFilter) {
            this.customerFilter = customerFilter;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }





    private class CustomerHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView txtFirstname;
        private TextView txtLastname;
        private TextView txtAdress;
        private TextView txtPhone1;
        private TextView txtPhone2;
        private TextView txtPhone3;
        private TextView txtEmail;
        private TextView options;
        private int id;

        public CustomerHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            txtFirstname=(TextView)itemView.findViewById(R.id.customers_firstname_text);
            txtLastname=(TextView)itemView.findViewById(R.id.customers_lastname_text);
            txtAdress=(TextView)itemView.findViewById(R.id.customers_adress_text);
            txtPhone1=(TextView)itemView.findViewById(R.id.customers_phone1_text);
            txtPhone2=(TextView)itemView.findViewById(R.id.customers_phone2_text);
            txtPhone3=(TextView)itemView.findViewById(R.id.customers_phone3_text);
            txtEmail=(TextView)itemView.findViewById(R.id.customers_email_text);
            options = (TextView)itemView.findViewById(R.id.customer_options);

            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(CustomersActivity.this, v);
                    popup.inflate(R.menu.menu_options_me);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.me_modify:{
                                    Intent intent = new Intent(CustomersActivity.this, CustomersModifyActivity.class);
                                    intent.putExtra(CustomersModifyActivity.EXTRA_CUSTOMER_ID, customers.get(getAdapterPosition()).getId());
                                    startActivityForResult(intent, CODE_MODIFY_CUSTOMER);
                                    break;}
                                case R.id.me_delete:{
                                    Bundle save = new Bundle();
                                    save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());
                                    DialogConfirm dialogConfirm = DialogConfirm.newInstance("Eliminar cliente", "Eliminar", "Cancelar", save);
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

        public void bindCustomer(Customer customer)
        {
            id = customer.getId();
            txtFirstname.setText(customer.getFirstName());
            txtLastname.setText(customer.getLastName());
            txtAdress.setText(customer.getAddress());



            if (customer.getPhone1()==null)
            {
                txtPhone1.setVisibility(View.GONE);
                itemView.findViewById(R.id.customers_phone1_tag).setVisibility(View.GONE);
            }
            else
            {
                txtPhone1.setText(customer.getPhone1());
            }

            if (customer.getPhone2()==null)
            {
                txtPhone2.setVisibility(View.GONE);
                itemView.findViewById(R.id.customers_phone2_tag).setVisibility(View.GONE);
            }
            else
            {
                txtPhone2.setText(customer.getPhone2());
            }

            if (customer.getPhone3()==null)
            {
                txtPhone3.setVisibility(View.GONE);
                itemView.findViewById(R.id.customers_phone3_tag).setVisibility(View.GONE);
            }
            else
            {
                txtPhone3.setText(customer.getPhone3());
            }

            if (customer.getEmail()==null)
            {
                txtEmail.setVisibility(View.GONE);
                itemView.findViewById(R.id.customers_email_tag).setVisibility(View.GONE);
            }
            else
            {
                txtEmail.setText(customer.getEmail());
            }


        }

        @Override
        public void onClick(View v) {
            Toast.makeText(CustomersActivity.this, "Cliente: " + txtFirstname.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    private class CustomersAdapter extends RecyclerView.Adapter<CustomersActivity.CustomerHolder>
    {
        private List<Customer> customers;
        public CustomersAdapter(List<Customer> customers) {
            this.customers =customers;
        }

        @Override
        public void onBindViewHolder(CustomersActivity.CustomerHolder holder, int position) {
            holder.bindCustomer(customers.get(position));
        }

        @Override
        public CustomersActivity.CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.customers_list_item, parent, false);
            return new CustomersActivity.CustomerHolder(view);
        }

        @Override
        public int getItemCount() {
            return customers.size();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Flag_restoreSearch)
        {
            refreshRecyclerView();
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
        if(item.getItemId()==R.id.addItemToDb){
            Intent intent = new Intent(this, CustomersInsertActivity.class);
            startActivityForResult(intent, CODE_INSERT_CUSTOMER);
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
    public void onDialogConfirmPositiveClick(DialogFragment dialog) {
        Bundle save = ((DialogConfirm)dialog).getSavedData();
        int position = save.getInt(dialogSaveDataAdapterPosition);

        switch(inventory.deleteCustomer(customers.get(position)))
        {
            case AlreadyInUse:
                Toast.makeText(CustomersActivity.this, "No se puede eliminar un cliente que ha realizado una orden", Toast.LENGTH_SHORT).show();
                break;
            case Ok:
                Toast.makeText(CustomersActivity.this, "Cliente eliminado", Toast.LENGTH_SHORT).show();
                //refreshRecyclerView();
                customers.remove(position);
                //recyclerView.removeViewAt(layoutPosition);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, customers.size());
                break;
        }
    }

    @Override
    public void onDialogConfirmNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}

