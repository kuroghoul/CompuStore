package com.fiuady.compustore.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuro on 02/04/2017.
 */

public class AssemblyProducts {
    private int assemblyId;
    private int productId;
    private int qty;

    public AssemblyProducts(int assemblyId, int productId, int qty) {
        this.assemblyId = assemblyId;
        this.productId = productId;
        this.qty = qty;
    }

    public int getAssemblyId() {
        return assemblyId;
    }

    public void setAssemblyId(int assemblyId) {
        this.assemblyId = assemblyId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
