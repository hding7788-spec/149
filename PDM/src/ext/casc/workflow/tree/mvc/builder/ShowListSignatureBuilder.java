package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.change2.WTChangeOrder2;
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
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.components.TreeConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.workflow.PrintHelper;
import ext.casc.workflow.TaskConfigrationHelper;
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;
import ext.casc.workflow.signtrue.zp.SignatureService;
import ext.casc.workflow.tree.SignatureResultTreeHandler2;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.ShowListSignatureBuilder")
public class ShowListSignatureBuilder extends AbstractComponentBuilder {
	 private static final Logger log;
	    static {
	        try {
	            log = LogR.getLogger(ShowListSignatureBuilder.class.getName());
	        } catch (Exception e) {
	            throw new ExceptionInInitializerError(e);
	        }
	  }
	@Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		log.info("entry begin="+new Date());
		NmCommandBean commandBean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        Map map = commandBean.getRequestData().getParameterMap();
        List resultList = new ArrayList();
        //切换系统管理员
        WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		WTUser admin = (WTUser)SessionHelper.manager.setAdministrator();
        Object reloadTable = arg1.getParameter("reloadTable");
        if(reloadTable!=null&&"1".equals(reloadTable)){
	        String oid = (String) map.get("oid");// 读取任务页面的流程oid
	        String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(oid);
	        Object topObject = commandBean.getRequest().getSession().getAttribute("topObject");
	        /*if (topObject != null && (topObject instanceof WTPart)) {
	            return new SignatureResultTreeHandler2((WTPart) topObject);
	        } else {
	            return new SignatureResultTreeHandler2(activityName);
	        }*/
	        ReferenceFactory rf = new ReferenceFactory();
	        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
	        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
	        Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
	        if (pbo instanceof ProcessEnvelope) {
	            ProcessEnvelope pe = (ProcessEnvelope) pbo; // 获取pbo对象
	            List list = ProcessEnvelopeUtil.getAllMembersNoPart(wi, pe);
	            resultList.addAll(list);
	        } else if (pbo instanceof WTChangeOrder2) {
	            WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
	            List list = ChangeHelper.getChangeResultItem(ecn);
	            WTObject targetObj = PrintHelper.getReleatedDocByECN(ecn);
	            if(targetObj != null){
	            	resultList.add(targetObj);
	            }
	            resultList.addAll(list);
	            resultList.add(ecn);
	        } else if (pbo instanceof MPMProcessPlan) {
	        	resultList.add(pbo);
	        } else if (pbo instanceof WTDocument) {
	        	resultList.add(pbo);
	        } else if (pbo instanceof ChangePackaged) {
				ChangePackaged change = (ChangePackaged) pbo; // 获取pbo对象
				resultList.add(change);
				QueryResult qr = PersistenceHelper.manager.navigate(change,
						ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
						ChangePackagedResultLink.class, true);
				while (qr.hasMoreElements()) {
					WTObject wtobject = (WTObject) qr.nextElement();
					if (!(wtobject instanceof WTPart)) {
						resultList.add(wtobject);
					}
				}
			} else if (pbo instanceof ChangeRequest) {
			    ChangeRequest request = (ChangeRequest) pbo; // 获取pbo对象
			    resultList.add(request);
	        }

	        log.info("entry  begin2="+new Date());
			SignatureService.filtrate(resultList);
			log.info("entry  end="+new Date());
        }
        SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
        return resultList;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        NmCommandBean commandbean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        String signOid = commandbean.getRequest().getParameter("oid");
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(signOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        WfProcess process = activity.getParentProcess();
        String processName = process.getName();
        String isShowSignResult = (String) TaskConfigrationHelper.getActivityVariableByVarName(signOid,
                "isShowSignResult");
        if (isShowSignResult == null) {
            isShowSignResult = "";
        }
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("签审列表");
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setConfigurable(false);
        tableConfig.setId("show_list_signature");

        tableConfig.setActionModel("showListSignatureToolBar");
        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_folderbrowser_toolbar_open_submenu");
        tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setWidth(150);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(150);
        tableConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        tableConfig.addComponent(versionConfig);

        if (isShowSignResult.contains("内部会签")) {
            ColumnConfig neibuhuiqian = factory.newColumnConfig("neibu_sign_result", false);
            neibuhuiqian.setLabel("内部会签");
            neibuhuiqian.setDataUtilityId("SignatureResultDataUtility");
            neibuhuiqian.setWidth(200);
            tableConfig.addComponent(neibuhuiqian);
        }
        if (isShowSignResult.contains("外部会签")) {
            ColumnConfig waibuhuiqian = factory.newColumnConfig("waibu_sign_result", false);
            waibuhuiqian.setLabel("外部会签");
            waibuhuiqian.setDataUtilityId("SignatureResultDataUtility");
            waibuhuiqian.setWidth(200);
            tableConfig.addComponent(waibuhuiqian);
        }
        if (isShowSignResult.contains("工艺会签")) {
            ColumnConfig gongyihuiqian = factory.newColumnConfig("gongyiyuan_sign_result", false);
            gongyihuiqian.setLabel("工艺会签");
            gongyihuiqian.setDataUtilityId("SignatureResultDataUtility");
            gongyihuiqian.setWidth(200);
            tableConfig.addComponent(gongyihuiqian);
        }
        //if (isShowSignResult.contains("物资会签")) {
            ColumnConfig wuzihuiqian = factory.newColumnConfig("wuzi_sign_result", false);
            wuzihuiqian.setLabel("物资会签");
            wuzihuiqian.setDataUtilityId("SignatureResultDataUtility");
            wuzihuiqian.setWidth(200);
            tableConfig.addComponent(wuzihuiqian);
       // }

       // if (isShowSignResult.contains("可视化批注")) {
            ColumnConfig keshihuapizhu = factory.newColumnConfig("keshihuapizhu_result", false);
            keshihuapizhu.setLabel("可视化批注");
            keshihuapizhu.setDataUtilityId("SignatureResultDataUtility");
            keshihuapizhu.setWidth(200);
            tableConfig.addComponent(keshihuapizhu);
       // }
        ColumnConfig biaoshen = factory.newColumnConfig("biaoshen_sign_result", false);
        biaoshen.setLabel("标审");
        biaoshen.setDataUtilityId("SignatureResultDataUtility");
        biaoshen.setWidth(200);
        tableConfig.addComponent(biaoshen);

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
