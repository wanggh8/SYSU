package com.example.project2;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

public class Product {
    private String id;
    private String name;

    private String skill0;
    private String skill0_id;
    private String skill0_str;

    private String skill1;
    private String skill1_id;
    private String skill1_str;

    private String skill2;
    private String skill2_id;
    private String skill2_str;

    private String skill3;
    private String skill3_id;
    private String skill3_str;

    private String life;
    private String attack;
    private String called;
    private String skill;
    private String difficulty;
    private int position;
    String rootpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/honor/";

    public String[] hero_pos ={"","战士","法师","坦克","刺客","射手","辅助"};
    public int[] image_pos ={0,R.drawable.zhanshi,R.drawable.fashi,R.drawable.tanke,R.drawable.cike,R.drawable.sheshou,R.drawable.fuzhu};

    public Product(String id,String name,
                   String skill0,String skill0_id,String skill0_str, String skill1,String skill1_id,String skill1_str,
                   String skill2,String skill2_id,String skill2_str, String skill3,String skill3_id,String skill3_str,
                   String life, String attack, String called,int position,String skill,String difficulty) {
        this.id = id;
        this.name = name;

        this.skill0 = skill0;
        this.skill0_id = skill0_id;
        this.skill0_str = skill0_str;

        this.skill1 = skill1;
        this.skill1_id = skill1_id;
        this.skill1_str = skill1_str;

        this.skill2 = skill2;
        this.skill2_id = skill2_id;
        this.skill2_str = skill2_str;

        this.skill3 = skill3;
        this.skill3_id = skill3_id;
        this.skill3_str = skill3_str;

        this.life = life;
        this.attack = attack;
        this.called = called;
        this.position = position;
        this.skill = skill;
        this.difficulty = difficulty;
    }

    public String getId() {
        return id;
    }

    public String getSkill() {return skill;}
    public String getDifficulty() {return difficulty;}

    public void setSkill(String skill) {this.skill = skill;}
    public void setDifficulty(String difficulty) {this.difficulty = difficulty;}

    public String getSkill0() {
        return skill0;
    }
    public String getSkill0_id() {
        return skill0_id;
    }
    public String getSkill0_str() {
        return skill0_str;
    }

    public String getSkill1() {
        return skill1;
    }
    public String getSkill1_id() {
        return skill1_id;
    }
    public String getSkill1_str() {
        return skill1_str;
    }


    public String getSkill2() { return skill2; }
    public String getSkill2_id() { return skill2_id; }
    public String getSkill2_str() { return skill2_str; }

    public String getSkill3() {
        return skill3;
    }
    public String getSkill3_id() {
        return skill3_id;
    }
    public String getSkill3_str() {
        return skill3_str;
    }

    public String getName() {
        return name;
    }

    public int getPos_image(){return image_pos[position];}

    public String getpicture() {return rootpath +id+".jpg";}

    public String geticon() {return rootpath + id+"icon.jpg";}

    public String getLife() {return life;}

    public String getCalled() {return called;}

    public String getAttack() {return attack;}

    public String getPosition() {return hero_pos[position];}


    public void setAttack(String attack) {
        this.attack = attack;
    }

    public void setCalled(String called) {
        this.called = called;
    }

    public void setLife(String life) {
        this.life = life;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSkill0(String skill0) {
        this.skill0 = skill0;
    }

    public void setSkill1(String skill1) {
        this.skill1 = skill1;
    }

    public void setSkill2(String skill2) {
        this.skill2 = skill2;
    }

    public void setSkill3(String skill3) {
        this.skill3 = skill3;
    }

    public String getall() {
        return name+skill0+skill1+skill2+skill3+called+hero_pos[position]+"1";
    }



}