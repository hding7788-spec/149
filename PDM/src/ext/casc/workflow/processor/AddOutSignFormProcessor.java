package ext.casc.workflow.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class AddOutSignFormProcessor extends DefaultObjectFormProcessor{

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public FormResult doOperation(NmCommandBean commandBean,List<ObjectBean> list) throws WTException {
		FormResult formResult =  super.doOperation(commandBean, list);
		try {
        	NmOid nmOid = commandBean.getActionOid();
    		Object obj = nmOid.getRefObject();
            if(obj instanceof WorkItem){
            	WorkItem item = (WorkItem)obj;
            	WfActivity activity = (WfActivity) item.getSource().getObject();
                ProcessData processData = activity.getContext();
                ArrayList<NmOid> outSignInfo = ((ArrayList<NmOid>)processData.getValue("outSignInfo"));
    			if(outSignInfo == null){
    				outSignInfo = new ArrayList<NmOid>();
    			}
    			HashMap<String, List<NmOid>> addMap = commandBean.getAddedItems();
    			HashMap<String, List<NmOid>> removeMap = commandBean.getRemovedItems();
    			for(int i = 0; i < addMap.get("outSignTable").size(); i++){
    				NmOid oid = addMap.get("outSignTable").get(i);
    				HashMap<String,Object> map = new HashMap<String,Object>();
    				map.put("signName", commandBean.getText().get("signName_" + oid.toString()));
    				map.put("signCompany", commandBean.getText().get("signCompany_" + oid.toString()));
    				map.put("signDate", commandBean.getText().get("signDate_" + oid.toString()));
    				map.put("signRemark", commandBean.getText().get("signRemark_" + oid.toString()));
    				oid.setAdditionalInfo(map);
    				outSignInfo.add(oid);
    			}
    			for(int i = 0; i < removeMap.get("outSignTable").size(); i++){
    				NmOid oid = removeMap.get("outSignTable").get(i);
    				outSignInfo.remove(oid);
    			}
    			processData.setValue("outSignInfo", outSignInfo);
    			PersistenceHelper.manager.save(activity);
    			PersistenceHelper.manager.refresh(activity);
            }
        }catch (WTException e) {
            e.printStackTrace();
        }
		return formResult;
	}

}
