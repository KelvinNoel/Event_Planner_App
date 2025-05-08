package com.example.eventplannerapp.models;

public class Guest {
    private String name;
    private boolean confirmed;

    public Guest(String name, boolean confirmed) {
        this.name = name;
        this.confirmed = confirmed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}