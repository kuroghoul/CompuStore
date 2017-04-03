package com.fiuady.compustore.android.compustore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.fiuady.compustore.R;


public class MainActivity extends AppCompatActivity {




    private ImageButton Categories;
    private ImageButton Products;
    private ImageButton Assemblies;
    private ImageButton Customers;
    private ImageButton Orders;
    private ImageButton Logs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Categories = (ImageButton) findViewById(R.id.Categories);
        Products = (ImageButton) findViewById(R.id.Products);
        Assemblies = (ImageButton) findViewById(R.id.Assemblies);
        Customers = (ImageButton) findViewById(R.id.Clients);
        Orders = (ImageButton) findViewById(R.id.Orders);
        Logs = (ImageButton) findViewById(R.id.Logs);

        Categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoriesActivity(v);
            }
        });
        Products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductsActivity(v);
            }
        });
        Assemblies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssembliesActivity(v);
            }
        });
        Customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClientsActivity(v);
            }
        });
        Orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrdersActivity(v);
            }
        });
        Logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogsActivity(v);
            }
        });

    }
    public void openCategoriesActivity (View view)
    {
        Intent intent = new Intent(this, CategoriesActivity.class);
        startActivity(intent);
    }
    public void openProductsActivity (View view)
    {
        Intent intent = new Intent(this, ProductsActivity.class);
        startActivity(intent);
    }
    public void openAssembliesActivity (View view)
    {
        Intent intent = new Intent(this, AssembliesActivity.class);
        startActivity(intent);
    }
    public void openClientsActivity (View view)
    {
        Intent intent = new Intent(this, CustomersActivity.class);
        startActivity(intent);
    }
    public void openOrdersActivity (View view)
    {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
    }
    public void openLogsActivity (View view)
    {
        Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }

}
