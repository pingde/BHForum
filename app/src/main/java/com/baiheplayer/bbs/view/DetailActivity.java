package com.baiheplayer.bbs.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.Comment;
import com.baiheplayer.bbs.bean.CommentRespond;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.bean.TopicComtBack;
import com.baiheplayer.bbs.bean.TopicDetailBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * 帖子详情以及评论
 * Created by Administrator on 2016/12/1.
 */
@ContentView(R.layout.activity_detail)
public class DetailActivity extends BaseActivity {
    @ViewInject(R.id.pull_to_refresh)
    private PullToRefreshScrollView mPullToRefresh;
    @ViewInject(R.id.rv_comment_list)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.wv_forum_content)
    private WebView mContent;
    @ViewInject(R.id.cb_detail_is_Praise)
    private CheckBox topicIsPraise;
    @ViewInject(R.id.cb_detail_is_store)
    private CheckBox topicIsFavort;
    @ViewInject(R.id.ll_empty_notice)
    private LinearLayout emptyNotice;
    private static int TYPE_HAVE_COMMENT = 1;
    private static int TYPE_NONE_COMMENT = 0;
    private int currentPage = 1;
    private int totalPage = 1;
    private DbManager db;
    private Topic topic;     //详细的帖子内容
    private String topicId;  //帖子id
    private List<Comment> comments; //评论的数据源
    private CmtAdapter adapter;
    private int TYPE_RESP_COMMENT = 100;
    private int TYPE_ADD_COMMENT = 200;
    private boolean isLoadToContent = true;
    private ShareAction mShareAction;
    private UMShareListener mShareListener;
    @Override
    public void onView(Bundle b) {
        Log.i("chen", "界面DetailActivity执行");

        topicId = getIntent().getStringExtra("topicId");
        isLoadToContent = getIntent().getBooleanExtra("isLoadToContent", true);
        topic = null;
        comments = new ArrayList<>();
        if (topicId != null) {
            sendHttpToGetDetail();
            refreshComment(currentPage);
        }
        adapter = new CmtAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
        mPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefresh.setShowViewWhileRefreshing(true);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                comments.clear();
                currentPage = 1;
                refreshComment(currentPage);
                loadWebContent();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                refreshComment(currentPage + 1);
            }
        });
        //展示内容的webView
        WebSettings settings = mContent.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);  //图片大小适配
        mShareListener = new CustomShareListener(this);
        mShareAction = new ShareAction(DetailActivity.this).withText("hello")
                .setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,
                        SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA, SHARE_MEDIA.ALIPAY)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (snsPlatform.mShowWord.equals("umeng_sharebutton_copy")) {
                            Toast.makeText(DetailActivity.this, "复制文本按钮", Toast.LENGTH_LONG).show();
                        } else if (snsPlatform.mShowWord.equals("umeng_sharebutton_copyurl")) {
                            Toast.makeText(DetailActivity.this, "复制链接按钮", Toast.LENGTH_LONG).show();

                        } else {
                            UMWeb web = new UMWeb("https://www.baidu.com");
                            web.setTitle("来自分享面板标题");
                            web.setDescription("来自分享面板内容");
                            web.setThumb(new UMImage(DetailActivity.this, R.mipmap.ic_launcher));
                            new ShareAction(DetailActivity.this).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadWebContent() {

        if (isLoadToContent) {
            mContent.clearCache(true);
            if(topic == null ||TextUtils.isEmpty(topic.getContent())){
                emptyNotice.removeAllViews();
                mContent.loadUrl("file:///android_asset/rule/404.html");
            } else {
                mContent.loadDataWithBaseURL("file:///android_asset/", topic.getContent(), "text/html", "utf-8", "");
            }
        } else {
            isLoadToContent = true;
            mRecyclerView.scrollBy(0,mContent.getContentHeight());
        }

    }

    private void sendHttpToGetDetail() {
        RequestParams params = new RequestParams(HttpDatas.TOPIC_DETIAL);
        params.addParameter("id", topicId);
        params.setCacheSize(1024 * 1024 * 10); //10M
        params.setCacheDirName(topicId);
        params.setCacheMaxAge(Constant.expires);
//        Toast.makeText(this,"缓存地址："+params.getCacheDirName(),Toast.LENGTH_SHORT).show();
        HttpsUtils.getInstance().requestSavely(params, TopicDetailBack.class, new INetCallBack() {
            @Override
            public <T> void back(String type,T resultInfo) {
                TopicDetailBack data = (TopicDetailBack)resultInfo;
                if (data.getCode() == 0) {   //获得帖子文章的内容
                    topic = data.getData();
                    Log.i("chen", topic.getContent());
                    topicIsPraise.setChecked(topic.isPraise());
                    topicIsFavort.setChecked(topic.isFavorite());
                    loadWebContent();
                }
            }
            @Override
            public void fail(boolean isOnCallBack) {

            }
        });
    }

    private void refreshComment(int page) {
        if (page <= totalPage && page > 0) {
            RequestParams params = new RequestParams(HttpDatas.COMMENT_LIST);
            params.addParameter("topicId", topicId);
            params.addParameter("pcurrent", page);//可选页码
            params.addParameter("psize", 10);//可选数据量
            HttpsUtils.getInstance().requestSavely(params, TopicComtBack.class, new INetCallBack() {
                @Override
                public <T> void back(String type,T resultInfo) {
                    TopicComtBack data = (TopicComtBack)resultInfo;
                    if (data.getCode() == 0) {
                        totalPage = Integer.parseInt(data.getTotal());         //总页数
                        currentPage = Integer.parseInt(data.getCurrent());    //当前加载页号
                        comments.addAll(data.getList());
                        adapter.notifyDataSetChanged();
                        whenEmptyShow();
                    }
                    mPullToRefresh.onRefreshComplete();
                }

                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误");
                }
            });
        } else {
            //挂牌,已加载全部
            mPullToRefresh.setReleaseLabel("已全部加载", PullToRefreshBase.Mode.PULL_FROM_END);
            mPullToRefresh.onRefreshComplete();
        }

    }

    private void whenEmptyShow() {
        emptyNotice.removeAllViews();
        if (comments.isEmpty()) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_empty_view, null);
            TextView msg = (TextView)view.findViewById(R.id.tv_empty_notice);
            msg.setText("还没有评论，抢沙发");
            emptyNotice.addView(view);
            return;
        }

    }

    /**
     * 回退
     *
     * @param view
     */
    @Event(value = {R.id.img_arrow, R.id.img_back})
    private void goBack(View view) {
        finish();
    }

    /**
     * 分享
     * @param view
     */
    @Event(R.id.rl_add_share)
    private void socialShare(View view){
        mShareAction.open();
    }



    /**
     * 添加收藏
     *
     * @param view
     */
    @Event(R.id.iv_add_store)
    private void addStore(View view) {
        if (TextUtils.isEmpty(Constant.USER_ID)) {
            Toast.makeText(this, "登录之后更显精彩", Toast.LENGTH_SHORT).show();
            return;
        }
        if (topicIsFavort.isChecked()) {
            //取消收藏
            RequestParams params = new RequestParams(HttpDatas.REMOVE_STORE);
            params.addParameter("id", topicId);
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {
                    if (resultInfo.getCode() == 0) {
                        topicIsFavort.setChecked(false);
                    }
                }
                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误");
                }
            });
        } else {
            //添加收藏
            RequestParams params = new RequestParams(HttpDatas.ADD_TOPIC);
            params.addParameter("id", topicId);
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {
                    if (resultInfo.getCode() == 0) {
                        topicIsFavort.setChecked(true);
                    }
                }
                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误");
                }
            });
        }

    }

    /**
     * 写评论
     *
     * @param view
     */
    @Event(R.id.rl_add_comments)
    private void addComment(View view) {
        if (isLogin()) {
            Intent intent = new Intent(DetailActivity.this, CommentActivity.class);
            intent.putExtra("commentId", topicId);
            startActivityForResult(intent, TYPE_ADD_COMMENT);
        }

    }

    /**
     * 赞帖子
     *
     * @param view
     */
    @Event(R.id.rl_add_praise)
    private void addpraise(View view) {
        if (topicIsPraise.isChecked()) {
            //取消赞
            RequestParams params = new RequestParams(HttpDatas.REMOVE_FAVOR);
            params.addParameter("id", topicId);
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {
                    if (resultInfo.getCode() == 0) {
                        topicIsPraise.setChecked(false);
                    }
                }

                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误");
                }
            });
        } else {
            //点赞
            RequestParams params = new RequestParams(HttpDatas.FAVOR_TOPIC);
            params.addParameter("id", topicId);
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {
                    if (resultInfo.getCode() == 0) {
                        topicIsPraise.setChecked(true);
                    }
                }

                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误...");
                }
            });
        }
    }

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<DetailActivity> mActivity;

        private CustomShareListener(DetailActivity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST

                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
                if (t != null) {
                    com.umeng.socialize.utils.Log.d("throw", "throw:" + t.getMessage());
                }
            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {

            Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 屏幕横竖屏切换时避免出现window leak的问题
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mShareAction.close();
    }



    /**
     * 处理返回结果
     */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == TYPE_RESP_COMMENT && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String content = bundle.getString("cmtContent");
            String commentId = bundle.getString("commentId");
            if (content == null) {
                return;
            }
            RequestParams params = new RequestParams(HttpDatas.COMMENT_REPLY);
            params.addParameter("topicId", topicId);
            params.addParameter("commentId", commentId);  //怎么把评论id传过来
            params.addParameter("content", content);
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {
                    // 这里返回有一个
                    if (resultInfo.getCode() == 0) {
                        comments.clear();
                        currentPage = 1;
                        refreshComment(currentPage);
                        showInfo("评论成功");
                    }
                }
                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误...");
                }
            });
        }
        if (requestCode == TYPE_ADD_COMMENT && resultCode == Activity.RESULT_OK) {
            //添加评论
            Bundle bundle = data.getExtras();
            String content = bundle.getString("cmtContent");
            String commentId = bundle.getString("commentId");
            if (content == null) {
                return;
            }
            RequestParams params = new RequestParams(HttpDatas.COMMENT_ADD);
            params.addParameter("topicId", topicId);
            params.addParameter("content", content);
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {
                    comments.clear();
                    currentPage = 1;
                    refreshComment(currentPage);
                    showInfo("评论成功");
                    mRecyclerView.scrollBy(0, mContent.getContentHeight());
                }

                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误...");
                }
            });

        }
    }

