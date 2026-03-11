package com.glaway.mpm.pdf.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.glaway.mpm.pdf.CharUtil;
import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.XmlUtility;
import com.itextpdf.text.Font;

public class ReportForm4PDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String partOid;
	private String formName;
	//用于记录PDF的页数
	private int page = 2;
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
	public ReportForm4PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public ReportForm4PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "ReportForm4.pdf";
			//从第一页开始写数据
			setEcnParams(params);
			formName = formName + "_" + page;
			templateList.add(formName);
			printer.addTempl(formName, template);
//			printer.addText(formName, "更改单号",params.get("ecnNo"));
			//记录基本数据，比如工艺文件编号等
			setCommData(printer,formName);
			//记录是否有数据
			boolean b = false;
			List<Element> procedureList = techElement.selectNodes("dataItemValue");
			if(procedureList != null && !procedureList.isEmpty()) {
				for(int i = 0;i < procedureList.size();i++) {
					Element mEle = procedureList.get(i);
					writeElement(techElement,mEle,printer,index,formName);
//					index++;
					b = true;
				}
				for (int i = 0; i < templateList.size(); i++) {

					printer.addText(templateList.get(i), "页数", "共"+String.valueOf(page)+"页");
				}
				String temp = Form1PDFBuilder.temp;
				if (temp!=null&&!"".equals(temp)&&!"null".equals(temp)) {
					printer.addText(temp, "页码总数", "共"+String.valueOf(page)+"页");
					printer.addText(temp, "PBOM版本", "P:"+PDFUtil.objectToString(techElement.attributeValue("partVersion")));
					printer.addText(temp, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
				}
							}

//			if(!b) {
//				printer.removeTempl(formName);
//				templateList.clear();
//			}
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

	private void writeElement(Element procedure,Element element,LcmPdfPrinter printer,int row,String templateName) {
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
        	templateList.add(formName);
			printer.addTempl(formName, template);
			//记录基本数据，比如工艺文件编号等
			setCommData(printer,formName);
		}
		String  n = null;
		 String page1 = element.attributeValue("shuliang");
		 if (page1!=null&&!"".equals(page1)&&!"null".equals(page1)) {
			n=page1;
		}
		 printer.addText(templateName, "序号_"+(row), (flag++)+"");
	        writeProcess(PDFUtil.objectToString(element.attributeValue("daihao")), 6, printer, "代号_",row,templateName);
	        String name =  PDFUtil.objectToString(element.attributeValue("mingcheng"));
	        writeProcess(name, 6, printer, "名称_",row,templateName);
	        printer.addText(templateName, "每产品数量_"+(row), PDFUtil.objectToString(element.attributeValue("mcpsl")));
	        printer.addText(templateName, "备件数量_"+(row), PDFUtil.objectToString(element.attributeValue("bjsl")));
	        printer.addText(templateName, "试验件数量_"+(row), PDFUtil.objectToString(element.attributeValue("syjsl")));
	        writeProcess(PDFUtil.objectToString(element.attributeValue("zhuzhibumen")), 2, printer, "主制部门_",row,templateName);
	        writeProcess(PDFUtil.objectToString(element.attributeValue("diyibumen")), 16, printer, "工艺过程_",row,templateName);
	        writeProcess(PDFUtil.objectToString(element.attributeValue("comment")), 4, printer, "备注_",row,templateName);
        printer.addText(templateName, "当前页数", "第"+String.valueOf(page)+"页");
        printer.addText(templateName, "当前页数2", "第"+String.valueOf(page)+"页");

        if(isNew) {
        	index += r2;
        } else {
        	index += r1;
        }
	}

	private int writeProcess(String valueStr,int count,LcmPdfPrinter printer,String key,int row,String templateName) {
        char[]  clArr=valueStr.toCharArray();
        //n用于计算valueStr总共写了多少行
        int n = 1;
        //用于记录写入了第几个特殊符号的图片
        int m = 0;
        //记录当前数据占了多少行
        int k = 0;
        boolean b = false;
        //用于记录当前行还剩余几个字符空间
        float tempCount = count;
        //用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
        String value = "";
        //标记是否换行
        boolean isNext = false;
        //循环n,逐个读取字符串的字符
        for(int i=0;i<clArr.length;i++) {
                if(CharUtil.isChinese(String.valueOf(clArr[i]))) {
                    if(tempCount>1) {
                        tempCount = tempCount - 1;
                    } else {
                        tempCount = tempCount - 1;
                    }
                } else {
                    //tempCount = tempCount - 1;
                    tempCount = tempCount - 0.58f;
                }
                value = value + clArr[i];

            if(!isNext && tempCount > 0 && i < (clArr.length-1)) {
                continue;
            }

            if(value == null || "".equals(value.trim())) {
                isNext = false;
                continue;
            }

            if(row > 16) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
                //记录当前新曾了页面
                b = true;
                //新增加了页面，则重新开始计数
                k = 0;
                //行指针重新从第一行开始写数据
                index = 1;
                row = 1;
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
            }

            //记录占用了多少行
            k++;
            printer.addText(templateName, key+row, value);
            row++;
            n++;
            tempCount = count;
            value = "";
            isNext = false;
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
