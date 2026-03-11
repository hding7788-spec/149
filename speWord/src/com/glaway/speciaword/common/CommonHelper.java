/**
 * 
 */
package com.glaway.speciaword.common;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.glaway.speciaword.component.SWObservable;

/**
 * @author MosesX
 * @2013-4-26
 * 
 * 
 *            帮助类
 * 
 */
public class CommonHelper {

//	public static String TEMP_LOCAL_IMAGE_FOLDER = "";
	public static final String SEPERATOR = "@#$%^";

	private static ResourceBundle specialCodeResource = null;
	public static final String GREEK = "Greek";
	public static final String PUNCTUATION = "Punctuation";
	public static final String MATH = "Math";
	public static final String UNIT = "Unit";
	public static final String NUMBER = "Number";

	public static SWObservable SWOBSERVABLE_INS = new SWObservable();

	public static Map<Integer, String> hashMap = new HashMap<Integer, String>();

	/**
	 * 保存或者显示的时候替换占位符
	 * 
	 * @param str
	 * @param flag
	 * @return
	 */
	public static String replaceSeperator(String str, boolean flag, String imageFolder) {
		if (str != null && imageFolder != null) {
			if (flag) {
				str = str.replace(imageFolder, SEPERATOR);
			} else {
				str = str.replace(SEPERATOR, imageFolder);
			}
		}
		return str;
	}

	public static String replaceSaveSeperator(String str, String imageFolder) {
		return replaceSeperator(str, true, imageFolder);
	}

	public static String replaceReadSeperator(String str, String imageFolder) {
		return replaceSeperator(str, false, imageFolder);
	}

	public static Map<String, List<String>> symbolMap = new HashMap<String, List<String>>();

	public static List<String> getSpecWordNames(int index) {
		if (hashMap.size() == 0) {
			hashMap.put(0, "weld");
			hashMap.put(1, "condition");
			hashMap.put(2, "others");
		}

		if (symbolMap.size() == 0) {
			symbolMap = initSpeMap();
		}

		return symbolMap.get(hashMap.get(index));

	}

	/**
	 * 初始化特殊字符的Map
	 * 
	 * @return
	 */
	private static Map<String, List<String>> initSpeMap() {
		Map<String, List<String>> symbolMap = new HashMap<String, List<String>>();
		// URL url = CommonHelper.class
		// .getResource("/com/glaway/speciaword/resource/templates/specWord/");
		// File parentFile = new File(url.getFile());
		// if (parentFile.exists()) {
		// File[] parentFiles = parentFile.listFiles();
		// if (parentFiles != null && parentFiles.length != 0) {
		// for (File tempFile : parentFiles) {
		// String parentFileName = tempFile.getName();
		// File[] files = tempFile.listFiles();
		// List<String> types = new ArrayList<String>();
		// if (files != null && files.length != 0) {
		// for (File temp : files) {
		// String fileName = temp.getName();
		// if (fileName.endsWith(".svg")) {
		// types.add(fileName.substring(0,
		// fileName.lastIndexOf(".")));
		// }
		// }
		// }
		// symbolMap.put(parentFileName, types);
		// }
		//
		// }
		// }

		List<String> types = new ArrayList<String>();
		for (int i = 1; i <= 9; i++) {
			types.add("hj0" + i);

		}
		symbolMap.put("weld", types);

		types = new ArrayList<String>();
		types.add("l");
		types.add("m");
		types.add("p");
		types.add("s");

		symbolMap.put("condition", types);

		types = new ArrayList<String>();
		for (int i = 1; i <= 8; i++) {
			types.add("qt0" + i);

		}
		symbolMap.put("others", types);
		return symbolMap;
	}

	/***
	 * 计算字符串生成图片的尺寸
	 * 
	 * @param text
	 * @return
	 */
	public static Dimension calculateStringToImageSize(String text, int fontSize) {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		Font font = new Font("宋体", Font.PLAIN, fontSize);
		FontMetrics metrics = g.getFontMetrics(font);
		int height = metrics.getHeight();
		int width = metrics.stringWidth(text);

		return new Dimension(width, height);
	}

	/**
	 * 将BufferImage对象本地化
	 * 
	 * @param image
	 */
	public static String saveImageToLocal(BufferedImage image, String fileName, String imageFolder) {
		String imagePath = imageFolder + File.separator + "content";
		File imageDir = new File(imagePath);
		if (!imageDir.exists()) {
			imageDir.mkdirs();
		}
		String file = imageDir + File.separator + fileName + ".png";
		// 关闭流 否则删除图片失败
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(new File(file));
			ImageIO.write(image, "png", os);
			return new File(file).getAbsolutePath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

//	public static void setLocalImageFolder(String folder, String imageFolder) {
//		// 检测位置是否存在 不存在则生成响应的文件夹
//		File file = new File(folder);
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		TEMP_LOCAL_IMAGE_FOLDER = folder;
//	}

	/**
	 * 设置绑定属性文件
	 * 
	 * @param value
	 */
	public static void setBundles(String value) {
		specialCodeResource = PropertyResourceBundle.getBundle(value);
	}

	/***
	 * 获取属性
	 * 
	 * @param key
	 * @return
	 */
	public static String getResource(String key) {
		try {
			return specialCodeResource.getString(key);
		} catch (MissingResourceException mrex) {
		}

		return null;
	}

	public static String getSpecialWordText(String text) {
		if (text != null) {
			text = text.replaceAll("&nbsp;", " ");
			text = text.replaceAll("&amp;", "&");
			text = text.replaceAll("&#160;", " ");
			text = text.replaceAll("&lt;", "<");
			text = text.replaceAll("&gt;", ">");
			text = text.replaceAll("&quot;", "，");
			text = filterTags(text);
			text = text.replace("\n", "");
			text = text.replace(" ", "");
		}
		return text;
	}

	private static String filterTags(String text) {
		while (true) {
			if (text != null) {
				int index = text.indexOf("<");
				if (index != -1) {
					String prefix = text.substring(0, index);
					text = text.substring(index);
					int endIndex = text.indexOf(">");
					if (endIndex != -1) {
						text = text.substring(endIndex + 1);
					}
					text = prefix + text;
					return filterTags(text);
				} else {
					return text;
				}
			}
			return text;
		}
	}

	/**
	 * 获取图片
	 * 
	 * @return
	 */
	public static byte[] getResourceImage(String folderName, String imageName) {
		InputStream is = null;
		byte[] bytes = null;
		BufferedInputStream bis = null;
		try {
			is = CommonHelper.class.getResourceAsStream("/com/glaway/speciaword/resource/templates/" + folderName + "/"
					+ imageName);
			bis = new BufferedInputStream(is);
			// bytes = new byte[is.available()];
			// is.read(bytes);
			bytes = new byte[bis.available()];
			bis.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytes;
	}
}
