package com.example.eventplannerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplannerapp.R;
import com.example.eventplannerapp.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private final OnTaskStatusChangedListener statusChangedListener;
    private final OnTaskDeleteListener deleteListener;

    public interface OnTaskStatusChangedListener {
        void onTaskStatusChanged(int position);
    }

    public interface OnTaskDeleteListener {
        void onTaskDeleted(int position);
    }

    public TaskAdapter(List<Task> taskList, OnTaskStatusChangedListener statusChangedListener, OnTaskDeleteListener deleteListener) {
        this.taskList = taskList;
        this.statusChangedListener = statusChangedListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskDescription.setText(task.getDescription());
        holder.taskCheckBox.setChecked(task.isCompleted());

        // Set click listeners
        holder.taskCheckBox.setOnClickListener(v -> {
            if (statusChangedListener != null) {
                statusChangedListener.onTaskStatusChanged(holder.getAdapterPosition());
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onTaskDeleted(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheckBox;
        TextView taskDescription;
        ImageButton btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.checkbox_task);
            taskDescription = itemView.findViewById(R.id.text_task_description);
            btnDelete = itemView.findViewById(R.id.btn_delete_task);
        }
    }
}