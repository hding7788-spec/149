package com.glaway.mpm.mpmresource.validator;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.type.TypedUtility;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMSkillMaster;
import com.ptc.windchill.mpml.resource.MPMTooling;

public class MPMRelationshipValidator extends DefaultSimpleValidationFilter  {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		Persistable po = criteria.getContextObject().getObject();
		if (po instanceof MPMTooling) {
			MPMTooling tooling = (MPMTooling) po;
			String typeName = TypedUtility.getTypeIdentifier(tooling).getTypename();
			if ("SBToSBLink".equals(key.getComponentID()) && typeName.contains(TypeNameConstants.SB)) {
				try {
					IBAHelper helper = new IBAHelper(tooling);
					String equipNum = Util.formateString(helper.getIBAValue(AttributeConstants.equipmentNumber));
					if ("".equals(equipNum)) {
						status = UIValidationStatus.ENABLED;
					}
				} catch (WTException e) {
					e.printStackTrace();
				}
			} else if ("GXMCToGZhongLink".equals(key.getComponentID()) && typeName.contains(TypeNameConstants.GXMC)) {
				status = UIValidationStatus.ENABLED;
			}
		}
		return status;
	}

	
}
