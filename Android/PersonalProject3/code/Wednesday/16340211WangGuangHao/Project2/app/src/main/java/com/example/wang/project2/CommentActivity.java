package com.example.wang.project2;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CommentActivity extends AppCompatActivity {
    private List<CommentInfo> mData = null;
    private Context mContext;
    private MyAdapter mAdapter = null;
    private ListView list_item;
    private EditText comment;
    private Button sendbtn;
    private static final String DB_NAME= "db_name";
    private static final String TABLE_NAME = "comment";
    public myDB dbHelper = new myDB(CommentActivity.this,DB_NAME,null,1);
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        comment = findViewById(R.id.Comment);
        sendbtn = findViewById(R.id.send_btn);
        mContext = CommentActivity.this;
        list_item = (ListView) findViewById(R.id.listView);
        mData = new LinkedList<CommentInfo>();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(CommentActivity.this);
        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(CommentActivity.this);
        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(CommentActivity.this);
        //数据库读取
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME,null, null, null, null, null, null);
        if (cursor.getCount() != 0 ){
            cursor.moveToNext();
            do {
                String user_name = cursor.getString(1);
                Cursor cursor1 = db.query("user",null, "name = ?",new String[] {user_name}, null, null, null, null);
                if (cursor1.getCount() != 0 ){
                    cursor1.moveToNext();
                    byte[] in=cursor1.getBlob(cursor1.getColumnIndex("icon"));
                    bitmap= BitmapFactory.decodeByteArray(in,0,in.length);
                }
                cursor1.close();
                mData.add(new CommentInfo(cursor.getString(1),cursor.getString(2),
                        bitmap,cursor.getString(0),cursor.getInt(3),cursor.getInt(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        //添加item
        mAdapter = new MyAdapter((LinkedList<CommentInfo>) mData, mContext);
        list_item.setAdapter(mAdapter);
        //设置弹窗
        dialog.setTitle("Info");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 点击后的事件处理
            }
        });

        //发送按钮侦听
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentstr=comment.getText().toString();
                if (TextUtils.isEmpty(commentstr)){//用户名为空
                    Toast.makeText(CommentActivity.this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    SimpleDateFormat myTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    Intent intent = getIntent();
                    Bundle bd = intent.getExtras();
                    String username = bd.getString("name");

                    Cursor cursor = db.query("user",null, "name = ?",new String[] {username}, null, null, null, null);
                    if (cursor.getCount() != 0 ){
                        cursor.moveToNext();
                        byte[] in=cursor.getBlob(cursor.getColumnIndex("icon"));
                        bitmap= BitmapFactory.decodeByteArray(in,0,in.length);

                    }
                    cursor.close();

                    ContentValues cv = new ContentValues();
                    Random random = new Random();
                    int num = random.nextInt(20);
                    int zan = random.nextInt(2);
                    if (zan >= 1) {
                        cv.put("zan",R.mipmap.red);
                        mAdapter.add(new CommentInfo(username,commentstr,bitmap
                                ,myTime.format(date),num,R.mipmap.red));
                    }
                    else {
                        cv.put("zan",R.mipmap.white);
                        mAdapter.add(new CommentInfo(username,commentstr,bitmap
                                ,myTime.format(date),num,R.mipmap.white));
                    }
                    cv.put("date", myTime.format(date));
                    cv.put("name", username);
                    cv.put("speak", commentstr);
                    cv.put("num",num);
                    db.insert(TABLE_NAME, null, cv);
                    db.close();
                    comment.setText("");
                }
            }
        });
        //listview短按
        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = mData.get(i).getName();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if( getApplicationContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED )
                        ActivityCompat.requestPermissions(CommentActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                }
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = \"" + username + "\"", null, null);
                String number = "\nPhone: ";
                if (cursor.getCount() != 0 ){
                    cursor.moveToFirst();
                    do {
                        number += cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + "         ";
                    } while (cursor.moveToNext());
                }
                else {
                    number = "\nPhone number not exist.";
                }
                dialog.setMessage("Username: "+ username + number);
                dialog.show();
            }
        });

        dialog1.setTitle("Delete or Not?");
        dialog1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 点击后的事件处理

            }
        });
        dialog2.setTitle("Report or Not?");
        dialog2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 点击后的事件处理
                Toast.makeText(CommentActivity.this,"Report successfully.",Toast.LENGTH_SHORT).show();
            }
        });
        dialog2.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 点击后的事件处理

            }
        });

        //listview长按
        list_item.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                dialog1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 点击后的事件处理
                        String date = mData.get(position).getDate();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        String whereClause = "date =?";
                        String[] whereArgs = {date};
                        db.delete(TABLE_NAME,whereClause,whereArgs);
                        db.close();
                        mAdapter.remove(position);
                    }
                });
                if (position >= 0) {
                    String username = mData.get(position).getName();
                    Intent intent = getIntent();
                    Bundle bd = intent.getExtras();
                    String username1 = bd.getString("name");
                    if (username.equals(username1)){
                        dialog1.show();
                    }
                    else {
                        dialog2.show();
                    }
                }
                return true;
            }
        });
    }
}
