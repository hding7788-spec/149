package ext.casc.workflow.util;

import java.beans.PropertyVetoException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.iba.value.IBAHolder;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTRuntimeException;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.ptc.core.htmlcomp.util.VersionComparator;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.baseline.BaselineHelper;
import ext.casc.change.CSCChange;
import ext.casc.change.ChangeHelper;
import ext.casc.changephase.ChangePhaseConstants;
import ext.casc.changephase.PhaseComparator;
import ext.casc.doc.DocumentUtil;
import ext.casc.doc.EPMDocumentHelper;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.number.ASESNumberUtil;
import ext.casc.util.CSCIBA;
import ext.casc.util.IBAHelper;


public class WorkflowValidationUtil4Dwg {
	
	public static final String VALIDATE_OK = "OK";
	public static final String RESOURCE = "ext.ases.workflow.resource.WorkflowRB";
	
	private static Logger logger = LogR.getLogger(WorkflowValidationUtil4Dwg.class.getName());
	
	
	public static String ECR_BianZhiValidate(WTChangeRequest2 ecr) {
		CSCIBA ecrIBA = new CSCIBA(ecr);
		String ecnChangeType = ecrIBA.getIBAValue(ChangePhaseConstants.ASES_IBA_CHANGETYPE);
		String ecnPhaseCode = ecrIBA.getIBAValue(ChangePhaseConstants.ASES_IBA_JIEDUANBIAOSHI);
		// 检测是否在需要ECR付pdf附件的阶段类别内
		if (ecnChangeType == null || ecnPhaseCode == null) {
			return "未选择正确阶段标示和更改类别！";
		}
		
		String combinePhaseCode_Changetype = ecnPhaseCode + ecnChangeType;
		if (ChangePhaseConstants.ASES_PHASE_ARRAY_PHASE_CHANGETYPE_ECR.indexOf(combinePhaseCode_Changetype) != -1) {
			Map attachemntsMap = CSCChange.getAttachments(ecr, ContentRoleType.SECONDARY);
			boolean containsPdf = false;
			if (attachemntsMap.size()>0)
				containsPdf = true;
			/*for (Object key : attachemntsMap.keySet()) {
				if (key.toString().endsWith(".pdf")) {
					containsPdf = true;
					break;
				}
			}*/
			if (!containsPdf) return ecnPhaseCode + "阶段" + ecnChangeType + "类"  + " 更改需将现纸质“更改方案论证报告”扫描为电子文件，作为变更请求的附件上传。 ";
		}
		return WorkflowValidationUtil4Dwg.VALIDATE_OK;
		
	}
	
	public static boolean validateLifecycleState(LifeCycleManaged lcm)
	{
		if(lcm != null)
		{
			if(!lcm.getLifeCycleState().equals(State.INWORK) &&
					!lcm.getLifeCycleState().equals(State.toState("ASES_MODIFY")))
				return false;
		}
		return true;
	}
	
