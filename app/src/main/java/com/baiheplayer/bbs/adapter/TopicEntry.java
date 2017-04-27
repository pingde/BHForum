package com.baiheplayer.bbs.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.IImageListener;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.view.DetailActivity;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/14.
 */

public class TopicEntry extends RecyclerView.ViewHolder {
    ImageView avatar;
    TextView nickname;
    TextView createTime;
    TextView title;
    TextView countView;
    TextView countPraise;
    TextView countComment;
    CheckBox isPraise;
    ImageView moreOptionClick;
    LinearLayout contentCard;
    LinearLayout pictures;
    LinearLayout summary;
    LinearLayout doComment;
    LinearLayout doPraise;
    LinearLayout doMoreOption; //显示或隐藏
    LinearLayout doStore;  //收藏
    LinearLayout doReport; //举报
    LinearLayout doMoreCmt;

    Context context;
    Topic topic;

    HashMap<String, Boolean> storeMem;

    public TopicEntry(View itemView) {
        super(itemView);
        avatar = (ImageView) itemView.findViewById(R.id.iv_forum_poster_head);
        nickname = (TextView) itemView.findViewById(R.id.tv_forum_author);   // 跳转个人
        createTime = (TextView) itemView.findViewById(R.id.tv_forum_time);
        title = (TextView) itemView.findViewById(R.id.tv_forum_title);
        summary = (LinearLayout) itemView.findViewById(R.id.ll_forum_summary);
        countView = (TextView) itemView.findViewById(R.id.tv_forum_seen);
        countComment = (TextView) itemView.findViewById(R.id.tv_forum_comments);
        countPraise = (TextView) itemView.findViewById(R.id.tv_forum_support);
        isPraise = (CheckBox) itemView.findViewById(R.id.iv_forum_support);
        moreOptionClick = (ImageView) itemView.findViewById(R.id.img_forum_more);
        contentCard = (LinearLayout) itemView.findViewById(R.id.ll_item_topic);
        doMoreOption = (LinearLayout) itemView.findViewById(R.id.ll_forum_more);
        pictures = (LinearLayout) itemView.findViewById(R.id.ll_forum_picture);
        doStore = (LinearLayout) itemView.findViewById(R.id.ll_forum_add_store);    //收藏
        doReport = (LinearLayout) itemView.findViewById(R.id.ll_forum_report);      //举报
        doComment = (LinearLayout) itemView.findViewById(R.id.ll_forum_comment);    //做评论
        doPraise = (LinearLayout) itemView.findViewById(R.id.ll_forum_praise);      //点个赞
        doMoreCmt = (LinearLayout) itemView.findViewById(R.id.ll_speed_comment);    //弹出添加评论才的小抽屉
    }


