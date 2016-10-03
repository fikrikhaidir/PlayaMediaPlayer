package com.vandalicious.playamediaplayer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;



public class All_Songs extends AppCompatActivity {
    ListView playlist;
    String items;
    String a, b;
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    TextView tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);
        playlist = (ListView) findViewById(R.id.listAllSongs);
        File ext = new File("/storage/sdcard1");

        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
        songsManager pm = new songsManager();
        this.songsList = pm.getPlayList();
        for (int i = 0; i < songsList.size(); i++) {
            HashMap<String, String> song = songsList.get(i);
            songsListData.add(song);
        }
        ListAdapter adapter = new SimpleAdapter(this, songsListData,
                R.layout.playlist_item, new String[]{"songTitle"}, new int[]{
                R.id.songTitle});
        playlist.setAdapter(adapter);



        playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String songIndex = String.valueOf(position);
                Intent in = new Intent(getApplicationContext(),
                        player.class);
                in.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                in.putExtra("songIndex", songIndex);

                setResult(100, in);
                finish();
                startActivity(in);
            }
        });
    }



}
