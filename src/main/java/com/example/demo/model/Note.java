package com.example.demo.model;

import javax.xml.soap.Node;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

//数据模型类
public class Note {

    /*
        ids:            主键，ID
        times:          时间
        title:          标题
        content:        内容
        audioPath:      音频存储路径
        picturePath:    图片存储路径
     */
    private String user;
    private Integer id;
    private Integer ids;
    private Timestamp times;
    private String title;
    private String content;
    private String audioPath;
    private String picturePath;
    private Timestamp bgShareTime;
    private Timestamp edShareTime;

    public Note(String user,Integer id,Integer ids, Timestamp times, String title, String content, String audioPath, String picturePath,Timestamp bgShareTime,Timestamp edShareTime) {
        this.user = user;
        this.id = id;
        this.ids = ids;
        this.times = times;
        this.title = title;
        this.content = content;
        this.audioPath = audioPath;
        this.picturePath = picturePath;
        this.bgShareTime = bgShareTime;
        this.edShareTime = edShareTime;
    }
    public Note(Integer ids){
        this.ids = ids;
        this.times = null;
        this.title = "";
        this.content = "";
        this.audioPath = "";
        this.picturePath = "";
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIds() {
        return ids;
    }

    public void setIds(Integer ids) {
        this.ids = ids;
    }

    public Timestamp getTimes() {
        return times;
    }

    public void setTimes(Timestamp times) {
        this.times = times;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Timestamp getBgShareTime() {
        return bgShareTime;
    }

    public void setBgShareTime(Timestamp bgShareTime) {
        this.bgShareTime = bgShareTime;
    }

    public Timestamp getEdShareTime() {
        return edShareTime;
    }

    public void setEdShareTime(Timestamp edShareTime) {
        this.edShareTime = edShareTime;
    }

    @Override
    public String toString() {
        return "Note{" +
                "user='" + user + '\'' +
                ", id=" + id +
                ", ids=" + ids +
                ", times=" + times +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", audioPath='" + audioPath + '\'' +
                ", picturePath='" + picturePath + '\'' +
                ", bgShareTime=" + bgShareTime +
                ", edShareTime=" + edShareTime +
                '}';
    }
}
