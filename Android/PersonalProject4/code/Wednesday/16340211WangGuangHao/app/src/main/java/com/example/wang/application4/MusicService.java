package com.example.wang.application4;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    public MediaPlayer mediaPlayer = new MediaPlayer();
    public int length = 0;
    public final IBinder binder = new MyBinder();
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/data/山高水长.mp3";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MusicService() {
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            length = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //播放/暂停
    public void playOrPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    //停止
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //退出
    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        stopSelf();
        super.onDestroy();
    }

    //通过Binder来保持Activity和Service的通信（写在service类）


    public class MyBinder extends Binder {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                //返回歌曲当前播放进程的详细信息
                case 1:
                    Bundle bundle = new Bundle();
                    bundle.putInt("cur", mediaPlayer.getCurrentPosition());
                    bundle.putInt("end", length);
                    if (mediaPlayer.isPlaying()) {
                        bundle.putInt("flag", 1);
                    }
                    else {
                        bundle.putInt("flag", 2);
                    }
                    reply.writeBundle(bundle);
                    break;
                case 2:
                    playOrPause();
                    break;
                case 3:
                    stop();
                    break;
                case 4:
                    onDestroy();
                    break;
                case 5:
                    mediaPlayer.seekTo(data.readInt());
                    break;
                case 6:
                    Log.i("error",data.readString());
                    path = data.readString();
                    try {

                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(path);
                        mediaPlayer.prepare();
                        length = mediaPlayer.getDuration();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return super.onTransact(code, data, reply, flags);
        }

    }
    //绑定服务
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //解绑服务
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


}
