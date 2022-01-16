package com.example.project2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

public class search extends AppCompatActivity {
    EditText search_tag;
    ArrayList<Product> alllist;
    ArrayList<Product> newlist;
    ArrayList<Product> templist;
    MyAdapter searchAdapter;
    RecyclerView sv;
    private static final String DB_NAME= "db_name";
    private static final String TABLE_NAME = "hero";
    private static final String TABLE_NAME1 = "skill";
    public MyDB dbHelper = new MyDB(search.this,DB_NAME,null,1);

    private void getData() {
        String skill0 = null,skill1 = null,skill2 =null,skill3=null;
        String skill0str = null,skill1str = null,skill2str =null,skill3str=null;
        String skill0id = null,skill1id = null,skill2id =null,skill3id=null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME,null, null, null, null, null, null);
        if (cursor.getCount() != 0 ){
            cursor.moveToNext();
            do {
                String heroname = cursor.getString(cursor.getColumnIndex("name"));
                String heroid = cursor.getString(cursor.getColumnIndex("hero_id"));
                String isadd =  cursor.getString(cursor.getColumnIndex("is_add"));
                int pos_hero = cursor.getInt(cursor.getColumnIndex("hero_type"));
                String life = cursor.getString(cursor.getColumnIndex("live"));
                String attack = cursor.getString(cursor.getColumnIndex("attack"));
                String skin = cursor.getString(cursor.getColumnIndex("skin_name"));
                String skilleffort = cursor.getString(cursor.getColumnIndex("skill"));
                String difficult = cursor.getString(cursor.getColumnIndex("difficulty"));

                Cursor cursor1 = db.query(TABLE_NAME1,null, "hero_id = ?",new String[]{heroid}, null, null, "skill_id asc", null);
                if (cursor1.getCount() !=0){

                    cursor1.moveToNext();
                    skill0=cursor1.getString(cursor1.getColumnIndex("name"));
                    skill0id=cursor1.getString(cursor1.getColumnIndex("skill_id"));
                    skill0str=cursor1.getString(cursor1.getColumnIndex("description"));

                    cursor1.moveToNext();
                    skill1=cursor1.getString(cursor1.getColumnIndex("name"));
                    skill1id=cursor1.getString(cursor1.getColumnIndex("skill_id"));
                    skill1str=cursor1.getString(cursor1.getColumnIndex("description"));

                    cursor1.moveToNext();
                    skill2=cursor1.getString(cursor1.getColumnIndex("name"));
                    skill2id=cursor1.getString(cursor1.getColumnIndex("skill_id"));
                    skill2str=cursor1.getString(cursor1.getColumnIndex("description"));

                    cursor1.moveToNext();
                    skill3=cursor1.getString(cursor1.getColumnIndex("name"));
                    skill3id=cursor1.getString(cursor1.getColumnIndex("skill_id"));
                    skill3str=cursor1.getString(cursor1.getColumnIndex("description"));
                }
                cursor1.close();

                alllist.add(new Product(heroid,heroname,
                        skill0,skill0id,skill0str,skill1,skill1id,skill1str,
                        skill2,skill2id,skill2str,skill3,skill3id,skill3str,
                        life,attack,skin,pos_hero,skilleffort,difficult));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alllist = new ArrayList<>();
        getData();
        newlist = new ArrayList<>();
        setContentView(R.layout.search_hero);
        search_tag = findViewById(R.id.search_tag_input);
        sv = findViewById(R.id.search_recyclerView);
        sv.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new MyAdapter(newlist, templist, 2,search.this);
        sv.setAdapter(searchAdapter);

        search_tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                newlist.clear();
                if (input.length() == 0) {
                    searchAdapter.notifyDataSetChanged();
                    return;
                }
                Log.i("what", "hhh1");
                for (int pos = 0; pos < alllist.size(); pos++) {
                    Log.i("what", alllist.get(pos).getall());
                    Log.i("whatt", input);
                    if (alllist.get(pos).getall().indexOf(input) != -1) {
                        newlist.add(alllist.get(pos));
                    }
                    Log.i("hello", newlist.toString());
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count,int after) {
                Log.i("what", "hhh2");
                String input = s.toString();
                if (input.length() == 0) {
                    newlist.clear();
                    searchAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //将editText中改变的值设置的HashMap中
                Log.i("what", "hhh3");
                String input = s.toString();
                if (input.length() == 0) {
                    newlist.clear();
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //加载
        setDrawLeft(search_tag, R.mipmap.search);
    }

    private void setDrawLeft(EditText editText,int res){
        Drawable drawable = getResources().getDrawable(res);
        drawable.setBounds(0,0,80,80);
        editText.setCompoundDrawables(drawable,null,null,null);
    }




}
