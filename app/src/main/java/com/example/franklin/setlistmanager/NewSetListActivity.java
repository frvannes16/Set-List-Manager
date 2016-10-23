package com.example.franklin.setlistmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewSetListActivity extends AppCompatActivity {

    private DatabaseReference setListRef;

    EditText mName;
    TextView mCurrentDateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_set_list);

        // UI & content setup
        mName = (EditText) findViewById(R.id.setListName);
        mCurrentDateTV = (TextView) findViewById(R.id.current_date_tv);
        Time date = new Time();
        date.setToNow();
        mCurrentDateTV.setText(date.format("%d %B %y"));

        // Firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        setListRef = database.getReference("setlists");
    }

    public void createNewSetList(View view) {
        String name = mName.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(this, "Please name you new setlist!", Toast.LENGTH_LONG).show();
            return;
        }
        SetList newItem = new SetList(name, FirebaseAuth.getInstance().getCurrentUser().getUid());

        setListRef.child(newItem.getOwnerUID()).push().setValue(newItem);  // root -> setlists -> UserUID -> array of setlists.
        finish();
    }
}
