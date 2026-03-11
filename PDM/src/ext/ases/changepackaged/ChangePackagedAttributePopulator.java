/**
 * @(#)changePackagedAttributePopalutor.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/4
 */
package ext.ases.changepackaged;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import wt.enterprise.RevisionControlled;
import wt.log4j.LogR;
import wt.services.applicationcontext.implementation.DefaultServiceProvider;
import wt.util.WTException;

import com.ptc.core.components.forms.CreateEditFormProcessorHelper;
import com.ptc.core.components.forms.DefaultAttributePopulator;
import com.ptc.core.meta.common.AttributeIdentifier;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.AttributeTypeIdentifierSet;
import com.ptc.core.meta.common.IdentifierFactory;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.container.common.AttributeTypeSummary;
import com.ptc.core.meta.container.common.State;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class ChangePackagedAttributePopulator extends DefaultAttributePopulator
    implements Serializable
{

    public ChangePackagedAttributePopulator()
    {
        attributeList = new ArrayList();
        attributeShortList = new ArrayList();
        attributeShortList.add("name");
        attributeShortList.add("number");
        attributeList.add("name");
        attributeList.add("number");
        attributeList.add("description");
    }

    public List getAttributeList()
    {
        return attributeList;
    }

    public List getAttributeShortList()
    {
        return attributeShortList;
    }

    public void setAttributeList(List list)
    {
        attributeList = list;
    }

    public void setAttributeShortList(List list)
    {
        attributeList = list;
    }

    public TypeInstance prePopulateAttributeDisplayValues(TypeInstance typeinstance, NmCommandBean nmcommandbean)
    {
        logger.debug((new StringBuilder()).append("The TypeInstance contains before update(prePopulate): ").append(typeinstance).toString());
        try
        {
            NmOid nmoid = nmcommandbean.getPrimaryOid();
            if(nmoid != null)
            {
                Iterator iterator = getAttributeList().iterator();
                do
                {
                    if(!iterator.hasNext())
                    {
                        break;
                    }
                    String s = (String)iterator.next();
                    AttributeIdentifier aattributeidentifier[] = typeinstance.getAttributeIdentifiers(s);
                    if(aattributeidentifier != null && aattributeidentifier.length > 0)
                    {
                        if(nmcommandbean != null)
                        {
                            if(nmoid.isA(ChangePackaged.class)){
                                ChangePackaged changePackaged = (ChangePackaged)nmoid.getRefObject();
                            	processAttribute(changePackaged, s, typeinstance, aattributeidentifier[0]);
                            }else if(nmoid.isA(RevisionControlled.class)){
                            	RevisionControlled rc = (RevisionControlled)nmoid.getRefObject();
                            	processAttribute2(rc, s, typeinstance, aattributeidentifier[0]);
                            }
                        }
                    }
                } while(true);
            }
        }
        catch(WTException wtexception)
        {
            logger.warn("An exception occured processing the propagated values.");
            wtexception.printStackTrace();
        }
        return typeinstance;
    }

    public void processAttribute(ChangePackaged changePackaged, String s, TypeInstance typeinstance, AttributeIdentifier attributeidentifier)
        throws WTException
    {
        if(s.equals("number"))
            setAttribute(typeinstance, attributeidentifier, changePackaged.getNumber());
        else
        if(s.equals("name"))
            setAttribute(typeinstance, attributeidentifier, changePackaged.getName());
        else
        if(s.equals("description"))
            setAttribute(typeinstance, attributeidentifier, changePackaged.getDescription());
    }
    
    public void processAttribute2(RevisionControlled rc, String s, TypeInstance typeinstance, AttributeIdentifier attributeidentifier)
	    throws WTException
	{
	    if(s.equals("name"))
	        setAttribute(typeinstance, attributeidentifier, rc.getName()+ " 打印申请单");
	}

    public void processNameNumberAttribute(ChangePackaged changePackaged, String s, TypeInstance typeinstance, AttributeIdentifier attributeidentifier)
        throws WTException
    {
        if(s.equals("number"))
            setAttribute(typeinstance, attributeidentifier, changePackaged.getNumber());
        else
        if(s.equals("name"))
            setAttribute(typeinstance, attributeidentifier, changePackaged.getName());
    }

    protected void setAttribute(TypeInstance typeinstance, AttributeIdentifier attributeidentifier, Object obj)
        throws WTException
    {
        State state = typeinstance.getState(attributeidentifier);
        if(obj != null && (state == State.UNINITIALIZED || state == State.DEFAULT))
        {
            typeinstance.put(attributeidentifier, obj);
            typeinstance.setState(attributeidentifier, State.DEFAULT);
        }
    }

    protected void setNewAttribute(TypeInstance typeinstance, AttributeIdentifier attributeidentifier, Object obj)
        throws WTException
    {
        State state = typeinstance.getState(attributeidentifier);
        if(obj != null && (state == State.UNINITIALIZED || state == State.DEFAULT))
        {
            typeinstance.put(attributeidentifier, obj);
            typeinstance.setState(attributeidentifier, State.NEW);
        }
    }

    public TypeInstance setAttributeValues(TypeInstance typeinstance, NmCommandBean nmcommandbean)
    {
        logger.debug((new StringBuilder()).append("The TypeInstance contains before update(setAttrValues): ").append(typeinstance).toString());
        ChangePackaged changePackaged = null;
        NmOid nmoid = null;
        TypeIdentifier typeidentifier = null;
        String s = nmcommandbean.getTextParameter("textbox1Hidden");
        String s1 = nmcommandbean.getTextParameter("insertNumber");
        if(s == null && s1 == null)
        {
            return typeinstance;
        }
        try
        {
            if(s != null)
            {
                nmoid = NmOid.newNmOid(s);
                changePackaged = (ChangePackaged)nmoid.getRef();
            }
            typeidentifier = (TypeIdentifier)typeinstance.getIdentifier().getDefinitionIdentifier();
            AttributeTypeIdentifierSet attributetypeidentifierset = new AttributeTypeIdentifierSet();
            AttributeTypeIdentifier attributetypeidentifier = (AttributeTypeIdentifier)IDENTIFIER_FACTORY.get("name", typeidentifier);
            attributetypeidentifierset.add(attributetypeidentifier);
            attributetypeidentifier = (AttributeTypeIdentifier)IDENTIFIER_FACTORY.get("number", typeidentifier);
            attributetypeidentifierset.add(attributetypeidentifier);
            typeinstance = CreateEditFormProcessorHelper.addAttributesToTypeInstance(typeinstance, attributetypeidentifierset);
        }
        catch(WTException wtexception)
        {
            logger.warn("An exception occured processing the propagated values.");
            wtexception.printStackTrace();
        }
        try
        {
            if(s1 != null && changePackaged == null)
            {
                AttributeIdentifier aattributeidentifier[] = typeinstance.getAttributeIdentifiers("number");
                setNewAttribute(typeinstance, aattributeidentifier[0], s1);
                AttributeTypeSummary attributetypesummary = typeinstance.getAttributeTypeSummary((AttributeTypeIdentifier)IDENTIFIER_FACTORY.get("number", typeidentifier));
                attributetypesummary.put("EDITABLE", Boolean.valueOf(false));
                logger.debug((new StringBuilder()).append("number is EDITABLE: ").append(attributetypesummary.isEditable()).toString());
            }
            if(nmoid != null && nmoid.isA(ext.ases.envelope.ProcessEnvelope.class))
            {
                typeinstance.getConstraintContainer().setEnabled(false);
                Iterator iterator = getAttributeShortList().iterator();
                do
                {
                    if(!iterator.hasNext())
                    {
                        break;
                    }
                    String s2 = (String)iterator.next();
                    AttributeIdentifier aattributeidentifier1[] = typeinstance.getAttributeIdentifiers(s2);
                    if(aattributeidentifier1 != null && aattributeidentifier1.length > 0)
                    {
                        processNameNumberAttribute(changePackaged, s2, typeinstance, aattributeidentifier1[0]);
                    }
                } while(true);
                typeinstance.getConstraintContainer().setEnabled(true);
            }
        }
        catch(WTException wtexception1)
        {
            logger.warn("An exception occured processing the propagated values.");
            wtexception1.printStackTrace();
        }
        logger.debug((new StringBuilder()).append("The TypeInstance contains after update(setAttrValues): ").append(typeinstance).toString());
        return typeinstance;
    }
    
    public void clearAttribute(ChangePackaged changePackaged, String s, TypeInstance typeinstance, AttributeIdentifier attributeidentifier)
        throws WTException
    {
        if(s.equals("name") || s.equals("description"))
        {
            typeinstance.put(attributeidentifier, null);
            typeinstance.setState(attributeidentifier, State.UNINITIALIZED);
        }
    }


    private static final long serialVersionUID = 0x2eb53a96daL;
    private static final IdentifierFactory IDENTIFIER_FACTORY = (IdentifierFactory)DefaultServiceProvider.getService(IdentifierFactory.class, "logical");
    private static final transient Logger logger = LogR.getLogger(ChangePackagedAttributePopulator.class.getName());
    private List attributeList;
    private List attributeShortList;
    public static final String SEARCH_RESULT = "textbox1Hidden";
    public static final String SEARCH_NUMBER = "insertNumber";

}