package com.glaway.mpm.pbombuilder.license;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * <p>
 * 封装同RSA非对称加密算法有关的方法，可用于数字签名，RSA加密解密
 * </p>
 * 
 * @Copyright:WDSsoft
 */

public class RSATool {
	public static final String PRODUCT = "product";//product:3DMPM;version:1.2;mac:123456;cpu:intell;hd:sd;expiry:2013-05-30;
	public static final String VERSION = "version";
	public static final String MAC_ADDRESS = "mac";
	public static final String CPU = "cpu";
	public static final String HARD_DISK = "hd";
	public static final String EXPIRY = "expiry";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	
	public RSATool() {
	}

	/**
	 * 使用私钥加密数据 用一个已打包成byte[]形式的私钥加密数据，即数字签名
	 * 
	 * @param keyInByte
	 *            打包成byte[]的私钥
	 * @param source
	 *            要签名的数据，一般应是数字摘要
	 * @return 签名 byte[]
	 */
	public static byte[] sign(byte[] keyInByte, byte[] source) {
		try {
			PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(keyInByte);
			KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initSign(privKey);
			sig.update(source);
			return sig.sign();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 验证数字签名
	 * 
	 * @param keyInByte
	 *            打包成byte[]形式的公钥
	 * @param source
	 *            原文的数字摘要
	 * @param sign
	 *            签名（对原文的数字摘要的签名）
	 * @return 是否证实 boolean
	 */
	public static boolean verify(byte[] keyInByte, byte[] source, byte[] sign) {
		try {
			KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
			Signature sig = Signature.getInstance("SHA1withRSA");
			X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(keyInByte);
			PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
			sig.initVerify(pubKey);
			sig.update(source);
			return sig.verify(sign);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 建立新的密钥对，返回打包的byte[]形式私钥和公钥
	 * 
	 * @return 
	 *         包含打包成byte[]形式的私钥和公钥的object[],其中，object[0]为私钥byte[],object[1]为公钥byte
	 *         []
	 */
	public static Object[] giveRSAKeyPairInByte() {
		KeyPair newKeyPair = creatmyKey();
		if (newKeyPair == null)
			return null;
		Object[] re = new Object[2];
		if (newKeyPair != null) {
			PrivateKey priv = newKeyPair.getPrivate();
			byte[] b_priv = priv.getEncoded();
			PublicKey pub = newKeyPair.getPublic();
			byte[] b_pub = pub.getEncoded();
			re[0] = b_priv;
			re[1] = b_pub;
			return re;
		}
		return null;
	}

	/**
	 * 新建密钥对
	 * 
	 * @return KeyPair对象
	 */
	public static KeyPair creatmyKey() {
		KeyPair myPair;
		long mySeed;
		mySeed = System.currentTimeMillis();
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			random.setSeed(mySeed);
			keyGen.initialize(1024, random);
			myPair = keyGen.generateKeyPair();
		} catch (Exception e1) {
			return null;
		}
		return myPair;
	}

	/**
	 * 使用RSA公钥加密数据
	 * 
	 * @param pubKeyInByte
	 *            打包的byte[]形式公钥
	 * @param data
	 *            要加密的数据
	 * @return 加密数据
	 */
	public static byte[] encryptByRSA(byte[] pubKeyInByte, byte[] data) {
		try {
			KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(pubKeyInByte);
			PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用RSA私钥解密
	 * 
	 * @param privKeyInByte
	 *            私钥打包成byte[]形式
	 * @param data
	 *            要解密的数据
	 * @return 解密数据
	 */
	public static byte[] decryptByRSA(byte[] privKeyInByte, byte[] data) {
		try {
			PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(
					privKeyInByte);
			KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
//	public static void saveByteFile(String fileName, byte[] priKey)
//	{
//		OutputStream outputStream;
//		try{
//			File file = new File("d:\\" +  fileName);
//			System.out.println(file.getPath());
//			outputStream = new FileOutputStream(file);
//			outputStream.write(priKey);
//			outputStream.close();
////			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
////			objectOutputStream.writeObject(priKey);
//		}catch(IOException e)
//		{
//			System.out.print("私钥保存失败！");
//			e.printStackTrace();
//		}
//	}
//	
	
	
	
	public static String readFile(String path)
	{
		Reader reader;
		try{
			reader = new InputStreamReader(new FileInputStream(path));
			BufferedReader bufferReader = new BufferedReader(reader);
			StringBuffer sb = new StringBuffer();
			String rLine ="";
			while((rLine = bufferReader.readLine()) != null)
			{
				sb.append(rLine);
			}
			return sb.toString();
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static void writeFile(String path, String value)
	{
		try{
			OutputStream outputStream;
			File file = new File(path);
			System.out.println(file.getPath());
			outputStream = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
			writer.write(value);
			writer.flush();
			
			outputStream.close();
			writer.close();
			
		}catch(Exception e){
			System.out.println("文件写入失败。");
			e.printStackTrace();
		}
	}
	
	/*
	 * 结构化license 字符串
	 */
	public static Map<String, String> parseLienseContent(String str)
	{
		Map<String, String> result = new HashMap<String, String>();
		String[] temp = str.split(";");
		for(int i = 0; i < temp.length; i++)
		{
			String[] unitTemp = temp[i].split(":");
			if(unitTemp.length == 2)
			{
				result.put(unitTemp[0], unitTemp[1]);
			}
		}
		return result;
	}
	
	
//	public static void read
	/*
	 * 生成私钥公钥对
	 */
	public static void generatePairKeys()
	{
		
	}
	/*
	 * 生成license申请文件
	 * 
	 */
//	public static void generateLicenseApplyFile(String saveApplyFilepath, String publicKeyPath) throws Exception //
//	{
//		DateFormat df = new SimpleDateFormat(RSATool.DATE_FORMAT);
//		// 获得摘要
//		String src = "product:3DMPM;version:1.2"; //;mac:123456;cpu:intell;hd:sd;expiry:2013-05-06
//		
////		System.out.println(src);
//		
//		String cpu = SystemInfor.getCPUSerial();
//		src = src + ";cpu:" + cpu;
//		
//		
//		String hd = SystemInfor.getHDSerial();
//		src = src + ";hd:" + hd;
//		
//		
//		String mac = SystemInfor.getMACAddress();
//		src = src + ";mac:" + mac;
//		
//		Calendar  calendar = Calendar.getInstance();
//		calendar.add(Calendar.MONTH, 1);
//		String strExpiry = df.format(calendar.getTime());
//		src = src + ";expiry:" + strExpiry;
//		
//		//AES 加密
//		byte[] btAES = src.getBytes("utf-8");//AESUtil.encrypt(src, "glaway.com");
//		// 使用私钥对摘要进行加密 获得密文 即数字签名
//		// 使用公钥对摘要进行加密 获得密文
//		String strPublicKey = readFile(publicKeyPath);
//		byte[] btPublicKey = Base64.altBase64ToByteArray(strPublicKey);
//		
//		byte[] sign = encryptByRSA(btPublicKey, btAES);
//					
////		byte[] sign = sign(btPrivateKey, btAES);
//		
//		//生成license申请 文件
//		writeFile(saveApplyFilepath, Base64.byteArrayToAltBase64(sign));
//	}
	
	
	/*
	 * 生成license申请文件
	 * 
	 */
	public static void generateLicenseFile(String saveLicenseFilepath, String publicKeyPath, String license) throws Exception //
	{
		DateFormat df = new SimpleDateFormat(RSATool.DATE_FORMAT);
		
		//AES 加密
		byte[] btAES = license.getBytes("utf-8");//AESUtil.encrypt(src, "glaway.com");
		// 使用私钥对摘要进行加密 获得密文 即数字签名
		// 使用公钥对摘要进行加密 获得密文
		String strPublicKey = readFile(publicKeyPath);
		byte[] btPublicKey = Base64.altBase64ToByteArray(strPublicKey);
		
		byte[] sign = encryptByRSA(btPublicKey, btAES);
					
//		byte[] sign = sign(btPrivateKey, btAES);
		
		//生成license 文件
		writeFile(saveLicenseFilepath, Base64.byteArrayToAltBase64(sign));
	}
	
	
	
	public static void main(String[] args) {
		Object[] v = giveRSAKeyPairInByte();
		
//		String temp = Base64.byteArrayToAltBase64((byte[])v[0]);
//		System.out.println(temp.length());
		
		//将公私钥保存如文件
		writeFile("d:\\licenseprivatekey.key", Base64.byteArrayToAltBase64((byte[])v[0]));
		writeFile("d:\\licensepublickey.key", Base64.byteArrayToAltBase64((byte[])v[1]));
		
		
		//读取私钥
		String strPrivateKey = readFile("d:\\applyprivatekey.key");
//		System.out.println(strPrivateKey.equals(temp));
		byte[] btPrivateKey = Base64.altBase64ToByteArray(strPrivateKey);
		
		
		//读取公钥
		String strPublicKey = readFile("d:\\applypublickey.key");
		byte[] btPublicKey = Base64.altBase64ToByteArray(strPublicKey);
		
		
	}
	/**
	 * 测试
	 */
//	public static void main2(String[] args) {
//		try {
//			// AES 加密
////			SecretKey aesKey = AESUtil.generateSecretKey();
//
////			writeFile("d:\\test", "douzhang张南京鼓楼区ssss909");
////			
////			String value = readFile("d:\\test");
////			System.out.println(value);
//			// 私钥加密 公钥解密
//			// 生成私钥-公钥对
//			Object[] v = giveRSAKeyPairInByte();
//			
////			String temp = Base64.byteArrayToAltBase64((byte[])v[0]);
////			System.out.println(temp.length());
//			
//			//将公私钥保存如文件
//			writeFile("d:\\applyprivatekey.key", Base64.byteArrayToAltBase64((byte[])v[0]));
//			writeFile("d:\\applypublickey.key", Base64.byteArrayToAltBase64((byte[])v[1]));
//			
//			
//			//读取私钥
//			String strPrivateKey = readFile("d:\\applyprivatekey.key");
////			System.out.println(strPrivateKey.equals(temp));
//			byte[] btPrivateKey = Base64.altBase64ToByteArray(strPrivateKey);
//			
//			
//			//读取公钥
//			String strPublicKey = readFile("d:\\applypublickey.key");
//			byte[] btPublicKey = Base64.altBase64ToByteArray(strPublicKey);
//			
//			
//			
//			DateFormat df = new SimpleDateFormat(RSATool.DATE_FORMAT);
//			
//			
//			// 获得摘要
//			String src = "product:3DMPM;version:1.2;mac:123456;cpu:intell;hd:sd";
//			
//			Calendar  calendar = Calendar.getInstance();
//			calendar.add(Calendar.MONTH, 1);
//			String strExpiry = df.format(calendar.getTime());
//			
//			src = src + ";expiry:" + strExpiry;
//			System.out.println(strExpiry);
//			
//			SystemInfor.getCPUSerial();
//			
//			Sigar sigar = new Sigar();
//			org.hyperic.sigar.FileSystem[] filesystems = sigar.getFileSystemList();
//			System.out.println(filesystems[0].getDevName());
//			SystemInfor.getHDSerial();
//			
//			
//			InetAddress ia = InetAddress.getLocalHost();
//			SystemInfor.getMACAddress();
//			
//			
//			
//			byte[] btAES = src.getBytes("utf-8");//AESUtil.encrypt(src, "macip");
//			// 使用私钥对摘要进行加密 获得密文 即数字签名
//			// 使用公钥对摘要进行加密 获得密文
//						byte[] sign = encryptByRSA(btPublicKey, btAES);
//						
////			byte[] sign = sign(btPrivateKey, btAES);
//			
//			//生成license 文件
//			writeFile("d:\\license", Base64.byteArrayToAltBase64(sign));
//			
//			//读取licensed文件
//			String strLicense =readFile("d:\\license");
//			byte[] btLicense = Base64.altBase64ToByteArray(strLicense);
//			
//			// 使用私钥对密文进行解密 返回解密后的数据
//			byte[] bttextLicense = decryptByRSA(btPrivateKey, btLicense);
//			
//			
//			System.out.println("license 内容===========================");
//			System.out.println(new String(bttextLicense, "utf-8"));
//			
//			Map licenseMap = parseLienseContent(new String(bttextLicense, "utf-8"));
//			
//			System.out.println(licenseMap.get(RSATool.PRODUCT));
//			System.out.println(licenseMap.get(RSATool.VERSION));
//			System.out.println(licenseMap.get(RSATool.CPU));
//			System.out.println(licenseMap.get(RSATool.HARD_DISK));
//			System.out.println(licenseMap.get(RSATool.MAC_ADDRESS));
//			System.out.println(licenseMap.get(RSATool.EXPIRY));
//			
//			
//			
//			
//			
//			
//			
//			Date today = new Date();
//			Date expiryDate = df.parse("2013-05-30");
//			
//			if(expiryDate.getTime() < today.getTime())
//			{
//				System.out.println("软件已经过期。");
//			}
//			
//			
////			saveByteFile("license", sign);
//			
//			// 使用公钥对密文进行解密,解密后与摘要进行匹配
//			boolean yes = verify((byte[]) v[1], btAES, sign);
//			if (yes)
//				System.out.println("1. 匹配成功 合法的签名!");
//			
//			
//			//System.out.print(new String(sign,"utf-8"));
//			
//
//			// 公钥加密 私钥解密
//			// 生成私钥-公钥对
//			KeyPair kp = creatmyKey();
//			// 获得摘要
//			byte[] source1 = "double".getBytes("utf-8");// Digest.MdigestSHA("假设这是要加密的客户数据");
//			source1 = AESUtil.encrypt("doublzhang", "123");
//			// 使用公钥对摘要进行加密 获得密文
//			byte[] sign1 = encryptByRSA(kp.getPublic().getEncoded(), source1);
//			// 使用私钥对密文进行解密 返回解密后的数据
//			byte[] newSource1 = decryptByRSA(kp.getPrivate().getEncoded(),
//					sign1);
//			
//			
//			// 对比源数据与解密后的数据
//			if (Arrays.equals(source1, newSource1)) {
//				System.out.print("匹配成功 合法的私钥!");
//				System.out.print(new String(AESUtil.decrypt(newSource1, "123"), "utf-8"));
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
}
