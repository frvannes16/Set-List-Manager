package com.example.franklin.setlistmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franklin.setlistmanager.R;
import com.example.franklin.setlistmanager.helpers.AuthHelper;
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

    // Data
    private SetListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlist_list);

        // Firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("setlists");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onCreate: User not logged in");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        Query userSetLists = myRef.child(user.getUid());

        // UI setup
        ListView mSetlistLV = (ListView) findViewById(R.id.setlist_LV);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setlist_list_menu, menu);  // substitute in our custom menu.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selected.
        switch(item.getItemId()){
            case R.id.menu_help:
                Toast.makeText(this, "Help!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_share:
                Toast.makeText(this, "Share!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_logout:
                AuthHelper.logout(this);
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
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
