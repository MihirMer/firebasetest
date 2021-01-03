package com.example.firebasetest;

public class Artist {
    private String artistID;
    private String artistName;
    private String genre;

    public Artist() {
    }

    public Artist(String artistID, String artistName, String genre) {
        this.artistID = artistID;
        this.artistName = artistName;
        this.genre = genre;
    }

    public String getArtistID() {
        return artistID;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getGenre() {
        return genre;
    }

}
