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

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;

@ComponentBuilder("SearchGONGYIRENWU_table_id_gyrw")
public class GYRWBuilder extends AbstractComponentBuilder {
    public static String partNumber;
    public static String cpdh;
    public static String xhdh;
    public static String jdbj;

    private final static String CREATE_DATE = "thePersistInfo.createStamp";
    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        String work = String.valueOf(params.getParameter("work"));
        ArrayList<ProcessTaskItem> list = new ArrayList<ProcessTaskItem>();
        if ("search".equals(work)) {
            NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
            String faqizhe = ProcessUtil.getNotNullParam(params.getParameter("faqizhe"));
            if (faqizhe!=null&&!"".equals(faqizhe)) {

                faqizhe=faqizhe.split(",")[0].split("=")[1];
            }
            String jieshouzhe = ProcessUtil.getNotNullParam(params.getParameter("jieshouzhe"));
            if (jieshouzhe!=null&&!"".equals(jieshouzhe)) {

                jieshouzhe=jieshouzhe.split(",")[0].split("=")[1];
            }
            String contain = ProcessUtil.getNotNullParam(params.getParameter("containerTypeList"));
            String xinghaodaihao = ProcessUtil.getNotNullParam(params.getParameter("xinghaodaihao"));
            String renwuState = ProcessUtil.getNotNullParam(params.getParameter("rwzt"));
            String chejian = ProcessUtil.getNotNullParam(params.getParameter("cj"));
            String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
            String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));

            String startDate2 = ProcessUtil.getNotNullParam(params.getParameter("startDate2"));
            String endDate2 = ProcessUtil.getNotNullParam(params.getParameter("endDate2"));

            //计划时间
            String startDate3 = ProcessUtil.getNotNullParam(params.getParameter("startDate3"));
            String endDate3 = ProcessUtil.getNotNullParam(params.getParameter("endDate3"));
//            String startDate = (String) commandBean.getText().get("startDate_col_startDate");
//            String endDate = (String) commandBean.getText().get("endDate_col_endDate");
            if (!"".equals(endDate)&&!"null".equals(endDate)&&endDate!=null) {
                endDate=addOneDate(endDate);
            }
            if (!"".equals(endDate2)&&!"null".equals(endDate2)&&endDate2!=null) {
                endDate2=addOneDate(endDate2);
            }

            if (!"".equals(endDate3)&&!"null".equals(endDate3)&&endDate3!=null) {
                endDate3=addOneDate(endDate3);
            }
//            String yuqirenwu = ProcessUtil.getNotNullParam(params.getParameter("yuqirenwu"));
            String partNumber = ProcessUtil.getNotNullParam(params.getParameter("partNumber"));
            String tasktype = ProcessUtil.getNotNullParam(params.getParameter("tasktype"));
