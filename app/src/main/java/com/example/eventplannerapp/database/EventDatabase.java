package com.example.eventplannerapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.eventplannerapp.models.Event;

// Update the version number from whatever it currently is to the next number
// For example, if it was 1, change it to 2
@Database(entities = {Event.class}, version = 2, exportSchema = false)
public abstract class EventDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "event_planner_db";
    private static EventDatabase instance;

    public abstract EventDao eventDao();

    public static synchronized EventDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            EventDatabase.class,
                            DATABASE_NAME
                    )
                    // This will destroy and recreate the database if the schema changes
                    // Only use this for development, not for production apps
                    .fallbackToDestructiveMigration()
                    // For a production app, define proper migration strategies instead
                    // .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return instance;
    }

    // Example of a proper migration strategy (for reference)
    /*
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Implement proper schema migration operations here
            // For example:
            // database.execSQL("ALTER TABLE events ADD COLUMN new_column TEXT");
        }
    };
    */
}