//    private void showInfo(String info){
//        Snackbar.make(mRecyclerView,info,Snackbar.LENGTH_SHORT).show();
//    }

    /**
     * 评论的适配器
     */
    private class CmtAdapter extends RecyclerView.Adapter {

        @Override
        public int getItemCount() {
            return comments.size();
        }

        @Override
        public int getItemViewType(int position) {

            return TYPE_HAVE_COMMENT;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_forum_comment, null);
            return new CommentEntry(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final Comment comment = comments.get(position);
            final CommentEntry entry = (CommentEntry) holder;
            ImageOptions options = new ImageOptions.Builder().setCircular(true).build();
//            ImageLoader.getInstance().displayImage(comment.getAvatar(),entry.avatar);
            x.image().bind(entry.avatar, comment.getAvatar(), options);
            entry.nikename.setText(comment.getNickname());
            entry.createTime.setText(SaveUtils.formatTime(comment.getCreateTime()));
            entry.countPraise.setText(comment.getCountPraise());
            entry.content.setText(comment.getContent());
            if (comment.getUserId().equals(Constant.USER_ID)) {
                entry.deleteCmt.setVisibility(View.VISIBLE);
            }
            entry.deleteCmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestParams params = new RequestParams(HttpDatas.COMMENT_REMOVE);
                    params.addParameter("topicId", topicId);
                    params.addParameter("commentId", comment.getId());
                    HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                        @Override
                        public void back(DataBack resultInfo) {
                            if (resultInfo.getCode() == 0) {
                                comments.clear();
                                refreshComment(currentPage);
                                showInfo("评论删除成功");
                            }
                        }

                        @Override
                        public void fail(boolean isOnCallBack) {
                            showInfo("网络错误...");
                        }
                    });
                }
            });
            entry.responseCmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLogin()) {    //回复评论
                        Intent intent = new Intent(DetailActivity.this, CommentActivity.class);
                        intent.putExtra("commentId", comment.getId());
                        startActivityForResult(intent, TYPE_RESP_COMMENT);
                    }
                }
            });
            entry.isPraise.setChecked(comment.isPraise());
            entry.doPraise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (entry.isPraise.isChecked()) {
                        // 取消赞
                        RequestParams params = new RequestParams(HttpDatas.COMMENT_UNPRAISE);
                        params.addParameter("topicId", topicId);
                        params.addParameter("commentId", comment.getId());
                        HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                            @Override
                            public void back(DataBack resultInfo) {
                                if (resultInfo.getCode() == 0) {
                                    entry.isPraise.setChecked(false);
                                    int num = Integer.parseInt(entry.countPraise.getText().toString()) - 1;
                                    entry.countPraise.setText(String.valueOf(num));
                                } else {
                                    showInfo(resultInfo.getMsg());
                                }
                            }

                            @Override
                            public void fail(boolean isOnCallBack) {
                                showInfo("网络错误...");
                            }
                        });
                    } else {
                        //点赞
                        RequestParams params = new RequestParams(HttpDatas.COMMENT_PRAISE);
                        params.addParameter("topicId", topicId);
                        params.addParameter("commentId", comment.getId());
                        HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                            @Override
                            public void back(DataBack resultInfo) {
                                if (resultInfo.getCode() == 0) {
                                    entry.isPraise.setChecked(true);
                                    int num = Integer.parseInt(entry.countPraise.getText().toString()) + 1;
                                    entry.countPraise.setText(String.valueOf(num));
                                } else {
                                    showInfo(resultInfo.getMsg());
                                }
                            }
                            @Override
                            public void fail(boolean isOnCallBack) {
                                showInfo("网络错误...");
                            }
                        });
                    }
                }
            });
            entry.replys.removeAllViews();
            List<CommentRespond> responds = comment.getList();
            if (responds != null) {
                for (int i = 0; i < responds.size(); i++) {
                    final CommentRespond respond = responds.get(i);
                    View view = LayoutInflater.from(DetailActivity.this).inflate(R.layout.item_forum_reply, null);
                    final ImageView reply_avatar = (ImageView) view.findViewById(R.id.img_reply_avatar);
                    TextView reply_nickname = (TextView) view.findViewById(R.id.tv_reply_nike_name);
                    TextView reply_createTime = (TextView) view.findViewById(R.id.tv_reply_current_time);
                    TextView reply_content = (TextView) view.findViewById(R.id.tv_reply_content);
                    ImageView reply_deleteCmt = (ImageView) view.findViewById(R.id.cb_reply_click_delete);
                    ImageView reply_response = (ImageView) view.findViewById(R.id.cb_reply_click_response);
                    reply_deleteCmt.setVisibility(View.INVISIBLE);
                    if (respond.getUserId().equals(Constant.USER_ID)) {
                        reply_deleteCmt.setVisibility(View.VISIBLE);
                    }
                    reply_deleteCmt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {              //删除评论
                            RequestParams params = new RequestParams(HttpDatas.COMMENT_REMOVE);
                            params.addParameter("topicId", topicId);
                            params.addParameter("commentId", respond.getId());
                            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                                @Override
                                public void back(DataBack resultInfo) {
                                    if (resultInfo.getCode() == 0) {
                                        comments.clear();
                                        refreshComment(currentPage);
                                    }
                                    showInfo(resultInfo.getCode() == 0 ? "删除成功" : resultInfo.getMsg());
                                }
                                @Override
                                public void fail(boolean isOnCallBack) {
                                    showInfo("网络错误...");
                                }
                            });
                        }
                    });
                    reply_response.setOnClickListener(new View.OnClickListener() {   //回复评论
                        @Override
                        public void onClick(View v) {
                            if (isLogin()) {
                                Intent intent = new Intent(DetailActivity.this, CommentActivity.class);
                                intent.putExtra("commentId", respond.getId());
                                startActivityForResult(intent, TYPE_RESP_COMMENT);
                            }

                        }
                    });
                    final TextView reply_countPraise = (TextView) view.findViewById(R.id.tv_reply_count_praise);
                    final CheckBox reply_isPraise = (CheckBox) view.findViewById(R.id.cb_reply_is_praise);
                    LinearLayout reply_doPraise = (LinearLayout) view.findViewById(R.id.ll_reply_click_praise);
                    x.image().bind(reply_avatar, respond.getAvatar(), options);
                    reply_nickname.setText(respond.getNickname());
                    reply_createTime.setText(SaveUtils.formatTime(respond.getCreateTime()));
                    reply_content.setText(respond.getContent());
                    reply_countPraise.setText(respond.getCountPraise());
                    reply_isPraise.setChecked(respond.isPraise());
                    reply_doPraise.setOnClickListener(new View.OnClickListener() {
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
                                        if (resultInfo.getCode() == 0) {
                                            reply_isPraise.setChecked(false);
                                            int num = Integer.parseInt(reply_countPraise.getText().toString()) - 1;
                                            reply_countPraise.setText(String.valueOf(num));
                                        } else {
                                            showInfo(resultInfo.getMsg());
                                        }
                                    }
                                    @Override
                                    public void fail(boolean isOnCallBack) {
                                       showInfo("网络错误...");
                                    }
                                });
                            } else {
                                //点赞
                                RequestParams params = new RequestParams(HttpDatas.COMMENT_PRAISE);
                                params.addParameter("topicId", topicId);
                                params.addParameter("commentId", respond.getId());
                                HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                                    @Override
                                    public void back(DataBack resultInfo) {
                                        if (resultInfo.getCode() == 0) {
                                            reply_isPraise.setChecked(true);
                                            int num = Integer.parseInt(reply_countPraise.getText().toString()) + 1;
                                            reply_countPraise.setText(String.valueOf(num));
                                        } else {
                                            showInfo(resultInfo.getMsg());
                                        }
                                    }
                                    @Override
                                    public void fail(boolean isOnCallBack) {
                                        showInfo("网络错误...");
                                    }
                                });
                            }
                        }
                    });
                    entry.replys.addView(view);
                }
                if (responds.size() > 2) {
                    View v = LayoutInflater.from(DetailActivity.this).inflate(R.layout.item_forum_cmt_more, null);
                    TextView clickMore = (TextView) v.findViewById(R.id.tv_click_reply_more);
//                    String temp = "更多"+comment.get+"条精彩评论";
//                    clickMore.setText(temp);
                    clickMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailActivity.this, CommentDetailActivity.class);
                            intent.putExtra("commentId", comment.getId());
                            intent.putExtra("topicId", topicId);
                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_mov_right_in, R.anim.anim_mov_left_out);
                        }
                    });
                    entry.replys.addView(v);
                }

            }
        }
    }

    private class CommentEntry extends RecyclerView.ViewHolder {
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
            responseCmt = (ImageView) itemView.findViewById(R.id.cb_click_response);
            replys = (LinearLayout) itemView.findViewById(R.id.ll_reply);
            doPraise = (LinearLayout) itemView.findViewById(R.id.ll_click_praise);
        }
    }

    private boolean isLogin(){
        boolean isLogin = false;
        if(Constant.USER_ID !=null && Constant.USER_ID.length()>0){
            isLogin = true;
        } else {
            showInfo("尚未登录");
        }
        return isLogin;
    }


    private void showInfo(String info) {
        Snackbar.make(mRecyclerView, info, Snackbar.LENGTH_SHORT).show();
    }

    private String temp_comtent2 = "今日，王菲“幻乐一场2016”演唱会在上海举行，相比12月5日门票起售仅用了30秒的时间就显示已全部售罄，门票最终被炒到几十万元。而之后泡沫幻灭，价格犹如过山车一样打折出售。不过，VR陀螺更关注此次王菲演唱会VR直播情况如何<br/>\n" +
            "                                                           今日，王菲“幻乐一场2016”演唱会在上海举行，相比12月5日门票起售仅用了30秒的时间就显示已全部售罄，门票最终被炒到几十万元。而之后泡沫幻灭，价格犹如过山车一样打折出售。不过，VR陀螺更关注此次王菲演唱会VR直播情况如何<br/>\n" +
            "                                                           <br/>\n" +
            "                                                           据悉天后全球首场VR直播演唱会仅VR直播加特效技术支持耗资高达3千万，数字王国VR团队提供直播及特效，微鲸与腾讯提供平台支持。<br/>\n" +
            "                                                           <br/>\n" +