//            String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
//            String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));
         // 最后更新时间起始条件
            TimeZone tz = WTContext.getContext().getTimeZone();
            Calendar ca = Calendar.getInstance(tz);
            try {
                int index=0;
                QuerySpec qs = new QuerySpec(ProcessTaskItem.class);

                if (startDate != null && !"".equals(startDate)) {
                    Date dateFrom = WTStandardDateFormat.parse(startDate, "yyyy/M/d");
                    // 加入时区信息
                    ca.setTime(dateFrom);
                    dateFrom = ca.getTime();
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc1 =  new SearchCondition(ProcessTaskItem.class,CREATE_DATE,SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime()+8*60*60*1000));
                    qs.appendSearchCondition(sc1);
                }


                if (endDate != null && !"".equals(endDate)) {
                    Date dateFrom1 = WTStandardDateFormat.parse(endDate, "yyyy/M/d");
                    // 加入时区信息
                    ca.setTime(dateFrom1);
                    dateFrom1 = ca.getTime();
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc11 =  new SearchCondition(ProcessTaskItem.class,CREATE_DATE,SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime()+8*60*60*1000));
                    qs.appendSearchCondition(sc11);
                }
                if (startDate2 != null && !"".equals(startDate2)) {
                    Date dateFrom = WTStandardDateFormat.parse(startDate2, "yyyy/M/d");
                    // 加入时区信息
                    ca.setTime(dateFrom);
                    dateFrom = ca.getTime();
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc1 =  new SearchCondition(ProcessTaskItem.class,"thePersistInfo.modifyStamp",SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime()+8*60*60*1000));
                    qs.appendSearchCondition(sc1);
                }


                if (endDate2 != null && !"".equals(endDate2)) {
                    Date dateFrom1 = WTStandardDateFormat.parse(endDate2, "yyyy/M/d");
                    // 加入时区信息
                    ca.setTime(dateFrom1);
                    dateFrom1 = ca.getTime();
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc11 =  new SearchCondition(ProcessTaskItem.class,"thePersistInfo.modifyStamp",SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime()+8*60*60*1000));
                    qs.appendSearchCondition(sc11);
                }

                if (startDate3 != null && !"".equals(startDate3)) {
                    Date dateFrom = WTStandardDateFormat.parse(startDate3, "yyyy/M/d");
                    // 加入时区信息
                    ca.setTime(dateFrom);
                    dateFrom = ca.getTime();
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc1 =  new SearchCondition(ProcessTaskItem.class,"endDate",SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime()+8*60*60*1000));
                    qs.appendSearchCondition(sc1);
                }


                if (endDate3 != null && !"".equals(endDate3)) {
                    Date dateFrom1 = WTStandardDateFormat.parse(endDate3, "yyyy/M/d");
                    // 加入时区信息
                    ca.setTime(dateFrom1);
                    dateFrom1 = ca.getTime();
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc11 =  new SearchCondition(ProcessTaskItem.class,"endDate",SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime()+8*60*60*1000));
                    qs.appendSearchCondition(sc11);
                }
                if (tasktype != null && !"".equals(tasktype)) {
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_TYPE, SearchCondition.EQUAL, tasktype, false);
                    qs.appendSearchCondition(sc);
                }
                if (renwuState != null && !"".equals(renwuState)) {
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_ITEM_STATE, SearchCondition.EQUAL, renwuState, false);
                    qs.appendSearchCondition(sc);
                }
                if (chejian != null && !"".equals(chejian)) {
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.CHEJIAN, SearchCondition.EQUAL, chejian, false);
                    qs.appendSearchCondition(sc);
                }
                if (partNumber != null && !"".equals(partNumber)) {
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.LIKE, "%"+partNumber+"%", false);
                    qs.appendSearchCondition(sc);
                }
                if (faqizhe != null && !"".equals(faqizhe)) {
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.ZHURENGONGYISHI, SearchCondition.EQUAL, faqizhe, false);
                    qs.appendSearchCondition(sc);
                }
                if (jieshouzhe != null && !"".equals(jieshouzhe)) {
                    if (index>0) {
                        qs.appendAnd();
                    }
                    index++;
                    SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.OWNER, SearchCondition.EQUAL, jieshouzhe, false);
                    qs.appendSearchCondition(sc);
                }
                if (contain!=null&&!"".equals(contain)) {
                    if (contain.startsWith("OR:wt.pdmlink.PDMLinkProduct")) {
                        contain=contain.split("OR:wt.pdmlink.PDMLinkProduct:")[1];
                        if (index>0) {
                            qs.appendAnd();
                        }
                        index++;
//                        qs.appendWhere(new SearchCondition(ProcessTaskItem.class,ProcessTaskItem.CONTAINER_ID,Long.valueOf(contain), true));
                        qs.appendWhere(new SearchCondition(ProcessTaskItem.class,ProcessTaskItem.CONTAINER_ID,SearchCondition.EQUAL, Long.valueOf(contain).longValue()));
                    }

                }


                QueryResult qr = PersistenceHelper.manager.find(qs);
                  while (qr.hasMoreElements()) {
                    ProcessTaskItem pti = (ProcessTaskItem) qr.nextElement();
//                    if ("on".equals(yuqirenwu)) {
//                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//                        Timestamp endDate1 = pti.getEndDate();
//                        if (endDate1!=null) {
//                            String endTime = format.format(endDate1).trim();
//                            Timestamp shijianshijian = pti.getModifyTimestamp();
//                            if (shijianshijian!=null) {
//                                String shijianTime = format.format(shijianshijian).trim();
//                                Long endTimeLong = Long.valueOf(endTime);
//                                Long shijianTimeLong = Long.valueOf(shijianTime);
//                                if (shijianTimeLong>endTimeLong) {
//                                    list.add(pti);
//                                }
//                            }
//                            }
//
//                        }else{

                            list.add(pti);
//                        }

                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setId("SearchGONGYIRENWU_table_id_gyrw");
//        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setSelectable(true);
        tableConfig.setConfigurable(true);


        tableConfig.setActionModel("custom_SearchWorkItem_actions");
        tableConfig.setLabel("工艺任务完成情况汇总表");
        ColumnConfig renwuleixing = factory.newColumnConfig("taskItemName", true);
        renwuleixing.setAutoSize(true);
        renwuleixing.setLabel("任务名称");
        tableConfig.addComponent(renwuleixing);

        ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
        numberColumnConfig.setAutoSize(true);
        numberColumnConfig.setInfoPageLink(true);
        tableConfig.addComponent(numberColumnConfig);

        ColumnConfig wenjianName = factory.newColumnConfig("gyrwName", true);
        wenjianName.setAutoSize(true);
        wenjianName.setLabel("名称");
        wenjianName.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(wenjianName);

        ColumnConfig banben = factory.newColumnConfig("version", true);
        banben.setAutoSize(true);
        banben.setLabel("版本");
        tableConfig.addComponent(banben);

        ColumnConfig gyrwState = factory.newColumnConfig("taskItemState", true);
        gyrwState.setLabel("状态");
        gyrwState.setAutoSize(true);
        tableConfig.addComponent(gyrwState);


        ColumnConfig technicsState = factory.newColumnConfig("technicsState", true);
        technicsState.setLabel("工艺文件状态");
        technicsState.setAutoSize(true);
        technicsState.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(technicsState);



        ColumnConfig chejian = factory.newColumnConfig("chejian", true);
        chejian.setLabel("车间");
        chejian.setAutoSize(true);
        tableConfig.addComponent(chejian);

        ColumnConfig owner = factory.newColumnConfig("owner", true);
        owner.setLabel("责任人");
        owner.setAutoSize(true);
        owner.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(owner);

        ColumnConfig zhurengongyishi = factory.newColumnConfig("zhurengongyishi", true);
        zhurengongyishi.setLabel("发起者");
        zhurengongyishi.setId("zhurengongyishi");
        tableConfig.addComponent(zhurengongyishi);

        ColumnConfig createDate = factory.newColumnConfig("thePersistInfo.createStamp", true);
        createDate.setLabel("创建时间");
        createDate.setId("thePersistInfo.createStamp");
        tableConfig.addComponent(createDate);

        ColumnConfig gyrwJhEndTime = factory.newColumnConfig("endDate", true);
        gyrwJhEndTime.setAutoSize(true);
        gyrwJhEndTime.setLabel("计划完成时间");
        tableConfig.addComponent(gyrwJhEndTime);

        ColumnConfig gyrwSjEndTime = factory.newColumnConfig("thePersistInfo.modifyStamp", true);
        gyrwSjEndTime.setAutoSize(true);
        gyrwSjEndTime.setLabel("实际完成时间");
        gyrwSjEndTime.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(gyrwSjEndTime);

        ColumnConfig yuqiFlag = factory.newColumnConfig("yuqiFlag", true);
        yuqiFlag.setAutoSize(true);
        yuqiFlag.setLabel("是否逾期");
        yuqiFlag.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(yuqiFlag);

        return tableConfig;
    }

    private String addOneDate(String date) throws ParseException {
        Calendar   calendar   =   new   GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date de = df.parse(date);
        calendar.setTime(de);
        calendar.add(calendar.DATE,1);
      return  new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime());
    }

}
