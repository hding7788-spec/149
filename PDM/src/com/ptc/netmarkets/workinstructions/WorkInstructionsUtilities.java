package com.ptc.netmarkets.workinstructions;

import com.infoengine.SAK.IeService;
import com.infoengine.object.factory.Att;
import com.infoengine.object.factory.Element;
import com.infoengine.object.factory.Group;
import com.ptc.core.components.util.OidHelper;
import com.ptc.core.foundation.filter.common.NavigationCriteriaHolder;
import com.ptc.core.foundation.filter.server.NavigationCriteriaHolderServerHelper;
import com.ptc.core.meta.common.AttributeIdentifier;
import com.ptc.core.meta.common.DataTypesUtility;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmURLFactoryBean;
import com.ptc.netmarkets.workinstructions.WorkInstructionsConfigSpecParams.Container;
import com.ptc.windchill.mpml.MPMLinkProperties;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationHolder;
import com.ptc.windchill.mpml.resource.MPMWorkCenter;
import java.beans.PropertyVetoException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import wt.associativity.NCServerHolder;
import wt.change2.Changeable2;
import wt.change2.changeStatus.ChangeStatus;
import wt.change2.changeStatus.ChangeStatusHelper;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.FormatContentHolder;
import wt.content.URLData;
import wt.doc.WTDocument;
import wt.eff.EffContext;
import wt.effectivity.ConfigurationItem;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.IconDelegate;
import wt.fc.IconDelegateFactory;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTSet;
import wt.federation.FederationUtilities;
import wt.filter.NavCriteriaContext;
import wt.filter.NavigationCriteria;
import wt.filter.NavigationCriteriaHelper;
import wt.help.HelpLinkHelper;
import wt.inf.container.WTContainer;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.objecttag.ObjectTagFilter;
import wt.objecttag.ObjectTagFilterItem;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.part.WTPartBaselineConfigSpec;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartEffectivityConfigSpec;
import wt.part.WTPartStandardConfigSpec;
import wt.session.SessionHelper;
import wt.units.FloatingPointWithUnits;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.baseline.Baseline;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.Variation1;
import wt.vc.views.Variation2;
import wt.vc.views.View;

public class WorkInstructionsUtilities {
    public static String globalInstance = null;
    public static final String globalHRef = "../../../";
    public static final String ppValue = MPMLinkProperties.getDefault().getProperty("com.ptc.windchill.mpml.WorkInstructionIllustrationPpValue");
    public static final String fileExtensionsList = MPMLinkProperties.getDefault().getProperty("com.ptc.windchill.mpml.WorkInstructionIllustrationFileExtensionAllowed");
    private static final Logger mpmlLogger = LogR.getLogger(WorkInstructionsUtilities.class.getName());

    public WorkInstructionsUtilities() {
    }

    public static String getOid(Element var0) {
        try {
            NmOid var1 = OidHelper.getNmOid(var0);
            ObjectIdentifier var2 = var1.getOid();
            String var3 = var2.getStringValue();
            return var3;
        } catch (Exception var4) {
            mpmlLogger.error("Converting an obid to an oid failed: " + var4.getMessage());
            return null;
        }
    }

    public static String sortOrder(Vector var0) {
        try {
            for(int var1 = 0; var1 < var0.size(); ++var1) {
                Integer.parseInt((String)var0.elementAt(var1));
            }

            return "NUMERIC";
        } catch (NumberFormatException var2) {
            return "ALPHA";
        }
    }

    public static String getTime(String var0, String var1) {
        try {
            boolean var3 = false;
            int var9 = var0.indexOf("s");
            String var2 = var0.substring(0, var9);
            double var4 = Double.valueOf(var2.trim());
            DecimalFormat var6 = new DecimalFormat("0.00");
            if (var1 != null && !var1.equals("") && !var1.equalsIgnoreCase("s")) {
                if (var1.equalsIgnoreCase("m")) {
                    var4 /= 60.0;
                    return var6.format(var4) + " " + var1;
                } else {
                    var4 /= 3600.0;
                    return var6.format(var4) + " " + var1;
                }
            } else {
                return var6.format(var4) + " " + var1;
            }
        } catch (NumberFormatException var7) {
            return var0;
        } catch (Exception var8) {
            return var0;
        }
    }

    public static String getGlobalHRef() {
        return "../../../";
    }

    public static String stringToHTMLString(String var0) {
        if (var0 == null) {
            return "";
        } else {
            StringBuffer var1 = new StringBuffer(var0.length());
            boolean var2 = false;
            int var3 = var0.length();

            for(int var5 = 0; var5 < var3; ++var5) {
                char var4 = var0.charAt(var5);
                if (var4 == ' ') {
                    if (var2) {
                        var2 = false;
                        var1.append("&nbsp;");
                    } else {
                        var2 = true;
                        var1.append(' ');
                    }
                } else {
                    var2 = false;
                    if (var4 == '"') {
                        var1.append("&quot;");
                    } else if (var4 == '&') {
                        var1.append("&amp;");
                    } else if (var4 == '<') {
                        var1.append("&lt;");
                    } else if (var4 == '>') {
                        var1.append("&gt;");
                    } else if (var4 == '\n') {
                        var1.append("<br>");
                    } else {
                        int var6 = '\uffff' & var4;
                        if (var6 < 160) {
                            var1.append(var4);
                        } else {
                            var1.append("&#");
                            var1.append((new Integer(var6)).toString());
                            var1.append(';');
                        }
                    }
                }
            }

            return var1.toString();
        }
    }

