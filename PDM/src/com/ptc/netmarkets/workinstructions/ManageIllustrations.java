package com.ptc.netmarkets.workinstructions;

import com.infoengine.object.factory.Element;
import com.ptc.core.components.util.OidHelper;
import com.ptc.core.foundation.associativity.AssociativityProperties;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.wvs.server.ui.UIHelper;
import com.ptc.wvs.server.util.WVSContentHelper;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import org.apache.log4j.Logger;
import wt.content.*;
import wt.fc.*;
import wt.log4j.LogR;
import wt.method.RemoteMethodServer;
import wt.preference.PreferenceHelper;
import wt.preference.PreferenceService2;
import wt.representation.*;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.viewmarkup.*;

public class ManageIllustrations
{

    public ManageIllustrations(Element element1)
    {
        illustrationURLs = new Vector();
        element = element1;
        getRepresentations();
    }

    private static Persistable getOid(Element element1)
    {
        try {
            Persistable persistable;
            NmOid nmoid = OidHelper.getNmOid(element1);
            WTReference wtreference = nmoid.getWtRef();
            persistable = wtreference.getObject();
            return persistable;
            
        } catch (WTRuntimeException e) {
            mpmlLogger.error((new StringBuilder()).append("Converting a ufid to an oid failed: ").append(e.getMessage()).toString());
        } catch (WTException e) {
            mpmlLogger.error((new StringBuilder()).append("Converting a ufid to an oid failed: ").append(e.getMessage()).toString());
        }
        return null;
    }

    public String[] getMarkupData(String s, Locale locale)
    {
        Class aclass[];
        Object aobj[];
        if(SERVER)
            return UIHelper.getMarkupData(s, locale);
        aclass = (new Class[] {
            String.class, Locale.class
        });
        aobj = (new Object[] {
            s, locale
        });
        try {
            return (String[])(String[])RemoteMethodServer.getDefault().invoke("getMarkupData", UIHelper.class.getName(), null, aclass, aobj);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getRepresentations()
    {
        TreeMap treemap;
        RepresentationService representationservice;
        Representable representable;
        treemap = new TreeMap();
        representationservice = RepresentationHelper.service;
        representable = (Representable)getOid(element);
        if(representable == null)
        {
            mpmlLogger.info("Could not get the representation.");
            illustrationURLs = null;
            return;
        }
        Representation representation =null;
        try {
            representation = representationservice.getDefaultRepresentation(representable);
            if(representation == null)
            {
                mpmlLogger.debug("No representations found for this operation");
                illustrationURLs = null;
                return;
            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try
        {
            for(QueryResult queryresult = ViewMarkUpHelper.service.getMarkUps((Viewable)representation); queryresult.hasMoreElements();)
            {
                WTMarkUp wtmarkup = (WTMarkUp)queryresult.nextElement();
                String s = wtmarkup.getName();
                QueryResult queryresult1 = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
                while(queryresult1.hasMoreElements()) 
                {
                    ContentItem contentitem = (ContentItem)queryresult1.nextElement();
                    if(contentitem instanceof ApplicationData)
                    {
                        ApplicationData applicationdata = (ApplicationData)contentitem;
                        String s1 = (new StringBuilder()).append(pattern).append(s).append(extension).toString();
                        if(applicationdata.getFileName().equalsIgnoreCase(s1))
                        {
                            String as[] = getMarkupData(wtmarkup.getPersistInfo().getObjectIdentifier().getStringValue(), WTContext.getContext().getLocale());
                            String s2 = as[14];
                            URL url = WVSContentHelper.getDownloadURL(applicationdata, representation);
                            String as1[] = {
                                url.toString(), s2, s
                            };
                            treemap.put(s, as1);
                        }
                    }
                }
            }

            Object obj;
            for(Iterator iterator = treemap.keySet().iterator(); iterator.hasNext(); illustrationURLs.add(treemap.get(obj)))
                obj = iterator.next();

        }
        catch(WTException wtexception)
        {
            illustrationURLs = null;
            mpmlLogger.error((new StringBuilder()).append("Exception when getting representations for work instructions: ").append(wtexception.getMessage()).toString());
        }
        catch(Exception exception)
        {
            mpmlLogger.error((new StringBuilder()).append("Exception when getting representations for work instructions: ").append(exception.getMessage()).toString());
            illustrationURLs = null;
        }
        return;
    }

    public Vector getIllustrationURLs()
    {
        return illustrationURLs;
    }

    public String getMarkUpNameFromAdditionalInfo(WTMarkUp wtmarkup)
    {
        if(wtmarkup.getName() == null || wtmarkup.getName().length() == 0)
            return wtmarkup.getName();
        for(StringTokenizer stringtokenizer = new StringTokenizer(wtmarkup.getAdditionalInfo(), "<@@>"); stringtokenizer.hasMoreTokens();)
        {
            String s = stringtokenizer.nextToken();
            if(s.startsWith((new StringBuilder()).append(wtmarkup.getName()).append(" ").toString()))
                return s;
        }

        return wtmarkup.getName();
    }

    private static final boolean SERVER;
    private static final int MARKUP_LOAD_URL = 14;
    private Element element;
    private static String pattern = AssociativityProperties.getDefault().getProperty("com.ptc.windchill.mpml.WorkInstructionIllustrationNamePattern");
    private static String extension;
    private Vector illustrationURLs;
    private static final Logger mpmlLogger = LogR.getLogger(ManageIllustrations.class.getName());
    private static final String DELIMITER = "<@@>";

    static 
    {
        SERVER = RemoteMethodServer.ServerFlag;
        extension = null;
        try
        {
            extension = (String)PreferenceHelper.service.getValue("com/ptc/windchill/mpml/pv/image/Extension", "WINDCHILL");
            if(extension == null)
                extension = ".gif";
            if(!extension.startsWith("."))
                extension = (new StringBuilder()).append(".").append(extension).toString();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            extension = ".gif";
        }
    }
}
