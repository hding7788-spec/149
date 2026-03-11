package com.glaway.mpm.print.util;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.constants.DocumentConstants;
import com.glaway.mpm.constants.ProcessPlanConstants;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.util.PersistableUtil;
import com.glaway.mpm.print.GWPrintApplyRecordManager;
import com.glaway.mpm.print.GWPrintConnectionQR;
import com.glaway.mpm.print.GWPrintDistributeRecordManager;
import com.glaway.mpm.print.PrintUserCodeProcessor;
import com.glaway.mpm.print.bean.CmPrintDistributerecordBean;
import com.glaway.mpm.print.bean.CmUserQrCodeBean;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.model.GWPrintApplyRecord;
import com.glaway.mpm.print.model.GWPrintDistributeRecord;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.*;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan._MPMProcessPlan;
import ext.casc.constants.Constants;
import ext.casc.doc.CSCDoc;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.CommonUtil;
import ext.casc.util.SoftTypeUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.change2.WTChangeOrder2;
import wt.change2._ChangeOrder2;
import wt.doc.WTDocument;
import wt.doc._WTDocument;
import wt.fc.*;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTCollection;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.iba.value._StringValue;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.pds.oracle81.OracleDataSource;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionServerHelper;
import wt.type.*;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.workflow.engine.InvalidDataException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import javax.xml.rpc.ServiceException;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class PrintDataQueryUtil {

    private static VaLogger logger = VaLogger.getLogger(PrintDataQueryUtil.class.getName());

    private static int index[] = {0};

    public static List<CmPrintInfoBean> queryPrintFiles(CmPrintQueryBean cmPrintQueryBean) throws WTException, WTPropertyVetoException, RemoteException {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        String fileType = cmPrintQueryBean.getFileType();
        long t1 = System.currentTimeMillis();
        if (fileType.equals(PrintServerConstants.FILETYPE_ZSGY)
                || fileType.equals(PrintServerConstants.FILETYPE_TYGY)
                || fileType.equals(PrintServerConstants.FILETYPE_YHTYGY)
                || fileType.equals(PrintServerConstants.FILETYPE_ZSTYGY)
                || fileType.equals(PrintServerConstants.FILETYPE_SLHGY)
                || fileType.equals(PrintServerConstants.FILETYPE_LSGY)) {
            list = queryMpmProcessPlanPrintFiles(cmPrintQueryBean);
        } else if (fileType.equals(PrintServerConstants.FILETYPE_GYZTB)) {
            List<CmPrintInfoBean> ppList = queryMpmProcessPlanPrintFiles(cmPrintQueryBean);
            list.addAll(ppList);
			/*List<CmPrintInfoBean> docList = queryWTDocumentPrintFiles(cmPrintQueryBean);
			list.addAll(docList);*/
        } else if (fileType.equals(PrintServerConstants.FILETYPE_GYGGD)) {
//			List<CmPrintInfoBean> ecnList = queryWTChangeOrder2PrintFiles(cmPrintQueryBean);
//			list.addAll(ecnList);

			/*List<CmPrintInfoBean> docList = queryWTDocumentPrintFiles(cmPrintQueryBean);
			list.addAll(docList);*/
        } else if (fileType.equals(PrintServerConstants.FILETYPE_GYTZD)
                || fileType.equals(PrintServerConstants.FILETYPE_GYZFA)
                || fileType.equals(PrintServerConstants.FILETYPE_QTBG)) {
//			list = queryWTDocumentPrintFiles(cmPrintQueryBean);
        } else if (fileType.equals("")) {
            if (!cmPrintQueryBean.getTechnicsType().equals("") || !cmPrintQueryBean.getCindex().equals("")) {
                List<CmPrintInfoBean> ppList = queryMpmProcessPlanPrintFiles(cmPrintQueryBean);
                list.addAll(ppList);
            } else {
                List<CmPrintInfoBean> ppList = queryMpmProcessPlanPrintFiles(cmPrintQueryBean);
                list.addAll(ppList);

//				List<CmPrintInfoBean> ecnList = queryWTChangeOrder2PrintFiles(cmPrintQueryBean);
//				list.addAll(ecnList);

				/*List<CmPrintInfoBean> docList = queryWTDocumentPrintFiles(cmPrintQueryBean);
				list.addAll(docList);*/

                long t2 = System.currentTimeMillis();
                logger.info("查询耗时：" + (t2 - t1) + "ms");
            }
        }
        return list;
    }

    /**
     * 查询文件总入口 add by zhuhao
     *
     * @param cmPrintQueryBean
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     */
    public static List<CmPrintInfoBean> queryPrintApplicationFiles(CmPrintQueryBean cmPrintQueryBean) throws WTException, WTPropertyVetoException, RemoteException {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        String fileType = cmPrintQueryBean.getFileType();
        if (fileType.equals(PrintServerConstants.FILETYPE_GYGC)) {//工艺规程
            list = queryProcessPlanPrintFiles(cmPrintQueryBean, fileType);
        } else if (fileType.equals(PrintServerConstants.FILETYPE_GYGGD)) {//工艺更改单
            List<CmPrintInfoBean> ecnList = queryWTChangeOrder2PrintFiles(cmPrintQueryBean, fileType);
            list.addAll(ecnList);
        } else if (fileType.equals(PrintServerConstants.FILETYPE_GYZFA)//工艺总方案
                || fileType.equals(PrintServerConstants.FILETYPE_FYZFA)//工艺分方案
                || fileType.equals(PrintServerConstants.FILETYPE_GYJSTZD)//工艺技术通知单
                || fileType.equals(PrintServerConstants.FILETYPE_GYJSXY)//工艺技术协议
                || fileType.equals(PrintServerConstants.FILETYPE_TYGY))//通用工艺
        {
            list = queryPrintFiles(cmPrintQueryBean, fileType);
        } else if (fileType.equals(PrintServerConstants.FILETYPE_QT)) {
            list = queryOtherFiles(cmPrintQueryBean, fileType);
        }
        return list;
    }

    private static List<CmPrintInfoBean> queryOtherFiles(CmPrintQueryBean cmPrintQueryBean, String fileType) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        try {
            QuerySpec querySpec = new QuerySpec(WTDocument.class);
            querySpec.setAdvancedQueryEnabled(true);
            //查到类型和受控文件
            TypeDefinitionReference tdr1 = TypedUtilityServiceHelper.service.getTypeDefinitionReference("casc.sast.149.QITALEIWENDANG");//其他类文档
            TypeDefinitionReference tdr2 = TypedUtilityServiceHelper.service.getTypeDefinitionReference("casc.sast.149.QA_REPORT");//质量报告
            TypeDefinitionReference tdr3 = TypedUtilityServiceHelper.service.getTypeDefinitionReference("casc.sast.149.GONGYIDINGXING");//工艺定型
            TypeDefinitionReference tdr4 = TypedUtilityServiceHelper.service.getTypeDefinitionReference("casc.sast.149.GONGYIJIANDING");//工艺鉴定
            TypeDefinitionReference tdr5 = TypedUtilityServiceHelper.service.getTypeDefinitionReference("casc.sast.149.GONGYIFENXICEHUAZONGJIE");//工艺分析策划总结
            TypeDefinitionReference tdr6 = TypedUtilityServiceHelper.service.getTypeDefinitionReference("casc.sast.149.JISHUKETI");//技术课题
            TypeDefinitionReference tdr7 = TypedUtilityServiceHelper.service.getTypeDefinitionReference("casc.sast.149.TEST_REPORT");//总体测发报告

            querySpec.appendOpenParen();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr1.getKey().getBranchId()), index);
            querySpec.appendOr();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr2.getKey().getBranchId()), index);
            querySpec.appendOr();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr3.getKey().getBranchId()), index);
            querySpec.appendOr();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr4.getKey().getBranchId()), index);
            querySpec.appendOr();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr5.getKey().getBranchId()), index);
            querySpec.appendOr();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr6.getKey().getBranchId()), index);
            querySpec.appendOr();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr7.getKey().getBranchId()), index);
            querySpec.appendCloseParen();
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);
            //根据编号查询
            if (!cmPrintQueryBean.getFileNumber().equals("")) {
                querySpec.appendAnd();
                querySpec.appendWhere(new SearchCondition(WTDocument.class, _WTDocument.NUMBER, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileNumber() + "%", false), index);
            }
            //根据名称查询
            if (!cmPrintQueryBean.getFileName().equals("")) {
                querySpec.appendAnd();
                querySpec.appendWhere(new SearchCondition(WTDocument.class, _WTDocument.NAME, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileName() + "%", false), index);
            }
            //根据版本查询
            if (!cmPrintQueryBean.getVersion().equals("")) {
                querySpec.appendAnd();
                querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", cmPrintQueryBean.getVersion()), index);
//				if(cmPrintQueryBean.getVersion().length() ==1){
//					querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", cmPrintQueryBean.getVersion()),index);
//				}
//				else{
//					String[] versionStr = cmPrintQueryBean.getVersion().split("\\.");
//					querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", versionStr[0]),index);
//					querySpec.appendAnd();
//					querySpec.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId", "=", versionStr[1]),index);
//				}
            }
            //根据软属性查询
            Map<String, String> attrMap = new HashMap<String, String>();
            //根据阶段标记
            if (!cmPrintQueryBean.getPhaseCode().equals("")) {
                attrMap.put(DocumentConstants.IBA_PHASECODE, cmPrintQueryBean.getPhaseCode());
            }
            if (attrMap.size() != 0) {
                ClassAttribute caDocId = new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
                if (caDocId != null) {
                    for (String ibaName : attrMap.keySet()) {
                        if (attrMap.get(ibaName) != null && !attrMap.get(ibaName).equals("")) {
                            querySpec.appendAnd();
                            SubSelectExpression ss = getStringIBAQuery(ibaName, attrMap.get(ibaName));
                            if (ss != null) {
                                querySpec.appendWhere(new SearchCondition(caDocId, SearchCondition.IN, ss), new int[]{0});
                            }
                        }
                    }
                }
            }

            QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
            queryResult = new LatestConfigSpec().process(queryResult);
            while (queryResult.hasMoreElements()) {
                WTDocument doc = (WTDocument) queryResult.nextElement();
                CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(doc, fileType);
                list.add(cmPrintInfoBean);
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 查询受控中工艺文件 add by zhuhao 20180131
     *
     * @param cmPrintQueryBean
     * @param fileType         类型
     * @return
     */
    private static List<CmPrintInfoBean> queryPrintFiles(CmPrintQueryBean cmPrintQueryBean, String fileType) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        try {
            String typeStr = PrintUtil.getProcessType(fileType);
            if ("".equals(typeStr) || typeStr == null) {
                return list;
            }
            QuerySpec querySpec = new QuerySpec(WTDocument.class);
            querySpec.setAdvancedQueryEnabled(true);
            //查到类型和受控文件
            TypeDefinitionReference tdr = TypedUtilityServiceHelper.service.getTypeDefinitionReference(typeStr);
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr.getKey().getBranchId()), index);
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);
            //根据编号查询
            if (!cmPrintQueryBean.getFileNumber().equals("")) {
                querySpec.appendAnd();
                querySpec.appendWhere(new SearchCondition(WTDocument.class, _WTDocument.NUMBER, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileNumber() + "%", false), index);
            }
            //根据名称查询
            if (!cmPrintQueryBean.getFileName().equals("")) {
                querySpec.appendAnd();
                querySpec.appendWhere(new SearchCondition(WTDocument.class, _WTDocument.NAME, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileName() + "%", false), index);
            }
            //根据版本查询
            if (!cmPrintQueryBean.getVersion().equals("")) {
                querySpec.appendAnd();
                querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", cmPrintQueryBean.getVersion()), index);
//				if(cmPrintQueryBean.getVersion().length() ==1){
//					querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", cmPrintQueryBean.getVersion()),index);
//				}
//				else{
//					String[] versionStr = cmPrintQueryBean.getVersion().split("\\.");
//					querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", versionStr[0]),index);
//					querySpec.appendAnd();
//					querySpec.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId", "=", versionStr[1]),index);
//				}
            }
            //根据软属性查询
            Map<String, String> attrMap = new HashMap<String, String>();
            //根据阶段标记
            if (!cmPrintQueryBean.getPhaseCode().equals("")) {
                attrMap.put(DocumentConstants.IBA_PHASECODE, cmPrintQueryBean.getPhaseCode());
            }
            if (attrMap.size() != 0) {
                ClassAttribute caDocId = new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
                if (caDocId != null) {
                    for (String ibaName : attrMap.keySet()) {
                        if (attrMap.get(ibaName) != null && !attrMap.get(ibaName).equals("")) {
                            querySpec.appendAnd();
                            SubSelectExpression ss = getStringIBAQuery(ibaName, attrMap.get(ibaName));
                            if (ss != null) {
                                querySpec.appendWhere(new SearchCondition(caDocId, SearchCondition.IN, ss), new int[]{0});
                            }
                        }
                    }
                }
            }

            QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
            queryResult = new LatestConfigSpec().process(queryResult);
            while (queryResult.hasMoreElements()) {
                WTDocument doc = (WTDocument) queryResult.nextElement();
                CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(doc, fileType);
                list.add(cmPrintInfoBean);
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * 查询工艺规程 add by zhuhao
     *
     * @param cmPrintQueryBean
     * @param fileType
     * @return
     */
    @SuppressWarnings("deprecation")
    private static List<CmPrintInfoBean> queryProcessPlanPrintFiles(CmPrintQueryBean cmPrintQueryBean, String fileType) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        try {
            ArrayList<TypeIdentifier> gywjList = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
            String[] name = new String[gywjList.size()];
            List<String> fileTypeList = new ArrayList<String>();
            String fileTypeAll = "";
            for (int i = 0; i < name.length; i++) {
                name[i] = gywjList.get(i).toString().replace("WCTYPE|", "");
                fileTypeAll = gywjList.get(i).toString().replace("WCTYPE|", "");
                fileTypeAll = fileTypeAll.replace("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|", "");
                fileTypeList.add(fileTypeAll);
            }
            long[] docTypeId = getDocTypeId(name);
            QuerySpec qs = new QuerySpec(WTDocument.class);
            qs.setAdvancedQueryEnabled(true);
			/*qs.appendWhere(new SearchCondition(WTDocument.class,	"state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);
			qs.appendAnd();
  		    qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });*/
//			int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);
//			qs.appendWhere(new SearchCondition(new ClassAttribute(WTDocument.class, "typeDefinitionReference.key.branchId"), SearchCondition.IN, new ArrayExpression(docTypeId)));
            if (fileTypeList != null && fileTypeList.size() != 0) {
//				qs.appendAnd();
                qs.appendOpenParen();
                for (int i = 0; i < fileTypeList.size(); i++) {
                    TypeDefinitionReference tdr = TypedUtilityServiceHelper.service.getTypeDefinitionReference(fileTypeList.get(i));
                    qs.appendWhere(new SearchCondition(WTDocument.class, Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr.getKey().getBranchId()), index);
                    if (i < fileTypeList.size() - 1) {
                        qs.appendOr();
                    } else {
                        qs.appendCloseParen();
                    }
                }
            }

            //根据名称
            if (!"".equals(cmPrintQueryBean.getFileName())) {
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileName() + "%", true));
            }
            //根据版本查询
			/*if(!cmPrintQueryBean.getVersion().equals("")){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", cmPrintQueryBean.getVersion()),new int[1]);
			}*/
            //根据软属性查询
            Map<String, String> attrMap = new HashMap<String, String>();
            //根据阶段标记
            if (!cmPrintQueryBean.getPhaseCode().equals("")) {
                attrMap.put(DocumentConstants.IBA_PHASECODE, cmPrintQueryBean.getPhaseCode());
            }
            if (!"".equals(cmPrintQueryBean.getFileNumber())) {
                attrMap.put(DocumentConstants.IBA_NUMBER, cmPrintQueryBean.getFileNumber());
            }
            if (attrMap.size() != 0) {
                ClassAttribute caDocId = new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
                if (caDocId != null) {
                    for (String ibaName : attrMap.keySet()) {
                        if (attrMap.get(ibaName) != null && !attrMap.get(ibaName).equals("")) {
                            qs.appendAnd();
                            SubSelectExpression ss = getStringIBAQuery(ibaName, attrMap.get(ibaName));
                            if (ss != null) {
                                qs.appendWhere(new SearchCondition(caDocId, SearchCondition.IN, ss), new int[]{0});
                            }
                        }
                    }
                }
            }
