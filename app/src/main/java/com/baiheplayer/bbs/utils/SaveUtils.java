package com.baiheplayer.bbs.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.bean.map.Province;
import com.baiheplayer.bbs.common.CommonData;
import com.baiheplayer.bbs.encrypt.RandomUtil;
import com.baiheplayer.bbs.encrypt.encrypt.AESMsgCrypt;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.onegravity.rteditor.media.choose.MediaEvent;

import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2016/11/22.
 */

public class SaveUtils {
    /**
     * 获得当前时间
     *
     * @return
     */
    public static long getCurrentTime() {
        return new Date().getTime();
    }

    public static String getDate() {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(today);
    }

    /**
     * 格式化时间显示
     *
     * @param string
     * @return
     */
    public static String formatTime(String string) {
        Date now = new Date();
        Date time = new Date();
        time.setTime(Long.parseLong(string));
        SimpleDateFormat sdf = null;
        now.getDay();
        if (now.getYear() != time.getYear()) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日");
        } else if (now.getMonth() == time.getMonth() && now.getDate() == time.getDate()) {
            sdf = new SimpleDateFormat("HH:mm");
        } else {
            sdf = new SimpleDateFormat("MM月dd日 HH:mm");

        }
        return sdf.format(time);
    }

    /**
     * 检验邮箱
     *
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 检验号码
     *
     * @param str
     * @return
     */
    public static boolean isMobileNum(String str) {
//        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//        Matcher matcher = pattern.matcher(str);
        if(str.length() == 11 &&str.startsWith("1")){
            return true;
        } else {
            return false;
        }
//        return matcher.matches();
    }


    /**
     * 查询网络状态
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        boolean isWifi = isWifiConneted(context);
        boolean isMobile = isMobileConneted(context);
        if (!isMobile && !isWifi) {
            return false;
        }
        return true;
    }

    private static boolean isWifiConneted(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null) {
            return wifiInfo.isConnected();
        } else {
            return false;
        }
    }

    private static boolean isMobileConneted(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo != null) {
            return mobileInfo.isConnected();
        } else {
            return false;
        }
    }


    /**
     * 初始化ImageLoader
     */
    public static void initImageLoader(Context context) {
        int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        MemoryCacheAware<String, Bitmap> memoryCache;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            memoryCache = new LruMemoryCache(memoryCacheSize);
        } else {
            memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
        }
        DisplayImageOptions load_option = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(load_option).denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(new File(CommonData.IMAGES_FOLDER))).memoryCache(memoryCache)
                .tasksProcessingOrder(QueueProcessingType.LIFO).threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3).build();
        ImageLoader.getInstance().init(config);
    }

    public static String formatCacheSize(long cacheSize) {
        DecimalFormat df = new DecimalFormat("######0.00");
        if (cacheSize <= 0) {
            return "无缓存";
        } else if (cacheSize < 1000) {
            return cacheSize + "B";
        } else if (cacheSize > 1000 && cacheSize < 1000 * 1000) {
            return df.format(cacheSize / 1000.00f) + "kB";
        } else {
            return df.format(cacheSize / (1000 * 1000.00)) + "MB";
        }

    }

    /**
     * 填入图片前的压缩
     *
     * @param event
     * @return
     */
    public static File reformAndPostEvent(Context context, MediaEvent event) {
        String image_name = event.getMedia().getFileName();
        int image_width = event.getMedia().getWidth();
        int screenWidth = Constant.screenWidth;
//        File path = new File(Environment.getExternalStorageDirectory(), "Android/data/" + App.getInstance().getPackageName() + "/files");
        File path = context.getExternalFilesDir("/images/");
//        File file = new File(path, "/images/" + image_name);
        File file = new File(path, image_name);
        Log.i("chen", "file路径:" + file.getPath() + " 获得的event大小:" + file.length() + "原event大小：" + event.getMedia().getSize());
        int targetSize = 800;
        if (image_width > targetSize) {
            BitmapFactory.Options old_opt = new BitmapFactory.Options();
            old_opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getPath(), old_opt);
            int old_width = old_opt.outWidth;
            int old_height = old_opt.outHeight;
            float size = devive(old_height,old_width);
            int new_width = (targetSize);
            float scale = old_width / targetSize;
            if (scale <= 0) scale = 1;
//            int new_height = (int) (old_height / scale);
            int new_height = (int) (new_width * size);
            BitmapFactory.Options new_opt = new BitmapFactory.Options();
            new_opt.inJustDecodeBounds = false;
            new_opt.inSampleSize = (int) scale + 1;
            new_opt.outHeight = new_height;
            new_opt.outWidth = new_width;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), new_opt);
            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
                file.createNewFile();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, os);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return file;
    }

    public static float devive(float num1,float num2){
        float jieguo  = num1 / num2;
        return jieguo;
    }


    public static String encrypt(String content,String password){
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result.toString(); // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RequestParams getEncrypt(RequestParams params ){
        List<KeyValue> queryStringList = params.getQueryStringParams();
        if(queryStringList.size()>0){
            try {
                AESMsgCrypt aesMsgCrypt = new AESMsgCrypt(Constant.accessToken, Constant.aesKey, CommonData.APPID);
                String timeStamp = Long.toString(System.currentTimeMillis());
                String nonce = RandomUtil.getCharacterAndNumber(8);
                Map<String,Object> map = new HashMap<>();
                for(int i=0;i<queryStringList.size();i++){
                    map.put(queryStringList.get(i).key,queryStringList.get(i).value);
                    params.removeParameter(queryStringList.get(i).key);
                }
//                String gson = new Gson().toJson(map);
                String encryptData = aesMsgCrypt.encryptJSONMsg(new Gson().toJson(map), timeStamp, nonce);
                params.addParameter("data",encryptData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return params;
    }
}
