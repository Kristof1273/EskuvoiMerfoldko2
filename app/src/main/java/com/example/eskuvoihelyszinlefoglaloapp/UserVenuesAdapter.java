package com.example.eskuvoihelyszinlefoglaloapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class UserVenuesAdapter extends RecyclerView.Adapter<UserVenuesAdapter.UserVenueViewHolder> {
    private List<Venue> userVenueList;
    private Context context;

    public UserVenuesAdapter(List<Venue> userVenueList, Context context) {
        this.userVenueList = userVenueList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserVenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_venue, parent, false); // 🔹 Külön fájl használata
        return new UserVenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVenueViewHolder holder, int position) {
        Venue venue = userVenueList.get(position);
        holder.textVenueName.setText(venue.getName());
        holder.imageVenue.setImageResource(venue.getImageResourceId());

        holder.itemView.setScaleX(0.7f);
        holder.itemView.setScaleY(0.7f);
        holder.itemView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(500)
                .setStartDelay(position * 100)
                .start();


        // 🔹 Törlés gomb eseménykezelése
        holder.buttonDeleteVenue.setOnClickListener(view -> {
            deleteVenueFromFirestore(venue);
        });
        holder.buttonEditVenue.setOnClickListener(view -> {
            Intent intent = new Intent(context, VenueEditActivity.class);
            intent.putExtra("VENUE_NAME", venue.getName());
            intent.putExtra("VENUE_LOCATION", venue.getLocation());
            intent.putExtra("VENUE_IMAGE", venue.getImageResourceId());
            context.startActivity(intent);
        });
    }

    private void deleteVenueFromFirestore(Venue venue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("venues").whereEqualTo("name", venue.getName())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        db.collection("venues").document(document.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    userVenueList.remove(venue);
                                    notifyDataSetChanged();
                                    Intent intent = new Intent(context, VenueActivity.class);
                                    context.startActivity(intent);
                                })
                                .addOnFailureListener(e -> e.printStackTrace());
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    @Override
    public int getItemCount() {
        return userVenueList.size();
    }

    static class UserVenueViewHolder extends RecyclerView.ViewHolder {
        public View buttonEditVenue;
        TextView textVenueName;
        ImageView imageVenue;
        Button buttonDeleteVenue;

        public UserVenueViewHolder(@NonNull View itemView) {
            super(itemView);
            textVenueName = itemView.findViewById(R.id.textVenueName);
            imageVenue = itemView.findViewById(R.id.imageVenue);
            buttonDeleteVenue = itemView.findViewById(R.id.buttonDeleteVenue);
            buttonEditVenue = itemView.findViewById(R.id.buttonEditVenue);
        }
    }
}
