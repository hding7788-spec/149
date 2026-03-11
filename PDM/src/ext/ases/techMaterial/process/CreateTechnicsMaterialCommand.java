package ext.ases.techMaterial.process;

import com.glaway.mpm.mpmresource.processors.CustomerObjectFormProcessor;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.techMaterial.TechnicsMaterial;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CreateTechnicsMaterialCommand extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) {
		FormResult formResult = new FormResult();
		FeedbackMessage message = new FeedbackMessage();
		HttpServletRequest request = arg0.getRequest();
		Map map = request.getParameterMap();
		String[] nameValues = (String[]) map.get("tmame");
		if (nameValues.length > 0) {
			try {
				String nameValue = nameValues[0];
				boolean flag = TechnicsMaterialUtils.hasTechncsiMaterialName(nameValue);
				if (flag) {
					formResult.setStatus(FormProcessingStatus.FAILURE);
					message.addMessage("工艺物资名称已经存在，不允许重复创建！");
					formResult.addFeedbackMessage(message);
					return formResult;
				}
				TechnicsMaterial material = TechnicsMaterial.newTechnicsMaterial();
				 // 设置生命周期为已发布
	            LifeCycleState materialState = LifeCycleState.newLifeCycleState();

	            material.setState(materialState);
				material.setName(nameValue);
				String folderName = "工艺物资名称";
				if(nameValue!=null && nameValue.startsWith("字典_")){
					materialState.setState(State.toState("INWORK"));
					material.setNumber("ZD_"+nameValue);
					folderName = "字典库";
				}else{
					materialState.setState(State.toState("APPROVED"));
					material.setNumber("GYWZMC"+TechnicsMaterialUtils.getUqipNumber());
				}
				WTContainerRef wtContainerRef = getWTContainerRef(WTContainer.class, "工艺物资信息库");
				long nowtime = Calendar.getInstance().getTimeInMillis();
				Timestamp createStamp = new Timestamp(nowtime);
				material.setContainerReference(wtContainerRef);
				Folder folder = null;
				try {
					folder = FolderHelper.service.getFolder("/Default/"+folderName, wtContainerRef);
				} catch (FolderNotFoundException e) {
					boolean accessEnforcedflag = SessionServerHelper.manager.setAccessEnforced(false);
					folder = FolderHelper.service.createSubFolder("/Default/"+folderName, wtContainerRef);
					SessionServerHelper.manager.setAccessEnforced(accessEnforcedflag);
				}

				FolderHelper.assignLocation(material, folder);
				 PersistenceServerHelper.manager.store(material, createStamp, createStamp);
				formResult.setStatus(FormProcessingStatus.SUCCESS);
				message.addMessage("新建工艺物资名称成功");
				formResult.addFeedbackMessage(message);

			} catch (Exception e) {
				e.printStackTrace();
				formResult.setStatus(FormProcessingStatus.FAILURE);
				message.addMessage("新建工艺物资名称失败");
				formResult.addFeedbackMessage(message);
			}
		}

		return formResult;
	}

	public static WTContainerRef getWTContainerRef(Class kass, String name) throws WTException {
		try {
			QuerySpec qs = new QuerySpec(kass);
			SearchCondition sc = new SearchCondition(kass, WTContainer.NAME, SearchCondition.EQUAL, name, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			WTContainer container;
			if (qr.hasMoreElements()) {
				container = (WTContainer) qr.nextElement();
				return WTContainerRef.newWTContainerRef(container);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
