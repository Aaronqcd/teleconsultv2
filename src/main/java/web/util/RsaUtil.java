package web.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加密和解密工具
 *
 * @Author: longmao
 * @CreateDate: 2020-05-25
 */
public class RsaUtil{

    /**
     * 数字签名，密钥算法
     */
    private static final String RSA_KEY_ALGORITHM = "RSA";

    /**
     * 数字签名签名/验证算法
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * RSA密钥长度，RSA算法的默认密钥长度是1024密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 1024;

    private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM+eodFA0PUxQ+pcrjDU71qXH0djkI3jwF3YUizzDECdnm/hQGAsvY8145OgjCa5+F+LSvMWOsNidR9efHuNxJwXjp8JUbKfzEHkkjzOUnUrs2b/FTLwq2wj6046Hqb3pDRYjlhqT/p6rb3H2Qws9skPlCsZz6L4sGpOyR43/tcNAgMBAAECgYAVKk3CwEHNDSWoSaR0U/DGomtM4siEvngI2RmffXN9TtQtAaCHbWgxpkO58/71n7XDP7b3SwfYjTv1Y7N7APvO4syVG+dN6vKigEs/Fv6ijvu6ISmDhbjvlp2EIUSHn8m84Uuc7wvksV/mW4YVGXUox7QfKfMm0HaK4JdsIaPSXQJBAO3vJ0FIOVvTWeEgne+7dlfAsCVY52SekegO5KB0fo0zhR0f1TwsRgXoRJtTip+9QDjCWYDL/wFC2wuViTOoIYsCQQDfYj1nsNBGcRdqQnmnYnqlkqXNrdyIKZ36OAooy0S4ZxRZ4BO6Mm3JxoSnKMq3tYVvIsn4Qn2MeEaH0R3O9szHAkEA2Z015ftMmrN/LOxMBwsJfdD6Se46FEkDYZ7dc/OYG0TXpn+K43IKyTRaK0YJL3hD2KXIfogVPu4KsVmfFuPbaQJBAK6JTb0k07cWSdtGkVMNiRKxYEcyXyssiTimbJmvKMSEFcybXg6PtGSBbchGAQ5FEDrjjbciDIKiv0kDRS0efKUCQEanoMDkYmyHT1LmGiCBcLagma3le6e6Sqwdt9PSkjSzYWhIUjR29gwVPP3K4f9fHuM0ipYCaL2u1TPVAl7ImEw=";

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPnqHRQND1MUPqXK4w1O9alx9HY5CN48Bd2FIs8wxAnZ5v4UBgLL2PNeOToIwmufhfi0rzFjrDYnUfXnx7jcScF46fCVGyn8xB5JI8zlJ1K7Nm/xUy8KtsI+tOOh6m96Q0WI5Yak/6eq29x9kMLPbJD5QrGc+i+LBqTskeN/7XDQIDAQAB";
    /**
     * 密钥转成字符串
     *
     * @param key
     * @return
     */
    public static String encodeBase64String(byte[] key) {
        return Base64.encodeBase64String(key);
    }

    /**
     * 密钥转成byte[]
     *
     * @param key
     * @return
     */
    public static byte[] decodeBase64(String key) {
        return Base64.decodeBase64(key);
    }


    /**
     * 公钥解密
     *
     * @param data   待解密的数据
     * @param pubKey 公钥
     * @return 解密后的数据
     * @throws Exception
     */
    public static byte[] decryptByPubKey(byte[] data, byte[] pubKey) throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     *
     * @param data      解密前的字符串
     * @param publicKey 公钥
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decryptByPubKey(String data, String publicKey) throws Exception {
        byte[] pubKey = RsaUtil.decodeBase64(publicKey);
        byte[] design = decryptByPubKey(Base64.decodeBase64(data), pubKey);
        return new String(design);
    }

    /**
     * 私钥解密
     *
     * @param data   待解密的数据
     * @param priKey 私钥
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPriKey(byte[] data, byte[] priKey) throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data       解密前的字符串
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decryptByPriKey(String data) throws Exception {
        byte[] priKey = RsaUtil.decodeBase64(PRIVATE_KEY);
        byte[] design = decryptByPriKey(Base64.decodeBase64(data), priKey);
        return new String(design);
    }

    /**
     * RSA校验数字签名
     *
     * @param data   待校验数据
     * @param sign   数字签名
     * @param pubKey 公钥
     * @return boolean 校验成功返回true，失败返回false
     */
    public static boolean verify(byte[] data, byte[] sign, byte[] pubKey) throws Exception {
        // 实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        // 初始化公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        // 产生公钥
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        // 实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        // 初始化Signature
        signature.initVerify(publicKey);
        // 更新
        signature.update(data);
        // 验证
        return signature.verify(sign);
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param PUBLIC_KEY
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(PUBLIC_KEY);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }
}