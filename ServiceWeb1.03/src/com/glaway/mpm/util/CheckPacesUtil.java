package com.glaway.mpm.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;



import wt.util.WTProperties;

public class CheckPacesUtil {

    public static String getXmlPath(String technicsNumber) throws IOException{
    	 WTProperties wtProperties = WTProperties.getLocalProperties();
         String codebasePath = wtProperties.getProperty("wt.codebase.location");
		 String xmlPath = codebasePath + File.separator + "temp" + File.separator + "publish"
					+ File.separator + technicsNumber + File.separator + technicsNumber + ".xml";
		 return xmlPath;
    }
    public static Node getCurrentPace(Document doc, String stepNumber, String paceNumber){
    	Node pace = doc.selectSingleNode("//steps/QMProcedureInfo[@stepNumber='"+stepNumber+"']/paces/QMProcedureInfo[@stepNumber='"+paceNumber+"']");
    	return pace;
    }
	public static List<String> getImgStr(String htmlStr) {
		String img = "";
		Pattern p_image;
		Matcher m_image;
		List<String> pics = new ArrayList<String>();

		String regEx_img = "<img.*src=(.*?)[^>]*?>"; // 图片链接地址
		p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
		m_image = p_image.matcher(htmlStr);
		while (m_image.find()) {
			img = img + "," + m_image.group();
			Matcher m = Pattern.compile("src=\'?(.*?)(\'|>|\\s+)").matcher(img); // 匹配src
			while (m.find()) {
				pics.add(m.group(1));
			}
		}
		return pics;
	}

	public static String Html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		try {
			// 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>}
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

			// 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>}
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";

			// 定义图片HTML标签的正则表达式
			String regEx_html = "<img src=[^>]+>";

			// 定义HTML标签的正则表达式
			String regEx_html2 = "<[^>]+>";

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("♀"); // 过滤html标签

			p_html = Pattern.compile(regEx_html2, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return textStr;// 返回文本字符串
	}
	public static String changeHtml(String temp){
		if(temp != null && !"".equals(temp)) {
			temp = temp.replace("@#$%^\\", "");
			temp = temp.replaceAll("@#\\$", "");
			temp = temp.replace("\\", "/");
			temp = temp.replaceAll("WORKSPACE_PATH/", "");
			temp = temp.replaceAll("<html>", "").trim();
			temp = temp.replaceAll("<head>", "").trim();
			temp = temp.replaceAll("</head>", "").trim();
			temp = temp.replaceAll("<body>", "").trim();
			temp = temp.replaceAll("</body>", "").trim();
			temp = temp.replaceAll("</html>", "").trim();
			temp = temp.replaceAll("<br>", "").trim();
	        	if(temp.contains("<p style='margin-top: 0'>")) {
	        		temp = temp.replaceAll("<p style='margin-top: 0'>", "").trim();
	        		//gxnr = gxnr.replaceAll("</p>", "").trim();
	        	}
	        	if(temp.contains("<p style='margin-top:5'>")) {
	        		temp = temp.replaceAll("<p style='margin-top:5'>", "").trim();
	        		//gxnr = gxnr.replaceAll("</p>", "").trim();
	        	}
	        	//为了计算字符方便，替换字符串中&nbsp;为特殊符号♣，标识该处为空格，在写入时再替换回来
//	        	temp = temp.replace("&nbsp;", "♣").trim();
	        	temp = temp.replace("&nbsp;", "").trim();
//	        	//替换字符串中</p>为特殊符号♂，标识该处为换行
//	        	temp = temp.replaceAll("</p>", "♂").trim();
	        	temp = temp.replaceAll("</p>", "").trim();
	        	temp=replaceTeShuFuHao(temp);
	        	temp =temp.replace("\n", "");
		}
		return temp;
	}
	public static String replaceTeShuFuHao(String str) {
	    //❤♠♥
	  //替换字符串中所有的双引号，为❤
        //str = str.replaceAll("&quot;", "❤").trim();
        str = str.replaceAll("&quot;", "").trim();
        //替换字符串中所有的大于号，为♠
       // str = str.replaceAll("&gt;", "♠").trim();
        str = str.replaceAll("&gt;", "").trim();
        //替换字符串中所有的小于号，为♥
        //str = str.replaceAll("&lt;", "♥").trim();
        str = str.replaceAll("&lt;", "").trim();
        return str;
    }
//    public static Node getCheckEquips(Node pace){
//    	Node ele = pace.selectSingleNode("//checkEquips/QMEquipmentInfo");
//    	return ele;
//    }
//    public static Node getCheckSdashboard(Node pace){
//    	Node ele = pace.selectSingleNode("//checkSdashboard/QMSDashboardInfo");
//    	return ele;
//    }

}
