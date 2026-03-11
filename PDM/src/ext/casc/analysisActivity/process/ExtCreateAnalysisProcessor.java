package ext.casc.analysisActivity.process;

import com.glaway.mpm.util.FolderUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.util.IBAHelper;
import wt.change2.Category;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeRequest2;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ExtCreateAnalysisProcessor extends DefaultObjectFormProcessor {

    /**
     * 方法功能: 文件夹按钮 创建更改影响分析
     *
     * @author cjh
     * @date 2024/3/28
     */
    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> beans) throws WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

        FormResult formResult = new FormResult();
        try {
			try {
				String name = (String) commandBean.getText().get("name");
				String secret = (String) ((ArrayList)commandBean.getComboBox().get("secret")).get(0);
				WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
				WTContainer container = commandBean.getContainer();
				WTContainerRef containerRef = commandBean.getContainerRef();
				WTChangeRequest2 ecr = WTChangeRequest2.newWTChangeRequest2();
				TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference("casc.sast.149.PROCESS_ECR");
				ecr.setTypeDefinitionReference(typeRef);
				ecr.setName("CHANGE_REQUEST");
				ecr.setDescription(name);
				ecr.setCategory(Category.OTHER);
				ecr.setContainer(container);
				String folderPath = "Default/02工艺文件/20变更申请单";
				Folder folder = FolderUtil.getFolder(folderPath, containerRef);
				if (folder == null) {
					try {
						FolderHelper.service.saveFolderPath(folderPath,  containerRef);
					} catch (Exception e) {
					}
				}
				VersionControlHelper.assignIterationCreator(ecr, WTPrincipalReference.newWTPrincipalReference(currentUser));
				ecr = (WTChangeRequest2) ChangeHelper2.service.saveChangeRequest(ecr);
				IBAHelper.setIBAStringValue(ecr, "SECRET", secret);
			} catch(WTException e) {
				e.printStackTrace();
			} catch(WTPropertyVetoException e) {
				e.printStackTrace();
			} catch(RemoteException e) {
				e.printStackTrace();
			}
            formResult.setStatus(FormProcessingStatus.SUCCESS);
            formResult.setNextAction(FormResultAction.NONE);
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage("启动更改影响分析成功");
            formResult.addFeedbackMessage(message);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formResult;
    }

}