    public static Object getObject(String var0) {
        Persistable var1 = null;

        try {
            ReferenceFactory var2 = new ReferenceFactory();
            var1 = var2.getReference(var0).getObject();
            return var1;
        } catch (WTException var3) {
            return var1;
        }
    }

    public static String getImageUrl(Object var0) throws Exception {
        URL var1 = null;
        if (var0 instanceof FormatContentHolder) {
            FormatContentHolder var2 = (FormatContentHolder)var0;

            try {
                var2 = (FormatContentHolder)ContentHelper.service.getContents(var2);
            } catch (Exception var12) {
                var12.printStackTrace();
                throw new WTException(var12);
            }

            ContentItem var3 = ContentHelper.getPrimary(var2);

            try {
                if (var3 instanceof ApplicationData) {
                    ApplicationData var4 = (ApplicationData)var3;
                    String[] var5 = fileExtensionsList.split(",");
                    String var6 = var4.getFileName();
                    String var7 = var6.substring(var6.lastIndexOf("."));
                    String[] var8 = var5;
                    int var9 = var5.length;

                    for(int var10 = 0; var10 < var9; ++var10) {
                        String var11 = var8[var10];
                        if (var11 != null && var11.trim().length() != 0 && var7.equalsIgnoreCase(var11)) {
                            var1 = ContentHelper.getDownloadURL((ContentHolder)var0, var4, false, (String)null);
                            return var1.toString();
                        }
                    }
                }
            } catch (WTException var13) {
                var13.printStackTrace();
                throw new WTException(var13);
            }
        }

        return null;
    }

    public static String getDocumentDownloadUrl(Object var0) throws Exception {
        URL var1 = null;
        if (var0 instanceof FormatContentHolder) {
            FormatContentHolder var2 = (FormatContentHolder)var0;

            try {
                var2 = (FormatContentHolder)ContentHelper.service.getContents(var2);
            } catch (Exception var6) {
                var6.printStackTrace();
                throw new WTException(var6);
            }

            ContentItem var3 = ContentHelper.getPrimary(var2);

            try {
                if (var3 instanceof ApplicationData) {
                    ApplicationData var4 = (ApplicationData)var3;
                    var1 = ContentHelper.service.getDownloadURL((ContentHolder)var0, var4);
                    return var1.toString();
                }

                if (var3 instanceof URLData) {
                    return ((URLData)var3).getUrlLocation();
                }
            } catch (WTException var5) {
                var5.printStackTrace();
                throw new WTException(var5);
            }
        }

        return null;
    }

    public static String getAttributeValueFromAGroup(IeService var0, String var1, int var2, String var3) throws Exception {
        try {
            return var0.getAttributeValue(var1, var2, var3);
        } catch (Exception var5) {
            throw new Exception();
        }
    }

    public static Vector<String[]> getDocsImagesUrls(IeService var0, NmURLFactoryBean var1, String var2) throws WTException {
        Vector var3 = new Vector();

        try {
            Group var4 = var0.getGroup(var2);
            String var5 = null;

            for(int var6 = 0; var6 < var4.getElementCount(); ++var6) {
                Element var7 = var4.getElementAt(var6);
                Att var8 = var7.getAtt("com.ptc.windchill.mpml.MPMDocumentReferenceLink.illustration");
                if (var8 == null) {
                    var8 = var7.getAtt("com.ptc.windchill.mpml.MPMDocumentDescribeLink.illustration");
                }

                var5 = var8.getValue().toString();
                if (Boolean.valueOf(var5)) {
                    String var9 = var0.getAttributeValue(var2, var6, "obid");
                    String var10 = getOid(var9);
                    Object var11 = getObject(var10);
                    String var12 = getImageUrl(var11);
                    String var13 = var0.getAttributeValue(var2, var6, "name");
                    if (var12 != null) {
                        String[] var14 = new String[]{var12, var12, var13};
                        var3.add(var14);
                    }
                }
            }

            return var3;
        } catch (Exception var15) {
            var15.printStackTrace();
            throw new WTException(var15);
        }
    }

    public static Vector getDocAndAnnotationIllustrations(Vector<String[]> var0, Vector<String[]> var1) {
        if (var0 != null) {
            if (var1 != null) {
                var0.addAll(var1);
            }

            return var0;
        } else {
            return var1;
        }
    }

