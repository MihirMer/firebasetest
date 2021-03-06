package com.example.firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrackActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button button;
    private SeekBar seekBar;
    private ListView listView;

    private DatabaseReference databaseTracks;

    private List<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        textView = findViewById(R.id.artistName);
        editText = findViewById(R.id.trackName);
        seekBar = findViewById(R.id.seekBarRating);
        button = findViewById(R.id.addTrackButton);
        listView = findViewById(R.id.listViewTrack);

        tracks = new ArrayList<>();

        Intent intent = getIntent();
        String artistName = intent.getStringExtra(MainActivity.ARTIST_NAME);
        String artistId = intent.getStringExtra(MainActivity.ARTIST_ID);
        textView.setText(artistName);

        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(artistId);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrack();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseTracks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tracks.clear();
                for (DataSnapshot trackSnapshot : snapshot.getChildren()){
                    Track track = trackSnapshot.getValue(Track.class);
                    tracks.add(track);
                }
                TrackList trackListAdapter = new TrackList(AddTrackActivity.this, tracks);
                listView.setAdapter(trackListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveTrack() {
        String trackName = editText.getText().toString().trim();
        String rating = Integer.toString(seekBar.getProgress());
        if(!TextUtils.isEmpty(trackName)){
            String id = databaseTracks.push().getKey();
            Track track = new Track(id, trackName,rating);
            databaseTracks.child(id).setValue(track);
            Toast.makeText(this, "Track added successfully", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Track name can't be empty", Toast.LENGTH_SHORT).show();
        }
    }
}