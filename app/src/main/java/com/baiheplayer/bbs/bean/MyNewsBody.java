package com.baiheplayer.bbs.bean;

/**
 * 我的消息，MyNews的属性body是一条json数据，用这个类来解析
 * Created by Administrator on 2017/2/14.
 */

public class MyNewsBody {
    private String content;             //通知消息内容
    private String fromUserId;          //用户id
    private String fromUserNickname;    //用户昵称
    private String fromUserAvatar;      //用户头像
    private int type;                   //消息的种类

    private Topic  topicDetail;
    private Comment commentDetail;
    private CommentRespond replyCommentDetail;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserNickname() {
        return fromUserNickname;
    }

    public void setFromUserNickname(String fromUserNickname) {
        this.fromUserNickname = fromUserNickname;
    }

    public String getFromUserAvatar() {
        return fromUserAvatar;
    }

    public void setFromUserAvatar(String fromUserAvatar) {
        this.fromUserAvatar = fromUserAvatar;
    }

    public Topic getTopicDetail() {
        return topicDetail;
    }

    public void setTopicDetail(Topic topicDetail) {
        this.topicDetail = topicDetail;
    }

    public Comment getCommentDetail() {
        return commentDetail;
    }

    public void setCommentDetail(Comment commentDetail) {
        this.commentDetail = commentDetail;
    }

    public CommentRespond getReplyCommentDetail() {
        return replyCommentDetail;
    }

    public void setReplyCommentDetail(CommentRespond replyCommentDetail) {
        this.replyCommentDetail = replyCommentDetail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
