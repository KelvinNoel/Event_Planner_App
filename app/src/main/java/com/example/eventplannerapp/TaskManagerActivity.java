package com.example.eventplannerapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TaskManagerActivity extends AppCompatActivity {

    private TextView tvTotalTasks, tvCompleteTasks;
    private Button btnAdd, btnSort, btnSeeAll;
    private ImageButton btnBack;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        // Initialize views
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvCompleteTasks = findViewById(R.id.tvCompleteTasks);
        btnAdd = findViewById(R.id.btnAdd);
        btnSort = findViewById(R.id.btnSort);
        btnSeeAll = findViewById(R.id.btnSeeAll);
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);

        // Set initial data
        tvTotalTasks.setText("10");
        tvCompleteTasks.setText("06");

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
                Toast.makeText(TaskManagerActivity.this, "Add button clicked", Toast.LENGTH_SHORT).show();
                // Implement add task functionality here
            }
        });

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskManagerActivity.this, "Sort button clicked", Toast.LENGTH_SHORT).show();
                // Implement sort functionality here
            }
        });

        btnSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskManagerActivity.this, "See All button clicked", Toast.LENGTH_SHORT).show();
                // Implement see all functionality here
            }
        });
    }
}