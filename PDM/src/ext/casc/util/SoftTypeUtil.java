package ext.casc.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Locale;

import wt.epm.util.EPMSoftTypeServerUtilities;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.iba.definition.AbstractAttributeDefinition;
import wt.iba.definition.AttributeDefinition;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.DefaultServiceProvider;
import wt.session.SessionHelper;
import wt.type.TypeDefinitionReference;
import wt.type.Typed;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.DefinitionIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.descriptor.common.DefinitionDescriptor;
import com.ptc.core.meta.descriptor.common.DefinitionDescriptorFactory;
import com.ptc.core.meta.type.command.typemodel.common.AbstractTypeModelCommand;
import com.ptc.core.meta.type.command.typemodel.common.GetChildrenCommand;
import com.ptc.core.meta.type.mgmt.common.TypeDefinitionDefaultView;
import com.ptc.core.meta.type.mgmt.server.impl.TypeSingleAttrConstraint;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;
public class SoftTypeUtil implements RemoteAccess {
	/**
	 * copied from QueryTypeTreeWebjectDelegate
	 * Executes GetChildrenCommand to get direct children of given type
	 * @param root_types
	 * @param containerReference
	 * @return array of children
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static ArrayList<TypeIdentifier> getChildTypes(ArrayList<TypeIdentifier> root_types, String containerReference) throws WTPropertyVetoException, WTException{
		// get all direct childern
		AbstractTypeModelCommand command = new GetChildrenCommand();

		ArrayList returnTypes = new ArrayList<TypeIdentifier>();
		for (int x=0; x < root_types.size();x++){
			//set the type for the command
			command.setType_id(root_types.get(x));
			//Set the container that the types should be gotten from
			WTReference contextContainerRef = null;


			//            if ( !containerReference.equals ("") ){
			/*            	contextContainerRef = CSCFederationUtiliies.getObjectRefByUfid (containerReference);

        		CSCDebug.outDebugInfo("CONTEXT CONTAINER REF 2 = " + contextContainerRef.toString());
             	if (contextContainerRef != null){
             		command.setContainerIdentifier(TypeIdentifierUtility.getTypeInstanceIdentifier((Object)contextContainerRef.getObject()));

                	CSCDebug.outDebugInfo("CONTEXT CONTAINER TYPE 3 = " + TypeIdentifierUtility.getTypeInstanceIdentifier((Object)contextContainerRef.getObject()));
             	}*/

