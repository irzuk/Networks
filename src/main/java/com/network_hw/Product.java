package com.network_hw;


public class Product {
    static Integer UNIQUE_ID = 0;
    private Integer id;
    private String name;
    private String description;
    // private TODO add image

    Product(String name, String description) {
        this.name = name;
        this.description = description;
        synchronized (UNIQUE_ID) {
            id = UNIQUE_ID;
            UNIQUE_ID++;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // public getImage

    public void updateProduct(String newName, String newDescription) {
       setName(name);
       setDescription(description);
    }

}
