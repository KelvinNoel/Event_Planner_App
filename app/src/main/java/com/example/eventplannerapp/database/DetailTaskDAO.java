package com.example.eventplannerapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventplannerapp.models.DetailTask;

import java.util.List;

@Dao
public interface DetailTaskDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTask(DetailTask task);

    @Update
    int updateTask(DetailTask task);

    @Delete
    int deleteTask(DetailTask task);

    @Query("SELECT * FROM detail_tasks ORDER BY createdAt DESC")
    List<DetailTask> getAllTasks();

    @Query("SELECT * FROM detail_tasks WHERE isCompleted = :completed ORDER BY createdAt DESC")
    List<DetailTask> getTasksByCompletion(boolean completed);

    @Query("SELECT * FROM detail_tasks WHERE venue LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    List<DetailTask> searchTasks(String query);

    @Query("SELECT COUNT(*) FROM detail_tasks")
    int getTotalTaskCount();

    @Query("SELECT COUNT(*) FROM detail_tasks WHERE isCompleted = 1")
    int getCompletedTaskCount();
}