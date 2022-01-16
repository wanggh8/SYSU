package com.example.password_keeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.password_keeper.R;
import com.example.password_keeper.constants.Constants;
import com.example.password_keeper.storage.MySP;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置页面的activity
 */
public class SettingsActivity extends AppCompatActivity {

    RelativeLayout mPasswordLength;
    SwitchCompat mSwitchAlphabat;
    SwitchCompat mSwitchNumber;
    SwitchCompat mSwitchSymbol;
    TextView mChangePasswordTv;
    private MySP mMySP;

//    设置界面
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mPasswordLength = findViewById(R.id.password_length);
        mSwitchAlphabat = findViewById(R.id.switch_alphabat);
        mSwitchNumber = findViewById(R.id.switch_number);
        mSwitchSymbol = findViewById(R.id.switch_symbol);
        mChangePasswordTv = findViewById(R.id.change_password_tv);

        //从本地读取用户设置，将各个选项设置成用户设置好的
        mMySP = new MySP(this).getmMySP();
        mSwitchAlphabat.setChecked(mMySP.load(Constants.IS_ALPHABAT_ON, false));
        mSwitchNumber.setChecked(mMySP.load(Constants.IS_NUMBER_ON, false));
        mSwitchSymbol.setChecked(mMySP.load(Constants.IS_SYMBOL_ON, false));

        mSwitchAlphabat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMySP.save(Constants.IS_ALPHABAT_ON, true);
                } else {
                    mMySP.save(Constants.IS_ALPHABAT_ON, false);
                }
            }
        });

        mSwitchNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMySP.save(Constants.IS_NUMBER_ON, true);
                } else {
                    mMySP.save(Constants.IS_NUMBER_ON, false);
                }
            }
        });

        mSwitchSymbol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMySP.save(Constants.IS_SYMBOL_ON, true);
                } else {
                    mMySP.save(Constants.IS_SYMBOL_ON, false);
                }
            }
        });

        mChangePasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

    }

}

