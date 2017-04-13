package com.fiuady.compustore.db;

/**
 * Created by Kuro on 02/04/2017.
 */



public class Product {
    private int id;
    private ProductCategory productCategory;
    private String description;
    private int price;
    private int qty;



    public Product(int id, ProductCategory productCategory, String description, int price, int qty) {
        this.id = id;
        this.productCategory = productCategory;
        this.description = description;
        this.price = price;
        this.qty = qty;
    }

    public Product (Product product)
    {
        this.id = product.getId();
        this.productCategory = product.getProductCategory();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.qty = product.getQty();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
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
