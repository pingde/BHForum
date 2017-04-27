package com.baiheplayer.bbs.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.bean.LocalInfo;
import com.baiheplayer.bbs.bean.map.Province;

import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/20.
 */

public class FileUtils {
    /**
     * 存储数据文件
     * /data/data/包名/file
     *
     * @param context
     * @param list
     * @param name
     */
    public static void saveData(Context context, List list, String name) {
        File file = new File(context.getFilesDir(), name);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                    Log.i("chen", "文件" + name + "已存储");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static File getPictureFile(String name) {
        File file = new File(Environment.getExternalStorageDirectory(), "BH_Player" + File.separator + name);

        return file;
    }

    public static String getPictureString(String name) {
        return Environment.getExternalStorageDirectory().toString() + File.separator + "BH_Player" + File.separator + name;
    }

    /**
     * 读取数据文件
     * /data/data/包名/file
     *
     * @param context
     * @param name
     * @return
     */
    public static List readData(Context context, String name) {
        List<Province> list = new ArrayList<>();
        File file = new File(context.getFilesDir(), name);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            list = (List<Province>) ois.readObject();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 有账号则绑定到阿里推送
     * @param pushService
     */
    public static void bindAccount(CloudPushService pushService){
        LocalInfo localInfo =new LocalInfo();
        try {
            localInfo = x.getDb(App.getDaoConfig()).selector(LocalInfo.class).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(!TextUtils.isEmpty(localInfo.getUserId())){
            pushService.bindAccount(localInfo.getUserId(), new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    Log.i("chen","绑定成功"+s);
                }

                @Override
                public void onFailed(String s, String s1) {

                }
            });
        }
    }

    /**
     * 从阿里推送解绑
     */
    public static void unBindAccount(){
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFailed(String s, String s1) {

            }
        });
    }

}
