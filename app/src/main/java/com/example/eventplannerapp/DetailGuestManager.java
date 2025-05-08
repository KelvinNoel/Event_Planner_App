package com.example.eventplannerapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager class for handling guest list operations
 */
public class DetailGuestManager {
    private final List<DetailGuest> allGuests;
    private List<DetailGuest> filteredGuests;
    private static DetailGuestManager instance;

    private DetailGuestManager() {
        allGuests = new ArrayList<>();
        filteredGuests = new ArrayList<>();
    }

    public static synchronized DetailGuestManager getInstance() {
        if (instance == null) {
            instance = new DetailGuestManager();
        }
        return instance;
    }

    public void addGuest(String name, DetailGuest.GuestStatus status) {
        DetailGuest newGuest = new DetailGuest(name, status);
        allGuests.add(newGuest);
        if (filteredGuests != allGuests) {
            // Add to filtered list if it matches current filter
            filteredGuests.add(newGuest);
        }
    }

    public void removeGuest(String id) {
        DetailGuest guestToRemove = null;
        for (DetailGuest guest : allGuests) {
            if (guest.getId().equals(id)) {
                guestToRemove = guest;
                break;
            }
        }

        if (guestToRemove != null) {
            allGuests.remove(guestToRemove);
            filteredGuests.remove(guestToRemove);
        }
    }

    public void updateGuestStatus(String id, DetailGuest.GuestStatus newStatus) {
        for (DetailGuest guest : allGuests) {
            if (guest.getId().equals(id)) {
                guest.setStatus(newStatus);
                break;
            }
        }
    }

    public List<DetailGuest> getAllGuests() {
        return new ArrayList<>(allGuests);
    }

    public List<DetailGuest> getGuestsByStatus(DetailGuest.GuestStatus status) {
        List<DetailGuest> statusGuests = new ArrayList<>();
        for (DetailGuest guest : allGuests) {
            if (guest.getStatus() == status) {
                statusGuests.add(guest);
            }
        }
        filteredGuests = statusGuests;
        return filteredGuests;
    }

    public List<DetailGuest> searchGuests(String query) {
        if (query == null || query.isEmpty()) {
            filteredGuests = new ArrayList<>(allGuests);
            return filteredGuests;
        }

        String searchQuery = query.toLowerCase().trim();
        List<DetailGuest> searchResults = new ArrayList<>();

        for (DetailGuest guest : allGuests) {
            if (guest.getName().toLowerCase().contains(searchQuery)) {
                searchResults.add(guest);
            }
        }

        filteredGuests = searchResults;
        return filteredGuests;
    }

    public int getAttendingCount() {
        int count = 0;
        for (DetailGuest guest : allGuests) {
            if (guest.getStatus() == DetailGuest.GuestStatus.ATTENDING) {
                count++;
            }
        }
        return count;
    }

    public int getDeclinedCount() {
        int count = 0;
        for (DetailGuest guest : allGuests) {
            if (guest.getStatus() == DetailGuest.GuestStatus.DECLINED) {
                count++;
            }
        }
        return count;
    }

    public int getPendingCount() {
        int count = 0;
        for (DetailGuest guest : allGuests) {
            if (guest.getStatus() == DetailGuest.GuestStatus.PENDING) {
                count++;
            }
        }
        return count;
    }

    public int getTotalCount() {
        return allGuests.size();
    }

    public void clearAllGuests() {
        allGuests.clear();
        filteredGuests.clear();
    }
}