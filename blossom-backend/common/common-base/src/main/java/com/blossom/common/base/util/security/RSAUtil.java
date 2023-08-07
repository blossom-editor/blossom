package com.blossom.common.base.util.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA 是非对称的密码算法，密钥分公钥和私钥
 * <p>公钥用来加密，或验签
 * <p>私钥用于解密，或加签
 *
 * @since 0.0.1
 * @author xzzz
 */
public class RSAUtil {

    /**
     * 密钥算法
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法：MD2withRSA, SHA1WithRSA, SHA256withRSA, SHA384withRSA, SHA512withRSA
     */
    public static final String SIGN_ALGORITHM = "MD5withRSA";

    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";


    public static void main(String[] args) {
        // 生成公钥私钥对
        Map<String, String> map = RSAUtil.getKeyMap(String.valueOf(System.currentTimeMillis()));
        // 公钥, 提供给外部
        String publicKey = map.get(RSAUtil.PUBLIC_KEY);
        // 私钥, 服务器使用
        String privateKey = map.get(RSAUtil.PRIVATE_KEY);

        // 加密
        byte[] byteArr = RSAUtil.encrypt("test123！！！！", publicKey);
        System.out.println("加密后的密文：\n" + Arrays.toString(byteArr));
        System.out.println("解密后的明文：\n" + RSAUtil.decrypt(byteArr, privateKey));

        // 签名  验证签名
        String sign = RSAUtil.sign("sign_test_123", privateKey);
        System.out.println("使用私钥进行签名：" + sign);
        System.out.println("使用公钥验证签名：" + RSAUtil.verifySign("sign_test_123", sign, publicKey));
    }

    /**
     * 获取秘钥对
     *
     * @param seedStr 种子字符串
     * @return Map<String, String> 秘钥集合
     */
    public static Map<String, String> getKeyMap(String seedStr) {
        return getKeyMap(seedStr, KEY_ALGORITHM);
    }

    /**
     * 获取秘钥对
     *
     * @param seedStr   种子字符串
     * @param algorithm 算法
     * @return Map<String, String> 秘钥集合
     */
    public static Map<String, String> getKeyMap(String seedStr, String algorithm) {
        Map<String, String> keyMap = new HashMap<>(4);
        KeyPair keyPair = getKeyPair(seedStr, algorithm);
        keyMap.put(PUBLIC_KEY, getPublicKey(keyPair));
        keyMap.put(PRIVATE_KEY, getPrivateKey(keyPair));
        return keyMap;
    }

    /**
     * 将数据使用公钥加密
     *
     * @param content      待加密内容
     * @param publicKeyStr 公钥字符串
     * @return byte[] 加密后的数据
     */
    public static byte[] encrypt(String content, String publicKeyStr) {
        return encrypt(content, publicKeyStr, KEY_ALGORITHM, StandardCharsets.UTF_8);
    }

    /**
     * 将数据使用公钥加密
     *
     * @param content      待加密的数据
     * @param publicKeyStr 公钥字符串
     * @param algorithm    算法
     * @param charset      字符集
     * @return String 加密后的数据
     */
    public static byte[] encrypt(String content, String publicKeyStr, String algorithm, Charset charset) {
        if (null == charset) {
            charset = StandardCharsets.UTF_8;
        }
        PublicKey publicKey = getPublicKey(publicKeyStr, algorithm);
        return publicKeyEncrypt(content.getBytes(charset), publicKey, algorithm);
    }

    /**
     * 使用私钥数据进行解密
     *
     * @param content       待解密的数据
     * @param privateKeyStr 私钥字符串
     * @return String 解密后的数据
     */
    public static String decrypt(byte[] content, String privateKeyStr) {
        return decrypt(content, privateKeyStr, KEY_ALGORITHM, StandardCharsets.UTF_8);
    }

    /**
     * 使用私钥数据进行解密
     *
     * @param content       待解密的数据
     * @param privateKeyStr 私钥字符串
     * @param algorithm     算法
     * @param charset       字符集
     * @return String 解密后的数据
     */
    public static String decrypt(byte[] content, String privateKeyStr, String algorithm, Charset charset) {
        if (null == charset) {
            charset = StandardCharsets.UTF_8;
        }
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        byte[] byteArr = privateKeyDecrypt(content, privateKey, algorithm);
        return new String(byteArr, charset);
    }

