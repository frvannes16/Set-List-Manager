package com.example.franklin.setlistmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franklin.setlistmanager.R;
import com.example.franklin.setlistmanager.helpers.AuthHelper;
import com.example.franklin.setlistmanager.helpers.MetronomeTask;
import com.example.franklin.setlistmanager.helpers.Song;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SetlistActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private static final String TAG = "SetListActivity:";
    public static final int REQUEST_CODE = 1001;

    // Metronome
    MetronomeTask metronome;
    ScheduledThreadPoolExecutor executor;

    // Firebase
    DatabaseReference setlistRef;
    SongListAdapter mAdapter;


    //UI
    ListView mSongsLV;
    EditText mSongNameET;
    EditText mBPMET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlist);

        // Get intent information to find which set list we're in.
        Intent intent = getIntent();
        String setlistString = (String) intent.getExtras().get(SetlistListActivity.SETLIST_REF);
        String userString = (String) intent.getExtras().get(SetlistListActivity.USER_REF);
        String ref = String.format("/setlists/%s/%s/songs", setlistString, userString);
        Log.d(TAG, String.format(
                "onCreate: refString is setlists/%s/%s/songs",
                setlistString,
                userString));

        // UI setup
        mSongsLV = (ListView) findViewById(R.id.songs_LV);
        mSongNameET = (EditText) findViewById(R.id.song_name_ET);
        mBPMET = (EditText) findViewById(R.id.bpm_ET);

        // Make the song name the first editable field
        mSongNameET.requestFocus();


        // Init Firebase
        setlistRef = FirebaseDatabase.getInstance().getReference().child(ref);
        Log.d(TAG, setlistRef.toString());

        mAdapter = new SongListAdapter(setlistRef);
        mSongsLV.setAdapter(mAdapter);
        mSongsLV.setOnItemClickListener(this);
        mSongsLV.setOnItemLongClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selected.
        switch(item.getItemId()){
            case R.id.menu_edit:
                Toast.makeText(this, "Edit!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_logout:
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                AuthHelper.logout(this);
                return true;
            default:
                return false;
        }
    }

    public void addSong(View view) {
        String name = mSongNameET.getText().toString();
        int bpm = Integer.parseInt(mBPMET.getText().toString());

        // Clear UI
        mBPMET.setText("");
        mSongNameET.setText("");
        Song newSong = new Song(name, bpm);
        setlistRef.push().setValue(newSong);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // if executor already exists, then stop it.
        if (executor != null){
            executor.shutdown();
        }

        // Start Metronome
        int bpm = mAdapter.getItem(position).getBpm();
        metronome = new MetronomeTask(bpm);
        executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(metronome, 0, TimeUnit.NANOSECONDS);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (executor != null){
            executor.shutdown();
        }
        final DatabaseReference songToDeleteRef = mAdapter.getRef(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_song)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        songToDeleteRef.removeValue();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        // create the AlertDialog object
        return true;
    }


    private class SongListAdapter extends FirebaseListAdapter<Song> {

        SongListAdapter(Query ref) {
            super(SetlistActivity.this, Song.class, android.R.layout.two_line_list_item, ref);
        }

        SongListAdapter(DatabaseReference ref) {
            super(SetlistActivity.this, Song.class, android.R.layout.two_line_list_item, ref);
        }

        @Override
        protected void populateView(View v, Song song, int position) {
            ((TextView) v.findViewById(android.R.id.text1)).setText(song.getName());
            ((TextView) v.findViewById(android.R.id.text2)).setText(
                    String.format(Locale.US, "BPM: %d", song.getBpm())
            );
        }
    }
}
