package com.example.bbcchallenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ListFiller extends ArrayAdapter<albumClass> {
    private final Context thisContext;
    private final ArrayList<albumClass> items;
    public ListFiller(@NonNull Context context, ArrayList<albumClass> resource) {
        super(context, 0, resource);
        thisContext = context;
        items = resource;
    }

    @NonNull
    @Override
    public View getView(int _pos, @NonNull View _convertView, @NonNull ViewGroup parent) {
        View listItem = _convertView; // get item
        if(listItem == null) {
            listItem = LayoutInflater.from(thisContext).inflate(R.layout.listview, parent, false); // add to list item
        }

        TextView text = (TextView)listItem.findViewById(R.id.label); // init text view

        albumClass album = items.get(_pos); // get current album
        text.setText(album.getTitle() + " by " + album.getArtist() + ": " + album.getRelease()); // display message

        return text;
    }
}
