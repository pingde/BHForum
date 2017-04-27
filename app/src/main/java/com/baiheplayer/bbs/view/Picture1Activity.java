package com.baiheplayer.bbs.view;

import android.os.Bundle;
import android.util.Log;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.ui.ScaleView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2017/4/21.
 */
@ContentView(R.layout.activity_picture1)
public class Picture1Activity extends BaseActivity {

    @ViewInject(R.id.sv_image)
    private ScaleView mImage;


    @Override
    public void onView(Bundle savedInstanceState) {
        String imageUrl = getIntent().getStringExtra("image");
        ImageLoader.getInstance().displayImage(imageUrl,mImage);
        Log.i("chen","展示图片 "+imageUrl);
    }
}
