package com.baiheplayer.bbs.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;

import com.baiheplayer.bbs.inter.IImageListener;
import com.baiheplayer.bbs.view.PictureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/11.
 */

public class ImageClickListenerImpl implements IImageListener {
    private Activity activity;
    public ImageClickListenerImpl(){}

    public ImageClickListenerImpl(Activity activity){
        this.activity = activity;
    }
    @Override
    public void onImageClick(View view, List<String> urls, int position) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int height = view.getHeight();
        int width = view.getWidth();
        Log.i("chen", "点击在屏幕上的位置" + location[0] + " " + location[1] + " " + width + " " + height);
        Intent intent = new Intent(activity, PictureActivity.class);
        intent.putExtra("images", (ArrayList) urls);
        intent.putExtra("position", position);
        if (Build.VERSION.SDK_INT < 21) {
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        } else {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(view, location[0] + width / 2, location[1] + height / 2, width, height);
            ActivityCompat.startActivity(activity, intent, options.toBundle());
        }
    }
}
