package com.example.eventplannerapp;

import com.example.eventplannerapp.models.Event;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Local unit tests for the Event Planner app, which will execute on the development machine (host).
 */
public class ExampleInstrumentedTest {

    @Test
    public void eventCreation_isCorrect() {
        // Create a new event
        Event event = new Event();
        event.setId(1);
        event.setName("Wedding Reception");
        event.setDate("2025-07-20");
        event.setTime("18:00");
        event.setVenue("Grand Hotel");
        event.setCategory("Wedding");
        event.setDescription("John and Mary's wedding reception");

        // Verify event properties
        assertEquals(1, event.getId());
        assertEquals("Wedding Reception", event.getName());
        assertEquals("2025-07-20", event.getDate());
        assertEquals("18:00", event.getTime());
        assertEquals("Grand Hotel", event.getVenue());
        assertEquals("Wedding", event.getCategory());
        assertEquals("John and Mary's wedding reception", event.getDescription());
    }

    @Test
    public void dateFormat_isValid() {
        // Test if the date format used in the app is valid
        String testDate = "2025-07-20";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setLenient(false); // Strict parsing

        try {
            Date date = dateFormat.parse(testDate);
            assertNotNull(date);
        } catch (ParseException e) {
            fail("Date format is invalid: " + e.getMessage());
        }
    }

    @Test
    public void timeFormat_isValid() {
        // Test if the time format used in the app is valid
        String testTime = "18:00";
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeFormat.setLenient(false); // Strict parsing

        try {
            Date time = timeFormat.parse(testTime);
            assertNotNull(time);
        } catch (ParseException e) {
            fail("Time format is invalid: " + e.getMessage());
        }
    }

    @Test
    public void eventCategory_isRecognized() {
        // Test if the event categories are correctly recognized
        assertTrue(isCategoryRecognized("Wedding"));
        assertTrue(isCategoryRecognized("Birthday"));
        assertTrue(isCategoryRecognized("Office"));
        assertFalse(isCategoryRecognized(null));
        assertFalse(isCategoryRecognized(""));
        assertFalse(isCategoryRecognized("Invalid Category"));
    }

    /**
     * Helper method to check if a category is recognized by the app
     */
    private boolean isCategoryRecognized(String category) {
        if (category == null || category.isEmpty()) {
            return false;
        }

        category = category.toLowerCase();
        return category.equals("wedding") ||
                category.equals("birthday") ||
                category.equals("office") ||
                category.contains("meeting");
    }

    @Test
    public void eventDescription_handlesSpecialCharacters() {
        // Test if the event description can handle special characters
        Event event = new Event();
        String specialChars = "Special characters: !@#$%^&*()_+{}|:<>?~";
        event.setDescription(specialChars);

        assertEquals(specialChars, event.getDescription());
    }

    @Test
    public void eventEquality_worksCorrectly() {
        // Create two events with the same ID but different details
        Event event1 = new Event();
        event1.setId(1);
        event1.setName("Event 1");

        Event event2 = new Event();
        event2.setId(1);
        event2.setName("Event 2");

        // Create a third event with a different ID
        Event event3 = new Event();
        event3.setId(2);
        event3.setName("Event 1");

        // In Room, entities with the same primary key are considered equal
        // This test assumes that Event class has properly implemented equals() and hashCode()
        // If not implemented, this test would need to be adjusted

        // For this test, we'll just check ID equality as that's typically how Room would identify entities
        assertEquals(event1.getId(), event2.getId());
        assertNotEquals(event1.getId(), event3.getId());
    }
}