package com.baiheplayer.bbs.encrypt;


public class EncryptMsg {
    /*
	 * 加密内容
	 */
    private String encrypt;

    /*
     * 消息签名
     */
    private String msgSignature;

    /*
     * 时间戳
     */
    private String timeStamp;

    /*
     * 随机混淆参数
     */
    private String nonce;

    public EncryptMsg() {

    }

    public EncryptMsg(String encrypt, String signature, String timestamp, String nonce) {
        this.encrypt = encrypt;
        this.msgSignature = signature;
        this.timeStamp = timestamp;
        this.nonce = nonce;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

    public String getMsgSignature() {
        return msgSignature;
    }

    public void setMsgSignature(String msgSignature) {
        this.msgSignature = msgSignature;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
