package com.baiheplayer.bbs.bean;

/**
 * 评论的回复详情
 * Created by Administrator on 2016/12/1.
 */

public class CommentRespond {
    private String id;
    private String userId;
    private String nickname;
    private String createTime;
    private String avatar;
    private String content;
    private String countPraise;
    private boolean isPraise;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