//			qs = new LatestConfigSpec().appendSearchCriteria(qs);
//			QueryResult qr = PersistenceHelper.manager.find(qs);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
            qr = new LatestConfigSpec().process(qr);
            while (qr.hasMoreElements()) {
//				Object[] obj = (Object[]) qr.nextElement();
                boolean hasFound = false;
                WTDocument doc = (WTDocument) qr.nextElement();
                String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
                if (docType.contains("casc.sast.149.reportTechnics")) {
                    QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
                    while (qr2.hasMoreElements()) {
                        WTDocument docVersion = (WTDocument) qr2.nextElement();
                        WTDocument latestDoc = (WTDocument) VersionControlHelper.service.getLatestIteration(docVersion, false);
                        String state = latestDoc.getState().getState().getDisplay(Locale.CHINA);
                        if (Constants.STATE_YIPIZHUN.equals(state)) {
                            String sver = latestDoc.getVersionIdentifier().getValue().toString();
                            if ("space".equals(sver)) {
                                QueryResult qrProcs = WfEngineHelper.service
                                        .getAssociatedProcesses(latestDoc, null, null);
                                if (qrProcs.hasMoreElements()) {
                                    WfProcess proc = (WfProcess) qrProcs.nextElement();
                                    if (proc.getTemplate().getName().equals(Constants.WFN_WUJIREPORTPROCESSWF)) {
                                        CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, fileType);
                                        list.add(cmPrintInfoBean);
                                        hasFound = true;
                                    }
                                }
                            } else {
                                WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(latestDoc);
                                Iterator it = coll.iterator();
                                if (it.hasNext()) {
                                    CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, fileType);
                                    list.add(cmPrintInfoBean);
                                    hasFound = true;
                                }
                            }

                        }
                        if (hasFound) {
                            break;
                        }
                    }
                } else {//非报表类工艺
                    String pplanType = IBAHelper.getIBAValue(doc, "PPLANTYPE");
                    if ("正式工艺文件".equals(pplanType)) {
                        QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
                        while (qr2.hasMoreElements()) {
                            WTDocument docVersion = (WTDocument) qr2.nextElement();
                            WTDocument latestDoc = (WTDocument) VersionControlHelper.service.getLatestIteration(docVersion, false);
                            String state = latestDoc.getState().getState().getDisplay(Locale.CHINA);
                            if (Constants.STATE_YIPIZHUN.equals(state)) {
                                String sver = latestDoc.getVersionIdentifier().getValue().toString();
                                if ("space".equals(sver)) {
                                    QueryResult qrProcs = WfEngineHelper.service
                                            .getAssociatedProcesses(latestDoc, null, null);
                                    if (qrProcs.hasMoreElements()) {
                                        WfProcess proc = (WfProcess) qrProcs.nextElement();
                                        if (proc.getTemplate().getName().equals(Constants.WFN_WUJIPROCESSWF)) {
                                            CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, fileType);
                                            list.add(cmPrintInfoBean);
                                            hasFound = true;
                                        }
                                    }
                                } else {
                                    WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(latestDoc);
                                    Iterator it = coll.iterator();
                                    if (it.hasNext()) {
                                        CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, fileType);
                                        list.add(cmPrintInfoBean);
                                        hasFound = true;
                                    }
                                }

                            }
                            if (hasFound) {
                                break;
                            }

                        }
                    }else if("临时工艺文件".equals(pplanType)){
                        QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
                        while (qr2.hasMoreElements()) {
                            WTDocument docVersion = (WTDocument) qr2.nextElement();
                            WTDocument latestDoc = (WTDocument) VersionControlHelper.service.getLatestIteration(docVersion, false);
                            String state = latestDoc.getState().getState().getDisplay(Locale.CHINA);
                            if (Constants.STATE_YIPIZHUN.equals(state)) {
                                CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, fileType);
                                list.add(cmPrintInfoBean);
                                hasFound = true;

                            }
                            if (hasFound) {
                                break;
                            }

                        }
                    }
                }
            }
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 基于BOM查询文件 add by zhuhao
     *
     * @param cmPrintQueryBean
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     */
    public static List<CmPrintInfoBean> queryFilesOnBom(CmPrintQueryBean cmPrintQueryBean) throws WTException, WTPropertyVetoException, RemoteException {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        String fileNumber = cmPrintQueryBean.getPartNumber();
        String fileName = cmPrintQueryBean.getPartName();
        boolean isnum = true;
        boolean isname = true;
        if (fileNumber == null || "".equals(fileNumber)) {
            isnum = false;
        }
        if (fileName == null || "".equals(fileName)) {
            isname = false;
        }
        if (isnum || isname) {
            QueryResult qr = searchAllIteratedByNumber(fileNumber, fileName);
            ReferenceFactory rf = new ReferenceFactory();
            while (qr.hasMoreElements()) {
                CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
                WTPart part = (WTPart) qr.nextElement();
                List<WTDocument> Docs = null;
				try {
					Docs = getAllWTDocumentByAllSameVersionViewPart(part);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
                String mainProcessFile = "";
                for (WTDocument doc : Docs) {
                    String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
                    if (docType.contains("casc.sast.149.reportTechnics")) {
                        QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
                        while (qr2.hasMoreElements()) {
                            WTDocument docVersion = (WTDocument) qr2.nextElement();
                            WTDocument latestDoc = (WTDocument) VersionControlHelper.service.getLatestIteration(docVersion, false);
                            if(latestDoc != null){
                            	String name = latestDoc.getName();
                            	if(name.contains("工艺文件目录")){
                            		mainProcessFile = name;
                            	}
                            }
                        }
                    }
                }
                String veroid = rf.getReference(part).toString();
                String number = part.getNumber();
                String name = part.getName();
                String version = part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue();
                String phaseCode = IBAHelper.getIBAValue(part, "PHASE_CODE");
                cmPrintInfoBean.setOid(veroid);
                cmPrintInfoBean.setFileNumber(number);
                cmPrintInfoBean.setFileName(name);
                cmPrintInfoBean.setVersion(version);
                cmPrintInfoBean.setPhaseCode(phaseCode);
                cmPrintInfoBean.setDocVR(veroid);
                cmPrintInfoBean.setMainTechnics(mainProcessFile);
                list.add(cmPrintInfoBean);
            }
        }
        return list;
    }

    @SuppressWarnings("deprecation")
    public static QueryResult searchAllIteratedByNumber(String number, String name) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "%" + number + "%"), new int[0]);
            if (name != null && !"".equals(name)) {
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, "%" + name + "%"), new int[0]);
            }
            View view = ViewHelper.service.getView("Manufacturing");
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[0]);
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            return qr;
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

    public static List<String> getAllChildPartOid(CmPrintInfoBean cmPrintInfoBean) throws Exception {
    	List<String> list = new ArrayList<String>();
    	String partNumber = cmPrintInfoBean.getFileNumber();
		WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber,"Manufacturing");
		if (part == null) {
		    return list;
		}
		//获取bom中所有的零件
		List<WTPart> allPart = new ArrayList<WTPart>();
		allPart.add(part);
		DownloadTechnicsReportUtil.getAllChildPart(part, allPart);
		//获取子零件中的工艺文件
		for (WTPart childPart : allPart) {
			list.add(String.valueOf(childPart.getPersistInfo().getObjectIdentifier().getId()));
		}
		logger.debug("list-size="+list.size());
    	return list;
    }
    
	public static List<CmPrintInfoBean> queryBomFilesByPart(String partOid, String mainTechnics) throws Exception {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		WTPart part = WTPartUtil.getPartByOid(Long.valueOf(partOid));
		String partType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(part);
		ArrayList<WTDocument> docList = null;
		if (partType.contains(SopConstants.SOP_TYPE_SOPPART)) {
			boolean exist = false;
			docList = getDoc(part, SopConstants.SOP_TYPE_SOPDOC, com.glaway.mpm.constants.Constants.planning);
			for (WTDocument doc : docList) {
				QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
				while (qr2.hasMoreElements()) {
					WTDocument latestDoc = (WTDocument) qr2.nextElement();
					String state = latestDoc.getState().getState().getDisplay(Locale.CHINA);
					if (Constants.STATE_YIPIZHUN.equals(state)) {
						String sver = latestDoc.getVersionIdentifier().getValue().toString();
						if ("space".equals(sver)) {
							QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(latestDoc, null, null);
							if (qrProcs.hasMoreElements()) {
								WfProcess proc = (WfProcess) qrProcs.nextElement();
								if (proc.getTemplate().getName().equals(SopConstants.SOP_WORKFLOW_SOPPROCESS)) {
									CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_SOPGYGC);
									cmPrintInfoBean2.setMainTechnics(mainTechnics);
									list.add(cmPrintInfoBean2);
									exist = true;
								}
							}
						} else {
							CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_SOPGYGC);
							cmPrintInfoBean2.setMainTechnics(mainTechnics);
							list.add(cmPrintInfoBean2);
							exist = true;
						}
					}
					if (exist) {
						break;
					}
				}
			}
			return list;
		} else {
			boolean hasFound = false;
			docList = getDoc(part);
			for (WTDocument doc : docList) {
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
				if (docType.contains("casc.sast.149.reportTechnics")) {
					QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
					while (qr2.hasMoreElements()) {
						WTDocument latestDoc = (WTDocument) qr2.nextElement();
						String state = latestDoc.getState().getState().getDisplay(Locale.CHINA);
						if (Constants.STATE_YIPIZHUN.equals(state)) {
							String sver = latestDoc.getVersionIdentifier().getValue().toString();
							if ("space".equals(sver)) {
								QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(latestDoc, null, null);
								if (qrProcs.hasMoreElements()) {
									WfProcess proc = (WfProcess) qrProcs.nextElement();
									if (proc.getTemplate().getName().equals(Constants.WFN_WUJIREPORTPROCESSWF)) {
										CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
										cmPrintInfoBean2.setMainTechnics(mainTechnics);
										list.add(cmPrintInfoBean2);
										hasFound = true;
									}
								}
							} else {
								CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
								cmPrintInfoBean2.setMainTechnics(mainTechnics);
								list.add(cmPrintInfoBean2);
								hasFound = true;
							}
						}
						if (hasFound) {
							break;
						}
					}
				} else {// 非报表类工艺
					String pplanType = IBAHelper.getIBAValue(doc, "PPLANTYPE");
					if ("正式工艺文件".equals(pplanType)) {
						QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
						while (qr2.hasMoreElements()) {
							WTDocument latestDoc = (WTDocument) qr2.nextElement();
							String state = latestDoc.getState().getState().getDisplay(Locale.CHINA);
							if (Constants.STATE_YIPIZHUN.equals(state)) {
								String sver = latestDoc.getVersionIdentifier().getValue().toString();
								if ("space".equals(sver)) {
									QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(latestDoc, null, null);
									if (qrProcs.hasMoreElements()) {
										WfProcess proc = (WfProcess) qrProcs.nextElement();
										if (proc.getTemplate().getName().equals(Constants.WFN_WUJIPROCESSWF)) {
											CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
											cmPrintInfoBean2.setMainTechnics(mainTechnics);
											list.add(cmPrintInfoBean2);
											hasFound = true;
										}
									} else {
										WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(latestDoc);
										Iterator<?> it = coll.iterator();
										if (it.hasNext()) {
											CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
											cmPrintInfoBean2.setMainTechnics(mainTechnics);
											list.add(cmPrintInfoBean2);
											hasFound = true;
										}
									}
								} else {
									CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
									cmPrintInfoBean2.setMainTechnics(mainTechnics);
									list.add(cmPrintInfoBean2);
									hasFound = true;
								}
							}
							if (hasFound) {
								break;
							}
						}
					}
				}
			}
			logger.debug("list-size=" + list.size());
			return list;
		}
	}
    
    /**
     * 通过零件查询工艺文件 add by zhuhao
     *
     * @param cmPrintInfoBean
     * @return
     */
    public static List<CmPrintInfoBean> queryBomFiles(CmPrintInfoBean cmPrintInfoBean) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        String partNumber = cmPrintInfoBean.getFileNumber();
        String mainTechnics = cmPrintInfoBean.getMainTechnics();
        try {
            WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber,"Manufacturing");
            if (part == null) {
                return list;
            }
            //获取bom中所有的零件
            List<WTPart> allPart = new ArrayList<WTPart>();
            allPart.add(part);
            DownloadTechnicsReportUtil.getAllChildPart(part, allPart);
            //获取子零件中的工艺文件
            for (WTPart childPart : allPart) {
                boolean hasFound = false;
                ArrayList<WTDocument> docList = getDoc(childPart);
                for (WTDocument doc : docList) {
                    String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
                    if (docType.contains("casc.sast.149.reportTechnics")) {
                        QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
                        while (qr2.hasMoreElements()) {
                            WTDocument docVersion = (WTDocument) qr2.nextElement();
                            WTDocument latestDoc = (WTDocument) VersionControlHelper.service.getLatestIteration(docVersion, false);
                            String state = latestDoc.getState().getState().getDisplay(Locale.CHINA);
                            if (Constants.STATE_YIPIZHUN.equals(state)) {
                                String sver = latestDoc.getVersionIdentifier().getValue().toString();
                                if ("space".equals(sver)) {
                                    QueryResult qrProcs = WfEngineHelper.service
                                            .getAssociatedProcesses(latestDoc, null, null);
                                    if (qrProcs.hasMoreElements()) {
                                        WfProcess proc = (WfProcess) qrProcs.nextElement();
                                        if (proc.getTemplate().getName().equals(Constants.WFN_WUJIREPORTPROCESSWF)) {
                                            CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
                                            cmPrintInfoBean2.setMainTechnics(mainTechnics);
                                            list.add(cmPrintInfoBean2);
                                            hasFound = true;
                                        }
                                    }
                                } else {
//                                    WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(latestDoc);
//                                    Iterator it = coll.iterator();
//                                    if (it.hasNext()) {
                                        CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
                                        cmPrintInfoBean2.setMainTechnics(mainTechnics);
                                        list.add(cmPrintInfoBean2);
                                        hasFound = true;
//                                    }
                                }

                            }
                            if (hasFound) {
                                break;
                            }
                        }
                    } else {//非报表类工艺
                        String pplanType = IBAHelper.getIBAValue(doc, "PPLANTYPE");
                        if ("正式工艺文件".equals(pplanType)) {
                            QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
                            while (qr2.hasMoreElements()) {
                                WTDocument docVersion = (WTDocument) qr2.nextElement();
                                WTDocument latestDoc = (WTDocument) VersionControlHelper.service.getLatestIteration(docVersion, false);
                                String state = latestDoc.getState().getState().getDisplay(Locale.CHINA);
                                if (Constants.STATE_YIPIZHUN.equals(state)) {
                                    String sver = latestDoc.getVersionIdentifier().getValue().toString();
                                    if ("space".equals(sver)) {
                                        QueryResult qrProcs = WfEngineHelper.service
                                                .getAssociatedProcesses(latestDoc, null, null);
                                        if (qrProcs.hasMoreElements()) {
                                            WfProcess proc = (WfProcess) qrProcs.nextElement();
                                            if (proc.getTemplate().getName().equals(Constants.WFN_WUJIPROCESSWF)) {
                                                CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
                                                cmPrintInfoBean2.setMainTechnics(mainTechnics);
                                                list.add(cmPrintInfoBean2);
                                                hasFound = true;
                                            }
                                        }else{
                                        	 WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(latestDoc);
                                             Iterator it = coll.iterator();
                                             if (it.hasNext()) {
                                                 CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
                                                 cmPrintInfoBean2.setMainTechnics(mainTechnics);
                                                 list.add(cmPrintInfoBean2);
                                                 hasFound = true;
                                             }
                                        }
                                    } else {
//                                        WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(latestDoc);
//                                        Iterator it = coll.iterator();
//                                        if (it.hasNext()) {
                                            CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(latestDoc, PrintServerConstants.FILETYPE_GYGC);
                                            cmPrintInfoBean2.setMainTechnics(mainTechnics);
                                            list.add(cmPrintInfoBean2);
                                            hasFound = true;
//                                        }
                                    }

                                }
                                if (hasFound) {
                                    break;
                                }

                            }
                        }
                    }
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return list;
    }


	public static long[] getDocTypeId(String[] name) throws RemoteException, WTException {
        long[] value = new long[name.length];
        for (int i = 0; i < name.length; i++) {
            TypeDefinitionReference gywj = ClientTypedUtility.getTypeDefinitionReference(name[i]);
            long id = 0;
            if (gywj != null) {
                id = gywj.getKey().getBranchId();
                value[i] = id;
            }
        }

        return value;

    }

    public static ArrayList<WTDocument> getDoc(WTPart part) {
        ArrayList<WTDocument> list = new ArrayList<WTDocument>();
        try {
            List<WTDocument> document = getAllWTDocumentByAllSameVersionViewPart(part);
            if (document != null && !document.isEmpty()) {
                for (WTDocument doc : document) {
                    String docState = doc.getState().getState().getDisplay(Locale.CHINA);
                    if ("已批准".equals(docState)) {
                        list.add(doc);
                    }
                }

            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<WTDocument> getAllWTDocumentByAllSameVersionViewPart(WTPart part) throws WTException, PropertyVetoException, DocumentException {
        QueryResult qr = ProcessPlanHelper.searchAllIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
        List<WTDocument> docList = new ArrayList<WTDocument>();
        while (qr.hasMoreElements()) {
            WTPart newpart = (WTPart) qr.nextElement();
            QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qr2 = lcs.process(qr2);
            while (qr2.hasMoreElements()) {
                WTDocument document = (WTDocument) qr2.nextElement();
                String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
                if (typeName.contains("casc.sast.149.PROCESS_PLAN")) {
                    document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
                    if (docList.isEmpty() || !WTPartUtil.checkNumber(docList, document.getNumber())) {
                        docList.add(document);
                    }
                }
            }
        }
        return docList;
    }
    
    /**
         * 查询部件指定视图、大版本下所有小版本部件包含的指定类型的已批准文档(去重复) -- add by hz 2020/1/9
     * @param part
     * @param docType
     * @param viewName
     * @return
     */
    public static ArrayList<WTDocument> getDoc(WTPart part, String docType, String viewName) {
        ArrayList<WTDocument> list = new ArrayList<WTDocument>();
        try {
            List<WTDocument> document = getTypeDocByVersionViewPart(part, docType, viewName);
            if (document != null && !document.isEmpty()) {
                for (WTDocument doc : document) {
                    String docState = doc.getState().getState().getDisplay(Locale.CHINA);
                    if ("已批准".equals(docState)) {
                        list.add(doc);
                    }
                }

            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
         *  查询部件指定视图、大版本下所有小版本部件包含的指定类型的文档 -- add by hz 2020/1/9
     * @param part
     * @param docType
     * @param viewName
     * @return
     * @throws WTException
     * @throws PropertyVetoException
     * @throws DocumentException
     */
	public static List<WTDocument> getTypeDocByVersionViewPart(WTPart part, String docType, String viewName) throws WTException, PropertyVetoException, DocumentException {
		QueryResult qr = ProcessPlanHelper.searchAllIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), viewName);
		List<WTDocument> docList = new ArrayList<WTDocument>();
		while (qr.hasMoreElements()) {
			WTPart newpart = (WTPart) qr.nextElement();
			QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr2 = lcs.process(qr2);
			while (qr2.hasMoreElements()) {
				WTDocument document = (WTDocument) qr2.nextElement();
				String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
				if (typeName.contains(docType)) {
					document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
					if (docList.isEmpty() || !WTPartUtil.checkNumber(docList, document.getNumber())) {
						docList.add(document);
					}
				}
			}
		}
		return docList;
	}

    public static List<CmPrintInfoBean> printFilesMgt(CmPrintQueryBean cmPrintQueryBean, boolean isZxdy) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        List<GWPrintApplyRecord> gwPrintApplyRecords = GWPrintApplyRecordManager.queryGWPrintApplyRecord(cmPrintQueryBean);
        for (GWPrintApplyRecord gwPrintApplyRecord : gwPrintApplyRecords) {
            String processOid = gwPrintApplyRecord.getProcessOid();
            String processNumber = gwPrintApplyRecord.getProcessNumber();
            String processName = gwPrintApplyRecord.getProcessName();
            String version = gwPrintApplyRecord.getVersion();
            String phaseCode = gwPrintApplyRecord.getPhaseCode();
            String fileType = gwPrintApplyRecord.getFileType();
            String secret = gwPrintApplyRecord.getSecret();
            String pindex = gwPrintApplyRecord.getPindex();
            String tsBaseline = gwPrintApplyRecord.getTs_Baseline();
            String page = gwPrintApplyRecord.getPage();
            String isBlueCard = gwPrintApplyRecord.getIsBlueCard();
            String ecnNumber = gwPrintApplyRecord.getEcnNumber();
            String printRequire = gwPrintApplyRecord.getPrintRequire();
            String barCode = gwPrintApplyRecord.getBarCode();
            long applier = gwPrintApplyRecord.getApplier();
            Date applyDate = gwPrintApplyRecord.getApplyDate();
            long printor = gwPrintApplyRecord.getPrintor();
            Date printDate = gwPrintApplyRecord.getPrintDate();
            String printStatus = gwPrintApplyRecord.getPrintStatus();
            String rejectStatus = gwPrintApplyRecord.getRejectStatus();

            CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
            cmPrintInfoBean.setOid(processOid);
            cmPrintInfoBean.setFileNumber(processNumber);
            cmPrintInfoBean.setFileName(processName);
            cmPrintInfoBean.setVersion(version);
            cmPrintInfoBean.setPhaseCode(phaseCode);
            cmPrintInfoBean.setFileType(fileType);
            cmPrintInfoBean.setSecret(secret);
            cmPrintInfoBean.setPindex(pindex);
            cmPrintInfoBean.setBaseline(tsBaseline);
            cmPrintInfoBean.setPageCount(page);
            cmPrintInfoBean.setBlueCard(Boolean.parseBoolean(isBlueCard));
            cmPrintInfoBean.setEcnNumber(ecnNumber);
            cmPrintInfoBean.setPrintDescription(printRequire);
            cmPrintInfoBean.setQrCode(barCode);
            cmPrintInfoBean.setPrintState(printStatus);
            cmPrintInfoBean.setRejectState(rejectStatus);
            WTUser applierUser = (WTUser) PersistableUtil.getPersistable(PrintServerConstants.OID_WTUSER + applier);
            if (applierUser != null) {
                cmPrintInfoBean.setApplier(applierUser.getFullName());
            }

            WTUser printorUser = (WTUser) PersistableUtil.getPersistable(PrintServerConstants.OID_WTUSER + printor);
            if (printorUser != null) {
                cmPrintInfoBean.setPrinter(printorUser.getFullName());
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

            if (applyDate != null) {
                cmPrintInfoBean.setApplyDate(dateFormat.format(applyDate));
            }

            if (printDate != null) {
                cmPrintInfoBean.setPrintDate(dateFormat.format(printDate));
            }

            //获取分发记录
            List<GWPrintDistributeRecord> gwPrintDistributeRecords = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCode(barCode);
            if (!isZxdy) {
                if (gwPrintDistributeRecords.size() != 0) {
                    String distributeDeptAndQuanity = GWPrintDistributeRecordManager.getDistributeDeptAndQuanity(gwPrintDistributeRecords);
                    cmPrintInfoBean.setDistributeDeptAndCount(distributeDeptAndQuanity);
                    list.add(cmPrintInfoBean);
                }
            } else {
                if (gwPrintDistributeRecords.size() == 0) {
                    list.add(cmPrintInfoBean);
                }
            }
        }
        return list;
    }

    public static List<CmPrintInfoBean> queryMpmProcessPlanPrintFiles(CmPrintQueryBean cmPrintQueryBean) throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, Object> attributeMap = new HashMap<String, Object>();
        boolean isZxdy = false;

        //工艺文件编号
        if (!cmPrintQueryBean.getFileNumber().equals("")) {
            attributeMap.put(ProcessPlanConstants.MBA_PROCESSNUMBER, cmPrintQueryBean.getFileNumber());
        }

        //工艺类型
        if (!cmPrintQueryBean.getTechnicsType().equals("")) {
            attributeMap.put(ProcessPlanConstants.MBA_PROCESSTYPE, cmPrintQueryBean.getTechnicsType());
        }

        //文件类型
        if (!cmPrintQueryBean.getFileType().equals("")) {
            attributeMap.put(ProcessPlanConstants.MBA_PROCESSCATEGORY, cmPrintQueryBean.getFileType());
        }

        //阶段标记
        if (!cmPrintQueryBean.getPhaseCode().equals("")) {
            attributeMap.put(ProcessPlanConstants.MBA_PHASECODE, cmPrintQueryBean.getPhaseCode());
        }

        //产品代号
        if (!cmPrintQueryBean.getPindex().equals("")) {
            attributeMap.put(ProcessPlanConstants.MBA_PINDEX, cmPrintQueryBean.getPindex());
        }

        //零部件编号
        if (!cmPrintQueryBean.getPartNumber().equals("")) {
            attributeMap.put(ProcessPlanConstants.MBA_PARTNUMBER, cmPrintQueryBean.getPartNumber());
        }

        //零件图号
        if (!cmPrintQueryBean.getCindex().equals("")) {
            attributeMap.put(ProcessPlanConstants.MBA_CINDEX, cmPrintQueryBean.getCindex());
        }

        QuerySpec querySpec = new QuerySpec(MPMProcessPlan.class);
        querySpec.setAdvancedQueryEnabled(true);

        //根据名称查询
        querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, _MPMProcessPlan.NAME, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileName() + "%", false), index);

        //根据生命周期状态查询
        if (!cmPrintQueryBean.getLifeCycleState().equals("")) {
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, "state.state", SearchCondition.EQUAL, cmPrintQueryBean.getLifeCycleState()), index);
        } else {
            isZxdy = true;
        }

        //根据版本查询
        if (!cmPrintQueryBean.getVersion().equals("")) {
            querySpec.appendAnd();
            if (cmPrintQueryBean.getVersion().length() == 1) {
                querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, "versionInfo.identifier.versionId", "=", cmPrintQueryBean.getVersion()), index);
            } else {
                String[] versionStr = cmPrintQueryBean.getVersion().split("\\.");
                querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, "versionInfo.identifier.versionId", "=", versionStr[0]), index);
                querySpec.appendAnd();
                querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, "iterationInfo.identifier.iterationId", "=", versionStr[1]), index);
            }
        }

        //修改者
        if (!cmPrintQueryBean.getModifior().equals("")) {
            List<WTUser> userList = UserUtil.getWTUsersByName(cmPrintQueryBean.getModifior());
            if (userList != null && userList.size() > 0) {
                querySpec.appendAnd();
                querySpec.appendOpenParen();
                for (int i = 0; i < userList.size(); i++) {
                    querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL, userList.get(i).getPersistInfo().getObjectIdentifier().getId()), index);
                    if (i < userList.size() - 1) {
                        querySpec.appendOr();
                    } else {
                        querySpec.appendCloseParen();
                    }
                }
            }

        }

        //型号
        if (!cmPrintQueryBean.getMindex().equals("")) {
            WTContainer container = WTContainerUtil.getContainerByMindex(cmPrintQueryBean.getMindex());
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(MPMProcessPlan.class, "containerReference.key.id", SearchCondition.EQUAL, container.getPersistInfo().getObjectIdentifier().getId()), index);
        }

        //根据标准属性查询
        if (attributeMap.size() != 0) {
            MPMResourceUtil.querySAttributeValue(MPMProcessPlan.class, querySpec, attributeMap);
        }

        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
        queryResult = new LatestConfigSpec().process(queryResult);
        while (queryResult.hasMoreElements()) {
            MPMProcessPlan processPlan = (MPMProcessPlan) queryResult.nextElement();

            String processNumber = CommonUtil.objectToString(MBAUtil.getValue(processPlan, ProcessPlanConstants.MBA_PROCESSNUMBER));
            if (processNumber.endsWith("(无效)"))
                continue;

            CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(processPlan, null);
            if (cmPrintInfoBean.getFileType() != null && cmPrintInfoBean.getFileType().length() > 0) {
                if (isZxdy) {
                    list.add(cmPrintInfoBean);
                } else {
                    if (!cmPrintInfoBean.getFileType().equals(PrintServerConstants.FILETYPE_LSGY)
                            && !cmPrintInfoBean.getFileType().equals(PrintServerConstants.FILETYPE_DXGY)) {
                        wjdysqAddQuery(cmPrintInfoBean, list, cmPrintQueryBean.getFileState());
                    }
                }
            }
        }
        return list;
    }

    /**
     * 查询是否发起打印申请及分发情况
     *
     * @param cmPrintInfoBean
     * @param list
     */
    public static void wjdysqAddQuery(CmPrintInfoBean cmPrintInfoBean, List<CmPrintInfoBean> list, String fileState) {
        try {
            List<GWPrintApplyRecord> gwPrintApplyRecords = GWPrintApplyRecordManager.queryGWPrintApplyRecordByProcessOid(cmPrintInfoBean.getOid(), fileState);
            if (gwPrintApplyRecords != null && !gwPrintApplyRecords.isEmpty()) {
                for (GWPrintApplyRecord gwPrintApplyRecord : gwPrintApplyRecords) {
                    List<GWPrintDistributeRecord> gwPrintDistributeRecords = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCode(gwPrintApplyRecord.getBarCode());
                    String deptAndQuanity = GWPrintDistributeRecordManager.getDistributeDeptAndQuanity(gwPrintDistributeRecords);
                    CmPrintInfoBean printInfoBean = new CmPrintInfoBean();
                    printInfoBean = (CmPrintInfoBean) BeanUtils.cloneBean(cmPrintInfoBean);
                    printInfoBean.setFileState(gwPrintApplyRecord.getPrintStatus());
                    printInfoBean.setDistributeDeptAndCount(deptAndQuanity);
                    list.add(printInfoBean);
                }
            } else {
                if (fileState.length() == 0) {
                    list.add(cmPrintInfoBean);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<CmPrintInfoBean> queryWTDocumentPrintFiles(CmPrintQueryBean cmPrintQueryBean, String fileType) throws WTException, WTPropertyVetoException, RemoteException {
        QuerySpec querySpec = new QuerySpec(WTDocument.class);
        querySpec.setAdvancedQueryEnabled(true);
//		boolean isZxdy = false;
        //根据生命周期状态查询
        querySpec.appendWhere(new SearchCondition(WTDocument.class, _WTDocument.NAME, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileName() + "%", false), index);

        //-------*2017.02.15*-------//
        String filetype = cmPrintQueryBean.getFileType();
//		List<String> fileList = new ArrayList<String>();
        //根据类型查询
        if (filetype != null && !filetype.equals("")) {
            String typeStr = "casc.sast.149.PROCESS_NOTICE";
//			if(filetype.equals(PrintServerConstants.FILETYPE_GYZFA)){
//				typeStr = "casc.sast.800.PROCESS_PROGRAM";
//			}else if(filetype.equals(PrintServerConstants.FILETYPE_GYZTB)){
//				typeStr = "casc.sast.800.PROCESSSTATUSTABLE";
//			}else if(filetype.equals(PrintServerConstants.FILETYPE_GYGGD)){
//				typeStr = "casc.sast.800.PROCESS_EC_NOTICE";
//			}
//			else if(filetype.equals(PrintServerConstants.FILETYPE_QTBG)){
//				//读取其它报告的配置文件
//				String[] fileTypes = LoadPropertiesConfig.getInstance(7).getQTBGClassNameValue(filetype);
//				for (String fileTypeString : fileTypes) {
//					fileList.add(fileTypeString);
//				}
//			}
			/*String typeHead = WCTypeIdentifier.PROTOCOL + AbstractHierarchicalIdentifier.HIERARCHY_SEPARATOR;
			if(typeStr.startsWith(typeHead)){
				typeStr = typeStr.substring(typeHead.length());
			}
			TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(typeStr);*/
//			if(fileList!=null&&fileList.size()!=0){
//				querySpec.appendAnd();
//				querySpec.appendOpenParen();
//				for (int i = 0; i < fileList.size(); i++) {
//					TypeDefinitionReference tdr = TypedUtilityServiceHelper.service.getTypeDefinitionReference(fileList.get(i));
//					querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr.getKey().getBranchId()), index);
//					if (i < fileList.size() - 1) {
//						querySpec.appendOr();
//					} else {
//						querySpec.appendCloseParen();
//					}
//				}
//			}else{
            TypeDefinitionReference tdr = TypedUtilityServiceHelper.service.getTypeDefinitionReference(typeStr);
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr.getKey().getBranchId()), index);
        }
//		}

        //根据编号查询
        if (!cmPrintQueryBean.getFileNumber().equals("")) {
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _WTDocument.NUMBER, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileNumber() + "%", false), index);
        }

        //根据生命周期状态查询
        if (cmPrintQueryBean.getLifeCycleState() != null && !cmPrintQueryBean.getLifeCycleState().equals("")) {
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);
        }
