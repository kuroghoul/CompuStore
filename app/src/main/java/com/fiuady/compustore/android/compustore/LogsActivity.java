package com.fiuady.compustore.android.compustore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.Product;

import java.util.ArrayList;

public class LogsActivity extends AppCompatActivity {

    private ImageButton requiredProducts;
    private ImageButton orderSimulator;
    private ImageButton salesSummary;
    static String dialogProductListTag = "com.fiuady.compustore.android.compustore.productsactivity.dialogProductListTag";
    private Inventory inventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        inventory = new Inventory(this);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requiredProducts = (ImageButton)findViewById(R.id.logsactivity_imgbtn_processByCustomer);
        orderSimulator = (ImageButton)findViewById(R.id.logsactivity_imgbtn_processByDate);
        salesSummary = (ImageButton)findViewById(R.id.logsactivity_imgbtn_processByTotal);


        requiredProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> productsIdList = new ArrayList<Integer>();
                ArrayList<Integer> productsQtyList = new ArrayList<Integer>();
                for (Product product :inventory.getAllRequiredProductsToConfirmAll())
                {
                    productsIdList.add(product.getId());
                    productsQtyList.add(product.getQty());
                }

                Bundle args = new Bundle();
                Bundle save = new Bundle();


                args.putString(DialogProductList.ARG_TITLE,"Productos faltantes");
                args.putString(DialogProductList.ARG_BTN_POSITIVE,"Ok");
                args.putString(DialogProductList.ARG_QTY_TAG, "Faltan");
                args.putIntegerArrayList(DialogProductList.ARG_PRODUCT_ID_LIST, productsIdList);
                args.putIntegerArrayList(DialogProductList.ARG_PRODUCT_QTY_LIST, productsQtyList);

                DialogProductList dialogProductList = DialogProductList.newInstance(dialogProductListTag, args);
                dialogProductList.show(getSupportFragmentManager(), dialogProductListTag);
            }
        });


        orderSimulator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogsActivity.this, OrderSimulatorActivity.class);
                startActivity(intent);
            }
        });

        salesSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogsActivity.this, SalesSummaryActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
}
