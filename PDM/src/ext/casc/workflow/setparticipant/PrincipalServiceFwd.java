package ext.casc.workflow.setparticipant;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class PrincipalServiceFwd implements RemoteAccess, PrincipalService,
		Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6616388835150739541L;

	static final boolean SERVER = RemoteMethodServer.ServerFlag;

	private static final String FC_RESOURCE = "wt.fc.fcResource";

	private static Manager getManager() throws WTException {

		Manager manager = ManagerServiceFactory.getDefault().getManager(PrincipalService.class);

		if (manager == null) {
			Object[] param = { "ext.ases.workflow.setparticipant.PrincipalService" };
			throw new WTException(FC_RESOURCE,wt.fc.fcResource.UNREGISTERED_SERVICE, param);
		}
		return manager;
	}

	public void saveTeamRole(Map<String, List<String>> roleUser, String oid,
			Locale locale) throws WTException {
		if(SERVER){
			((PrincipalService)getManager()).saveTeamRole(roleUser, oid, locale);
		}else{
			try {
				Class[] argTypes = { Map.class,String.class ,Locale.class};
				Object[] args = { roleUser,oid,locale};
				RemoteMethodServer.getDefault().invoke("saveTeamRole", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "saveTeamRole" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "saveTeamRole" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}
	
	public void saveTeamRole(String roleName, List<WTUser> userList, Object obj,Locale locale) throws WTException {
		if(SERVER){
			((PrincipalService)getManager()).saveTeamRole(roleName, userList, obj,locale);
		}else{
			try {
				Class[] argTypes = { String.class,List.class ,Object.class,Locale.class};
				Object[] args = { roleName, userList, obj,locale};
				RemoteMethodServer.getDefault().invoke("saveTeamRole", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "saveTeamRole" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "saveTeamRole" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<WTGroup> getUserGroups(WTUser user) throws WTException {
		if(SERVER){
			return ((PrincipalService)getManager()).getUserGroups(user);
		}else{
			try {
				Class[] argTypes = { WTUser.class };
				Object[] args = { user };
				return (List<WTGroup>) RemoteMethodServer.getDefault().invoke("getUserGroups", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "getUserGroups" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "getUserGroups" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<Role, List<WTUser>> getUserByRole(String oid, List<String> roleList) throws WTException {
		if(SERVER){
			return (Map<Role,List<WTUser>>)((PrincipalService)getManager()).getUserByRole(oid, roleList);
		}else{
			try {
				Class[] argTypes = { String.class,List.class };
				Object[] args = { oid,roleList};
				return (Map<Role,List<WTUser>>) RemoteMethodServer.getDefault().invoke("getUserByRole", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "getUserByRole" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "getUserByRole" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Role, List<WTUser>> getRoleAndUserByContainer(String oid) throws WTException {
		if(SERVER){
			return (Map<Role,List<WTUser>>)((PrincipalService)getManager()).getRoleAndUserByContainer(oid);
		}else{
			try {
				Class[] argTypes = { String.class };
				Object[] args = { oid};
				return (Map<Role,List<WTUser>>) RemoteMethodServer.getDefault().invoke("getRoleAndUserByContainer", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "getRoleAndUserByContainer" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "getRoleAndUserByContainer" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<Role, List<WTUser>> getRoleAndUserByContainer(Object obj) throws WTException {
		if(SERVER){
			return (Map<Role,List<WTUser>>)((PrincipalService)getManager()).getRoleAndUserByContainer(obj);
		}else{
			try {
				Class[] argTypes = { Object.class };
				Object[] args = { obj};
				return (Map<Role,List<WTUser>>) RemoteMethodServer.getDefault().invoke("getRoleAndUserByContainer", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "getRoleAndUserByContainer" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "getRoleAndUserByContainer" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getUserRoles(WTUser user,String oid) throws WTException {
		if(SERVER){
			return ((PrincipalService)getManager()).getUserRoles(user,oid);
		}else{
			try {
				Class[] argTypes = { WTUser.class ,String.class};
				Object[] args = { user };
				return (List<String>) RemoteMethodServer.getDefault().invoke("getUserRoles", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "getUserRoles" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "getUserRoles" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getRoles(WTUser user, String oid) throws WTException {
		if(SERVER){
			return ((PrincipalService)getManager()).getRoles(user,oid);
		}else{
			try {
				Class[] argTypes = { WTUser.class ,String.class};
				Object[] args = { user,oid };
				return (List<String>) RemoteMethodServer.getDefault().invoke("getRoles", null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException) {
					throw (WTException) targetE;
				}
				Object[] param = { "getRoles" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "getRoles" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<WTUser> getUser(String userName, Locale locale) throws WTException {
		if (SERVER) {
			return ((PrincipalService) getManager()).getUser(userName, locale);
		} else {
			try {
				Class[] argTypes = { String.class, Locale.class };
				Object[] args = { userName, locale };
				return (List<WTUser>) RemoteMethodServer.getDefault().invoke("getUser",null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "getUser" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "getUser" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}

	public Map<Role, List<WTPrincipalReference>> getRoleAndUserByContainer(
			Object pbo, String roleKey) throws WTException {
		if (SERVER) {
			return ((PrincipalService) getManager()).getRoleAndUserByContainer(pbo, roleKey);
		} else {
			try {
				Class[] argTypes = { Object.class, String.class };
				Object[] args = { pbo, roleKey };
				return (Map<Role, List<WTPrincipalReference>>) RemoteMethodServer.getDefault().invoke("getRoleAndUserByContainer",null, this, argTypes, args);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof WTException){
					throw (WTException) targetE;
				}
				Object[] param = { "getRoleAndUserByContainer" };
				throw new WTException(targetE, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (RemoteException rme) {
				Object[] param = { "getUser" };
				throw new WTException(rme, FC_RESOURCE,wt.fc.fcResource.OPERATION_FAILURE, param);
			}
		}
	}
}
