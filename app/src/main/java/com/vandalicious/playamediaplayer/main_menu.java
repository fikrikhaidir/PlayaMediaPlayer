package com.vandalicious.playamediaplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class main_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);


    }

    public void PlaylistAll(View v){
        Intent intent = new Intent(this,All_Songs.class);
        startActivity(intent);
    }

    public void sensorTest(View v){
        Intent intent = new Intent(this,sensorTest.class);
        startActivity(intent);
    }

    public void aboutPage(View v){
        Intent intent = new Intent(this,about.class);
        startActivity(intent);
    }
}