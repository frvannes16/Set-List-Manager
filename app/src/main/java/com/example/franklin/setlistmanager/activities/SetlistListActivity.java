package com.example.franklin.setlistmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franklin.setlistmanager.R;
import com.example.franklin.setlistmanager.helpers.SetList;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SetlistListActivity extends AppCompatActivity
    implements AdapterView.OnItemClickListener{

    public static final String SETLIST_REF = "SETLIST_REF";
    public static final String USER_REF = "USER_REF";
    private String TAG = "SETLIST_LIST_ACTIVITY";

    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    // Data
    private SetListAdapter mAdapter;

    // UI
    private ListView mSetlistLV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlist_list);

        // Firebase setup
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("setlists");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        Query userSetLists = myRef.child(user.getUid());

        // UI setup
        mSetlistLV = (ListView) findViewById(R.id.setlist_LV);
        mSetlistLV.setOnItemClickListener(this);
        mAdapter = new SetListAdapter(userSetLists);

        mSetlistLV.setAdapter(mAdapter);  // Pushes Firebase ref to ListView
        // Ready
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    public void addSetlist(View view) {
        Intent createSetList = new Intent(this, NewSetListActivity.class);
        startActivity(createSetList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String setlistref = mAdapter.getRef(position).getKey();
        String userref = mAdapter.getRef(position).getParent().getKey();
        Intent intent = new Intent(this, SetlistActivity.class);
        intent.putExtra(SETLIST_REF, setlistref);
        intent.putExtra(USER_REF, userref);
        startActivity(intent);
    }

    private class SetListAdapter extends FirebaseListAdapter<SetList> {

        SetListAdapter(Query ref) {
            super(SetlistListActivity.this, SetList.class, android.R.layout.simple_list_item_1, ref);
        }

        SetListAdapter(DatabaseReference ref) {
            super(SetlistListActivity.this, SetList.class, android.R.layout.simple_list_item_1, ref);
        }

        @Override
        protected void populateView(View v, SetList setlist, int position) {
            ((TextView) v.findViewById(android.R.id.text1)).setText(setlist.getName());
        }

    }
}
