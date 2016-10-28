package com.example.franklin.setlistmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franklin.setlistmanager.BuildConfig;
import com.example.franklin.setlistmanager.R;
import com.example.franklin.setlistmanager.helpers.AuthHelper;
import com.example.franklin.setlistmanager.helpers.MetronomeTask;
import com.example.franklin.setlistmanager.helpers.Song;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SetlistActivity extends AppCompatActivity {

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
//        mSongsLV.setOnItemClickListener(this);
//        mSongsLV.setOnItemLongClickListener(this);
        mSongsLV.setOnTouchListener(new MyOnTouchListener());

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
        switch (item.getItemId()) {
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


    public void onItemClick(View view, int position) {
        // if executor already exists, then stop it.
        if (executor != null) {
            executor.shutdown();
        }

        // Start Metronome
        int bpm = mAdapter.getItem(position).getBpm();
        metronome = new MetronomeTask(bpm);
        executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(metronome, 0, TimeUnit.NANOSECONDS);

    }


    public boolean onItemLongClick(View view, int position) {
        if (executor != null) {
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

    private class MyOnTouchListener implements View.OnTouchListener {

        private static final int MAX_CLICK_DURATION = 200;
        private static final int LONG_CLICK_DURATION = 300;
        private long startClickTime;
        private boolean waiting = false;  // Waiting for the end of the click.
        private View targetView;


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO: invoke click animations.
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!waiting) {
                        Log.d(TAG, "onTouch: Action Down");
                        // Check start click time to see if click.
                        targetView = getListItemFromEvent(mSongsLV, event);
                        if (targetView == null) {
                            return false;
                        } else {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            waiting = true;
                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    // TODO: determine whether dragging or not. Use VelocityTracker, addMovement.
                    if (waiting) {
                        long holdDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (holdDuration > LONG_CLICK_DURATION) {
                            Log.d(TAG, "onTouch: Long click");
                            onItemLongClick(targetView, mSongsLV.getPositionForView(targetView));
                            reset();
                            return true;
                        }
                    }
                    return false;
                case MotionEvent.ACTION_UP:
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if (clickDuration < MAX_CLICK_DURATION) {
                        Log.d(TAG, "onTouch: Action Up");
                        onItemClick(targetView, mSongsLV.getPositionForView(targetView));
                    }
                    reset();
                    return true;
            }
            return false;
        }

        void reset() {
            startClickTime = 0;
            waiting = false;
            targetView = null;
        }

        @Nullable
        private View getListItemFromEvent(ListView parent, MotionEvent event) {
            // Find the targeted view.
            Rect rect = new Rect();
            int childCount = parent.getChildCount();

            // First check. See if touch coordinate is passed the last list child.
            View lastChild = parent.getChildAt(childCount - 1);
            Rect lastChildRect = new Rect();
            lastChild.getHitRect(lastChildRect);
            float X = event.getX();
            float Y = event.getY();
            if (Y > lastChildRect.bottom && X > lastChildRect.right) {
                // Out of ListView parent bounds
                return null;
            }

            // Within bounds. See which item it's referring to.
            View child;
            for (int i = 0; i < childCount; i++) {
                child = parent.getChildAt(i);
                child.getHitRect(rect);
                if (rect.contains((int) X, (int) Y)) {
                    return child;  // Found the targeted child.
                }
            }

            return null;  // Couldn't find the child.
        }
    }
}
