package ext.casc.common.mvc.builder.analysis;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.mvc.components.*;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.process.util.ProcessUtil;
import wt.change2.WTAnalysisActivity;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.type.TypedUtility;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

@ComponentBuilder("ext.casc.common.mvc.builder.analysis.SearchDeptDesignAnalysisActivityBuilder")
public class SearchDeptDesignAnalysisActivityBuilder extends AbstractConfigurableTableBuilder {

    public static Map<String, String> typeMap = null;

    static {
        typeMap = new HashMap<String, String>();
        typeMap.put(AnalysisConstant.TYPE_PBOM, "PBOM");
        typeMap.put(AnalysisConstant.TYPE_TECHNICS, "工艺");
        typeMap.put(AnalysisConstant.TYPE_ZAIZHIPIN, "在制品");
        typeMap.put(AnalysisConstant.TYPE_YIZHIPIN, "已制品");
    }

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String work = String.valueOf(params.getParameter("work"));
        if("search".equals(work)) {
            String dept = ProcessUtil.getNotNullParam(params.getParameter("dept"));
            String containerStr = ProcessUtil.getNotNullParam(params.getParameter("containerTypeList"));
            String type = ProcessUtil.getNotNullParam(params.getParameter("type"));
            String ananumber = ProcessUtil.getNotNullParam(params.getParameter("ananumber"));
            String objnumber = ProcessUtil.getNotNullParam(params.getParameter("objnumber"));
            String objname = ProcessUtil.getNotNullParam(params.getParameter("objname"));
            String state = ProcessUtil.getNotNullParam(params.getParameter("state"));
            String tasktype = ProcessUtil.getNotNullParam(params.getParameter("tasktype"));
            String responser = ProcessUtil.getNotNullParam(params.getParameter("responser"));
            String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
            String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));

            DBConnUtil conn = null;
            ResultSet rs = null;
            try {
                conn = new DBConnUtil();
                StringBuilder sb = new StringBuilder();
                sb.append("select aoe.*,ana.*,link.CLASSNAMEKEYROLEBOBJECTREF,link.PLNUMBER," +
                        "pe.PROCESSENVELOPENUMBER,pe.NAME as PENAME," +
                        "cp.CHANGEPACKAGED,cp.NAME as CPNAME," +
                        "ecrm.WTCHGREQUESTNUMBER,ecrm.NAME as ECRNAME " +
                        "from ANALYSISOBJENTRY aoe " +
                        "inner join WTANALYSISACTIVITYMASTER anam on anam.WTCHGANALYSISNUMBER = aoe.ANALYSISNUMBER " +
                        "inner join WTANALYSISACTIVITY ana on ana.IDA3MASTERREFERENCE = anam.IDA2A2 " +
                        "left join ANALYSISTOSOURCELINK link on link.IDA3A5 = ana.IDA2A2 ");
                sb.append("left join PROCESSENVELOPE pe on pe.IDA2A2 = link.IDA3B5 ");
                sb.append("left join CHANGEPACKAGED cp on cp.IDA2A2 = link.IDA3B5 ");
                sb.append("left join WTCHANGEREQUEST2 ecr on ecr.IDA2A2 = link.IDA3B5 ");
                sb.append("left join WTCHANGEREQUEST2MASTER ecrm on ecrm.IDA2A2 = ecr.IDA3MASTERREFERENCE ");
                sb.append("where aoe.CMKEYID is not null ");
                if(StrUtil.isNotEmpty(dept)){
                    sb.append("and aoe.UNIT = '" + dept + "' ");
                }
                if(StrUtil.isNotEmpty(containerStr)) {
                    String[] containers = containerStr.split(":");
                    if(containers.length > 2) {
                        containerStr = containers[2];
                        sb.append(" and ana.IDA3CONTAINERREFERENCE = '" + Long.valueOf(containerStr).longValue() + "'");
                    }
                }
                if(StrUtil.isNotEmpty(ananumber)) {
                    sb.append(" and anam.WTCHGANALYSISNUMBER like '%" + ananumber + "%'");
                }
                if(AnalysisConstant.SEARCH_TYPE_SHEJIGENGAI.equals(type)) {
                    sb.append(" and link.CLASSNAMEKEYROLEBOBJECTREF = '" + ChangePackaged.class.getName() + "'");
                    if(StrUtil.isNotEmpty(objnumber)) {
                        sb.append(" and cp.CHANGEPACKAGED like '%" + objnumber + "%'");
                    }
                    if(StrUtil.isNotEmpty(objname)) {
                        sb.append(" and cp.NAME like '%" + objname + "%'");
                    }
                } else if(AnalysisConstant.SEARCH_TYPE_SHEJIPIANLI.equals(type)) {
                    sb.append(" and link.CLASSNAMEKEYROLEBOBJECTREF = '" + ProcessEnvelope.class.getName() + "'");
                    if(StrUtil.isNotEmpty(objnumber)) {
                        sb.append(" and pe.PROCESSENVELOPENUMBER like '%" + objnumber + "%'");
                    }
                    if(StrUtil.isNotEmpty(objname)) {
                        sb.append(" and pe.NAME like '%" + objname + "%'");
                    }
                } else {
                    if(StrUtil.isNotEmpty(objnumber)) {
                        sb.append(" and (cp.CHANGEPACKAGED like '%" + objnumber + "%' or " +
                                "pe.PROCESSENVELOPENUMBER like '%" + objnumber + "%' or " +
                                "ecrm.WTCHGREQUESTNUMBER like '%" + objnumber + "%') ");
                    }
                    if(StrUtil.isNotEmpty(objname)) {
                        sb.append(" and (cp.NAME like '%" + objname + "%' or " +
                                "pe.NAME like '%" + objname + "%' or " +
                                "ecrm.NAME like '%" + objname + "%') ");
                    }
                }
                if(StrUtil.isNotEmpty(state)) {
                    sb.append(" and aoe.DEALSTATUS = '" + state + "'");
                }
                if(StrUtil.isNotEmpty(tasktype)) {
                    sb.append(" and aoe.DATATYPE LIKE '%" + tasktype + "%'");
                }
                if(StrUtil.isNotEmpty(responser)) {
                    responser = responser.split(",")[0].split("=")[1];
                    WTUser user = UserUtil.getUser(responser);
                    if(user != null) {
                        ReferenceFactory rf = new ReferenceFactory();
                        String userId = rf.getReferenceString(user);
                        sb.append(" and aoe.responser = '" + userId + "'");
                    }
                }

                // 最后更新时间起始条件
                TimeZone tz = WTContext.getContext().getTimeZone();
                Calendar ca = Calendar.getInstance(tz);
                if(StrUtil.isNotEmpty(startDate)) {
                    Date dateFrom = WTStandardDateFormat.parse(startDate, "yyyy/M/d");
                    // 加入时区信息
                    ca.setTime(dateFrom);
                    dateFrom = ca.getTime();
                    Timestamp timestamp = new Timestamp(dateFrom.getTime() + 8 * 60 * 60 * 1000);
                    String format = DateUtil.format(timestamp, "yyyy-MM-dd HH:mm:ss");
                    sb.append(" and ana.CREATESTAMPA2 >= to_date('" + format + "','YYYY-MM-DD HH24:MI:SS')");
                }
                if(StrUtil.isNotEmpty(endDate)) {
                    Date dateFrom = WTStandardDateFormat.parse(endDate, "yyyy/M/d");
                    // 加入时区信息
                    ca.setTime(dateFrom);
                    dateFrom = ca.getTime();
                    Timestamp timestamp = new Timestamp(dateFrom.getTime() + 8 * 60 * 60 * 1000);
                    String format = DateUtil.format(timestamp, "yyyy-MM-dd HH:mm:ss");
                    sb.append(" and ana.CREATESTAMPA2 <= to_date('" + format + "','YYYY-MM-DD HH24:MI:SS')");
                }
                sb.append(" order by anam.CREATESTAMPA2 desc");
                rs = conn.executeQuery(sb.toString());
                while(rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    String veroid = rs.getString("VEROID");
                    String datatype = rs.getString("DATATYPE");
                    String analysisNumber = rs.getString("ANALYSISNUMBER");
                    String ida2A2 = rs.getString("IDA2A2");
                    String sourceClazz = rs.getString("CLASSNAMEKEYROLEBOBJECTREF");
                    String objNumber = "";
                    String name = "";
                    if(ProcessEnvelope.class.getName().equals(sourceClazz)){
                        objNumber = rs.getString("PROCESSENVELOPENUMBER");
                        name = rs.getString("PENAME");
                    } else if(ChangePackaged.class.getName().equals(sourceClazz)) {
                        objNumber = rs.getString("CHANGEPACKAGED");
                        name = rs.getString("CPNAME");
                    } else if(WTChangeRequest2.class.getName().equals(sourceClazz)) {
                        objNumber = rs.getString("WTCHGREQUESTNUMBER");
                        name = rs.getString("ECRNAME");
                    }
                    String ida3CONTAINERREFERENCE = rs.getString("IDA3CONTAINERREFERENCE");
                    String ida3D2ITERATIONINFO = rs.getString("IDA3D2ITERATIONINFO");
                    String createstampa2 = rs.getString("CREATESTAMPA2");
                    String unit = rs.getString("UNIT");
                    String zerenren = rs.getString("RESPONSER");
                    Timestamp timestamp = rs.getTimestamp("TASKTIME");
                    String dealstatus = rs.getString("DEALSTATUS");
                    PDMLinkProduct product = WTContainerUtil.getProductByOid(Long.valueOf(ida3CONTAINERREFERENCE));
                    //型号
                    map.put("containerName", product.getName());
                    //影响分析编号
                    map.put("analysisNumber", analysisNumber);
                    map.put("analysisurl", "<input type=\"button\" name=\"analysis\" value=\"查看\" onClick=\"javascript:viewAnalysis('OR:" + WTAnalysisActivity.class.getName() + ":" + ida2A2 + "');\" />\n");
                    //更改/偏离单号
                    map.put("objNumber", objNumber);
                    //更改/偏离名称
                    map.put("objName", name);
                    //偏离单编号
                    map.put("plNumber", rs.getString("PLNUMBER"));
                    //分析单据黄建时间
                    map.put("analysisTime", createstampa2);
                    //分析单据创建者
                    WTUser anaCreator = UserUtil.getWTUser(Long.parseLong(ida3D2ITERATIONINFO));
                    if(anaCreator != null) {
                        map.put("creator", anaCreator.getFullName());
                    }
                    //子任务子类
                    map.put("tasktype", typeMap.get(datatype));
                    //责任部门
                    map.put("dept", unit);
                    //任务到达时间
                    map.put("taskTime", DateUtil.format(timestamp, "yyyy-MM-dd"));
                    try {
                        ReferenceFactory rf = new ReferenceFactory();
                        Persistable persistable = rf.getReference("VR:" + veroid).getObject();
                        if(persistable != null) {
                            if(persistable instanceof WTPart) {
                                //主对象编号
                                map.put("pboNumber", ((WTPart) persistable).getNumber());
                                //主对象名称
                                map.put("pboName", ((WTPart) persistable).getName());
                            } else if(persistable instanceof WTDocument) {
                                WTDocument doc = (WTDocument) persistable;
                                TypeIdentifier identifier = TypedUtility.getTypeIdentifier(doc);
                                String typename = identifier.getTypename();
                                if(typename.contains("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN")) {
                                    WTPart part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(doc);
                                    if(part != null) {
                                        map.put("pboNumber", part.getNumber());
                                    }else {
                                        map.put("pboNumber", doc.getNumber());
                                    }
                                } else {
                                    map.put("pboNumber", doc.getNumber());
                                }
                                map.put("pboName", doc.getName());
                            }
                        }
                        map.put("taskState", dealstatus);
                        //任务状态
                        if(StrUtil.isNotEmpty(zerenren)) {
                            WTUser user = (WTUser) rf.getReference(zerenren).getObject();
                            map.put("responser", user.getFullName());
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    list.add(map);
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(rs != null) {
                    rs.close();
                }
                if(conn != null) {
                    conn.close();
                }
            }
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setSelectable(true);
        table.setConfigurable(true);
        table.setActionModel("analysis_export_list");
        table.setId("ext.casc.common.mvc.builder.analysis.SearchDeptDesignAnalysisActivityBuilder");

        table.setLabel("设计更改/偏离影响任务列表");

        ColumnConfig columnConfig = factory.newColumnConfig("containerName", "型号", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("analysisNumber", "更改/偏离影响分析单号", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("analysisurl", "", true);
        columnConfig.setDataUtilityId("AnalysisDataUtility");
        columnConfig.setWidth(40);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("objNumber", "设计更改/偏离单号", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("objName", "名称", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("plNumber", "偏离单编号", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("analysisTime", "单据创建时间", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("creator", "单据创建者", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("tasktype", "子任务类别", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("pboNumber", "产品编号", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("pboName", "产品名称", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("dept", "责任部门", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("responser", "责任人", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("taskTime", "流程到达时间", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("taskState", "流程状态", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        return table;
    }

    @Override
    public ConfigurableTable buildConfigurableTable(String s) throws WTException {
        return null;
    }
}
