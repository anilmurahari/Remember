package edu.niu.cs.z1761257.grocerylist;

/**
 * Created by Pravin on 5/5/16.
 */
public class Grocery_Item {

    private int id;
    private String name;
    private int status;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public Grocery_Item(int id, String name, int status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
}
