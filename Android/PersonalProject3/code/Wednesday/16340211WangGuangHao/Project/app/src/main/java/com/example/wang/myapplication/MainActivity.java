package com.example.wang.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "Password";
    public static final String PREFERENCE_PACKAGE = "com.example.wang.myapplication";
    public static final int MODE = Context.MODE_PRIVATE;
    private boolean tag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText new_password = findViewById(R.id.NewPassword);
        final EditText con_password = findViewById(R.id.ConfirmPassword);
        final Button ok = findViewById(R.id.ok_btn);
        final Button clear = findViewById(R.id.clear_btn0);
        Context context = null;
        try {
            context = this.createPackageContext(PREFERENCE_PACKAGE,Context.CONTEXT_IGNORE_SECURITY);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_NAME, MODE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final String spassword = sharedPref.getString("Password", null);
        if (spassword != null) {
            tag = false;
            new_password.setVisibility(View.INVISIBLE);
            con_password.setHint("Password");
        }

        //清除按钮侦听
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                con_password.setText("");
                new_password.setText("");
            }
        });

        // OK按钮侦听
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password1 = new_password.getText().toString();
                String password2 = con_password.getText().toString();
                //注册时
                if(tag) {
                    if (TextUtils.isEmpty(password1) && TextUtils.isEmpty(password2)){
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else if (!password1.equals(password2)) {
                        Toast.makeText(MainActivity.this, "Password Mismatch.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        editor.putString("Password", password1);
                        editor.commit();
                        tag = false;
                        new_password.setVisibility(View.INVISIBLE);
                        con_password.setHint("Password");
                        con_password.setText("");
                        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                        startActivity(intent);
                    }
                }
                //登陆时
                else {
                    if (TextUtils.isEmpty(password2)) {
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else if (password2.equals(spassword) || password2.equals(password1)){
                        con_password.setText("");
                        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Invalid Password.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
