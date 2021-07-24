package com.example.bbcchallenge;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button idSearch, artistSearch, addAlbum, deleteAlbum;
    private EditText idType, artistType, deleteSearch;
    private ArrayList<albumClass> albums = new ArrayList<>();
    private ArrayList<albumClass> searchResults = new ArrayList<>();
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
        deleteAlbum = (Button) findViewById(R.id.DeleteButton);

        idType = (EditText) findViewById(R.id.idSearchBar);
        artistType = (EditText) findViewById(R.id.artistSearchBar);
        deleteSearch = (EditText) findViewById(R.id.deleteBar);

        listView = (ListView) findViewById(R.id.list_item);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlbumInfo(albums.get(position));
            }
        });

        extractAlbums();
        for (String output : lines)
        {
            Log.e("output", output);
        }

        fillAlbums();
        Log.e("Album count", String.valueOf(albums.size()));
        for (albumClass album : albums) {
            Log.e("Titles", album.getTitle());
        }

        albums.remove(albums.get(0));
        displayAlbums(albums);
    }

    public void onClick(View aView)
    {
        if (aView == addAlbum) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            View view = getLayoutInflater().inflate(R.layout.new_album_view, null);
            builder.setView(view);
            builder.setTitle("New Album");
            builder.show();

            EditText newArtist = view.findViewById(R.id.newArtistBar);
            EditText newTitle = view.findViewById(R.id.newTitleBar);
            EditText newDate = view.findViewById(R.id.newDateBar);
            EditText newId = view.findViewById(R.id.newIdBar);

            Button createAlbum = view.findViewById(R.id.create);
            createAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(newArtist.getText() != null && newTitle.getText() != null && newDate != null && newId != null) {
                        albumClass newAlbum = new albumClass(newId.getText().toString(), newTitle.getText().toString(), newArtist.getText().toString(), newDate.getText().toString());
                        albums.add(newAlbum);
                        displayAlbums(albums);
                        Toast.makeText(MainActivity.this, "Album Created, Close to view", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (aView == deleteAlbum) {
            for (albumClass album : albums) {
                if(deleteSearch.getText().toString().equals(album.getId())) {
                    albums.remove(album);
                }
            }
            displayAlbums(albums);
        } else if (aView == idSearch) {
            searchResults.clear();
            for (albumClass album : albums) {
                if (idType.getText().toString().equals(album.getId())) {
                    searchResults.add(album);
                }
            }
            displayAlbums(searchResults);
        } else if (aView == artistSearch) {
            searchResults.clear();
            for (albumClass album : albums) {
                if(artistType.getText().toString().toLowerCase().equals(album.getArtist().toLowerCase())) {
                    searchResults.add(album);
                }
            }
            displayAlbums(searchResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void extractAlbums() {
        try {
            DataInputStream csvFile = new DataInputStream(getAssets().open(String.format("rms_albums.csv")));
            Scanner scanner = new Scanner(csvFile);
            String breaker = ",";;
            while (scanner.hasNext()) {
                String inputs[];
                String line = scanner.nextLine();
                inputs = line.split(breaker);
                for (String breaks : inputs) {
                lines.add(breaks);
                }
            }
        } catch (IOException exception) {
           Log.d("Scanning Error", exception.toString());
        }
    }

    public void fillAlbums() {
        int scanTag = 0;
        for (int i = 0; i < lines.size() - 1; i++){
            //Log.e("for @ i", lines.get(i));
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

    public void displayAlbums(ArrayList<albumClass> _albums) {

        ListFiller adapter = new ListFiller(MainActivity.this, _albums);
        listView.setAdapter(adapter);

        int listHeight = listView.getPaddingTop() - listView.getPaddingBottom();
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            View item = listView.getAdapter().getView(i, null, listView);
            if(item instanceof ViewGroup) {
                item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            item.measure(0, 0);
            listHeight += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = listHeight + (listView.getDividerHeight() * adapter.getCount() - 1);
        listView.setLayoutParams(params);
    }

    public void showAlbumInfo(albumClass album) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        View view = getLayoutInflater().inflate(R.layout.alert_view, null);
        builder.setView(view);
        builder.setTitle(album.getTitle());
        builder.show();

        TextView idText = view.findViewById(R.id.idText);
        idText.setText(album.getId());
        TextView infoText = view.findViewById(R.id.albumInfo);
        infoText.setText(album.getTitle() + " by " + album.getArtist() + " released in " + album.getRelease());

        EditText editArtistText = view.findViewById(R.id.EditArtistName);
        EditText editTitletext = view.findViewById(R.id.editTitleText);
        EditText editReleaseText = view.findViewById(R.id.editReleaseDate);

        Button editArtistButton = view.findViewById(R.id.editArtsitbutton);
        Button editTitleButton = view.findViewById(R.id.EdittitleButton);
        Button editReleaseButton = view.findViewById(R.id.EditReleaseDate);

        editArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                album.setArtist(editArtistText.getText().toString());
                displayAlbums(albums);
                Toast.makeText(MainActivity.this, "Artist Edited, Close page to finish", Toast.LENGTH_LONG).show();
            }
        });
        editTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                album.setTitle(editTitletext.getText().toString());
                displayAlbums(albums);
                Toast.makeText(MainActivity.this, "Title Edited, Close page to finish", Toast.LENGTH_LONG).show();
            }
        });
        editReleaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                album.setRelease(editReleaseText.getText().toString());
                displayAlbums(albums);
                Toast.makeText(MainActivity.this, "Year Released Edited, Close page to finish", Toast.LENGTH_LONG).show();
            }
        });
    }
}