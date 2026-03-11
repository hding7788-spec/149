package ext.ases.techMaterial.process;

import java.util.ArrayList;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;

import ext.ases.techMaterial.TechnicsMaterialEntries;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;

public class TechMaterialProcessor {
	
	
	/**
	 * 删除
	 * 
	 * @param commandBean
	 * @return
	 */
	public static FormResult deleteTechMaterialEntries(NmCommandBean commandBean) {
		FormResult form = new FormResult();
		FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
		FeedbackMessage message = new FeedbackMessage();
		try {
			String oid = commandBean.getActionOid().getOid().toString();
			NmOid nmOid = commandBean.getActionOid();
			Object obj = nmOid.getRefObject();
			String descption="";
			if(obj instanceof TechnicsMaterialEntries){
				TechnicsMaterialEntries tme=(TechnicsMaterialEntries) obj;
				descption=tme.getDescription();
			}
			//删除工艺条目
			TechnicsMaterialUtils.deleteTechMaterialEntries(oid);
			//删除工艺物资名称下的所有数据字典
			TechnicsMaterialUtils.deleteAttrInfo(oid,descption);
			message.addMessage("删除成功");
		} catch (Exception e) {
			formProcessingStatus = FormProcessingStatus.FAILURE;
			message.addMessage("删除失败");
			e.printStackTrace();
		} finally {
			form.setStatus(formProcessingStatus);
			form.addFeedbackMessage(message);
			form.setNextAction(FormResultAction.NONE);
		}
		return form;
	}
	/**
	 * 删除
	 * 
	 * @param commandBean
	 * @return
	 */
	public static FormResult deleteTechMaterial(NmCommandBean commandBean) {
		FormResult form = new FormResult();
		FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
		FeedbackMessage message = new FeedbackMessage();
		try {
			String oid = commandBean.getActionOid().getOid().toString();
			
			//删除工艺物资名称
			TechnicsMaterialUtils.deleteTechMaterial(oid);
			//删除工艺物资名称下的所有数据字典
			TechnicsMaterialUtils.deleteAllDictionary(oid);
			message.addMessage("删除成功");
		} catch (Exception e) {
			formProcessingStatus = FormProcessingStatus.FAILURE;
			message.addMessage("删除失败");
			e.printStackTrace();
		} finally {
			form.setStatus(formProcessingStatus);
			form.addFeedbackMessage(message);
			form.setNextAction(FormResultAction.NONE);
		}
		return form;
	}

	/**
	 * 删除
	 * 
	 * @param commandBean
	 * @return
	 */
	public static FormResult deleteDictionary(NmCommandBean commandBean) {
		FormResult form = new FormResult();
		FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
		StringBuffer msg = new StringBuffer();
		FeedbackMessage message = new FeedbackMessage();
		try {
			String oid = commandBean.getActionOid().getOid().toString();
			ArrayList selected = commandBean.getSelected();
			for (int i = 0; i < selected.size(); i++) {
				NmContext object = (NmContext) selected.get(i);
				String dicOid = object.getTargetOid().toString();
				TechnicsMaterialUtils.deleteTmnAndtmdLink(oid, dicOid);
			}

			msg.append("删除成功");
		} catch (Exception e) {
			formProcessingStatus = FormProcessingStatus.FAILURE;
			msg.append("删除失败");
			e.printStackTrace();
		} finally {
			form.setStatus(formProcessingStatus);
			form.setNextAction(FormResultAction.NONE);
		}
		return form;
	}
}
