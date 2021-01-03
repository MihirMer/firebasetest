package com.example.firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ARTIST_NAME = "artistname";
    public static final String ARTIST_ID = "artistid";


    private Button button;
    private EditText editText;
    private Spinner spinner;
    private ListView listView;
    private List<Artist> artistList;

    private DatabaseReference databaseArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        editText = findViewById(R.id.name);
        spinner = findViewById(R.id.spinner);
        databaseArtist = FirebaseDatabase.getInstance().getReference("artists");
        listView = findViewById(R.id.listViewArtists);
        artistList = new ArrayList<Artist>();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = artistList.get(position);
                showUpdateDialog(artist.getArtistID(), artist.getArtistName());
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = artistList.get(position);
                Intent intent = new Intent(getApplicationContext(), AddTrackActivity.class);
                intent.putExtra(ARTIST_NAME, artist.getArtistName());
                intent.putExtra(ARTIST_ID, artist.getArtistID());
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseArtist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                artistList.clear();
                for (DataSnapshot artistSnapshot: snapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    artistList.add(artist);
                }
                ArtistList adapter = new ArtistList(MainActivity.this, artistList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void showUpdateDialog(final String artistId, String artistName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_update, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.EditTextName);
        final Button buttonUpdate = dialogView.findViewById(R.id.ButtonUpdate);
        final Spinner spinnerGenres = dialogView.findViewById(R.id.spinnerGenre);
        final Button buttonDelete = dialogView.findViewById(R.id.ButtonDelete);

        builder.setTitle("Updating Artist "+artistName);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenres.getSelectedItem().toString();

                if(TextUtils.isEmpty(name)){
                    editTextName.setError("Name required");
                    return;
                }
                updateArtist(artistId, name, genre);
                alertDialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArtist(artistId);
                alertDialog.dismiss();
            }
        });
    }

    private void deleteArtist(String artistId) {
        DatabaseReference databaseReferenceArtist = FirebaseDatabase.getInstance().getReference("artists").child(artistId);
        DatabaseReference databaseReferencetracks = FirebaseDatabase.getInstance().getReference("tracks").child(artistId);

        databaseReferenceArtist.removeValue();
        databaseReferencetracks.removeValue();

        Toast.makeText(this, "Artist deleted Successfully", Toast.LENGTH_SHORT).show();

    }

    private void updateArtist(String id, String nameNew, String genreNew){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(id);
        Artist artist = new Artist(id, nameNew, genreNew);
        databaseReference.setValue(artist);
        Toast.makeText(this, "Artist updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void addArtist() {
        String name = editText.getText().toString().trim();
        String genre = spinner.getSelectedItem().toString();
        
        if(!TextUtils.isEmpty(name)){
            String id = databaseArtist.push().getKey();
            Artist artist = new Artist(id, name, genre);
            databaseArtist.child(id).setValue(artist);
            Toast.makeText(this, "Artist added", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
        }
    }
}