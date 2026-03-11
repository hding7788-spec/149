/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.util;

import com.glaway.mpm.resource.Constants;
import com.glaway.mpm.sjzyk.SjzykBean;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
	static int GB_SP_DIFF = 160;
	// 存放国标一级汉字不同读音的起始区位码
	static int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635,
			3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600 };

	// 存放国标一级汉字不同读音的起始区位码对应读音
	static char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'w', 'x', 'y', 'z' };
	public static final String SPECIAL_SPACE = "　";
	public static final String[] speWords = new String[] {"β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "Δ", "Γ", "Β", "Α", "ω", "ψ", "χ", "φ", "υ", "τ", "σ",
		"ρ", "π", "ο", "Ε", "Ζ", "Η", "Θ", "Ι", "Κ", "Λ", "Μ", "Ν", "Ξ", "Ο", "Π", "Ρ", "Σ", "Τ", "Υ", "Φ", "Χ", "Ψ", "Ω", "α", "♀"};
	public String getTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date currentTime = new Date();

		return formatter.format(currentTime);

	}

	public static String trim(String str) {
		if (str == null) {
			return null;
		}
		str = str.trim();
		while (true) {
			if (str.startsWith("　") || str.startsWith(" ")) {
				str = str.substring(1, str.length());
				continue;
			} else {
				break;
			}
		}
		while (true) {
			if (str.endsWith("　") || str.endsWith(" ")) {
				str = str.substring(0, str.length() - 1);
				continue;
			} else {
				break;
			}
		}
		return str;
	}

	/**
	 * 除去所有的空格
	 *
	 * @param str
	 * @return
	 */
	public static String trimAllSpace(String str) {
		if (str == null) {
			return null;
		}
		str = str.replaceAll("　", "");
		str = str.replaceAll(" ", "");
		return str;
	}

	public static String ConvertToJsonFormat(String strData) {
		if (strData == null)
			return "";
		strData = strData.replace("&", "&amp;");
		strData = strData.replace(">", "&gt;");
		strData = strData.replace("<", "&lt;");
		strData = strData.replace("\"", "&quot;");
		strData = strData.replace("'", "&apos;");

		return strData;
	}

	public static Logger getLogger(Class<?> clazz) {
		PropertyConfigurator.configure(System.getProperty("user.dir")
				+ "/log4j.properties");
		return Logger.getLogger(clazz);
	}

	/**
	 * 将输入流写入文件
	 *
	 * @param is
	 *            输入流
	 * @param destPath
	 *            目标目录
	 */
	public static void writeInputStreamToFile(InputStream is, String destPath) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(new FileOutputStream(destPath));
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openURL(String url) {
		try {
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + url);
		} catch (IOException e) {
			e.printStackTrace();
			SwingUtil.showMessageDialog("您的浏览器不支持", Constants.TIP, 1);
		}
	}

	public static String filterUnit(String str) {
		if (str == null) {
			return null;
		}
		str = trimAllSpace(str);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ((c >= '0' && c <= '9') || c == '.') {
				continue;
			} else {
				return str.substring(0, i);
			}
		}
		return str;
	}

	public static String toString(Class clazz, Object object) {
		String result = "";
		for (Field field : clazz.getDeclaredFields()) {
			try {
				String name = field.getName();
				if (!"serialVersionUID".equals(name)) {
					field.setAccessible(true);
					result += field.getName() + "=" + field.get(object) + ",";
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if (result.length() != 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();
	 }

	public static JComboBox getDWJComboBox() {
		JComboBox comboBox = new JComboBox();
        comboBox.addItem("个");
        comboBox.addItem("只");
        comboBox.addItem("件");
		comboBox.addItem("毫米");
		comboBox.addItem("升");
		comboBox.addItem("毫升");
		comboBox.addItem("立方米");
		comboBox.addItem("平方米");
		comboBox.addItem("米");
        comboBox.addItem("克");
        comboBox.addItem("千克");
        comboBox.addItem("磅(lb)");
        comboBox.addItem("英寸(inch)");
        return comboBox;
	}

	public static JComboBox getDepartmentJComboBox() {
		JComboBox comboBox = new JComboBox();
        comboBox.addItem("1");
        comboBox.addItem("2");
        comboBox.addItem("3");
        comboBox.addItem("4");
        comboBox.addItem("5");
        comboBox.addItem("6");
        comboBox.addItem("7");
        comboBox.addItem("8");
        comboBox.addItem("项");
        return comboBox;
	}

	/**
	 * 检验工艺数量填写
	 * @param str
	 * @param unit
	 * @return
	 */
	public static boolean checkGysl(String str, String unit){
		boolean isMatch;
		String zzs = "^[1-9]\\d*$";
		String xs = "^[0-9]+(.[0-9]{1,3})?$";
		if("个".equals(unit) || "只".equals(unit) || "件".equals(unit)){
			isMatch = Pattern.matches(zzs, str);
		}else{
			isMatch = Pattern.matches(xs, str);
		}
		return isMatch;
	}
	public static boolean isDouble(String input) {
		if((input == null) || ("".equals(input))) {
			return false;
		}
		return (input.matches("\\d+\\.\\d*") || input.matches("\\d+"));
	}

	public static boolean checkIsNumeric(TableModel tm, int[] cols) {
		int rows = tm.getRowCount();
		for(int i=0;i<rows;i++){
			for(int col:cols) {
				String num = objectToString(tm.getValueAt(i, col));
				boolean b = CommonUtil.isDouble(num);
				if(!b) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean checkValueIsNull(TableModel tm, int[] cols) {
		int rows = tm.getRowCount();
		for(int i=0;i<rows;i++){
			for(int col:cols) {
				String value = objectToString(tm.getValueAt(i, col));
				if(value == null || "".equals(value)) {
					return false;
				}
			}
		}
		return true;
	}

	public static String formatType(String type) {
    	if("BZJ".equals(type)) {
    		return "标准件";
    	} else if ("YQJ".equals(type)) {
    		return "元器件";
    	} else {
    		return type;
    	}
    }

	public static String formatTypeString(String type) {
    	if("标准件".equals(type)) {
    		return "BZJ";
    	} else if ("元器件".equals(type)) {
    		return "YQJ";
    	} else {
    		return type;
    	}
    }

	public static String objectToString(Object obj) {
		if(obj == null) {
			return "";
		} else if ("null".equals(obj)) {
			return "";
		} else {
			return obj.toString();
		}
	}

	public static synchronized void setTableStyle(JTable table) {
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

			private static final long serialVersionUID = -7032987399719259731L;

			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				// 设置奇数行底色。
				if (row % 2 == 0) {
					setBackground(Color.white);
				}
				// 设置偶数行底色
				else if (row % 2 == 1) {
					setBackground(new Color(206, 231, 255));

				}
				return super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
			}
		};
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
		}
	}

	public static Element createPartElement(int i,SjzykBean bean, String name) {
		Element element = DocumentHelper.createElement(name);
		XmlUtility.setAttributeValue(element, "dataType", CommonUtil.objectToString(bean.getDataType()));
		XmlUtility.setAttributeValue(element, "number", CommonUtil.objectToString(bean.getNumber()));
		XmlUtility.setAttributeValue(element, "sjbm", CommonUtil.objectToString(bean.getSjbm()));
		XmlUtility.setAttributeValue(element, "name", CommonUtil.objectToString(bean.getName()));
		XmlUtility.setAttributeValue(element, "wzjc", CommonUtil.objectToString(bean.getWzjc()));
		XmlUtility.setAttributeValue(element, "bmyyjb", CommonUtil.objectToString(bean.getBmyyjb()));
		XmlUtility.setAttributeValue(element, "bmzt", CommonUtil.objectToString(bean.getBmzt()));
		XmlUtility.setAttributeValue(element, "bmlx", CommonUtil.objectToString(bean.getBmlx()));
		XmlUtility.setAttributeValue(element, "xh", CommonUtil.objectToString(bean.getXh()));
		XmlUtility.setAttributeValue(element, "cllx", CommonUtil.objectToString(bean.getXhgg()));
		XmlUtility.setAttributeValue(element, "hsl", CommonUtil.objectToString(bean.getHsl()));
		XmlUtility.setAttributeValue(element, "ph", CommonUtil.objectToString(bean.getPh()));
		XmlUtility.setAttributeValue(element, "bzh", CommonUtil.objectToString(bean.getBzh()));
		XmlUtility.setAttributeValue(element, "gg", CommonUtil.objectToString(bean.getGg()));
		XmlUtility.setAttributeValue(element, "gyzt", CommonUtil.objectToString(bean.getGyzt()));
		XmlUtility.setAttributeValue(element, "cybz", CommonUtil.objectToString(bean.getCybz()));
		XmlUtility.setAttributeValue(element, "jd", CommonUtil.objectToString(bean.getJd()));
		XmlUtility.setAttributeValue(element, "zltz", CommonUtil.objectToString(bean.getZltz()));
		XmlUtility.setAttributeValue(element, "pzggbz", CommonUtil.objectToString(bean.getPzggbz()));
		XmlUtility.setAttributeValue(element, "cl", CommonUtil.objectToString(bean.getCl()));
		XmlUtility.setAttributeValue(element, "xhgg", CommonUtil.objectToString(bean.getXhgg()));
		XmlUtility.setAttributeValue(element, "zldj", CommonUtil.objectToString(bean.getZldj()));
		XmlUtility.setAttributeValue(element, "zgf", CommonUtil.objectToString(bean.getZgf()));
		XmlUtility.setAttributeValue(element, "xxgf", CommonUtil.objectToString(bean.getXxgf()));
		XmlUtility.setAttributeValue(element, "fzxs", CommonUtil.objectToString(bean.getFzxs()));
		XmlUtility.setAttributeValue(element, "wxcc", CommonUtil.objectToString(bean.getWxcc()));
		XmlUtility.setAttributeValue(element, "zytj", CommonUtil.objectToString(bean.getZytj()));
		XmlUtility.setAttributeValue(element, "fjxy", CommonUtil.objectToString(bean.getFjxy()));
		XmlUtility.setAttributeValue(element, "jxxndjhyd", CommonUtil.objectToString(bean.getJxxndjhyd()));
		XmlUtility.setAttributeValue(element, "bmcl", CommonUtil.objectToString(bean.getBmcl()));
		XmlUtility.setAttributeValue(element, "rcl", CommonUtil.objectToString(bean.getRcl()));
		XmlUtility.setAttributeValue(element, "cpxs", CommonUtil.objectToString(bean.getCpxs()));
		XmlUtility.setAttributeValue(element, "cpdj", CommonUtil.objectToString(bean.getCpdj()));
		XmlUtility.setAttributeValue(element, "bnxs", CommonUtil.objectToString(bean.getBnxs()));
		XmlUtility.setAttributeValue(element, "tssm", CommonUtil.objectToString(bean.getTssm()));
		XmlUtility.setAttributeValue(element, "sfjk", CommonUtil.objectToString(bean.getSfjk()));
		XmlUtility.setAttributeValue(element, "jldw", CommonUtil.objectToString(bean.getJldw()));
		XmlUtility.setAttributeValue(element, "syfw", CommonUtil.objectToString(bean.getSyfw()));
		XmlUtility.setAttributeValue(element, "gysl", CommonUtil.objectToString(bean.getGysl()));
		XmlUtility.setAttributeValue(element, "dw", CommonUtil.objectToString(bean.getDw()));
		XmlUtility.setAttributeValue(element, "sjsl", CommonUtil.objectToString(bean.getSjsl()));
		XmlUtility.setAttributeValue(element, "kzjs", CommonUtil.objectToString(bean.getKzjs()));
		XmlUtility.setAttributeValue(element, "xlcc", CommonUtil.objectToString(bean.getXlcc()));
		XmlUtility.setAttributeValue(element, "sl", CommonUtil.objectToString(bean.getSl()));
		XmlUtility.setAttributeValue(element, "sjkzjs", CommonUtil.objectToString(bean.getSjkzjs()));
		XmlUtility.setAttributeValue(element, "sjcc", CommonUtil.objectToString(bean.getSjcc()));
		XmlUtility.setAttributeValue(element, "partNumber", CommonUtil.objectToString(bean.getPartNumber()));
		XmlUtility.setAttributeValue(element, "parentPartNumber", CommonUtil.objectToString(bean.getParentPartNumber()));
		XmlUtility.setAttributeValue(element, "gys", CommonUtil.objectToString(bean.getGys()));//供应商
		XmlUtility.setAttributeValue(element, "comment", CommonUtil.objectToString(bean.getComment()));//备注
		XmlUtility.setAttributeValue(element, "dataFrom", "sjzyk");//数据来源
		XmlUtility.setAttributeValue(element, "bmdj", CommonUtil.objectToString(bean.getBmdj()));//编码等级
		XmlUtility.setAttributeValue(element, "kfzbtid", CommonUtil.objectToString(bean.getKfzbtid()));//抗辐指标TID
		XmlUtility.setAttributeValue(element, "kfzbsee", CommonUtil.objectToString(bean.getKfzbsee()));//抗辐指标SEE
		XmlUtility.setAttributeValue(element, "xncs", CommonUtil.objectToString(bean.getXncs()));//性能参数
		XmlUtility.setAttributeValue(element, "jdmgdj_state", CommonUtil.objectToString(bean.getJdmgdj_state()));//是否静电敏感
		XmlUtility.setAttributeValue(element, "jdmgdj", CommonUtil.objectToString(bean.getJdmgdj()));//经典敏感等级
		XmlUtility.setAttributeValue(element, "smdj", CommonUtil.objectToString(bean.getSmdj()));//湿敏等级
		return element;
	}

	public static SjzykBean creatSjzykBean(Element element) {
		SjzykBean bean = new SjzykBean();
		bean.setDataType(element.attributeValue("dataType"));
		bean.setNumber(element.attributeValue("number"));
		bean.setSjbm(element.attributeValue("sjbm"));
		bean.setName(element.attributeValue("name"));
		bean.setWzjc(element.attributeValue("wzjc"));
		bean.setBmyyjb(element.attributeValue("bmyyjb"));
		bean.setBmzt(element.attributeValue("bmzt"));
		bean.setBmlx(element.attributeValue("bmlx"));
		bean.setXh(element.attributeValue("xh"));
		bean.setCllx(element.attributeValue("cllx"));
		bean.setHsl(element.attributeValue("hsl"));
		bean.setPh(element.attributeValue("ph"));
		bean.setBzh(element.attributeValue("bzh"));
		bean.setGg(element.attributeValue("gg"));
		bean.setGyzt(element.attributeValue("gyzt"));
		bean.setCybz(element.attributeValue("cybz"));
		bean.setJd(element.attributeValue("jd"));
		bean.setZltz(element.attributeValue("zltz"));
		bean.setPzggbz(element.attributeValue("pzggbz"));
		bean.setCl(element.attributeValue("cl"));
		bean.setXhgg(element.attributeValue("xhgg"));
		bean.setZldj(element.attributeValue("zldj"));
		bean.setZgf(element.attributeValue("zgf"));
		bean.setXxgf(element.attributeValue("xxgf"));
		bean.setFzxs(element.attributeValue("fzxs"));
		bean.setWxcc(element.attributeValue("wxcc"));
		bean.setZytj(element.attributeValue("zytj"));
		bean.setFjxy(element.attributeValue("fjxy"));
		bean.setJxxndjhyd(element.attributeValue("jxxndjhyd"));
		bean.setBmcl(element.attributeValue("bmcl"));
		bean.setRcl(element.attributeValue("rcl"));
		bean.setCpxs(element.attributeValue("cpxs"));
		bean.setCpdj(element.attributeValue("cpdj"));
		bean.setBnxs(element.attributeValue("bnxs"));
		bean.setTssm(element.attributeValue("tssm"));
		bean.setSfjk(element.attributeValue("sfjk"));
		bean.setJldw(element.attributeValue("jldw"));
		bean.setSyfw(element.attributeValue("syfw"));
		bean.setGysl(element.attributeValue("gysl"));
		bean.setDw(element.attributeValue("dw"));
		bean.setSjsl(element.attributeValue("sjsl"));
		bean.setKzjs(element.attributeValue("kzjs"));
		bean.setXlcc(element.attributeValue("xlcc"));
		bean.setSl(element.attributeValue("sl"));
		bean.setSjkzjs(element.attributeValue("sjkzjs"));
		bean.setSjcc(element.attributeValue("sjcc"));
		bean.setPartNumber(element.attributeValue("partNumber"));
		bean.setParentPartNumber(element.attributeValue("parentPartNumber"));
		bean.setComment(element.attributeValue("comment"));
		bean.setGys(element.attributeValue("gys"));
		return bean;
	}

	public static boolean isCharOrNumeric(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean isAllNumeric(String maxLong) {
		// TODO Auto-generated method stub
		return false;
	}

	public static String convertShortcut(String str) {
		String convertStr = "";
		if(str.contains("/"))str=str.replace("/", "");
		if(str.contains("\\"))str=str.replace("\\", "");
		if(str.contains("-"))str=str.replace("-", "");
		if(str.contains("("))str=str.replace("(", "");
		if(str.contains(")"))str=str.replace(")", "");
		if(str.contains("）"))str=str.replace("）", "");
		if(str.contains("（"))str=str.replace("（", "");
		if(str.contains("."))str=str.replace(".", "");
		if(str.contains("0"))str=str.replace("0", "l");
		if(str.contains("1"))str=str.replace("1", "y");
		if(str.contains("2"))str=str.replace("2", "e");
		if(str.contains("3"))str=str.replace("3", "s");
		if(str.contains("4"))str=str.replace("4", "s");
		if(str.contains("5"))str=str.replace("5", "w");
		if(str.contains("6"))str=str.replace("6", "l");
		if(str.contains("7"))str=str.replace("7", "q");
		if(str.contains("8"))str=str.replace("8", "b");
		if(str.contains("9"))str=str.replace("9", "j");
		if (str != null && !"".equals(str)) {
			for (int i = 0; i < str.length(); i++) {
				String substr=str.substring(i, i + 1);
				char c=substr.charAt(0);
				if(('z'>=c&&c>='a')||'Z'>=c&&c>='A'){
					convertStr = convertStr+substr;
				}else{
					convertStr = convertStr + convert(substr);
				}
			}
		}
		return convertStr.toLowerCase();
	}

	public static char convert(String ch) {
		// 国标码和区位码转换常量
		byte[] bytes = new byte[2];
		char result = '-';
		try {
			bytes = ch.getBytes("GB2312");
		} catch (UnsupportedEncodingException e) {
			return 'a';
		}

		int secPosValue = 0;
		int i;
		for (i = 0; i < bytes.length; i++) {
			bytes[i] -= GB_SP_DIFF;
		}
		if(bytes.length == 2) {
			secPosValue = bytes[0] * 100 + bytes[1];
		} else {
			secPosValue = bytes[0] * 100;
		}
		for (i = 0; i < 23; i++) {
			if (secPosValue >= secPosValueList[i] && secPosValue < secPosValueList[i + 1]) {
				result = firstLetter[i];
				break;
			}
		}
		return result;
	}


	 /**
     * 按照正则表达式获取尺寸信息集合
     * @param htmlStr     需要处理的含有尺寸信息的文本
     * @return
     */
    public static List<String> getContentInfo(String htmlStr) {
        List<String> pics = new ArrayList<String>();
        String speWordStr = "";
        String speWordStr2 = "";
        //拼接特殊符号，如直径等符号
        for (String speWord : speWords) {
    	speWordStr += speWord;
    	speWordStr2 += "|" + speWord;
        }
        //提取特殊符号中字母或指定特殊符号或数字开始，特殊符号，图片，字母或数字结尾的字符串
        String regEx = "(([\\w" + speWordStr + "])+[" + speWordStr + "[^\u2E80-\u9FFF|\\(|\\)|\\（|\\）|，|,|。|♂]\\w" + speWordStr + "]+)" + speWordStr2 + "+";
        Pattern pattern = Pattern.compile(regEx);
	Matcher match = pattern.matcher(htmlStr);
	while(match.find()) {
		String fullPath = match.group().trim();
		//if (fullPath.length() > 0) {
			pics.add(fullPath);
		//}
	}
        return pics;
    }

    /**
     * 替换尺寸信息为指定符号，待打印时再替换回来
     * @param inputString
     * @return
     */
    public static String subText(String inputString) {
        String htmlStr = inputString;
        String textStr = "";

        try {
            String speWordStr = "";
            String speWordStr2 = "";
            //拼接特殊符号，如直径等符号
            for (String speWord : speWords) {
        	    speWordStr += speWord;
        	    speWordStr2 += "|" + speWord;
            }
            //提取特殊符号中字母或数字开始，特殊符号，图片，字母或数字结尾的字符串
            String regEx = "(([\\w" + speWordStr + "])+[" + speWordStr + "[^\u2E80-\u9FFF|\\(|\\)|\\（|\\）|，|,|。|♂]\\w" + speWordStr + "]+)" + speWordStr2 + "+";
            htmlStr = htmlStr.replaceAll(regEx, "◐");
            textStr = htmlStr.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textStr;// 返回文本字符串
    }
    //检验是否是大版本
	public static boolean isVersionFormat(String str) {
		if("SPACE".equals(str) || "space".equals(str)){
			return true;
		}else if(str.length() == 1){
			Pattern pattern = Pattern.compile("^[a-zA-Z]*$");
			return pattern.matcher(str).find();
		}
		return false;
	}
	//检验是否是版本
	public static boolean isVersion(String str){
		if(!str.contains(".")){
			return isVersionFormat(str);
		}else{
			String str1 = str.substring(0 , str.indexOf("."));
			String str2 = str.substring(str.indexOf(".") + 1, str.length());
			if(isVersionFormat(str1) && isInteger(str2)){
				return true;
			}
		}

		return false;
	}

	public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
  }

	public static String getNumber(String str) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	public static boolean isAllEnChar(String str) {
		boolean flag = false;
		Pattern pattern = Pattern.compile("^[a-zA-Z]*$");
		flag = pattern.matcher(str).find();
		return flag;
	}

	/**
	 * 加法运算
	 * @param m1
	 * @param m2
	 * @return
	 */
	public static double addDouble(double m1, double m2) {
		BigDecimal p1 = new BigDecimal(Double.toString(m1));
		BigDecimal p2 = new BigDecimal(Double.toString(m2));
		return p1.add(p2).doubleValue();
	}

	/**
	 * 减法运算
	 * @param m1
	 * @param m2
	 * @return
	 */
	public static double subDouble(double m1, double m2) {
		BigDecimal p1 = new BigDecimal(Double.toString(m1));
		BigDecimal p2 = new BigDecimal(Double.toString(m2));
		return p1.subtract(p2).doubleValue();
	}

	/**
	 * 零件项目分类
	 * @return
	 */
	public static JComboBox getXmflJComboBox() {
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("原材料");
		comboBox.addItem("主要材料");
		comboBox.addItem("试件原材料");
		return comboBox;
	}
	/**
	 * 装配项目分类
	 * @return
	 */
	public static JComboBox getZpXmflJComboBox() {
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("配套件");
		comboBox.addItem("主要材料");
		comboBox.addItem("试件");
		return comboBox;
	}
	public static boolean isNull(String s){
		if(s==null){
			return true;
		}
		if("".equals(s)){
			return true;
		}
		if("null".equals(s)){
			return true;
		}
		return false;
	}
}
