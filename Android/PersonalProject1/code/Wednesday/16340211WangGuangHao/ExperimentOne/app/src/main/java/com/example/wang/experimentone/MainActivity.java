package com.example.wang.experimentone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //加载布局
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

        //窗口生成
        dialog.setTitle("提示");
        // RadioButton rButton = findViewById(radioGroup.getCheckedRadioButtonId());
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 点击后的事件处理
                Toast.makeText(MainActivity.this, "对话框“取消”按钮被点击", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 点击后的事件处理
                Toast.makeText(MainActivity.this, "对话框“确定”按钮被点击", Toast.LENGTH_SHORT).show();
            }
        });
        //侦听组单选按钮
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final RadioButton rbutton = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                Toast.makeText(getApplication(),rbutton.getText().toString()+"被选中", Toast.LENGTH_SHORT).show();

            }
        });
        //侦听搜索按键按钮
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton rbutton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                if (TextUtils.isEmpty(searchContent.getText().toString())) {
                    // 弹出Toast消息
                    Toast.makeText(MainActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.equals(searchContent.getText().toString(), "Health")) {
                    dialog.setMessage(rbutton.getText().toString()+"搜索成功");
                    dialog.show();
                }
                else {
                    dialog.setMessage("搜索失败");
                    dialog.show();
                }
            }
        });
    }
}