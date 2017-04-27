package com.baiheplayer.bbs.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */

public class Topic {
    private String id;              //帖子id
    private String createTime;       //发帖时间
    private String userId;          //发帖用户id
    private String nickname;        //发帖用户昵称
    private String avatar;          //发帖用户头像
    private String title;           //标题
    private String summary;         //简介
    private String content;         //详细信息
    private List<String> images;        //图片
    private String countView;       //查看次数
    private String countPraise;      //点赞次数
    private String countFavorite;   //收藏次数
    private String countComment;    //评论次数
    private boolean isFavorite;     //当前用户是否收藏
    private boolean isPraise;       //。。。     点赞
    private boolean isReport;       //。。。     举报
    private String reason;
    private int status;
    private boolean statusDesc;
    private boolean isDelete;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCountView() {
        return countView;
    }

    public void setCountView(String countView) {
        this.countView = countView;
    }

    public String getCountPrase() {
        return countPraise;
    }

    public void setCountPrase(String countPrase) {
        this.countPraise = countPrase;
    }

    public String getCountFavorite() {
        return countFavorite;
    }

    public void setCountFavorite(String countFavorite) {
        this.countFavorite = countFavorite;
    }

    public String getCountComment() {
        return countComment;
    }

    public void setCountComment(String countComment) {
        this.countComment = countComment;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
    }

    public boolean isReport() {
        return isReport;
    }

    public void setReport(boolean report) {
        isReport = report;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getCountPraise() {
        return countPraise;
    }

    public void setCountPraise(String countPraise) {
        this.countPraise = countPraise;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String creatTime) {
        this.createTime = creatTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(boolean statusDesc) {
        this.statusDesc = statusDesc;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
