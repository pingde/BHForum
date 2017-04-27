package com.baiheplayer.bbs.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.image.ImageOptions;

import java.util.List;

/**
 * 查看大图的适配
 * Created by Administrator on 2017/1/6.
 */

public class PopPicAdapter extends PagerAdapter {
    private DefOnTouchListener listener;
    private Context context;
    private List<String> pictures;
    private int width;
    public PopPicAdapter(){
    }

    public PopPicAdapter(Context context,List pictures,int width){
        this.context = context;
        this.pictures = pictures;
        this.width = width;
        listener = new DefOnTouchListener();
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView temp_image = new ImageView(context);
//        ImageView temp_image = new DragPhotoView(context);
        ImageOptions options = new ImageOptions.Builder().setFadeIn(true).setUseMemCache(true).build();
        ImageLoader.getInstance().displayImage(pictures.get(position),temp_image);
//        x.image().bind(temp_image,pictures.get(position),options);
//        ImageLoader.getInstance().displayImage(pictures.get(position),temp_image);
        container.addView(temp_image);
        temp_image.setOnTouchListener(listener);
        return temp_image;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    private class DefOnTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }
}
