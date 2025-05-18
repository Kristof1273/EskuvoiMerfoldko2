package com.example.eskuvoihelyszinlefoglaloapp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("MyJobService", "JobScheduler aktiválódott!"); // 🔹 Ellenőrzés, hogy fut-e
        NotificationHelper.sendNotification(this);
        return false;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
