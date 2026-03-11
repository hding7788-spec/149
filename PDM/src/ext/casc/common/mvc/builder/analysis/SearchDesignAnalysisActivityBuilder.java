package ext.casc.common.mvc.builder.analysis;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.constants.Constants;
import ext.casc.distribute.integration.OtherSystemIntegrationHelper;
import ext.casc.distribute.service.NodeFinder;
import ext.casc.distribute.service.NodeInstanceTaskService;
import ext.casc.distribute.vo.DWGraphVo;
import ext.casc.distribute.vo.NcResponseDataVo;
import ext.casc.process.ProcessConstants;
import ext.casc.process.util.ProcessUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@ComponentBuilder("ext.casc.common.mvc.builder.analysis.SearchDesignAnalysisActivityBuilder")
public class SearchDesignAnalysisActivityBuilder extends AbstractConfigurableTableBuilder {

    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SearchDesignAnalysisActivityBuilder.class);
    TimedCache<String, DWGraphVo> dwGraphVoTimedCache = CacheUtil.newTimedCache(6000);

    public static Map<String, String> typeMap = null;

    static {
        typeMap = new HashMap<>();
        typeMap.put(AnalysisConstant.TYPE_PBOM, "PBOM");
        typeMap.put(AnalysisConstant.TYPE_TECHNICS, "工艺");
        typeMap.put(AnalysisConstant.TYPE_ZAIZHIPIN, "在制品");
        typeMap.put(AnalysisConstant.TYPE_YIZHIPIN, "已制品");
    }

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        dwGraphVoTimedCache.cancelPruneSchedule(); // 关闭定时任务
        dwGraphVoTimedCache.prune();

        List<Map<String, String>> list = new ArrayList<>();
        String work = String.valueOf(params.getParameter("work"));
        if("search".equals(work)) {
            String containerStr = ProcessUtil.getNotNullParam(params.getParameter("containerTypeList"));
            String type = ProcessUtil.getNotNullParam(params.getParameter("type"));
            String ananumber = ProcessUtil.getNotNullParam(params.getParameter("ananumber"));
            String objnumber = ProcessUtil.getNotNullParam(params.getParameter("objnumber"));
            String objname = ProcessUtil.getNotNullParam(params.getParameter("objname"));
            String state = ProcessUtil.getNotNullParam(params.getParameter("state"));
            String tasktype = ProcessUtil.getNotNullParam(params.getParameter("tasktype"));
            String dept = ProcessUtil.getNotNullParam(params.getParameter("dept"));
            String creatorStr = ProcessUtil.getNotNullParam(params.getParameter("creator"));
            String responser = ProcessUtil.getNotNullParam(params.getParameter("responser"));
            String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
            String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));
            String viewType = ((JcaComponentParams) params).getHelperBean().getNmCommandBean().getRadio().get("viewType") + "";
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
                if("detail".equals(viewType)) {
                    if(AnalysisConstant.TYPE_PBOM.equals(tasktype) || AnalysisConstant.TYPE_TECHNICS.equals(tasktype)) {
                        // 查询PBOM或者工艺
                        sb.append("select aoe.*,ana.*,link.CLASSNAMEKEYROLEBOBJECTREF,link.PLNUMBER," +
                                "pe.PROCESSENVELOPENUMBER,pe.NAME as PENAME," +
                                "cp.CHANGEPACKAGED,cp.NAME as CPNAME," +
                                "ecrm.WTCHGREQUESTNUMBER,ecrm.NAME as ECRNAME, " +
                                "NULL AS DEALTYPE, " +
                                "NULL AS CARD, " +
                                "NULL AS PRODUCT, " +
                                "NULL AS CREATETIMESTAMP, " +
                                "NULL AS COMMENTS, " +
                                "NULL AS MESRESPONSER " +
                                "from ANALYSISOBJENTRY aoe " +
                                "inner join WTANALYSISACTIVITYMASTER anam on anam.WTCHGANALYSISNUMBER = aoe.ANALYSISNUMBER " +
                                "inner join WTANALYSISACTIVITY ana on ana.IDA3MASTERREFERENCE = anam.IDA2A2 " +
                                "left join ANALYSISTOSOURCELINK link on link.IDA3A5 = ana.IDA2A2 " +
                                "left join PROCESSENVELOPE pe on pe.IDA2A2 = link.IDA3B5 " +
                                "left join CHANGEPACKAGED cp on cp.IDA2A2 = link.IDA3B5 " +
                                "left join WTCHANGEREQUEST2 ecr on ecr.IDA2A2 = link.IDA3B5 " +
                                "left join WTCHANGEREQUEST2MASTER ecrm on ecrm.IDA2A2 = ecr.IDA3MASTERREFERENCE " +
                                "where aoe.CMKEYID is not null and aoe.DATATYPE = '" + tasktype + "' ");
                        if(StrUtil.isNotEmpty(dept)){
                            sb.append("and aoe.UNIT = '" + dept + "' ");
                        }
                    } else if(AnalysisConstant.PRODUCT.equals(tasktype)) {
                        // 查询制品
                        sb.append("select ana.*,link.CLASSNAMEKEYROLEBOBJECTREF,link.PLNUMBER, " +
                                "pe.PROCESSENVELOPENUMBER,pe.NAME as PENAME, " +
                                "cp.CHANGEPACKAGED,cp.NAME as CPNAME, " +
                                "ecrm.WTCHGREQUESTNUMBER,ecrm.NAME as ECRNAME, " +
                                "aoe.ANALYSISNUMBER,  " +
                                "aoe.VEROID,  " +
                                "aoe.DATATYPE, " +
                                "aoe.PRODUCT, " +
                                "aoe.RESPONSER,  " +
                                "aoe.UNIT,  " +
                                "aoe.TASKTIME,  " +
                                "aoe.COMPLETETIME,  " +
                                "gdp.CMKEYID,  " +
                                "COALESCE(CASE WHEN gdp.STATUS = '已闭环' THEN '已完成' ELSE gdp.STATUS END,aoe.DEALSTATUS) AS DEALSTATUS, " +
                                "gdp.DEALTYPE, " +
                                "gdp.CARD, " +
                                "gdp.CREATETIMESTAMP, " +
                                "gdp.COMMENTS, " +
                                "gdp.RESPONSER AS MESRESPONSER " +
                                "from ANALYSISOBJENTRY aoe " +
                                "inner join WTANALYSISACTIVITYMASTER anam on anam.WTCHGANALYSISNUMBER = aoe.ANALYSISNUMBER " +
                                "inner join WTANALYSISACTIVITY ana on ana.IDA3MASTERREFERENCE = anam.IDA2A2 " +
                                "left join ANALYSISTOSOURCELINK link on link.IDA3A5 = ana.IDA2A2 " +
                                "left join PROCESSENVELOPE pe on pe.IDA2A2 = link.IDA3B5 " +
                                "left join CHANGEPACKAGED cp on cp.IDA2A2 = link.IDA3B5 " +
                                "left join WTCHANGEREQUEST2 ecr on ecr.IDA2A2 = link.IDA3B5 " +
                                "left join WTCHANGEREQUEST2MASTER ecrm on ecrm.IDA2A2 = ecr.IDA3MASTERREFERENCE " +
                                "left join GWDEALPRODUCTRECORD gdp on aoe.ANALYSISNUMBER = gdp.ANALYSISNUMBER " +
                                "and aoe.VEROID = gdp.VEROID " +
                                "and ((aoe.DATATYPE = 'zproduct' AND gdp.SOURCE = '在制品') " +
                                "OR (aoe.DATATYPE = 'yproduct' AND gdp.SOURCE = '已制品')) " +
                                "where aoe.CMKEYID is not null " +
                                "and aoe.DATATYPE in ('zproduct', 'yproduct') ");
                        if(StrUtil.isNotEmpty(dept)) {
                            sb.append("and aoe.UNIT = '" + dept + "' ");
                        }
                    } else {
                        //查询所有
                        // 查询制品
                        sb.append("select ana.*,link.CLASSNAMEKEYROLEBOBJECTREF,link.PLNUMBER, " +
                                "pe.PROCESSENVELOPENUMBER,pe.NAME as PENAME, " +
                                "cp.CHANGEPACKAGED,cp.NAME as CPNAME, " +
                                "ecrm.WTCHGREQUESTNUMBER,ecrm.NAME as ECRNAME, " +
                                "aoe.ANALYSISNUMBER,  " +
                                "aoe.VEROID,  " +
                                "aoe.DATATYPE, " +
                                "aoe.PRODUCT, " +
                                "aoe.RESPONSER,  " +
                                "aoe.UNIT,  " +
                                "aoe.TASKTIME,  " +
                                "aoe.COMPLETETIME,  " +
                                "gdp.CMKEYID,  " +
                                "COALESCE(CASE WHEN gdp.STATUS = '已闭环' THEN '已完成' ELSE gdp.STATUS END,aoe.DEALSTATUS) AS DEALSTATUS, " +
                                "gdp.DEALTYPE, " +
                                "gdp.CARD, " +
                                "gdp.CREATETIMESTAMP, " +
                                "gdp.COMMENTS, " +
                                "gdp.RESPONSER AS MESRESPONSER " +
                                "from ANALYSISOBJENTRY aoe " +
                                "inner join WTANALYSISACTIVITYMASTER anam on anam.WTCHGANALYSISNUMBER = aoe.ANALYSISNUMBER " +
                                "inner join WTANALYSISACTIVITY ana on ana.IDA3MASTERREFERENCE = anam.IDA2A2 " +
                                "left join ANALYSISTOSOURCELINK link on link.IDA3A5 = ana.IDA2A2 " +
                                "left join PROCESSENVELOPE pe on pe.IDA2A2 = link.IDA3B5 " +
                                "left join CHANGEPACKAGED cp on cp.IDA2A2 = link.IDA3B5 " +
                                "left join WTCHANGEREQUEST2 ecr on ecr.IDA2A2 = link.IDA3B5 " +
                                "left join WTCHANGEREQUEST2MASTER ecrm on ecrm.IDA2A2 = ecr.IDA3MASTERREFERENCE " +
                                "left join GWDEALPRODUCTRECORD gdp on aoe.ANALYSISNUMBER = gdp.ANALYSISNUMBER " +
                                "and aoe.VEROID = gdp.VEROID " +
                                "and ((aoe.DATATYPE = 'zproduct' AND gdp.SOURCE = '在制品') " +
                                "OR (aoe.DATATYPE = 'yproduct' AND gdp.SOURCE = '已制品')) " +
                                "where aoe.CMKEYID is not null ");
                        if(StrUtil.isNotEmpty(dept)) {
                            sb.append("and aoe.UNIT = '" + dept + "' ");
                        }
                    }
                } else {
                    sb.append("SELECT anam.WTCHGANALYSISNUMBER AS ANALYSISNUMBER, " +
                            "ana.IDA2A2, " +
                            "ana.IDA3CONTAINERREFERENCE, " +
                            "ana.IDA3D2ITERATIONINFO, " +
                            "ana.CREATESTAMPA2, " +
                            "ana.STATESTATE, " +
                            "NULL AS CMKEYID, " +
                            "NULL AS VEROID, " +
                            "NULL AS DATATYPE, " +
                            "NULL AS PRODUCT, " +
                            "NULL AS TASKTIME, " +
                            "NULL AS DEALSTATUS, " +
                            "NULL AS UNIT, " +
                            "NULL AS RESPONSER, " +
                            "NULL AS COMPLETETIME, " +
                            "NULL AS CARD, " +
                            "NULL AS DEALTYPE, " +
                            "NULL AS CREATETIMESTAMP, " +
                            "NULL AS COMMENTS, " +
                            "NULL AS MESRESPONSER, " +
                            "link.CLASSNAMEKEYROLEBOBJECTREF, " +
                            "link.PLNUMBER, " +
                            "pe.PROCESSENVELOPENUMBER, " +
                            "pe.NAME AS PENAME, " +
                            "cp.CHANGEPACKAGED, " +
                            "cp.NAME AS CPNAME, " +
                            "ecrm.WTCHGREQUESTNUMBER, " +
                            "ecrm.NAME AS ECRNAME " +
                            "FROM WTANALYSISACTIVITYMASTER anam " +
                            "INNER JOIN WTANALYSISACTIVITY ana ON ana.IDA3MASTERREFERENCE = anam.IDA2A2 " +
                            "LEFT JOIN ANALYSISTOSOURCELINK link ON link.IDA3A5 = ana.IDA2A2 " +
                            "LEFT JOIN PROCESSENVELOPE pe ON pe.IDA2A2 = link.IDA3B5 " +
                            "LEFT JOIN CHANGEPACKAGED cp ON cp.IDA2A2 = link.IDA3B5 " +
                            "LEFT JOIN WTCHANGEREQUEST2 ecr ON ecr.IDA2A2 = link.IDA3B5 " +
                            "LEFT JOIN WTCHANGEREQUEST2MASTER ecrm ON ecrm.IDA2A2 = ecr.IDA3MASTERREFERENCE " +
                            "WHERE anam.WTCHGANALYSISNUMBER IS NOT NULL ");
                    if(StrUtil.isNotEmpty(state)) {
                        if(AnalysisConstant.DEAL_STATUS_WORKING.equals(state)) {
                            state = Constants.STATE_INWORK;
                        } else if(AnalysisConstant.DEAL_STATUS_FINISH.equals(state)) {
                            state = Constants.STATE_APPROVED;
                        }
                    }
                }

                // 添加通用过滤条件
                if(creator != null) {
                    sb.append(" AND ana.IDA3D2ITERATIONINFO = '").append(creator.getPersistInfo().getObjectIdentifier().getId()).append("' ");
                }
                if(StrUtil.isNotEmpty(containerStr)) {
                    String[] containers = containerStr.split(":");
                    if(containers.length > 2) {
                        containerStr = containers[2];
                        sb.append(" AND ana.IDA3CONTAINERREFERENCE = '").append(Long.valueOf(containerStr).longValue()).append("' ");
                    }
                }
                if(StrUtil.isNotEmpty(ananumber)) {
                    sb.append(" AND anam.WTCHGANALYSISNUMBER LIKE '%").append(ananumber).append("%' ");
                }
                if(AnalysisConstant.SEARCH_TYPE_SHEJIGENGAI.equals(type)) {
                    sb.append(" AND link.CLASSNAMEKEYROLEBOBJECTREF = '").append(ChangePackaged.class.getName()).append("' ");
                    if(StrUtil.isNotEmpty(objnumber)) {
                        sb.append(" AND cp.CHANGEPACKAGED LIKE '%").append(objnumber).append("%' ");
                    }
                    if(StrUtil.isNotEmpty(objname)) {
                        sb.append(" AND cp.NAME LIKE '%").append(objname).append("%' ");
                    }
                } else if(AnalysisConstant.SEARCH_TYPE_SHEJIPIANLI.equals(type)) {
                    sb.append(" AND link.CLASSNAMEKEYROLEBOBJECTREF = '").append(ProcessEnvelope.class.getName()).append("' ");
                    if(StrUtil.isNotEmpty(objnumber)) {
                        sb.append(" AND pe.PROCESSENVELOPENUMBER LIKE '%").append(objnumber).append("%' ");
                    }
                    if(StrUtil.isNotEmpty(objname)) {
                        sb.append(" AND pe.NAME LIKE '%").append(objname).append("%' ");
                    }
                } else {
                    if(StrUtil.isNotEmpty(objnumber)) {
                        sb.append(" AND (cp.CHANGEPACKAGED LIKE '%")
                                .append(objnumber).append("%' OR " + "pe.PROCESSENVELOPENUMBER LIKE '%")
                                .append(objnumber).append("%' OR " + "ecrm.WTCHGREQUESTNUMBER LIKE '%")
                                .append(objnumber).append("%') ");
                    }
                    if(StrUtil.isNotEmpty(objname)) {
                        sb.append(" AND (cp.NAME LIKE '%").append(objname).append("%' OR " + "pe.NAME LIKE '%")
                                .append(objname).append("%' OR " + "ecrm.NAME LIKE '%")
                                .append(objname).append("%') ");
                    }
                }
                if(StrUtil.isNotEmpty(state) && "detail".equals(viewType)) {
                    if(AnalysisConstant.DEAL_STATUS_FINISH.equals(state)){
                        sb.append(" AND (gdp.STATUS IS NOT NULL OR aoe.DEALSTATUS = '").append(state).append("') ");
                    } else if(AnalysisConstant.DEAL_STATUS_WORKING.equals(state)) {
                        sb.append(" AND gdp.STATUS IS NULL AND aoe.DEALSTATUS = '").append(state).append("' ");
                    }
                } else if(StrUtil.isNotEmpty(state)) {
                    if(Constants.STATE_INWORK.equals(state)){
                        sb.append(" AND (ana.STATESTATE = '").append(state).append("' OR ana.STATESTATE = 'REWORK') ");
                    } else {
                        sb.append(" AND ana.STATESTATE = '").append(state).append("' ");
                    }
                }
                if(StrUtil.isNotEmpty(responser) && "detail".equals(viewType)) {
                    responser = responser.split(",")[0].split("=")[1];
                    WTUser user = UserUtil.getUser(responser);
                    if(user != null) {
                        ReferenceFactory rf = new ReferenceFactory();
                        String userId = rf.getReferenceString(user);
                        sb.append(" AND aoe.RESPONSER = '").append(userId).append("' ");
                    }
                }
                if(StrUtil.isNotEmpty(startDate)) {
                    Date dateFrom = WTStandardDateFormat.parse(startDate, "yyyy/M/d");
                    Calendar ca = Calendar.getInstance(WTContext.getContext().getTimeZone());
                    ca.setTime(dateFrom);
                    dateFrom = ca.getTime();
                    Timestamp timestamp = new Timestamp(dateFrom.getTime() + 8 * 60 * 60 * 1000);
                    String format = DateUtil.format(timestamp, "yyyy-MM-dd HH:mm:ss");
                    sb.append(" AND ana.CREATESTAMPA2 >= TO_DATE('").append(format).append("','YYYY-MM-DD HH24:MI:SS') ");
                }
                if(StrUtil.isNotEmpty(endDate)) {
                    Date dateFrom = WTStandardDateFormat.parse(endDate, "yyyy/M/d");
                    Calendar ca = Calendar.getInstance(WTContext.getContext().getTimeZone());
                    ca.setTime(dateFrom);
                    dateFrom = ca.getTime();
                    Timestamp timestamp = new Timestamp(dateFrom.getTime() + 8 * 60 * 60 * 1000);
                    String format = DateUtil.format(timestamp, "yyyy-MM-dd HH:mm:ss");
                    sb.append(" AND ana.CREATESTAMPA2 <= TO_DATE('").append(format).append("','YYYY-MM-DD HH24:MI:SS') ");
                }
                sb.append(" ORDER BY ana.CREATESTAMPA2 DESC");

                logger.info("SQL Query for tasktype={},sql={}", tasktype, sb);

                rs = conn.executeQuery(sb.toString());
                while(rs.next()) {
                    String analysisNumber = rs.getString("ANALYSISNUMBER") != null ? rs.getString("ANALYSISNUMBER") : "";
                    String veroid = rs.getString("VEROID") != null ? rs.getString("VEROID") : "";

                    Map<String, String> map = new HashMap<>();
                    String datatype = rs.getString("DATATYPE") != null ? rs.getString("DATATYPE") : "";
                    String anaProduct = rs.getString("PRODUCT") != null ? rs.getString("PRODUCT") : "";
                    String gdp_cmkeyid = rs.getString("CMKEYID") != null ? rs.getString("CMKEYID") : "";
                    String dealType = rs.getString("DEALTYPE") != null ? rs.getString("DEALTYPE") : "";
                    String zerenren = "";
                    String unit = rs.getString("UNIT") != null ? rs.getString("UNIT") : "";
                    Timestamp timestamp = rs.getTimestamp("TASKTIME");
                    String completetime = rs.getString("COMPLETETIME") != null ? rs.getString("COMPLETETIME") : "";
                    String card = rs.getString("CARD") != null ? rs.getString("CARD") : "";
                    String dealstatus = rs.getString("DEALSTATUS") != null ? rs.getString("DEALSTATUS") : "";
                    String taskName = "";
                    String taskTime = timestamp != null ? DateUtil.format(timestamp, "yyyy-MM-dd") : "";
                    String itSystem = "PDM系统";  //默认PDM系统
                    String createstampa2 = rs.getString("CREATESTAMPA2") != null ? rs.getString("CREATESTAMPA2") : "";
                    String createTime = "";
                    if(StrUtil.isNotEmpty(createstampa2)) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = sdf.parse(createstampa2);
                            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                            createTime = sdf.format(date);
                        } catch(Exception e) {
                            logger.error("Failed to parse CREATESTAMPA2: {}", createstampa2);
                        }
                    }
                    map.put("analysisTime", createTime);
                    String ida3D2ITERATIONINFO = rs.getString("IDA3D2ITERATIONINFO") != null ? rs.getString("IDA3D2ITERATIONINFO") : "";
                    WTUser anaCreator = null;
                    if(StrUtil.isNotEmpty(ida3D2ITERATIONINFO)) {
                        try {
                            anaCreator = UserUtil.getWTUser(Long.parseLong(ida3D2ITERATIONINFO));
                        } catch(NumberFormatException e) {
                            logger.error("Invalid IDA3D2ITERATIONINFO: {}", ida3D2ITERATIONINFO);
                        }
                    }
                    if(anaCreator != null) {
                        map.put("creator", anaCreator.getFullName());
                    }

                    if("detail".equals(viewType)) {
                        datatype = typeMap.get(datatype);
                        if(AnalysisConstant.DEAL_STATUS_WORKING.equals(dealstatus)){
                            if(AnalysisConstant.SOURCE_ZAIZHIPIN.equals(datatype) || AnalysisConstant.SOURCE_YIZHIPIN.equals(datatype)) {
                                if(StrUtil.isEmpty(anaProduct)){
                                    //主任师还未处理更改影响分析任务
                                    taskName = "更改影响分析";
                                    taskTime = createTime;
                                    itSystem = "PDM系统";
                                    if(anaCreator != null) {
                                        zerenren = anaCreator.getFullName();
                                    }
                                } else {
                                    if(StrUtil.isEmpty(card)) {
                                        //NC/MES还未返回处理意见
                                        if(AnalysisConstant.SOURCE_ZAIZHIPIN.equals(datatype)) {
                                            NcResponseDataVo ncResponseDataVo = OtherSystemIntegrationHelper.getNcResponseDataVo(analysisNumber, veroid);
                                            if(ncResponseDataVo.getZZPZZDetail() != null && ncResponseDataVo.getZZPZZDetail().size() > 0) {
                                                //已制品计调员已经处理，工艺员未处理
                                                taskName = "待工艺员处理制品意见";
                                                taskTime = ncResponseDataVo.getZZPZZDetail().get(0).getZzpzzendtime();
                                                itSystem = "MES系统";
                                            } else {
                                                //计调员未处理
                                                taskName = "待计调员处理制品意见";
                                                taskTime = createTime;
                                                itSystem = "NC系统";
                                                zerenren = NodeInstanceTaskService.getPlanningDispatcherName(analysisNumber, veroid, AnalysisConstant.SOURCE_YIZHIPIN);
                                            }
                                        } else if(AnalysisConstant.SOURCE_YIZHIPIN.equals(datatype)) {
                                            taskName = "待计调员处理制品意见";
                                            taskTime = createTime;
                                            itSystem = "NC系统";
                                        }
                                    } else {
                                        if(StringUtils.endsWith(gdp_cmkeyid, "_ZJWX")) {
                                            datatype = "整件外协";
                                            if(!AnalysisConstant.DEAL_STATUS_FINISH.equals(dealstatus)) {
                                                taskName = "待外协员处理整件外协";
                                                String createtimestamp = rs.getString("CREATETIMESTAMP") != null ? rs.getString("CREATETIMESTAMP") : "";
                                                if(StrUtil.isNotEmpty(createtimestamp)) {
                                                    try {
                                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        Date date = sdf.parse(createtimestamp);
                                                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                                                        taskTime = sdf.format(date);
                                                    } catch(Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                itSystem = "NC系统";
                                                String mesResponser = rs.getString("MESRESPONSER") != null ? rs.getString("MESRESPONSER") : "";
                                                if(StrUtil.isNotEmpty(mesResponser)) {
                                                    zerenren = mesResponser;
                                                }
                                            }
                                        } else {
                                            String comments = rs.getString("COMMENTS") != null ? rs.getString("COMMENTS") : "";
                                            if("WAITNC".equals(comments)) {
                                                //NC驳回等待重新处理
                                                taskName = "待计调员重新处理制品意见";
                                                taskTime = createTime;
                                                itSystem = "NC系统";
                                                zerenren = NodeInstanceTaskService.getPlanningDispatcherName(analysisNumber, veroid, AnalysisConstant.SOURCE_YIZHIPIN);
                                            } else {
                                                if(AnalysisConstant.SOURCE_YIZHIPIN.equals(datatype) && card.contains("_")) {
                                                    card = card.split("_")[0];
                                                }
                                                logger.info(analysisNumber + " " + gdp_cmkeyid + " " + card + " " + dealType + " " + datatype);
                                                JSONObject wfJson = NodeFinder.findCurrentNode(analysisNumber, veroid, dwGraphVoTimedCache, card, dealType, datatype);
                                                logger.info("wfJson={}", wfJson);
                                                JSONObject node = wfJson.optJSONObject("node");
                                                if(node != null) {
                                                    taskName = node.optString("taskName", taskName);
                                                    taskTime = node.optString("taskStartDate", taskTime);
                                                    dealstatus = node.optString("taskState", dealstatus);
                                                    itSystem = node.optString("itSystem", "");
                                                    zerenren = node.optString("taskOwner", zerenren);
                                                    if("已制品返修工艺计划下达".equals(taskName) || "已制品返修执行中".equals(taskName)) {
                                                        card = node.optString("card", card);
                                                        unit = zerenren;
                                                        zerenren = "/";
                                                    } else if("已制品已提前返修完成".equals(taskName)) {
                                                        unit = zerenren;
                                                        zerenren = "/";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                if(timestamp == null) {
                                    taskName = "更改影响分析";
                                    taskTime = createTime;
                                    if(anaCreator != null) {
                                        zerenren = anaCreator.getFullName();
                                    }
                                }else {
                                    taskName = "更改执行";
                                }
                            }
                        }
                        if(AnalysisConstant.DEAL_STATUS_FINISH.equals(dealstatus)) {
                            itSystem = "PDM系统";
                            if(StrUtil.isEmpty(taskName)){
                                taskName = datatype + "已完成";
                            }
                        }
                        map.put("taskName", taskName);
                        map.put("taskTime", taskTime);
                        map.put("taskState", dealstatus);
                        map.put("itSystem", itSystem);
                        map.put("responser", zerenren);
                        map.put("card", card);
                    } else {
                        String stateStr = rs.getString("STATESTATE") != null ? rs.getString("STATESTATE") : "";
                        if(StrUtil.isNotEmpty(stateStr)) {
                            dealstatus = ProcessConstants.map.getOrDefault(stateStr, "");
                        }
                        map.put("taskState", dealstatus);
                    }
                    String ida2A2 = rs.getString("IDA2A2") != null ? rs.getString("IDA2A2") : "";
                    String sourceClazz = rs.getString("CLASSNAMEKEYROLEBOBJECTREF") != null ? rs.getString("CLASSNAMEKEYROLEBOBJECTREF") : "";
                    String objNumber = "";
                    String name = "";
                    if(ProcessEnvelope.class.getName().equals(sourceClazz)) {
                        objNumber = rs.getString("PROCESSENVELOPENUMBER") != null ? rs.getString("PROCESSENVELOPENUMBER") : "";
                        name = rs.getString("PENAME") != null ? rs.getString("PENAME") : "";
                    } else if(ChangePackaged.class.getName().equals(sourceClazz)) {
                        objNumber = rs.getString("CHANGEPACKAGED") != null ? rs.getString("CHANGEPACKAGED") : "";
                        name = rs.getString("CPNAME") != null ? rs.getString("CPNAME") : "";
                    } else if(WTChangeRequest2.class.getName().equals(sourceClazz)) {
                        objNumber = rs.getString("WTCHGREQUESTNUMBER") != null ? rs.getString("WTCHGREQUESTNUMBER") : "";
                        name = rs.getString("ECRNAME") != null ? rs.getString("ECRNAME") : "";
                    }
                    String ida3CONTAINERREFERENCE = rs.getString("IDA3CONTAINERREFERENCE") != null ? rs.getString("IDA3CONTAINERREFERENCE") : "";
                    PDMLinkProduct product = null;
                    if(StrUtil.isNotEmpty(ida3CONTAINERREFERENCE)) {
                        try {
                            product = WTContainerUtil.getProductByOid(Long.parseLong(ida3CONTAINERREFERENCE));
                        } catch(NumberFormatException e) {
                            logger.error("Invalid IDA3CONTAINERREFERENCE: {}", ida3CONTAINERREFERENCE);
                        }
                    }
                    map.put("containerName", product != null ? product.getName() : "");
                    map.put("analysisNumber", analysisNumber);
                    map.put("analysisurl", "<input type=\"button\" name=\"analysis\" value=\"查看\" onClick=\"javascript:viewAnalysis('OR:" + WTAnalysisActivity.class.getName() + ":" + ida2A2 + "');\" />");
                    map.put("objNumber", objNumber);
                    map.put("objName", name);
                    map.put("plNumber", rs.getString("PLNUMBER") != null ? rs.getString("PLNUMBER") : "");
                    map.put("tasktype", datatype);
                    map.put("dept", unit);
                    map.put("completeTime", completetime);

                    try {
                        ReferenceFactory rf = new ReferenceFactory();
                        if(StrUtil.isNotEmpty(veroid)) {
                            Persistable persistable = rf.getReference("VR:" + veroid).getObject();
                            if(persistable != null) {
                                if(persistable instanceof WTPart) {
                                    map.put("pboNumber", ((WTPart) persistable).getNumber());
                                    map.put("pboName", ((WTPart) persistable).getName());
                                } else if(persistable instanceof WTDocument) {
                                    WTDocument doc = (WTDocument) persistable;
                                    TypeIdentifier identifier = TypedUtility.getTypeIdentifier(doc);
                                    String typename = identifier.getTypename();
                                    if(typename.contains("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN")) {
                                        WTPart part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(doc);
                                        if(part != null) {
                                            map.put("pboNumber", part.getNumber());
                                        } else {
                                            map.put("pboNumber", doc.getNumber());
                                        }
                                    } else {
                                        map.put("pboNumber", doc.getNumber());
                                    }
                                    map.put("pboName", doc.getName());
                                }
                            }
                        }
                        if(StrUtil.isEmpty(zerenren)) {
                            try {
                                zerenren = rs.getString("RESPONSER") != null ? rs.getString("RESPONSER") : "";
                                WTUser user = (WTUser) rf.getReference(zerenren).getObject();
                                map.put("responser", user != null ? user.getFullName() : zerenren);
                            } catch(Exception e) {
                                map.put("responser", responser);
                                logger.error("Failed to resolve RESPONSER: {}", zerenren);
                            }
                        }
                    } catch(Exception e) {
                        logger.error("Error processing reference for VEROID: {}, RESPONSER:{}", veroid, zerenren);
                        continue;
                    }
                    list.add(map);
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(rs != null) {
                    try {
                        rs.close();
                    } catch(SQLException e) {
                        logger.error("Failed to close ResultSet: {}", e.getMessage());
                    }
                }
                if(conn != null) {
                    try {
                        conn.close();
                    } catch(SQLException e) {
                        logger.error("Failed to close DB connection: {}", e.getMessage());
                    }
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
        table.setId("ext.casc.common.mvc.builder.analysis.SearchDesignAnalysisActivityBuilder");
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

        columnConfig = factory.newColumnConfig("card", "路卡号", false);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("taskName", "环节名称", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("completeTime", "要求完成时间", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("taskTime", "流程到达时间", false);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("taskState", "流程状态", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("itSystem", "信息系统", true);
        columnConfig.setAutoSize(true);
        table.addComponent(columnConfig);
        return table;
    }

    @Override
    public ConfigurableTable buildConfigurableTable(String s) throws WTException {
        return null;
    }
}