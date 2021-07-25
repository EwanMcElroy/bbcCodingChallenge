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

//
// Programmed by Ewan McElroy - 2021
//
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // variables
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

        // init buttons
        idSearch = (Button) findViewById(R.id.idButton);
        idSearch.setOnClickListener(this);
        artistSearch = (Button) findViewById(R.id.artistButton);
        artistSearch.setOnClickListener(this);
        addAlbum = (Button) findViewById(R.id.addButton);
        addAlbum.setOnClickListener(this);
        deleteAlbum = (Button) findViewById(R.id.DeleteButton);
        deleteAlbum.setOnClickListener(this);

        // init Edit texts
        idType = (EditText) findViewById(R.id.idSearchBar);
        artistType = (EditText) findViewById(R.id.artistSearchBar);
        deleteSearch = (EditText) findViewById(R.id.deleteBar);

        // init list
        listView = (ListView) findViewById(R.id.list_item);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlbumInfo(albums.get(position)); // if list item clicked
            }
        });

        extractAlbums(); // extract album info from csv file
        for (String output : lines)
        {
            Log.e("output", output);
        }

        fillAlbums(); // put csv data into albums
        Log.e("Album count", String.valueOf(albums.size()));

        albums.remove(albums.get(0)); // remove the top line
        displayAlbums(albums); // display album in list view
    }

    public void onClick(View aView)
    {
        if (aView == addAlbum) { // if the add album button is pressed
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // create new builder
            builder.setCancelable(true); // set cancelable
            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                } // close alert if button is pressed
            });
            View view = getLayoutInflater().inflate(R.layout.new_album_view, null); // get alert layout
            builder.setView(view); // set layout to alert view
            builder.setTitle("New Album"); // set title of alert
            builder.show(); // show alert

            // init alert edit texts
            EditText newArtist = view.findViewById(R.id.newArtistBar);
            EditText newTitle = view.findViewById(R.id.newTitleBar);
            EditText newDate = view.findViewById(R.id.newDateBar);
            EditText newId = view.findViewById(R.id.newIdBar);

            // init buttons
            Button createAlbum = view.findViewById(R.id.create);
            createAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(newArtist.getText() != null && newTitle.getText() != null && newDate != null && newId != null) { // if all fields are full
                        albumClass newAlbum = new albumClass(newId.getText().toString(), newTitle.getText().toString(), newArtist.getText().toString(), newDate.getText().toString()); // create new album
                        albums.add(newAlbum); // add new album to array
                        displayAlbums(albums); // display new list
                        Toast.makeText(MainActivity.this, "Album Created, Close to view", Toast.LENGTH_LONG).show(); // display toast message to close alert
                    }
                }
            });
        } else if (aView == deleteAlbum) { // if delete album is pressed
            // init delete varaibles
            int index = 0;
            int deleteIndex = -1;
            for (albumClass album : albums) { // for every album
                if(deleteSearch.getText().toString().equals(album.getId())) { // if delete bar = album id
                    deleteIndex = index; // set delete index to current index
                }
            index++; // increase index
            }
            if (deleteIndex != -1) { // if deleteIndex isn't null
                albums.remove(deleteIndex); // remove delete index from array
            }
            displayAlbums(albums); // display new list
        } else if (aView == idSearch) { // if id search button pressed
            searchResults.clear(); // clear search results
            for (albumClass album : albums) { // for every album
                if (idType.getText().toString().equals(album.getId())) { // if id edit text = album id
                    searchResults.add(album); // add album to search results
                }
            }
            displayAlbums(searchResults); // display search results
        } else if (aView == artistSearch) { // if artist search button pressed
            searchResults.clear(); // clear search results
            for (albumClass album : albums) { // for every album
                if(artistType.getText().toString().toLowerCase().equals(album.getArtist().toLowerCase())) { // if artist name = artist edit text
                    searchResults.add(album); // add album to search results
                }
            }
            displayAlbums(searchResults); // display search results
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void extractAlbums() {
        try {
            DataInputStream csvFile = new DataInputStream(getAssets().open(String.format("rms_albums.csv"))); // get rms_albums.csv from the assets file
            Scanner scanner = new Scanner(csvFile); // create a scanner
            String breaker = ","; // set a breaker to seperate data
            while (scanner.hasNext()) { // while the scanner can still read
                String inputs[]; // init line array
                String line = scanner.nextLine(); // read line into variable
                inputs = line.split(breaker); // seperate info from line into array
                for (String breaks : inputs) {
                lines.add(breaks); // add data into line array
                }
            }
        } catch (IOException exception) {
           Log.d("Scanning Error", exception.toString()); // display error
        }
    }

    public void fillAlbums() {
        int scanTag = 0; // set scanning tag to 0 to start
        for (String line : lines){
            switch (scanTag) {
                case 0:
                    albumClass album = new albumClass(); // create new album
                    albums.add(album); // add new album to array
                    album.setId(line); // set album id
                    scanTag++; // increment tag
                    break;
                case 1:
                    albums.get(albums.size() - 1).setTitle(line); // set album title
                    scanTag++; // increment tag
                    break;
                case 2:
                    albums.get(albums.size() - 1).setArtist(line); // set album artist
                    scanTag++; // increment tag
                    break;
                case 3:
                    albums.get(albums.size() - 1).setRelease(line); // set album release date
                    Log.e("Date", line);
                    scanTag = 0; // reset tag
                    break;
            }
        }
    }

    public void displayAlbums(ArrayList<albumClass> _albums) {

        ListFiller adapter = new ListFiller(MainActivity.this, _albums); // create new list filler class
        listView.setAdapter(adapter); // set list adapter

        int listHeight = listView.getPaddingTop() - listView.getPaddingBottom(); // calulate the difference between the padding top and bottom
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            View item = listView.getAdapter().getView(i, null, listView);
            if(item instanceof ViewGroup) {
                item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)); // for every item in the list set paramaters to match the parent
            }
            item.measure(0, 0); // measure the height
            listHeight += item.getMeasuredHeight(); // increase the list height
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams(); // set view group paramaters to list view paramaters
        params.height = listHeight + (listView.getDividerHeight() * adapter.getCount() - 1); // set parameter height
        listView.setLayoutParams(params); // set list view paramaters
    }

    public void showAlbumInfo(albumClass album) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // create builder
        builder.setCancelable(true); // swt cancelable
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            } // set close button
        });
        View view = getLayoutInflater().inflate(R.layout.alert_view, null); // get alert view
        builder.setView(view); // set alert view
        builder.setTitle(album.getTitle()); // set title
        builder.show(); // show alert

        // init text view
        TextView idText = view.findViewById(R.id.idText);
        idText.setText(album.getId());
        TextView infoText = view.findViewById(R.id.albumInfo);
        infoText.setText(album.getTitle() + " by " + album.getArtist() + " released in " + album.getRelease());

        // init edit text
        EditText editArtistText = view.findViewById(R.id.EditArtistName);
        EditText editTitletext = view.findViewById(R.id.editTitleText);
        EditText editReleaseText = view.findViewById(R.id.editReleaseDate);

        // init buttons
        Button editArtistButton = view.findViewById(R.id.editArtsitbutton);
        Button editTitleButton = view.findViewById(R.id.EdittitleButton);
        Button editReleaseButton = view.findViewById(R.id.EditReleaseDate);

        editArtistButton.setOnClickListener(new View.OnClickListener() { // if edit arstist clicked
            @Override
            public void onClick(View v) {
                album.setArtist(editArtistText.getText().toString()); // edit artist name using edit text
                displayAlbums(albums); // display edit
                Toast.makeText(MainActivity.this, "Artist Edited, Close page to finish", Toast.LENGTH_LONG).show(); // display close message
            }
        });
        editTitleButton.setOnClickListener(new View.OnClickListener() { // if edit title button clicked
            @Override
            public void onClick(View v) {
                album.setTitle(editTitletext.getText().toString()); // edit title using edit text
                displayAlbums(albums); // display albums
                Toast.makeText(MainActivity.this, "Title Edited, Close page to finish", Toast.LENGTH_LONG).show(); // show close message
            }
        });
        editReleaseButton.setOnClickListener(new View.OnClickListener() { // if edit release button clicked
            @Override
            public void onClick(View v) {
                album.setRelease(editReleaseText.getText().toString()); // edit release date using edit text
                displayAlbums(albums); // display albums
                Toast.makeText(MainActivity.this, "Year Released Edited, Close page to finish", Toast.LENGTH_LONG).show(); // show  close message
            }
        });
    }
}