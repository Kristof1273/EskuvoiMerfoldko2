package com.example.eskuvoihelyszinlefoglaloapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class VenueActivity extends AppCompatActivity {
    private static final String LOG_TAG = VenueActivity.class.getName();
    private FirebaseUser user;
    private FirebaseFirestore mfirebaseStore;
    private CollectionReference mVenues;
    private List<Venue> venueList = new ArrayList<>();
    private VenueAdapter adapter;
    private Spinner spinnerSortOptions;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mfirebaseStore = FirebaseFirestore.getInstance();
        mVenues = mfirebaseStore.collection("venues");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewVenues);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VenueAdapter(venueList, this);
        recyclerView.setAdapter(adapter);

        // 🔹 Eredeti gombok megtartása
        Button buttonViewBookings = findViewById(R.id.buttonViewBookings);
        buttonViewBookings.setOnClickListener(view -> startActivity(new Intent(this, MyBookingsActivity.class)));

        Button buttonCreateVenue = findViewById(R.id.buttonCreateVenue);
        buttonCreateVenue.setOnClickListener(view -> startActivity(new Intent(this, VenueCreationActivity.class)));

        Button buttonUserVenues = findViewById(R.id.buttonUserVenues);
        buttonUserVenues.setOnClickListener(view -> startActivity(new Intent(this, UserVenuesActivity.class)));

        // 🔹 Spinner inicializálása és kezelése
        spinnerSortOptions = findViewById(R.id.spinnerSortOptions);
        spinnerSortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Név szerint növekvő")) {
                    loadVenuesSortedByName(Query.Direction.ASCENDING);
                } else if (selectedOption.equals("Név szerint csökkenő")) {
                    loadVenuesSortedByName(Query.Direction.DESCENDING);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 🔹 SearchView kezelése (keresés név alapján)
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadVenuesFilteredByName(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                loadVenuesFilteredByName(newText);
                return true;
            }
        });

        uploadVenuesToFirestore(); // 🔹 Alaphelyszínek feltöltése, ha szükséges
        loadVenuesFromFirestore(); // 🔹 Adatok betöltése Firestore-ból
    }

    // 🔹 Feltöltés Firestore-ba (előre beállított helyszínek)
    private void uploadVenuesToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("venues")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        List<Venue> venues = new ArrayList<>();
                        venues.add(new Venue("Rózsakert", R.drawable.rozsakert, "Budapest, Rózsakert utca 5"));
                        venues.add(new Venue("Aranyhal tó", R.drawable.aranyhalto, "Szeged, Tisza Lajos utca 10"));
                        venues.add(new Venue("Kalocsai Kastély", R.drawable.kalocsai_kastely, "Kalocsa, Kastély utca 2"));
                        venues.add(new Venue("Duna Palota", R.drawable.duna_palota, "Budapest, Duna korzó 10"));

                        for (Venue venue : venues) {
                            db.collection("venues").add(venue)
                                    .addOnSuccessListener(documentReference -> Log.d(LOG_TAG, "Sikeresen hozzáadva: " + venue.getName()))
                                    .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt az adatfeltöltés során!", e));
                        }
                    } else {
                        Log.d(LOG_TAG, "Az előre beállított helyszínek már léteznek, nem töltjük fel újra.");
                    }
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a helyszínek ellenőrzésekor!", e));
    }

    // 🔹 Név szerinti növekvő/csökkenő rendezés
    private void loadVenuesSortedByName(Query.Direction direction) {
        mVenues.orderBy("name", direction)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        venueList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String location = document.getString("location");
                            int imageResourceId = getImageResourceForVenue(name);
                            venueList.add(new Venue(name, imageResourceId, location));
                        }
                        adapter.notifyDataSetChanged();
                        Log.d(LOG_TAG, "Helyszínek név szerint rendezve!");
                    } else {
                        Log.e(LOG_TAG, "Hiba történt az adatok lekérésekor!", task.getException());
                    }
                });
    }

    // 🔹 Keresés név alapján
    private void loadVenuesFilteredByName(String query) {
        String searchQuery = query.toLowerCase();

        mVenues.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                venueList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name").toLowerCase();
                    if (name.contains(searchQuery)) {
                        String location = document.getString("location");
                        int imageResourceId = getImageResourceForVenue(name);
                        venueList.add(new Venue(name, imageResourceId, location));
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.e(LOG_TAG, "Hiba történt az adatok lekérésekor!", task.getException());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVenuesFromFirestore(); // 🔹 Automatikusan frissíti a listát
    }

    private void loadVenuesFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(LOG_TAG, "Felhasználó nincs bejelentkezve!");
            return;
        }

        String currentUserId = user.getUid(); // 🔹 Bejelentkezett felhasználó ID-ja

        mVenues.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                venueList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String ownerId = document.getString("ownerId"); // 🔹 Hirdetés tulajdonosa
                    if (ownerId == null || !ownerId.equals(currentUserId)) {
                        // 🔹 Csak azokat adjuk hozzá, amelyek NEM a bejelentkezett user hirdetései
                        String name = document.getString("name");
                        String location = document.getString("location");
                        int imageResourceId = getImageResourceForVenue(name);
                        venueList.add(new Venue(name, imageResourceId, location));
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.e(LOG_TAG, "Hiba történt az adatok lekérésekor!", task.getException());
            }
        });
    }



    private int getImageResourceForVenue(String venueName) {
        switch (venueName) {
            case "Rózsakert":
                return R.drawable.rozsakert;
            case "Aranyhal tó":
                return R.drawable.aranyhalto;
            case "Kalocsai Kastély":
                return R.drawable.kalocsai_kastely;
            case "Duna Palota":
                return R.drawable.duna_palota;
            default:
                return R.drawable.aranyhalto;
        }
    }

}
