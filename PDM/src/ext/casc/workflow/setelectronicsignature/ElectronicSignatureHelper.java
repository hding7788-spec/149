package ext.casc.workflow.setelectronicsignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import ext.casc.change.ChangeHelper;
import ext.casc.util.WCUtil;
import ext.csc.utilities.CSCProcess;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.maturity.PromotionNotice;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.org.electronicIdentity.ElectronicIdentification;
import wt.org.electronicIdentity.ElectronicSignature;
import wt.org.electronicIdentity.ElectronicallySignable;
import wt.org.electronicIdentity.SignElectronicSignature;
import wt.org.electronicIdentity.UserElectronicIDLink;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.engine.WfProcess;

public class ElectronicSignatureHelper {	
	public static void setElectronicSignatureToPBORelateObjects(WfProcess process){
		try {
//			System.out.println("Processing ElectronicSignatureCopier of Workflow " + process.getName());
			
	        WTPrincipalReference userRef = SessionHelper.manager.getPrincipalReference();
	        WTUser user = (WTUser)userRef.getObject();

			if(!checkUserElectronicSignature(user)){
				newUserElectronicSignature(user);		//若当前用户无电子签名信息，则需要添加签名，否则在系统为对象设置电子签名时会自动添加“--”
			}
	        
			ArrayList<HashMap<String, String>> arrayRoutingHistory = CSCProcess.getProcessRoutingHistory(process);
						
			ReferenceFactory rf = new ReferenceFactory();
			WTObject pbo = (WTObject) process.getContext().getValue(WfDefinerHelper.PRIMARY_BUSINESS_OBJECT);

			ArrayList aRelatedObject = getAssociatedObjects(pbo);
			for (int i = 0; i < aRelatedObject.size(); i++) {
				WTObject relatedObject = (WTObject)aRelatedObject.get(i);
				for (int j = 0; j < arrayRoutingHistory.size(); j++) {
					HashMap<String, String> hmRoutingHistory = (HashMap<String, String>)arrayRoutingHistory.get(j);
					String comments = hmRoutingHistory.get(CSCProcess.WORK_COMMENTS);
					String instructions = hmRoutingHistory.get(CSCProcess.PROCESS_NAME) + "_" + hmRoutingHistory.get(CSCProcess.WORK_NAME);
					String role = hmRoutingHistory.get(CSCProcess.WORK_ROLE);
					String vote = hmRoutingHistory.get(CSCProcess.WORK_VOTE);
					SignElectronicSignature.setObjectsElectronicSignature(user, (ElectronicallySignable)relatedObject, comments, instructions, role, vote);
//					System.out.println(comments + " - " + instructions + " - " + role + " - " + vote);
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}
	
	   /**
     * 得到与 Document 或者 Part 所关联的对象,Dependcy Document, Describe Document 或者 Reference Document
     * 若对象为Part则返回DescribeDocument以及最新版的ReferenceDocument
     * 若对象为Document则返回DependencyDocument以及DescribedByPart
     * 
     * @param wtobject
     * @return
     * @throws WTException 
     */
    public static ArrayList getAssociatedObjects(WTObject wtobject) throws WTException{
        ArrayList result = new ArrayList();
        ArrayList objects = new ArrayList();
        
        if(wtobject instanceof WTPart){
            objects = WCUtil.getRelatedWTObjectByPart((WTPart) wtobject);
        }else if(wtobject instanceof WTDocument){
            objects = WCUtil.getRelatedWTObjectByDoc((WTDocument) wtobject);
        }
        result = objects;
        return result;
    }
	
	/**
	 * 如果当前工作流环节定义了布尔型变量setRelatedObjectSignature，且值为true，则在完成活动时根据PBO类型不同，将相关签名信息设置到相关对象当中；<br>
	 * <br>
	 * 升级包在跑流程时需要更新相关对象电子签名信息：<br>
	 * 1. 打包签审需要将升级包的所关联对象状态与升级包状态同步<br>
	 * 2. 打包签审的流程中签名信息要设置到PBO本身及所关联的对象当中<br>
	 * 
	 * @param user			当前用户
	 * @param wtobject		需要被写入电子签名的对象
	 * @param comments		流程节点意见
	 * @param instructions	流程节点描述
	 * @param role			流程角色
	 * @param vote			流程节点投票
	 */
	public static void setElectronicSignatureToObjectRelateObjects(WTUser user, WTObject wtobject, String comments, String instructions, String role, String vote){		
		try{
			if(!checkUserElectronicSignature(user)){
				//no signature found!
				newUserElectronicSignature(user);		//若当前用户无电子签名信息，则需要添加签名，否则在系统为对象设置电子签名时会自动添加“--”
			}

			//对对象本身进行签名设置
			if(wtobject instanceof ElectronicallySignable){
				SignElectronicSignature.setObjectsElectronicSignature(user, (ElectronicallySignable)wtobject, comments, instructions, role, vote);
//				CSCDebug.outDebugInfo("Setting ElectronicSignature to Object " 
//						+ wtobject.toString() + 
//						" | Information="  + user.getName() + "-" + comments + " - " + instructions + " - " + role + " - " + vote);
			}
			//对对象关联物进行签名设置
			if(wtobject instanceof WTChangeOrder2){
				WTChangeOrder2 ecn = (WTChangeOrder2)wtobject;
				ArrayList aRelatedObject1 = ChangeHelper.getChangeAffectItem(ecn);
				ArrayList aRelatedObject2 = ChangeHelper.getChangeResultItem(ecn);
				
				ArrayList aRelatedObject = new ArrayList();
				aRelatedObject.addAll(aRelatedObject1);
				aRelatedObject.addAll(aRelatedObject2);
				for (int i = 0; i < aRelatedObject.size(); i++) {
					WTObject relatedObject = (WTObject)aRelatedObject.get(i);
					if(relatedObject instanceof ElectronicallySignable){
						SignElectronicSignature.setObjectsElectronicSignature(user, (ElectronicallySignable)relatedObject, comments, instructions, role, vote);
//						CSCDebug.outDebugInfo("Setting ElectronicSignature to Related Object " 
//								+ relatedObject.toString() + 
//								" | Information=" + user.getName() + "-" + comments + " - " + instructions + " - " + role + " - " + vote);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean checkUserElectronicSignature(WTUser user) throws QueryException, WTException{
		Hashtable hash = OrganizationServicesHelper.manager.getUserElectronicIdentification(false, user);
		if (hash == null){
			return false;
		}
		return true;
	}
	
	private static void newUserElectronicSignature(WTUser user) throws WTException, WTPropertyVetoException{
//		CSCDebug.outDebugInfo("User " + user.toString() + "'s Signature doesn't found, Creating... ");
		ElectronicSignature signature = null;
		signature = ElectronicSignature.newElectronicSignature();
		signature.setName(user.getFullName());
		signature.setActive(true);
		signature = (ElectronicSignature) PersistenceHelper.manager
				.save(signature);
		signature = (ElectronicSignature) PersistenceHelper.manager
				.refresh(signature);
		UserElectronicIDLink userIDLink = (UserElectronicIDLink) PersistenceHelper.manager
				.save(UserElectronicIDLink.newUserElectronicIDLink(user,
						(ElectronicIdentification) signature));
	}
	
//	public static void main(String[] args) throws WTException {
//		String obid = args[0];
//		TaskHelper taskhelper = new TaskHelper();
//		WTObject wtobject = (WTObject) taskhelper.getObjectFromUfid(obid);
//		
//		String username = args[1];
//		String comments = args[2];
//		String instructions = args[3];
//		String role = args[4];	
//		String vote = args[5];
//		
//		WTUser user = CSCPrincipal.getUserByName(username);
//		setElectronicSignatureToObjectRelateObjects(user, wtobject, comments, instructions, role, vote);
//		//setElectronicSignatureToObjectRelateObjects(user, wtobject, "测试Signature7", "测试流程节点7", "审核者8", "同意");
//	}
}
