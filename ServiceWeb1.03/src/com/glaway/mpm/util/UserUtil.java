package com.glaway.mpm.util;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.model.data.CmUser;
import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.org.*;
import wt.pds.StatementSpec;
import wt.query.*;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class UserUtil {
	private static VaLogger logger = VaLogger.getLogger(UserUtil.class.getName());

	/**
	 * 获取当前用户信息
	 *
	 * @return CmUser 当前用户
	 */
	public static CmUser getCurrentUser() {
		CmUser user = new CmUser();
		try {
			WTUser wtUser = (WTUser)SessionHelper.getPrincipal();
			user.setName(wtUser.getName());
			user.setFullName(wtUser.getFullName());
			user.setOid(PersistenceHelper.getObjectIdentifier(wtUser).getId());
			user.setEmail(wtUser.getEMail());
			//获取当前用户所属部门
			user.setDepartment(getDepartment(wtUser.getName()));
		} catch (WTException e) {
			logger.error(e);
		}
		return user;
	}

	public static CmUser getCreator(RevisionControlled revisionControlled) throws WTException {
		CmUser user = new CmUser();
		user.setOid(PersistenceHelper.getObjectIdentifier(getWTUserByName(revisionControlled.getCreatorName())).getId());
		user.setName(revisionControlled.getCreatorName());
		user.setFullName(revisionControlled.getCreatorFullName());
		user.setEmail(CommonUtil.objectToString(revisionControlled.getCreatorEMail()));
		user.setDepartment(getDepartment(revisionControlled.getCreatorName()));
		return user;
	}

	public static CmUser getModifier(RevisionControlled revisionControlled) throws WTException {
		CmUser user = new CmUser();
		user.setOid(PersistenceHelper.getObjectIdentifier(getWTUserByName(revisionControlled.getModifierName())).getId());
		user.setName(revisionControlled.getModifierName());
		user.setFullName(revisionControlled.getModifierFullName());
		user.setEmail(CommonUtil.objectToString(revisionControlled.getModifierEMail()));
		user.setDepartment(getDepartment(revisionControlled.getModifierName()));
		return user;
	}

	/**
	 * 获取用户所属部门
	 *
	 * @param userName
	 * @return String
	 */
	public static String getDepartment(String userName) {
		String department = "";
		try {
			WTUser user = getWTUserByName(userName);
			WTGroup group = getGroupsByUser(user);
			if (group != null) {
				department = group.getName();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return department;
	}

	/**
	 * 根据用户获取其所属组
	 * @return
	 * @throws WTException
	 */
	public static WTGroup getGroupsByUser(WTPrincipal principal) throws WTException {
		WTGroup wtGroup = null;
    	QueryResult qResult = MPMUtil.queryGroup();
		while (qResult.hasMoreElements()) {
			wtGroup = (WTGroup) qResult.nextElement();
			if(wtGroup.isMember(principal) && wtGroup.getDescription()!=null&&wtGroup.getDescription().endsWith("车间")) {
				return wtGroup;
			}
		}
		return null;
    }

	public static WTUser getWTUserByName(String userName) throws WTException {
		QuerySpec qSpec = new QuerySpec(WTUser.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(WTUser.class, _WTPrincipal.NAME, SearchCondition.LIKE, "%" + userName + "%");
        qSpec.appendWhere(sCondition, index);
        qSpec.appendOr();
        sCondition = new SearchCondition(WTUser.class, _WTUser.FULL_NAME, SearchCondition.LIKE, "%" + userName + "%");
        qSpec.appendWhere(sCondition, index);
        qSpec.appendOr();
        sCondition = new SearchCondition(WTUser.class, _WTUser.LAST, SearchCondition.LIKE, "%" + userName + "%");
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        if (qResult.hasMoreElements()) {
            return (WTUser) qResult.nextElement();
        }
        return null;
	}

	public static List<WTUser> getWTUserByFullName(String userName) throws WTException {
		List<WTUser> userList = new ArrayList<WTUser>();
		QuerySpec qSpec = new QuerySpec(WTUser.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(WTUser.class, WTUser.FIRST, SearchCondition.LIKE, "%" + userName + "%");
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        while (qResult.hasMoreElements()) {
        	WTUser user = (WTUser) qResult.nextElement();
        	userList.add(user);
        }
        return userList;
	}

	public static CmUser getCmUserByName(String fullName) throws WTException {
		CmUser user = null;
		if (null != fullName && !fullName.equals("")) {
			user = new CmUser();
			WTUser wtUser = getWTUserByName(fullName);
			if (wtUser != null) {
				try {
					user.setName(wtUser.getName());
					user.setFullName(wtUser.getFullName());
					user.setOid(PersistenceHelper.getObjectIdentifier(wtUser).getId());
					user.setEmail(wtUser.getEMail());
					//获取当前用户所属部门
					user.setDepartment(getDepartment(wtUser.getName()));
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return user;
	}

	public static WTUser getWTUser(long userId) throws WTException {
		QuerySpec qSpec = new QuerySpec(WTUser.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(WTUser.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, userId);
        qSpec.appendWhere(sCondition, index);

        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        if (qResult.hasMoreElements()) {
            return (WTUser) qResult.nextElement();
        }
        return null;
	}

	public static CmUser getCmUser(WTUser wtUser) throws WTException {
		CmUser user = new CmUser();
		if (wtUser != null) {
			try {
				user.setName(wtUser.getName());
				user.setFullName(wtUser.getFullName());
				user.setOid(PersistenceHelper.getObjectIdentifier(wtUser).getId());
				user.setEmail(wtUser.getEMail());
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	/**
	 * 通过组名查询组
	 * @param groupName
	 * @return
	 * @throws WTException
	 */
    public static WTGroup queryGroup(String groupName) throws WTException {
    	WTGroup group = null;
        try {
            QuerySpec queryspec = new QuerySpec(WTGroup.class);
            String A = queryspec.getFromClause().getAliasAt(0);
            TableColumn INTERNAL = new TableColumn(A, "INTERNAL");
            TableColumn CLASSNAMEKEYCONTAINERREFEREN = new TableColumn(A,
                    "CLASSNAMEKEYCONTAINERREFEREN");
            TableColumn name = new TableColumn(A,
                    "NAME");

            queryspec.appendWhere(new SearchCondition(INTERNAL, "=",
                    new ConstantExpression(0)), new int[] { 0 });
            queryspec.appendAnd();
            queryspec.appendWhere(new SearchCondition(
                    CLASSNAMEKEYCONTAINERREFEREN, "=", new ConstantExpression(
                            (Object) "wt.inf.container.OrgContainer")),
                    new int[] { 0 });

            queryspec.appendAnd();
            queryspec.appendWhere(new SearchCondition(
            		name, "=", new ConstantExpression(
                            (Object) groupName)),
                    new int[] { 0 });

            QueryResult qr = PersistenceServerHelper.manager.query(queryspec);
            if(qr.hasMoreElements()){
            	group = (WTGroup)qr.nextElement();
            }
        } catch (QueryException e) {
            e.printStackTrace();
        }
        return group;
    }

    public static WTUser getUser(String name) throws WTException {
        QuerySpec qs = new QuerySpec(WTUser.class);
        int index[] = { 0 };
        SearchCondition scCondition = new SearchCondition(WTUser.class, _WTPrincipal.NAME, SearchCondition.EQUAL, name);
        qs.appendWhere(scCondition, index);
        QueryResult result = PersistenceHelper.manager.find((StatementSpec) qs);
        if (result.hasMoreElements()) {
            return (WTUser) result.nextElement();
        }

        return null;
    }
    public static List<WTGroup> queryGroup2(String groupName) throws WTException {
    	List<WTGroup> list = new ArrayList<WTGroup>();
        try {
            QuerySpec queryspec = new QuerySpec(WTGroup.class);
            String A = queryspec.getFromClause().getAliasAt(0);
            TableColumn INTERNAL = new TableColumn(A, "INTERNAL");
            TableColumn CLASSNAMEKEYCONTAINERREFEREN = new TableColumn(A,
                    "CLASSNAMEKEYCONTAINERREFEREN");
            TableColumn name = new TableColumn(A,
                    "NAME");

            queryspec.appendWhere(new SearchCondition(INTERNAL, "=",
                    new ConstantExpression(0)), new int[] { 0 });
            queryspec.appendAnd();
            queryspec.appendWhere(new SearchCondition(
                    CLASSNAMEKEYCONTAINERREFEREN, "=", new ConstantExpression(
                            (Object) "wt.inf.container.OrgContainer")),
                    new int[] { 0 });

            queryspec.appendAnd();
            queryspec.appendWhere(new SearchCondition(
            		name, SearchCondition.LIKE, new ConstantExpression(
                            "%" + groupName + "%")),
                    new int[] { 0 });

            QueryResult qr = PersistenceServerHelper.manager.query(queryspec);
            while(qr.hasMoreElements()){
            	WTGroup group = (WTGroup)qr.nextElement();
            	list.add(group);
            }
        } catch (QueryException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static WTGroup getZlyGroupsByUser(WTPrincipal principal) throws WTException {
		WTGroup wtGroup = null;
    	QueryResult qResult = MPMUtil.queryGroup();
		while (qResult.hasMoreElements()) {
			wtGroup = (WTGroup) qResult.nextElement();
			if(wtGroup.isMember(principal) && wtGroup.getName().startsWith("资料员")) {
				return wtGroup;
			}
		}
		return null;
    }

	public static List<WTUser> getWTUsersByName(String userName) throws WTException {
		List<WTUser> userList = new ArrayList<WTUser>();
		QuerySpec qSpec = new QuerySpec(WTUser.class);
        int[] index = { 0 };
        SearchCondition sCondition = new SearchCondition(WTUser.class, _WTPrincipal.NAME, SearchCondition.LIKE, "%" + userName + "%");
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        while (qResult.hasMoreElements()) {
        	WTUser user = (WTUser) qResult.nextElement();
        	userList.add(user);
        }
        return userList;
	}
	public static List<WTUser> getWTUsers(String userName, String fullName) throws WTException {
		List<WTUser> userList = new ArrayList<WTUser>();
		QuerySpec qSpec = new QuerySpec(WTUser.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(WTUser.class, _WTPrincipal.NAME, SearchCondition.LIKE, "%" + userName + "%");
		qSpec.appendWhere(sCondition, index);
		qSpec.appendAnd();
		sCondition = new SearchCondition(WTUser.class, _WTUser.FULL_NAME, SearchCondition.LIKE, "%" + fullName + "%");
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		while (qResult.hasMoreElements()) {
			WTUser user = (WTUser) qResult.nextElement();
			userList.add(user);
		}
		return userList;
	}

	public static WTUser getUserByEmail(String email) {
		WTUser user = null;
		try {
			Enumeration e = OrganizationServicesHelper.manager.findUser("eMail", email);
			if (e.hasMoreElements())
				user = (WTUser) e.nextElement();
		} catch (WTException wte) {
			wte.printStackTrace();
		}
		return user;
	}

	public static WTUser getUserByHrCode(String hrCode) {
		WTUser user = null;
		try {
			Enumeration e = OrganizationServicesHelper.manager.findUser("mobilePhoneNumber", hrCode);
			if (e.hasMoreElements())
				user = (WTUser) e.nextElement();
		} catch (WTException wte) {
			wte.printStackTrace();
		}
		return user;
	}

	public static List<WTUser> getWTUsersLikeNameAndFullName(String userName) throws WTException {
		List<WTUser> userList = new ArrayList<WTUser>();
		QuerySpec qSpec = new QuerySpec(WTUser.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(WTUser.class, _WTPrincipal.NAME, SearchCondition.LIKE, "%" + userName + "%");
		qSpec.appendWhere(sCondition, index);
		qSpec.appendOr();
		sCondition = new SearchCondition(WTUser.class, _WTUser.FULL_NAME, SearchCondition.LIKE, "%" + userName + "%");
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		while (qResult.hasMoreElements()) {
			WTUser user = (WTUser) qResult.nextElement();
			userList.add(user);
		}
		return userList;
	}

}
