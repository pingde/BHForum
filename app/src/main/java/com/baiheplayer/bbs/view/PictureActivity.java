package com.baiheplayer.bbs.view;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.adapter.PictureAdapter;
import com.baiheplayer.bbs.ui.ScaleView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/1/7.
 */
@ContentView(R.layout.activity_picture)
public class PictureActivity extends BaseActivity {

    private List<String> mList;
    //    private DragPhotoView[] mPhotoViews;
    private ScaleView[] mPhotoViews;

    private ArrayList<String> mDatas;
    private String mUrl;    //当前图片的网络路径
    private int mPosition;

    private PictureAdapter adapter;

    @ViewInject(R.id.viewpager)
    private ViewPager mViewPager;
    @ViewInject(R.id.tv_title)
    private TextView mTitle;
    @ViewInject(R.id.tv_save_pic)
    private TextView mSave;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onView(Bundle b) {
        mDatas = (ArrayList<String>) getIntent().getSerializableExtra("images");
        mPosition = getIntent().getIntExtra("position", 0);
        adapter = new PictureAdapter(this,mDatas);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mPosition);
        mViewPager.setPageMargin(32);
        mTitle.setText((mPosition+1)+"/"+mDatas.size());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTitle.setText((position+1)+"/"+mDatas.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 回退
     */
    @Event(R.id.img_back)
    private void goBack(View view){
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

    /**
     * 保存图片到SDCard的Picture里面
     * @param view
     */
    @Event(R.id.tv_save_pic)
    private void requestSavePicture(View view){
        mUrl = mDatas.get(mViewPager.getCurrentItem());
         new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("系统提示")
                .setMessage("是否保存图片")
                .setNegativeButton("不了",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savePicture2SDCard(mUrl);
                    }
                }).show();
    }

    private void savePicture2SDCard(String mUrl){
        String picName = mUrl.substring(mUrl.lastIndexOf("/")+1);   //,mUrl.lastIndexOf(".")
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),picName);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(mUrl);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            boolean isFinish = bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();

            String message = isFinish? "图片"+picName+"已放入SDCard":"/(ㄒoㄒ)/~~保存失败";
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos!=null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(file!=null){
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                sendBroadcast(intent);
            }
            if(bitmap.isRecycled()){
                bitmap.recycle();    //回收
            }
        }



    }

}







