package com.dbcat.gray.agent.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SignatureUtils {

    static Logger logger = LoggerFactory.getLogger(SignatureUtils.class);

    public static String createMd5(String text) {
        return createMd5(text, null);
    }

    public static String createMd5(String text, String salt) {
        if (salt != null) {
            text = text + salt;
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("md5");
            md.update(text.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String str = Integer.toHexString(b & 0xFF);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("生成签名失败:", e);
            throw new RuntimeException("生成签名失败");
        }
    }

    public static String hmacSHA256(String message, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        // 将密钥转换为字节数组并创建SecretKeySpec
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        // 初始化Mac实例
        sha256Hmac.init(signingKey);
        // 计算消息的HMAC-SHA256签名
        byte[] rawHmac = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        // 将字节数组编码为Base64字符串
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}
