// Fixed GuestAdapter implementation
package com.example.eventplannerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplannerapp.R;
import com.example.eventplannerapp.models.Guest;

import java.util.List;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.GuestViewHolder> {

    private final List<Guest> guestList;
    private final OnGuestDeletedListener deleteListener;
    private final OnGuestClickedListener clickListener;

    public interface OnGuestDeletedListener {
        void onGuestDeleted(int position);
    }

    public interface OnGuestClickedListener {
        void onGuestClicked(int position);
    }

    public GuestAdapter(List<Guest> guestList, OnGuestDeletedListener deleteListener, OnGuestClickedListener clickListener) {
        this.guestList = guestList;
        this.deleteListener = deleteListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guest, parent, false);
        return new GuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        Guest guest = guestList.get(position);
        holder.guestName.setText(guest.getName() + ("(confirmed)"));

        // Use holder.getAdapterPosition() for safety instead of position parameter
        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                deleteListener.onGuestDeleted(adapterPosition);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                clickListener.onGuestClicked(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return guestList.size();
    }

    static class GuestViewHolder extends RecyclerView.ViewHolder {
        TextView guestName;
        ImageButton deleteButton;

        GuestViewHolder(@NonNull View itemView) {
            super(itemView);
            guestName = itemView.findViewById(R.id.text_guest_name);
            deleteButton = itemView.findViewById(R.id.btn_delete_guest);
        }
    }
}