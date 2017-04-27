package com.baiheplayer.bbs;

//import com.umeng.socialize.media.WBShareCallBackActivity;

import android.os.Bundle;
import android.util.Log;

import com.umeng.socialize.media.WBShareCallBackActivity;

/**
 * Created by wangfei on 15/12/3.
 */
public class WBShareActivity extends WBShareCallBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("chen","WBShareActivity onCreate()");
    }
}
