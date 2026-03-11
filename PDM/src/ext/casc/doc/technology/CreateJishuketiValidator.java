package ext.casc.doc.technology;

import wt.folder.Cabinet;
import wt.folder.SubFolder;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CreateJishuketiValidator extends DefaultSimpleValidationFilter{

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey uivalidationkey, UIValidationCriteria uivalidationcriteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		Object object = uivalidationcriteria.getContextObject().getObject();
		if(object instanceof SubFolder){
			SubFolder folder = (SubFolder)object;
			String containerName = folder.getContainer().getName();
			System.out.println(containerName);
			if("技术课题".equals(containerName)){
				status = UIValidationStatus.ENABLED;
			}
		}
		if(object instanceof Cabinet){
			Cabinet cabinet = (Cabinet) object;
			String containerName = cabinet.getContainerName();
			if("技术课题".equals(containerName)){
				status = UIValidationStatus.ENABLED;
			}
		}
		return status;
	}

}