//		else {
//			isZxdy = true;
//		}

        //修改者
        if (!cmPrintQueryBean.getModifior().equals("")) {
            List<WTUser> userList = UserUtil.getWTUsersByName(cmPrintQueryBean.getModifior());
            if (userList != null && userList.size() > 0) {
                querySpec.appendAnd();
                querySpec.appendOpenParen();
                for (int i = 0; i < userList.size(); i++) {
                    querySpec.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL, userList.get(i).getPersistInfo().getObjectIdentifier().getId()), index);
                    if (i < userList.size() - 1) {
                        querySpec.appendOr();
                    } else {
                        querySpec.appendCloseParen();
                    }
                }
            }

        }

        //根据版本查询
        if (!cmPrintQueryBean.getVersion().equals("")) {
            querySpec.appendAnd();
            if (cmPrintQueryBean.getVersion().length() == 1) {
                querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", cmPrintQueryBean.getVersion()), index);
            } else {
                String[] versionStr = cmPrintQueryBean.getVersion().split("\\.");
                querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", versionStr[0]), index);
                querySpec.appendAnd();
                querySpec.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.identifier.iterationId", "=", versionStr[1]), index);
            }
        }

        Map<String, String> attrMap = new HashMap<String, String>();
        //阶段标记
        if (!cmPrintQueryBean.getPhaseCode().equals("")) {
            attrMap.put(DocumentConstants.IBA_PHASECODE, cmPrintQueryBean.getPhaseCode());
        }
        //产品代号
        if (!cmPrintQueryBean.getPindex().equals("")) {
            attrMap.put(DocumentConstants.IBA_PINDEX, cmPrintQueryBean.getPindex());
        }
        //零件图号
        if (!cmPrintQueryBean.getCindex().equals("")) {
            attrMap.put(DocumentConstants.IBA_CINDEX, cmPrintQueryBean.getCindex());
        }
        //零部件编号
        if (!cmPrintQueryBean.getPartNumber().equals("")) {
            attrMap.put(DocumentConstants.IBA_PARTNUMBER, cmPrintQueryBean.getPartNumber());
        }
        //型号
        if (!cmPrintQueryBean.getMindex().equals("")) {
            attrMap.put(DocumentConstants.IBA_MINDEX, cmPrintQueryBean.getMindex());
        }
        //根据软属性查询
        if (attrMap.size() != 0) {
            ClassAttribute caDocId = new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
            if (caDocId != null) {
                for (String ibaName : attrMap.keySet()) {
                    if (attrMap.get(ibaName) != null && !attrMap.get(ibaName).equals("")) {
                        querySpec.appendAnd();
                        SubSelectExpression ss = getStringIBAQuery(ibaName, attrMap.get(ibaName));
                        if (ss != null) {
                            querySpec.appendWhere(new SearchCondition(caDocId, SearchCondition.IN, ss), new int[]{0});
                        }
                    }
                }
            }
        }

        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
        queryResult = new LatestConfigSpec().process(queryResult);
        while (queryResult.hasMoreElements()) {
            WTDocument doc = (WTDocument) queryResult.nextElement();
            CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(doc, fileType);
//			String[] fileTypes = LoadPropertiesConfig.getInstance(7).getQTBGVFileTypeValue();
//			if(cmPrintInfoBean.getFileType()!=null){
//				boolean isOtherReport = false;
//				for (String string : fileTypes) {
//					if(cmPrintInfoBean.getFileType().equals(string)){
//						isOtherReport = true;
//						break;
//					}
//				}
//				if(cmPrintInfoBean.getFileType().equals(PrintServerConstants.FILETYPE_GYTZD)
//						||cmPrintInfoBean.getFileType().equals(PrintServerConstants.FILETYPE_GYZFA)
//						||cmPrintInfoBean.getFileType().equals(PrintServerConstants.FILETYPE_GYZTB)
//						||cmPrintInfoBean.getFileType().equals(PrintServerConstants.FILETYPE_GYGGD)
//						||isOtherReport){
//					if(isZxdy){
//						list.add(cmPrintInfoBean);
//					}else{
//						wjdysqAddQuery(cmPrintInfoBean, list, cmPrintQueryBean.getFileState());
//					}
//				}
//			}
            list.add(cmPrintInfoBean);
        }
        return list;
    }


    /**
     * 更改单查询 add by zhuhao
     *
     * @param cmPrintQueryBean
     * @param fileType
     * @return
     * @throws WTPropertyVetoException
     * @throws RemoteException
     * @throws WTException
     */
    public static List<CmPrintInfoBean> queryWTChangeOrder2PrintFiles(CmPrintQueryBean cmPrintQueryBean, String fileType) throws WTPropertyVetoException, RemoteException, WTException {
        QuerySpec querySpec = new QuerySpec(WTChangeOrder2.class);
        querySpec.setAdvancedQueryEnabled(true);
        querySpec.appendWhere(new SearchCondition(WTChangeOrder2.class, "state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);

        //根据编号查询
        if (!cmPrintQueryBean.getFileNumber().equals("")) {
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTChangeOrder2.class, _ChangeOrder2.NUMBER, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileNumber() + "%", false), index);
        }
        //根据名称查询
        if (!"".equals(cmPrintQueryBean.getFileName())) {
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTChangeOrder2.class, _ChangeOrder2.NAME, SearchCondition.LIKE, "%" + cmPrintQueryBean.getFileName() + "%", false), index);
        }

        //根据版本查询
        if (!cmPrintQueryBean.getVersion().equals("")) {
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTChangeOrder2.class, "versionInfo.identifier.versionId", "=", cmPrintQueryBean.getVersion()), index);
        }

        Map<String, String> attrMap = new HashMap<String, String>();
        //根据阶段标记
        if (!cmPrintQueryBean.getPhaseCode().equals("")) {
            attrMap.put(DocumentConstants.IBA_PHASECODE, cmPrintQueryBean.getPhaseCode());
        }
        //根据软属性查询
        if (attrMap.size() != 0) {
            ClassAttribute caEcnId = new ClassAttribute(WTChangeOrder2.class, "thePersistInfo.theObjectIdentifier.id");
            if (caEcnId != null) {
                for (String ibaName : attrMap.keySet()) {
                    if (attrMap.get(ibaName) != null && !attrMap.get(ibaName).equals("")) {
                        querySpec.appendAnd();
                        SubSelectExpression ss = getStringIBAQuery(ibaName, attrMap.get(ibaName));
                        if (ss != null) {
                            querySpec.appendWhere(new SearchCondition(caEcnId, SearchCondition.IN, ss), new int[]{0});
                        }
                    }
                }
            }
        }

        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
        queryResult = new LatestConfigSpec().process(queryResult);
        while (queryResult.hasMoreElements()) {
            WTChangeOrder2 ecn = (WTChangeOrder2) queryResult.nextElement();
            CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(ecn, fileType);
            list.add(cmPrintInfoBean);
        }
        return list;
    }

    private static SubSelectExpression getStringIBAQuery(String ibaName,
                                                         String ibaValue) throws WTException, WTPropertyVetoException,
            RemoteException {
        // 获取IBA属性定义
        AttributeDefDefaultView addv = IBADefinitionHelper.service
                .getAttributeDefDefaultViewByPath(ibaName);
        if (addv == null)
            throw new IBADefinitionException("No IBA Definition: " + ibaName);
        long ibaDefId = addv.getObjectID().getId();
        QuerySpec qs = new QuerySpec();
        int idx = qs.appendClassList(StringValue.class, false);
        qs.appendSelect(new ClassAttribute(StringValue.class,
                "theIBAHolderReference.key.id"), new int[]{idx}, false);
        qs.appendWhere(new SearchCondition(StringValue.class,
                        "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId),
                new int[]{idx});
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(StringValue.class,
                        _StringValue.VALUE2, SearchCondition.LIKE, "%" + ibaValue + "%"),
                new int[]{idx});
        return new SubSelectExpression(qs);
    }

    private static SubSelectExpression getStringIBAQueryJQ(String ibaName,
                                                           String ibaValue) throws WTException, WTPropertyVetoException,
            RemoteException {
        // 获取IBA属性定义
        AttributeDefDefaultView addv = IBADefinitionHelper.service
                .getAttributeDefDefaultViewByPath(ibaName);
        if (addv == null)
            throw new IBADefinitionException("No IBA Definition: " + ibaName);
        long ibaDefId = addv.getObjectID().getId();
        QuerySpec qs = new QuerySpec();
        int idx = qs.appendClassList(StringValue.class, false);
        qs.appendSelect(new ClassAttribute(StringValue.class,
                "theIBAHolderReference.key.id"), new int[]{idx}, false);
        qs.appendWhere(new SearchCondition(StringValue.class,
                        "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId),
                new int[]{idx});
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(StringValue.class,
                        _StringValue.VALUE2, SearchCondition.EQUAL, ibaValue),
                new int[]{idx});
        return new SubSelectExpression(qs);
    }

    public static List<CmPrintInfoBean> ylqFileAddQuery(CmPrintQueryBean cmPrintQueryBean) throws Exception {
        return GWPrintDistributeRecordManager.queryYlqGWPrintDistributeRecord(cmPrintQueryBean);
    }

    public static List<WTUser> queryUser(String userName, String fullName) throws WTException {
        List<WTUser> userList = null;
        if (userName != null && !"".equals(userName) && fullName != null && !"".equals(fullName)) {
            userList = UserUtil.getWTUsers(userName, fullName);
        } else if (userName != null && !"".equals(userName)) {
            userList = UserUtil.getWTUsersByName(userName);
        } else if (fullName != null && !"".equals(fullName)) {
            userList = UserUtil.getWTUserByFullName(fullName);
        }
        return userList;
    }

    public static String queryUserCode(String userOid) throws WTException {
        String userCode = "";
        if (userOid != null && !"".equals(userOid)) {
            String sql = "select usercode from GWUSERCODETABLE where useroid='" + userOid + "'";
            DBConnUtil conn = null;
            try {
                conn = new DBConnUtil();
                ResultSet rt = conn.executeQuery(sql);
                if (rt.next()) {
                    userCode = rt.getString(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return userCode;
    }

    /**
     * 查询工艺文件目录 add by zhuhao
     *
     * @param cmPrintQueryBean
     * @return
     */
    public static List<CmPrintInfoBean> queryFileOnProcessDirectory(CmPrintQueryBean cmPrintQueryBean) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        try {
            String fileType = "casc.sast.149.reportTechnics";
            QuerySpec querySpec = new QuerySpec(WTDocument.class);
            querySpec.setAdvancedQueryEnabled(true);
            //查到受控中工艺文件目录
            TypeDefinitionReference tdr = TypedUtilityServiceHelper.service.getTypeDefinitionReference(fileType);
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr.getKey().getBranchId()), index);
            /*querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);
            */
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _WTDocument.NAME, SearchCondition.LIKE, "%工艺文件目录%", false), index);
            //根据版本查询
            if (!cmPrintQueryBean.getVersion().equals("")) {
                querySpec.appendAnd();
                querySpec.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", "=", cmPrintQueryBean.getVersion()), index);
            }
            //根据软属性查询
            Map<String, String> attrMap = new HashMap<String, String>();
            //根据阶段标记
            if (!cmPrintQueryBean.getPhaseCode().equals("")) {
                attrMap.put(DocumentConstants.IBA_PHASECODE, cmPrintQueryBean.getPhaseCode());
            }
            //根据编号查询
            if (!cmPrintQueryBean.getFileNumber().equals("")) {
                attrMap.put(DocumentConstants.IBA_NUMBER, cmPrintQueryBean.getFileNumber());
            }
            if (attrMap.size() != 0) {
                ClassAttribute caDocId = new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
                if (caDocId != null) {
                    for (String ibaName : attrMap.keySet()) {
                        if (attrMap.get(ibaName) != null && !attrMap.get(ibaName).equals("")) {
                            querySpec.appendAnd();
                            SubSelectExpression ss = getStringIBAQuery(ibaName, attrMap.get(ibaName));
                            if (ss != null) {
                                querySpec.appendWhere(new SearchCondition(caDocId, SearchCondition.IN, ss), new int[]{0});
                            }
                        }
                    }
                }
            }

            QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
            queryResult = new LatestConfigSpec().process(queryResult);
            while (queryResult.hasMoreElements()) {
                WTDocument doc = (WTDocument) queryResult.nextElement();
                CmPrintInfoBean cmPrintInfoBean = PrintDataBuildUtil.buildCmPrintInfoBean(doc, "工艺文件目录");
                list.add(cmPrintInfoBean);
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 通过工艺文件目录查询所有工艺文件 add by zhuhao
     *
     * @param cmPrintInfoBean
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<CmPrintInfoBean> queryProcessFiles(CmPrintInfoBean cmPrintInfoBean) {
        String oid = cmPrintInfoBean.getOid();
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        try {
            WTDocument doc = CSCDoc.getDocumentByOid(oid);
            CmPrintInfoBean cmPrintInfoBeanFile = PrintDataBuildUtil.buildCmPrintInfoBean(doc, "工艺文件目录");
            String processNumber = cmPrintInfoBean.getFileNumber();
            cmPrintInfoBeanFile.setPrintpath(processNumber);
            cmPrintInfoBeanFile.setProcessfile(true);
            list.add(cmPrintInfoBeanFile);
            String lifeCycleState = cmPrintInfoBeanFile.getLifeCycle();
            Element ele = getElementByDoc(doc);
            Element temp = ele.element("XWReportTechnicsInfo");
            List<Element> xmlList = temp.elements("dataItemValue");
            for (Element xmlEle : xmlList) {
                WTDocument ProcessDoc = null;
                String pplanNumber = xmlEle.attributeValue("bianhao");
                String pplanName = xmlEle.attributeValue("mingcheng");
                //文档编号
                String fileNumber = xmlEle.attributeValue("fileNumber");
                if (fileNumber == null || "null".equals(fileNumber)) {
                    fileNumber = "";
                }
                //版本
                String version = xmlEle.attributeValue("version");
                if (version == null || "null".equals(version)) {
                    version = "";
                }
                if (!"".equals(fileNumber) && !"".equals(version)&&"已批准".equals(lifeCycleState)) {//文档编号和版本都不为空
                    boolean isInteger = PrintUtil.isInteger(fileNumber);
                    if (isInteger) {
//						ProcessDoc = WTDocumentUtil.getDocumentByNumberAndVersion(fileNumber, version);
                        ProcessDoc = WTDocumentUtil.getDocumentByNumberAndAllVersion(fileNumber, version);
                    }
                } else if (!"".equals(fileNumber) && "".equals(version)) {//只有编号
                    boolean isInteger = PrintUtil.isInteger(fileNumber);
                    if (isInteger) {
                        ProcessDoc = WTDocumentUtil.getLatestDocumentByNumber(fileNumber);
                    }
                } else if ("".equals(fileNumber) && "".equals(version)) {//文档编号和版本都为空
                    String name = pplanName + "(" + pplanNumber + ")";
                    ProcessDoc = WTDocumentUtil.getDocumnetByNameAndIBAAndVersion(name, "PPNUMBER", pplanNumber, "");
                }
                if (ProcessDoc == null) {
                    continue;
                }
                QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(ProcessDoc, null, null);
                if (qrProcs.hasMoreElements()) {
                    WfProcess proc = (WfProcess) qrProcs.nextElement();
                    if(proc.getTemplate().getName().equals(Constants.WFN_SANJIPROCESSWF)){
                        continue;
                    }

                }
                CmPrintInfoBean cmPrintInfoBean2 = PrintDataBuildUtil.buildCmPrintInfoBean(ProcessDoc, "工艺规程");
                cmPrintInfoBean2.setPrintpath(processNumber);
                cmPrintInfoBean2.setProcessfile(true);
                cmPrintInfoBean2.setAddFormBOM(false);
                list.add(cmPrintInfoBean2);
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Element getElementByDoc(WTDocument document) throws WTException, PropertyVetoException, FileNotFoundException, DocumentException {
        String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
                + java.util.UUID.randomUUID().toString() + File.separator;
        String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
        String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
        ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
        File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
        SAXReader reader = new SAXReader();
        Document dom = reader.read(xmlFile);
        Element rootElement = dom.getRootElement();
        FileUtil.deleteFile(new File(tempFilePath));
        return rootElement;
    }

    /**
     * 查询对应的分厂打印文件 add by zhuhao
     *
     * @param userName
     * @param oid
     * @param category
     * @return
     */
    public static List<CmPrintInfoBean> queryReceiveData(String userName, String oid, String category) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        Connection conn = null;
        try {
            String dept = "";
            if (!"".equals(userName) && userName != null) {
                dept = PrintUtil.getUserByName(userName);
                if ("".equals(dept) || dept == null) {
                    return list;
                } else {
                    StringBuffer sb1 = new StringBuffer();
                    sb1.append("SELECT * FROM gwprintbarcode WHERE applyrecordid IN ");
                    sb1.append("(SELECT GWKEYID FROM gwprintdistributerecord WHERE applyrecordid IN ");
                    sb1.append("(SELECT GWKEYID FROM gwprintapplyrecord WHERE gwprintapplyrecord.pbooid = '");
                    sb1.append(oid + "')) AND GDEPT = '" + dept + "'");
                    if("BD".equals(category)){
                    	sb1.append(" AND OFFSET = 'true'");
                    }
                    conn = OracleDataSource.getOracleDataSource().getConnection();
                    conn.setAutoCommit(false);
                    Statement state1 = conn.createStatement();
                    Statement state2 = conn.createStatement();
                    ResultSet rs1 = state1.executeQuery(sb1.toString());
                    while (rs1.next()) {
                        CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
                        String barcodeid = rs1.getString("GWKEYID");
                        cmPrintInfoBean.setOid(barcodeid);
                        cmPrintInfoBean.setQrName(rs1.getString("BARCODE"));//二维码
                        cmPrintInfoBean.setReceipter(rs1.getString("GUSER"));//领用人
                        cmPrintInfoBean.setReceiptDept(rs1.getString("GDEPT"));//领用人部门
                        cmPrintInfoBean.setReceiptDate(rs1.getString("GDATE"));//领用日期
                        cmPrintInfoBean.setFileState(rs1.getString("FILESTATUS"));//领用状态
                        cmPrintInfoBean.setTemporarySeal(rs1.getString("BATCH"));
                        String sql2 = "SELECT * FROM gwprintapplyrecord WHERE gwkeyid IN " +
                                "(SELECT applyrecordid FROM gwprintdistributerecord WHERE gwkeyid IN " +
                                "(SELECT applyrecordid FROM gwprintbarcode WHERE GWKEYID = '" + barcodeid + "'))";
                        ResultSet rs2 = state2.executeQuery(sql2);
                        while (rs2.next()) {
                            cmPrintInfoBean.setDocVR(rs2.getString("DOCVR"));
                            cmPrintInfoBean.setFileNumber(rs2.getString("DOCNUMBER"));
                            cmPrintInfoBean.setFileName(rs2.getString("DOCNAME"));
                            cmPrintInfoBean.setVersion(rs2.getString("VERSION"));
                            cmPrintInfoBean.setPhaseCode(rs2.getString("PHASECODE"));
                            cmPrintInfoBean.setSecret(rs2.getString("SECRET"));
                        }
                        list.add(cmPrintInfoBean);
                    }
                }
            } else {
                list = loadPaperFile(oid, category);
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 查询纸质文件数据
     *
     * @param oid
     * @param category
     * @return
     */
    public static List<CmPrintInfoBean> loadPaperFile(String oid, String category) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        if (oid == null || "".equals(oid)) {
            return list;
        }
        DBConnUtil conn1 = null;
        DBConnUtil conn2 = null;
        try {
            conn1 = new DBConnUtil();
            conn2 = new DBConnUtil();
            String sql1 = "SELECT * FROM gwprintbarcode WHERE applyrecordid IN " +
                    "(SELECT GWKEYID FROM gwprintdistributerecord WHERE applyrecordid IN " +
                    "(SELECT GWKEYID FROM gwprintapplyrecord WHERE gwprintapplyrecord.pbooid = '" + oid + "'))";
            if("BD".equals(category)){
            	sql1 = sql1 + " AND OFFSET = 'true'";
            }
            ResultSet rs1 = conn1.executeQuery(sql1);
            while (rs1.next()) {
                CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
                String barcodeid = rs1.getString("GWKEYID");
                cmPrintInfoBean.setOid(barcodeid);
                cmPrintInfoBean.setQrName(rs1.getString("BARCODE"));//二维码
                cmPrintInfoBean.setReceipter(rs1.getString("GUSER"));//领用人
                cmPrintInfoBean.setReceiptDept(rs1.getString("GDEPT"));//领用人部门
                cmPrintInfoBean.setReceiptDate(rs1.getString("GDATE"));//领用日期
                cmPrintInfoBean.setFileState(rs1.getString("FILESTATUS"));//领用状态
                cmPrintInfoBean.setTemporarySeal(rs1.getString("BATCH"));
                String sql2 = "SELECT * FROM gwprintapplyrecord WHERE gwkeyid IN " +
                        "(SELECT applyrecordid FROM gwprintdistributerecord WHERE gwkeyid IN " +
                        "(SELECT applyrecordid FROM gwprintbarcode WHERE GWKEYID = '" + barcodeid + "'))";
                ResultSet rs2 = conn2.executeQuery(sql2);
                while (rs2.next()) {
                    cmPrintInfoBean.setDocVR(rs2.getString("DOCVR"));
                    cmPrintInfoBean.setFileNumber(rs2.getString("DOCNUMBER"));
                    cmPrintInfoBean.setFileName(rs2.getString("DOCNAME"));
                    cmPrintInfoBean.setVersion(rs2.getString("VERSION"));
                    cmPrintInfoBean.setPhaseCode(rs2.getString("PHASECODE"));
                    cmPrintInfoBean.setSecret(rs2.getString("SECRET"));
                }
                list.add(cmPrintInfoBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn1 != null) {
                    conn1.close();
                }
                if (conn2 != null) {
                    conn2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static String getCategory(String oid) {
        Connection conn = null;
        String category = "";
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT DOCVR FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + oid + "'");
            ResultSet rs = state.executeQuery(sb.toString());

            while (rs.next()) {
                category = rs.getString("DOCVR");
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return category;
    }

    public static List<CmPrintInfoBean> loadPrintBarCode(String oid, String category,String dept) {
        List<CmPrintInfoBean> beanList = new ArrayList<CmPrintInfoBean>();
        Connection conn = null;
        try {
            List<CmPrintInfoBean> list = queryPrintApply(oid);
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (CmPrintInfoBean applyBean : list) {
                String gwkeyid = applyBean.getOid();
                StringBuffer sb = new StringBuffer();
                sb.append("SELECT * FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
                sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = ");
                sb.append("'" + gwkeyid + "'");
                if(dept != null &&!dept.isEmpty()){
                    sb.append(" and DISTRIBUTEDEPT='"+dept+"'");
                }
                sb.append(")");
                ResultSet rs = state.executeQuery(sb.toString());
                //检查是否有条码为空的打印条目
                while(rs.next()){
                    if(StrUtil.isEmpty(rs.getString("BARCODE"))) {
                        if ("工艺更改单".equals(applyBean.getFileType())) {
                            WTChangeOrder2 wtChangeOrder2 = PrintWorkflowUtil.getWTChangeOrder2ByNumber(applyBean.getTechnicsNumber());
                            if(wtChangeOrder2 != null) {
                                GWPrintConnectionQR.connectionQR(wtChangeOrder2, gwkeyid);
                            }
                        } else {
                            List<WTDocument> docs = WTDocumentUtil.getAllDocumentByNumber(applyBean.getTechnicsNumber());
                            WTDocument doc = null;
                            precise:
                            for (WTDocument doc0 : docs) {
                                String docVersion = doc0.getVersionIdentifier().getValue() + "." + doc0.getIterationIdentifier().getValue();
                                if (docVersion.equals(applyBean.getVersion())) {
                                    doc = doc0;
                                    break precise;
                                }
                            }
                            if (doc != null) {
                                GWPrintConnectionQR.connectionQR(doc, gwkeyid);
                            }
                        }
                    }
                }

                rs = state.executeQuery(sb.toString());
                while (rs.next()) {
                	if(category != null && !"".equals(category) && !"WL".equals(category) && !"ZZ".equals(category)){
                		if(rs.getString("OFFSET") == null || "".equals(rs.getString("OFFSET"))){
                			continue;
                		}
                    }
                    CmPrintInfoBean newBean = new CmPrintInfoBean();
                    newBean.setOid(rs.getString("GWKEYID"));
                    newBean.setFileState(rs.getString("FILESTATUS"));
                    newBean.setQrCode(rs.getString("BARCODE"));
                    newBean.setPrinter(rs.getString("PUSER"));
                    newBean.setPrintDate(rs.getString("PDATE"));
                    newBean.setDistributeDeptAndCount(rs.getString("GDEPT"));
                    newBean.setDocVR(applyBean.getDocVR());
                    newBean.setFileNumber(applyBean.getFileNumber());
                    newBean.setFileName(applyBean.getFileName());
                    newBean.setVersion(applyBean.getVersion());
                    newBean.setPhaseCode(applyBean.getPhaseCode());
                    newBean.setSecret(applyBean.getSecret());
                    newBean.setTemporarySeal(rs.getString("BATCH"));
                    newBean.setTechnicsNumber(applyBean.getTechnicsNumber());
                    beanList.add(newBean);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch(RemoteException e) {
            e.printStackTrace();
        } catch(ServiceException e) {
            e.printStackTrace();
        } catch(WTException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return beanList;
    }

    /**
     * 查询打印表
     *
     * @param oid
     * @return
     * @author zhuhao
     * @date 2018-4-11
     */
    private static List<CmPrintInfoBean> queryPrintApply(String oid) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE PBOOID = '");
        sb.append(oid);
        sb.append("'");
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
                cmPrintInfoBean.setOid(rs.getString("GWKEYID"));
                cmPrintInfoBean.setDocVR(rs.getString("DOCVR"));
                cmPrintInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintInfoBean.setFileName(rs.getString("DOCNAME"));
                cmPrintInfoBean.setVersion(rs.getString("VERSION"));
                cmPrintInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintInfoBean.setSecret(rs.getString("SECRET"));
                cmPrintInfoBean.setTemporarySeal(rs.getString("BATCH"));
                cmPrintInfoBean.setTechnicsNumber(rs.getString("TECHNICSNUMBER"));
                list.add(cmPrintInfoBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void queryChangeIDToProcess(String docOid, String ecnOid, WfProcess process) {
        String infor = "";
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            if (!"".equals(docOid)) {
                StringBuffer sb = new StringBuffer();
                sb.append("SELECT GWKEYID FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
                sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID IN ");
                sb.append("(SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE DOCVR = '" + docOid + "'))");
                ResultSet rs = state.executeQuery(sb.toString());
                while (rs.next()) {
                    if ("".equals(infor)) {
                        infor = rs.getString("GWKEYID");
                    } else {
                        infor = infor + ":" + rs.getString("GWKEYID");
                    }
                }
            }
            if (!"".equals(ecnOid)) {
                StringBuffer sb = new StringBuffer();
                sb.append("SELECT GWKEYID FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
                sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID IN ");
                sb.append("(SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE DOCVR = '" + ecnOid + "'))");
                ResultSet rs = state.executeQuery(sb.toString());
                while (rs.next()) {
                    if ("".equals(infor)) {
                        infor = rs.getString("GWKEYID");
                    } else {
                        infor = infor + ":" + rs.getString("GWKEYID");
                    }
                }
            }
            ProcessData processData = process.getContext();
            processData.setValue("infor", infor);
            PersistenceHelper.manager.save(process);
//			 if(!"".equals(infor)){
//					return true;
//			}
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
//		return false;
        catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 查询BOM和关联的工艺文件
     *
     * @param value
     * @return 工艺文件文档编号
     * @author zhuhao
     * @date 2018-4-25
     * @update by hz--支持sop零部件下工艺的搜索 
     */
    public static List<String> getTechnicsNumberByBOM(String value) {
        List<String> list = new ArrayList<String>();
        QueryResult qr = searchAllIteratedByNumber(value);
        while (qr.hasMoreElements()) {
            WTPart part = (WTPart) qr.nextElement();
            ArrayList<WTDocument> Docs = getDoc(part, SopConstants.SOP_TYPE_SOPDOC, com.glaway.mpm.constants.Constants.planning);
            for (WTDocument doc1 : Docs) {
                if (!list.contains(doc1.getNumber())) {
                    list.add(doc1.getNumber());
                }
            }
        }
        return list;
    }

    private static QueryResult searchAllIteratedByNumber(String value) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, value), new int[0]);
            qs.appendOr();
            qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.EQUAL, value), new int[0]);
            View view = ViewHelper.service.getView("Manufacturing");
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[0]);
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            return qr;
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }

    public static List<CmPrintRecordInfoBean> queryPrintInfo(List<String> technicsList) {
        List<CmPrintRecordInfoBean> beanList = new ArrayList<CmPrintRecordInfoBean>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String technicsNumber : technicsList) {
                StringBuffer sb = new StringBuffer();
                sb.append("select a.*, b.*, c.GWKEYID as ID, c.BARCODE, c.FILESTATUS, c.GDATE, c.GDEPT, c.GUSER,c.PUSER,c.PDATE ");
                sb.append("from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c ");
                sb.append("where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid ");
                sb.append("and a.TECHNICSNUMBER = '" + technicsNumber + "'");
                ResultSet rs = state.executeQuery(sb.toString());
                while (rs.next()) {
                    CmPrintRecordInfoBean cmPrintRecordInfoBean1 = new CmPrintRecordInfoBean();
                    cmPrintRecordInfoBean1.setUnit(rs.getString("OUTDEPT"));
                    String applyrecordid = rs.getString("ID");
                    cmPrintRecordInfoBean1.setBarTableID(applyrecordid);
                    cmPrintRecordInfoBean1.setFileNumber(rs.getString("DOCNUMBER"));
                    cmPrintRecordInfoBean1.setFileName(rs.getString("DOCNAME"));
                    cmPrintRecordInfoBean1.setFileType(rs.getString("FILETYPE"));
                    cmPrintRecordInfoBean1.setDocVersion(rs.getString("VERSION"));
                    cmPrintRecordInfoBean1.setPhaseCode(rs.getString("PHASECODE"));
                    cmPrintRecordInfoBean1.setSecret(rs.getString("SECRET"));
                    cmPrintRecordInfoBean1.setDistributeStatus(rs.getString("FILESTATUS"));
                    cmPrintRecordInfoBean1.setPrintDate(rs.getString("PRINTDATE"));
                    cmPrintRecordInfoBean1.setGetDate(rs.getString("GDATE"));
                    cmPrintRecordInfoBean1.setGetDept(rs.getString("GDEPT"));
                    cmPrintRecordInfoBean1.setGetUser(rs.getString("GUSER"));
                    cmPrintRecordInfoBean1.setBarCode(rs.getString("BARCODE"));
                    cmPrintRecordInfoBean1.setPrintUser(rs.getString("PUSER"));
                    cmPrintRecordInfoBean1.setPrintDate(rs.getString("PDATE"));
                    String docvr = rs.getString("DOCVR");
                    if (null != docvr) {
                        Persistable persistable = PersistableUtil.getPersistable(docvr);
                        String containerName = "";
                        if (persistable != null) {
                            if (persistable instanceof WTDocument) {
                                WTDocument document = (WTDocument) persistable;
                                containerName = document.getContainerName();
                            } else if (persistable instanceof WTChangeOrder2) {
                                WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
                                containerName = changeOrder.getContainerName();
                            }
                        }
                        cmPrintRecordInfoBean1.setContainerName(containerName);
                        cmPrintRecordInfoBean1.setType(containerName);
                    }
                    String receivedate = getReceiveDate(applyrecordid);
                    cmPrintRecordInfoBean1.setRecoverDate(receivedate);
                    beanList.add(cmPrintRecordInfoBean1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return beanList;
    }

    private static String getReceiveDate(String applyrecordid) {
        Connection conn = null;
        String receivedate = "";
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("select RECEIVEDATE from GWPRINTRECOVERRECORD where APPLYRECORDID = '" + applyrecordid + "'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                receivedate = rs.getString("RECEIVEDATE");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return receivedate;
    }

    public static List<String> getTechnicsNumberByProcessDirectory(String value) {
        List<String> list = new ArrayList<String>();
        try {
            String fileType = "casc.sast.149.reportTechnics";
            QuerySpec querySpec = new QuerySpec(WTDocument.class);
            querySpec.setAdvancedQueryEnabled(true);
            //查到受控中工艺文件目录
            TypeDefinitionReference tdr = TypedUtilityServiceHelper.service.getTypeDefinitionReference(fileType);
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _Typed.TYPE_DEFINITION_REFERENCE + "." + _TypeDefinitionReference.KEY + "." + _TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL, tdr.getKey().getBranchId()), index);
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, ProcessPlanConstants.LIFECYCLE_EN_APPROVED), index);
            querySpec.appendAnd();
            querySpec.appendWhere(new SearchCondition(WTDocument.class, _WTDocument.NAME, SearchCondition.LIKE, "%工艺文件目录%", false), index);
            //根据软属性查询
            Map<String, String> attrMap = new HashMap<String, String>();
            //根据编号查询
            attrMap.put(DocumentConstants.IBA_NUMBER, value);
            if (attrMap.size() != 0) {
                ClassAttribute caDocId = new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
                if (caDocId != null) {
                    for (String ibaName : attrMap.keySet()) {
                        if (attrMap.get(ibaName) != null && !attrMap.get(ibaName).equals("")) {
                            querySpec.appendAnd();
                            SubSelectExpression ss = getStringIBAQueryJQ(ibaName, attrMap.get(ibaName));
                            if (ss != null) {
                                querySpec.appendWhere(new SearchCondition(caDocId, SearchCondition.IN, ss), new int[]{0});
                            }
                        }
                    }
                }
            }
            QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
            queryResult = new LatestConfigSpec().process(queryResult);
            while (queryResult.hasMoreElements()) {
                WTDocument doc = (WTDocument) queryResult.nextElement();
                list.add(doc.getNumber());
                Element ele = getElementByDoc(doc);
                Element temp = ele.element("XWReportTechnicsInfo");
                List<Element> xmlList = temp.elements("dataItemValue");
                for (Element xmlEle : xmlList) {
                    WTDocument ProcessDoc = null;
                    String pplanNumber = xmlEle.attributeValue("bianhao");
                    String pplanName = xmlEle.attributeValue("mingcheng");
                    //文档编号
                    String fileNumber = xmlEle.attributeValue("fileNumber");
                    if ("null".equals(fileNumber) || fileNumber == null) {
                        fileNumber = "";
                    }
                    if (!"".equals(fileNumber)) {
                        if (!list.contains(fileNumber)) {
                            list.add(fileNumber);
                        }
                    } else if ("".equals(fileNumber)) {//文档编号和版本都为空
                        String name = pplanName + "(" + pplanNumber + ")";
                        ProcessDoc = WTDocumentUtil.getDocumnetByNameAndIBAAndVersion(name, "PPNUMBER", pplanNumber, "");
                        if (ProcessDoc == null) {
                            continue;
                        }
                        if (!list.contains(ProcessDoc.getNumber())) {
                            list.add(ProcessDoc.getNumber());
                        }
                    }
                    if (ProcessDoc == null) {
                        continue;
                    }
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<CmPrintRecordInfoBean> queryRecoverValueByUser(List<String> strList, String userName) {
        List<CmPrintRecordInfoBean> beanList = null;
        List<String> list = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String id : strList) {
                StringBuffer sb = new StringBuffer();
                sb.append("SELECT APPLYRECORDID FROM GWPRINTRECOVERRECORD WHERE GWKEYID = '" + id + "'");
                ResultSet rs = state.executeQuery(sb.toString());
                while (rs.next()) {
                    String applyrecordid = rs.getString("APPLYRECORDID");
                    if (!list.contains(applyrecordid)) {
                        list.add(applyrecordid);
                    }
                }
            }
            if (list != null && !list.isEmpty()) {
                beanList = queryRecoverBarCodeValueByApplyrecordid(list, userName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return beanList;
    }

    private static List<CmPrintRecordInfoBean> queryRecoverBarCodeValueByApplyrecordid(List<String> list, String userName) {
        List<CmPrintRecordInfoBean> beanList = new ArrayList<CmPrintRecordInfoBean>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String id : list) {
                StringBuffer sb = new StringBuffer();
                sb.append("SELECT * FROM GWPRINTBARCODE WHERE GWKEYID = '" + id + "' AND GUSER = '" + userName + "'");
                ResultSet rs = state.executeQuery(sb.toString());
                while (rs.next()) {
                    CmPrintRecordInfoBean cmPrintRecordQueryBean = new CmPrintRecordInfoBean();
                    cmPrintRecordQueryBean.setBarTableID(rs.getString("GWKEYID"));
                    cmPrintRecordQueryBean.setGetDept(rs.getString("GDEPT"));
                    cmPrintRecordQueryBean.setBarCode(rs.getString("BARCODE"));
                    cmPrintRecordQueryBean.setMiddleStatus(rs.getString("FILESTATUS"));
                    cmPrintRecordQueryBean = queryRecoverValueByApplyrecordid(cmPrintRecordQueryBean, rs.getString("GWKEYID"));
                    cmPrintRecordQueryBean = PrintUserCodeProcessor.queryRecycleState(cmPrintRecordQueryBean, rs.getString("GWKEYID"));
                    beanList.add(cmPrintRecordQueryBean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return beanList;
    }


    private static CmPrintRecordInfoBean queryRecoverValueByApplyrecordid(CmPrintRecordInfoBean cmPrintRecordQueryBean, String id) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GWKEYID = '" + id + "'))");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
            	String fileNumber = rs.getString("DOCNUMBER");
            	String fileName = rs.getString("DOCNAME");
            	String version = rs.getString("VERSION");
                cmPrintRecordQueryBean.setFileNumber(fileNumber);
                cmPrintRecordQueryBean.setFileName(fileName);
                cmPrintRecordQueryBean.setDocVersion(version);
                cmPrintRecordQueryBean.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordQueryBean.setSecret(rs.getString("SECRET"));
                String docvr = rs.getString("DOCVR");
                if (null != docvr) {
                	String containerName = "";
                    String lifeCycle = "";
                	if("WL".equals(docvr) || "ZZ".equals(docvr)){
                		Map<String, String> map = new HashMap<String, String>();
                		map.put("number", fileNumber);
                		map.put("name", fileName);
                		map.put("version", version);
                		containerName = PrintDataQueryUtil.getCategoryByOutFile(map);
                	}else{
                		Persistable persistable = PersistableUtil.getPersistable(docvr);
                        if (persistable != null) {
                            if (persistable instanceof WTDocument) {
                                WTDocument document = (WTDocument) persistable;
                                containerName = document.getContainerName();
                                lifeCycle = document.getState().getState().getDisplay(Locale.CHINA);
                            } else if (persistable instanceof WTChangeOrder2) {
                                WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
                                containerName = changeOrder.getContainerName();
                                lifeCycle = changeOrder.getState().getState().getDisplay(Locale.CHINA);
                            }
                        }
                	}
                    cmPrintRecordQueryBean.setLifeCycleState(lifeCycle);
                    cmPrintRecordQueryBean.setContainerName(containerName);
                }
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cmPrintRecordQueryBean;
    }

    public static String queryLosePboNumber(String barTableID) {
        String losePboNumber = "";
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT LOSEPBONUMBER FROM GWPRINTLOSERECORD WHERE PRINTBARCODEID = '" + barTableID + "'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                losePboNumber = rs.getString("LOSEPBONUMBER");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return losePboNumber;
    }

    public static String querydelayPboNumber(String barTableID) {
        String delayPboNumber = "";
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT DELAYPBONUMBER FROM GWPRINTRECOVERRECORD WHERE APPLYRECORDID = '" + barTableID + "'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                delayPboNumber = rs.getString("DELAYPBONUMBER");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return delayPboNumber;
    }

    public static String getUserNameBySign(String scanInput) {
        String userName = "";
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT USERNAME FROM GWUSERSIGN WHERE SIGN = '" + scanInput + "'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                userName = rs.getString("USERNAME");
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userName;
    }
	    public static List<CmUserQrCodeBean> getCmUserByUserName(String userName) {
        List<CmUserQrCodeBean> userBeanList = new ArrayList<CmUserQrCodeBean>();
        Connection conn = null;
        String name;
        String sign;
        CmUserQrCodeBean cmUserQrCodeBean;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            String sql = "SELECT USERNAME,SIGN FROM GWUSERSIGN WHERE USERNAME = '" + userName + "'";
            ResultSet rs = state.executeQuery(sql);
            while (rs.next()) {
                name = rs.getString("USERNAME");
                sign = rs.getString("SIGN");
                cmUserQrCodeBean = new CmUserQrCodeBean();
                cmUserQrCodeBean.setUserName(name);
                cmUserQrCodeBean.setUserCode(sign);
                userBeanList.add(cmUserQrCodeBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userBeanList;
    }


	public static List<CmPrintInfoBean> searchPrintInfo(CmPrintQueryBean cmPrintQueryBean) {
		List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
		String fileNumber = cmPrintQueryBean.getFileNumber();
		String fileName = cmPrintQueryBean.getFileName();
		String fileType = cmPrintQueryBean.getFileType();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE PRINTSTATUS = '已分发'");
			if(fileType != null && !"".equals(fileType)){
				sb.append(" AND FILETYPE = '" + fileType + "'");
			}
			if(fileNumber != null && !"".equals(fileNumber)){
				sb.append(" AND DOCNUMBER LIKE '%" + fileNumber + "%'");
			}
			if(fileName != null && !"".equals(fileName)){
				sb.append(" AND DOCNAME LIKE '%" + fileName + "%'");
			}
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				CmPrintInfoBean printInfoBean = new CmPrintInfoBean();
				printInfoBean.setOid(rs.getString("GWKEYID"));
				printInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
				printInfoBean.setFileName(rs.getString("DOCNAME"));
				printInfoBean.setVersion(rs.getString("VERSION"));
				printInfoBean.setPhaseCode(rs.getString("PHASECODE"));
				printInfoBean.setSecret(rs.getString("SECRET"));
				printInfoBean.setTemporarySeal(rs.getString("BATCH"));
				printInfoBean.setPrintState(rs.getString("PRINTSTATUS"));
				printInfoBean.setDistributeDeptAndCount(rs.getString("DISMESSAGE"));
				printInfoBean.setDocVR(rs.getString("DOCVR"));
				printInfoBean.setPbooid(rs.getString("PBOOID"));
				printInfoBean.setFileType(rs.getString("FILETYPE"));
				printInfoBean.setTechnicsNumber(rs.getString("TECHNICSNUMBER"));

				String processfile = rs.getString("PROCESSFILE");
				if("true".equals(processfile)){
					printInfoBean.setProcessfile(true);
				}else{
					printInfoBean.setProcessfile(false);
				}
				//add by jyx 增加所属产品库名称 和 分发状态受控状态
				String persistableOid = rs.getString("DOCVR");
				String containerName = "";
				String fileState = "";
				String lifeCycle = "";
				Persistable persistable = PersistableUtil.getPersistable(persistableOid);
				if(persistable instanceof WTDocument){
					WTDocument document = (WTDocument)persistable;
					containerName = document.getContainerName();
					lifeCycle = document.getState().getState().getDisplay(Locale.CHINA);
				}else if(persistable instanceof WTChangeOrder2){
					WTChangeOrder2 changeOrder = (WTChangeOrder2)persistable;
					containerName = changeOrder.getContainerName();
					lifeCycle = changeOrder.getState().getState().getDisplay(Locale.CHINA);
				}
				fileState = PrintDataBuildUtil.searchFileState("","",persistableOid,"");
				printInfoBean.setContainerName(containerName);
				printInfoBean.setFileState(fileState);
				printInfoBean.setLifeCycle(lifeCycle);
				list.add(printInfoBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
			return list;
	}

    public static String setEmployeeNo(String userName, String sign) {
        String message;
        String uName = queryEmployeeNoIsExist(sign);
        if(uName != null && !"".equals(uName)){
            message = "该工号已被" + uName + "占用，保存失败！";
        }else{
            boolean isExist = queryEmployeeNo(userName);
            if (isExist) {
                message = updateEmployeeNo(userName, sign);
            } else {
                message = insertEmployeeNo(userName, sign);
            }
        }
        return message;
    }

    public static boolean queryEmployeeNo(String userName) {
        Connection conn = null;
        boolean flag = Boolean.FALSE;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            String sql = "SELECT * FROM GWUSERSIGN WHERE USERNAME='" + userName + "'";
            ResultSet rs = state.executeQuery(sql);
            if (rs.next()) {
                flag = Boolean.TRUE;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
    public static String queryEmployeeNoIsExist(String sign) {
        Connection conn = null;
        String userName = "";
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            String sql = "SELECT *  FROM GWUSERSIGN WHERE SIGN='" + sign + "'";
            ResultSet rs = state.executeQuery(sql);
            if (rs.next()) {
                userName = rs.getString("USERNAME");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userName;
    }

    public static String updateEmployeeNo(String userName, String sign) {
        Connection conn = null;
        String message = "";
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            String sql = "UPDATE GWUSERSIGN SET SIGN='" + sign + "' WHERE USERNAME='" + userName + "'";
            state.executeUpdate(sql);
            message = "更新成功";
        } catch (SQLException e) {
            message = "用户：" + userName + "工号更新失败!";
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                message = "用户：" + userName + "工号更新失败!";
                e.printStackTrace();
            }
        }
        return message;
    }

    public static String insertEmployeeNo(String userName, String sign) {
        Connection conn = null;
        String message = "";
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            String sql = "INSERT INTO GWUSERSIGN (USERNAME, SIGN) values ('" + userName + "','" + sign + "')";
            state.executeUpdate(sql);
            message = "保存成功";
        } catch (SQLException e) {
            message = "用户：" + userName + "工号保存失败!";
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                message = "用户：" + userName + "工号保存失败!";
                e.printStackTrace();
            }
        }
        return message;
    }

    public static String deleteEmployeeNo(String userName) {
        Connection conn = null;
        String message = "";
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            String sql = "DELETE FROM GWUSERSIGN WHERE USERNAME='" + userName + "";
            state.executeUpdate(sql);
            message = "删除成功";
        } catch (SQLException e) {
            message = "删除失败";
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                message = "删除失败";
                e.printStackTrace();
            }
        }
        return message;
    }

	public static List<CmPrintDistributerecordBean> queryPrintDistributerecord(String id) {
		List<CmPrintDistributerecordBean> list = new ArrayList<CmPrintDistributerecordBean>();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '" + id + "'");
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				CmPrintDistributerecordBean bean = new CmPrintDistributerecordBean();
				bean.setGwkeyid(rs.getString("GWKEYID"));
				bean.setDocVR(rs.getString("DOCVR"));
				bean.setApplyrecordid(id);
				bean.setDistributedept(rs.getString("DISTRIBUTEDEPT"));
				bean.setDistributequantity(rs.getString("DISTRIBUTEQUANTITY"));
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return list;
	}

	public static String getBatchById(String id) {
		String batch = "";
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT BATCH FROM GWPRINTAPPLYRECORD WHERE GWKEYID = '" + id + "'");
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				batch = rs.getString("BATCH");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return batch;

	}

	public static List<String> queryLinkForOffSet(String id, List<String> oldDeptList) {
		List<String> list = new ArrayList<String>();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '" + id + "'");
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				String dept = rs.getString("DISTRIBUTEDEPT");
				if(!oldDeptList.contains(dept)){
					list.add(rs.getString("GWKEYID"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return list;
	}

	public static List<String> queryLinkChangeOffSet(String id, List<String> oldDeptList) {
		List<String> list = new ArrayList<String>();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '" + id + "'");
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				String dept = rs.getString("DISTRIBUTEDEPT");
				if(oldDeptList.contains(dept)){
					list.add(rs.getString("GWKEYID"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return list;
	}

	public static Map<String, String> queryMapChangeOffSet(String id, List<String> oldDeptList) {
		Map<String, String> map = new HashMap<String, String>();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '" + id + "'");
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				String dept = rs.getString("DISTRIBUTEDEPT");
				if(oldDeptList.contains(dept)){
					map.put(rs.getString("GWKEYID"), rs.getString("DISTRIBUTEDEPT"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return map;
	}

	public static List<String> queryObjId(String name, String pbooid, String info) {
		List<String> list = new ArrayList<String>();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			if("DYSQ".equals(name) || "BDSQ".equals(name)){
				sb.append("SELECT DOCVR FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + pbooid + "'");
			}else if("JGYZ".equals(name)){
				sb.append("SELECT DOCVR FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
                sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
                sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE PBOOID = '" + pbooid + "'))");
			}else if("WJQF".equals(name) || "WJFC".equals(name)||"YSSQ".equals(name)){
				List<String> idList = new ArrayList<String>();
				String[] temp = info.split(":");
				for (String string : temp) {
					idList.add(string);
				}
				sb.append("SELECT DOCVR FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
                sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
                sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GWKEYID = '" + idList.get(0) + "'))");
			}else if("YCSQ".equals(name)){
				List<String> idList = new ArrayList<String>();
				String[] temp = info.split(":");
				for (String string : temp) {
					idList.add(string);
				}
				sb.append("SELECT DOCVR FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
                sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
                sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GWKEYID IN ");
                sb.append("(SELECT APPLYRECORDID FROM GWPRINTRECOVERRECORD WHERE GWKEYID = '" + idList.get(0) + "')))");
			}
			ResultSet rs = state.executeQuery(sb.toString());
			while (rs.next()) {
				String docvr = rs.getString("DOCVR");
				if(docvr !=null && !"".equals(docvr) && !list.contains(docvr)){
					list.add(docvr);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return list;
	}

	public static List<String> getDeptById(List<String> idList) {
		List<String> list = new ArrayList<String>();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			for(String id : idList){
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT GDEPT FROM GWPRINTBARCODE WHERE GWKEYID = '" + id + "'");
				ResultSet rs = state.executeQuery(sb.toString());
				while(rs.next()){
					String dept = rs.getString("GDEPT");
					if(!list.contains(dept)){
						list.add(dept);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return list;
	}

	public static String queryOutFileState(String number, String name, String fileVersion) {
		String fileState = "未分发";
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT PRINTSTATUS FROM GWPRINTAPPLYRECORD WHERE DOCNUMBER = '" + number + "' AND DOCNAME = '" + name + "'");
			if(fileVersion != null && !"".equals(fileVersion)){
				sb.append(" AND VERSION = '" + fileVersion + "'");
			}
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				fileState = rs.getString("PRINTSTATUS");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return fileState;
	}

	public static boolean checkRepeatOutFile(CmPrintQueryBean cmPrintQueryBean, String category) {
		String fileNumber = cmPrintQueryBean.getFileNumber();
		String version = cmPrintQueryBean.getVersion();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM GWOTHERFILES WHERE FILENUMBER = '" + fileNumber + "'");
			if(version != null && !"".equals(version)){
				sb.append(" AND VERSION = '" + version + "'");
			}
			if("ZZ".equals(category)){
				sb.append(" AND DEPT = 'null'");
			}
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return false;
	}

	public static Map<String, String> getOutFileNumberAndVersionById(String pbooid, String name) {
		Map<String, String> map = new HashMap<String, String>();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			if("JGYZ".equals(name)){
				sb.append("SELECT DOCNUMBER, DOCNAME, VERSION FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
				sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
	            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE PBOOID = '" + pbooid + "')) ");
			}else{
				sb.append("SELECT DOCNUMBER, DOCNAME, VERSION FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + pbooid + "'");
			}
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				map.put("number", rs.getString("DOCNUMBER"));
				map.put("name", rs.getString("DOCNAME"));
				map.put("version", rs.getString("VERSION"));
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return map;
	}

	public static String getCategoryByOutFile(Map<String, String> map) {
		String number = map.get("number");
		String name = map.get("name");
		String version = map.get("version");
		String category = "";
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT CONTAINER FROM GWOTHERFILES WHERE FILENUMBER = '" + number + "' AND FILENAME = '" + name + "'");
			if(version != null && !"".equals(version)){
				sb.append(" AND VERSION = '" + version + "'");
			}
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				category = rs.getString("CONTAINER");
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return category;
	}

	public static Map<String, String> getOutFileNumberAndVersionByInfoId(String info, String name) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> idList = new ArrayList<String>();
		String[] temp = info.split(":");
		for (String string : temp) {
			idList.add(string);
		}
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			if("YSSQ".equals(name) || "WJFC".equals(name) || "WJQF".equals(name)){
				sb.append("SELECT DOCNUMBER, DOCNAME, VERSION FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
				sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
	            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GWKEYID = '" + idList.get(0) + "')) ");
			}else{
				sb.append("SELECT DOCNUMBER, DOCNAME, VERSION FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
				sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
	            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GWKEYID IN ");
	            sb.append("(SELECT APPLYRECORDID FROM GWPRINTRECOVERRECORD WHERE GWKEYID = '" + idList.get(0) + "')))");
			}
            ResultSet rs = state.executeQuery(sb.toString());
            while(rs.next()){
            	String fileNumber = rs.getString("DOCNUMBER");
            	String fileName = rs.getString("DOCNAME");
            	String version = rs.getString("VERSION");
            	map.put("number", fileNumber);
            	map.put("name", fileName);
            	map.put("version", version);
            	break;
            }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return map;
	}

	public static String getDocOidByBarCode(String barCode) {
		String docOid = "";
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT DOCVR FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
			sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE BARCODE = '" + barCode + "')) ");
            ResultSet rs = state.executeQuery(sb.toString());
            while(rs.next()){
            	docOid = rs.getString("DOCVR");
            }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return docOid;
	}

	public static CmPrintInfoBean getAllInfoByBarCode(String barCode) {
		CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
			sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE BARCODE = '" + barCode + "')) ");
            ResultSet rs = state.executeQuery(sb.toString());
            while(rs.next()){
            	String fileNumber = rs.getString("DOCNUMBER");
            	String fileName = rs.getString("DOCNAME");
            	String version = rs.getString("VERSION");
            	String phaseCode = rs.getString("PHASECODE");
            	String secret = rs.getString("SECRET");
            	String fileType = rs.getString("FILETYPE");
            	cmPrintInfoBean.setFileNumber(fileNumber);
            	cmPrintInfoBean.setFileName(fileName);
            	cmPrintInfoBean.setVersion(version);
            	cmPrintInfoBean.setPhaseCode(phaseCode);
            	cmPrintInfoBean.setSecret(secret);
            	cmPrintInfoBean.setFileType(fileType);
            }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return cmPrintInfoBean;
	}

	public static Map<String, String> getDocNumberAndVersionByBarCode(String barCode) {
		Map<String, String> map = new HashMap<String, String>();
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT DOCNUMBER, DOCNAME, VERSION FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
			sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE BARCODE = '" + barCode + "')) ");
            ResultSet rs = state.executeQuery(sb.toString());
            while(rs.next()){
            	map.put("number", rs.getString("DOCNUMBER"));
            	map.put("version", rs.getString("VERSION"));
            	break;
            }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return map;
	}

	public static boolean checkRepeatInputFile(String fileNumber, String version, String category) {
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM GWOTHERFILES WHERE FILENUMBER = '" + fileNumber + "'");
			if(!"".equals(version)){
				sb.append(" AND VERSION = '" + version + "'");
			}
			if("".equals(category)){
				sb.append(" AND DEPT != 'null'");
			}else{
				sb.append(" AND DEPT = 'null'");
			}
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return false;
	}

	public static String getKeyidByOid(String id) {
		String result = "";
		Connection conn = null;
		try {
			conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			Statement state = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE DOCVR = '"+id+"'");
			ResultSet rs = state.executeQuery(sb.toString());
			while(rs.next()){
				result = rs.getString("GWKEYID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return result;
	}

}
