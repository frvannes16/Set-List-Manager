package com.example.franklin.setlistmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SetlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlist);
        Intent intent = getIntent();
        String refString = (String) intent.getExtras().get(SetlistListActivity.SETLIST_REF);
    }
}
