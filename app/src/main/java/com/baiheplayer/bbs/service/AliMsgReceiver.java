package com.baiheplayer.bbs.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.MyNewsBody;
import com.baiheplayer.bbs.utils.Share;
import com.baiheplayer.bbs.view.CommentDetailActivity;
import com.baiheplayer.bbs.view.DetailActivity;
import com.google.gson.Gson;


/**
 * Created by Administrator on 2017/2/20.
 */

public class AliMsgReceiver extends MessageReceiver {
    private String contentText;
    private CPushMessage message;
    private int noticeId = 10;
    private static final int PRAISE_TOPIC = 10;
    private static final int COMMENT_TOPIC = 11;
    private static final int RESP_COMMENT = 12;
    private static final int STORE_TOPIC = 13;
    private static final int PRAISE_COMMENT = 14;

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        super.onMessage(context, cPushMessage);
        message = cPushMessage;
        Log.i("chen", "接收到的推送是： " + cPushMessage.getTitle() + " " + cPushMessage.getContent());
        MyNewsBody mnb = new Gson().fromJson(cPushMessage.getContent(), MyNewsBody.class);
        App.setHaveNews(true);
        if (!Share.getBoolean("acceptPush", true)) {  //关闭推送
            return;
        }
        switch (mnb.getType()) {

            case PRAISE_TOPIC:
                contentText = "《" + mnb.getTopicDetail().getTitle() + "》";
                Intent intent10 = new Intent(context, DetailActivity.class);
                intent10.putExtra("topicId", mnb.getTopicDetail().getId());
                sendNotification(context, intent10, mnb);
                break;
            case STORE_TOPIC:
                contentText = "《" + mnb.getTopicDetail().getTitle() + "》";
                Intent intent13 = new Intent(context, DetailActivity.class);
                intent13.putExtra("topicId", mnb.getTopicDetail().getId());
                sendNotification(context, intent13, mnb);
                break;
            case COMMENT_TOPIC:
                contentText = "《" + mnb.getTopicDetail().getTitle() + "》";
                Intent intent11 = new Intent(context, DetailActivity.class);
                intent11.putExtra("topicId", mnb.getTopicDetail().getId());
                sendNotification(context, intent11, mnb);
                break;
            case RESP_COMMENT:   //回复评论
                contentText = "\"" + mnb.getCommentDetail().getContent() + "\"";
                Intent intent12 = new Intent(context, CommentDetailActivity.class);
                intent12.putExtra("topicId", mnb.getTopicDetail().getId());
                intent12.putExtra("commentId", mnb.getReplyCommentDetail().getId());
                sendNotification(context, intent12, mnb);
                break;
            case PRAISE_COMMENT:
                contentText = "\"" + mnb.getCommentDetail().getContent() + "\"";
                Intent intent14 = new Intent(context, CommentDetailActivity.class);
                intent14.putExtra("topicId", mnb.getTopicDetail().getId());
                intent14.putExtra("commentId", mnb.getCommentDetail().getId());
                sendNotification(context, intent14, mnb);
                break;
        }
    }

    private void sendNotification(Context context, Intent i, MyNewsBody mnb) {

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mnb.getContent())  //message.getTitle()
                .setContentText(contentText)
                .setContentIntent(pi);
        nm.notify(++noticeId, mBuilder.build());
        Log.i("chen", "通知发送成功 ");
    }
}
