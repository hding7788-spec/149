package com.glaway.mpm.pdf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.glaway.mpm.sjzyk.LoadConfigAttributes;

public class WriterStandard {


	public static int Form11=23;
	public static int Form13=22;
	public static int Form14=29;
	public static int Form21=20;
	public static int Form27=60;
	public static int Form27_KZNR=33;
	public static int Form27_ZLKZCX=54;
	public static int Form31=20;
	public static int Form35=20;
	public static int Form37=18;
	public static int Form38=17;
	public static int Form5=28;
	public static int Form8=30;
	public static int Form8a=29;
	public static int Form9=21;
	public static int ReportForm26_KZNR=22;

	public static List<String> speWord = new ArrayList<String>();
	static{try {
		InputStream is = LoadConfigAttributes.class.getClassLoader().getResourceAsStream("config.xml");
		SAXReader reader = new SAXReader();
		Document document = reader.read(is);
		Element root = document.getRootElement();
		List nodes = root.selectNodes("config/speWord");
		for (int i=0;i<nodes.size();i++) {
			Element element = (Element) nodes.get(i);
			speWord.add(element.getText());
		}
	} catch (DocumentException e) {
		e.printStackTrace();
	}

	}

}
