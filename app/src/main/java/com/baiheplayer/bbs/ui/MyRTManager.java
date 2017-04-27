package com.baiheplayer.bbs.ui;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baiheplayer.bbs.bean.local.UpLoadImage;
import com.baiheplayer.bbs.bean.local.UploadBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.IUploadCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.media.RTMedia;
import com.onegravity.rteditor.media.choose.MediaEvent;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/9.
 */

public class MyRTManager extends RTManager {
    private IUploadCallBack callBack;   //回传图片网址
    private Context context;
    private String batchId;
    private ArrayList<UpLoadImage> lists;

    /**
     * @param rtApi              The proxy to "the outside world"
     * @param savedInstanceState If the component is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it most
     */
    public MyRTManager(RTApi rtApi, Bundle savedInstanceState) {
        super(rtApi, savedInstanceState);
    }

    public void addIUploadCallBackListener(Context _context,IUploadCallBack _callBack){
        context = _context;
        callBack = _callBack;
        lists = new ArrayList<>();

    }


    @Override
    public void onEventMainThread(MediaEvent event) {
        final RTMedia media = event.getMedia();
        // TODO: 2017/2/9  缩放图片 然后上传

        File imageFile = SaveUtils.reformAndPostEvent(context,event);   //完成缩放，返回原路径
        Log.i("chen","选择的图片是："+media.getFileExtension()+" "+media.getFileName());
        super.onEventMainThread(event);
        RequestParams params = new RequestParams(HttpDatas.UP_LOAD);
        if(!TextUtils.isEmpty(batchId)){
            params.addParameter("batchId",batchId);
        }
        params.addParameter("files",new File(imageFile.getPath()));
        HttpsUtils.getInstance().requestSavely(params, UploadBack.class, new INetCallBack() {
            @Override
            public <T> void back( String type,T resultInfo) {
                UploadBack data = (UploadBack)resultInfo;
                if(data.getCode() == 0){
                    batchId = data.getBatchId();
                    UpLoadImage image = data.getFiles().get(0);
                    callBack.addBackUrl(batchId,media.getFileName(),image);
                    Toast.makeText(context,"图片上传成功",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context,"图片上传失败",Toast.LENGTH_SHORT).show();
                    onUndo();
                }

            }
            @Override
            public void fail(boolean isOnCallBack) {

            }
        });

    }
}
