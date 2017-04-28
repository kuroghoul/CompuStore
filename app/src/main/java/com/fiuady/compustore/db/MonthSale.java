package com.fiuady.compustore.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Kuro on 28/04/2017.
 */

public class MonthSale {
    private Calendar calendar;
    private int total;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    public MonthSale (String date, int total)
    {
        this.total = total;
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

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