    public static void replaceAllWithLocalizedValues(IeService var0, String var1, String var2) {
        Group var3 = var0.getGroup(var1);
        if (var3 != null) {
            Group var4 = var0.getGroup(var2);
            if (var4 != null) {
                for(int var5 = 0; var5 < var3.getElementCount(); ++var5) {
                    for(int var6 = 0; var6 < var4.getElementCount(); ++var6) {
                        Element var7 = var4.getElementAt(var6);
                        String var8 = var7.getAtt("name").getValue().toString();
                        Object var9 = var3.getAttributeValue(var5, var8);
                        if (var9 != null) {
                            Att var10 = var7.getAtt("value");
                            if (var10 != null) {
                                Att var11 = var7.getAtt("localizedValue");
                                if (var11 != null) {
                                    for(int var12 = 0; var12 < var11.getValueCount(); ++var12) {
                                        if (var9.equals(var10.getRawValueAt(var12))) {
                                            var3.setAttributeValue(var5, var8, var11.getRawValueAt(var12));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    public static void replaceWithLocalizedValues(IeService var0, String var1, String var2, String var3, String var4) {
        Group var5 = var0.getGroup(var3);
        Group var6 = var0.getGroup(var1);
        if (var6 != null) {
            for(int var7 = 0; var7 < var6.getElementCount(); ++var7) {
                if (var5 == null) {
                    return;
                }

                for(int var8 = 0; var8 < var5.getElementCount(); ++var8) {
                    Element var9 = var5.getElementAt(var8);
                    String var10 = var9.getAtt("name").getValue().toString();
                    if (var10.equalsIgnoreCase(var4)) {
                        Object var11 = var6.getAttributeValue(var7, var2);
                        if (var11 != null) {
                            Att var12 = var9.getAtt("value");
                            if (var12 != null) {
                                Att var13 = var9.getAtt("localizedValue");
                                if (var13 != null) {
                                    for(int var14 = 0; var14 < var13.getValueCount(); ++var14) {
                                        if (var11.equals(var12.getRawValueAt(var14))) {
                                            var6.setAttributeValue(var7, var2, var13.getRawValueAt(var14));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public static Object getLocalizedValue(IeService var0, Object var1, String var2, String var3) {
        Group var4 = var0.getGroup(var2);

        for(int var5 = 0; var5 < var4.getElementCount(); ++var5) {
            Element var6 = var4.getElementAt(var5);
            String var7 = var6.getAtt("name").getValue().toString();
            if (var7.equalsIgnoreCase(var3)) {
                Att var8 = var6.getAtt("value");
                if (var8 != null) {
                    Att var9 = var6.getAtt("localizedValue");
                    if (var9 != null) {
                        for(int var10 = 0; var10 < var9.getValueCount(); ++var10) {
                            if (var1 instanceof String) {
                                if (((String)var1).equalsIgnoreCase((String)var8.getRawValueAt(var10))) {
                                    return var9.getRawValueAt(var10);
                                }
                            } else if (var1.equals(var8.getRawValueAt(var10))) {
                                return var9.getRawValueAt(var10);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public static String getLocalizedValue(String var0, String var1) {
        return var1 != null && var1.trim().length() != 0 ? WTMessage.getLocalizedMessage(var0, var1, (Object[])null) : "";
    }

    public static HashMap<String, String> getModifierUserName(IeService var0, String var1) {
        HashMap var2 = null;

        try {
            Group var3 = var0.getGroup(var1);
            String var4 = "";

            for(int var5 = 0; var5 < var3.getElementCount(); ++var5) {
                String var6 = var0.getAttributeValue(var1, var5, "obid");
                String var7 = getOid(var6);
                Object var8 = getObject(var7);
                if (var8 instanceof RevisionControlled) {
                    WTPrincipalReference var9 = ((RevisionControlled)var8).getModifier();
                    if (var9 != null) {
                        var4 = var9.getFullName();
                    }

                    if (var2 == null) {
                        var2 = new HashMap(5);
                    }

                    var2.put(var6, var4);
                }
            }
        } catch (Exception var10) {
        }

        return var2;
    }

    public static ConfigSpec getConfigSpec(String var0, HashMap<String, String> var1) throws Exception {
        Object var2 = null;

        try {
            if (!var0.equals("") && !var0.equalsIgnoreCase("LATEST")) {
                if (!var0.equalsIgnoreCase("BASELINE")) {
                    String var4;
                    Variation2 var8;
                    String var12;
                    Variation1 var13;
                    String var14;
                    if (var0.equalsIgnoreCase("EFFECTIVITY")) {
                        WTPartEffectivityConfigSpec var10 = WTPartEffectivityConfigSpec.newWTPartEffectivityConfigSpec();
                        var4 = (String)var1.get("SELECTBY_CONFIG_ITEM_REF");
                        if (var4 != null && !var4.equals("")) {
                            var10.setEffectiveConfigItem((ConfigurationItem)FederationUtilities.getObjectByUfid(var4));
                        }

                        var4 = (String)var1.get("SELECTBY_CONTEXT_REF");
                        if (var4 != null && !var4.equals("")) {
                            var10.setEffectiveContext((EffContext)FederationUtilities.getObjectByUfid(var4));
                        }

                        var4 = (String)var1.get("SELECTBY_DATE");
                        if (var4 != null && !var4.equals("")) {
                            DateFormat var5 = DateFormat.getDateInstance();
                            Date var6 = var5.parse(var4);
                            Calendar var7 = Calendar.getInstance(WTContext.getContext().getTimeZone(), WTContext.getContext().getLocale());
                            var10.setEffectiveDate(new Timestamp(var6.getTime() - (long)var7.get(15) - (long)var7.get(16)));
                        }

                        var4 = (String)var1.get("SELECTBY_UNIT");
                        if (var4 != null && !var4.equals("")) {
                            var10.setEffectiveUnit(var4);
                        }

                        var4 = (String)var1.get("SELECTBY_VIEW_REF");
                        if (var4 != null && !var4.equals("")) {
                            var10.setView((View)FederationUtilities.getObjectByUfid(var4));
                        }

                        var12 = (String)var1.get("SELECTBY_VARIATION1");
                        var13 = "".equals(var12) ? null : Variation1.toVariation1(var12);
                        if (var13 != null) {
                            var10.setVariation1(var13);
                        }

                        var14 = (String)var1.get("SELECTBY_VARIATION2");
                        var8 = "".equals(var14) ? null : Variation2.toVariation2(var14);
                        if (var8 != null) {
                            var10.setVariation2(var8);
                        }

                        var2 = var10;
                    } else {
                        if (!var0.equalsIgnoreCase("STANDARD") && !var0.equalsIgnoreCase("STANDARD_PART")) {
                            throw new Exception();
                        }

                        WTPartStandardConfigSpec var11 = WTPartStandardConfigSpec.newWTPartStandardConfigSpec();
                        var4 = (String)var1.get("SELECTBY_VIEW_REF");
                        if (var4 != null && !var4.equals("")) {
                            var11.setView((View)FederationUtilities.getObjectByUfid(var4));
                        }

                        var12 = (String)var1.get("SELECTBY_VARIATION1");
                        var13 = "".equals(var12) ? null : Variation1.toVariation1(var12);
                        if (var13 != null) {
                            var11.setVariation1(var13);
                        }

                        var14 = (String)var1.get("SELECTBY_VARIATION2");
                        var8 = "".equals(var14) ? null : Variation2.toVariation2(var14);
                        if (var8 != null) {
                            var11.setVariation2(var8);
                        }

                        var4 = (String)var1.get("SELECTBY_LIFECYCLE_STATE");
                        if (var4 != null && !var4.equals("")) {
                            var11.setLifeCycleState(State.toState(var4));
                        }

                        var4 = (String)var1.get("SELECTBY_INCLUDE_WORKING");
                        if (var4 != null && !var4.equals("")) {
                            if (!var4.equalsIgnoreCase("true") && !var4.equalsIgnoreCase("yes") && !var4.equalsIgnoreCase("on")) {
                                if (!var4.equalsIgnoreCase("false") && !var4.equalsIgnoreCase("no") && !var4.equalsIgnoreCase("off")) {
                                    throw new Exception();
                                }

                                var11.setWorkingIncluded(false);
                            } else {
                                var11.setWorkingIncluded(true);
                            }
                        }

                        var2 = var11;
                    }
                } else {
                    String var3 = (String)var1.get("SELECTBY_BASELINE_REF");
                    if (var3 != null && var3.equals("")) {
                        var3 = (String)var1.get("SELECTBY_PARAM");
                        if (var3 == null || var3.equals("")) {
                            throw new Exception();
                        }
                    }

                    var2 = WTPartBaselineConfigSpec.newWTPartBaselineConfigSpec((Baseline)FederationUtilities.getObjectByUfid(var3));
                }
            } else {
                var2 = new LatestConfigSpec();
            }

            return (ConfigSpec)var2;
        } catch (Exception var9) {
            return null;
        }
    }

    public static String getProcessPlanOid(String var0, NCServerHolder var1) {
        try {
            MPMOperation var2 = (MPMOperation)getObject(var0);
            MPMProcessPlan var3 = MPMProcessPlanHelper.service.getMPMProcessPlan(var2, var1);
            if (var3 == null) {
                var3 = MPMProcessPlanHelper.service.getMPMProcessPlan(var2, NCServerHolder.makeForLatestConfigSpec());
            }

            Persistable var4 = ObjectReference.newObjectReference(var3).getObject();
            return var4.toString();
        } catch (Exception var5) {
            return null;
        }
    }

    public static WTSet getAllConfigSpecAssemblies(MPMProcessPlan var0, ConfigSpec var1) {
        try {
            NCServerHolder var2 = NCServerHolder.makeForConfigSpec(var1);
            return MPMProcessPlanHelper.service.getAllConfigSpecAssemblies(var0, var2);
        } catch (Exception var3) {
            return null;
        }
    }

    public static HashMap<String, HashMap<String, String>> getLocalizedFloatingPointUnitAttributesForOPerations(IeService var0, String var1, String[] var2, String[] var3, Locale var4) throws Exception {
        HashMap var5 = new HashMap();
        Group var6 = var0.getGroup(var1);
        if (var6 == null) {
            return null;
        } else {
            for(int var7 = 0; var7 < var6.getElementCount(); ++var7) {
                Element var8 = var6.getElementAt(var7);
                String var9 = var8.getAtt("obid").getValue().toString();
                HashMap var10 = new HashMap();
                if (var8.getAtt("standard") == null) {
                    for(int var11 = 0; var11 < var2.length; ++var11) {
                        Att var12 = var8.getAtt(var2[var11]);
                        if (var12 != null) {
                            Object var13 = var12.getRawValue();
                            String var14 = "";
                            if (!(var13 instanceof FloatingPointWithUnits)) {
                                if (var13 instanceof String) {
                                    var14 = getLocalizedFloatingPointUnitAttrValue((String)var13, var3[var11], var4);
                                }
                            } else {
                                var14 = getLocalizedFloatingPointUnitAttrValue((FloatingPointWithUnits)var13, var3[var11], var4);
                            }

                            var10.put(var2[var11], var14);
                        }
                    }

                    var5.put(var9, var10);
                }
            }

            return var5;
        }
    }

    public static String getLocalizedFloatingPointUnitAttrValue(FloatingPointWithUnits var0, String var1, Locale var2) {
        try {
            return DataTypesUtility.toString(var0, var1, 0, var2);
        } catch (Exception var4) {
            var4.printStackTrace();
            return var0.toString();
        }
    }

    public static String getLocalizedFloatingPointUnitAttrValue(String var0, String var1, Locale var2) {
        try {
            FloatingPointWithUnits var3 = FloatingPointWithUnits.valueOf(var0);
            return DataTypesUtility.toString(var3, var1, var2);
        } catch (Exception var4) {
            var4.printStackTrace();
            return var0;
        }
    }

    public static String getLocalizedFloatingPointUnitAttrValue(String var0, String var1, String var2) {
        try {
            FloatingPointWithUnits var3 = FloatingPointWithUnits.valueOf(var0);
            if (var1 != null && var1.length() > 0) {
                int var4 = Integer.valueOf(var1.trim());
                return DataTypesUtility.toString(var3, var2, var4, WTContext.getContext().getLocale());
            } else {
                return DataTypesUtility.toString(var3, var2, WTContext.getContext().getLocale());
            }
        } catch (Exception var5) {
            return var0;
        }
    }

    public static String getPendingStatusIconString(String var0) {
        String var1 = "";
        if (var0 != null && !var0.equals("")) {
            try {
                Object var2 = getObject(var0);
                if (var2 instanceof Changeable2) {
                    Changeable2 var3 = (Changeable2)var2;
                    if (var3.isHasPendingChange()) {
                        var1 = "../../../" + ChangeStatusHelper.getChangeStatusIcon(ChangeStatus.PENDING_CHANGE, true);
                    }
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

        return var1;
    }

    public static String getPendingTooltip() {
        String var0 = "";

        try {
            var0 = ChangeStatus.PENDING_CHANGE.getDisplay(SessionHelper.getLocale());
        } catch (WTException var2) {
            var2.printStackTrace();
        }

        return var0;
    }

    public static Group getPartInfoParamListGroup(Group var0, HashMap var1) {
        Element var2 = new Element();
        var2.addAtt(new Att("partIterations", var1.get("partIterations")));
        var2.addAtt(new Att("rolledUpQuantity", var1.get("summedQty")));
        var2.addAtt(new Att("quantityUnit", var1.get("quantity.unit")));
        var2.addAtt(new Att("assemblyContext", var1.get("assemblyContext")));
        var2.addAtt(new Att("partLineNumbers", var1.get("lineNumber.value")));
        var0.addElement(var2);
        return var0;
    }

    public static HashMap<ArrayList, ArrayList> getColumnOrderMap(String var0) {
        HashMap var1 = new HashMap(1);
        ArrayList var2 = new ArrayList();
        ArrayList var3 = new ArrayList();
        String[] var4 = var0.split(",");
        if (var4 != null && var4.length > 0) {
            for(int var8 = 0; var8 < var4.length; ++var8) {
                if (var4[var8].contains(" ")) {
                    String[] var9 = var4[var8].split(" ");
                    var2.add(var9[0].trim());
                    var3.add("none");

                    for(int var7 = 1; var7 < var9.length; ++var7) {
                        var2.add(var9[var7].trim());
                        var3.add(var9[var7 - 1].trim());
                    }
                } else {
                    var2.add(var4[var8].trim());
                    var3.add("none");
                }
            }

            var1.put(var2, var3);
        } else {
            String[] var5 = var0.split(" ");
            if (var5 != null && var5.length > 0) {
                var2.add(var5[0].trim());
                var3.add("none");

                for(int var6 = 1; var6 < var5.length; ++var6) {
                    var2.add(var5[var6].trim());
                    var3.add(var5[var6 - 1]);
                }

                var1.put(var2, var3);
            }
        }

        return var1;
    }

    public static int getMaxRowsPerPage(String var0) {
        if (var0.equals("A4")) {
            return MPMLinkProperties.maxRowsPerPageA4;
        } else if (var0.equals("A3")) {
            return MPMLinkProperties.maxRowsPerPageA3;
        } else if (var0.equals("LETTER")) {
            return MPMLinkProperties.maxRowsPerPageLetter;
        } else {
            return var0.equals("LEDGER") ? MPMLinkProperties.maxRowsPerPageLedger : 0;
        }
    }

    public static int getColumnsNumberPerPage(String var0) {
        if (var0.equals("A4")) {
            return MPMLinkProperties.maxColumnPerPage_A4;
        } else if (var0.equals("A3")) {
            return MPMLinkProperties.maxColumnPerPage_A3;
        } else if (var0.equals("LETTER")) {
            return MPMLinkProperties.maxColumnPerPage_Letter;
        } else {
            return var0.equals("LEDGER") ? MPMLinkProperties.maxColumnPerPage_Ledger : 0;
        }
    }

    public static String getColumnOrderString(String var0) {
        if (var0.equals("A4")) {
            return MPMLinkProperties.wiColumnOrderA4;
        } else if (var0.equals("A3")) {
            return MPMLinkProperties.wiColumnOrderA3;
        } else if (var0.equals("LETTER")) {
            return MPMLinkProperties.wiColumnOrderLetter;
        } else {
            return var0.equals("LEDGER") ? MPMLinkProperties.wiColumnOrderLedger : null;
        }
    }

    public static ArrayList<String> getTableJSPFList(String var0) {
        String var1 = null;
        ArrayList var2 = new ArrayList();
        if (var0.equals("A4")) {
            var1 = MPMLinkProperties.wiColumnTableJSPFOrderA4;
        } else if (var0.equals("A3")) {
            var1 = MPMLinkProperties.wiColumnTableJSPFOrderA3;
        } else if (var0.equals("LETTER")) {
            var1 = MPMLinkProperties.wiColumnTableJSPFOrderLetter;
        } else if (var0.equals("LEDGER")) {
            var1 = MPMLinkProperties.wiColumnTableJSPFOrderLedger;
        }

        if (var1 != null && var1.length() > 0) {
            String[] var3 = var1.split(",");
            String[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String var7 = var4[var6];
                var2.add(var7);
            }

            return var2;
        } else {
            return null;
        }
    }

    public static String getProcessPlanOid(NCServerHolder var0, WorkInstructionsConfigSpecParams var1) {
        String var2 = null;
        if (var1.getPpOid() != null && !var1.getPpOid().equals(ppValue)) {
            var2 = var1.getPpOid();
        } else if (Container.PROCESS_PLAN.equals(var1.getContainer())) {
            var2 = var1.getObjOid();
        } else if (Container.OPERATION.equals(var1.getContainer())) {
            var2 = getProcessPlanOid(var1.getObjOid(), var0);
        } else {
            var2 = null;
        }

        return var2;
    }

    public static Group getAssmInfoGroup(String var0, NCServerHolder var1) {
        MPMProcessPlan var2 = (MPMProcessPlan)getObject(var0);
        Group var3 = new Group("serviceArguments");
        Element var4 = new Element();
        var4.addAtt(new Att("ppObject", var2));
        var4.addAtt(new Att("configSpecSetObject", var1));
        var3.addElement(var4);
        return var3;
    }

    public static synchronized Properties getOpLabelProperties(IeService var0) {
        Properties var1 = new Properties();
        var1.setProperty("name", var0.getAttributeValue("CURRENT_OPERATION", 0, "name"));
        String var2 = null;

        try {
            if (var0.getAttributeValue("CURRENT_OPERATION", 0, "com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink.operationLabel") == null) {
                var2 = var0.getAttributeValue("CURRENT_OPERATION", 0, "operationLabel");
            } else {
                var2 = var0.getAttributeValue("CURRENT_OPERATION", 0, "com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink.operationLabel");
            }
        } catch (Exception var4) {
            var2 = var0.getAttributeValue("CURRENT_OPERATION", 0, "operationLabel");
        }

        var1.setProperty("operationLabel", var2);
        var1.setProperty("number", var0.getAttributeValue("CURRENT_OPERATION", 0, "number"));
        return var1;
    }

    public static synchronized Properties getParentPropertiesForSubOP(IeService var0) {
        String var1 = null;
        Properties var2 = new Properties();

        try {
            if (var0.getAttributeValue("CURRENT_OPERATION", 0, "com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink.operationLabel") == null) {
                var1 = var0.getAttributeValue("CURRENT_OPERATION", 0, "operationLabel");
            } else {
                var1 = var0.getAttributeValue("CURRENT_OPERATION", 0, "com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink.operationLabel");
            }
        } catch (Exception var4) {
            var1 = var0.getAttributeValue("CURRENT_OPERATION", 0, "operationLabel");
        }

        if (var1 != null) {
            var2.setProperty("operationLabel", var1);
        }

        var2.setProperty("name", var0.getAttributeValue("CURRENT_OPERATION", 0, "name"));
        var2.setProperty("number", var0.getAttributeValue("CURRENT_OPERATION", 0, "number"));
        return var2;
    }

    public static synchronized Properties getSequenceProperties(IeService var0, Hashtable var1) {
        Properties var2 = new Properties();
        TypeInstanceIdentifier var3 = null;
        Enumeration var4 = var0.getElements("CURRENT_SEQUENCE");
        Element var5 = (Element)var4.nextElement();

        try {
            var3 = TypeIdentifierUtility.getTypeInstanceIdentifier(getOid((String)var5.getValue("com.ptc.windchill.mpml.processplan.sequence.MPMSequenceUsageLink.obid")));
        } catch (Exception var13) {
            var13.printStackTrace();
        }

        var2.setProperty("number", var0.getAttributeValue("CURRENT_SEQUENCE", 0, "number"));
        var2.setProperty("name", var0.getAttributeValue("CURRENT_SEQUENCE", 0, "name"));
        var2.setProperty("category", var0.getAttributeValue("CURRENT_SEQUENCE", 0, "category"));
        String var6 = null;
        AttributeIdentifier[] var8;
        int var10;
        if (var3 != null && var5.getAttributeIdentifiers("branchingOperationUsageLinkID") != null && var5.getAttributeIdentifiers("branchingOperationUsageLinkID").length > 0) {
            AttributeIdentifier[] var7 = var5.getAttributeIdentifiers("branchingOperationUsageLinkID");
            var8 = var7;
            int var9 = var7.length;

            for(var10 = 0; var10 < var9; ++var10) {
                AttributeIdentifier var11 = var8[var10];
                if (var11.getContext().isEquivalentTypeInstanceIdentifier(var3)) {
                    var6 = (String)var5.getValue(var11);
                    break;
                }
            }
        }

        if (var6 != null) {
            Properties var14 = (Properties)var1.get(var6);
            if (var14 != null) {
                var2.setProperty("branchingOperationLabel", var14.getProperty("operationLabel"));
                var2.setProperty("branchingOperationName", var14.getProperty("name"));
                var2.setProperty("branchingOperationNumber", var14.getProperty("number"));
                var2.setProperty("branchingType", var0.getAttributeValue("CURRENT_SEQUENCE", 0, "branchingType"));
            }
        }

        String var15 = null;
        if (var3 != null && var5.getAttributeIdentifiers("returnOperationUsageLinkID") != null && var5.getAttributeIdentifiers("returnOperationUsageLinkID").length > 0) {
            var8 = var5.getAttributeIdentifiers("returnOperationUsageLinkID");
            AttributeIdentifier[] var17 = var8;
            var10 = var8.length;

            for(int var18 = 0; var18 < var10; ++var18) {
                AttributeIdentifier var12 = var17[var18];
                if (var12.getContext().isEquivalentTypeInstanceIdentifier(var3)) {
                    var15 = (String)var5.getValue(var12);
                    break;
                }
            }
        }

        if (var15 != null) {
            Properties var16 = (Properties)var1.get(var15);
            if (var16 != null) {
                var2.setProperty("returnOperationLabel", var16.getProperty("operationLabel"));
                var2.setProperty("returnOperationName", var16.getProperty("name"));
                var2.setProperty("returnOperationNumber", var16.getProperty("number"));
                var2.setProperty("returnType", var0.getAttributeValue("CURRENT_SEQUENCE", 0, "returnType"));
            }
        }

        return var2;
    }

    public static String[] getUnitListForLocal(String var0) {
        String[] var8 = new String[]{var0, var0, var0, var0, var0, var0, var0};
        return var8;
    }

    public static int[] getColumnNumberList(ArrayList<String> var0) {
        int var1 = 1;
        int[] var2 = new int[var0.size()];

        for(int var3 = 0; var3 < var0.size(); ++var3) {
            if (((String)var0.get(var3)).equals("none")) {
                var2[var3] = var1++;
            } else {
                var2[var3] = var2[var3 - 1];
            }
        }

        return var2;
    }

    public static boolean isPreviousCollumnsPrintDone(ArrayList<String> var0, ArrayList<String> var1, int var2) {
        boolean var3 = true;

        for(int var4 = 0; var4 < var2; ++var4) {
            if (!var1.contains(var0.get(var4))) {
                var3 = false;
                break;
            }
        }

        return var3;
    }

    public static String getHelpLink() throws Exception {
        URL var0 = new URL(HelpLinkHelper.createHelpHREF("ExpMPM_ViewWorkInstruction"));
        return var0.toString();
    }

    public static WTContainer getContainer(WTObject var0, IeService var1) {
        WTContainer var2 = null;
        if (var0 instanceof WTDocument) {
            WTDocument var3 = (WTDocument)var0;
            var2 = var3.getContainer();
        }

        if (var0 instanceof EPMDocument) {
            EPMDocument var4 = (EPMDocument)var0;
            var2 = var4.getContainer();
        }

        return var2;
    }

    public static String getContentIconUrl(WTObject var0, IeService var1, String var2) throws WTException, PropertyVetoException, IllegalAccessException, InvocationTargetException {
        String var3 = null;
        if (var0 instanceof WTDocument) {
            var3 = var1.getAttributeValue("CURRENT_DOCUMENT", 0, "format.standardIconStr");
            var3 = var2 + var3;
        }

        if (var0 instanceof EPMDocument) {
            IconDelegateFactory var4 = IconDelegateFactory.getInstance();
            IconDelegate var5 = var4.getIconDelegate(var0);
            var3 = IconDelegateFactory.getIconResource(var5);
        }

        return var3;
    }

    public static String getPrimaryObjectOid(WorkInstructionsConfigSpecParams var0) {
        String var1 = var0.getObjOid();
        return var1;
    }

    public static String getMasterOid(WorkInstructionsConfigSpecParams var0) {
        String var1 = var0.getObjOid();
        Iterated var2 = (Iterated)getObject(var1);
        Mastered var3 = var2.getMaster();
        String var4 = var3.getPersistInfo().getObjectIdentifier().toString();
        return "OR:" + var4;
    }

    public static MPMWorkCenter getWorkCenter(WorkInstructionsConfigSpecParams var0) {
        String var1 = var0.getObjOid();
        MPMWorkCenter var2 = (MPMWorkCenter)getObject(var1);
        return var2;
    }

    public static MPMProcessPlan getProcessPlan(String var0) {
        MPMProcessPlan var1 = (MPMProcessPlan)getObject(var0);
        return var1;
    }

    public static MPMOperation getOperation(String var0) {
        String var1 = getOid(var0);
        MPMOperation var2 = (MPMOperation)getObject(var1);
        return var2;
    }

    public static String getOid(String var0) {
        try {
            StringTokenizer var1 = new StringTokenizer(var0, ":");
            String var2 = var1.nextToken() + ":" + var1.nextToken() + ":" + var1.nextToken();
            return var2;
        } catch (Exception var3) {
            return var0;
        }
    }

    public static NavigationCriteriaHolder getNavigationCriteriaHolder(Object var0, HttpServletRequest var1) throws WTException, WTPropertyVetoException, UnsupportedEncodingException, ParseException {
        String var2 = var1.getParameter("effType");
        String var3 = var1.getParameter("baseline");
        if (var3 != null && !var3.equals("")) {
            WTArrayList var4 = new WTArrayList();
            var4.add(var0);
            NavigationCriteria var5 = NavigationCriteriaHelper.service.getDefaultNavigationCriteria(var4, (NavCriteriaContext)null);
            var5.getConfigSpecs().clear();
            if (var2 != null && !var2.equals("")) {
                EffectivityContextFindDelegate var6 = (new MPMWorkinstructionDelegateFactory()).getEffectivityContextFindDelegate((MPMOperationHolder)var0);
                String var7 = var6.getEffectivityContextObjectReference((MPMOperationHolder)var0, var1);
                if (var7 != null && var7.contains(":")) {
                    var5.getConfigSpecs().add(buildEffectivityConfigSpec(var1, var7));
                }
            }

            if (var3 != null && !var3.equals("")) {
                var5.getConfigSpecs().add(buildBaselineConfigSpec(var1));
            }

            NavigationCriteriaHolder var8 = NavigationCriteriaHolder.buildNavigationCriteriaHolder(var1.getRemoteAddr(), var1.getSession().getId(), "ptc.cust.ProcessPlanExplorer");
            NavigationCriteriaHolderServerHelper.establishNCID(var8, var5);
            return var8;
        } else {
            return null;
        }
    }

    public static WTPartConfigSpec buildBaselineConfigSpec(HttpServletRequest var0) throws UnsupportedEncodingException, WTPropertyVetoException, WTException {
        String var1 = var0.getParameter("baseline");
        Baseline var2 = (Baseline)getObject(var1);
        if (var2 == null) {
            throw new WTException("Baseline ConfigSPec: " + var1 + "is not persisted");
        } else {
            WTPartBaselineConfigSpec var3 = WTPartBaselineConfigSpec.newWTPartBaselineConfigSpec(var2);
            return WTPartConfigSpec.newWTPartConfigSpec(var3);
        }
    }

    public static WTPartConfigSpec buildEffectivityConfigSpec(HttpServletRequest var0, String var1) throws UnsupportedEncodingException, WTException, WTPropertyVetoException, ParseException {
        String var2 = var0.getParameter("effType");
        String var3 = var0.getParameter("effDate");
        String var4 = var0.getParameter("effUnit");
        String var5 = var0.getParameter("var1");
        String var6 = var0.getParameter("var2");
        String var7 = var0.getParameter("viewRef");
        WTPartEffectivityConfigSpec var8 = WTPartEffectivityConfigSpec.newWTPartEffectivityConfigSpec();
        if (var1 != null && !var1.equals("")) {
            EffContext var9 = (EffContext)FederationUtilities.getObjectByUfid(var1);
            if (var9 == null) {
                throw new WTException("provided Effectivity context: " + var1 + " is not persisted");
            } else {
                var8.setEffectiveContext(var9);
                if (var2 != null && !var2.equals("")) {
                    var8.setEffType(var2);
                    if (var3 != null && !var3.equals("")) {
                        SimpleDateFormat var11 = new SimpleDateFormat("yyyy-mm-dd");
                        Date var10 = var11.parse(var3);
                        var8.setEffectiveDate(new Timestamp(var10.getTime()));
                    }

                    if (var4 != null && !var4.equals("")) {
                        var8.setEffectiveUnit(var4);
                    }

                    if (var5 != null && !var5.equals("")) {
                        var8.setVariation1(Variation1.toVariation1(var5));
                    }

                    if (var6 != null && !var6.equals("")) {
                        var8.setVariation2(Variation2.toVariation2(var6));
                    }

                    if (var7 != null && !var7.equals("")) {
                        View var12 = (View)FederationUtilities.getObjectByUfid(var7);
                        var8.setView(var12);
                    }

                    return WTPartConfigSpec.newWTPartConfigSpec(var8);
                } else {
                    throw new WTException("Effectivity Type is mandatory");
                }
            }
        } else {
            throw new WTException("Effectivity Context must be provided");
        }
    }

    public static String getActiveAssembliesFromPartTag(NCServerHolder var0, WorkInstructionsConfigSpecParams var1) throws WTException {
        String var2 = "";
        if (var0 == null) {
            return var2;
        } else {
            NavigationCriteria var3 = var0.getNavigationCriteria();
            Collection var4 = var3.getFiltersByClass(ObjectTagFilter.class);
            if (var4 != null && !var4.isEmpty()) {
                ArrayList var5 = new ArrayList();
                Iterator var6 = var4.iterator();

                Iterator var9;
                while(var6.hasNext()) {
                    ObjectTagFilter var7 = (ObjectTagFilter)var6.next();
                    List var8 = var7.getObjectTagFilterItemList();
                    var9 = var8.iterator();

                    while(var9.hasNext()) {
                        ObjectTagFilterItem var10 = (ObjectTagFilterItem)var9.next();
                        var5.add(var10.getObjectTaggerRef().toString());
                    }
                }

                String var12 = getProcessPlanOid(var0, var1);
                MPMProcessPlan var13 = (MPMProcessPlan)getObject(var12);
                Collection var14 = MPMProcessPlanHelper.service.getAllConfigSpecAssembliesFromPP(var13, var0);
                var9 = var14.iterator();

                while(var9.hasNext()) {
                    WTPart var11 = (WTPart)var9.next();
                    if (var5.contains(var11.getMaster().toString())) {
                        var2 = var2 + var11.getPersistInfo().getObjectIdentifier().getStringValue() + ",";
                    }
                }
            }

            return var2;
        }
    }
}