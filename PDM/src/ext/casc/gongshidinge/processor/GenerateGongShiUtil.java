package ext.casc.gongshidinge.processor;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.processplan.helper.ZhuFuLinkUtil;
import com.glaway.mpm.util.*;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.Constants;
import ext.casc.gongshidinge.bean.GWGongshiHistoryRecord;
import ext.casc.integrate.util.BomUtil;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import ext.casc.workflow.WorkflowHelper;
import ext.ptc.ViewWIHelper;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.fc.*;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTArrayList;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.IconSelector;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenerateGongShiUtil {

    private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.GLAWAY_149_CONFIG_PATH);

    public static JSONObject generateProcessPlanTable(String oid) throws Exception {
        ReferenceFactory rf = new ReferenceFactory();
        WTPart wtPart = (WTPart) rf.getReference(oid).getObject();
        List<WTPart> allChildrenList = new ArrayList();
        List<WTPart> parents = new ArrayList();
        parents.add(wtPart);
        allChildrenList.add(wtPart);
        WTContainer parentContainer = wtPart.getContainer();
        boolean zhixingjingli = AccessAdminUtil.isGroup("执行经理");
        getAllChildPartsByPart(allChildrenList, parents, parentContainer);
        JSONObject jsonObject = generateData(allChildrenList,zhixingjingli);
        JSONArray array = (JSONArray) jsonObject.get("data");
        jsonObject.put("totalCount", array.length());
        return jsonObject;
    }

    public static JSONObject generateData(List<WTPart> datas, boolean zhixingjingli) throws WTException {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        ReferenceFactory rf = new ReferenceFactory();
        String icon = "";
        try {
            icon = getIconByClass(MPMOperation.class);
        } catch(WTException e) {
            e.printStackTrace();
        }
        WTUser user = (WTUser) SessionHelper.getPrincipal();
        Map<String,String> roleMap = new HashMap<String,String>();
        if(!datas.isEmpty() && !zhixingjingli){
            roleMap = getGongShiRoleMapByUser(datas.get(0).getContainer(), user);
        }

        MPMProcessPlan plan = null;
        MPMOperationMaster master = null;
        MPMOperation operation = null;
        MPMTooling tooling = null;
        String IsSheBeiGS = null;
        try {
            for (WTPart part : datas) {
                List allPlan = ViewWIHelper.getProcessPlan(part);
                for (Object object : allPlan) {
                    Map<String,String> workShopMap = new HashMap<String,String>();
                    plan = (MPMProcessPlan) object;
                    WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
                    if(document == null) {
                        continue;
                    }
                    String dept = IBAHelper.getIBAStringValue(document, "DEPT");
                    String roleName = WorkflowHelper.getRoleNameByDept(dept);
                    if(!roleMap.containsKey(roleName) && !zhixingjingli) {
                        continue;
                    }
                    List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
                    for(MPMOperationUsageLink link : links) {
                        String hasFu = "否";
                        String stepNumber = link.getOperationLabel();
                        if(stepNumber.startsWith("0")){
                            stepNumber = stepNumber.substring(1);
                        }
                        master = (MPMOperationMaster) link.getRoleBObject();
                        operation = ViewWIHelper.getMpmOperation(master.getNumber());
                        JSONObject jsonObject = new JSONObject();
                        String veroid = rf.getReference(operation).toString();
                        veroid = veroid.replaceAll(">", ":");
                        jsonObject.put("id", veroid);
                        jsonObject.put("type_icon", "<img src=\"" + icon + "\">");
                        jsonObject.put("partnumber", part.getNumber() + "@" + rf.getReferenceString(part));
                        jsonObject.put("partname", part.getName());
	                    List<GLZhuFuLink> record = ZhuFuLinkUtil.getRecord(document.getNumber(), document.getVersionIdentifier().getValue(), stepNumber);
	                    if(!record.isEmpty()) {
	                        hasFu = "是";
	                    }
	                    jsonObject.put("number", document.getNumber() + "@" + rf.getReferenceString(document));
	                    jsonObject.put("name", document.getName());
	                    jsonObject.put("code", document.getNumber());
	                    jsonObject.put("version", document.getVersionIdentifier().getValue() + "." + document.getIterationIdentifier().getValue());

                        String shiDingEState = IBAHelper.getIBAStringValue(document, "GongShiDingEState");
                        jsonObject.put("gongShiDingEState", StrUtil.isEmpty(shiDingEState) ? "" : shiDingEState);

	                    jsonObject.put("state", document.getState().getState().getDisplay(Locale.CHINA));
	                    jsonObject.put("stepNumber", stepNumber);
                        jsonObject.put("stepName", operation.getName());

                        tooling = MPMResourceUtil.getMPMToolingByName(operation.getName(), SopConstants.SOP_TYPE_PROCEDUCENAME);
                        IsSheBeiGS = IBAHelper.getIBAStringValue(tooling, "IsSheBeiGS");
                        jsonObject.put("isSheBeiGS", IsSheBeiGS == null ? "" : IsSheBeiGS);

                        jsonObject.put("hasFu", hasFu);
                        String workShop = IBAHelper.getIBAStringValue(operation, "workShop");
                        if(StrUtil.isEmpty(workShop)) {
                            if(workShopMap.isEmpty()){
                                workShopMap = GenerateGongShiUtil.getWorkShopByWTDocumentAndStep(document);
                            }
                            workShop = workShopMap.get(link.getOperationLabel());
                            if(StrUtil.isNotEmpty(workShop)){
                                IBAHelper.setIBAStringValue(operation, "workShop", workShop);
                            }
                        }
                        jsonObject.put("workShop", workShop);
                        String zhunjie = IBAHelper.getIBAStringValue(operation, "ZJGS");
                        String danjian = IBAHelper.getIBAStringValue(operation, "DJGS");
                        String danJianSheBeiGS = IBAHelper.getIBAStringValue(operation, "DanJianSheBeiGS");
                        if(StringUtils.equals(hasFu, "是")){
                            if(!"0".equals(zhunjie)){
                                IBAHelper.setIBAStringValue(operation, "ZJGS", "0");
                            }
                            if(!"0".equals(danjian)){
                                IBAHelper.setIBAStringValue(operation, "DJGS", "0");
                            }
                            if(!"0".equals(danJianSheBeiGS)){
                                IBAHelper.setIBAStringValue(operation, "DanJianSheBeiGS", "0");
                            }
                            jsonObject.put("zhunjie", "0");
                            jsonObject.put("danjian", "0");
                        }else {
                            jsonObject.put("zhunjie", zhunjie);
                            jsonObject.put("zhunjie_old", zhunjie);
                            jsonObject.put("danjian", danjian);
                            jsonObject.put("danjian_old", danjian);

                            jsonObject.put("danJianSheBeiGS", danJianSheBeiGS);
                            jsonObject.put("danJianSheBeiGS_old", danJianSheBeiGS);

                        }
                        jsonObject.put("isZhuRen", zhixingjingli);
                        array.put(jsonObject);
                    }
                }
            }
            result.put("data", array);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void getAllChildPartsByPart(List allChildrenList, List parents, WTContainer parentContainer) throws WTException {
        Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents), getDefaultConfigSpec());
        WTContainer childContainer = null;
        for (ListIterator i = parents.listIterator(); i.hasNext(); ) {
            WTPart parent = (WTPart) i.next();
            Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }
            List children = new ArrayList(branch.length);

            for (Persistable[] child : branch) {
                Persistable per = child[1];
                if (!(per instanceof WTPart) && !(per instanceof WTPartMaster)) {
                    continue;
                }
                WTPart childPart = null;
                if (per instanceof WTPart) {
                    childPart = (WTPart) per;
                    childPart = WCUtil.getLatestPartByView((Master) childPart.getMaster(), "Manufacturing");
                } else if (per instanceof WTPartMaster) {
                    childPart = WCUtil.getLatestPartByView((Master) per, "Manufacturing");
                }

                if (childPart == null) {
                    continue;
                }

                IBAUtility ibaUtility = new IBAUtility(childPart);
                String partType = ibaUtility.getIBAValue("MTYPE");

                if (!children.contains(childPart)) {
                    children.add(childPart);
                }

                //过滤不是自制件、外配套件、带料委外件、不带料委外件类型的零部件
                if (Constants.TYPE_ZIZHIJIAN.equals(partType)
                        || Constants.TYPE_WAIPEITAOJIAN.equals(partType)
                        || Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
                        || Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)) {
                    childContainer = childPart.getContainer();
                    if (!parentContainer.getName().equals(childContainer.getName())) {//过滤掉借用件，即是跟父件不在同一产品库下的零部件
                        continue;
                    }
                    if (!allChildrenList.contains(childPart)) {
                        allChildrenList.add(childPart);
                    }


                }
            }
            getAllChildPartsByPart(allChildrenList, children, parentContainer);
        }
    }

    protected static ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

    public static String getIconByClass(Class clazz) throws WTException {
        String imgURL = null;
        try {
            IconDelegate delegate = IconDelegateFactory.getInstance()
                    .getIconDelegate(clazz);
            IconSelector selector = delegate.getStandardIconSelector();
            while (!selector.isResourceKey()) {
                delegate = delegate.resolveSelector(selector);
                selector = delegate.getStandardIconSelector();
            }
            imgURL = selector.getIconKey();
        } catch (Exception e) {
            throw new WTException(e);
        }
        return imgURL;
    }

    public static Map<String, String> getWorkShopByWTDocumentAndStep(WTDocument doc) {
        Map<String,String> map = new HashMap<String,String>();
        try {
            if(doc != null) {
                Element techEle = BomUtil.getTechincisElement(doc, null, null);
                Element stepsElement = techEle.element("steps");
                if(stepsElement != null) {
                    List list = stepsElement.elements();
                    if(list != null && !list.isEmpty()) {
                        for(Object object : list) {
                            Element procedureEle = (Element) object;
                            String stepNum = procedureEle.attributeValue("stepNumber");

                            int numberLength = 3 - stepNum.length();
                            for(int i = 0; i < numberLength; i++) {
                                stepNum = 0 + stepNum;
                            }

                            map.put(stepNum, procedureEle.attributeValue("workShop"));
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> getGongShiRoleMapByUser(WTContainer container, WTUser user) throws WTException {
        Map<String,String> map = new HashMap<String,String>();
        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
        String roles = propertiesUtil.getProperty("roles");
        for(String roleName : roles.split(",")) {
            roleName = propertiesUtil.getProperty(roleName);
            Role role = Role.toRole(roleName);
            if(role == null) {
                continue;
            }
            ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
            for(WTPrincipalReference reference : arrayList) {
                Object object2 = reference.getPrincipal();
                if(object2 instanceof WTUser) {
                    WTUser wtUser = (WTUser) object2;
                    if(wtUser.getName().equals(user.getName())) {
                        map.put(roleName, roleName);
                        break;
                    }
                } else if(object2 instanceof WTGroup) {
                    WTGroup group = (WTGroup) object2;
                    if(group.isMember(user)) {
                        map.put(roleName, roleName);
                        break;
                    }
                }
            }
        }
        return map;
    }

    public static String submitGongshi(String params,String oid) {
        Transaction tx = null;
        String result = "sucess";
        String dingEState= "";
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            tx = new Transaction();
            tx.start();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            ReferenceFactory rf = new ReferenceFactory();
            WTUser user = (WTUser) SessionHelper.getPrincipal();

            WTPart part = (WTPart) rf.getReference(oid).getObject();
            if(part == null){
                return null;
            }

            boolean zhixingjingli = AccessAdminUtil.isGroup("执行经理");
            if (zhixingjingli) {
                dingEState = "已审核";
            } else {
                dingEState = "进行中";
            }

            Map<String, MPMOperation> operationMap = new HashMap<>();
            List<GWGongshiHistoryRecord> records = new ArrayList<GWGongshiHistoryRecord>();
            Set<WTDocument> documentSet = new HashSet<WTDocument>();
            Set<MPMProcessPlan> processPlanSet = new HashSet<MPMProcessPlan>();
            if(StrUtil.isNotEmpty(params)) {
                String[] datas = params.split("@!@");
                for(String data : datas) {
                    if(StrUtil.isNotEmpty(data)) {
                        String[] values = data.split("&&&");
                        Map<String, String> map = new HashMap<String, String>();
                        for(String s : values) {
                            if(!"".equals(s) && s.contains("=")) {
                                String[] ss2 = s.split("=",2);
                                if(s.endsWith("=")) {
                                    map.put(ss2[0], "");
                                } else {
                                    map.put(ss2[0], ss2[1]);
                                }
                            }
                        }
                        String id = map.get("id");
                        String vrOid = "VR:" + id;
                        String number = map.get("number");
                        String name = map.get("name");
                        String stepNumber = map.get("stepNumber");
                        String newzhunjie = map.get("zhunjie");
                        String newdanjian = map.get("danjian");
                        String newdanJianSheBeiGS = map.get("danJianSheBeiGS");
                        Persistable persistable = rf.getReference(vrOid).getObject();
                        if(persistable instanceof MPMOperation) {
                            MPMOperation operation = (MPMOperation) persistable;
                            MPMOperationUsageLink link = MPMProcessPlanUtil.getMPMOperationUsageLinkByMPMOperation(operation);
                            String zhunjie = IBAHelper.getIBAStringValue(operation, "ZJGS");
                            String danjian = IBAHelper.getIBAStringValue(operation, "DJGS");
                            String danJianSheBeiGS = IBAHelper.getIBAStringValue(operation, "DanJianSheBeiGS");
                            MPMProcessPlan plan = null;
                            if(link != null) {
                                if(link.getRoleAObject() != null && link.getRoleAObject() instanceof MPMProcessPlan){
                                    plan = (MPMProcessPlan) link.getRoleAObject();
                                    if(plan != null) {
                                        processPlanSet.add(plan);
                                    }
                                    //获取关联的工艺，更新工时标识
                                    WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
                                    if(document != null) {
                                        documentSet.add(document);
                                    }

                                }
                            }
                            if(!newzhunjie.equals(zhunjie) || !newdanjian.equals(danjian)|| !newdanJianSheBeiGS.equals(danJianSheBeiGS)){
                                IBAHelper.setIBAStringValue(operation, "ZJGS", newzhunjie);
                                IBAHelper.setIBAStringValue(operation, "DJGS", newdanjian);
                                IBAHelper.setIBAStringValue(operation, "DanJianSheBeiGS", newdanJianSheBeiGS);
                                //新建修改历史记录
                                GWGongshiHistoryRecord record = new GWGongshiHistoryRecord();
                                record.setVerOid(id);
                                record.setTecNumber(plan.getNumber());
                                record.setTecName(plan.getName());
                                if(StrUtil.isEmpty(record.getTecNumber())){
                                    record.setTecNumber(number);
                                    record.setTecName(name);
                                }
                                record.setStepId(operation.getNumber());
                                record.setStepNumber(stepNumber);
                                record.setStepName(operation.getName());
                                record.setZhunjie(zhunjie);
                                record.setDanjian(danjian);
                                record.setDanJianSheBeiGS(danJianSheBeiGS);
                                record.setCreator(user.getFullName());
                                record.setCreateTimeStamp(Timestamp.valueOf(sdf.format(new Date())));
                                records.add(record);
                            }
                            operationMap.put(StrUtil.toString(operation.getPersistInfo().getObjectIdentifier().getId()), operation);
                        }
                    }
                }
            }

            for(MPMProcessPlan plan : processPlanSet) {
                List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
                for(MPMOperationUsageLink link : links) {
                    MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
                    MPMOperation operation = ViewWIHelper.getMpmOperation(master.getNumber());
                    if(!operationMap.containsKey(StrUtil.toString(operation.getPersistInfo().getObjectIdentifier().getId()))) {
                        operationMap.put(StrUtil.toString(operation.getPersistInfo().getObjectIdentifier().getId()), operation);
                    }
                }
            }

            for(GWGongshiHistoryRecord record : records) {
                CmPersistenceHelper.manager.save(record);
            }

            ProcessEnvelope pe = null;
            if(!operationMap.isEmpty()) {
                if(!zhixingjingli){
                    pe = ProcessEnvelope.newProcessEnvelope();
                    pe.setNumber(new Date().getTime() + "");
                    WTContainer container = part.getContainer();
                    pe.setName("工时定额签审单"+"-"+part.getNumber()+"-"+pe.getCreatorFullName());
                    String type = "WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.GSDE";
                    TypeIdentifier id = TypeHelper.getTypeIdentifier(type);
                    pe = (ProcessEnvelope) CoreMetaUtility.setType(pe, id);

                    Folder folder = FolderHelper.service.getFolder("/Default", WTContainerRef.newWTContainerRef(container));
                    if (folder != null){
                        FolderHelper.assignLocation((FolderEntry) pe, folder);
                    }
                    pe.setContainer(container);
                    PersistenceHelper.manager.save(pe);

                    for(MPMOperation operation : operationMap.values()) {
                        EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(pe,operation);
                        envelopememberlink.setDescription("工序工时定额签审关联关系");
                        PersistenceHelper.manager.save(envelopememberlink);
                    }
                    boolean flag = WorkflowUtil.startProcess(pe, "工时定额签审流程", part.getNumber(), new HashMap<String, String>());
                    if(!flag){
                        return null;
                    }
                }
            }

            updateState(documentSet, "GongShiDingEState", dingEState);

            tx.commit();
            tx = null;
            //如果是定额员角色
            if(pe != null){
                result = "dingeyuan";
            }
            return result;
        } catch (Exception e) {
            if(tx!=null){
                tx.rollback();
            }
            e.printStackTrace();
        }finally{
            SessionServerHelper.manager.setAccessEnforced(enforce);
            if(tx != null){
                tx.rollback();
            }
            tx = null;
        }
        return null;
    }

    public static String saveGongshi(String params,String oid) {
        Transaction tx = null;
        String result = "sucess";
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            tx = new Transaction();
            tx.start();
            ReferenceFactory rf = new ReferenceFactory();
            WTPart part = (WTPart) rf.getReference(oid).getObject();
            if (part == null) {
                return null;
            }
            if (StrUtil.isNotEmpty(params)) {
                String[] datas = params.split("@!@");
                for (String data : datas) {
                    if (StrUtil.isNotEmpty(data)) {
                        String[] values = data.split("&&&");
                        Map<String, String> map = new HashMap<String, String>();
                        for (String s : values) {
                            if (!"".equals(s) && s.contains("=")) {
                                String[] ss2 = s.split("=", 2);
                                if (s.endsWith("=")) {
                                    map.put(ss2[0], "");
                                } else {
                                    map.put(ss2[0], ss2[1]);
                                }
                            }
                        }
                        String id = map.get("id");
                        String vrOid = "VR:" + id;
                        String newzhunjie = map.get("zhunjie");
                        String newdanjian = map.get("danjian");
                        String newdanJianSheBeiGS = map.get("danJianSheBeiGS");
                        Persistable persistable = rf.getReference(vrOid).getObject();
                        if (persistable instanceof MPMOperation) {
                            MPMOperation operation = (MPMOperation) persistable;
                            String zhunjie = IBAHelper.getIBAStringValue(operation, "ZJGS");
                            String danjian = IBAHelper.getIBAStringValue(operation, "DJGS");
                            String danJianSheBeiGS = IBAHelper.getIBAStringValue(operation, "DanJianSheBeiGS");
                            if (!newzhunjie.equals(zhunjie) || !newdanjian.equals(danjian) || !newdanJianSheBeiGS.equals(danJianSheBeiGS)) {
                                IBAHelper.setIBAStringValue(operation, "ZJGS", newzhunjie);
                                IBAHelper.setIBAStringValue(operation, "DJGS", newdanjian);
                                IBAHelper.setIBAStringValue(operation, "DanJianSheBeiGS", newdanJianSheBeiGS);
                            }
                        }
                    }
                }
            }
            tx.commit();
            tx = null;
            return result;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
            if (tx != null) {
                tx.rollback();
            }
            tx = null;
        }
        return null;
    }

    public static JSONObject generateHistoryData(String oid) {
        JSONObject returnJson = new JSONObject();
        JSONArray array = new JSONArray();
        ReferenceFactory rf = new ReferenceFactory();
        if(StrUtil.isNotEmpty(oid)){
            String icon = "";
            try {
                icon = getIconByClass(MPMOperation.class);
            } catch(WTException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String[] ids = oid.split("@!@");
            for(String id : ids) {
                try {
                    CmQuerySpec qs = new CmQuerySpec(GWGongshiHistoryRecord.class);
                    qs.appendWhere(GWGongshiHistoryRecord.VEROID, CmQuerySpec.EQUAL, id);
                    qs.appendOrderBy(GWGongshiHistoryRecord.CREATETIME,true);
                    CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
                    while(qr.hasNext()) {
                        GWGongshiHistoryRecord record = (GWGongshiHistoryRecord) qr.next();
                        JSONObject object = new JSONObject();
                        object.put("id", record.getKeyId());
                        object.put("type_icon", "<img src=\"" + icon + "\">");
                        object.put("technicsnumber", record.getTecNumber());
                        object.put("technicsname", record.getTecName());
                        object.put("number", record.getStepNumber());
                        object.put("name", record.getStepName());
                        object.put("zhunjie", record.getZhunjie());
                        object.put("danjian", record.getDanjian());
                        object.put("danJianSheBeiGS", record.getDanJianSheBeiGS());
                        object.put("modifier", record.getCreator());
                        object.put("modifyTime", sdf.format(record.getCreateTimeStamp()));
                        array.put(object);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        returnJson.put("data", array);
        returnJson.put("totalCount", array.length());
        return returnJson;
    }

    public static void createSubmitHistory(Object pbo) {
        try {
            List<MPMOperation> ops = new ArrayList<MPMOperation>();
            WTUser user = (WTUser) SessionHelper.getPrincipal();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            if(pbo instanceof WTDocument) {
                WTDocument doc = (WTDocument) pbo;
                MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
                ops = WorkflowHelper.getMPMOperationsByMpmPr(plan);
            } else if(pbo instanceof WTChangeOrder2) {
                WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
                MPMProcessPlan beforePlan = null;
                MPMProcessPlan afterPlan = null;
                QueryResult qr = ChangeHelper2.service.getChangeablesBefore(ecn);
                while(qr.hasMoreElements()) {
                    Object object = qr.nextElement();
                    if(object instanceof WTDocument) {
                        if(beforePlan == null) {
                            WTDocument doc = (WTDocument) object;
                            beforePlan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
                        }
                    } else if(object instanceof MPMProcessPlan) {
                        beforePlan = (MPMProcessPlan) object;
                        break;
                    }
                }
                QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(ecn);
                while(qResult.hasMoreElements()) {
                    Object object = qResult.nextElement();
                    if(object instanceof WTDocument) {
                        if(afterPlan == null) {
                            WTDocument doc = (WTDocument) object;
                            afterPlan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
                        }
                    } else if(object instanceof MPMProcessPlan) {
                        afterPlan = (MPMProcessPlan) object;
                        break;
                    }
                }

                if(afterPlan != null) {
                    ops.addAll(WorkflowHelper.getOperationChangeList(afterPlan, beforePlan));
                }
            } else if(pbo instanceof ProcessEnvelope) {
                ProcessEnvelope pe = (ProcessEnvelope) pbo;
                ArrayList members = ProcessEnvelopeUtil.getAllMembers(pe);
                for(Object member : members) {
                    if(member instanceof MPMOperation) {
                        ops.add((MPMOperation) member);
                    }
                }
            }
            if(ops.isEmpty()) {
                return;
            }
            List<GWGongshiHistoryRecord> records = new ArrayList<GWGongshiHistoryRecord>();
            ReferenceFactory rf = new ReferenceFactory();
            for(MPMOperation operation : ops) {
                //新建修改历史记录
                String veroid = rf.getReference(operation).toString();
                veroid = veroid.replaceAll(">", ":");
                MPMOperationUsageLink link = MPMProcessPlanUtil.getMPMOperationUsageLinkByMPMOperation(operation);
                String zhunjie = IBAHelper.getIBAStringValue(operation, "ZJGS");
                String danjian = IBAHelper.getIBAStringValue(operation, "DJGS");
                String  danJianSheBeiGS = IBAHelper.getIBAStringValue(operation, "DanJianSheBeiGS");
                GWGongshiHistoryRecord record = new GWGongshiHistoryRecord();
                record.setVerOid(veroid);
                if(link != null) {
                    if(link.getRoleAObject() != null && link.getRoleAObject() instanceof MPMProcessPlan){
                        MPMProcessPlan plan = (MPMProcessPlan) link.getRoleAObject();
                        record.setTecNumber(plan.getNumber());
                        record.setTecName(plan.getName());
                    }
                }
                record.setStepId(operation.getNumber());
                record.setStepNumber(Util.formateInteger(link.getOperationLabel()));
                record.setStepName(operation.getName());
                record.setZhunjie(zhunjie);
                record.setDanjian(danjian);
                record.setDanJianSheBeiGS(danJianSheBeiGS);
                record.setCreator(user.getFullName());
                record.setCreateTimeStamp(Timestamp.valueOf(sdf.format(new Date())));
                records.add(record);
            }
            for(GWGongshiHistoryRecord record : records) {
                CmPersistenceHelper.manager.save(record);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateState(Set<WTDocument> documentSet, String ibaName, String state) {
        for (WTDocument document : documentSet) {
            try {
                IBAHelper.setIBAStringValue(document, ibaName, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
