package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ptc.windchill.mpml.MPMDocumentDescribeLink;
import org.apache.log4j.Logger;

import wt.change2.ChangeHelper2;
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
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
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
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.workflow.PrintHelper;
import ext.casc.workflow.WorkflowHelper;
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;
import ext.casc.workflow.signtrue.zp.ISignatureParser;
import ext.casc.workflow.signtrue.zp.SignatureGYYXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureService;
/**
 * 工时定额信息显示
 * @author liangbo
 *
 */
@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.ShowListGongShiDingEBuilder")
public class ShowListGongShiDingEBuilder  extends AbstractComponentBuilder {
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
		NmCommandBean commandBean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        NmOid nmOid = commandBean.getPageOid();
        Object obj = nmOid.getRefObject();
        if(obj instanceof WTDocument){
        	WTDocument doc = (WTDocument)obj;

            QueryResult queryResult = PersistenceHelper.manager.navigate(doc,
                    MPMDocumentDescribeLink.ROLE_AOBJECT_ROLE, MPMDocumentDescribeLink.class);
            queryResult = new LatestConfigSpec().process(queryResult);
            if (queryResult.hasMoreElements()) {
                MPMProcessPlan plan = (MPMProcessPlan) queryResult.nextElement();
                return  WorkflowHelper.getMPMOperationsByMpmPr(plan);
            }

        }else if(obj instanceof WTChangeOrder2){
        	WTChangeOrder2 ecn = (WTChangeOrder2)obj;
        	QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(ecn);
        	WTDocument document = null;
        	while (qResult.hasMoreElements()) {
                Object object = qResult.nextElement();
                if(object instanceof  WTDocument){
                    document = (WTDocument)object;
                	String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
			         if (typeName.contains("PROCESSPLAN")) {
			            break;
			         }
			      }
            }
        	if(document==null){
        		WTObject targetObj = PrintHelper.getReleatedDocByECN(ecn);
                if(targetObj != null&&targetObj instanceof WTDocument){
                	document = (WTDocument)targetObj;
                }
        	}
        	if(document == null){
        		return null;
        	}
       	    QuerySpec qSpec = new QuerySpec(MPMProcessPlan.class);
            int[] index = { 0 };
            SearchCondition sCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER,SearchCondition.EQUAL, document.getNumber());
            qSpec.appendWhere(sCondition, index);
            qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qResult = lcs.process(qResult);
            if (qResult.hasMoreElements()) {
           	  MPMProcessPlan plan = (MPMProcessPlan) qResult.nextElement();
           	  return  WorkflowHelper.getMPMOperationsByMpmPr(plan);
            }

       }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        NmCommandBean commandBean = ((JcaComponentParams) params)
                .getHelperBean().getNmCommandBean();
		Map map = commandBean.getRequestData().getParameterMap();
		JcaTableConfig tableConfig =(JcaTableConfig) factory.newTableConfig();
        tableConfig.setLabel("工时定额列表");
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
        tableConfig.setConfigurable(false);
        tableConfig.setId("ext.casc.workflow.tree.mvc.builder.ShowListGongShiDingEBuilder");
        tableConfig.setSelectable(true);
//        tableConfig.setActionModel("workItem_mpmoperation_action");

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", true);
        numberConfig.setInfoPageLink(true);
        numberConfig.setAutoSize(true);
        tableConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", true);
        nameConfig.setDataUtilityId("ext.casc.workflow.tree.MPMOperationDataUtility");
        nameConfig.setAutoSize(true);
        tableConfig.addComponent(nameConfig);


        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        tableConfig.addComponent(versionConfig);

        ColumnConfig huiqianresult = factory.newColumnConfig("ZJGS",true);
        huiqianresult.setLabel("准结");
       // huiqianresult.setDataUtilityId("ext.casc.workflow.tree.MPMOperationDataUtility");
        huiqianresult.setWidth(50);
        tableConfig.addComponent(huiqianresult);

        ColumnConfig huiqianadvise = factory.newColumnConfig("DJGS", true);
        huiqianadvise.setLabel("单件");
        //huiqianadvise.setDataUtilityId("ext.casc.workflow.tree.MPMOperationDataUtility");
        huiqianadvise.setWidth(200);
        tableConfig.addComponent(huiqianadvise);

        return tableConfig;
    }


}
