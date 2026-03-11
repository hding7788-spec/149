package ext.casc.util;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.iba.constraint.IBAConstraintException;
import wt.iba.definition.DefinitionLoader;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.AttributeDefNodeView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.RatioDefView;
import wt.iba.definition.litedefinition.ReferenceDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.TimestampDefView;
import wt.iba.definition.litedefinition.URLDefView;
import wt.iba.definition.litedefinition.UnitDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.RatioValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.litevalue.TimestampValueDefaultView;
import wt.iba.value.litevalue.URLValueDefaultView;
import wt.iba.value.litevalue.UnitValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.LoadValue;
import wt.iba.value.service.StandardIBAValueService;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView.RuleDataObject;
import com.ptc.core.lwc.common.view.EnumerationDefinitionReadView;
import com.ptc.core.lwc.common.view.EnumerationEntryReadView;
import com.ptc.core.lwc.common.view.EnumerationMembershipReadView;
import com.ptc.core.lwc.common.view.PropertyValueReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifierHelper;

public class IBAUtil{ 

    Hashtable ibaContainer;
    
    IBAHolder ibaHolder;
  
    private IBAUtil() 
    {
        ibaContainer = new Hashtable();
    }

    public IBAUtil(IBAHolder ibaholder) {
        initializeIBAHolder(ibaholder);
    }
    
    public static AbstractValueView getIBAValueView( DefaultAttributeContainer dac, String ibaName, String ibaClass )
		throws WTException
	{
		AbstractValueView aabstractvalueview[] = null;
		AbstractValueView avv = null;
		aabstractvalueview = dac.getAttributeValues();
		for(int j = 0; j < aabstractvalueview.length; j++)
		{
			String thisIBAName = aabstractvalueview[j].getDefinition().getName();
			String thisIBAValue = IBAValueUtility.getLocalizedIBAValueDisplayString(aabstractvalueview[j], Locale.CHINA);
			String thisIBAClass = (aabstractvalueview[j].getDefinition()).getAttributeDefinitionClassName();
			if (thisIBAName.equals(ibaName) && thisIBAClass.equals(ibaClass))
			{
				avv=aabstractvalueview[j];
				break;
			}
		}
		return avv;
	}
   
