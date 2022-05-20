package com.hd123.baas.sop.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class GuanDataUtils {
  public static final String CHARSET = "UTF-8";
  public static final String RSA_ALGORITHM = "RSA";
  public static final int KEY_SIZE = 1024;

  public static Map<String, String> createKeys() {
    // 为RSA算法创建一个KeyPairGenerator对象
    KeyPairGenerator kpg;
    try {
      kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
    }

    // 初始化KeyPairGenerator对象,密钥长度
    kpg.initialize(KEY_SIZE);
    // 生成密匙对
    KeyPair keyPair = kpg.generateKeyPair();
    // 得到公钥
    Key publicKey = keyPair.getPublic();
    String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
    // 得到私钥
    Key privateKey = keyPair.getPrivate();
    String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
    Map<String, String> keyPairMap = new HashMap<String, String>();
    keyPairMap.put("publicKey", publicKeyStr);
    keyPairMap.put("privateKey", privateKeyStr);

    return keyPairMap;
  }

  public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
    // 通过X509编码的Key指令获得公钥对象
    KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
    RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    return key;
  }

  public static RSAPrivateKey getPrivateKey(String privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    // 通过PKCS#8编码的Key指令获得私钥对象
    KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
    RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    return key;
  }

  public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
    try {
      Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, privateKey);
      return Base64.encodeBase64String(
          rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
    } catch (Exception e) {
      throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
    }
  }

  public static String publicDecrypt(String data, RSAPublicKey publicKey) {
    try {
      Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, publicKey);
      return new String(
          rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()),
          CHARSET);
    } catch (Exception e) {
      throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
    }
  }

  private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
    int maxBlock = 0;
    if (opmode == Cipher.DECRYPT_MODE) {
      maxBlock = keySize / 8;
    } else {
      maxBlock = keySize / 8 - 11;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;
    byte[] buff;
    int i = 0;
    try {
      while (datas.length > offSet) {
        if (datas.length - offSet > maxBlock) {
          buff = cipher.doFinal(datas, offSet, maxBlock);
        } else {
          buff = cipher.doFinal(datas, offSet, datas.length - offSet);
        }
        out.write(buff, 0, buff.length);
        i++;
        offSet = i * maxBlock;
      }
    } catch (Exception e) {
      e.getMessage();
    }
    byte[] resultDatas = out.toByteArray();
    try {
      out.close();
    } catch (Exception e) {
      e.getMessage();
    }
    return resultDatas;
  }

  public static String toHexString(String s) {
    String str = "";
    for (int i = 0; i < s.length(); i++) {
      int ch = (int) s.charAt(i);
      str += Integer.toHexString(ch);
    }
    return str;
  }

  public static void main(String[] args) throws Exception {
    Map<String, String> keyMap = createKeys();
    String publicKey = keyMap.get("publicKey");
    String privateKey = keyMap.get("privateKey");
    System.out.println("公钥: \n\r" + publicKey);
    System.out.println("私钥： \n\r" + privateKey);
    System.out.println("私钥加密——公钥解密");
    String str = "{\"domainId\":\"mingkanghui\",\"externalUserId\":\"15869142295\",\"timestamp\":"
        + System.currentTimeMillis() / 1000 + "}";
    System.out.println("\r明文：\r\n" + str);
    privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKl+0Tihp4YY9KusCCSICidm32VW/IM07TDZK1GuhSUuci1zvZzt+82xqiAnneJ0Z7sPwBflV9qDFr/0RIWKDMn00HYkIvLikuWWpNctNbczPem+WVYJA+CZbdzK/TJcOhPvkMnGQUyy1X5LfIy0N8cbh1uBIbaFRcxWoGLt4aQnAgMBAAECgYA4G9oo1CK/2n0MY3uilEJAPubPBle7G4c1d37GoG5FG7YCY+EuFP4ZXqoB0PuMHprEKvedMXe+x0x9qOASENwgaKP0K2+IxhOglJs3dfGz6kzjofd1LdhvGhuDUDVWaREcjxIIvbXqTNql32gxWEpXCJAZ88Gdn50sWCnLwlHLgQJBAN8mLTG/7Y0psiV2zNnjQR4e2sHRi54MopHz+2TsVh0uqZNC2MAVwQRogjOQ5LrRwF8LQuZKfWF1eNIlI15dEucCQQDCcpdmblioXybEJdCiM++6atm2G3QfBp5GaOg78bN/CpgJOc0bqXBwH2Ex3iv2edKj/42meAFAqsR5eG3TrfzBAkEAl+JFOqp3BvENZ0CQN/HdPaIkpW16CU5yTMNzJgNSrbQ4CZqjK0LjSJvVm0GQ3bOsq0Rf+Z1T78TkQqyygST6mwJAEH5V10tu28FOcX7fppKPOBnOI8NKY0NVc5V8dXE4D4Ofh9DOVBVYQzp2LRuyUPLeaijIJCGzwX96sO8FKdptQQJBALk+VCXwyYE8Tjym3TuS26Uy75XGW+5PrNphxMUVyFgHfPjZe1Nav1/8X+EpO5ycYQJuVXeNTnmFWbvoGWuIM1w=";
    privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIN9BM7itY03/Xsp75bae9X6WpAsQCVuMjlABsLadkhDQ3QU9S2TVAIowaxxtKZWA3X+/74o6TJ/+4bYQTVQX8FPrRoLS5TV8wekNwYuBCQUxSn4QaCkmjDJFqieBnCTqvQ0Ys+aWdQT9wxcA9FCzZ5jc/rR+LBJicjkReXI92LLAgMBAAECgYBSpgChsisACxOPmTp6DfcM9wJ54PapEG0NQ0t8qyjyHyWFD69U6GhDuy6RoB829pXSU2iZNMnv7jZB+ihyxISK7UcR18nOdkN6atVs9dSOAkqXiuc2yuHSr0d/xLPxqjwsZ3bdGCtnEu7L7WMztDhOVJB51AX9DeLBmoUbb4+JkQJBALpmLJAUJRyfbYBozCbIJ9GafQwby3NzCbK48mZzIDyNsajRJ+BUNJGAxO990AslsQUwF0nRBBFGj/TMHs3rQB0CQQC0lfG6COzyE+Y3PtjZv0lKwdAmPHfGa9zkUDhF2ID2HpLNLHrNRXm8Xpiu/JhuoRzan+xNujdgJf4Z4QZzjYoHAkBGLWWlsQ/v4M43RD1odH8ZWRKvVl+Yzbze/ua0T6/ZXHeA2Y4r1UgdZ9+9Ux7as4wcKej6r6jaYDier6MBYAq5AkB4OtB6bJDcxzTiGTylFH8Gf61Gl8Gi4JNriqE8l12c/1P7uB52ZkgbituavV/7X9bnE8xvH4jYwdLAhhoWeuinAkAIKY0Z7RatPTXPOeChsGEceYDwoXq61DWia5241fuuwjBYLyJm5nVMiSKczm0sZO1mz4+i3qvUtLzxuhloCy6F";
    String encodedData = privateEncrypt(str, getPrivateKey(privateKey));
    System.out.println("密文：\r\n" + toHexString(encodedData));
    String decodedData = publicDecrypt(encodedData, getPublicKey(publicKey));
    System.out.println("解密后文字: \r\n" + decodedData);

    String url ="http://data.mkh.cn:9999/m/app/seb8405790be54821882a4ce?provider=mingkanghui&ssoToken=" + toHexString(encodedData);
    System.out.println(url);
  }
}
