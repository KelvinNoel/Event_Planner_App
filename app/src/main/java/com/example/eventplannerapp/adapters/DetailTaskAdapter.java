package com.example.eventplannerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplannerapp.R;
import com.example.eventplannerapp.models.DetailTask;

import java.util.ArrayList;
import java.util.List;

public class DetailTaskAdapter extends RecyclerView.Adapter<DetailTaskAdapter.DetailTaskViewHolder> {

    private List<DetailTask> detailTasks;
    private Context context;
    private OnTaskChangeListener listener;

    public DetailTaskAdapter(Context context) {
        this.context = context;
        this.detailTasks = new ArrayList<>();
    }

    public interface OnTaskChangeListener {
        void onTaskStatusChanged(DetailTask task, int position);
        void onTaskClicked(DetailTask task, int position);
    }

    public void setOnTaskChangeListener(OnTaskChangeListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DetailTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detail_task, parent, false);
        return new DetailTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailTaskViewHolder holder, int position) {
        DetailTask task = detailTasks.get(position);

        holder.tvVenue.setText(task.getVenue());
        holder.cbStatus.setChecked(task.isCompleted());

        holder.cbStatus.setOnClickListener(v -> {
            task.setCompleted(holder.cbStatus.isChecked());
            if (listener != null) {
                listener.onTaskStatusChanged(task, position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClicked(task, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return detailTasks.size();
    }

    public void setDetailTasks(List<DetailTask> detailTasks) {
        this.detailTasks = detailTasks;
        notifyDataSetChanged();
    }

    public void addDetailTask(DetailTask task) {
        detailTasks.add(task);
        notifyItemInserted(detailTasks.size() - 1);
    }

    public void updateDetailTask(DetailTask task, int position) {
        detailTasks.set(position, task);
        notifyItemChanged(position);
    }

    public void removeDetailTask(int position) {
        detailTasks.remove(position);
        notifyItemRemoved(position);
    }

    public List<DetailTask> getDetailTasks() {
        return detailTasks;
    }

    // Filter tasks based on search text
    public void filterTasks(String query) {
        // Implementation would depend on how you want to store and filter your data
        // For now, this is left as a placeholder
    }

    // Sort tasks by venue or completion status
    public void sortByVenue(boolean ascending) {
        if (ascending) {
            detailTasks.sort((t1, t2) -> t1.getVenue().compareToIgnoreCase(t2.getVenue()));
        } else {
            detailTasks.sort((t1, t2) -> t2.getVenue().compareToIgnoreCase(t1.getVenue()));
        }
        notifyDataSetChanged();
    }

    public void sortByCompletion(boolean completedFirst) {
        if (completedFirst) {
            detailTasks.sort((t1, t2) -> Boolean.compare(t2.isCompleted(), t1.isCompleted()));
        } else {
            detailTasks.sort((t1, t2) -> Boolean.compare(t1.isCompleted(), t2.isCompleted()));
        }
        notifyDataSetChanged();
    }

    public void sortByCreatedDate(boolean newest) {
        if (newest) {
            detailTasks.sort((t1, t2) -> Long.compare(t2.getCreatedAt(), t1.getCreatedAt()));
        } else {
            detailTasks.sort((t1, t2) -> Long.compare(t1.getCreatedAt(), t2.getCreatedAt()));
        }
        notifyDataSetChanged();
    }

    static class DetailTaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvVenue;
        CheckBox cbStatus;

        DetailTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVenue = itemView.findViewById(R.id.tvTaskVenue);
            cbStatus = itemView.findViewById(R.id.cbTaskStatus);
        }
    }
}