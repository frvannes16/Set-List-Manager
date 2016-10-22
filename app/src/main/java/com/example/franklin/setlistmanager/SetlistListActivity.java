package com.example.franklin.setlistmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SetlistListActivity extends AppCompatActivity {

    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    // Data
    private ArrayList<SetList> setlists = new ArrayList<>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlist_list);
        database = FirebaseDatabase.getInstance();
        myRef =  database.getReference();
    }

    public void addSetlist(View view) {
        Intent createSetList = new Intent(this, NewSetListActivity.class);
        startActivity(createSetList);
    }
}
