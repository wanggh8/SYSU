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

import com.example.password_keeper.MyApplication;
import com.example.password_keeper.R;
import com.example.password_keeper.beans.User;
import com.example.password_keeper.constants.Constants;
import com.example.password_keeper.greendao.UserDao;
import com.example.password_keeper.secures.BCrypt;
import com.example.password_keeper.storage.MySP;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册界面
 */
public class RegisterActivity extends AppCompatActivity {


    EditText mUsernameEt;
    EditText mPassword1Et;
    EditText mPassword2Et;
    Button mrRgisterButton;
    private MySP mMySP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mUsernameEt = findViewById(R.id.username_et);
        mPassword1Et = findViewById(R.id.password1_et);
        mPassword2Et = findViewById(R.id.password2_et);
        mrRgisterButton = findViewById(R.id.register_button);
        mMySP = new MySP(this).getmMySP();
        mMySP.save(Constants.IS_ALPHABAT_ON, true);
        mMySP.save(Constants.IS_NUMBER_ON, true);
        mMySP.save(Constants.IS_SYMBOL_ON, true);
        mMySP.save(Constants.KEY_LENGTH, 8);
        //注册按钮的点击事件
        mrRgisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsernameEt.getText().toString().trim();
                String password1 = mPassword1Et.getText().toString().trim();
                String password2 = mPassword2Et.getText().toString().trim();

                if(username.length() > 0
                        && password1.equals(password2)
                        && password1.length() > 8){
                    UserDao userDao = MyApplication.getInstance().getDaoSession().getUserDao();
                    //用户名直接存入数据库，密码加密后存入数据库
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(BCrypt.hashpw(password1, BCrypt.gensalt()));
                    userDao.insert(user);

                    mMySP.save(Constants.IS_HAS_USER, true);
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("注意");
                    if (username.length() == 0) {
                        builder.setMessage("用户名不能为空，请仔细检查！");
                    }
                    else {
                        if (!password1.equals((password2))) {
                            builder.setMessage("两次输入密码不一致，请再次确认！");
                        }
                        else {
                            builder.setMessage("密码必须大于八位，请重新输入！");
                        }
                    }
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create().show();
                }
            }
        });
    }

}
