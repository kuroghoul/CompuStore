package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Inventory;
import com.fiuady.compustore.db.MonthSale;
import com.fiuady.compustore.db.Order;
import com.fiuady.compustore.db.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SalesSummaryActivity extends AppCompatActivity {
    private static String dialogOrderListTag = "com.fiuady.compustore.android.compustore.salessummaryactivity.dialogOrderListTag";

    SimpleDateFormat formatter = new SimpleDateFormat("MMM-yyyy");
    private ArrayList<MonthSale> monthSales;
    private Inventory inventory;

    private MonthSaleAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_summary);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        inventory = new Inventory(this);

        monthSales = inventory.getAllMonthSales();
        recyclerView=(RecyclerView)findViewById(R.id.salesSummaryActivity_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MonthSaleAdapter(monthSales, this);
        recyclerView.setAdapter(adapter);


    }



















    private class MonthSaleHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private TextView txtMonth;
        private TextView txtTotal;
        private MonthSale monthSale;



        public MonthSaleHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            txtMonth=(TextView)itemView.findViewById(R.id.sales_by_month_item_date);
            txtTotal=(TextView)itemView.findViewById(R.id.sales_by_month_item_total);
        }

        public void bindOrder(MonthSale monthSale)
        {
            this.monthSale = monthSale;
            txtMonth.setText(formatter.format(monthSale.getCalendar().getTime()));
            txtTotal.append(String.valueOf(monthSale.getTotal()));
        }


        @Override
        public void onClick(View v) {

            //ArrayList<Integer> productsIdList = new ArrayList<Integer>();
            //for (Product product :inventory.getAllProducts())
            //{
            //    productsIdList.add(product.getId());
            //}
//
            Bundle args = new Bundle();
            //Bundle save = new Bundle();
            //save.putInt(dialogSaveDataAdapterPosition, getAdapterPosition());
//
            args.putString(DialogOrderSaleList.ARG_TITLE,"Órdenes");
            args.putString(DialogOrderSaleList.ARG_BTN_POSITIVE,"Ok");
            args.putSerializable(DialogOrderSaleList.ARG_CALENDAR, monthSale.getCalendar());
            //args.putBundle(DialogProductList.ARG_SAVE_DATA, save);
            //args.putIntegerArrayList(DialogProductList.ARG_PRODUCT_ID_LIST, productsIdList);
//
            DialogOrderSaleList dialogOrderSaleList = DialogOrderSaleList.newInstance(dialogOrderListTag, args);
            dialogOrderSaleList.show(getSupportFragmentManager(), dialogOrderListTag);
        }

        public TextView getTxtMonth() {
            return txtMonth;
        }

        public TextView getTxtTotal() {
            return txtTotal;
        }

        public MonthSale getMonthSale() {
            return monthSale;
        }
    }



    private class MonthSaleAdapter extends RecyclerView.Adapter<SalesSummaryActivity.MonthSaleHolder> {
        private ArrayList<MonthSale> monthSales;
        private Context context;

        public MonthSaleAdapter(ArrayList<MonthSale> monthSales, Context context) {
            this.monthSales = monthSales;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final SalesSummaryActivity.MonthSaleHolder holder, int position) {
            holder.bindOrder(this.monthSales.get(position));
        }

        @Override
        public SalesSummaryActivity.MonthSaleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.sales_by_month_item, parent, false);
            return new SalesSummaryActivity.MonthSaleHolder(view);
        }

        @Override
        public int getItemCount() {
            return this.monthSales.size();
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
