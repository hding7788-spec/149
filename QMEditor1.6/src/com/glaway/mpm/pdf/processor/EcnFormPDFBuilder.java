package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.pdf.CharUtil;
import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.XmlUtility;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.change.CSCChange;
import ext.casc.ecn.changeinfo.ModifyChangeinfo;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.util.IBAUtility;
import ext.casc.workflow.PrintHelper;
import org.dom4j.Document;
import org.dom4j.Element;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;

import java.beans.PropertyVetoException;
import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EcnFormPDFBuilder extends PDFBuilder{

	private WTChangeOrder2 changeOrder2;
	private String techFloder = null;
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
	public EcnFormPDFBuilder(WTChangeOrder2 changeOrder2, String filePath, String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.changeOrder2 = changeOrder2;
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			IBAUtility changeOrder2Iba = new IBAUtility(changeOrder2);
			String language =changeOrder2Iba.getIBAValue("Language");
			template = PDFUtil.getFormTemplateFolderPath() + "EcnForm.pdf";
			if("英文".equals(language)){
				template = PDFUtil.getFormTemplateFolderPath() + "EcnForm_English.pdf";
			}
			formName = formName + "_" + page;
			templateList.add(formName);
			printer.addTempl(formName, template);
			//----获取编制活动的完成时间----add by liangbo----

			//-----------end-----------

			//记录基本数据，比如工艺文件编号等
			setCommData(printer,formName);
//			writeElement(changeOrder2, printer, index, formName);
			String[] changeBefor = null;
			String[] changeAfter = null;
			String[] changeCause = null;
			String[] designChangeNum = null;
			String changeBefor2 = "";
			String changeAfter2 = "";
			String changeCause2 = "";
			String sdesignChangeNum = "";
			List<Map<String, Object>> allinfo = null;
			List<String> changeCuse2 = new ArrayList<String>();
			List<String> denumbers = new ArrayList<String>();
			//获取更改单号 add by zhuhao 2017.5.23
			String num = changeOrder2.getNumber();
			allinfo = ModifyChangeinfo.searchAll(num);
			//判断数据库内是否已有数据 add by zhuhao 2017.5.23
			if(!allinfo.isEmpty()){
				for(int i=0;i<allinfo.size();i++){
					changeBefor2 += (String) allinfo.get(i).get("CHANGEINFO_BEFORE")+"~";
					changeAfter2 += (String) allinfo.get(i).get("CHANGEINFO_BEHIND")+"~";
					changeCause2 += (String) allinfo.get(i).get("CHANGEINFO_REASON")+"~";
					sdesignChangeNum += (String) allinfo.get(i).get("CHANGEINFO_PLAN")+"~";
				}
				if(!"".equals(sdesignChangeNum)){
					changeBefor2 = changeBefor2.substring(0,changeBefor2.length()-1);
					changeAfter2 =changeAfter2.substring(0,changeAfter2.length()-1);
					changeCause2 = changeCause2.substring(0,changeCause2.length()-1);
					sdesignChangeNum = sdesignChangeNum.substring(0,sdesignChangeNum.length()-1);
					}
				changeBefor = changeBefor2.split("~");
				changeAfter = changeAfter2.split("~");
				changeCause = changeCause2.split("~");

			}else{

				changeBefor = changeOrder2Iba.getIBAValue("CHANGEBEFOR").split("~");
				changeAfter = changeOrder2Iba.getIBAValue("CHANGEAFTER").split("~");
				changeCause = changeOrder2Iba.getIBAValue("CHANGECAUSE").split("~");
				sdesignChangeNum = changeOrder2Iba.getIBAValue("DESIGNCHANGENUM");

			}
			if(sdesignChangeNum!=null&&!"".equals(sdesignChangeNum)){
				designChangeNum =sdesignChangeNum.split("~");
				for(String str:designChangeNum){
					if(str!=null && !"null".equals(str) && !"".equals(str)){
						denumbers.add(str);
					}
				}
			}

			for(int j=0;j<changeCause.length;j++){
				String str = (String)changeCause[j];
				if("设计更改".equals(str)){
					str = str + (String)denumbers.get(0);
					denumbers.remove(0);
				}
				changeCuse2.add(str);
			}
			if (changeCuse2 != null && changeCuse2.size() > 0) {
				for (int i = 0; i < changeCuse2.size(); i++) {
					writeElement((String) changeBefor[i], (String) changeAfter[i], (String) changeCuse2.get(i), printer, index, formName);
					if (index != 1) {
						index = index + 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCommData(LcmPdfPrinter printer, String templateName) {

		try {
			IBAUtility changeOrder2Iba = new IBAUtility(changeOrder2);
			String ecntype = changeOrder2Iba.getIBAValue("ECNTYPE");
			WTDocument beforDoc = null;
			WTDocument afterDoc = null;
			MPMProcessPlan beforPPlan = null;
			WTPart beforPart = null;

			List cas = CSCChange.getReleatedCA(changeOrder2, false);
			for (int j = 0; j < cas.size(); j++) {
				WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
				ArrayList<WTObject> befors = CSCChange.getCAAffectedData(ca);
				for (int i = 0; i < befors.size(); i++) {
					WTObject befor = befors.get(i);
					if (befor instanceof MPMProcessPlan) {
						beforPPlan = (MPMProcessPlan) befor;
					} else if (befor instanceof WTPart) {
						beforPart = (WTPart) befor;
					} else if(befor instanceof WTDocument) {
						beforDoc = (WTDocument) befor;
					}
				}
				ArrayList<WTObject> afters = CSCChange.getCAResultItem(ca);
				for (int i = 0; i < afters.size(); i++) {
					WTObject after = afters.get(i);
					if (after instanceof WTDocument) {
						afterDoc = (WTDocument) after;
					}
				}
			}
			String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(changeOrder2).toString();

			//文档更改打印
			if(objectType.indexOf("casc.sast.149.DOCUMENT_ECN") > -1){
				// 受影响文档对象
				if (beforDoc != null) {
					System.out.println("beforDoc======" + beforDoc);
					IBAUtility beforDocIba = new IBAUtility(beforDoc);
					String time = getBianzhiTime(beforDoc);
					if(time == null || "".equals(time)){
						Timestamp tamptime = beforDoc.getCreateTimestamp();
						java.text.DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
						time = format.format(tamptime);
					}
					String version = beforDoc.getIterationDisplayIdentifier().toString();
					String name = beforDoc.getName();
					String str = time + "编制的" + version + "版本的工艺规程" + name + "作废";

					printer.addText(templateName, "更改前文件信息", str);

					String phaseCode = PDFUtil.objectToString(beforDocIba.getIBAValue("PHASE_CODE"));
					if(phaseCode == null || "".equals(phaseCode)){
						IBAUtility changeOrderIba = new IBAUtility(changeOrder2);
						phaseCode = PDFUtil.objectToString(changeOrderIba.getIBAValue("PHASE_CODE"));
					}
					String index = PDFUtil.getPhaseCodeIndex(phaseCode);
					printer.addText(templateName, "阶段标记_" + index, phaseCode);
				}

				// 更改后文档
				if (afterDoc != null) {
					IBAUtility afterDocIba = new IBAUtility(afterDoc);
					//文档没有产品代号，映射型号代号
					printer.addText(templateName, "产品代号", PDFUtil.objectToString(afterDocIba.getIBAValue("MINDEX")));

					// 更改后信息
					String str = "";
					if (!"作废更改".equals(ecntype)) {
						String time = getBianzhiTime(changeOrder2);
						if(time == null || "".equals(time)){
							Timestamp tamptime = afterDoc.getCreateTimestamp();
							java.text.DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
							time = format.format(tamptime);
						}
						String version = afterDoc.getIterationDisplayIdentifier().toString();
						String name = afterDoc.getName();
						str = "由" + time + "编制的" + version + "版本的工艺规程" + name + "代替";
					}
					printer.addText(templateName, "更改后文件信息", str);
				}

				printer.addText(templateName, "页数", String.valueOf(page));
				printer.addText(templateName, "当前页数", String.valueOf(page));

				printer.addText(templateName, "更改标记", PDFUtil.objectToString(changeOrder2Iba.getIBAValue("CHANGETYPE")));

				printer.addText(templateName, "编号", PDFUtil.objectToString(changeOrder2.getNumber()));

				String changeNoticeType = getChangeNoticeType(PDFUtil.objectToString(changeOrder2Iba.getIBAValue("CHANGENOTICETYPE")));
				printer.addText(templateName, "更改类别", changeNoticeType);

				String yizhiping = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("YIZHIPING"));
				String zaizhiping = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("ZAIZHIPING"));
				if(!"".equals(yizhiping) || !"".equals(zaizhiping)){
					printer.addText(templateName, "制品处理意见", "已制品" + yizhiping + ", 在制品" + zaizhiping);
				}

				String secret = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("SECRET"));
				if ("无".equals(secret)) {
					secret = "公开";
				}
				printer.addText(templateName, "密级", secret);
				printer.addText(templateName, "实施日期", "自批准之日起");
				printer.addText(templateName, "备注", PDFUtil.objectToString(changeOrder2Iba.getIBAValue("REMARK")));
			}else{
				// 受影响对象
				if (beforPPlan != null) {
					if (!"新增更改".equals(ecntype)) {
						if(beforDoc == null){
							beforDoc = MPMProcessPlanUtil.getWTDocumentByProcessPlan(beforPPlan);
						}
						System.out.println("beforDoc======" + beforDoc);
						IBAUtility beforDocIba = new IBAUtility(beforDoc);
						String changeBeforInfor = changeOrder2Iba.getIBAValue("CHANGEBEFORINFOR");
						if (changeBeforInfor != null && !"null".equals(changeBeforInfor) && !"".equals(changeBeforInfor)) {
							printer.addText(templateName, "更改前文件信息", changeBeforInfor);
						} else {
							String str = "";
							String time = getBianzhiTime(beforDoc);//优化点
							if(time == null || "".equals(time)){
								Timestamp tamptime = beforDoc.getCreateTimestamp();
								java.text.DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
								time = format.format(tamptime);
							}
							String version = beforDoc.getVersionIdentifier().getValue() + "." + beforDoc.getIterationIdentifier().getValue();
							String batch = beforDocIba.getIBAValue("BATCH");
							if(batch == null){
								Element technicsElement = getTechnicsElement(afterDoc);
								batch = technicsElement.attributeValue("PCNO");
							}
							String ppnum = beforDocIba.getIBAValue("PPNUMBER");

							if (batch != null && !"".equals(batch) && !"null".equals(batch)) {
								str = time + "编制的" + version + "(" + batch + ")" + "版本的工艺规程" + ppnum + "作废";
							} else {
								str = time + "编制的" + version + "版本的工艺规程" + ppnum + "作废";
							}
							printer.addText(templateName, "更改前文件信息", str);
						}

						String phaseCode = PDFUtil.objectToString(beforDocIba.getIBAValue("PHASE_CODE"));
						if(phaseCode == null || "".equals(phaseCode)){
							IBAUtility changeOrderIba = new IBAUtility(changeOrder2);
							phaseCode = PDFUtil.objectToString(changeOrderIba.getIBAValue("PHASE_CODE"));
						}
						String index = PDFUtil.getPhaseCodeIndex(phaseCode);
						printer.addText(templateName, "阶段标记_" + index, phaseCode);
					}
				}

				// 更改后工艺规程对象
				if (afterDoc != null) {
					IBAUtility afterDocIba = new IBAUtility(afterDoc);
					printer.addText(templateName, "产品代号", PDFUtil.objectToString(afterDocIba.getIBAValue("PINDEX")));

					// 更改后信息
					String str = "";
					if (!"作废更改".equals(ecntype)) {
						String time = getBianzhiTime(changeOrder2);
						if(time == null || "".equals(time)){
							Timestamp tamptime = afterDoc.getCreateTimestamp();
							java.text.DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
							time = format.format(tamptime);
						}
						String version = afterDoc.getVersionIdentifier().getValue() + "." + afterDoc.getIterationIdentifier().getValue();
						String batch = afterDocIba.getIBAValue("BATCH");
						if(batch == null){
							Element technicsElement = getTechnicsElement(afterDoc);
							if(technicsElement!=null) {
								batch = technicsElement.attributeValue("PCNO");
							}
						}
						String ppnum = afterDocIba.getIBAValue("PPNUMBER");
						if("新增更改".equals(ecntype)){
							if (batch != null && !"".equals(batch) && !"null".equals(batch)) {
								str = "新增" + time + "编制的" + version + "(" + batch + ")" + "版本的工艺规程" + ppnum;
							} else {
								str = "新增" + time + "编制的" + version + "版本的工艺规程" + ppnum;
							}
						}else{
							if (batch != null && !"".equals(batch) && !"null".equals(batch)) {
								str = "由" + time + "编制的" + version + "(" + batch + ")" + "版本的工艺规程" + ppnum + "代替";
							} else {
								str = "由" + time + "编制的" + version + "版本的工艺规程" + ppnum + "代替";
							}
						}

					}
					printer.addText(templateName, "更改后文件信息", str);
				}
				// 新增更改
				if (beforPart != null) {
					IBAUtility beforPartIba = new IBAUtility(beforPart);
					String phaseCode = PDFUtil.objectToString(beforPartIba.getIBAValue("PHASE_CODE"));
					String index = PDFUtil.getPhaseCodeIndex(phaseCode);
					printer.addText(templateName, "阶段标记_" + index, phaseCode);
					printer.addText(templateName, "产品代号", PDFUtil.objectToString(beforPartIba.getIBAValue("PINDEX")));
				}

				printer.addText(templateName, "页数", String.valueOf(page));
				printer.addText(templateName, "当前页数", String.valueOf(page));

				String fafangdanwei = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("FAFANGDANWEI"));
				String fafangfenshu = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("FAFANGFENSHU"));
				printer.addText(templateName, "发放单位", fafangdanwei + "  " + fafangfenshu);

				String gongYiWenJianMuLuNum = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("GONGYIWENJIANMULUNUM"));
				System.out.println("gongYiWenJianMuLuNum========" + gongYiWenJianMuLuNum);
				printer.addText(templateName, "工艺文件目录编号", gongYiWenJianMuLuNum);

				printer.addText(templateName, "更改标记", PDFUtil.objectToString(changeOrder2Iba.getIBAValue("CHANGETYPE")));

				WTPart part = getPartByIbaGYML(gongYiWenJianMuLuNum);
				if (part != null) {
					printer.addText(templateName, "部件编号", PDFUtil.objectToString(part.getNumber() + "   " + part.getName()));
				}
				printer.addText(templateName, "编号", PDFUtil.objectToString(changeOrder2.getNumber()));

				String changeNoticeType = getChangeNoticeType(PDFUtil.objectToString(changeOrder2Iba.getIBAValue("CHANGENOTICETYPE")));
				printer.addText(templateName, "更改类别", changeNoticeType);

				String yizhiping = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("YIZHIPING"));
				String zaizhiping = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("ZAIZHIPING"));
				printer.addText(templateName, "制品处理意见", "已制品" + yizhiping + ", 在制品" + zaizhiping);

				String secret = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("SECRET"));
				if ("无".equals(secret)) {
					secret = "公开";
				}
				printer.addText(templateName, "密级", secret);
				printer.addText(templateName, "实施日期", "自批准之日起");
				printer.addText(templateName, "备注", PDFUtil.objectToString(changeOrder2Iba.getIBAValue("REMARK")));
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch(RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeElement(String befor, String after, String cause, LcmPdfPrinter printer,int row,String templateName) {
		//恢复默认值
		isNew = false;
		r1 = 0;
		r2 = 0;

		if(row>11) {
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
        	if(!templateList.contains(formName)){
        		templateList.add(formName);
        	}
			printer.addTempl(formName, template);
			//记录基本数据，比如工艺文件编号等
			setCommData2(printer,formName);
		}
			writeProcess(PDFUtil.objectToString(cause), 4, printer, "更改原因_",row,templateName);
			writeProcess(PDFUtil.objectToString(befor), 17, printer, "更改前内容_",row,templateName);
			writeProcess(PDFUtil.objectToString(after), 17, printer, "更改后内容_",row,templateName);
			 if(isNew) {
		        	index += r2;
		        } else {
		        	index += r1;
		        }

	}

	private int writeProcess(String valueStr,int count,LcmPdfPrinter printer,String key,int row,String templateName) {
		valueStr =valueStr.replace("\n", "");
		valueStr =valueStr.replace("\r", "");
        char[] clArr = valueStr.toCharArray();

        //n用于计算valueStr总共需要几行来写入数据
        int n = 1;
        //用于记录当前行还剩余几个字符空间
        float tempCount = count;
        //记录当前数据占了多少行
        int k = 0;
        boolean b = false;
        //循环n行，将第n行的数据写入PDF页面的row+i行
        //用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
    	String value = "";
    	for(int i=0;i<clArr.length;i++) {
    		if(CharUtil.isChinese(String.valueOf(clArr[i]))) {
				if(tempCount>1) {
					tempCount = tempCount - 1;
				} else {
					tempCount = tempCount - 1;
				}
			} else {
				tempCount = tempCount - 0.58f;
			}
			value = value + clArr[i];
			if(tempCount > 0 && i < (clArr.length-1)) {
	    		continue;
	    	}
	    	if(value == null || "".equals(value.trim())) {
	    		continue;
	    	}

    		if(row > 11) {//如果PDF页面已经写到最后一行，则需要增加一页来继续写
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
        			setCommData2(printer,formName);
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
    		n++;
    		tempCount = count;
    		value = "";
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

	public static WTPart getPartByIbaGYML(String gongYiWenJianMuLuNum){
		String name = "工艺文件目录"+"("+gongYiWenJianMuLuNum+")";
		WTPart part = null;
		//			part = TechnicsIntf.getRelatedPartByReportTechnisName(name);
		part = ProcessEditorToWCIntfRMI.getRelatedPartByReportTechnisName(name);
		return part;
	}


	public static String getChangeNoticeType(String str){

		if("1类".equals(str) ){
			return "1类";
		}else if("首飞后2类".equals(str) || "设计2类".equals(str)|| "首飞前2类".equals(str) ){
			return "2类";
		}else if("3类".equals(str) || "设计3类".equals(str)){
			return "3类";
		}else if("Ⅰ类".equals(str) || "设计Ⅰ类".equals(str)){
			return "Ⅰ类";
		}else if("首飞后Ⅱ类".equals(str) || "设计Ⅱ类".equals(str)|| "首飞前Ⅱ类".equals(str) ){
			return "Ⅱ类";
		}else if("Ⅲ类".equals(str) || "设计Ⅲ类".equals(str)){
			return "Ⅲ类";
		}

		return str;
	}

	public void setCommData2(LcmPdfPrinter printer, String templateName) {

		try {
			IBAUtility changeOrder2Iba = new IBAUtility(changeOrder2);
			String ecntype = changeOrder2Iba.getIBAValue("ECNTYPE");
			WTDocument beforDoc = null;
			WTDocument afterDoc = null;
			MPMProcessPlan beforPPlan = null;
			WTPart beforPart = null;

			List cas = CSCChange.getReleatedCA(changeOrder2, false);
			for (int j = 0; j < cas.size(); j++) {
				WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
				ArrayList<WTObject> befors = CSCChange.getCAAffectedData(ca);
				for (int i = 0; i < befors.size(); i++) {
					WTObject befor = befors.get(i);
					if (befor instanceof MPMProcessPlan) {
						beforPPlan = (MPMProcessPlan) befor;
					} else if (befor instanceof WTPart) {
						beforPart = (WTPart) befor;
					}
				}
				ArrayList<WTObject> afters = CSCChange.getCAResultItem(ca);
				for (int i = 0; i < afters.size(); i++) {
					WTObject after = afters.get(i);
					if (after instanceof WTDocument) {
						afterDoc = (WTDocument) after;
					}
				}
			}
			// 受影响对象
			if (beforPPlan != null) {
				if (!"新增更改".equals(ecntype)) {
					beforDoc = MPMProcessPlanUtil.getWTDocumentByProcessPlan(beforPPlan);
					System.out.println("beforDoc======" + beforDoc);
					IBAUtility beforDocIba = new IBAUtility(beforDoc);

					String phaseCode = PDFUtil.objectToString(beforDocIba.getIBAValue("PHASE_CODE"));
					if(phaseCode == null || "".equals(phaseCode)){
						IBAUtility changeOrderIba = new IBAUtility(changeOrder2);
						phaseCode = PDFUtil.objectToString(changeOrderIba.getIBAValue("PHASE_CODE"));
					}
					String index = PDFUtil.getPhaseCodeIndex(phaseCode);
					printer.addText(templateName, "阶段标记_" + index, phaseCode);
				}
			}

			// 更改后工艺规程对象
			if (afterDoc != null) {
				IBAUtility afterDocIba = new IBAUtility(afterDoc);
				printer.addText(templateName, "产品代号", PDFUtil.objectToString(afterDocIba.getIBAValue("PINDEX")));

			}
			// 新增更改
			if (beforPart != null) {
				IBAUtility beforPartIba = new IBAUtility(beforPart);
				String phaseCode = PDFUtil.objectToString(beforPartIba.getIBAValue("PHASE_CODE"));
				String index = PDFUtil.getPhaseCodeIndex(phaseCode);
				printer.addText(templateName, "阶段标记_" + index, phaseCode);
				printer.addText(templateName, "产品代号", PDFUtil.objectToString(beforPartIba.getIBAValue("PINDEX")));
			}

			printer.addText(templateName, "页数", String.valueOf(page));
			printer.addText(templateName, "当前页数", String.valueOf(page));

			String fafangdanwei = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("FAFANGDANWEI"));
			String fafangfenshu = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("FAFANGFENSHU"));
			printer.addText(templateName, "发放单位", fafangdanwei + "  " + fafangfenshu);

			printer.addText(templateName, "编号", PDFUtil.objectToString(changeOrder2.getNumber()));


			String secret = PDFUtil.objectToString(changeOrder2Iba.getIBAValue("SECRET"));
			if ("无".equals(secret)) {
				secret = "公开";
			}
			printer.addText(templateName, "密级", secret);

		} catch (WTException e) {
			e.printStackTrace();
		}
	}
	private String getBianzhiTime(WTObject pbo) throws WTException{
		if(pbo instanceof WTChangeOrder2){
			return getProcessBianzhiTime(pbo);
		}else if(pbo instanceof WTDocument){
			WTDocument doc =(WTDocument)pbo;
			WTCollection coll2 = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
			Iterator it2 = coll2.iterator();
			if (it2.hasNext()) {
       		    WTChangeOrder2 ecn =(WTChangeOrder2) ((ObjectReference) it2.next()).getObject();
       		    return getProcessBianzhiTime(ecn);
       	  	}else{
       	  		return getProcessBianzhiTime(doc);
       	  	}
		}
		return null;
	}

	private String getProcessBianzhiTime(WTObject pbo){
		QueryResult qrProcs;
		try {
			qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
			WfProcess proc = null;
			while (qrProcs.hasMoreElements()) {
				WfProcess process = (WfProcess) qrProcs.nextElement();
				//if (process.getState().equals(WfState.OPEN_RUNNING) || process.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
					if(process.getStartTime()==null){
						continue;
					}
					if(proc!=null ){
						if(process.getStartTime().after(proc.getStartTime())){
							proc = process;
						}
					}else{
						proc = process;
					}
				//}

			}
			if(proc==null){
				return null;
			}
			List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
			activityList = PrintHelper.getActivities(proc, activityList);
			Iterator iterator = activityList.iterator();
			while(iterator.hasNext()){
				WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
				String activityName = wfactivity.getName();
				if(activityName.equals("编制")){
					Timestamp endTime = wfactivity.getEndTime();
					if(endTime != null){
						return WTStandardDateFormat.format(wfactivity.getEndTime(), "yyyy/MM/dd");
					}
				}
			}
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public Element getTechnicsElement(WTDocument document){
		byte[] bytes = null;
		if (document != null) {
			ApplicationData data;
			try {
				data = WTDocumentUtil.getPrimaryByDocument(document);
				bytes = WTDocumentUtil.applicationDataToByte(data);
				String fileName = data.getFileName();
				if (fileName.toLowerCase().endsWith(".zip")) {
					fileName = fileName.substring(0, fileName.length() - 4);
				}

				String filepath = PropertiesUtil.getTempPath() + File.separator + fileName; // 路径1： 主内容解压路径
				File dir = new File(filepath);
				if(!dir.exists()){
					dir.mkdirs();
				}
				ZipUtil.unZip(bytes, filepath);
//				String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
				File xmlFile = new File(filepath + File.separator + fileName + ".xml");
				Document doc = XmlUtility.getDocument(xmlFile);
				Element technicElement = XmlUtility.getTechnicsElement(doc);
				return technicElement;
			} catch (WTException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
