package com.glaway.mpm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

public class RecentSaveUtil {
	private static final String TEMP_DIR_KEY = "java.io.tmpdir";
	private static final int RECENT_SIZE = 4;
	private static final String RECENT_OPEN_FILE_NAME = "recent.txt";
	private static Vector recentOpenVector = new Vector();
	private static final int FAVORITE_SIZE = 20;
	private static final String FAVORITE_FILE_NAME = "favorite.txt";

	static {
		initRecentOpenVector();
	}

	public static void saveRecentOpenFile() throws Exception {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(getRecentOpenFile()));
			String str = "";
			for (int i = 0; i < recentOpenVector.size(); i++) {
				str = (String) recentOpenVector.elementAt(i);
				writer.write(str);
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("保存最近打开工艺的临时文件时出现错误！");
		}
	}

	public static Vector getRecentOpenVector() {
		return recentOpenVector;
	}

	public static void addtoFavorite(String technicsNumber) throws Exception {
		Vector vec = getFavoriteVector();
		if (vec.contains(technicsNumber))
			throw new Exception("要添加的工艺编号已经在收藏夹中！");
		if (vec.size() >= 20)
			throw new Exception("收藏夹已满，请先清理收藏夹中没用的数据后再添加！");
		vec.add(technicsNumber);
		addtoFavorite(vec);
	}

	public static Vector getFavoriteVector() {
		Vector vec = new Vector();
		File favoriteFile = getFavoriteFile();
		if (favoriteFile.isFile()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						favoriteFile));
				String line = "";
				while (true) {
					line = reader.readLine();
					if (line == null)
						break;
					line = line.trim();
					if (!line.equals("")) {
						vec.add(line);
					}
				}
				reader.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		return vec;
	}

	public static void addtoFavorite(Vector vec) throws Exception {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					getFavoriteFile()));
			String str = "";
			for (int i = 0; i < vec.size(); i++) {
				str = (String) vec.elementAt(i);
				writer.write(str);
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {

			e.printStackTrace();
			throw new Exception("保存收藏夹文件时出现错误！");
		}
	}

	public static void deleteRecent(String technicsNumber) {
		if (recentOpenVector.contains(technicsNumber))
			recentOpenVector.remove(technicsNumber);
	}

	private static void initRecentOpenVector() {
		File recentFile = getRecentOpenFile();
		if (recentFile.isFile()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						recentFile));
				String line = "";
				while (true) {
					line = reader.readLine();
					if (line == null)
						break;
					line = line.trim();
					if (!line.equals("")) {
						recentOpenVector.add(line);
					}
				}
				reader.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	private static File getFavoriteFile() {
		String tempDir = getTempDir();
		String fileDir = tempDir + "favorite.txt";

		return new File(fileDir);
	}

	public static void addRecent(String technicsNumber) {
		if (recentOpenVector.contains(technicsNumber))
			recentOpenVector.remove(technicsNumber);
		if (recentOpenVector.size() >= 4) {
			recentOpenVector.remove(0);
		}
		recentOpenVector.add(technicsNumber);
	}

	private static File getRecentOpenFile() {
		String tempDir = getTempDir();
		String fileDir = tempDir + "recent.txt";
		return new File(fileDir);
	}

	private static String getTempDir() {
		String tempDir = System.getProperty("java.io.tmpdir");
		return tempDir + File.separator;
	}

	public static boolean isAccord(String str, String wild) {
		if ((str == null) || (wild == null) || (str.equals(""))
				|| (wild.equals(""))) {
			return false;
		}
		if (-1 == wild.indexOf("*")) {
			if (str.equals(wild)) {
				return true;
			}
			return false;
		}

		ArrayList list = new ArrayList();
		StringTokenizer mToken = new StringTokenizer(wild, "*");
		while (mToken.hasMoreTokens())
			list.add(mToken.nextToken());
		if (list.isEmpty())
			return true;
		String head = "";
		String end = "";
		head = (String) list.get(0);
		end = (String) list.get(list.size() - 1);
		int index = 0;
		if ((wild.startsWith("*")) && (!wild.endsWith("*"))) {
			if (1 == list.size()) {
				if (str.endsWith(end)) {
					return true;
				}
				return false;
			}

			if (str.endsWith(end)) {
				return isAccordWildchar(
						str.substring(0, str.length() - end.length()),
						wild.substring(0, wild.length() - end.length()));
			}
			return false;
		}

		if ((!wild.startsWith("*")) && (wild.endsWith("*"))) {
			if (1 == list.size()) {
				if (str.startsWith(head)) {
					return true;
				}
				return false;
			}

			if (str.startsWith(head)) {
				return isAccordWildchar(str.substring(head.length()),
						wild.substring(head.length()));
			}
			return false;
		}

		if ((wild.startsWith("*")) && (wild.endsWith("*"))) {
			return isAccordWildchar(str, wild);
		}

		if ((str.startsWith(head)) && (str.endsWith(end))) {
			return isAccordWildchar(
					str.substring(head.length(), str.length() - end.length()),
					wild.substring(head.length(), wild.length() - end.length()));
		}

		return false;
	}

	private static boolean isAccordWildchar(String str, String wild) {
		ArrayList list = new ArrayList();
		StringTokenizer mToken = new StringTokenizer(wild, "*");
		while (mToken.hasMoreTokens())
			list.add(mToken.nextToken());
		String temp = "";
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			temp = (String) list.get(i);
			index = str.indexOf(temp);
			if (-1 == index)
				return false;
			try {
				str = str.substring(index + temp.length());
			} catch (StringIndexOutOfBoundsException e) {

				str = "";
			}
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		getRecentOpenFile();
	}
}
