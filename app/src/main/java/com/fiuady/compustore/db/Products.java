package com.fiuady.compustore.db;

/**
 * Created by Kuro on 02/04/2017.
 */

public class Products {
    private int id;
    private ProductCategories productCategories;
    private String description;
    private int price;
    private int qty;

    public Products(int id, ProductCategories productCategories, String description, int price, int qty) {
        this.id = id;
        this.productCategories = productCategories;
        this.description = description;
        this.price = price;
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProductCategories getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(ProductCategories productCategories) {
        this.productCategories = productCategories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
