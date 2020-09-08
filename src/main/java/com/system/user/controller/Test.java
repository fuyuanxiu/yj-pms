package com.system.user.controller;

import java.io.IOException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//key为abcdefghijklmnopqrstuvwx的Base64编码
		try {
			byte[] key=new BASE64Decoder().decodeBuffer("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4");
			byte[] data="2".getBytes("UTF-8");

			byte[] str3 = des3EncodeECB(key,data );
	        byte[] str4 = ees3DecodeECB(key, str3);
	        System.out.println(new BASE64Encoder().encode(str3));//加密后的结果
	        System.out.println(new String(str4, "UTF-8"));//解密后的结果
			
			String str = "1";
			Integer ikey = 8+18;
			byte[] temp = new byte[255] ;
			byte[] b = str.getBytes("iso8859-1");
			int i = 0;
			for (; i < b.length; i++) {
				temp[i] = new Integer(new Integer(b[i]).intValue() ^ (8 + 18))
						.byteValue();
			}
			String result = 8 + new String(temp);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
	}
	
	private static String proPass(String src) throws Exception {
		String result = "";
		int first = new Integer(src.substring(0, 1)).intValue();
		String src_tem = src.substring(1);
		byte[] b = src_tem.getBytes("iso8859-1");
		byte[] temp = b;
		int i = 0;
		for (; i < b.length; i++) {
			temp[i] = new Integer(new Integer(temp[i]).intValue() ^ (first + 18))
			.byteValue();
		}
		result = new String(temp);
		return result;
	}
	
	/**
	 * ECB加密,不要IV
	 * 
	 * @param key
	 *            固定密钥字节数组

	 * @param data
	 *            登录名字节数组
	 * @return   token字节数组，经Base64编码，得到token明文
	 * @throws Exception
	 */
	public static byte[] des3EncodeECB(byte[] key, byte[] data)
			throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
		
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);

        return bOut;

	}
	
	 /**
     * ECB解密,不要IV
     * @param key 密钥
     * @param data Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] ees3DecodeECB(byte[] key, byte[] data)
            throws Exception {

        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, deskey);

        byte[] bOut = cipher.doFinal(data);

        return bOut;

    }
}