   public static DefaultAttributeContainer getContainer(IBAHolder ibaHolder)
		throws WTException, RemoteException
	{
		ibaHolder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaHolder);
		DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer)ibaHolder.getAttributeContainer();
		return defaultattributecontainer;
	}
	
	private static java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public String getAllIBAString(String delim,Vector filter){
    	StringBuffer stringbuffer = new StringBuffer();
      Enumeration enumeration = ibaContainer.keys();
      try {
        while(enumeration.hasMoreElements()) {
      	  String s = (String)enumeration.nextElement();
      	  if(filter!=null && filter.contains(s)){
        		AbstractValueView abstractvalueview = (AbstractValueView)((Object[])ibaContainer.get(s))[1];
        		if(abstractvalueview instanceof wt.iba.value.litevalue.TimestampValueDefaultView){
      	  		try{
								wt.iba.value.litevalue.TimestampValueDefaultView w=(wt.iba.value.litevalue.TimestampValueDefaultView)abstractvalueview;
								ResourceBundle formatResource = ResourceBundle.getBundle("wt.iba.value.litevalue.DisplayFormat", WTContext.getContext().getLocale());
              	String formatString = formatResource.getString("timestampOutputFormat");
              	java.text.SimpleDateFormat formats = new java.text.SimpleDateFormat(formatString);
								stringbuffer.append(s+delim+format.format(formats.parse(w.getLocalizedDisplayString()))+delim);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
        			stringbuffer.append(s+delim+IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, SessionHelper.manager.getLocale())+delim);
        		}
        	}
      	}
      }
      catch(Exception exception)  {
        exception.printStackTrace();
      }
      return stringbuffer.toString();
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        Enumeration enumeration = ibaContainer.keys();
        try {
            while(enumeration.hasMoreElements()) {
                String s = (String)enumeration.nextElement();
                AbstractValueView abstractvalueview = (AbstractValueView)((Object[])ibaContainer.get(s))[1];
                stringbuffer.append(s + " - " + IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, SessionHelper.manager.getLocale()));
                stringbuffer.append('\n');
            }
        }
        catch(Exception exception)  {
            exception.printStackTrace();
        }
        return stringbuffer.toString();
    }

    public String getIBAValue(String s){
        try {
            return getIBAValue(s, SessionHelper.manager.getLocale());
        }
        catch(WTException wte) {
            wte.printStackTrace();
        }
        return null;
    }

    public String getIBAValue(String s, Locale locale) {
    	Object[] obj=(Object[])ibaContainer.get(s);
    	if(obj==null) return null;
    	AbstractValueView avv=(AbstractValueView)obj[1];
    	if(avv==null) return null;
        try {
        	String rs = IBAValueUtility.getLocalizedIBAValueDisplayString(avv, locale);
        	if (!(rs == null || "".equals(rs))) {
	        	String rsStr = null;
	        	TypeIdentifier ti = TypedUtility.getTypeIdentifier(ibaHolder);
	    		try {
	    			TypeDefinitionReadView tv = TypeDefinitionServiceHelper.service.getTypeDefView(ti);
	    			AttributeDefinitionReadView av = tv.getAttributeByName(s);
	    			if (av != null) {
	    				Collection<ConstraintDefinitionReadView> constraints = av.getAllConstraints();
	    				for (ConstraintDefinitionReadView constraint : constraints) {
	    				    RuleDataObject rdo = constraint.getRuleDataObj();
	    				    if (rdo != null) {
	    				    	EnumerationDefinitionReadView edv = rdo.getEnumDef();
	    				    	if (edv != null) {
	    					    	Map<String, EnumerationEntryReadView> entryViewMap = edv.getAllEnumerationEntries();
	    					    	for (String key : entryViewMap.keySet()) {
	    					    		EnumerationMembershipReadView emv = edv.getMembershipByName(key);
	    					    		EnumerationEntryReadView member = emv.getMember();
	    					    		PropertyValueReadView readView = member.getPropertyValueByName("displayName");
	    					    		if (readView != null && rs.equals(member.getName())) {
	    					    			rsStr = readView.getValue().toString();
	    					    			break;
	    					    		}
	    					    	}
	    				    	}
	    				    }
	    				    if (rsStr != null) {
	    				    	rs = rsStr;
	    				    	break;
	    				    }
	    				}
	    			}
	    		} catch (WTException e) {
	    			e.printStackTrace();
	    		}
        	}
            return rs;
        } catch(WTException wte) {
            wte.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<String> getIBAEnumConstraints(String s) {
    	ArrayList<String> result=new ArrayList<String>();
    	Object[] obj=(Object[])ibaContainer.get(s);
    	if(obj==null) return null;
    	AbstractValueView avv=(AbstractValueView)obj[1];
    	if(avv==null) return null;
        try {
        	String rs = IBAValueUtility.getLocalizedIBAValueDisplayString(avv, SessionHelper.manager.getLocale());
        	if (!(rs == null || "".equals(rs))) {
        		List<String> enums=new ArrayList<String>();
	        	TypeIdentifier ti = TypedUtility.getTypeIdentifier(ibaHolder);
	    		try {
	    			TypeDefinitionReadView tv = TypeDefinitionServiceHelper.service.getTypeDefView(ti);
	    			AttributeDefinitionReadView av = tv.getAttributeByName(s);
	    			if (av != null) {
	    				Collection<ConstraintDefinitionReadView> constraints = av.getAllConstraints();
	    				for (ConstraintDefinitionReadView constraint : constraints) {
	    				    RuleDataObject rdo = constraint.getRuleDataObj();
	    				    if (rdo != null) {
	    				    	EnumerationDefinitionReadView edv = rdo.getEnumDef();
	    				    	if (edv != null) {
	    					    	Map<String, EnumerationEntryReadView> entryViewMap = edv.getAllEnumerationEntries();
	    					    	for (String key : entryViewMap.keySet()) {
	    					    		result.add(key);
	    					    	}
	    				    	}
	    				    }
	    				    if (result.size()>0) {	    				    	
	    				    	break;
	    				    }
	    				}
	    			}
	    		} catch (WTException e) {
	    			e.printStackTrace();
	    		}
        	}
        	if(result.size()==0) result.add(rs);
            return result;
        } catch(WTException wte) {
            wte.printStackTrace();
        }
        return null;
    }
    
    
    public String getIBAValueWithDefult(String s){
		try
        {
            return getIBAValueWithDefult(s, SessionHelper.manager.getLocale());
        }
        catch(WTException wte)
        {
            wte.printStackTrace();
        }
        return null;    
    }    
    
    public String getIBAValueWithDefult(String s,Locale loc){
    	String str=getIBAValue(s,loc);
    	if(str!=null&&str.equalsIgnoreCase("default")) str="";
    	return str;
    }

    private void initializeIBAHolder(IBAHolder ibaholder)
    {
        ibaContainer = new Hashtable();
        try
        {
            ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, SessionHelper.manager.getLocale(), null);
            this.ibaHolder = ibaholder;
            DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer)ibaholder.getAttributeContainer();
            if(defaultattributecontainer != null)
            {
                AttributeDefDefaultView aattributedefdefaultview[] = defaultattributecontainer.getAttributeDefinitions();
                for(int i = 0; i < aattributedefdefaultview.length; i++)
                {
                    AbstractValueView aabstractvalueview[] = defaultattributecontainer.getAttributeValues(aattributedefdefaultview[i]);
                    if(aabstractvalueview != null)
                    {
                        Object aobj[] = new Object[2];
                        aobj[0] = aattributedefdefaultview[i];
                        aobj[1] = aabstractvalueview[0];
                        ibaContainer.put(aattributedefdefaultview[i].getName(), ((aobj)));
                    }
                }

            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public IBAHolder updateIBAHolder(IBAHolder ibaholder)
        throws Exception
    {
        ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, SessionHelper.manager.getLocale(), null);
        DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer)ibaholder.getAttributeContainer();
        for(Enumeration enumeration = ibaContainer.elements(); enumeration.hasMoreElements();)
            try
            {
                Object aobj[] = (Object[])enumeration.nextElement();
                AbstractValueView abstractvalueview = (AbstractValueView)aobj[1];
                AttributeDefDefaultView attributedefdefaultview = (AttributeDefDefaultView)aobj[0];
                if(abstractvalueview.getState() == 1)
                {
                    defaultattributecontainer.deleteAttributeValues(attributedefdefaultview);
                    abstractvalueview.setState(3);
                    defaultattributecontainer.addAttributeValue(abstractvalueview);
                }
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }

        ibaholder.setAttributeContainer(defaultattributecontainer);
        return ibaholder;
    }

    public void setIBAValue(String s, String s1) throws WTPropertyVetoException{
        AbstractValueView abstractvalueview = null;
        AttributeDefDefaultView attributedefdefaultview = null;
        Object aobj[] = (Object[])ibaContainer.get(s);
        if(aobj != null){
            abstractvalueview = (AbstractValueView)aobj[1];
            attributedefdefaultview = (AttributeDefDefaultView)aobj[0];
        }
        if(abstractvalueview == null)
            attributedefdefaultview = getAttributeDefinition(s);
        if(attributedefdefaultview == null){
//            System.out.println("definition is null ...");
            return;
        }
        abstractvalueview = internalCreateValue(attributedefdefaultview, s1);
        if(abstractvalueview == null){
//            System.out.println("after creation, iba value is null ..");
        }else{
            abstractvalueview.setState(1);
            Object aobj1[] = new Object[2];
            aobj1[0] = attributedefdefaultview;
            aobj1[1] = abstractvalueview;
            ibaContainer.put(attributedefdefaultview.getName(), ((aobj1)));
        }
    }

    public static AttributeDefDefaultView getAttributeDefinition(String s){
        AttributeDefDefaultView attributedefdefaultview = null;
        try{
            attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
            if(attributedefdefaultview == null){
                AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader.getAttributeDefinition(s);
                if(abstractattributedefinizerview != null)
                    attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView)abstractattributedefinizerview);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        return attributedefdefaultview;
    }

  
    private AbstractValueView internalCreateValue(AbstractAttributeDefinizerView abstractattributedefinizerview, String s){
        AbstractValueView abstractvalueview = null;
        if(abstractattributedefinizerview instanceof FloatDefView)
            abstractvalueview = LoadValue.newFloatValue(abstractattributedefinizerview, s, null);
        else
        if(abstractattributedefinizerview instanceof StringDefView)
            abstractvalueview = LoadValue.newStringValue(abstractattributedefinizerview, s);
        else
        if(abstractattributedefinizerview instanceof IntegerDefView)
            abstractvalueview = LoadValue.newIntegerValue(abstractattributedefinizerview, s);
        else
        if(abstractattributedefinizerview instanceof RatioDefView)
            abstractvalueview = LoadValue.newRatioValue(abstractattributedefinizerview, s, null);
        else
        if(abstractattributedefinizerview instanceof TimestampDefView)
            abstractvalueview = LoadValue.newTimestampValue(abstractattributedefinizerview, s);
        else
        if(abstractattributedefinizerview instanceof BooleanDefView)
            abstractvalueview = LoadValue.newBooleanValue(abstractattributedefinizerview, s);
        else
        if(abstractattributedefinizerview instanceof URLDefView)
            abstractvalueview = LoadValue.newURLValue(abstractattributedefinizerview, s, null);
        else
        if(abstractattributedefinizerview instanceof ReferenceDefView)
            abstractvalueview = LoadValue.newReferenceValue(abstractattributedefinizerview, "ClassificationNode", s);
        else
        if(abstractattributedefinizerview instanceof UnitDefView)
            abstractvalueview = LoadValue.newUnitValue(abstractattributedefinizerview, s, null);
        return abstractvalueview;
    }
    
  
    public static AttributeDefDefaultView getAttributeDefinition(String s, boolean flag){
        AttributeDefDefaultView attributedefdefaultview = null;
        try{
            attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
            if(attributedefdefaultview == null) {
                AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader.getAttributeDefinition(s);
                if(abstractattributedefinizerview != null)
                    attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView)abstractattributedefinizerview);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return attributedefdefaultview;
    }
   
	public static Vector getIBAValueViews(DefaultAttributeContainer dac, String ibaName, String ibaClass) throws WTException {
		AbstractValueView aabstractvalueview[] = null;
		AbstractValueView avv = null;
		Vector vResult = new Vector();
		aabstractvalueview = dac.getAttributeValues();
		for (int j = 0; j < aabstractvalueview.length; j++) {
			String thisIBAName = aabstractvalueview[j].getDefinition().getName();
			String thisIBAClass = (aabstractvalueview[j].getDefinition()).getAttributeDefinitionClassName();
			if (thisIBAName.equals(ibaName) && thisIBAClass.equals(ibaClass)) {
				avv = aabstractvalueview[j];
				vResult.add(avv);
			}
		}
		return vResult;
	}
    
	public static void setIBAAnyValue(WTObject obj, String ibaName,String newValue) throws WTException, RemoteException, WTPropertyVetoException, ParseException{
		AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
		IBAHolder ibaholder = (IBAHolder)obj;
		
		String ibaClass = "";
		if(attributedefdefaultview instanceof FloatDefView){
			ibaClass = "wt.iba.definition.FloatDefinition";
		}else if(attributedefdefaultview instanceof StringDefView){
			ibaClass = "wt.iba.definition.StringDefinition";
		}else if(attributedefdefaultview instanceof IntegerDefView){
			ibaClass = "wt.iba.definition.IntegerDefinition";
		}else if(attributedefdefaultview instanceof RatioDefView){
			ibaClass = "wt.iba.definition.RatioDefinition";
		}else if(attributedefdefaultview instanceof TimestampDefView){
			ibaClass = "wt.iba.definition.TimestampDefinition";
		}else if(attributedefdefaultview instanceof BooleanDefView){
			ibaClass = "wt.iba.definition.BooleanDefinition";
		}else if(attributedefdefaultview instanceof URLDefView){
			ibaClass = "wt.iba.definition.URLDefinition";
		}else if(attributedefdefaultview instanceof ReferenceDefView){
			ibaClass = "wt.iba.definition.ReferenceDefinition";
		}else if(attributedefdefaultview instanceof UnitDefView){
			ibaClass = "wt.iba.definition.UnitDefinition";
		}

	    // store the new iteration (this will copy forward the obsolete set of IBA values in the database)
		//ibaholder = (IBAHolder)PersistenceHelper.manager.store( (Persistable)ibaholder );

	    // load IBA values from DB (because obsolete IBA values have
	    // been copied forward to new iteration by IBA persistence event handlers)
		ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, "CSM", null, null);

	    // clear the container to remove all obsolete IBA values and persist this
	    // to remove IBA values from database
	    //*deleteAllIBAValues(ibaholder );
		ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, SessionHelper.manager.getLocale(), null);
		DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer)(ibaholder).getAttributeContainer();
		
		Vector vAbstractvalueview = getIBAValueViews(defaultattributecontainer,ibaName,ibaClass);

		for(int i=0; i<vAbstractvalueview.size(); i++){	
			AbstractValueView abstractvalueview = (AbstractValueView) vAbstractvalueview.get(i);
			defaultattributecontainer.deleteAttributeValue(abstractvalueview);
			StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaholder, null, null, null);
			ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, "CSM", null, null);
		}

		if(!newValue.equals("")){
			if(attributedefdefaultview instanceof FloatDefView){
				setIBAFloatValue(obj, ibaName,Float.parseFloat(newValue));
//				System.out.println("setIBAFloatValue");
			}else if(attributedefdefaultview instanceof StringDefView){
				if(newValue.contains("strMultiValuePTC")){
					String[] newMultiString = newValue.split("strMultiValuePTC");
					setIBAStringValues(obj, ibaName,newMultiString);
//		        	System.out.println("setIBAStringMultiValue");
				}else{
		        	setIBAStringValue(obj, ibaName,newValue);
//		        	System.out.println("setIBAStringValue");
				}
			}else if(attributedefdefaultview instanceof IntegerDefView){
	        	setIBAIntegerValue(obj, ibaName,Integer.parseInt(newValue));
//				System.out.println("setIBAIntegerValue");
			}else if(attributedefdefaultview instanceof RatioDefView){
	        	setIBARatioValue(obj, ibaName,Double.parseDouble(newValue));
//				System.out.println("setIBARatioValue");
			}else if(attributedefdefaultview instanceof TimestampDefView){
				if (!newValue.contains(":")) newValue = newValue + " 00:00:00";
				
	            String format = "yyyy-MM-dd HH:mm:ss";
	            if (SessionHelper.manager.getLocale().toString().equals("zh_CN")|| SessionHelper.manager.getLocale().toString().equals("zh_TW")) {
	               format = "yyyy/MM/dd HH:mm:ss";
	            }
	          	java.text.SimpleDateFormat formats = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	          	java.text.SimpleDateFormat formatSource = new java.text.SimpleDateFormat(format);
	        	setIBATimestampValue(obj, ibaName, Timestamp.valueOf(formats.format(formatSource.parse(newValue))));
//				System.out.println("setIBATimestampValue");
			}else if(attributedefdefaultview instanceof BooleanDefView){
	        	setIBABooleanValue(obj, ibaName,Boolean.parseBoolean(newValue));
//				System.out.println("setIBABooleanValue");
			}else if(attributedefdefaultview instanceof URLDefView){
	          	setIBAURLValue(obj, ibaName,newValue);
//				System.out.println("setIBAURLValue");
			}else if(attributedefdefaultview instanceof ReferenceDefView){
//	        	System.out.println("ReferenceDefView");
			}else if(attributedefdefaultview instanceof UnitDefView){
	        	setIBAUnitValue(obj, ibaName,Double.parseDouble(newValue));
//				System.out.println("setIBAUnitValue");
			}
		}
	}
	
	   /**
	 * set iba attribute value
	 * @param obj 		object
	 * @param ibaName 	attribute name
	 * @param newValue 	attribute value
	 * @return void
	 */
	public static void setIBAStringValue( WTObject obj, String ibaName,String newValue ) throws WTException{
		String ibaClass = "wt.iba.definition.StringDefinition";
		System.out.println("ENTER..." + ibaName + "..." + newValue);
		try{
			if(obj instanceof IBAHolder){
				IBAHolder ibaHolder = (IBAHolder)obj;
				DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
				if(defaultattributecontainer == null){
					defaultattributecontainer = new DefaultAttributeContainer();
					ibaHolder.setAttributeContainer(defaultattributecontainer);
				}
				StringValueDefaultView abstractvaluedefaultview = (StringValueDefaultView)getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
				if(abstractvaluedefaultview != null){
					abstractvaluedefaultview.setValue(newValue);
					defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
				}else{
					AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
					StringValueDefaultView abstractvaluedefaultview1 = new StringValueDefaultView((StringDefView)attributedefdefaultview, newValue);
					defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
				}
				ibaHolder.setAttributeContainer(defaultattributecontainer);
				StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
				ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
				//wt.iba.value.service.LoadValue.applySoftAttributes(ibaHolder);
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
	public static void setIBAStringValues(WTObject obj, String ibaName, String[] newValue) throws WTException{
		String oneNewValue = "";
		try{
			if(obj instanceof IBAHolder){
				for(int i=0; i<newValue.length; i++){
					oneNewValue = newValue[i];
					IBAHolder ibaHolder = (IBAHolder)obj;
					DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
					if(defaultattributecontainer == null){
						defaultattributecontainer = new DefaultAttributeContainer();
						ibaHolder.setAttributeContainer(defaultattributecontainer);
					}

					AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
					StringValueDefaultView abstractvaluedefaultview1 = new StringValueDefaultView((StringDefView)attributedefdefaultview, oneNewValue);
					defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);

					ibaHolder.setAttributeContainer(defaultattributecontainer);
					StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
					ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
					//wt.iba.value.service.LoadValue.applySoftAttributes(ibaHolder);
				}
			}
			System.out.println("ENTER..." + ibaName + "..." + newValue.toString());
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

	public static void setIBABooleanValue( WTObject obj, String ibaName,boolean newValue ) throws WTException{
		String ibaClass = "wt.iba.definition.BooleanDefinition";
		try{
			if(obj instanceof IBAHolder){
				IBAHolder ibaHolder = (IBAHolder)obj;
				DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
				if(defaultattributecontainer == null){
					defaultattributecontainer = new DefaultAttributeContainer();
					ibaHolder.setAttributeContainer(defaultattributecontainer);
				}
				BooleanValueDefaultView abstractvaluedefaultview = (BooleanValueDefaultView)getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
				if(abstractvaluedefaultview != null){
					abstractvaluedefaultview.setValue(newValue);
					defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
				}else{
					AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
					BooleanValueDefaultView abstractvaluedefaultview1 = new BooleanValueDefaultView((BooleanDefView)attributedefdefaultview, newValue);
					defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
				}
				ibaHolder.setAttributeContainer(defaultattributecontainer);
				StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
				ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
		
	}

	public static void setIBAIntegerValue(WTObject obj, String ibaName,int newValue ) throws WTException{
		String ibaClass = "wt.iba.definition.IntegerDefinition";
		try{
			if(obj instanceof IBAHolder){
				IBAHolder ibaHolder = (IBAHolder)obj;
				DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
				if(defaultattributecontainer == null){
					defaultattributecontainer = new DefaultAttributeContainer();
					ibaHolder.setAttributeContainer(defaultattributecontainer);
				}
				IntegerValueDefaultView abstractvaluedefaultview = (IntegerValueDefaultView)getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
				if(abstractvaluedefaultview != null){
					abstractvaluedefaultview.setValue(newValue);
					defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
				}else{
					AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
					IntegerValueDefaultView abstractvaluedefaultview1 = new IntegerValueDefaultView((IntegerDefView)attributedefdefaultview, newValue);
					defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
				}
				ibaHolder.setAttributeContainer(defaultattributecontainer);
				StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
				ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

	public static void setIBAFloatValue(WTObject obj, String ibaName,float newValue ) throws WTException{
		String ibaClass = "wt.iba.definition.FloatDefinition";
		try{
			if(obj instanceof IBAHolder){
				IBAHolder ibaHolder = (IBAHolder)obj;
				DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
				if(defaultattributecontainer == null){
					defaultattributecontainer = new DefaultAttributeContainer();
					ibaHolder.setAttributeContainer(defaultattributecontainer);
				}
				
				String strFloatValue = String.valueOf(newValue);
				StringTokenizer st = new StringTokenizer(strFloatValue,".");
				System.out.println();
				int iFloatLength = 0;
				if(st.hasMoreElements()){
					st.nextElement();
					if(st.hasMoreElements()){
						iFloatLength = ((String)st.nextElement()).length();
					}
				}
				if (iFloatLength == 0){
					iFloatLength = -1;
				}else{
					iFloatLength = iFloatLength+ 1;
				}
					
				FloatValueDefaultView abstractvaluedefaultview = (FloatValueDefaultView)getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
				if(abstractvaluedefaultview != null){
					abstractvaluedefaultview.setValue(newValue);
					abstractvaluedefaultview.setPrecision(iFloatLength);
					defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
				}else{
					AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
					FloatValueDefaultView abstractvaluedefaultview1 = new FloatValueDefaultView((FloatDefView)attributedefdefaultview,newValue,iFloatLength);
					defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
				}
				ibaHolder.setAttributeContainer(defaultattributecontainer);
				StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
				ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

	public static void setIBARatioValue(WTObject obj, String ibaName,double newValue ) throws WTException{
		String ibaClass = "wt.iba.definition.RatioDefinition";
		try{
			if(obj instanceof IBAHolder){
				IBAHolder ibaHolder = (IBAHolder)obj;
				DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
				if(defaultattributecontainer == null){
					defaultattributecontainer = new DefaultAttributeContainer();
					ibaHolder.setAttributeContainer(defaultattributecontainer);
				}
				RatioValueDefaultView abstractvaluedefaultview = (RatioValueDefaultView)getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
				if(abstractvaluedefaultview != null){
					abstractvaluedefaultview.setValue(newValue);
					
					defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
				}else{
					AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
					RatioValueDefaultView abstractvaluedefaultview1 = new RatioValueDefaultView((RatioDefView)attributedefdefaultview);
					defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
				}
				ibaHolder.setAttributeContainer(defaultattributecontainer);
				StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
				ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

	public static void setIBATimestampValue(WTObject obj, String ibaName,Timestamp newValue ) throws WTException{
		String ibaClass = "wt.iba.definition.TimestampDefinition";
		try{
			if(obj instanceof IBAHolder){
				IBAHolder ibaHolder = (IBAHolder)obj;
				DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
				if(defaultattributecontainer == null){
					defaultattributecontainer = new DefaultAttributeContainer();
					ibaHolder.setAttributeContainer(defaultattributecontainer);
				}
				TimestampValueDefaultView abstractvaluedefaultview = (TimestampValueDefaultView)getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
				if(abstractvaluedefaultview != null){
					abstractvaluedefaultview.setValue(newValue);
					
					defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
				}else{
					AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
					TimestampValueDefaultView abstractvaluedefaultview1 = new TimestampValueDefaultView((TimestampDefView)attributedefdefaultview, newValue);
					defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
				}
				ibaHolder.setAttributeContainer(defaultattributecontainer);
				StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
				ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

	public static void setIBAURLValue(WTObject obj, String ibaName,String newValue ) throws WTException{
		String ibaClass = "wt.iba.definition.URLDefinition";

		try{
			StringTokenizer st = new StringTokenizer(newValue,"$$$");
			String urlValue = "";
			String urlDesc = "";
			while(st.hasMoreElements()){
				urlValue = st.nextToken();
				if(st.hasMoreElements()) urlDesc = st.nextToken();
			}
			if(obj instanceof IBAHolder){
				IBAHolder ibaHolder = (IBAHolder)obj;
				DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
				if(defaultattributecontainer == null){
					defaultattributecontainer = new DefaultAttributeContainer();
					ibaHolder.setAttributeContainer(defaultattributecontainer);
				}
				URLValueDefaultView abstractvaluedefaultview = (URLValueDefaultView)getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
				if(abstractvaluedefaultview != null){
					abstractvaluedefaultview.setValue(urlValue);
					abstractvaluedefaultview.setDescription(urlDesc);
					defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
				}else{
					AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
					URLValueDefaultView abstractvaluedefaultview1 = new URLValueDefaultView((URLDefView)attributedefdefaultview,urlValue,urlDesc);
					defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
				}
				ibaHolder.setAttributeContainer(defaultattributecontainer);
				StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
				ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
	public static void setIBAUnitValue(WTObject obj, String ibaName,double newValue) throws WTException{
		String ibaClass = "wt.iba.definition.UnitDefinition";
		try{
			if(obj instanceof IBAHolder){
				IBAHolder ibaHolder = (IBAHolder)obj;
				DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
				if(defaultattributecontainer == null){
					defaultattributecontainer = new DefaultAttributeContainer();
					ibaHolder.setAttributeContainer(defaultattributecontainer);
				}
				UnitValueDefaultView abstractvaluedefaultview = (UnitValueDefaultView)getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
				
				String strFloatValue = String.valueOf(newValue);
				StringTokenizer st = new StringTokenizer(strFloatValue,".");
				System.out.println();
				int iFloatLength = 0;
				if(st.hasMoreElements()){
					st.nextElement();
					if(st.hasMoreElements()){
						iFloatLength = ((String)st.nextElement()).length();
					}
				}
				iFloatLength = iFloatLength+ 1;
				if(abstractvaluedefaultview != null){
					abstractvaluedefaultview.setValue(newValue);
					abstractvaluedefaultview.setPrecision(iFloatLength);
					defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
				}else{
					AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
					UnitValueDefaultView abstractvaluedefaultview1 = new UnitValueDefaultView((UnitDefView)attributedefdefaultview,newValue,iFloatLength);
					defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
				}
				ibaHolder.setAttributeContainer(defaultattributecontainer);
				StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
				ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
	public static boolean isIBAContained(String iba, Map IBAMap) throws WTException {
		boolean contained = false;
		try {			
			Iterator itr = IBAMap.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry entry = (Map.Entry) itr.next();
				Object key = entry.getKey();
				if (iba.equals((String) key)) {
					contained = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contained;
	}
	
	public static String getIBAValueWhenCreate(IBAHolder docObj, String ibaKey) throws IBAConstraintException {
		String rs = "";
		try {
			DefaultAttributeContainer attrContainer = (DefaultAttributeContainer) docObj.getAttributeContainer();
	    	if (attrContainer != null) {
	    		AbstractValueView[] attrValViews = attrContainer.getAttributeValues();
	    		for (AbstractValueView attrValView : attrValViews) {
	    			String logicDef = attrValView.getDefinition().getName();
	    			if (ibaKey.equals(logicDef)) {
	    				StringValueDefaultView strView = (StringValueDefaultView) attrValView;
	    				rs = strView.getValue();
	    				String rtStr = null;
	    				TypeDefinitionReadView tv = TypeDefinitionServiceHelper.service.getTypeDefView(TypeIdentifierHelper.getType(docObj));
	        			AttributeDefinitionReadView av = tv.getAttributeByName(ibaKey);
	        			if (av != null) {
	        				Collection<ConstraintDefinitionReadView> constraints = av.getAllConstraints();
	        				for (ConstraintDefinitionReadView constraint : constraints) {
	        				    RuleDataObject rdo = constraint.getRuleDataObj();
	        				    if (rdo != null) {
	        				    	EnumerationDefinitionReadView edv = rdo.getEnumDef();
	        				    	if (edv != null) {
	        					    	Map<String, EnumerationEntryReadView> entryViewMap = edv.getAllEnumerationEntries();
	        					    	for (String key : entryViewMap.keySet()) {
	        					    		EnumerationMembershipReadView emv = edv.getMembershipByName(key);
	        					    		EnumerationEntryReadView member = emv.getMember();
	        					    		PropertyValueReadView readView = member.getPropertyValueByName("displayName");
	        					    		if (readView != null && rs.equals(member.getName())) {
	        					    			rtStr = readView.getValue().toString();
	        					    			break;
	        					    		}
	        					    	}
	        				    	}
	        				    }
	        				    if (rtStr != null) {
	        				    	break;
	        				    }
	        				}
	        			}
	        			if (rtStr != null) {
	        				rs = rtStr;
	        			}
	    				break;
	    			}
	    		}
	    	}
		} catch (WTException e) {
			e.printStackTrace();
		}
    	return rs;
	}

	public static void setIBAValueWhenCreate(IBAHolder docObj, String ibaKey, String ibaValue) throws IBAConstraintException {
		DefaultAttributeContainer attrContainer = (DefaultAttributeContainer) docObj.getAttributeContainer();
    	if (attrContainer != null) {
    		AbstractValueView[] attrValViews = attrContainer.getAttributeValues();
    		if (attrValViews == null || attrValViews.length == 0) {
    			
    		} else {
	    		for (AbstractValueView attrValView : attrValViews) {
	    			String logicDef = attrValView.getDefinition().getName();
	    			if (ibaKey.equals(logicDef)) {
	    				StringValueDefaultView strView = (StringValueDefaultView) attrValView;
						try {
							strView.setValue(ibaValue);
						} catch (WTPropertyVetoException e) {
							e.printStackTrace();
						}
						attrContainer.updateAttributeValue(strView);
						break;
	    			}
	    		}
    		}
    	}
	}
	
	
	public static void main(String[] args) throws WTRuntimeException, WTException {
		String oid = "";
		ReferenceFactory rf = new ReferenceFactory();
		Persistable po = rf.getReference(oid).getObject();
		String ibaKey = "test";
		IBAUtil ibaUtil = new IBAUtil((IBAHolder) po);
		
		System.out.println(ibaUtil.getIBAValue(ibaKey));
		
		
	}
	
}
