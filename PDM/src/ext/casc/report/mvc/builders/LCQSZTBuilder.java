package ext.casc.report.mvc.builders;

import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.process.ProcessConstants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.workflow.PrintHelper;
import org.apache.commons.lang.StringUtils;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfBallot;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@ComponentBuilder("SearchWorkItem_table_id_lcqsz")
public class LCQSZTBuilder extends AbstractComponentBuilder {
    public static String partNumber;
    public static String cpdh;
    public static String xhdh;
    public static String jdbj;

    @SuppressWarnings("deprecation")
    public static List getQueryWfprocess(long qidongzhe, String contain, String startdate, String enddate, String sjlx, String lczt, String name, String number, String lcmc, String lchj, WTUser fzr) {

        List<LCQSZTBWfprocessEntity> list = new ArrayList<LCQSZTBWfprocessEntity>();
        try {
            int index = 0;
            WfProcess process = null;
            TimeZone tz = WTContext.getContext().getTimeZone();
            Calendar ca = Calendar.getInstance(tz);
            QuerySpec qs = new QuerySpec(WfProcess.class);
            if (startdate != null && !"".equals(startdate)) {
                Date dateFrom = WTStandardDateFormat.parse(startdate, "yyyy/M/d");
                // 加入时区信息
                ca.setTime(dateFrom);
                dateFrom = ca.getTime();
                if (index > 0) {
                    qs.appendAnd();
                }
                index++;
                SearchCondition sc1 = new SearchCondition(WfProcess.class, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime() + 8 * 60 * 60 * 1000));
                qs.appendSearchCondition(sc1);
            }
            if (enddate != null && !"".equals(enddate)) {
                Date dateFrom1 = WTStandardDateFormat.parse(enddate, "yyyy/M/d");
                // 加入时区信息
                ca.setTime(dateFrom1);
                dateFrom1 = ca.getTime();
                if (index > 0) {
                    qs.appendAnd();
                }
                index++;
                SearchCondition sc11 = new SearchCondition(WfProcess.class, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime() + 8 * 60 * 60
                        * 1000));
                qs.appendSearchCondition(sc11);
            }

