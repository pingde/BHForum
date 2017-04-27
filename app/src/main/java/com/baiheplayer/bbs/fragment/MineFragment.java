package com.baiheplayer.bbs.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.local.BGPicBack;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.LocalInfo;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.InfoHelper;
import com.baiheplayer.bbs.view.LoginActivity;
import com.baiheplayer.bbs.view.MyNewsActivity;
import com.baiheplayer.bbs.view.MyStoreActivity;
import com.baiheplayer.bbs.view.MyTopicActivity;
import com.baiheplayer.bbs.view.NoticeActivity;
import com.baiheplayer.bbs.view.PersonActivity;
import com.baiheplayer.bbs.view.PhotoActivity;
import com.baiheplayer.bbs.view.SettingActivity;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

/**
 * Created by Administrator on 2016/11/14.
 */
@ContentView(R.layout.fragment_mine)
public class MineFragment extends BaseFragment {
    @ViewInject(R.id.img_mine_background)   //背景
    private ImageView mBackGround;
    @ViewInject(R.id.cb_reminder_news)      //消息指示器
    private CheckBox mReminderNews;
    @ViewInject(R.id.cb_reminder_notice)    //消息指示器
    private CheckBox mReminderNotice;
    @ViewInject(R.id.img_mine_head)         //头像
    private ImageView mAvatar;
    @ViewInject(R.id.tv_mine_name)          //昵称
    private TextView mName;
    @ViewInject(R.id.tv_mine_signature)     //签名
    private TextView mSignature;
    private LocalInfo localInfo;
    private Context context;
    private DbManager db;
    private int CHANGE_BACKGROUND = 7;
    private int CHANGE_AVATAR = 8;

    @Override
    public void getData() {
        context = getContext();

        App app = (App) (getActivity().getApplication());
        localInfo = InfoHelper.getInstance().getInfo();

    }

    @Override
    public void onView() {
        if (localInfo != null) {
            refreshPrivateMessage();
        }

    }

    private void refreshPrivateMessage() {
        mName.setText(localInfo.getNickname() == null ? "昵称" : localInfo.getNickname());
        mSignature.setText(localInfo.getSignature() == null ? "来一句简介" : localInfo.getSignature());
        mReminderNews.setChecked(App.isHaveNews());
        mReminderNotice.setChecked(false);

        String avatar = localInfo.getAvatar() == null ? "" : localInfo.getAvatar();
        ImageOptions options = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(mAvatar, localInfo.getAvatar(), options);
        String background = localInfo.getBackground() == null ? "" : localInfo.getBackground();
        ImageOptions optionsForBackground = new ImageOptions.Builder().setUseMemCache(true).build();
        x.image().bind(mBackGround, localInfo.getBackground(), optionsForBackground);

    }


    /**
     * 点击导航
     *
     * @param view
     */            //R.id.ll_mine_book, R.id.ll_mine_bookAddress,
    @Event(value = {R.id.ll_mine_topic, R.id.ll_mine_store, R.id.ll_mine_news, R.id.img_mine_head,
            R.id.ll_mine_setting, R.id.img_mine_background, R.id.ll_mine_notice})
    private void mineOption(View view) {

        if (view.getId() == R.id.img_mine_head) {
            Intent intent = new Intent();

            if (localInfo.getUserId() != null && localInfo.isLogState()) {   //如果用户有过登录
                intent.setClass(context, PersonActivity.class);
            } else {
                intent.setClass(context, LoginActivity.class);
            }
            startActivityForResult(intent, CHANGE_AVATAR);
        }

//        if (localInfo.getUserId() == null) {
//            showInfo("用户未登录");
//            return;
//        }

        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.ll_mine_topic:
                intent.setClass(context, MyTopicActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_mine_store:
                intent.setClass(context, MyStoreActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_mine_news:
                mReminderNews.setChecked(false);
                intent.setClass(context, MyNewsActivity.class);
                startActivity(intent);
                break;
//                case R.id.ll_mine_book:
//                    intent.putExtra("name", "我的预定");
//                    Toast.makeText(context, "。。。", Toast.LENGTH_SHORT).show();
//                    break;
            case R.id.ll_mine_notice:
                mReminderNotice.setChecked(false);
                intent.putExtra("name", "我的通知");
                intent.setClass(context, NoticeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_mine_setting:
                intent.setClass(context, SettingActivity.class);
                startActivity(intent);
                break;
//                case R.id.ll_mine_bookAddress:
//                    Log.i("chen", "点击了管理地址");
//                    startActivity(new Intent(context, ManagerAddrActivity.class));
//                    break;
            case R.id.img_mine_background: //换背景
                String[] item = new String[]{"更换背景图片", "移除背景图片"};
                // TODO: 2017/4/14 此处使用EventBus重写
                new AlertDialog.Builder(context).setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentForBgPic = new Intent(context, PhotoActivity.class);
                                intentForBgPic.putExtra("title", "更换背景");
                                intentForBgPic.putExtra("content", localInfo.getBackground());
                                startActivityForResult(intentForBgPic, 7);
                                break;
                            case 1:
                                RequestParams params = new RequestParams(HttpDatas.BG_REMOVE);
                                HttpsUtils.getInstance().requestSavely(params, DataBack.class, new INetCallBack() {

                                    @Override
                                    public <T> void back(String type, T resultInfo) {
                                        DataBack data = (DataBack) resultInfo;
                                        localInfo.setBackground(null);
                                        x.image().bind(mBackGround, null);
                                        try {
                                            db.saveOrUpdate(localInfo);
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void fail(boolean isOnCallBack) {

                                    }
                                });
                                break;
                        }
                    }
                }).show();
                break;
        }

    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //更换背景图片
        if (requestCode == CHANGE_BACKGROUND && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            final String bgUri = bundle.getString("newContent");
            Log.i("chen", "相册获返回的是：" + bgUri);
            RequestParams params = new RequestParams(HttpDatas.BG_CHANGE);
            params.addParameter("image", new File(bgUri));
            HttpsUtils.getInstance().getObjectFromNet(params, BGPicBack.class, new ISimpleNetCallBack<BGPicBack>() {
                @Override
                public void back(BGPicBack resultInfo) {
                    if (resultInfo.getCode() == 0) {
                        String bg_pic_uri = resultInfo.getBackground();
                        Log.i("chen", "服务器返回的是：" + bg_pic_uri);
                        localInfo.setBackground(bg_pic_uri);
                        x.image().bind(mBackGround, bgUri);   //取本地的先用上
                        InfoHelper.getInstance().update(localInfo);
                    }
                }

                @Override
                public void fail(boolean isOnCallBack) {

                }
            });
        }
        //点击查看了头像，刷新一下LocalInfo
        if (requestCode == CHANGE_AVATAR && resultCode == Activity.RESULT_OK) {
            Log.i("chen", "执行刷新动作>>>>>>>>>>");
        }
    }

    private void showInfo(String info) {
        Snackbar.make(mAvatar, info, Snackbar.LENGTH_SHORT).show();
    }


}
