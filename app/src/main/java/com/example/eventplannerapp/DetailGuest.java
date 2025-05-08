package com.example.eventplannerapp;

public class DetailGuest {
    private String name;
    private GuestStatus status;
    private String id;  // For unique identification

    public DetailGuest(String name, GuestStatus status) {
        this.name = name;
        this.status = status;
        this.id = String.valueOf(System.currentTimeMillis());  // Simple unique ID generation
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GuestStatus getStatus() {
        return status;
    }

    public void setStatus(GuestStatus status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public enum GuestStatus {
        ATTENDING,
        DECLINED,
        PENDING
    }
}