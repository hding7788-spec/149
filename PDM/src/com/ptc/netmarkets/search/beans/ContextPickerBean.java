/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10)
// Source File Name:   ContextPickerBean.java

package com.ptc.netmarkets.search.beans;

import com.ptc.core.components.rendering.AbstractGuiComponent;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.SuggestTextBox;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.search.SearchWebConstants;
import com.ptc.netmarkets.search.rendering.guicomponents.RecentlyVisitedContextComboCreator;
import com.ptc.netmarkets.search.rendering.guicomponents.RecentlyVisitedContextComboValueStrategy;
import com.ptc.netmarkets.search.utils.ObjectTypeDisplayNamesCache;
import com.ptc.netmarkets.search.utils.SearchUtils;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.search.server.SearchPreferencesHelper;
import java.util.*;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;
import org.apache.log4j.Logger;
import wt.access.NotAuthorizedException;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.inf.container.WTContainer;
import wt.log4j.LogR;
import wt.recent.RecentlyVisitedHelper;
import wt.recent.RecentlyVisitedService;
import wt.util.*;

// Referenced classes of package com.ptc.netmarkets.search.beans:
//            AbstractPickerBean

public class ContextPickerBean extends AbstractPickerBean
{

    public ContextPickerBean()
    {
        objectType = "wt.fc.Persistable";
        typeComponentId = "context:allSearch";
        customAccessController = "";
        excludeSubTypes = "";
        showTypePicker = "true";
        defaultProITypeComponentId = "ProI.contextPicker";
        showSuggestion = "false";
        extraSuggestAttributes = "";
        suggestMinChars = null;
        suggestServiceKey = "";
        field = null;
        showRecentlyUsed = "";
        showMemberOfCheckBoxes = "";
        showMultiSelectRecentlyUsed = "";
        recentlyUsedListComboBox = null;
        locale = new Locale("en");
        hashmap = null;
        selectedIndex = -1;
        onChangeForRecentlyUsed = "";
        showAllContext = true;
    }

