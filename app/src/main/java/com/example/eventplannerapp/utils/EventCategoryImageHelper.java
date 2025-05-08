package com.example.eventplannerapp.utils;

import com.example.eventplannerapp.R;

/**
 * Helper class to map event categories to their respective header images
 */
public class EventCategoryImageHelper {

    /**
     * Returns the appropriate image resource ID for a given event category
     *
     * @param category The event category string
     * @return The resource ID for the corresponding image
     */
    public static int getImageResourceForCategory(String category) {
        if (category == null) {
            return R.drawable.header_default;
        }

        switch (category.toLowerCase()) {
            case "birthday":
                return R.drawable.header_birthday;
            case "wedding":
                return R.drawable.header_wedding;
            case "meeting":
                return R.drawable.header_meeting;
            case "conference":
                return R.drawable.header_conference;
            case "party":
                return R.drawable.header_party;
            case "dinner":
                return R.drawable.header_dinner;
            case "workshop":
                return R.drawable.header_workshop;
            default:
                return R.drawable.header_default;
        }
    }
}