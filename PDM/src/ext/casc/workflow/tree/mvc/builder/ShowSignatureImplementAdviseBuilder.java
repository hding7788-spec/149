package ext.casc.workflow.tree.mvc.builder;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.ixb.IXBConstants;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewUtil;
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;
import ext.casc.workflow.signtrue.zp.SignatureService;
import org.apache.log4j.Logger;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ShowSignatureImplementAdviseBuilder extends AbstractComponentBuilder {
	 private static final Logger log;
	    static {
	        try {
	            log = LogR.getLogger(ShowSignatureImplementAdviseBuilder.class.getName());
	        } catch (Exception e) {
	            throw new ExceptionInInitializerError(e);
	        }
	  }
    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        /*NmCommandBean commandBean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        Map map = commandBean.getRequestData().getParameterMap();
        String oid = (String)map.get("oid");//读取任务页面的流程oid
        String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(oid);
        Object topObject = commandBean.getRequest().getSession().getAttribute("topObject");
        if (topObject != null&&(topObject instanceof WTPart)) {
            return new SignatureResultTreeHandler2((WTPart)topObject);
        }else {
            return new SignatureResultTreeHandler2(activityName);
        }*/
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
		        } else if(pbo instanceof Preview) {
                    Preview preview = (Preview) pbo;
                    ArrayList<WTObject> list = PreviewUtil.getAllMembers(preview);
                    resultList.addAll(list);
                }

		        log.info("entry begin2="+new Date());

                //20250103 start
                List tempList = new ArrayList();
                tempList.addAll(resultList);
                try {
                    SignatureService.filtrate(resultList);
                } catch(IllegalArgumentException e){
                    System.out.println("====>>>> jdk bug: " + e);
                    e.printStackTrace();
                    resultList = new ArrayList();
                    resultList.addAll(tempList);
                }
                //end

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
        // String isShowSignResult = (String) TaskConfigrationHelper.getActivityVariableByVarName(signOid,
        // "isShowSignResult");
        // if (isShowSignResult == null) {
        // isShowSignResult = "";
        // }
        boolean isPreview = false;
        if(processName.indexOf(IXBConstants.PREVIEWWORKFLOWNAME) > -1){
            isPreview = true;
        }

        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("签审记录");
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setConfigurable(false);
        tableConfig.setId("show_tree_signature");
        //treeConfig.setExpansionLevel("full");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_folderbrowser_toolbar_open_submenu");
        tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        if(isPreview) {
            numberConfig.setInfoPageLink(false);
        }else {
            numberConfig.setInfoPageLink(true);
        }
        numberConfig.setWidth(150);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(180);
        tableConfig.addComponent(nameConfig);

        if(!isPreview){
            ColumnConfig versionConfig = factory.newColumnConfig("version", false);
            versionConfig.setWidth(200);
            tableConfig.addComponent(versionConfig);
        }

        // 工艺会签
        ColumnConfig gongyihuiqian = factory.newColumnConfig("gongyiyuan_sign_result", false);
        if(isPreview){
            gongyihuiqian.setLabel("工艺预审");
            gongyihuiqian.setWidth(800);
        }else {
            gongyihuiqian.setLabel("工艺会签");
            gongyihuiqian.setWidth(200);
        }
        gongyihuiqian.setDataUtilityId("SignatureResultDataUtility");
        tableConfig.addComponent(gongyihuiqian);

        if(!isPreview){
            ColumnConfig wuzihuiqian = factory.newColumnConfig("wuzi_sign_result", false);
            wuzihuiqian.setLabel("物资会签");
            wuzihuiqian.setDataUtilityId("SignatureResultDataUtility");
            wuzihuiqian.setWidth(200);
            tableConfig.addComponent(wuzihuiqian);

            ColumnConfig keshihuapizhu = factory.newColumnConfig("keshihuapizhu_result", false);
            keshihuapizhu.setLabel("可视化批注");
            keshihuapizhu.setDataUtilityId("SignatureResultDataUtility");
            keshihuapizhu.setWidth(200);
            tableConfig.addComponent(keshihuapizhu);
            // 工艺会签
            ColumnConfig allhuiqian = factory.newColumnConfig("allhuiqian_sign_result", false);
            allhuiqian.setLabel("所有会签记录");
            allhuiqian.setDataUtilityId("SignatureResultDataUtility");
            allhuiqian.setWidth(100);
            tableConfig.addComponent(allhuiqian);
        }

        if (processName.indexOf(Constants.WF_PART_APPROVAL) > -1 || processName.indexOf(Constants.WF_SJ_ECN) > -1
                || processName.indexOf(Constants.WF_149ECNPAKAGE) > -1 || processName.indexOf(Constants.WF_149APPROVAL_HUIQIAN) > -1
                || processName.indexOf(Constants.WF_149APPROVAL_ZHENGSHI_HUIQIAN) > -1|| processName.indexOf(Constants.WF_149APPROVAL_JISHUHUIQIAN) > -1 || processName.indexOf(Constants.WF_149ECNPAKAGE_JISHUHUIQIAN) > -1) {
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
