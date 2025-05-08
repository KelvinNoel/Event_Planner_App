package com.example.eventplannerapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplannerapp.database.EventDatabase;
import com.example.eventplannerapp.models.Event;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateEventActivity extends AppCompatActivity {

    private EventDatabase eventDatabase;
    private ExecutorService executorService;
    private final Calendar myCalendar = Calendar.getInstance();
    private TextInputEditText dateEditText;
    private TextInputEditText timeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize executor service for background tasks
        executorService = Executors.newSingleThreadExecutor();

        MaterialButton createButton = findViewById(R.id.buttonCreateEvent);

        TextInputEditText eventNameEditText = findViewById(R.id.editTextEventName);
        dateEditText = findViewById(R.id.editTextDate);
        timeEditText = findViewById(R.id.editTextTime);
        TextInputEditText venueEditText = findViewById(R.id.editTextVenue);
        TextInputEditText categoryEditText = findViewById(R.id.editTextCategory);
        TextInputEditText descriptionEditText = findViewById(R.id.editTextDescription);
        TextInputEditText budgetEditText = findViewById(R.id.editTextBudget);
        TextInputEditText taskEditText = findViewById(R.id.editTextTask);
        TextInputEditText guestEditText = findViewById(R.id.editTextGuest);

        // Initialize the Room database
        eventDatabase = EventDatabase.getInstance(this);

        // Set up DatePickerDialog
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate();
        };

        // Open DatePicker when user clicks on date field
        dateEditText.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(CreateEventActivity.this, date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        // Set up TimePicker
        timeEditText.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this,
                    (view, hourOfDay, selectedMinute) -> {
                        String amPm = hourOfDay < 12 ? "AM" : "PM";
                        int displayHour = hourOfDay > 12 ? hourOfDay - 12 : hourOfDay;
                        displayHour = displayHour == 0 ? 12 : displayHour; // Handle midnight
                        timeEditText.setText(String.format(Locale.getDefault(), "%d:%02d %s",
                                displayHour, selectedMinute, amPm));
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        // When Create button is clicked, insert data into the database
        createButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText() != null ? eventNameEditText.getText().toString() : "";
            String eventDate = dateEditText.getText() != null ? dateEditText.getText().toString() : "";
            String eventTime = timeEditText.getText() != null ? timeEditText.getText().toString() : "";
            String venue = venueEditText.getText() != null ? venueEditText.getText().toString() : "";
            String category = categoryEditText.getText() != null ? categoryEditText.getText().toString() : "";
            String description = descriptionEditText.getText() != null ? descriptionEditText.getText().toString() : "";
            String budgetText = budgetEditText.getText() != null ? budgetEditText.getText().toString() : "0";
            String task = taskEditText.getText() != null ? taskEditText.getText().toString() : "";
            String guest = guestEditText.getText() != null ? guestEditText.getText().toString() : "";

            if (TextUtils.isEmpty(eventName) || TextUtils.isEmpty(eventDate) ||
                    TextUtils.isEmpty(eventTime) || TextUtils.isEmpty(venue)) {
                Toast.makeText(CreateEventActivity.this, "Please fill out all the required fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            double budget = 0.0;
            try {
                if (!TextUtils.isEmpty(budgetText)) {
                    budget = Double.parseDouble(budgetText);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(CreateEventActivity.this, "Please enter a valid budget amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the event object AND POPULATE IT WITH DATA
            Event newEvent = new Event();
            newEvent.setName(eventName);
            newEvent.setDate(eventDate);
            newEvent.setTime(eventTime);
            newEvent.setVenue(venue);
            newEvent.setCategory(category);
            newEvent.setDescription(description);
            newEvent.setNotes(task + "\n\nGuests: " + guest); // Store task and guest in notes field

            // Insert the event into the database in a background thread
            executorService.execute(() -> {
                eventDatabase.eventDao().insertEvent(newEvent);
                runOnUiThread(() -> {
                    Toast.makeText(CreateEventActivity.this, "Event Created!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close this activity after creating the event
                });
            });
        });
    }

    private void updateDate() {
        String myFormat = "MMM dd, yyyy"; // Format for displaying the date
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}