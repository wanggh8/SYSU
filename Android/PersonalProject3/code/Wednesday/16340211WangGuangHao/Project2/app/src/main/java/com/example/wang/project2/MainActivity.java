package com.example.wang.project2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    public static final String PREFERENCE_PACKAGE = "com.example.wang.myapplication";
    public static final int MODE = Context.MODE_PRIVATE;
    private static final String DB_NAME= "db_name";
    private static final String TABLE_NAME = "user";
    private boolean isRig = false;
    private ImageView imageButton;
    private Bitmap bitmap;
    public myDB dbHelper = new myDB(MainActivity.this,DB_NAME,null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.me);
        final EditText user = findViewById(R.id.User);
        final EditText password = findViewById(R.id.Password);
        final EditText new_password = findViewById(R.id.NewPassword);
        final EditText con_password = findViewById(R.id.ConfirmPassword);
        final Button ok = findViewById(R.id.ok_btn);
        final Button clear = findViewById(R.id.clear_btn0);
        imageButton = findViewById(R.id.image);
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final RadioButton rbutton = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                if (rbutton.getText().toString().equals("Register")) {
                    imageButton.setVisibility(View.VISIBLE);
                    user.setVisibility(View.VISIBLE);
                    password.setVisibility(View.GONE);
                    new_password.setVisibility(View.VISIBLE);
                    con_password.setVisibility(View.VISIBLE);
                    isRig = true;
                }
                else {
                    imageButton.setVisibility(View.GONE);
                    user.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    new_password.setVisibility(View.GONE);
                    con_password.setVisibility(View.GONE);
                    isRig = false;
                }
            }
        });

        //清除按钮侦听
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                con_password.setText("");
                new_password.setText("");
                user.setText("");
                password.setText("");
            }
        });

        // OK按钮侦听
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //注册时
                String username = user.getText().toString();
                String passwords = password.getText().toString();//字符串密码
                String password1 = new_password.getText().toString();
                String password2 = con_password.getText().toString();

                if(isRig) {
                    if (TextUtils.isEmpty(username)){//用户名为空
                        Toast.makeText(MainActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else if (TextUtils.isEmpty(password1) && TextUtils.isEmpty(password2)){//密码为空
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else if (!password1.equals(password2)) {
                        Toast.makeText(MainActivity.this, "Password Mismatch.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        Cursor cursor = db.query(TABLE_NAME,null, "name = ?",
                                new String[] {username},
                                null, null, null);
                        if (cursor.getCount() == 0){
                            ContentValues values = new ContentValues();
                            values.put("name", username);
                            values.put("password", password1);
                            final ByteArrayOutputStream os = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, os);
                            values.put("icon", os.toByteArray());
                            db.insert(TABLE_NAME, null, values);
                            db.close();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Username already existed.", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                        db.close();
                    }
                }
                //登陆时
                else {
                    if (TextUtils.isEmpty(username)){//用户名为空
                        Toast.makeText(MainActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    if (passwords.equals("")) {//密码为空
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else {//都不为空

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        Cursor cursor = db.query(TABLE_NAME,null, "name = ?",
                                new String[] {username},
                                null, null, null);
                        if (cursor.getCount() >0){
                            cursor.moveToNext();
                            String correctPass = cursor.getString(1);//正确密码
                            if (passwords.equals(correctPass)){
                                Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                                Bundle bd = new Bundle();
                                bd.putString("name",username);
                                intent.putExtras(bd);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Invalid Password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {//密码错误
                            Toast.makeText(MainActivity.this, "Username not existed.", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                        db.close();
                    }
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 激活系统图库，选择一张图片
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            // 得到图片的全路径
            Uri uri = data.getData();
            // 通过路径加载图片
            //这里省去了图片缩放操作，如果图片过大，可能会导致内存泄漏
            //图片缩放的实现，请看：https://blog.csdn.net/reality_jie_blog/article/details/16891095
            //imageButton.setImageURI(uri);
            try{
                bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
            }catch (IOException e) {
                e.printStackTrace();
            }

            imageButton.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}


