package ext.casc.util;

import com.ptc.windchill.mpml.resource.MPMTooling;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.definition._AttributeHierarchyChild;
import wt.iba.value._StringValue;
import wt.org.OrganizationServicesHelper;
import wt.org.PrincipalCollationKeyFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.SortedEnumeration;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import java.util.*;

public class QueryHelper {
	public static List<WTUser> getUserByName(String userName, Locale criteria)
			throws WTException {
		List<WTUser> results = new ArrayList<WTUser>();
		boolean enforce = wt.session.SessionServerHelper.manager
				.setAccessEnforced(false);
		try {
			Enumeration usernames = OrganizationServicesHelper.manager
					.findUser("fullName", userName);
			if (usernames != null) {
				while (usernames.hasMoreElements()) {
					WTUser curUser = (WTUser) usernames.nextElement();
					results.add(curUser);
				}
			}

			SortedEnumeration fullusernames = new SortedEnumeration(
					OrganizationServicesHelper.manager.findUser("name",
							userName), new PrincipalCollationKeyFactory(
							criteria));
			if (fullusernames != null)
				while (fullusernames.hasMoreElements()) {
					WTUser curUser = (WTUser) fullusernames.nextElement();
					if (!results.contains(curUser) && !curUser.isDisabled())
						results.add(curUser);
				}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return results;
	}


	public static MPMTooling queryMPMToolingByIBA(Map<String, String> ibaMap) {
		SessionServerHelper.manager.setAccessEnforced(false);
		boolean flag = true;
		try {
			QuerySpec qs = new QuerySpec();
			qs.setAdvancedQueryEnabled(true);
			int ibaHolderIndex = qs.appendClassList(MPMTooling.class, true);
			if(ibaMap.size() > 0){
				for (String ibaname : ibaMap.keySet()) {
					if(ibaMap.get(ibaname) != null && !ibaMap.get(ibaname).equals("")){
						int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
						int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
						// Latest Iteration
						SearchCondition scLatestIteration = new SearchCondition(MPMTooling.class, WTAttributeNameIfc.LATEST_ITERATION,
								SearchCondition.IS_TRUE);
						// String Value With IBA Holder
						SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class,
								"theIBAHolderReference.key.id", MPMTooling.class, WTAttributeNameIfc.ID_NAME);
						// String Value With Definition
						SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class,
								"definitionReference.key.id", StringDefinition.class, WTAttributeNameIfc.ID_NAME);
						qs.appendWhere(scLatestIteration, ibaHolderIndex);
						qs.appendAnd();
						qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
						qs.appendAnd();
						qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);

						// String Definition 软属性名称
						SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME,
								SearchCondition.EQUAL, ibaname);
						// String Value 软属性值
						SearchCondition scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE,
								SearchCondition.LIKE, ibaMap.get(ibaname).toUpperCase());

						qs.appendAnd();
						qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
						qs.appendAnd();
						qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
					}
				}
			}
			qs = new LatestConfigSpec().appendSearchCriteria(qs);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			while(qr.hasMoreElements()){
				Object[] obj = (Object[]) qr.nextElement();
				return (MPMTooling)obj[0];
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		SessionServerHelper.manager.setAccessEnforced(flag);
    	}
	    return null ;
	}

	public static MPMTooling queryMPMToolingByIBA(String name,Map<String, String> ibaMap) {
		SessionServerHelper.manager.setAccessEnforced(false);
		boolean flag = true;
		try {
			QuerySpec qs = new QuerySpec();
			qs.setAdvancedQueryEnabled(true);
			int ibaHolderIndex = qs.appendClassList(MPMTooling.class, true);
			SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.EQUAL, name);
			qs.appendWhere( sc, ibaHolderIndex);
			qs.appendAnd();
			int i=0;
			if(ibaMap.size() > 0){
				for (String ibaname : ibaMap.keySet()) {
					if(ibaMap.get(ibaname) != null && !ibaMap.get(ibaname).equals("")){
						if(i>0){
							qs.appendAnd();
						}
						int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
						int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
						// Latest Iteration
						SearchCondition scLatestIteration = new SearchCondition(MPMTooling.class, WTAttributeNameIfc.LATEST_ITERATION,
								SearchCondition.IS_TRUE);
						// String Value With IBA Holder
						SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class,
								"theIBAHolderReference.key.id", MPMTooling.class, WTAttributeNameIfc.ID_NAME);
						// String Value With Definition
						SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class,
								"definitionReference.key.id", StringDefinition.class, WTAttributeNameIfc.ID_NAME);
						qs.appendWhere(scLatestIteration, ibaHolderIndex);
						qs.appendAnd();
						qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
						qs.appendAnd();
						qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);

						// String Definition 软属性名称
						SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME,
								SearchCondition.EQUAL, ibaname);
						// String Value 软属性值
						SearchCondition scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE,
								SearchCondition.LIKE, ibaMap.get(ibaname).toUpperCase());

						qs.appendAnd();
						qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
						qs.appendAnd();
						qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
						i++;
					}
				}
			}
			qs = new LatestConfigSpec().appendSearchCriteria(qs);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			System.out.println(qr.size()+"******$$$$$$$****");
			while(qr.hasMoreElements()){
				Object[] obj = (Object[]) qr.nextElement();
				return (MPMTooling)obj[0];
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		SessionServerHelper.manager.setAccessEnforced(flag);
    	}
	    return null ;
	}

}
