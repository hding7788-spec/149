package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.pdf.*;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.TemplateIntf;
import com.itextpdf.text.Font;
import org.apache.commons.io.IOUtils;
import org.dom4j.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Form45PDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String partOid;
	private String formName;
	private String imgTemName;
	private String contentTemName;
	private Element stepElement;
	//用于记录PDF的页数
	private int page = 1;
	private int imgPage = 1;
	private int contentPage = 1;
	private String template;
	//用于记录序号值
	private int flag = 1;
	private int index = 1;
	//保存所有的PDF页面名称
	private List<String> templateList = new ArrayList<String>();
	public Form45PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.imgTemName = formName;
		this.contentTemName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form45PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.imgTemName = formName;
		this.contentTemName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "Form45.pdf";
			setEcnParams(params);
			//从第一页开始写数据
	          Boolean flag=false;
	            List<Element> stepList = techElement.selectNodes("steps/QMProcedureInfo");
	            //开始循环遍历所有工序
	            for (Element step : stepList) {
	            	imgPage = page;
					contentPage = page;
					stepElement = step;
//	                List<Element> jiantuEle = step.selectNodes("images/PDrawingInfo");
	                /**输出某一道工序下的所有简图*/
					List<Element> jiantuEle = step.selectNodes("images/PDrawingInfo");
					for (Element jiantu : jiantuEle) {
//						formName = formName + "_" + page;
						imgTemName = formName +"_"+ imgPage;
						templateList.add(imgTemName);
						writePDFElement(techElement, jiantu, printer, 1, imgTemName, step);
						imgPage++;
					}
					List<Element> paces = step.selectNodes("paces/QMProcedureInfo");
	                for(Element pace : paces){
	                	List<Element> pacePDrawings = pace.selectNodes("images/PDrawingInfo");
	                	for(Element pacePDrawing : pacePDrawings){
	                		imgTemName = formName +"_"+ imgPage;
	                		templateList.add(imgTemName);
	                		writePDFElement(techElement, pacePDrawing, printer, 1, imgTemName, step);
	                		imgPage++;
	                	}
	                }
				/** 输出某道工序的所有工序工步内容 */
	                index = 1;
					contentTemName = formName +"_"+ contentPage;
					printer.addTempl(contentTemName, template);
					setCommData(printer, contentTemName,step);
					templateList.add(contentTemName);
					Element procedureContent = (Element) step.selectNodes("procedureContent").get(0);
					writeElement(step, procedureContent, printer, (index), formName);
					contentPage ++;
					if(imgPage > contentPage){
						page = imgPage;
					}else{
						page = contentPage;
					}
				}
//	                for(Element jiantu : jiantuEle){
//	                    flag=true;
//	                    formName = formName + "_" + page;
//	                    temName = temName + "_" + page;
//	                    templateList.add(temName);
//	                    writePDFElement(techElement,jiantu,printer,1,temName,step);
//	                    page++;
//	                }
//	            if (!flag) {
//	                formName = formName + "_" + page;
//	                temName = temName + "_" + page;
//	                templateList.add(formName);
//	                printer.addTempl(formName, template);
//
//	            }

//	            page = 1;
//	            formName = temName2 + "_" + page;
//	            for (Element procedure : stepList) {
//	                //获取当前工序的工序内容
//	                List<Element> procedureList = procedure.selectNodes("procedureContent");
//	                setCommData(printer,formName,procedure);
//	                for(int i = 0;i < procedureList.size();i++) {
//	                    Element mEle = procedureList.get(i);
//	                    writeElement(procedure,mEle,printer,(index),formName);
//	                }
//	            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void writeElement(Element procedure,Element element,LcmPdfPrinter printer,int row,String templateName) {
        //恢复默认值
        isNew = false;
        r1 = 0;
        r2 = 0;

        if(row>14) {
            index = 1;
            //如果已经到最后一行，则数据应该写到下一页,index从第一行开始
            row = 1;
            //新曾一页PDF开始写数据
            String cn = contentTemName.substring(contentTemName.lastIndexOf('_') + 1, contentTemName.length());
            if(cn != null && !"".equals(cn)) {
                int icn = Integer.valueOf(cn);
                if(contentPage > icn) {
                    icn++;
                    contentTemName = formName + "_" + icn;
                } else {
                	contentPage++;
                    contentTemName = formName + "_" + contentPage;
                }
            } else {
            	contentPage++;
                contentTemName = formName + "_" + contentPage;
            }
//            templateName = contentTemName;
            if (!templateList.contains(contentTemName)) {
                printer.addTempl(contentTemName, template);
                templateList.add(contentTemName);
//                setCommData(printer,formName);
                setCommData(printer, contentTemName, stepElement);
            }
            //记录基本数据，比如工艺文件编号等
        }


        //写入工序内容
        String gxnr = PDFUtil.objectToString(element.getTextTrim());
        //是否关键工序
        String isKey=PDFUtil.objectToString(procedure.attributeValue("isKey"));
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
            }
            if(gxnr.contains("<p style='margin-top:5'>")) {
                gxnr = gxnr.replaceAll("<p style='margin-top:5'>", "").trim();
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
            writeProcessNew(temp, 20, printer, "内容_",row,contentTemName,allList);
        }

        if(isNew) {
            index += r2;
        } else {
            index += r1;
        }

