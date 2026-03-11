package ext.casc.workflow.doc.mvc.builder;

import java.util.ArrayList;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.workflow.PrintHelper;


@ComponentBuilder("ext.casc.workflow.doc.mvc.builder.ShowReviewObjBuilder")
public class ShowReviewObjBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		WorkItem workItem = (WorkItem)commandBean.getPageOid().getRefObject();
//		WfActivity activity = (WfActivity) workItem.getSource().getObject();
//        WfProcess process = activity.getParentProcess();
//        ProcessData processData = process.getContext();
		Persistable per = workItem.getPrimaryBusinessObject().getObject();
		ArrayList<Persistable> objects = new ArrayList<Persistable>();
		if(per instanceof WTDocument){
			objects.add(per);
		}else if(per instanceof WTChangeOrder2){
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) per;
			objects.add(changeOrder2);
			WTObject targetObj = PrintHelper.getReleatedDocByECN(changeOrder2);
            if(targetObj != null){
            	if(targetObj instanceof WTDocument){
            		WTDocument document = (WTDocument)targetObj;
            		QueryResult allIterDocs = VersionControlHelper.service.allIterationsOf(document.getMaster());
            		if(allIterDocs.hasMoreElements()) {
            			document = (WTDocument)allIterDocs.nextElement();
            			objects.add(document);
            		}
            	}else{
            		 objects.add(targetObj);
            	}
                return objects;
            }
			QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
            while (qResult.hasMoreElements()) {
                Object object = qResult.nextElement();
                if(object instanceof  Persistable){
                	objects.add((Persistable)object);
                }
            }
		}
		return objects;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		WorkItem workItem = (WorkItem)commandBean.getPageOid().getRefObject();
		WfActivity activity = (WfActivity) workItem.getSource().getObject();

        Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");

        String activityName = activity.getName();
		ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        if(pbo instanceof WTChangeOrder2){
        	 table.setLabel("更改后数据");

         }else{
        	 table.setLabel("签审对象");
  		  }

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_task_objTable_actions");
        table.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setWidth(100);
//        numberConfig.setAutoSize(true);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
//        nameConfig.setAutoSize(true);
        nameConfig.setWidth(150);
        table.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
//        versionConfig.setAutoSize(true);
        versionConfig.setWidth(50);
        table.addComponent(versionConfig);

        ColumnConfig stateConfig = factory.newColumnConfig("state", false);
//      versionConfig.setAutoSize(true);
        stateConfig.setWidth(50);
         table.addComponent(stateConfig);


         /*if(!"编制".equals(activityName)&&!"修改".equals(activityName)){
        	  ColumnConfig huiqianresult = factory.newColumnConfig("sign_result",false);
              huiqianresult.setLabel("会签结论");
              huiqianresult.setDataUtilityId("SignatureTreeDataUtility1");
              huiqianresult.setWidth(50);
              table.addComponent(huiqianresult);

              ColumnConfig huiqianadvise = factory.newColumnConfig("sign_advise", false);
              huiqianadvise.setLabel("会签意见");
              huiqianadvise.setDataUtilityId("SignatureTreeDataUtility1");
              huiqianadvise.setWidth(200);
              table.addComponent(huiqianadvise);
         }*/

        if("外部会签".equals(activityName)){
        	ColumnConfig singInfo = factory.newColumnConfig("addSignInfo", false);
            singInfo.setLabel("填写会签信息");
            singInfo.setDataUtilityId("MpmplanReleaseDataUtility");
            singInfo.setWidth(200);
//            singInfo.setAutoSize(true);
            table.addComponent(singInfo);
        }else{
        	ColumnConfig singInfo1 = factory.newColumnConfig("showSignInfo", false);
            singInfo1.setLabel("查看会签信息");
            singInfo1.setDataUtilityId("MpmplanReleaseDataUtility");
//            singInfo1.setAutoSize(true);
            singInfo1.setWidth(200);
            table.addComponent(singInfo1);
        }


		return table;
	}


}
