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

public class MyBookingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = MyBookingsActivity.class.getName();
    private List<Booking> bookingList = new ArrayList<>();
    private BookingAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings); // 🔹 Ez legyen az első sor!

        recyclerView = findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookingAdapter(bookingList, this);
        recyclerView.setAdapter(adapter);

        loadBookingsFromFirestore(); // 🔹 Foglalások betöltése Firestore-ból
    }

    // 🔹 Felhasználó saját foglalásainak lekérése Firestore-ból
    private void loadBookingsFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(LOG_TAG, "Felhasználó nincs bejelentkezve!");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 🔹 Az új lekérési útvonal megfelelően működik az `userBookings` alkollekcióval
        db.collection("bookings").document(user.getUid()).collection("userBookings")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookingList.clear();
                    if (querySnapshot.isEmpty()) {
                        Log.w(LOG_TAG, "Nincsenek foglalások a Firestore-ban!");
                    } else {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String venueName = document.getString("venueName");
                            String date = document.getString("date");
                            String status = document.getString("status");

                            if (venueName != null && date != null && status != null) {
                                bookingList.add(new Booking(venueName, date, status));
                                Log.d(LOG_TAG, "Foglalás hozzáadva: " + venueName + " - " + date);
                            } else {
                                Log.e(LOG_TAG, "Hibás adat Firestore-ban: " + document.getId());
                            }
                        }

                        Log.d(LOG_TAG, "Foglalások betöltve! Összesen: " + bookingList.size());
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a foglalások lekérésekor!", e));
    }

    private void navigate_to_login() {
        Intent intent = new Intent(this, loginActivity.class);
        startActivity(intent);
        finish();
    }
}
