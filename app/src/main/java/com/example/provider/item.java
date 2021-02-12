package com.example.provider;

public class item {

    private String name;
    private String id;

    public item(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
