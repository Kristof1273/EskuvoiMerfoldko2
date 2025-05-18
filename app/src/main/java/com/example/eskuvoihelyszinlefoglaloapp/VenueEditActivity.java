package com.example.eskuvoihelyszinlefoglaloapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
public class VenueEditActivity extends AppCompatActivity {
    private static final String LOG_TAG = VenueEditActivity.class.getName();
    private EditText editVenueName, editVenueLocation, editVenueImageResource;
    private FirebaseFirestore db;
    private String oldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_venue);

        db = FirebaseFirestore.getInstance();

        editVenueName = findViewById(R.id.editVenueName);
        editVenueLocation = findViewById(R.id.editVenueLocation);
        editVenueImageResource = findViewById(R.id.editVenueImageResource); // 🔹 Új mező hozzáadása
        Button buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        oldName = getIntent().getStringExtra("VENUE_NAME");
        editVenueName.setText(oldName);
        editVenueLocation.setText(getIntent().getStringExtra("VENUE_LOCATION"));
        editVenueImageResource.setText(String.valueOf(getIntent().getIntExtra("VENUE_IMAGE", R.drawable.aranyhalto))); // 🔹 Kitöltés az aktuális értékkel

        buttonSaveChanges.setOnClickListener(view -> updateVenueInFirestore());
    }

    private void updateVenueInFirestore() {
        String newName = editVenueName.getText().toString();
        String newLocation = editVenueLocation.getText().toString();
        int newImageResource;

        try {
            newImageResource = Integer.parseInt(editVenueImageResource.getText().toString());
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Érvénytelen imageResource ID!");
            return;
        }

        Map<String, Object> venueUpdates = new HashMap<>();
        venueUpdates.put("name", newName);
        venueUpdates.put("location", newLocation);
        venueUpdates.put("imageResource", newImageResource);

        db.collection("venues").whereEqualTo("name", oldName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (var document : querySnapshot) {
                        db.collection("venues").document(document.getId()).update(venueUpdates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(LOG_TAG, "Helyszín módosítva: " + newName);
                                    navigateBack();
                                })
                                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a módosítás során!", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt az adatlekérés során!", e));
    }

    private void navigateBack() {
        Intent intent = new Intent(this, UserVenuesActivity.class);
        startActivity(intent);
        finish();
    }
/*
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastVenueName", editVenueName.getText().toString());
        editor.putString("lastVenueLocation", editVenueLocation.getText().toString());
        editor.apply();
        Log.d("VenueEditActivity", "Adatok mentve az onPause-ban!");
    }
*/
}

