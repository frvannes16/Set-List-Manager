package com.example.franklin.setlistmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SetlistListActivity extends AppCompatActivity {

    private String TAG = "SETLIST_LIST_ACTIVITY";

    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    // Data
    private ArrayList<SetList> setlists = new ArrayList<>(0);
    //    private ListView mSetlistLV;
    private TextView mSetListName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlist_list);
        database = FirebaseDatabase.getInstance();
        myRef =  database.getReference("setlists");
//        mSetlistLV = (ListView) findViewById(R.id.setlist_LV);
        mSetListName = (TextView) findViewById(R.id.setlist_name);
        Query userSetLists = myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).limitToFirst(1);
        userSetLists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SetList sl = null;
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    sl = snap.getValue(SetList.class);
                }

                if(sl == null){
                    // do nothing
                    return;
                } else {
                    mSetListName.setText(sl.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "DB cancelled:" + databaseError.getMessage());
            }
        });

    }

    public void addSetlist(View view) {
        Intent createSetList = new Intent(this, NewSetListActivity.class);
        startActivity(createSetList);
    }
}
