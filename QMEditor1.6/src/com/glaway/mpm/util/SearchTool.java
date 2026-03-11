package com.glaway.mpm.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SearchTool {
	public static boolean isResult(String s, String technicsNumber,
			String technicsType, String partNumber, String productNumber) {
		String[] ss = s.split("`");
		if (!isAccord(ss[0], technicsNumber))
			return false;
		if (!ss[2].equals(technicsType))
			return false;
		if ((!partNumber.equals("")) && (!isAccord(ss[3], partNumber))) {
			return false;
		}

		return true;
	}

	public static boolean isResult(String s, String productNumber,
			String productName) {
		String[] ss = s.split("`");
		if ((!productNumber.equals("")) && (!isAccord(ss[0], productNumber)))
			return false;
		if ((!productName.equals("")) && (!isAccord(ss[1], productName)))
			return false;
		return true;
	}

	private static boolean isAccord(String str, String wild) {
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
}
