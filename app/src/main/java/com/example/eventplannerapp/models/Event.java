package com.example.eventplannerapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String date;
    private String time;
    private String venue;
    private String category;
    private String description;
    private double budget;
    private String tasks;
    private String guests;

    // Add a no-argument constructor required by Room
    public Event() {
        // Required empty constructor
    }

    public Event(String name, String date, String time, String venue, String category, String description, double budget, String task, String guest) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.category = category;
        this.description = description;
        this.budget = budget;
        this.tasks = "";
        this.guests = "";
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getTasks() {
        return tasks;
    }

    public void setTasks(String tasks) {
        this.tasks = tasks;
    }

    public String getGuests() {
        return guests;
    }

    public void setGuests(String guests) {
        this.guests = guests;
    }

    public void setNotes(String notes) {

    }
}