package com.baiheplayer.bbs.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.local.PostBack;
import com.baiheplayer.bbs.bean.local.UpLoadImage;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.inter.IUploadCallBack;
import com.baiheplayer.bbs.ui.MyRTManager;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Administrator on 2016/11/18.
 */
@ContentView(R.layout.activity_post)
public class PostActivity extends BaseActivity {

    @ViewInject(R.id.toolbar)
    private RelativeLayout mToolbar;
    @ViewInject(R.id.rll_post_activity)
    private LinearLayout mLinearLayout;
    @ViewInject(R.id.et_user_title)
    private EditText mTitle;
    @ViewInject(R.id.tv_post_time)
    private TextView mTime;
    @ViewInject(R.id.rtEditText)
    private RTEditText mContent;
    @ViewInject(R.id.img_user_post)
    private ImageView enSurePost;

    private TextView info;
    private static final int CLOSE_VIEW = 90;
    private static final int CLOSE_POP = 91;
    private static final int OPEN_POP = 92;
    private static final int NOT_SURE = 93;
    private MyRTManager mRTManager;
    private String batchId;
    private PopupWindow pop;
    private ArrayList<String> image_names;
    private HashMap<String, UpLoadImage> images;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLOSE_VIEW:
                    finish();
                    break;
                case CLOSE_POP:
                    pop.dismiss();
                    if (info.getText().equals("发布成功"))
                        sendEmptyMessage(90);
                    break;
                case OPEN_POP:
                    pop.showAsDropDown(mToolbar);
                    break;
                case NOT_SURE:
                    break;
            }
        }
    };


    @Override
    public void onView(Bundle bundle) {

        image_names = new ArrayList<>();    //找出来本地url的图片
        images = new HashMap<>();           //接收回传的图片地址
        mTime.setText(SaveUtils.getDate());
        RTApi rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        mRTManager = new MyRTManager(rtApi, null);
        mRTManager.addIUploadCallBackListener(this, new UploadCallBackImpl());  //添加回传监听
        ViewGroup toolbarContainer = (ViewGroup) findViewById(R.id.rte_toolbar_container);
        RTToolbar rtToolbar = (RTToolbar) findViewById(R.id.shr_toolbar);
        if (rtToolbar != null) {
            mRTManager.registerToolbar(toolbarContainer, rtToolbar);
        }
        mRTManager.registerEditor(mContent, true);
//        mContent.setRichTextEditing(true, "SH.baiheplayer");
        initLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    /**
     * 取消
     *
     * @param view
     */
    @Event(R.id.rl_user_cancel)
    private void cancelButton(View view) {
        finish();

    }

    /**
     * 发帖
     *
     * @param view
     */
    @Event(R.id.img_user_post)
    private void postToForum(View view) {
        String title = mTitle.getText().toString().trim();
        String content = mContent.getText(RTFormat.HTML);
        Log.i("chen", "原先的文章为" + content);
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Snackbar.make(mTime, "标题与内容不全", Snackbar.LENGTH_SHORT).show();
            return;
        }
        showLoading("发布中");
        String new_con = substitudeUrl(content);  //替换url
        Log.i("chen", "替换的结果为" + new_con);
        RequestParams params = new RequestParams(HttpDatas.POST_TOPIC);
        params.addParameter("title", title);
        params.addParameter("content", new_con);  //先不替换地址
        params.addParameter("batchId", batchId);
        HttpsUtils.getInstance().getObjectFromNet(params, PostBack.class, new ISimpleNetCallBack<PostBack>() {
            @Override
            public void back(PostBack resultInfo) {
                if (resultInfo.getCode() == 0) {
                    dismissLoading("发布成功");
                } else {
                    dismissLoading(resultInfo.getMsg());
                }
            }

            @Override
            public void fail(boolean isOnCallBack) {
                Snackbar.make(mContent,"网络错误",Snackbar.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRTManager != null) {
            mRTManager.onDestroy(true);
        }
    }
//*****************************private method***************************************

    /**
     * 回传的实现
     */
    private class UploadCallBackImpl implements IUploadCallBack {

        @Override
        public void addBackUrl(String b, String name, UpLoadImage image) {
            batchId = b;
            image_names.add(name);
            images.put(name, image);
            Log.i("chen", "回传的信息：" + b + " " + name + " " + image.getFileName() + " " + image.getFileUrl());
        }
    }

    /**
     * 替换url    pictures, images
     */
    private String substitudeUrl(String content) {
        StringBuffer strBuffer = new StringBuffer();
        String[] parts = content.split("\"");
        for (int i = 0; i < parts.length; i++) {
            String temp = parts[i];
            if (temp.contains(".png") || temp.contains(".jpg") || temp.contains(".JPG") || temp.contains(".JPEG") || temp.contains(".PNG")) {
                for (String image_name : image_names) {
                    if (temp.contains(image_name)) {
                        UpLoadImage image = images.get(image_name);
//                        Log.i("chen",image.getFileUrl());
                        temp = image.getFileUrl();//+ "\" style=\"width:100%;height:auto";
                    }
                }
            }
            strBuffer.append(temp);
            if (i < parts.length - 1) {
                strBuffer.append("\"");
            }
        }
        return strBuffer.toString();
    }

    private void initLoading() {
        View view = View.inflate(this, R.layout.item_pop_loading, null);
        pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb_loading);
        info = (TextView) view.findViewById(R.id.tv_now);
        pop.setContentView(view);
        pop.setFocusable(true);
        pop.setTouchable(false);
        pop.setOutsideTouchable(false);
        ColorDrawable dw = new ColorDrawable();//出问题就把1111111111 摆回去
        pop.setBackgroundDrawable(dw);
    }

    private void showLoading(String text) {
        info.setText(text);
        handler.sendEmptyMessage(OPEN_POP);
    }

    private void dismissLoading(String text) {
        info.setText(text);
        handler.sendEmptyMessageDelayed(CLOSE_POP, 1000);
    }

}
