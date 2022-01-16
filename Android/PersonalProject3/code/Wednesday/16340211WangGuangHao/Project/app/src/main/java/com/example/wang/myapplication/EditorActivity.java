package com.example.wang.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity {

    private String filename = "test_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_edit);
        Button save = findViewById(R.id.save_btn);
        Button load = findViewById(R.id.load_btn);
        Button clear = findViewById(R.id.clear_btn);
        final EditText file = findViewById(R.id.file);

        //SAVE按钮侦听
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try (FileOutputStream fileOutputStream = openFileOutput(filename, MODE_PRIVATE)) {
                    String input = file.getText().toString();
                    fileOutputStream.write(input.getBytes());
                    Toast.makeText(EditorActivity.this, "Save successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Toast.makeText(EditorActivity.this, "Fail to save file.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //LOAD按钮侦听
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try (FileInputStream fileInputStream = openFileInput(filename)) {
                    byte[] contents = new byte[fileInputStream.available()];
                    fileInputStream.read(contents);
                    file.setText(new String(contents));
                    Toast.makeText(EditorActivity.this, "Load successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Toast.makeText(EditorActivity.this, "Fail to load file.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file.setText("");
            }
        });

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}