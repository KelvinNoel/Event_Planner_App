package com.example.eventplannerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplannerapp.adapters.GuestAdapter;
import com.example.eventplannerapp.adapters.TaskAdapter;
import com.example.eventplannerapp.database.EventDatabase;
import com.example.eventplannerapp.models.Event;
import com.example.eventplannerapp.models.Guest;
import com.example.eventplannerapp.models.Task;
import com.example.eventplannerapp.utils.EventCategoryImageHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailsActivity extends AppCompatActivity {

    private EventDatabase eventDatabase;
    private ExecutorService executorService;
    private int eventId;
    private Event currentEvent;

    private RecyclerView tasksRecyclerView;
    private RecyclerView guestsRecyclerView;
    private TaskAdapter taskAdapter;
    private GuestAdapter guestAdapter;
    private List<Task> taskList = new ArrayList<>();
    private List<Guest> guestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        eventDatabase = EventDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        setSupportActionBar(findViewById(R.id.topAppBar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("eventId")) {
            eventId = getIntent().getIntExtra("eventId", -1);
        } else {
            String eventTitle = getIntent().getStringExtra("eventTitle");
            CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
            collapsingToolbar.setTitle(eventTitle != null ? eventTitle : "Event Details");
            showError("No event ID provided");
            return;
        }

        setupRecyclerViews();

        ExtendedFloatingActionButton fabEditEvent = findViewById(R.id.fab_edit_event);
        fabEditEvent.setOnClickListener(v -> {
            if (currentEvent != null) {
                Intent editIntent = new Intent(DetailsActivity.this, EditEventActivity.class);
                editIntent.putExtra("eventId", eventId);
                startActivity(editIntent);
            }
        });

        findViewById(R.id.btn_add_task).setOnClickListener(v -> {
            if (currentEvent != null) {
                showAddTaskDialog();
            }
        });

        findViewById(R.id.btn_add_guest).setOnClickListener(v -> {
            if (currentEvent != null) {
                showAddGuestDialog();
            }
        });

        loadEventDetails(eventId);
    }

    private void setupRecyclerViews() {
        tasksRecyclerView = findViewById(R.id.tasks_recycler_view);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, this::onTaskStatusChanged, this::onTaskDeleted);
        tasksRecyclerView.setAdapter(taskAdapter);

        guestsRecyclerView = findViewById(R.id.guests_recycler_view);
        guestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        guestAdapter = new GuestAdapter(guestList, this::onGuestDeleted, this::onGuestClicked);
        guestsRecyclerView.setAdapter(guestAdapter);
    }

    private void loadEventDetails(int eventId) {
        executorService.execute(() -> {
            currentEvent = eventDatabase.eventDao().getEventById(eventId);
            if (currentEvent != null) {
                parseTasks(currentEvent.getTasks());
                parseGuests(currentEvent.getGuests());
                runOnUiThread(this::updateUI);
            } else {
                runOnUiThread(() -> showError("Event not found"));
            }
        });
    }

    private void updateUI() {
        if (currentEvent == null) return;

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(currentEvent.getName());

        findViewById(R.id.event_header_image).setBackgroundResource(
                EventCategoryImageHelper.getImageResourceForCategory(currentEvent.getCategory()));

        TextView dateTimeTextView = findViewById(R.id.text_date_time);
        TextView venueTextView = findViewById(R.id.text_venue);
        TextView categoryTextView = findViewById(R.id.text_category);
        TextView descriptionTextView = findViewById(R.id.text_description);
        TextView budgetTextView = findViewById(R.id.text_budget);
        TextView tasksTextView = findViewById(R.id.text_tasks);
        TextView guestsTextView = findViewById(R.id.text_guests);

        String dateTime = currentEvent.getDate() + " at " + currentEvent.getTime();
        dateTimeTextView.setText(dateTime);
        venueTextView.setText(currentEvent.getVenue());
        categoryTextView.setText(currentEvent.getCategory());
        descriptionTextView.setText(currentEvent.getDescription());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        budgetTextView.setText(currencyFormat.format(currentEvent.getBudget()));

        if (taskList.isEmpty()) {
            tasksRecyclerView.setVisibility(View.GONE);
            tasksTextView.setVisibility(View.VISIBLE);
            tasksTextView.setText("No tasks added yet");
        } else {
            tasksRecyclerView.setVisibility(View.VISIBLE);
            tasksTextView.setVisibility(View.GONE);
            taskAdapter.notifyDataSetChanged();
        }

        if (guestList.isEmpty()) {
            guestsRecyclerView.setVisibility(View.GONE);
            guestsTextView.setVisibility(View.VISIBLE);
            guestsTextView.setText("No guests added yet");
        } else {
            guestsRecyclerView.setVisibility(View.VISIBLE);
            guestsTextView.setVisibility(View.GONE);
            guestAdapter.notifyDataSetChanged();
        }
    }

    private void parseTasks(String tasksString) {
        taskList.clear();
        if (tasksString != null && !tasksString.isEmpty()) {
            String[] taskArray = tasksString.split(",");
            for (String task : taskArray) {
                String trimmedTask = task.trim();
                if (!trimmedTask.isEmpty()) {
                    boolean isCompleted = trimmedTask.startsWith("[x]");
                    if (trimmedTask.startsWith("[x]") || trimmedTask.startsWith("[ ]")) {
                        trimmedTask = trimmedTask.substring(3).trim();
                    }
                    taskList.add(new Task(trimmedTask, isCompleted));
                }
            }
        }
    }

    private void parseGuests(String guestsString) {
        guestList.clear();
        if (guestsString != null && !guestsString.isEmpty()) {
            String[] guestArray = guestsString.split(",");
            for (String guest : guestArray) {
                String trimmedGuest = guest.trim();
                if (!trimmedGuest.isEmpty()) {
                    boolean confirmed = trimmedGuest.endsWith("(confirmed)");
                    if (confirmed) {
                        trimmedGuest = trimmedGuest.substring(0, trimmedGuest.length() - 11).trim();
                    }
                }
            }
        }
    }

    private void saveTasks() {
        if (currentEvent == null) return;

        StringBuilder tasksBuilder = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            tasksBuilder.append(task.isCompleted() ? "[x] " : "[ ] ").append(task.getDescription());
            if (i < taskList.size() - 1) tasksBuilder.append(", ");
        }

        String tasks = tasksBuilder.toString();
        executorService.execute(() -> {
            currentEvent.setTasks(tasks);
            eventDatabase.eventDao().updateEvent(currentEvent);
        });
    }

    private void saveGuests() {
        if (currentEvent == null) return;

        StringBuilder guestsBuilder = new StringBuilder();
        for (int i = 0; i < guestList.size(); i++) {
            Guest guest = guestList.get(i);
            guestsBuilder.append(guest.getName());
            if (guest.isConfirmed()) guestsBuilder.append(" (confirmed)");
            if (i < guestList.size() - 1) guestsBuilder.append(", ");
        }

        String guests = guestsBuilder.toString();
        executorService.execute(() -> {
            currentEvent.setGuests(guests);
            eventDatabase.eventDao().updateEvent(currentEvent);
        });
    }

    private void onTaskStatusChanged(int position) {
        Task task = taskList.get(position);
        task.setCompleted(!task.isCompleted());
        taskAdapter.notifyItemChanged(position);
        saveTasks();
    }

    private void onTaskDeleted(int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskList.remove(position);
                    taskAdapter.notifyItemRemoved(position);
                    saveTasks();
                    updateUI();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void onGuestDeleted(int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Remove Guest")
                .setMessage("Are you sure you want to remove this guest?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    guestList.remove(position);
                    guestAdapter.notifyItemRemoved(position);
                    saveGuests();
                    updateUI();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Fixed onGuestClicked method in DetailsActivity
    // Fixed implementation - navigate to GuestListActivity when any guest card is clicked
    private void onGuestClicked(int position) {
        // Simply navigate to GuestListActivity with the event ID
        // This will show the complete guest list for this event
        Intent intent = new Intent(DetailsActivity.this, GuestListActivity.class);
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }

    private void showAddTaskDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Add New Task")
                .setView(R.layout.dialog_add_task)
                .setPositiveButton("Add", (dialog, which) -> {
                    Task newTask = new Task("New task added", false);
                    taskList.add(newTask);
                    taskAdapter.notifyItemInserted(taskList.size() - 1);
                    saveTasks();
                    updateUI();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddGuestDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Add New Guest")
                .setView(R.layout.dialog_add_guest)
                .setPositiveButton("Add", (dialog, which) -> {
                    Guest newGuest = new Guest("New Guest", false);
                    guestList.add(newGuest);
                    guestAdapter.notifyItemInserted(guestList.size() - 1);
                    saveGuests();
                    updateUI();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_share) {
            shareEventDetails();
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteEventDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareEventDetails() {
        if (currentEvent != null) {
            String shareText = "Event: " + currentEvent.getName() + "\n" +
                    "Date & Time: " + currentEvent.getDate() + " at " + currentEvent.getTime() + "\n" +
                    "Venue: " + currentEvent.getVenue() + "\n" +
                    "Category: " + currentEvent.getCategory() + "\n" +
                    "Description: " + currentEvent.getDescription();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share Event Details"));
        }
    }

    private void showDeleteEventDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    executorService.execute(() -> {
                        eventDatabase.eventDao().deleteEvent(currentEvent);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
