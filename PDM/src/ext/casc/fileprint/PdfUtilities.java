package ext.casc.fileprint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.PropertyResourceBundle;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTProperties;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.ptc.wvs.server.util.Util;

import ext.casc.util.IBAHelper;

public class PdfUtilities implements RemoteAccess {

    private static final boolean SERVER = RemoteMethodServer.ServerFlag;
    public static final String ATTR = "ATTR";
    public static final String DOCSEPEATOR = "&&&";
    public static final String FILESEPEATOR = ";;;qqq";
    public static final String SOURCEKEY = "SOURCE";
    public static final String PRINTKEY = "PRINT";
    public static final String ATTRVALUE = "ATTRVALUE";
    public static final String LOCATIONX = "LOCATIONX";
    public static final String LOCATIONY = "LOCATIONY";
    public static final String OUTPUTPAGE = "OUTPUTPAGE";
    public static final String FONTSIZE = "FONTSIZE";
    public static final String DIRECTION = "DIRECTION";
    private static final Logger log;

    /**
     * @param args
     */
    private static final String filePrintProperties = "ext.casc.fileprint.fileprint";
    public static PropertyResourceBundle prBundle = (PropertyResourceBundle) PropertyResourceBundle
            .getBundle(filePrintProperties);

    static {
        try {
            WTProperties wtproperties = WTProperties.getLocalProperties();
            log = LogR.getLogger(FilePrintUtil.class.getName());
        } catch (Throwable throwable) {
            System.err.println("WVSService: Error reading ext.casc.fileprint..* properties");
            throwable.printStackTrace(System.err);
            throw new ExceptionInInitializerError(throwable);
        }
    }

    public PdfUtilities() {

    }

