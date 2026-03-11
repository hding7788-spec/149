package ext.casc.report.mvc.builders;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;

import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.process.ProcessPlan;
import ext.casc.process.util.ProcessUtil;

@ComponentBuilder("SearchGONGYIRENWU_table_id_fgyrw")
public class FGYRWBuilder extends AbstractComponentBuilder {
    public static String partNumber;
    public static String cpdh;
    public static String xhdh;
    public static String jdbj;

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        String work = String.valueOf(params.getParameter("work"));
        ArrayList<ProcessPlan> list = new ArrayList<ProcessPlan>();
        ArrayList<ProcessPlan> list1 = new ArrayList<ProcessPlan>();
        if ("search".equals(work)) {

            NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
            String startdate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
            String enddate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));
            if (!"".equals(enddate) && !"null".equals(enddate) && enddate != null) {
                enddate = addOneDate(enddate);
            }
            String zhuti = ProcessUtil.getNotNullParam(params.getParameter("zhuti"));
            String contain = ProcessUtil.getNotNullParam(params.getParameter("containerTypeList"));
            String xiangmubu = ProcessUtil.getNotNullParam(params.getParameter("xiangmubu"));
            String zhixingbumen = ProcessUtil.getNotNullParam(params.getParameter("zhixingbumen"));

            int index=0;
            QuerySpec qs = new QuerySpec(ProcessPlan.class);
            TimeZone tz = WTContext.getContext().getTimeZone();
            Calendar ca = Calendar.getInstance(tz);
            if (startdate != null && !"".equals(startdate)) {
                Date dateFrom = WTStandardDateFormat.parse(startdate, "yyyy/M/d");
                // 加入时区信息
                ca.setTime(dateFrom);
                dateFrom = ca.getTime();
                if (index>0) {
                    qs.appendAnd();
                }
                index++;
                SearchCondition sc1 =  new SearchCondition(ProcessPlan.class,"thePersistInfo.createStamp",SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime()+8*60*60*1000));
                qs.appendSearchCondition(sc1);
            }


            if (enddate != null && !"".equals(enddate)) {
                Date dateFrom1 = WTStandardDateFormat.parse(enddate, "yyyy/M/d");
                // 加入时区信息
                ca.setTime(dateFrom1);
                dateFrom1 = ca.getTime();
                if (index>0) {
                    qs.appendAnd();
                }
                index++;
                SearchCondition sc11 =  new SearchCondition(ProcessPlan.class,"thePersistInfo.createStamp",SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime()+8*60*60*1000));
                qs.appendSearchCondition(sc11);
            }
            if (contain != null && !"".equals(contain)) {
                if (contain.startsWith("OR:wt.pdmlink.PDMLinkProduct")) {
                    contain=contain.split("OR:wt.pdmlink.PDMLinkProduct:")[1];
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    qs.appendWhere(new SearchCondition(ProcessPlan.class,ProcessPlan.CONTAINER_ID,SearchCondition.EQUAL, Long.valueOf(contain).longValue()));
//                    qs.appendWhere(new SearchCondition(ProcessPlan.class, ProcessPlan.CONTAINER_ID, contain, true));
                }
            }
            if (xiangmubu != null && !"".equals(xiangmubu)) {
                if (index>0) {
                    qs.appendAnd();
                }
                index++;
                qs.appendWhere(new SearchCondition(ProcessPlan.class,ProcessPlan.XIANGMUBU,SearchCondition.EQUAL, xiangmubu));
                //qs.appendWhere(new SearchCondition(ProcessPlan.class, ProcessPlan.XIANGMUBU, xiangmubu, true));
            }


            QueryResult qr = PersistenceHelper.manager.find(qs);
            while (qr.hasMoreElements()) {
                ProcessPlan plan = (ProcessPlan) qr.nextElement();
                    list.add(plan);
            }
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setId("SearchGONGYIRENWU_table_id_fgyrw");
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setConfigurable(false);
        tableConfig.setSelectable(true);
        tableConfig.setSingleSelect(true);
        tableConfig.setActionModel("custom_SearchWorkItem_actions");
        tableConfig.setLabel("非工艺设计类工艺任务统计报表");
        ColumnConfig fgyrwxinghao = factory.newColumnConfig("containerReference", true);
        fgyrwxinghao.setAutoSize(true);
        fgyrwxinghao.setLabel("上下文");
        tableConfig.addComponent(fgyrwxinghao);

        ColumnConfig fgyrwztlx = factory.newColumnConfig("zhuti", true);
        fgyrwztlx.setAutoSize(true);
        fgyrwztlx.setLabel("主题类型");
        tableConfig.addComponent(fgyrwztlx);

        ColumnConfig fgyrwxmb = factory.newColumnConfig("xiangmubu", true);
        fgyrwxmb.setAutoSize(true);
        fgyrwxmb.setLabel("项目部");
        tableConfig.addComponent(fgyrwxmb);

        ColumnConfig fgyrwzxcj = factory.newColumnConfig("fgyzhixingchejian", true);
        fgyrwzxcj.setAutoSize(true);
        fgyrwzxcj.setLabel("执行车间");
        fgyrwzxcj.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(fgyrwzxcj);

        ColumnConfig fgyrwzxr = factory.newColumnConfig("fgyrwzxr", true);
        fgyrwzxr.setLabel("执行人");
        fgyrwzxr.setAutoSize(true);
        fgyrwzxr.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(fgyrwzxr);

        ColumnConfig zrgyssjsj = factory.newColumnConfig("zrgyssjsj", true);
        zrgyssjsj.setAutoSize(true);
        zrgyssjsj.setLabel("主任工艺师设置计划完成时间");
        zrgyssjsj.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(zrgyssjsj);

        ColumnConfig cjzzshjhwcsj = factory.newColumnConfig("cjzzshjhwcsj", true);
        cjzzshjhwcsj.setAutoSize(true);
        cjzzshjhwcsj.setLabel("车间组长设置计划完成时间");
        cjzzshjhwcsj.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(cjzzshjhwcsj);

        ColumnConfig sjwcsh = factory.newColumnConfig("sjwcsh", true);
        sjwcsh.setAutoSize(true);
        sjwcsh.setLabel("实际完成时间");
        sjwcsh.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(sjwcsh);

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

}
