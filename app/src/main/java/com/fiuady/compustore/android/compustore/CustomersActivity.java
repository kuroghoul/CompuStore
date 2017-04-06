package com.fiuady.compustore.android.compustore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Customer;
import com.fiuady.compustore.db.Inventory;


import java.util.List;

public class CustomersActivity extends AppCompatActivity {

    private Inventory inventory;
    private RecyclerView recyclerView;
    private CustomersActivity.CustomersAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        inventory = new Inventory(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);
        recyclerView=(RecyclerView)findViewById(R.id.customers_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new CustomersActivity.CustomersAdapter(inventory.getAllCustomers());
        recyclerView.setAdapter(adapter);

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

        }

        public void bindCustomer(Customer customer)
        {
            txtFirstname.setText(customer.getFirstName());
            txtLastname.setText(customer.getLastName());
            txtAdress.setText(customer.getAdress());
            txtPhone1.setText(customer.getPhone1());
            txtPhone2.setText(customer.getPhone2());
            txtPhone3.setText(customer.getPhone3());
            txtEmail.setText(customer.getEmail());

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

}