    public void onBind(final Context context, final Topic topic, final HashMap<String, Boolean> storeMem) {
        this.context = context;
        this.topic = topic;
        this.storeMem = storeMem;
        ImageOptions options = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(avatar, topic.getAvatar(), options);
        /**动态添加图片*/
        int picNum = 0;
        for (int i = 0; i < topic.getImages().size(); i++) {
            if (!TextUtils.isEmpty(topic.getImages().get(i))) {
                ++picNum;
            }
        }
        final int ll_width = Constant.screenWidth == 0 ? 1080 : (int) (Constant.screenWidth * 0.92);
        int width = 0;
        int height = 0;
        if (picNum == 1) {
            width = ll_width;
            height = (int) (ll_width * 0.5625);
        } else if (picNum == 2) {
            width = ll_width / 2;
            height = ll_width / 2;
        } else if (picNum == 3) {
            width = ll_width / 3;
            height = ll_width / 3;
        }
        ImageOptions io = new ImageOptions.Builder().setUseMemCache(true).build();
//        Log.i("chen", "指定的" + vh.pictures.getWidth() + " w:" + width + " h:" + height);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        pictures.removeAllViews();
        for (int i = 0; i < picNum; i++) {
            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            x.image().bind(imageView, topic.getImages().get(i), io);
            pictures.addView(imageView);
            if (i < picNum - 1) {                //添加空隙
                ImageView space = new ImageView(context);
                space.setLayoutParams(new LinearLayout.LayoutParams(10, height));
                pictures.addView(space);
            }
//            final int _i = i;
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onImageClick(v, topic.getImages(), _i);
//                }
//            });
        }
        nickname.setText(topic.getNickname());
        createTime.setText(SaveUtils.formatTime(topic.getCreateTime()));
        title.setText(topic.getTitle());
        if (!TextUtils.isEmpty(topic.getSummary())) {
            View view = View.inflate(context, R.layout.widget_text, null);
            TextView text = (TextView) view.findViewById(R.id.module_text);
            text.setText(topic.getSummary());
            summary.removeAllViews();
            summary.addView(view);
        }
        storeMem.put(topic.getId(), topic.isFavorite());  //将图片的收藏状况保存起来
        countView.setText(topic.getCountView());
        countComment.setText(topic.getCountComment());
        countPraise.setText(topic.getCountPrase());
        isPraise.setChecked(topic.isPraise());
        // TODO: 2016/11/30 添加点赞逻辑
        //展示举报收藏的PopupWinidow
        moreOptionClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_pop_share, null);
                PopupWindow popWin = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popWin.setFocusable(true);
                popWin.setTouchable(true);
                popWin.setOutsideTouchable(true);
                ColorDrawable dw = new ColorDrawable();
                popWin.setBackgroundDrawable(dw);
                popWin.showAsDropDown(v, -100, 20);
                LinearLayout store = (LinearLayout) view.findViewById(R.id.ll_forum_add_store);
                final LinearLayout report = (LinearLayout) view.findViewById(R.id.ll_forum_report);
                final CheckBox isStore = (CheckBox) view.findViewById(R.id.cb_item_store);

