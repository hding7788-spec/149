package ext.casc.common.mvc.builder.analysis;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.mvc.components.*;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.integrate.process.ProcessService;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;
import wt.change2.WTAnalysisActivity;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

@ComponentBuilder("ext.casc.common.mvc.builder.analysis.SearchTechnicsAnalysisActivityBuilder")
public class SearchTechnicsAnalysisActivityBuilder extends AbstractConfigurableTableBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String work = String.valueOf(params.getParameter("work"));
        if("search".equals(work)) {
            String containerStr = ProcessUtil.getNotNullParam(params.getParameter("containerTypeList"));
            String ananumber = ProcessUtil.getNotNullParam(params.getParameter("ananumber"));
            String objnumber = ProcessUtil.getNotNullParam(params.getParameter("objnumber"));
            String objname = ProcessUtil.getNotNullParam(params.getParameter("objname"));
            String creatorStr = ProcessUtil.getNotNullParam(params.getParameter("creator"));
            String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
            String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));
            WTUser creator = null;
            if(StrUtil.isNotEmpty(creatorStr)) {
                creatorStr = creatorStr.split(",")[0].split("=")[1];
                creator = UserUtil.getUser(creatorStr);
            }

            DBConnUtil conn = null;
            ResultSet rs = null;
            try {
                conn = new DBConnUtil();
                StringBuilder sb = new StringBuilder();
                sb.append("select co.IDA2A2, " +
                        "       co.IDA3CONTAINERREFERENCE, " +
                        "       co.CREATESTAMPA2, " +
                        "       co.STATESTATE, " +
                        "       co.IDA3B2ITERATIONINFO, " +
                        "       com.NAME, " +
                        "       com.WTCHGORDERNUMBER, " +
                        "       aoe.ANALYSISNUMBER, " +
                        "       asl.CLASSNAMEKEYROLEBOBJECTREF, " +
                        "       asl.IDA3B5 " +
                        "from WTCHANGEORDER2 co " +
                        "         inner join WTCHANGEORDER2MASTER com on com.IDA2A2 = co.IDA3MASTERREFERENCE " +
                        "         inner join ANALYSISOBJENTRY aoe on aoe.RELATEDORDER = com.WTCHGORDERNUMBER " +
                        "         inner join WTANALYSISACTIVITYMASTER anam on anam.WTCHGANALYSISNUMBER = aoe.ANALYSISNUMBER " +
                        "         inner join WTANALYSISACTIVITY ana on ana.IDA3MASTERREFERENCE = anam.IDA2A2 " +
                        "         left join ANALYSISTOSOURCELINK asl on asl.IDA3A5 = ana.IDA2A2 where co.IDA2A2 is not null ");
                if(creator != null) {
                    sb.append("and ana.IDA3D2ITERATIONINFO = '" + creator.getPersistInfo().getObjectIdentifier().getId() + "' ");
                }
                if(StrUtil.isNotEmpty(containerStr)) {
                    String[] containers = containerStr.split(":");
                    if(containers.length > 2) {
                        containerStr = containers[2];
                        sb.append(" and co.IDA3CONTAINERREFERENCE = '" + Long.valueOf(containerStr).longValue() + "'");
                    }
                }
                if(StrUtil.isNotEmpty(ananumber)) {
                    sb.append(" and aoe.ANALYSISNUMBER like '%" + ananumber + "%'");
                }
                if(StrUtil.isNotEmpty(objnumber)) {
                    sb.append(" and com.WTCHGORDERNUMBER like '%" + objnumber + "%'");
                }
                if(StrUtil.isNotEmpty(objname)) {
                    sb.append(" and com.NAME like '%" + objname + "%'");
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
                    sb.append(" and co.CREATESTAMPA2 >= to_date('" + format + "','YYYY-MM-DD HH24:MI:SS')");
                }
                if(StrUtil.isNotEmpty(endDate)) {
                    Date dateFrom = WTStandardDateFormat.parse(endDate, "yyyy/M/d");
                    // 加入时区信息
                    ca.setTime(dateFrom);
                    dateFrom = ca.getTime();
                    Timestamp timestamp = new Timestamp(dateFrom.getTime() + 8 * 60 * 60 * 1000);
                    String format = DateUtil.format(timestamp, "yyyy-MM-dd HH:mm:ss");
                    sb.append(" and co.CREATESTAMPA2 <= to_date('" + format + "','YYYY-MM-DD HH24:MI:SS')");
                }

                rs = conn.executeQuery(sb.toString());
                while(rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    String ida2A2 = rs.getString("IDA2A2");
                    String number = rs.getString("WTCHGORDERNUMBER");
                    WTChangeOrder2 changeOrder2 = ProcessService.getWTChangeOrder2ByNumber(number);
                    if(changeOrder2 == null) {
                        continue;
                    }
                    String name = rs.getString("NAME");
                    String analysisNumber = rs.getString("ANALYSISNUMBER");
                    String ida3CONTAINERREFERENCE = rs.getString("IDA3CONTAINERREFERENCE");
                    String ida3B2ITERATIONINFO = rs.getString("IDA3B2ITERATIONINFO");
                    String createstampa2 = rs.getString("CREATESTAMPA2");
                    String statestate = rs.getString("STATESTATE");
                    String classnamekeyrolebobjectref = rs.getString("CLASSNAMEKEYROLEBOBJECTREF");
                    String ida3B5 = rs.getString("IDA3B5");
                    //型号
                    PDMLinkProduct product = WTContainerUtil.getProductByOid(Long.valueOf(ida3CONTAINERREFERENCE));
                    map.put("containerName", product.getName());
                    //更改单编号
                    String orderNumber = " app/#ptc1/tcomp/infoPage?oid=OR:" + WTChangeOrder2.class.getName() + ":" + ida2A2;
                    map.put("orderNumber", "<a href=\"" + orderNumber + "\"  target=_blank>" + number + " </a>");
                    //更改/偏离单号
                    map.put("orderName", name);
                    //更改单属性
                    IBAUtility utility = new IBAUtility(changeOrder2);
                    map.put("changeOrderType", utility.getIBAValue("CHANGENOTICETYPE"));
                    map.put("changeCause", utility.getIBAValue("CHANGECAUSE"));
                    map.put("changeBefore", utility.getIBAValue("CHANGEBEFOR"));
                    map.put("changeAfter", utility.getIBAValue("CHANGEAFTER"));
                    //更改单创建时间
                    map.put("createTime", createstampa2);
                    //更改单修改者
                    WTUser modifier = UserUtil.getWTUser(Long.parseLong(ida3B2ITERATIONINFO));
                    if(modifier != null) {
                        map.put("modifier", modifier.getFullName());
                    }
                    //更改单状态
                    String stateStr = "";
                    State state = State.toState(statestate);
                    if(state != null) {
                        stateStr = state.getDisplay(Locale.CHINA);
                    }
                    map.put("state", stateStr);
                    //更改影响分析编号
                    ReferenceFactory rf = new ReferenceFactory();
                    WTAnalysisActivity activity = AnalysisUtil.getWTAnalysisActivityByNumber(analysisNumber);
                    if(activity != null) {
                        String analysisurl = " app/#ptc1/tcomp/infoPage?oid=" + rf.getReferenceString(activity);
                        map.put("analysisNumber", "<a href=\"" + analysisurl + "\"  target=_blank>" + activity.getNumber() + " </a>");
                    }
                    //影响源
                    map.put("objNumber", "");
                    if(StrUtil.isNotEmpty(classnamekeyrolebobjectref) && StrUtil.isNotEmpty(ida3B5)){
                        try {
                            Persistable object = rf.getReference(classnamekeyrolebobjectref + ":" + ida3B5).getObject();
                            if(object != null) {
                                if(object instanceof ChangePackaged) {
                                    ChangePackaged cp = (ChangePackaged) object;
                                    String url = " app/#ptc1/tcomp/infoPage?oid=" + rf.getReferenceString(cp);
                                    map.put("objNumber", "<a href=\"" + url + "\"  target=_blank>" + cp.getNumber() + " </a>");
                                } else if(object instanceof WTChangeRequest2) {
                                    WTChangeRequest2 request2 = (WTChangeRequest2) object;
                                    String url = " app/#ptc1/tcomp/infoPage?oid=" + rf.getReferenceString(request2);
                                    map.put("objNumber", "<a href=\"" + url + "\"  target=_blank>" + request2.getNumber() + " </a>");
                                } else if(object instanceof ProcessEnvelope) {
                                    ProcessEnvelope pe = (ProcessEnvelope) object;
                                    String url = " app/#ptc1/tcomp/infoPage?oid=" + rf.getReferenceString(pe);
                                    map.put("objNumber", "<a href=\"" + url + "\"  target=_blank>" + pe.getNumber() + " </a>");
                                }
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                            continue;
                        }
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
        table.setActionModel("custom_export_processTask");
        table.setId("ext.casc.common.mvc.builder.analysis.SearchTechnicsAnalysisActivityBuilder");

        table.setLabel("工艺更改影响任务列表");

        ColumnConfig columnConfig = factory.newColumnConfig("containerName", "型号", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("orderNumber", "工艺更改单号", true);
        columnConfig.setDataUtilityId("AnalysisDataUtility");
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("orderName", "名称", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("changeOrderType", "更改类别", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("changeCause", "更改原因", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("changeBefore", "更改前", true);
        columnConfig.setDataUtilityId("AnalysisDataUtility");
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("changeAfter", "更改后", true);
        columnConfig.setDataUtilityId("AnalysisDataUtility");
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("createTime", "创建时间", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("modifier", "修改者", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("state", "状态", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("analysisNumber", "更改影响分析单号", true);
        columnConfig.setDataUtilityId("AnalysisDataUtility");
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("objNumber", "工艺更改申请单/设计更改单号", true);
        columnConfig.setDataUtilityId("AnalysisDataUtility");
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        return table;
    }

    @Override
    public ConfigurableTable buildConfigurableTable(String s) throws WTException {
        return null;
    }
}
