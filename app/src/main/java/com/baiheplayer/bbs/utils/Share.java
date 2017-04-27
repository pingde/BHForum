package com.baiheplayer.bbs.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference管理类
 * Created by Administrator on 2017/4/17.
 */

public class Share {
    private static boolean isInit = false ;
    private static SharedPreferences sharePreference;
    private static SharedPreferences.Editor editor;
    //SharedPreferences 文件对应的名字
    public final static String PREF_NAME = "setting";

    /**
     * init Share ,must be called before use
     * Share
     * @param context
     */
    public static void init(Context context) {
        //如果已经初始化，那么不在初始化
        if(isInit)
            return ;
        sharePreference = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        editor = sharePreference.edit();
        isInit = true ;
    }

    public static void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putString(String key, String value) {
        System.out.println("put_str--------->"+value);
        editor.putString(key, value);
        editor.commit();
    }

    public static void putLong(String key ,long value){
        editor.putLong(key, value);
        editor.commit();
    }

    public static void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key,boolean defaultValue){
        return sharePreference.getBoolean(key, defaultValue);
    }

    public static String getString(String key,String defValue){
        return sharePreference.getString(key, defValue);
    }

    public static int getInt(String key,int defValue){
        return sharePreference.getInt(key, defValue);
    }

    public static long getLong(String key,long defValue){
        return sharePreference.getLong(key, defValue);
    }
}
