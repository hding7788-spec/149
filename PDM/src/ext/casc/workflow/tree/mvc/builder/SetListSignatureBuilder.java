package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.change2.WTChangeOrder2;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.core.ui.resources.ComponentType;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;
import ext.casc.workflow.signtrue.zp.ISignatureParser;
import ext.casc.workflow.signtrue.zp.SignatureGYYXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureService;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.SetListSignatureBuilder")
public class SetListSignatureBuilder  extends AbstractComponentBuilder {
	private String activityName="";
	private static final Logger log;
    static {
        try {
            log = LogR.getLogger(SetListSignatureBuilder.class.getName());
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
  }
	@Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        log.info("entry SetListSignatureBuilder.buildComponentData begin="+new Date());
		NmCommandBean commandBean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        List resultList = new ArrayList();


      //切换系统管理员
        WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		WTUser admin = (WTUser)SessionHelper.manager.setAdministrator();
        Map map = commandBean.getRequestData().getParameterMap();
        String oid = (String)map.get("oid");//读取任务页面的流程oid
        if("".equals(activityName)){
        	 activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(oid);
        }

        Object topObject = commandBean.getRequest().getSession().getAttribute("topObject");
        /*if (topObject != null&&(topObject instanceof WTPart)) {
            return new SignatureTreeHandler2((WTPart)topObject);
        }else {
            return new SignatureTreeHandler2(activityName);
        }*/
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
        ISignatureParser parser = null;
        if(parser==null){
			if("指派工艺员".equals(activityName)||Constants.ACTIVITYNAME_ZPGYHQ.equals(activityName)){
				parser = new SignatureGYZZXMLParser((ContentHolder)pbo);
			}else if("工艺会签".equals(activityName)||"物资会签".equals(activityName)){
				parser = new SignatureGYYXMLParser((ContentHolder)pbo);
			}
		}
        if(pbo instanceof ProcessEnvelope){
			ProcessEnvelope pe = (ProcessEnvelope)pbo;			//获取pbo对象
			List list = SignatureService.getGYHQDealMembers(pe,currentuser,wi,parser);
			resultList.addAll(list);
		}else if (pbo instanceof WTChangeOrder2){
			WTChangeOrder2 ecn = (WTChangeOrder2)pbo;
			List list = ChangeHelper.getChangeResultItem(ecn);
			list = SignatureService.getGYHQDealMembers(list, currentuser, wi,parser);
			resultList.addAll(list);
            if(parser==null||parser.hasPrivilege((Persistable)ecn, currentuser, wi)){
            	resultList.add(ecn);
            }

		}else if (pbo instanceof WTDocument){
			resultList.add(pbo);
		}else if (pbo instanceof MPMProcessPlan) {
			resultList.add(pbo);
        } else if (pbo instanceof ChangeRequest) {
            ChangeRequest request = (ChangeRequest) pbo; // 获取pbo对象
            if(parser==null||parser.hasPrivilege((Persistable)request, currentuser, wi)){
            	resultList.add(request);
            }
        } else if (pbo instanceof ChangePackaged) {
			ChangePackaged change = (ChangePackaged) pbo; // 获取pbo对象
			if(parser==null||parser.hasPrivilege((Persistable)change, currentuser, wi)){
				resultList.add(change);
			}
			QueryResult qr = PersistenceHelper.manager.navigate(change,
					ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
					ChangePackagedResultLink.class, true);
			while (qr.hasMoreElements()) {
				WTObject wtobject = (WTObject) qr.nextElement();
				if (!(wtobject instanceof WTPart)) {
					if(parser==null||parser.hasPrivilege((Persistable)wtobject, currentuser, wi)){
						resultList.add(wtobject);
					}
				}
			}
		}
        log.info("entry  begin2="+new Date());
		SignatureService.filtrate(resultList);
		log.info("entry  end="+new Date());
		SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
        return resultList;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        NmCommandBean commandBean = ((JcaComponentParams) params)
                .getHelperBean().getNmCommandBean();
		Map map = commandBean.getRequestData().getParameterMap();
		String oid = (String)map.get("oid");//读取任务页面的流程oid
		ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        WfProcess process = activity.getParentProcess();
        String processName = process.getName();
		activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(oid);
		JcaTableConfig tableConfig =(JcaTableConfig) factory.newTableConfig();
        tableConfig.setLabel("签审列表");
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
        tableConfig.setConfigurable(false);
        tableConfig.setId("ext.casc.workflow.tree.mvc.builder.SetListSignatureBuilder");
        tableConfig.setSelectable(true);
        tableConfig.setActionModel("workItem_table_action");
        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_folderbrowser_toolbar_open_submenu");
        tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setAutoSize(true);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setAutoSize(true);
        tableConfig.addComponent(nameConfig);


        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        tableConfig.addComponent(versionConfig);

        ColumnConfig huiqianresult = factory.newColumnConfig("sign_result",false);
        huiqianresult.setLabel("会签结论");
        huiqianresult.setDataUtilityId("SignatureTreeDataUtility1");
        huiqianresult.setWidth(50);
        tableConfig.addComponent(huiqianresult);

        ColumnConfig huiqianadvise = factory.newColumnConfig("sign_advise", false);
        huiqianadvise.setLabel("会签意见");
        huiqianadvise.setDataUtilityId("SignatureTreeDataUtility1");
        huiqianadvise.setWidth(200);
        tableConfig.addComponent(huiqianadvise);
        ColumnConfig checkReport = factory.newColumnConfig("check_report", false);
        checkReport.setLabel("工艺检查报告");
        checkReport.setDataUtilityId("SignatureTreeDataUtility1");
        checkReport.setWidth(200);
        tableConfig.addComponent(checkReport);
        if (activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
	        ColumnConfig columnConfig = factory.newColumnConfig("zhuzhichejian",false);
	        columnConfig.setLabel("*主制车间");
	        columnConfig.setId("zhuzhichejian");
	        //columnConfig.setInputFieldType("text");
	        //columnConfig.setDefaultFreeze(false);
	        columnConfig.setDataUtilityId("SignatureEpmsDataUtility");
	        columnConfig.setWidth(50);
	        tableConfig.addComponent(columnConfig);

	        ColumnConfig columnConfig2 = factory.newColumnConfig("fuzhuchejian",false);
	        columnConfig2.setLabel("辅制车间");
	        columnConfig2.setId("fuzhichejian");
	        columnConfig2.setDataUtilityId("SignatureEpmsDataUtility");
	        //columnConfig2.setInputFieldType("ComboBox");
	        columnConfig2.setWidth(200);
	        tableConfig.addComponent(columnConfig2);
        }

        if (processName.indexOf(Constants.WF_PART_APPROVAL) > -1 || processName.indexOf(Constants.WF_SJ_ECN) > -1
                || processName.indexOf(Constants.WF_149ECNPAKAGE) > -1 || processName.indexOf(Constants.WF_149APPROVAL_HUIQIAN) > -1
                || processName.indexOf(Constants.WF_149APPROVAL_ZHENGSHI_HUIQIAN) > -1) {
            ColumnConfig cmat = factory.newColumnConfig("CMAT", false);
            cmat.setAutoSize(true);
            cmat.setLabel("材料");
            tableConfig.addComponent(cmat);

            ColumnConfig cmatup = factory.newColumnConfig("CMAT_UP", false);
            cmatup.setAutoSize(true);
            cmatup.setLabel("材料上标");
            tableConfig.addComponent(cmatup);

            ColumnConfig cmatdown = factory.newColumnConfig("CMAT_DOWN", false);
            cmatdown.setAutoSize(true);
            cmatdown.setLabel("材料下标");
            tableConfig.addComponent(cmatdown);

            ColumnConfig csize = factory.newColumnConfig("CSIZE", false);
            csize.setAutoSize(true);
            csize.setLabel("规格");
            tableConfig.addComponent(csize);

            ColumnConfig count = factory.newColumnConfig("count", false);
            count.setAutoSize(true);
            count.setLabel("数量");
            count.setDataUtilityId("SignatureResultDataUtility");
            tableConfig.addComponent(count);

            ColumnConfig designer = factory.newColumnConfig("DESIGNER", false);
            designer.setAutoSize(true);
            designer.setLabel("设计者");
            tableConfig.addComponent(designer);

            ColumnConfig ptc_material_name = factory.newColumnConfig("PTC_MATERIAL_NAME", false);
            ptc_material_name.setAutoSize(true);
            ptc_material_name.setLabel("材料名称");
            tableConfig.addComponent(ptc_material_name);
        }

        return tableConfig;
    }


}
