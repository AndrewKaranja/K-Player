package com.karanjah.kplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity {
    private ImageButton mPlay,mBack,mPlaylist;
    private TextView songPositionTextView;
    private TextView songDurationTextView;
    private SeekBar playSeekbar;
    MediaPlayer mp;
    int totalTime;
    String songPosition;
    int a;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent i=getIntent();
        a=i.getIntExtra(MainActivity.SONG_POSITION,0);
      songPosition=i.getStringExtra(MainActivity.SONG_POSITION);
        List<String> musicFileListPlay = (List<String>) i.getSerializableExtra(MainActivity.MUSIC_LIST);





        songPositionTextView=findViewById(R.id.currentPosition2);
        songDurationTextView=findViewById(R.id.songDuration2);
        mPlay=findViewById(R.id.imagePlay2);
        playSeekbar=findViewById(R.id.seekBar2);
        mBack=findViewById(R.id.imageBack);
        mPlaylist=findViewById(R.id.imagePlaylist);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PlayActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        mPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PlayActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });



//PositionBar
        final String musicFilePath=musicFileListPlay.get(a);
        Toast.makeText(PlayActivity.this, musicFilePath, Toast.LENGTH_LONG).show();
        final int songDuration = getMusic(musicFilePath);
        playSeekbar.setMax(songDuration);
        songDurationTextView.setText(createTimeLable(songDuration));
        playSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mp.seekTo(progress);
                    playSeekbar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Stopping
                if(!mp.isPlaying()){
                    mp.start();
                    mPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
                }else {
                    //Playing
                    mp.pause();
                    mPlay.setImageDrawable(getDrawable(R.drawable.ic_play));


                }
            }
        });

        //Thread (Update playSeekBar and labels)

        //check if a song is playing

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mp!=null){
                    try{
                        Message msg=new Message();
                        msg.what=mp.getCurrentPosition();
                        handler.sendMessage(msg);

                        Thread.sleep(1000);
                    }catch (InterruptedException e){

                    }
                }
            }
        }).start();

    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int currentPosition=msg.what;
            playSeekbar.setProgress(currentPosition);

            String elapsedTime=createTimeLable(currentPosition);
            songPositionTextView.setText(elapsedTime);
        }
    };





    private int getMusic(String path){

        mp=new MediaPlayer();
        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mp.getDuration();
    }

    public String createTimeLable(int time){
        String timeLable="";
        int min=time/1000/60;
        int sec=time/1000 %60;

        timeLable=min+":";
        if (sec<10)timeLable+="0";
        timeLable+=sec;
        return timeLable;
    }






}
