package com.example.eskuvoihelyszinlefoglaloapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class VenueActivity extends AppCompatActivity {
    private static final String LOG_TAG = VenueActivity.class.getName();
    private FirebaseUser user;
    public void navigate_to_login(View view) {
        Intent intent = new Intent(this, loginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = new View(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG,"Authenticated user");
        }else{Log.d(LOG_TAG,"Authenticating user failed!"); navigate_to_login(view);}
        // nem tudom jo e mert megorzi adatot




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewVenues);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Mintaadatok a helyszínekhez
        List<Venue> venues = new ArrayList<>();
        venues.add(new Venue("Rózsakert", R.drawable.rozsakert, "Budapest, Rózsakert utca 5"));
        venues.add(new Venue("Aranyhal tó", R.drawable.aranyhalto, "Szeged, Tisza Lajos utca 10"));
        venues.add(new Venue("Kalocsai Kastély", R.drawable.kalocsai_kastely, "Kalocsa, Kastély utca 2"));
        venues.add(new Venue("Duna Palota", R.drawable.duna_palota, "Budapest, Duna korzó 10"));

        VenueAdapter adapter = new VenueAdapter(venues, this);
        recyclerView.setAdapter(adapter);
    }
}
