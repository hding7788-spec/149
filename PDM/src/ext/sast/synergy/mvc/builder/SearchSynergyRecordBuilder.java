package ext.sast.synergy.mvc.builder;

import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.csc.utilities.principal.CSCPrincipal;
import ext.sast.center.record.GWMQRecordService;
import ext.sast.center.record.bean.GWMQRecord;
import ext.sast.synergy.constants.SynergyConstants;
import ext.sast.synergy.util.SynergyUtil;
import wt.org.WTUser;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ComponentBuilder("SearchSynergyRecordBuilder_ID")
public class SearchSynergyRecordBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        Map<String, String> parametersMap = getQueryParameters(config, params);
        System.out.println("parametersMap=" + parametersMap);
        String cjsj = parametersMap.get("CJSJ");
        String jssj = parametersMap.get("JSSJ");
        List<GWMQRecord> list = new ArrayList<GWMQRecord>();
        if(parametersMap.size() == 0){
            list = GWMQRecordService.defaultQuery();
        }
        else{
            Map<String, String> equalsparams = new HashMap<String, String>();
            parametersMap.remove("CJSJ");
            parametersMap.remove("JSSJ");
            list = GWMQRecordService.query(equalsparams, parametersMap, cjsj, jssj);
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setActionModel("custom_export_processTask");
        tableConfig.setSelectable(true);
//        tableConfig.setActionModel("custom_synergy_Menu");
        tableConfig.setId("SearchSynergyRecordBuilder_ID");

        tableConfig.setLabel("协同流程记录列表");
/*        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);*/

        //送审单编号
        ColumnConfig orderNumber = factory.newColumnConfig(GWMQRecord.ORDERNUMBER, true);
        orderNumber.setAutoSize(true);
        orderNumber.setLabel(SynergyConstants.SSDBH);
        orderNumber.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(orderNumber);

        //送审单名称
        ColumnConfig orderName = factory.newColumnConfig(GWMQRecord.ORDERNAME, true);
        orderName.setAutoSize(true);
        orderName.setLabel(SynergyConstants.SSDMC);
        tableConfig.addComponent(orderName);

        //流程名称
        ColumnConfig processName = factory.newColumnConfig(GWMQRecord.PROCESSNAME, true);
        processName.setAutoSize(true);
        processName.setLabel(SynergyConstants.LCMC);
        processName.setHidden(true);
        tableConfig.addComponent(processName);

        //送审人
        ColumnConfig orderOwner = factory.newColumnConfig(GWMQRecord.ORDEROWNER, true);
        orderOwner.setAutoSize(true);
        orderOwner.setLabel(SynergyConstants.SSR);
        tableConfig.addComponent(orderOwner);

        //所属产品
        ColumnConfig productName = factory.newColumnConfig(GWMQRecord.PRODUCTNAME, true);
        productName.setAutoSize(true);
        productName.setLabel(SynergyConstants.SSCP);
        tableConfig.addComponent(productName);

        //发起站点
        ColumnConfig sender = factory.newColumnConfig(GWMQRecord.SENDER, true);
        sender.setAutoSize(true);
        sender.setLabel(SynergyConstants.FQZD);
        tableConfig.addComponent(sender);

        //接收站点
        ColumnConfig receiver = factory.newColumnConfig(GWMQRecord.RECEIVER, true);
        receiver.setAutoSize(true);
        receiver.setLabel(SynergyConstants.JSZD);
        tableConfig.addComponent(receiver);

        //发送状态
        ColumnConfig sendState = factory.newColumnConfig(GWMQRecord.SENDSTATE, true);
        sendState.setAutoSize(true);
        sendState.setLabel(SynergyConstants.FSZT);
        tableConfig.addComponent(sendState);

        //类型
        ColumnConfig sendType = factory.newColumnConfig(GWMQRecord.SENDTYPE, true);
        sendType.setAutoSize(true);
        sendType.setLabel(SynergyConstants.LX);
        tableConfig.addComponent(sendType);

        //创建时间
        ColumnConfig createTimeStamp = factory.newColumnConfig(GWMQRecord.CREATETIMESTAMP, true);
        createTimeStamp.setAutoSize(true);
        createTimeStamp.setLabel(SynergyConstants.CJSJ);
        createTimeStamp.setHidden(true);
        tableConfig.addComponent(createTimeStamp);

        //发送时间
        ColumnConfig sendTime = factory.newColumnConfig(GWMQRecord.SENDTIME, true);
        sendTime.setAutoSize(true);
        sendTime.setLabel(SynergyConstants.FSSJ);
        tableConfig.addComponent(sendTime);

        //更新时间
        ColumnConfig updateTime = factory.newColumnConfig(GWMQRecord.UPDATETIME, true);
        updateTime.setAutoSize(true);
        updateTime.setLabel(SynergyConstants.GXSJ);
        tableConfig.addComponent(updateTime);

        //消息状态
        ColumnConfig msgState = factory.newColumnConfig(GWMQRecord.MSGSTATE, true);
        msgState.setAutoSize(true);
        msgState.setLabel(SynergyConstants.XXZT);
        msgState.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(msgState);

        //错误信息
        ColumnConfig errorMsg = factory.newColumnConfig(GWMQRecord.ERRORMSG, true);
        errorMsg.setAutoSize(true);
        errorMsg.setLabel(SynergyConstants.CWXX);
        tableConfig.addComponent(errorMsg);

        //全流程监控
       /* ColumnConfig qlcjk = factory.newColumnConfig("qlcjk", true);
        qlcjk.setAutoSize(true);
        qlcjk.setLabel(SynergyConstants.QLCJK);
        qlcjk.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(qlcjk);*/

/*        //重新发送
        ColumnConfig cxfs = factory.newColumnConfig("cxfs", true);
        cxfs.setAutoSize(true);
        cxfs.setLabel(SynergyConstants.CXFS);
        cxfs.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(cxfs);

        //重新接收
        ColumnConfig cxjs = factory.newColumnConfig("cxjs", true);
        cxjs.setAutoSize(true);
        cxjs.setLabel(SynergyConstants.CXJS);
        cxjs.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(cxjs);*/

        //重新反馈
        ColumnConfig cxfk = factory.newColumnConfig("cxfk", true);
        cxfk.setAutoSize(true);
        cxfk.setLabel(SynergyConstants.CXFK);
        cxfk.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(cxfk);

        //操作
        ColumnConfig cz = factory.newColumnConfig("cz", true);
        cz.setAutoSize(true);
        cz.setLabel(SynergyConstants.CZ);
        cz.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(cz);
        return tableConfig;
    }

    public static Map<String, String> getQueryParameters(ComponentConfig config, ComponentParams params) throws WTException{
        NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        HttpServletRequest request = commandBean.getRequest();

        Map<String, String> map = new HashMap<String, String>();
        String DJLX = null == request.getParameter("DJLX") ? "" : request.getParameter("DJLX");
        String DJZT = null == request.getParameter("DJZT") ? "" : request.getParameter("DJZT");
        String FQDW = null == request.getParameter("FQDW") ? "" : request.getParameter("FQDW");
        String JSDW = null == request.getParameter("JSDW") ? "" : request.getParameter("JSDW");
        String CPMC = null == request.getParameter("CPMC") ? "" : request.getParameter("CPMC");
        String FQR = null == request.getParameter("FQR") ? "" : request.getParameter("FQR");
        String DJBH = null == request.getParameter("DJBH") ? "" : request.getParameter("DJBH");
        String DJMC = null == request.getParameter("DJMC") ? "" : request.getParameter("DJMC");
        String LCMC = null == request.getParameter("LCMC") ? "" : request.getParameter("LCMC");
        String XXZT = null == request.getParameter("XXZT") ? "" : request.getParameter("XXZT");
        String CJSJ = null == request.getParameter("null___CJSJ_col_CJSJ___textbox") ? "" : request.getParameter("null___CJSJ_col_CJSJ___textbox");
        String JSSJ = null == request.getParameter("null___JSSJ_col_JSSJ___textbox") ? "" : request.getParameter("null___JSSJ_col_JSSJ___textbox");

        if(!DJBH.equals(""))
            map.put(GWMQRecord.ORDERNUMBER, DJBH);
        if(!DJMC.equals(""))
            map.put(GWMQRecord.ORDERNAME, DJMC);
        if(!LCMC.equals(""))
            map.put(GWMQRecord.PROCESSNAME, LCMC);
        if(!"".equals(FQR)) {
        	WTUser user = CSCPrincipal.getUserByName(FQR);
            map.put(GWMQRecord.ORDEROWNER, user.getFullName());

        }
        if(!CPMC.equals(""))
            map.put(GWMQRecord.PRODUCTNAME, CPMC);
        if(!FQDW.equals(""))
            map.put(GWMQRecord.SENDER, FQDW.replaceAll("厂", "").replaceAll("所", ""));
        if(!JSDW.equals(""))
            map.put(GWMQRecord.RECEIVER, JSDW.replaceAll("厂", "").replaceAll("所", ""));
        if(!DJLX.equals(""))
            map.put(GWMQRecord.SENDTYPE, DJLX);
        if(!DJZT.equals(""))
            map.put(GWMQRecord.SENDSTATE, DJZT);
        if(!XXZT.equals(""))
            map.put(GWMQRecord.MSGSTATE, XXZT);
       
        if(!CJSJ.equals(""))
            map.put("CJSJ", SynergyUtil.covertDate(CJSJ));
        if(!JSSJ.equals(""))
            map.put("JSSJ", SynergyUtil.covertDate(JSSJ));
        return map;
    }

    
}