    public void init(PageContext pagecontext, NmCommandBean nmcommandbean)
        throws WTException
    {
        super.init(pagecontext, nmcommandbean);
        if(!isAttributeValueAvailable("pickerTitle"))
        {
            String s = WTMessage.getLocalizedMessage("com.ptc.windchill.enterprise.search.client.searchClientResource", "CONTEXTS_PICKER_TITLE", null, nmcommandbean.getLocale());
            setPickerTitle(s);
        }
        if(!isAttributeValueAvailable("helpSelectorKey"))
            setHelpSelectorKey("ContextPickerHelpSelectorKey");
        setObjectType((String)pagecontext.getAttribute("objectType"));
        setTypeComponentId((String)pagecontext.getAttribute("typeComponentId"));
        setCustomAccessController((String)pagecontext.getAttribute("customAccessController"));
        setExcludeSubTypes((String)pagecontext.getAttribute("excludeSubTypes"));
        setShowTypePicker((String)pagecontext.getAttribute("showTypePicker"));
        setShowSuggestion((String)pagecontext.getAttribute("showSuggestion"));
        setExtraSuggestAttributes((String)pagecontext.getAttribute("extraSuggestAttributes"));
        setSuggestMinChars((String)pagecontext.getAttribute("suggestMinChars"));
        setSuggestServiceKey((String)pagecontext.getAttribute("suggestServiceKey"));
        setLocale(nmcommandbean.getLocale());
        setShowAllContext((String)pagecontext.getAttribute("showAllContext"));
        String s1 = null;
        if(pagecontext.getAttribute("showRecentlyUsed") != null)
            s1 = (String)pagecontext.getAttribute("showRecentlyUsed");
        if(s1 == null)
            setShowRecentlyUsed("false");
        else
            setShowRecentlyUsed(s1);
        String s2 = null;
        if(pagecontext.getAttribute("showMemberOfCheckBoxes") != null)
            s2 = (String)pagecontext.getAttribute("showMemberOfCheckBoxes");
        if(s2 == null)
            setShowMemberOfCheckBoxes("false");
        else
            setShowMemberOfCheckBoxes(s2);
        if(getShowRecentlyUsed() != null && getShowRecentlyUsed().equals("true"))
        {
            HashMap hashmap1 = null;
            if(pagecontext.getAttribute("mergeContainerTypes") != null)
                hashmap1 = (HashMap)pagecontext.getAttribute("mergeContainerTypes");
            if(hashmap1 != null)
                setMergeContainerTypes(hashmap1, pagecontext.getRequest());
            String s4 = null;
            if(pagecontext.getAttribute("showMultiSelectRecentlyUsed") != null)
                s4 = (String)pagecontext.getAttribute("showMultiSelectRecentlyUsed");
            if(s4 == null)
                setShowMultiSelectRecentlyUsed("false");
            else
            if(s4 != null)
                setShowMultiSelectRecentlyUsed(s4);
            String s6 = null;
            if(pagecontext.getAttribute("pickerCallback") != null)
                s6 = (String)pagecontext.getAttribute("pickerCallback");
            if(s6 == null)
                if(getShowMultiSelectRecentlyUsed() == "false")
                    setPickerCallback("contextPickerCallBackJCACombo");
                else
                    setPickerCallback("contextPickerCallBackMultiSelect");
            String s8 = null;
            if(pagecontext.getAttribute("onChangeForRecentlyUsed") != null)
                s8 = (String)pagecontext.getAttribute("onChangeForRecentlyUsed");
            if(s8 == null)
            {
                if(getShowMultiSelectRecentlyUsed() == "false")
                    setOnChangeFunction("selectRecentlyVisitedContextJCACombo");
                else
                    setOnChangeFunction("selectRecentlyVisitedContextMultiSelect");
            } else
            if(s8 != null)
                setOnChangeFunction(s8);
        } else
        {
            String s3 = getDefaultHiddenValue();
            if(s3 == null || s3.equals("") || s3.trim().length() == 0)
            {
                String s5 = getContainerRef();
                if(s5 != null && s5.length() > 0 && s5.indexOf("OrgContainer") == -1)
                {
                    setDefaultHiddenValue(s5);
                    String s7 = SearchUtils.getContainerName(s5);
                    setDefaultValue(s7);
                    initializeTextBox();
                }
            }
        }
        if("true".equalsIgnoreCase(getShowSuggestion()))
            createSuggestTextBox();
    }

    private void setShowAllContext(String s)
    {
        showAllContext = !"false".equalsIgnoreCase(s);
    }

    public boolean getShowAllContext()
    {
        return showAllContext;
    }

    public String getObjectType()
    {
        return objectType;
    }

    protected void setObjectType(String s)
    {
        if(s != null && s.trim().length() > 0)
            objectType = s;
    }

    public String getTypeComponentId()
    {
        return typeComponentId;
    }

    protected void setTypeComponentId(String s)
    {
        if(s != null && s.trim().length() > 0)
            typeComponentId = s;
        else
        if(SearchWebConstants.isProI)
            typeComponentId = defaultProITypeComponentId;
    }

    public String getCustomAccessController()
    {
        return customAccessController;
    }

    protected void setCustomAccessController(String s)
    {
        if(s != null && s.trim().length() > 0)
            customAccessController = s;
    }

    public String getExcludeSubTypes()
    {
        return excludeSubTypes;
    }

    protected void setExcludeSubTypes(String s)
    {
        if(s != null && s.trim().length() > 0)
            excludeSubTypes = s;
    }

    public String getShowTypePicker()
    {
        return showTypePicker;
    }

    protected void setShowTypePicker(String s)
    {
        if("false".equalsIgnoreCase(s))
            showTypePicker = s;
    }

    public String getExtraSuggestAttributes()
    {
        return extraSuggestAttributes;
    }

    protected void setExtraSuggestAttributes(String s)
    {
        if(s != null && s.trim().length() > 0)
            extraSuggestAttributes = s;
    }

    public String getShowSuggestion()
    {
        return showSuggestion;
    }

    protected void setShowSuggestion(String s)
    {
        if(s != null && s.trim().length() > 0)
        {
            showSuggestion = s;
            setReadOnlyPickerTextBox("false");
        }
    }

    public String getSuggestMinChars()
    {
        return suggestMinChars;
    }

