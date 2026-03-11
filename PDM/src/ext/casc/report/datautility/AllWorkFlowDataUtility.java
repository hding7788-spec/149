package ext.casc.report.datautility;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.netmarkets.util.misc.NmAction;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.preview.Preview;
import ext.casc.report.AllWorkFlow;
import ext.casc.report.BOMReport;
import ext.casc.report.mvc.builders.LCQSZTBWfprocessEntity;
import ext.casc.util.NmTableGUIComponent;
import ext.casc.workflow.TaskConfigrationHelper;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.httpgw.URLFactory;
import wt.part.WTPart;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;

import java.util.ArrayList;
import java.util.Locale;

public class AllWorkFlowDataUtility extends AbstractDataUtility {
	public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
		if("infoPageAction".equals(columnName)){
			URLFactory factory = new URLFactory();
			String base = factory.getHREF("app/#ptc1/tcomp/infoPage?oid=");
			AllWorkFlow awf = (AllWorkFlow)obj;
			NmAction action = new NmAction();
			action.setIcon("details.gif");
			action.setPageURL(base + awf.getProcessOid());
			action.setToolTip("查看计划");
			return action;
		}else if("partName".equals(columnName)){
			GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
			URLFactory factory = new URLFactory();
			String base = factory.getHREF("app/#ptc1/tcomp/infoPage?oid=");
			BOMReport awf = (BOMReport)obj;
			String value="<a href='javascript:void(0)' onclick='window.open(\""+base + awf.getPartOid()+"\",\"_blank\")'>"+awf.getPartName()+"</a>";
			 NmTableGUIComponent gui = new NmTableGUIComponent(value);
             guicomponentarrayMain.addGUIComponent(gui);
//			NmAction action = new NmAction();
//			action.setIcon("details.gif");
//			action.setPageURL(base + awf.getPartOid());
//			action.setToolTip("查看详细信息");
			return guicomponentarrayMain;
		}else if("pboState".equals(columnName)){
			String state = "";
			try {
				AllWorkFlow awf = (AllWorkFlow)obj;
				String processOid = awf.getProcessOid();
				if(processOid!=null && !"".equals(processOid)){
					ReferenceFactory rf = new ReferenceFactory();
					Persistable object = rf.getReference(processOid).getObject();
					if(object instanceof WfProcess){
						WfProcess process = (WfProcess) object;
						Object pbo = TaskConfigrationHelper.getPBOByWfProcess(process);
						if(pbo!=null){
							if(pbo instanceof WTDocument){
								WTDocument doc = (WTDocument) pbo;
								state = doc.getState().getState().getDisplay(Locale.CHINA);
							}else if(pbo instanceof WTChangeOrder2){
								WTChangeOrder2 wo2 = (WTChangeOrder2) pbo;
								state = wo2.getState().getState().getDisplay(Locale.CHINA);
							}else if(pbo instanceof WTPart){
								WTPart part = (WTPart) pbo;
								state = part.getState().getState().getDisplay(Locale.CHINA);
							}else if(pbo instanceof WTChangeActivity2){
								WTChangeActivity2 ca2 = (WTChangeActivity2) pbo;
								state = ca2.getState().getState().getDisplay(Locale.CHINA);
							}else if(pbo instanceof WTChangeRequest2){
								WTChangeRequest2 cr2 = (WTChangeRequest2) pbo;
								state = cr2.getState().getState().getDisplay(Locale.CHINA);
							}else if(pbo instanceof ProcessEnvelope){
								ProcessEnvelope pe = (ProcessEnvelope) pbo;
								ArrayList list = EnvelopeHelper.service.getAllMembers(pe);
								for (int i = 0; i < list.size(); i++) {
									RevisionControlled rc = (RevisionControlled) list.get(i);
									if(rc instanceof WTDocument){
										WTDocument doc = (WTDocument) rc;
										state = doc.getState().getState().getDisplay(Locale.CHINA);
										break;
									}
								}
							}else if(pbo instanceof ChangePackaged){
								ChangePackaged cp = (ChangePackaged) pbo;
								state = cp.getState().getState().getDisplay(Locale.CHINA);
							}else if(pbo instanceof MPMProcessPlan){
								MPMProcessPlan plan = (MPMProcessPlan) pbo;
								state = plan.getState().getState().getDisplay(Locale.CHINA);
							}else if(pbo instanceof ChangeRequest){
								ChangeRequest cr = (ChangeRequest) pbo;
								state = cr.getState().getState().getDisplay(Locale.CHINA);
							}else if(pbo instanceof Preview){
								Preview preview = (Preview) pbo;
								state = preview.getState().getState().getDisplay(Locale.CHINA);
							}else{
								state = "";
							}
						}
					}
				}
			}catch (Exception e){
				e.printStackTrace();
				return state;
			}
			return state;
		}else if("wenjianbianhao".equals(columnName)){
			GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
			URLFactory factory = new URLFactory();
			String base = factory.getHREF("app/#ptc1/tcomp/infoPage?oid=");
			LCQSZTBWfprocessEntity entity = (LCQSZTBWfprocessEntity)obj;
			String value="<a href='javascript:void(0)' onclick='window.open(\""+base + entity.getWenjianoid()+"\",\"_blank\")'>"+entity.getWenjianbianhao()+"</a>";
			NmTableGUIComponent gui = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(gui);
			return guicomponentarrayMain;
		}
		return null;
	}
}
