package com.baiheplayer.bbs.inter;


import com.baiheplayer.bbs.bean.local.UpLoadImage;

/**
 * MyRTManager上传图片到服务器返回的服务器图片地址需要回传给PostActivity
 * Created by Administrator on 2017/2/9.
 *
 */

public interface IUploadCallBack {
    void addBackUrl(String batchId, String name, UpLoadImage image);
}
