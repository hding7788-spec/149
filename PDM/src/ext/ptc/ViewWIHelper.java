package ext.ptc;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.pds.StatementSpec;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.representation.Representation;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.ViewHelper;
import wt.viewmarkup.ViewMarkUpHelper;
import wt.viewmarkup.Viewable;
import wt.viewmarkup.WTMarkUp;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;

import com.ptc.windchill.mpml.processplan.MPMPartToProcessPlanLink;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToOperatedPartLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToPartLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.wvs.server.util.ETB;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.RepUpdateUtils;
import com.ptc.wvs.server.util.Util;

import ext.casc.constants.Constants;
import ext.casc.workflow.PrintHelper;
import ext.casc.workflow.util.MyComparator;

public class ViewWIHelper {

    public ViewWIHelper() {
    }

    public static void main(String args[]) {
        try {
            // java.io.InputStream in = new BufferedInputStream(new FileInputStream("C:\\ptc\\user.properties"));
            // Properties p = new Properties();
            // p.load(in);

            ViewWIHelper helper = new ViewWIHelper();
            helper.getPartByTuHao("11111112");
        } catch (WTException e) {
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static List getProcessPlan(WTPart part) {
        if (part == null) {
            return null;
        }
        List list = new ArrayList();
        MPMProcessPlan plan = null;
        try {
            QueryResult linkQR = MPMProcessPlanHelper.service.getPartToProcessPlanLinks(part);
            ObjectVector ov = new ObjectVector();
            for (; linkQR.hasMoreElements(); ov.addElement(plan)) {
                MPMPartToProcessPlanLink planLink = (MPMPartToProcessPlanLink) linkQR.nextElement();
                plan = planLink.getProcessPlan();
            }

            LatestConfigSpec lcsConfigSpec = new LatestConfigSpec();
            QueryResult qResult = new QueryResult(ov);
            for (qResult = lcsConfigSpec.process(qResult); qResult.hasMoreElements(); list.add((MPMProcessPlan) qResult
                    .nextElement()))
                ;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<MPMOperationUsageLink> getMPMOperationUsageLinkByMpmPr(MPMProcessPlan plan) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(plan).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        List<MPMOperationUsageLink> list = new ArrayList<MPMOperationUsageLink>();
        while (qResult.hasMoreElements()) {
            MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
            //MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
            //MPMOperation operation = getMpmOperation(master.getNumber());
            list.add(link);
        }
        Collections.sort(list, new MyComparator());
        return list;
    }

    public static List<MPMOperationUsageLink> getMPMOperationUsageLinkByMpmOper(MPMOperation operation) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(operation).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        List<MPMOperationUsageLink> list = new ArrayList<MPMOperationUsageLink>();
        while (qResult.hasMoreElements()) {
            MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
            //MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
            //MPMOperation childOperation = getMpmOperation(master.getNumber());
            list.add(link);
        }
        Collections.sort(list, new MyComparator());
        return list;
    }

    public static String getMPMOperationLabel(MPMOperation operation) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(operation.getMaster()).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleBObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        if (qResult.hasMoreElements()) {
            MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
            return link.getOperationLabel();
        }
        return "";
    }

    public static String getMPMOperationLabel(Persistable persistable,MPMOperation operation) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
        int[] index = { 0 };
        long longIdA = PersistenceHelper.getObjectIdentifier(persistable).getId();
        long longIdB = PersistenceHelper.getObjectIdentifier(operation.getMaster()).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, longIdA);
        qSpec.appendWhere(sCondition, index);
        qSpec.appendAnd();
        sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleBObjectRef.key.id",
                SearchCondition.EQUAL, longIdB);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        if (qResult.hasMoreElements()) {
            MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
            return link.getOperationLabel();
        }
        return "";
    }

    public static MPMOperation getMpmOperation(String number) throws WTException {
        QuerySpec qSpec = new QuerySpec(MPMOperation.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(MPMOperation.class, MPMOperation.NUMBER,
                SearchCondition.EQUAL, number);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        qResult = new LatestConfigSpec().process(qResult);
        if (qResult.hasMoreElements()) {
            MPMOperation mpmOperation = (MPMOperation) qResult.nextElement();
            return mpmOperation;
        }
        return null;
    }

    /**
     * 通过MPMOperation获取默认表示法的PVS相关文件
     *
     * @param mpmOperation
     * @return
     */
    public static String getAppDatasForPvsFileByMPMOperation(MPMOperation mpmOperation) {
        String fileName = "";
        try {
            WTProperties props = WTProperties.getLocalProperties();
            String wthomepath = props.getProperty("wt.home");
            QueryResult qrRep = PublishUtils.getRepresentations(mpmOperation);
            StringBuffer stringbuffer = new StringBuffer(300);
            byte[] buf = new byte[2048];
            while (qrRep.hasMoreElements()) {
                Object object = qrRep.nextElement();
                Representation representation = (Representation) object;
                representation = (Representation) ContentHelper.service.getContents(representation);
                Vector vector1 = ContentHelper.getContentList(representation);
                for (int l = 0; l < vector1.size(); l++) {
                    ContentItem contentitem = (ContentItem) vector1.elementAt(l);
                    if (!(contentitem instanceof ApplicationData)) {
                        continue;
                    }
                    ApplicationData applicationdata = (ApplicationData) contentitem;
                    if (applicationdata.getRole() != ContentRoleType.PRODUCT_VIEW_ED) {
                        continue;
                    }
                    ApplicationData applicationdata3 = RepUpdateUtils.processDeferredUpdateRepresentation(
                            applicationdata, representation);
                    if (applicationdata3 != null) {
                        representation = (Representation) ContentHelper.service.getContents(representation);
                        vector1 = ContentHelper.getContentList(representation);
                    }
                    break;
                }

                for (int j1 = 0; j1 < vector1.size(); j1++) {
                    ContentItem contentitem1 = (ContentItem) vector1.elementAt(j1);
                    if (!(contentitem1 instanceof ApplicationData)) {
                        continue;
                    }
                    ApplicationData applicationdata1 = (ApplicationData) contentitem1;
                    String tempFileName = applicationdata1.getFileName();
                    if (tempFileName.endsWith(".pvs") || tempFileName.endsWith(".prt")) {
                        tempFileName = String.valueOf((new Date()).getTime())
                                + tempFileName.substring(tempFileName.indexOf("."), tempFileName.length());
                        fileName = tempFileName;
                    }
                    String path = (new StringBuilder(String.valueOf(wthomepath))).append(File.separator)
                            .append("codebase").append(File.separator).append("netmarkets").append(File.separator)
                            .append("jsp")
                            .append(File.separator).append("flv").append(File.separator)
                            .append(new String(tempFileName.getBytes(), Charset.forName("GB2312")))
                            .toString();
                    InputStream is = ContentServerHelper.service.findContentStream(applicationdata1);
                    File file = new File(path);

                    FileOutputStream fos = new FileOutputStream(file);
                    int j = 0;
                    while ((j = is.read(buf, 0, buf.length)) >= 0) {
                        fos.write(buf, 0, j);
                    }
                    fos.close();

                    // ContentServerHelper.service.writeContentStream(applicationdata1, path);
                }

                QueryResult qResult = ViewMarkUpHelper.service.getMarkUps((Viewable) representation);
                while (qResult.hasMoreElements()) {
                    WTMarkUp wtmarkup = (WTMarkUp) qResult.nextElement();
                    String s3 = wtmarkup.getAdditionalInfo();
                    ETB etb = new ETB(s3);
                    String s4 = etb.getWcFile();
                    String s5 = s4;
                    int j = s4.indexOf("!>");
                    if (j >= 0 && j < s4.length() - 3) {
                        s5 = s4.substring(j + 2);
                        s3 = Util.SandR(s3, s4, s5);
                    }
                    String s6 = etb.getTargetWcFile();
                    j = s6.indexOf("!>");
                    if (j >= 0 && j < s6.length() - 3) {
                        s3 = Util.SandR(s3, s6, s6.substring(j + 2));
                    }
                    stringbuffer.append(s3).append("\n");

                    wtmarkup = (WTMarkUp) ContentHelper.service.getContents(wtmarkup);
                    Vector<ApplicationData> vector2 = ContentHelper.getContentListAll(wtmarkup);
                    for (ApplicationData applicationData : vector2) {
                        String tempFileName;
                        if (applicationData.getRole() == ContentRoleType.THUMBNAIL) {
                            tempFileName = FileUtil.setExtension(s5, "gif");
                        } else {
                            tempFileName = s5;
                        }
                        String path = (new StringBuilder(String.valueOf(wthomepath))).append(File.separator)
                                .append("codebase").append(File.separator).append("netmarkets").append(File.separator)
                                .append("jsp")
                                .append(File.separator).append("flv").append(File.separator)
                                .append(new String(tempFileName.getBytes(), Charset.forName("GB2312")))
                                .toString();

                        InputStream is = ContentServerHelper.service.findContentStream(applicationData);
                        FileOutputStream fos = new FileOutputStream(new File(path));
                        int k = 0;
                        while ((k = is.read(buf, 0, buf.length)) >= 0) {
                            fos.write(buf, 0, k);
                        }
                        fos.close();

                        // ContentServerHelper.service.writeContentStream(applicationData, path);
                    }
                }
            }
            if (fileName.contains(".")) {
                String etbName = fileName.substring(0, fileName.indexOf(".")) + ".etb";
                String etbpath = (new StringBuilder(String.valueOf(wthomepath))).append(File.separator)
                        .append("codebase").append(File.separator).append("netmarkets").append(File.separator)
                        .append("jsp").append(File.separator).append("flv").append(File.separator)
                        .append(new String(etbName.getBytes(), Charset.forName("GB2312")))
                        .toString();
                FileOutputStream fos = new FileOutputStream(new File(etbpath));
                fos.write(stringbuffer.toString().getBytes(Charset.forName("UTF-8")));
                fos.flush();
                fos.close();
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static WTPart getMPart(String number) {
        WTPart part = null;
        try {
            QuerySpec partQS = new QuerySpec(WTPartMaster.class);
            int index[] = new int[1];
            SearchCondition scType = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, "=",
                    number.toUpperCase());
            partQS.appendWhere(scType, index);
            QueryResult partQR = PersistenceHelper.manager.find((StatementSpec) partQS);
            if (partQR.hasMoreElements()) {
                WTPartMaster master = (WTPartMaster) partQR.nextElement();
                WTPartStandardConfigSpec cs = WTPartStandardConfigSpec.newWTPartStandardConfigSpec();
                wt.vc.views.View view = ViewHelper.service.getView("Manufacturing");
                cs.setView(view);
                QueryResult results = ConfigHelper.service.filteredIterationsOf(master, cs);
                LatestConfigSpec lcs = new LatestConfigSpec();
                results = lcs.process(results);
                if (results.hasMoreElements()) {
                    part = (WTPart) results.nextElement();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return part;
    }

    /**
     * 获取通过BOM分配的部件的集合
     *
     * @param operation
     * @return List<MPMOperationToPartLink> 部件集合
     * @throws WTException
     */
    public static List<MPMOperationToPartLink> getPartUsesLinkByMPMOPer(MPMOperation operation) throws WTException {
        List<MPMOperationToPartLink> list = new ArrayList<MPMOperationToPartLink>();
        QuerySpec qSpec = new QuerySpec(MPMOperationToPartLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(operation).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationToPartLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        Map<String, String> map = null;
        while (qResult.hasMoreElements()) {
            MPMOperationToPartLink link = (MPMOperationToPartLink) qResult.nextElement();
            WTPartMaster partMaster = (WTPartMaster) link.getRoleBObject();
            String qu = String.valueOf(link.getQuantity().getAmount());
            map = new HashMap<String, String>();
            map.put(partMaster.getNumber(), qu);
            list.add(link);
        }
        return list;
    }

    /**
     * 获取操作的非操作耗材部件
     *
     * @param operation
     * @return List<MPMOperationToOperatedPartLink> 非操作耗材部件集合
     * @throws WTException
     */
    public static List<MPMOperationToOperatedPartLink> getOperatedPartUsesLinkByMPMOPer(MPMOperation operation)
            throws WTException {
        List<MPMOperationToOperatedPartLink> list = new ArrayList<MPMOperationToOperatedPartLink>();
        QuerySpec qSpec = new QuerySpec(MPMOperationToOperatedPartLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(operation).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationToOperatedPartLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        while (qResult.hasMoreElements()) {
            MPMOperationToOperatedPartLink link = (MPMOperationToOperatedPartLink) qResult.nextElement();
            list.add(link);
        }
        return list;
    }

    /**
     * 获取通过资源分配的资源集合
     *
     * @param operation
     * @return List<MPMOperationToConsumableLink> 资源集合
     * @throws WTException
     */
    public static List<MPMOperationToConsumableLink> getMaterialByMPMOper(MPMOperation operation) throws WTException {
        List<MPMOperationToConsumableLink> list = new ArrayList<MPMOperationToConsumableLink>();
        QuerySpec qSpec = new QuerySpec(MPMOperationToConsumableLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(operation).getId();
        SearchCondition sCondition = new SearchCondition(MPMOperationToConsumableLink.class, "roleAObjectRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        while (qResult.hasMoreElements()) {
            MPMOperationToConsumableLink link = (MPMOperationToConsumableLink) qResult.nextElement();
            list.add(link);
        }
        return list;
    }

    public static boolean detailPrintedFlag(MPMOperation operation) throws WTException {
        // 获取使用过的部件
        List<MPMOperationToPartLink> list = getPartUsesLinkByMPMOPer(operation);
        // 获取非操作耗材部件
        List<MPMOperationToConsumableLink> materialList = getMaterialByMPMOper(operation);
        // 获取资源分配的资源
        List<MPMOperationToOperatedPartLink> operatedList = getOperatedPartUsesLinkByMPMOPer(operation);
        if ((list != null && !list.isEmpty()) || (materialList != null && !materialList.isEmpty())
                || (operatedList != null && !operatedList.isEmpty())) {
            return true;
        }

        return false;
    }

    public static WTPart getPartByTuHao(String cindex) throws WTException, WTPropertyVetoException {
        WTPart part = null;
        QuerySpec qSpec = new QuerySpec();
        int index0 = qSpec.addClassList(WTPart.class, true);
        int index1 = qSpec.addClassList(StringValue.class, false);
        int index2 = qSpec.addClassList(StringDefinition.class, false);

        String[] aliases = new String[3];
        aliases[0] = qSpec.getFromClause().getAliasAt(index0);
        aliases[1] = qSpec.getFromClause().getAliasAt(index1);
        aliases[2] = qSpec.getFromClause().getAliasAt(index2);

        TableColumn tc0 = new TableColumn(aliases[0], "ida2a2");
        TableColumn tc1 = new TableColumn(aliases[1], "IDA3A4");
        TableColumn tc2 = new TableColumn(aliases[1], "IDA3A6");
        TableColumn tc3 = new TableColumn(aliases[1], "value");
        TableColumn tc4 = new TableColumn(aliases[2], "IDA2A2");
        TableColumn tc5 = new TableColumn(aliases[2], "name");

        qSpec.appendWhere(new SearchCondition(tc0, "=", tc1), new int[] { index0, index1 });
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(tc2, "=", tc4), new int[] { index1, index2 });
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(tc3, "=", new ConstantExpression(cindex)), new int[] { index1 });
        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(tc5, "=", new ConstantExpression("CINDEX")), new int[] { index2 });
        // System.out.println("----sql:"+qSpec.toString());
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        if (qResult.hasMoreElements()) {
            Persistable[] persistables = (Persistable[]) qResult.nextElement();
            part = (WTPart) persistables[0];
            WTPartStandardConfigSpec cs = WTPartStandardConfigSpec.newWTPartStandardConfigSpec();
            wt.vc.views.View view = ViewHelper.service.getView("Manufacturing");
            cs.setView(view);
            QueryResult results = ConfigHelper.service.filteredIterationsOf(part.getMaster(), cs);
            LatestConfigSpec lcs = new LatestConfigSpec();
            results = lcs.process(results);
            if (results.hasMoreElements()) {
                part = (WTPart) results.nextElement();
            }
        }
        return part;
    }



    public static WfProcess getWfProcessByPbo(WTObject pbo) {
        WfProcess process = null;
        try {
            Enumeration<WfProcess> enumeration = WfEngineHelper.service.getAssociatedProcesses(pbo,
                    WfState.CLOSED_COMPLETED_EXECUTED);
            while (enumeration.hasMoreElements()) {
                process = enumeration.nextElement();
                String wfName = process.getName();
                // System.out.println("-------process:"+wfName);
                if (wfName.indexOf(Constants.WF_MPMPPLAN_APPROVAL) != -1) {
                    return process;
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }

        return null;
    }

}
