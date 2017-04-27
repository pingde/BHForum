package com.baiheplayer.bbs.ui;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.R;

/**
 * 适配器所需要的评论的类
 * Created by Administrator on 2016/12/2.
 */

public class CommentEntry extends RecyclerView.ViewHolder {

    ImageView avatar;
    TextView nikename;
    TextView createTime;
    TextView content;
    TextView countPraise;
    LinearLayout replys;
    CheckBox isPraise;
    ImageView deleteCmt;        //删除评论
    ImageView responseCmt;      //回复评论
    LinearLayout doPraise;   //点赞
    public CommentEntry(View itemView) {
        super(itemView);
        avatar = (ImageView) itemView.findViewById(R.id.img_avatar);
        nikename = (TextView) itemView.findViewById(R.id.tv_nike_name);
        createTime = (TextView) itemView.findViewById(R.id.tv_create_time);
        content = (TextView) itemView.findViewById(R.id.tv_comment_content);
        countPraise = (TextView) itemView.findViewById(R.id.tv_count_praise);
        isPraise = (CheckBox) itemView.findViewById(R.id.cb_is_praise);
        deleteCmt = (ImageView) itemView.findViewById(R.id.cb_click_delete);
        responseCmt = (ImageView)itemView.findViewById(R.id.cb_click_response);
        replys = (LinearLayout) itemView.findViewById(R.id.ll_reply);
        doPraise = (LinearLayout) itemView.findViewById(R.id.ll_click_praise);
    }
}
