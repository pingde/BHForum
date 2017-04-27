package com.baiheplayer.bbs.encrypt.encrypt;



import com.baiheplayer.bbs.encrypt.EncryptMsg;
import com.baiheplayer.bbs.encrypt.RandomUtil;
import com.baiheplayer.bbs.encrypt.encrypt.base64.Base64;
import com.google.gson.Gson;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class AESMsgCrypt {
    private static final Logger logger = Logger.getLogger("AseMsgCrypt");
    private byte[] aesKey;
    private String token;
    private String appId;
    private Charset CHARSET_ENCODING = Charset.forName("UTF-8");

    /**
     *
     * <p>
     * 构造函数
     * </p>
     *
     * @param token
     *            API 平台上，开发者设置的token
     * @param encodingAesKey
     *            API 平台上，开发者设置的EncodingAESKey
     * @param appId
     *            API 平台 appid
     */
    public AESMsgCrypt(String token, String encodingAesKey, String appId) throws Exception {
        if (encodingAesKey.length() != 43) {
            throw new Exception("SymmetricKey非法");
        }

        this.token = token;
        this.appId = appId;
        aesKey = Base64.decode(encodingAesKey + "=");
    }

    /**
     *
     * <p>
     * 生成4个字节的网络字节序
     * </p>
     *
     * @param sourceNumber
     * @return
     */
    public byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }


    /**
     *
     * <p>
     * 对明文进行加密 16 位随机数加密混淆
     * </p>
     *
     * @param text
     *            需要加密的明文
     * @return 加密后base64编码的字符串
     */
    public String encrypt(String text) throws Exception {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = RandomUtil.getCharacterAndNumber(16).getBytes(CHARSET_ENCODING);
        byte[] textBytes = text.getBytes(CHARSET_ENCODING);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] appidBytes = appId.getBytes(CHARSET_ENCODING);

		/* randomStr + networkBytesOrder + text + appid */
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(appidBytes);

		/* ... + pad: 使用自定义的填充方式对明文进行补位填充 */
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

		/* 获得最终的字节流, 未加密 */
        byte[] unencrypted = byteCollector.toBytes();

        try {
			/* 设置加密模式为AES的CBC模式 */
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

			/* 加密 */
            byte[] encrypted = cipher.doFinal(unencrypted);

			/* 使用BASE64对加密后的字符串进行编码 */
            return new String(Base64.encode(encrypted));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("aes加密失败");
        }
    }


    public String encryptJSONMsg(String replyMsg, String timeStamp, String nonce) throws Exception {
        return encryptMsg(replyMsg, timeStamp, nonce);
    }

    /**
     *
     * <p>
     * 将公众平台回复用户的消息加密打包.
     * <ol>
     * <li>对要发送的消息进行AES-CBC加密</li>
     * <li>生成安全签名</li>
     * <li>将消息密文和安全签名打包成xml格式</li>
     * </ol>
     * </p>
     *
     * @param replyMsg
     *            公众平台待回复用户的消息，xml格式的字符串
     * @param timeStamp
     *            时间戳，可以自己生成，也可以用URL参数的timestamp
     * @param nonce
     *            随机串，可以自己生成，也可以用URL参数的nonce
     * @return 加密后的可以直接回复用户的密文，包括msg_signature, timestamp, nonce,
     *         encrypt的xml格式的字符串
     */
    protected String encryptMsg(String replyMsg, String timeStamp, String nonce) throws Exception {

		/* 随机生成16位字符串混淆加密 */
        String encrypt = encrypt(replyMsg);

		/* 时间戳 */
        if (timeStamp == null || "".equals(timeStamp)) {
            timeStamp = Long.toString(System.currentTimeMillis());
        }

		/* 生成安全签名 */
        String signature = SHA1.getSHA1(token, timeStamp, nonce, encrypt);
        logger.fine("encryptMsg sha1: " + signature);

        return new Gson().toJson(new EncryptMsg(encrypt, signature, timeStamp, nonce));
    }
}
