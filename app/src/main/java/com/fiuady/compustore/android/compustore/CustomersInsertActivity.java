package com.fiuady.compustore.android.compustore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;

public class CustomersInsertActivity extends AppCompatActivity {

    Inventory inventory;
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText addressET;
    private EditText phone1ET;
    private EditText phone2ET;
    private EditText phone3ET;
    private EditText emailET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_insert);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        inventory = new Inventory(this);

        firstNameET = (EditText)findViewById(R.id.customers_edit_firstname_text);
        lastNameET = (EditText)findViewById(R.id.customers_edit_lastname_text);
        addressET = (EditText)findViewById(R.id.customers_edit_address_text);
        phone1ET = (EditText)findViewById(R.id.customers_edit_phone1_text);
        phone2ET = (EditText)findViewById(R.id.customers_edit_phone2_text);
        phone3ET = (EditText)findViewById(R.id.customers_edit_phone3_text);
        emailET = (EditText)findViewById(R.id.customers_edit_email_text);



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
            switch (inventory.insertCustomer(firstNameET.getText().toString(),
                    lastNameET.getText().toString(),
                    addressET.getText().toString(),
                    phone1ET.getText().toString(),
                    phone2ET.getText().toString(),
                    phone3ET.getText().toString(),
                    emailET.getText().toString()))
            {
                case InvalidFirstName:
                    break;
                case InvalidLastName:
                    break;
                case InvalidAddress:
                    break;
                case Ok:
                    Toast.makeText(CustomersInsertActivity.this, "Cliente agregado con éxito", Toast.LENGTH_SHORT).show();
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
