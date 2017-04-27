package com.baiheplayer.bbs.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.CmtDetailBack;
import com.baiheplayer.bbs.bean.Comment;
import com.baiheplayer.bbs.bean.CommentRespond;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.LocalInfo;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */
@ContentView(R.layout.activity_commentdetail)
public class CommentDetailActivity extends BaseActivity {

    @ViewInject(R.id.img_avatar)
    private ImageView cmtAvatar;
    @ViewInject(R.id.tv_nike_name)
    private TextView cmtNickname;
    @ViewInject(R.id.tv_create_time)
    private TextView cmtCreateTime;
    @ViewInject(R.id.cb_is_praise)
    private CheckBox cmtIsPraise;
    @ViewInject(R.id.tv_count_praise)
    private TextView cmtCountPraise;
    @ViewInject(R.id.tv_comment_content)
    private TextView cmtContent;
    @ViewInject(R.id.ll_reply)
    private LinearLayout replys;
    @ViewInject(R.id.cb_click_delete)
    private ImageView cmtDelete;
    private Comment comment;
    private String commentId;
    private String topicId;
    private List<CommentRespond> responds;

    @Override
    public void onView(Bundle savedInstanceState) {
        Log.i("chen", "界面CommentActivity执行");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        commentId = bundle.getString("commentId");
        topicId = bundle.getString("topicId");
        comment = new Comment();
        responds = new ArrayList<>();
        refreshData();

    }

    private void refreshData() {
        RequestParams params = new RequestParams(HttpDatas.COMMENT_DETAIL);
        params.addParameter("topicId", topicId);
        params.addParameter("commentId", commentId);
        HttpsUtils.getInstance().getObjectFromNet(params, CmtDetailBack.class, new ISimpleNetCallBack<CmtDetailBack>() {
            @Override
            public void back(CmtDetailBack resultInfo) {
                resultInfo = (CmtDetailBack) resultInfo;
                if (resultInfo.getCode() == 0) {
                    responds.clear();
                    comment = resultInfo.getCommentDetail();
                    responds.addAll(resultInfo.getReply());
                    displayMsg();

                }
            }

            @Override
            public void fail(boolean isOnCallBack) {

            }
        });
    }

    private void displayMsg() {
        ImageOptions options = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(cmtAvatar, comment.getAvatar(), options);
        cmtNickname.setText(comment.getNickname());
        cmtCreateTime.setText(SaveUtils.formatTime(comment.getCreateTime()));
        cmtContent.setText(comment.getContent());
        cmtIsPraise.setChecked(comment.isPraise());
        cmtCountPraise.setText(comment.getCountPraise());
        if (comment.getUserId().equals(Constant.USER_ID)) {
            cmtDelete.setVisibility(View.VISIBLE);
        } else {
            cmtDelete.setVisibility(View.INVISIBLE);
        }
        //逐个添加item
        replys.removeAllViews();
        for (int i = 0; i < responds.size(); i++) {
            final CommentRespond respond = responds.get(i);
            View view = LayoutInflater.from(CommentDetailActivity.this).inflate(R.layout.item_forum_reply, null);
            ImageView rAvatar = (ImageView) view.findViewById(R.id.img_reply_avatar);
            TextView rNickname = (TextView) view.findViewById(R.id.tv_reply_nike_name);
            TextView rCreateTime = (TextView) view.findViewById(R.id.tv_reply_current_time);
            final TextView rCountPraise = (TextView) view.findViewById(R.id.tv_reply_count_praise);
            final CheckBox rIsPraise = (CheckBox) view.findViewById(R.id.cb_reply_is_praise);
            ImageView rDoComment = (ImageView) view.findViewById(R.id.cb_reply_click_response);
            LinearLayout rDoPraise = (LinearLayout) view.findViewById(R.id.ll_reply_click_praise);
            TextView rContent = (TextView) view.findViewById(R.id.tv_reply_content);
            ImageView rDoDelete = (ImageView) view.findViewById(R.id.cb_reply_click_delete);
            //设值
            x.image().bind(rAvatar, respond.getAvatar(), options);
            rNickname.setText(respond.getNickname());
            rCreateTime.setText(SaveUtils.formatTime(respond.getCreateTime()));
            rContent.setText(respond.getContent());
            rCountPraise.setText(respond.getCountPraise());
            rIsPraise.setChecked(respond.isPraise());
            if (respond.getUserId().equals(Constant.USER_ID)) {  //空值？
                rDoDelete.setVisibility(View.VISIBLE);
            }
            rDoPraise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rIsPraise.isChecked()) {
                        //取消赞
                        RequestParams params = new RequestParams(HttpDatas.COMMENT_UNPRAISE);
                        params.addParameter("topicId", topicId);
                        params.addParameter("commentId", commentId);
                        HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                            @Override
                            public void back(DataBack resultInfo) {

                                if (resultInfo.getCode() == 0) {
                                    rIsPraise.setChecked(false);
                                    int num = Integer.parseInt(rCountPraise.getText().toString()) - 1;
                                    rCountPraise.setText(String.valueOf(num));
                                }
                            }

                            @Override
                            public void fail(boolean isOnCallBack) {
                                showInfo("网络错误");
                            }
                        });
                    } else {
                        //点赞
                        RequestParams params = new RequestParams(HttpDatas.COMMENT_PRAISE);
                        params.addParameter("topicId", topicId);
                        params.addParameter("commentId", commentId);
                        HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {

                            @Override
                            public void back(DataBack resultInfo) {
                                if (resultInfo.getCode() == 0) {
                                    rIsPraise.setChecked(true);
                                    int num = Integer.parseInt(rCountPraise.getText().toString()) + 1;
                                    rCountPraise.setText(String.valueOf(num));
                                }
                            }


                            @Override
                            public void fail(boolean isOnCallBack) {
                                showInfo("网络错误");
                            }
                        });
                    }


                }
            });
            rDoComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                   //回复评论