//            "                                                           <img src=\"http://www.baidu.com/img/bd_logo1.png\">\n"+
            "                                                           目前在微鲸PC端网页无法购买VR直播门票，腾讯视频提供了两种直播观看方式，普通版直播免费，VR版直播售价腾讯视频普通用户30元、会员25元。截止12月29日腾讯视频共有90多万观众进行了预约，VR观众数据未知，VR陀螺本着求真的态度第一时间观看了王菲VR演唱会直播。<br/>\n" +
            "                                                           <br/>\n" +
            "                                                           目前在微鲸PC端网页无法购买VR直播门票，腾讯视频提供了两种直播观看方式，普通版直播免费，VR版直播售价腾讯视频普通用户30元、会员25元。截止12月29日腾讯视频共有90多万观众进行了预约，VR观众数据未知，VR陀螺本着求真的态度第一时间观看了王菲VR演唱会直播。<br/>\n" +
            "                                                           <br/>\n" +
            "                                                           3000万的成本换来的是这样的效果？！<br/>\n" +
            "                                                           <br/>\n" +
            "                                                           <img src=\"http://www.baidu.com/img/bd_logo1.png\" style=\"width:100%;height:auto\">" +
            "                                                           原本以为7点半开始的直播一直拖到8点半，纪录片中一直在播放此次演唱会的花絮，VR陀螺感受到了这场演唱会的“大制作”，因此抱着非常期待的心情等待着。<br/>\n" +
            "                                                           <br/>\n" +
            "                                                           据介绍，此次负责VR直播及特效的王菲男友谢霆锋任大中华区主席的数字王国团队。数字王国是全球最大的独立视觉特效公司数字王国（Digital Domain），在里约奥运会上完成了史上第一次使用VR技术转播赛事。<br/>\n" +
            "                                                           <br/>\n" +
            "                                                           在王菲演唱会VR拍摄上则启用自研的超高清360度VR摄像机Kronos及Zeus担纲全景拍摄。Kronos能够进行4K全景视频的实时缝合，同时还配备4K全系统拍摄流程方案，据称可以将10多兆的画面迅速降解为3、4兆，而且整合陀螺仪和惯性测量加速度计来提供稳定的拍摄与实时串流，其内置的麦克风支持3D立体声音效，这些因素结合起来极大提升了VR直播的质量。<br/>\n" +
            "                                                            <br/>\n" +
            "                                                          （更多精彩资讯，<a href=\"https://www.baidu.com\">点击下载华尔街见闻App</a>)<br/>\n" +
            "                                                           写一个链接<a href=\"http://www.baidu.com\">百度一下</a>" +
            "                                                           <br/>\n" +
            "                                                           <br/>";

}
