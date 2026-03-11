package com.glaway.mpm.print.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;

import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.util.CommonUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import ext.casc.util.PropertiesReader;

public class LocalPrintUtil {
	private static final String filePrintProperties = "com/glaway/mpm/print/config/filePrintPdfSignatureConfig";
	static PropertiesReader signtemplateReader = new PropertiesReader(filePrintProperties, "UTF-8", "ISO-8859-1");
	static final int LINE = 7;
	/**
	 * 打印申请生成PDF
	 * @param filePath PDF路径
	 * @param QrcodePath 二维码路径
	 * @param sealPath 印章路径
	 * @param batch 批次
	 * @return
	 */
	public static int generatePDF(String filePath, String QrcodePath, String sealPath, String batch,String fileType,String qRName,String dept){
		//int[] QrcodeCoordinates = {180, 120};//二维码坐标
		//int[] sealCoordinates = {110, 500};//印章坐标
		int countPage = 0;//统计PDF页数
		String qrCodeFMCoords = signtemplateReader.getValue(fileType + "_QRCode_FM");//二位码图片
		String qrCodeFMCoordsSize = signtemplateReader.getValue(fileType + "_QRCode_FM_SIZE");//二位码图片大小
		String qrCodeTextFMCoords = signtemplateReader.getValue(fileType + "_QRCodeText_FM");//条码坐标
		String qrCodeTextFontSize = signtemplateReader.getValue(fileType + "_QRCodeText_FONTSIZE");//条码字体大小
		String baselineCoords = signtemplateReader.getValue(fileType + "_Baseline");//基线初始坐标
		String baselineCoordsFONTSIZE = signtemplateReader.getValue(fileType + "_Baseline_FONTSIZE");//基线字体大小
		String yOffSet = signtemplateReader.getValue(fileType + "_Baseline_yOffSet");//基线Y方向坐标
		String sealCoordinates = signtemplateReader.getValue(fileType + "_Seal_FM");//受控章坐标
		if(null == qrCodeFMCoords || "".equals(qrCodeFMCoords)){
			qrCodeFMCoords="180,120";
		}
		if(null == qrCodeTextFMCoords || "".equals(qrCodeTextFMCoords)){
			qrCodeTextFMCoords="180,80";
		}
		if(null == baselineCoords || "".equals(baselineCoords)){
			baselineCoords="650,320";
		}
		if(null == yOffSet || "".equals(yOffSet)){
			yOffSet="30";
		}
		if(null == sealCoordinates || "".equals(sealCoordinates)){
			sealCoordinates="110,500";
		}

		// 读取模板文件
		try {
			InputStream input = new FileInputStream(new File(filePath));
			PdfReader reader = new PdfReader(input);
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(filePath));
			PdfContentByte over = stamper.getOverContent(1);
			//将图片写入pdf
			/**
			 * 二维码输出
			 */
			if(QrcodePath != null && !"".equals(QrcodePath)){
				String[] coordinates = qrCodeFMCoords.split(",");
				float axisX =  Float.parseFloat(coordinates[0]);
				float axisY =  Float.parseFloat(coordinates[1]);
				float qrCodeFMSize =  Float.parseFloat(qrCodeFMCoordsSize);
				Image  Qrcode = Image.getInstance(QrcodePath);
				Qrcode.scalePercent(qrCodeFMSize);
				over.addImage(Qrcode, Qrcode.getScaledWidth(), 0f, 0f, Qrcode.getScaledHeight(),axisX, axisY);
			}
			/**
			 * 受控章输出
			 */
			if(sealPath != null && !"".equals(sealPath)){
				Image  seal = Image.getInstance(sealPath);
				String[] coordinates = sealCoordinates.split(",");
				float axisX =  Float.parseFloat(coordinates[0]);
				float axisY =  Float.parseFloat(coordinates[1]);
				over.addImage(seal, seal.getScaledWidth(), 0f, 0f, seal.getScaledHeight(), axisX, axisY);
			}

			/**
			 * 批次章输出规则：
			 * 1、宋体四号
			 * 2、横向输出，从上至下排列
			 * 3、横向最大输出字符数量：20
			 * 4、纵向最大排列数量：8
			 * 5、仅工艺规程按此规则输出
			 */
			String[] batchList = null;
			if(batch != null && !"".equals(batch) && !"null".equals(batch)){
				BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				String[] coordinates = baselineCoords.split(",");
				float axisX =  Float.parseFloat(coordinates[0]);
				float axisY =  Float.parseFloat(coordinates[1]);
				float offset =  Float.parseFloat(yOffSet);
				float fontsize =  Float.parseFloat(baselineCoordsFONTSIZE);
				over.beginText();
	            over.setFontAndSize(bf, fontsize);
	            over.setColorFill(BaseColor.RED);
				if(batch.contains(",")){
					batchList = batch.split(",");
					for(int i=0; i<batchList.length;i++){
						if(i <= LINE){
							String batchSub = batchList[i];
							if("null".equals(batchSub)){
								continue;
							}
							if(batchSub.length() > 20){
								batchSub = batchList[i].substring(0, 20);
							}
							over.showTextAligned(Element.ALIGN_LEFT, batchSub, axisX, axisY, 0);
							axisY = axisY - offset;
						}
					}
				}else if(batch.contains(";")){
					batchList = batch.split(";");
					for(int i=0; i<batchList.length;i++){
						if(i <= LINE){
							String batchSub = batchList[i];
							if("null".equals(batchSub)){
								continue;
							}
							if(batchSub.length() > 20){
								batchSub = batchList[i].substring(0, 20);
							}
							over.showTextAligned(Element.ALIGN_LEFT, batchSub, axisX, axisY, 0);
							axisY = axisY - offset;
						}
					}
				}else{
					over.showTextAligned(Element.ALIGN_LEFT, batch, axisX, axisY, 0);
				}
				over.endText();
			}
			if(null != qRName || !"".equals(qRName)){
				BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				String[] coordinates = qrCodeTextFMCoords.split(",");
	            float axisX =  Float.parseFloat(coordinates[0]);
				float axisY =  Float.parseFloat(coordinates[1]);
				float fontSize =  Float.parseFloat(qrCodeTextFontSize);
				over.beginText();
				over.setFontAndSize(bf, fontSize);
				over.setColorFill(BaseColor.BLACK);
				over.showTextAligned(Element.ALIGN_LEFT, qRName, axisX, axisY, 0);
				over.endText();
			}
			if(null != dept || !"".equals(dept)){
				BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				String[] coordinates = qrCodeTextFMCoords.split(",");
	            float axisX =  Float.parseFloat(coordinates[0]);
				float axisY =  Float.parseFloat(coordinates[1])-10;
				float fontSize =  Float.parseFloat(qrCodeTextFontSize);
				over.beginText();
				over.setFontAndSize(bf, fontSize);
				over.setColorFill(BaseColor.BLACK);
				over.showTextAligned(Element.ALIGN_LEFT, dept, axisX, axisY, 0);
				over.endText();
			}
			countPage = reader.getNumberOfPages();
			if("PROCESS_PLAN".equals(fileType)||"PROCESS_NOTICE".equals(fileType)||"TECHNOLOGY_AGREEMENT".equals(fileType)
					||"CHANGEORDER".equals(fileType)||"OTHERREPORT".equals(fileType)){
				int page = reader.getNumberOfPages();
				if(page%2==1){
					stamper.insertPage(page+1, new Rectangle(600, 800));//增加一页空白的PDF
				}
			}
			stamper.close();
	        reader.close();
	        input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return countPage;
	}

