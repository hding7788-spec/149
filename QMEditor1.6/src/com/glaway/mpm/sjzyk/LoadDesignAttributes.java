package com.glaway.mpm.sjzyk;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class LoadDesignAttributes {

	private static Document document;
	private static LoadDesignAttributes instance = null;
	public synchronized static LoadDesignAttributes getInstance(){
		if(instance == null){
			instance = new LoadDesignAttributes();
			instance.loadConfig();
		}
		return instance;
	}

	public void loadConfig() {
		try {
			InputStream is = LoadDesignAttributes.class.getClassLoader().getResourceAsStream("designAttributes.xml");
			SAXReader reader = new SAXReader();
			document = reader.read(is);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public static List<Map<String,String>> getAllDesignAttr() {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Element root = document.getRootElement();
		List nodes = root.selectNodes("default/attribute");
		Map<String,String> map = null;
		String key = "";
		String display = "";
		for (int i=0;i<nodes.size();i++) {
			Element element = (Element) nodes.get(i);
			map =new HashMap<String,String>();
			key = element.attributeValue("name");
			display = element.getText();
			map.put(key, display);
			list.add(map);
		}
		return list;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadDesignAttributes.getInstance().getAllDesignAttr();
	}

}
