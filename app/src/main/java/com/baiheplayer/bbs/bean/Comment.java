package com.baiheplayer.bbs.bean;

import java.util.List;

/**
 * 评论详情
 * Created by Administrator on 2016/12/1.
 */

public class Comment {
    private String id;          //评论id
    private String userId;      //评论用户id
    private String nickname;    //评论用户昵称
    private String createTime;  //评论时间
    private String avatar;      //评论用户头像
    private String content;     //评论内容
    private String countPraise; //累计赞
    private boolean isPraise;   //给过赞
    private Reply  reply;       //回复列表

    private class Reply{
        private String total;
        private String pages;
        private String current;
        private List<CommentRespond> list;
    }
    public String getTotal() {
        return reply.total;
    }

    public void setTotal(String total) {
        this. reply.total = total;
    }

    public String getPages() {
        return  reply.pages;
    }

    public void setPages(String pages) {
        this. reply.pages = pages;
    }

    public String getCurrent() {
        return reply.current;
    }

    public void setCurrent(String current) {
        this. reply.current = current;
    }

    public List<CommentRespond> getList() {
        return  reply.list;
    }

    public void setList(List<CommentRespond> list) {
        this. reply.list = list;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCountPraise() {
        return countPraise;
    }

    public void setCountPraise(String countPraise) {
        this.countPraise = countPraise;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
