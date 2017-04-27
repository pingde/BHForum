package com.baiheplayer.bbs.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.MyNews;
import com.baiheplayer.bbs.bean.MyNewsBody;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.view.DetailActivity;
import com.google.gson.Gson;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/2/10.
 */

public class MyNewsAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MyNews> list;
    private static int PRAISE_TOPIC = 10;
    private static int COMMENT_TOPIC = 11;
    private static int RESP_COMMENT = 12;
    private static int STORE_TOPIC = 13;
    private static int PRAISE_COMMENT = 14;

    public MyNewsAdapter() {
    }

    public MyNewsAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_my_news, null);
        return new MyNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyNews myNews = list.get(position);
        String str_body = myNews.getBody();
        MyNewsBody mnb = new Gson().fromJson(str_body, MyNewsBody.class);

        MyNewsViewHolder vh = (MyNewsViewHolder) holder;
        ImageOptions options = new ImageOptions.Builder().setCircular(true).setCrop(true).build();
        x.image().bind(vh.avatar, mnb.getFromUserAvatar(), options);
        vh.nickName.setText(mnb.getFromUserNickname());
//        vh.nickName.setText(mnb.getFromUserNickname());
        vh.createTime.setText(SaveUtils.formatTime(String.valueOf(myNews.getCreateTime())));
        dealNews(myNews, mnb, vh);
        final int type = myNews.getType();
        vh.doDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToDetail = new Intent();
                intentToDetail.putExtra("topicId", myNews.getRelated());
                intentToDetail.setClass(context, DetailActivity.class);
                if (type == PRAISE_TOPIC || type == COMMENT_TOPIC || type == STORE_TOPIC) {
                    intentToDetail.putExtra("isLoadToContent", true);
                } else {
                    intentToDetail.putExtra("isLoadToContent", false);
                }
                context.startActivity(intentToDetail);
            }
        });
    }

    private void dealNews(MyNews myNews, MyNewsBody mnb, MyNewsViewHolder vh) {
        if (myNews.getType() == PRAISE_TOPIC) {           //赞了帖子10
            vh.newsContent.setText("赞了你的文章");   //
            vh.newsTarget.setText("《" + mnb.getTopicDetail().getTitle() + "》");
            vh.doResponse.setVisibility(View.INVISIBLE);
        }
        if (myNews.getType() == COMMENT_TOPIC) {          //评论帖子11
            vh.newsContent.setText(mnb.getContent());
            vh.newsType.setText("评论我的文章:");
            vh.newsTarget.setText("《" + mnb.getTopicDetail().getTitle() + "》");
            vh.doResponse.setVisibility(View.VISIBLE);
        }
        if (myNews.getType() == RESP_COMMENT) {           //回复评论12
            vh.newsContent.setText(mnb.getContent());
            vh.newsType.setText("回复我的评论:");
            vh.newsTarget.setText(mnb.getCommentDetail().getContent());
            vh.doResponse.setVisibility(View.VISIBLE);
        }
        if (myNews.getType() == STORE_TOPIC) {            //收藏帖子13
            vh.newsContent.setText("收藏了我的文章");
            vh.newsTarget.setText("《" + mnb.getTopicDetail().getTitle() + "》");
            vh.doResponse.setVisibility(View.INVISIBLE);
        }
        if (myNews.getType() == PRAISE_COMMENT) {         //点赞评论14
            vh.newsContent.setText("赞了我的评论");
            vh.newsTarget.setText(mnb.getCommentDetail().getContent());
            vh.doResponse.setVisibility(View.INVISIBLE);
        }
    }


    class MyNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;       //消息人的头像
        TextView nickName;      //消息人的昵称
        TextView newsContent;   //赞、收藏、回复、评论
        TextView newsType;      //评论时显示蓝色过渡字
        TextView newsTarget;    //被消息的目标
        TextView createTime;    //回复的时间
        LinearLayout doResponse;//回复一下
        RelativeLayout doDetail;

        public MyNewsViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.img_avatar);
            nickName = (TextView) itemView.findViewById(R.id.tv_nike_name);
            newsContent = (TextView) itemView.findViewById(R.id.tv_my_news_content);
            newsType = (TextView) itemView.findViewById(R.id.tv_my_news_type);
            newsTarget = (TextView) itemView.findViewById(R.id.tv_my_news_target);
            createTime = (TextView) itemView.findViewById(R.id.tv_create_time);
            doResponse = (LinearLayout) itemView.findViewById(R.id.ll_my_news_response);
            doDetail = (RelativeLayout) itemView.findViewById(R.id.rl_detail);
        }
    }
}