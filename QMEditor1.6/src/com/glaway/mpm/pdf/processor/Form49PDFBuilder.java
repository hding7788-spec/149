package com.glaway.mpm.pdf.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.XmlUtility;

public class Form49PDFBuilder extends PDFBuilder {

	private Element techElement;
	private String techFloder = null;
	private String partOid;
	private String formName;
	// 用于记录PDF的页数
	private int page = 1;
	// 用于记录当前PDF页面写到了第几行
	private int index = 1;
	private String template;
	// 用于记录序号值
	private int flag = 1;
	// 记录是否新增了PDF页面
	private boolean isNew = false;
	// 记录没有新增PDF页面时的最大占有行数
	private int r1 = 0;
	// 记录新增页面PDF时的最大占有行数
	private int r2 = 0;
	// 保存所有的PDF页面名称
	private List<String> templateList = new ArrayList<String>();
	private Map<String, String> styleMap = new HashMap<String, String>();
	private List<Element> equipElements;
	private List<Element> toolsElements;
	private List<Element> knifeToolsElements;
	private List<Element> measuresElements;
	private List<Element> sdashboardElements;
	private List<Element> unsdashboardElements;

	public Form49PDFBuilder(String filePath, String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
		addStyle();
	}
	public Form49PDFBuilder(Element techElement, String filePath, String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
		addStyle();
	}

	public void addStyle() {
		styleMap.put("measures/QMMeasureInfo", "measures");// 量具
		styleMap.put("tools/QMToolInfo", "tools");// 工装
		styleMap.put("knifeTools/QMKnifeToolInfo", "knifeTools");// 刀具
		styleMap.put("equips/QMEquipmentInfo", "equips");// 设备
		styleMap.put("sdashboard/QMSDashboardInfo", "sdashboard");// 仪器仪表
		styleMap.put("unsdashboard/QMUnSDashboardInfo", "unsdashboard");// 非仪器仪表

	}

