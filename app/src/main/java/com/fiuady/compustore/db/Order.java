package com.fiuady.compustore.db;

/**
 * Created by Kuro on 02/04/2017.
 */

public class Order {
    private int id;
    private OrderStatus orderStatus;
    private Customer customer;
    private String date; //Probablemente lo cambie después a objeto Date, de momento como String para probar rapidamente
    private String changeLog;

    public Order(int id, OrderStatus orderStatus, Customer customer, String date, String changeLog) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.customer = customer;
        this.date = date;
        this.changeLog = changeLog;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }
}
