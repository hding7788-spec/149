package com.bjsasc.avidm.mq.fileserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class FSUtil {
	protected final static Logger logger = Logger.getLogger(FSUtil.class);

	public final static String AES = "AES";

	public final static String ALGORITHM_AES = "AES/CBC/PKCS5Padding";

	public final static String FILE_ID = "file_id";

	public final static String KEY = "key";

	public final static String IVKEY = "ivkey";

	public final static String BOUNDARY_PREFIX = "--";

	public final static String CRLF = "\r\n";

	public static final long KB = 1024;

	public static final long MB = KB * 1024;

	public static final long GB = MB * 1024;

	protected static String displayFileSize(long size) {
		if (size >= GB) {
			return String.format("%.1fGB", (float) size / GB);
		} else if (size >= MB) {
			float value = (float) size / MB;
			return String.format(value > 100 ? "%.0fMB" : "%.1fMB", value);
		} else if (size >= KB) {
			float value = (float) size / KB;
			return String.format(value > 100 ? "%.0fKB" : "%.1fKB", value);
		} else {
			return String.format("%dB", size);
		}
	}

	protected static String generateMultipartBoundary() {
		final String prefix = "----------";
		Random rand = new Random();
		String boundary = "";

		try {

			final byte[] MULTIPART_CHARS = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
					.getBytes("US-ASCII");

			byte[] bytes = new byte[rand.nextInt(11) + 30]; // a random size
															// from 30 to 40
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)];
			}

			boundary = prefix + new String(bytes, "US-ASCII");
		} catch (Exception e) {
			String s = "产生Http Multipart Boundary发生错误";
			logger.error(s, e);
			throw new RuntimeException(s);
		}

		return boundary;
	}

	/**
	 * 加密上传文件
	 * 
	 * @param file		本地文件地址
	 * @param endpoint	上传文件的REST地址，如：http://10.0.1.117:8080/avidm/rest/dc/attach/upload
	 * @return	如未发生异常，则返回JSON对象，里头的内容为：
	 * 			file_id	文件服务器返回的唯一标识
	 * 			key		AES加密的key
	 * 			ivkey	AES加密的ivkey	
	 * 		
	 */
	public static JSONObject upload(String file, String endpoint) {
		JSONObject result = null;

		File f = new File(file);
		if (!f.exists()) {
			String s = "指定的本地文件不存在：" + file;
			logger.error(s);
			throw new RuntimeException(s);
		}

		Map<String, String> m = new HashMap<String, String>();
		HttpURLConnection connection = null;
		OutputStream os = null;
		InputStream is = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		CipherInputStream cis = null;

		try {
			logger.debug("开始加密上传文件：" + f.getAbsolutePath());
			logger.debug("要上传文件的大小为：" + displayFileSize(f.length()));
			long b = System.currentTimeMillis();

			Cipher cipher = initCipher(m);
			connection = initUploadConnection(endpoint);

			// 设置boundary
			String boundary = generateMultipartBoundary();
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			// 开始输出文件流
			os = connection.getOutputStream();

			writeLine(os, BOUNDARY_PREFIX + boundary);

			String content = "Content-Disposition: form-data; name=\"file\"; filename=\"" + f.getName() + "\"";
			writeLine(os, content);

			content = "Content-Type: application/octet-stream";
			writeLine(os, content);

			content = "Content-Transfer-Encoding: binary";
			writeLine(os, content);

			// 写文件前空行
			os.write(CRLF.getBytes());
			os.flush();

			// 开始写入文件
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			cis = new CipherInputStream(bis, cipher);

			byte[] buffer = new byte[8192];
			int len = cis.read(buffer);
			while (len > 0) {
				os.write(buffer, 0, len);
				len = cis.read(buffer);
			}

			os.write(CRLF.getBytes());
			os.flush();

			// boundary结束行
			content = BOUNDARY_PREFIX + boundary + BOUNDARY_PREFIX;
			writeLine(os, content);

			// flush
			os.flush();
			os.close();

			// 如果结果不为HTTP_OK，则抛异常
			int status = connection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				is = connection.getInputStream();
				StringWriter sw = new StringWriter();
				IOUtils.copy(is, sw, "UTF-8");
				String resp = sw.toString();
				result = new JSONObject(resp);
			} else {
				is = connection.getErrorStream();
				StringWriter sw = new StringWriter();
				IOUtils.copy(is, sw, "UTF-8");
				String resp = sw.toString();
				String s = "服务器返回错误信息为：" + resp;

				throw new RuntimeException(s);
			}

			result.put(KEY, m.get(KEY));
			result.put(IVKEY, m.get(IVKEY));

			long e = System.currentTimeMillis() - b;
			logger.debug("文件上传完成，耗时：" + e + "ms");
		} catch (Exception e) {
			String s = "加密上传文件过程中发生错误";
			logger.error(s, e);
			throw new RuntimeException(s, e);
		} finally {
			close(os);
			close(is);
			close(connection);
			close(cis);
			close(bis);
			close(fis);
		}

		return result;
	}

	/**
	 * 从文件服务器下载文件，并解密
	 * 
	 * @param localFile	保存到本地的文件，如：d:/download.zip
	 * @param endpoint	下载文件的REST地址，如：http://10.0.1.117:8080/avidm/rest/dc/attach/download
	 * @param file_id	文件的唯一标识
	 * @param key		AES加密的key
	 * @param ivkey		AES解密的ivkey
	 */
	public static void download(String localFile, String endpoint, String file_id, String key, String ivkey) {
		HttpURLConnection connection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		CipherOutputStream cos = null;

		try {
			logger.debug("开始下载文件，文件id为：" + file_id);
			logger.debug("下载到本地的文件为：" + localFile);
			long b = System.currentTimeMillis();

			Cipher cipher = initCipher(key, ivkey);
			connection = initDownloadConnection(endpoint, file_id);

			int status = connection.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				is = connection.getErrorStream();
				StringWriter sw = new StringWriter();
				IOUtils.copy(is, sw, "UTF-8");
				String resp = sw.toString();
				String s = "服务器返回错误信息为：" + resp;

				throw new RuntimeException(s);
			}

			is = connection.getInputStream();

			// 开始写入文件
			File f = new File(localFile);
			fos = new FileOutputStream(f);
			bos = new BufferedOutputStream(fos);
			cos = new CipherOutputStream(bos, cipher);

			byte[] buffer = new byte[8192];
			int len = is.read(buffer);
			while (len > 0) {
				cos.write(buffer, 0, len);
				len = is.read(buffer);
			}

			long e = System.currentTimeMillis() - b;
			logger.debug("文件下载完成，耗时：" + e + "ms");

			close(cos);
			close(bos);
			close(fos);
			logger.debug("下载的文件大小为：" + displayFileSize(f.length()));
		} catch (Exception e) {
			String s = "下载文件过程中发生错误";
			logger.error(s, e);
			throw new RuntimeException(s, e);
		} finally {
			close(cos);
			close(bos);
			close(fos);

			close(is);
			close(connection);
		}
	}

	// 加密模式
	private static Cipher initCipher(Map<String, String> m) throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(AES);
		kg.init(128);

		SecretKey sk = kg.generateKey();
		byte[] key = sk.getEncoded();
		m.put(KEY, Hex.encodeHexString(key));

		sk = kg.generateKey();
		byte[] ivk = sk.getEncoded();
		m.put(IVKEY, Hex.encodeHexString(ivk));

		SecretKeySpec spec = new SecretKeySpec(key, AES);
		IvParameterSpec iv = new IvParameterSpec(ivk);

		Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
		cipher.init(Cipher.ENCRYPT_MODE, spec, iv);

		return cipher;
	}

	// 解密模式
	private static Cipher initCipher(String key, String ivkey) throws Exception {
		SecretKeySpec spec = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), AES);
		IvParameterSpec iv = new IvParameterSpec(Hex.decodeHex(ivkey.toCharArray()));

		Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
		cipher.init(Cipher.DECRYPT_MODE, spec, iv);

		return cipher;
	}

	private static HttpURLConnection initUploadConnection(String endpoint) throws Exception {
		URL url = new URL(endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// 设置Http连接属性，有输入输出
		connection.setDoInput(true);
		connection.setDoOutput(true);

		// 不使用缓存
		connection.setUseCaches(false);

		// 使用POST方法
		connection.setRequestMethod("POST");

		// 加密模式流模式文件大小未知，使用chunked模式
		connection.setChunkedStreamingMode(4096);

		// 设置Http头
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Cache-Control", "no-cache");

		return connection;
	}

	private static HttpURLConnection initDownloadConnection(String endpoint, String file_id) throws Exception {
		if (!endpoint.endsWith("/")) {
			endpoint += "/";
		}

		endpoint += file_id;
		URL url = new URL(endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// 设置Http连接属性，有输入输出
		connection.setDoInput(true);
		connection.setDoOutput(true);

		// 不使用缓存
		connection.setUseCaches(false);

		// 使用POST方法
		connection.setRequestMethod("POST");

		// 设置Http头
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Cache-Control", "no-cache");

		return connection;
	}

	private static void writeLine(OutputStream os, String s) throws UnsupportedEncodingException, IOException {
		os.write(s.getBytes("UTF-8"));
		os.write(CRLF.getBytes());
	}

	public static void close(HttpURLConnection conn) {
		if (conn == null) {
			return;
		}

		try {
			conn.disconnect();
		} catch (Exception e) {
			String s = "关闭Http连接发生错误";
			logger.error(s, e);
		}
	}

	public static void close(Closeable closeable) {
		if (closeable == null) {
			return;
		}

		try {
			closeable.close();
		} catch (Exception e) {
			// 忽略错误
		}
	}
}
