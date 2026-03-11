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

public class Form34PDFBuilder extends PDFBuilder{

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
	public Form34PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form34PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "Form34.pdf";
			//从第一页开始写数据
			setEcnParams(params);
			formName = formName + "_" + page;
			templateList.add(formName);
			printer.addTempl(formName, template);
			//记录基本数据，比如工艺文件编号等
			setCommData(printer,formName);
//			printer.addText(formName, "更改单号",params.get("ecnNo"));
			boolean b = false;

			//获取配套明细表数据
			Element peitaoElement = techElement.element(XmlUtility.PEITAOLISTTABLE);
			//如果ele为空，则表示在工艺编辑器中还没有对明细表进行修改过，此时配套数据从零部件的下级子件中获取
			//如果ele不为空，则从工艺文件的xml中获取
			if(peitaoElement == null) {
				/*System.out.println("---------partList-----"+partList);
				if(partList != null && !partList.isEmpty()) {
					for (Map<String,String> map : partList) {
						writeElement(techElement,map,printer,index,formName);
						b = true;
					}
				}*/
			} else {
				List<Element> list = XmlUtility.getPeiTaoListTableElements(techElement);
				Map<String,String> map = null;
				for (Element element : list) {
					b = true;
					map = new HashMap<String, String>();
					String number = element.attributeValue("number");
					map.put("partNumber", number);
					map.put("partName", element.attributeValue("name"));
					map.put("useCount", element.attributeValue("useCount"));
					map.put("dw", element.attributeValue("dw"));
					map.put("MTYPE", element.attributeValue("MTYPE"));

					//自制件不需要填写牌号、规格、标准号
					if(!"自制件".equals(element.attributeValue("MTYPE"))){
						map.put("XHPH", element.attributeValue("XHPH"));
						map.put("CSIZE", element.attributeValue("CSIZE"));
						map.put("jstj", element.attributeValue("jstj"));
					}

					map.put("comment", element.attributeValue("comment"));

					map.put("gys", element.attributeValue("gys"));
					map.put("xncs", element.attributeValue("xncs"));
					map.put("CINDEX", number);
					writeElement(techElement,map,printer,index,formName);
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


	private void writeElement(Element procedure,Map<String,String> map,LcmPdfPrinter printer,int row,String templateName) {
		//恢复默认值
		isNew = false;
		r1 = 0;
		r2 = 0;

		if(row>16) {
			index = 1;
        	//如果已经到最后一行，则数据应该写到下一页,index从第一行开始
			row = 1;
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
//        	templateList.add(formName);
        	if(!templateList.contains(formName)){
        		templateList.add(formName);
        	}
			printer.addTempl(formName, template);
			//记录基本数据，比如工艺文件编号等
			setCommData(printer,formName);
		}

		printer.addText(templateName, "序号_"+(row), (flag++)+"");

		if(map.get("dw")==null||"".equals(map.get("dw"))||"null".equals(map.get("dw"))){
			printer.addText(templateName, "单位_"+(row),"个");
		}else{
			printer.addText(templateName, "单位_"+(row), map.get("dw"));
		}
        printer.addText(templateName, "数量_"+(row), map.get("useCount"));
       // printer.addText(templateName, "来自何处_"+(row), "");
        String lzhc = PDFUtil.objectToString(map.get("gys"));
        //printer.addText(templateName, "来自何处_"+(row), lzhc);
        writeProcess(lzhc, 4, printer, "来自何处_",row,templateName);

        String partNumber = PDFUtil.objectToString(map.get("CINDEX"));
        writeProcess(partNumber, 7, printer, "代号_",row,templateName);

        String standNumber =PDFUtil.objectToString(map.get("jstj"));
        writeProcess(standNumber, 4, printer, "标准号_",row,templateName);

        String partName = PDFUtil.objectToString(map.get("partName"));
        writeProcess(partName, 7, printer, "名称_",row,templateName);

        String xhph = PDFUtil.objectToString(map.get("XHPH"));
        writeProcess(xhph, 4, printer, "牌号_",row,templateName);

        String csize = PDFUtil.objectToString(map.get("CSIZE"));
        writeProcess(csize, 6, printer, "规格_",row,templateName);

        String mtype = PDFUtil.objectToString(map.get("MTYPE"));
        writeProcess(mtype, 3, printer, "类别_",row,templateName);

        String bz = PDFUtil.objectToString(map.get("comment"));
        writeProcess(bz, 5, printer, "备注_",row,templateName);

		String xncs = PDFUtil.objectToString(map.get("xncs"));
		writeProcess(xncs, 4, printer, "性能参数_",row,templateName);

        if(isNew) {
        	index += r2;
        } else {
        	index += r1;
        }

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
//        			templateList.add(formName);
        			if(!templateList.contains(formName)){
                		templateList.add(formName);
                	}
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
