package com.example.eskuvoihelyszinlefoglaloapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class UserVenuesActivity extends AppCompatActivity {
    private static final String LOG_TAG = UserVenuesActivity.class.getName();
    private List<Venue> userVenueList = new ArrayList<>();
    private UserVenuesAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_venues);

        recyclerView = findViewById(R.id.recyclerViewUserVenues);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserVenuesAdapter(userVenueList, this);
        recyclerView.setAdapter(adapter);

        loadUserVenuesFromFirestore();
    }

    private void loadUserVenuesFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(LOG_TAG, "Felhasználó nincs bejelentkezve!");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("venues")
                .whereEqualTo("ownerId", user.getUid()) // 🔹 Csak az adott felhasználó helyszíneit kérjük le
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    userVenueList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String name = document.getString("name");
                        String location = document.getString("location");
                        Long imageResourceLong = document.getLong("imageResource");
                        int imageResource = (imageResourceLong != null) ? imageResourceLong.intValue() : R.drawable.aranyhalto;

                        userVenueList.add(new Venue(name, imageResource, location));
                    }

                    adapter.notifyDataSetChanged();
                    Log.d(LOG_TAG, "Felhasználó helyszínei betöltve! Összesen: " + userVenueList.size());
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a helyszínek lekérésekor!", e));
    }



    public void deleteVenueFromFirestore(Venue venue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("venues").whereEqualTo("name", venue.getName())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        db.collection("venues").document(document.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(LOG_TAG, "Helyszín törölve: " + venue.getName());

                                    // 🔹 Navigáció vissza a VenueActivity-be
                                    Intent intent = new Intent(UserVenuesActivity.this, VenueActivity.class);
                                    startActivity(intent);
                                    finish(); // 🔹 Bezárjuk az aktuális aktivitást
                                })
                                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a törlés során!", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt az adatlekérés során!", e));
    }


}
