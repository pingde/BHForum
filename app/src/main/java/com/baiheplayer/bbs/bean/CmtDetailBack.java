package com.baiheplayer.bbs.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */

public class CmtDetailBack extends DataBack {
    private Data data;

    private class Data {
        private Comment commentDetail;
        private List<CommentRespond> reply;
        private Topic topicDetail;

    }
    public Comment getCommentDetail() {
        return data.commentDetail;
    }

    public void setCommentDetail(Comment commentDetail) {
        this.data.commentDetail = commentDetail;
    }

    public Topic getTopicDetail() {
        return data.topicDetail;
    }

    public void setTopicDetail(Topic topicDetail) {
        this.data.topicDetail = topicDetail;
    }

    public List<CommentRespond> getReply() {
        return data.reply;
    }

    public void setReply(List<CommentRespond> reply) {
        this.data.reply = reply;
    }
}
