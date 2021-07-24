package com.example.bbcchallenge;

public class albumClass {
    private String id, title, artist, release;

    public albumClass()
    {
        id = "";
        title = "";
        artist = "";
        release = "";
    }
    public albumClass(String _id, String _title, String _artist, String _release)
    {
        id = _id;
        title = _title;
        artist = _artist;
        release = _release;
    }

    public String getId() {return id;}
    public String getTitle() {return title;}
    public String getArtist() {return artist;}
    public String getRelease() {return release;}

    public void setId(String _id) { id = _id;}
    public void setTitle(String _title) { title = _title;}
    public void setArtist(String _artist) { artist = _artist;}
    public void setRelease(String _release) {release = _release;}
}
