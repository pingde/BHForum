package com.baiheplayer.bbs.bean;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/25.
 */

public class UserBack extends DataBack {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private class Data {
        private String userId;
        private String nickname;
        private String avatar;
        private String signature;
        private String phone;
        private String email;
        private String addressFull;

    }

    public String getUserId() {
        return data.userId;
    }

    public void setUserId(String userId) {
        this.data.userId = userId;
    }

    public String getNickname() {
        return  data.nickname;
    }

    public void setNickname(String nickname) {
        this. data.nickname = nickname;
    }

    public String getAvatar() {
        return  data.avatar;
    }

    public void setAvatar(String avatar) {
        this. data.avatar = avatar;
    }

    public String getSignature() {
        return  data.signature;
    }

    public void setSignature(String signature) {
        this. data.signature = signature;
    }

    public String getPhone() {
        return  data.phone;
    }

    public void setPhone(String phone) {
        this. data.phone = phone;
    }

    public String getEmail() {
        return  data.email;
    }

    public void setEmail(String email) {
        this. data.email = email;
    }

    public String getAddressFull() {
        return  data.addressFull;
    }

    public void setAddressFull(String addressFull) {
        this. data.addressFull = addressFull;
    }
}
