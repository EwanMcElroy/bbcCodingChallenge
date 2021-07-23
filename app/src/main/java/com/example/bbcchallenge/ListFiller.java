package com.example.bbcchallenge;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class ListFiller extends ArrayAdapter<albumClass> {
    public ListFiller(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