    protected void setSuggestMinChars(String s)
    {
        suggestMinChars = s;
    }

    public String getSuggestServiceKey()
    {
        return suggestServiceKey;
    }

    protected void setSuggestServiceKey(String s)
    {
        if(s != null && s.trim().length() > 0)
            suggestServiceKey = s;
        else
            suggestServiceKey = "contextServicePicker";
    }

    protected void setShowRecentlyUsed(String s)
    {
        showRecentlyUsed = s;
    }

    public String getShowRecentlyUsed()
    {
        return showRecentlyUsed;
    }

    protected void setShowMemberOfCheckBoxes(String s)
    {
        showMemberOfCheckBoxes = s;
    }

    public String getShowMemberOfCheckBoxes()
    {
        return showMemberOfCheckBoxes;
    }

    protected void setShowMultiSelectRecentlyUsed(String s)
    {
        showMultiSelectRecentlyUsed = s;
    }

    public String getShowMultiSelectRecentlyUsed()
    {
        return showMultiSelectRecentlyUsed;
    }

    public ComboBox getRecentlyUsedComboBox()
    {
        return recentlyUsedListComboBox;
    }

    protected void setLocale(Locale locale1)
    {
        locale = locale1;
    }

    public Locale getLocale()
    {
        return locale;
    }

    private void createSuggestTextBox()
    {
        SuggestTextBox suggesttextbox = new SuggestTextBox(getDisplayFieldId(), "contextServicePicker");
        suggesttextbox.setAltHiddenFieldName(getId());
        if(getSuggestMinChars() != null)
            try
            {
                int i = Integer.parseInt(getSuggestMinChars());
                if(i < 1)
                    i = 3;
                suggesttextbox.setMinChars(i);
            }
            catch(NumberFormatException numberformatexception)
            {
                if(logger.isDebugEnabled())
                    logger.debug(numberformatexception.getLocalizedMessage(), numberformatexception);
            }
        if(getContainerRef().length() > 0)
            suggesttextbox.addParm("containerRef", getContainerRef());
        suggesttextbox.addParm("displayAttribute", getDisplayAttribute());
        suggesttextbox.addParm("extraSuggestAttributes", getExtraSuggestAttributes());
        suggesttextbox.addParm("objectType", getObjectType());
        suggesttextbox.addParm("componentId", getTypeComponentId());
        String s = (new StringBuilder()).append("componentId=").append(getTypeComponentId()).append("$").append("accessControllers=").append(getCustomAccessController()).append("$").append("excludeSubtypes=").append(getExcludeSubTypes()).toString();
        suggesttextbox.addParm("taskParams", s);
        setTextBox(suggesttextbox);
        initializeTextBox();
    }

    public String getReadOnlyPickerTextBox()
    {
        if("true".equalsIgnoreCase(getShowSuggestion()))
            return "false";
        else
            return super.getReadOnlyPickerTextBox();
    }

    private List getRecentlyVisitedObjectList()
        throws WTException
    {
        java.util.Vector vector = RecentlyVisitedHelper.service.getRecentlyVisitedObjectStack();
        return vector;
    }

    public AbstractGuiComponent getField()
        throws ClassNotFoundException, WTException
    {
        if(getShowRecentlyUsed() != null && getShowRecentlyUsed().equals("true"))
        {
            RecentlyVisitedContextComboCreator recentlyvisitedcontextcombocreator = new RecentlyVisitedContextComboCreator(new RecentlyVisitedContextComboValueStrategy());
            recentlyvisitedcontextcombocreator.setLocale(locale);
            recentlyvisitedcontextcombocreator.setPickerId(getId());
            recentlyvisitedcontextcombocreator.setDisplayFieldId(getDisplayFieldId());
            recentlyvisitedcontextcombocreator.setOnChangeFunction(getOnChangeFunction());
            recentlyvisitedcontextcombocreator.setTypeComponentId(getTypeComponentId());
            recentlyvisitedcontextcombocreator.setExcludeSubTypes(getExcludeSubTypes());
            recentlyvisitedcontextcombocreator.setMergeContainerTypes(getMergeContainerTypes());
            field = recentlyvisitedcontextcombocreator.create();
        } else
        {
            field = getTextBox();
        }
        return field;
    }

