/**
 * @(#)ProcessEnvelopeRuleMigrator.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/4
 */
package ext.ases.envelope;

import java.io.IOException;
import wt.inf.container.WTContainerRef;
import wt.rule.Rule;
import wt.rule.RuleMigrator;
import wt.rule.init.*;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class ProcessEnvelopeRuleMigrator
    implements RuleMigrator
{
    public ProcessEnvelopeRuleMigrator() {
    }
    public Object migrateRule(Object obj, Rule rule, WTContainerRef wtcontainerref)
        throws WTException, WTPropertyVetoException
    {
        throw new WTException(new UnsupportedOperationException((new StringBuilder()).append("This method not currentlty supported.\n").append(CLASSNAME).append(".migrateRule(Object, Rule, WTContainerRef)").toString()));
    }

    public void setContents(Object obj, Rule rule, WTContainerRef wtcontainerref)
        throws WTException, WTPropertyVetoException
    {
        throw new WTException(new UnsupportedOperationException((new StringBuilder()).append("This method not currentlty supported.\n").append(CLASSNAME).append(".setContents(Object, Rule, WTContainerRef)").toString()));
    }

    public void setContents(Rule rule)
        throws WTException, WTPropertyVetoException
    {
        AttributeValues attributevalues = InitRuleHelper.getAttributeValues(rule);
        if(InitRuleHelper.hasAttrValue("lifeCycle.id", rule))
        {
            if(!InitRuleHelper.hasAttrConstraint("lifeCycle.id", rule))
            {
                AttrConstraint attrconstraint = new AttrConstraint(attributevalues, "lifeCycle.id", "com.ptc.core.rule.server.impl.GatherAttributeConstraints", false, false, false);
                Value value = new Value(attrconstraint, "com.ptc.core.rule.server.impl.GetServerAssignedConstraint");
                Value value8 = new Value(attrconstraint, "com.ptc.core.rule.server.impl.GetImmutableConstraint");
                attributevalues.setAttrConstraint(attrconstraint);
            }
            if(!InitRuleHelper.hasAttrConstraint("lifeCycle", rule))
            {
                AttrConstraint attrconstraint1 = new AttrConstraint(attributevalues, "lifeCycle", "com.ptc.core.rule.server.impl.GatherAttributeConstraints", false, false, false);
                Value value1 = new Value(attrconstraint1, "com.ptc.core.rule.server.impl.GetServerAssignedConstraint");
                Value value9 = new Value(attrconstraint1, "com.ptc.core.rule.server.impl.GetImmutableConstraint");
                attributevalues.setAttrConstraint(attrconstraint1);
            }
        }
        if(InitRuleHelper.hasAttrValue("teamTemplate.id", rule))
        {
            if(!InitRuleHelper.hasAttrConstraint("teamTemplate.id", rule))
            {
                AttrConstraint attrconstraint2 = new AttrConstraint(attributevalues, "teamTemplate.id", "com.ptc.core.rule.server.impl.GatherAttributeConstraints", false, false, false);
                Value value2 = new Value(attrconstraint2, "com.ptc.core.rule.server.impl.GetServerAssignedConstraint");
                Value value10 = new Value(attrconstraint2, "com.ptc.core.rule.server.impl.GetImmutableConstraint");
                attributevalues.setAttrConstraint(attrconstraint2);
            }
            if(!InitRuleHelper.hasAttrConstraint("teamTemplate", rule))
            {
                AttrConstraint attrconstraint3 = new AttrConstraint(attributevalues, "teamTemplate", "com.ptc.core.rule.server.impl.GatherAttributeConstraints", false, false, false);
                Value value3 = new Value(attrconstraint3, "com.ptc.core.rule.server.impl.GetServerAssignedConstraint");
                Value value11 = new Value(attrconstraint3, "com.ptc.core.rule.server.impl.GetImmutableConstraint");
                attributevalues.setAttrConstraint(attrconstraint3);
            }
        }
        if(InitRuleHelper.hasAttrValue("number", rule) && !InitRuleHelper.hasAttrConstraint("number", rule))
        {
            AttrConstraint attrconstraint4 = new AttrConstraint(attributevalues, "number", "com.ptc.core.rule.server.impl.GatherAttributeConstraints", false, false, false);
            Value value4 = new Value(attrconstraint4, "com.ptc.core.rule.server.impl.GetServerAssignedConstraint");
            Value value12 = new Value(attrconstraint4, "com.ptc.core.rule.server.impl.GetImmutableConstraint");
            attributevalues.setAttrConstraint(attrconstraint4);
        }
        if(InitRuleHelper.hasAttrValue("folder.id", rule) && !InitRuleHelper.hasAttrConstraint("folder.id", rule))
        {
            AttrConstraint attrconstraint5 = new AttrConstraint(attributevalues, "folder.id", "com.ptc.core.rule.server.impl.GatherAttributeConstraints", false, false, false);
            Value value5 = new Value(attrconstraint5, "com.ptc.core.rule.server.impl.GetServerPreGeneratedValue");
            attributevalues.setAttrConstraint(attrconstraint5);
        }
        if(InitRuleHelper.hasAttrValue("number", rule) && !InitRuleHelper.hasAttrConstraint("number", rule))
        {
            AttrConstraint attrconstraint6 = new AttrConstraint(attributevalues, "number", "com.ptc.core.rule.server.impl.GatherAttributeConstraints", false, false, false);
            Value value6 = new Value(attrconstraint6, "com.ptc.core.rule.server.impl.GetServerAssignedConstraint");
            Value value13 = new Value(attrconstraint6, "com.ptc.core.rule.server.impl.GetImmutableConstraint");
            attributevalues.setAttrConstraint(attrconstraint6);
        }
        if(InitRuleHelper.hasAttrValue("folder.id", rule) && !InitRuleHelper.hasAttrConstraint("folder.id", rule))
        {
            AttrConstraint attrconstraint7 = new AttrConstraint(attributevalues, "folder.id", "com.ptc.core.rule.server.impl.GatherAttributeConstraints", false, false, false);
            Value value7 = new Value(attrconstraint7, "com.ptc.core.rule.server.impl.GetServerPreGeneratedValue");
            attributevalues.setAttrConstraint(attrconstraint7);
        }
        try
        {
            InitRuleHelper.setContents(rule, attributevalues);
        }
        catch(IOException ioexception)
        {
            throw new WTException(ioexception);
        }
    }


    private static final String RESOURCE = "wt.rule.impl.implResource";
    private static final String CLASSNAME = ext.ases.envelope.ProcessEnvelopeRuleMigrator.class.getName();
    public static final String WT_RULE_ENUM_TYPE_CONSTANT = "wt.rule.algorithm.EnumTypeConstant";
    public static final String WT_DOCUMENT_ENUM_TYPE_SELECTION = "$$Document";
    public static final String WT_DOC_DOCUMENT_TYPE = "wt.doc.DocumentType";
    public static final String WT_DOC_DEPARTMENT_LIST = "wt.doc.DepartmentList";
    public static final String WT_DOC_DEPARTMENT_LLIST_SELECTION = "ENG";
    public static final String DOC_TYPE = "docType";
    public static final String DOC_DEPARTMENT = "department";

}