package com.example.franklin.setlistmanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.franklin.setlistmanager.R;
import com.example.franklin.setlistmanager.helpers.MetronomeTask;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SetlistActivity extends AppCompatActivity {

    MetronomeTask metronome;
    long interval;

    ScheduledThreadPoolExecutor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlist);
        Intent intent = getIntent();
        String refString = (String) intent.getExtras().get(SetlistListActivity.SETLIST_REF);

        executor = new ScheduledThreadPoolExecutor(1);
        metronome = new MetronomeTask(120);
        interval = metronome.getInterval();
    }

    @Override
    protected void onResume() {
        super.onResume();
        executor.scheduleAtFixedRate(metronome, 0, interval, TimeUnit.NANOSECONDS);
    }
}
