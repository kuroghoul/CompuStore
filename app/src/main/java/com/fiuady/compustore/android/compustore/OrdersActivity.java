package com.fiuady.compustore.android.compustore;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuady.compustore.R;
import com.fiuady.compustore.db.Order;
import com.fiuady.compustore.db.Inventory;

import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    private Inventory inventory;
    private RecyclerView recyclerView;
    private OrdersActivity.OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inventory = new Inventory(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerView=(RecyclerView)findViewById(R.id.order_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        adapter = new OrdersActivity.OrderAdapter(inventory.getAllOrders(), this);
        recyclerView.setAdapter(adapter);

    }



    private class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private TextView txtClient;
        private TextView txtStatus;
        private TextView txtDate;
        private TextView options;
        private String fullName;

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

            fullName=order.getCustomer().getFirstName()+ order.getCustomer().getLastName();
            txtClient.setText(fullName);
            txtStatus.setText(order.getOrderStatus().getDescription());
            txtDate.setText(order.getDate());



        }

        @Override
        public void onClick(View v) {
            Toast.makeText(OrdersActivity.this, "Orden de: " + txtClient.getText(), Toast.LENGTH_SHORT).show();
        }


        public TextView getTxtClient() {
            return txtClient;
        }



        public TextView getOptions() {
            return options;
        }
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrdersActivity.OrderHolder> {
        private List<Order> orders;
        private Context context;

        public OrderAdapter(List<Order> orders, Context context) {
            this.orders = orders;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final OrdersActivity.OrderHolder holder, int position) {
            holder.bindOrder(orders.get(position));

            holder.getOptions().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, holder.getOptions());

                    popup.inflate(R.menu.menu_options_me);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    Toast.makeText(OrdersActivity.this, item.getTitle().toString() + " " + holder.getTxtClient().getText(), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.menu2:
                                    Toast.makeText(OrdersActivity.this, item.getTitle().toString() + " " + holder.getTxtClient().getText(), Toast.LENGTH_SHORT).show();
                                    break;
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
            return orders.size();
        }
    }
}





