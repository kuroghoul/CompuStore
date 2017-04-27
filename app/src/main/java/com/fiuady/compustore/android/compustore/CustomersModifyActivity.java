package com.fiuady.compustore.android.compustore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Customer;
import com.fiuady.compustore.db.Inventory;

public class CustomersModifyActivity extends AppCompatActivity {
    public static String EXTRA_CUSTOMER_ID = "com.fiuady.compustore.android.compustore.customermodifysactivity.extracustomerid";
    private Inventory inventory;
    private Customer customer;
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText addressET;
    private EditText phone1ET;
    private EditText phone2ET;
    private EditText phone3ET;
    private EditText emailET;
    private int extraCustomerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_insert);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        extraCustomerId = intent.getIntExtra(EXTRA_CUSTOMER_ID, -1);
        if(extraCustomerId!=-1)
        {
            inventory = new Inventory(this);
            customer = inventory.getCustomerById(extraCustomerId);
            firstNameET = (EditText)findViewById(R.id.customers_edit_firstname_text);
            firstNameET.append(customer.getFirstName());
            lastNameET = (EditText)findViewById(R.id.customers_edit_lastname_text);
            lastNameET.append(customer.getLastName());
            addressET = (EditText)findViewById(R.id.customers_edit_address_text);
            if(customer.getAddress()!=null) {
                addressET.append(customer.getAddress());
            }
            phone1ET = (EditText)findViewById(R.id.customers_edit_phone1_text);
            if(customer.getPhone1()!=null)
            {
                phone1ET.append(customer.getPhone1());
            }
            phone2ET = (EditText)findViewById(R.id.customers_edit_phone2_text);
            if(customer.getPhone2()!=null)
            {
                phone2ET.append(customer.getPhone2());
            }

            phone3ET = (EditText)findViewById(R.id.customers_edit_phone3_text);
            if(customer.getPhone3()!=null)
            {
                phone3ET.append(customer.getPhone3());
            }

            emailET = (EditText)findViewById(R.id.customers_edit_email_text);
            if(customer.getEmail()!=null) {
                emailET.append(customer.getEmail());
            }
        }
        else
        {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_customers_insert,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.customers_menu_insert_save)
        {
            Customer oldCustomer = customer;
            Customer newCustomer = new Customer(customer);
            newCustomer.setFirstName(firstNameET.getText().toString());
            newCustomer.setLastName(lastNameET.getText().toString());
            newCustomer.setAdress(addressET.getText().toString());
            newCustomer.setPhone1(phone1ET.getText().toString());
            newCustomer.setPhone2(phone2ET.getText().toString());
            newCustomer.setPhone3(phone3ET.getText().toString());
            newCustomer.setEmail(emailET.getText().toString());

            switch (inventory.modifyCustomer(newCustomer))
            {
                case InvalidFirstName:
                    break;
                case InvalidLastName:
                    break;
                case InvalidAddress:
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
}
