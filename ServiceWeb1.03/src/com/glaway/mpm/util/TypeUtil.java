package com.glaway.mpm.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.fc.Persistable;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.method.RemoteAccess;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.meta.type.command.typemodel.common.AbstractTypeModelCommand;
import com.ptc.core.meta.type.command.typemodel.common.GetChildrenCommand;
import com.ptc.core.ui.validation.UIValidationStatus;

public class TypeUtil implements RemoteAccess {
	private static int index[] = { 0 };
	private static final String CLASSNAME = TypeUtil.class.getName();

	/**
	 * 设置对象类型
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param part
	 * @param objectType
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 * @throws WTException
	 *
	 */
	public static void setType(Persistable persistable, String objectType) throws WTPropertyVetoException,
			RemoteException, WTException {
		if (objectType == null || "".equals(objectType)) {
			return;
		}
		TypeIdentifier typeIdentifier = TypedUtility.getTypeIdentifier(objectType);
		TypeHelper.setType(persistable, typeIdentifier);
	}

	/**
	 * 获取子皆类型
	 *
	 * @author qianlong
	 * @date 2012-12-14
	 * @param objectType
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 *
	 */
	public static List<TypeIdentifier> getChildTypes(TypeIdentifier typeIdentifier) throws WTPropertyVetoException,
			WTException {
		List<TypeIdentifier> typeList = new ArrayList<TypeIdentifier>();
		// get all direct childern
		AbstractTypeModelCommand command = new GetChildrenCommand();
		command.setType_id(typeIdentifier);
		command = (AbstractTypeModelCommand) command.execute();
		TypeIdentifier[] childTypes = command.getAnswer();
		for (int y = 0; y < childTypes.length; y++) {
			typeList.add(childTypes[y]);
		}
		return typeList;
	}

	/**
	 * 获取子皆类型
	 *
	 * @author qianlong
	 * @date 2012-12-14
	 * @param objectType
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 *
	 */
	public static List<TypeIdentifier> getChildTypes(String typeName) throws WTPropertyVetoException, WTException {
		List<TypeIdentifier> typeList = new ArrayList<TypeIdentifier>();
		TypeIdentifier typeIdentifier = TypedUtility.getTypeIdentifier(typeName);
		// get all direct childern
		AbstractTypeModelCommand command = new GetChildrenCommand();
		command.setType_id(typeIdentifier);
		command = (AbstractTypeModelCommand) command.execute();
		TypeIdentifier[] childTypes = command.getAnswer();
		for (int y = 0; y < childTypes.length; y++) {
			typeList.add(childTypes[y]);
		}
		return typeList;
	}

	/**
	 * 获取最下皆的类型
	 *
	 * @author qianlong
	 * @date 2012-12-15
	 * @param typeName
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void getAllLastType(TypeIdentifier typeIdentifier, List<TypeIdentifier> list)
			throws WTPropertyVetoException, WTException {
		List<TypeIdentifier> childTypeList = getChildTypes(typeIdentifier);
		if (childTypeList == null || childTypeList.size() == 0) {
			list.add(typeIdentifier);
		} else {
			for (TypeIdentifier type : childTypeList) {
				getAllLastType(type, list);
			}
		}
	}

	/**
	 * 获取所有的子皆类型
	 *
	 * @author qianlong
	 * @date 2012-12-14
	 * @param map
	 * @param typeIdentifier
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 *
	 */
	public static void getAllChildTypes(Map<TypeIdentifier, List<TypeIdentifier>> map, TypeIdentifier typeIdentifier)
			throws WTPropertyVetoException, WTException {
		List<TypeIdentifier> list = getChildTypes(typeIdentifier);
		if (list.size() != 0) {
			map.put(typeIdentifier, list);
			for (TypeIdentifier identifier : list) {
				getAllChildTypes(map, identifier);
			}
		}

	}

	/**
	 * 获取类型对象的BranchId
	 *
	 * @author qianlong
	 * @date 2012-12-13
	 * @param objectType
	 * @return
	 * @throws RemoteException
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void getTypeQuery(Class objectClass, String objectType, QuerySpec querySpec) throws RemoteException,
			WTException {
		TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(objectType);
		long branchid = typeRef.getKey().getBranchId();
		querySpec.appendWhere(new SearchCondition(objectClass, "typeDefinitionReference.key.branchId",
				SearchCondition.EQUAL, branchid), index);
	}



	public static void main(String[] args) {
		// RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		// methodServer.setUserName("wcadmin");
		// methodServer.setPassword("wcadmin");
		// try {
		// methodServer.invoke("getChildTypes", CLASSNAME, null, null, null);
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		Map map = new HashMap();
		map.put("0", "2");
		map.put("1", "2");
		map.put("2", "2");
		map.put("3", "2");
		for (int i = 0; i < 3; i++) {
			Map map1 = new HashMap();
			map1.putAll(map);
			map1.remove(i + "");
		}
	}

	/**判断当前用户是不是主任工艺师
	 * */
	public static Boolean IsYouxiao() throws WTException {
	        WTUser  curentuser = (WTUser) SessionHelper.manager.getPrincipal();
	        WTContainer contain = Util.getContainerByName("工艺资源库");
	        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged)contain);
            Role role = Role.toRole("ZHURENGONGYISHI");
            if (role==null) {
                return false;
            }
            ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
            for(WTPrincipalReference reference:arrayList){
                Object object2 = reference.getPrincipal();
                if(object2 instanceof WTUser){
                    WTUser user = (WTUser)object2;
                    if(user.getName().equals(curentuser.getName())){
                        return true;
                    }
                }
            }
            return false;

    }
	
	/** 
	 * 获取对象软类型
	 * @param obj
	 * @param isFullType
	 * @return
	 * @throws WTException
	 */
	public static String getSoftType(Object obj, boolean isFullType) throws WTException {
        String result = null;
        if (obj != null) {
            TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(obj);
            if (type != null) {
                String typeName = type.getTypename();
                if (!isFullType && typeName.indexOf('|') != -1) {
                    result = typeName.substring(typeName.lastIndexOf('|') + 1);
                } else {
                    result = typeName;
                }
            }
        }
        if (result == null) {
            result = "";
        }
        return result;
    }
}
