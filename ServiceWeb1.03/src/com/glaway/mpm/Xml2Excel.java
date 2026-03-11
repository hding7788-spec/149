/**
 *
 */
package com.glaway.mpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ext.casc.util.ExcelFileGenerator;

/**
 * @author cfire
 *
 */
public class Xml2Excel {
	public static void main(String[] args) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(new InputStreamReader(new FileInputStream("D:\\findbugs-result-CASC_SAST_805_SWBD_2020_06_23_09_58_29\\result.xml"), "GBK"));
		Element rootElement = document.getRootElement();

		ArrayList title = new ArrayList();
		title.add("错误信息");
		title.add("代码路径");
		title.add("方法名");
		title.add("代码行");
		title.add("问题描述");
		title.add("严重程度");

		ArrayList datas = new ArrayList();
		int i=0;
		for (Element BugInstance : (List<Element>) rootElement.getChildren("BugInstance")) {
			System.out.println(i);
			i++;
			ArrayList data = new ArrayList();
			String ShortMessage = BugInstance.getChildText("ShortMessage");
			data.add(ShortMessage);

			Element Method = BugInstance.getChild("Method");
			if(Method==null){
				Method = BugInstance.getChild("Field");
			}
			data.add(Method.getAttributeValue("classname"));
			data.add(Method.getAttributeValue("name"));

			Element SourceLine = BugInstance.getChild("SourceLine");
			data.add(SourceLine.getAttributeValue("start"));
			data.add("");

			data.add(BugInstance.getAttributeValue("priority"));

			datas.add(data);
		}

		ExcelFileGenerator file = new ExcelFileGenerator(title,datas);
		File xls = new File("D://result.xls");
		file.expordExcel(new FileOutputStream(xls));

	}
}
