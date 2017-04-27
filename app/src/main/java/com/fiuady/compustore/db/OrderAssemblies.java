package com.fiuady.compustore.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuro on 02/04/2017.
 */

public class OrderAssemblies {
    private Order order;
    private Assembly assembly;
    private int qty;

    public OrderAssemblies(Order order, Assembly assembly, int qty) {
        this.order = order;
        this.assembly = assembly;
        this.qty = qty;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Assembly getAssembly() {
        return assembly;
    }

    public void setAssembly(Assembly assembly) {
        this.assembly = assembly;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
