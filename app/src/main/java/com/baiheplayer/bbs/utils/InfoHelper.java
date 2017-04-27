package com.baiheplayer.bbs.utils;

import android.util.Log;

import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.bean.LocalInfo;
import com.google.gson.Gson;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * 帮助统一管理用户信息
 * Created by Administrator on 2017/4/13.
 */

public class InfoHelper {
    private static InfoHelper helper;

    public static synchronized InfoHelper getInstance() {
        if (helper == null) {
            helper = new InfoHelper();
        }
        return helper;
    }

    private DbManager db;
    private LocalInfo info;


    public InfoHelper() {
        db = x.getDb(App.getDaoConfig());
    }


    /**
     * 获取从数据库获取用户信息LocalInfo，没有则返回false
     */
    public boolean init() {
        boolean success = false;
        if (info == null){
            info = new LocalInfo();
            Log.i("chen","新建一个LocalInfo");
        }

        try {
            LocalInfo temp = db.selector(LocalInfo.class).findFirst();
            Log.i("chen","获取数据库的Info  "+new Gson().toJson(temp));
            if(temp!=null){
               info = temp;
            }
        } catch (DbException e) {
            e.printStackTrace();
            info = new LocalInfo();
            Log.i("chen", "获取数据库失败");
        }
        return success;
    }


    /**
     * 保存实例到数据库和Helper，成功返回true
     *
     * @param info
     * @return
     */
    public boolean save(LocalInfo info) {
        boolean success = false;
        if(info.getRefreshToken().equals("") ||info.getRefreshToken()== null){
            return success;
        }
        LocalInfo info2 = new LocalInfo();
        info2.setId(2);
        LocalInfo info3 = new LocalInfo();
        info2.setId(3);
        LocalInfo info4 = new LocalInfo();
        info2.setId(4);
        try {
            db.save(info);
            db.save(info2);
            db.save(info3);
            db.save(info4);
            this.info = info;
            Log.i("chen","存一次 "+new Gson().toJson(info));
            success = true;
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            display();
        }
        return success;
    }

    public boolean update(LocalInfo info){
        boolean success = false;
        if(info.getRefreshToken().equals("") ||info.getRefreshToken()== null){
            return success;
        }

        try {
            db.update(info);
            this.info = info;
            Log.i("chen","刷新一次"+new Gson().toJson(info));
            success = true;
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            display();
        }
        return success;
    }

    /**
     * 返回获取到的实例
     *
     * @return
     */
    public LocalInfo getInfo() {
        return info;
    }

    public void clearUser() {
        info = new LocalInfo();
        try {
            db.update(info);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将存储信息转化成常量
     */
    public void display() {
        Constant.USER_ID = info.getUserId();
        Constant.USER_AVATAR = info.getAvatar();
        Constant.USER_NICKNAME = info.getNickname();
        Constant.USER_PHONE = info.getPhone();
        Constant.USER_EMAIL = info.getEmail();
        Constant.USER_PWD = info.getPassword();
        Constant.USER_ADDR= info.getAddressFull();
        Constant.accessToken = info.getAccessToken();
        Constant.refreshToken = info.getRefreshToken();
        Constant.aesKey = info.getAeskey();
        Constant.expires = info.getExpires();
    }

}
