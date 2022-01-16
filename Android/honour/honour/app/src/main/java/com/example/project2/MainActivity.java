package com.example.project2;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LinearLayout sListLayout;
    //收藏夹
    MyAdapter productListAdapter;
    MyAdapter productListAdapter2;
    RecyclerView rv;//主页面
    RecyclerView rv2;//添加页面
    //收藏夹内的内容
    ArrayList<Product> list;
    ArrayList<Product> list2;
    FloatingActionButton fab;
    FloatingActionButton fab2;
    boolean isList = false;

    private Bitmap picture;
    private Bitmap icon;

    public int[] image_pos ={0,R.drawable.zhanshi,R.drawable.fashi,R.drawable.tanke,R.drawable.cike,R.drawable.sheshou,R.drawable.fuzhu};
    public String[] hero_pos ={"","战士","法师","坦克","刺客","射手","辅助"};
    //数据库建立
    private static final String DB_NAME= "db_name";
    private static final String TABLE_NAME = "hero";
    private static final String TABLE_NAME1 = "skill";
    public MyDB dbHelper = new MyDB(MainActivity.this,DB_NAME,null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //悬浮框按钮
        fab = (FloatingActionButton) findViewById(R.id.fab); //页面切换按钮
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);//搜索按钮

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isList) {
                    rv.setVisibility(View.VISIBLE);
                    rv2.setVisibility(View.INVISIBLE);
                    fab.setImageResource(R.drawable.add);
                } else {
                    rv.setVisibility(View.INVISIBLE);
                    rv2.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.eye);
                }
                isList = !isList;
                return;
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), search.class);
                startActivity(intent);
            }
        });

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        getData();

        //recycleView的设置。
        rv = (RecyclerView)findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        productListAdapter = new MyAdapter(list, list2, 0,MainActivity.this);
        rv.setAdapter(productListAdapter);
        rv2 = (RecyclerView)findViewById(R.id.newrecyclerView);
        rv2.setLayoutManager(new LinearLayoutManager(this));
        productListAdapter2 = new MyAdapter(list2, list, 1,MainActivity.this);
        rv2.setAdapter(productListAdapter2);
    }

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

                if (isadd.equals("true")) {
                    list.add(new Product(heroid,heroname,
                            skill0,skill0id,skill0str,skill1,skill1id,skill1str,
                            skill2,skill2id,skill2str,skill3,skill3id,skill3str,
                            life,attack,skin,pos_hero,skilleffort,difficult));
                }
                else {
                    list2.add(new Product(heroid,heroname,
                            skill0,skill0id,skill0str,skill1,skill1id,skill1str,
                            skill2,skill2id,skill2str,skill3,skill3id,skill3str,
                            life,attack,skin,pos_hero,skilleffort,difficult));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    //响应新建页面
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        int productIndex = data.getExtras().getInt("index");
        if (productIndex >= 0) {
            Log.i("aaaaaaa", data.getExtras().toString());
            String hero_id = list.get(productIndex).getId();
            String skill0_id = list.get(productIndex).getSkill0_id();
            String skill1_id = list.get(productIndex).getSkill1_id();
            String skill2_id = list.get(productIndex).getSkill2_id();
            String skill3_id = list.get(productIndex).getSkill3_id();
            String new_name = data.getExtras().getString("name");
            String new_call = data.getExtras().getString("called");
            String new_skill0 = data.getExtras().getString("被动");
            Log.i("bbbbbbb",new_skill0);
            String new_skill1 = data.getExtras().getString("技能1");
            String new_skill2 = data.getExtras().getString("技能2");
            String new_skill3 = data.getExtras().getString("技能3");
            String new_life = data.getExtras().getString("生存能力");
            String new_attack = data.getExtras().getString("攻击能力");
            String new_difficulty =data.getExtras().getString("上手难度");
            String new_skilleffort =data.getExtras().getString("技能效果");

            list.get(productIndex).setName(new_name);
            list.get(productIndex).setCalled(new_call);
            //list.get(productIndex).setPosition(data.getExtras().getInt("位置"));
            list.get(productIndex).setSkill0(new_skill0);
            list.get(productIndex).setSkill1(new_skill1);
            list.get(productIndex).setSkill2(new_skill2);
            list.get(productIndex).setSkill3(new_skill3);
            list.get(productIndex).setLife(new_life);
            list.get(productIndex).setAttack(new_attack);
            list.get(productIndex).setDifficulty(new_difficulty);
            list.get(productIndex).setSkill(new_skilleffort);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("name", new_name);
            cv.put("skill", new_skilleffort);
            cv.put("difficulty", new_difficulty);
            cv.put("skin_name",new_call);
            cv.put("live", new_life);
            cv.put("attack", new_attack);
            db.update(TABLE_NAME,cv,"hero_id = ?",new String[]{hero_id});
            ContentValues cv0 = new ContentValues();
            cv0.put("name",new_skill0);
            db.update(TABLE_NAME1,cv0,"skill_id = ?",new String[]{skill0_id});
            ContentValues cv1 = new ContentValues();
            cv1.put("name",new_skill1);
            db.update(TABLE_NAME1,cv1,"skill_id = ?",new String[]{skill1_id});
            ContentValues cv2 = new ContentValues();
            cv2.put("name",new_skill2);
            db.update(TABLE_NAME1,cv2,"skill_id = ?",new String[]{skill2_id});
            ContentValues cv3 = new ContentValues();
            cv3.put("name",new_skill3);
            db.update(TABLE_NAME1,cv3,"skill_id = ?",new String[]{skill3_id});
            db.close();
            productListAdapter.notifyDataSetChanged();
        }
    }
}
