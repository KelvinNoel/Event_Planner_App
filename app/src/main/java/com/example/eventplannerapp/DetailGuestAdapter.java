package com.example.eventplannerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class DetailGuestAdapter extends RecyclerView.Adapter<DetailGuestAdapter.DetailGuestViewHolder> {

    private List<DetailGuest> guestList;
    private final Context context;
    private final DetailGuestActionListener actionListener;

    public DetailGuestAdapter(Context context, DetailGuestActionListener listener) {
        this.context = context;
        this.guestList = new ArrayList<>();
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public DetailGuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guest, parent, false);
        return new DetailGuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailGuestViewHolder holder, int position) {
        DetailGuest guest = guestList.get(position);
        holder.bind(guest);
    }

    @Override
    public int getItemCount() {
        return guestList.size();
    }

    public void setGuestList(List<DetailGuest> guests) {
        this.guestList = guests;
        notifyDataSetChanged();
    }

    public void addGuest(DetailGuest guest) {
        guestList.add(guest);
        notifyItemInserted(guestList.size() - 1);
    }

    public void removeGuest(int position) {
        if (position >= 0 && position < guestList.size()) {
            guestList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateGuestStatus(int position, DetailGuest.GuestStatus newStatus) {
        if (position >= 0 && position < guestList.size()) {
            DetailGuest guest = guestList.get(position);
            guest.setStatus(newStatus);
            notifyItemChanged(position);
        }
    }

    public List<DetailGuest> getGuestList() {
        return new ArrayList<>(guestList);
    }

    public List<DetailGuest> getGuestsByStatus(DetailGuest.GuestStatus status) {
        List<DetailGuest> filteredGuests = new ArrayList<>();
        for (DetailGuest guest : guestList) {
            if (guest.getStatus() == status) {
                filteredGuests.add(guest);
            }
        }
        return filteredGuests;
    }

    public int getAttendingCount() {
        return getGuestsByStatus(DetailGuest.GuestStatus.ATTENDING).size();
    }

    public int getDeclinedCount() {
        return getGuestsByStatus(DetailGuest.GuestStatus.DECLINED).size();
    }

    public int getPendingCount() {
        return getGuestsByStatus(DetailGuest.GuestStatus.PENDING).size();
    }

    // View holder for guest items
    class DetailGuestViewHolder extends RecyclerView.ViewHolder {
        private final TextView textGuestName;
        private final Chip chipConfirmed;
        private final ImageButton btnDeleteGuest;

        DetailGuestViewHolder(View itemView) {
            super(itemView);
            textGuestName = itemView.findViewById(R.id.text_guest_name);
            chipConfirmed = itemView.findViewById(R.id.chip_confirmed);
            btnDeleteGuest = itemView.findViewById(R.id.btn_delete_guest);

            // Set up click listeners
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onGuestClick(guestList.get(position), position);
                }
            });

            chipConfirmed.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onStatusClick(guestList.get(position), position);
                }
            });

            btnDeleteGuest.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onDeleteClick(guestList.get(position), position);
                }
            });
        }

        void bind(DetailGuest guest) {
            textGuestName.setText(guest.getName());

            // Update chip appearance based on status
            switch (guest.getStatus()) {
                case ATTENDING:
                    chipConfirmed.setText("Attending");
                    chipConfirmed.setChipBackgroundColor(
                            ContextCompat.getColorStateList(context, R.color.colorPending));
                    break;
                case DECLINED:
                    chipConfirmed.setText("Declined");
                    chipConfirmed.setChipBackgroundColor(
                            ContextCompat.getColorStateList(context, R.color.colorConfirmed));
                    break;
                case PENDING:
                    chipConfirmed.setText("Pending");
                    chipConfirmed.setChipBackgroundColor(
                            ContextCompat.getColorStateList(context, android.R.color.darker_gray));
                    break;
            }
        }
    }

    // Interface for handling guest interactions
    public interface DetailGuestActionListener {
        void onGuestClick(DetailGuest guest, int position);
        void onStatusClick(DetailGuest guest, int position);
        void onDeleteClick(DetailGuest guest, int position);
    }
}