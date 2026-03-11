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

public class LoadConfigAttributes {

	private static Document document;
	private static LoadConfigAttributes instance = null;
	public synchronized static LoadConfigAttributes getInstance(){
		if(instance == null){
			instance = new LoadConfigAttributes();
			instance.loadConfig();
		}
		return instance;
	}

	public void loadConfig() {
		try {
			InputStream is = LoadConfigAttributes.class.getClassLoader().getResourceAsStream("config.xml");
			SAXReader reader = new SAXReader();
			document = reader.read(is);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public Map<String,String> getAllDesignAttr() {
		Element root = document.getRootElement();
		List nodes = root.selectNodes("config/gylx");
		String key = "";
		String display = "";
		Map<String,String> AllMap =new HashMap<String,String>();
		for (int i=0;i<nodes.size();i++) {
		    List<String> list2=new ArrayList<String>();
			Element element = (Element) nodes.get(i);
			key = element.attributeValue("key");
			display = element.getText();
			String[] sq=display.split("、");
			for (int j = 0; j < sq.length; j++) {
//                list2.add(sq[j]);
			    AllMap.put(sq[j], key);
            }
		}
		return AllMap;
	}
	public List<String> getDept(){
		Element root = document.getRootElement();
		Element node = root.element("dept");
		String depts = node.getText();
		String dept[] = depts.trim().split("、");
		List<String> deptList = new ArrayList<String>();
		for(int i = 0; i < dept.length; i++){
			deptList.add(dept[i]);
		}
		return deptList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    Map<String,String> map = LoadConfigAttributes.getInstance().getAllDesignAttr();
	    System.out.println(map);

	}

}
