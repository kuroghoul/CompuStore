package com.fiuady.compustore.db;

/**
 * Created by Kuro on 02/04/2017.
 */

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String phone1;
    private String phone2;
    private String phone3;
    private String email;

    public Customer(int id, String firstName, String lastName, String address, String phone1, String phone2, String phone3, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.phone3 = phone3;
        this.email = email;
    }

    public Customer(Customer customer)
    {
        this.id = customer.getId();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.address = customer.getAddress();
        this.phone1 = customer.getPhone1();
        this.phone2 = customer.getPhone2();
        this.phone3 = customer.getPhone3();
        this.email = customer.getEmail();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAdress(String adress) {
        this.address = address;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(firstName).append(" ").append(lastName);
        return builder.toString();
    }
}
