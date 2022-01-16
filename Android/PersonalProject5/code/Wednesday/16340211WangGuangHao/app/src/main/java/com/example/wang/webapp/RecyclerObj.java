package com.example.wang.webapp;

import android.graphics.Bitmap;

public class RecyclerObj {
    private Boolean status;
    private Data data;
    private Bitmap bmp;
    public static class Data  {
        private int aid;
        private int state;
        private String cover;
        private String title;
        private String content;
        private int play;
        private String duration;
        private int video_review;
        private String create;
        private String rec;
        private int count;

        public int getAid() {
            return aid;
        }

        public int getCount() {
            return count;
        }

        public int getPlay() {
            return play;
        }

        public int getState() {
            return state;
        }

        public int getVideo_review() {
            return video_review;
        }

        public String getContent() {
            return content;
        }

        public String getCover() {
            return cover;
        }

        public String getCreate() {
            return create;
        }

        public String getDuration() {
            return duration;
        }

        public String getRec() {
            return rec;
        }

        public String getTitle() {
            return title;
        }

        public void setAid(int aid) {
            this.aid = aid;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public void setPlay(int play) {
            this.play = play;
        }

        public void setRec(String rec) {
            this.rec = rec;
        }

        public void setState(int state) {
            this.state = state;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setVideo_review(int video_review) {
            this.video_review = video_review;
        }

        public String get_info1() {
            return "播放： "+ getPlay()+""+" 评论： "+getVideo_review()+""+" 时长： "+getDuration();
        }

        public String get_info2() {
            return "创建时间： "+getCreate();
        }

    }

    public Boolean getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public Bitmap getBmp() {
        return bmp;
    }

}