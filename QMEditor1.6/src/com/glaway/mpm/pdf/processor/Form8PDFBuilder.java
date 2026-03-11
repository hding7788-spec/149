package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.XmlUtility;
import com.itextpdf.text.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Form8PDFBuilder extends PDFBuilder{
	private float totalWidth ;
	private final int rows = 16;
	/**装配工艺卡片*/
	public Form8PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form8PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "Form8.pdf";
			//从第一页开始写数据
			setEcnParams(params);
			formName = formName + "_" + page;
			templateList.add(formName);
			printer.addTempl(formName, template);
//			printer.addText(formName, "更改单号_1",params.get("ecnNo"));
			//记录基本数据，比如工艺文件编号等
			setCommData(printer,formName);
			if(index<=rows){
				totalWidth = printer.getFieldWidth(template, "内容_"+index);
			}else{
				totalWidth = printer.getFieldWidth(template, "内容_1");
			}

			//获取所有工序
			List<Element> steps = techElement.selectNodes("steps/QMProcedureInfo");
			//开始循环遍历所有工序
			for (Element procedure : steps) {
				//获取当前工序的工序内容
				List<Element> procedureList = procedure.selectNodes("procedureContent");
				for(int i = 0;i < procedureList.size();i++) {
					Element mEle = procedureList.get(i);
					if(index != 1) {
						index = index + 1;
					}
					writeElement(procedure,mEle,printer,(index),formName);
					//写入附图附表 一序一卡
					boolean isHas = PDFBuilder.buildProcedureFTFB(printer, techElement, procedure, techFloder, params, templateList);
					if(isHas){
						index += 15;
					}
				}
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

		String gysl = PDFUtil.objectToString(techElement.attributeValue("gysl"));
  		if(gysl==null  || "".equals(gysl)){
  			gysl = "1";
  		}
        printer.addText(templateName, "产品代号", PDFUtil.objectToString(techElement.attributeValue("PINDEX")));
        printer.addText(templateName, "数量", gysl);
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

	private void writeElement(Element procedure,Element element,LcmPdfPrinter printer,int row,String templateName) throws DocumentException, IOException {
		//恢复默认值
		isNew = false;
		r1 = 0;
		r2 = 0;

		if(row>rows) {
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
        	templateList.add(formName);
			printer.addTempl(formName, template);
			//记录基本数据，比如工艺文件编号等
			setCommData(printer,formName);
		}

		printer.addText(templateName, "序号_"+(row), PDFUtil.objectToString(procedure.attributeValue("stepNumber")));
//		printer.addText(templateName, "工序名称_"+(row), PDFUtil.objectToString(procedure.attributeValue("stepName")));
		printer.addText(templateName, "车间_"+(row), PDFUtil.objectToString(procedure.attributeValue("workShop")));

		if(!EditorConfig.isZS) {
			printer.addText(templateName, "准结_" + (row), PDFUtil.objectToString(procedure.attributeValue("ZJGS")));
			printer.addText(templateName, "单件_" + (row), PDFUtil.objectToString(procedure.attributeValue("DJGS")));
		}
        String gxmc = PDFUtil.objectToString(procedure.attributeValue("stepName"));
        writeProcess(gxmc, 3, printer, "工序名称_",row,templateName);

        //工序的设备
        String[] keys={"name","pindex"};
        String sbValues=getElementValues2((Element)procedure.selectNodes("equips").get(0),"QMEquipmentInfo",keys);
//        String sbValues = getElementValues((Element)procedure.selectNodes("equips").get(0),"QMEquipmentInfo","pindex");
        writeProcess(sbValues, 5, printer, "设备_",row,templateName);

        //工序的工装
        String gzValues = getElementValues((Element)procedure.selectNodes("tools").get(0),"QMToolInfo","toolNum");
        writeProcess(gzValues, 11, printer, "工艺装备_",row,templateName);
        //是否关键工序
        String isKey = PDFUtil.objectToString(procedure.attributeValue("isKey"));
        //写入工序内容
        String gxnr = PDFUtil.objectToString(element.getTextTrim());
        //写入工序参装件 add by liangbo 20180508
        Element stepParts = procedure.element("parts");
        String stepCzjStr = getCzjStr(stepParts);
        if("true".equals(isKey) && "".equals(stepCzjStr)){
        	gxnr = "关键工序"+gxnr;
        }
        if("true".equals(isKey) && !"".equals(stepCzjStr)){
			gxnr = "关键工序" + stepCzjStr + gxnr;
		}else if(!"true".equals(isKey) && !"".equals(stepCzjStr)){
			gxnr = stepCzjStr + gxnr;
		}
        if(row<=rows){
			totalWidth = printer.getFieldWidth(template, "内容_"+row);
		}else{
			totalWidth = printer.getFieldWidth(template, "内容_1");
		}
        if(gxnr != null && !"".equals(gxnr)) {
        	gxnr = gxnr.replace("@#$%^\\", "");
        	gxnr = gxnr.replaceAll("@#\\$", "");
        	gxnr = gxnr.replace("\\", "/");
        	gxnr = gxnr.replaceAll("WORKSPACE_PATH/", "");
        	gxnr = gxnr.replaceAll("<html>", "").trim();
        	gxnr = gxnr.replaceAll("<head>", "").trim();
        	gxnr = gxnr.replaceAll("</head>", "").trim();
        	gxnr = gxnr.replaceAll("<body>", "").trim();
        	gxnr = gxnr.replaceAll("</body>", "").trim();
        	gxnr = gxnr.replaceAll("</html>", "").trim();
        	gxnr = gxnr.replaceAll("<!--EndFragment-->", "").trim();
        	gxnr = gxnr.replaceAll("<br>", "").trim();
        	if(gxnr.contains("<p style='margin-top: 0'>")) {
        		gxnr = gxnr.replaceAll("<p style='margin-top: 0'>", "").trim();
        		//gxnr = gxnr.replaceAll("</p>", "").trim();
        	}
        	if(gxnr.contains("<p style='margin-top:5'>")) {
        		gxnr = gxnr.replaceAll("<p style='margin-top:5'>", "").trim();
        		//gxnr = gxnr.replaceAll("</p>", "").trim();
        	}
        	//为了计算字符方便，替换字符串中&nbsp;为特殊符号♣，标识该处为空格，在写入时再替换回来
        	gxnr = gxnr.replace("&nbsp;", "♣").trim();

        	//替换字符串中</p>为特殊符号♂，标识该处为换行
        	gxnr = gxnr.replaceAll("</p>", "♂").trim();
        	gxnr=replaceTeShuFuHao(gxnr);
            //替换表示图片的内为特殊符号(♀)，以方便写入
            String temp = PDFUtil.Html2Text(gxnr);
            //获取所有的图片内容(content/*.png)
            List<String> allList = PDFUtil.getImgStr(gxnr);
            Object[] wholeStr = PDFUtil.getWholeStr(temp);
            temp = (String)wholeStr[2];
            writeProcessGongXu(temp, totalWidth, printer, "内容_",row,rows,templateName,allList,wholeStr);
        }

        if(isNew) {
        	index += r2;
        } else {
        	index += r1;
        }

        if(!templateName.equals(formName)) {
        	templateName = formName;
        }

        //恢复默认值
  		isNew = false;
  		r1 = 0;
  		r2 = 0;

  		int c = 1;
        //获取当前工序所有工步的工步内容
		List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo");
		if(stepList == null || stepList.isEmpty()) {
			index++;
			return;
		}
		for (Element step : stepList) {
			int tempRow = index;
			boolean isB = isB(step);
            String str = PDFUtil.getKZD(step);
//			String gbsbValues = getElementValues((Element)step.selectNodes("equips").get(0),"QMEquipmentInfo","pindex");
		    String gbsbValues=getElementValues2((Element)step.selectNodes("equips").get(0),"QMEquipmentInfo",keys);
            writeProcess(gbsbValues, 5, printer, "设备_",tempRow,templateName);
            String gbgzValues = getElementValues((Element)step.selectNodes("tools").get(0),"QMToolInfo","toolNum");
            writeProcess(gbgzValues, 11, printer, "工艺装备_",tempRow,templateName);
            isKey = PDFUtil.objectToString(step.attributeValue("isKey"));
			Element mEle = (Element)step.elements("procedureContent").get(0);
			String gbnr = PDFUtil.objectToString(mEle.getTextTrim());

			if(tempRow<=rows){
				totalWidth = printer.getFieldWidth(template, "内容_"+tempRow);
			}else{
				totalWidth = printer.getFieldWidth(template, "内容_1");
			}

	        if(gbnr != null && !"".equals(gbnr)) {
	        	gbnr = gbnr.replace("@#$%^\\", "");
	        	gbnr = gbnr.replaceAll("@#\\$", "");
	        	gbnr = gbnr.replace("\\", "/");
	        	gbnr = gbnr.replaceAll("WORKSPACE_PATH/", "");
	        	gbnr = gbnr.replaceAll("<html>", "").trim();
	        	gbnr = gbnr.replaceAll("<head>", "").trim();
	        	gbnr = gbnr.replaceAll("</head>", "").trim();
	        	gbnr = gbnr.replaceAll("<body>", "").trim();
	        	gbnr = gbnr.replaceAll("</body>", "").trim();
	        	gbnr = gbnr.replaceAll("</html>", "").trim();
	        	gbnr = gbnr.replaceAll("<!--EndFragment-->", "").trim();
	        	gbnr = gbnr.replaceAll("<br>", "").trim();
	        	if(gbnr.contains("<p style='margin-top: 0'>")) {
	        		//gbnr = gbnr.substring(gbnr.indexOf("<p style='margin-top: 0'>") + 25, gbnr.indexOf("</p>"));
	        		gbnr = gbnr.replaceAll("<p style='margin-top: 0'>", "").trim();
	        		//gbnr = gbnr.replaceAll("</p>", "").trim();
	        	}
	        	if(gbnr.contains("<p style='margin-top:5'>")) {
	        		gbnr = gbnr.replaceAll("<p style='margin-top:5'>", "").trim();
	        		//gbnr = gbnr.replaceAll("</p>", "").trim();
	        	}
	        	//为了计算字符方便，替换字符串中&nbsp;为特殊符号♣，标识该处为空格，在写入时再替换回来
	        	gbnr = gbnr.replace("&nbsp;", "♣").trim();

	        	//替换字符串中</p>为特殊符号♂，标识该处为换行
	        	gbnr = gbnr.replaceAll("</p>", "♂").trim();
	        	gbnr=replaceTeShuFuHao(gbnr);
	            //替换表示图片的内为特殊符号(♀)，以方便写入
	            String temp = PDFUtil.Html2Text(gbnr);
	            //获取所有的图片内容(content/*.png)
	            List<String> allList = PDFUtil.getImgStr(gbnr);
	            Object[] wholeStr = PDFUtil.getWholeStr(temp);
	            temp = (String)wholeStr[2];
	            //恢复默认值
	      		//isNew = false;
	      		//r1 = 0;
	      		//r2 = 0;
//	            boolean isB = isB(step);
//	            String str = PDFUtil.getKZD(step);
//	            if("true".equals(isKey)){
//	            	 writeProcessGongBu(temp, WriterStandard.Form8, printer, "内容_",tempRow,16,templateName,allList,"(控制点)"+c++,isB);
//			    }else{
//			    }
	            //写入工序参装件 add by liangbo 20180508
				Element paceParts = step.element("parts");
		        String paceCzjStr = getCzjStr(paceParts);
	            if(paceCzjStr != null && !"".equals(paceCzjStr)){
					temp = paceCzjStr + temp;
				}
				writeProcessGongBu(temp, totalWidth, printer, "内容_", tempRow, rows, templateName, allList, str + c++, isB, wholeStr);

	            if(isNew) {
	            	index += r2;
	            } else {
	            	index += r1;
	            }

	            index++;

	            if(!templateName.equals(formName)) {
	            	templateName = formName;
	            }
	        }

	        //恢复默认值
      		isNew = false;
      		r1 = 0;
      		r2 = 0;
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

    		if(row > rows) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
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



	private int writeProcess(String valueStr,int count,LcmPdfPrinter printer,String key,int row,String templateName,List<String> allList) {
        char[] clArr = valueStr.toCharArray();

        //n用于计算valueStr总共需要几行来写入数据
        int n = clArr.length/count;

        //如果不能整除，则表示还需要多一行来写入余下的数据
        if(clArr.length%count != 0) {
        	n = n + 1;
        }

        //用于记录写入了第几个特殊符号的图片
        int m = 0;

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
    				//如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
        			if(clArr[col] == '♀') {
        				String path = allList.get(m++);
        				path = path.replace("/", File.separator);
        				String imgPath = techFloder + File.separator + path;

        				//计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
        				int[] img = PDFUtil.getImgWidth(imgPath);
        				if(img != null) {
        					int width = img[0];
        					int imageLength = width/25;
        					if(width%25 != 0) {
        						imageLength++;
        					}
        				}

        				//计算当前行还剩余多少个字符空间没有写


        				value = value + PDFUtil.getImgHtmlCode(imgPath);
        			} else {
        				value = value + clArr[col];
        			}
    			}
    		}

    		if(row > 16) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
    			//记录当前新曾了页面
    			b = true;
    			//新增加了页面，则重新开始计数
    			k = 0;

    			//行指针重新从第一行开始写数据
    			index = 1;

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
    		printer.addHtml(templateName, key+row, value);

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




	private String getElementValues(Element ele,String qname,String key) {
		String values = "";
		List<Element> list = ele.elements(qname);
		for (Element element : list) {
			String temp = element.attributeValue(key);
			if(temp != null && !"".equals(temp)) {
				if(temp.startsWith("A%")) {
					temp = temp.replaceAll("A%", "");
				}
				if(!"".equals(values)) {
					values = values +"," +temp;
				} else {
					values = temp;
				}
			}
		}
		return values;
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