            if (lczt != null && !"".equals(lczt)) {
                if ("正在运行".equals(lczt)) {
                    if (index > 0) {
                        qs.appendAnd();
                    }
                    index++;
                    qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.STATE, SearchCondition.EQUAL, "OPEN_RUNNING"));
                } else if ("已执行".equals(lczt)) {
                    if (index > 0) {
                        qs.appendAnd();
                    }
                    index++;
                    qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.STATE, SearchCondition.EQUAL, "CLOSED_COMPLETED_EXECUTED"));

                } else if ("已终止".equals(lczt)) {
                    if (index > 0) {
                        qs.appendAnd();
                    }
                    index++;
                    qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.STATE, SearchCondition.EQUAL, "CLOSED_TERMINATED"));
                }
            }
            if (contain != null && !"".equals(contain)) {
                if (contain.startsWith("OR:wt.pdmlink.PDMLinkProduct")) {
                    contain = contain.split("OR:wt.pdmlink.PDMLinkProduct:")[1];
                    if (index > 0) {
                        qs.appendAnd();
                    }
                    index++;
                    qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.CONTAINER_ID, SearchCondition.EQUAL, Long.valueOf(contain).longValue()));
                    // qs.appendWhere(new SearchCondition(WfProcess.class,
                    // WfProcess.CONTAINER_ID,
                    // Long.valueOf(contain).longValue()), true));
                }
            }
            if (sjlx != null && !"".equals(sjlx)) {
                if ("部件".equals(sjlx)) {
                    if (index > 0) {
                        qs.appendAnd();
                    }
                    index++;
                    qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.BUSINESS_OBJ_REFERENCE, SearchCondition.LIKE, "%" + "wt.part.WTPart" + "%", true));
                } else if ("文档".equals(sjlx)) {

                    if (index > 0) {
                        qs.appendAnd();
                    }
                    index++;
                    qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.BUSINESS_OBJ_REFERENCE, SearchCondition.LIKE, "%" + "wt.doc.WTDocument" + "%", true));
                } else if ("签审包".equals(sjlx)) {
                    if (index > 0) {
                        qs.appendAnd();
                    }
                    index++;
                    qs.appendWhere(new SearchCondition(WfProcess.class, WfProcess.BUSINESS_OBJ_REFERENCE, SearchCondition.LIKE, "%" + "ext.ases.envelope.ProcessEnvelope" + "%", true));
                }
            }
            if (qidongzhe != 0) {
                if (index > 0) {
                    qs.appendAnd();
                }
                index++;
                qs.appendWhere(new SearchCondition(WfProcess.class, "creator.key.id", SearchCondition.EQUAL, qidongzhe));
            }
            if (lcmc != null && !"".equals(lcmc)) {
                WfProcessDefinition processDefinition = WfDefinerHelper.service.getProcessDefinition(lcmc);
                if(processDefinition != null) {
                    long masterId = processDefinition.getProcessTemplate().getMaster().getPersistInfo().getObjectIdentifier().getId();
                    QuerySpec querySpec = new QuerySpec(WfProcessTemplate.class);
                    querySpec.appendWhere(new SearchCondition(WfProcessTemplate.class, "masterReference.key.id", SearchCondition.EQUAL, masterId));
                    if(ProcessConstants.LC_4.equals(lcmc)) {
                        WfProcessDefinition pd = WfDefinerHelper.service.getProcessDefinition(ProcessConstants.LC_3);
                        if(pd != null){
                            long mid = pd.getProcessTemplate().getMaster().getPersistInfo().getObjectIdentifier().getId();
                            querySpec.appendOr();
                            querySpec.appendWhere(new SearchCondition(WfProcessTemplate.class, "masterReference.key.id", SearchCondition.EQUAL, mid));
                        }
                    } else if(ProcessConstants.LC_6.equals(lcmc)) {
                        WfProcessDefinition pd = WfDefinerHelper.service.getProcessDefinition(ProcessConstants.LC_5);
                        if(pd != null){
                            long mid = pd.getProcessTemplate().getMaster().getPersistInfo().getObjectIdentifier().getId();
                            querySpec.appendOr();
                            querySpec.appendWhere(new SearchCondition(WfProcessTemplate.class, "masterReference.key.id", SearchCondition.EQUAL, mid));
                        }
                    }
                    QueryResult queryResult = PersistenceHelper.manager.find(querySpec);
                    if(queryResult.size()>0){
                        long[] value = new long[queryResult.size()];
                        int i = 0;
                        while(queryResult.hasMoreElements()) {
                            WfProcessTemplate template = (WfProcessTemplate) queryResult.nextElement();
                            value[i] = template.getPersistInfo().getObjectIdentifier().getId();
                            i++;
                        }
                        if (index > 0) {
                            qs.appendAnd();
                        }
                        index++;
                        qs.appendWhere(new SearchCondition(new ClassAttribute(WfProcess.class, "template.key.id"), SearchCondition.IN, new ArrayExpression(value)));
                    }
                }
            }
            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                process = (WfProcess) qr.nextElement();
                if(lchj != null && !"".equals(lchj) && fzr != null){
                    boolean isHas = false;
                    List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
                    activityList = PrintHelper.getActivities(process, activityList);
                    for(WfAssignedActivity activity : activityList) {
                        if(isHas){
                            break;
                        }
                        if(activity.getName().equals(lchj)){
                            Enumeration en1 = activity.getAssignments();
                            while(en1.hasMoreElements()){
                                if(isHas){
                                    break;
                                }
                                WfAssignment wfassignment = (WfAssignment) en1.nextElement();
                                Enumeration en2 = wfassignment.checkBallotStatus().elements();
                                while(en2.hasMoreElements()){
                                    WfBallot wfballot = (WfBallot) en2.nextElement();
                                    WTPrincipal wtp = wfballot.getVoter().getPrincipal();
                                    if(wtp instanceof WTUser) {
                                        WTUser user = (WTUser) wtp;
                                        if(fzr.getPersistInfo().getObjectIdentifier().getId() == user.getPersistInfo().getObjectIdentifier().getId()) {
                                            isHas = true;
                                            break;
                                        }
                                    }else if(wtp instanceof WTGroup){
                                        WTGroup group = (WTGroup) wtp;
                                        if(group.isMember(fzr)){
                                            isHas = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(isHas){
                        LCQSZTBWfprocessEntity entity = new LCQSZTBWfprocessEntity();
                        entity.setXinghao(process.getContainerName().toString());
                        entity.setWenjianoid(process.getBusinessObjReference());
                        entity.setWenjianbianhao(getWenjianBianhao(process));
                        entity.setWenjianmingcheng(getWenjianmingcheng(process));
                        entity.setLiuchengmingcheng(process.getTemplate().getName());
                        entity.setQidongzhe(process.getCreator().getFullName());
                        entity.setZhuangtai(process.getState().getDisplay(Locale.CHINA));
                        entity.setFaqishijian(process.getCreateTimestamp().toString());
                        if (process.getEndTime()!=null) {
                            entity.setWanchengshijian(process.getEndTime().toString());
                        }else{
                            entity.setWanchengshijian("");
                            List<String> info = getProcessStateInfo(process);
                            if(!info.isEmpty()){
                                String infoStr = StringUtils.join(info.toArray(),";");
                                entity.setHuanjiexinxi(infoStr);
                            }
                        }
                        list.add(entity);
                    }
                }else{
                    if (!"".equals(process) && !"null".equals(process) && process != null) {
                        LCQSZTBWfprocessEntity entity = new LCQSZTBWfprocessEntity();
                        entity.setXinghao(process.getContainerName().toString());
                        entity.setWenjianoid(process.getBusinessObjReference());
                        entity.setWenjianbianhao(getWenjianBianhao(process));
                        entity.setWenjianmingcheng(getWenjianmingcheng(process));
                        entity.setLiuchengmingcheng(process.getTemplate().getName());
                        entity.setQidongzhe(process.getCreator().getFullName());
                        entity.setZhuangtai(process.getState().getDisplay(Locale.CHINA));
                        entity.setFaqishijian(process.getCreateTimestamp().toString());
                        if (process.getEndTime()!=null) {
                            entity.setWanchengshijian(process.getEndTime().toString());
                        }else{
                            entity.setWanchengshijian("");
                            List<String> info = getProcessStateInfo(process);
                            if(!info.isEmpty()){
                                String infoStr = StringUtils.join(info.toArray(),";");
                                entity.setHuanjiexinxi(infoStr);
                            }
                        }
                        list.add(entity);
                    }
                }
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        String work = String.valueOf(params.getParameter("work"));
        ArrayList list = new ArrayList();
        if ("search".equals(work)) {
            String qidongzhe = ProcessUtil.getNotNullParam(params.getParameter("applicant"));
            String contain = ProcessUtil.getNotNullParam(params.getParameter("containerTypeList"));
            NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
            String startdate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
            String enddate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));
            if (!"".equals(enddate) && !"null".equals(enddate) && enddate != null) {
                enddate = addOneDate(enddate);
            }
            String sjlx = ProcessUtil.getNotNullParam(params.getParameter("sjlx"));
            String lczt = ProcessUtil.getNotNullParam(params.getParameter("lczt"));
            String name = ProcessUtil.getNotNullParam(params.getParameter("name"));
            String number = ProcessUtil.getNotNullParam(params.getParameter("number"));
            String lcmc = ProcessUtil.getNotNullParam(params.getParameter("lcmc"));
            String lchj = ProcessUtil.getNotNullParam(params.getParameter("lchj"));
            String lchjfzr = ProcessUtil.getNotNullParam(params.getParameter("lchjfzr"));
            long qidongzheId = 0;
            WTUser fzr = null;
            if (!"".equals(qidongzhe) && !"null".equals(qidongzhe) && qidongzhe != null) {
                String qidongString = qidongzhe.split(",")[0].split("=")[1];
                if (!"".equals(qidongString) && !"null".equals(qidongString) && qidongString != null) {
                    QuerySpec qs = new QuerySpec(WTUser.class);
                    qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, qidongString));
                    QueryResult qr = PersistenceHelper.manager.find(qs);
                    while (qr.hasMoreElements()) {
                        WTUser user = (WTUser) qr.nextElement();
                        qidongzheId = user.getPersistInfo().getObjectIdentifier().getId();
                    }
                }
            }
            if (!"".equals(lchjfzr) && !"null".equals(lchjfzr) && lchjfzr != null) {
                String fzrString = lchjfzr.split(",")[0].split("=")[1];
                if (!"".equals(fzrString) && !"null".equals(fzrString) && fzrString != null) {
                    QuerySpec qs = new QuerySpec(WTUser.class);
                    qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, fzrString));
                    QueryResult qr = PersistenceHelper.manager.find(qs);
                    while (qr.hasMoreElements()) {
                        fzr = (WTUser) qr.nextElement();
                    }
                }
            }

            return getQueryWfprocess(qidongzheId, contain, startdate, enddate, sjlx, lczt, name, number,lcmc,lchj,fzr);

        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setId("SearchWorkItem_table_id_lcqsz");
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setConfigurable(true);
        tableConfig.setSelectable(true);
        tableConfig.setSingleSelect(true);
        tableConfig.setActionModel("custom_SearchWorkItem_actions");
        tableConfig.setLabel("流程签署状态管理表");

        ColumnConfig shangxiawen = factory.newColumnConfig("xinghao", true);
        shangxiawen.setAutoSize(true);
        shangxiawen.setLabel("上下文");
        tableConfig.addComponent(shangxiawen);

        ColumnConfig bianhao = factory.newColumnConfig("wenjianbianhao", true);
        bianhao.setAutoSize(true);
        bianhao.setLabel("文件编号");
        bianhao.setDataUtilityId("AllWorkFlowDataUtility");
        tableConfig.addComponent(bianhao);

        ColumnConfig wenjianName = factory.newColumnConfig("wenjianmingcheng", true);
        wenjianName.setAutoSize(true);
        wenjianName.setLabel("文件名称");
        tableConfig.addComponent(wenjianName);

        ColumnConfig liuchengzhuti = factory.newColumnConfig("liuchengmingcheng", true);
        liuchengzhuti.setAutoSize(true);
        liuchengzhuti.setLabel("流程名称");
        tableConfig.addComponent(liuchengzhuti);

        ColumnConfig startor = factory.newColumnConfig("qidongzhe", true);
        startor.setAutoSize(true);
        startor.setLabel("启动者");
        tableConfig.addComponent(startor);

        ColumnConfig state = factory.newColumnConfig("zhuangtai", true);
        state.setLabel("状态");
        state.setAutoSize(true);
        tableConfig.addComponent(state);

        ColumnConfig faqishijian = factory.newColumnConfig("faqishijian", true);
        faqishijian.setLabel("流程发起时间");
        faqishijian.setAutoSize(true);
        tableConfig.addComponent(faqishijian);

        ColumnConfig wanchengshijian = factory.newColumnConfig("wanchengshijian", true);
        wanchengshijian.setLabel("流程完成时间");
        wanchengshijian.setAutoSize(true);
        tableConfig.addComponent(wanchengshijian);

        ColumnConfig huanjiexinxi = factory.newColumnConfig("huanjiexinxi", true);
        huanjiexinxi.setLabel("流程当前环节信息");
        huanjiexinxi.setAutoSize(true);
        tableConfig.addComponent(huanjiexinxi);


        return tableConfig;
    }

    private String addOneDate(String date) throws ParseException {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date de = df.parse(date);
        calendar.setTime(de);
        calendar.add(calendar.DATE, 1);
        return new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime());
    }

    private static String getActivityTime(Boolean flag, WfProcess process, String name) {
        List<WfBlock> allWfBlocks;
        try {
            allWfBlocks = PrintHelper.getAllBlock(process);
            List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
            activityList = PrintHelper.getActivities(process, activityList);
            for (WfBlock wfblock : allWfBlocks) {
                PrintHelper.getActivities(wfblock, activityList);
            }
            for (WfAssignedActivity wfAssignedActivity : activityList) {
                if (wfAssignedActivity.getTemplate().getName().equals(name)) {
                    SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    if (flag) {
                        String fbegin = dFormat.format(wfAssignedActivity.getCreateTimestamp());
                        return fbegin;
                    } else {
                        String end = dFormat.format(wfAssignedActivity.getEndTime());
                        return end;
                    }
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String getWenjianBianhao(WfProcess wf) {
        if (wf != null && !"".equals(wf) && !"null".equals(wf)) {
            try {

                String objReference = wf.getBusinessObjReference();

                if (objReference != null && !"".equals(objReference)) {
                    ReferenceFactory refefence = new ReferenceFactory();
                    Persistable persistable = refefence.getReference(objReference).getObject();
                    if (persistable instanceof WTDocument) {
                        WTDocument doc = (WTDocument) persistable;
                        if (doc.getNumber() != null && !"".equals(doc.getNumber()) && !"null".equals(doc.getNumber())) {
                            return doc.getNumber();
                        } else {
                            return "";
                        }
                    } else if (persistable instanceof WTPart) {
                        WTPart part = (WTPart) persistable;
                        if (part.getNumber() != null && !"".equals(part.getNumber()) && !"null".equals(part.getNumber())) {
                            return part.getNumber();
                        } else {
                            return "";
                        }
                    } else if (persistable instanceof ProcessEnvelope) {
                        ProcessEnvelope pe = (ProcessEnvelope) persistable;
                        if (pe.getNumber() != null && !"".equals(pe.getNumber()) && !"null".equals(pe.getNumber())) {
                            return pe.getNumber();
                        } else {
                            return "";
                        }
                    } else if(persistable instanceof ChangePackaged) {
                        ChangePackaged cp = (ChangePackaged) persistable;
                        if (cp.getNumber() != null && !"".equals(cp.getNumber()) && !"null".equals(cp.getNumber())) {
                            return cp.getNumber();
                        } else {
                            return "";
                        }
                    }
                    return "";
                } else {
                    return "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return "";
        }
        return "";

    }

    public static String getWenjianmingcheng(WfProcess wf) {
        if (wf != null && !"".equals(wf) && !"null".equals(wf)) {
            try {
                String objReference = wf.getBusinessObjReference();
                if (objReference != null && !"".equals(objReference) && !"null".equals(objReference)) {
                    ReferenceFactory refefence = new ReferenceFactory();
                    Persistable persistable = refefence.getReference(objReference).getObject();
                    if (persistable instanceof WTDocument) {
                        WTDocument doc = (WTDocument) persistable;
                        if (doc.getName() != null && !"".equals(doc.getName()) && !"null".equals(doc.getName())) {
                            return doc.getName();
                        } else {
                            return "";
                        }
                    } else if (persistable instanceof WTPart) {
                        WTPart part = (WTPart) persistable;
                        if (part.getName() != null && !"".equals(part.getName()) && !"null".equals(part.getName())) {
                            return part.getName();
                        } else {
                            return "";
                        }
                    } else if (persistable instanceof ProcessEnvelope) {
                        ProcessEnvelope pe = (ProcessEnvelope) persistable;
                        if (pe.getName() != null && !"".equals(pe.getName()) && !"null".equals(pe.getName())) {
                            return pe.getName();
                        } else {
                            return "";
                        }
                    } else if(persistable instanceof ChangePackaged) {
                        ChangePackaged cp = (ChangePackaged) persistable;
                        if (cp.getName() != null && !"".equals(cp.getName()) && !"null".equals(cp.getName())) {
                            return cp.getName();
                        } else {
                            return "";
                        }
                    }
                    return "";
                } else {
                    return "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return "";
        }
        return "";
    }

    /**
     * 获取流程当前活动环节 add by cjh
     * @param pbo
     * @return
     */
    public static List<String> getProcessStateInfo(WfProcess process){
        String message = "";
        List<String> activityStr = new ArrayList<String>();
        try {
            List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
            activityList = getActivities(process, activityList);
            Iterator iterator = activityList.iterator();
            while(iterator.hasNext()){
                int k = 0;
                WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
                String activityName = wfactivity.getName();
                Timestamp startTime = wfactivity.getStartTime();
                System.out.println("==>>activityName"+activityName);
                Enumeration en1 = null;
                Enumeration en2 = null;
                en1 = wfactivity.getAssignments();
                String userName = "";
                for (int i = 0; en1 != null && en1.hasMoreElements(); i++) {
                    WfAssignment wfassignment = (WfAssignment) en1.nextElement();
                    en2 = wfassignment.checkBallotStatus().elements();
                    for (int j = 0; en2 != null && en2.hasMoreElements(); j++) {
                        WfBallot wfballot = (WfBallot) en2.nextElement();
                        WTPrincipal wtp = wfballot.getVoter().getPrincipal();
                        if(wtp instanceof WTUser){
                            if(k == 0){
                                userName = ((WTUser) wtp).getFullName().toString();
                            }else{
                                userName = userName + "," + ((WTUser) wtp).getFullName().toString();
                            }
                        }
                        k++;
                    }
                }
                message = activityName + "(" + userName + ")("+startTime+")";
                activityStr.add(message);
            }
        } catch (WTException e1) {
            e1.printStackTrace();
        }
        return activityStr;
    }

    public static List getActivities(WfProcess wfprocess, List activityList) throws WTException {
        Enumeration enumeration = WfEngineHelper.service.getProcessSteps(wfprocess, null);
        while (enumeration.hasMoreElements()) {
            WfActivity wfactivity = (WfActivity) enumeration.nextElement();
            if (wfactivity instanceof WfAssignedActivity) {
                WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
                String state = wfassignedactivity.getState().getDisplay();
                if("正在运行".equals(state)){
                    activityList.add(wfassignedactivity);
                }
            }
        }
        return activityList;

    }

}
