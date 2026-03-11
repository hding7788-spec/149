package com.glaway.mpm.print.util;

import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.ObjectNoLongerExistsException;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.inf.container._WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.method.MethodContext;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.pom.WTConnection;
import wt.project.Role;
import wt.project._Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representable;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.baseline.BaselineHelper;
import wt.vc.baseline.Baselineable;
import wt.vc.baseline.ManagedBaseline;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import com.lowagie.text.pdf.PdfReader;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.resource.MPMPlant;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.model.data.CmBaseline;
import com.glaway.mpm.print.model.GWPrintApplyRecord;
import com.glaway.mpm.print.model.GWPrintDistributeRecord;
import com.glaway.mpm.print.model.GWPrintRecoverRecord;
import com.glaway.mpm.print.GWPrintApplyRecordManager;
import com.glaway.mpm.print.GWPrintDistributeRecordManager;
import com.glaway.mpm.print.GWPrintRecoverRecordManager;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.CmDistributionBean;
import com.glaway.mpm.print.data.CmImportBean;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.constants.ProcessPlanConstants;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;

import ext.casc.erp.CallWebServiceOME;
import ext.casc.erp.ERPUtil;
import ext.casc.util.CommonUtil;
import ext.casc.util.IBAUtil;

import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.print.util.LoadPropertiesConfig;
import com.glaway.mpm.parameter.util.PersistableUtil;
import com.glaway.mpm.util.UserUtil;
import ext.casc.util.WCUtil;
import ext.casc.workflow.util.PrintDistributionHelper;
import ext.csc.utilities.principal.CSCPrincipal;

public class PrintUtil {

	private static final VaLogger logger = VaLogger.getLogger(PrintUtil.class.getClass());

