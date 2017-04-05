package com.fiuady.compustore.db;

import java.util.List;

/**
 * Created by Kuro on 02/04/2017.
 */

public class OrderAssemblies {
    private int id;
    private List<Assembly> assemblies;
    private List<Integer> qty;

    public OrderAssemblies(int id, List<Assembly> assemblies, List<Integer> qty) {
        this.id = id;
        this.assemblies = assemblies;
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Assembly> getAssemblies() {
        return assemblies;
    }

    public void setAssemblies(List<Assembly> assemblies) {
        this.assemblies = assemblies;
    }

    public List<Integer> getQty() {
        return qty;
    }

    public void setQty(List<Integer> qty) {
        this.qty = qty;
    }
}
