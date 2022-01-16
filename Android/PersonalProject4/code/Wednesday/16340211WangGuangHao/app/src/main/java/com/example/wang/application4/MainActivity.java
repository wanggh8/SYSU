package com.example.wang.application4;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import rx.Observable;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private CircleImageView circle;
    private TextView name;
    private TextView author;
    private Button file;
    private Button play;
    private Button stop;
    private Button quit;
    private TextView curtime;
    private TextView endtime;
    private SeekBar timebar;

    private ServiceConnection sc;
    private IBinder mBinder;
    private boolean playing = false;
    private int degree = 0;
    private String newpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //动态申请权限
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        circle = findViewById(R.id.circle_image);
        name = findViewById(R.id.name);
        author = findViewById(R.id.author);

        file = findViewById(R.id.file);
        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        quit = findViewById(R.id.quit);

        curtime = findViewById(R.id.cur_time);
        endtime = findViewById(R.id.end_time);
        timebar = findViewById(R.id.time_bar);

        final SimpleDateFormat time = new SimpleDateFormat("mm:ss");

        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = service;
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                sc = null;
            }
        };
        Intent intent = new Intent(this, MusicService.class);
        //开启服务
        startService(intent);
        //绑定activity和服务
        bindService(intent, sc, Context.BIND_AUTO_CREATE);

        Observer<Bundle> observer = new Observer<Bundle>() {
            @Override
            public void onNext(Bundle s) {

                int cur = (int)s.get("cur");
                int end = (int)s.get("end");
                curtime.setText(time.format(cur));
                endtime.setText(time.format(end));
                timebar.setProgress(cur);
                timebar.setMax(end);

                if (playing){
                    degree += 2;
                    play.setBackgroundResource(R.mipmap.pause);
                }
                else {
                    play.setBackgroundResource(R.mipmap.play);
                }
                circle.setRotation(degree);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        Observable observable = Observable.create(new Observable.OnSubscribe<Bundle>() {
            @Override
            public void call(Subscriber<? super Bundle> subscriber) {
                int cur = 0;
                int end = 0;
                do {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                    try {
                        Parcel data = Parcel.obtain();
                        Parcel reply = Parcel.obtain();
                        if(mBinder != null && sc != null) {

                            mBinder.transact(1, data, reply, 0);
                            Bundle bundle = reply.readBundle(getClass().getClassLoader());
                            cur = bundle.getInt("cur", 0);
                            end = bundle.getInt("end", 0);
                            Bundle bundle2 = new Bundle();
                            bundle2.putInt("cur",cur);
                            bundle2.putInt("end",end);
                            subscriber.onNext(bundle2);

                            if (bundle.getInt("flag") == 1) {
                                playing = true;
                            }
                            else {
                                playing = false;
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }

                } while (true);
                //subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(observer);

        /*
        final Handler mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        curtime.setText(time.format(msg.arg1));
                        endtime.setText(time.format(msg.arg2));
                        timebar.setProgress(msg.arg1);
                        timebar.setMax(msg.arg2);

                        if (playing){
                            degree += 2;
                            play.setBackgroundResource(R.mipmap.pause);
                        }
                        else {
                            play.setBackgroundResource(R.mipmap.play);
                        }
                        circle.setRotation(degree);
                        break;
                }
            }
        };
        */

        // 新建线程
        /*
        final Runnable mRunnable =new Runnable() {
            @Override
            public void run() {
                int cur = 0;
                int end = 0;
                do {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Parcel data = Parcel.obtain();
                        Parcel reply = Parcel.obtain();
                        if(mBinder != null && sc != null) {

                            mBinder.transact(1, data, reply, 0);
                            Bundle bundle = reply.readBundle(getClass().getClassLoader());
                            cur = bundle.getInt("cur", 0);
                            end = bundle.getInt("end", 0);

                            Message msg = mHandler.obtainMessage();
                            msg.arg1 = cur;
                            msg.arg2 = end;
                            msg.what = 1;
                            if (bundle.getInt("flag") == 1) {
                                playing = true;
                            }
                            else {
                                playing = false;
                            }

                            // 发送消息
                            mHandler.sendMessage(msg);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                } while (true);
            }
        };
        Thread mThread = new Thread(mRunnable);
        mThread.start();
        */

        //播放/暂停
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    mBinder.transact(2, data, reply, 0);
                    if(playing) {
                        play.setBackgroundResource(R.mipmap.play);
                        playing = false;
                    } else {
                        play.setBackgroundResource(R.mipmap.pause);
                        playing = true;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //停止
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timebar.setProgress(0);
                degree = 0;
                circle.setRotation(degree);
                playing = false;
                play.setBackgroundResource(R.mipmap.play);
                try {
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    mBinder.transact(3, data, reply, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //退出
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mHandler.removeCallbacks(mRunnable);
                unbindService(sc);
                sc = null;
                try {
                    MainActivity.this.finish();
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    mBinder.transact(4, data, reply, 0);
                    System.exit(0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //拖动进度条
        timebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    try {
                        Parcel data = Parcel.obtain();
                        Parcel reply = Parcel.obtain();
                        data.writeInt(seekBar.getProgress());
                        mBinder.transact(5, data, reply, 0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //添加音乐
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(sc);
            MainActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (!data.getData().equals(null)){
                Uri uri = data.getData();
                newpath =  getPath(this, uri);
            }

            Toast.makeText(this,newpath,Toast.LENGTH_SHORT).show();
            if (newpath.equals(null)){
                return;
            }
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(newpath);
                byte[] img = mmr.getEmbeddedPicture();
                Bitmap bitmap = null;
                if (img != null) {
                    bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                    circle.setImageBitmap(bitmap);
                }

                playing = false;
                play.setBackgroundResource(R.mipmap.play);
                timebar.setProgress(0);
                degree = 0;
                circle.setRotation(degree);

                name.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                author.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

                Parcel data2 = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data2.writeString(newpath);

                mBinder.transact(6, data2, reply, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
