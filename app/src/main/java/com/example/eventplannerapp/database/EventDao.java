package com.example.eventplannerapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventplannerapp.models.Event;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    long insertEvent(Event event);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);

    @Query("SELECT * FROM events ORDER BY date ASC")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events WHERE id = :eventId")
    Event getEventById(int eventId);

    @Query("SELECT * FROM events WHERE category = :category ORDER BY date ASC")
    List<Event> getEventsByCategory(String category);

    @Query("SELECT * FROM events WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    List<Event> searchEvents(String query);
}