	public static String changeIntToStr(int number) {
        //数字对应的汉字
        String[] num = {"零","一","二","三","四","五","六","七","八","九"};
        //单位
        String[] unit = {"","十","百","千","万","十","百","千","亿","十","百","千","万亿"};
        //将输入数字转换为字符串
        String result = String.valueOf(number);
        //将该字符串分割为数组存放
        char[] ch = result.toCharArray();
        //结果 字符串
        String str = "";
        int length = ch.length;
        for (int i = 0; i < length; i++) {
            int c = (int)ch[i]-48;
            if(c != 0) {
                str += num[c]+unit[length-i-1];
            } else {
                str += num[c];
            }
        }
//        System.out.println(str);
        return str;
    }

	/**
	 * 判断是否符合录入要求
	 * @param cmPrintQueryBean
	 * @return
	 */
	public static boolean buildQueryBean(CmPrintQueryBean cmPrintQueryBean) {
		String number = cmPrintQueryBean.getFileNumber();
		String name = cmPrintQueryBean.getFileName();
//		String version = cmPrintQueryBean.getVersion();
		String fileType = cmPrintQueryBean.getFileType();
		String phaseCode = cmPrintQueryBean.getPhaseCode();
		String pageCount = cmPrintQueryBean.getPageCount();
		if(number == null || "".equals(number)){
			return false;
		}
		if(name == null || "".equals(name)){
			return false;
		}
		if(fileType == null || "".equals(fileType)){
			return false;
		}
		if(phaseCode == null || "".equals(phaseCode)){
			return false;
		}
		if(pageCount == null || "".equals(pageCount)){
			return false;
		}
		return true;
	}

