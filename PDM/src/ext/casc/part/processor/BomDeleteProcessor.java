package ext.casc.part.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.icu.text.MessageFormat;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;

import ext.ases.envelope.EnvelopeMemberLink;
import ext.casc.part.bean.PartUsageBean;
import wt.conflict.ConflictResolution;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTKeyedMap;
import wt.fc.collections.WTSet;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pom.POMInitException;
import wt.pom.PersistenceException;
import wt.pom.PersistentObjectManager;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlConflictType;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlResolutionType;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.ViewHelper;


/**
 * 用于部件操作菜单中Bom删除功能提交处理
 * @author Liluwen
 * @date 2025年8月14日上午9:57:59
 */
public class BomDeleteProcessor extends DefaultObjectFormProcessor{
	private static Logger LOGGER=Logger.getLogger(BomDeleteProcessor.class);
	
	/**
	 * 部件正在工作状态
	 */
	private static final String STATUS_INWORK="INWORK";
	
	/**
	 * Design视图
	 */
	private static final String VIEW_DESIGN="Design";
	
	/**
	 * Manufacture视图
	 */
	private static final String VIEW_MANUFACTURING="Manufacturing";
	
	/**
	 * 部件大版本space
	 */
	private static final String VERSION_SPACE="space";
	
	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> beans) throws WTException {
		FormResult result = super.doOperation(clientData, beans);
		result.setStatus(FormProcessingStatus.SUCCESS);
		
		NmOid nmoid=clientData.getActionOid();
    	Object targetPart=nmoid.getRefObject();
    	String checkMessage="";
    	if(targetPart instanceof WTPart) {
    		WTPart wtPart=(WTPart)targetPart;
    		List<PartUsageBean> subPartUsageList=null;
    		try {
				subPartUsageList=queryBomSubParts(wtPart);
				checkMessage=deleteRuleValidation(wtPart,subPartUsageList);
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			}
    		
    		Locale locale=SessionHelper.getLocale();
    		if(StringUtils.isNotBlank(checkMessage)) {
    			ArrayList localArrayList = new ArrayList();
    			WTMessage title = new WTMessage(
    					"ext.casc.part.resource.PartResource",
    					"EXECUTE_FAILURE", (Object[]) null);//创建成功
    			FeedbackMessage localFeedbackMessage = new FeedbackMessage(
    					FeedbackType.FAILURE, locale, title.getLocalizedMessage(locale),(ArrayList) null,checkMessage);
    			result.addFeedbackMessage(localFeedbackMessage);
    		} else {
    			executeDeleteBom(wtPart,subPartUsageList);
    			ArrayList localArrayList = new ArrayList();
    			WTMessage title = new WTMessage(
    					"ext.casc.part.resource.PartResource",
    					"EXECUTE_SUCCESS", (Object[]) null);//创建成功
    			FeedbackMessage localFeedbackMessage = new FeedbackMessage(
    					FeedbackType.SUCCESS, locale, title.getLocalizedMessage(locale),(ArrayList) null,checkMessage);
    			result.addFeedbackMessage(localFeedbackMessage);
    		}
    	}
		
		return result;
	}

	/** 
	  * @Description: 执行BOM删除
	  * 删除规则如下：
	  * a) 如果满足删除校验规则，则删除所选部件的对应space.0版本。
	  * b) 如果选择的为Design视图部件，则删除space.0（Design）版本，Manfacturing不影响 。
	  * c) 如果选择的为Manufacture视图部件，则删除Manufacturer视图所有小版本，Design版本不受影响。
	  * d) 如果选择的部件关联了下级部件，则与所选部件同步删除，并往下级部件进行遍历处理；
	  * b)	如果选择的部件关联了上级部件，则删除与上级部件的关联关系。
	  * @date 2025年8月14日下午5:41:17
	  * @author Jacky
	  * @param wtPart  
	  * @return 
	 * @throws PersistenceException 
	 * @throws POMInitException 
	 * @throws WTException 
	 * @throws WTPropertyVetoException 
	 * @throws VersionControlException 
	*/
	private void executeDeleteBom(WTPart wtPart, List<PartUsageBean> subPartUsageList)
			throws POMInitException, PersistenceException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false); //取消权限验证
		Transaction transaction=null;
		boolean canCommit = false;
		if(!PersistentObjectManager.getPom().isTransactionActive()) {
			transaction=new Transaction();
			canCommit=true;
			transaction.start();
		} 
		
		String partView = wtPart.getViewName();
		try {
			if (VIEW_DESIGN.equals(partView)) {
				deleteManufacturingPart(wtPart);
				deletePart(wtPart);
				deleteSubParts(subPartUsageList);	
			} 
			if(canCommit) {
				transaction.commit();
				transaction = null;
			}
		} catch (WTPropertyVetoException | WTException e) {
			e.printStackTrace();
		} finally {
			if(canCommit) {
				if (transaction != null) {
					transaction.rollback();
				}
			}
			SessionServerHelper.manager.setAccessEnforced(flag); //恢复权限验证
			
		}
	}

	/** 
	  * @Description: 逐层删除部件
	  * @date 2025年8月20日下午10:09:53
	  * @author Liluwen
	  * @param subPartUsageList
	  * @throws VersionControlException
	  * @throws WTPropertyVetoException
	  * @throws WTException  
	  * @return 
	*/
	private void deleteSubParts(List<PartUsageBean> subPartUsageList) throws VersionControlException, WTPropertyVetoException, WTException {
		if(CollectionUtils.isNotEmpty(subPartUsageList)) {
			for(PartUsageBean bean:subPartUsageList) {
				WTPart parent=bean.getPart();
				WTPart childPart=bean.getChildPart();
				String partView = childPart.getViewName();
				if (VIEW_DESIGN.equals(partView)) {
					boolean hasDiffParent=checkPartHasDiffParent(parent,childPart);
					if(!hasDiffParent) {
						deleteManufacturingPart(childPart);
						deletePart(childPart);
					}
				} 
				List<PartUsageBean> list=bean.getSubPartUsageList();
				deleteSubParts(list);
			}
		}
	}

	/** 
	  * @Description: 根据视图和编号获取最新版本部件
	  * @date 2025年11月7日下午7:16:21
	  * @author Liluwen
	  * @param partNumber
	  * @param view
	  * @return
	  * @throws WTException  
	  * @return 
	*/
	public WTPart getLatestPartByNumber(String partNumber, String view) throws WTException {
		if (partNumber == null || partNumber.length() == 0)
			return null;

		QuerySpec qs = new QuerySpec(WTPart.class);
		SearchCondition scNumber = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL,
				partNumber.toUpperCase());
		SearchCondition scLatestIteration = new SearchCondition(WTPart.class, WTAttributeNameIfc.LATEST_ITERATION,
				SearchCondition.IS_TRUE);
		qs.appendWhere(scNumber,new int[]{0});
		qs.appendAnd();
		qs.appendWhere(scLatestIteration,new int[]{0});
		if (view != null && view.length() > 0) {
			qs.appendAnd();
			SearchCondition scView = new SearchCondition(WTPart.class, "view.key", SearchCondition.EQUAL,
					PersistenceHelper.getObjectIdentifier(ViewHelper.service.getView(view)));
			qs.appendWhere(scView,new int[]{0});
		}
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr != null && qr.hasMoreElements())
			qr = (new LatestConfigSpec()).process(qr);

		if (qr != null && qr.hasMoreElements())
			return (WTPart) qr.nextElement();
		return null;
	}
	
	/** 
	  * @Description: 查询部件的Manufacturing视图版本后删除,删除所有Manufacturing版本
	  * @date 2025年11月7日下午7:09:11
	  * @author Liluwen
	  * @param childPart  
	  * @return 
	 * @throws WTException 
	 * @throws WTPropertyVetoException 
	*/
	private void deleteManufacturingPart(WTPart childPart) throws WTException, WTPropertyVetoException {
		WTPart mPart=getLatestPartByNumber(childPart.getNumber(),VIEW_MANUFACTURING);
		if(mPart!=null) {
			PersistenceHelper.manager.delete(mPart);
		}
	}

	/** 
	  * @Description: 检查部件除了当前父部件外是否还有其它父部件
	  * @date 2025年9月11日下午8:22:34
	  * @author Liluwen
	  * @param parent
	  * @param childPart
	  * @return  
	  * @return 
	 * @throws WTException 
	*/
	private boolean checkPartHasDiffParent(WTPart parent, WTPart childPart) throws WTException {
		QueryResult linkQR2 = WTPartHelper.service.getUsedByWTParts(childPart.getMaster());
		while (linkQR2.hasMoreElements()) {
			WTPart parentPart = (WTPart) linkQR2.nextElement();
			if(!parentPart.getNumber().equals(parent.getNumber())) {
				LOGGER.debug(MessageFormat.format("checkPartHasDiffParent.parent {0}.diffParent {1}", parent.getNumber(),parentPart.getNumber()));
				return true;
			}
			
		}
		return false;
	}

	/** 
	  * @Description: 如果选择的为Design视图部件，则删除space.0（Design）版本，Manfacturing不影响 。
	  * @date 2025年8月20日下午10:00:08
	  * @author Liluwen
	  * @param wtPart
	  * @throws VersionControlException
	  * @throws WTPropertyVetoException
	  * @throws WTException  
	  * @return 
	*/
	public void deletePart(WTPart wtPart) throws VersionControlException, WTPropertyVetoException, WTException {
		WTCollection wtCollection=new WTArrayList();
		wtCollection.add(wtPart);
		ConflictResolution conflict=new ConflictResolution(VersionControlConflictType.LATEST_ITERATION_DELETE,VersionControlResolutionType.ALLOW_LATEST_ITERATION_DELETE);
		ConflictResolution[] conflictResolution=new ConflictResolution[] {conflict};
		VersionControlHelper.service.deleteIterations(wtCollection, conflictResolution);
	}
	/** 
	  * @Description: 对部件和部件所有的下级子件做删除规则校验
	   	删除规则校验,校验规则如下：
	    a)	只能对“正在工作”状态的部件执行【BOM删除】操作，如果为其他状态，则提示“只能对“正在工作”状态的部件进行删除操作!”。
		b)	只能对Design视图的部件执行【BOM删除】操作，如果为其他视图，则提示“只能对Design的部件进行删除操作!”。
		d)	如果部件已关联文档，则提示“部件已关联文档，无法进行删除操作！”。
		e)	如果部件已关联CAD文档，则提示“部件已关联CAD文档，无法进行删除操作！”。
		f)	如果部件已关联签审包，则提示“部件已关联签审包，无法进行删除操作！”。
		g)	Design视图部件只能删除space.0版本的，如果为Design视图部件其他版本则提示“非Design视图部件space.0版本，无法进行删除操作！”
	  * @date 2025年8月14日上午10:48:36
	  * @author Jacky
	  * @param wtPart
	  * @return  
	  * @return 
	 * @throws WTException 
	*/
	private String deleteRuleValidation(WTPart wtPart, List<PartUsageBean> subPartUsageList) throws WTException {
		// 检查当前部件的删除规则
		StringBuilder checkData = checkSinglePart(wtPart);
		QueryResult	linkQR2=WTPartHelper.service.getUsedByWTParts(wtPart.getMaster());
		if(linkQR2.hasMoreElements()) {
			checkData.append("部件").append(wtPart.getNumber()).append("已被引用，无法进行删除操作！ ");
		}
		
		if (CollectionUtils.isNotEmpty(subPartUsageList)) {
			//检查当前部件所有层级子件是否符合删除规则
			checkSubPartDeleteRuleValidation(checkData, subPartUsageList);
		}

		return checkData.toString();
	}

	/** 
	  * @Description: 递归第二层到最底层BOM部件删除规则校验
	  * @date 2025年8月14日下午6:09:20
	  * @author Liluwen
	  * @param checkData
	  * @param partUsageList  
	  * @return 
	*/
	private void checkSubPartDeleteRuleValidation(StringBuilder checkData, List<PartUsageBean> partUsageList) {
		for(PartUsageBean subPartUsage:partUsageList) {
			StringBuilder singleCheckData=checkSinglePart(subPartUsage.getChildPart());
			if(StringUtils.isNotBlank(singleCheckData)) {
				checkData.append(singleCheckData);
			}
			
			List<PartUsageBean> subUsageList=subPartUsage.getSubPartUsageList();
			if(CollectionUtils.isNotEmpty(subUsageList)) {
				checkSubPartDeleteRuleValidation(checkData,subUsageList);
			}
		}
	}

	/** 
	  * @Description: 递归查询BOM中的所有子部件
	  * @date 2025年8月14日下午2:51:54
	  * @author Jacky
	  * @param wtPart
	  * @return  
	  * @return 
	 * @throws WTException 
	 * @throws WTPropertyVetoException 
	*/
	private List<PartUsageBean> queryBomSubParts(WTPart wtPart) throws WTException, WTPropertyVetoException {
		List<PartUsageBean> list=new ArrayList<PartUsageBean>();
		QueryResult sublinkQR = WTPartHelper.service.getUsesWTPartMasters(wtPart);
		while (sublinkQR.hasMoreElements()) {
			WTPartUsageLink usageLink = (WTPartUsageLink) sublinkQR.nextElement();
			WTPartMaster childPartMaster = (WTPartMaster) usageLink.getRoleBObject();
			WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec();
			configSpec.setView(wt.vc.views.ViewHelper.service.getView(wtPart.getView().getName()));

			WTPart childPart = null;
			for (QueryResult qr = ConfigHelper.service.filteredIterationsOf(childPartMaster, configSpec); qr
					.hasMoreElements();) {
				childPart = (WTPart) qr.nextElement();
			}
			
			PartUsageBean bean=new PartUsageBean();
			bean.setPart(wtPart);
			bean.setChildPart(childPart);
			bean.setWtPartUsageLink(usageLink);
			List<PartUsageBean> subPartUsageList=queryBomSubParts(childPart);
			if(CollectionUtils.isNotEmpty(subPartUsageList)) {
				bean.setSubPartUsageList(subPartUsageList);
			}
			
			list.add(bean);
		}
		return list;
	}


	/** 
	  * @Description: 检查单个部件是否符合删除规则
	  * @date 2025年8月14日下午5:45:06
	  * @author Jacky
	  * @param wtPart
	  * @return  
	  * @return 
	*/
	private StringBuilder checkSinglePart(WTPart wtPart) {
		StringBuilder data = new StringBuilder();
		String status = wtPart.getState().toString();
		String partView = wtPart.getViewName();
		if (!VIEW_DESIGN.equals(partView) ) {
			data.append("部件").append(wtPart.getNumber()).append("视图为\"").append(partView)
					.append("\",只能对Design的部件进行删除操作!");
		}
	
		boolean hashRelatedDoc = checkPartHasRelatedDocument(wtPart);
		if (hashRelatedDoc) {
			data.append("部件").append(wtPart.getNumber()).append("已关联文档，无法进行删除操作！ ");
		}
		boolean hasRelatedCAD = checkPartHasRelatedCAD(wtPart);
		if (hasRelatedCAD) {
			data.append("部件").append(wtPart.getNumber()).append("已关联CAD文档，无法进行删除操作！ ");
		}
		boolean hasRelatedEnvelope = checkPartHasRelatedEnvelope(wtPart);
		if (hasRelatedEnvelope) {
			data.append("部件").append(wtPart.getNumber()).append("已关联签审包，无法进行删除操作！ ");
		}

		if (VIEW_DESIGN.equals(partView)) {
			String masterVersion = wtPart.getVersionIdentifier().getValue();
			String seriesCodeStr = wtPart.getIterationIdentifier().getValue();
			if (VERSION_SPACE.equals(masterVersion)) {
				if (!"0".equals(seriesCodeStr)) {
					data.append("部件").append(wtPart.getNumber()).append("非Design视图部件space.0版本，无法进行删除操作! ");
				}
			}
		}

		return data;
	}

	/** 
	  * @Description: 检查部件是否包含关联的签审包
	  * @date 2025年8月14日下午1:13:08
	  * @author Jacky
	  * @param wtPart
	  * @return  
	  * @return 
	*/
	private boolean checkPartHasRelatedEnvelope(WTPart wtPart) {
		try {
			QueryResult qr = PersistenceHelper.manager.navigate(wtPart, "theProcessEnvelope",
			        EnvelopeMemberLink.class, false);
			if(qr.hasMoreElements()) {
				return true;
			}
			
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 
	  * @Description: 检查部件是否含有关联的CAD文档
	  * @date 2025年8月14日下午1:10:49
	  * @author Jacky
	  * @param wtPart
	  * @return  
	  * @return 
	*/
	private boolean checkPartHasRelatedCAD(WTPart wtPart) {
		try {
			QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(wtPart);
			if(qr.hasMoreElements()) {
				return true;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 
	  * @Description: 检查部件是否有关联的文档
	  * @date 2025年8月14日下午1:09:30
	  * @author Jacky
	  * @param wtPart
	  * @return  
	  * @return 
	*/
	private boolean checkPartHasRelatedDocument(WTPart wtPart) {
		try {
			//查询部件描述文档 
			QueryResult qr = WTPartHelper.service.getDescribedByDocuments(wtPart);
			if(qr.hasMoreElements()) {
				return true;
			}
			
			//查询部件参考文档
			WTArrayList wtarrayList = new WTArrayList(); 
			wtarrayList.add(wtPart);
			WTKeyedMap keyMap = wt.part.PartDocHelper.service.getAssociatedReferenceDocuments(wtarrayList);
			if(keyMap!=null&&keyMap.size()>0) {
				return true;
			}
			
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 
	 * @Description:检查部件是否包含工艺任务
	  * @date 2025年8月14日下午1:05:56
	  * @author Jacky
	  * @param wtPart
	  * @return  
	  * @return 
	*/
	private boolean checkPartHasRelatedCraft(WTPart wtPart) {
		try {
			WTSet wtSet=MPMProcessPlanHelper.service.getAllProcessPlans(wtPart);
			if(CollectionUtils.isNotEmpty(wtSet)) {
				return true;
			}
			QueryResult qr=MPMProcessPlanHelper.service.getPartToProcessPlanLinks(wtPart);
			if(qr.hasMoreElements()) {
				return true;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}
}
