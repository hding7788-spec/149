package ext.casc.change;

import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import wt.change2.ChangeHelper2;
import wt.change2.ChangeNoticeComplexity;
import wt.change2.ChangeOrder2;
import wt.change2.ChangeRequest2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.Streamed;
import wt.doc.WTDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.OrderByExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.team.Team;
import wt.team.TeamHelper;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;

import ext.casc.doc.CSCDoc;
import ext.casc.part.CSCPart;
import ext.casc.product.CSCProduct;
import ext.casc.util.CSCIBA;

public class CSCChange {
	
	public static String TYPE = "TYPE";
	
	public static String NAME = "NAME";
	
	public static String NEED_DATE = "NEEDDATE";
	
	public static String DESCRIPTION = "DESCRIPTION";
	
	public static String NUMBER = "NUMBER";

	
	/**
	 * This method is used to get TAECR by its name or number. 
	 * @param nameORnumber ECR name or number
	 * @return Engineering Change Request
	 */
	public static WTChangeRequest2 getECR(String nameORnumber){
		try {
			QuerySpec criteria = new QuerySpec(WTChangeRequest2.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeRequest2.class,
	        		WTChangeRequest2.NAME,SearchCondition.EQUAL,nameORnumber,false));	         
	        criteria.appendOr();
	        criteria.appendSearchCondition(new SearchCondition(WTChangeRequest2.class,
	        		WTChangeRequest2.NUMBER,SearchCondition.EQUAL,nameORnumber,false));
	        
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        if(results.hasMoreElements()){
	        	return (WTChangeRequest2)results.nextElement();
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return null;		
	}	

	
	public static WTChangeIssue getProblemReport(String nameORnumber){
		try {
			QuerySpec criteria = new QuerySpec(WTChangeIssue.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeIssue.class,
	        		WTChangeIssue.NAME,SearchCondition.EQUAL,nameORnumber,false));	         
	        criteria.appendOr();
	        criteria.appendSearchCondition(new SearchCondition(WTChangeIssue.class,
	        		WTChangeIssue.NUMBER,SearchCondition.EQUAL,nameORnumber,false));
	        
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        if(results.hasMoreElements()){
	        	return (WTChangeIssue)results.nextElement();
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return null;	
	}
	
	/**
	 * This method is used to get TAECR by its name. 
	 * @param ECR name
	 * @return an ArrayList filled with WTChangeRequest2 objects
	 */
	public static ArrayList getECRByName(String strName){
		ArrayList aWTECR = new ArrayList();
		try {
			QuerySpec criteria = new QuerySpec(WTChangeRequest2.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeRequest2.class,
	        		WTChangeRequest2.NAME,SearchCondition.LIKE,strName,false));	         
	        
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        while(results.hasMoreElements()){
	        	WTChangeRequest2 wtECR = (WTChangeRequest2)results.nextElement();
	        	aWTECR.add(wtECR);
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return aWTECR;		
	}	

	/**
	 * 
	 * @param ecr
	 * @param attributes
	 * @return
	 */
	public static WTChangeRequest2 updateECR(WTChangeRequest2 ecr,HashMap attributes){		
		try {						
			String desc = (String)attributes.get(DESCRIPTION);
			if(desc != null){
				ecr.setDescription(desc);
			}

			String need_date = getStringValue((String)attributes.get(NEED_DATE));
			if(!"".equals(need_date)){
				Date dh = new Date(need_date);
				ecr.setNeedDate(new Timestamp(dh.getTime()));	            
			}													
			
			String type = (String)attributes.get(TYPE);
			if(type!=null && !"".equals(type)){				
				if(!type.startsWith("WCTYPE|"))
					type = "WCTYPE|" + type;
				TypeIdentifier id = TypeHelper.getTypeIdentifier(type);												
				ecr = (WTChangeRequest2)CoreMetaUtility.setType(ecr,id);
			}
			
			ecr = (WTChangeRequest2)PersistenceHelper.manager.save(ecr);
			
		} catch (Exception e) {			
			e.printStackTrace();					
		}		
		return ecr;			
	}
	
	/**
	 * Query ECR by product name
	 * @return an ArrayList filled with PDMLinkProduct objects
	 */
	public static ArrayList getECRByProduct(String productName){
		ArrayList aWTECR = new ArrayList();
		PDMLinkProduct wtProduct = CSCProduct.getPDMLinkProduct(productName);
		if(wtProduct == null) return null;
		ObjectIdentifier objProduct = PersistenceHelper.getObjectIdentifier(wtProduct);
		
		try {
			QuerySpec criteria = new QuerySpec(WTChangeRequest2.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeRequest2.class,
	        		"containerReference.key",SearchCondition.EQUAL,objProduct));
	        ClassAttribute clsAttr = new ClassAttribute(WTChangeRequest2.class,WTChangeRequest2.NUMBER);
	        OrderBy order = new OrderBy((OrderByExpression)clsAttr,true);
	        criteria.appendOrderBy(order);
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        while(results.hasMoreElements()){
	        	WTChangeRequest2 wtECR = (WTChangeRequest2)results.nextElement();
	        	aWTECR.add(wtECR);
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return aWTECR;
	}
	
	/**
	 * Added by Ring 20080721 for getting the TAECR
	 * @param productName
	 * @return an ArrayList of WTChangeRequset2 which type is "TAECR"
	 */
	public static ArrayList getTAECRByProdudct(String productName){
		ArrayList aWTECR = new ArrayList();
		PDMLinkProduct wtProduct = CSCProduct.getPDMLinkProduct(productName);
		if(wtProduct == null) return null;
		ObjectIdentifier objProduct = PersistenceHelper.getObjectIdentifier(wtProduct);
		try {
			QuerySpec criteria= new QuerySpec(WTTypeDefinition.class);	
			criteria.appendSearchCondition(new SearchCondition(WTTypeDefinition.class, "logicalIdentifier", SearchCondition.LIKE, "%TAECR%"));
			QueryResult results = PersistenceHelper.manager.find(criteria);
			
			int i=0;
			long[] allLongArrID=new long[results.size()];
			while(results.hasMoreElements())
			{
				WTTypeDefinition wttypedef=(WTTypeDefinition)results.nextElement();	
				String identity = wttypedef.getIdentity();
				String id=identity.substring(identity.lastIndexOf(":")+1);
				allLongArrID[i]=Long.parseLong(id);
				i++;
			}
			
			QuerySpec criteria2 = new QuerySpec(WTChangeRequest2.class);
	        criteria2.appendSearchCondition(new SearchCondition(WTChangeRequest2.class,
	        		"containerReference.key",SearchCondition.EQUAL,objProduct));
	        
	        criteria2.appendAnd();
	        criteria2.appendSearchCondition(new SearchCondition(WTChangeRequest2.class,
	        		"typeDefinitionReference.key.id",allLongArrID));
	        
	        ClassAttribute clsAttr = new ClassAttribute(WTChangeRequest2.class,WTChangeRequest2.NUMBER);
	        OrderBy order = new OrderBy((OrderByExpression)clsAttr,true);
	        criteria2.appendOrderBy(order);
	        QueryResult results2 = PersistenceHelper.manager.find(criteria2);
	        
	        while(results2.hasMoreElements()){
	        	WTChangeRequest2 wtECR = (WTChangeRequest2)results2.nextElement();
	        	aWTECR.add(wtECR);
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return aWTECR;
	}
	
	/**
	 * This method is used to create ECR
	 * @param name name 
	 * @param number number
	 * @param attributes
	 * @param product
	 * @return
	 */
	public static WTChangeRequest2 createECR(String name,HashMap attributes,PDMLinkProduct product){
		//System.out.println("begin to create ECR name=" + name + " attributes=" + attributes + "  product=" + product);
		WTChangeRequest2 ecr = null;
		try {
			ecr = WTChangeRequest2.newWTChangeRequest2(name);
			if(product != null)
				ecr.setContainer(product);
			String description = getStringValue((String)attributes.get(DESCRIPTION));
			if(!"".equals(description))
				ecr.setDescription(description);	
			
			String need_date = getStringValue((String)attributes.get(NEED_DATE));
			if(!"".equals(need_date)){
				Date dh = new Date(need_date);
	            ecr.setNeedDate(new Timestamp(dh.getTime()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String type = getStringValue((String)attributes.get(TYPE));	
			if(!"".equals(type)){
				if(!type.startsWith("WCTYPE"))
					type = "WCTYPE|" + type;
				TypeIdentifier id = TypeHelper.getTypeIdentifier(type);												
				ecr = (WTChangeRequest2)CoreMetaUtility.setType(ecr,id);
			}			
		} catch (Exception e) {
//			System.out.println("\t===create ECR Error: when setting TYPE error happends");
			e.printStackTrace();			
		}
		
		try {
			ecr = (WTChangeRequest2)ChangeHelper2.service.saveChangeRequest(ecr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return ecr;
	}
	
	
	/**
	 * This method is used to get TAECR by its related Part 
	 * @param ECR name
	 * @return an ArrayList filled with WTChangeRequest2 objects
	 */
	public static ArrayList getECRByPart(String partNumber){
		ArrayList aWTECR = new ArrayList();
		String user = "";
		try{
			user = wt.session.SessionHelper.manager.getPrincipal().getName();
			wt.session.SessionHelper.manager.setAdministrator();
			
			WTPart wtPart = CSCPart.getPart(partNumber);
			if(wtPart == null){
				return null;
			}
			QueryResult results = ChangeHelper2.service.getRelevantChangeRequests(wtPart);
	        while(results.hasMoreElements()){
	        	WTChangeRequest2 wtECR = (WTChangeRequest2)results.nextElement();
	        	aWTECR.add(wtECR);
	        }
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(user != null && !user.equals(""))
					wt.session.SessionHelper.manager.setPrincipal(user);
			} catch (Exception e) {
				// TODO: handle exception
			}			
		}
		return aWTECR;
	}
	
	/**
	 * This method is used to get TAECR by its related Part 
	 * @param ECR name
	 * @return an ArrayList filled with WTChangeRequest2 objects
	 */
	public static ArrayList getECRByDoc(String docNumber){
		ArrayList aWTECR = new ArrayList();
		String user = "";
		try{
			user = wt.session.SessionHelper.manager.getPrincipal().getName();
			wt.session.SessionHelper.manager.setAdministrator();
			
			WTDocument wtDoc = CSCDoc.getDoc(docNumber);
			if(wtDoc == null){
				return null;
			}
			
			QueryResult results = ChangeHelper2.service.getRelevantChangeRequests(wtDoc);
	        while(results.hasMoreElements()){
	        	WTChangeRequest2 wtECR = (WTChangeRequest2)results.nextElement();
	        	aWTECR.add(wtECR);
	        }
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(user != null && !user.equals(""))
					wt.session.SessionHelper.manager.setPrincipal(user);
			} catch (Exception e) {
				// TODO: handle exception
			}			
		}
		return aWTECR;
	}
	
	/**
	 * 
	 */
	public static ArrayList getECRByIBAField(String IBAField, String value){
		ArrayList aWTECR = new ArrayList();
		Vector vWTECR = CSCIBA.getObjectByIBA(WTChangeRequest2.class, IBAField, value, true);
		
		for (int i = 0; i < vWTECR.size(); i++) {
			aWTECR.add(vWTECR.get(i));
		}
		return aWTECR;
	}
	
	/**
	 * Get All the ECRs which are not "Accomplished"
	 * @return ArrayList filled with WTChangeRequest2 objects
	 */
	public static ArrayList getOpeningECR(){
		ArrayList aWTECR = new ArrayList();
		try {
			QuerySpec criteria = new QuerySpec(WTChangeRequest2.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeRequest2.class,
	        		WTChangeRequest2.LIFE_CYCLE_STATE ,SearchCondition.NOT_IN,"Accomplished",false));	         
	        
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        while(results.hasMoreElements()){
	        	WTChangeRequest2 wtECR = (WTChangeRequest2)results.nextElement();
	        	aWTECR.add(wtECR);
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return aWTECR;	
	}
	
	/**
	 * Query ECR By a group of LifeCycle State.
	 * @param lifeCycle ArrayList stores the LifeCycle State of String.
	 * @return ArrayList stores the WTChangeRequest2 object
	 */
	public static ArrayList getECRByLifeCycle(ArrayList lifeCycle){
		ArrayList aWTECR = new ArrayList();
		try{
			QuerySpec criteria = new QuerySpec(WTChangeRequest2.class);
			for(int i=0; i<lifeCycle.size(); i++){
				String strLifeCycle = (String)lifeCycle.get(i);
		        criteria.appendSearchCondition(new SearchCondition(WTChangeRequest2.class,
		        		WTChangeRequest2.LIFE_CYCLE_STATE ,SearchCondition.EQUAL,strLifeCycle,false));	
		        if(!((i+1)== lifeCycle.size())){
		        	criteria.appendOr();
		        }
			}
//			System.out.println(criteria.toString());
			QueryResult results = PersistenceHelper.manager.find(criteria);
	        while(results.hasMoreElements()){
	        	WTChangeRequest2 wtECR = (WTChangeRequest2)results.nextElement();
	        	aWTECR.add(wtECR);
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		return aWTECR;
	}
	
	/**
	 * This method is used to get ECN by its name or number.
	 * @param nameORnumber ECN name or number
	 * @return Engineering Change Notice
	 */	
	public static WTChangeOrder2 getECN(String nameORnumber){
		try {
			QuerySpec criteria = new QuerySpec(WTChangeOrder2.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeOrder2.class,
	        							WTChangeOrder2.NAME,SearchCondition.EQUAL,nameORnumber,false));	         
	        criteria.appendOr();
	        criteria.appendSearchCondition(new SearchCondition(WTChangeOrder2.class,
										WTChangeOrder2.NUMBER,SearchCondition.EQUAL,nameORnumber,false));
	        
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        if(results.hasMoreElements()){
	        	return (WTChangeOrder2)results.nextElement();
	        }
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return null;
		
	}
	
	/**
	 * This method is used to get ECR by its name. 
	 * @param ECR name
	 * @return an ArrayList filled with WTChangeRequest2 objects
	 */
	public static ArrayList getECNByName(String strName){
		ArrayList aWTECN = new ArrayList();
		try {
			QuerySpec criteria = new QuerySpec(WTChangeOrder2.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeOrder2.class,
	        		WTChangeOrder2.NAME,SearchCondition.LIKE,strName,false));	         
	        
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        while(results.hasMoreElements()){
	        	WTChangeOrder2 wtECN = (WTChangeOrder2)results.nextElement();
	        	aWTECN.add(wtECN);
	        }
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return aWTECN;		
	}	

	/**
	 * Query ECN by product name
	 * @return an ArrayList filled with PDMLinkProduct objects
	 */
	public static ArrayList getECNByProduct(String productName){
		ArrayList aWTECN = new ArrayList();
		PDMLinkProduct wtProduct = CSCProduct.getPDMLinkProduct(productName);
		
		if(wtProduct == null) return null;
		ObjectIdentifier objProduct = PersistenceHelper.getObjectIdentifier(wtProduct);
		
		try {
			QuerySpec criteria = new QuerySpec(WTChangeOrder2.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeOrder2.class,
	        		"containerReference.key",SearchCondition.EQUAL,objProduct));
	        ClassAttribute clsAttr = new ClassAttribute(WTChangeOrder2.class,WTChangeOrder2.NUMBER);
	        OrderBy order = new OrderBy((OrderByExpression)clsAttr,true);
	        criteria.appendOrderBy(order);
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        while(results.hasMoreElements()){
	        	WTChangeOrder2 wtECN = (WTChangeOrder2)results.nextElement();
	        	aWTECN.add(wtECN);
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return aWTECN;
	}
	
	/**
	 * 
	 */
	public static ArrayList getECNByIBAField(String IBAField, String value){
		ArrayList aWTECN = new ArrayList();
		Vector vWTECN = CSCIBA.getObjectByIBA(WTChangeOrder2.class, IBAField, value, true);
		
		for (int i = 0; i < vWTECN.size(); i++) {
			aWTECN.add(vWTECN.get(i));
		}
		return aWTECN;
	}
	
	/**
	 * Get All the ECNs which are not "Accomplished"
	 * @return ArrayList filled with WTChangeOrder2 objects
	 */
	public static ArrayList getOpeningECN(){
		ArrayList aWTECN = new ArrayList();
		try {
			QuerySpec criteria = new QuerySpec(WTChangeOrder2.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeOrder2.class,
	        		WTChangeOrder2.LIFE_CYCLE_STATE ,SearchCondition.NOT_IN,"Accomplished",false));	         
	        
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        while(results.hasMoreElements()){
	        	WTChangeOrder2 wtECN = (WTChangeOrder2)results.nextElement();
	        	aWTECN.add(wtECN);
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return aWTECN;	
	}

	/**
	 * Query ECN By a group of LifeCycle State.
	 * @param lifeCycle ArrayList stores the LifeCycle State of String.
	 * @return ArrayList stores the WTChangeOrder2 object
	 */
	public static ArrayList getECNByLifeCycle(ArrayList lifeCycle){
		ArrayList aWTECN = new ArrayList();
		try{
			QuerySpec criteria = new QuerySpec(WTChangeOrder2.class);
			for(int i=0; i<lifeCycle.size(); i++){
				String strLifeCycle = (String)lifeCycle.get(i);
		        criteria.appendSearchCondition(new SearchCondition(WTChangeOrder2.class,
		        		WTChangeOrder2.LIFE_CYCLE_STATE ,SearchCondition.EQUAL,strLifeCycle,false));	
		        if(!((i+1)== lifeCycle.size())){
		        	criteria.appendOr();
		        }
			}
//			System.out.println(criteria.toString());
			QueryResult results = PersistenceHelper.manager.find(criteria);
	        while(results.hasMoreElements()){
	        	WTChangeOrder2 wtECN = (WTChangeOrder2)results.nextElement();
	        	aWTECN.add(wtECN);
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		return aWTECN;
	}
	
	
	/**
	 * This method is used to create ECN
	 * @param name
	 * @param attributes
	 * @param container
	 * @param ecr releated ECR
	 * @return
	 */
	public static WTChangeOrder2 createECN(String name,HashMap attributes,PDMLinkProduct container,WTChangeRequest2 ecr){
		WTChangeOrder2 ecn = null;		
		try {
			ecn = WTChangeOrder2.newWTChangeOrder2(name);			
			
			String desc = (String)attributes.get(DESCRIPTION);
			if(desc != null){
				ecn.setDescription(desc);
			}

			String need_date = getStringValue((String)attributes.get(NEED_DATE));
			if(!"".equals(need_date)){
				Date dh = new Date(need_date);
	            ecn.setNeedDate(new Timestamp(dh.getTime()));
			}													
			
			ecn.setContainer(container);
			String type = (String)attributes.get(TYPE);
			if(type!=null && !"".equals(type)){				
				if(!type.startsWith("WCTYPE|"))
					type = "WCTYPE|" + type;
				TypeIdentifier id = TypeHelper.getTypeIdentifier(type);												
				ecn = (WTChangeOrder2)CoreMetaUtility.setType(ecn,id);
			}
			
			ecn = (WTChangeOrder2)ChangeHelper2.service.saveChangeOrder(ecr, ecn);
//			System.out.println("\t==end of creating ECN " + ecn);
			
		} catch (Exception e) {			
			e.printStackTrace();					
		}		
		return ecn;			
	}
	
	/**
	 * 
	 * 
	 * @param name
	 * @param attributes
	 * @param container
	 * @return
	 */
	public static WTChangeOrder2 createECN(String number, String name,HashMap attributes,WTContainer container){
		WTChangeOrder2 ecn = null;	
		try {
			ecn = WTChangeOrder2.newWTChangeOrder2(name);			
			ecn.setChangeNoticeComplexity(ChangeNoticeComplexity.BASIC);

			if(number != null){
				ecn.setNumber(number);
			}
			
			String desc = (String)attributes.get(DESCRIPTION);
			if(desc != null){
				ecn.setDescription(desc);
			}

			String need_date = getStringValue((String)attributes.get(NEED_DATE));
			if(!"".equals(need_date)){
				Date dh = new Date(need_date);
	            ecn.setNeedDate(new Timestamp(dh.getTime()));
			}													
			
			ecn.setContainer(container);
			String type = (String)attributes.get(TYPE);
			if(type!=null && !"".equals(type)){				
				if(!type.startsWith("WCTYPE|"))
					type = "WCTYPE|" + type;
				TypeIdentifier id = TypeHelper.getTypeIdentifier(type);												
				ecn = (WTChangeOrder2)CoreMetaUtility.setType(ecn,id);
			}
			
			
			
			ecn = (WTChangeOrder2)ChangeHelper2.service.saveChangeOrder(ecn);
//			System.out.println("\t==end of creating ECN " + ecn);
			
		} catch (Exception e) {			
			e.printStackTrace();					
		}		
		return ecn;	
	}
	
	/**
	 * 
	 * @param ecn
	 * @param attributes
	 * @return
	 */
	public static WTChangeOrder2 updateECN(WTChangeOrder2 ecn,HashMap attributes){		
		try {						
			String desc = (String)attributes.get(DESCRIPTION);
			if(desc != null){
				ecn.setDescription(desc);
			}

			String need_date = getStringValue((String)attributes.get(NEED_DATE));
			if(!"".equals(need_date)){
				Date dh = new Date(need_date);
	            ecn.setNeedDate(new Timestamp(dh.getTime()));	            
			}													
			
			String type = (String)attributes.get(TYPE);
			if(type!=null && !"".equals(type)){				
				if(!type.startsWith("WCTYPE|"))
					type = "WCTYPE|" + type;
				TypeIdentifier id = TypeHelper.getTypeIdentifier(type);												
				ecn = (WTChangeOrder2)CoreMetaUtility.setType(ecn,id);
			}
			
			ecn = (WTChangeOrder2)PersistenceHelper.manager.save(ecn);
			
		} catch (Exception e) {			
			e.printStackTrace();					
		}		
		return ecn;			
	}

	/**
	 * Enhanced by Ring 20080428
	 * Add some members into ECR Approvers Role which is in ECR Team
	 * @param wtECR
	 * @param roleName
	 * @param members
	 * @return
	 */
	public static WTChangeRequest2 addECRTeamMemebers(WTChangeRequest2 wtECR, String roleName, ArrayList members){
		try{
			Team wtECRTeam = TeamHelper.service.getTeam(wtECR);
			Role role = Role.toRole(roleName);
			for (int i = 0; i < members.size(); i++) {
				WTPrincipal wtMember = (WTPrincipal)members.get(i);
				wtECRTeam.addPrincipal(role, wtMember);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		return wtECR;
	}
	
	/**
	 * Enhanced by Ring 20080516
	 * Add some members into ECN Approvers Role which is in ECN Team
	 * @param wtECN
	 * @param roleName
	 * @param members
	 * @return
	 */
	public static WTChangeOrder2 addECNTeamMemebers(WTChangeOrder2 wtECN, String roleName, ArrayList members){
		try{
			Team wtECNTeam = TeamHelper.service.getTeam(wtECN);
			Role role = Role.toRole(roleName);
			for (int i = 0; i < members.size(); i++) {
				WTPrincipal wtMember = (WTPrincipal)members.get(i);
				wtECNTeam.addPrincipal(role, wtMember);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		return wtECN;
	}
	
	/**
	 * 
	 * @param name
	 * @param attributes
	 * @param productName
	 * @param ecrName
	 * @return
	 */
	public static WTChangeOrder2 createECN(String name,HashMap attributes,String productName,String ecrName){
				
		PDMLinkProduct product = CSCProduct.getPDMLinkProduct(productName);		
		WTChangeRequest2 ecr = getECR(ecrName);
		if(product == null)
			return null;
		return createECN(name,attributes, product,ecr);				
	}
	
	public static WTChangeRequest2 createECR(String name,HashMap attributes,String productNumber){
		PDMLinkProduct product = CSCProduct.getPDMLinkProduct(productNumber);
		if(product == null)
			return null;
		return createECR(name,attributes,product);
		
	}
	
	/**
	 * This method is used to create CA
	 * @param name name
	 * @param attributes
	 * @param product context
	 * @param ecn releated ECN
	 * @return
	 */
	public static WTChangeActivity2 createCA(String name, HashMap attributes,WTChangeOrder2 ecn){
		WTChangeActivity2 ca = null;
		try {
			ca = WTChangeActivity2.newWTChangeActivity2(name);			
			
			//set description
			String desc = getStringValue((String)attributes.get(DESCRIPTION));
			if(!"".equals(desc))
				ca.setDescription(desc);
			
			//set need date
			String need_date = getStringValue((String)attributes.get(NEED_DATE));
			if(!"".equals(need_date)){
				Date dh = new Date(need_date);
	            ca.setNeedDate(new Timestamp(dh.getTime()));
			}
			
			WTContainer container = WTContainerHelper.getContainer(ecn);
			ca.setContainer(container);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(ca != null){
				String type = getStringValue((String)attributes.get(TYPE));	
				if(!"".equals(type)){
					if(!type.startsWith("WCTYPE"))
						type = "WCTYPE|" + type;
					TypeIdentifier id = TypeHelper.getTypeIdentifier(type);												
					ca = (WTChangeActivity2)CoreMetaUtility.setType(ca,id);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(ca != null)
				ca = (WTChangeActivity2)ChangeHelper2.service.saveChangeActivity(ecn, ca);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ca;
		
	}
	
	public static void deleteCA(WTChangeActivity2 ca){
		try {
			if(ca != null)
				ca = (WTChangeActivity2)ChangeHelper2.service.deleteChangeActivity(ca);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param name
	 * @param attributes
	 * @param product
	 * @param ecn
	 * @return
	 */
	public static WTChangeActivity2 createCA(String name, HashMap attributes,String ecn){		
		WTChangeOrder2 pdmECN = CSCChange.getECN(ecn);
		return createCA(name,attributes,pdmECN);
	}
	
	/**
	 * This method is used to get releated ECN from a given ECR
	 * @param ecr
	 * @param onlyName
	 * @return
	 */
	public static ArrayList getReleatedECN(WTChangeRequest2 ecr,boolean onlyName){
		ArrayList list = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeOrders(ecr);
			while(qr.hasMoreElements()){
				WTChangeOrder2 ecn = (WTChangeOrder2)qr.nextElement();
				if(onlyName)
					list.add(ecn.getName());
				else
					list.add(ecn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return list;		
	}
	
	/**
	 * This method is used to get Releated ECN from given Change Activity
	 * @param ca
	 * @param onlyName if only return name
	 * @return
	 */
	public static ArrayList getReleatedECN(WTChangeActivity2 ca,boolean onlyName){
		ArrayList list = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeOrder(ca);
			while(qr.hasMoreElements()){
				WTChangeOrder2 ecn = (WTChangeOrder2)qr.nextElement();
				if(onlyName)
					list.add(ecn.getName());
				else
					list.add(ecn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return list;		
	}
	
	/**
	 * This method is used to get releated ECR from a given ECN
	 * @param ecn 
	 * @param onlyName if only return name
	 * @return
	 */
	public static ArrayList getReleatedECR(WTChangeOrder2 ecn,boolean onlyName){
		ArrayList list = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeRequest(ecn);
			while(qr.hasMoreElements()){
				WTChangeRequest2 ecr = (WTChangeRequest2)qr.nextElement();
				if(onlyName)
					list.add(ecr.getName());
				else
					list.add(ecr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return list;		
	}
	
	
	/**
	 * This method is used to get CA by its name or number. 
	 * @param nameORnumber CA name or number
	 * @return WTChangeActivity2
	 */
	public static WTChangeActivity2 getCA(String nameORnumber){
		try {
			QuerySpec criteria = new QuerySpec(WTChangeActivity2.class);
	        criteria.appendSearchCondition(new SearchCondition(WTChangeActivity2.class,
	        		WTChangeActivity2.NAME,SearchCondition.EQUAL,nameORnumber,false));	         
	        criteria.appendOr();
	        criteria.appendSearchCondition(new SearchCondition(WTChangeActivity2.class,
	        		WTChangeActivity2.NUMBER,SearchCondition.EQUAL,nameORnumber,false));
	        
	        QueryResult results = PersistenceHelper.manager.find(criteria);
	        if(results.hasMoreElements()){
	        	return (WTChangeActivity2)results.nextElement();
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return null;		
	}
	
	/**
	 * 
	 * @param ecn
	 * @param onlyNumber
	 * @return
	 */
	public static ArrayList getReleatedCA(WTChangeOrder2 ecn,boolean onlyNumber){
		ArrayList list = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getChangeActivities(ecn);
			while(qr.hasMoreElements()){
				WTChangeActivity2 ca = (WTChangeActivity2)qr.nextElement();
				if(onlyNumber)
					list.add(ca.getNumber());
				else
					list.add(ca);
			}
		} catch (Exception e) {			
		}
		
		return list;
	}
	
	/**
	 * retrive a not-null value of a given string
	 * @param value
	 * @return
	 */
	public static String getStringValue(String value){
		if(value == null)
			return "";
		value = value.trim();
		return value;
		
	}
	
	/**
	 * 
	 * @param ecr
	 * @param affectedData
	 */
	public static void setECRAffectedData(WTChangeRequest2 ecr,Vector affectedData ){		
		String user = "";
    	try {
    		user = wt.session.SessionHelper.manager.getPrincipal().getName();
    		wt.session.SessionHelper.manager.setAdministrator();
		} catch (Exception e) {
		}
		try {
			Vector vector = ChangeHelper2.service.storeAssociations(wt.change2.RelevantRequestData2.class, ecr, affectedData);
//			if(vector != null)
//				System.out.println("\t==after adding affected data, return vector=" + vector.toString());
//			else
//				System.out.println("\t==after adding affected data, return vector=" + vector.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}	
		if(!"".equals(user)){
			try {
				wt.session.SessionHelper.manager.setPrincipal(user);
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 
	 * @param holder
	 * @param filepath
	 * @param role
	 * @return
	 */
	public static ContentHolder addAttachmentToChange(ContentHolder holder,String filepath,ContentRoleType role){
		if(holder == null)
			return null;
		try {		
			//holder = (ContentHolder)PersistenceHelper.manager.lockAndRefresh(holder);
			ApplicationData ap = ApplicationData.newApplicationData(holder);
			ap.setRole(role);
			ap = ContentServerHelper.service.updateContent(holder, ap, filepath);
			holder = (ContentHolder)PersistenceHelper.manager.save(holder);
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		return holder;
		
	}
	
	public static ContentHolder removeAllAttachmentsToChange(ContentHolder holder,ContentRoleType role){
		try {
			 QueryResult qr = ContentHelper.service.getContentsByRole(holder, role);
			 while(qr.hasMoreElements()){
				  ApplicationData data = (ApplicationData) qr.nextElement();
				  ContentServerHelper.service.deleteContent(holder, data);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return holder;

	}
	
	public static HashMap getAttachments(ContentHolder holder,ContentRoleType role){
		if(holder == null)
			return null;
		HashMap fileNameToURL = new HashMap();
		try {					
			QueryResult qr = ContentHelper.service.getContentsByRole(holder, role);
			while(qr.hasMoreElements()){
				ApplicationData ap = (ApplicationData)qr.nextElement();
				String filename = ap.getFileName();
				URL url = ContentHelper.getDownloadURL(holder, ap);
				fileNameToURL.put(filename, url.toString());
			}			
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		return fileNameToURL;		
	}

	public static ArrayList<InputStream> getAttachmentsInputStream(ContentHolder holder,ContentRoleType role){
		ArrayList<InputStream> result = new ArrayList<InputStream>();
		try {
			 QueryResult qr = ContentHelper.service.getContentsByRole(holder, role);
			 while(qr.hasMoreElements()){
				  ApplicationData data = (ApplicationData) qr.nextElement();
                  Streamed streamed = (Streamed) PersistenceHelper.manager.refresh(data.getStreamData().getObjectId());
                  InputStream ips = streamed.retrieveStream();
                  result.add(ips);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * @param cRequest
	 * @return
	 */
    public static String checkOrdersFinished(ChangeRequest2 cRequest)    
	{
    	try {
    		QueryResult result = ChangeHelper2.service.getChangeOrders(cRequest);
    	    return checkLifeCycleManagedFinished(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return null;
	}
    
    /**
     * 
     * @param cOrder
     * @return
     */
    public static String checkActivitiesFinished(ChangeOrder2 cOrder)   
	{
	    QueryResult result;
		try {
			result = ChangeHelper2.service.getChangeActivities(cOrder);
			return checkLifeCycleManagedFinished(result);
		} catch (Exception e) {		
			e.printStackTrace();
		}
	    return null;
	}
	
    /**
     * 
     * @param result
     * @return
     */
	public static String checkLifeCycleManagedFinished(QueryResult result)
    {
        String result_value = null;
        while(result.hasMoreElements()) 
        {
            Persistable persistable = (Persistable)result.nextElement();
            if(persistable instanceof LifeCycleManaged)
            {
                LifeCycleManaged life_cycle_managed = (LifeCycleManaged)persistable;
                if(life_cycle_managed.getLifeCycleState().equals(State.toState("Accomplished")))
                {
                    result_value = "Resolved";
                } else
                if(life_cycle_managed.getLifeCycleState().equals(State.toState("CANCELLED")))
                {
                    if(result_value != null || !result_value.equals("Accomplished"))
                    {
                        result_value = "Cancelled";
                    }
                } else
                {
                    return null;
                }
            }
        }
        return result_value;
    }
	
	
	public static ArrayList<WTObject> getCAAffectedData(WTChangeActivity2 ca){
		ArrayList<WTObject> result = new ArrayList<WTObject>();
		
		String user = "";
    	try {
    		user = wt.session.SessionHelper.manager.getPrincipal().getName();
    		wt.session.SessionHelper.manager.setAdministrator();
		} catch (Exception e) {
		}
		try {
			QueryResult qr = ChangeHelper2.service.getChangeablesBefore(ca);
			while (qr.hasMoreElements()) {
				WTObject wtobject = (WTObject)qr.nextElement();
				result.add(wtobject);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		if(!"".equals(user)){
			try {
				wt.session.SessionHelper.manager.setPrincipal(user);
			} catch (Exception e) {
			}
		}
		
		return result;
	}
	
	public static void setCAAffectedData(WTChangeActivity2 ca,Vector affectedData ){		
		String user = "";
    	try {
    		user = wt.session.SessionHelper.manager.getPrincipal().getName();
    		wt.session.SessionHelper.manager.setAdministrator();
		} catch (Exception e) {
		}
		try {
			Vector vector = ChangeHelper2.service.storeAssociations(wt.change2.AffectedActivityData.class, ca, affectedData);
//			if(vector != null)
//				System.out.println("\t==after adding CA affected data, return vector=" + vector.toString());
//			else
//				System.out.println("\t==after adding CA affected data, return vector=" + vector.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}	
		if(!"".equals(user)){
			try {
				wt.session.SessionHelper.manager.setPrincipal(user);
			} catch (Exception e) {
			}
		}
	}
	
	public static ArrayList<WTObject> getCAResultItem(WTChangeActivity2 ca){
		ArrayList<WTObject> result = new ArrayList<WTObject>();
		
		String user = "";
    	try {
    		user = wt.session.SessionHelper.manager.getPrincipal().getName();
    		wt.session.SessionHelper.manager.setAdministrator();
		} catch (Exception e) {
		}
		try {
			QueryResult qr = ChangeHelper2.service.getChangeablesAfter(ca);
			while (qr.hasMoreElements()) {
				WTObject wtobject = (WTObject)qr.nextElement();
				result.add(wtobject);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		if(!"".equals(user)){
			try {
				wt.session.SessionHelper.manager.setPrincipal(user);
			} catch (Exception e) {
			}
		}
		
		return result;
	}
	
	public static void setCAResultItem(WTChangeActivity2 ca,Vector affectedData ){		
		String user = "";
    	try {
    		user = wt.session.SessionHelper.manager.getPrincipal().getName();
    		wt.session.SessionHelper.manager.setAdministrator();
		} catch (Exception e) {
		}
		try {
			Vector vector = ChangeHelper2.service.storeAssociations(wt.change2.ChangeRecord2.class, ca, affectedData);
//			if(vector != null)
//				System.out.println("\t==after adding result items, return vector=" + vector.toString());
//			else
//				System.out.println("\t==after adding result items, return vector=" + vector.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}	
		if(!"".equals(user)){
			try {
				wt.session.SessionHelper.manager.setPrincipal(user);
			} catch (Exception e) {
			}
		}
	}
	
}
