package com.glaway.mpm.pdf.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.XmlUtility;

public class Form22PDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String partOid;
	private String formName;
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
	public Form22PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form22PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			String template = PDFUtil.getFormTemplateFolderPath() + "Form22.pdf";
//			formName=formName+"_"+page;
			setEcnParams(params);
			printer.addTempl(formName, template);
			//printer.addText(formName, "更改单号",params.get("ecnNo"));
			this.setCommData(printer,formName);
			templateList.add(formName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCommData(LcmPdfPrinter printer,String templateName) {
		//modify by zhuhao 20180312
		String partVersion = PDFUtil.objectToString(techElement.attributeValue("partVersion"));
		if(partVersion.contains(".")){
			partVersion = partVersion.substring(0, partVersion.indexOf("."));
		}
		//modify by zhuhao end
		printer.addText(templateName, "PBOM版本", "P:"+partVersion);
		//modify by machongqi 2015-7-2
          	printer.addText(templateName, "工艺文件版本", PDFUtil.objectToString(techElement.attributeValue("version")));
  		//modify by machongqi end
          	printer.addText(templateName, "批次号", PDFUtil.objectToString(techElement.attributeValue("PCNO")));
//		printer.addText(templateName, "标识", PDFUtil.objectToString(techElement.attributeValue("BIAOSHI")));

		printer.addText(templateName, "产品代号", PDFUtil.objectToString(techElement.attributeValue("PINDEX")));
        printer.addText(templateName, "工艺文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "名称", PDFUtil.objectToString(techElement.attributeValue("partName")));
        printer.addText(templateName, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));

        String phaseCode = PDFUtil.objectToString(techElement.attributeValue("PHASE_CODE"));
        String index = PDFUtil.getPhaseCodeIndex(phaseCode);
        printer.addText(templateName, "阶段标记_"+index, phaseCode);

        printer.addText(templateName, "序号_"+1, (flag++)+"");

        String useCount = PDFUtil.objectToString(techElement.attributeValue("gysl"));
        printer.addText(templateName, "数量_"+1, useCount);

        String jsxy = PDFUtil.objectToString(techElement.attributeValue("jsxy"));
//        printer.addText(templateName, "技术协议编号_"+1, jsxy);
        writeProcess(jsxy, 14, printer, "技术协议编号_",1,templateName);

        String zzdw = PDFUtil.objectToString(techElement.attributeValue("zzdw"));
        //modify by machongqi 2015-7-2
//        printer.addText(templateName, "承制单位_"+1, zzdw);
        writeProcess(zzdw, 4, printer, "承制单位_",1,templateName);
        //modify by machongqi end

        //add by machongqi 2015-7-2
        String bz = PDFUtil.objectToString(techElement.attributeValue("bz"));
        writeProcess(bz, 4, printer, "备注_",1,templateName);
        //add by machongqi end

        Element toolElement = techElement.element(XmlUtility.TOOL_GROUP);
        String gyzb = "";
        if(toolElement != null) {
        	for (Iterator<Element> it = toolElement.elementIterator(XmlUtility.TOOL_TAG); it.hasNext();) {
            	Element ele = it.next();
            	if(gyzb != null && !"".equals(gyzb)){
            		gyzb = gyzb + ";" + ele.attributeValue("toolNum");
            	} else {
            		gyzb = ele.attributeValue("toolNum");
            	}
    		}
            writeProcess(gyzb, 5, printer, "工艺装备_",1,templateName);
        }

        String partName = PDFUtil.objectToString(techElement.attributeValue("partName"));
        writeProcess(partName, 8, printer, "名称_",1,templateName);

        String partNumber = PDFUtil.objectToString(techElement.attributeValue("CINDEX"));
        writeProcess(partNumber, 14, printer, "代号_",1,templateName);

       // String cmat = PDFUtil.objectToString(techElement.attributeValue("CMAT"));
        String cmat = PDFUtil.getCmatByCLDE(techElement);
        writeProcess(cmat, 10, printer, "材料_",1,templateName);

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



	private int writeProcess(String valueStr,int count,LcmPdfPrinter printer,String key,int row,String templateName) {
        char[] clArr = valueStr.toCharArray();

        //n用于计算valueStr总共需要几行来写入数据
        int n = clArr.length/count;

        //如果不能整除，则表示还需要多一行来写入余下的数据
        if(clArr.length%count != 0) {
        	n = n + 1;
        }

        //记录当前数据占了多少行
        int k = 0;
        boolean b = false;
        //循环n行，将第n行的数据写入PDF页面的row+i行
        for(int i=0;i<n;i++) {
        	//用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
    		String value = "";
    		for(int j=0;j<count;j++) {
    			int col = count*i+j;
    			if(col<clArr.length) {
    				value = value + clArr[col];
    			}
    		}

    		if(row > 16) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
    			//记录当前新曾了页面
    			b = true;
    			//新增加了页面，则重新开始计数
    			k = 0;
    			//行指针重新从第一行开始写数据
    			index = 1;
            	//如果已经到最后一行，则数据应该写到下一页,index从第一行开始
            	row = 1;

//            	if(!isNew) {
            		isNew = true;
            		//新曾一页PDF开始写数据
            		String cn = templateName.substring(templateName.lastIndexOf('_')+1, templateName.length());
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
        			setCommData(printer,formName);
//            	} else {
//            		//如果已经存在新增页面，则直接在新增页面上写入数据
//            		templateName = formName;
//            	}
    		}

    		//记录占用了多少行
    		k++;

    		//写入PDF的key+(row)格子中
    		printer.addText(templateName, key+row, value);

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
		// TODO Auto-generated method stub
		return techFloder;
	}

	@Override
	public List<String> getTemplateList() {
		// TODO Auto-generated method stub
		return templateList;
	}
}
