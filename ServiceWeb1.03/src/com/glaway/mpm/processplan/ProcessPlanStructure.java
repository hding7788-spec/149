package com.glaway.mpm.processplan;

import com.glaway.mpm.change.qchange.helper.ChangeProcessPlanStructure;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.util.*;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMProcessMaterialMaster;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMToolingMaster;
import ext.casc.util.IBAUtility;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import wt.doc.WTDocument;
import wt.fc.collections.WTCollection;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProcessPlanStructure implements RemoteAccess {

	private static VaLogger logger = VaLogger.getLogger(ProcessPlanStructure.class.getName());

	private WTPart wtPart;
	private WTContainer wtContainer;
	private String folderPath = LoadConfig.getInstance().getTechnicsPathPrefix();
	private InputStream inputStream;
	private String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
			+  java.util.UUID.randomUUID().toString()+ File.separator;
	private WTDocument document;
	private String processType;
	private static String onBuildNumber;
	private String technicsType;
	private String folder;
	private String typeName;

//	public ProcessPlanStructure(WTPart wtPart, String processZipDocName, String technicsType,String processType) {
//		GLLogger.debug(this, "--wtPart--" + wtPart + "--processZipDocName--" + processZipDocName + "--processType--"
//				+ processType);
//		try {
//			if (null == wtPart) {
//				throw new WTException("参数wtPart是：" + wtPart);
//			}
//			this.wtPart = wtPart;
//			this.wtContainer = wtPart.getContainer();
//			this.processType = processType;
//			GLLogger.debug(this, "-------wtContainer--" + wtContainer.getName());
//			if (!Constants.normalProcess.equals(processType) && Constants.reworkProcess.equals(processType)
//					&& Constants.tempProcess.equals(processType)) {
//				throw new WTException(processType + " 文件类型不存在,实例化文件类型只有:" + Constants.normalProcess + ","
//						+ Constants.reworkProcess + "," + Constants.tempProcess + " 三种类型");
//			}
//			List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(wtPart, processZipDocName,technicsType, processType);
//			if (0 == list.size()) {
//				throw new WTException("零件对应的工艺文件不存在！");
//			}
//			this.document = list.get(0);
//		} catch (WTException e) {
//			e.printStackTrace();
//		} catch (WTRuntimeException e) {
//			e.printStackTrace();
//		} catch (WTPropertyVetoException e) {
//			e.printStackTrace();
//		}
//	}

	public ProcessPlanStructure(WTPart wtPart, WTDocument document, String processType) {
		GLLogger.debug(this, "--wtPart--" + wtPart + "--document--" + document + "--processType--" + processType);
		try {
			if (null == wtPart) {
				throw new WTException("参数wtPart是：" + wtPart);
			}
			this.wtPart = wtPart;
			this.wtContainer = wtPart.getContainer();
			GLLogger.debug(this, "-------wtContainer--" + wtContainer.getName());
			if (null == document) {
				throw new WTException("零件对应的工艺文件不存在");
			}
			this.document = document;
			this.processType = processType;
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		}
	}

	   public ProcessPlanStructure(WTPart wtPart, String docIda2a2, String processType) {
	        GLLogger.debug(this, "--wtPart--" + wtPart + "--document--" + document + "--processType--" + processType);
	        try {
	            if (null == wtPart) {
	                throw new WTException("参数wtPart是：" + wtPart);
	            }
	            this.wtPart = wtPart;
	            this.wtContainer = wtPart.getContainer();
	            GLLogger.debug(this, "-------wtContainer--" + wtContainer.getName());

	            WTDocument doc = (WTDocument)Util.getObjectByOid(WTDocument.class, docIda2a2);

	            if (null == doc) {
	                throw new WTException("零件对应的工艺文件不存在");
	            }
	            this.document = doc;
	            this.processType = processType;
	        } catch (WTException e) {
	            e.printStackTrace();
	        } catch (WTRuntimeException e) {
	            e.printStackTrace();
	        }
	    }
	   public ProcessPlanStructure(WTDocument wtDoc, String partIda2a2, String processType) {
	        try {
	            WTDocument doc = wtDoc;
	            this.wtPart = (WTPart)Util.getObjectByOid(WTPart.class, partIda2a2);
	            if (null == wtDoc) {
	                throw new WTException("参数wtDoc是：" + wtDoc);
	            }
	            this.document = doc;
	            this.processType = processType;
	            GLLogger.debug(this, "--wtPart--" + wtPart + "--document--" + document + "--processType--" + processType);
	            if (null == wtPart) {
	            	wtPart = WTDocumentUtil.getLatestDescribesWTPartsByDocument(wtDoc);
	            	if(wtPart == null) {
	            		throw new WTException("工艺文件关联的wtPart不存在：" + wtPart);
	            	}
	            }

	            this.wtContainer = wtPart.getContainer();
	            GLLogger.debug(this, "-------wtContainer--" + wtContainer.getName());
	            if (null == doc) {
	                throw new WTException("零件对应的工艺文件不存在");
	            }

	        } catch (WTException e) {
	            e.printStackTrace();
	        } catch (WTRuntimeException e) {
	            e.printStackTrace();
	        }
	    }

	/**
	 * 结构化工艺入口
	 *
	 * @author qianlong
	 * @date 2013-7-12
	 * @param processZipDocName
	 *            工艺压缩包名称
	 * @param processType
	 *            normal|rework|temp
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public MPMProcessPlan structureProcessPlan() throws Exception {
		FileOutputStream fileOutputStream = null;
		Transaction trans = new Transaction();
		WTUser creator = null;
		if(this.document!=null){
			creator =(WTUser) this.document.getModifier().getObject();

			String docVersion = this.document.getVersionIdentifier().getValue();
			if(!docVersion.startsWith("space")){
				MPMProcessPlan plan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(this.document.getNumber());
				if(plan!=null){
					return new ChangeProcessPlanStructure(document).structureSanJiProcessPlan();
				}
			}
		}

		WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		String deleteFile1 = null;

		try {
			if(creator!=null){
				SessionHelper.manager.setPrincipal(creator.getAuthenticationName());
			}

			// 解压工艺压缩包，获取工艺xml文件的流
			String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
			String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
			ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
			File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
			inputStream = new FileInputStream(xmlFile);

			deleteFile1 = tempFilePath;
			String version = document.getIterationDisplayIdentifier().toString();

			// 读取xml 创建结构

			trans.start();
			GLLogger.debug(this, "-------start structureProcessPlan----------");
			SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
			Element rootElement = xmlUtil.getRootElement();
			GLLogger.debug(this, "--rootElement--" + rootElement.getName());
			MPMProcessPlan processPlan = null;
			if ("technics".equals(rootElement.getName())) {
				for (Element rootAttrElement : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {

					/*try {
						NCMaterialHelper.structure(this.document, rootAttrElement);
					}catch (Exception e){
						e.printStackTrace();
					}*/

					GLLogger.debug(this, "--rootAttrElement--" + rootAttrElement.getName());
					technicsType = rootAttrElement.getAttributeValue("technicsType");
					String [][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
					for(int i=0;i<technicsTypes[1].length;i++){
						if(technicsTypes[1][i].equals(technicsType)){
							folder = folderPath+technicsTypes[1][i];
							typeName = "com.ptc.windchill.mpml.processplan.MPMProcessPlan|"+LoadConfig.getInstance().getLocalDomainName()+"."+technicsTypes[2][i];
							break;
						}
					}

					// 创建工艺
					processPlan = createProcessPlan(rootAttrElement, version);
					// 创建工时定额模型对象
//					WorkHourUtil.createProcessPlanFillTime(wtPart, processPlan.getName(), processType, processPlan
//							.toString(), document.toString());
					// 回写工艺的oid
					rootAttrElement.setAttribute("oid", Util.getStringOid(processPlan));
					// 创建工艺和零件的关联
					wtPart = WTPartUtil.getLatestPartByNumberAndView(wtPart, "Manufacturing");
					MPMProcessPlanUtil.createMPMPartToProcessPlanLink(wtPart, processPlan);
					// 创建工艺与压缩包的关联
					MPMProcessPlanUtil.createMPMDocumentDescribeLink(processPlan, document);

					// 处理工艺的子节点
					for (Element rootChildElement : (List<Element>) rootAttrElement.getChildren()) {
						GLLogger.debug(this, "--rootChildElement--" + rootChildElement.getName());
						if ("steps".equals(rootChildElement.getName())) {
							structureSteps(processPlan, rootChildElement);
						}
					}

					//上传工艺文件的工艺附表、工艺说明文件
					GLLogger.debug(this, "-------starting uploadAttach----------");
					uploadAttach(processPlan, rootAttrElement, tempFilePath + subFileName);
					GLLogger.debug(this, "-------end uploadAttach----------");

				}
			}

			GLLogger.debug(this, "-------end structureProcessPlan----------");
			trans.commit();

			trans = null;

			String xmlFilePath = tempFilePath + subFileName + File.separator + subFileName
					+ ".xml";
			// 替换工艺xml，打包工艺文件夹，上传工艺压缩包
			fileOutputStream = new FileOutputStream(new File(xmlFilePath), false);
			Format format = Format.getPrettyFormat();
			format.setEncoding("GBK");
			XMLOutputter xmlOutput = new XMLOutputter(format);
			xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);


			boolean compressflag = ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);
			if(compressflag){
				//WTDocumentUtil.setPrimaryForDocument(document, zipFileName, new FileInputStream(new File(tempFilePath+ zipFileName)));
			}


			return processPlan;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (trans != null) {
				trans.rollback();
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(deleteFile1!=null){
				FileUtil.deleteFile(new File(deleteFile1));
			}
			SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());

		}
	}

	@SuppressWarnings("unchecked")
	public MPMProcessPlan structureProcessPlan2() throws Exception {
		FileOutputStream fileOutputStream = null;
		Transaction trans = new Transaction();
		WTUser creator = null;
		if(this.document!=null){
			creator =(WTUser) this.document.getModifier().getObject();

			String docVersion = this.document.getVersionIdentifier().getValue();
			if(!docVersion.startsWith("space")){

				WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(this.document);
				Iterator it = coll.iterator();
				if (it.hasNext()) {
					MPMProcessPlan plan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(this.document.getNumber());
					if(plan!=null){
						 new ChangeProcessPlanStructure(document).structureProcessPlan();
						 return null;
					}
				}else{
					 new ChangeProcessPlanStructure(document).structureSanJiProcessPlan();
					 return null;
				}

			}
		}

		WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		String deleteFile1 = null;
		try {
			if(creator!=null){
				SessionHelper.manager.setPrincipal(creator.getAuthenticationName());
			}

			// 解压工艺压缩包，获取工艺xml文件的流
			String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
			String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
			ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
			File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
			inputStream = new FileInputStream(xmlFile);

			deleteFile1 = tempFilePath;
			String version = document.getIterationDisplayIdentifier().toString();

			// 读取xml 创建结构

			trans.start();
			GLLogger.debug(this, "-------start structureProcessPlan----------");
			SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
			Element rootElement = xmlUtil.getRootElement();
			GLLogger.debug(this, "--rootElement--" + rootElement.getName());
			MPMProcessPlan processPlan = null;
			if ("technics".equals(rootElement.getName())) {
				for (Element rootAttrElement : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
					GLLogger.debug(this, "--rootAttrElement--" + rootAttrElement.getName());

					/*try {
						NCMaterialHelper.structure(this.document, rootAttrElement);
					}catch (Exception e){
						e.printStackTrace();
					}*/

					technicsType = rootAttrElement.getAttributeValue("technicsType");
					String [][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
					for(int i=0;i<technicsTypes[1].length;i++){
						if(technicsTypes[1][i].equals(technicsType)){
							folder = folderPath+technicsTypes[1][i];
							typeName = "com.ptc.windchill.mpml.processplan.MPMProcessPlan|"+LoadConfig.getInstance().getLocalDomainName()+"."+technicsTypes[2][i];
							break;
						}
					}

					// 创建工艺
					processPlan = createProcessPlan(rootAttrElement, version);
					// 创建工时定额模型对象
//					WorkHourUtil.createProcessPlanFillTime(wtPart, processPlan.getName(), processType, processPlan
//							.toString(), document.toString());
					// 回写工艺的oid
					rootAttrElement.setAttribute("oid", Util.getStringOid(processPlan));
					// 创建工艺和零件的关联
					wtPart = WTPartUtil.getLatestPartByNumberAndView(wtPart, "Manufacturing");
					MPMProcessPlanUtil.createMPMPartToProcessPlanLink(wtPart, processPlan);
					// 创建工艺与压缩包的关联
					MPMProcessPlanUtil.createMPMDocumentDescribeLink(processPlan, document);

					// 处理工艺的子节点
					for (Element rootChildElement : (List<Element>) rootAttrElement.getChildren()) {
						GLLogger.debug(this, "--rootChildElement--" + rootChildElement.getName());
						if ("steps".equals(rootChildElement.getName())) {
							structureSteps(processPlan, rootChildElement);
						}
					}

					//上传工艺文件的工艺附表、工艺说明文件
					GLLogger.debug(this, "-------starting uploadAttach----------");
					uploadAttach(processPlan, rootAttrElement, tempFilePath + subFileName);
					GLLogger.debug(this, "-------end uploadAttach----------");
				}
			}

			GLLogger.debug(this, "-------end structureProcessPlan----------");
			trans.commit();
			trans = null;

			String xmlFilePath = tempFilePath + subFileName + File.separator + subFileName
					+ ".xml";
			// 替换工艺xml，打包工艺文件夹，上传工艺压缩包
			fileOutputStream = new FileOutputStream(new File(xmlFilePath), false);
			Format format = Format.getPrettyFormat();
			format.setEncoding("GBK");
			XMLOutputter xmlOutput = new XMLOutputter(format);
			xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);


			boolean compressflag = ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);
			if(compressflag){
				//WTDocumentUtil.setPrimaryForDocument(document, zipFileName, new FileInputStream(new File(tempFilePath+ zipFileName)));
			}


			return processPlan;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (trans != null) {
				trans.rollback();
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(deleteFile1!=null){
				FileUtil.deleteFile(new File(deleteFile1));
			}
			SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());

		}
	}

	@SuppressWarnings("unchecked")
	public MPMProcessPlan structureReportTechnicsProcessPlan() throws Exception {
		FileOutputStream fileOutputStream = null;
		Transaction trans = new Transaction();
		WTUser creator = null;
		if(this.document!=null){
			creator =(WTUser) this.document.getModifier().getObject();
		}
		WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		try {
			if(creator!=null){
				SessionHelper.manager.setPrincipal(creator.getAuthenticationName());
			}

			// 解压工艺压缩包，获取工艺xml文件的流
			String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
			String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
			ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
			File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
			inputStream = new FileInputStream(xmlFile);

			String version = document.getIterationDisplayIdentifier().toString();

			// 读取xml 创建结构

			trans.start();
			GLLogger.debug(this, "-------start structureProcessPlan----------");
			SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
			Element rootElement = xmlUtil.getRootElement();
			GLLogger.debug(this, "--rootElement--" + rootElement.getName());
			MPMProcessPlan processPlan = null;
			if ("technics".equals(rootElement.getName())) {
				for (Element rootAttrElement : (List<Element>) rootElement.getChildren("XWReportTechnicsInfo")) {
					GLLogger.debug(this, "--rootAttrElement--" + rootAttrElement.getName());
					technicsType = rootAttrElement.getAttributeValue("technicsType");
					typeName ="com.ptc.windchill.mpml.processplan.MPMProcessPlan|casc.sast.149.Process_reportTechnics";
					folder = folderPath+"报表类工艺文件";
					// 创建工艺
					processPlan = createProcessPlan(rootAttrElement, version);
					// 创建工时定额模型对象
//					WorkHourUtil.createProcessPlanFillTime(wtPart, processPlan.getName(), processType, processPlan
//							.toString(), document.toString());
					// 回写工艺的oid
					rootAttrElement.setAttribute("oid", Util.getStringOid(processPlan));
					// 创建工艺和零件的关联
					wtPart = WTPartUtil.getLatestPartByNumberAndView(wtPart, "Manufacturing");
					MPMProcessPlanUtil.createMPMPartToProcessPlanLink(wtPart, processPlan);
					// 创建工艺与压缩包的关联
					MPMProcessPlanUtil.createMPMDocumentDescribeLink(processPlan, document);


					//上传工艺文件的工艺附表、工艺说明文件
					GLLogger.debug(this, "-------starting uploadAttach----------");
					uploadAttach(processPlan, rootAttrElement, tempFilePath + subFileName);
					GLLogger.debug(this, "-------end uploadAttach----------");
				}
			}

			GLLogger.debug(this, "-------end structureProcessPlan----------");
			trans.commit();
			trans = null;

			// 替换工艺xml，打包工艺文件夹，上传工艺压缩包
			fileOutputStream = new FileOutputStream(new File(tempFilePath + subFileName + File.separator + subFileName
					+ ".xml"), false);
			Format format = Format.getPrettyFormat();
			format.setEncoding("GBK");
			XMLOutputter xmlOutput = new XMLOutputter(format);
			xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);

			boolean flag = ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);

			// 重新上传PBOM的XML
			if(flag){
				//WTDocumentUtil.setPrimaryForDocument(document, zipFileName, new FileInputStream(new File(tempFilePath+ zipFileName)));
			}
			return processPlan;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (trans != null) {
				trans.rollback();
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());

		}
	}

	/**
	 * 结构化工序
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param processPlan
	 * @param stepElement
	 * @throws Exception
	 *
	 */
	@SuppressWarnings("unchecked")
	private void structureSteps(MPMProcessPlan processPlan, Element stepElement) throws Exception {

		for (Element stepAttrElement : (List<Element>) stepElement.getChildren("QMProcedureInfo")) {
			GLLogger.debug(this, "--stepAttrElement--" + stepAttrElement.getName());
			// 创建工序
			MPMOperation operation = createOperation(stepAttrElement);
			// 回写工序的oid
			stepAttrElement.setAttribute("oid", Util.getStringOid(operation));
			// 创建工序与工艺的关联 ，工序号需要统一的三位数
			String stepNumber = stepAttrElement.getAttributeValue("stepNumber");
			int numberLength = 3 - stepNumber.length();
			for (int i = 0; i < numberLength; i++) {
				stepNumber = 0 + stepNumber;
			}
			MPMProcessPlanUtil.createMPMOperationUsageLink(processPlan, (MPMOperationMaster) operation.getMaster(),
					stepNumber);

			// 处理工序的子节点
			for (Element stepChildElement : (List<Element>) stepAttrElement.getChildren()) {
				GLLogger.debug(this, "--ProcedureChildElement--" + stepChildElement.getName());
				if ("paces".equals(stepChildElement.getName())) {
					//工步
					structureSubSteps(operation, stepChildElement);
				} else if ("materials".equals(stepChildElement.getName())) {
					//工艺辅料
					structureMaterials(operation, stepChildElement);
				} else if ("equips".equals(stepChildElement.getName())) {
					//设备
					structureEquipments(operation, stepChildElement);
				} else if ("tools".equals(stepChildElement.getName())) {
					//工装
					structureTools(operation, stepChildElement);
				} else if ("parts".equals(stepChildElement.getName())) {
					//参装件
					structureParts(operation, stepChildElement);
				} else if ("images".equals(stepChildElement.getName())) {
					//简图
					structureImages(operation, stepChildElement);
				} else if ("sdashboard".equals(stepChildElement.getName())) {
					//标准仪器仪表
					structureTools2(operation, stepChildElement,"QMSDashboardInfo");
				} else if ("unsdashboard".equals(stepChildElement.getName())) {
					//非标准仪器仪表
					structureTools2(operation, stepChildElement,"QMUnSDashboardInfo");
				} else if ("knifeTools".equals(stepChildElement.getName())) {
					//刀具
					structureTools2(operation, stepChildElement,"QMKnifeToolInfo");
				} else if ("measures".equals(stepChildElement.getName())) {
					//量具
					structureTools2(operation, stepChildElement,"QMMeasureInfo");
				}
			}
		}

	}

	/**
	 * 结构化工步
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param operation
	 * @param subStepElement
	 * @throws Exception
	 *
	 */
	@SuppressWarnings("unchecked")
	private void structureSubSteps(MPMOperation operation, Element subStepElement) throws Exception {

		for (Element subStepAttrElement : (List<Element>) subStepElement.getChildren("QMProcedureInfo")) {
			GLLogger.debug(this, "--subStepAttrElement--" + subStepAttrElement.getName());
			// 创建工步
			MPMOperation subOperation = createSubOperation(subStepAttrElement);
			// 回写工步的oid
			subStepAttrElement.setAttribute("oid", Util.getStringOid(subOperation));
			// 工步关联到工序
			try{
				MPMProcessPlanUtil.createMPMOperationUsageLink(operation, (MPMOperationMaster) subOperation.getMaster(),
					subStepAttrElement.getAttributeValue("stepNumber"));
			}catch(WTException e){
				e.printStackTrace();
			}

			// 处理工步的子节点
			for (Element subStepChildElement : (List<Element>) subStepAttrElement.getChildren()) {
				GLLogger.debug(this, "--subStepChildElement--" + subStepChildElement.getName());
				if ("materials".equals(subStepChildElement.getName())) {
					//工艺辅料
					structureMaterials(subOperation, subStepChildElement);
				} else if ("equips".equals(subStepChildElement.getName())) {
					//设备
					structureEquipments(subOperation, subStepChildElement);
				} else if ("tools".equals(subStepChildElement.getName())) {
					//工装
					structureTools(subOperation, subStepChildElement);
				} else if ("parts".equals(subStepChildElement.getName())) {
					//参装件
					structureParts(subOperation, subStepChildElement);
				} else if ("images".equals(subStepChildElement.getName())) {
					//简图
					structureImages(subOperation, subStepChildElement);
				} else if ("sdashboard".equals(subStepChildElement.getName())) {
					//标准仪器仪表
					structureTools2(subOperation, subStepChildElement,"QMSDashboardInfo");
				} else if ("unsdashboard".equals(subStepChildElement.getName())) {
					//非标准仪器仪表
					structureTools2(subOperation, subStepChildElement,"QMUnSDashboardInfo");
				} else if ("knifeTools".equals(subStepChildElement.getName())) {
					//刀具
					structureTools2(subOperation, subStepChildElement,"QMKnifeToolInfo");
				} else if ("measures".equals(subStepChildElement.getName())) {
					//量具
					structureTools2(subOperation, subStepChildElement,"QMMeasureInfo");
				}
			}
		}
	}

	/**
	 * 关联工艺辅料
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param object
	 * @param materialElement
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws ParseException
	 * @throws RemoteException
	 *
	 */
	@SuppressWarnings("unchecked")
	private void structureMaterials(Object object, Element materialElement) throws WTException,
			WTPropertyVetoException, RemoteException, ParseException {

		for (Element materialAttrElement : (List<Element>) materialElement.getChildren("QMMaterialInfo")) {
			GLLogger.debug(this, "--materialAttrElement--" + materialAttrElement.getName());
			// 获取工艺辅料
			String oid = materialAttrElement.getAttributeValue("oid");
			MPMProcessMaterial processMaterial = (MPMProcessMaterial) Util
					.getObjectByOid(MPMProcessMaterial.class, oid);
			if (null == processMaterial) {
				WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
				if (null == part) {
					continue;
					//throw new WTException("编号为：" + oid + "的材料不存在！");
				}
				MPMProcessPlanUtil.createOperationToOperatedPartLink((MPMOperation) object, (WTPartMaster) part
						.getMaster());
			} else {
				// 工艺辅料关联到工步或者工序
				MPMOperationToConsumableLink link = MPMProcessPlanUtil.createMPMOperationToConsumableLink(
						(MPMOperation) object, (MPMProcessMaterialMaster) processMaterial.getMaster());
//				setMaterialLinkIBA(link, materialAttrElement);
			}
		}

	}

	/**
	 * 关联设备
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param object
	 * @param equipmentElement
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	@SuppressWarnings("unchecked")
	private void structureEquipments(Object object, Element equipmentElement) throws WTException,
			WTPropertyVetoException {
		for (Element equipmentAttrElement : (List<Element>) equipmentElement.getChildren("QMEquipmentInfo")) {
			GLLogger.debug(this, "--equipmentAttrElement--" + equipmentAttrElement.getName());
			// 获取装备
			String number = equipmentAttrElement.getAttributeValue("number");
			MPMToolingMaster toolingMaster = MPMResourceUtil.getMPMToolingMasterByNumber(number);
			if (null == toolingMaster) {
				continue;
				//throw new WTException("编号为：" + number + "的设备不存在！");
			}
			// 装备关联到工序或者工步
			MPMProcessPlanUtil.createMPMOperationToConsumableLink((MPMOperation) object, toolingMaster);
		}
	}

	/**
	 * 关联工装、工量具、刀具
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param object
	 * @param toolElement
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	@SuppressWarnings("unchecked")
	private void structureTools(Object object, Element toolElement) throws WTException, WTPropertyVetoException {

		for (Element toolAttrElement : (List<Element>) toolElement.getChildren("QMToolInfo")) {
			GLLogger.debug(this, "--toolAttrElement--" + toolAttrElement.getName());
			// 获取工装
			MPMTooling tooling = (MPMTooling) Util.getObjectByOid(MPMTooling.class, toolAttrElement
					.getAttributeValue("oid"));
			if (null == tooling) {
				continue;
				//throw new WTException("编号为：" + toolAttrElement.getAttributeValue("toolNum") + "的工装、工量具或者刀具不存在！");
			}
			// 工装关联到工序或者工步
			MPMProcessPlanUtil.createMPMOperationToConsumableLink((MPMOperation) object, (MPMToolingMaster) tooling
					.getMaster());

		}
	}

	private void structureTools2(Object object, Element toolElement, String name) throws WTException, WTPropertyVetoException {

		for (Element toolAttrElement : (List<Element>) toolElement.getChildren(name)) {
			GLLogger.debug(this, "--toolAttrElement--" + toolAttrElement.getName());
			// 获取工装
			MPMTooling tooling = (MPMTooling) Util.getObjectByOid(MPMTooling.class, toolAttrElement
					.getAttributeValue("oid"));
			if (null == tooling) {
				continue;
				//throw new WTException("编号为：" + toolAttrElement.getAttributeValue("toolNum") + "的工装、工量具或者刀具不存在！");
			}
			// 工装关联到工序或者工步
			MPMProcessPlanUtil.createMPMOperationToConsumableLink((MPMOperation) object, (MPMToolingMaster) tooling
					.getMaster());

		}
	}

	/**
	 * 关联零件
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param object
	 * @param partElement
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	@SuppressWarnings("unchecked")
	private void structureParts(Object object, Element partElement) throws WTException, WTPropertyVetoException {
		for (Element partAttrElement : (List<Element>) partElement.getChildren("QMPartInfo")) {
			GLLogger.debug(this, "--partAttrElement--" + partAttrElement.getName());
			// 获取零件
			String partNumber = partAttrElement.getAttributeValue("partNumber");
			WTPartMaster partMaster = WTPartUtil.getWTPartMasterByNumber(partNumber);
			if (null == partMaster) {
				continue;
				//throw new WTException("编号为：" + partNumber + "的零件不存在！");
			}
			// 零件关联到工序或工步
			if(partMaster!=null){
				MPMProcessPlanUtil.createOperationToOperatedPartLink((MPMOperation) object, partMaster);
			}
		}
	}

	/**
	 * 关联图片
	 *
	 * @author qianlong
	 * @date 2012-11-28
	 * @param object
	 * @param imageElement
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("unchecked")
	private void structureImages(Object object, Element imageElement) throws WTException {

		for (Element imageAttrElement : (List<Element>) imageElement.getChildren("DrawingInfo")) {
			GLLogger.debug(this, "--imageAttrElement--" + imageAttrElement.getName());

		}
	}

	/**
	 * 创建工艺计划
	 *
	 * @author qianlong
	 * @throws Exception
	 * @date 2013-5-24
	 *
	 */
	private MPMProcessPlan createProcessPlan(Element rootAttrElement, String version) throws Exception {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		MPMProcessPlan processPlan = null;
        try{
			String name = rootAttrElement.getAttributeValue("technicsName");
			String technicsNumber = rootAttrElement.getAttributeValue("technicsNumber");
			String pplanNumber = rootAttrElement.getAttributeValue("pplanNumber");
			// 如果是 工装零件 文件夹结构不一样
	//		String partTypeName = TypedUtility.getTypeIdentifier(wtPart).getTypename();
	//		if (!partTypeName.contains(TypeNameConstants.gzRootPartTypeName)
	//				&& !partTypeName.contains(TypeNameConstants.gzToolingPartTypeName)) {
	//			if ("零件工艺".equals(technicsType)) {
	//				folderPath = "/Default/06：工艺文件/0602：零件工艺";
	//			} else if ("装配工艺".equals(technicsType)) {
	//				folderPath = "/Default/06：工艺文件/0601：装配工艺";
	//			}
	//		} else {
	//			folderPath = ((SubFolder) wtPart.getParentFolder().getObject()).getLocation() + "/"
	//					+ Constants.gzFolderName[5];
	//		}
	//		if ("零件工艺".equals(technicsType)) {
	//			processPlan = MPMProcessPlanUtil.createMPMProcessPlan(null, name, wtContainer,
	//					TypeNameConstants.PART_PROCESSPLAN_TYPE_NAME, folder, version);
	//		} else if ("装配工艺".equals(technicsType)) {
	//			processPlan = MPMProcessPlanUtil.createMPMProcessPlan(null, name, wtContainer,
	//					TypeNameConstants.ASSEMBLE_PROCESSPLAN_TYPE_NAME, folder, version);
	//		}
			processPlan = MPMProcessPlanUtil.createMPMProcessPlan(technicsNumber, name, wtContainer,typeName, folder, version);
			IBAHolder holder = (IBAHolder)processPlan;
			IBAUtility iba = new IBAUtility();
			try {
				iba.setIBAValue( "PPNUMBER", pplanNumber);
				holder =iba.updateAttributeContainer(holder);
				iba.updateIBAHolder(holder);
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null != processPlan) {
				processPlan = (MPMProcessPlan) Util.setLifecycle(processPlan, Constants.RELEASED);
				//setMPMProcessPlanIBA(rootAttrElement, processPlan);
			}
        }finally{
    		SessionServerHelper.manager.setAccessEnforced(flag);
        }
		return processPlan;
	}

	/**
	 * 设置工艺属性
	 *
	 * @author qianlong
	 * @date 2013-5-28
	 * @param processPlan
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	private static void setMPMProcessPlanIBA(Element rootAttrElement, MPMProcessPlan processPlan) throws WTException,
			WTPropertyVetoException, RemoteException {
		Map<String, String> ibaMap = new HashMap<String, String>();
		String productNumber = rootAttrElement.getAttributeValue(AttributeConstants.productNumber);
		ibaMap.put(AttributeConstants.productNumber, Util.formateString(productNumber));
		String productName = rootAttrElement.getAttributeValue(AttributeConstants.productName);
		ibaMap.put(AttributeConstants.productName, Util.formateString(productName));
		String parentPartNumber = rootAttrElement.getAttributeValue(AttributeConstants.parentPartNumber);
		ibaMap.put(AttributeConstants.parentPartNumber, Util.formateString(parentPartNumber));
		String partName = rootAttrElement.getAttributeValue(AttributeConstants.partName);
		ibaMap.put(AttributeConstants.partName, Util.formateString(partName));
		String partNumber = rootAttrElement.getAttributeValue(AttributeConstants.partNumber);
		ibaMap.put(AttributeConstants.partNumber, Util.formateString(partNumber));
		String backupReason = rootAttrElement.getAttributeValue(AttributeConstants.backupReason);
		ibaMap.put(AttributeConstants.backupReason, Util.formateString(backupReason));
		String maxBackupCount = rootAttrElement.getAttributeValue(AttributeConstants.maxBackupCount);
		ibaMap.put(AttributeConstants.maxBackupCount, Util.formateInteger(maxBackupCount));
		String backupRate = rootAttrElement.getAttributeValue(AttributeConstants.backupRate);
		ibaMap.put(AttributeConstants.backupRate, Util.formateInteger(backupRate));
		String materialType = rootAttrElement.getAttributeValue(AttributeConstants.materialType);
		ibaMap.put(AttributeConstants.materialType, Util.formateString(materialType));
		String isSpecial = rootAttrElement.getAttributeValue(AttributeConstants.isSpecial);
		ibaMap.put(AttributeConstants.isSpecial, Util.formateBoolean(isSpecial));
		String isKey = rootAttrElement.getAttributeValue(AttributeConstants.isKey);
		ibaMap.put(AttributeConstants.isKey, Util.formateBoolean(isKey));
		String isRework = rootAttrElement.getAttributeValue(AttributeConstants.isRework);
		ibaMap.put(AttributeConstants.isRework, Util.formateBoolean(isRework));
		String workShop = rootAttrElement.getAttributeValue(AttributeConstants.workShop);
		ibaMap.put(AttributeConstants.workShop, Util.formateString(workShop));
		String remark = rootAttrElement.getAttributeValue(AttributeConstants.remark);
		ibaMap.put(AttributeConstants.remark, Util.formateString(remark));
		//TODO 812工艺属性
		IBAHelper ibaHelper = new IBAHelper(processPlan);
		ibaHelper.setIBAValue(processPlan, ibaMap);
	}

	/**
	 * 创建工序
	 *
	 * @author qianlong
	 * @date 2013-5-24
	 * @param rootAttrElement
	 * @throws Exception
	 *
	 */
	private MPMOperation createOperation(Element stepAttrElement) throws Exception {
		String name = stepAttrElement.getAttributeValue("stepName");
		String stepNumber = stepAttrElement.getAttributeValue("stepNumber");
		String bsoID = stepAttrElement.getAttributeValue("bsoID");
		if(name==null||"".equals(name)){
			name = stepNumber;
		}
		MPMOperation operation = MPMProcessPlanUtil.createOperation(null, name, wtContainer,
				TypeNameConstants.OPERATION_TYPE_NAME, folder + "/工序");
		if (operation != null) {
			operation = (MPMOperation) Util.setLifecycle(operation, Constants.RELEASED);
			String workShop = stepAttrElement.getAttributeValue(AttributeConstants.workShop);
			IBAHelper.setIBAStringValue(operation,AttributeConstants.workShop, Util.formateString(workShop));
			IBAHelper.setIBAStringValue(operation,"BIAOSHI", bsoID);
			//setOperationIBA(operation, stepAttrElement);
		}

		return operation;
	}

	/**
	 * 设置工步工序属性
	 *
	 * @author qianlong
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 * @date 2013-7-6
	 */
	private static void setOperationIBA(MPMOperation operation, Element stepAttrElement) throws WTException,
			WTPropertyVetoException, RemoteException {
		Map<String, String> ibaMap = new HashMap<String, String>();
		String isKey = stepAttrElement.getAttributeValue(AttributeConstants.isKey);
		ibaMap.put(AttributeConstants.isKey, Util.formateBoolean(isKey));

		Element procedureContentElement = stepAttrElement.getChild("procedureContent");
		if (null != procedureContentElement) {
			String description = procedureContentElement.getText();
			operation.setDescription(description);
		}

		String workShop = stepAttrElement.getAttributeValue(AttributeConstants.workShop);
		ibaMap.put(AttributeConstants.workShop, Util.formateString(workShop));

		String workType = stepAttrElement.getAttributeValue(AttributeConstants.workType);
		ibaMap.put(AttributeConstants.workType, Util.formateString(workType));

		String workSpace = stepAttrElement.getAttributeValue(AttributeConstants.workSpace);
		ibaMap.put(AttributeConstants.workSpace, Util.formateString(workSpace));

		String PrepareWorkHours = stepAttrElement.getAttributeValue(AttributeConstants.PrepareWorkHours);
		ibaMap.put(AttributeConstants.PrepareWorkHours, Util.formateString(PrepareWorkHours));

		String TaktTime = stepAttrElement.getAttributeValue(AttributeConstants.TaktTime);
		ibaMap.put(AttributeConstants.TaktTime, Util.formateString(TaktTime));

		String NumberOfGroup = stepAttrElement.getAttributeValue(AttributeConstants.NumberOfGroup);
		ibaMap.put(AttributeConstants.NumberOfGroup, Util.formateString(NumberOfGroup));
		//TODO 812设置工序属性
		IBAHelper ibaHelper = new IBAHelper(operation);
		ibaHelper.setIBAValue(operation, ibaMap);
	}

	/**
	 * 创建工步
	 *
	 * @author qianlong
	 * @date 2013-5-24
	 * @param rootAttrElement
	 * @throws Exception
	 *
	 */
	private MPMOperation createSubOperation(Element subStepAttrElement) throws Exception {
		String name = subStepAttrElement.getAttributeValue("stepNumber");
		MPMOperation subOperation = MPMProcessPlanUtil.createSubOperation(null, name, wtContainer,
				TypeNameConstants.SUBOPERATION_TYPE_NAME, folder + "/工步");
		if (subOperation != null) {
			subOperation = (MPMOperation) Util.setLifecycle(subOperation, Constants.RELEASED);
//			setOperationIBA(subOperation, subStepAttrElement);
		}
		return subOperation;
	}

	/**
	 * 设置工步工序属性
	 *
	 * @author qianlong
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 * @date 2013-7-6
	 */
	private static void setMaterialLinkIBA(MPMOperationToConsumableLink link, Element materialAttrElement)
			throws WTException, WTPropertyVetoException, RemoteException {
		Map<String, String> ibaMap = new HashMap<String, String>();
		String materialQuota = materialAttrElement.getAttributeValue(AttributeConstants.materialQuota);
		ibaMap.put(AttributeConstants.materialQuota, Util.formateString(materialQuota));
		String useSize = materialAttrElement.getAttributeValue(AttributeConstants.useSize);
		ibaMap.put(AttributeConstants.useSize, Util.formateString(useSize));
		String singleSize = materialAttrElement.getAttributeValue(AttributeConstants.singleSize);
		ibaMap.put(AttributeConstants.singleSize, Util.formateString(singleSize));
		String partUnit = materialAttrElement.getAttributeValue(AttributeConstants.partUnit);
		ibaMap.put(AttributeConstants.partUnit, Util.formateInteger(partUnit));

		IBAHelper ibaHelper = new IBAHelper(link);
		ibaHelper.setIBAValue(link, ibaMap);
	}

	public static void uploadAttach(MPMProcessPlan pplan,Element rootAttrElement,String tempFilePath) throws WTException {
		//上传工艺说明
		String gydesFilePath = tempFilePath + File.separator + ".工艺说明.doc";
		System.out.println("------------gydesFilePath:"+gydesFilePath);
		MPMProcessPlanUtil.uploadAttach(pplan, gydesFilePath,"工艺说明.doc");

		for (Element rootChildElement : (List<Element>) rootAttrElement.getChildren()) {
			GLLogger.debug(ProcessPlanStructure.class.getName(), "--uploadAttach--" + rootChildElement.getName());
			if ("additiontables".equals(rootChildElement.getName())) {
				for (Element element : (List<Element>) rootChildElement.getChildren("additionaltable")) {
					String filePath = tempFilePath + File.separator + element.getAttributeValue("absolutePath");
					String fileName = element.getAttributeValue("attachName")+".doc";
					System.out.println("------------filePath:"+filePath);
					MPMProcessPlanUtil.uploadAttach(pplan, filePath,fileName);
				}
			}
		}
	}

	public static void test() {
		try {
			WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, "42578311");
			ProcessPlanStructure planStructure = new ProcessPlanStructure(document,"42304756", "normal");
			try {
				planStructure.structureProcessPlan();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");

		try {
			methodServer.invoke("test", ProcessPlanStructure.class.getName(), null, null, null);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

}
