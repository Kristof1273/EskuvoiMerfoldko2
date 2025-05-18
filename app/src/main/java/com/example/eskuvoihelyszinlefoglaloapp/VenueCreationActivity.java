package com.example.eskuvoihelyszinlefoglaloapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class VenueCreationActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final String LOG_TAG = VenueCreationActivity.class.getName(); // 🔹 LOG_TAG deklarálása
    private EditText editVenueName, editVenueLocation; // 🔹 EditText változók deklarálása
    private ImageView imagePreview; // 🔹 ImageView deklarálása
    private Uri imageUri;
    private Bitmap photoBitmap;
    private FirebaseFirestore db; // 🔹 Firestore adatbázis deklarálása

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_venue);
        db = FirebaseFirestore.getInstance(); // 🔹 Firestore inicializálása
        imagePreview = findViewById(R.id.imagePreview);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        Button buttonSaveVenue = findViewById(R.id.buttonSaveVenue);
        editVenueName = findViewById(R.id.editVenueName);
        editVenueLocation = findViewById(R.id.editVenueLocation);

        // 🔹 Kép kiválasztása galériából
        buttonSelectImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // 🔹 Fotókészítés kamerával
        buttonTakePhoto.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        });

        buttonSaveVenue.setOnClickListener(view -> saveVenueToFirestore());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri); // 🔹 Megjelenítjük a kiválasztott képet
        }
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            photoBitmap = (Bitmap) data.getExtras().get("data");
            imagePreview.setImageBitmap(photoBitmap); // 🔹 Megjelenítjük a készített fotót
        }
    }

    private void saveVenueToFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(LOG_TAG, "Felhasználó nincs bejelentkezve!");
            return;
        }

        String name = editVenueName.getText().toString().trim();
        String location = editVenueLocation.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty()) {
            Log.e(LOG_TAG, "Helyszín neve és címe nem lehet üres!");
            return;
        }

        // 🔹 Ellenőrzés, hogy van-e kép kiválasztva vagy fotó készítve
        if (imageUri == null && photoBitmap == null) {
            Log.e(LOG_TAG, "Nincs kiválasztott vagy készített kép!");
            return;
        }

        Map<String, Object> venue = new HashMap<>();
        venue.put("name", name);
        venue.put("location", location);
        venue.put("ownerId", user.getUid()); // 🔹 Felhasználó ID mentése

        db.collection("venues").add(venue)
                .addOnSuccessListener(documentReference -> {
                    Log.d(LOG_TAG, "Helyszín sikeresen mentve!");
                    finish(); // 🔹 Automatikusan visszatér VenueActivity-be
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a mentés során!", e));
    }
}
