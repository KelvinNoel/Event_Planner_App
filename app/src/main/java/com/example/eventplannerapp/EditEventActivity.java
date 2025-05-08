package com.example.eventplannerapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplannerapp.database.EventDatabase;
import com.example.eventplannerapp.models.Event;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditEventActivity extends AppCompatActivity {

    private EventDatabase eventDatabase;
    private ExecutorService executorService;
    private int eventId;
    private Event currentEvent;

    private EditText nameEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private EditText venueEditText;
    private Spinner categorySpinner;
    private EditText descriptionEditText;
    private EditText budgetEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Initialize database and executor
        eventDatabase = EventDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Event");

        // Initialize views
        initializeViews();

        // Set up category spinner with adapter
        setupCategorySpinner();

        // Get event ID from intent
        if (getIntent().hasExtra("eventId")) {
            eventId = getIntent().getIntExtra("eventId", -1);
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup date picker
        dateEditText.setOnClickListener(v -> showDatePicker());

        // Setup time picker
        timeEditText.setOnClickListener(v -> showTimePicker());

        // Setup save button
        Button saveButton = findViewById(R.id.btn_save);
        saveButton.setOnClickListener(v -> saveEvent());
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.edit_event_name);
        dateEditText = findViewById(R.id.edit_event_date);
        timeEditText = findViewById(R.id.edit_event_time);
        venueEditText = findViewById(R.id.edit_event_venue);
        categorySpinner = findViewById(R.id.spinner_category);
        descriptionEditText = findViewById(R.id.edit_event_description);
        budgetEditText = findViewById(R.id.edit_event_budget);
    }

    private void setupCategorySpinner() {
        String[] categories = getResources().getStringArray(R.array.event_categories);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        categorySpinner.setAdapter(adapter);

        // Set default selection if needed
        if (categories.length > 0) {
            categorySpinner.setSelection(0);
        }
    }

    private void loadEventDetails(int eventId) {
        executorService.execute(() -> {
            currentEvent = eventDatabase.eventDao().getEventById(eventId);

            if (currentEvent != null) {
                runOnUiThread(() -> {
                    // Populate fields with event data
                    nameEditText.setText(currentEvent.getName());
                    dateEditText.setText(currentEvent.getDate());
                    timeEditText.setText(currentEvent.getTime());
                    venueEditText.setText(currentEvent.getVenue());

                    // Set spinner selection based on category
                    String[] categories = getResources().getStringArray(R.array.event_categories);
                    for (int i = 0; i < categories.length; i++) {
                        if (categories[i].equals(currentEvent.getCategory())) {
                            categorySpinner.setSelection(i);
                            break;
                        }
                    }

                    descriptionEditText.setText(currentEvent.getDescription());

                    // Format budget with appropriate locale
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
                    String budgetStr = currencyFormat.format(currentEvent.getBudget())
                            .replace(currencyFormat.getCurrency().getSymbol(), "")
                            .trim();
                    budgetEditText.setText(budgetStr);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select event date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            dateEditText.setText(dateFormat.format(new Date(selection)));
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Select event time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = timePicker.getHour();
            int selectedMinute = timePicker.getMinute();

            String period = selectedHour >= 12 ? "PM" : "AM";
            int hour12Format = selectedHour > 12 ? selectedHour - 12 : (selectedHour == 0 ? 12 : selectedHour);

            String minuteString = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
            String timeString = hour12Format + ":" + minuteString + " " + period;

            timeEditText.setText(timeString);
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void saveEvent() {
        if (!validateInputs()) {
            return;
        }

        String name = nameEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String venue = venueEditText.getText().toString().trim();

        // Fix: Add null check for categorySpinner.getSelectedItem()
        Object selectedItem = categorySpinner.getSelectedItem();
        String category = selectedItem != null ? selectedItem.toString() : "";

        // Add validation for category
        if (category.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        String description = descriptionEditText.getText().toString().trim();

        // Parse budget value
        double budget = 0.0;
        try {
            String budgetText = budgetEditText.getText().toString().trim()
                    .replace(",", ""); // Remove commas
            budget = Double.parseDouble(budgetText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid budget amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update current event object
        currentEvent.setName(name);
        currentEvent.setDate(date);
        currentEvent.setTime(time);
        currentEvent.setVenue(venue);
        currentEvent.setCategory(category);
        currentEvent.setDescription(description);
        currentEvent.setBudget(budget);

        // Save to database
        executorService.execute(() -> {
            eventDatabase.eventDao().updateEvent(currentEvent);
            runOnUiThread(() -> {
                Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private boolean validateInputs() {
        if (nameEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dateEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select event date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (timeEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select event time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (venueEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter event venue", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (budgetEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter event budget", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Add validation for category spinner
        if (categorySpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select an event category", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}