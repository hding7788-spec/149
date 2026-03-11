package ext.casc.workflow.workflowtask;

import java.util.HashMap;
import java.util.List;

import ext.casc.util.RemoteUtility;


import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.workflow.WfException;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfTally;


public class ResultExpression {
	
	public static boolean ifPassHuiQian(String vote){
		if(vote!=null){
			if(vote.indexOf("reject")>=0){
				return false;
			}
			else
					return true;
			
		}
		else{
			return true;
		}
	}
	
	public static String oneVoteDown(ObjectReference arg0, String arg1, String arg2){
//		System.out.println("==== oneVoteDown Info begin ====");
//		System.out.println("arg0 "+arg0);
//		System.out.println("arg1 "+arg1);
//		System.out.println("arg2 "+arg2);
//		System.out.println("Try WfTally.all(arg0, arg1, arg2)");
//		System.out.println("..........................................");
		try {
			return WfTally.all(arg0, arg1, arg2);
		} catch (WfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static boolean havaMembers(ObjectReference self, String[] rolestrList) {
//		System.out.println("In RoleList-------Self is : "+self);
		boolean havaMembers = false;
		boolean printCtrl = true;
		for(int count=0 ; count<rolestrList.length ; count++){
			try {
				
					Role role = Role.toRole(rolestrList[count]);
					WfProcess process = (WfProcess)self.getObject();
					Team team = (Team) process.getTeamId().getObject();
	
					HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
					
					List userList = (List) rolePrincipalListMap.get(role);
					
					if(userList!=null && userList.size()>0){
//						if(printCtrl){
//							System.out.println(">>>>>>>>>>>>>>>  Role is " + role);
//							for (int i = 0; userList != null && i < userList.size(); i++) {
//								WTUser user = (WTUser) ((WTPrincipalReference) userList.get(i)).getObject();
//								System.out.println("    >>>>>>>>>>>>>>>  user is " + user.getName());
//								
//							}
//							
//						}
						havaMembers = true;
						break;
					}
					
					
						//TeamHelper.service.deleteRolePrincipalMap(role, user, team);
						
					
					
	//				Iterator it1 = roleUser.keySet().iterator();
	//				while (it1.hasNext()) {
	//					String roleName = (String) it1.next();
	//					System.out.println("begin to del role user role name is " + roleName);
	//					
	//
	//				}
	//				
	//				Enumeration mems = team.getPrincipalTarget(role);
	//				if (mems.hasMoreElements())
	//					havaMembers = true;
					
					
//					if(printCtrl){
//						System.out.println(" === MEMBER INFO === havaMembers " +havaMembers);
//						System.out.println(" === MEMBER INFO === rolestr " + rolestrList[count]);
//						System.out.println(" === MEMBER INFO === role " + role);
//						System.out.println(" === MEMBER INFO === self " + self);
//						System.out.println(" === MEMBER INFO === team " +team);
//	
//						
//					}
				
				
	
			} catch (Exception _wte) {
				_wte.printStackTrace();
			}
		}
		
//		if(printCtrl){
//			System.out.println(" === MEMBER INFO ===  havaMembers " +havaMembers);
//			System.out.println(" === METHOD END === ");
//			
//		}
		
		return havaMembers;
	}
	
	public static boolean havaMembers(ObjectReference self, String rolestr) {
		boolean havaMembers = false;
		boolean printCtrl = true;
		try {
			
				Role role = Role.toRole(rolestr);
				WfProcess process = (WfProcess)self.getObject();
				Team team = (Team) process.getTeamId().getObject();

				HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
				
				List userList = (List) rolePrincipalListMap.get(role);
				
				if(userList!=null && userList.size()>0){
//					if(printCtrl){
//						System.out.println(">>>>>>>>>>>>>>>  Role is " + role);
//						for (int i = 0; userList != null && i < userList.size(); i++) {
//							WTUser user = (WTUser) ((WTPrincipalReference) userList.get(i)).getObject();
//							System.out.println("    >>>>>>>>>>>>>>>  user is " + user.getName());
//							
//						}
//						
//					}
					havaMembers = true;
				}
				
				
					//TeamHelper.service.deleteRolePrincipalMap(role, user, team);
					
				
				
//				Iterator it1 = roleUser.keySet().iterator();
//				while (it1.hasNext()) {
//					String roleName = (String) it1.next();
//					System.out.println("begin to del role user role name is " + roleName);
//					
//
//				}
//				
//				Enumeration mems = team.getPrincipalTarget(role);
//				if (mems.hasMoreElements())
//					havaMembers = true;
				
				
//				if(printCtrl){
//					System.out.println(" === MEMBER INFO === havaMembers " +havaMembers);
//					System.out.println(" === MEMBER INFO === rolestr " + rolestr);
//					System.out.println(" === MEMBER INFO === role " + role);
//					System.out.println(" === MEMBER INFO === self " + self);
//					System.out.println(" === MEMBER INFO === team " +team);
//
//					
//				}
			
			

		} catch (Exception _wte) {
			_wte.printStackTrace();
		}
		
//		if(printCtrl){
//			System.out.println(" === MEMBER INFO ===  havaMembers " +havaMembers);
//			System.out.println(" === METHOD END === ");
//			
//		}
		
		return havaMembers;
	}
	
	public static void setElectronicSignature(WTUser user, WTObject pbo, String comments, String instructions, String role, String vote){
		RemoteUtility.setElectronicSignatureToObjectRelateObjects(user, pbo, comments, instructions, role, vote);
	}

}
