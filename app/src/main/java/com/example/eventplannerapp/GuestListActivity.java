package com.example.eventplannerapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class GuestListActivity extends AppCompatActivity {

    // UI Components
    private TextView attendingCountTextView;
    private TextView declinedCountTextView;
    private TextView pendingCountTextView;
    private TextView totalGuestsTextView;
    private EditText searchEditText;
    private TextView seeAllAttendingTextView;
    private TextView seeAllDeclinedTextView;
    private TextView seeAllPendingTextView;

    // Guest data
    private int attendingCount = 60;
    private int declinedCount = 30;
    private int pendingCount = 10;
    private int totalGuests = 100;

    // Sample guest data
    private List<Guest> attendingGuests;
    private List<Guest> declinedGuests;
    private List<Guest> pendingGuests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_list);

        initializeGuestData();
        initializeViews();
        setupListeners();
        updateGuestCounts();
    }

    private void initializeGuestData() {
        attendingGuests = new ArrayList<>();
        attendingGuests.add(new Guest("Barry Bruce", GuestStatus.ATTENDING));
        attendingGuests.add(new Guest("Allen Patrick", GuestStatus.ATTENDING));

        declinedGuests = new ArrayList<>();
        declinedGuests.add(new Guest("Tony Wallen", GuestStatus.DECLINED));

        pendingGuests = new ArrayList<>();
        pendingGuests.add(new Guest("Chris Adam", GuestStatus.PENDING));
    }

    private void initializeViews() {
        // Initialize toolbar
        MaterialToolbar toolbar = findViewById(R.id.backButton);
        setSupportActionBar(toolbar);

        // Initialize count TextViews
        attendingCountTextView = findViewById(R.id.attendingCountTextView);
        declinedCountTextView = findViewById(R.id.declinedCountTextView);
        pendingCountTextView = findViewById(R.id.pendingCountTextView);
        totalGuestsTextView = findViewById(R.id.totalGuestsTextView);

        // Initialize search EditText
        searchEditText = findViewById(R.id.searchEditText);

        // Initialize "See All" TextViews
        seeAllAttendingTextView = findViewById(R.id.seeAllAttendingTextView);
        seeAllDeclinedTextView = findViewById(R.id.seeAllDeclinedTextView);
        seeAllPendingTextView = findViewById(R.id.seeAllPendingTextView);
    }

    private void setupListeners() {
        // Set up back navigation
        MaterialToolbar toolbar = findViewById(R.id.backButton);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase().trim();
                if (!searchText.isEmpty()) {
                    filterGuests(searchText);
                } else {
                    Toast.makeText(GuestListActivity.this, "Showing all guests", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Set up "See All" click listeners
        seeAllAttendingTextView.setOnClickListener(v ->
                Toast.makeText(this, "Showing all attending guests", Toast.LENGTH_SHORT).show());

        seeAllDeclinedTextView.setOnClickListener(v ->
                Toast.makeText(this, "Showing all declined guests", Toast.LENGTH_SHORT).show());

        seeAllPendingTextView.setOnClickListener(v ->
                Toast.makeText(this, "Showing all pending guests", Toast.LENGTH_SHORT).show());

        setupGuestItemClickListeners();
    }

    private void setupGuestItemClickListeners() {
        // Set up guest card click listeners
        View barryCard = findViewById(R.id.barryBruceCard);
        if (barryCard != null) {
            barryCard.setOnClickListener(v ->
                    Toast.makeText(this, "Selected Barry Bruce", Toast.LENGTH_SHORT).show());
        }

        View allenCard = findViewById(R.id.allenPatrickCard);
        if (allenCard != null) {
            allenCard.setOnClickListener(v ->
                    Toast.makeText(this, "Selected Allen Patrick", Toast.LENGTH_SHORT).show());
        }

        View tonyCard = findViewById(R.id.tonyWallenCard);
        if (tonyCard != null) {
            tonyCard.setOnClickListener(v ->
                    Toast.makeText(this, "Selected Tony Wallen", Toast.LENGTH_SHORT).show());
        }

        View chrisCard = findViewById(R.id.chrisAdamCard);
        if (chrisCard != null) {
            chrisCard.setOnClickListener(v ->
                    Toast.makeText(this, "Selected Chris Adam", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateGuestCounts() {
        if (attendingCountTextView != null) {
            attendingCountTextView.setText(String.valueOf(attendingCount));
        }
        if (declinedCountTextView != null) {
            declinedCountTextView.setText(String.valueOf(declinedCount));
        }
        if (pendingCountTextView != null) {
            pendingCountTextView.setText(String.valueOf(pendingCount));
        }
        if (totalGuestsTextView != null) {
            totalGuestsTextView.setText(String.valueOf(totalGuests));
        }
    }

    private void filterGuests(String searchText) {
        // Implement search filtering logic here
        List<Guest> filteredAttending = new ArrayList<>();
        List<Guest> filteredDeclined = new ArrayList<>();
        List<Guest> filteredPending = new ArrayList<>();

        // Filter attending guests
        for (Guest guest : attendingGuests) {
            if (guest.getName().toLowerCase().contains(searchText)) {
                filteredAttending.add(guest);
            }
        }

        // Filter declined guests
        for (Guest guest : declinedGuests) {
            if (guest.getName().toLowerCase().contains(searchText)) {
                filteredDeclined.add(guest);
            }
        }

        // Filter pending guests
        for (Guest guest : pendingGuests) {
            if (guest.getName().toLowerCase().contains(searchText)) {
                filteredPending.add(guest);
            }
        }

        // Update UI with filtered results
        // For now, just show a toast message
        int totalFound = filteredAttending.size() + filteredDeclined.size() + filteredPending.size();
        Toast.makeText(this, "Found " + totalFound + " guests matching: " + searchText,
                Toast.LENGTH_SHORT).show();
    }

    public static class Guest {
        private final String name;
        private final GuestStatus status;

        public Guest(String name, GuestStatus status) {
            this.name = name;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public GuestStatus getStatus() {
            return status;
        }
    }

    public enum GuestStatus {
        ATTENDING,
        DECLINED,
        PENDING
    }
}