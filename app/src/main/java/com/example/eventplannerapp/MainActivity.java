package com.example.eventplannerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.eventplannerapp.database.EventDatabase;
import com.example.eventplannerapp.models.Event;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private EventDatabase eventDatabase;
    private ExecutorService executorService;
    private LinearLayout eventsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database and executor service
        eventDatabase = EventDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Find the LinearLayout that will contain our events
        eventsContainer = findViewById(R.id.events_container);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Apply padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinator_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar setup
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // Set up drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_task) {
                Intent intent = new Intent(MainActivity.this, TaskManagerActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // FAB click to launch CreateEventActivity
        FloatingActionButton fabAddEvent = findViewById(R.id.fab_add);
        fabAddEvent.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Set click listeners for category filters
        setupCategoryFilters();

        // Load events from database
        loadEvents();
    }

    private void setupCategoryFilters() {
        TextView weddingCategory = findViewById(R.id.category_wedding);
        TextView birthdayCategory = findViewById(R.id.category_birthday);
        TextView officeCategory = findViewById(R.id.category_office);
        TextView otherCategory = findViewById(R.id.category_other);

        weddingCategory.setOnClickListener(v -> filterEventsByCategory("Wedding"));
        birthdayCategory.setOnClickListener(v -> filterEventsByCategory("Birthday"));
        officeCategory.setOnClickListener(v -> filterEventsByCategory("Office"));
        otherCategory.setOnClickListener(v -> loadEvents()); // Show all events again
    }

    private void filterEventsByCategory(String category) {
        executorService.execute(() -> {
            // In a real app, you would add a method to your DAO to filter by category
            // Here we'll filter from all events
            List<Event> allEvents = eventDatabase.eventDao().getAllEvents();
            List<Event> filteredEvents = new java.util.ArrayList<>();

            for (Event event : allEvents) {
                if (event.getCategory() != null && event.getCategory().toLowerCase().contains(category.toLowerCase())) {
                    filteredEvents.add(event);
                }
            }

            runOnUiThread(() -> {
                eventsContainer.removeAllViews();

                if (filteredEvents.isEmpty()) {
                    TextView noEventsText = new TextView(this);
                    noEventsText.setText("No " + category + " events found");
                    noEventsText.setPadding(16, 16, 16, 16);
                    eventsContainer.addView(noEventsText);
                } else {
                    for (Event event : filteredEvents) {
                        addEventCard(event);
                    }
                }

                Toast.makeText(this, "Showing " + category + " events", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload events when returning to this activity
        loadEvents();
    }

    private void loadEvents() {
        executorService.execute(() -> {
            // Get all events from the database
            List<Event> events = eventDatabase.eventDao().getAllEvents();

            // Update UI on the main thread
            runOnUiThread(() -> {
                // Clear existing event cards
                if (eventsContainer != null) {
                    eventsContainer.removeAllViews();
                }

                // If there are no events, show a message
                if (events.isEmpty()) {
                    TextView noEventsText = new TextView(this);
                    noEventsText.setText("No events found. Create a new event!");
                    noEventsText.setPadding(16, 16, 16, 16);
                    noEventsText.setTextSize(16);
                    noEventsText.setTextColor(getResources().getColor(R.color.black));
                    eventsContainer.addView(noEventsText);
                } else {
                    // Add event cards dynamically
                    for (Event event : events) {
                        addEventCard(event);  // Method to add event cards as shown earlier
                    }
                }
            });
        });
    }


    private void addEventCard(Event event) {
        // Inflate the event card view
        View cardView = LayoutInflater.from(this)
                .inflate(R.layout.event_card_item, eventsContainer, false);

        // Find views in the card
        TextView dateTextView = cardView.findViewById(R.id.text_date);
        TextView titleTextView = cardView.findViewById(R.id.text_title);
        TextView timeTextView = cardView.findViewById(R.id.text_time);
        TextView locationTextView = cardView.findViewById(R.id.text_location);
        LinearLayout dateContainer = cardView.findViewById(R.id.date_container);
        Button deleteButton = cardView.findViewById(R.id.btn_delete_event);
        CardView eventCard = cardView.findViewById(R.id.event_card);

        // Set background color based on category
        String category = event.getCategory();
        int backgroundColor = getBackgroundColorForCategory(category);
        int dateContainerColor = getDateContainerColorForCategory(category);

        eventCard.setCardBackgroundColor(backgroundColor);
        dateContainer.setBackgroundColor(dateContainerColor);

        // Set event data to views
        dateTextView.setText(event.getDate());       // assuming format: "yyyy-MM-dd"
        titleTextView.setText(event.getName());
        timeTextView.setText(event.getTime());       // assuming format: "HH:mm"
        locationTextView.setText(event.getVenue());

        // Open DetailsActivity on card click
        eventCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("eventId", event.getId()); // Room auto-generates IDs if annotated with @PrimaryKey(autoGenerate = true)
            startActivity(intent);
        });

        // Delete event on delete button click
        deleteButton.setOnClickListener(v -> confirmDeleteEvent(event));

        // Add card to container
        eventsContainer.addView(cardView);


    }


    private void confirmDeleteEvent(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Event");
        builder.setMessage("Are you sure you want to delete '" + event.getName() + "'?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            executorService.execute(() -> {
                eventDatabase.eventDao().deleteEvent(event);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                    loadEvents(); // Reload the event list
                });
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int getBackgroundColorForCategory(String category) {
        // Set card background color based on category
        if (category == null) {
            return 0xFFEEEEEE; // Default gray
        }

        category = category.toLowerCase();
        if (category.contains("wedding")) {
            return 0xFFE0F7FA; // Light cyan
        } else if (category.contains("birthday")) {
            return 0xFFFFD580; // Light orange
        } else if (category.contains("office") || category.contains("meeting")) {
            return 0xFF90EE90; // Light green
        } else {
            return 0xFFEEEEEE; // Default gray
        }
    }

    private int getDateContainerColorForCategory(String category) {
        // Set date container color based on category
        if (category == null) {
            return 0xFF666666; // Default dark gray
        }

        category = category.toLowerCase();
        if (category.contains("wedding")) {
            return 0xFF00BCD4; // Cyan
        } else if (category.contains("birthday")) {
            return 0xFFC04000; // Dark orange
        } else if (category.contains("office") || category.contains("meeting")) {
            return 0xFF006400; // Dark green
        } else {
            return 0xFF666666; // Default dark gray
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}