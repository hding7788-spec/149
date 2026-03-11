package ext.ases.techMaterial.process;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.org.WTUser;
import wt.session.SessionHelper;

import com.glaway.mpm.mpmresource.processors.CustomerObjectFormProcessor;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.techMaterial.gwpersistable.GwPersistenceHelper;
import ext.ases.techMaterial.model.TechnicsMaterialLink;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;

public class CreateTechMaterialProcessor extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) {
		FormResult formResult = new FormResult();
		FeedbackMessage message = new FeedbackMessage();
		HttpServletRequest request = arg0.getRequest();
		Map map = request.getParameterMap();
		String[] nameValues = (String[]) map.get("Print");
		if (nameValues.length > 0) {
			try {
				String nameValue = nameValues[0];
				String oid = arg0.getActionOid().getOid().toString();
				// 判断是否创建了链接关系
				boolean relation = TechnicsMaterialUtils.isRelation(oid, nameValue);
				if (!relation) {
					TechnicsMaterialLink link = new TechnicsMaterialLink();
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String time = simpleDateFormat.format(date);
					link.setTmcreatetime(time);
					WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
					link.setTmcreator(currentUser.getFullName());
					link.setDictionaryid(nameValue);
					link.setTechnicsmaterialid(oid);
					GwPersistenceHelper.manager.save(link);
					formResult.setStatus(FormProcessingStatus.SUCCESS);
					message.addMessage("新建数据字典成功");
					formResult.addFeedbackMessage(message);
				} else {
					formResult.setStatus(FormProcessingStatus.FAILURE);
					message.addMessage("数据字典已经存在，不允许重复创建！");
					formResult.addFeedbackMessage(message);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return formResult;
	}

}
