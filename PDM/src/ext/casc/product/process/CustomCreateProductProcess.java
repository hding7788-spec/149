package ext.casc.product.process;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.container.WTContainerTemplate;
import wt.inf.template.ContainerTemplateHelper;
import wt.inf.template.WTContainerTemplateMaster;
import wt.inf.template._WTContainerTemplateMaster;
import wt.org.WTOrganization;
import wt.org.WTUser;
import wt.org._WTPrincipal;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.processors.CustomerObjectFormProcessor;
import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.product.util.CustomCreateProductUtil;

public class CustomCreateProductProcess extends CustomerObjectFormProcessor {

	public static void main(String[] args) {
	}

	@Override
	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> var2) throws WTException {

		FormResult formResult = new FormResult();
		FeedbackMessage message = new FeedbackMessage();
		HttpServletRequest request = arg0.getRequest();
		Map map = request.getParameterMap();
		String[] productNames = (String[]) map.get("productName");
		String[] productTemplates = (String[]) map.get("productTemplate");
		String[] descriptions = (String[]) map.get("description");
		String[] xhjhs = (String[]) map.get("xhjh");
		String[] xhlxs = (String[]) map.get("xhlx");
		String[] cpdhs = (String[]) map.get("cpdh");
		String[] fmxhdhs = (String[]) map.get("fmxhdh");
		if (productNames == null) {
			message.addMessage("产品名称不能为空!");
			formResult.addFeedbackMessage(message);
			formResult.setStatus(FormProcessingStatus.FAILURE);
			return formResult;
		} else if (productTemplates == null) {
			message.addMessage("产品模板不能为空!");
			formResult.addFeedbackMessage(message);
			formResult.setStatus(FormProcessingStatus.FAILURE);
			return formResult;
		} else if (xhlxs == null) {
			message.addMessage("型号类型不能为空!");
			formResult.setStatus(FormProcessingStatus.FAILURE);
		}
		if (productNames != null) {
			String productName = productNames[0];
			try {
				boolean hasProduct = CustomCreateProductUtil.hasProduct(productName);
				if (hasProduct) {
					message.addMessage("产品名称已经存在!");
					formResult.addFeedbackMessage(message);
					formResult.setStatus(FormProcessingStatus.FAILURE);
					return formResult;
				}
				String description="";
				if (descriptions==null) {
					description="";
				}else{
					description=descriptions[0];
				}
				PDMLinkProduct product = createProduct(productName, productTemplates[0],description);
				IBAHelper iba = new IBAHelper(product);
				iba.setIBAValue("PINDEX", cpdhs[0]);
				iba.setIBAValue("XHLX", xhlxs[0]);
				iba.setIBAValue("XHJH", xhjhs[0]);
				iba.setIBAValue("FMXHDH", fmxhdhs[0]);
				iba.updateAttributeContainer(product);
				iba.updateIBAHolder(product);
				message.addMessage("新建产品成功!");
				formResult.addFeedbackMessage(message);
				formResult.setStatus(FormProcessingStatus.SUCCESS);
				return formResult;
			} catch (Exception e) {
				message.addMessage("新建产品失败!");
				formResult.addFeedbackMessage(message);
				formResult.setStatus(FormProcessingStatus.FAILURE);
				return formResult;
			}

		}

		return formResult;
	}

	public static PDMLinkProduct createProduct(String productName, String productTemplate,String description) throws WTException {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		WTContainerTemplate localWTContainerTemplate = null;
		WTUser user = (WTUser) SessionHelper.getPrincipal();
		try {
			WTContainerTemplateMaster master = getTemplateByTemplateName(productTemplate);
			localWTContainerTemplate = ContainerTemplateHelper.service.getContainerTemplate(master);
			WTOrganization localWTOrganization = getOrgByOrgName("149");
			WTContainerRef localWTContainerRef = WTContainerHelper.service.getOrgContainerRef(localWTOrganization);
			PDMLinkProduct product = PDMLinkProduct.newPDMLinkProduct();
			product.setName(productName);
			product.setCreator(user);
			product.setContainerReference(localWTContainerRef);
			product.setContainerTemplate(localWTContainerTemplate);
			product.setDescription(description);
			product = (PDMLinkProduct) WTContainerHelper.service.create(product);
			return product;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return null;
	}

	public static WTOrganization getOrgByOrgName(String name) {
		WTOrganization org = null;
		try {
			QuerySpec spec = new QuerySpec(WTOrganization.class);
			spec.appendWhere(new SearchCondition(WTOrganization.class, _WTPrincipal.NAME, SearchCondition.EQUAL, name));
			QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) spec);
			if (qResult.hasMoreElements()) {
				org = (WTOrganization) qResult.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return org;

	}

	public static WTContainerTemplateMaster getTemplateByTemplateName(String name) {
		WTContainerTemplateMaster template = null;
		try {
			QuerySpec spec = new QuerySpec(WTContainerTemplateMaster.class);
			spec.appendWhere(new SearchCondition(WTContainerTemplateMaster.class, _WTContainerTemplateMaster.NAME, SearchCondition.EQUAL, name));
			QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) spec);
			if (qResult.hasMoreElements()) {
				template = (WTContainerTemplateMaster) qResult.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return template;

	}

}