                isStore.setChecked(storeMem.get(topic.getId()));
                store.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 2016/11/30 收藏
                        if (Constant.USER_ID == null || Constant.USER_ID.equals("")) {
                            Toast.makeText(context, "用户未登录", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (isStore.isChecked()) {
                            /**取消收藏*/
                            final RequestParams params = new RequestParams(HttpDatas.REMOVE_STORE);
                            params.addParameter("id", topic.getId());
                            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                                @Override
                                public void back(DataBack resultInfo) {
                                    if (resultInfo.getCode() == 0) {
                                        Toast.makeText(context, "已取消收藏", Toast.LENGTH_SHORT).show();
                                        isStore.setChecked(false);
                                        storeMem.put(topic.getId(), false);
                                    }
                                }

                                @Override
                                public void fail(boolean isOnCallBack) {
                                    showError();
                                }
                            });
                        } else {
                            /**添加收藏*/
                            final RequestParams params = new RequestParams(HttpDatas.ADD_TOPIC);
                            params.addParameter("id", topic.getId());
                            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                                @Override
                                public void back(DataBack resultInfo) {
                                    if (resultInfo.getCode() == 0) {
                                        Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                                        isStore.setChecked(true);
                                        storeMem.put(topic.getId(), true);
                                    }
                                }

                                @Override
                                public void fail(boolean isOnCallBack) {
                                    showError();
                                }
                            });
                        }

                    }
                });
                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //举报
                        View choise = LayoutInflater.from(context).inflate(R.layout.item_pop_report, null);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(choise);
                        TextView report_title = (TextView) choise.findViewById(R.id.tv_report_title);
                        report_title.setText(topic.getTitle());
                        final CheckBox cb_lie = (CheckBox) choise.findViewById(R.id.cb_report_lie);
                        final CheckBox cb_junk = (CheckBox) choise.findViewById(R.id.cb_report_junk);
                        final CheckBox cb_blue = (CheckBox) choise.findViewById(R.id.cb_report_blue);
                        final CheckBox cb_attack = (CheckBox) choise.findViewById(R.id.cb_report_attack);
                        final CheckBox cb_reform = (CheckBox) choise.findViewById(R.id.cb_report_reform);
                        final CheckBox cb_copy = (CheckBox) choise.findViewById(R.id.cb_report_copy);
                        builder.setNegativeButton("取消", null);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                StringBuffer buffer = new StringBuffer();
                                if (cb_lie.isChecked()) buffer.append("虚假信息，");
                                if (cb_junk.isChecked()) buffer.append("垃圾营销，");
                                if (cb_blue.isChecked()) buffer.append("情色信息，");
                                if (cb_attack.isChecked()) buffer.append("人身攻击，");
                                if (cb_reform.isChecked()) buffer.append("反动言论，");
                                if (cb_copy.isChecked()) buffer.append("涉嫌抄袭，");
                                Log.i("chen", "buffer:" + buffer.toString());
                                if (buffer.length() == 0) {
                                    Toast.makeText(context, "漏选举报原因", Toast.LENGTH_SHORT).show();
                                } else {
                                    RequestParams params = new RequestParams(HttpDatas.REPORT_TOPIC);
                                    params.addParameter("id", topic.getId());
                                    params.addParameter("reason", "垃圾营销");//buffer.toString()
                                    HttpsUtils.getInstance().requestSavely(params, DataBack.class, new INetCallBack() {
                                        @Override
                                        public <T> void back(String type, T resultInfo) {
                                            if (((DataBack) resultInfo).getCode() == 0) {
                                                Toast.makeText(context, "已收到您的反馈", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void fail(boolean isOnCallBack) {
                                            showError();
                                        }
                                    });
                                }
                            }
                        }).show();

                    }
                });
            }
        });


        doComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转评论界面
                Intent intent = new Intent(context, DetailActivity.class); //CommentActivity
                intent.putExtra("topicId", topic.getId());
                intent.putExtra("isLoadToContent", false);
                context.startActivity(intent);
            }
        });
        doPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.USER_ID == null || Constant.USER_ID.equals("")) {
                    Toast.makeText(context, "登录之后更显精彩", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isPraise.isChecked()) {
                    /**取消赞*/
                    RequestParams params = new RequestParams(HttpDatas.REMOVE_FAVOR);
                    params.addParameter("id", topic.getId());
                    HttpsUtils.getInstance().requestSavely(params, DataBack.class, new INetCallBack() {
                        @Override
                        public <T> void back(String type, T resultInfo) {
                            if (((DataBack) resultInfo).getCode() == 0) {
                                isPraise.setChecked(false);
                                int num = Integer.parseInt(countPraise.getText().toString()) - 1;
                                countPraise.setText(String.valueOf(num));
                            }
                        }

                        @Override
                        public void fail(boolean isOnCallBack) {
                            showError();
                        }
                    });
                } else {
                    /**点赞*/
                    RequestParams params = new RequestParams(HttpDatas.FAVOR_TOPIC);
                    params.addParameter("id", topic.getId());
                    HttpsUtils.getInstance().requestSavely(params, DataBack.class, new INetCallBack() {
                        @Override
                        public <T> void back(String type, T resultInfo) {
                            if (((DataBack) resultInfo).getCode() == 0) {
                                isPraise.setChecked(true);
                                int num = Integer.parseInt(countPraise.getText().toString()) + 1;
                                countPraise.setText(String.valueOf(num));
                            }
                        }

                        @Override
                        public void fail(boolean isOnCallBack) {
                            showError();
                        }
                    });
                }

            }
        });
        contentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/12/1 跳转到正文
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("topicId", topic.getId());
                context.startActivity(intent);
            }
        });


    }

    private void showError() {
        Snackbar.make(nickname,"网络错误...",Snackbar.LENGTH_SHORT).show();
    }
}
