package com.baiheplayer.bbs.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.IRefreshListener;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.view.DetailActivity;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by Administrator on 2017/4/14.
 */

public class MyTopicEntry extends RecyclerView.ViewHolder implements INetCallBack {

    ImageView avatar;
    TextView nikename;
    TextView title;
    TextView createTime;
    TextView countView;
    TextView countPraise;
    TextView countComment;
    CheckBox isPraise;
    ImageView moreOptionClick;
    RelativeLayout doSummary;
    RelativeLayout article;
    LinearLayout pictures;
    LinearLayout doComment;
    LinearLayout doPraise;
    LinearLayout doMoreOption; //显示或隐藏
    LinearLayout doStore;  //收藏
    LinearLayout doReport; //举报

    IRefreshListener i_refresh;
    Topic topic;

    public MyTopicEntry(View itemView) {
        super(itemView);
        avatar = (ImageView) itemView.findViewById(R.id.iv_forum_poster_head);
        nikename = (TextView) itemView.findViewById(R.id.tv_forum_author);   // 跳转个人
        title = (TextView) itemView.findViewById(R.id.tv_forum_title);
        createTime = (TextView) itemView.findViewById(R.id.tv_forum_time);
        countView = (TextView) itemView.findViewById(R.id.tv_forum_seen);
        countComment = (TextView) itemView.findViewById(R.id.tv_forum_comments);
        countPraise = (TextView) itemView.findViewById(R.id.tv_forum_support);
        isPraise = (CheckBox) itemView.findViewById(R.id.iv_forum_support);
        moreOptionClick = (ImageView) itemView.findViewById(R.id.img_forum_more);
        article = (RelativeLayout) itemView.findViewById(R.id.rl_forum_content);
        doSummary = (RelativeLayout) itemView.findViewById(R.id.rl_forum_summary);  //缩略的内容摘要
        doMoreOption = (LinearLayout) itemView.findViewById(R.id.ll_forum_more);
        pictures = (LinearLayout) itemView.findViewById(R.id.ll_forum_picture);
        doStore = (LinearLayout) itemView.findViewById(R.id.ll_forum_add_store);    //收藏
        doReport = (LinearLayout) itemView.findViewById(R.id.ll_forum_report);      //举报
        doComment = (LinearLayout) itemView.findViewById(R.id.ll_forum_comment);    //做评论
        doPraise = (LinearLayout) itemView.findViewById(R.id.ll_forum_praise);      //点个赞

    }

    public void onBind(final Context context,final Topic topic,final IRefreshListener i_refresh){
        this.i_refresh = i_refresh;
        this.topic = topic;
        ImageOptions options = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(avatar, topic.getAvatar(), options);
        int picNum = topic.getImages().size();
        if (picNum != 0) {
            int width = (pictures.getWidth() / picNum);
            // TODO: 2016/11/30  计算宽高
            for (int i = 0; i < picNum; i++) {
                ImageView imageView = new ImageView(context);
                imageView.setMaxWidth(width);
                x.image().bind(imageView, topic.getImages().get(i));
                pictures.addView(imageView);
            }
        }
        nikename.setText(topic.getNickname());
        title.setText(topic.getTitle());
        if (!TextUtils.isEmpty(topic.getSummary())) {
            View view = LayoutInflater.from(context).inflate(R.layout.widget_text, doSummary);
            TextView summary = (TextView) view.findViewById(R.id.module_text);
            summary.setText(topic.getSummary());
        }

        createTime.setText(SaveUtils.formatTime(topic.getCreateTime()));
        countView.setText(topic.getCountView());
        countComment.setText(topic.getCountComment());
        countPraise.setText(topic.getCountPrase());
        isPraise.setChecked(topic.isPraise());
        moreOptionClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {    //删除
                Snackbar.make(moreOptionClick, "删除帖子？", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams params = new RequestParams(HttpDatas.REMOVE_TOPIC);
                        params.addParameter("id", topic.getId());
                        HttpsUtils.getInstance().requestSavely( params, DataBack.class, MyTopicEntry.this);
                    }
                }).show();
            }
        });

        doComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class); //CommentActivity
                intent.putExtra("topicId", topic.getId());
                intent.putExtra("isLoadToContent", false);
                context.startActivity(intent);
            }
        });
        doPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPraise.isChecked()) {
                    //取消赞
                    RequestParams params = new RequestParams(HttpDatas.REMOVE_FAVOR);
//                        params.addParameter("accessToken", localInfo.getAccessToken());
                    params.addParameter("id", topic.getId());
                    HttpsUtils.getInstance().requestSavely(params, DataBack.class,MyTopicEntry.this);
                } else {
                    //点赞
                    RequestParams params = new RequestParams(HttpDatas.FAVOR_TOPIC);
                    params.addParameter("id", topic.getId());
                    HttpsUtils.getInstance().requestSavely(params, DataBack.class, MyTopicEntry.this);
                }
            }
        });
        article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/12/1 跳转到正文
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("topicId", topic.getId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public <T> void back(String type, T resultInfo) {
        DataBack data = (DataBack)resultInfo;
        if(data.getCode() == 0){
            if(type.equals(HttpDatas.REMOVE_TOPIC)){
                Snackbar.make(moreOptionClick, "删除成功", Snackbar.LENGTH_LONG).show();
                //刷新数据
                i_refresh.onRefreshComplete(topic.getId());
            }
            if(type.equals(HttpDatas.REMOVE_FAVOR)){
                isPraise.setChecked(false);
                int num = Integer.parseInt(countPraise.getText().toString()) - 1;
                countPraise.setText(String.valueOf(num));
            }
            if(type.equals(HttpDatas.FAVOR_TOPIC)){
                isPraise.setChecked(true);
                int num = Integer.parseInt(countPraise.getText().toString()) + 1;
                countPraise.setText(String.valueOf(num));
            }
        }

    }

    @Override
    public void fail(boolean isOnCallBack) {
        Snackbar.make(nikename,"网络错误",Snackbar.LENGTH_SHORT).show();
    }
}
