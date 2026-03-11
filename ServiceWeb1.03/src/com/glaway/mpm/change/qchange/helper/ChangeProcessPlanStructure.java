package com.glaway.mpm.change.qchange.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.constants.AttributeConstants;
import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.constants.TypeNameConstants;
import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.processplan.ProcessPlanStructure;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.*;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMProcessMaterialMaster;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMToolingMaster;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import ext.casc.workflow.tree.mvc.builder.SetListGongShiDingEBuilder;
import ext.ptc.ViewWIHelper;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeRecord2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
import wt.folder.FolderHelper;
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
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.*;

public class ChangeProcessPlanStructure implements RemoteAccess {

	private WTPart wtPart;
	private WTContainer wtContainer;
	private String folderPath;
	private InputStream inputStream;
	private String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
			+ UUID.randomUUID().toString() + File.separator;
	private WTDocument document;
	private WTObject pbo;
	private String folder;
	private String typeName;
	public ChangeProcessPlanStructure(String partIda2a2, String docOid, String processType,WTObject pbo) {
		this.pbo = pbo;
		try {
			String partOid = "wt.part.WTPart:"+partIda2a2;
			WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(partOid);

			if(part != null) {
				GLLogger.debug(this, "-------part--" + part);
				this.wtContainer = wtPart.getContainer();
				GLLogger.debug(this, "-------wtContainer--" + wtContainer.getName());
//				if (!Constants.normalProcess.equals(processType) && Constants.reworkProcess.equals(processType)
//						&& Constants.tempProcess.equals(processType)) {
//					throw new WTException(processType + " 文件类型不存在,实例化文件类型只有:" + Constants.normalProcess + ","
//							+ Constants.reworkProcess + "," + Constants.tempProcess + " 三种类型");
//				}
//				List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(wtPart, null, Constants.normalProcess);
				if(!docOid.contains("WTDocument")) {
					docOid = "wt.doc.WTDocument:"+docOid;
				}
				WTDocument doc = (WTDocument) ReferenceFactory.getObjectbyOid(docOid);
				if (doc == null) {
					throw new WTException("零件对应的工艺文件不存在");
				}
				doc = WTDocumentUtil.getLatestDocumentByNumber(doc.getNumber());
				this.document = doc;
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public ChangeProcessPlanStructure(String partIda2a2, String docOid, String processType) {
		GLLogger.debug(this, "--partIda2a2--" + partIda2a2 + "--docOid--" + docOid + "--processType--"
				+ processType);
		try {
			String partOid = "wt.part.WTPart:"+partIda2a2;
			WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(partOid);
			GLLogger.debug(this, "-------part--" + part);
			if(part != null) {
				this.wtPart = part;
				this.wtContainer = wtPart.getContainer();
				GLLogger.debug(this, "-------wtContainer--" + wtContainer.getName());
//				if (!Constants.normalProcess.equals(processType) && Constants.reworkProcess.equals(processType)
//						&& Constants.tempProcess.equals(processType)) {
//					throw new WTException(processType + " 文件类型不存在,实例化文件类型只有:" + Constants.normalProcess + ","
//							+ Constants.reworkProcess + "," + Constants.tempProcess + " 三种类型");
//				}
//				List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(wtPart, null, Constants.normalProcess);
				if(!docOid.contains("WTDocument")) {
					docOid = "wt.doc.WTDocument:"+docOid;
				}
				WTDocument doc = (WTDocument) ReferenceFactory.getObjectbyOid(docOid);
				if (doc == null) {
					throw new WTException("零件对应的工艺文件不存在");
				}
				doc = WTDocumentUtil.getLatestDocumentByNumber(doc.getNumber());
				this.document = doc;
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public ChangeProcessPlanStructure(WTPart wtPart, String processZipDocName, String processType) {
		GLLogger.debug(this, "--wtPart--" + wtPart + "--processZipDocName--" + processZipDocName + "--processType--"
				+ processType);
		try {
			if (null == wtPart) {
				throw new WTException("参数wtPart是：" + wtPart);
			}
			this.wtPart = wtPart;
			this.wtContainer = wtPart.getContainer();
			GLLogger.debug(this, "-------wtContainer--" + wtContainer.getName());
			if (!Constants.normalProcess.equals(processType) && Constants.reworkProcess.equals(processType)
					&& Constants.tempProcess.equals(processType)) {
				throw new WTException(processType + " 文件类型不存在,实例化文件类型只有:" + Constants.normalProcess + ","
						+ Constants.reworkProcess + "," + Constants.tempProcess + " 三种类型");
			}
			List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(wtPart, null, Constants.normalProcess);
			if (0 == list.size()) {
				throw new WTException("零件对应的工艺文件不存在");
			}
			this.document = list.get(0);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

	public ChangeProcessPlanStructure(WTPart wtPart, WTDocument document) {
		GLLogger.debug(this, "--wtPart--" + wtPart + "--document--" + document);
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
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		}
	}

	public ChangeProcessPlanStructure( WTDocument document) {
		try {
			this.wtContainer = document.getContainer();
			GLLogger.debug(this, "-------wtContainer--" + wtContainer.getName());

			this.document = document;
			WTPart part = WCUtil.getRelatedWTPartByDoc(document);
			this.wtPart = part;
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MPMProcessPlan structureSanJiProcessPlan() {
		FileOutputStream fileOutputStream = null;
		Transaction trans = new Transaction();
		MPMProcessPlan processPlan = null;
		WTUser creator = null;
		WTUser currentuser = null;
		boolean flag = false;
		try {
			creator =(WTUser) document.getModifier().getObject();
			currentuser = (WTUser)SessionHelper.manager.getPrincipal();
			if(creator!=null){
				SessionHelper.manager.setPrincipal(creator.getAuthenticationName());
				flag = true;
			}
			// 解压工艺压缩包，获取工艺xml文件的流
			String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
			String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
			ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
			File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
			inputStream = new FileInputStream(xmlFile);

			// 读取xml 创建结构

			trans.start();
			GLLogger.debug(this, "-------start structureProcessPlan----------");
			SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
			Element rootElement = xmlUtil.getRootElement();
			GLLogger.debug(this, "---rootElement-" + rootElement.getName());
			if (XMLConstants.technics.equals(rootElement.getName())) {
				for (Element rootAttrElement : (List<Element>) rootElement.getChildren(XMLConstants.QMFawTechnicsInfo)) {
					GLLogger.debug(this, "---rootAttrElement-" + rootAttrElement.getName());
					/*try {
						NCMaterialHelper.structure(this.document, rootAttrElement);
					}catch (Exception e){
						e.printStackTrace();
					}*/

					if(rootAttrElement.getAttributeValue("oid")!=null&&!"".equals(rootAttrElement.getAttributeValue("oid"))){
						processPlan = (MPMProcessPlan) Util.getObjectByOid(MPMProcessPlan.class,
								rootAttrElement.getAttributeValue("oid"));
					}
					if(processPlan==null){
						processPlan = ext.casc.integrate.util.ProcessUtil.getProcessPlanByNumber(document.getNumber());
					}else if(!document.getNumber().equals(processPlan.getNumber())){
						processPlan = ext.casc.integrate.util.ProcessUtil.getProcessPlanByNumber(document.getNumber());
					}
					// 升版工艺
					MPMProcessPlan newProcessPlan = reviseProcessPlanVersion(processPlan, rootAttrElement,creator);
					folderPath = FolderHelper.service.getFolder(newProcessPlan).getFolderPath();
					// 创建工时定额模型对象
					//WorkHourUtil.createProcessPlanFillTime(wtPart, processPlan.getName(), Constants.changeProcess,
					//		processPlan.toString(), document.toString());
					// 回写工艺的oid
					rootAttrElement.setAttribute("oid", Util.getStringOid(newProcessPlan));
					// 创建工艺和零件的关联
					MPMProcessPlanUtil.createMPMPartToProcessPlanLink(wtPart, newProcessPlan);

					String version = wtPart.getVersionIdentifier().getValue();
					WTPart part = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class,wtPart.getNumber(),version,wtPart.getViewName());
					if(part.getPersistInfo().getObjectIdentifier().getId()!=wtPart.getPersistInfo().getObjectIdentifier().getId()){
						MPMProcessPlanUtil.createMPMPartToProcessPlanLink(part, newProcessPlan);
					}

					// 创建工艺与压缩包的关联
					MPMProcessPlanUtil.createMPMDocumentDescribeLink(newProcessPlan, document);


					// 处理工艺的子节点
					for (Element rootChildElement : (List<Element>) rootAttrElement.getChildren()) {
						GLLogger.debug(this, "---rootChildElement-" + rootChildElement.getName());
						if (XMLConstants.steps.equals(rootChildElement.getName())) {
							structureSteps(newProcessPlan,processPlan, rootChildElement);
						}
					}

					//上传工艺文件的工艺附表、工艺说明文件
					GLLogger.debug(this, "-------starting uploadAttach----------");
					uploadAttach(newProcessPlan, rootAttrElement, tempFilePath + subFileName);
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

			boolean compressflag = ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);

			// 重新上传PBOM的XML
			if(compressflag){
				//WTDocumentUtil.setPrimaryForDocument(document, zipFileName, new FileInputStream(new File(tempFilePath
						//+ zipFileName)));
			}

		} catch (Exception e) {
			e.printStackTrace();
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
			if(flag && currentuser!=null){
			    try {
					SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return processPlan;

	}

	/**
	 * 结构化工艺入口
	 *
	 * @author qianlong
	 * @date 2012-9-10
	 * @param request
	 * @param response
	 * @throws WTException
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void structureProcessPlan() throws WTException {
		FileOutputStream fileOutputStream = null;
		Transaction trans = new Transaction();
		WTUser creator = null;
		WTUser currentuser = null;
		boolean flag = false;
		try {
			currentuser = (WTUser)SessionHelper.manager.getPrincipal();
			creator =(WTUser) document.getModifier().getObject();

			boolean isReportProcess = false;
			if(document!=null){
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
				if(docType.contains("reportTechnics")){
					isReportProcess = true;
				}
			}
			if(isReportProcess){
				MPMProcessPlan	processPlan = ext.casc.integrate.util.ProcessUtil.getProcessPlanByNumber(document.getNumber());
				if (processPlan != null) {
					processPlan = reviseProcessPlanVersion(processPlan,creator);
					//folderPath = FolderHelper.service.getFolder(processPlan).getFolderPath();
					// 创建工艺和零件的关联
					if(wtPart!=null)
						MPMProcessPlanUtil.createMPMPartToProcessPlanLink(wtPart, processPlan);
					// 创建工艺与压缩包的关联
					if(document!=null)
						MPMProcessPlanUtil.createMPMDocumentDescribeLink(processPlan, document);
				}
				return;
			}
			// 解压工艺压缩包，获取工艺xml文件的流
			String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
			String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
			ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
			File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
			inputStream = new FileInputStream(xmlFile);

			// 读取xml 创建结构

			trans.start();
			GLLogger.debug(this, "-------start structureProcessPlan----------");
			SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
			Element rootElement = xmlUtil.getRootElement();
			//GLLogger.debug(this, "---rootElement-" + rootElement.getName());
			if (XMLConstants.technics.equals(rootElement.getName())) {
				for (Element rootAttrElement : (List<Element>) rootElement.getChildren(XMLConstants.QMFawTechnicsInfo)) {
					//GLLogger.debug(this, "---rootAttrElement-" + rootAttrElement.getName());
					/*try {
						NCMaterialHelper.structure(document, rootAttrElement);
					}catch (Exception e){
						e.printStackTrace();
					}*/

					MPMProcessPlan processPlan = null;
					if(rootAttrElement.getAttributeValue("oid")!=null&&!"".equals(rootAttrElement.getAttributeValue("oid"))){
						processPlan = (MPMProcessPlan) Util.getObjectByOid(MPMProcessPlan.class,
								rootAttrElement.getAttributeValue("oid"));
					}
					if(processPlan!=null){
						if(!processPlan.getNumber().equals(rootAttrElement.getAttributeValue("technicsNumber"))){
							processPlan=null;
						}
					}
					if(processPlan==null){
						processPlan = ext.casc.integrate.util.ProcessUtil.getProcessPlanByNumber(document.getNumber());
					}else if(!document.getNumber().equals(processPlan.getNumber())){
						processPlan = ext.casc.integrate.util.ProcessUtil.getProcessPlanByNumber(document.getNumber());
					}

					if(pbo == null && processPlan!=null){
						QueryResult qr2 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices( processPlan);
			        	  if (qr2.hasMoreElements()) {
			        		  WTChangeOrder2 ecn = (WTChangeOrder2) qr2.nextElement();
			        		  pbo = ecn;
			        	  }
					}
					if(pbo == null&&document!=null){
						WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(document);
						Iterator it = coll.iterator();
						if (it.hasNext()) {
							  Object o =  it.next();
							  if(o instanceof WTChangeOrder2){
								  WTChangeOrder2 ecn = (WTChangeOrder2)o ;
								  pbo = ecn;
							  }else if(o instanceof ObjectReference){
			                      ObjectReference orf = (ObjectReference)o;
			                      WTChangeOrder2 ecn = (WTChangeOrder2)orf.getObject();
								  pbo = ecn;
							  }
			        	  }
					}

					// 升版工艺
					MPMProcessPlan oldPlan = null;
					if (processPlan != null) {
						oldPlan = processPlan;
						processPlan = reviseProcessPlanVersion(processPlan, rootAttrElement,creator );
						folderPath = FolderHelper.service.getFolder(processPlan).getFolderPath();
					} else {
						if(creator!=null){
							SessionHelper.manager.setPrincipal(creator.getAuthenticationName());
							flag = true;
						}
						String technicsType = rootAttrElement.getAttributeValue("technicsType");
						String [][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
						for(int i=0;i<technicsTypes[1].length;i++){
							if(technicsTypes[1][i].equals(technicsType)){
								folderPath = LoadConfig.getInstance().getTechnicsPathPrefix();
								folder = folderPath+technicsTypes[1][i];
								typeName = "com.ptc.windchill.mpml.processplan.MPMProcessPlan|"+LoadConfig.getInstance().getLocalDomainName()+"."+technicsTypes[2][i];
								break;
							}
						}
						String version = document.getIterationDisplayIdentifier().toString();
						processPlan = createProcessPlan(rootAttrElement, version);
					}
					// 创建工时定额模型对象
					//WorkHourUtil.createProcessPlanFillTime(wtPart, processPlan.getName(), Constants.changeProcess,
					//		processPlan.toString(), document.toString());
					// 回写工艺的oid
					rootAttrElement.setAttribute("oid", Util.getStringOid(processPlan));
					// 创建工艺和零件的关联
					MPMProcessPlanUtil.createMPMPartToProcessPlanLink(wtPart, processPlan);
					// 创建工艺与压缩包的关联
					MPMProcessPlanUtil.createMPMDocumentDescribeLink(processPlan, document);

					String version = wtPart.getVersionIdentifier().getValue();
					WTPart part = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class,wtPart.getNumber(),version,wtPart.getViewName());
					if(part.getPersistInfo().getObjectIdentifier().getId()!=wtPart.getPersistInfo().getObjectIdentifier().getId()){
						MPMProcessPlanUtil.createMPMPartToProcessPlanLink(part, processPlan);
					}

					//关联修订工艺规程到更改单的更改后文件
					Vector v = new Vector();
					v.add(processPlan);
					if(pbo != null) {
						QueryResult ecaResult = ChangeHelper2.service.getChangeActivities((WTChangeOrder2)pbo);
						while (ecaResult.hasMoreElements()) {
							WTChangeActivity2 activity2 = (WTChangeActivity2) ecaResult.nextElement();
							ChangeHelper2.service.storeAssociations(ChangeRecord2.class, activity2,v);
						}
					}

					// 处理工艺的子节点
					for (Element rootChildElement : (List<Element>) rootAttrElement.getChildren()) {
						GLLogger.debug(this, "---rootChildElement-" + rootChildElement.getName());
						if (XMLConstants.steps.equals(rootChildElement.getName())) {
							structureSteps(processPlan, oldPlan, rootChildElement);
						}
					}

					//上传工艺文件的工艺附表、工艺说明文件
					//GLLogger.debug(this, "-------starting uploadAttach----------");
					uploadAttach(processPlan, rootAttrElement, tempFilePath + subFileName);
					//GLLogger.debug(this, "-------end uploadAttach----------");
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

			boolean compressflag =ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);

			// 重新上传PBOM的XML
			if(compressflag){

			//WTDocumentUtil.setPrimaryForDocument(document, zipFileName, new FileInputStream(new File(tempFilePath
					//+ zipFileName)));
			}

		} catch (Exception e) {
			e.printStackTrace();
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
			if(flag && currentuser!=null){
			    SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
			}
		}

	}

	/**
	 * 结构化报表类工艺入口
	 *
	 * @author qianlong
	 * @date 2012-9-10
	 * @param request
	 * @param response
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void structureReportTechnicsProcessPlan() {
		FileOutputStream fileOutputStream = null;
		Transaction trans = new Transaction();
		WTUser creator =null;
		WTUser currentuser = null;
		boolean flag = false;
		try {
			// 解压工艺压缩包，获取工艺xml文件的流
			creator =(WTUser) document.getModifier().getObject();
			currentuser = (WTUser)SessionHelper.manager.getPrincipal();
			if(creator!=null){
				SessionHelper.manager.setPrincipal(creator.getAuthenticationName());
				flag = true;
			}
			String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
			String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
			ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
			File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
			inputStream = new FileInputStream(xmlFile);

			// 读取xml 创建结构

			trans.start();
			GLLogger.debug(this, "-------start structureProcessPlan----------");
			SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
			Element rootElement = xmlUtil.getRootElement();
			GLLogger.debug(this, "---rootElement-" + rootElement.getName());
			if (XMLConstants.technics.equals(rootElement.getName())) {
				for (Element rootAttrElement : (List<Element>) rootElement.getChildren(XMLConstants.QMFawTechnicsInfo)) {
					GLLogger.debug(this, "---rootAttrElement-" + rootAttrElement.getName());

					MPMProcessPlan processPlan = (MPMProcessPlan) Util.getObjectByOid(MPMProcessPlan.class,
							rootAttrElement.getAttributeValue("oid"));
					if(processPlan==null){
						processPlan = ext.casc.integrate.util.ProcessUtil.getProcessPlanByNumber(rootAttrElement.getAttributeValue("technicsNumber"));
					}else if(!document.getNumber().equals(processPlan.getNumber())){
						processPlan = ext.casc.integrate.util.ProcessUtil.getProcessPlanByNumber(document.getNumber());
					}
					// 升版工艺
					processPlan = reviseProcessPlanVersion(processPlan, rootAttrElement,creator);
					folderPath = FolderHelper.service.getFolder(processPlan).getFolderPath();
					// 创建工时定额模型对象
					//WorkHourUtil.createProcessPlanFillTime(wtPart, processPlan.getName(), Constants.changeProcess,
					//		processPlan.toString(), document.toString());
					// 回写工艺的oid
					rootAttrElement.setAttribute("oid", Util.getStringOid(processPlan));
					// 创建工艺和零件的关联
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

			boolean compressflag =ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);

			// 重新上传PBOM的XML
			if(compressflag){
			//WTDocumentUtil.setPrimaryForDocument(document, zipFileName, new FileInputStream(new File(tempFilePath
					//+ zipFileName)));
			}

		} catch (Exception e) {
			e.printStackTrace();
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
			if(flag && currentuser!=null){
			    try {
					SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


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
	private void structureSteps(MPMProcessPlan processPlan,MPMProcessPlan oldPlan, Element stepElement) throws Exception {
		Map<String, String[]> stepGongshiMap = new HashMap<String, String[]>();
		if(oldPlan != null) {
			stepGongshiMap = getStepGongshiMapByPlan(oldPlan);
		}

		for (Element stepAttrElement : (List<Element>) stepElement.getChildren(XMLConstants.QMProcedureInfo)) {
			//GLLogger.debug(this, "---stepAttrElement-" + stepAttrElement.getName());
			// 创建工序
			MPMOperation operation = createOperation(stepAttrElement,stepGongshiMap);
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
				//GLLogger.debug(this, "---stepChildElement-" + stepChildElement.getName());
				if (XMLConstants.paces.equals(stepChildElement.getName())) {
					structureSubSteps(operation, stepChildElement);
				} else if (XMLConstants.materials.equals(stepChildElement.getName())) {
					structureMaterials(operation, stepChildElement);
				} else if (XMLConstants.equips.equals(stepChildElement.getName())) {
					structureEquipments(operation, stepChildElement);
				} else if (XMLConstants.tools.equals(stepChildElement.getName())) {
					structureTools(operation, stepChildElement);
				} else if (XMLConstants.parts.equals(stepChildElement.getName())) {
					structureParts(operation, stepChildElement);
				} else if (XMLConstants.images.equals(stepChildElement.getName())) {
					structureImages(operation, stepChildElement);
				}
			}
		}

	}

	public static Map<String, String[]> getStepGongshiMapByPlan(MPMProcessPlan plan) {
		Map<String, String[]> stepIdMap = new HashMap<String, String[]>();
		try {
			List<MPMOperationUsageLink> beforeLinks = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
			Map<String,String> idMap = new HashMap<String, String>();
			for(MPMOperationUsageLink beforeLink : beforeLinks) {
				MPMOperationMaster beforeMaster = (MPMOperationMaster) beforeLink.getRoleBObject();
				MPMOperation beforeOperation = ViewWIHelper.getMpmOperation(beforeMaster.getNumber());
				String stepNumber = Util.formateInteger(beforeLink.getOperationLabel());
				String stepId = ext.casc.util.IBAHelper.getIBAStringValue(beforeOperation, "BIAOSHI");
				if(StrUtil.isEmpty(stepId) || "null".equals(stepId)) {
					if(CollUtil.isEmpty(idMap)){
						idMap = SetListGongShiDingEBuilder.getStepIdMapByPlan(plan);
					}
					stepId = idMap.get(stepNumber);
					ext.casc.util.IBAHelper.setIBAStringValue(beforeOperation, "BIAOSHI", stepId);
				}
				String zhunjie = ext.casc.util.IBAHelper.getIBAStringValue(beforeOperation, "ZJGS");
				String danjian = ext.casc.util.IBAHelper.getIBAStringValue(beforeOperation, "DJGS");
				String shebei = ext.casc.util.IBAHelper.getIBAStringValue(beforeOperation, "DanJianSheBeiGS");
				stepIdMap.put(stepId, new String[]{zhunjie, danjian, shebei});
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return stepIdMap;
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

		for (Element subStepAttrElement : (List<Element>) subStepElement.getChildren(XMLConstants.QMProcedureInfo)) {
			//GLLogger.debug(this, "---subStepAttrElement-" + subStepAttrElement.getName());
			// 创建工步
			MPMOperation subOperation = createSubOperation(subStepAttrElement);
			// 回写工步的oid
			subStepAttrElement.setAttribute("oid", Util.getStringOid(subOperation));
			// 工步关联到工序
			MPMProcessPlanUtil.createMPMOperationUsageLink(operation, (MPMOperationMaster) subOperation.getMaster(),
					subStepAttrElement.getAttributeValue("stepNumber"));

			// 处理工步的子节点
			for (Element subStepChildElement : (List<Element>) subStepAttrElement.getChildren()) {
				//GLLogger.debug(this, "---subStepChildElement-" + subStepChildElement.getName());
				if (XMLConstants.materials.equals(subStepChildElement.getName())) {
					structureMaterials(subOperation, subStepChildElement);
				} else if (XMLConstants.equips.equals(subStepChildElement.getName())) {
					structureEquipments(subOperation, subStepChildElement);
				} else if (XMLConstants.tools.equals(subStepChildElement.getName())) {
					structureTools(subOperation, subStepChildElement);
				} else if (XMLConstants.parts.equals(subStepChildElement.getName())) {
					structureParts(subOperation, subStepChildElement);
				} else if (XMLConstants.images.equals(subStepChildElement.getName())) {
					structureImages(subOperation, subStepChildElement);
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

		for (Element materialAttrElement : (List<Element>) materialElement.getChildren(XMLConstants.QMMaterialInfo)) {
			//GLLogger.debug(this, "--materialAttrElement--" + materialAttrElement.getName());
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
				setMaterialLinkIBA(link, materialAttrElement);
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
		for (Element equipmentAttrElement : (List<Element>) equipmentElement.getChildren(XMLConstants.QMEquipmentInfo)) {
			GLLogger.debug(this, "--equipmentAttrElement--" + equipmentAttrElement.getName());
			// 获取装备
			MPMTooling equipment = (MPMTooling) Util.getObjectByOid(MPMTooling.class, equipmentAttrElement
					.getAttributeValue("oid"));
			if (null == equipment) {
				continue;
				//throw new WTException("编号为：" + equipmentAttrElement.getAttributeValue("oid") + "的设备不存在！");
			}
			// 装备关联到工序或者工步
			MPMProcessPlanUtil.createMPMOperationToConsumableLink((MPMOperation) object, (MPMToolingMaster) equipment
					.getMaster());
		}
	}

	/**
	 * 关联工装、工具、刀具、量具、设备
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

		for (Element toolAttrElement : (List<Element>) toolElement.getChildren(XMLConstants.QMToolInfo)) {
			GLLogger.debug(this, "---toolAttrElement-" + toolAttrElement.getName());
			// 获取工装
			MPMTooling tooling = (MPMTooling) Util.getObjectByOid(MPMTooling.class, toolAttrElement
					.getAttributeValue("oid"));
			if (null == tooling) {
				continue;
				//throw new WTException("编号为：" + toolAttrElement.getAttributeValue("toolNum") + "的工装、工量具或者刀具不存在！");
			}
			// 工装关联到工序或者工步
			if(tooling!=null){
				MPMProcessPlanUtil.createMPMOperationToConsumableLink((MPMOperation) object, (MPMToolingMaster) tooling
						.getMaster());
			}
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
		for (Element partAttrElement : (List<Element>) partElement.getChildren(XMLConstants.QMPartInfo)) {
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

		for (Element imageAttrElement : (List<Element>) imageElement.getChildren(XMLConstants.PDrawingInfo)) {
			GLLogger.debug(this, "---imageAttrElement-" + imageAttrElement.getName());

		}
	}

	/**
	 * 升版工艺规程
	 *
	 * @author qianlong
	 * @throws Exception
	 * @date 2013-5-24
	 *
	 */
	public MPMProcessPlan reviseProcessPlanVersion(MPMProcessPlan processPlan, Element rootAttrElement,WTUser creator)
			throws Exception {
		processPlan = MPMProcessPlanUtil.reviseProcessPlanVersion(processPlan,creator);
		if (null != processPlan) {
			// 删除升版后工艺状态
			processPlan = (MPMProcessPlan) Util.setLifecycle(processPlan, Constants.RELEASED);
			// 删除升版后工艺属性
			//setMPMProcessPlanIBA(rootAttrElement, processPlan);
			// 删除升版后工艺与工步工序的关联
			MPMProcessPlanUtil.deleteMPMOperationUsageLink(processPlan);
			// 删除升版后工艺与零件的关联
			MPMProcessPlanUtil.deleteMPMPartToProcessPlanLink(processPlan);
			// 删除升版后工艺与文档的关联
			MPMProcessPlanUtil.deleteMPMDocumentDescribeLink(processPlan);
		}
		return processPlan;
	}

	public MPMProcessPlan reviseProcessPlanVersion(MPMProcessPlan processPlan,WTUser creator)
			throws Exception {
		processPlan = MPMProcessPlanUtil.reviseProcessPlanVersion(processPlan,creator);
		if (null != processPlan) {
			// 删除升版后工艺状态
			processPlan = (MPMProcessPlan) Util.setLifecycle(processPlan, Constants.RELEASED);
			// 删除升版后工艺属性
			//setMPMProcessPlanIBA(rootAttrElement, processPlan);
			// 删除升版后工艺与工步工序的关联
			MPMProcessPlanUtil.deleteMPMOperationUsageLink(processPlan);
			// 删除升版后工艺与零件的关联
			MPMProcessPlanUtil.deleteMPMPartToProcessPlanLink(processPlan);
			// 删除升版后工艺与文档的关联
			MPMProcessPlanUtil.deleteMPMDocumentDescribeLink(processPlan);
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

		IBAHelper ibaHelper = new IBAHelper(processPlan);
		ibaHelper.setIBAValue(processPlan, ibaMap);
	}

	/**
	 * 创建工序
	 *
	 * @param rootAttrElement
	 * @param stepIdMap
	 * @throws Exception
	 * @author qianlong
	 * @date 2013-5-24
	 */
	private MPMOperation createOperation(Element stepAttrElement, Map<String, String[]> stepGongshiMap) throws Exception {
		String name = stepAttrElement.getAttributeValue("stepName");
		String stepNumber = stepAttrElement.getAttributeValue("stepNumber");
		String bsoID = stepAttrElement.getAttributeValue("bsoID");
		if(name==null||"".equals(name)){
			name = stepNumber;
		}
		MPMOperation operation = MPMProcessPlanUtil.createOperation(null, name, wtContainer,
				TypeNameConstants.OPERATION_TYPE_NAME, folderPath + "/工序");
		if (operation != null) {
			operation = (MPMOperation) Util.setLifecycle(operation, Constants.RELEASED);
			String workShop = stepAttrElement.getAttributeValue(AttributeConstants.workShop);
			IBAHelper.setIBAStringValue(operation, com.glaway.mpm.mpmresource.AttributeConstants.workShop, Util.formateString(workShop));
			if(stepGongshiMap.containsKey(bsoID)){
				String[] strings = stepGongshiMap.get(bsoID);
				if(strings.length > 2){
					String zhunjie = strings[0];
					String danjian = strings[1];
					String shebei = strings[2];
					if(StrUtil.isNotEmpty(zhunjie)){
						IBAHelper.setIBAStringValue(operation, "ZJGS", zhunjie);
					}
					if(StrUtil.isNotEmpty(danjian)){
						IBAHelper.setIBAStringValue(operation, "DJGS", danjian);
					}
					if(StrUtil.isNotEmpty(shebei)){
						IBAHelper.setIBAStringValue(operation, "DanJianSheBeiGS", shebei);
					}
				}
			}
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

		Element procedureContentElement = stepAttrElement.getChild(XMLConstants.procedureContent);
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
				TypeNameConstants.SUBOPERATION_TYPE_NAME, folderPath + "/工步");
		if (subOperation != null) {
			subOperation = (MPMOperation) Util.setLifecycle(subOperation, Constants.RELEASED);
			//setOperationIBA(subOperation, subStepAttrElement);
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
		//ibaHelper.setIBAValue(link, ibaMap);
	}

	public static void uploadAttach(MPMProcessPlan pplan,Element rootAttrElement,String tempFilePath) throws WTException {
		//上传工艺说明
		String gydesFilePath = tempFilePath + File.separator + ".工艺说明.doc";
		//System.out.println("------------gydesFilePath:"+gydesFilePath);
		MPMProcessPlanUtil.uploadAttach(pplan, gydesFilePath,".工艺说明.doc");

		for (Element rootChildElement : (List<Element>) rootAttrElement.getChildren()) {
			GLLogger.debug(ProcessPlanStructure.class.getName(), "--uploadAttach--" + rootChildElement.getName());
			if ("additiontables".equals(rootChildElement.getName())) {
				for (Element element : (List<Element>) rootChildElement.getChildren("additionaltable")) {
					String filePath = tempFilePath + File.separator + element.getAttributeValue("absolutePath");
					String fileName = element.getAttributeValue("attachName")+".doc";
					//System.out.println("------------filePath:"+filePath);
					MPMProcessPlanUtil.uploadAttach(pplan, filePath,fileName);
				}
			}
		}
	}

	public static void test(String partid,String docid,String type) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {

			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partid);
			WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, docid);
			document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
			ChangeProcessPlanStructure planStructure = new ChangeProcessPlanStructure(part, document);
			if("1".equals(type)){
				planStructure.structureProcessPlan();
			} if("2".equals(type)){
				planStructure.structureSanJiProcessPlan();
			}  else{
				planStructure.structureReportTechnicsProcessPlan();
			}

		} catch (WTException e) {
			e.printStackTrace();
		}finally{
			 SessionServerHelper.manager.setAccessEnforced(enforce);
		}
	}

	public static void main(String[] args) {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		String partid = args[0];
		String docid = args[1];
		String type = args[2];
		try {
			methodServer.invoke("test", ChangeProcessPlanStructure.class.getName(), null, new Class[] {String.class,String.class,String.class}, new Object[] {partid,docid,type});
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
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
		String technicsType = rootAttrElement.getAttributeValue("technicsType");
		String name = rootAttrElement.getAttributeValue("technicsName");
		String technicsNumber = rootAttrElement.getAttributeValue("technicsNumber");
		String pplanNumber = rootAttrElement.getAttributeValue("pplanNumber");
		MPMProcessPlan processPlan = null;
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

		return processPlan;
	}

}
