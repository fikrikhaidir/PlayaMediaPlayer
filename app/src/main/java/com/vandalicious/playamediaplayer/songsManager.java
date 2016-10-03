package com.vandalicious.playamediaplayer;

/**
 * Created by vandalicious on 23/06/16.
 */
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class songsManager {
    // SDCard Path
    final String MEDIA_PATH = new String("/storage/sdcard1/Music");
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    public songsManager(){

    }

    public ArrayList<HashMap<String, String>> getPlayList(){
        File home = new File(MEDIA_PATH);

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());
                songsList.add(song);
            }
        }
        return songsList;
    }

    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}