    /**
     * 生成密钥对：密钥对中包含公钥和私钥
     *
     * @param seedStr 种子字符串
     * @return KeyPair 包含 RSA 公钥与私钥的 keyPair
     */
    private static KeyPair getKeyPair(String seedStr) {
        return getKeyPair(seedStr, KEY_ALGORITHM);
    }

    /**
     * 生成密钥对：密钥对中包含公钥和私钥
     *
     * @param seedStr   种子字符串
     * @param algorithm 算法
     * @return KeyPair 包含 RSA 公钥与私钥的 keyPair
     */
    private static KeyPair getKeyPair(String seedStr, String algorithm) {
        if (null == algorithm || "".equals(algorithm)) {
            algorithm = KEY_ALGORITHM;
        }

        try {
            // 获得RSA密钥对的生成器实例
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            if (null == seedStr || "".equals(seedStr)) {
                seedStr = String.valueOf(System.currentTimeMillis());
            }
            // 说的一个安全的随机数
            SecureRandom secureRandom = new SecureRandom(seedStr.getBytes(StandardCharsets.UTF_8));
            // 初始化密钥对生成器，密钥大小为512、1024、2048
            keyPairGenerator.initialize(512, secureRandom);
            // 获得密钥对
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("生成秘钥对时，不存在 %s 这种算法", algorithm));
        } catch (Exception e) {
            throw new RuntimeException("生成秘钥对失败", e);
        }
    }

    /**
     * 获取公钥 (并进行 Base64 编码，返回一个 Base64 编码后的字符串)
     *
     * @param keyPair 秘钥对
     * @return 返回一个 Base64 编码后的公钥字符串
     */
    private static String getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 获取私钥(并进行 Base64 编码，返回一个 Base64 编码后的字符串)
     *
     * @param keyPair 秘钥对
     * @return 返回一个 Base64 编码后的私钥字符串
     */
    private static String getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将 Base64 编码后的公钥转换成 PublicKey 对象
     *
     * @param publicKeyStr 公钥字符串
     * @return PublicKey 公钥对象
     */
    public static PublicKey getPublicKey(String publicKeyStr) {
        return getPublicKey(publicKeyStr, KEY_ALGORITHM);
    }

    /**
     * 将 Base64 编码后的公钥转换成 PublicKey 对象
     *
     * @param publicKeyStr 公钥字符串
     * @param algorithm    算法
     * @return PublicKey 公钥对象
     */
    private static PublicKey getPublicKey(String publicKeyStr, String algorithm) {
        if (null == algorithm || "".equals(algorithm)) {
            algorithm = KEY_ALGORITHM;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("获取公钥时，不存在 %s 这种算法", algorithm));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(String.format("获取公钥时，公钥 %s 非法", publicKeyStr));
        } catch (Exception e) {
            throw new RuntimeException("获取公钥失败", e);
        }
    }

    /**
     * 将 Base64 编码后的私钥转换成 PrivateKey 对象
     *
     * @param privateKeyStr 私钥字符串
     * @return PrivateKey 私钥对象
     */
    public static PrivateKey getPrivateKey(String privateKeyStr) {
        return getPrivateKey(privateKeyStr, KEY_ALGORITHM);
    }

    /**
     * 将 Base64 编码后的私钥转换成 PrivateKey 对象
     *
     * @param privateKeyStr 私钥字符串
     * @return PrivateKey 私钥对象
     */
    private static PrivateKey getPrivateKey(String privateKeyStr, String algorithm) {
        if (null == algorithm || "".equals(algorithm)) {
            algorithm = KEY_ALGORITHM;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("获取公钥时，不存在 %s 这种算法", algorithm));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(String.format("获取公钥时，公钥 %s 非法", privateKeyStr));
        } catch (Exception e) {
            throw new RuntimeException("获取公钥失败", e);
        }
    }

    /**
     * 公钥加密
     *
     * @param content   待加密的内容 byte[]
     * @param publicKey 加密所需的公钥对象 PublicKey
     * @return 加密后的字节数组 byte[]
     */
    private static byte[] publicKeyEncrypt(byte[] content, PublicKey publicKey) {
        return publicKeyEncrypt(content, publicKey, KEY_ALGORITHM);
    }

    /**
     * 公钥加密
     *
     * @param content   待加密的内容 byte[]
     * @param publicKey 加密所需的公钥对象 PublicKey
     * @param algorithm 算法
     * @return 加密后的字节数组 byte[]
     */
    private static byte[] publicKeyEncrypt(byte[] content, PublicKey publicKey, String algorithm) {
        if (null == algorithm || "".equals(algorithm)) {
            algorithm = KEY_ALGORITHM;
        }
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("公钥加密时，不存在 %s 这种算法", algorithm));
        } catch (NoSuchPaddingException | BadPaddingException e) {
            throw new RuntimeException("公钥加密时，传入的待加密内容无效");
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("公钥加密时，传入的待加密内容长度不会");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(String.format("公钥加密时，秘钥 %s 无效", publicKey));
        }
    }

    /**
     * 私钥解密
     *
     * @param content    待解密的内容 byte[]
     * @param privateKey 解密需要的私钥对象 PrivateKey
     * @return 解密后的字节数组 byte[]
     */
    private static byte[] privateKeyDecrypt(byte[] content, PrivateKey privateKey) {
        return privateKeyDecrypt(content, privateKey, KEY_ALGORITHM);
    }

    /**
     * 私钥解密
     *
     * @param content    待解密的内容 byte[]
     * @param privateKey 解密需要的私钥对象 PrivateKey
     * @param algorithm  算法
     * @return 解密后的字节数组 byte[]
     */
    private static byte[] privateKeyDecrypt(byte[] content, PrivateKey privateKey, String algorithm) {
        if (null == algorithm || "".equals(algorithm)) {
            algorithm = KEY_ALGORITHM;
        }
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("私钥解密时，不存在 %s 这种算法", algorithm));
        } catch (NoSuchPaddingException | BadPaddingException e) {
            throw new RuntimeException("私钥解密时，传入的待加密内容无效");
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("私钥解密时，传入的待加密内容长度不会");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(String.format("私钥解密时，秘钥 %s 无效", privateKey));
        }
    }

    /**
     * 签名
     *
     * @param content    内容
     * @param privateKey 私钥字符串
     * @return String 签名数据
     */
    public static String sign(String content, String privateKey) {
        return sign(content, privateKey, StandardCharsets.UTF_8, SIGN_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param content    内容
     * @param privateKey 私钥字符串
     * @param charset    字符集
     * @param algorithm  签名算法
     * @return String 签名数据
     */
    public static String sign(String content, String privateKey, Charset charset, String algorithm) {
        try {
            PrivateKey priKey = RSAUtil.getPrivateKey(privateKey);
            Signature signature = Signature.getInstance(algorithm);

            signature.initSign(priKey);
            if (charset != null) {
                signature.update(content.getBytes(charset));
            } else {
                signature.update(content.getBytes());
            }
            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("签名时，不存在 %s 这种算法", algorithm));
        } catch (SignatureException e) {
            throw new RuntimeException("签名时，获取签名异常", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("签名时，秘钥无效", e);
        }
    }

    /**
     * 验证签名
     *
     * @param content   内容
     * @param sign      签名
     * @param publicKey 公钥
     * @return boolean true：成功、false：失败
     */
    public static boolean verifySign(String content, String sign, String publicKey) {
        return verifySign(content, sign, publicKey, StandardCharsets.UTF_8, SIGN_ALGORITHM);
    }

    /**
     * 验证签名
     *
     * @param content   内容
     * @param sign      签名
     * @param publicKey 公钥
     * @param charset   字符集
     * @param algorithm 签名算法
     * @return boolean true：成功、false：失败
     */
    public static boolean verifySign(String content, String sign, String publicKey, Charset charset, String algorithm) {
        try {
            PublicKey pubKey = RSAUtil.getPublicKey(publicKey);
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pubKey);
            if (charset != null) {
                signature.update(content.getBytes(charset));
            } else {
                signature.update(content.getBytes());
            }
            return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("验证签名时，不存在 %s 这种算法", algorithm));
        } catch (SignatureException e) {
            throw new RuntimeException("验证签名时，获取签名异常", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("签名时，秘钥无效", e);
        }
    }
}
