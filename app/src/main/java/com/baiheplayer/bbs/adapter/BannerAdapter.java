package com.baiheplayer.bbs.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baiheplayer.bbs.bean.Banner;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.view.DetailActivity;
import com.baiheplayer.bbs.view.NetJSActivity;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * 两个Fragment共同的Banner图
 * Created by Administrator on 2016/11/30.
 */

public class  BannerAdapter extends PagerAdapter implements INetCallBack {
    private Context context;
    private List<Banner> banners;
    private Banner choosedBanner;

    public BannerAdapter() {}

    public BannerAdapter(Context context, List list) {
        this.context = context;
        this.banners = list;

    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        ImageView imageView = new ImageView(context);

        ImageOptions options = new ImageOptions.Builder().setUseMemCache(true).build();
        x.image().bind(imageView, banners.get(position).getImage(),options);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosedBanner = banners.get(position);
                RequestParams params = new RequestParams(HttpDatas.BANNER_CLICK);
                params.addParameter("id",choosedBanner.getId());
                Log.i("chen","Banner的id:"+choosedBanner.getId());
                HttpsUtils.getInstance().requestSavely(params, DataBack.class,BannerAdapter.this);
            }
        });
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public <T> void back(String type, T resultInfo) {
        DataBack data = (DataBack) resultInfo;
        if(data.getCode() == 0){
            if (choosedBanner.getType()==1) {
                if(!TextUtils.isEmpty(choosedBanner.getUrl())){    //如果有url，则用界面显示出来
                    Intent intent = new Intent(context, NetJSActivity.class);
                    intent.putExtra("url",choosedBanner.getUrl());
                    context.startActivity(intent);
                }

            } else if (choosedBanner.getType()==2) {
                Log.i("chen", "跳转至帖子" + choosedBanner.getTopicId());
                if(!TextUtils.isEmpty(choosedBanner.getTopicId())){ //如果topicIdcunzai的话
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("topicId",choosedBanner.getTopicId());
                    context.startActivity(intent);
                }
            } else if (choosedBanner.getType()==2) {
                Log.i("chen", "跳转至商品,待定");
            }
        }
    }

    @Override
    public void fail(boolean isOnCallBack) {

    }
}
