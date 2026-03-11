package ext.casc.gongshidinge.mvc.builder;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.*;
import com.ptc.mvc.components.*;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAHelper;
import ext.casc.workflow.util.WorkflowUtil;
import ext.ptc.ViewWIHelper;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTCollection;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;

import java.util.*;

@ComponentBuilder("gongshi_querystate")
public class QueryGongShiStateBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        String work = String.valueOf(params.getParameter("work"));
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if("search".equals(work)) {
            String number = ProcessUtil.getNotNullParam(params.getParameter("technicsNumber"));
            String name = ProcessUtil.getNotNullParam(params.getParameter("technicsName"));
            if(StrUtil.isNotEmpty(number) || StrUtil.isNotEmpty(name)) {
                QuerySpec qs = new QuerySpec(WTDocument.class);
                int index[] = {0};
                if(StrUtil.isNotEmpty(number)){
                    qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "%" + number + "%", false), index);
                    qs.appendAnd();
                }
                if(StrUtil.isNotEmpty(name)){
                    qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + name + "%", false), index);
                    qs.appendAnd();
                }
//                TypeUtil.getTypeQuery(WTDocument.class, "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", qs);
//                qs.appendAnd();
                qs.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.latest", "TRUE"), index);
                QueryResult qr = PersistenceHelper.manager.find(qs);
                while(qr.hasMoreElements()) {
                    WTDocument document = (WTDocument) qr.nextElement();
                    String gongShiDingEState = IBAHelper.getIBAStringValue(document, "GongShiDingEState");
                    if("已导入".equals(gongShiDingEState) || "已审核".equals(gongShiDingEState)) {
                        continue;
                    }
                    Map<String, String> map = new HashMap<>();
                    String docNumber = document.getNumber();
                    try {
                        ReferenceFactory rf = new ReferenceFactory();
                        docNumber = document.getNumber() + "@" + rf.getReferenceString(document);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    map.put("gongshiNumber", docNumber);
                    map.put("name", document.getName());
                    map.put("docVersion", document.getIterationDisplayIdentifier().toString());
                    map.put("gongshiState", gongShiDingEState);
                    MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(document);
                    String workingPENumber = "";
                    String workingPEOwner = "";
                    if(plan != null) {
                        List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
                        for(MPMOperationUsageLink link : links) {
                            if(StrUtil.isNotEmpty(workingPENumber)){
                                break;
                            }
                            if(link.getRoleBObject() != null && link.getRoleBObject() instanceof MPMOperationMaster) {
                                MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
                                MPMOperation operation = ViewWIHelper.getMpmOperation(master.getNumber());
                                QueryResult result = PersistenceHelper.manager.navigate(operation, "theProcessEnvelope", EnvelopeMemberLink.class, false);
                                while(result.hasMoreElements()) {
                                    EnvelopeMemberLink memberLink = (EnvelopeMemberLink) result.nextElement();
                                    ProcessEnvelope pe = memberLink.getProcessEnvelope();
                                    if(!"已批准".equals(pe.getState().getState().getDisplay(Locale.CHINA))) {
                                        workingPENumber = pe.getNumber();
                                        workingPEOwner = pe.getCreator().getFullName();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    map.put("workingPENumber", workingPENumber);
                    map.put("workingPEOwner", workingPEOwner);
                    String workflowInfo = "-";
                    WfProcess process = null;
                    if("space".equals(document.getVersionInfo().getIdentifier().getValue())) {
                        process = WorkflowUtil.getWfProcessLikeName(document, "工艺文件签审流程");
                    } else {
                        WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(document);
                        Iterator it = coll.iterator();
                        if (it.hasNext()) {
                            WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                            if (ecn != null) {
                                process = WorkflowUtil.getWfProcessLikeName(ecn, "工艺更改单签审流程");
                            }
		}
                    }
                    WfAssignedActivity workActivity = null;
                    WfAssignedActivity shenpiActivity = null;
                    if(process != null) {
                        QuerySpec spec = new QuerySpec(WfAssignedActivity.class);
                        spec.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key", SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(process)));
                        spec.appendOrderBy(new OrderBy(new ClassAttribute(WfAssignedActivity.class, WfAssignedActivity.CREATE_TIMESTAMP), false), index);
                        QueryResult result = PersistenceHelper.manager.find(spec);
                        while (result.hasMoreElements()) {
                            WfAssignedActivity wfactivity = (WfAssignedActivity) result.nextElement();
                            if("工时定额编制".equals(wfactivity.getName())) {
                                workActivity = wfactivity;
                            } else if("工时定额审批".equals(wfactivity.getName())) {
                                shenpiActivity = wfactivity;
                            }
                        }
                    }
                    if(workActivity == null && shenpiActivity == null) {
                        workflowInfo = "工艺文件签审中";
                    }
                    if(workActivity != null && workActivity.getState().equals(WfState.OPEN_RUNNING)) {
                        workflowInfo = "工时定额编制中";
                    } else {
                        if(shenpiActivity != null) {
                            if(shenpiActivity.getState().equals(WfState.OPEN_RUNNING)) {
                                workflowInfo = "工时定额审批中";
                            } else if(shenpiActivity.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
                                workflowInfo = "工时定额编制审批完成";
                            }
                        }
                    }
                    map.put("workflowInfo", workflowInfo);
                    list.add(map);
                }
            }
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setId("gongshi_querystate");
        tableConfig.setSelectable(true);
        tableConfig.setConfigurable(true);

        tableConfig.setActionModel("custom_SearchWorkItem_actions");
        tableConfig.setLabel("工时定额状态源查询表");

        ColumnConfig number = factory.newColumnConfig("gongshiNumber", true);
        number.setAutoSize(true);
        number.setLabel("工艺文件编号");
        number.setDataUtilityId("PartDataUtility");
        tableConfig.addComponent(number);

        ColumnConfig name = factory.newColumnConfig("name", true);
        name.setAutoSize(true);
        name.setLabel("工艺文件名称");
        tableConfig.addComponent(name);

        ColumnConfig version = factory.newColumnConfig("docVersion", true);
        version.setAutoSize(true);
        version.setLabel("版本");
        tableConfig.addComponent(version);

        ColumnConfig gongshiState = factory.newColumnConfig("gongshiState", true);
        gongshiState.setAutoSize(true);
        gongshiState.setLabel("工时定额状态");
        tableConfig.addComponent(gongshiState);

        ColumnConfig workingPENumber = factory.newColumnConfig("workingPENumber", true);
        workingPENumber.setAutoSize(true);
        workingPENumber.setLabel("关联工时定额签审包编号");
        tableConfig.addComponent(workingPENumber);

        ColumnConfig workingPEOwner = factory.newColumnConfig("workingPEOwner", true);
        workingPEOwner.setAutoSize(true);
        workingPEOwner.setLabel("关联工时定额签审包负责人");
        tableConfig.addComponent(workingPEOwner);

        ColumnConfig workflowInfo = factory.newColumnConfig("workflowInfo", true);
        workflowInfo.setAutoSize(true);
        workflowInfo.setLabel("工艺签审流程状态");
        tableConfig.addComponent(workflowInfo);

        return tableConfig;
    }

}