	public static boolean validateDuplicateInProcEnv(WTObject wtobject)
	{	
		try {
			QueryResult qr = EnvelopeHelper.service.getEnvelopeByMemberObject((RevisionControlled)wtobject);	
			if (qr != null && qr.size() > 0) {// 文档已经在一个审签包中
				while (qr.hasMoreElements()) {
					EnvelopeMemberLink eml = (EnvelopeMemberLink) qr.nextElement();
					ProcessEnvelope pe = eml.getProcessEnvelope();														
					if(!pe.getLifeCycleState().equals(State.toState("CANCELLED")))
						return false;														
				}
			}	
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String ECN_BianZhiValidate(WTChangeOrder2 ecn) throws WTException {
		StringBuffer returnMsg = new StringBuffer("");
		CSCIBA ecnIBA = new CSCIBA(ecn);
		String ecnChangeType = ecnIBA.getIBAValue(ChangePhaseConstants.ASES_IBA_CHANGETYPE);
		String ecnPhaseCode = ecnIBA.getIBAValue(ChangePhaseConstants.ASES_IBA_JIEDUANBIAOSHI);
		
		Map ecnAttachments = CSCChange.getAttachments(ecn, ContentRoleType.SECONDARY);
		
		boolean containPDFGengGaiDan = false;
		boolean containDOCGengGaiDan = false;
		int size = ecnAttachments.size();
		for (Object keyFileName : ecnAttachments.keySet()) {
			String postfix = keyFileName.toString().substring(keyFileName.toString().lastIndexOf(".")+1);
			if (postfix.equalsIgnoreCase("pdf")) {
				containPDFGengGaiDan = true;
			}
			//if (postfix.equalsIgnoreCase("doc")||postfix.equalsIgnoreCase("docx")) {
			//	containDOCGengGaiDan = true;
			//}
		}
		
		// 检测是否有pdf格式的更改单
		if (!containPDFGengGaiDan) {
			String SINGATURE_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","ECN_VALIDATION_NO_PDF",null,Locale.getDefault());
			returnMsg.append(SINGATURE_MESSAGE + "\n");
		}
		
		if (containPDFGengGaiDan&&size<2) {
			String SINGATURE_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","ECN_VALIDATION_NO_PDF",null,Locale.getDefault());
			returnMsg.append(SINGATURE_MESSAGE + "\n");
		}
		
		if (ecnChangeType == null || ecnPhaseCode == null) {
			returnMsg.append("未选择正确阶段标示和更改类别！\n");
		}
		
		String combinePhaseCode_Changetype = ecnPhaseCode + ecnChangeType;
		
		List<WTChangeRequest2> ecrList = CSCChange.getReleatedECR(ecn, false);
		
		// 检测是否在需要ECR付pdf附件的阶段类别内
		if (Arrays.asList(ChangePhaseConstants.ASES_PHASE_ARRAY_PHASE_CHANGETYPE_ECR.split("\\" + ChangePhaseConstants.ASES_PHASE_SEP)).indexOf(combinePhaseCode_Changetype) != -1) {
			if (ecrList == null || ecrList.size() == 0) {
				returnMsg.append(ecnPhaseCode + "阶段" + ecnChangeType + "类" + "更改要求先提交变更申请。\n");
			}
			else {
				boolean containReport = false;
				for (WTChangeRequest2 ecr : ecrList) {
					if (!ecr.getLifeCycleState().equals(State.toState("APPROVED"))) {
						returnMsg.append("附加更改请求：" + ecr.getName() + " 未批准！\n");
					}
					Map attachmentsMap = CSCChange.getAttachments(ecr, ContentRoleType.SECONDARY);
					for (Object key : attachmentsMap.keySet()) {
						// 已批准且有报告的ecr才有效
						if (key.toString().endsWith(".pdf")) {
							containReport = true;
							break;
						}
					}
					if (containReport) break;
				}
				if (!containReport) returnMsg.append("需要：" + "更改方案论证报告.pdf" + " 文件\n");
			}
		}
		
		List<RevisionControlled> caBeforeItems = ChangeHelper.getChangeAffectItem(ecn);
		List<RevisionControlled> caAfterItems = ChangeHelper.getChangeResultItem(ecn);
		StringBuffer validateAttr = new StringBuffer("");
		for(int i = 0 ; i < caAfterItems.size() ; i++){
			WTObject wto = (WTObject)caAfterItems.get(i);
			if(wto instanceof WTDocument){
				WTDocument wtd = (WTDocument)wto;
				String s = WorkflowValidationUtil4Dwg.validateDocAttribute(wtd);
				if(!s.equals(WorkflowValidationUtil4Dwg.VALIDATE_OK))
					validateAttr.append(s);
			}
			if((wto instanceof WTPart)||(wto instanceof EPMDocument)){
				WTObject wtobject = (WTObject)wto;
				String s = WorkflowValidationUtil4Dwg.validateEPMPartAttribute(wtobject);
				if(!s.equals(WorkflowValidationUtil4Dwg.VALIDATE_OK))
					validateAttr.append(s);
			}
			String value = validateRepresentable(wto);
			if(!value.equals(WorkflowValidationUtil4Dwg.VALIDATE_OK)){
				validateAttr.append(value);
			}
			boolean ischeckout = wt.vc.wip.WorkInProgressHelper.isCheckedOut((wt.enterprise.RevisionControlled)wto);
			if(ischeckout)
				validateAttr.append("请将流程中的对象: " + wto.getIdentity() + " 检入再提交!\n");
		}
		String va = validateAttr.toString();
		if(!("".equals(va)))
			return va;
		
		if (caBeforeItems != null && caAfterItems != null && !caBeforeItems.isEmpty() && !caAfterItems.isEmpty()) {
			int countEqual = 0;
			for (RevisionControlled caBeforeObj : caBeforeItems) {
				for (RevisionControlled caAfterobj : caAfterItems) {
					// 如果数据在ca的改前有，改后也得有，并且，要判断图样文件！
					if (caBeforeObj.getMaster().equals(caAfterobj.getMaster())) {
						countEqual++;
						// 比较版本
						int reviseResult = VersionComparator.getInstance(true).compare(caBeforeObj, caAfterobj);
						int versionResult = VersionComparator.getInstance(false).compare(caBeforeObj, caAfterobj);
						// 检测 改前数据状态
						
						try {
							if (!caBeforeObj.getLifeCycleState().equals(State.toState("APPROVED")) || WorkInProgressHelper.isCheckedOut(caBeforeObj)) {
								returnMsg.append("请检查：" + caBeforeObj.getIdentity() + " 状态。\n");
							}
							
							if (reviseResult < 0 || (reviseResult == 0 && versionResult < 0)) {
								// 检测可视化
								String representValidationResult = WorkflowValidationUtil4Dwg.validateRepresentable(caAfterobj);
								if (!WorkflowValidationUtil4Dwg.VALIDATE_OK.equals(representValidationResult)) {
									returnMsg.append(representValidationResult);
								}
							}else {
								returnMsg.append("改后数据 " + caAfterobj.getIdentity() + " 的版本号小于改前" + caBeforeObj.getIdentity() + "\n");
							}
						} catch (Exception e) {
							logger.error(e.getLocalizedMessage());
							return e.getLocalizedMessage();
						}
					}
				}
			}
			
//			if (countEqual < caBeforeItems.size()) returnMsg.append("改前数据在改后的审签不存在\n");
			
		} else if(caAfterItems == null || caAfterItems.isEmpty()){
			returnMsg.append("请增加产生的新版本数据到更改单中！！！\n");
		}
		
		String rs = returnMsg.toString();
		return "".equals(rs) ? WorkflowValidationUtil4Dwg.VALIDATE_OK : rs;
	}
	
	
	/**
	 * 必须是成套部件，图纸
	 * 检查转阶段数据是否全部已批准状态;
	 * 检查转阶段数据是否是最新版
	 * 检查要转入阶段是否高于旧阶段.
	 * 检查是否有转阶段ECN的pdf文件
	 * @param ecn
	 * @param toPhaseCode 从流程中获取
	 * @return
	 */
	public static String CS_ECN_BianZhiValidate(WTChangeOrder2 ecn, String toPhaseCode) {
		StringBuffer returnMsg = new StringBuffer("");
		// 检测 toPhaseCode 是否在可选范围内；
		if (!PhaseComparator.PHASE_STATE_LIST.contains(toPhaseCode)) returnMsg.append("要转入的阶段标记：" + toPhaseCode + "不是合法的标记!\n");
		CSCIBA ecn_iba = new CSCIBA(ecn);
		String fromPhaseCode = ecn_iba.getIBAValue(ChangePhaseConstants.ASES_IBA_JIEDUANBIAOSHI);
		
//		// 检查是否有默认的ca
//		QueryResult activitiesResult = ChangeHelper2.service.getChangeActivities(ecn);
//		Vector<ChangeActivity2> other_ca_List = new Vector<ChangeActivity2>();
//		ChangeActivity2 ca = null;
//		boolean found = false;
//		while (activitiesResult.hasMoreElements() && !found) {
//			ca = (ChangeActivity2) activitiesResult.nextElement();
//			// 默认 CA 是成套部件
//			if (ChangeConstants.ASES_CA_MISSION_ECN_AFFECTED.contains(ca.getName())) {
//				found = true;
//			} else {
//				// 其他的 CA 关联非成套部件
//				other_ca_List.add(ca);
//			}
//		}
//		if (!found) {
//			throw new WTException("找不到默认的 Change Activity！！！"); 
//		}
		// TODO 检测是否有跨套，多套，情况
//		List<WTPart> setPartList = ChangePhaseHelper.extractAffectedPackagedPartFromECN(ecn);
//		if (setPartList.isEmpty()) {
//			return "必须以成套部件发起！";
//		} else if (setPartList.size() > 1) {
//			return "转阶段不允许多套发起！";
//		} else if (setPartList.size() == 1) {
//			// TODO 遍历 成套部件，看是否有跨套的情况！
//			
//		}
		
		try {
			// 检测要转入的阶段是否高于旧阶段
			int compareResult = new PhaseComparator().compare(fromPhaseCode, toPhaseCode);
			if (compareResult <= 0) {
				returnMsg.append("请选择正确的阶段！" + "改前：" + fromPhaseCode + "\t改后:" + toPhaseCode + "\n");
			}
			
			// 检测改前CA数据是否都是已发布或者已批准，是否在ECN阶段或者之前
			// 检测是是最新版版本
			List caBeforeItems = ChangeHelper.getChangeAffectItem(ecn);
			for (Object obj : caBeforeItems) {
				RevisionControlled revise_obj = (RevisionControlled) obj;
				State objLifeState = revise_obj.getLifeCycleState();
				// 检测是不是可转阶段的状态
				if (!(objLifeState.equals(State.toState("APPROVED")))) {
					returnMsg.append("转阶段数据：" + revise_obj.getLifeCycleState().getDisplay(Locale.CHINA) + " 不是已批准或者已发行状态！！\n");
				}
				
				if (WorkInProgressHelper.isCheckedOut(revise_obj)) {
					returnMsg.append(revise_obj.getDisplayIdentifier() + "被检出状态，无法转阶段!\n");
				}
				
				// 文件阶段标志大于from阶段，过滤不合理部件
				if (IBAHolder.class.isAssignableFrom(obj.getClass())) {
					if (obj instanceof WTPart) {
						CSCIBA objIBA = new CSCIBA((IBAHolder) obj);
						String objPhaseCode = objIBA.getIBAValue(ChangePhaseConstants.ASES_IBA_JIEDUANBIAOSHI);
						if (objPhaseCode != null) {
							int objcompResult = new PhaseComparator().compare(objPhaseCode, toPhaseCode);
							if (objcompResult < 0) {
								returnMsg.append("请选择正确的阶段！" + "改前：" + fromPhaseCode + "\t物件: " + revise_obj.getDisplayIdentifier() + "\t" + objPhaseCode + "\n");
							}
						}
					}
				
				} else {
					returnMsg.append(((WTObject)obj).getDisplayIdentifier() + " 不是可转阶段的类！\n");
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			return e.getLocalizedMessage();
		}
		
		// 检测是否有pdf文件
		/*去掉转阶段pdf文件的检查
		Map attachmentMap = CSCChange.getAttachments(ecn, ContentRoleType.SECONDARY);
		boolean containCS_ECN_PDF = false;
		for (Object key : attachmentMap.keySet()) {
			if (key.toString().endsWith(".pdf")) {
				containCS_ECN_PDF = true;
				break;
			}
		}
		
		
		if (!containCS_ECN_PDF) {
			String SINGATURE_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","CS_VALIDATION_NO_PDF",null,Locale.getDefault());
			returnMsg.append(SINGATURE_MESSAGE);
		}*/

		String rs = returnMsg.toString();
		return "".equals(rs) ? WorkflowValidationUtil4Dwg.VALIDATE_OK : rs;
	}
	
	public static String getAvalibabeECNPhaseCode(Object obj) throws WTException{
		StringBuffer sb = new StringBuffer();
		if (obj instanceof WTChangeOrder2) {
			WTChangeOrder2 ecn = (WTChangeOrder2) obj;
			CSCIBA ecn_iba = new CSCIBA(ecn);
			String fromPhaseCode = ecn_iba.getIBAValue(ChangePhaseConstants.ASES_IBA_JIEDUANBIAOSHI);
			String[] allPhases = ChangePhaseConstants.ASES_PHASE_ARRAY.split("\\" + ChangePhaseConstants.ASES_PHASE_SEP);
			List<String> allPhasesList = Arrays.asList(allPhases);
			int pos = allPhasesList.indexOf(fromPhaseCode);
			if (pos == -1) {
				throw new WTException(fromPhaseCode + " 建立ecn时的阶段标示不是有效阶段标示。");
			} else if (pos + 1 == allPhasesList.size()) {
				throw new WTException(fromPhaseCode + " 已经是最后阶段");
			}
			for(pos++ ; pos < allPhasesList.size(); pos++) {
				sb.append(allPhasesList.get(pos)).append(',');
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * 改前CA状态需要检查
	 * 需要有技术固化pdf文件
	 * @param ecn
	 * @return
	 */
	public static String SNAPSHOT_ECN_BianzhiValidate(WTChangeOrder2 ecn) {
		StringBuffer returnMsg = new StringBuffer("");
		CSCIBA ecn_iba = new CSCIBA(ecn);
		String fromPhaseCode = ecn_iba.getIBAValue(ChangePhaseConstants.ASES_IBA_JIEDUANBIAOSHI);

		
		// 检测改前CA数据是否都是已发布或者已批准
		List caBeforeItems = ChangeHelper.getChangeAffectItem(ecn);
		State[] states = new State[] {State.toState("APPROVED")};
		String caBeforeResult = WorkflowUtil.isAllWTObjectAtAssignedStateNullSafe(caBeforeItems, "不是已批准或者已发行状态！！", states);
		if (!WorkflowValidationUtil4Dwg.VALIDATE_OK.equals(caBeforeResult)) returnMsg.append(caBeforeResult);
		
		// 检测改前CA数据是否检出
		try {
			for (Object caBeforeObj : caBeforeItems) {
				if (WorkInProgressHelper.isCheckedOut((Workable)caBeforeObj)) {
					returnMsg.append(((Workable)caBeforeObj).getIdentity() + "是检出状态！\n");
				}
			}
		} catch (WTException e) {
			logger.error(e.getLocalizedMessage());
			return e.getLocalizedMessage();
		}

		// 检测是否有pdf文件
//		Map attachmentMap = CSCChange.getAttachments(ecn, ContentRoleType.SECONDARY);
//		boolean containCS_ECN_PDF = false;
//		for (Object key : attachmentMap.keySet()) {
//			if (key.toString().endsWith(".pdf")) {
//				containCS_ECN_PDF = true;
//				break;
//			}
//		}
//
//		if (!containCS_ECN_PDF) return "需要 pdf 文件做为附件!";
		
		
		
		String rs = returnMsg.toString();
		return "".equals(rs) ? WorkflowValidationUtil4Dwg.VALIDATE_OK : rs;
	}
	
	
	public static String TEMP_ECN_BianzhiValidate(WTChangeOrder2 ecn) {
//		// 检查是否有默认的ca
//		QueryResult activitiesResult = ChangeHelper2.service.getChangeActivities(ecn);
//		Vector<ChangeActivity2> other_ca_List = new Vector<ChangeActivity2>();
//		ChangeActivity2 ca = null;
//		boolean found = false;
//		while (activitiesResult.hasMoreElements() && !found) {
//			ca = (ChangeActivity2) activitiesResult.nextElement();
//			// 默认 CA 是成套部件
//			if (ChangeConstants.ASES_CA_MISSION_ECN_AFFECTED.contains(ca.getName())) {
//				found = true;
//			} else {
//				// 其他的 CA 关联非成套部件
//				other_ca_List.add(ca);
//			}
//		}
//		if (!found) {
//			throw new WTException("找不到默认的 Change Activity！！！"); 
//		}

		// 检测数据是否是已批准或发行状态
		List caBeforeItems = ChangeHelper.getChangeAffectItem(ecn);
//		State[] states = new State[] {State.toState(WorkflowConstants.ASES_STATE_APPROVED)};
//		String caBeforeResult = WorkflowUtil.isAllWTObjectAtAssignedState(caBeforeItems, "不是已批准或者已发行状态！！", states);
//		if (!WorkflowValidationUtil.VALIDATE_OK.equals(caBeforeResult)) return caBeforeResult;
		
		// 检测改前CA数据是否检出
		try {
			for (Object caBeforeObj : caBeforeItems) {
				if (WorkInProgressHelper.isCheckedOut((Workable)caBeforeObj)) {
					return ((Workable)caBeforeObj).getIdentity() + "是检出状态！";
				}
			}
		} catch (WTException e) {
			logger.error(e.getLocalizedMessage());
			return e.getLocalizedMessage();
		}

		// 改后是否关联技术通知单
		List caAfter = ChangeHelper.getChangeResultItem(ecn);
		boolean hasJSNote = false;
		WTDocument jsDoc = null;
		for (Object obj : caAfter) {
			// TODO 检测软类型
			if (TypeIdentifierHelper.getType(obj).toExternalForm().contains("TECHNOTICE_DOC")) {
				jsDoc = (WTDocument) obj;
				hasJSNote = true;
				break;
			}
		}
		if (!hasJSNote) {
			return "技术通知单要关联改后数据！！";
		} else {
			String jsDocValidation = "";
			try {
				jsDocValidation = validateRepresentable(jsDoc);
			} catch (WTException e) {
				logger.error(e.getLocalizedMessage());
				return e.getLocalizedMessage();
			}
			if (!"OK".equals(jsDocValidation)) {
				return jsDoc.getIdentity() + jsDocValidation;
			}
		}
		return "OK";
	}

	
	/**
	 * 产品结构管理批准完成校验
	 * 检查CAD数据是否生成可视化文件，检查图档是否手工添加同编号命名的用于打印的PDF附件，明确提示哪些图纸缺少可视化文件或PDF附件
	 * 对于PROE设计图纸和OFFICE工具生成的文件，检查可视化文件是否生成，对于AutoCAD和Protel等其它生成的，检查附件中是否存在有同编号命名的PDF文件
	 * @param wtObj
	 * @return
	 * @throws WTException 
	 */
	public static String PIZHUN_QIANSHEN_BianzhiValidation(WTObject wtObj) throws WTException {
		return WorkflowValidationUtil4Dwg.validateRepresentable(wtObj);
	}
	
	/**
	 * 检查是否有可视化文件
	 * @param representable
	 * @return
	 * @throws WTException
	 */
	public static boolean hasRepresentableDoc(Representable representable) throws WTException {
		if (representable == null) throw new WTException("可视化文件为空！");
		ApplicationData primaryAppData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(representable, ContentRoleType.PRIMARY);
		if (qr.hasMoreElements()) {
			primaryAppData = (ApplicationData) qr.nextElement();
		}
		// 如果没有主内容，则抛出异常
		if (primaryAppData == null) throw new WTException(representable.getIdentity() + "没有主内容文件!");
		boolean result = false;
		if (representable instanceof WTDocument) {
			Representation representation = RepresentationHelper.service.getDefaultRepresentation(representable);
			if (representation == null) {
				logger.error("可视化默认表示法为空！");
				throw new WTException(representable.getIdentity() + "可视化文件为空！");
			} else {
				ContentHolder holder = null;
				try {
					holder = ContentHelper.service.getContents(representation);
				} catch (WTException e1) {
					e1.printStackTrace();
				} catch (PropertyVetoException e1) {
					e1.printStackTrace();
				}
				logger.info("内容Holder类型：" + holder.getType());
				if (holder != null) {
					Vector enuma = ContentHelper.getContentListAll(holder);
					logger.info("内容大小:" + enuma.size());
					try {
						for (Object apObj : enuma) {
							ApplicationData ap = (ApplicationData) apObj;
							String decodedFilename = URLDecoder.decode(ap.getFileName(), "UTF8");
							logger.info("系统内表示法的文件名：" + decodedFilename);
							if (decodedFilename.endsWith(".pdf")) {
								result = true;
								break;
							}
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						logger.error(e.getLocalizedMessage());
					}
				}
			}
		} else if(representable instanceof EPMDocument) {
			EPMDocument epmDoc = (EPMDocument) representable;
			// 如果是2D，就检测可视化
			if (epmDoc.getDocType().equals(EPMDocumentHelper.CADDcouemntType[1])) {
				Representation representation = RepresentationHelper.service.getDefaultRepresentation(representable);
				if (representation == null) {
					logger.error("可视化默认表示法为空！");
					throw new WTException(representable.getIdentity() + "可视化文件为空！");
				} else {
					ContentHolder holder = null;
					try {
						holder = ContentHelper.service.getContents(representation);
					} catch (WTException e1) {
						e1.printStackTrace();
					} catch (PropertyVetoException e1) {
						e1.printStackTrace();
					}
					logger.info("内容Holder类型：" + holder.getType());
					if (holder != null) {
						Vector enuma = ContentHelper.getContentListAll(holder);
						logger.info("内容大小:" + enuma.size());
						try {
							for (Object apObj : enuma) {
								ApplicationData ap = (ApplicationData) apObj;
								String decodedFilename = URLDecoder.decode(ap.getFileName(), "UTF8");
								logger.info("系统内表示法的文件名：" + decodedFilename);
								if (decodedFilename.endsWith(".pdf")) {
									result = true;
									break;
								}
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							logger.error(e.getLocalizedMessage());
						}
					}
				}
			} else {
				result = true;
			}
		} else {
			result = false;
		}
		return result;
	}
	
	/**
	 * 产品结构管理批准完成校验
	 * 检查CAD数据是否生成可视化文件，检查图档是否手工添加同编号命名的用于打印的PDF附件，明确提示哪些图纸缺少可视化文件或PDF附件
	 * 对于PROE设计图纸和OFFICE工具生成的文件，检查可视化文件是否生成，对于AutoCAD和Protel等其它生成的，检查附件中是否存在有同编号命名的PDF文件
	 * @param wtObj
	 * @return
	 * @throws WTException 
	 */
	public static String validateRepresentable(WTObject wtObj) throws WTException {
		if (wtObj instanceof EPMDocument) {
			EPMDocument epmDoc = (EPMDocument) wtObj;
			EPMAuthoringAppType authType = epmDoc.getAuthoringApplication() ;
			// FIXME 可是文档的判断
			if (!WorkflowValidationUtil4Dwg.hasRepresentableDoc(epmDoc)) return  epmDoc.getDisplayIdentifier() + "没有生成可视化文件!";
		} else if (wtObj instanceof WTDocument) {
			WTDocument wtDoc = (WTDocument) wtObj;
			// 判断是是否是office类型
			Map docRelatedObjMap = DocumentUtil.getAttachmentsFromDocument(wtDoc, ContentRoleType.PRIMARY);
			for (Object key : docRelatedObjMap.keySet()) {
				String primaryFileName = key.toString();
				List<String> extList = Arrays.asList("xls|xlsx|doc|docx|ppt|pptx".split("\\" + "|"));
				if (extList.contains(primaryFileName.substring(primaryFileName.lastIndexOf('.') + 1))) {
					// 如果是office文件，检查可视化
					if (!WorkflowValidationUtil4Dwg.hasRepresentableDoc(wtDoc)) {
						return wtDoc.getDisplayIdentifier() + " 没有生成可视化!";
					}
				} else if(!primaryFileName.substring(primaryFileName.lastIndexOf('.') + 1).equalsIgnoreCase("pdf")
						&& !primaryFileName.substring(primaryFileName.lastIndexOf('.') + 1).toUpperCase().equalsIgnoreCase("DWG")){//如果是dwg将不进行判断
					// 不是的话，检查与主内容一致的pdf
					Map secondayMap = DocumentUtil.getAttachmentsFromDocument(wtDoc, ContentRoleType.SECONDARY);
					boolean hasSameNamePDF = false;
					for (Object secondaryKey : secondayMap.keySet()) {
						String secondaryFileName = secondaryKey.toString();
						// 判断是否有同名的.pdf文件
						String primaryMainName = primaryFileName.substring(0, primaryFileName.lastIndexOf('.'));
						String secondaryMainName = secondaryFileName.substring(0, secondaryFileName.lastIndexOf('.'));
						if (primaryMainName.equals(secondaryMainName) && secondaryFileName.endsWith(".pdf")) {
							hasSameNamePDF = true;
						}
					}
					if (!hasSameNamePDF) {
						return wtDoc.getDisplayIdentity() + " 没有PDF格式附件，请提交一个与主内容同名的PDF格式附件。";
					}
				}
			}
		}
		return WorkflowValidationUtil4Dwg.VALIDATE_OK; 
	}
	
	
	
	/**
	 * 返回是否有生成可视化，为文档签审做准备
	 * @param wtDoc
	 * @return
	 * @throws WTException
	 */
	public static String validateRepresentableDocShenQian(WTDocument wtDoc) throws WTException {
		// 判断是是否是office类型
		Map docRelatedObjMap = DocumentUtil.getAttachmentsFromDocument(wtDoc, ContentRoleType.PRIMARY);
		for (Object key : docRelatedObjMap.keySet()) {
			String primaryFileName = key.toString();
			List<String> extList = Arrays.asList("xls|xlsx|doc|docx|ppt|pptx".split("\\" + "|"));
			if(primaryFileName.substring(primaryFileName.lastIndexOf('.') + 1).equalsIgnoreCase("pdf")){
				return VALIDATE_OK;
			}else
			if (extList.contains(primaryFileName.substring(primaryFileName.lastIndexOf('.') + 1))) {
				// 如果是office文件，检查可视化
				if (!WorkflowValidationUtil4Dwg.hasRepresentableDoc(wtDoc)) {
					return wtDoc.getDisplayIdentifier() + " 没有生成可视化!";
				}
			} else if(!primaryFileName.substring(primaryFileName.lastIndexOf('.') + 1).equalsIgnoreCase("pdf")
					&& !primaryFileName.substring(primaryFileName.lastIndexOf('.') + 1).toUpperCase().equalsIgnoreCase("DWG")){//如果是dwg将不进行判断
				// 不是的话，检查与主内容一致的pdf
				Map secondayMap = DocumentUtil.getAttachmentsFromDocument(wtDoc, ContentRoleType.SECONDARY);
				boolean hasSameNamePDF = false;
				for (Object secondaryKey : secondayMap.keySet()) {
					String secondaryFileName = secondaryKey.toString();
					// 判断是否有同名的.pdf文件
					String primaryMainName = primaryFileName.substring(0, primaryFileName.lastIndexOf('.'));
					String secondaryMainName = secondaryFileName.substring(0, secondaryFileName.lastIndexOf('.'));
					if (primaryMainName.equals(secondaryMainName) && secondaryFileName.endsWith(".pdf")) {
						hasSameNamePDF = true;
					}
				}
				if (!hasSameNamePDF) {
					return wtDoc.getDisplayIdentity() + " 没有PDF格式附件，请提交一个与主内容同名的PDF格式附件。";
				}
			}
		}
		return VALIDATE_OK;
	}
	
	/**
	 * 校验文档属性
	 * @param wtDoc
	 * @return
	 * @throws WTException
	 */
	public static String validateDocAttribute(WTDocument wtDoc) throws WTException {
		String softtype = IBAHelper.getSoftType(wtDoc);
		if(softtype.indexOf("TECHNOTICE_DOC")>-1)
			return WorkflowValidationUtil4Dwg.VALIDATE_OK;
		String checkDocAttribute = "";
		StringBuffer returnMsg = new StringBuffer("");
		try {
			checkDocAttribute = ASESNumberUtil.getStrFromProperties("ext.ases.workflow.checkDocAttribute", "ext.ases.ases");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 获得文档要检查的属性
		
		String docAttribute[] = checkDocAttribute.split(",");
		for(int i=0;i<docAttribute.length;i++){
			String attr = docAttribute[i];
			String value = IBAHelper.getIBAStringValue(wtDoc, attr);
			if(attr.equalsIgnoreCase("PHASE_CODE"))
				attr = "阶段标记";
			else if(attr.equalsIgnoreCase("SUBTYPE"))
				attr = "小类";
			if(value==null)
				returnMsg.append(wtDoc.getIdentity() + " 的" + attr + " 必填属性没有设置，请修改后再提交流程!\n");
			
		}
		String rs = returnMsg.toString();
		return "".equals(rs) ? WorkflowValidationUtil4Dwg.VALIDATE_OK : rs;
	}
	
	/**
	 * 校验EPM文档，部件属性
	 * @param wtDoc
	 * @return
	 * @throws WTException
	 */
	public static String validateEPMPartAttribute(WTObject wtobject) throws WTException {
		String checkDocAttribute = "";
		StringBuffer returnMsg = new StringBuffer("");
		try {
			checkDocAttribute = ASESNumberUtil.getStrFromProperties("ext.ases.workflow.checkEPMPartAttribute", "ext.ases.ases");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 获得文档要检查的属性
		
		String docAttribute[] = checkDocAttribute.split(",");
		for(int i=0;i<docAttribute.length;i++){
			String attr = docAttribute[i];
			String value = IBAHelper.getIBAStringValue(wtobject, attr);
			if(attr.equalsIgnoreCase("PHASE_CODE"))
				attr = "阶段标记";
			else if(attr.equalsIgnoreCase("SUBTYPE"))
				attr = "小类";
			if(value==null)
				returnMsg.append(wtobject.getIdentity() + " 的" + attr + " 必填属性没有设置，请修改后再提交流程!\n");
			
		}
		String rs = returnMsg.toString();
		return "".equals(rs) ? WorkflowValidationUtil4Dwg.VALIDATE_OK : rs;
	}
	
	
	/**
	 * 基线归档时候要检测基线内的数据
	 * @param baseline
	 * @return
	 */
	public static String BaselineAchiveValidation(ManagedBaseline baseline) {
		// 检查baseline 中的数据是否是已批准而且是checkin状态
		Vector baselineObjVector = BaselineHelper.getBaselinable(baseline);
		
		try {
			for (Object baselineObj : baselineObjVector) {
				RevisionControlled baselineWTObj = (RevisionControlled) baselineObj;
				if (!baselineWTObj.getLifeCycleState().equals(State.toState("APPROVED")) || WorkInProgressHelper.isCheckedOut(baselineWTObj)) {
					return "请检查：" + baselineWTObj.getIdentity() + " 状态。";
				}
			}
		} catch (WTException e) {
			logger.error(e.getLocalizedMessage());
			return e.getLocalizedMessage();
		}
		return WorkflowValidationUtil4Dwg.VALIDATE_OK;
	}
	
	public static String validataTask(Object pbo) throws WTException{
		WTPrincipal wtprincipal = SessionHelper.manager.getPrincipal();
		if(pbo instanceof ProcessEnvelope){
			ProcessEnvelope pe = (ProcessEnvelope)pbo;
			List dataList = EnvelopeHelper.service.getAllMembers(pe);
			//modified by zf 20100420			
			//成套件批量签审时，必须携带明细表
			boolean isPackageProcess = false;//是否成套件签审流程
			boolean hasMx = false;//是否包含明细表
			String peType = TypeIdentifierHelper.getType(pe).toString();			
			if (peType.indexOf("casc.sast.149.APPROVEFORM") != -1) {
				RevisionControlled topObj = EnvelopeHelper.service.getTopObject(pe);
				if(topObj !=null)
					logger.debug("topObj:"+topObj.getIdentity());
				if ((topObj != null) && (topObj instanceof WTPart)){
					String isPackagedPart = IBAHelper.getIBAStringValue(topObj, "SETMARK");
					if(isPackagedPart.equals("是"))
						isPackageProcess = true;
				}				
			}
			StringBuffer returnMsg = new StringBuffer("");
			for(int i = 0 ; i < dataList.size() ; i++){
				WTObject wto = (WTObject)dataList.get(i);
				if(wto instanceof WTDocument){
					WTDocument wtd = (WTDocument)wto;
					String s = WorkflowValidationUtil4Dwg.validateDocAttribute(wtd);
					if(!s.equals(WorkflowValidationUtil4Dwg.VALIDATE_OK))
						returnMsg.append(s);
					if(!wtd.getModifierName().equals(wtprincipal.getName()))
						returnMsg.append(wtd.getIdentity() + " 不是您修改的文档，请移除再提交!\n");
				}
				
				if((wto instanceof WTPart)||(wto instanceof EPMDocument)){
					WTObject wtobject = (WTObject)wto;
					String s = WorkflowValidationUtil4Dwg.validateEPMPartAttribute(wtobject);
					if(!s.equals(WorkflowValidationUtil4Dwg.VALIDATE_OK))
						returnMsg.append(s);
					if(wtobject instanceof WTPart){
						if(!((WTPart)wtobject).getModifierName().equals(wtprincipal.getName()))
							returnMsg.append(((WTPart)wtobject).getIdentity() + " 不是您修改的部件，请移除再提交!\n");
					}
					if(wtobject instanceof EPMDocument){
						if(!((EPMDocument)wtobject).getModifierName().equals(wtprincipal.getName()))
							returnMsg.append(((EPMDocument)wtobject).getIdentity() + " 不是您修改的模型或图纸，请移除再提交!\n");
					}
				}
				//ywu 2011.1.18 增加提交签审时生命周期状态的校对
				if(wto instanceof LifeCycleManaged)
				{
					if(!WorkflowValidationUtil4Dwg.validateLifecycleState((LifeCycleManaged)wto));
					{
						returnMsg.append("只有正在工作/修改中状态的对象才能提交签审："+wto.getIdentity()+",请移除再提交\n");						
					}
				}
				
				//增加重复投入签审包校验
				if(!WorkflowValidationUtil4Dwg.validateDuplicateInProcEnv(wto))
				{
					returnMsg.append("对象已在另一个签审包中，不能提交签审："+wto.getIdentity()+",请移除再提交\n");
				}
			}
			String rs = returnMsg.toString();
			if(!("".equals(rs)))
				return rs;
			
			for(int i = 0 ; i < dataList.size() ; i++){
				WTObject wto = (WTObject)dataList.get(i);
//				if(wto instanceof WTDocument){
//					WTDocument wtd = (WTDocument)wto;
//					System.out.println("wtd name is "+wtd.getName() +" version is "+wtd.getVersionIdentifier().getValue()+"."+wtd.getIterationIdentifier().getValue());
//				}
				if (WorkInProgressHelper.isCheckedOut((Workable)wto)) {
					return wto.getIdentity() + "被检出，请检入后再提交!";
				}
				if(wto instanceof WTDocument){
					String docSubType = IBAHelper.getIBAStringValue(wto, "SUBTYPE");
					if ("明细表".equals(docSubType))
						hasMx = true;
				}
				String value = validateRepresentable(wto);
				if(!value.equals(WorkflowValidationUtil4Dwg.VALIDATE_OK)){
					return value;
				}
			}
			//logger.debug("isPackageProcess:"+isPackageProcess);
			//logger.debug("haxMx:"+hasMx);
			//if (isPackageProcess && !hasMx)
				//return "成套件签审时需带有明细表，请将明细表添加到签审单中!";
		}
		
		return WorkflowValidationUtil4Dwg.VALIDATE_OK;
	}
		
	public static String validataOutDocTask(Object pbo) throws WTException{
		if(pbo instanceof ProcessEnvelope){
			ProcessEnvelope pe = (ProcessEnvelope)pbo;
			List dataList = EnvelopeHelper.service.getAllMembers(pe);
			StringBuffer returnMsg = new StringBuffer("");
			for(int i = 0 ; i < dataList.size() ; i++){
				WTObject wto = (WTObject)dataList.get(i);
				if (WorkInProgressHelper.isCheckedOut((Workable)wto)) {
					returnMsg.append(wto.getIdentity() + " 被检出，请检入后再提交!\n");
				}
			}
			String rs = returnMsg.toString();
			if(!("".equals(rs)))
				return rs;
		}
		return WorkflowValidationUtil4Dwg.VALIDATE_OK;
	}
	
	
	//ywu 2010.11.19 提交/终止前检查是否检出
	public static String validateCheckout(Object pbo) throws WTException{
		if(pbo instanceof ProcessEnvelope){
			ProcessEnvelope pe = (ProcessEnvelope)pbo;
			List dataList = EnvelopeHelper.service.getAllMembers(pe);
			StringBuffer returnMsg = new StringBuffer("");
			for(int i = 0 ; i < dataList.size() ; i++){
				WTObject wto = (WTObject)dataList.get(i);
				if (WorkInProgressHelper.isCheckedOut((Workable)wto)) {
					returnMsg.append(wto.getIdentity() + " 被检出，请检入后再完成!\n");
				}
			}
			String rs = returnMsg.toString();
			if(!("".equals(rs)))
				return rs;
		}else if(pbo instanceof WTChangeOrder2)
		{
			List<RevisionControlled> caAfterItems = ChangeHelper.getChangeResultItem((WTChangeOrder2)pbo);
			StringBuffer returnMsg = new StringBuffer("");
			for(int i = 0 ; i < caAfterItems.size() ; i++){
				WTObject wto = (WTObject)caAfterItems.get(i);
				boolean ischeckout = wt.vc.wip.WorkInProgressHelper.isCheckedOut((wt.enterprise.RevisionControlled)wto);
				if(ischeckout)
					returnMsg.append(wto.getIdentity() + " 被检出，请检入后再完成!\n");
			}
			String rs = returnMsg.toString();
			if(!("".equals(rs)))
				return rs;			
		}
		return WorkflowValidationUtil4Dwg.VALIDATE_OK;
	}
	
	public static void main(String[] args) throws WTRuntimeException, WTException {
		//OR%3Awt.epm.EPMDocument%3A185840
		//VR:wt.epm.EPMDocument:193726
//		Representable testEcn = (Representable) new ReferenceFactory().getReference("OR:wt.epm.EPMDocument:184053").getObject();// A1 派生，无效
//		Representable testEcn = (Representable) new ReferenceFactory().getReference("OR:wt.epm.EPMDocument:184712").getObject();// A3 派生, 无效
//		Representable testEcn = (Representable) new ReferenceFactory().getReference("VR:wt.epm.EPMDocument:184049").getObject();// A3 派生，无效
//		Representable testEcn = (Representable) new ReferenceFactory().getReference("OR:wt.epm.EPMDocument:187567").getObject();// B3 派生, 有效
//		Representable testEcn = (Representable) new ReferenceFactory().getReference("OR:wt.epm.EPMDocument:187297").getObject();// B2 none
//		Representable testEcn = (Representable) new ReferenceFactory().getReference("OR:wt.epm.EPMDocument:192850").getObject();// C1 B3 复制
//		Representable testEcn = (Representable) new ReferenceFactory().getReference("VR:wt.epm.EPMDocument:193726").getObject();// DRW
		Representable testEcn = (Representable) new ReferenceFactory().getReference("VR:wt.doc.WTDocument:208649").getObject();// DRW
		
		WorkflowValidationUtil4Dwg.validateRepresentableDocShenQian((WTDocument)testEcn);
	}

}