    /*
     * 该方法通过获得待写文件路径，保留源文件内容，产生一个临时文件进行写入 Hashtable中存放所需要的属性.
     *
     * sourcefile -------- 下载至临时目录的附件
     * printfile --------- 生成的带有打印信息的文件
     * vector ------------ 存放属性值，包括属性，值，坐标
     * outputpage -------- 写入第几页，如果为0，则为每页都写,如果为-1，表示写入尾页；
     */
    public static File writeToPDF(String sourcefile, String printfile, Vector vector, String format) throws WTException {
        try {
            String extention = Util.getExtension(sourcefile);
            String removeExtention = Util.removeExtension(sourcefile);
            File file = new File(sourcefile);
            PdfReader reader = new PdfReader(sourcefile);
            Rectangle pageSize = reader.getPageSize(1);// 595.92X842.0
            // A4(595,842),A3(842,1190),A2(1190,1684),A1(1684,2384),A0(2384,3370)字体横向打印
            // A4(595,842),A3(1190,842),A2(1684,1190),A1(2384,1684),A0(3370,2384,)字体纵向打印

            // 定义临时文件
            File tempFile = new File(printfile);
            //tempFile = copyFile(file, tempFile);
            int n = reader.getNumberOfPages();
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", false);
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(tempFile));
            String attr = "";
            String value = "";
            String locationx = "";
            String locationy = "";
            String fontsizeStr = "";
            float fontsize = 0;
            int outputpage = 0;
            float x = 0;
            float y = 0;
            String direction = "";
            float rotation = 0;

            // 读签审输出信息
            for (int i = 0; i < vector.size(); i++) {
                Hashtable hs = (Hashtable) vector.elementAt(i);
                attr = (String) hs.get(ATTR);
                value = (String) hs.get(ATTRVALUE);
                locationx = (String) hs.get(LOCATIONX);
                locationy = (String) hs.get(LOCATIONY);
                fontsizeStr = (String) hs.get(FONTSIZE);
                fontsize = Float.parseFloat(fontsizeStr);
                x = Float.parseFloat(locationx);
                y = Float.parseFloat(locationy);
                PdfContentByte over = stamp.getOverContent(1);
                over.beginText();
                over.setFontAndSize(bf, fontsize);
                over.showTextAligned(Element.ALIGN_RIGHT, value, x, y, rotation);
                over.endText();
            }
            stamp.close();
            return tempFile;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 该方法通过获得待写文件路径，保留源文件内容，产生一个临时文件进行写入 Hashtable中存放所需要的属性.
     *
     * sourcefile -------- 下载至临时目录的附件
     * printfile --------- 生成的带有打印信息的文件
     * vector ------------ 存放属性值，包括属性，值，坐标
     * outputpage -------- 写入第几页，如果为0，则为每页都写,如果为-1，表示写入尾页；
     */
    public static File writeToPDF(String sourcefile, String printfile, Vector vector, int outputpage, String diretion,
            float fontsize, String format) throws WTException {
        try {
            String extention = Util.getExtension(sourcefile);
            String removeExtention = Util.removeExtension(sourcefile);
            File file = new File(sourcefile);
            PdfReader reader = new PdfReader(sourcefile);
            Rectangle pageSize = reader.getPageSize(1);// 595.92X842.0
            float width = pageSize.getWidth();
            log.debug("-------------width:"+width);
            // A0:3370,A1:2384,A2:1684,A3:1190,A4:595
            // 定义临时文件
            File tempFile = new File(printfile);
            int n = reader.getNumberOfPages();
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", false);
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(tempFile));

            String attr = "";
            String value = "";
            String locationx = "";
            String locationy = "";
            float x = 0;
            float y = 0;
            float rotation = 0;

            if (diretion.equalsIgnoreCase("vertical")) {
                rotation = 0;
            } else if (diretion.equalsIgnoreCase("horizontal")) {
                rotation = 270;
            }
            log.debug("-------rotation:"+rotation);
            // 读签审输出信息
            for (int i = 0; i < vector.size(); i++) {
                Hashtable hs = (Hashtable) vector.elementAt(i);
                attr = (String) hs.get(ATTR);
                value = (String) hs.get(ATTRVALUE);
                if("null".equalsIgnoreCase(value) || value == null){
                	value="";
                }
                if (value.contains(",")) {
                    value = value.replaceAll(",", "");
                }
                locationx = (String) hs.get(LOCATIONX);
                locationy = (String) hs.get(LOCATIONY);
                if(hs.get(OUTPUTPAGE) != null){
                	outputpage = (Integer)hs.get(OUTPUTPAGE);
                }
                String fontString = (String) hs.get(FONTSIZE);
                if(fontString!=null&&!"".equals(fontString)){
                	fontsize = Float.valueOf(fontString);
                }
                log.debug("-------fontString:"+fontString);

                log.debug("-----attr:" + attr + "   value:" + value + "   x:" + locationx + "   y:" + locationy);
                if (attr.startsWith("HUIQIAN") || vector.size() == 6){
                    x = Float.valueOf(locationx).floatValue();// 会签位置在左边，不需要再计算
                } else {
//                    if (width < 596F) {
//                        x = sub(width, Float.valueOf(locationx).floatValue());
//                    } else if (width > 1190F && width < 1191F) {
//                        x = sub(width, Float.valueOf(locationx).floatValue()) - 30F;// A3
//                    } else if (width > 1684F && width < 1685F) {
//                        x = sub(width, Float.valueOf(locationx).floatValue()) - 35F;// A2
//                    } else {
//                        x = sub(width, Float.valueOf(locationx).floatValue()) - 40F;// A0,A1
//                    }
                    x = Float.valueOf(locationx).floatValue();// 会签位置在左边，不需要再计算
                }
                y = Float.valueOf(locationy).floatValue();
                // 向PDF输出信息
                int j = 0;
                while (j < n) {
                    j++;
                    String curpage = String.valueOf(j);//分页数  add by zhuhao 2017.4.20
                    String totpage = String.valueOf(n);//总页数  add by zhuhao 2017.4.20
                    if (outputpage == 0) // 写入每一页
                    {
                        PdfContentByte over = stamp.getOverContent(j);
                        over.beginText();
                        over.setFontAndSize(bf, fontsize);
                        if (attr.indexOf("JDBJ") > -1) {
                            over.showTextAligned(Element.ALIGN_LEFT, "  ", x, y, rotation);
                        }
                        if (attr.startsWith("HUIQIAN")&&!attr.equals("HUIQIAN0")&&!attr.equals("HUIQIAN1")&&!attr.equals("HUIQIAN2")) {
                            over.showTextAligned(Element.ALIGN_LEFT, value, x, y-5, rotation);
                        //判断当前页码  add by zhuhao 2017.4.20
                        }else if(attr.startsWith("CURPAGE")){
                        	over.showTextAligned(Element.ALIGN_LEFT,curpage, x, y, rotation);
                        //判断总页码  add by zhuhao 2017.4.20
                        }else if(attr.startsWith("TOTPAGE")){
                        	over.showTextAligned(Element.ALIGN_LEFT,totpage, x, y, rotation);
                        //发往单位和通知内容首行到35换行 add by zhuaho 2017.5.5
                        }else if(attr.startsWith("FAWANGDANWEI")||attr.startsWith("TONGZHIBIAOTI")){
                        	char[] clArr = value.toCharArray();
                        	int count =35;  //定义每行输出35个字
                        	double count2 = 0;
                        	int m = clArr.length/count;
                        	if(clArr.length%count!=0){
                        		m = m+1;
                        	}
                        	float y2=y;//还原y坐标
                        	count2 = count;
                        	//用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
                        	String chrvalue = "";
                        	for(int p=0;p<clArr.length;p++){
                        		if(!isChinese(String.valueOf(clArr[p]))){
                        			count2 = count2 - 0.5;
                        		}else{
                        			count2 = count2 -1;
                        		}

                        		chrvalue = chrvalue + clArr[p];
                        		if(count2<=0 || p == clArr.length-1){
                        			over.showTextAligned(Element.ALIGN_LEFT, chrvalue, x, y2, rotation);
                                	y2 -= fontsize+1;//移到下一行写入
                                	chrvalue = "";//重置value
                                	count2 = count;//重置count2
                            	}
                        	}
                        }else{
                            over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        }
                        over.endText();
                    }

                    if (j == outputpage)// 写入指定页
                    {
                        PdfContentByte over = stamp.getOverContent(j);
                        over.beginText();
                        over.setFontAndSize(bf, fontsize);
                        if("NUMBER".equals(attr)){
                        	if("FORMATFANGANA4".equals(format) || "FORMATQITALEIWENDANGA4".equals(format)){
                        		//编号换行 add by zhuhao 2017.05.31
                            	char[] clArr = value.toCharArray();
                            	int count = 6;
                            	double count2 = count;
                            	float y2=y;//还原y坐标
                            	String chrvalue = "";
                            	for(int p=0;p<clArr.length;p++){
                            		if(!isChinese(String.valueOf(clArr[p]))){
                            			count2 = count2 - 0.5;
                            		}else{
                            			count2 = count2 -1;
                            		}
                            		chrvalue = chrvalue + clArr[p];
                            		if(count2<=0 || p == clArr.length-1){
                            			over.showTextAligned(Element.ALIGN_LEFT, chrvalue, x, y2, rotation);
                                    	y2 -= fontsize+1;//移到下一行写入
                                    	chrvalue = "";//重置value
                                    	count2 = count;//重置count2
                                	}
                            	}
                        	}else{
                        		over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        	}
                        }else if ("NAME".equals(attr) && ("FORMATFANGANA4".equals(format) || "FORMATQITALEIWENDANGA4".equals(format))){
                        	//名称换行 add by zhuhao 2017.05.31
                        	char[] clArr = value.toCharArray();
                        	int count = 17;
                        	double count2 = count;
                        	float y2=y;//还原y坐标
                        	String chrvalue = "";
                        	for(int p=0;p<clArr.length;p++){
                        		if(!isChinese(String.valueOf(clArr[p]))){
                        			count2 = count2 - 0.5;
                        		}else{
                        			count2 = count2 -1;
                        		}
                        		chrvalue = chrvalue + clArr[p];
                        		if(count2<=0 || p == clArr.length-1){
                        			over.showTextAligned(Element.ALIGN_LEFT, chrvalue, x, y2, rotation);
                                	y2 -= fontsize+fontsize+7;//移到下一行写入
                                	chrvalue = "";//重置value
                                	count2 = count;//重置count2
                            	}
                        	}
                        }else if (attr.indexOf("JDBJ") > -1) {
                            over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        }else if (attr.startsWith("HUIQIAN")&&!attr.equals("HUIQIAN0")&&!attr.equals("HUIQIAN1")&&!attr.equals("HUIQIAN2")) {
                            over.showTextAligned(Element.ALIGN_LEFT, value, x, y-5, rotation);
                        }else if("MIJI".equals(attr) && "无".equals(value)){
                        	value = "";
                        	over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        }else if("ZIDINGYI".equals(attr)){
                        	value = "▇";
                        	over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        }else{
                        	String[] values = value.split("/");
                        	String signValue="";
                        	if(values.length>=3){
                        		signValue = values[0]+"      "+values[2];
                        	}else {
                        		signValue = values[0];
                        	}
                        	over.showTextAligned(Element.ALIGN_LEFT, signValue, x, y, rotation);
                            //over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        }
                        over.endText();
                        break;
                    }

                    if (outputpage == -1)// 写入尾页
                    {
                        PdfContentByte over = stamp.getOverContent(n);
                        over.beginText();
                        over.setFontAndSize(bf, fontsize);
                        if (attr.indexOf("JDBJ") > -1) {
                            over.showTextAligned(Element.ALIGN_LEFT, "  ", x, y, rotation);
                        }
                        if (attr.startsWith("HUIQIAN")&&!attr.equals("HUIQIAN0")&&!attr.equals("HUIQIAN1")&&!attr.equals("HUIQIAN2")) {
                            over.showTextAligned(Element.ALIGN_LEFT, value, x, y-5, rotation);
                        }else{
                            over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        }
                        over.endText();
                        break;
                    }

                    if (outputpage == -2)// 除了第一页都签
                    {
                    	if(j > 1){
	                        PdfContentByte over = stamp.getOverContent(j);
	                        over.beginText();
	                        over.setFontAndSize(bf, fontsize);
	                        if("NUMBER".equals(attr)){
                            	over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
	                        }else{
	                        	if(value.contains("/")){
	                            	String[] values = value.split("/");
	                            	String signValue="";
	                            	if(values.length>=3){
	                            		signValue = values[0]+"      "+values[2];
	                            	}else {
	                            		signValue = values[0];
	                            	}
	                            	if(format.contains("SOP") && (attr.equals("SHEJI") || attr.equals("JIAODUI") || attr.equals("SHENHE"))){
	                            		if(values[0].replaceAll(" ","").length()==2){
	                            			x = Float.valueOf("615").floatValue();
	                            		}
	                            	}
	                            	over.showTextAligned(Element.ALIGN_LEFT, signValue, x, y, rotation);
	                            }else{
	                            	over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
	                            }
	                        }

                            over.endText();
                    	}
                    }

                    if (outputpage == -3) // 写入结构化工艺文件 每一页
                    {
                        PdfContentByte over = stamp.getOverContent(j);
                        over.beginText();
                        over.setFontAndSize(bf, fontsize);
                        signHuiQianInfo(attr, value, locationx, x, y, rotation, hs,
    							over);
                        over.endText();
                    }
                }
            }
            stamp.close();
            return tempFile;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static void signHuiQianInfo(String attr, String value,
			String locationx, float x, float y, float rotation, Hashtable hs,
			PdfContentByte over) {
		if (attr.startsWith("HUIQIAN")) {
		  	float yspace = 18;
		  	Object yspaceObj = hs.get("yspace");
		  	if(yspaceObj != null){
		  		if(yspaceObj instanceof Float){
		  			yspace = (Float) yspaceObj;
		  		}else if(yspaceObj instanceof String){
		  			yspace = Float.parseFloat((String)yspaceObj);
		  		}
		  	}
		  	float xspace = 18;
		  	Object xspaceObj = hs.get("xspace");
		  	if(xspaceObj != null){
		  		if(xspaceObj instanceof Float){
		  			xspace = (Float) xspaceObj;
		  		}else if(xspaceObj instanceof String){
		  			if(xspaceObj != null && !"".equals(xspaceObj)){
		  				xspace = Float.parseFloat((String)xspaceObj);
		  			}
		  		}
		  	}
		  //FORMATGYECN_YANSHI_A4.uniquevalueattr11.direction=horizontal
		  	Object directionObj = hs.get("DIRECTION");
		  	String directionStr="";
		  	if(directionObj != null){
		  		if(directionObj instanceof String){
		  			if(directionObj != null && !"".equals(directionObj)){
		  				directionStr = (String) directionObj;
		  			}
		  		}
		  	}
		  	//=222/123/2015-04-20;444/333/2015-04-20
		      	String[] valuesStr = value.split(";");
		      	if("horizontal".equals(directionStr)){
		      		for(int huiqianIndex=0 ; huiqianIndex< valuesStr.length ; huiqianIndex++){
			      		String valueStr = valuesStr[huiqianIndex];
			      		 log.debug("-----attr:" + attr + "   value:" + valueStr + "   x:" + locationx + "   y:" + y+" yspace="+yspace+"  xspace="+xspace+" directionStr="+directionStr);
			      		 if(valueStr.contains("/")){
			      			 String[] values = valueStr.split("/");
			      			 String department = values[1];
			      			 String userName = values[0];
			      			 String signDate = values[2];
			      			 log.debug("-----attr:" + attr + "   value:" + department + "   x:" + x + "   y:" + (y-huiqianIndex*yspace));
			      			 over.showTextAligned(Element.ALIGN_LEFT, department, x, (y-huiqianIndex*yspace), rotation);
			      			 log.debug("-----attr:" + attr + "   value:" + userName + "   x:" + (x+xspace) + "   y:" + (y-huiqianIndex*yspace));
			      			 over.showTextAligned(Element.ALIGN_LEFT, userName, (x+xspace), (y-huiqianIndex*yspace), rotation);
			      			 log.debug("-----attr:" + attr + "   value:" + signDate + "   x:" + (x+2*xspace) + "   y:" + (y-huiqianIndex*yspace));
			     			 over.showTextAligned(Element.ALIGN_LEFT, signDate, (x+2*xspace), (y-huiqianIndex*yspace), rotation);
			      		 }else{
			      			 over.showTextAligned(Element.ALIGN_LEFT, valueStr, (x+huiqianIndex*xspace), (y-huiqianIndex*yspace), rotation);
			      		 }
			      	}
		      	}else{
		      	for(int huiqianIndex=0 ; huiqianIndex< valuesStr.length ; huiqianIndex++){
		      		String valueStr = valuesStr[huiqianIndex];
		      		 log.debug("-----attr:" + attr + "   value:" + valueStr + "   x:" + locationx + "   y:" + y+" yspace="+yspace+"  xspace="+xspace+" directionStr="+directionStr);
		      		 if(valueStr.contains("/")){
		      			 String[] values = valueStr.split("/");
		      			 String department = values[1];
		      			 String userName = values[0];
		      			 String signDate = values[2];
		      			 log.debug("-----attr:" + attr + "   value:" + department + "   x:" + (x+huiqianIndex*xspace) + "   y:" + y);
		      			 over.showTextAligned(Element.ALIGN_LEFT, department, (x+huiqianIndex*xspace), y, rotation);
		      			 log.debug("-----attr:" + attr + "   value:" + userName + "   x:" + (x+huiqianIndex*xspace) + "   y:" + (y-yspace));
		      			 over.showTextAligned(Element.ALIGN_LEFT, userName, (x+huiqianIndex*xspace), (y-yspace), rotation);
		      			 log.debug("-----attr:" + attr + "   value:" + signDate + "   x:" + (x+huiqianIndex*xspace) + "   y:" + (y-2*yspace));
		     			 over.showTextAligned(Element.ALIGN_LEFT, signDate, (x+huiqianIndex*xspace), (y-2*yspace), rotation);
		      		 }else{
		      			 over.showTextAligned(Element.ALIGN_LEFT, valueStr, (x+huiqianIndex*xspace), (y-huiqianIndex*yspace), rotation);
		      		 }
		      	}
		      	}
		  }else{
		    	over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
		  }
	}

    public static File writeToPDFForECN(String sourcefile, String printfile,
            Vector vector, int outputpage, String diretion, float fontsize) throws WTException {
        log.debug("------------writeToPDFForECN  outputpage:"+outputpage+"   diretion:"+diretion+"   fontsize:"+fontsize);
        try {
            String extention = Util.getExtension(sourcefile);
            String removeExtention = Util.removeExtension(sourcefile);

            File file = new File(sourcefile);

            PdfReader reader = new PdfReader(sourcefile);
            Rectangle pageSize = reader.getPageSize(1);// 595.92X842.0
            float width = pageSize.getWidth();
            // A0:3370,A1:2384,A2:1684,A3:1190,A4:595

            // 定义临时文件
            File tempFile = new File(printfile);
            log.debug("--------tempFile:"+tempFile);
            int n = reader.getNumberOfPages();
            log.debug("--------n:"+n);
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", false);
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(tempFile));

            String attr = "";
            String value = "";
            String locationx = "";
            String locationy = "";
            float x = 0;
            float y = 0;
            float rotation = 0;

            if (diretion.equalsIgnoreCase("vertical")){
                rotation = 0;
            } else if (diretion.equalsIgnoreCase("horizontal")){
                rotation = 270;
            }
            log.debug("--------vector:"+vector);
            // 读签审输出信息
            for (int i = 0; i < vector.size(); i++) {
                Hashtable hs = (Hashtable) vector.elementAt(i);
                attr = (String) hs.get(ATTR);
                value = (String) hs.get(ATTRVALUE);
                if (value.contains(",")) {
                    value = value.replaceAll(",", "");
                }
                locationx = (String) hs.get(LOCATIONX);
                locationy = (String) hs.get(LOCATIONY);
                x = Float.valueOf(locationx).floatValue();
                y = Float.valueOf(locationy).floatValue();
                // 向PDF输出信息
                int j = 0;
                while (j < n) {
                    j++;
                    log.debug("--------j:"+j);
                    log.debug("--------outputpage:"+outputpage);
                    if (outputpage == 0) // 写入每一页
                    {
                        PdfContentByte over = stamp.getOverContent(j);
                        over.beginText();
                        over.setFontAndSize(bf, fontsize);
                        over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        over.endText();
                    }

                    if (j == outputpage)// 写入指定页
                    {
                        PdfContentByte over = stamp.getOverContent(j);
                        over.beginText();
                        over.setFontAndSize(bf, fontsize);
                        over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        over.endText();
                        break;
                    }

                    if (outputpage == -1)// 写入尾页
                    {
                        PdfContentByte over = stamp.getOverContent(n);
                        over.beginText();
                        over.setFontAndSize(bf, fontsize);
                        over.showTextAligned(Element.ALIGN_LEFT, value, x, y, rotation);
                        over.endText();
                        break;
                    }
                }
            }
            stamp.close();
            return tempFile;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1
     *            被减数
     * @param v2
     *            减数
     * @return 两个参数的差
     */
    public static float sub(float v1, float v2) {
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.subtract(b2).floatValue();
    }
    public static boolean isChinese(String strName) {

        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }
    private static boolean isChinese(char c) {
    	if (isEnglish(c+"")){
    		return false;
    	}
    	if (isNum(c+"")){
    		return false;
    	}
    	return true;
    }
    private static boolean isEnglish(String c) {
    	return c.matches("^[a-zA-Z]*");
    }
    private static boolean isNum(String c) {
    	return c.matches("^[0-9]*");
    }
}