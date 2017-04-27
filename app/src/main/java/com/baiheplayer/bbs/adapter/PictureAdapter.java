package com.baiheplayer.bbs.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baiheplayer.bbs.ui.ScaleView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 用于适配 点击查看大图
 * location: PictureActivity
 * Created by Administrator on 2017/1/11.
 */

public class PictureAdapter extends PagerAdapter {
    private Context context;
    private List<String> list;

    public PictureAdapter() {
    }

    public PictureAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ScaleView image = new ScaleView(context);
        ImageLoader.getInstance().displayImage(list.get(position),image);
        container.addView(image);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View)object);
    }
}
