package ext.casc.changeRequest;

import com.glaway.mpm.util.FolderUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.forms.processors.CreateChangeRequestFormProcessor;
import wt.change2.WTChangeRequest2;
import wt.change2.WTChangeRequest2Master;
import wt.change2.WTChangeRequest2MasterIdentity;
import wt.fc.IdentityHelper;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.rmi.RemoteException;
import java.util.List;

public class ExtCreateChangeRequestFormProcessor extends CreateChangeRequestFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> beans) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

		String folderPath = "Default/02工艺文件/20变更申请单";
		Folder folder = FolderUtil.getFolder(folderPath, commandBean.getContainerRef());
		if (folder == null) {
			try {
				FolderHelper.service.saveFolderPath(folderPath,  commandBean.getContainerRef());
			} catch (Exception e) {
			}
		}

		FormResult formResult = super.doOperation(commandBean, beans);//返回表单内容
		try {
			WTChangeRequest2 ecr = null;
			for (ObjectBean bean : beans) {
				Object obj = bean.getObject();
				if (obj instanceof WTChangeRequest2) {
					ecr = (WTChangeRequest2) obj;
					String ecrType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(ecr);
					if (ecrType.endsWith("casc.sast.149.PROCESS_ECR")) {
						String numberString = Change2Util.getECRNumber(ecr.getContainer());
						WTChangeRequest2Master master = (WTChangeRequest2Master) ecr.getMaster();
						WTChangeRequest2MasterIdentity idy = (WTChangeRequest2MasterIdentity) master.getIdentificationObject();
						idy.setNumber(numberString);
						master = (WTChangeRequest2Master) IdentityHelper.service.changeIdentity(master, idy);
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch(WTPropertyVetoException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return formResult;
	}

}
