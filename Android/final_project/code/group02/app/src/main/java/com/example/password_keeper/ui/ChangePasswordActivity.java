package com.example.password_keeper.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.example.password_keeper.MyApplication;
import com.example.password_keeper.R;
import com.example.password_keeper.beans.User;
import com.example.password_keeper.constants.Constants;
import com.example.password_keeper.greendao.UserDao;
import com.example.password_keeper.secures.BCrypt;
import com.example.password_keeper.secures.BaseSecure;
import com.example.password_keeper.storage.MySP;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 更改密码界面
 */
public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivity";

    @BindView(R.id.old_password_et)
    EditText mOldPasswordEt;

    @BindView(R.id.new_password1_et)
    EditText mNewPassword1Et;

    @BindView(R.id.new_password2_et)
    EditText mNewPassword2Et;

    @BindView(R.id.update_button)
    Button mUpdateButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }


    /**
     * 点击提交按钮
     */
    @OnClick(R.id.update_button)
    public void onViewClicked() {
        MySP mySP = new MySP(this).getmMySP();
        int minLength = mySP.load(Constants.KEY_LENGTH, 8);
        String oldPassword = mOldPasswordEt.getText().toString().trim();
        String newPassword1 = mNewPassword1Et.getText().toString().trim();
        String newPassword2 = mNewPassword2Et.getText().toString().trim();

        if(BaseSecure.isOldPasswordCorrect(oldPassword)
                && newPassword1.length() > minLength
                && newPassword1.equals(newPassword2)){
            UserDao userDao = MyApplication.getInstance().getDaoSession().getUserDao();
            User user = userDao.queryBuilder().where(UserDao.Properties.Id.eq(1L)).unique();
            user.setPassword(BCrypt.hashpw(newPassword1, BCrypt.gensalt()));
            userDao.update(user);

            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("注意");
            builder.setMessage("输入有误，请重新输入！");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mOldPasswordEt.setText(null);
                    mNewPassword1Et.setText(null);
                    mNewPassword2Et.setText(null);
                }
            });
            builder.setNegativeButton("取消", null);
            builder.create().show();

        }
    }
}
