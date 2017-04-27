package com.fiuady.compustore.db;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static java.text.DateFormat.getDateInstance;


/**
 * Created by Kuro on 02/04/2017.
 */

public class Order {
    private int id;
    private OrderStatus orderStatus;
    private Customer customer;
    private String date; //Probablemente lo cambie después a objeto Date, de momento como String para probar rapidamente
    private Calendar calendar;
    private String changeLog;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    public Order(int id, OrderStatus orderStatus, Customer customer, String date, String changeLog) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.customer = customer;
        this.date = date;
        this.changeLog = changeLog;

        calendar = Calendar.getInstance();
        try{
            calendar.setTime(formatter.parse(date)) ;
        }catch (ParseException e){
            e.printStackTrace();
        }

    }


    public Calendar getCalendar() {

        return calendar;
    }

    //public void setCalendar(Calendar calendar) {
    //    this.calendar = calendar;
    //    date = String.valueOf(calendar.DAY_OF_MONTH) + "-" + String.valueOf(calendar.MONTH) + "-" + String.valueOf(calendar.YEAR);
    //}

    public SimpleDateFormat getFormatter() {
        return formatter;
    }

    public void setFormatter(SimpleDateFormat formatter) {
        this.formatter = formatter;
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
