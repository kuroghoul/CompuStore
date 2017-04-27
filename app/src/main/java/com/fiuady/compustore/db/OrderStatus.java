package com.fiuady.compustore.db;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Kuro on 02/04/2017.
 */

public class OrderStatus {
    private int id;
    private String description;
    private boolean editable; //podría ser booleana, pero puse int de momento para evitar complicaciones
    private ArrayList<Integer> previous; //posiblemente se necesite impementar de otra forma
    private ArrayList <Integer> next; //lo mismo para este

    public OrderStatus(int id, String description, boolean editable, String previous, String next) {
        this.id = id;
        this.description = description;
        this.editable = editable;

        this.previous = new ArrayList<Integer>();
        for (String s : previous.split("\\s*,\\s*"))
        {
            if(!s.equals("-")) {
                this.previous.add(Integer.valueOf(s));
            }
        }

        this.next = new ArrayList<Integer>();
        for (String s : next.split("\\s*,\\s*"))
        {
            if(!s.equals("-")) {
                this.next.add(Integer.valueOf(s));
            }
        }
    }
    //Creo que de este no hay que hacer setters


    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEditable() {
        return editable;
    }

    public ArrayList<Integer> getPrevious() {
        return previous;
    }

    public ArrayList<Integer> getNext() {
        return next;
    }
}
