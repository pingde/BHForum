package com.baiheplayer.bbs.adapter;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.Comment;
import com.baiheplayer.bbs.bean.CommentRespond;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by Administrator on 2017/4/14.
 */

public class CommentEntry extends ViewHolder implements INetCallBack {
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

    String topicId;

    Context context;
    public CommentEntry(View itemView) {
        super(itemView);
        avatar = (ImageView) itemView.findViewById(R.id.img_avatar);
        nikename = (TextView) itemView.findViewById(R.id.tv_nike_name);
        createTime = (TextView) itemView.findViewById(R.id.tv_create_time);
        content = (TextView) itemView.findViewById(R.id.tv_comment_content);
        countPraise = (TextView) itemView.findViewById(R.id.tv_count_praise);
        isPraise = (CheckBox) itemView.findViewById(R.id.cb_is_praise);
        deleteCmt = (ImageView) itemView.findViewById(R.id.cb_click_delete);
        responseCmt = (ImageView) itemView.findViewById(R.id.cb_click_response);
        replys = (LinearLayout) itemView.findViewById(R.id.ll_reply);
        doPraise = (LinearLayout) itemView.findViewById(R.id.ll_click_praise);

    }


    public void onBind(Context context,final String topicId, final Comment comment) {
        this.context = context;
        this.topicId = topicId;
        ImageOptions options = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(avatar, comment.getAvatar(), options);
        nikename.setText(comment.getNickname());        //昵称
        createTime.setText(comment.getCreateTime());    //时间   // TODO: 2016/12/1 格式转换
        countPraise.setText(comment.getCountPraise());  //赞数
        content.setText(comment.getContent());          //内容
        if (comment.getUserId().equals(Constant.USER_ID)) {
            deleteCmt.setVisibility(View.VISIBLE);
        }

        deleteCmt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/12/2 删除评论

            }
        });

        responseCmt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/12/2 回复评论
            }
        });
        isPraise.setChecked(comment.isPraise());
        doPraise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPraise.isChecked()) {
                    // 取消赞
                    RequestParams params = new RequestParams(HttpDatas.COMMENT_UNPRAISE);
                    params.addParameter("topicId", topicId);
                    params.addParameter("commentId", comment.getId());
                    HttpsUtils.getInstance().requestSavely(params, DataBack.class,CommentEntry.this);
                } else {
                    //点赞
                    RequestParams params = new RequestParams(HttpDatas.COMMENT_PRAISE);
                    params.addParameter("topicId", topicId);
                    params.addParameter("commentId", comment.getId());
                    HttpsUtils.getInstance().requestSavely(params, DataBack.class,CommentEntry.this);
                }
            }
        });
        List<CommentRespond> responds = comment.getList();
        if (responds != null) {
            for (int i = 0; i < responds.size(); i++) {
                final CommentRespond respond = responds.get(i);
                View view = LayoutInflater.from(context).inflate(R.layout.item_forum_reply, null);
                final ImageView reply_avatar = (ImageView) view.findViewById(R.id.img_reply_avatar);
                TextView reply_nickname = (TextView) view.findViewById(R.id.tv_reply_nike_name);
                TextView reply_createTime = (TextView) view.findViewById(R.id.tv_reply_current_time);
                TextView reply_content = (TextView) view.findViewById(R.id.tv_reply_content);
                final TextView reply_countPraise = (TextView) view.findViewById(R.id.tv_reply_count_praise);
                final CheckBox reply_isPraise = (CheckBox) view.findViewById(R.id.cb_reply_is_praise);
                LinearLayout reply_doPraise = (LinearLayout) view.findViewById(R.id.ll_reply_click_praise);
                x.image().bind(reply_avatar, respond.getAvatar(), options);
                reply_nickname.setText(respond.getNickname());
                reply_createTime.setText(respond.getCreateTime());
                reply_content.setText(respond.getContent());
                reply_countPraise.setText(respond.getCountPraise());
                reply_isPraise.setChecked(respond.isPraise());
                reply_doPraise.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (reply_isPraise.isChecked()) {
                            //取消赞
                            RequestParams params = new RequestParams(HttpDatas.COMMENT_UNPRAISE);
                            params.addParameter("topicId", topicId);
                            params.addParameter("commentId", respond.getId());
                            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {



                                @Override
                                public void back(DataBack resultInfo) {
                                    if(resultInfo.getCode() == 0){
                                        reply_isPraise.setChecked(false);
                                        int num = Integer.parseInt(reply_countPraise.getText().toString()) - 1;
                                        reply_countPraise.setText(String.valueOf(num));
                                    }

                                }

                                @Override
                                public void fail(boolean isOnCallBack) {

                                }

                            });
                        } else {
                            //点赞
                            RequestParams params = new RequestParams(HttpDatas.COMMENT_PRAISE);
                            params.addParameter("topicId", topicId);
                            params.addParameter("commentId", respond.getId());
                            HttpsUtils.getInstance().requestSavely(params, DataBack.class, new INetCallBack() {

                                @Override
                                public <T> void back(String type, T resultInfo) {
                                    if (((DataBack)resultInfo).getCode() == 0) {
                                        reply_isPraise.setChecked(true);
                                        int num = Integer.parseInt(reply_countPraise.getText().toString()) + 1;
                                        reply_countPraise.setText(String.valueOf(num));
                                    }
                                }

                                @Override
                                public void fail(boolean isOnCallBack) {
                                    Snackbar.make(nikename,"网络出现错误(⊙v⊙)。。。",Snackbar.LENGTH_SHORT).show();
                                }

                            });
                        }
                    }
                });
                // TODO: 2016/12/1 回复点赞
                replys.addView(view);
            }
        }
    }

    @Override
    public <T> void back(String type, T resultInfo) {
        DataBack data = (DataBack) resultInfo;
        if (data.getCode() == 0) {
            if (type.equals(HttpDatas.COMMENT_UNPRAISE)) {   //取消赞
                isPraise.setChecked(false);
                int num = Integer.parseInt(countPraise.getText().toString()) - 1;
                countPraise.setText(String.valueOf(num));
            }
            if (type.equals(HttpDatas.COMMENT_PRAISE)) {     //点赞
                isPraise.setChecked(true);
                int num = Integer.parseInt(countPraise.getText().toString()) + 1;
                countPraise.setText(String.valueOf(num));
            }
        }
    }

    @Override
    public void fail(boolean isOnCallBack) {
        Snackbar.make(nikename,"网络出现错误(⊙v⊙)。。。",Snackbar.LENGTH_SHORT).show();
    }
}
