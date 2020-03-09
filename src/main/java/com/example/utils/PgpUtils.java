package com.example.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class PgpUtils {

    public static char[] ksPassword = "123456".toCharArray();

    public static String robotEntryAlias = "robot";

    public static String serverEntryAlias = "server";

    /**
     * 加载Keystore
     * @param pwdArray Keystore密码
     * @return Keystore对象
     * @throws IOException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    private static KeyStore loadKeystore(char[] pwdArray)
            throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("pkcs12");
        InputStream resourceAsStream = PgpUtils.class.getResourceAsStream("/keystore");
        ks.load(resourceAsStream, pwdArray);
        return ks;
    }

    /**
     * 从Keystore中获取指定PrivateKeyEntry
     * @param ks KeyStore对象
     * @param alias entry别名
     * @param entryPwd entry密码
     * @return PrivateKeyEntry对象
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private static KeyStore.PrivateKeyEntry getPrivateKeyEntry(KeyStore ks, String alias, char[] entryPwd)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(entryPwd);
        return (KeyStore.PrivateKeyEntry) ks.getEntry(alias, entryPassword);
    }

    /**
     * 从Keystore中获取指定entry的证书
     * @param ks KeyStore对象
     * @param alias entry别名
     * @return 证书对象
     * @throws KeyStoreException
     */
    private static Certificate getCert(KeyStore ks, String alias) throws KeyStoreException {
        return ks.getCertificate(alias);
    }

    /**
     * 从Keystore中获取指定entry的私钥
     * @param ks KeyStore对象
     * @param alias entry别名
     * @param entryPwd entry密码
     * @return 私钥对象
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private static PrivateKey getPrivateKey(KeyStore ks, String alias, char[] entryPwd)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (PrivateKey) ks.getKey(alias, entryPwd);
    }

    /**
     * 使用SHA-256算法计算摘要
     * @param input 输入数据
     * @return 摘要
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static byte[] sha256(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes("UTF-8"));
    }

    /**
     * 生成签名（SHA256WithRSA）。
     * @param plainText 输入数据
     * @param privateKey 签名方私钥
     * @return 签名数据
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    private static byte[] sign(byte[] plainText, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(privateKey);
        signature.update(plainText);
        return signature.sign();
    }

    /**
     * 验证签名（SHA256withRSA）
     * @param plainText 明文
     * @param signatureText 签名
     * @param cert 签名方证书
     * @return 签名是否有效
     * @throws Exception
     */
    private static boolean verifySignature(byte[] plainText, byte[] signatureText, Certificate cert) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(cert.getPublicKey());
        signature.update(plainText);
        return signature.verify(signatureText);
    }

    /**
     * 使用AES算法进行加密
     * @param plainText 明文
     * @param secretKey 密钥
     * @return 密文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private static byte[] encryptAes(byte[] plainText, SecretKey secretKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plainText);
    }

    /**
     * 使用AES算法进行解密
     * @param cypherext 密文
     * @param secretKey 密钥
     * @return 明文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private static byte[] decryptAes(byte[] cypherext, SecretKey secretKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(cypherext);
    }

    /**
     * 随机生成AES密钥
     * @return AES密钥
     * @throws NoSuchAlgorithmException
     */
    private static SecretKey genAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    /**
     * 将AES密钥数据封装为SecretKeySpec对象
     * @param keyByte AES密钥数据
     * @return SecretKeySpec对象
     */
    private static SecretKeySpec genAESKey(byte[] keyByte) {
        return new SecretKeySpec(keyByte, "AES");
    }

    /**
     * 使用RSA算法进行加密
     * @param plainText 明文
     * @param cert 接收方证书
     * @return 密文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private static byte[] encryptRSA(byte[] plainText, Certificate cert)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, cert.getPublicKey());
        return cipher.doFinal(plainText);
    }

    /**
     * 使用RSA算法进行解密
     * @param cypherText 密文
     * @param privateKey 接收方密钥
     * @return 明文
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private static byte[] decryptRSA(byte[] cypherText, PrivateKey privateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(cypherText);
    }

    /**
     * 使用PGP算法进行加密。
     * @param plainText 明文
     * @param ksPwd Keystore密码
     * @param senderEntryAlias 发送方entry
     * @param receiverEntryAlias 接收方entry
     * @return PGP密文。格式为：[256位签名] + [变长密文]
     * @throws CertificateException
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encryptPGP(byte[] plainText, char[] ksPwd, String senderEntryAlias, String receiverEntryAlias)
            throws CertificateException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, IOException,
            SignatureException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        KeyStore ks = loadKeystore(ksPwd);

        // sign
        PrivateKey privateKey = getPrivateKey(ks, senderEntryAlias, ksPwd);
        byte[] signature = sign(plainText, privateKey);
        debug("PGP发送方，对明文签名", signature);

        // 拼接签名和明文
        byte[] signatureAndPlainText = MiscUtils.mergeByteArray(signature, plainText);

        // 对签名和明文进行对称加密
        SecretKey secretKey = genAESKey();
        byte[] cypherText = encryptAes(signatureAndPlainText, secretKey);
        debug("PGP发送方，对签名和明文加密", cypherText);

        // 用接收方公钥对对称密钥加密
        Certificate receiverCert = getCert(ks, receiverEntryAlias);
        byte[] encryptedSecretKey = encryptRSA(secretKey.getEncoded(), receiverCert);
        debug("PGP发送方，对加密密钥加密", encryptedSecretKey);
//        System.out.println(encryptedSecretKey.length);

        // 拼接对称密钥和密文
        byte[] secretKeyAndCipherText = MiscUtils.mergeByteArray(encryptedSecretKey, cypherText);
        debug("PGP发送方，送出报文", secretKeyAndCipherText);

        return secretKeyAndCipherText;
    }

    public static byte[] robotEncryptPGP(byte[] plainText) {
        try {
            return encryptPGP(plainText, ksPassword, robotEntryAlias, serverEntryAlias);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] serverEncryptPGP(byte[] plainText) {
        try {
            return encryptPGP(plainText, ksPassword, serverEntryAlias, robotEntryAlias);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用PGP算法进行解密。
     * @param pgpData PGP密文
     * @param ksPwd Keystore密码
     * @param senderEntryAlias 发送方entry
     * @param receiverEntryAlias 接收方entry
     * @return 明文（不含签名）
     * @throws Exception
     */
    public static byte[] decryptPGP(byte[] pgpData, char[] ksPwd, String senderEntryAlias, String receiverEntryAlias) throws Exception {
        byte[] encryptedSecretKey = new byte[256];
        System.arraycopy(pgpData, 0, encryptedSecretKey, 0, 256);
        byte[] cypherText = new byte[pgpData.length - 256];
        System.arraycopy(pgpData, 256, cypherText, 0, cypherText.length);

        KeyStore ks = loadKeystore(ksPwd);

        // 用private key解密会话密钥
        PrivateKey privateKey = getPrivateKey(ks, receiverEntryAlias, ksPwd);
        byte[] secretKey = decryptRSA(encryptedSecretKey, privateKey);
        debug("PGP接收方，恢复会话密钥", secretKey);

        // 用session key对密文解密
        byte[] signatureAndPlainText = decryptAes(cypherText, genAESKey(secretKey));

        // 分离签名和负载内容
        byte[] signature = new byte[256];
        System.arraycopy(signatureAndPlainText, 0, signature, 0, 256);
        byte[] plainText = new byte[signatureAndPlainText.length - 256];
        System.arraycopy(signatureAndPlainText, 256, plainText, 0, plainText.length);
        debug("PGP接收方，恢复签名", signature);
        debug("PGP接收方，恢复原文", plainText);

        // 验证签名
        Certificate senderCert = getCert(ks, senderEntryAlias);
        boolean verifyResult = verifySignature(plainText, signature, senderCert);
        debug("PGP接收方，签名验证", verifyResult);

        return plainText;
    }

    public static byte[] robotDecryptPGP(byte[] pgpData) {
        try {
            return decryptPGP(pgpData, ksPassword, serverEntryAlias, robotEntryAlias);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] serverDecryptPGP(byte[] pgpData) {
        try {
            return decryptPGP(pgpData, ksPassword, robotEntryAlias, serverEntryAlias);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void debug(String msg, byte[] data) {
//        data = data == null ? new byte[0] : data;
//        System.out.println(msg + " " + Hex.encodeHexString(data));
    }

    private static void debug(String msg, boolean data) {
//        System.out.println(msg + " " + data);
    }


    public static void main(String[] args) throws Throwable {

        String orig_plainText = "我是明文我是我是明文我是我是明文我是我是明文我是我是明文我是我是明文我是我是明文我是";
        byte[] plainTextByte = orig_plainText.getBytes(StandardCharsets.UTF_8);

//        sha256("a a a a ");

//        KeyStore ks = loadKeystore(pwdArray);
//
//        KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKeyEntry(ks, "robot", pwdArray);
//        PrivateKey privateKey = privateKeyEntry.getPrivateKey();
//        Certificate cert = privateKeyEntry.getCertificate();
//        System.out.println(cert.getPublicKey().getAlgorithm());
//
//        byte[] signatureText = sign(plainTextByte, privateKeyEntry.getPrivateKey());
//        verifySignature(plainTextByte, signatureText, cert.getPublicKey());



//        SecretKey key = genAESKey();
//        byte[] cipherText = encryptAes(text.getBytes("UTF-8"), key);
//        key = genAESKey(key.getEncoded());
//        byte[] plainText = decryptAes(cipherText, key);
//        System.out.println(new String(plainText, "UTF-8"));


//        byte[] cypherText = encryptRSA(plainTextByte, cert);
//        byte[] plainText = decryptRSA(cypherText, privateKey);
//        System.out.println("RSA decrypt: " + new String(plainText, "UTF-8"));

        byte[] encryptPGPMsg = encryptPGP(plainTextByte, ksPassword, "robot", "server");
        debug("==========================================", null);
        byte[] decrypt_plainText = decryptPGP(encryptPGPMsg, ksPassword, "robot", "server");
        debug("对比两端结果：", orig_plainText.equals(new String(decrypt_plainText, "UTF-8")));

    }
}
