package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

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

import ext.casc.constants.Constants;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewUtil;
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.SetPreviewDataSignatureBuilder")
public class SetPreviewDataSignatureBuilder extends AbstractComponentBuilder {
	private String activityName="";
    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        WorkItem wi = (WorkItem) commandBean.getPageOid().getRefObject();
        Persistable pbo = (Persistable) wi.getPrimaryBusinessObject().getObject();
        List<Object> list = new ArrayList<Object>();
        if(pbo instanceof Preview) {
        	Preview pre = (Preview)pbo;
        	list.addAll(PreviewUtil.getAllMembers(pre));
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        NmCommandBean commandBean = ((JcaComponentParams) params)
                .getHelperBean().getNmCommandBean();
		Map map = commandBean.getRequestData().getParameterMap();
		String oid = (String)map.get("oid");//读取任务页面的流程oid
		activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(oid);
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("预审列表");
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setConfigurable(false);
        tableConfig.setId("ext.casc.workflow.tree.mvc.builder.SetPreviewDataSignatureBuilder");
        tableConfig.setSelectable(true);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(false);
        numberConfig.setWidth(150);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setWidth(150);
        tableConfig.addComponent(nameConfig);

        if(activityName.equals(Constants.ACTIVITYNAME_SHEJISHUJUGONGYIYUSHEN)){
	        ColumnConfig huiqianresult = factory.newColumnConfig("sign_result", false);
	        huiqianresult.setLabel("会签结论");
	        huiqianresult.setDataUtilityId("SignatureTreeDataUtility1");
	        huiqianresult.setWidth(100);
	        tableConfig.addComponent(huiqianresult);

	        ColumnConfig huiqianadvise = factory.newColumnConfig("sign_advise", false);
	        huiqianadvise.setLabel("会签意见");
	        huiqianadvise.setDataUtilityId("SignatureTreeDataUtility1");
	        huiqianadvise.setWidth(200);
	        tableConfig.addComponent(huiqianadvise);
        }else  if(activityName.equals(Constants.ACTIVITYNAME_GONGYIYUSHENLUOSHIYIJIANFANKUI)){
	        ColumnConfig advice_result = factory.newColumnConfig("advice_result", false);
	        advice_result.setLabel("意见汇总");
	        advice_result.setDataUtilityId("PreviewDataUtility");
	        advice_result.setWidth(100);
	        tableConfig.addComponent(advice_result);

	        ColumnConfig implementadvise = factory.newColumnConfig("implementadvise", false);
	        implementadvise.setLabel("落实意见");
	        implementadvise.setDataUtilityId("PreviewDataUtility");
	        implementadvise.setWidth(200);
	        tableConfig.addComponent(implementadvise);
        }
        ColumnConfig versionConfig = factory.newColumnConfig("objVer", false);
        versionConfig.setLabel("版本");
        versionConfig.setAutoSize(true);
        versionConfig.setWidth(50);
        tableConfig.addComponent(versionConfig);

        return tableConfig;
    }

}