	public static boolean buildChangeNotice(CmPrintQueryBean cmPrintQueryBean){
		String number = cmPrintQueryBean.getFileNumber();
		String date = cmPrintQueryBean.getChangeData();
		String content = cmPrintQueryBean.getChangeContent();
		if(number == null || "".equals(number)){
			return false;
		}
		if(date == null || "".equals(date)){
			return false;
		}
		if(content == null || "".equals(content)){
			return false;
		}
		return true;
	}

	/**
	 * 通过打印状态获取已分发的文件编号 add by zhuhao
	 * @param cmPrintInfoBean
	 * @return
	 */
	public static String getDistributed(CmPrintInfoBean cmPrintInfoBean) {
		String name = "";
		if(!"未分发".equals(CommonUtil.objectToString(cmPrintInfoBean.getFileState()))){
			name = CommonUtil.objectToString(cmPrintInfoBean.getFileName());
		}
		return name;
	}

	/**
	 *通过受控状态获取已分发的文件编号
	* @author zhuhao
	* @date 2018-4-11
	* @return
	 */
	public static String getChackLifeCycle(CmPrintInfoBean cmPrintInfoBean) {
		String name = "";
		if(!"已批准".equals(CommonUtil.objectToString(cmPrintInfoBean.getLifeCycle()))){
			name = CommonUtil.objectToString(cmPrintInfoBean.getFileName());
		}
		return name;
	}

	/**
	 * 判断扫的二维码是文件二维码还是工牌
	* @author zhuhao
	* @date 2018-5-21
	* @param str
	* @return
	 */
	public static boolean checkStr(String str) {
		int count = 0;
		char[] ca = str.toCharArray();
		for(int i = 0; i < ca.length; i++){
			if(ca[i] == '|'){
				count++;
			}
		}
		if(count == 6){
			return true;
		}
		return false;
	}

	/**
	 * 文件二维码构造map
	 * DABH:二维码,WJBH:文件编号,MC:名称,LX:类型,MJ:密级,BB:版本,SL:数量
	* @author zhuhao
	* @date 2018-5-21
	* @param scanInput
	* @return
	 */
	public static Map<String, String> buildFileMap(String scanInput) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> list = java.util.Arrays.asList(scanInput.split("\\|"));
		for(String info : list){
			String key = info.substring(0, info.indexOf(":"));
			String value = info.substring(info.indexOf(":") + 1, info.length());
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 修改打印申请构造第一次加载文件id集合
	* @author zhuhao
	* @date 2018-5-23
	* @param printInfoBeanList
	* @return
	 */
	public static List<String> buildModifyId(List<CmPrintInfoBean> printInfoBeanList) {
		if(printInfoBeanList == null || printInfoBeanList.isEmpty()){
			return null;
		}
		List<String> list = new ArrayList<String>();
		for(CmPrintInfoBean cmPrintInfoBean : printInfoBeanList){
			String oid = cmPrintInfoBean.getDocVR();
			list.add(oid);
		}
		return list;
	}

	/**
	 * 外来/纸质文件打印申请校验
	* @author zhuhao
	* @date 2018-6-12
	* @param table
	* @return
	 */
	public static String checkFileApply(JTable table) {
		List<String> list = new ArrayList<String>();
		String containerName = "";
		for (int row = 0; row < table.getRowCount(); row++) {
			CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
			String fileNumber = cmPrintInfoBean.getFileNumber();
			String fileName = cmPrintInfoBean.getFileName();
			String version = cmPrintInfoBean.getVersion();
			String str = fileNumber + "," + fileName + "," + version;
			if(list.contains(str)){
				return "列表内有相同的文档";
			}
			list.add(str);
			String deptAndCount = CommonUtil.objectToString(table.getValueAt(row, 8));
			if ("".equals(deptAndCount)) {//校对是否未设置分发部门和份数
				return "分布部门或份数不能为空";
			}
			if("".equals(containerName)){
				containerName = CommonUtil.objectToString(cmPrintInfoBean.getContainerName());
			}
			if(!containerName.equals(CommonUtil.objectToString(cmPrintInfoBean.getContainerName()))){
				return  "存在型号类型不相同的文档";
			}
		}
		return "";
	}
}