	public static String getPDFPageCountByName(Persistable per) {
		try {
			ApplicationData appData = null;
			if (per instanceof MPMProcessPlan
					|| per instanceof WTDocument) {
				appData = WCUtil.getRepresentation((Representable) per);
			}

			if (appData == null) {
				appData =  PrintUtil.getPDFFile((ContentHolder) per);
			}

			if (appData != null) {
				InputStream istream = ContentServerHelper.service.findContentStream(appData);
				if (istream != null) {
					PdfReader reader = new PdfReader(istream);
					int page = reader.getNumberOfPages();
					return CommonUtil.objectToString(page);
				}
			}
		} catch (ObjectNoLongerExistsException e) {
			logger.error(e);
		} catch (WTException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (PropertyVetoException e) {
			logger.error(e);
		}
		return "";
	}

	public static ApplicationData getPDFFile(ContentHolder holder) {
		try {
			QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			ApplicationData printData = null;
			ApplicationData secondaryData = null;
			while (qr.hasMoreElements()) {
				ApplicationData appData = (ApplicationData) qr.nextElement();
				String name = appData.getFileName();
				if(holder instanceof MPMProcessPlan){
					MPMProcessPlan processPlan = (MPMProcessPlan)holder;
					String ppOid = String.valueOf(PersistenceHelper.getObjectIdentifier(processPlan).getId());
					if (name.startsWith("Print_") && name.endsWith(ProcessPlanConstants.PDF)) {
						printData = appData;
					}

					if(name.startsWith(ppOid) && name.endsWith(ProcessPlanConstants.PDF)){
						secondaryData = appData;
					}

				}else if(holder instanceof WTChangeOrder2){
					WTChangeOrder2 WTChangeOrder2 = (WTChangeOrder2)holder;
					String number = WTChangeOrder2.getNumber();
					if (name.startsWith("Print_"+number) && name.endsWith(ProcessPlanConstants.PDF) && name.contains("PDFPreview")) {
						printData = appData;
						break;
					}
				} else {
					if (name.startsWith("Print_") && name.endsWith(ProcessPlanConstants.PDF)) {
						printData = appData;
						break;
					}
				}
			}
			/*if(holder instanceof WTDocument ){
				WTDocument wtDocument = (WTDocument) holder;
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtDocument);
				if(docType.contains("casc.sast.149.TY_PROCESS_DOC")){
					QueryResult qrP = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
					while (qrP.hasMoreElements()) {
						ApplicationData appData = (ApplicationData) qrP.nextElement();
						String name = appData.getFileName();
						if(name.endsWith(ProcessPlanConstants.PDF)){
							printData = appData;
							break;
						}
					}
				}
			}*/

			if(printData != null){
				return printData;
			}else{
				return secondaryData;
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public static List<CmBaseline> getBaseline(String oid, String fileType) throws NumberFormatException, WTException {
		Baselineable baselineable = (Baselineable) PersistableUtil.getPersistable(oid);
		List<CmBaseline> baselineList = new ArrayList<CmBaseline>();
		QueryResult qr = BaselineHelper.service.getBaselines(baselineable);
		CmBaseline cmBaseline = null;
		IBAHelper helper = null;
		while (qr.hasMoreElements()) {
			ManagedBaseline baseline = (ManagedBaseline) qr.nextElement();
			helper = new IBAHelper(baseline);
			String status = helper.getIBAValue("TS_STATUS");
			cmBaseline = new CmBaseline();
			cmBaseline.setOid(baseline.getPersistInfo().getObjectIdentifier().getId());
			cmBaseline.setStatus(status);
			baselineList.add(cmBaseline);
		}
		return baselineList;
	}

	public static Baselineable getBaselineableByOid(long oid, Class<?> objClass) throws WTException {
		QuerySpec qSpec = new QuerySpec(objClass);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(objClass, "thePersistInfo.theObjectIdentifier.id",
                SearchCondition.EQUAL, oid);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        Baselineable baselineable = null;
        if (qResult.hasMoreElements()) {
        	baselineable = (Baselineable) qResult.nextElement();
		}
        return baselineable;
	}

	public static Vector<String> getDistributeDept() {
		//return LoadPropertiesConfig.getInstance(4).getDistributeDept();
		Vector<String> mpmPlantVector = new Vector<String>();
		try {
			QueryResult result = MPMResourceUtil.getAllPlant();
			while (result.hasMoreElements()) {
				MPMPlant mpmPlant = (MPMPlant) result.nextElement();
				mpmPlantVector.add(mpmPlant.getName());
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mpmPlantVector;
	}

	public static Vector<String> getOutsideDept() {
		return LoadPropertiesConfig.getInstance(4).getOutsideDept();
	}

	public static synchronized String getPrintObjNumber(String objType, String preFix) {
		String number = "";
		String serialNumber = "";
		int num = 1;
		boolean flag = true;
		try {
			num = getSerialNumber(preFix, objType) + 1;
			do {
				serialNumber = dealSerialNumber(num, objType);
				number = preFix + serialNumber;
				flag = true;
				if (!flag) {
					num = num + 1;
				}
				if (num > 10000) {
					flag = true;
				}
			} while (!flag);
			updateSerialNumber(preFix, num);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return number;
	}

	/**
	 * 根据前缀获取流水号
	 *
	 * @param prefix
	 * @return
	 * @throws WTException
	 */
	public static int getSerialNumber(String prefix, String objType) throws WTException {
		int number = 0;
		MethodContext context = MethodContext.getContext();
		WTConnection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = (WTConnection) context.getConnection();
			String sql = "select serialnumber from printserialnumber where prefix = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, prefix);
			rs = ps.executeQuery();
			if (rs.next()) {
				number = rs.getInt("serialnumber");
			} else {
				sql = "insert into printserialnumber(prefix, serialnumber, objecttype) values(?, ? ,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, prefix);
				ps.setInt(2, number);
				ps.setString(3, objType);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (!rs.isClosed()) {
					rs.close();
				}
				if (!ps.isClosed()) {
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return number;
	}

	/**
	 * 更新流水号
	 *
	 * @param prefix
	 * @param number
	 */
	public static void updateSerialNumber(String prefix, int number) {
		MethodContext context = MethodContext.getContext();
		WTConnection conn = null;
		PreparedStatement ps = null;
		try {
			conn = (WTConnection) context.getConnection();
			String sql = "update printserialnumber set serialnumber = ? where prefix = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, number);
			ps.setString(2, prefix);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (!ps.isClosed()) {
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 流水号不足3位，用0补齐
	 *
	 * @param number
	 * @return
	 */
	public static String dealSerialNumber(int number, String objType) {
		String value = "";
		if(objType.equals("QRCODE")){
			if (number < 10) {
				value = "000" + number;
			} else if (number < 99) {
				value = "00" + number;
			} else if(number < 999){
				value = "0" + number;
			} else {
				value = "" + number;
			}
		}else if(objType.equals("PRINTAPPLYRECORD") ||
					objType.equals("PRINTDISTRIBUTERECORD") ||
						objType.equals("PRINTRECOVERRECORD")){
			if (number < 10) {
				value = "000000000" + number;
			} else if (number < 99) {
				value = "00000000" + number;
			} else if(number < 999){
				value = "0000000" + number;
			} else if(number < 9999){
				value = "000000" + number;
			} else if(number < 99999){
				value = "00000" + number;
			} else if(number < 999999){
				value = "0000" + number;
			} else if(number < 9999999){
				value = "000" + number;
			} else if(number < 99999999){
				value = "00" + number;
			} else if(number < 999999999){
				value = "0" + number;
			} else {
				value = "" + number;
			}
		}
		return value;
	}

	/**
	 * 工作流程任务页面显示的打印文件列表
	 *
	 * @param wfProcess
	 * @param object
	 * @return
	 */
	public static String getPrintFilesUrl(ObjectReference reference, WTObject pbo, String type, String name) {
		String oid = "OR%3Awt.doc.WTDocument%3A" + PersistenceHelper.getObjectIdentifier(pbo).getId();
		String params = "type=" + type + "&oid=" + oid + "&startFrom=0";
		String url = "<a href=\"netmarkets/jsp/mpm/startFilePrintFrame.jsp?"+params+"\" target=\"_blank\">"+name+"</a>";
		return url;
	}

	/**
	 * 判断打印申请文件是否有驳回项
	 * @param pbo
	 * @return
	 * @throws Exception
	 */
	public static boolean isRejectPrintApply(WTObject pbo) throws Exception {
		boolean flag = false;
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument)pbo;
			List<GWPrintApplyRecord> gwPrintApplyRecords = GWPrintApplyRecordManager.queryGWPrintApplyRecordByPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
			for(GWPrintApplyRecord gwPrintApplyRecord : gwPrintApplyRecords){
				if(gwPrintApplyRecord.getRejectStatus().equals(PrintServerConstants.REJECTSTATUS_YBH)){
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	public static boolean rejectCount(WTObject pbo) throws Exception {
		boolean flag = false;
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument)pbo;
			List<GWPrintApplyRecord> gwPrintApplyRecords = GWPrintApplyRecordManager.queryGWPrintApplyRecordByPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
			int printApplyCount = gwPrintApplyRecords.size();
			int printRejectCount = 0;
			for(GWPrintApplyRecord gwPrintApplyRecord : gwPrintApplyRecords){
				if(gwPrintApplyRecord.getRejectStatus().equals(PrintServerConstants.REJECTSTATUS_YBH)){
					printRejectCount ++;
				}
			}
			if(printApplyCount == printRejectCount){
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 设置人员至角色中
	 * @param reference
	 * @param pbo
	 * @param roleName
	 * @return
	 * @throws Exception
	 */
	public static void setUserToRole(ObjectReference reference, WTObject pbo, String roleName) throws Exception {
		Persistable persistable = reference.getObject();
		WfProcess wfProcess = null;
		if (persistable instanceof WfProcess) {
			wfProcess = (WfProcess)persistable;
		} else if (persistable instanceof WfAssignedActivity) {
			WfAssignedActivity wfaa = (WfAssignedActivity)persistable;
			wfProcess = wfaa.getParentProcess();
		}
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument)pbo;
			List<WTGroup> groups = new ArrayList<WTGroup>();
			List<GWPrintApplyRecord> gwPrintApplyRecords = GWPrintApplyRecordManager.queryGWPrintApplyRecordByPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
			for(GWPrintApplyRecord gwPrintApplyRecord : gwPrintApplyRecords){
				String printStatus = gwPrintApplyRecord.getPrintStatus();
				if(printStatus.equals(PrintServerConstants.PRINTSTATUS_YDY)){
					List<GWPrintDistributeRecord> gwPrintDistributeRecords = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCode(gwPrintApplyRecord.getBarCode());
					for(GWPrintDistributeRecord gwPrintDistributeRecord : gwPrintDistributeRecords){
						String groupName = gwPrintDistributeRecord.getDistributeDept();
						WTGroup group = UserUtil.queryGroup("资料员_" + groupName);
						if(group == null){
							group = UserUtil.queryGroup(PrintServerConstants.GROUP_XXDAC);
						}
						if(!groups.contains(group)){
							groups.add(group);
						}
					}
				}
			}
			Role role = _Role.toRole(roleName);
	        Team team = (Team) wfProcess.getTeamId().getObject();
			for(WTGroup group : groups){
				logger.debug("设置组：" + group.getName() + "至角色：" + roleName);
				team.addPrincipal(role, group);
			}
			team = (Team) PersistenceHelper.manager.refresh(team);
		    team = (Team) PersistenceHelper.manager.save(team);
		}
	}

	public static void setGroupUserToRole(ObjectReference reference, WTObject pbo, String roleName, String groupName) throws Exception {
		Persistable persistable = reference.getObject();
		WfProcess wfProcess = null;
		if (persistable instanceof WfProcess) {
			wfProcess = (WfProcess)persistable;
		} else if (persistable instanceof WfAssignedActivity) {
			WfAssignedActivity wfaa = (WfAssignedActivity)persistable;
			wfProcess = wfaa.getParentProcess();
		}
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument)pbo;
			List<WTGroup> groups = new ArrayList<WTGroup>();
			List<GWPrintRecoverRecord> gwPrintRecoverRecords = GWPrintRecoverRecordManager.queryGWPrintRecoverRecordByPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
			for(GWPrintRecoverRecord gwPrintRecoverRecord : gwPrintRecoverRecords){
				if (groupName.equals("部门领导_")) {
					String recoverDept = gwPrintRecoverRecord.getRecoverDept();
					groupName = groupName + recoverDept;
				}
				logger.debug("===============1" + groupName);
				WTGroup group = UserUtil.queryGroup(groupName);
				logger.debug("===============2" + group);
				if(!groups.contains(group)){
					groups.add(group);
				}
			}

			Role role = _Role.toRole(roleName);
	        Team team = (Team) wfProcess.getTeamId().getObject();
	        for(WTGroup group : groups){
	        	logger.debug("=======3" + group);
				logger.debug("设置组：" + group.getName() + "至角色：" + roleName);
				team.addPrincipal(role, group);
			}
			team = (Team) PersistenceHelper.manager.refresh(team);
		    team = (Team) PersistenceHelper.manager.save(team);
		}
	}

	public static String getMoreObjectOids(List<?> list) throws WTException{
		StringBuffer buf = new StringBuffer();
		for(Object object : list){
			if (object instanceof NmOid) {
	            NmOid nmOid = (NmOid) object;
	            Object obj = nmOid.getRefObject();
	            if(obj instanceof MPMProcessPlan){
					MPMProcessPlan mpmProcessPlan = (MPMProcessPlan)obj;
					String oid = PrintServerConstants.OID_MPMPROCESSPLAN + mpmProcessPlan.getPersistInfo().getObjectIdentifier().getId();
					buf.append(oid).append("-");
				}else if(obj instanceof WTChangeOrder2){
					WTChangeOrder2 ecn = (WTChangeOrder2)obj;
					String oid = PrintServerConstants.OID_WTCHANGEORDER2 + ecn.getPersistInfo().getObjectIdentifier().getId();
					buf.append(oid).append("-");
				}else if(obj instanceof WTDocument){
					WTDocument doc = (WTDocument)obj;
					String oid = PrintServerConstants.OID_WTDOCUMENT + doc.getPersistInfo().getObjectIdentifier().getId();
					buf.append(oid).append("-");
				}
			}
		}
		return buf.toString().substring(0, buf.length()-1);
	}

	public static void isAllPrint(WTObject pbo) throws Exception {
		boolean flag = true;
		StringBuffer buf = new StringBuffer();
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument)pbo;
			List<GWPrintApplyRecord> list = GWPrintApplyRecordManager.queryGWPrintApplyRecordByPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
			for(GWPrintApplyRecord gwPrintApplyRecord : list){
				String printStatus = gwPrintApplyRecord.getPrintStatus();
				String rejectStatus = gwPrintApplyRecord.getRejectStatus();
				if(printStatus.equals(PrintServerConstants.PRINTSTATUS_WDY) && rejectStatus.equals(PrintServerConstants.REJECTSTATUS_WBH)){
					flag = false;
					buf.append("不允许完成任务：\n");
					buf.append("工艺文件：" + gwPrintApplyRecord.getProcessNumber() + "未打印，请执行打印或驳回！\n");
				}
			}
		}
		if (!flag) {
			throw new WTException(buf.toString());
		}
	}

	public static boolean verifyPrintApplyRecordIsSave(List<CmPrintInfoBean> list) throws Exception{
		boolean flag = true;
		for(CmPrintInfoBean cmPrintInfoBean : list){
			String processOid = cmPrintInfoBean.getOid();
			String rejectStaus = cmPrintInfoBean.getRejectState();
			GWPrintApplyRecord gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByProcessOidAndRejectStatus(processOid, rejectStaus);
			if(gwPrintApplyRecord == null){
				flag = false;
			}
		}
		return flag;
	}

	public static ApplicationData getPDFSignFile(ContentHolder holder, String oid) {
		ApplicationData appData = null;
		try {
//			ReferenceFactory factory = new ReferenceFactory();
//			Persistable per = factory.getReference(oid).getObject();
//			String perOid = String.valueOf(PersistenceHelper.getObjectIdentifier(per).getId());
			QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				appData = (ApplicationData) qr.nextElement();
//				String name = appData.getFileName();
//				if (name.equals(perOid + "_sign.pdf")) {
				return appData;
//				}
			}

			//处理历史数据问题
			while (qr.hasMoreElements()) {
				appData = (ApplicationData) qr.nextElement();
				String name = appData.getFileName();
				if (name.endsWith("_sign.pdf")) {
					return appData;
				}
			}
		} catch (WTException e) {
			logger.error(e);
		}
		return null;
	}

	public static CmAttachment getPDFSignFileAttachment(Persistable per, String oid) throws Exception{
		CmAttachment attachment = null;
		//ApplicationData appData = getPDFSignFile(holder, oid);
		ApplicationData appData =getPDFFile((ContentHolder) per);
		String softType = getSoftType((WTObject) per);
		if (appData != null) {
			attachment = new CmAttachment();
			attachment.setFileName(appData.getFileName());
			attachment.setBytes(CommonUtil.applicationDataToByte(appData));
			attachment.setTemplateType(softType);
		}
		return attachment;
	}
    /**
    *获取文件类型
    * @author ACE CHEN
    * @param obj
    * @return
    * @throws WTException
    */
   public static String getSoftType(WTObject obj) throws WTException {
       String typeName = "";
       if(obj instanceof WTChangeOrder2){
    	   return "CHANGEORDER";
       }
       TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(obj);
       typeName = type.getTypename();
       if(typeName.contains("casc.sast.149.PROCESS_PLAN")){
    	   return "PROCESS_PLAN";
       }
       //通用工艺 add by liangbo
       if(typeName.contains("casc.sast.149.TY_PROCESS_DOC")){
		   return "TONGYONGPROCESS";
	   }
       if(typeName.contains("casc.sast.149.SOPDoc")) {
    	   return "SOPDOC";
       }
       int nIndex1 = typeName.lastIndexOf("|");
       int nIndex2 = typeName.lastIndexOf(".");
       int nIndex = nIndex1 > nIndex2 ? nIndex1 : nIndex2;
       if (nIndex >= 0) {
       	typeName = typeName.substring(nIndex + 1);
       }
       if(typeName.equals("PROCESS_NOTICE")){
    	   return "PROCESS_NOTICE";
       }else if(typeName.equals("GONGYIFENFANGAN")||typeName.equals("GONGYIZONGFANGAN")
    		   ||typeName.equals("TECHNOLOGY_AGREEMENT")){
    	   //工艺总方案和工艺分方案，使用的是技术协议的模板
    	   return "TECHNOLOGY_AGREEMENT";
       }else{
    	   return "OTHERREPORT";
       }
   }

	public static void deleteTempFiles(String folderName){
		//删除文件夹结构
		String tempPath = FileUtil.getWncTmpPath();
		FileUtil.deleteSubFile(tempPath + File.separator + PrintServerConstants.FOLDER_PRINT + File.separator + folderName);
		//删除父文件夹
		File folder = new File(tempPath + File.separator + PrintServerConstants.FOLDER_PRINT + File.separator + folderName);
		if(folder != null){
			folder.delete();
		}
		//删除压缩包
		File file = new File(tempPath + File.separator + PrintServerConstants.FOLDER_PRINT + File.separator + folderName + ".zip");
		if(file != null){
			file.delete();
		}
	}

	public static Map<String, String> queryWTUserInfo(String userName) throws WTException{
		Map<String, String> map = null;
		WTUser user = UserUtil.getUser(userName);
		if(user != null){
			map = new HashMap<String, String>();
			map.put("oid", String.valueOf(user.getPersistInfo().getObjectIdentifier().getId()));
			map.put("name", user.getName());
			map.put("fullName", user.getFullName());
			List<WTGroup> groups = UserUtil.queryGroup2("资料员");
			for(WTGroup group : groups){
				if(group.isMember(user)){
					map.put("dept", group.getName());
					break;
				}
			}
		}
		return map;
	}

	public static void clearRecoverInfo(WTObject pbo) throws Exception {
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument)pbo;
			List<GWPrintRecoverRecord> list = GWPrintRecoverRecordManager.queryGWPrintRecoverRecordByPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
			for(GWPrintRecoverRecord gwPrintRecoverRecord : list){
				GwPersistenceHelper.manager.delete(gwPrintRecoverRecord);
			}
		}
	}

	public static void isAllReceive(WTObject pbo) throws Exception {
		boolean flag = true;
		StringBuffer buf = new StringBuffer();
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument)pbo;
			List<GWPrintRecoverRecord> list = GWPrintRecoverRecordManager.queryGWPrintRecoverRecordByPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
			for(GWPrintRecoverRecord gwPrintRecoverRecord : list){
				long recoverCount = gwPrintRecoverRecord.getRecoverQuantity();
				long receiveCount = gwPrintRecoverRecord.getReceiveQuantity();
				if(recoverCount != receiveCount){
					flag = false;
					GWPrintApplyRecord gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByQRCode(gwPrintRecoverRecord.getBarCode());
					buf.append("不允许完成任务：\n");
					buf.append("工艺文件：" + gwPrintApplyRecord.getProcessNumber() + "尚未全部回收！\n");
				}
			}
		}
		if (!flag) {
			throw new WTException(buf.toString());
		}
	}

	public static WTContainer getContainerByName(String name) throws WTException {
		WTContainer container = null;
		QuerySpec querySpec = new QuerySpec(WTContainer.class);
		querySpec.appendWhere(new SearchCondition(WTContainer.class, _WTContainer.NAME, SearchCondition.EQUAL, name));
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			container = (WTContainer) queryResult.nextElement();
		}
		return container;
	}

	public static String getProcessType(String type){
		if("工艺总方案".equals(type)){
			return "casc.sast.149.GONGYIZONGFANGAN";
		}else if("工艺分方案".equals(type)){
			return "casc.sast.149.GONGYIFENFANGAN";
		}else if("工艺技术通知单".equals(type)){
			return "casc.sast.149.PROCESS_NOTICE";
		}else if("工艺技术协议".equals(type)){
			return "casc.sast.149.TECHNOLOGY_AGREEMENT";
		}else if("通用工艺".equals(type)){
			return "casc.sast.149.TY_PROCESS_DOC";
		}
		return "";
	}

	public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
	}

	//查询当前用户名
 	 public static String getUserName() throws WTException{
 		 String userName = SessionHelper.manager.getPrincipal().getName();
 		 return userName;
 	 }

 	 //根据用户名获得用户部门
 	 public static String getUserDepartment() throws WTException { // add by lkc 2018.1.25
 		 String userName = getUserName();
 		 String result = "";
 		 WTUser user = CSCPrincipal.getUserByName(userName);
 		 if (user == null ) {
 			 return "";
 		 }
	        Enumeration groups = user.parentGroupNames();
	        while (groups.hasMoreElements()) {
	        	String gname = (String) groups.nextElement();
	            if (gname.indexOf("分厂") > -1 && gname.indexOf("工艺组长") < 0){
	                return gname.substring(0, 3);
	            }else if (gname.indexOf("资料员") > -1 && gname.indexOf("工艺组长") < 0){
	            	return gname.substring(0, gname.length() - 4);
	        	}else if (gname.indexOf("领导") > -1 && gname.indexOf("工艺组长") < 0){
	            	return gname.substring(0, gname.length() - 3);
	        	}else if (gname.indexOf("项目办") > -1 && gname.indexOf("工艺组长") < 0){
	            	return gname.substring(0, gname.length() - 4);
	        	}else if (gname.indexOf("档案室") > -1 && gname.indexOf("分厂") < 0){
	        		return gname.substring(0, 3);
	        	}else if (gname.indexOf("档案员组") > -1 && gname.indexOf("分厂") < 0){
	        		return gname.substring(0, 3);
	        	}
	        }
	        return "";
	    }

 	 public static String getUserDept() throws WTException{
 		 String userName = getUserName();
 		String dept = getUserByName(userName);
 		 return dept;
 	 }

 	 /**
 	  * 通用户名获取用户所在组
 	 * @author zhuhao
 	 * @date 2018-3-27
 	 * @param userName
 	 * @return
 	 * @throws WTException
 	  */
 	 public static String getUserByName(String userName) throws WTException{
 		WTUser user = CSCPrincipal.getUserByName(userName);
 		if(user == null){
 			return "";
 		}
 		String group = "";
 		Enumeration groups = user.parentGroupNames();
        while (groups.hasMoreElements()) {
        	String gname = (String) groups.nextElement();
        	if(gname.endsWith("分厂资料员组")){
        		group = gname.substring(0, gname.indexOf("资料员组"));
        		break;
        	}else if(gname.equals("档案室库管理员组")){
        		group = "档案室";
        		break;
        	}
        }
		return group;
 	 }

	 public static String getUserDepartment(String userName) throws WTException { // add by lkc 2018.1.25
 		 WTUser user = CSCPrincipal.getUserByName(userName);
 		 if (user == null || "Administrator".equals(user)) {
 			 return "";
 		 }
	        Enumeration groups = user.parentGroupNames();
	        while (groups.hasMoreElements()) {
	        	String gname = (String) groups.nextElement();
	            if (gname.indexOf("分厂") > -1 && gname.indexOf("工艺组长") < 0){
	                return gname.substring(0, 3);
	            }else if (gname.indexOf("资料员") > -1 && gname.indexOf("工艺组长") < 0){
	            	return gname.substring(0, gname.length() - 4);
	        	}else if (gname.indexOf("领导") > -1 && gname.indexOf("工艺组长") < 0){
	            	return gname.substring(0, gname.length() - 3);
	        	}else if (gname.indexOf("项目办") > -1 && gname.indexOf("工艺组长") < 0){
	            	return gname.substring(0, gname.length() - 4);
	        	}else if (gname.indexOf("档案室") > -1 && gname.indexOf("分厂") < 0){
	        		return gname.substring(0, 3);
	        	}else if (gname.indexOf("档案员组") > -1 && gname.indexOf("分厂") < 0){
	        		return gname.substring(0, 3);
	        	}
	        }
	        return "";
	    }
	 //根据用户名获得用户所在组
 	 public static String getUserGroup() throws WTException { // add by lkc 2018.1.25
 		 String userName = getUserName();
 		 String result = "";
 		 WTUser user = CSCPrincipal.getUserByName(userName);
 		 if (user == null || "Administrator".equals(user)) {
 			 return "";
 		 }
	        Enumeration groups = user.parentGroupNames();
	        while (groups.hasMoreElements()) {
	        	String gname = (String) groups.nextElement();
	            if (gname.indexOf("资料员") > -1 && gname.indexOf("工艺组长") < 0 && gname.indexOf("分厂") > -1){
	            	return "部门资料员";
	        	}else if (gname.indexOf("领导") > -1 && gname.indexOf("工艺组长") < 0 &&  gname.indexOf("分厂") > -1){
	            	return "部门领导";
	        	}else if (gname.indexOf("档案员") > -1 ){
	            	return "档案员";
	        	}else if (gname.indexOf("库房管理员") > -1 ){
	            	return "库房管理员";
	        	}else if (gname.indexOf("项目办") > -1 ){
	            	return "项目办";
	        	}
	        }
	        return "";
	    }

 	/**
  	 * 查询组下面所有的用户
  	 *
  	 * @param group
  	 * @param userList
  	 * @throws WTException
  	 */
  	public static void searchGroupUserList(WTGroup group, List<WTUser> userList) throws WTException { // add by lkc 2018.1.25
  		if (userList != null && group != null) {
  			Enumeration<?> enumeration = OrganizationServicesHelper.manager.members(group);
  			Object obj = null;
  			WTUser user;
  			while (enumeration.hasMoreElements()) {
  				obj = enumeration.nextElement();
  				if (obj instanceof WTUser) {
  					user = (WTUser) obj;
  					if (!userList.contains(user)) {
  						userList.add((WTUser) obj);
  					}
  				} else if (obj instanceof WTGroup) {
  					searchGroupUserList((WTGroup) obj, userList);
  				}
  			}
  		}
  	}

  	/**
  	 * 读取配置文件中的部门
  	 * @return
  	 */
	public static String[] getAllDept() {
		List<String> list = PrintDistributionHelper.getAllPrintDept();
		if(list == null || list.isEmpty()){
			return null;
		}
		//从配置文件中读取的部门转成数组
		String[] dept = new String[list.size()];
		list.toArray(dept);
		return dept;
	}

	/**
	 * 获取领取信息
	* @author zhuhao
	* @date 2018-3-27
	* @param name
	* @return
	* @throws WTException
	 */
	public static CmDistributionBean getDeptMessage(String name) throws WTException {
		CmDistributionBean cmDistributionBean = new CmDistributionBean();
		WTUser user = CSCPrincipal.getUserByName(name);
		if(user == null){
			return null;
		}
		String dept = getUserByName(name);
		cmDistributionBean.setReceiptor(user.getFullName());
		cmDistributionBean.setReceiveDept(dept);
		cmDistributionBean.setReceiveFile(name);
		return cmDistributionBean;
	}

	public static void writeBytes(String filePath, byte[] bytes) {
		if (bytes == null) {
			return;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			int size = bytes.length / 1024;
			for (int i = 0; i < size; i++) {
				fos.write(bytes, i * 1024, 1024);
			}
			fos.write(bytes, size * 1024, bytes.length % 1024);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文件转成数组
	* @author zhuhao
	* @date 2018-4-8
	* @param filePath
	* @return
	* @throws IOException
	 */
	private static byte[] InputStream2ByteArray(String filePath) throws IOException {
	    InputStream in = new FileInputStream(filePath);
	    byte[] data = toByteArray(in);
	    in.close();
	    return data;
	}

	private static byte[] toByteArray(InputStream in) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024 * 4];
	    int n = 0;
	    while ((n = in.read(buffer)) != -1) {
	        out.write(buffer, 0, n);
	    }
	    return out.toByteArray();
	}

	public static byte[] getImageByte(String name) {
		WTProperties wtProperties;
		byte[] fileByte = null;
		try {
			wtProperties = WTProperties.getLocalProperties();
			String codebasePath = wtProperties.getProperty("wt.codebase.location");
			String path = codebasePath + File.separator +"printApply";
			path = path + File.separator + name;
			if(!path.endsWith(".jpg")){
				path = path + ".jpg";
			}
			fileByte = InputStream2ByteArray(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileByte;
	}

	public static String getModelType(String str){
		String result = "";
		if("运载型号".equals(str)){
			result = "Y";
		}else if("战术型号".equals(str)){
			result = "Z";
		}else if("飞船型号".equals(str)){
			result = "F";
		}else{
			result = "Q";
		}
		return result;
	}

	public static boolean hasChangeNotice(CmImportBean bean) {
		if(bean.getChangeNoticeNumber() != null || bean.getChangeContent() != null ||
				bean.getChangeDate() != null || bean.getChangeNoticeOutDept() != null || bean.getViewChange() != null){
			return true;
		}
		return false;
	}

	/**
	 * 根据打印申请的分发部门及份数和补发申请的分发部门及份数生成新的分发部门及份数
	* @author zhuhao
	* @date 2018-5-25
	* @param offSet
	* @param oldSet
	* @return
	 */
	public static String buildNewDeptAndCount(String offSet, String oldSet) {
		String result = "";
		List<String> newList = java.util.Arrays.asList(offSet.split(","));
		List<String> newDeptList = new ArrayList<String>();
		Map<String, String> newMap = new HashMap<String, String>();
		List<String> oldList = java.util.Arrays.asList(oldSet.split(","));
		List<String> oldDeptList = new ArrayList<String>();
		Map<String, String> oldMap = new HashMap<String, String>();
		for(String oldStr : oldList){
			String oldDept = oldStr.substring(0, oldStr.indexOf(":"));
			String oldCount = oldStr.substring(oldStr.indexOf(":") + 1, oldStr.indexOf("份"));
			oldDeptList.add(oldDept);
			oldMap.put(oldDept, oldCount);
		}
		for(String newStr : newList){
			String newDept = newStr.substring(0, newStr.indexOf(":"));
			String newCount = newStr.substring(newStr.indexOf(":") + 1, newStr.indexOf("份"));
			newDeptList.add(newDept);
			newMap.put(newDept, newCount);
		}
		//开始拼接分发部门和份数
		List<String> allList = new ArrayList<String>();
		for(String oldDept : oldDeptList){
			String count = oldMap.get(oldDept);
			if(newDeptList.contains(oldDept)){
				String newCount = newMap.get(oldDept);
				count = String.valueOf(Integer.parseInt(count) + Integer.parseInt(newCount));
				allList.add(oldDept);
			}
			String deptAndCount = oldDept + ":" + count + "份";
			if("".equals(result)){
				result = deptAndCount;
			}else{
				result = result + "," + deptAndCount;
			}
		}
		for(String newDept : newDeptList){
			if(allList.contains(newDept)){
				continue;
			}
			String count = newMap.get(newDept);
			String deptAndCount = newDept + ":" + count + "份";
			if("".equals(result)){
				result = deptAndCount;
			}else{
				result = result + "," + deptAndCount;
			}
		}

		return result;
	}

	/**
	 * 构造档案员组名
	* @author zhuhao
	 * @param pbooid
	 * @param name
	 * @param info
	* @date 2018-5-29
	* @return
	 * @throws WTException
	 */
	public static String buildArchivists(String name, String pbooid, String info) throws WTException {
		String category = "";
		List<String> categoryList = new ArrayList<String>();
		categoryList.add("运载型号");
		categoryList.add("飞船型号");
		categoryList.add("战术型号");
		if("DYSQ".equals(name) || "JGYZ".equals(name) || "WJQF".equals(name)||
				"WJFC".equals(name)|| "YCSQ".equals(name)||"YSSQ".equals(name)||"BDSQ".equals(name)){//打印申请,加盖印章,文件启封,文件封存,延迟申请,遗失申请,补打申请
			List<String> list = PrintDataQueryUtil.queryObjId(name, pbooid, info);
			if(list != null && !list.isEmpty()){
				String docvr = list.get(0);
				if (null != docvr) {
					if("WL".equals(docvr) || "ZZ".equals(docvr)){
						Map<String, String> map = new HashMap<String, String>();
						if(info != null && !"".equals(info)){
							map = PrintDataQueryUtil.getOutFileNumberAndVersionByInfoId(info, name);
						}else{
							map = PrintDataQueryUtil.getOutFileNumberAndVersionById(pbooid, name);
						}
						category = PrintDataQueryUtil.getCategoryByOutFile(map);
					}else{
						Persistable persistable = PersistableUtil.getPersistable(docvr);
						if (persistable != null) {
							if (persistable instanceof WTDocument) {
								WTDocument document = (WTDocument) persistable;
								IBAHelper ibaHelper = new IBAHelper((IBAHolder) document.getContainer());
								category = ibaHelper.getIBAValue("XHLX");
							} else if (persistable instanceof WTChangeOrder2) {
								WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
								IBAHelper ibaHelper = new IBAHelper((IBAHolder) changeOrder.getContainer());
								category = ibaHelper.getIBAValue("XHLX");
							}
						}
					}
				}
			}
		}else if("GGDYHS".equals(name)){//更改打印回收
			category = info;
		}
		if(category == null || "".equals(category)){
			throw new WTException("型号类型为空");
		}
		if(!categoryList.contains(category)){
			category = "新品型号";
		}
		return category + "档案员组";
	}

	/**
	 * 构造部门主任组名
	* @author zhuhao
	 * @param name
	 * @param info
	* @date 2018-5-29
	* @return
	 * @throws WTException
	 */
	public static String buildDirector(String name, String info) throws WTException {
		String category = "";
		List<String> categoryList = new ArrayList<String>();
		categoryList.add("运载型号");
		categoryList.add("飞船型号");
		categoryList.add("战术型号");
		if("WJFC".equals(name)){//打印申请,加盖印章,文件启封,文件封存
			List<String> list = PrintDataQueryUtil.queryObjId(name, "", info);
			if(list != null && !list.isEmpty()){
				String docvr = list.get(0);
				if (null != docvr) {
					if("WL".equals(docvr) || "ZZ".equals(docvr)){
						Map<String, String> map = PrintDataQueryUtil.getOutFileNumberAndVersionByInfoId(info, name);
						category = PrintDataQueryUtil.getCategoryByOutFile(map);
					}else{
						Persistable persistable = PersistableUtil.getPersistable(docvr);
						if (persistable != null) {
							if (persistable instanceof WTDocument) {
								WTDocument document = (WTDocument) persistable;
								IBAHelper ibaHelper = new IBAHelper((IBAHolder) document.getContainer());
								category = ibaHelper.getIBAValue("XHLX");
							} else if (persistable instanceof WTChangeOrder2) {
								WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
								IBAHelper ibaHelper = new IBAHelper((IBAHolder) changeOrder.getContainer());
								category = ibaHelper.getIBAValue("XHLX");
							}
						}
					}
				}
			}
		}
		if(category == null || "".equals(category)){
			throw new WTException("型号类型为空");
		}
		if(!categoryList.contains(category)){
			category = "新品型号";
		}
		if("运载型号".equals(category)){
			category = "事业一部";
		}else if("战术型号".equals(category)){
			category = "事业二部";
		}else if("飞船型号".equals(category)){
			category = "事业三部";
		}else{
			category = "事业四部";
		}
		return category + "主任组";
	}

	/**
	 * 检测当前用户是否是产品库下的主任工艺师
	* @author zhuhao
	* @date 2018-5-31
	* @param containerName
	* @return
	 */
	public static boolean checkContainerRole(String containerName) {
		try {
			WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
			if (currentUser.getName().equals("Administrator")){
				return true;
			}
			WTContainer wtContainer = getContainerByName(containerName);
			ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
	    	Role role = Role.toRole("ZHURENGONGYISHI");
	    	if(role == null){
	    		return false;
	    	}
	    	List<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
	    	for (WTPrincipalReference reference : arrayList) {
	    		Object object2 = reference.getPrincipal();
	    		if (object2 instanceof WTUser) {
	    			WTUser user = (WTUser) object2;
	    			if (user.getName().equals(currentUser.getName())) {
	    				return true;
	    			}
	    		}else if (object2 instanceof WTGroup) {
	    			WTGroup group = (WTGroup) object2;
	    			if (group.isMember(currentUser)) {
	    				return true;
	    			}
	    		}
	    	}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 文档入库同步到现行库
	* @author zhuhao
	* @date 2018-6-21
	* @param listBean
	* @param category
	 */
	public static void synchDangan(List<CmPrintRecordInfoBean> listBean, String category) {
		List<String> oidList = new ArrayList<String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<CmPrintInfoBean> list2= new ArrayList<CmPrintInfoBean>();
		try {
			for(CmPrintRecordInfoBean bean : listBean){
				String barCode = bean.getBarCode();
				if("WL".equals(category) || "ZZ".equals(category)){
					Map<String, String> map = PrintDataQueryUtil.getDocNumberAndVersionByBarCode(barCode);
					if(list.contains(map)){
						continue;
					}
					list.add(map);
					CmPrintInfoBean cmPrintInfoBean = PrintDataQueryUtil.getAllInfoByBarCode(barCode);
					if(list2.contains(cmPrintInfoBean)){
						continue;
					}
					list2.add(cmPrintInfoBean);
					String file = createPrintDocXml(cmPrintInfoBean);
					CallWebServiceOME.callWebServiceUploadFile(file);
				}else{
					String docOid = PrintDataQueryUtil.getDocOidByBarCode(barCode);
					if(oidList.contains(docOid)){
						continue;
					}
					oidList.add(docOid);
					Persistable persistable  = PersistableUtil.getPersistable(docOid);
					if(persistable instanceof WTDocument){
						WTDocument doc = (WTDocument)persistable;
						String file1 = ERPUtil.createCommonDocXml(doc,"0");
		                CallWebServiceOME.callWebServiceUploadFile(file1);
					}else if(persistable instanceof WTChangeOrder2){
						WTChangeOrder2 changeOrder = (WTChangeOrder2)persistable;
						String file2 = ERPUtil.createCommonDocXml(changeOrder,"0");
	                    CallWebServiceOME.callWebServiceUploadFile(file2);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *外来文件编写XML
	* @author zhuhao
	* @date 2018-6-21
	* @param doc
	* @param pdmtype
	* @return
	* @throws Exception
	 */
	public static String createPrintDocXml(CmPrintInfoBean cmPrintInfoBean) throws Exception{
//		SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Element root=DocumentHelper.createElement("archman");

		Element prop=DocumentHelper.createElement("DocSource");
		prop.addElement("company").addText("国睿信维");
		prop.addElement("system").addText("windchill");
		prop.addElement("type").addText("现行文档");

		root.add(prop);
		String subPath = "";
		subPath = cmPrintInfoBean.getFileNumber();
		subPath = subPath.replaceAll("/", "_");
		ERPUtil.createElement(root,"内部标识","IID",cmPrintInfoBean.getFileNumber());
		ERPUtil.createElement(root,"文件夹ID","Folder_ID","2627");
		ERPUtil.createElement(root,"现行库还是预立卷","PDMTYPE", "0");
		ERPUtil.createElement(root,"文件来源","InOrOut","0");
		ERPUtil.createElement(root,"编号","BookNo",cmPrintInfoBean.getFileNumber());
		ERPUtil.createElement(root,"产品代号","productId","");
		ERPUtil.createElement(root,"产品","ProductIID","");
		ERPUtil.createElement(root,"阶段","Phase",cmPrintInfoBean.getPhaseCode());
		ERPUtil.createElement(root,"分系统","SubSystem","");
		ERPUtil.createElement(root,"名称","Name",cmPrintInfoBean.getFileName());
		ERPUtil.createElement(root,"密级","SecLev",cmPrintInfoBean.getSecret());
		ERPUtil.createElement(root,"文件类型","DocType",cmPrintInfoBean.getFileType());
		ERPUtil.createElement(root,"页数","PageCount","");
		ERPUtil.createElement(root,"份数","Count","1");
		ERPUtil.createElement(root,"入库份数","EnrollCount","1");
		ERPUtil.createElement(root,"文件简字","GenWord","");
		ERPUtil.createElement(root,"编写人","Creator","");
		ERPUtil.createElement(root,"编制单位","CreateUnit","149");
		ERPUtil.createElement(root,"编制日期","CreateDate","");
		ERPUtil.createElement(root,"是否评审","IsCheck","0");
		ERPUtil.createElement(root,"格式","Format","1");
		ERPUtil.createElement(root,"备注","Notes","");
		ERPUtil.createElement(root,"外来文原编号","FlowNo","");
		ERPUtil.createElement(root,"版本","VerId",cmPrintInfoBean.getVersion());
		ERPUtil.createElement(root,"编写人标识","CreatorIID","");
		ERPUtil.createElement(root,"接收人标识","AcceptorIID","");
		ERPUtil.createElement(root,"接收人","Acceptor","");
		ERPUtil.createElement(root,"状态","Status","");
		ERPUtil.createElement(root,"阶段内部标识","PhaseIID","");


		WTProperties wtp = WTProperties.getLocalProperties();
		String wtTemp = wtp.getProperty("wt.temp");
		String pathID = java.util.UUID.randomUUID().toString();
		String tempPath=wtTemp+File.separator+pathID;

		ERPUtil.writeXML(root,tempPath,subPath);
		String zipPath=ERPUtil.createZip(tempPath,subPath);
		return zipPath;
	}
}
