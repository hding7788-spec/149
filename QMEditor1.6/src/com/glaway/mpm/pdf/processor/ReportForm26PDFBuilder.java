package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.pdf.*;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ReportForm26PDFBuilder extends PDFBuilder{

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
	private int gongxuhao = 1;
	//保存所有的PDF页面名称
	private List<String> templateList = new ArrayList<String>();
	public ReportForm26PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public ReportForm26PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "ReportForm26.pdf";
//		    template = PDFUtil.getFormTemplateFolderPath() + "Form8a.pdf";

			//从第一页开始写数据
			setEcnParams(params);
			formName = formName + "_" + 2;
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
				gongxuhao++;
				}
				for (int i = 0; i < templateList.size(); i++) {

					printer.addText(templateList.get(i), "页码_1", "共"+String.valueOf(page)+"页");
				}

				String temp = Form1PDFBuilder.temp;
				if (temp!=null&&!"".equals(temp)&&!"null".equals(temp)) {
					printer.addText(temp, "页码总数", "共"+String.valueOf(page)+"页");
					printer.addText(temp, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
					printer.addText(temp, "PBOM版本", "P:"+PDFUtil.objectToString(techElement.attributeValue("partVersion")));
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

		printer.addText(templateName, "产品代号", PDFUtil.objectToString(techElement.attributeValue("PINDEX")));
        printer.addText(templateName, "工艺文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "名称", PDFUtil.objectToString(techElement.attributeValue("partName")));
        printer.addText(templateName, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
        String phaseCode = PDFUtil.objectToString(techElement.attributeValue("PHASE_CODE"));
        String index = PDFUtil.getPhaseCodeIndex(phaseCode);
        printer.addText(templateName, "阶段标记_"+index, phaseCode);

        printer.addText(templateName, "页码_2", "第"+String.valueOf(page)+"页");
        printer.addText(templateName, "页码_3", "第"+String.valueOf(page)+"页");

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

	private void writeElement(Element procedure,Element element,LcmPdfPrinter printer,int row,String templateName) throws Exception {
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

		printer.addText(templateName, "序号_"+(row), (flag++)+"");
        writeProcess(PDFUtil.objectToString(element.attributeValue("partNumber")), 7, printer, "产品代号_",row,templateName);
        writeProcess( PDFUtil.objectToString(element.attributeValue("partName")), 7, printer, "名称_",row,templateName);
//        writeProcess(PDFUtil.objectToString(gongxuhao), 3, printer, "工序号_",row,templateName);
        printer.addText(templateName, "工序号_"+(row), PDFUtil.objectToString(element.attributeValue("gongxuhao")));
        printer.addText(templateName, "工序名称_"+(row), PDFUtil.objectToString(element.attributeValue("gongxumingcheng")));
        writeProcess(PDFUtil.objectToString(element.attributeValue("zzdw")), 6, printer, "制造单位_",row,templateName);
        writeProcess(PDFUtil.objectToString(element.attributeValue("beizhu")), 6, printer, "备注_",row,templateName);
		String kongzhineirong = PDFUtil.objectToString(element.attributeValue("kongzhineirong"));
        String kznr = PDFUtil.objectToString(element.attributeValue("kznrFlag"));
        if("新增".equals(kongzhineirong)){
			if(kznr != null && !"".equals(kznr)) {
				kznr=getHtmlString(kznr);
				//替换表示图片的内为特殊符号(♀)，以方便写入
				String temp = PDFUtil.Html2Text(kznr);
				//获取所有的图片内容(content/*.png)
				List<String> allList = PDFUtil.getImgStr(kznr);
//            	writeProcessDescribe(temp, WriterStandard.Form27, printer, "控制内容_",row,16,templateName,allList,number);
				writeProcessKznr(temp, WriterStandard.ReportForm26_KZNR, printer, "控制内容_",row,16,templateName,allList,"");
			}
		}else{
			String number = PDFUtil.objectToString(element.attributeValue("technicsNumber"));
			String filePath = WorkSpaceUtil.getTechnicsDirectory(number);
			if(filePath == null){
				filePath = WorkSpaceUtil.createTechnicsDirectory(number);
				Vector<Object> vector = MesParameterProcessor.getTechnicsByTechnicNumber(number);
				byte[] bytes = (byte[]) vector.get(1);
				TechnicsReleaseUtil.unZip(bytes, filePath);
			}

			if(kznr != null && !"".equals(kznr)) {
				kznr=getHtmlString(kznr);
				//替换表示图片的内为特殊符号(♀)，以方便写入
				String temp = PDFUtil.Html2Text(kznr);
				//获取所有的图片内容(content/*.png)
				List<String> allList = PDFUtil.getImgStr(kznr);
//            	writeProcessDescribe(temp, WriterStandard.Form27, printer, "控制内容_",row,16,templateName,allList,number);
				writeProcessKznr(temp, WriterStandard.ReportForm26_KZNR, printer, "控制内容_",row,16,templateName,allList,number);
			}
		}

        if(isNew) {
        	if(r2==0){
        		r2=1;
			}
        	index += r2;
        } else {
			if(r1==0){
				r1=1;
			}
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
	/**
	 * 将html格式标签过滤掉，提取图片路径和文本
	 * @author machongqi
	 * @param str
	 * @date 2015-6-4
	 * @return
	 */

	    private String getHtmlString(String str){
	        if(str!=null&&!"".equals(str)){
	            str = str.replace("@#$%^\\", "");
	            str = str.replaceAll("@#\\$", "");
	            str = str.replace("\\", "/");
	            str = str.replaceAll("WORKSPACE_PATH/", "");
	            str = str.replaceAll("<html>", "").trim();
	            str = str.replaceAll("<head>", "").trim();
	            str = str.replaceAll("</head>", "").trim();
	            str = str.replaceAll("<body>", "").trim();
	            str = str.replaceAll("</body>", "").trim();
	            str = str.replaceAll("</html>", "").trim();
	            str = str.replaceAll("<br>", "").trim();
	        if(str.contains("<p style='margin-top: 0'>")) {
	            str = str.replaceAll("<p style='margin-top: 0'>", "").trim();
	            //gxnr = gxnr.replaceAll("</p>", "").trim();
	        }
	        if(str.contains("<p style='margin-top:5'>")) {
	            str = str.replaceAll("<p style='margin-top:5'>", "").trim();
	            //gxnr = gxnr.replaceAll("</p>", "").trim();
	        }
	        //为了计算字符方便，替换字符串中&nbsp;为特殊符号♣，标识该处为空格，在写入时再替换回来
	        str = str.replace("&nbsp;", "♣").trim();

	        //替换字符串中</p>为特殊符号♂，标识该处为换行
	        str = str.replaceAll("</p>", "♂").trim();
	        str=replaceTeShuFuHao(str);
	        }
	        return str;
	    }
	    public  int writeProcessKznr(String valueStr,int count,LcmPdfPrinter printer,String key,int row,int pdfrow,String templateName,List<String> allList,String number) {

//	      List<String>  list=new  ArrayList<String>();
//	      String  newValueStr=floatDateHuanHang(list,valueStr);
//	      char[]  clArr=newValueStr.toCharArray();
	//
//	      int c=0;
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
	            //tempCount--;

	            //如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
	            if(clArr[i] == '♀') {
	                String path = allList.get(m++);
	                path = path.replace("/", File.separator);
					String imgPath = "";
	                if(!"".equals(number)){
						String idString=techFloder.split("technics\\\\")[0]+"technics\\"+number;
						imgPath = idString + File.separator + path;
					}else{
						imgPath = path;
					}
//	                String imgPath = techFloder + File.separator + path;
	                System.out.println("----------->>>>"+path);
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
	                    System.out.println(path+" is not exsit!");
	                    //value = value + PDFUtil.getImgHtmlCode(imgPath);
	                }
	            } else if (clArr[i] == '♂') {
	                isNext = true;
	            } else if (clArr[i] == '❤') {
	                value = value + "&quot;";
	                tempCount = tempCount - 0.58f;
	            }else if (clArr[i] == '♠') {
	                value = value + "&gt;";
	                tempCount = tempCount - 0.58f;
	            }else if (clArr[i] == '♥') {
	                value = value + "&lt;";
	                tempCount = tempCount - 0.58f;
	            } else if (clArr[i] == '♣') {
	                value = value + "&nbsp;";
	                tempCount = tempCount - 1;
	            } else if (WriterStandard.speWord.contains(clArr[i]+"")) {
	                value = value + clArr[i];
	                tempCount = tempCount - 2f;
	            }
//	          else if(clArr[i] == '☀'){
//	                 String str=list.get(c);
//	                 float length=str.toCharArray().length;
//	                 length=length*0.5f;
//	                 if(length>tempCount){
//	                      i--;//需要回退一个字符
//	                      m--;
//	                      tempCount = 0;
//	                 }
//	                 else{
//	                     tempCount = tempCount - length;
//	                     value=value+str;
//	                     c++;
//	                 }
//	          }
	            else {
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

//	          if(!isNext && tempCount > 0 && i < (clArr.length-1)) {
//	              continue;
//	          }
	            if(!isNext && tempCount > 0 && i < (clArr.length-1)) {
	                continue;
	            }

	            if(value == null || "".equals(value.trim())) {
	                isNext = false;
	                continue;
	            }

	            if(row > pdfrow) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
	                //记录当前新曾了页面
	                b = true;
	                //新增加了页面，则重新开始计数
	                k = 0;
	                isNew = true;
	                //行指针重新从第一行开始写数据
	                index = 1;

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
	                    //if(!templateList.contains(formName)){
	                        templateList.add(formName);
	                    //}
	                    printer.addTempl(formName, template);
	                    //记录基本数据，比如工艺文件编号等
	                    setCommData(printer,formName);
	            }

	            //记录占用了多少行
	            k++;
	            //写入PDF的key+(row)格子中
	            printer.addHtml(templateName, key+row,value);
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
}
