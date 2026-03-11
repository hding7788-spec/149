package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记录该工艺所有工序列表信息：工序号、工序名称、工序简述、前置工序
 *
 * @author cjh
 *
 */
public class Form50PDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String formName;
	private final int rows = 32;
	//用于记录PDF的页数
	private int page = 1;
	//用于记录当前PDF页面写到了第几行
	private int index = 1;
	private String template;
	//用于记录序号值
	private int flag = 1;
	//记录是否新增了PDF页面
	private boolean isNew = false;
	//记录没有新增PDF页面时的最大占有行数
	private int r1 = 0;
	//记录新增页面PDF时的最大占有行数
	private int r2 = 0;
	//保存所有的PDF页面名称
	private List<String> templateList = new ArrayList<String>();

	public Form50PDFBuilder(String filePath, String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
	}
	public Form50PDFBuilder(Element techElement, String filePath, String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
	}
	
	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "Form50.pdf";
			//从第一页开始写数据
			setEcnParams(params);
			formName = formName + "_" + page;
			templateList.add(formName);
			printer.addTempl(formName, template);
			//记录基本数据，比如工艺文件编号等
			setCommData(printer,formName);

			boolean b = false;

			//获取工序列表信息
			Element stepElement = techElement.element(XmlUtility.STEP_TAG);
			if(stepElement != null) {
				List<Element> list = XmlUtility.getAllStepsOrderByStepNumber(techElement);
				if(list != null && list.size()>0){
					Map<String,String> map = null;
					for (Element element : list) {
						b = true;
						map = new HashMap<String, String>();
						String stepNumber = element.attributeValue("stepNumber");
						String stepName = element.attributeValue("stepName");
//						String enforce = element.attributeValue("enforce");
//						if("否".equals(enforce)){
//							stepName += "(可选)";
//						}
						String gxjs = element.attributeValue("GXJS");
						String preStep = element.attributeValue("preStep");
						map.put("stepNumber", stepNumber);
						map.put("stepName", stepName);
						map.put("gxjs", gxjs);
						map.put("preStep", preStep);
						writeElement(map,printer,index,formName);
					}
				}
			}
			if(!b) {
				printer.removeTempl(formName);
				templateList.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCommData(LcmPdfPrinter printer,String templateName) {
		String partVersion = PDFUtil.objectToString(techElement.attributeValue("partVersion"));
		if(partVersion.contains(".")){
			partVersion = partVersion.substring(0, partVersion.indexOf("."));
		}
		printer.addText(templateName, "PBOM版本", "P:"+partVersion);
		printer.addText(templateName, "工艺文件版本", PDFUtil.objectToString(techElement.attributeValue("version")));
		printer.addText(templateName, "批次号", PDFUtil.objectToString(techElement.attributeValue("PCNO")));
//		printer.addText(templateName, "标识", PDFUtil.objectToString(techElement.attributeValue("BIAOSHI")));

		printer.addText(templateName, "产品代号", PDFUtil.objectToString(techElement.attributeValue("PINDEX")));
        printer.addText(templateName, "工艺文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "名称", PDFUtil.objectToString(techElement.attributeValue("partName")));
        printer.addText(templateName, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));

        String phaseCode = PDFUtil.objectToString(techElement.attributeValue("PHASE_CODE"));
        String index = PDFUtil.getPhaseCodeIndex(phaseCode);
        printer.addText(templateName, "阶段标记_"+index, phaseCode);

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
        printer.addText(templateName, "签名", "");
        printer.addText(templateName, "日期", "");
    }

	private void writeElement(Map<String, String> map, LcmPdfPrinter printer, int row, String templateName) {
		//恢复默认值
		isNew = false;
		r1 = 0;
		r2 = 0;

		if(row > rows) {
			index = 1;
			//如果已经到最后一行，则数据应该写到下一页,index从第一行开始
			row = 1;
			//新曾一页PDF开始写数据
			String cn = templateName.substring(templateName.lastIndexOf('_') + 1);
			if(cn != null && !"".equals(cn)) {
				int icn = Integer.valueOf(cn);
				if(page > icn) {
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
			//记录基本数据，比如工艺文件编号等
			setCommData(printer, formName);
		}

		printer.addText(templateName, "工序号_" + (row), map.get("stepNumber"));
		printer.addText(templateName, "工序名称_" + (row), map.get("stepName"));
		//工序简述只映射前十个字

		String gxjs = map.get("gxjs") == null ? "" : map.get("gxjs");
		if(gxjs.length() > 10) {
			gxjs = gxjs.substring(0, 10);
		}
		printer.addText(templateName, "工序简述_" + (row), gxjs);
		//前置工序只映射前置工序号
		String preStep = map.get("preStep") == null ? "" : map.get("preStep");
		if(preStep.contains(",")) {
			writePreStep(preStep, 5, printer, "前置_", row, templateName);
		} else {
			if(preStep.indexOf("_") > -1) {
				preStep = preStep.substring(0, preStep.indexOf("_"));
			}
			printer.addText(templateName, "前置_" + (row), preStep);
			r1++;
		}

		if(isNew) {
			index += r2;
		} else {
			index += r1;
		}
	}

	private int writePreStep(String valueStr, int count, LcmPdfPrinter printer, String key, int row, String templateName) {
		String[] preArray = valueStr.split(",");

		//n用于计算valueStr总共需要几行来写入数据
		int n = preArray.length / count;

		//如果不能整除，则表示还需要多一行来写入余下的数据
		if(preArray.length % count != 0) {
			n = n + 1;
		}

		//记录当前数据占了多少行
		int k = 0;
		boolean b = false;
		//循环n行，将第n行的数据写入PDF页面的row+i行
		for(int i = 0; i < n; i++) {
			String value = "";


			for(int j = 0; j < 5; j++) {
				int y = 5 * i + j;
				if(y >= preArray.length){
					break;
				}
				String str = preArray[y];
				str = str.indexOf("_") > -1 ? str.substring(0,str.indexOf("_")) : str;
				value = "".equals(value) ?	str : value + "," + str;
			}

			if(row > rows) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
				//记录当前新曾了页面
				b = true;
				//新增加了页面，则重新开始计数
				k = 0;
				//行指针重新从第一行开始写数据
				index = 1;
				//如果已经到最后一行，则数据应该写到下一页,index从第一行开始
				row = 1;

				isNew = true;
				//新曾一页PDF开始写数据
				String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
				if(cn != null && !"".equals(cn)) {
					int icn = Integer.valueOf(cn);
					if(page > icn) {
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
				//记录基本数据，比如工艺文件编号等
				setCommData(printer, formName);
			}

			//记录占用了多少行
			k++;

			//写入PDF的key+(row)格子中
			printer.addText(templateName, key + row, value);

			row++;
		}

		if(b) {
			if(r2 < k) {
				r2 = k;
			}
		} else {
			if(r1 < k) {
				r1 = k;
			}
		}

		return n;
	}

	@Override
	public String getTechFloder() {
		return techFloder;
	}

	@Override
	public List<String> getTemplateList() {
		return templateList;
	}
}
