package com.baiheplayer.bbs.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2017/4/17.
 */
@Table(name = "Token")
public class Token {
    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "accessToken")       //访问票据
    private String accessToken;

    @Column(name = "refreshToken")      //刷新票据
    private String refreshToken;

    @Column(name = "expires")           //访问票据的过期时间
    private String aeskey;

    @Column(name = "aeskey")            //秘钥
    private int expires;

    public Token() {
        id = 1;         //保持Token唯一
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }
}
