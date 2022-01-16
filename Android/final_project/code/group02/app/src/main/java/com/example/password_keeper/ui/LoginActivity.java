package com.example.password_keeper.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.password_keeper.R;
import com.example.password_keeper.secures.BaseSecure;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *登陆界面
 */
public class LoginActivity extends AppCompatActivity {


    EditText mUsernameEt;
    EditText mPasswordEt;
    Button mLoginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsernameEt = findViewById(R.id.username);
        mPasswordEt = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                if(BaseSecure.isUserInputLegal(username, password)
                        && BaseSecure.authenticateUser(username, password))
                {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("用户名或密码输入错误，请重新输入！");
                    builder.setNegativeButton("取消", null);
                    builder.create().show();
                }
            }
        });
    }
}