			/*            	WTContainer container = (WTContainer)CSCUtil.getObjectByOid(containerReference);
            	CSCDebug.outDebugInfo("CONTEXT CONTAINER 2 = " + container.toString());
            	TypeInstanceIdentifier typeID = TypeIdentifierUtility.getTypeInstanceIdentifier(container);
            	CSCDebug.outDebugInfo("CONTEXT CONTAINER TYPE 3 = " + typeID.toString());
            	command.setContainerIdentifier(typeID);*/
			command = (AbstractTypeModelCommand) command.execute();
			TypeIdentifier[] childTypes = command.getAnswer();
			for (int y=0; y< childTypes.length;y++){
				returnTypes.add(childTypes[y]);
			}
			//         	}
	}
		return returnTypes;
	}

	/**
	 * Get Child Types by one parent Type
	 * @param root_type
	 * @param containerReference
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static ArrayList<TypeIdentifier> getChildTypes(String root_type, String containerReference) throws WTPropertyVetoException, WTException{
		TypeIdentifier typeChild = TypeHelper.getTypeIdentifier(root_type);
		ArrayList<TypeIdentifier> aTypes = new ArrayList<TypeIdentifier>();
		aTypes.add(typeChild);

		return getChildTypes(aTypes, containerReference);
	}

	public static DefinitionDescriptor getTypeIdentifierDefinition(TypeIdentifier type, Locale locale) throws WTException{
		if (type != null){
			DefinitionIdentifier[] types = {type};
			DefinitionDescriptorFactory DESCRIPTOR_FACTORY = (DefinitionDescriptorFactory)DefaultServiceProvider.getService(DefinitionDescriptorFactory.class, "default" );

			DefinitionDescriptor[] defs = DESCRIPTOR_FACTORY.get(types, null, locale);
			if ((defs != null) && (defs.length > 0)){
				DefinitionDescriptor def = defs[0];
				return def;
			}
		}
		return null;
	}

	public static DefinitionDescriptor getTypeIdentifierDefinition(TypeIdentifier type) throws WTException{
		return getTypeIdentifierDefinition(type, SessionHelper.getLocale());
	}

	public static DefinitionDescriptor getTypeIdentifierDefinition(String type) throws WTException{
		TypeIdentifier typeId = TypeHelper.getTypeIdentifier(type);
		return getTypeIdentifierDefinition(typeId);
	}

	public static ArrayList<String> getTypeIdentifierDefinitionsByDisplayName(String typeDisplayName){
		ArrayList<String> result = new ArrayList<String>();
		try{
			QuerySpec qs = new QuerySpec(WTTypeDefinitionMaster.class);
			SearchCondition sc = new SearchCondition(WTTypeDefinitionMaster.class, WTTypeDefinitionMaster.DISPLAY_NAME_KEY, SearchCondition.EQUAL,typeDisplayName,false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);

			while (qr.hasMoreElements()){
				WTTypeDefinitionMaster typeMaster = (WTTypeDefinitionMaster)qr.nextElement();
				String typeIdName = typeMaster.getIntHid();
				if(!result.contains(typeIdName))
					result.add(typeIdName);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}



	/**
	 * ͨ���������ж�ָ���������Ƿ��Ǳ���ֵ
	 * @param softtype ������LogicalIdentity
	 * @param attrname ������LogicalIdentity
	 * @return �Ƿ��Ǳ���ֵ
	 */
	public static boolean isRequiredAttribute(String softtype, String attrname) {
		boolean result = false;

		WTTypeDefinition softtypeDef = getSoftType(softtype);
		AbstractAttributeDefinition ibaDef = (AbstractAttributeDefinition)getAttributeDefinition(attrname);

		TypeSingleAttrConstraint requiredConstraint = getRequiredConstraint(softtypeDef, ibaDef);
		if (requiredConstraint != null) {
			return true;
		}
		return result;
	}


	/**
	 * ȡ�������Ե���ʾ���
	 * @param attrname �������
	 * @return ��ʾ���
	 */
	public static String getAttributeDisplayName(String attrname) {
		AbstractAttributeDefinition ibaDef = (AbstractAttributeDefinition)getAttributeDefinition(attrname);
		if (ibaDef == null) {
			return "";
		}

		return ibaDef.getDisplayName();
	}


	public static WTTypeDefinition getSoftType(String logicalName) {
		WTTypeDefinition softType = null;
		try {
			QuerySpec qs = new QuerySpec(WTTypeDefinition.class);
			SearchCondition sc = new SearchCondition(WTTypeDefinition.class, WTTypeDefinition.LOGICAL_IDENTIFIER,
					SearchCondition.EQUAL, logicalName.trim(), false);
			qs.appendWhere(sc);
			qs.appendAnd();
			sc = new SearchCondition(WTTypeDefinition.class, "iterationInfo.latest", "TRUE");
			qs.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				softType = (WTTypeDefinition) qr.nextElement();
			}
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
		return softType;
	}

	private WTTypeDefinition getSoftType(Typed typed) {
		WTTypeDefinition td = null;
		if (typed.getTypeDefinitionReference() != null) {
			TypeDefinitionReference tdr = typed.getTypeDefinitionReference();
			try {
				TypeDefinitionDefaultView tddv = EPMSoftTypeServerUtilities.getTypeDefinition(tdr);
				td = (WTTypeDefinition) PersistenceHelper.manager.refresh(tddv.getObjectID());
			} catch (WTException e) {
//				e.printStackTrace();
			}
		}
		return td;
	}

	private static AttributeDefinition getAttributeDefinition(String softAttributeName) {
		AttributeDefinition softAttributeDefinition = null;
		// to be continue;
		if (softAttributeName == null || softAttributeName.length() == 0)
			return null;
		try {
			QuerySpec queryStringDefinition = new QuerySpec(AttributeDefinition.class);
			SearchCondition sc = new SearchCondition(AttributeDefinition.class, "name", SearchCondition.EQUAL,
					softAttributeName.trim());
			queryStringDefinition.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find(queryStringDefinition);

			if (qr.size() == 0) {
				return null;
			} else {
				return (AttributeDefinition) qr.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return softAttributeDefinition;
	}

	private static TypeSingleAttrConstraint getDiscreteSetConstraint(WTTypeDefinition softType,
			AbstractAttributeDefinition attrDefinition) {

		try {
			QuerySpec queryConstraints = new QuerySpec(
					com.ptc.core.meta.type.mgmt.server.impl.TypeSingleAttrConstraint.class);
			SearchCondition sc1 = new SearchCondition(
					com.ptc.core.meta.type.mgmt.server.impl.TypeSingleAttrConstraint.class,
					"attributeDefinition.key.id", SearchCondition.EQUAL, attrDefinition.getPersistInfo()
							.getObjectIdentifier().getId());
			SearchCondition sc2 = new SearchCondition(
					com.ptc.core.meta.type.mgmt.server.impl.TypeSingleAttrConstraint.class,
					"typeDefinitionReference.key.id", SearchCondition.EQUAL, new Long(softType.toString().substring(
							softType.toString().indexOf(":") + 1)));
			SearchCondition sc3 = new SearchCondition(
					com.ptc.core.meta.type.mgmt.server.impl.TypeSingleAttrConstraint.class, "enforcementRuleClassName",
					SearchCondition.EQUAL, "com.ptc.core.meta.container.common.impl.DiscreteSetConstraint");

			queryConstraints.appendWhere(sc1);
			queryConstraints.appendAnd();
			queryConstraints.appendWhere(sc2);
			queryConstraints.appendAnd();
			queryConstraints.appendWhere(sc3);
			QueryResult qr = PersistenceHelper.manager.find(queryConstraints);

			if (qr.size() == 0) {
				return null;
			} else {
				return (TypeSingleAttrConstraint) qr.nextElement();
			}
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}

	private static TypeSingleAttrConstraint getRequiredConstraint(WTTypeDefinition softType,
			AbstractAttributeDefinition attrDefinition) {

		try {
			QuerySpec queryConstraints = new QuerySpec(
					com.ptc.core.meta.type.mgmt.server.impl.TypeSingleAttrConstraint.class);
			SearchCondition sc1 = new SearchCondition(
					com.ptc.core.meta.type.mgmt.server.impl.TypeSingleAttrConstraint.class,
					"attributeDefinition.key.id", SearchCondition.EQUAL, attrDefinition.getPersistInfo()
							.getObjectIdentifier().getId());
			SearchCondition sc2 = new SearchCondition(
					com.ptc.core.meta.type.mgmt.server.impl.TypeSingleAttrConstraint.class,
					"typeDefinitionReference.key.id", SearchCondition.EQUAL, new Long(softType.toString().substring(
							softType.toString().indexOf(":") + 1)));
			SearchCondition sc3 = new SearchCondition(
					com.ptc.core.meta.type.mgmt.server.impl.TypeSingleAttrConstraint.class, "enforcementRuleClassName",
					SearchCondition.EQUAL, "com.ptc.core.meta.container.common.impl.ValueRequiredConstraint");

			queryConstraints.appendWhere(sc1);
			queryConstraints.appendAnd();
			queryConstraints.appendWhere(sc2);
			queryConstraints.appendAnd();
			queryConstraints.appendWhere(sc3);
			QueryResult qr = PersistenceHelper.manager.find(queryConstraints);
			TypeSingleAttrConstraint  tsac = null;
			while(qr.hasMoreElements()){
				if(tsac==null)
					tsac = (TypeSingleAttrConstraint) qr.nextElement();
			}
			return tsac;
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}


	public static String getTypeDisplayValue(WTObject obj, Locale local) throws WTException, RemoteException, InvocationTargetException {
		if (!RemoteMethodServer.ServerFlag) {
			Class<?>[] clz = new Class<?>[] {WTObject.class, Locale.class};
			Object[] objs = new Object[] {obj, local};
			return (String) RemoteMethodServer.getDefault().invoke("getTypeDisplayValue", SoftTypeUtil.class.getName(), null, clz, objs);
		}
		return TypeHelper.getLocalizedTypeString(obj, local);
	}
}