//        if(!templateName.equals(formName)) {
//            templateName = formName;
//        }

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

            Element mEle = (Element)step.elements("procedureContent").get(0);
            String gbnr = PDFUtil.objectToString(mEle.getTextTrim());
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
                System.out.println("-------gbnr1--"+gbnr);
                //替换表示图片的内为特殊符号(♀)，以方便写入
                String temp = PDFUtil.Html2Text(gbnr);
                System.out.println("-------gbnr2--"+gbnr);
                //获取所有的图片内容(content/*.png)
                List<String> allList = PDFUtil.getImgStr(gbnr);
                boolean isB = isB(step);
                String paceKZD = PDFUtil.getKZD(step); // add by liangbo   17/02/27
                //写入工序参装件 add by liangbo 20180508
				Element paceParts = step.element("parts");
		        String paceCzjStr = getCzjStr(paceParts);
	            if(paceCzjStr != null && !"".equals(paceCzjStr)){
					temp = paceCzjStr + temp;
				}
                writeProcess2New(temp, 20, printer, "内容_",tempRow,formName,allList,paceKZD+c++,isB);

                if(isNew) {
                    index += r2;
                } else {
                    index += r1;
                }

                index++;

//                if(!templateName.equals(formName)) {
//                    templateName = formName;
//                }
            }

            //恢复默认值
            isNew = false;
            r1 = 0;
            r2 = 0;
        }
    }
	private int writeProcess2New(String valueStr,int count,LcmPdfPrinter printer,String key,int row,String templateName,List<String> allList,String c, boolean isB) {
        valueStr = c+","+valueStr;
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
        System.out.println("--writeProcess2New---valueStr-----"+valueStr);
        //循环n,逐个读取字符串的字符
        for(int i=0;i<clArr.length;i++) {
            //tempCount--;

            /*if(i == 0) {
                value = c+",";
            }*/

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
                tempCount = tempCount - 1;
                value = value + "&nbsp;";
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
            }  else {
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
                continue;
            }

            if(row > 14) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
                //记录当前新曾了页面
                b = true;
                //新增加了页面，则重新开始计数
                k = 0;

                //行指针重新从第一行开始写数据
                index = 1;

                row = 1;

//              if(!isNew) {
                    isNew = true;
                    //新曾一页PDF开始写数据
                    String cn = contentTemName.substring(contentTemName.lastIndexOf('_') + 1, contentTemName.length());
                    if(cn != null && !"".equals(cn)) {
                        int icn = Integer.valueOf(cn);
                        if(contentPage > icn) {
                            icn++;
                            contentTemName = formName + "_" + icn;
                        } else {
                        	contentPage++;
                            contentTemName = formName + "_" + contentPage;
                        }
                    } else {
                    	contentPage++;
                        contentTemName = formName + "_" + contentPage;
                    }
                    if(!templateList.contains(contentTemName)){
                        templateList.add(contentTemName);
                        printer.addTempl(contentTemName, template);
                        //记录基本数据，比如工艺文件编号等
//                        setCommData(printer,formName);
                        setCommData(printer, contentTemName);

                    }
//              } else {
//                  //如果已经存在新增页面，则直接在新增页面上写入数据
//                  templateName = formName;
//              }
            }

            //记录占用了多少行
            k++;

            //写入PDF的key+(row)格子中
            //modify by machongqi 2015-6-12
            if(isB){
                printer.addHtml2Blod(contentTemName, key+row, value,Font.BOLD);
            }else {
                printer.addHtml2Blod(contentTemName, key+row, value,Font.NORMAL);
            }
            //modify by machongqi end

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
	private int writeProcessNew(String valueStr,int count,LcmPdfPrinter printer,String key,int row,String templateName,List<String> allList) {
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

            if(row > 14) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
                //记录当前新曾了页面
                b = true;
                //新增加了页面，则重新开始计数
                k = 0;
                isNew = true;
                row = 1;

                //新曾一页PDF开始写数据
                String cn = contentTemName.substring(contentTemName.lastIndexOf('_') + 1, contentTemName.length());
                if(cn != null && !"".equals(cn)) {
                    int icn = Integer.valueOf(cn);
                    if(contentPage > icn) {
                        icn++;
                        contentTemName = formName + "_" + icn;
                    } else {
                    	contentPage++;
                        contentTemName = formName + "_" + contentPage;
                    }
                } else {
                	contentPage++;
                    contentTemName = formName + "_" + contentPage;
                }

 //               templateName = formName;
                if(!templateList.contains(contentTemName)){
                    templateList.add(contentTemName);
                    printer.addTempl(contentTemName, template);
                    //记录基本数据，比如工艺文件编号等
//                    setCommData(printer,formName);
                    setCommData(printer, contentTemName, stepElement);
                }
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


	private void writePDFElement(Element technics,Element element,LcmPdfPrinter printer,int row,String templateName,Element stepElement) {

        String path = PDFUtil.objectToString(element.attributeValue("absolutePath"));
        System.out.println("-------path------"+path);
        if(path != null && !"".equals(path)) {
        	if(path.endsWith("dwg")){
        		String tempDocNumber = XmlUtility.getAttributeValue(element, "tempDocNumber");
        		String docNumber = XmlUtility.getAttributeValue(element, "docNumber");
        		float top = 1.9f;
        		float left = 4.9f;
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
    				System.out.println("------temName---" + imgTemName);
    				System.out.println("------templateName---" + templateName);
    				printer.addTechnicStatePDFImage(template, imgTemName, key, pdfFile,left, top);
//    				printer.addText(formName, "制造部门", "12");
    				setCommData(printer,imgTemName,stepElement);
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
        		printer.addTempl(imgTemName, template);
        		setCommData(printer,imgTemName,stepElement);
        		System.out.println("-------file path------"+(techFloder + File.separator + path));
        		printer.addImage(templateName, "图片", techFloder + File.separator + path);
        	}
        }
//        else{
//        	printer.addTempl(temName, template);
//        	setCommData(printer,temName,stepElement);
//        }
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
            	String cn = contentTemName.substring(contentTemName.lastIndexOf('_')+1, contentTemName.length());
        		if(cn != null && !"".equals(cn)) {
        			int icn = Integer.valueOf(cn);
        			if(contentPage > icn) {
        				icn++;
        				contentTemName = formName + "_" + icn;
        			} else {
        				contentPage++;
        				contentTemName = formName + "_" + contentPage;
        			}
        		} else {
        			contentPage++;
        			contentTemName = formName + "_" + contentPage;
        		}
//            	templateName = formName;
            	if (!templateList.contains(contentTemName)) {

            	    templateList.add(contentTemName);
            	    printer.addTempl(contentTemName, template);
            	    //记录基本数据，比如工艺文件编号等
            	    setCommData(printer, contentTemName, stepElement);
                }

    		}

    		System.out.println("------key----"+(key+row));
    		//写入PDF的key+(row)格子中
    		System.out.println("------value----"+value);
			printer.addHtml(templateName, key+row, value);

    		row++;
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
                    values = values +"；"+temp;
                } else {
                    values = temp;
                }
            }
        }
        return values;
    }
    public void setCommData(LcmPdfPrinter printer, String templateName,Element gongyi) {

        printer.addText(templateName, "工序号", PDFUtil.objectToString(gongyi.attributeValue("stepNumber")));
        String stepName = PDFUtil.objectToString(gongyi.attributeValue("stepEnglishName"));
		if("".equals(stepName)){
			stepName = PDFUtil.objectToString(gongyi.attributeValue("stepName"));
		}
		printer.addText(templateName, "工序名称", stepName);
        printer.addText(templateName, "车间", PDFUtil.objectToString(gongyi.attributeValue("workShop")));
      //工序的工装
        String gzValues = getElementValues((Element)gongyi.selectNodes("tools").get(0),"QMToolInfo","toolNum");
        printer.addText(templateName, "工艺装备", gzValues);
        String[] keys={"name","pindex"};
        String gbsbValues=getElementValues2((Element)gongyi.selectNodes("equips").get(0),"QMEquipmentInfo",keys);
        printer.addText(templateName, "设备", gbsbValues);
        String gysl = PDFUtil.objectToString(techElement.attributeValue("gysl"));
        if(gysl == null || "".equals(gysl)) {
            gysl = PDFUtil.objectToString(techElement.attributeValue("useCount"));
        }
        printer.addText(templateName, "产品数量", gysl);

        Element clde = XmlUtility.getTechnicsCLDEElement(techElement);
        if(clde != null) {
            List<Element> ycl = XmlUtility.getTechnicsYCLDE(clde);
            if(ycl != null && !ycl.isEmpty()) {
                Element ele = ycl.get(0);
                printer.addText(templateName, "材料牌号", PDFUtil.objectToString(ele.attributeValue("xhph"))+"  "+PDFUtil.objectToString(ele.attributeValue("gyztrcl")));
                printer.addText(templateName, "材料名称", PDFUtil.objectToString(ele.attributeValue("chmc")));
                printer.addText(templateName, "规格", PDFUtil.objectToString(ele.attributeValue("gg")));
                printer.addText(templateName, "技术条件_1", PDFUtil.objectToString(ele.attributeValue("fjtj")));
                printer.addText(templateName, "技术条件_2", PDFUtil.objectToString(ele.attributeValue("jstj")));
                printer.addText(templateName, "可制件数_1", PDFUtil.objectToString(ele.attributeValue("kzjs")));
                printer.addText(templateName, "产品毛坯尺寸", PDFUtil.objectToString(ele.attributeValue("xlcc")));
            } else {
                ycl = XmlUtility.getTechnicsSJZYKYCLDE(clde);
                if(ycl != null && !ycl.isEmpty()) {
                    Element ele = ycl.get(0);
                    printer.addText(templateName, "材料牌号", PDFUtil.objectToString(ele.attributeValue("ph"))+"  "+PDFUtil.objectToString(ele.attributeValue("gyzt")));
                    printer.addText(templateName, "材料名称", PDFUtil.objectToString(ele.attributeValue("name")));
                    printer.addText(templateName, "规格", PDFUtil.objectToString(ele.attributeValue("gg")));
                    //printer.addText(templateName, "技术条件_1", PDFUtil.objectToString(ele.attributeValue("fjtj")));
                    printer.addText(templateName, "技术条件_2", PDFUtil.objectToString(ele.attributeValue("cybz")));
                    printer.addText(templateName, "可制件数_1", PDFUtil.objectToString(ele.attributeValue("kzjs")));
                    printer.addText(templateName, "产品毛坯尺寸", PDFUtil.objectToString(ele.attributeValue("xlcc")));
                }
            }

            List<Element> sjycl = XmlUtility.getTechnicsSJYCLDE(clde);
            if(sjycl != null && !sjycl.isEmpty()) {
                Element ele = sjycl.get(0);
                printer.addText(templateName, "试件数量", PDFUtil.objectToString(ele.attributeValue("sjsl")));
                printer.addText(templateName, "试件毛坯尺寸", PDFUtil.objectToString(ele.attributeValue("sjcc")));
                printer.addText(templateName, "可制件数_2", PDFUtil.objectToString(ele.attributeValue("sjkzjs")));
            } else {
                sjycl = XmlUtility.getTechnicsSJZYKSJYCLDE(clde);
                if(sjycl != null && !sjycl.isEmpty()) {
                    Element ele = sjycl.get(0);
                    printer.addText(templateName, "试件数量", PDFUtil.objectToString(ele.attributeValue("sl")));
                    printer.addText(templateName, "试件毛坯尺寸", PDFUtil.objectToString(ele.attributeValue("sjcc")));
                    printer.addText(templateName, "可制件数_2", PDFUtil.objectToString(ele.attributeValue("sjkzjs")));
                }
            }
        }

        printer.addText(templateName, "PBOM版本", "P:"+PDFUtil.objectToString(techElement.attributeValue("partVersion")));
        //modify by machongqi 2015-7-2
            printer.addText(templateName, "工艺文件版本", PDFUtil.objectToString(techElement.attributeValue("version")));
        //modify by machongqi end
            printer.addText(templateName, "批次号", PDFUtil.objectToString(techElement.attributeValue("PCNO")));
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
