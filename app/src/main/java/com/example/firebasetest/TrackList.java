package com.example.firebasetest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TrackList extends ArrayAdapter<Track> {

    private Activity context;
    private List<Track> trackList;


    public TrackList(Activity context, List<Track> trackList){
        super(context, R.layout.track_list_layout, trackList);
        this.context = context;
        this.trackList = trackList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.track_list_layout, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.trackName);
        TextView textViewRating = listViewItem.findViewById(R.id.trackRating);

        Track track = trackList.get(position);
        textViewName.setText(track.getTrackName());
        textViewRating.setText(track.getTrackRating());


        return listViewItem;
    }
}
