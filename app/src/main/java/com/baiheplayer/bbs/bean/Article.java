package com.baiheplayer.bbs.bean;


import java.util.List;

/**
 * Created by Administrator on 2016/12/31.
 */

public class Article {
    private String id;              //帖子id
    private String title;           //标题
    private String author;          //作者
    private long date;       //发帖时间
    private String url;             //url
    private List<String> images;        //图片

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
