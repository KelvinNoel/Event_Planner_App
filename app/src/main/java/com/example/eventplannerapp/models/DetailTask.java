package com.example.eventplannerapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "detail_tasks")
public class DetailTask {
    @PrimaryKey
    @NonNull
    private String id;
    private String venue;
    private String description;
    private boolean isCompleted;
    private long createdAt;

    // Default constructor
    public DetailTask() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.isCompleted = false;
    }

    // Constructor with parameters
    public DetailTask(String venue, String description) {
        this.id = UUID.randomUUID().toString();
        this.venue = venue;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
        this.isCompleted = false;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}