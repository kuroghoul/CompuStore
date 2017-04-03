package com.fiuady.compustore.db;

/**
 * Created by Kuro on 02/04/2017.
 */

public class Assemblies {
    private int id;
    private String description;

    public Assemblies(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
