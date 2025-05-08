package com.example.eventplannerapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class GuestListActivity extends AppCompatActivity implements DetailGuestAdapter.DetailGuestActionListener {

    // UI Components
    private TextView attendingCountTextView;
    private TextView declinedCountTextView;
    private TextView pendingCountTextView;
    private TextView totalGuestsTextView;
    private EditText searchEditText;
    private TextView seeAllAttendingTextView;
    private TextView seeAllDeclinedTextView;
    private TextView seeAllPendingTextView;
    private RecyclerView guestRecyclerView;
    private DetailGuestAdapter guestAdapter;
    private FloatingActionButton fabAddGuest;

    // Guest data manager
    private DetailGuestManager guestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_list);

        // Initialize guest manager
        guestManager = DetailGuestManager.getInstance();

        // Initialize sample data if needed
        if (guestManager.getTotalCount() == 0) {
            initializeSampleGuestData();
        }

        initializeViews();
        setupRecyclerView();
        setupListeners();
        updateGuestCounts();
    }

    private void initializeSampleGuestData() {
        // Add some sample guests
        guestManager.addGuest("Barry Bruce", DetailGuest.GuestStatus.ATTENDING);
        guestManager.addGuest("Allen Patrick", DetailGuest.GuestStatus.ATTENDING);
        guestManager.addGuest("Tony Wallen", DetailGuest.GuestStatus.DECLINED);
        guestManager.addGuest("Chris Adam", DetailGuest.GuestStatus.PENDING);
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

        // Initialize RecyclerView
        guestRecyclerView = findViewById(R.id.guestRecyclerView);

        // Initialize FAB
        fabAddGuest = findViewById(R.id.fabAddGuest);
    }

    private void setupRecyclerView() {
        guestAdapter = new DetailGuestAdapter(this, this);
        guestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        guestRecyclerView.setAdapter(guestAdapter);

        // Initialize with all guests
        guestAdapter.setGuestList(guestManager.getAllGuests());
    }

    private void setupListeners() {
        // Set up back navigation
        MaterialToolbar toolbar = findViewById(R.id.backButton);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Set up search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase().trim();
                List<DetailGuest> filteredGuests = guestManager.searchGuests(searchText);
                guestAdapter.setGuestList(filteredGuests);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Set up "See All" click listeners
        seeAllAttendingTextView.setOnClickListener(v -> {
            List<DetailGuest> attendingGuests = guestManager.getGuestsByStatus(DetailGuest.GuestStatus.ATTENDING);
            guestAdapter.setGuestList(attendingGuests);
            Toast.makeText(this, "Showing attending guests", Toast.LENGTH_SHORT).show();
        });

        seeAllDeclinedTextView.setOnClickListener(v -> {
            List<DetailGuest> declinedGuests = guestManager.getGuestsByStatus(DetailGuest.GuestStatus.DECLINED);
            guestAdapter.setGuestList(declinedGuests);
            Toast.makeText(this, "Showing declined guests", Toast.LENGTH_SHORT).show();
        });

        seeAllPendingTextView.setOnClickListener(v -> {
            List<DetailGuest> pendingGuests = guestManager.getGuestsByStatus(DetailGuest.GuestStatus.PENDING);
            guestAdapter.setGuestList(pendingGuests);
            Toast.makeText(this, "Showing pending guests", Toast.LENGTH_SHORT).show();
        });

        // Set up add guest button
        fabAddGuest.setOnClickListener(v -> showAddGuestDialog());
    }

    private void updateGuestCounts() {
        if (attendingCountTextView != null) {
            attendingCountTextView.setText(String.valueOf(guestManager.getAttendingCount()));
        }
        if (declinedCountTextView != null) {
            declinedCountTextView.setText(String.valueOf(guestManager.getDeclinedCount()));
        }
        if (pendingCountTextView != null) {
            pendingCountTextView.setText(String.valueOf(guestManager.getPendingCount()));
        }
        if (totalGuestsTextView != null) {
            totalGuestsTextView.setText(String.valueOf(guestManager.getTotalCount()));
        }
    }

    private void showAddGuestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_guest, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.editTextGuestName);
        RadioGroup statusRadioGroup = dialogView.findViewById(R.id.radioGroupStatus);

        builder.setTitle("Add New Guest")
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Guest name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DetailGuest.GuestStatus status = DetailGuest.GuestStatus.PENDING;
                    int selectedId = statusRadioGroup.getCheckedRadioButtonId();

                    if (selectedId == R.id.radioAttending) {
                        status = DetailGuest.GuestStatus.ATTENDING;
                    } else if (selectedId == R.id.radioDeclined) {
                        status = DetailGuest.GuestStatus.DECLINED;
                    }

                    guestManager.addGuest(name, status);
                    guestAdapter.setGuestList(guestManager.getAllGuests());
                    updateGuestCounts();

                    Toast.makeText(this, "Guest added successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditStatusDialog(DetailGuest guest, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_status, null);
        builder.setView(dialogView);

        RadioGroup statusRadioGroup = dialogView.findViewById(R.id.radioGroupStatus);

        // Set current status
        switch (guest.getStatus()) {
            case ATTENDING:
                statusRadioGroup.check(R.id.radioAttending);
                break;
            case DECLINED:
                statusRadioGroup.check(R.id.radioDeclined);
                break;
            case PENDING:
                statusRadioGroup.check(R.id.radioPending);
                break;
        }

        builder.setTitle("Update Status for " + guest.getName())
                .setPositiveButton("Update", (dialog, which) -> {
                    DetailGuest.GuestStatus newStatus = DetailGuest.GuestStatus.PENDING;
                    int selectedId = statusRadioGroup.getCheckedRadioButtonId();

                    if (selectedId == R.id.radioAttending) {
                        newStatus = DetailGuest.GuestStatus.ATTENDING;
                    } else if (selectedId == R.id.radioDeclined) {
                        newStatus = DetailGuest.GuestStatus.DECLINED;
                    }

                    guestManager.updateGuestStatus(guest.getId(), newStatus);
                    guestAdapter.notifyItemChanged(position);
                    updateGuestCounts();

                    Toast.makeText(this, "Guest status updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmationDialog(DetailGuest guest, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Guest")
                .setMessage("Are you sure you want to remove " + guest.getName() + " from the guest list?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    guestManager.removeGuest(guest.getId());
                    guestAdapter.notifyItemRemoved(position);
                    updateGuestCounts();
                    Toast.makeText(this, "Guest removed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // DetailGuestAdapter.DetailGuestActionListener implementation
    @Override
    public void onGuestClick(DetailGuest guest, int position) {
        Toast.makeText(this, "Selected " + guest.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusClick(DetailGuest guest, int position) {
        showEditStatusDialog(guest, position);
    }

    @Override
    public void onDeleteClick(DetailGuest guest, int position) {
        showDeleteConfirmationDialog(guest, position);
    }
}