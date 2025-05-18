package com.example.eskuvoihelyszinlefoglaloapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "AlarmManager aktiválódott!"); // 🔹 Ellenőrzés, hogy fut-e
        NotificationHelper.sendNotification(context);
        Intent serviceIntent = new Intent(context, NotificationService.class);
        context.startService(serviceIntent);
    }

}
