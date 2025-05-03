package com.example.eventplannerapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BudgetTrackerActivity extends AppCompatActivity {

    private TextView tvTotalBudget, tvSpend, tvRemaining;
    private Button btnAdd, btnSort, btnSeeAll;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_tracker);

        // Initialize views
        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        tvSpend = findViewById(R.id.tvSpend);
        tvRemaining = findViewById(R.id.tvRemaining);
        btnAdd = findViewById(R.id.btnAdd);
        btnSort = findViewById(R.id.btnSort);
        btnSeeAll = findViewById(R.id.btnSeeAll);
        btnBack = findViewById(R.id.btnBack);

        // Set up click listeners
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BudgetTrackerActivity.this, "Add button clicked", Toast.LENGTH_SHORT).show();
                // Implement add functionality here
            }
        });

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BudgetTrackerActivity.this, "Sort button clicked", Toast.LENGTH_SHORT).show();
                // Implement sort functionality here
            }
        });

        btnSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BudgetTrackerActivity.this, "See All button clicked", Toast.LENGTH_SHORT).show();
                // Implement see all functionality here
            }
        });
    }
}