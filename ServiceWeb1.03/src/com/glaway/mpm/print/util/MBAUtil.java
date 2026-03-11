package com.glaway.mpm.print.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypedUtility;
import wt.util.WTException;

import com.glaway.mpm.log.VaLogger;
import com.ptc.core.htmlcomp.util.TypeHelper;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView.RuleDataObject;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCNormalizedObject;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.DiscreteSet;
import com.ptc.core.meta.common.IllegalFormatException;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.core.meta.type.common.TypeInstanceFactory;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;

import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.constants.ProcessPlanConstants;

public class MBAUtil implements RemoteAccess, Serializable {
    private static final long serialVersionUID = 150882489242046185L;

    private static VaLogger logger = VaLogger.getLogger(MBAUtil.class.getName());

    public static Object[] getLegalSetBySofttypeIBA(String softtype, String attrname) throws WTException {
        Object[] result = null;
        try {
            TypeIdentifier ti = TypedUtility.getTypeIdentifier(softtype);

            TypeDefinitionReadView tv = TypeDefinitionServiceHelper.service.getTypeDefView(ti);
            AttributeDefinitionReadView av = tv.getAttributeByName(attrname);
            Collection<ConstraintDefinitionReadView> constraints = av.getAllConstraints();
            for (ConstraintDefinitionReadView constraint : constraints) {
                String rule = constraint.getRule().getKey().toString();
                if (rule.indexOf("com.ptc.core.meta.container.common.impl.DiscreteSetConstraint") > -1) {
                    RuleDataObject rdo = constraint.getRuleDataObj();
                    if (rdo != null) {
                        DiscreteSet data = (DiscreteSet) rdo.getRuleData();
                        result = data.getElements();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new WTException(e, "Get Enumeration [" + attrname + "] for type [" + softtype + "] failed.");
        }
        return result;
    }

    /**
     * @param p
     * @param dataMap
     * @return
     * @throws WTException
     */
    public static void setValue(Persistable p, Map<String, Object> dataMap) throws WTException {

        Locale loc = null;
        try {
            loc = SessionHelper.getLocale();
            setValue(p, loc, dataMap);
        } catch (WTException e) {
        	e.printStackTrace(System.out);
        	logger.debug("Set IBA Value Fail" + e.getMessage());
            return;
        }
    }

    /**
     * @param p
     * @param loc
     * @param dataMap
     * @return
     * @throws WTException
     */
    public static Persistable setValue(Persistable p, Locale loc, Map<String, Object> dataMap) throws WTException {
        LWCNormalizedObject lwcObject = new LWCNormalizedObject(p, null, loc, new UpdateOperationIdentifier());
        Iterator<String> keyIt = dataMap.keySet().iterator();
        String key = null;
        lwcObject.load(dataMap.keySet());
        while (keyIt.hasNext()) {
            key = keyIt.next();
            lwcObject.set(key, dataMap.get(key));
        }
        lwcObject.apply();
        PersistenceServerHelper.manager.update(p);
        return p;
    }

    public static Persistable setObjectValue(Persistable p, String key, Object value) throws WTException {
    	Locale loc = null;
        try {
            loc = SessionHelper.getLocale();
        } catch (WTException e) {
        	logger.debug("Set IBA Value Fail" + e.getMessage());
        }
        LWCNormalizedObject lwcObject = new LWCNormalizedObject(p, null, loc, new UpdateOperationIdentifier());
        lwcObject.load(key);
        lwcObject.set(key, value);
        lwcObject.apply();
        PersistenceServerHelper.manager.update(p);
        return p;
    }

    /**
     * @param p
     * @return
     */
    public static Map<String, Object> getAllAttribute(Persistable p, Locale loc) {
        TypeInstance typeInstance;
        Map<String, Object> dataMap = new HashMap<String, Object>();
        try {
            LWCNormalizedObject lwcObject = new LWCNormalizedObject(p, null, loc, null);
            TypeInstanceIdentifier typeinstanceidentifier = ClientTypedUtility.getTypeInstanceIdentifier(p);
            typeInstance = TypeInstanceFactory.newTypeInstance(typeinstanceidentifier);

            TypeIdentifier typeidentifier = (TypeIdentifier) typeInstance.getIdentifier().getDefinitionIdentifier();
            Set attrs = TypeHelper.getSoftAttributes(typeidentifier);
            Iterator attIt = attrs.iterator();
            String attrFullName = "";
            String attrName = "";
            int idx = 0;
            while (attIt.hasNext()) {
                attrFullName = attIt.next().toString();
                idx = attrFullName.lastIndexOf("|");
                attrName = attrFullName.substring(idx + 1);
                lwcObject.load(attrName);
                dataMap.put(attrName, lwcObject.get(attrName));
            }

        } catch (IllegalFormatException e) {

            e.printStackTrace();
        } catch (WTException e) {

            e.printStackTrace();
        }
        return dataMap;
    }

    /**
     * @param p
     * @return
     */
    public static Map<String, Object> getAllAttribute(Persistable p) {
        Locale loc = null;
        try {
            loc = SessionHelper.getLocale();
        } catch (WTException e) {
            e.printStackTrace();
            return null;

        }
        return getAllAttribute(p, loc);
    }

    /**
     * @param p
     * @param key
     * @return
     * @throws WTException
     */
    public static Object getValue(Persistable p, String key)  {
        Locale loc = null;
        try {
            loc = SessionHelper.getLocale();
        } catch (WTException e) {
        	logger.debug("Get IBA Value Fail" + e.getMessage());
            return null;
        }
        return getValue(p, loc, key);
    }

    /**
     * @param p
     * @param key
     * @return
     * @throws WTException
     */
    public static Object getValue(Persistable targetObj, Locale locale, String ibaName) {
        Object ibaValue = null;
        if (!RemoteMethodServer.ServerFlag) {
            try {
                return RemoteMethodServer.getDefault().invoke("getValue", MBAUtil.class.getName(), null, new Class[] { Persistable.class, Locale.class, String.class },new Object[] { targetObj, locale, ibaName });
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            try {
                LWCNormalizedObject obj = new LWCNormalizedObject(targetObj, null, locale, null);
                obj.load(ibaName);
                ibaValue = obj.get(ibaName);
            } catch (WTException e) {
                    e.printStackTrace();
            }
       }
      return ibaValue;
    }


}
