package com.example.bbcchallenge;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button idSearch, artistSearch, addAlbum, deleteAlbum;
    private EditText idType, artistType;
    private ArrayList<albumClass> albums = new ArrayList<>();
    private List<String> lines = new ArrayList<>();
    private ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idSearch = (Button) findViewById(R.id.idButton);
        idSearch.setOnClickListener(this);
        artistSearch = (Button) findViewById(R.id.artistButton);
        artistSearch.setOnClickListener(this);
        addAlbum = (Button) findViewById(R.id.addButton);
        addAlbum.setOnClickListener(this);
        deleteAlbum = (Button) findViewById(R.id.deleteButton);

        idType = (EditText) findViewById(R.id.idSearchBar);
        artistType = (EditText) findViewById(R.id.artistSearchBar);

        listView = (ListView) findViewById(R.id.list_item);

        extractAlbums();
        for (String output : lines)
        {
            Log.e("output", output);
        }

        fillAlbums();
        Log.e("Album count", String.valueOf(albums.size()));


    }

    public void onClick(View aView)
    {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void extractAlbums() {
        //try (Reader reader = Files.newBufferedReader(Paths.get("rms_albums.csv"))) {
        try {
            DataInputStream csvFile = new DataInputStream(getAssets().open(String.format("rms_albums.csv")));
            Scanner scanner = new Scanner(csvFile);
            String breaker = ",";
            scanner.useDelimiter(breaker);
            while (scanner.hasNext()) {
                String line = scanner.next();
                lines.add(line);
            }
        } catch (IOException exception) {
           Log.d("Scanning Error", exception.toString());
        }
    }

    public void fillAlbums() {
        int scanTag = 0;
        for (int i = 0; i < lines.size() - 1; i++){
            switch (scanTag) {
                case 0:
                    albumClass album = new albumClass();
                    albums.add(album);
                    album.setId(lines.get(i));
                    scanTag++;
                    break;
                case 1:
                    albums.get(albums.size() - 1).setTitle(lines.get(i));
                    scanTag++;
                    break;
                case 2:
                    albums.get(albums.size() - 1).setArtist(lines.get(i));
                    scanTag++;
                    break;
                case 3:
                    albums.get(albums.size() - 1).setRelease(lines.get(i));
                    scanTag = 0;
                    break;
            }
        }
    }
}