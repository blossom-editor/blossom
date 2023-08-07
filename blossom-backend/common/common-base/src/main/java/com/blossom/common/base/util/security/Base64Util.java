package com.blossom.common.base.util.security;

import java.io.IOException;
import java.util.Base64;

/**
 * 消息编码算法
 * @author xzzz
 * @since 0.0.1
 */
public class Base64Util {

	public static String encrypt(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	public static String decrypt(String data) throws IOException {
		return new String(Base64.getDecoder().decode(data));
	}
}
