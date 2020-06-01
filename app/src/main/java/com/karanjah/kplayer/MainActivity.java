package com.karanjah.kplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String SONG_POSITION="com,karanjah.kplayer.MainActivity.SONG_POSITION";
    public static String MUSIC_LIST="com,karanjah.kplayer.MainActivity.MUSIC_LIST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private static final String[] PERMISSIONS={
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int REQUEST_PERMISSI0NS=12345;
    private static final int PERMISSIONS_COUNT=1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean permissionsDenied(){
        for(int i=0;i<PERMISSIONS_COUNT;i++){
            if(checkSelfPermission(PERMISSIONS[i])!= PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissionsDenied()){
            ((ActivityManager)(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            recreate();
        }else {
         onResume();
        }
    }

    private boolean isMusicPlayerInit;

    private List<String> musicFileList;

    private void addMusicFilesFrom(String dirPath){
        final File musicDir= new File(dirPath);
        if(!musicDir.exists()){
            musicDir.mkdir();
            return;
        }
        final File[] files=musicDir.listFiles();
        for(File file:files){
            final String path=file.getAbsolutePath();
            if(path.endsWith(".mp3")){
                musicFileList.add(path);
            }
        }
    }

    private void fillMusicList(){
        musicFileList.clear();
        addMusicFilesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));
        addMusicFilesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
    }

    private MediaPlayer mp;

    private int playMusicFile(String path){
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

//    private void updateSongProgress(){
//
//    }
    private int songPosition;
    private volatile boolean isSongPlaying;
    private int mPosition;

    private void playSong(){
        final String musicFilePath=musicFileList.get(mPosition);
        final int songDuration = playMusicFile(musicFilePath)/1000;
        seekbar.setMax(songDuration);
        seekbar.setVisibility(View.VISIBLE);
        playbackView.setVisibility(View.VISIBLE);
        songDurationTV.setText(createTimeLable(songDuration));
//        isSongPlaying=true;
//        new Thread(){
//
//            public void run() {
//
//               isSongPlaying=true;
//                songPosition =0;
//                while(isSongPlaying && songPosition<songDuration){
//                    try {
//                        Thread.sleep(1000);
//                    }catch (InterruptedException e){
//                        e.printStackTrace();
//                    }
//                    if (isSongPlaying){
//                        songPosition++;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                seekbar.setProgress(songPosition);
//                                songPositionTV.setText(createTimeLable(songPosition));
//                            }
//                        });
//                    }
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mp.pause();
//
//
//                       songPosition = 0;
//                        Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
//                            mp.seekTo(songPosition*1000);
//                            songPositionTV.setText(createTimeLable(songPosition));
//                            pauseBtn.setText("PLAY");
//                            playBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
//                            isSongPlaying = false;
//                            seekbar.setProgress(songPosition);
//
//                      else {
//                          songPositionTV.setText(createTimeLable(songPosition));
//                          mp.seekTo(songPosition);
//                          seekbar.setProgress(songPosition);
//
//                      }
//                    }
//                });
//
//            }
//        }.start();
    }
    public String createTimeLable(int time){
        String timeLable="";
        int min=time/60;
        int sec=time%60;

        timeLable=min+":";
        if (sec<10)timeLable+="0";
        timeLable+=sec;
        return timeLable;
    }

    private TextView songPositionTV;
    private TextView songDurationTV;
    private SeekBar seekbar;
    private View playbackView;

    private ImageButton playBtn;

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && permissionsDenied()){
            requestPermissions(PERMISSIONS,REQUEST_PERMISSI0NS);
            return;
        }
        if(!isMusicPlayerInit){
            final ListView listView=findViewById(R.id.listView);
            final TextAdapter textAdapter=new TextAdapter();
            musicFileList=new ArrayList<>();
            fillMusicList();
            textAdapter.setData(musicFileList);
            listView.setAdapter(textAdapter);
            seekbar=findViewById(R.id.seekBar);


            //seek Bar code
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int songProgress;
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        mp.seekTo(progress*1000);
                    seekbar.setProgress(progress);
                    }else {
                        songProgress = progress;

                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
//              songPosition=songProgress;
//                  mp.seekTo(songPosition*1000);


                }
            });

            songPositionTV=findViewById(R.id.currentPosition);
            songDurationTV=findViewById(R.id.songDuration);


            playBtn=findViewById(R.id.imagePlay);

            playbackView=findViewById(R.id.playBackView);


            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playThread();
                    if(mp.isPlaying()) {
                        mp.pause();
//                        pauseBtn.setText("PLAY");
                        playBtn.setImageDrawable(getDrawable(R.drawable.ic_play));
                    }else{
                        if(songPosition==0){
                            playSong();
                        }else {
                            mp.start();
                            seekbar.setProgress(songPosition);
                            songPositionTV.setText(createTimeLable(songPosition));

                        }
//                        pauseBtn.setText("PAUSE");
                        playBtn.setImageDrawable(getDrawable(R.drawable.ic_pause));
//                        mp.start();

                    }
                    isSongPlaying=!isSongPlaying;
                }
            });




            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if(mp!=null){
//                        mp.stop();
//                        mp.release();
//                    }
                    mPosition=position;
                    Intent intent=new Intent(MainActivity.this,PlayActivity.class);
                    intent.putExtra(MUSIC_LIST, (Serializable) musicFileList);
                    intent.putExtra(SONG_POSITION,mPosition);

                    startActivity(intent);

//
//                    playSong();

                }
            });


            isMusicPlayerInit=true;
        }

    }

    private void playThread(){
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
            currentPosition/=1000;
            seekbar.setProgress(currentPosition);

            String elapsedTime=createTimeLable(currentPosition);
            songPositionTV.setText(elapsedTime);
        }
    };


    class TextAdapter extends BaseAdapter{
        private List<String> data =new ArrayList<>();
        void setData(List<String> mData){
            data.clear();
            data.addAll(mData);
            notifyDataSetChanged();
        }
        @Override
        public int getCount(){
            return data.size();
        }
        @Override
        public String getItem(int position){
            return null;
        }
        @Override
        public long getItemId(int position){
            return 0;
        }
        @Override
        public View getView(int position, View convertView ,ViewGroup parent){
          if(convertView==null){

              convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
              convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.myItem)));
          }
          ViewHolder holder=(ViewHolder) convertView.getTag();
          final String item=data.get(position);
          holder.info.setText(item.substring(
                  item.lastIndexOf('/')+1));
          return convertView;
        }

        class ViewHolder{
            TextView info;
            ViewHolder(TextView mInfo){
                info=mInfo;
            }

        }




    }

}

