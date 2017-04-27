package com.baiheplayer.bbs.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 管理Token
 * Created by Administrator on 2016/11/22.
 */
@Table(name = "LocalInfo")
public class LocalInfo {
    @Column(name = "id", isId = true)
    private int id = 1;

    @Column(name = "aeskey")  //秘钥
    private String aeskey = "";

    @Column(name = "accessToken")     //访问票据
    private String accessToken = "";

    @Column(name = "refreshToken")    //刷新票据
    private String refreshToken = "";

    @Column(name = "expires")        //访问票据的过期时间
    private int expires = 7200;

    @Column(name = "userId")         //用户id
    private String userId = "";

    @Column(name = "nickname")       //用户昵称
    private String nickname = "";

    @Column(name = "avatar")         //用户头像
    private String avatar = "";

    @Column(name = "phone")          //用户电话
    private String phone = "";

    @Column(name = "password")          //用户邮箱
    private String password = "";

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "email")          //用户密码
    private String email = "";

    @Column(name = "signature")      //个性签名
    private String signature = "";

    @Column(name = "addressFull")    //地址
    private String addressFull = "";

    @Column(name = "logState")       //登录状态
    private boolean logState = false;

    @Column(name = "background")     //背景图片
    private String background = "";

    @Column(name = "Longitude")      //经度
    private String longitude = "";

    @Column(name = "Latitude()")     //维度
    private String latitude = "";

    public LocalInfo() {
        this.id = 1;
    }


    /**********************************************************/
    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
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

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLogState() {
        return logState;
    }

    public void setLogState(boolean logState) {
        this.logState = logState;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAddressFull() {
        return addressFull;
    }

    public void setAddressFull(String addressFull) {
        this.addressFull = addressFull;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
