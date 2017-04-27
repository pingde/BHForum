package com.baiheplayer.bbs.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.utils.SaveUtils;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2016/11/16.
 */
@ContentView(R.layout.activity_photo)
public class PhotoActivity extends BaseActivity {
    private final int CAMERA_WITH_DATA = 1;
    private static final int FLAG_CHOOSE_IMG = 2;    //本地图片选取标志
    private static final int FLAG_MODIFY_FINISH = 3;//截取结束标志
    public static String TMP_PATH = "temp.png";    //要处理的图片
    @ViewInject(R.id.tv_phone_title)    //标题
    private TextView mTitle;
    @ViewInject(R.id.rl_pic_container)
    private RelativeLayout mContainer;
//    @ViewInject(R.id.img_user_photo)    //
//    private ImageView mAvatar;
    private String imgUri;   //新获取的图片路径；

    @Override
    public void onView(Bundle b) {
        //传入要处理的Uri；
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        imgUri = bundle.getString("content");
        mTitle.setText(bundle.getString("title"));
//        x.image().bind(mAvatar, imgUri);
        showPicture(imgUri);
    }

    /**
     * 回退
     * @param view
     */
    @Event(value = {R.id.img_back, R.id.btn_photo_cancel, R.id.img_arrow})
    private void closeActivity(View view) {
       onBackPressed();
    }

    /**
     * 确定更新头像,
     */
    @Event(R.id.tv_phone_update)
    private void updateAvatar(View view) {
        Intent intent = new Intent();
        intent.putExtra("newContent", imgUri);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void showPicture(String path){
        Log.i("chen","需要显示得图片："+path);
        mContainer.removeAllViews();
        ImageView scaleView = new ImageView(this);
        x.image().bind(scaleView,path);
        mContainer.addView(scaleView);
    }

    /**
     * 选择图片
     * @param view
     */
    @Event(value = {R.id.btn_photo_album, R.id.btn_photo_camera, R.id.btn_photo_cancel})
    private void dealClickEvent(View view) {
        switch (view.getId()) {
            case R.id.btn_photo_album:
                getAlbumPic();
                break;
            case R.id.btn_photo_camera:
                getCameraPic();
                break;
            case R.id.btn_photo_cancel:
                finish();
                break;
        }

    }

    private void getAlbumPic() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, FLAG_CHOOSE_IMG);
    }

    private void getCameraPic() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        TMP_PATH = SaveUtils.getCurrentTime() + ".png";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                new File(Environment.getExternalStorageDirectory(),TMP_PATH)));
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }

    // 裁剪图片的Activity
    private void startCropImageActivity(String path) {
        Intent intent = new Intent(this, CutPictureActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, FLAG_MODIFY_FINISH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLAG_CHOOSE_IMG && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (!TextUtils.isEmpty(uri.getAuthority())) {
                    Cursor cursor = getContentResolver().query(uri,
                            new String[]{MediaStore.Images.Media.DATA},
                            null, null, null);
                    if (null == cursor) {
                        Snackbar.make(mContainer, "图片没找到", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                        return;
                    }
                    cursor.moveToFirst();
                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    cursor.close();

                    Intent intent = new Intent(this, CutPictureActivity.class);
                    intent.putExtra("path", path);
                    startActivityForResult(intent, FLAG_MODIFY_FINISH);
                } else {
                    Intent intent = new Intent(this, CutPictureActivity.class);
                    intent.putExtra("path", uri.getPath());
                    startActivityForResult(intent, FLAG_MODIFY_FINISH);
                }
            }
        } else if (requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK) {
            if (data != null) {

                imgUri = null;
                imgUri = data.getStringExtra("path");
//
                showPicture(imgUri);
            }
        }
        switch (requestCode) {
            case CAMERA_WITH_DATA:
                // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                startCropImageActivity(Environment.getExternalStorageDirectory()
                        + "/" + TMP_PATH);
//                startCropImageActivity(SaveUtils.getPictureString(picFromCamera));
                break;
        }
    }

    public String getFilePath(Uri mUri) {
        try {
            if (mUri.getScheme().equals("file")) {
                return mUri.getPath();
            } else {
                return getFilePathByUri(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    // 获取文件路径通过url
    private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
        Cursor cursor = getContentResolver()
                .query(mUri, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(1);
    }
}
