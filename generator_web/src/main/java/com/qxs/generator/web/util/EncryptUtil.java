package com.qxs.generator.web.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/***
 * 加密解密工具类
 * 
 * @author qixingshen
 * @date 2018-06-11
 **/
@SuppressWarnings("restriction")
public class EncryptUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtil.class);

	/**
	 * des加密
	 * 
	 * @param str
	 *            要加密的字符串
	 * @param encryptKey
	 *            加密关键字
	 * @return 加密完成的字符串
	 **/
	public static String desEncode(String str, String encryptKey) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		try {
			// 生成一个可信任的随机数源
			SecureRandom sr = new SecureRandom();

			// 从原始密钥数据创建DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(encryptKey.getBytes());

			// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(dks);

			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");

			// 用密钥初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

			return new BASE64Encoder().encode(cipher.doFinal(str.getBytes()));
		} catch (InvalidKeyException e) {
			LOGGER.error("加密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("加密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			LOGGER.error("加密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			LOGGER.error("加密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("加密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			LOGGER.error("加密失败:[{}]",e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * des解密
	 * 
	 * @param str
	 *            要解密的字符串
	 * @param encryptKey
	 *            解密关键字
	 * @return 解密完成的字符串
	 **/
	public static String desDecode(String str, String encryptKey) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] buf = decoder.decodeBuffer(str);
			// 生成一个可信任的随机数源
			SecureRandom sr = new SecureRandom();

			// 从原始密钥数据创建DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(encryptKey.getBytes());

			// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(dks);

			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance("DES");

			// 用密钥初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

			return new String(cipher.doFinal(buf));
		} catch (InvalidKeyException e) {
			LOGGER.error("解密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("解密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			LOGGER.error("解密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			LOGGER.error("解密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			LOGGER.error("解密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			LOGGER.error("解密失败:[{}]",e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOGGER.error("解密失败:[{}]",e);
			throw new RuntimeException(e);
		}
	}

}
