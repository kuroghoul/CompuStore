package com.fiuady.compustore.db;

/**
 * Created by Kuro on 02/04/2017.
 */

public class OrderStatus {
    private int id;
    private String description;
    private int editable; //podría ser booleana, pero puse int de momento para evitar complicaciones
    private String previous; //posiblemente se necesite impementar de otra forma
    private String next; //lo mismo para este

    public OrderStatus(int id, String description, int editable, String previous, String next) {
        this.id = id;
        this.description = description;
        this.editable = editable;
        this.previous = previous;
        this.next = next;
    }
    //Creo que de este no hay que hacer setters


    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getEditable() {
        return editable;
    }

    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }
}
