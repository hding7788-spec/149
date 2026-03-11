package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.pdf.CharUtil;
import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.pdf.WriterStandard;

import com.glaway.mpm.util.XmlUtility;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;


import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

public class SopForm3PDFBuilder extends PDFBuilder {
	    private String templateName;
	    private int yjwjPage = 1;
	    private int descriptionPage = 1;
	    private int yijvRow = 1;
	    private List<String> allList;
	    //记录行数
	    private int row = 1;
	    float yjwjTempCount = 23;
	    float descriptionTemCount = 30;
	    private List<String> templateList = new ArrayList<String>();


    public SopForm3PDFBuilder(Element techElement, String filePath, String formName) {
        this.techElement = techElement;
        this.techFloder = filePath;
        this.formName = formName;
    }

    @Override
    public void buildPDF(LcmPdfPrinter printer, List<Map<String, String>> partList, Map<String, String> params) {
    	try {
			template = PDFUtil.getFormTemplateFolderPath() + "SopForm3.pdf";
			templateList.add(formName);
			printer.addTempl(formName, template);
			setEcnParams(params);
			setCommData(printer,formName);
			templateName = formName;
            List<Element> borrowTechnics = XmlUtility.getBorrowTechnics(techElement);
            for(Element element : borrowTechnics){
//                String number = element.attributeValue("technicsNumber");
//                String name  = element.attributeValue("technicsName");
                String type = element.attributeValue("technicsType");
                if("国家标准".equals(type)){
                    writeYJWJElement(element,printer,params);
                }
            }
            templateName = formName;
            row = 1;
            String technicsDescribe = XmlUtility.getTechnicsDescribe(techElement);
            if(technicsDescribe != null && !technicsDescribe.isEmpty()){
                writeOtherDescription(technicsDescribe, printer, params);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void writeOtherDescription(String tecDescription, LcmPdfPrinter printer, Map<String, String> params) throws DocumentException, IOException {
    	Object[] wholeStr = transDecContent(tecDescription);
		List<String> values = (List<String>) wholeStr[0];
		int wholeIndex = 0;
		int m = 0;
		tecDescription = (String) wholeStr[2];
		char[] clArr = tecDescription.toCharArray();
		if (row > 12) {
			descriptionTemCount = printer.getFieldWidth(template, "其他说明_" + (row - 12));
		} else {
			descriptionTemCount = printer.getFieldWidth(template, "其他说明_" + row);
		}
		float tempCount = descriptionTemCount;
		String outputValue = "";
		boolean isNext = false;
		for (int i = 0; i < clArr.length; i++) {
			if (clArr[i] == '♀') {
				String path = allList.get(m++);
				path = path.replace("/", File.separator);
				String imgPath = techFloder + File.separator + path;
				// 计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
				int[] img = PDFUtil.getImgWidth(imgPath);
				if (img != null) {
					int width = img[0];
					if (width > tempCount) {// 剩余长度不够，则换到下一行写
						i--;// 需要回退一个字符
						m--;
						tempCount = 0;
					} else {
						tempCount = tempCount - width;
						outputValue = outputValue + PDFUtil.getImgHtmlCode(imgPath);
					}
				} else {
					System.out.println(imgPath + " is not exsit!");
				}
			} else if (clArr[i] == '♂') {
				isNext = true;
			} else if (clArr[i] == '♣') {
				tempCount = tempCount - baseFont.getWidthPoint(" ", 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + "&nbsp;";
				}
			} else if (clArr[i] == '❤') {
				tempCount = tempCount - baseFont.getWidthPoint("\"", 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + "&quot;";
				}
			} else if (clArr[i] == '♠') {
				tempCount = tempCount - baseFont.getWidthPoint(">", 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + "&gt;";
				}
			} else if (clArr[i] == '♥') {
				tempCount = tempCount - baseFont.getWidthPoint("<", 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + "&lt;";
				}
			} else if (clArr[i] == '♦') {
				String printvalue = values.get(wholeIndex);
				float tempLength = baseFont.getWidthPoint(printvalue, 12f);
				if (descriptionTemCount < tempLength) {
					tempLength = baseFont.getWidthPoint(message, 12f);
					System.out.println(values.get(wholeIndex) + "内容过长，输出有误，请分割后输出！");
					JOptionPane.showMessageDialog(null, values.get(wholeIndex) + "内容过长，输出有误，请分割换行后输出！", "提示", 1);
					JOptionPane.showMessageDialog(null, "字符过长会造成PDF数据丢失，请引起重视", "提示", 1);
				}
				tempCount = tempCount - tempLength;
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + printvalue;
					wholeIndex++;
				}
			} else {
				tempCount = tempCount - baseFont.getWidthPoint(clArr[i], 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + clArr[i];
				}
			}
			if (!isNext && tempCount > 0 && i < (clArr.length - 1)) {
				continue;
			}
			if (outputValue == null || "".equals(outputValue.trim())) {
				isNext = false;
				continue;
			}
			if (row > 12) {
				row = 1;
				descriptionPage++;
				templateName = formName + "_" + descriptionPage;
				if (!templateList.contains(templateName)) {
					templateList.add(templateName);
					printer.addTempl(templateName, template);
					setEcnParams(params);
	                setCommData(printer,templateName);
				}
			}
			printer.addHtml2Blod(templateName, "其他说明_" + row, outputValue, Font.NORMAL);
			row++;
			tempCount = descriptionTemCount;
			outputValue = "";
			isNext = false;
		}
    }

    /**
     *
     * @param borrowEle
     * @param printer
     */
    private void writeYJWJElement(Element borrowEle,LcmPdfPrinter printer, Map<String, String> params) {
        if(row > 12){
            row = 1;
            yjwjPage++;
            templateName = formName + "_" + yjwjPage;
            printer.addTempl(templateName, template);
            templateList.add(templateName);
            setEcnParams(params);
            setCommData(printer,templateName);
        }
        String number = borrowEle.attributeValue("technicsNumber");
        String name = borrowEle.attributeValue("technicsName");
        String valueStr = number + " " + name;
        char[] clArr = valueStr.toCharArray();
        float tempCount = yjwjTempCount;
        String outputValue = "";
        boolean isOne = true;
        for(int i=0;i<clArr.length;i++) {
            if(CharUtil.isChinese(String.valueOf(clArr[i]))) {
                //如果是中文，长度 -1
                tempCount = tempCount - 1;
            } else {
                //不是中文，长度 -0.58
                tempCount = tempCount - 0.58f;
            }
            outputValue = outputValue + clArr[i];
            if(tempCount > 0 && i < (clArr.length-1)) {
                continue;
            }
            if(outputValue == null || "".equals(outputValue.trim())) {
                continue;
            }
            if(row > 12){
                row = 1;
                yjwjPage++;
                templateName = formName + "_" + yjwjPage;
                printer.addTempl(templateName, template);
                templateList.add(templateName);
                setEcnParams(params);
                setCommData(printer,templateName);
            }
            if(isOne){
            	printer.addText(templateName, "依据文件_" + row, yijvRow+" : "+outputValue);
            }else{
            	printer.addText(templateName, "依据文件_" + row, outputValue);
            }
            isOne = false;
            row++;
            tempCount = yjwjTempCount;
            outputValue = "";
        }
        yijvRow++;
    }

    @Override
    public void setCommData(LcmPdfPrinter printer, String templateName) {
        printer.addText(templateName, "文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "文件名称", PDFUtil.objectToString(techElement.attributeValue("pplanName")));
        printer.addText(templateName, "版本号", PDFUtil.objectToString(techElement.attributeValue("version")));

        printer.addText(templateName, "定制区域", PDFUtil.objectToString(techElement.attributeValue("CustomArea")));
        printer.addText(templateName, "操作岗位", PDFUtil.objectToString(techElement.attributeValue("OperationJob")));
        //printer.addText(templateName, "其他说明", PDFUtil.objectToString(techElement.attributeValue("description")));

        printer.addText(templateName, "编制", "");
        printer.addText(templateName, "编制时间", "");
        printer.addText(templateName, "校对", "");
        printer.addText(templateName, "校对时间", "");
        printer.addText(templateName, "审核", "");
        printer.addText(templateName, "审核时间", "");

        printer.addText(templateName, "会签_姓名_1", "");
        printer.addText(templateName, "会签_部门_1", "");
        printer.addText(templateName, "会签_时间_1", "");
        printer.addText(templateName, "会签_姓名_2", "");
        printer.addText(templateName, "会签_部门_2", "");
        printer.addText(templateName, "会签_时间_2", "");

        printer.addText(templateName, "更改标记", ecnBiaoJi);
        printer.addText(templateName, "更改单号", ecnNo);
        printer.addText(templateName, "更改签名", "");
        printer.addText(templateName, "日期", "");
    }

    @Override
    public String getTechFloder() {
        return techFloder;
    }

    @Override
    public List<String> getTemplateList() {
        return templateList;
    }

    private Object[] transDecContent(String gbnr) {
		Object[] wholeStr = null;
		if (gbnr != null && !"".equals(gbnr)) {
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
			if (gbnr.contains("<p style='margin-top: 0'>")) {
				gbnr = gbnr.replaceAll("<p style='margin-top: 0'>", "").trim();
			}
			if (gbnr.contains("<p style='margin-top:5'>")) {
				gbnr = gbnr.replaceAll("<p style='margin-top:5'>", "").trim();
			}
			// 为了计算字符方便，替换字符串中&nbsp;为特殊符号♣，标识该处为空格，在写入时再替换回来
			gbnr = gbnr.replace("&nbsp;", "♣").trim();
			// 替换字符串中</p>为特殊符号♂，标识该处为换行
			gbnr = gbnr.replaceAll("</p>", "♂").trim();
			gbnr = replaceTeShuFuHao(gbnr);
			// 替换表示图片的内为特殊符号(♀)，以方便写入
			String temp = PDFUtil.Html2Text(gbnr);
			// 获取所有的图片内容(content/*.png)
			allList = PDFUtil.getImgStr(gbnr);
			wholeStr = PDFUtil.getWholeStr(temp);
		}
		return wholeStr;
	}
}

