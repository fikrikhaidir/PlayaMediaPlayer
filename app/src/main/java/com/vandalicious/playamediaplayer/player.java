package com.vandalicious.playamediaplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer.OnCompletionListener;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast; //untuk testing data


import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;


public class player extends AppCompatActivity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener, SensorEventListener {

    private ImageButton btnPlay;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private SeekBar songProgressBar;
    public TextView songTitleLabel;
    public ShareButton shareButton;
    public MediaPlayer mp;
    private Handler mHandler = new Handler();
    private songsManager songManager;
    private utilities utils;
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    public Integer songIndex;
    public static Activity player;
    public String sharedTitle;

    //Sensor
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 950;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Facebook session
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //end of facebook Session

        setContentView(R.layout.activity_player);

        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnNext = (ImageButton) findViewById(R.id.btn_skip_next);
        btnPrevious = (ImageButton) findViewById(R.id.btn_skip_previous);
        btnPlaylist = (ImageButton) findViewById(R.id.btn_playlist);
        songProgressBar = (SeekBar) findViewById(R.id.seekBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        shareButton = (ShareButton) findViewById(R.id.fb_share_button);

        player = this;
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);


        mp = new MediaPlayer();
        songManager = new songsManager();
        utils = new utilities();
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);
        songsList = songManager.getPlayList();
        checkSongIndex();
        playSong(songIndex);

        String title = "Now listening : ".concat("'").concat(sharedTitle).concat("'").concat(" from playa media player !");
        final ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("www.facebook.com/playamediaplayer/"))
                .setContentTitle(sharedTitle)
                .setQuote(title)
                .setContentDescription("Played with Playa Media Player")
                .build();
        shareButton.setShareContent(content);


            btnPlay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (mp.isPlaying()) {
                        if (mp != null) {
                            mp.pause();
                            btnPlay.setImageResource(android.R.drawable.ic_media_play);
                        }
                    } else {
                        if (mp != null) {
                            mp.start();
                            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                        }
                    }

                }
            });


            btnNext.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (currentSongIndex < (songsList.size() - 1)) {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        playSong(0);
                        currentSongIndex = 0;
                    }

                }
            });

            btnPrevious.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (currentSongIndex > 0) {
                        playSong(currentSongIndex - 1);
                        currentSongIndex = currentSongIndex - 1;
                    } else {
                        playSong(songsList.size() - 1);
                        currentSongIndex = songsList.size() - 1;
                    }

                }
            });

        shareButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0){
                buildContent();
//                shareDialog.show(content);
            }
        });

        }


    public void buildContent(){
        String title = "Now listening : ".concat("'").concat(sharedTitle).concat("'").concat(" from playa media player !");
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("www.facebook.com/playamediaplayer/"))
                .setContentTitle(sharedTitle)
                .setQuote(title)
                .setContentDescription("Played with Playa Media Player")
                .build();
        shareButton.setShareContent(content);
    }

    public void turnOff(){
        this.mp.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.turnOff();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
//        turnOff();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.turnOff();

    }

    protected void onResume() {
        super.onResume();
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void checkSongIndex(){
        Intent intent = getIntent();
        if(intent.hasExtra("songIndex")){
            songIndex = Integer.parseInt(intent.getStringExtra("songIndex"));
        }else{
            songIndex = 0;
        }
    }

    public void goToPlaylist(View v){
        Intent intent = new Intent(this,All_Songs.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }

    public void  playSong(int songIndex){
        if(this.mp.isPlaying()){
            this.mp.stop();
        }
        try {
            this.mp.reset();
            this.mp.setDataSource(songsList.get(songIndex).get("songPath"));
            this.mp.prepare();
            this.mp.start();
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);
            sharedTitle = songTitle;

            btnPlay.setImageResource(android.R.drawable.ic_media_pause);

            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                    if (currentSongIndex < (songsList.size() - 1)) {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        playSong(0);
                        currentSongIndex = 0;
                        }

                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();


            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

}
