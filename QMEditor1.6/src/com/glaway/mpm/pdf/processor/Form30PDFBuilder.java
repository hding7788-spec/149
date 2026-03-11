package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Form30PDFBuilder extends PDFBuilder{

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
	public Form30PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form30PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		try {
			//PDF模板文件
			template = PDFUtil.getFormTemplateFolderPath() + "Form30.pdf";
			//从第一页开始写数据
			formName = formName + "_" + page;
			templateList.add(formName);
			printer.addTempl(formName, template);
			//记录基本数据，比如工艺文件编号等
			setEcnParams(params);
			setCommData(printer,formName);
//			printer.addText(formName, "更改单号",params.get("ecnNo"));
			//记录是否有数据
			boolean b = false;

			List<Element> steps = techElement.selectNodes("steps/QMProcedureInfo");
			//开始循环遍历所有工序
			List<Element> eleList = new ArrayList<Element>();
			for (Element procedure : steps) {
				//获取当前工序的所有工艺辅料元素
				List<Element> procedureList = procedure.selectNodes("materials/QMMaterialInfo");
				for(int i = 0;i < procedureList.size();i++) {
					Element mEle = procedureList.get(i);
					eleList.add(mEle);
//					writeElement(procedure,mEle,printer,index,formName);
//					b = true;
				}
				//获取当前工序的所有工步的工艺辅料元素
				List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/materials/QMMaterialInfo");
				for(int i = 0;i < stepList.size();i++) {
					Element mEle = stepList.get(i);
					eleList.add(mEle);
//					writeElement(procedure,mEle,printer,index,formName);
//					b = true;
				}
			}
			//过滤删除集合中的重复元素,并累加数量
			for(int i = 0; i < eleList.size(); i++){
				for(int j = eleList.size() - 1; j > i; j--){
					Element ele1 = eleList.get(i);
					Element ele2 = eleList.get(j);
					String toolNumber1 = ele1.attributeValue("materialNumber");
					String count1 = ele1.attributeValue("sl");
					if(count1 == null || count1.equals("")){
						count1 = "0";
					}
					String toolNumber2 = ele2.attributeValue("materialNumber");
					String count2 = ele2.attributeValue("sl");
					if(count2 == null || count2.equals("")){
						count2 = "0";
					}
					if(toolNumber1.equals(toolNumber2)){
						eleList.remove(j);
//						if(count1.contains(".") || count2.contains(".")){
							double allCount = Double.parseDouble(count1) + Double.parseDouble(count2);
							ele1.setAttributeValue("sl", String.valueOf(allCount));
//						}else{
//							int allCount = Integer.parseInt(count1) + Integer.parseInt(count2);
//							ele1.setAttributeValue("sl", String.valueOf(allCount));
//						}
					}

				}
			}
			for(int i = 0;i < eleList.size();i++) {
				Element mEle = eleList.get(i);
				writeElement(null,mEle,printer,index,formName);

				b = true;
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
          	printer.addText(templateName, "批次号", PDFUtil.objectToString(techElement.attributeValue("PCNO")));
		printer.addText(templateName, "标识", "辅材定额");

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

	private void writeElement(Element procedure,Element element,LcmPdfPrinter printer,int row,String templateName) {
		//恢复默认值
		isNew = false;
		r1 = 0;
		r2 = 0;

		if(row>15) {
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

		printer.addText(templateName, "序号_"+(row), (flag++)+"");
		printer.addText(templateName, "计量单位_"+(row), PDFUtil.objectToString(element.attributeValue("jldw")));
        printer.addText(templateName, "数量_"+(row), PDFUtil.objectToString(element.attributeValue("sl")));
//        printer.addText(templateName, "使用车间_"+(row), PDFUtil.objectToString(procedure.attributeValue("workShop")));
        printer.addText(templateName, "使用车间_"+(row), PDFUtil.objectToString(element.attributeValue("sycj")));
//        printer.addText(templateName, "备注_"+(row), PDFUtil.objectToString(element.attributeValue("bz")));
        String bz = PDFUtil.objectToString(element.attributeValue("bz"));
        writeProcess(bz, 5, printer, "备注_",row,templateName);
        String dh = PDFUtil.objectToString(techElement.attributeValue("CINDEX"));
        writeProcess(dh, 10, printer, "代号_",row,templateName);

        String partName = PDFUtil.objectToString(techElement.attributeValue("partName"));
        writeProcess(partName, 7, printer, "名称_",row,templateName);

        //材料名称每格只写7个字符，多余的写在下一行的
        String clname = PDFUtil.objectToString(element.attributeValue("materialName"));
        writeProcess(clname, 7, printer, "材料名称_",row,templateName);

        String ph = PDFUtil.objectToString(element.attributeValue("mindex"));
        writeProcess(ph, 7, printer, "牌号_",row,templateName);

        String gg = PDFUtil.objectToString(element.attributeValue("csize"));
        writeProcess(gg, 7, printer, "规格_",row,templateName);

        String jstj = PDFUtil.objectToString(element.attributeValue("jstj"));
        writeProcess(jstj, 11, printer, "技术条件_",row,templateName);

        String pzgg = PDFUtil.objectToString(element.attributeValue("clgg"));
        writeProcess(pzgg, 11, printer, "品种规格_",row,templateName);

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

    		if(row > 15) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
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