//                    if (localInfo.getUserId() != null) {
//                        Intent intent = new Intent(CommentDetailActivity.this, CommentActivity.class);
//                        intent.putExtra("commentId", respond.getId());
//                        startActivityForResult(intent, 300);
//                    } else {
                    showInfo("没有小回复");
//                    }

                }
            });
            rDoDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                   //删除评论
                    RequestParams params = new RequestParams(HttpDatas.COMMENT_REMOVE);
                    params.addParameter("topicId", topicId);
                    params.addParameter("commentId", respond.getId());
                    HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                        @Override
                        public void back(DataBack resultInfo) {

                            if (resultInfo.getCode() == 0) {
                                replys.removeView(replys.findViewWithTag(respond.getId()));
                            }
                        }

                        @Override
                        public void fail(boolean isOnCallBack) {
                            showInfo("网络错误...");
                        }
                    });
                }
            });
            view.setTag(respond.getId());  //设值控件炳
            replys.addView(view);
        }

    }

    /**
     * 点赞取消赞
     *
     * @param view
     */
    @Event(R.id.ll_click_praise)
    private void addOrCancelPraise(View view) {
        if (isLogin()) {
            if (cmtIsPraise.isChecked()) {
                //取消赞
                RequestParams params = new RequestParams(HttpDatas.COMMENT_UNPRAISE);
                params.addParameter("topicId", topicId);
                params.addParameter("commentId", commentId);
                HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                    @Override
                    public void back(DataBack resultInfo) {
                        if (resultInfo.getCode() == 0) {
                            cmtIsPraise.setChecked(false);
                            int num = Integer.parseInt(cmtCountPraise.getText().toString()) - 1;
                            cmtCountPraise.setText(String.valueOf(num));
                        }
                    }

                    @Override
                    public void fail(boolean isOnCallBack) {
                        Snackbar.make(cmtAvatar, "网络错误...", Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
                //点赞
                RequestParams params = new RequestParams(HttpDatas.COMMENT_PRAISE);
                params.addParameter("topicId", topicId);
                params.addParameter("commentId", commentId);
                HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                    @Override
                    public void back(DataBack resultInfo) {
                        if (resultInfo.getCode() == 0) {
                            cmtIsPraise.setChecked(true);
                            int num = Integer.parseInt(cmtCountPraise.getText().toString()) + 1;
                            cmtCountPraise.setText(String.valueOf(num));
                        }
                    }

                    @Override
                    public void fail(boolean isOnCallBack) {
                        Snackbar.make(cmtAvatar, "网络错误...", Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        }

    }

    /**
     * 回复评论
     *
     * @param view
     */
    @Event(R.id.cb_click_response)
    private void replyComment(View view) {
        if (isLogin()) {
            Intent intent = new Intent(CommentDetailActivity.this, CommentActivity.class);
            intent.putExtra("commentId", commentId);
            startActivityForResult(intent, 300);
        }

    }

    /**
     * 回退
     *
     * @param view
     */
    @Event(value = {R.id.img_arrow, R.id.img_back})
    private void goback(View view) {
//        Intent intent = new Intent(this,DetailActivity.class);
//        intent.putExtra("topicId",topicId);
//        startActivity(intent);
//        overridePendingTransition(R.anim_post_up.anim_mov_left_in,R.anim_post_up.anim_mov_right_out);
        finish();
    }

    /**
     * 处理评论的编写返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String content = bundle.getString("cmtContent");
            String commentId = bundle.getString("commentId");
            if (content == null) {
                return;
            }
            RequestParams params = new RequestParams(HttpDatas.COMMENT_REPLY);
            params.addParameter("topicId", topicId);
            params.addParameter("commentId", commentId);  //id发给编辑页面，然后回传到这里
            params.addParameter("content", content);
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {

                    if (resultInfo.getCode() == 0) {
                        responds.clear();
                        refreshData();
                        showInfo("评论成功");
                    } else {
                        showInfo(resultInfo.getMsg());
                    }
                }

                @Override
                public void fail(boolean isOnCallBack) {

                }
            });
        }
    }

    public boolean isLogin() {
        boolean isLogin = false;
        if (Constant.USER_ID != null && Constant.USER_ID.length() > 0) {
            isLogin = true;
        } else {
            showInfo("尚未登录");
        }
        return isLogin;
    }


    private void showInfo(String str) {
        Snackbar.make(cmtAvatar, str, Snackbar.LENGTH_SHORT).setAction("action", null).show();
    }
}