    public void setMergeContainerTypes(HashMap hashmap1, ServletRequest servletrequest)
    {
        hashmap = hashmap1;
        String s = (String)hashmap.get("selectedValue");
        if(s == null)
            s = "";
        setDefaultHiddenValue(s);
        servletrequest.setAttribute(getTypesToMergeParam(), hashmap);
    }

    public HashMap getMergeContainerTypes()
    {
        return hashmap;
    }

    public void setOnChangeFunction(String s)
    {
        onChangeForRecentlyUsed = s;
    }

    public String getOnChangeFunction()
    {
        return onChangeForRecentlyUsed;
    }

    public String getTypesToMergeParam()
    {
        return "typesToMerge";
    }

    public String[] getContextDisplayPairListForSearch()
        throws WTException
    {
        String s = SearchPreferencesHelper.getPreferenceValue("/com/ptc/windchill/enterprise/search", "searchContextDisplayList");
        StringBuilder stringbuilder = new StringBuilder();
        StringBuilder stringbuilder1 = new StringBuilder();
        StringBuilder stringbuilder2 = new StringBuilder();
        if(s != null && s.trim().length() > 0)
        {
            s = s.trim();
            String as[] = s.split(",");
            ReferenceFactory referencefactory = new ReferenceFactory();
            String as2[] = as;
            int i = as2.length;
            for(int j = 0; j < i; j++)
            {
                String s1 = as2[j];
                try
                {
                	if("".equals(s1)){
                		continue;
                	}

                    WTReference wtreference = referencefactory.getReference(s1);
                    String s2 = "";
                    WTContainer wtcontainer = (WTContainer)wtreference.getObject();
                    s2 = wtcontainer.getName();
                    TypeIdentifier typeidentifier = TypeIdentifierUtility.getTypeIdentifier(wtcontainer);
                    String s3 = (new StringBuilder()).append("WCTYPE|").append(typeidentifier.getTypename()).toString();
                    String s4 = ObjectTypeDisplayNamesCache.getImagePath(s3, getLocale());
                    if(stringbuilder1.length() > 0)
                        stringbuilder1.append(",");
                    stringbuilder1.append(s1);
                    if(stringbuilder.length() > 0)
                        stringbuilder.append(",");
                    stringbuilder.append(s2);
                    if(stringbuilder2.length() > 0)
                        stringbuilder2.append(",");
                    stringbuilder2.append(s4);
                    continue;
                }
                catch(WTRuntimeException wtruntimeexception)
                {
                    if(wtruntimeexception.getNestedThrowable() instanceof NotAuthorizedException)
                    {
                        if(logger.isDebugEnabled())
                            logger.debug(wtruntimeexception.getLocalizedMessage(), wtruntimeexception);
                    } else
                    {
                        throw wtruntimeexception;
                    }
                }
            }

        }
        String as1[] = {
            stringbuilder.toString(), stringbuilder1.toString(), stringbuilder2.toString()
        };
        return as1;
    }

    private static final String CONTEXT_PICKER_HELP_SELECTOR_KEY = "ContextPickerHelpSelectorKey";
    private String objectType;
    private String typeComponentId;
    private String customAccessController;
    private String excludeSubTypes;
    private String showTypePicker;
    private String defaultProITypeComponentId;
    private String showSuggestion;
    private String extraSuggestAttributes;
    private String suggestMinChars;
    private String suggestServiceKey;
    private static final String CLASSNAME = ContextPickerBean.class.getName();
    private static final Logger logger = LogR.getLogger(CLASSNAME);
    private AbstractGuiComponent field;
    private String showRecentlyUsed;
    private String showMemberOfCheckBoxes;
    private String showMultiSelectRecentlyUsed;
    private ComboBox recentlyUsedListComboBox;
    private Locale locale;
    private HashMap hashmap;
    private int selectedIndex;
    private String onChangeForRecentlyUsed;
    private boolean showAllContext;

}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\ptc\Windchill_10.0\Windchill\srclib\wnc\Search.jar
	Total time: 141 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/