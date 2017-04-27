package com.baiheplayer.bbs.bean.local;

import com.baiheplayer.bbs.bean.DataBack;

/**
 * Created by Administrator on 2016/11/22.
 */

public class TokenBack extends DataBack {
    //{"data":{
    // "expires":7200,
    // "accessToken":"E3zzHhmwmhSt7yp0lvBU2NVVhCU9F27DIUUDHXUD6gi_B-X_91Y.",
    // "aesKey":"743136102121592Mp02512058xiN4k9Tbd6Y141o22M",
    // "refreshToken":"VXi3EGbLnTrA2HlzyswJo6NUx2o5FG_EaxkCRSoCtg0."},
    // "code":0,
    // "msg":"操作成功"}
    private Data data;



    public Data getData() {
        return data;
    }
    private class Data{
        private int expires;
        private String accessToken;
        private String aesKey;
        private String refreshToken;
    }
    public int getExpires() {
        return data.expires;
    }

    public void setExpires(int expires) {
        this.data.expires = expires;
    }

    public String getAccessToken() {
        return data.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.data.accessToken = accessToken;
    }

    public String getAesKey() {
        return data.aesKey;
    }

    public void setAesKey(String aeskey) {
        this.data.aesKey = aeskey;
    }

    public String getRefreshToken() {
        return data.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.data.refreshToken = refreshToken;
    }
}


