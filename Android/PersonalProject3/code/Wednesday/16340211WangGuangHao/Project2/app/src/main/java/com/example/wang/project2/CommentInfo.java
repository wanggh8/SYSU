package com.example.wang.project2;

import android.graphics.Bitmap;

public class CommentInfo  {
    private String Name;
    private String Speak;
    private Bitmap aIcon;
    private String Date;
    private int Num;
    private int Zan;

    public CommentInfo() {
    }

    public CommentInfo(String Name, String Speak, Bitmap aIcon, String Date, int Num,int Zan) {
        this.Name = Name;
        this.Speak = Speak;
        this.aIcon = aIcon;
        this.Date = Date;
        this.Num = Num;
        this.Zan = Zan;
    }

    public String getName() {
        return Name;
    }

    public String getSpeak() {
        return Speak;
    }

    public Bitmap getIcon() {
        return aIcon;
    }
    public int getNum(){
        return Num;
    }
    public int getZan(){
        return Zan;
    }
    public String getDate(){
        return Date;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setSpeak(String Speak) {
        this.Speak = Speak;
    }


    public void setNum(int Num) {
        this.Num = Num;
    }
}