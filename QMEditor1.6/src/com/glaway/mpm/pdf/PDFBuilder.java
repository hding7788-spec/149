package com.glaway.mpm.pdf;

import com.glaway.mpm.pdf.processor.Form7GYFBPDFBuilder;
import com.glaway.mpm.pdf.processor.Form7GYFTPDFBuilder;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.XmlUtility;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import ext.casc.integrate.util.Constants;
import org.dom4j.Element;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PDFBuilder {

    protected String message = "【长字符】";
    public Element techElement;
    public String techFloder = null;
    public String partOid;
    public String formName;
    //用于记录PDF的页数
    public int page = 1;
    //用于记录当前PDF页面写到了第几行
    public int index = 1;
    public String template;
    //用于记录序号值
    public int flag = 1;
    //记录是否新增了PDF页面
    public boolean isNew = false;
    //记录没有新增PDF页面时的最大占有行数
    public int r1 = 0;
    //记录新增页面PDF时的最大占有行数
    public int r2 = 0;
    //保存所有的PDF页面名称

    public List<String> templateList = new ArrayList<String>();
    public String ecnNo;
    public String ecnBiaoJi;
    public static BaseFont baseFont = null;

    static {
        try {
            baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setEcnParams(Map<String, String> params) {
        ecnNo = params.get("ecnNo");
        if (ecnNo == null) {
            ecnNo = "";
        }
        ecnBiaoJi = params.get("ecnBiaoJi");
        if (ecnBiaoJi == null) {
            ecnBiaoJi = "";
        }
    }

    public abstract void setCommData(LcmPdfPrinter printer, String templateName);

    public abstract void buildPDF(LcmPdfPrinter printer, List<Map<String, String>> partList, Map<String, String> params);

    /**
     * 工艺文件夹
     *
     * @return
     */
    public abstract String getTechFloder();

    /**
     * 返回所有的模板名称
     *
     * @return
     */
    public abstract List<String> getTemplateList();


    public static String floatDateHuanHang(List<String> list, String valueStr) {
        char[] clArr = valueStr.toCharArray();
        int i = 0;
        int j = 0;
        for (int t = 0; t < clArr.length; t++) {
            if (clArr[t] == '.') {
                String str = String.valueOf(clArr[t]);
                if (Character.isDigit((clArr[t - 1])) && Character.isDigit((clArr[t + 1]))) {
                    for (i = t - 1; i > 0; i--) {
                        if (Character.isDigit((clArr[i]))) {
                            str = String.valueOf(clArr[i]) + str;
                        } else {
                            break;
                        }
                    }

                    for (j = t + 1; j < clArr.length; j++) {
                        if (Character.isDigit((clArr[j]))) {
                            str = str + String.valueOf(clArr[j]);
                        } else {
                            break;
                        }
                    }
                    list.add(str);
                    valueStr = valueStr.replace(str, "☀");
                    System.out.println(valueStr);
                    if (valueStr.contains("\\.")) {
                        floatDateHuanHang(list, valueStr);
                    }
                }

            }

        }
        return valueStr;
    }


    protected int writeProcessGongXu(String valueStr, int count, LcmPdfPrinter printer, String key, int row, int pdfrow, String templateName, List<String> allList) {
//    	List<String>  list=new  ArrayList<String>();
//		String  newValueStr=floatDateHuanHang(list,valueStr);
//		char[]  clArr=newValueStr.toCharArray();
//
//		int c=0;

        char[] clArr = valueStr.toCharArray();

        //n用于计算valueStr总共写了多少行
        int n = 1;

        //用于记录写入了第几个特殊符号的图片
        int m = 0;

        //记录当前数据占了多少行
        int k = 0;
        boolean b = false;

        //用于记录当前行还剩余几个字符空间
//        int tempCount = count;
        float tempCount = count;

        //用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
        String value = "";

        //标记是否换行
        boolean isNext = false;

        //循环n,逐个读取字符串的字符
        for (int i = 0; i < clArr.length; i++) {
            //tempCount--;

            //如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
            if (clArr[i] == '♀') {
                String path = allList.get(m++);
                path = path.replace("/", File.separator);
                String imgPath = techFloder + File.separator + path;

                //计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
                int[] img = PDFUtil.getImgWidth(imgPath);
                if (img != null) {
                    int width = img[0];
                    int imageLength = width / 12;
                    if (width % 25 != 0) {
                        imageLength++;
                    }

                    //计算当前行剩余多少个字符空间是否够写入该图片
                    if (imageLength > tempCount) {//剩余长度不够，则换到下一行写
                        i--;//需要回退一个字符
                        m--;
                        tempCount = 0;
                    } else {
                        tempCount = tempCount - imageLength;
                        value = value + PDFUtil.getImgHtmlCode(imgPath);
                    }
                } else {
                    System.out.println(imgPath + " is not exsit!");
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
            } else if (clArr[i] == '♠') {
                value = value + "&gt;";
                tempCount = tempCount - 0.58f;
            } else if (clArr[i] == '♥') {
                value = value + "&lt;";
                tempCount = tempCount - 0.58f;
            } else if (WriterStandard.speWord.contains(clArr[i] + "")) {
                value = value + clArr[i];
                tempCount = tempCount - 2f;
            }
//			else if(clArr[i] == '☀'){
//	               String str=list.get(c);
//	               float length=str.toCharArray().length;
//	               length=length*0.5f;
//	               if(length>tempCount){
//	            	    i--;//需要回退一个字符
//						m--;
//						tempCount = 0;
//	               }
//	               else{
//	            	   tempCount = tempCount - length;
//	            	   value=value+str;
//	            	   c++;
//	               }
//			}
            else {
                if (CharUtil.isChinese(String.valueOf(clArr[i]))) {
                    if (tempCount > 1) {
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

            if (!isNext && tempCount > 0 && i < (clArr.length - 1)) {
                continue;
            }

            if (value == null || "".equals(value.trim())) {
                isNext = false;
                continue;
            }

            if (row > pdfrow) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
                //记录当前新曾了页面
                b = true;
                //新增加了页面，则重新开始计数
                k = 0;

                //行指针重新从第一行开始写数据
                index = 1;

                row = 1;

                //if(!isNew) {
                isNew = true;
                //新曾一页PDF开始写数据
                String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
                if (cn != null && !"".equals(cn)) {
                    int icn = Integer.valueOf(cn);
                    if (page > icn) {
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
                if (!templateList.contains(formName)) {
                    templateList.add(formName);
                }
                printer.addTempl(formName, template);
                //记录基本数据，比如工艺文件编号等
                setCommData(printer, formName);
                //} else {
                //如果已经存在新增页面，则直接在新增页面上写入数据
                //templateName = formName;
                //}
            }

            //记录占用了多少行
            k++;

            //写入PDF的key+(row)格子中
            //modify by machongqi 2015-6-12
            if (value.startsWith("关键工序")) {
                String subValue = value;
                value = value.substring(0, 4);
                printer.addHtml2Blod(templateName, key + row, value, Font.BOLD);
                subValue = subValue.substring(4);
                printer.addHtml2Blod(templateName, key + row, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + subValue, Font.NORMAL);
            } else {
                printer.addHtml2Blod(templateName, key + row, value, Font.NORMAL);
            }
            //modify by machongqi end

            row++;

            n++;
            tempCount = count;
            value = "";
            isNext = false;
        }

        if (b) {
            if (r2 < k) {
                r2 = k;
            }
        } else {
            if (r1 < k) {
                r1 = k;
            }
        }

        return n;
    }

    protected int writeProcessGongXu(String valueStr, float count, LcmPdfPrinter printer, String key, int row, int pdfrow, String templateName, List<String> allList, Object[] wholeStr) {
        List<String> values = (List<String>) wholeStr[0];
        //List<Float> lengths = (List<Float>)wholeStr[1];
        int wholeIndex = 0;


        char[] clArr = valueStr.toCharArray();

        //n用于计算valueStr总共写了多少行
        int n = 1;

        //用于记录写入了第几个特殊符号的图片
        int m = 0;

        //记录当前数据占了多少行
        int k = 0;
        boolean b = false;

        //用于记录当前行还剩余几个字符空间
//        int tempCount = count;
        float tempCount = count;
        try {
            if (row > pdfrow) {
                tempCount = printer.getFieldWidth(template, key + (row - pdfrow));
            } else {
                tempCount = printer.getFieldWidth(template, key + row);
            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
        String value = "";

        //标记是否换行
        boolean isNext = false;

        //循环n,逐个读取字符串的字符
        for (int i = 0; i < clArr.length; i++) {
            //tempCount--;

            //如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
            if (clArr[i] == '♀') {
                String path = allList.get(m++);
                path = path.replace("/", File.separator);
                String imgPath = techFloder + File.separator + path;

                //计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
                int[] img = PDFUtil.getImgWidth(imgPath);
                if (img != null) {
                    int width = img[0];
                    //计算当前行剩余多少个字符空间是否够写入该图片
                    if (width > tempCount) {//剩余长度不够，则换到下一行写
                        i--;//需要回退一个字符
                        m--;
                        tempCount = 0;
                    } else {
                        tempCount = tempCount - width;
                        value = value + PDFUtil.getImgHtmlCode(imgPath);
                    }
                } else {
                    System.out.println(imgPath + " is not exsit!");
                    //value = value + PDFUtil.getImgHtmlCode(imgPath);
                }
            } else if (clArr[i] == '♂') {
                isNext = true;
            } else if (clArr[i] == '♣') {
                tempCount = tempCount - baseFont.getWidthPoint(" ", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&nbsp;";
                }
            } else if (clArr[i] == '❤') {
                tempCount = tempCount - baseFont.getWidthPoint("\"", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&quot;";
                }
            } else if (clArr[i] == '♠') {
                tempCount = tempCount - baseFont.getWidthPoint(">", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&gt;";
                }
            } else if (clArr[i] == '♥') {
                tempCount = tempCount - baseFont.getWidthPoint("<", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&lt;";
                }
            } else if (clArr[i] == '♦') {
                String printvalue = values.get(wholeIndex);
                float tempLength = baseFont.getWidthPoint(printvalue, 12f);
                if (count < tempLength) {
                    tempLength = baseFont.getWidthPoint(message, 12f);
                    System.out.println(values.get(wholeIndex) + "内容过长，输出有误，请分割后输出！");
                    JOptionPane.showMessageDialog(null, values.get(wholeIndex) + "内容过长，输出有误，请分割后输出！", "提示", 1);
                    JOptionPane.showMessageDialog(null, "字符过长会造成PDF数据丢失，请引起重视", "提示", 1);
                }

                tempCount = tempCount - tempLength;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + printvalue;
                    wholeIndex++;
                }
            } else {
                tempCount = tempCount - baseFont.getWidthPoint(clArr[i], 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + clArr[i];
                }
            }

            if (!isNext && tempCount > 0 && i < (clArr.length - 1)) {
                continue;
            }

            if (value == null || "".equals(value.trim())) {
                isNext = false;
                continue;
            }

            if (row > pdfrow) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
                //记录当前新曾了页面
                b = true;
                //新增加了页面，则重新开始计数
                k = 0;

                //行指针重新从第一行开始写数据
                index = 1;

                row = 1;

                //if(!isNew) {
                isNew = true;
                //新曾一页PDF开始写数据
                String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
                if (cn != null && !"".equals(cn)) {
                    int icn = Integer.valueOf(cn);
                    if (page > icn) {
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
                if (!templateList.contains(formName)) {
                    templateList.add(formName);
                }
                printer.addTempl(formName, template);
                //记录基本数据，比如工艺文件编号等
                setCommData(printer, formName);
                //} else {
                //如果已经存在新增页面，则直接在新增页面上写入数据
                //templateName = formName;
                //}
            }

            //记录占用了多少行
            k++;

            //写入PDF的key+(row)格子中
            //modify by machongqi 2015-6-12
            if (value.startsWith("关键工序")) {
                String subValue = value;
                value = value.substring(0, 4);
                printer.addHtml2Blod(templateName, key + row, value, Font.BOLD);
                subValue = subValue.substring(4);
                printer.addHtml2Blod(templateName, key + row, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + subValue, Font.NORMAL);
            } else {
                printer.addHtml2Blod(templateName, key + row, value, Font.NORMAL);
            }
            //modify by machongqi end

            row++;

            n++;
            //tempCount = count;
            try {
                if (row > pdfrow) {
                    tempCount = printer.getFieldWidth(template, key + (row - pdfrow));
                } else {
                    tempCount = printer.getFieldWidth(template, key + row);
                }
            } catch (DocumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            value = "";
            isNext = false;
        }

        if (b) {
            if (r2 < k) {
                r2 = k;
            }
        } else {
            if (r1 < k) {
                r1 = k;
            }
        }

        return n;
    }


    protected int writeProcessGongBu(String valueStr, float count, LcmPdfPrinter printer, String key, int row, int pdfrow, String templateName, List<String> allList, String c, boolean isB, Object[] wholeStr) {
        List<String> values = (List<String>) wholeStr[0];
        //List<Float> lengths = (List<Float>)wholeStr[1];
        int wholeIndex = 0;
//
//		int p=0;
        if (c != null) {
            valueStr = c + "." + valueStr;
        }
        char[] clArr = valueStr.toCharArray();

        //n用于计算valueStr总共写了多少行
        int n = 1;

        //用于记录写入了第几个特殊符号的图片
        int m = 0;

        //记录当前数据占了多少行
        int k = 0;
        boolean b = false;

        //用于记录当前行还剩余几个字符空间
        //if(row<=rows){
        //}else{
        //totalWidth = printer.getFieldWidth(template, "装配检测内容_1");
        //}
        float tempCount = count;
        try {
            if (row > pdfrow) {
                tempCount = printer.getFieldWidth(template, key + (row - pdfrow));
            } else {
                tempCount = printer.getFieldWidth(template, key + row);
            }

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
        String value = "";

        //标记是否换行
        boolean isNext = false;

        //循环n,逐个读取字符串的字符
        for (int i = 0; i < clArr.length; i++) {
            //tempCount--;

            //if(i == 0) {
            //value = c+",";
            //}

            //如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
            if (clArr[i] == '♀') {
                String path = allList.get(m++);
                path = path.replace("/", File.separator);
                String imgPath = techFloder + File.separator + path;

                //计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
                int[] img = PDFUtil.getImgWidth(imgPath);
                if (img != null) {
                    int width = img[0];

                    if (width > tempCount) {//剩余长度不够，则换到下一行写
                        i--;//需要回退一个字符
                        m--;
                        tempCount = 0;
                    } else {
                        tempCount = tempCount - width;
                        value = value + PDFUtil.getImgHtmlCode(imgPath);
                    }

                } else {
                    System.out.println(imgPath + " is not exsit!");
                    //value = value + PDFUtil.getImgHtmlCode(imgPath);
                }
            } else if (clArr[i] == '♂') {
                isNext = true;
            } else if (clArr[i] == '♣') {
                tempCount = tempCount - baseFont.getWidthPoint(" ", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&nbsp;";
                }
            } else if (clArr[i] == '❤') {
                tempCount = tempCount - baseFont.getWidthPoint("\"", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&quot;";
                }
            } else if (clArr[i] == '♠') {
                tempCount = tempCount - baseFont.getWidthPoint(">", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&gt;";
                }
            } else if (clArr[i] == '♥') {
                tempCount = tempCount - baseFont.getWidthPoint("<", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&lt;";
                }
            } else if (clArr[i] == '♦') {
                String printvalue = values.get(wholeIndex);
                float tempLength = baseFont.getWidthPoint(printvalue, 12f);
                if (count < tempLength) {
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
                    value = value + printvalue;
                    wholeIndex++;
                }
            } else {
                tempCount = tempCount - baseFont.getWidthPoint(clArr[i], 12f);
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + clArr[i];
                }
            }

            if (!isNext && tempCount > 0 && i < (clArr.length - 1)) {
                continue;
            }

            if (value == null || "".equals(value.trim())) {
                isNext = false;
                continue;
            }

            if (row > pdfrow) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
                //记录当前新曾了页面
                b = true;
                //新增加了页面，则重新开始计数
                k = 0;

                //行指针重新从第一行开始写数据
                index = 1;

                row = 1;

                //if(!isNew) {
                isNew = true;
                //新曾一页PDF开始写数据
                String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
                if (cn != null && !"".equals(cn)) {
                    int icn = Integer.valueOf(cn);
                    if (page > icn) {
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
                if (!templateList.contains(formName)) {
                    templateList.add(formName);
                }
                printer.addTempl(formName, template);
                //记录基本数据，比如工艺文件编号等
                setCommData(printer, formName);
                //} else {
                //如果已经存在新增页面，则直接在新增页面上写入数据
                //	templateName = formName;
                //}
            }

            //记录占用了多少行
            k++;
            //写入PDF的key+(row)格子中
            //modify by machongqi 2015-6-12
            if (isB) {
                printer.addHtml2Blod(templateName, key + row, value, Font.BOLD);
            } else {
                printer.addHtml2Blod(templateName, key + row, value, Font.NORMAL);
            }
            //modify by machongqi end
            row++;

            n++;
            //tempCount = count;
            try {

                if (row > pdfrow) {
                    tempCount = printer.getFieldWidth(template, key + (row - pdfrow));
                } else {
                    tempCount = printer.getFieldWidth(template, key + row);
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            value = "";
            isNext = false;
        }

        if (b) {
            if (r2 < k) {
                r2 = k;
            }
        } else {
            if (r1 < k) {
                r1 = k;
            }
        }

        return n;
    }

    protected int writeProcessGongBu(String valueStr, int count, LcmPdfPrinter printer, String key, int row, int pdfrow, String templateName, List<String> allList, String c, boolean isB) {

//    	List<String>  list=new  ArrayList<String>();
//		String  newValueStr=floatDateHuanHang(list,valueStr);
//		char[]  clArr=newValueStr.toCharArray();
//
//		int p=0;
        valueStr = c + "." + valueStr;
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
        for (int i = 0; i < clArr.length; i++) {
            //tempCount--;

            //if(i == 0) {
            //value = c+",";
            //}

            //如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
            if (clArr[i] == '♀') {
                String path = allList.get(m++);
                path = path.replace("/", File.separator);
                String imgPath = techFloder + File.separator + path;

                //计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
                int[] img = PDFUtil.getImgWidth(imgPath);
                if (img != null) {
                    int width = img[0];
                    int imageLength = width / 12;
                    if (width % 25 != 0) {
                        imageLength++;
                    }

                    //计算当前行剩余多少个字符空间是否够写入该图片
                    if (imageLength > tempCount) {//剩余长度不够，则换到下一行写
                        i--;//需要回退一个字符
                        m--;
                        tempCount = 0;
                    } else {
                        tempCount = tempCount - imageLength;
                        value = value + PDFUtil.getImgHtmlCode(imgPath);
                    }
                } else {
                    System.out.println(imgPath + " is not exsit!");
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
            } else if (clArr[i] == '♠') {
                value = value + "&gt;";
                tempCount = tempCount - 0.58f;
            } else if (clArr[i] == '♥') {
                value = value + "&lt;";
                tempCount = tempCount - 0.58f;
            } else if (WriterStandard.speWord.contains(clArr[i] + "")) {
                value = value + clArr[i];
                tempCount = tempCount - 2f;
            }
//			else if(clArr[i] == '☀'){
//	               String str=list.get(p);
//	               float length=str.toCharArray().length;
//	               length=length*0.5f;
//	               if(length>tempCount){
//	            	    i--;//需要回退一个字符
//						m--;
//						tempCount = 0;
//	               }
//	               else{
//	            	   tempCount = tempCount - length;
//	            	   value=value+str;
//	            	   p++;
//	               }
//			}
            else {
                if (CharUtil.isChinese(String.valueOf(clArr[i]))) {
                    if (tempCount > 1) {
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

            if (!isNext && tempCount > 0 && i < (clArr.length - 1)) {
                continue;
            }

            if (value == null || "".equals(value.trim())) {
                isNext = false;
                continue;
            }

            if (row > pdfrow) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
                //记录当前新曾了页面
                b = true;
                //新增加了页面，则重新开始计数
                k = 0;

                //行指针重新从第一行开始写数据
                index = 1;

                row = 1;

                //if(!isNew) {
                isNew = true;
                //新曾一页PDF开始写数据
                String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
                if (cn != null && !"".equals(cn)) {
                    int icn = Integer.valueOf(cn);
                    if (page > icn) {
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
                if (!templateList.contains(formName)) {
                    templateList.add(formName);
                }
                printer.addTempl(formName, template);
                //记录基本数据，比如工艺文件编号等
                setCommData(printer, formName);
                //} else {
                //如果已经存在新增页面，则直接在新增页面上写入数据
                //	templateName = formName;
                //}
            }

            //记录占用了多少行
            k++;

            //写入PDF的key+(row)格子中
            //modify by machongqi 2015-6-12
            if (isB) {
                printer.addHtml2Blod(templateName, key + row, value, Font.BOLD);
            } else {
                printer.addHtml2Blod(templateName, key + row, value, Font.NORMAL);
            }
            //modify by machongqi end
            row++;

            n++;
            tempCount = count;
            value = "";
            isNext = false;
        }

        if (b) {
            if (r2 < k) {
                r2 = k;
            }
        } else {
            if (r1 < k) {
                r1 = k;
            }
        }

        return n;
    }

    protected int writeProcessDescribe(String valueStr, float count, LcmPdfPrinter printer, String key, int row, int pdfrow, String templateName, List<String> allList, Object[] wholeStr) {
        List<String> values = (List<String>) wholeStr[0];
        //List<Float> lengths = (List<Float>)wholeStr[1];
        int wholeIndex = 0;

//    	List<String>  list=new  ArrayList<String>();
//		String  newValueStr=floatDateHuanHang(list,valueStr);
//		char[]  clArr=newValueStr.toCharArray();
//
//		int c=0;
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
        try {
            if (row > pdfrow) {
                tempCount = printer.getFieldWidth(template, key + (row - pdfrow));
            } else {
                tempCount = printer.getFieldWidth(template, key + row);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
        String value = "";

        //标记是否换行
        boolean isNext = false;

        //循环n,逐个读取字符串的字符
        for (int i = 0; i < clArr.length; i++) {
            //tempCount--;

            //如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
            if (clArr[i] == '♀') {
                String path = allList.get(m++);
                path = path.replace("/", File.separator);
                String imgPath = techFloder + File.separator + path;
                System.out.println("----------->>>>" + path);
                //计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
                int[] img = PDFUtil.getImgWidth(imgPath);
                if (img != null) {
                    int width = img[0];
                    if (width > tempCount) {//剩余长度不够，则换到下一行写
                        i--;//需要回退一个字符
                        m--;
                        tempCount = 0;
                    } else {
                        tempCount = tempCount - width;
                        value = value + PDFUtil.getImgHtmlCode(imgPath);
                    }
                } else {
                    System.out.println(path + " is not exsit!");
                    //value = value + PDFUtil.getImgHtmlCode(imgPath);
                }
            } else if (clArr[i] == '♂') {
                isNext = true;
            } else if (clArr[i] == '♣') {
                tempCount = tempCount - baseFont.getWidthPoint(" ", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&nbsp;";
                }
            } else if (clArr[i] == '❤') {
                tempCount = tempCount - baseFont.getWidthPoint("\"", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&quot;";
                }
            } else if (clArr[i] == '♠') {
                tempCount = tempCount - baseFont.getWidthPoint(">", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&gt;";
                }
            } else if (clArr[i] == '♥') {
                tempCount = tempCount - baseFont.getWidthPoint("<", 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + "&lt;";
                }
            } else if (clArr[i] == '♦') {
                String printvalue = values.get(wholeIndex);
                float tempLength = baseFont.getWidthPoint(printvalue, 12f);
                if (count < tempLength) {
                    tempLength = baseFont.getWidthPoint(message, 12f);
                    System.out.println(values.get(wholeIndex) + "内容过长，输出有误，请分割后输出！");
                    JOptionPane.showMessageDialog(null, values.get(wholeIndex) + "内容过长，输出有误，请分割后输出！", "提示", 1);
                    JOptionPane.showMessageDialog(null, "字符过长会造成PDF数据丢失，请引起重视", "提示", 1);
                }

                tempCount = tempCount - tempLength;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + printvalue;
                    wholeIndex++;
                }
            } else {
                tempCount = tempCount - baseFont.getWidthPoint(clArr[i], 12f);
                ;
                if (tempCount < 0) {
                    i--;
                    tempCount = 0;
                } else {
                    value = value + clArr[i];
                }
            }

//    		if(!isNext && tempCount > 0 && i < (clArr.length-1)) {
//    			continue;
//    		}
            if (!isNext && tempCount > 0 && i < (clArr.length - 1)) {
                continue;
            }

            if (value == null || "".equals(value.trim())) {
                isNext = false;
                continue;
            }

            if (row > pdfrow) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
                //记录当前新曾了页面
                b = true;
                //新增加了页面，则重新开始计数
                k = 0;

                //行指针重新从第一行开始写数据
                index = 1;

                row = 1;

                //新曾一页PDF开始写数据
                String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
                if (cn != null && !"".equals(cn)) {
                    int icn = Integer.valueOf(cn);
                    if (page > icn) {
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
                setCommData(printer, formName);
            }

            //记录占用了多少行
            k++;
            //写入PDF的key+(row)格子中
            printer.addHtml(templateName, key + row, value);
            row++;

            n++;
            //tempCount = count;
            try {
                if (row > pdfrow) {
                    tempCount = printer.getFieldWidth(template, key + (row - pdfrow));
                } else {
                    tempCount = printer.getFieldWidth(template, key + row);
                }
            } catch (DocumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            value = "";
            isNext = false;

        }
        if (b) {
            if (r2 < k) {
                r2 = k;
            }
        } else {
            if (r1 < k) {
                r1 = k;
            }
        }

        return n;
    }

    protected int writeProcessDescribe(String valueStr, int count, LcmPdfPrinter printer, String key, int row, int pdfrow, String templateName, List<String> allList) {

//    	List<String>  list=new  ArrayList<String>();
//		String  newValueStr=floatDateHuanHang(list,valueStr);
//		char[]  clArr=newValueStr.toCharArray();
//
//		int c=0;
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
        for (int i = 0; i < clArr.length; i++) {
            //tempCount--;

            //如果是图片，则先把图片前面部分的数据先写入PDF，然后再插入图片
            if (clArr[i] == '♀') {
                String path = allList.get(m++);
                path = path.replace("/", File.separator);
                String imgPath = techFloder + File.separator + path;
                System.out.println("----------->>>>" + path);
                //计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
                int[] img = PDFUtil.getImgWidth(imgPath);
                if (img != null) {
                    int width = img[0];
                    int imageLength = width / 12;
                    if (width % 25 != 0) {
                        imageLength++;
                    }

                    //计算当前行剩余多少个字符空间是否够写入该图片
                    if (imageLength > tempCount) {//剩余长度不够，则换到下一行写
                        i--;//需要回退一个字符
                        m--;
                        tempCount = 0;
                    } else {
                        tempCount = tempCount - imageLength;
                        value = value + PDFUtil.getImgHtmlCode(imgPath);
                    }
                } else {
                    System.out.println(path + " is not exsit!");
                    //value = value + PDFUtil.getImgHtmlCode(imgPath);
                }
            } else if (clArr[i] == '♂') {
                isNext = true;
            } else if (clArr[i] == '❤') {
                value = value + "&quot;";
                tempCount = tempCount - 0.58f;
            } else if (clArr[i] == '♠') {
                value = value + "&gt;";
                tempCount = tempCount - 0.58f;
            } else if (clArr[i] == '♥') {
                value = value + "&lt;";
                tempCount = tempCount - 0.58f;
            } else if (WriterStandard.speWord.contains(clArr[i] + "")) {
                value = value + clArr[i];
                tempCount = tempCount - 2f;
            }
//			else if(clArr[i] == '☀'){
//	               String str=list.get(c);
//	               float length=str.toCharArray().length;
//	               length=length*0.5f;
//	               if(length>tempCount){
//	            	    i--;//需要回退一个字符
//						m--;
//						tempCount = 0;
//	               }
//	               else{
//	            	   tempCount = tempCount - length;
//	            	   value=value+str;
//	            	   c++;
//	               }
//			}
            else {
                if (CharUtil.isChinese(String.valueOf(clArr[i]))) {
                    if (tempCount > 1) {
                        tempCount = tempCount - 1;
                    } else {
                        tempCount = tempCount - 1;
                    }
                } else {
                    tempCount = tempCount - 0.58f;
                }
                value = value + clArr[i];
            }

//    		if(!isNext && tempCount > 0 && i < (clArr.length-1)) {
//    			continue;
//    		}
            if (!isNext && tempCount > 0 && i < (clArr.length - 1)) {
                continue;
            }

            if (value == null || "".equals(value.trim())) {
                isNext = false;
                continue;
            }

            if (row > pdfrow) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
                //记录当前新曾了页面
                b = true;
                //新增加了页面，则重新开始计数
                k = 0;

                //行指针重新从第一行开始写数据
                index = 1;

                row = 1;

                //新曾一页PDF开始写数据
                String cn = templateName.substring(templateName.lastIndexOf('_') + 1, templateName.length());
                if (cn != null && !"".equals(cn)) {
                    int icn = Integer.valueOf(cn);
                    if (page > icn) {
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
                setCommData(printer, formName);
            }

            //记录占用了多少行
            k++;
            //写入PDF的key+(row)格子中
            printer.addHtml(templateName, key + row, value);
            row++;

            n++;
            tempCount = count;
            value = "";
            isNext = false;

        }
        if (b) {
            if (r2 < k) {
                r2 = k;
            }
        } else {
            if (r1 < k) {
                r1 = k;
            }
        }

        return n;
    }

   /* public int writeProcess(String valueStr,int count,LcmPdfPrinter printer,String key,int row,int pdfrow,String templateName) {
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

    		if(row > pdfrow) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
    			//记录当前新曾了页面
    			b = true;
    			//新增加了页面，则重新开始计数
    			k = 0;
    			//行指针重新从第一行开始写数据
    			index = 1;
            	//如果已经到最后一行，则数据应该写到下一页,index从第一行开始
            	row = 1;

            	//if(!isNew) {
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
            	//} else {
            		//如果已经存在新增页面，则直接在新增页面上写入数据
            		//templateName = formName;
            	//}
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
*/


    public String getElementValues2(Element ele, String qname, String[] keys) {

        String values2 = "";
        List<Element> list = ele.elements(qname);
        for (Element element : list) {
            String values = "";
            for (String key : keys) {
                String temp = element.attributeValue(key);
                if (temp != null && !"".equals(temp)) {
                    if (temp.startsWith("A%")) {
                        temp = temp.replaceAll("A%", "");
                    }
                    if (!"".equals(values)) {
                        values = values + "(" + temp + ")";
                    } else {
                        values = temp;
                    }
                }
            }
            if (!"".equals(values2)) {
                values2 = values2 + "," + values;
            } else {
                values2 = values;
            }
        }
        return values2;
    }

    public String replaceTeShuFuHao(String str) {
        //❤♠♥
        //替换字符串中所有的双引号，为❤
        str = str.replaceAll("&quot;", "❤").trim();
        //替换字符串中所有的大于号，为♠
        str = str.replaceAll("&gt;", "♠").trim();
        //替换字符串中所有的小于号，为♥
        str = str.replaceAll("&lt;", "♥").trim();
        return str;


    }

    /*
     * 是否加粗
     */
    protected boolean isB(Element step) {
        boolean isB = false;
        String paceIsKey = PDFUtil.objectToString(step.attributeValue("isKey"));
        String isGJJYD = PDFUtil.objectToString(step.attributeValue("isGJJYD"));
        String isQZJYD = PDFUtil.objectToString(step.attributeValue("isQZJYD"));
        String isGYGJTX = PDFUtil.objectToString(step.attributeValue("isGYGJTX"));
        String isGCGJTX = PDFUtil.objectToString(step.attributeValue("isGCGJTX"));
        String isDYWKZD = PDFUtil.objectToString(step.attributeValue("isDYWKZD"));
        String isGYGJJYD = PDFUtil.objectToString(step.attributeValue("isGYGJJYD"));
        String isShuangGang = PDFUtil.objectToString(step.attributeValue("isShuangGang"));

        if ("true".equalsIgnoreCase(paceIsKey)
                || "true".equalsIgnoreCase(isGJJYD)
                || "true".equalsIgnoreCase(isQZJYD)
                || "true".equalsIgnoreCase(isGYGJTX)
                || "true".equalsIgnoreCase(isGCGJTX)
                || "true".equalsIgnoreCase(isDYWKZD)
                || "true".equalsIgnoreCase(isGYGJJYD)
                || "true".equalsIgnoreCase(isShuangGang)) {
            isB = true;
        }
        return isB;
    }

    protected String getCzjStr(Element partsElement) {
        String result = "";
        if(ext.casc.util.Tools.isNull(partsElement)) return "";

        List<CzjPart> czjList = getCzjParts(partsElement);
        if (czjList != null && czjList.size() > 0) {
            result = "参装件:♂";
            for (CzjPart czjPart : czjList) {
                result += czjPart.toString() + "♂";
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected List<CzjPart> getCzjParts(Element parts) {
        List<CzjPart> czjPartList = new ArrayList<CzjPart>();
        CzjPart czjPart = null;
        if(parts==null){
            return czjPartList;
        }
        List<Element> partList = parts.selectNodes("QMPartInfo");
        for (Element ele : partList) {
            String name = ele.attributeValue("partName");//名称
            String bh = ele.attributeValue("partNumber");//编号
            String ph = ele.attributeValue("XHPH");//牌号
            String gg = ele.attributeValue("GG");//规格
            Double count = Double.valueOf(ele.attributeValue("useCount"));//数量
            String type = ele.attributeValue("MTYPE");//类型
            String dw = ele.attributeValue("DW2");//单位
            if(dw == null){
                dw = "";
            }
            String bzh = ele.attributeValue("bzh");//单位
            if(bzh == null){
                bzh = "";
            }
            String dataType = ele.attributeValue("dataType");//单位
            if (dw == null || "".equals(dw)) {
                dw = "个";
            }
            czjPart = new CzjPart(name, bh, ph, gg, count, type, dw,bzh,dataType);

            czjPartList.add(czjPart);
        }
        /**合并相同元素*/
        for (int i = 0; i < czjPartList.size() - 1; i++) {
            for (int j = czjPartList.size() - 1; j > i; j--) {
                if (Constants.TYPE_ZIZHIJIAN.equals(czjPartList.get(i).getType()) ||
                        Constants.TYPE_WAIGOUJIAN.equals(czjPartList.get(i).getType()) ||
                        Constants.TYPE_DAILIAOWEIWAIJIAN.equals(czjPartList.get(i).getType()) ||
                        Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(czjPartList.get(i).getType()) ||
                        Constants.TYPE_WAIPEITAOJIAN.equals(czjPartList.get(i).getType())) {
                    if (czjPartList.get(j).getName().equals(czjPartList.get(i).getName())
                            && czjPartList.get(j).getBh().equals(czjPartList.get(i).getBh())) {
                        double totalCount = CommonUtil.addDouble(czjPartList.get(i).getCount(), czjPartList.get(j).getCount());
                        czjPartList.get(i).setCount(totalCount);
                        czjPartList.remove(j);
                    }
                } else {
                    if (czjPartList.get(j).getName().equals(czjPartList.get(i).getName())
                            && czjPartList.get(j).getPh().equals(czjPartList.get(i).getPh())
                            && czjPartList.get(j).getGg().equals(czjPartList.get(i).getGg())
                            && czjPartList.get(j).getBzh().equals(czjPartList.get(i).getBzh())) {
                        double totalCount = CommonUtil.addDouble(czjPartList.get(i).getCount(), czjPartList.get(j).getCount());
                        czjPartList.get(i).setCount(totalCount);
                        czjPartList.remove(j);
                    }
                }
            }
        }
        return czjPartList;
    }

    public static class CzjPart {
        /**
         * 名称
         */
        private String name;
        /**
         * 编号
         */
        private String bh;
        /**
         * 牌号
         */
        private String ph;
        /**
         * 规格
         */
        private String gg;
        /**
         * 数量
         */
        private double count;
        /**
         * 类型
         */
        private String type;
        /**
         * 单位
         */
        private String dw;
        /**
         * 标准号
         */
        private String bzh;
        /**
         * 类型
         */
        private String dataType;

        public CzjPart() {
        }

        public CzjPart(String name, String bh, String ph, String gg, Double count, String type, String dw,String bzh, String dataType) {
            this.name = name;
            this.bh = bh;
            this.ph = ph;
            this.gg = gg;
            this.count = count;
            this.type = type;
            this.dw = dw;
            this.bzh = bzh;
            this.dataType = dataType;
        }

        @Override
        public String toString() {
            String result = "";
            if (Constants.TYPE_ZIZHIJIAN.equals(type) || Constants.TYPE_WAIGOUJIAN.equals(type) || Constants.TYPE_DAILIAOWEIWAIJIAN.equals(type) || Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(type) || Constants.TYPE_WAIPEITAOJIAN.equals(type)) {
                result = name + "♣♣♣♣" + bh + "♣♣♣♣" + count + dw;
            } else if (count == 0) {
                result = name;
            } else {
                if ("标准件".equals(dataType)||"标准紧固件".equals(dataType)) {
                    result = name + "♣♣♣♣" + bzh + "♣♣♣♣" + gg + "♣♣♣♣" + count + dw;
                } else {
                    result = name + "♣♣♣♣" + ph + "♣♣♣♣" + gg + "♣♣♣♣" + count + dw;
                }
            }
            return result.toString();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBh() {
            return bh;
        }

        public void setBh(String bh) {
            this.bh = bh;
        }

        public String getPh() {
            return ph;
        }

        public void setPh(String ph) {
            this.ph = ph;
        }

        public String getGg() {
            return gg;
        }

        public void setGg(String gg) {
            this.gg = gg;
        }

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = count;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDw() {
            return dw;
        }

        public void setDw(String dw) {
            this.dw = dw;
        }

        public String getBzh() {
            return bzh;
        }

        public void setBzh(String bzh) {
            this.bzh = bzh;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }

    public static Map<String, CzjPart> getAllCzjMap(Element techElement) {
        Map<String, CzjPart> czjPartMap = new HashMap<String, CzjPart>();
        List<Element> stepElements = XmlUtility.getAllSteps(techElement);
        CzjPart czjPart;
        for (Element stepElement : stepElements) {
            Element stepPartsElement = XmlUtility.getParts(stepElement);
            if (stepPartsElement != null) {
                List<Element> czjElements = stepPartsElement.elements("QMPartInfo");
                for (Element czjElement : czjElements) {
                    String partNumber = czjElement.attributeValue("partNumber");
                    String zcMark = czjElement.attributeValue("ZCMARK");
                    double useCount = Double.valueOf(czjElement.attributeValue("useCount"));
                    if (czjPartMap.containsKey(partNumber)) {
                        czjPart = czjPartMap.get(partNumber);
//                        czjPart.setCount(czjPart.getCount() + 1);
                        if("Z".equals(zcMark)){
                            czjPart.setCount(CommonUtil.addDouble(czjPart.getCount(),useCount));
                        }else if("C".equals(zcMark)){
                            czjPart.setCount(CommonUtil.subDouble(czjPart.getCount(),useCount));
                        }
                    } else {
                        czjPart = new CzjPart();
                        czjPart.setBh(partNumber);
//                        czjPart.setCount(1);
                        if("Z".equals(zcMark)){
                            czjPart.setCount(CommonUtil.addDouble(0,useCount));
                        }else if("C".equals(zcMark)){
                            czjPart.setCount(CommonUtil.subDouble(0,useCount));
                        }
                        czjPartMap.put(partNumber, czjPart);
                    }
                }
            }
            List<Element> paceElements = XmlUtility.getAllPaces(stepElement);
            for (Element paceElement : paceElements) {
                Element pacePartsElement = XmlUtility.getParts(paceElement);
                if (pacePartsElement != null) {
                    List<Element> czjElements = pacePartsElement.elements("QMPartInfo");
                    for (Element czjElement : czjElements) {
                        String partNumber = czjElement.attributeValue("partNumber");
                        String zcMark = czjElement.attributeValue("ZCMARK");
                        double useCount = Double.valueOf(czjElement.attributeValue("useCount"));
                        if (czjPartMap.containsKey(partNumber)) {
                            czjPart = czjPartMap.get(partNumber);
//                            czjPart.setCount(czjPart.getCount() + 1);
                            if("Z".equals(zcMark)){
                                czjPart.setCount(CommonUtil.addDouble(czjPart.getCount(),useCount));
                            }else if("C".equals(zcMark)){
                                czjPart.setCount(CommonUtil.subDouble(czjPart.getCount(),useCount));
                            }
                        } else {
                            czjPart = new CzjPart();
                            czjPart.setBh(partNumber);
//                            czjPart.setCount(1);
                            if("Z".equals(zcMark)){
                                czjPart.setCount(CommonUtil.addDouble(0,useCount));
                            }else if("C".equals(zcMark)){
                                czjPart.setCount(CommonUtil.subDouble(0,useCount));
                            }
                            czjPartMap.put(partNumber, czjPart);
                        }
                    }
                }
            }
        }
        return czjPartMap;
    }

    //写入工序节点附图附表
    public static boolean buildProcedureFTFB(LcmPdfPrinter printer, Element techElement, Element procedure, String techFloder, Map<String, String> params, List<String> templateList) {
        boolean isHas = false;
        try {
            //写入附图附表
            //工艺附图
            String stepNumber = procedure.attributeValue("stepNumber");
            Class<?> ftCls = Class.forName("com.glaway.mpm.pdf.processor.Form7GYFTPDFBuilder");
            Class<?> fbCls = Class.forName("com.glaway.mpm.pdf.processor.Form7GYFBPDFBuilder");
            Constructor ftClsConstructor = ftCls.getConstructor(Element.class, String.class, String.class);
            Constructor fbClsConstructor = fbCls.getConstructor(Element.class, String.class, String.class);
            Form7GYFTPDFBuilder ft = (Form7GYFTPDFBuilder) ftClsConstructor.newInstance(techElement, techFloder, "工序" + stepNumber + "附图");
            Form7GYFBPDFBuilder fb = (Form7GYFBPDFBuilder) fbClsConstructor.newInstance(techElement, techFloder, "工序" + stepNumber + "附表");
            ft.buildProcedure(printer, params, procedure);
            fb.buildProcedure(printer, params, procedure);
            if(!ft.getTemplateList().isEmpty() || !fb.getTemplateList().isEmpty()) {
                List<String> ftTemplateList = ft.getTemplateList();
                if(!ftTemplateList.isEmpty()) {
                    templateList.addAll(ftTemplateList);
                    isHas = true;
                }
                List<String> fbTemplateList = fb.getTemplateList();
                if(!fbTemplateList.isEmpty()) {
                    templateList.addAll(fbTemplateList);
                    isHas = true;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return isHas;
    }
}
