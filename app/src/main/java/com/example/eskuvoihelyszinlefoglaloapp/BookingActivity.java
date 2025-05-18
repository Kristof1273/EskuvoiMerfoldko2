package com.example.eskuvoihelyszinlefoglaloapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {
    private static final String LOG_TAG = BookingActivity.class.getName();
    private String selectedDate = ""; // Tárolja a kiválasztott dátumot

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        String venueName = getIntent().getStringExtra("VENUE_NAME");
        TextView textSelectedVenue = findViewById(R.id.textSelectedVenue);
        textSelectedVenue.setText("Helyszín: " + venueName);

        CalendarView calendarView = findViewById(R.id.calendarView);
        Button buttonConfirmBooking = findViewById(R.id.buttonConfirmBooking);

        // 🔹 A kiválasztott dátum eltárolása (Javított verzió)
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
            Log.d(LOG_TAG, "Kiválasztott dátum: " + selectedDate); // Ellenőrzés Logcat-ben
        });

        buttonConfirmBooking.setOnClickListener(view -> saveBookingToFirestore(venueName));


    }

    // 🔹 Foglalás mentése Firestore-ba
    private void saveBookingToFirestore(String venueName) {
        if (selectedDate.isEmpty()) {
            Log.e(LOG_TAG, "Nincs kiválasztott dátum!");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "unknown_user";

        Map<String, Object> booking = new HashMap<>();
        booking.put("venueName", venueName);
        booking.put("date", selectedDate); // 🔹 Javított dátum formátum
        booking.put("status", "Confirmed");

        db.collection("bookings").document(userId) // Felhasználó ID szerint mentjük
                .collection("userBookings") // Felhasználó saját foglalásai
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Log.d(LOG_TAG, "Foglalás sikeresen mentve! ID: " + documentReference.getId());

                    // 🔹 Navigáció a MyBookingsActivity-be a sikeres mentés után
                    Intent intent = new Intent(BookingActivity.this, MyBookingsActivity.class);
                    startActivity(intent);
                    finish(); // 🔹 Bezárjuk a BookingActivity-t, hogy ne lehessen visszalépni
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a mentés során!", e));
    }
}
