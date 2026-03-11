package com.glaway.mpm.pdf.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.Element;

import com.glaway.mpm.pdf.CharUtil;
import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.pdf.WriterStandard;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.TemplateIntf;

public class Form43PDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String partOid;
	private String formName;
	//用于记录PDF的页数
	private int page = 1;
	private String template;
	//用于记录序号值
	private int flag = 1;
	//保存所有的PDF页面名称
	private List<String> templateList = new ArrayList<String>();
	public Form43PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form43PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "Form43.pdf";
			setEcnParams(params);
			List<Element> gyztList = techElement.selectNodes("technicsStateTables/technicsStateTable");
			//开始循环遍历所有工序
			for (Element gyzt : gyztList) {
				//从第一页开始写数据
				formName = formName + "_" + page;
				templateList.add(formName);

				//记录基本数据，比如工艺文件编号等
//				printer.addText(formName, "更改单号",params.get("ecnNo"));
//				setCommData(printer,formName,gyzt);
				writeElement(techElement,gyzt,printer,1,formName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCommData(LcmPdfPrinter printer,String templateName,Element gyzt) {
		String zzdw = PDFUtil.objectToString(gyzt.attributeValue("zzdw"));
		String sydw = PDFUtil.objectToString(gyzt.attributeValue("sydw"));
		printer.addText(templateName, "制造部门",zzdw );
        printer.addText(templateName, "使用部门",sydw );

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

	private void writeElement(Element technics,Element element,LcmPdfPrinter printer,int row,String templateName) {
		printer.addText(templateName, "序号_1", (flag++)+"");

        String path = PDFUtil.objectToString(element.attributeValue("absolutePath"));
        System.out.println("-------path------"+path);
        if(path != null && !"".equals(path)) {
        	if(path.endsWith("dwg")){
        		String tempDocNumber = XmlUtility.getAttributeValue(element, "tempDocNumber");
        		String docNumber = XmlUtility.getAttributeValue(element, "docNumber");
        		float top = 1.2f;
        		float left = 2.9f;
        		String key = "图片";
        		byte[] dwgPdf =  TemplateIntf.getDwgPdfTemplateByteRMI(docNumber);// DWG->PDF
        		if(dwgPdf==null){
        			return;
        		}
        		String tempDir = this.getTechFloder() + File.separator+ "fbtemp";
        		File tempDirFile = new File(tempDir);
        		if(!tempDirFile.exists()) {
        			tempDirFile.mkdir();
        		}
        		String pdf = tempDir + File.separator + System.currentTimeMillis()+ ".pdf";
        		File pdfFile =  new File(pdf);
        		OutputStream dwgOs = null;
        		try {
    				dwgOs = new FileOutputStream(pdfFile);
    				IOUtils.write(dwgPdf, dwgOs);
    				System.out.println("------pdf---" + pdf);
    				System.out.println("------formName---" + formName);
    				System.out.println("------templateName---" + templateName);
    				printer.addTechnicStatePDFImage(template, formName, key, pdfFile,left, top);
//    				printer.addText(formName, "制造部门", "12");
    				setCommData(printer,formName,element);
//    				System.out.println("------list---" + list);
//    				templateList.addAll(list);
    			} catch (FileNotFoundException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			} finally{
        			if(dwgOs!=null)
						try {
							dwgOs.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        		}
        	}else{
        		printer.addTempl(formName, template);
        		setCommData(printer,formName,element);
        		System.out.println("-------file path------"+(techFloder + File.separator + path));
        		printer.addImage(templateName, "图片", techFloder + File.separator + path);
        	}
        }else{
        	printer.addTempl(formName, template);
        	setCommData(printer,formName,element);
        }
        List<Element> list = element.elements();
        for (Element element2 : list) {
        	//读取工艺状态值
        	String gyztValue = element2.getTextTrim();
        	if(gyztValue != null && !"".equals(gyztValue)) {
        		gyztValue = gyztValue.replace("@#$%^\\", "");
        		gyztValue = gyztValue.replaceAll("@#\\$", "");
        		gyztValue = gyztValue.replace("\\", "/");
        		gyztValue = gyztValue.replaceAll("WORKSPACE_PATH/", "");
        		gyztValue = gyztValue.replaceAll("<br>", "").trim();
        		if(gyztValue.contains("<p style='margin-top: 0'>")) {
        			gyztValue = gyztValue.replaceAll("<p style='margin-top: 0'>", "").trim();
        			//gxnr = gxnr.replaceAll("</p>", "").trim();
        		}
        		if(gyztValue.contains("<p style='margin-top:5'>")) {
        			gyztValue = gyztValue.replaceAll("<p style='margin-top:5'>", "").trim();
        			//gxnr = gxnr.replaceAll("</p>", "").trim();
        		}

        		//为了计算字符方便，替换字符串中&nbsp;为特殊符号♣，标识该处为空格，在写入时再替换回来
        		gyztValue = gyztValue.replace("&nbsp;", "♣").trim();

        		//替换字符串中</p>为特殊符号♂，标识该处为换行
        		gyztValue = gyztValue.replaceAll("</p>", "♂").trim();
        		gyztValue=replaceTeShuFuHao(gyztValue);
        		//替换表示图片的内为特殊符号(♀)，以方便写入
        		String temp = PDFUtil.Html2Text(gyztValue);
        		//获取所有的图片内容(content/*.png)
        		List<String> allList = PDFUtil.getImgStr(gyztValue);
        		System.out.println("-------allList------"+allList);
        		writeProcessNew(temp, 20, printer, "工艺状态_",row,templateName,allList,element);
        	}

		}
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
        int index = 0;

        //循环n行，将第n行的数据写入PDF页面的row+i行
        for(int i=0;i<n;i++) {
        	//用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
    		String value = "";
    		for(int j=0;j<count;j++) {
    			int col = count*i+j;
    			if(col<clArr.length) {
    				//如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
        			if(clArr[col] == '♀') {
        				String path = allList.get(index++);
        				path = path.replace("/", File.separator);
        				String imgPath = techFloder + File.separator + path;
        				value = value + PDFUtil.getImgHtmlCode(imgPath);
        			} else {
        				value = value + clArr[col];
        			}
    			}
    		}

    		if(value == null || "".equals(value.trim())) {
    			continue;
    		}

    		if(row > 16) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
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

    		System.out.println("------key----"+(key+row));
    		//写入PDF的key+(row)格子中
    		System.out.println("------value----"+value);
			printer.addHtml(templateName, key+row, value);

    		row++;
    	}

        return n;
	}

	private int writeProcessNew(String valueStr,int count,LcmPdfPrinter printer,String key,int row,String templateName,List<String> allList,Element gyzt) {
		char[] clArr = valueStr.toCharArray();
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
		System.out.println("--writeProcessNew---valueStr-----"+valueStr);
        for(int i=0;i<clArr.length;i++) {
        	//tempCount--;

    		//如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
			if(clArr[i] == '♀') {
				String path = allList.get(m++);
				path = path.replace("/", File.separator);
				String imgPath = techFloder + File.separator + path;

				//计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
				int[] img = PDFUtil.getImgWidth(imgPath);
				if(img != null) {
					int width = img[0];
					int imageLength = width/12;
					if(width%25 != 0) {
						imageLength++;
					}

					//计算当前行剩余多少个字符空间是否够写入该图片
					if(imageLength > tempCount) {//剩余长度不够，则换到下一行写
						i--;//需要回退一个字符
						m--;
						tempCount = 0;
					} else {
						tempCount = tempCount - imageLength;
						value = value + PDFUtil.getImgHtmlCode(imgPath);
					}
				} else {
					System.out.println(imgPath+" is not exsit!");
					//value = value + PDFUtil.getImgHtmlCode(imgPath);
				}
			} else if (clArr[i] == '♂') {
				isNext = true;
			} else if (clArr[i] == '♣') {
				value = value + "&nbsp;";
				tempCount = tempCount - 1;
			} else if (clArr[i] == '❤') {
                value = value + "&quot;";
                tempCount = tempCount - 0.58f;
            }else if (clArr[i] == '♠') {
                value = value + "&gt;";
                tempCount = tempCount - 0.58f;
            }else if (clArr[i] == '♥') {
                value = value + "&lt;";
                tempCount = tempCount - 0.58f;
            } else if (WriterStandard.speWord.contains(clArr[i]+"")) {
                value = value + clArr[i];
                tempCount = tempCount - 2f;
            } else {
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
			}

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
    			setCommData(printer,formName,gyzt);
    		}

    		//记录占用了多少行
    		k++;

    		//写入PDF的key+(row)格子中
    		/*if(value.contains("×")){
    			printer.addText(templateName, key+row, value);
    		}else{

    		}*/
    		//value = value.replaceAll("×", "×");
    		printer.addHtml(templateName, key+row, value);

    		row++;

    		n++;
    		tempCount = count;
    		value = "";
    		isNext = false;
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

	@Override
	public void setCommData(LcmPdfPrinter printer, String templateName) {

		printer.addText(templateName, "PBOM版本", "P:"+PDFUtil.objectToString(techElement.attributeValue("partVersion")));
		//modify by machongqi 2015-7-2
          	printer.addText(templateName, "工艺文件版本", PDFUtil.objectToString(techElement.attributeValue("version")));
  		//modify by machongqi end
          	printer.addText(templateName, "批次号", PDFUtil.objectToString(techElement.attributeValue("PCNO")));
//		printer.addText(templateName, "标识", PDFUtil.objectToString(techElement.attributeValue("BIAOSHI")));

		printer.addText(templateName, "产品代号", PDFUtil.objectToString(techElement.attributeValue("PINDEX")));
        printer.addText(templateName, "工艺文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "名称", PDFUtil.objectToString(techElement.attributeValue("partName")));
        printer.addText(templateName, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
        printer.addText(templateName, "图纸版本", PDFUtil.objectToString(techElement.attributeValue("imageVersion")));

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
}
