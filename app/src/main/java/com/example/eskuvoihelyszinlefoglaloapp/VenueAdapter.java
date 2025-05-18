package com.example.eskuvoihelyszinlefoglaloapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.VenueViewHolder> {
    private List<Venue> venueList;
    private Context context;

    public VenueAdapter(List<Venue> venueList, Context context) {
        this.venueList = venueList;
        this.context = context;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_venue, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {
        Venue venue = venueList.get(position);
        holder.textVenueName.setText(venue.getName());
        holder.imageVenue.setImageResource(venue.getImageResourceId());


        // Foglalás gomb: például BookingActivity indítása
        holder.buttonBook.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("VENUE_NAME", venue.getName());
            context.startActivity(intent);
        });

        holder.buttonLocation.setOnClickListener(v -> {
            // Lekérjük a helyszín címét a venue modelből
            String locationQuery = venue.getLocation();

            // Ellenőrizd, hogy a location nem üres
            if(locationQuery == null || locationQuery.trim().isEmpty()){
                Toast.makeText(context, "Nincs megadva helyszín.", Toast.LENGTH_SHORT).show();
                return;
            }

            // URI előállítása – itt opcionálisan használhatunk Uri.encode()-t, de egyszerű esetben ez így is működik
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + locationQuery);

            // Új Intent, amely VIEW műveletet indít a URI-val
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

            // Ha nincs arra szükség, ne kényszerítsd Google Maps-et (tedd kommentbe vagy távolítsd el)
            // mapIntent.setPackage("com.google.android.apps.maps");

            // Ellenőrizzük, hogy van-e olyan alkalmazás, amely kezeli az intentet
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            } else {
                Toast.makeText(context, "Nincs olyan alkalmazás telepítve, ami meg tudja nyitni a helyszínt.", Toast.LENGTH_SHORT).show();
            }
        });
        holder.buttonLocation.setOnClickListener(v -> {
            // Lekérjük a helyszín címét a venue modelből
            String locationQuery = venue.getLocation();

            // Ellenőrizd, hogy a location nem üres
            if(locationQuery == null || locationQuery.trim().isEmpty()){
                Toast.makeText(context, "Nincs megadva helyszín.", Toast.LENGTH_SHORT).show();
                return;
            }

            // URI előállítása – itt opcionálisan használhatunk Uri.encode()-t, de egyszerű esetben ez így is működik
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + locationQuery);

            // Új Intent, amely VIEW műveletet indít a URI-val
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

            // Ha nincs arra szükség, ne kényszerítsd Google Maps-et (tedd kommentbe vagy távolítsd el)
            // mapIntent.setPackage("com.google.android.apps.maps");

            // Ellenőrizzük, hogy van-e olyan alkalmazás, amely kezeli az intentet
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            } else {
                Toast.makeText(context, "Nincs olyan alkalmazás telepítve, ami meg tudja nyitni a helyszínt.", Toast.LENGTH_SHORT).show();
            }
        });
        holder.buttonLocation.setOnClickListener(v -> {
            // Lekérjük a Venue modellből a helyszín string-et
            String locationQuery = venue.getLocation();

            if (locationQuery == null || locationQuery.trim().isEmpty()) {
                Toast.makeText(context, "Nincs megadva helyszín.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Összeállítjuk a Google Maps keresési URL-t
            String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(locationQuery);

            // URI objektum létrehozása
            Uri mapsUri = Uri.parse(url);

            // Intent létrehozása, amely a böngészőből indítja meg az URL megnyitását
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, mapsUri);
            // Adjuk hozzá, hogy böngészőjellegűnek kezelje az intentet:
            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            // Ha esetleg nem Activity kontextusból hívod meg, ez segíthet:
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                // A chooser használata biztosítja, hogy a rendszer felsorolja az elérhető alkalmazásokat
                context.startActivity(Intent.createChooser(browserIntent, "Válassz böngészőt"));
            } catch (Exception ex) {
                Toast.makeText(context, "Nincs elérhető böngésző alkalmazás.", Toast.LENGTH_SHORT).show();
            }
        });
        Animation shimmerAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in_shimmer);
        holder.imageVenue.startAnimation(shimmerAnimation);




    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

    static class VenueViewHolder extends RecyclerView.ViewHolder {
        TextView textVenueName;
        ImageView imageVenue;
        Button buttonBook;
        Button buttonLocation;

        public VenueViewHolder(@NonNull View itemView) {
            super(itemView);
            textVenueName = itemView.findViewById(R.id.textVenueName);
            imageVenue = itemView.findViewById(R.id.imageVenue);
            buttonBook = itemView.findViewById(R.id.buttonBook);
            buttonLocation = itemView.findViewById(R.id.buttonLocation);
        }
    }
}
