package ext.casc.workflow.tree.mvc.builder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.processplan.helper.ZhuFuLinkUtil;
import com.glaway.mpm.util.*;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.*;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.gongshidinge.processor.GenerateGongShiUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.StringUtil;
import ext.casc.util.IBAHelper;
import ext.ptc.ViewWIHelper;
import org.jdom.Element;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.*;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.SetListGongShiDingEBuilder")
public class SetListGongShiDingEBuilder extends AbstractComponentBuilder {

    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        WorkItem wi = getWorkItemFromParams(arg1);
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        if (pbo instanceof WTDocument) {
            list.addAll(handleWTDocument((WTDocument) pbo));
        } else if (pbo instanceof WTChangeOrder2) {
            list.addAll(handleWTChangeOrder2((WTChangeOrder2) pbo));
        } else if (pbo instanceof ProcessEnvelope) {
            list.addAll(handleProcessEnvelope((ProcessEnvelope) pbo));
        }

        // 添加排序逻辑：首先按planNumber排序，其次按stepNumber排序
        Collections.sort(list, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> m1, Map<String, String> m2) {
                // 先比较planNumber
                String planNumber1 = m1.getOrDefault("planNumber", "");
                String planNumber2 = m2.getOrDefault("planNumber", "");
                // 尝试将planNumber解析为Long进行比较，以支持更大的数字范围
                try {
                    Long number1 = Long.parseLong(planNumber1);
                    Long number2 = Long.parseLong(planNumber2);
                    int compare = number1.compareTo(number2);
                    if (compare != 0) {
                        return compare;
                    }
                } catch (NumberFormatException e) {
                    // 如果无法解析为Long，则使用字符串比较
                    return planNumber1.compareTo(planNumber2);
                }


                // 如果planNumber相同，则比较stepNumber
                String stepNumber1 = m1.getOrDefault("stepNumber", "");
                String stepNumber2 = m2.getOrDefault("stepNumber", "");
                // 尝试将stepNumber解析为整数进行比较，以确保数字排序正确
                try {
                    Integer step1 = Integer.parseInt(stepNumber1);
                    Integer step2 = Integer.parseInt(stepNumber2);
                    return step1.compareTo(step2);
                } catch (NumberFormatException e) {
                    // 如果无法解析为整数，则使用字符串比较
                    return stepNumber1.compareTo(stepNumber2);
                }
            }
        });

        return list;
    }

    private WorkItem getWorkItemFromParams(ComponentParams params) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        Map parameterMap = commandBean.getRequestData().getParameterMap();
        Object ooid = parameterMap.get("oid");

        ReferenceFactory rf = new ReferenceFactory();
        String oid = ooid instanceof String[] ? ((String[]) ooid)[0] : (String) ooid;
        return (WorkItem) rf.getReference(oid).getObject();
    }

    private List<Map<String, String>> handleWTDocument(WTDocument document) throws Exception {
        MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(document);
        return getOperationMapByWTDocument(plan, null, true);
    }

    private List<Map<String, String>> handleWTChangeOrder2(WTChangeOrder2 ecn) throws Exception {
        MPMProcessPlan beforePlan = getProcessPlanFromChangeables(ChangeHelper2.service.getChangeablesBefore(ecn));
        MPMProcessPlan afterPlan = getProcessPlanFromChangeables(ChangeHelper2.service.getChangeablesAfter(ecn));

        if (afterPlan != null) {
            return getOperationMapByWTDocument(afterPlan, beforePlan, true);
        }
        return new ArrayList<Map<String, String>>();
    }

    private MPMProcessPlan getProcessPlanFromChangeables(QueryResult qr) throws WTException {
        while (qr.hasMoreElements()) {
            Object object = qr.nextElement();
            if (object instanceof MPMProcessPlan) {
                return (MPMProcessPlan) object;
            } else if (object instanceof WTDocument) {
                return MPMProcessPlanUtil.getProcessPlanByWTDocument((WTDocument) object);
            }
        }
        return null;
    }

    private List<Map<String, String>> handleProcessEnvelope(ProcessEnvelope pe) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        ArrayList members = ProcessEnvelopeUtil.getAllMembers(pe);

        for (Object obj : members) {
            if (obj instanceof MPMOperation) {
                MPMOperation operation = (MPMOperation) obj;
                Map<String, String> map = buildOperationMapFromEnvelope(operation);
                if (map != null) {
                    list.add(map);
                }
            }
        }
        return list;
    }

    private Map<String, String> buildOperationMapFromEnvelope(MPMOperation operation) throws Exception {
        MPMOperationUsageLink link = MPMProcessPlanUtil.getMPMOperationUsageLinkByMPMOperation(operation);
        if (link == null) return null;

        Persistable persistable = link.getRoleAObject();
        if (!(persistable instanceof MPMProcessPlan)) return null;

        MPMProcessPlan plan = (MPMProcessPlan) persistable;
        WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
        if (document == null) return null;

        Map<String, String> map = new HashMap<String, String>();

        // 基础信息设置
        setBasicDocumentInfo(map, document);
        setOperationInfo(map, operation, link);

        // 设备工时信息 - 修复：所有类型都应该有这个逻辑
	    try {
		    setToolingInfo(map, operation);
	    } catch (Exception e) {
		    throw new RuntimeException(e);
	    }

	    // 工时定额信息
        setWorkTimeQuotaInfo(map, operation, document, link.getOperationLabel());

        return map;
    }

    private void setBasicDocumentInfo(Map<String, String> map, WTDocument document) throws WTException {
        WTPart part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(document);
        if (part != null) {
            map.put("partNumber", part.getNumber());
            map.put("partName", part.getName());
        }

        map.put("planNumber", document.getNumber());
        map.put("planName", document.getName());
        map.put("docVersion", document.getVersionIdentifier().getValue() + "." +
                              document.getIterationIdentifier().getValue());
        map.put("state", document.getState().getState().getDisplay(Locale.CHINA));
    }

    private void setOperationInfo(Map<String, String> map, MPMOperation operation, MPMOperationUsageLink link) {
        String stepNumber = link.getOperationLabel();
        if (stepNumber.startsWith("0")) {
            stepNumber = stepNumber.substring(1);
        }
        map.put("stepNumber", stepNumber);
        map.put("stepName", operation.getName());

	    String workShop = null;
	    try {
		    workShop = IBAHelper.getIBAStringValue(operation, "workShop");
	    } catch (WTException e) {
		    throw new RuntimeException(e);
	    }
	    map.put("workshop", workShop);
        map.put("oid", PersistenceHelper.getObjectIdentifier(operation).toString());
    }

    private void setToolingInfo(Map<String, String> map, MPMOperation operation) throws Exception {
        MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(operation.getName(), SopConstants.SOP_TYPE_PROCEDUCENAME);
        String isSheBeiGS = IBAHelper.getIBAStringValue(tooling, "IsSheBeiGS");
        map.put("IsSheBeiGS", StringUtil.object2String(isSheBeiGS));
    }

    private void setWorkTimeQuotaInfo(Map<String, String> map, MPMOperation operation, WTDocument document, String operationLabel) throws Exception {
        String stepNumber = operationLabel;
        if (stepNumber.startsWith("0")) {
            stepNumber = stepNumber.substring(1);
        }

        List<GLZhuFuLink> record = ZhuFuLinkUtil.getRecord(document.getNumber(), document.getVersionIdentifier().getValue(), stepNumber);

        if (!record.isEmpty()) {
            map.put("danjian", "false");
            map.put("zhunjie", "false");
            map.put("danJianSheBeiGS", "false");
        } else {
            setIBAValues(map, operation);
        }
    }

    private void setIBAValues(Map<String, String> map, MPMOperation operation) throws Exception {
        map.put("danjian", StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "DJGS")));
        map.put("zhunjie", StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "ZJGS")));
        map.put("danJianSheBeiGS", StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "DanJianSheBeiGS")));
    }

    public static List<Map<String, String>> getOperationMapByWTDocument(MPMProcessPlan plan, MPMProcessPlan beforePlan, boolean flag) {
        if (plan == null) {
            return new ArrayList<Map<String, String>>();
        }

	    List<Map<String, String>> list = null;
	    try {
		    list = new ArrayList<Map<String, String>>();
		    WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
		    WTPart part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(document);
		    List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);

		    // 处理变更前后的步骤ID映射
		    Map<String, String> stepIdMap = new HashMap<String, String>();
		    if (beforePlan != null) {
		        stepIdMap = buildStepIdMap(beforePlan);
		    }

		    Map<String, String> afterIdMap = new HashMap<String, String>();
		    Map<String, String> workShopMap = new HashMap<String, String>();

		    for (MPMOperationUsageLink link : links) {
		        MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
		        MPMOperation operation = ViewWIHelper.getMpmOperation(master.getNumber());
		        String stepNumber = Util.formateInteger(link.getOperationLabel());

		        // 处理变更逻辑
		        if (beforePlan != null && shouldSkipOperation(operation, plan, stepIdMap, afterIdMap, stepNumber)) {
		            continue;
		        }

		        Map<String, String> map = buildOperationMap(operation, link, plan, document, part, workShopMap, flag);
		        list.add(map);
		    }
	    } catch (Exception e) {
		    throw new RuntimeException(e);
	    }

	    return list;
    }

    private static boolean shouldSkipOperation(MPMOperation operation, MPMProcessPlan plan,
                                               Map<String, String> stepIdMap, Map<String, String> afterIdMap,
                                               String stepNumber) throws WTException {
        String bsoID = IBAHelper.getIBAStringValue(operation, "BIAOSHI");
        if (StrUtil.isEmpty(bsoID) || "null".equals(bsoID)) {
            if (CollUtil.isEmpty(afterIdMap)) {
                afterIdMap.putAll(getStepIdMapByPlan(plan));
            }
            bsoID = afterIdMap.get(stepNumber);
            IBAHelper.setIBAStringValue(operation, "BIAOSHI", bsoID);
        }
        return StrUtil.isNotEmpty(bsoID) && stepIdMap.containsKey(bsoID);
    }

    private static Map<String, String> buildOperationMap(MPMOperation operation, MPMOperationUsageLink link,
                                                         MPMProcessPlan plan, WTDocument document, WTPart part,
                                                         Map<String, String> workShopMap, boolean flag) throws Exception {
        Map<String, String> map = new HashMap<String, String>();

        // 基础信息
        if (part != null) {
            map.put("partNumber", part.getNumber());
            map.put("partName", part.getName());
        }

        map.put("planNumber", plan.getNumber());
        map.put("planName", plan.getName());
        map.put("docVersion", document.getVersionIdentifier().getValue() + "." +
                              document.getIterationIdentifier().getValue());
        map.put("state", document.getState().getState().getDisplay(Locale.CHINA));

        String stepNumber = Util.formateInteger(link.getOperationLabel());
        map.put("stepNumber", stepNumber);
        map.put("stepName", operation.getName());

        // 工作坊信息
        setWorkShopInfo(map, operation, link, document, workShopMap);

        map.put("oid", PersistenceHelper.getObjectIdentifier(operation).toString());
        map.put("hasFZ", "否");

        // 设备工时信息 - 修复：所有类型都应该有这个逻辑
        setToolingInfoForOperation(map, operation);

        // 设置工时定额
        setOperationQuotaInfo(map, operation, document, stepNumber, flag);

        return map;
    }

    private static void setWorkShopInfo(Map<String, String> map, MPMOperation operation,
                                        MPMOperationUsageLink link, WTDocument document,
                                        Map<String, String> workShopMap) throws WTException {
        String workShop = IBAHelper.getIBAStringValue(operation, "workShop");
        if (StrUtil.isEmpty(workShop)) {
            if (workShopMap.isEmpty()) {
                workShopMap.putAll(GenerateGongShiUtil.getWorkShopByWTDocumentAndStep(document));
            }
            workShop = workShopMap.get(link.getOperationLabel());
            if (StrUtil.isNotEmpty(workShop)) {
                IBAHelper.setIBAStringValue(operation, "workShop", workShop);
            }
        }
        map.put("workshop", workShop);
    }

    private static void setToolingInfoForOperation(Map<String, String> map, MPMOperation operation) throws Exception {
        MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(operation.getName(), SopConstants.SOP_TYPE_PROCEDUCENAME);
        String isSheBeiGS = IBAHelper.getIBAStringValue(tooling, "IsSheBeiGS");
        map.put("isSheBeiGS", StringUtil.object2String(isSheBeiGS));
    }

    private static void setOperationQuotaInfo(Map<String, String> map, MPMOperation operation,
                                              WTDocument document, String stepNumber, boolean flag) throws WTException {
        // 默认设置IBA值
        map.put("danjian", StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "DJGS")));
        map.put("zhunjie", StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "ZJGS")));
        map.put("danJianSheBeiGS", StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "DanJianSheBeiGS")));

        if (flag) {
            List<GLZhuFuLink> record = ZhuFuLinkUtil.getRecord(document.getNumber(),
                    document.getVersionIdentifier().getValue(), stepNumber);

            if (!record.isEmpty()) {
                GLZhuFuLink zhuFuLink = record.get(0);
                if ("已关联".equals(zhuFuLink.getOperation())) {
                    handleLinkedOperation(map, zhuFuLink);
                }
            }
        }
    }

    private static void handleLinkedOperation(Map<String, String> map, GLZhuFuLink zhuFuLink) throws WTException {
        String fztechnicsnumber = zhuFuLink.getFztechnicsnumber();
        String fztechnicsversion = zhuFuLink.getFztechnicsversion();

        if (StrUtil.isNotEmpty(fztechnicsnumber) && StrUtil.isNotEmpty(fztechnicsversion)) {
            WTDocument doc = WTDocumentUtil.getLaestWTDocumentByNumberAndVersion(fztechnicsnumber, fztechnicsversion);
            if (doc != null) {
                map.put("hasFZ", "是");
                map.put("danjian", "false");
                map.put("zhunjie", "false");
                map.put("danJianSheBeiGS", "false");
            }
        }
    }

    private static Map<String, String> buildStepIdMap(MPMProcessPlan beforePlan) throws WTException {
        Map<String, String> stepIdMap = new HashMap<String, String>();
        List<MPMOperationUsageLink> beforeLinks = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(beforePlan);
        Map<String, String> idMap = new HashMap<String, String>();

        for (MPMOperationUsageLink beforeLink : beforeLinks) {
            MPMOperationMaster beforeMaster = (MPMOperationMaster) beforeLink.getRoleBObject();
            MPMOperation beforeOperation = ViewWIHelper.getMpmOperation(beforeMaster.getNumber());
            String stepNumber = Util.formateInteger(beforeLink.getOperationLabel());
            String stepId = IBAHelper.getIBAStringValue(beforeOperation, "BIAOSHI");

            if (StrUtil.isEmpty(stepId) || "null".equals(stepId)) {
                if (CollUtil.isEmpty(idMap)) {
                    idMap = getStepIdMapByPlan(beforePlan);
                }
                stepId = idMap.get(stepNumber);
                IBAHelper.setIBAStringValue(beforeOperation, "BIAOSHI", stepId);
            }
            stepIdMap.put(stepId, stepId);
        }

        return stepIdMap;
    }

    public static Map<String, String> getStepIdMapByPlan(MPMProcessPlan plan) throws WTException {
        Map<String, String> map = new HashMap<String, String>();
        WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);

        if (document == null) {
            return map;
        }

        InputStream is = null;
        try {
            String tempFilePath = PropertiesUtil.getTempPath() + File.separator + IdUtil.randomUUID() + File.separator;
            String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
            String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));

            ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
            File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");

            is = new FileInputStream(xmlFile);
            SWXMLUtil xmlUtil = new SWXMLUtil(is);
            Element rootElement = xmlUtil.getRootElement();
            Element qmFawTechnicsInfo = rootElement.getChild("QMFawTechnicsInfo");
            Element steps = qmFawTechnicsInfo.getChild("steps");
            List<Element> proceduresList = steps.getChildren("QMProcedureInfo");

            for (Element procedure : proceduresList) {
                String stepNumber = procedure.getAttributeValue("stepNumber");
                String bsoID = procedure.getAttributeValue("bsoID");
                map.put(stepNumber, bsoID);
            }
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return map;
    }

    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        JcaTableConfig tableConfig = createTableConfig(factory);
        addTableColumns(factory, tableConfig);
        return tableConfig;
    }

    private JcaTableConfig createTableConfig(ComponentConfigFactory factory) {
        JcaTableConfig tableConfig = (JcaTableConfig) factory.newTableConfig();
        tableConfig.setLabel("工时定额列表");
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
        tableConfig.setConfigurable(false);
        tableConfig.setId("ext.casc.workflow.tree.mvc.builder.SetListGongShiDingEBuilder");
        tableConfig.setSelectable(true);
        tableConfig.setActionModel("workItem_mpmoperation_action");
        return tableConfig;
    }

    private void addTableColumns(ComponentConfigFactory factory, JcaTableConfig tableConfig) {
        // 基础列配置
        addBasicColumns(factory, tableConfig);
        // 特殊列配置
        addSpecialColumns(factory, tableConfig);
    }

    private void addBasicColumns(ComponentConfigFactory factory, JcaTableConfig tableConfig) {
        // 使用数组避免重复代码
        String[] columnIds = {"partNumber", "partName", "planNumber", "planName", "docVersion", "state", "stepNumber", "stepName", "workshop", "hasFZ"};
        String[] columnLabels = {"部件图号", "部件名称", "工艺编号", "工艺名称", "版本", "状态", "工序号", "工序名称", "制造单位", "是否关联辅制"};

        for (int i = 0; i < columnIds.length; i++) {
            ColumnConfig config = factory.newColumnConfig(columnIds[i], true);
            config.setLabel(columnLabels[i]);
            config.setAutoSize(true);
            tableConfig.addComponent(config);
        }
    }

    private void addSpecialColumns(ComponentConfigFactory factory, JcaTableConfig tableConfig) {
        // 准结列
        ColumnConfig config = factory.newColumnConfig("zhunjie", true);
        config.setLabel("准结");
        config.setDataUtilityId("ext.casc.workflow.tree.MPMOperationDataUtility");
        config.setWidth(100);
        tableConfig.addComponent(config);

        // 单件人工列
        config = factory.newColumnConfig("danjian", true);
        config.setLabel("单件人工");
        config.setDataUtilityId("ext.casc.workflow.tree.MPMOperationDataUtility");
        config.setWidth(100);
        tableConfig.addComponent(config);

        // 单件设备列
        config = factory.newColumnConfig("danJianSheBeiGS", true);
        config.setLabel("单件设备");
        config.setDataUtilityId("ext.casc.workflow.tree.MPMOperationDataUtility");
        config.setWidth(120);
        tableConfig.addComponent(config);

        // 是否设备工时列
        config = factory.newColumnConfig("isSheBeiGS", true);
        config.setLabel("是否设备工时");
        config.setWidth(80);
        tableConfig.addComponent(config);
    }
}