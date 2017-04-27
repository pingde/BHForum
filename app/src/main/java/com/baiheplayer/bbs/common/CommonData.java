package com.baiheplayer.bbs.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Administrator on 2016/11/22.
 */

public class CommonData {

    public static String APPID = "4003ysNzZ490C5g9by";
    public static String alicloudPushDevideid = "66c7d55203aa44538b47c6e7aa3157a8";
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().toString();
    public static final String IMAGES_FOLDER = SDCARD_PATH + File.separator + "com.baiheplayer.bbs" + File.separator + "images" + File.separator;
    public static String QQ_SERVICE = "mqqapi://im/chat?chat_type=wpa&uin=2121325083&version=1&src_type=web";
    public static String OFFICIAL_WEB = "https://www.baiheplayer.com";
    public static String CORP_EMAIL = "support@baiheplayer.com";
    public static String HOT_CALL = "tel:4007008088";  //
    public static int  PLATFORM = 2;
    /**八位随机数混淆*/
    public static int getRandomNum(){
        int random = 0;
        for(int i=0;i<8;i++){
            int num =(int)(Math.random()*10);
            if(i==0 && num == 0){
                num++;
            }
            random = random*10 +num;
        }
       return random;
    }
    /**MD5请求字符串的签名*/
    //MD5("baihe_&lang={lang}&appid={appid}&grantType={grantType}&time={time}&nonce={nonce}
    public static String getMessageDigist(String time,String num){
        String convertString = "baihe_&lang={lang}&appid={"+ CommonData.APPID+"}&grantType={"+1+"}&time={"+time+"}&nonce={"+num+"}";
        return md5(convertString);
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}