	@Override
	public void buildPDF(LcmPdfPrinter printer, List<Map<String, String>> partList, Map<String, String> params) {
		// TODO Auto-generated method stub
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "Form49.pdf";
			// 从第一页开始写数据
			setEcnParams(params);
			formName = formName + "_" + page;
			templateList.add(formName);
			printer.addTempl(formName, template);
			// 记录基本数据，比如工艺文件编号等
			setCommData(printer, formName);
			// 记录是否有数据
			boolean b = false;

			List<Element> steps = techElement.selectNodes("steps/QMProcedureInfo");
			Map<String, Map<String, List<Element>>> allMap = new HashMap<String, Map<String, List<Element>>>();
			Map<String, List<Element>> map = null;
			// 开始循环遍历所有工序
			for (Element procedure : steps) {
				// 获取当前工序的所有工艺辅料元素
				map = new HashMap<String, List<Element>>();
				String stepNumber = procedure.attributeValue("stepNumber");
				String stepEnglishName = procedure.attributeValue("stepEnglishName");
				String stepName = procedure.attributeValue("stepName");
				if (stepEnglishName == null || "".equals(stepEnglishName)) {
					stepEnglishName = stepName;
				}
				String key = stepNumber + "---" + stepEnglishName;
				for (String zyKey : styleMap.keySet()) {
					List<Element> eleList = new ArrayList<Element>();
					String style = styleMap.get(zyKey);
					List<Element> procedureList = procedure.selectNodes(zyKey);
					for (int i = 0; i < procedureList.size(); i++) {
						b = true;
						Element mEle = procedureList.get(i);
						eleList.add(mEle);
					}
					List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/" + zyKey);
					for (int i = 0; i < stepList.size(); i++) {
						b = true;
						Element mEle = stepList.get(i);
						eleList.add(mEle);
					}
					// for(int i = 0; i < eleList.size(); i++){
					// for(int j = eleList.size() - 1; j > i; j--){
					// Element ele1 = eleList.get(i);
					// Element ele2 = eleList.get(j);
					// String toolNumber = ele1.attributeValue("number");
					// String toolNumber2 = ele2.attributeValue("number");
					// if(toolNumber.equals(toolNumber2)){
					// eleList.remove(j);
					// }
					// }
					// }
					map.put(style, eleList);
				}
				allMap.put(key, map);
			}

			// for (String key:allMap.keySet()) {
			// Map<String, List<Element>> map2 = allMap.get(key);
			// for(String flag:map2.keySet()){
			// List<Element> list = map2.get(flag);
			// for(Element ele:list){
			// b=true;
			// writeElement(flag,key,techElement, ele, printer, index,
			// formName);
			// }
			// }
			// }
			for (String key : allMap.keySet()) {
				Map<String, List<Element>> map2 = allMap.get(key);
				index = writeElement2(key, map2, techElement, printer, index, formName);
			}

			if (!b) {
				printer.removeTempl(formName);
				templateList.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeElement(String flag, String gongxu, Element procedure, Element ele, LcmPdfPrinter printer, int row, String templateName) {
		// 恢复默认值
		isNew = false;
		r1 = 0;
		r2 = 0;

		if (row > 16) {
			index = 1;
			// 如果已经到最后一行，则数据应该写到下一页,index从第一行开始
			row = 1;
			// 新曾一页PDF开始写数据
			String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
			if (cn != null && !"".equals(cn)) {
				int icn = Integer.valueOf(cn);
				if (page > icn) {
					icn++;
					formName = templateName + "_" + icn;
				} else {
					page++;
					formName = templateName + "_" + page;
				}
			} else {
				page++;
				formName = templateName + "_" + page;
			}
			templateName = formName;
			templateList.add(formName);
			printer.addTempl(formName, template);
			// 记录基本数据，比如工艺文件编号等
			setCommData(printer, formName);
		}

		if ("measures".equals(flag)) {// 量具
			String[] split = gongxu.split("---");
			printer.addText(templateName, "序号_" + (row), split[0]);
			printer.addText(templateName, "工序名称_" + (row), split[1]);
			printer.addText(templateName, "数量_" + (row), "1");
			printer.addText(templateName, "使用部门_" + (row), PDFUtil.objectToString(procedure.attributeValue("DEPT")));

			String name = PDFUtil.objectToString(ele.attributeValue("EnglishName"));
			writeProcess(name, 7, printer, "量具_", row, templateName);
			String bz = PDFUtil.objectToString(ele.attributeValue("bz"));
			writeProcess(bz, 4, printer, "备注_", row, templateName);

		} else if ("tools".equals(flag)) {// 工装
			String[] split = gongxu.split("---");
			printer.addText(templateName, "序号_" + (row), split[0]);
			printer.addText(templateName, "工序名称_" + (row), split[1]);
			printer.addText(templateName, "数量_" + (row), "1");
			printer.addText(templateName, "使用部门_" + (row), PDFUtil.objectToString(procedure.attributeValue("DEPT")));
			String name = PDFUtil.objectToString(ele.attributeValue("EnglishName"));
			writeProcess(name, 7, printer, "工装_", row, templateName);
			String bz = PDFUtil.objectToString(ele.attributeValue("bz"));
			writeProcess(bz, 4, printer, "备注_", row, templateName);

		} else if ("knifeTools".equals(flag)) {// 刀具
			String[] split = gongxu.split("---");
			printer.addText(templateName, "序号_" + (row), split[0]);
			printer.addText(templateName, "工序名称_" + (row), split[1]);
			printer.addText(templateName, "数量_" + (row), "1");
			printer.addText(templateName, "使用部门_" + (row), PDFUtil.objectToString(procedure.attributeValue("DEPT")));
			String name = PDFUtil.objectToString(ele.attributeValue("EnglishName"));
			writeProcess(name, 7, printer, "刀具_", row, templateName);
			String bz = PDFUtil.objectToString(ele.attributeValue("bz"));
			writeProcess(bz, 4, printer, "备注_", row, templateName);

		} else if ("equips".equals(flag)) {// 设备
			String[] split = gongxu.split("---");
			printer.addText(templateName, "序号_" + (row), split[0]);
			printer.addText(templateName, "工序名称_" + (row), split[1]);
			printer.addText(templateName, "数量_" + (row), "1");
			printer.addText(templateName, "使用部门_" + (row), PDFUtil.objectToString(procedure.attributeValue("DEPT")));
			String name = PDFUtil.objectToString(ele.attributeValue("EnglishName"));
//			System.out.println("-----------ele.asXml---" + ele.asXML());
			writeProcess(name, 7, printer, "设备_", row, templateName);
			String bz = PDFUtil.objectToString(ele.attributeValue("bz"));
			writeProcess(bz, 4, printer, "备注_", row, templateName);

		} else if ("sdashboard".equals(flag)) {// 仪器仪表
			String[] split = gongxu.split("---");
			printer.addText(templateName, "序号_" + (row), split[0]);
			printer.addText(templateName, "工序名称_" + (row), split[1]);
			printer.addText(templateName, "数量_" + (row), "1");
			printer.addText(templateName, "使用部门_" + (row), PDFUtil.objectToString(procedure.attributeValue("DEPT")));
			String name = PDFUtil.objectToString(ele.attributeValue("EnglishName"));
			writeProcess(name, 7, printer, "仪器仪表_", row, templateName);
			String bz = PDFUtil.objectToString(ele.attributeValue("beizhu"));
			writeProcess(bz, 4, printer, "备注_", row, templateName);

		} else if ("unsdashboard".equals(flag)) {// 非仪器仪表
			String[] split = gongxu.split("---");
			printer.addText(templateName, "序号_" + (row), split[0]);
			printer.addText(templateName, "工序名称_" + (row), split[1]);
			printer.addText(templateName, "数量_" + (row), "1");
			printer.addText(templateName, "使用部门_" + (row), PDFUtil.objectToString(procedure.attributeValue("DEPT")));
			String name = PDFUtil.objectToString(ele.attributeValue("EnglishName"));
			writeProcess(name, 7, printer, "非仪器仪表_", row, templateName);
			String bz = PDFUtil.objectToString(ele.attributeValue("beizhu"));
			writeProcess(bz, 4, printer, "备注_", row, templateName);
		}

		if (isNew) {
			index += r2;
		} else {
			index += r1;
		}

	}

	private int writeElement2(String gongxu, Map<String, List<Element>> map, Element procedure, LcmPdfPrinter printer, int index, String templateName) {
		String[] split = gongxu.split("---");
		int row = index;
		equipElements = map.get("equips");
		toolsElements = map.get("tools");
		knifeToolsElements = map.get("knifeTools");
		measuresElements = map.get("measures");
		sdashboardElements = map.get("sdashboard");
		unsdashboardElements = map.get("unsdashboard");

		int maxSize = getMaxSizeOfElementList();
		if(maxSize > 0){
			printer.addText(templateName, "序号_" + (row), split[0]);
			printer.addText(templateName, "工序名称_" + (row), split[1]);
			// writeProcess("", 4, printer, "备注_",row,templateName);
			printer.addText(templateName, "备注_" + row, "");
		}
		for(int i = 0; i < maxSize; i++){

			int rowMax = 0; //各名称所占最大行数
			int n = 0;
			if(row > 16){
				// 行指针重新从第一行开始写数据
				index = row - 16;
				// 如果已经到最后一行，则数据应该写到下一页,index从第一行开始
				row = index;
				String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
				if (cn != null && !"".equals(cn)) {
					int icn = Integer.valueOf(cn);
					if (page > icn) {
						icn++;
						formName = templateName + "_" + icn;
					} else {
						page++;
						formName = templateName + "_" + page;
					}
				} else {
					page++;
					formName = templateName + "_" + page;
				}
				templateName = formName;
			}
			printer.addText(templateName, "数量_" + (row), "1");
			printer.addText(templateName, "使用部门_" + (row), PDFUtil.objectToString(procedure.attributeValue("DEPT")));
			Element equipElement = getElment(equipElements, i);
			if(equipElement != null){
				String name = PDFUtil.objectToString(equipElement.attributeValue("EnglishName"));
				if("".equals(name)){
					name = PDFUtil.objectToString(equipElement.attributeValue("name"));
				}
				n = writeProcess(name, 7, printer, "设备_", row, templateName);
				if(n > rowMax){
					rowMax = n;
				}
			}
			Element tooElement = getElment(toolsElements, i);
			if(tooElement != null){
				String name = PDFUtil.objectToString(tooElement.attributeValue("EnglishName"));
				if("".equals(name)){
					name = PDFUtil.objectToString(tooElement.attributeValue("toolName"));
				}
				n = writeProcess(name, 7, printer, "工装_", row, templateName);
				if(n > rowMax){
					rowMax = n;
				}
			}
			Element knifElement = getElment(knifeToolsElements, i);
			if(knifElement != null){
				String name = PDFUtil.objectToString(knifElement.attributeValue("EnglishName"));
				if("".equals(name)){
					name = PDFUtil.objectToString(knifElement.attributeValue("toolName"));
				}
				n = writeProcess(name, 7, printer, "刀具_", row, templateName);
				if(n > rowMax){
					rowMax = n;
				}
			}
			Element measElement = getElment(measuresElements, i);
			if(measElement != null){
				String name = PDFUtil.objectToString(measElement.attributeValue("EnglishName"));
				if(name==null||"".equals(name)){
					name =PDFUtil.objectToString(measElement.attributeValue("name"));
				}
				n = writeProcess(name, 7, printer, "量具_", row, templateName);
				if(n > rowMax){
					rowMax = n;
				}
			}
			Element sdashboardElement = getElment(sdashboardElements, i);
			if(sdashboardElement != null){
				String name = PDFUtil.objectToString(sdashboardElement.attributeValue("EnglishName"));
				if("".equals(name)){
					name =  PDFUtil.objectToString(sdashboardElement.attributeValue("name"));
				}
				n = writeProcess(name, 7, printer, "仪器仪表_", row, templateName);
				if(n > rowMax){
					rowMax = n;
				}
			}
			Element unsdashboardElement = getElment(unsdashboardElements, i);
			if(unsdashboardElement != null){
				String name = PDFUtil.objectToString(unsdashboardElement.attributeValue("EnglishName"));
				if("".equals(name)){
					name = PDFUtil.objectToString(unsdashboardElement.attributeValue("name"));
				}
				n = writeProcess(name, 7, printer, "非仪器仪表_", row, templateName);
				if(n > rowMax){
					rowMax = n;
				}
			}
			row += rowMax;
		}
		return row;
	}

	public void setCommData(LcmPdfPrinter printer, String templateName) {

		//modify by zhuhao 20180312
		String partVersion = PDFUtil.objectToString(techElement.attributeValue("partVersion"));
		if(partVersion.contains(".")){
			partVersion = partVersion.substring(0, partVersion.indexOf("."));
		}
		//modify by zhuhao end
		printer.addText(templateName, "PBOM版本", "P:" + partVersion);
		// modify by machongqi 2015-7-2
		printer.addText(templateName, "工艺文件版本", PDFUtil.objectToString(techElement.attributeValue("version")));
		// modify by machongqi end

		printer.addText(templateName, "批次号", PDFUtil.objectToString(techElement.attributeValue("PCNO")));
//		printer.addText(templateName, "标识", PDFUtil.objectToString(techElement.attributeValue("BIAOSHI")));

		printer.addText(templateName, "产品代号", PDFUtil.objectToString(techElement.attributeValue("PINDEX")));
		printer.addText(templateName, "工艺文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
		printer.addText(templateName, "名称", PDFUtil.objectToString(techElement.attributeValue("partName")));
		printer.addText(templateName, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
		printer.addText(templateName, "图纸版本", PDFUtil.objectToString(techElement.attributeValue("imageVersion")));

		String phaseCode = PDFUtil.objectToString(techElement.attributeValue("PHASE_CODE"));
		String index = PDFUtil.getPhaseCodeIndex(phaseCode);
		printer.addText(templateName, "阶段标记_" + index, phaseCode);

		printer.addText(templateName, "编制", "");
		printer.addText(templateName, "编制时间", "");
		printer.addText(templateName, "校对", "");
		printer.addText(templateName, "校对时间", "");
		printer.addText(templateName, "审核", "");
		printer.addText(templateName, "审核时间", "");

		printer.addText(templateName, "会签_1", "");
		printer.addText(templateName, "会签_2", "");
		printer.addText(templateName, "会签_3", "");
		printer.addText(templateName, "会签_4", "");
		printer.addText(templateName, "会签_5", "");
		printer.addText(templateName, "会签_6", "");
		printer.addText(templateName, "会签_7", "");
		printer.addText(templateName, "会签_8", "");
		printer.addText(templateName, "会签_9", "");
		printer.addText(templateName, "会签_10", "");

		printer.addText(templateName, "更改标记", ecnBiaoJi);
		printer.addText(templateName, "更改单号", ecnNo);
		printer.addText(templateName, "更改签名", "");
		printer.addText(templateName, "日期", "");
	}

	private int writeProcess(String valueStr, int count, LcmPdfPrinter printer, String key, int row, String templateName) {
		char[] clArr = valueStr.toCharArray();

		// n用于计算valueStr总共需要几行来写入数据
		int n = clArr.length / count;

		// 如果不能整除，则表示还需要多一行来写入余下的数据
		if (clArr.length % count != 0) {
			n = n + 1;
		}

		// 记录当前数据占了多少行
		int k = 0;
		boolean b = false;
		// 循环n行，将第n行的数据写入PDF页面的row+i行
		for (int i = 0; i < n; i++) {
			// 用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
			String value = "";
			for (int j = 0; j < count; j++) {
				int col = count * i + j;
				if (col < clArr.length) {
					value = value + clArr[col];
				}
			}

			if (row > 16) {// 如果PDF页面已经写到最后一行，则需要增加一页来继续写
				// 记录当前新曾了页面
				b = true;
				// 新增加了页面，则重新开始计数
				k = 0;
				// 行指针重新从第一行开始写数据
				index = 1;
				// 如果已经到最后一行，则数据应该写到下一页,index从第一行开始
				row = 1;

				// if(!isNew) {
				isNew = true;
				// 新曾一页PDF开始写数据
				String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
				if (cn != null && !"".equals(cn)) {
					int icn = Integer.valueOf(cn);
					if (page > icn) {
						icn++;
						formName = templateName + "_" + icn;
					} else {
						page++;
						formName = templateName + "_" + page;
					}
				} else {
					page++;
					formName = templateName + "_" + page;
				}
				templateName = formName;
				templateList.add(formName);
				printer.addTempl(formName, template);
				// 记录基本数据，比如工艺文件编号等
				setCommData(printer, formName);
				// } else {
				// //如果已经存在新增页面，则直接在新增页面上写入数据
				// templateName = formName;
				// }
			}

			// 记录占用了多少行
			k++;

			// 写入PDF的key+(row)格子中
			printer.addText(templateName, key + row, value);
			row++;
		}

		if (b) {
			if (r2 < k) {
				r2 = k;
			}
		} else {
			if (r1 < k) {
				r1 = k;
			}
		}

		return n;
	}
	public int getMaxSizeOfElementList(){
		int maxSize = 0;
		int m = equipElements.size();
		if(m > maxSize){
			maxSize = m;
		}
		m = toolsElements.size();
		if(m > maxSize){
			maxSize = m;
		}
		m = knifeToolsElements.size();
		if(m > maxSize){
			maxSize = m;
		}
		m = measuresElements.size();
		if(m > maxSize){
			maxSize = m;
		}
		m = sdashboardElements.size();
		if(m > maxSize){
			maxSize = m;
		}
		m = unsdashboardElements.size();
		if(m > maxSize){
			maxSize = m;
		}
		return maxSize;
	}
	public Element getElment(List<Element> elements, int i){
		Element ele = null;
		if(elements.size() > 0 && i < elements.size() ){
			ele = elements.get(i);
		}
		return ele;
	}

	@Override
	public String getTechFloder() {
		// TODO Auto-generated method stub
		return techFloder;
	}

	@Override
	public List<String> getTemplateList() {
		// TODO Auto-generated method stub
		return templateList;
	}
}
