package com.example.eventplannerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    // Declare views
    private TextView eventTitleTextView;
    private TextView venueTextView;
    private TextView timeTextView;
    private TextView totalGuestsTextView;
    private TextView totalBudgetTextView;
    private PieChart attendancePieChart;
    private PieChart budgetPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize views
        eventTitleTextView = findViewById(R.id.text_event_title_name);
        venueTextView = findViewById(R.id.text_event_venue);
        timeTextView = findViewById(R.id.text_event_time);
        totalGuestsTextView = findViewById(R.id.text_total_guests);
        totalBudgetTextView = findViewById(R.id.budgetBreakdownTextView);
        attendancePieChart = findViewById(R.id.pieChart);
        budgetPieChart = findViewById(R.id.budgetPieChart);

        // Get data from Intent
        String eventTitle = getIntent().getStringExtra("eventTitle");
        String venue = getIntent().getStringExtra("venue");
        String time = getIntent().getStringExtra("time");
        int totalGuests = getIntent().getIntExtra("totalGuests", 0);

        // Set text views
        eventTitleTextView.setText(eventTitle);
        venueTextView.setText(venue);
        timeTextView.setText(time);

        // Set up attendance pie chart
        setupAttendancePieChart();
        // Set up budget pie chart
        setupBudgetPieChart();
        totalGuestsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(DetailsActivity.this, GuestListActivity.class);
            startActivity(intent);
        });
        totalBudgetTextView.setOnClickListener(v -> {
            // Intent to go to the GuestListActivity
            Intent intent = new Intent(DetailsActivity.this, BudgetTrackerActivity.class);
            startActivity(intent);
        });

    }



    // Method to setup attendance pie chart
    private void setupAttendancePieChart() {
        // Sample attendance data
        int confirmedGuests = 60;
        int pendingGuests = 30;
        int declinedGuests = 10;

        // Create dataset with entries
        ArrayList<PieEntry> attendanceEntries = getPieEntries(
                new float[]{confirmedGuests, pendingGuests, declinedGuests},
                new String[]{"Confirmed", "Pending", "Declined"}
        );

        PieDataSet dataSet = new PieDataSet(attendanceEntries, "Attendance Breakdown");

        // Set colors for attendance chart
        dataSet.setColors(new int[]{
                getResources().getColor(android.R.color.holo_green_light), // confirmed - green
                getResources().getColor(android.R.color.holo_orange_light), // pending - orange
                getResources().getColor(android.R.color.holo_red_light)  // declined - red
        });

        PieData pieData = new PieData(dataSet);
        attendancePieChart.setData(pieData);

        // Optional enhancements
        attendancePieChart.getDescription().setEnabled(false);
        attendancePieChart.setDrawHoleEnabled(true);
        attendancePieChart.setUsePercentValues(true);
        attendancePieChart.setEntryLabelTextSize(12f);

        attendancePieChart.invalidate(); // Refresh the chart
    }

    // Method to setup budget pie chart
    private void setupBudgetPieChart() {
        // Sample budget data
        float catering = 3000f;  // $3000 for catering
        float venue = 5000f;     // $5000 for venue
        float decor = 2000f;     // $2000 for decoration

        // Create dataset with entries
        ArrayList<PieEntry> budgetEntries = getPieEntries(
                new float[]{catering, venue, decor},
                new String[]{"Catering", "Venue", "Decor"}
        );

        PieDataSet budgetDataSet = new PieDataSet(budgetEntries, "Budget Breakdown");

        // Set colors for budget chart (different from attendance chart)
        budgetDataSet.setColors(new int[]{
                getResources().getColor(android.R.color.holo_blue_light),  // catering - blue
                getResources().getColor(android.R.color.holo_purple),      // venue - purple
                getResources().getColor(android.R.color.holo_green_dark)   // decor - dark green
        });

        PieData budgetPieData = new PieData(budgetDataSet);
        budgetPieChart.setData(budgetPieData);

        // Optional enhancements
        budgetPieChart.getDescription().setEnabled(false);
        budgetPieChart.setDrawHoleEnabled(true);
        budgetPieChart.setUsePercentValues(true);
        budgetPieChart.setEntryLabelTextSize(12f);

        budgetPieChart.invalidate(); // Refresh the chart
    }

    // Helper method to create pie chart data entries
    private ArrayList<PieEntry> getPieEntries(float[] values, String[] labels) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            entries.add(new PieEntry(values[i], labels[i]));
        }
        return entries;
    }
}