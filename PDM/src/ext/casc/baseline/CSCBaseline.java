package ext.casc.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.vc.baseline.BaselineHelper;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.Baselineable;
import wt.vc.baseline.ManagedBaseline;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;

public class CSCBaseline {
	public static String NAME = "NAME";
	public static String NUMBER = "NUMBER";
	public static String FOLDER = "FOLDER";
	public static String DESCRIPTION = "DESCRIPTION";
	public static String LIFECYCLE = "LIFECYCLE";
	public static String STATUS = "STATUS";
	public static String TYPE = "TYPE";
	
	/**
	 * This method is used to create a baseline
	 * @param name baseline name
	 * @param number baseline number
	 * @param attributes baseline attributes, such as NAME,FOLDER,DESCRIPTION, LIFECYCLE,TYPE
	 * @param container WTContainer
	 * @return baseline that be created
	 */
	public static  ManagedBaseline createBaseline(String name,String number,HashMap attributes,WTContainer container){
		if (attributes == null) attributes = new HashMap();
		 ManagedBaseline baseline = null;
		 String description = (String)attributes.get(DESCRIPTION);
         String folder = (String)attributes.get(FOLDER);
         String status = (String)attributes.get(STATUS);
      
         if(container == null){
//        	 System.out.println("TABaseline.class Error: Method=createBaseline, message= container can't be blank.");
        	 return null;
         }      
         
         WTContainerRef ref = null;
         try {
        	 ref = WTContainerRef.newWTContainerRef(container);
		} catch (Exception e) {			
		}
		
         Folder location = null;
         if(folder == null){
        	 folder= "/Default";
         }
       
    	 try {
			location = FolderHelper.service.getFolder(folder,ref);
			 if(location == null){
        		 location = FolderHelper.service.saveFolderPath(folder,ref );
        	 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        	        
		String lifecycle = (String)attributes.get(LIFECYCLE);
		LifeCycleTemplate lifecycleTemplate = null;
		try {
			if(lifecycle != null && !"".equals(lifecycle))
				lifecycleTemplate = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle,ref);
		} catch (Exception e) {
			// TODO: handle exception
//			System.out.print("get lifecycle template erorr: message= ");
			e.printStackTrace();
		}
         
		try {
			
			baseline = ManagedBaseline.newManagedBaseline();			
	         baseline.setName(name);         
	         
	         if(number != null && !"".equals(number))
	        	 baseline.setNumber(number);
	         
	         if(description != null)	        	 
	        	 baseline.setDescription(description);	         
	         
	         baseline.setContainer(container);
	         
	         if(location != null)
	        	 FolderHelper.assignLocation(baseline, location);
	         
	         if(status != null && !"".equals(status)) {
	        	 LifeCycleState lifeState = LifeCycleState.newLifeCycleState();
	        	 lifeState.setState(State.toState(status));
	        	 baseline.setState(lifeState);
	         }
	         
	         String type = (String)attributes.get(TYPE);
	         if(type!=null && !"".equals(type)){				
					if(!type.startsWith("WCTYPE|"))
						type = "WCTYPE|" + type;
					TypeIdentifier id = TypeHelper.getTypeIdentifier(type);		
					if(id != null)
						baseline = (ManagedBaseline)CoreMetaUtility.setType(baseline,id);
			}
	         if(lifecycleTemplate != null)
	        	 baseline = (ManagedBaseline)LifeCycleHelper.setLifeCycle(baseline, lifecycleTemplate);
	         
	         PersistenceHelper.manager.store(baseline);
		} catch (Exception e) {
//			System.out.println("TABaseline.class Error: Method=createBaseline, message=" + e.getMessage());
			e.printStackTrace();
		}			
		return baseline;
	}
	
	public static ManagedBaseline updateBaseline(ManagedBaseline baseline, String baselineDesc){
		try {
			if (baseline == null) return baseline;
			if (baselineDesc == null) baselineDesc = "";
			baseline.setDescription(baselineDesc);
			baseline = (ManagedBaseline)PersistenceHelper.manager.save(baseline);
		} catch (Exception e) {
			// TODO: handle exception
//			System.out.println("TABaseline.class Error: Method=updateBaseline, message=" + e.getMessage());
			e.printStackTrace();
		}
		return baseline;
	}
	
	public static void addBaselineable(ManagedBaseline baseline,Vector objects){
		try {
			BaselineHelper.service.addToBaseline(objects, baseline);
			
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addBaselineable(ManagedBaseline baseline,Baselineable object){
		try {
			if(object == null)
				return ;
			BaselineHelper.service.addToBaseline(object, baseline);
			
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void removeFromBaseline(ManagedBaseline baseline,Vector objects){
		try {
			BaselineHelper.service.removeFromBaseline(objects, baseline);
			
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void removeFromBaseline(ManagedBaseline baseline,Baselineable object){
		try {
			BaselineHelper.service.removeFromBaseline(object, baseline);
			
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ManagedBaseline getBaseline(String number) {
		ManagedBaseline baseline = null;
		String user = "";
		try {
			user = wt.session.SessionHelper.manager.getPrincipal().getName();
			wt.session.SessionHelper.manager.setAdministrator();

			QuerySpec qs = new QuerySpec(ManagedBaseline.class);
			SearchCondition sc = new SearchCondition(ManagedBaseline.class,
					ManagedBaseline.NUMBER, SearchCondition.EQUAL, number,
					false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				baseline = (ManagedBaseline) qr.nextElement();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (!"".equals(user)) {
				try {
					wt.session.SessionHelper.manager.setPrincipal(user);
				} catch (Exception e) {
				}
			}
		}
		return baseline;
	}
	
	public static ManagedBaseline getBaselineByName(String name) {
		ManagedBaseline baseline = null;
		String user = "";
		try {
			user = wt.session.SessionHelper.manager.getPrincipal().getName();
			wt.session.SessionHelper.manager.setAdministrator();

			QuerySpec qs = new QuerySpec(ManagedBaseline.class);
			SearchCondition sc = new SearchCondition(ManagedBaseline.class,
					ManagedBaseline.NAME, SearchCondition.EQUAL, name, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				baseline = (ManagedBaseline) qr.nextElement();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (!"".equals(user)) {
				try {
					wt.session.SessionHelper.manager.setPrincipal(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return baseline;
	}
	
	public static ArrayList getBaselines(String name,String containerName){
		String user = "";
		ArrayList list = new ArrayList();
		try {
    		user = wt.session.SessionHelper.manager.getPrincipal().getName();
//    		System.out.println("GET BASELINES USER IS:" + user);
    		wt.session.SessionHelper.manager.setAdministrator();
		
			QuerySpec qs = new QuerySpec(ManagedBaseline.class);			
			SearchCondition sc = new
					SearchCondition(ManagedBaseline.class, ManagedBaseline.NAME, SearchCondition.LIKE,"%"+containerName+"%"+name+"%",false);
			qs.appendSearchCondition(sc);
			//qs.appendAnd();
			
			//PDMLinkProduct wtProduct = TAProduct.getPDMLinkProduct(containerName);
			//WTContainerRef wtcontainerref = wtProduct.getContainerReference();
			//ObjectIdentifier objectidentifier = ObjectIdentifier.newObjectIdentifier(wtcontainerref.getKey().toString());
						
			//SearchCondition sc2 = new
			//		SearchCondition(ManagedBaseline.class, "containerReference.key", "=", objectidentifier);
			//qs.appendWhere(sc2);
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()){				
				ManagedBaseline baseline = (ManagedBaseline)qr.nextElement();
				list.add(baseline);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally{
			if(!"".equals(user)){
				try {
					wt.session.SessionHelper.manager.setPrincipal(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	
	public static ArrayList getBaselinesByNumberLike(String numberLike) {
		String user = "";
		ArrayList list = new ArrayList();
		try {
			user = wt.session.SessionHelper.manager.getPrincipal().getName();
			// System.out.println("GET BASELINES USER IS:" + user);
			wt.session.SessionHelper.manager.setAdministrator();

			QuerySpec qs = new QuerySpec(ManagedBaseline.class);
			SearchCondition sc = new SearchCondition(ManagedBaseline.class,
					ManagedBaseline.NUMBER, SearchCondition.LIKE, "%"
							+ numberLike + "%", false);
			qs.appendSearchCondition(sc);
			// qs.appendAnd();

			// PDMLinkProduct wtProduct =
			// TAProduct.getPDMLinkProduct(containerName);
			// WTContainerRef wtcontainerref =
			// wtProduct.getContainerReference();
			// ObjectIdentifier objectidentifier =
			// ObjectIdentifier.newObjectIdentifier(wtcontainerref.getKey().toString());

			// SearchCondition sc2 = new
			// SearchCondition(ManagedBaseline.class, "containerReference.key",
			// "=", objectidentifier);
			// qs.appendWhere(sc2);

			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				ManagedBaseline baseline = (ManagedBaseline) qr.nextElement();
				list.add(baseline);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (!"".equals(user)) {
				try {
					wt.session.SessionHelper.manager.setPrincipal(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	/*
	public static ArrayList getBaselines(String name, String containerName){
		ArrayList list = new ArrayList();
		ArrayList aBaseline = getBaselines(name);
		for(int i=0; i<aBaseline.size(); i++){
			ManagedBaseline baseline = (ManagedBaseline)aBaseline.get(i);
			if(baseline.getContainerName().equals(containerName)){
				list.add(baseline);
			}
		}
		return list;
	}*/
	
	/**
	 * get baseline by a given baselineable object, 
	 * @param baselineable
	 * @param onlyNumber whether only return number or not.
	 * @return
	 */
	public static ArrayList getBaselines(Baselineable baselineable,
			boolean onlyNumber) {
		ArrayList list = new ArrayList();
		String user = "";
		try {
			user = wt.session.SessionHelper.manager.getPrincipal().getName();
			wt.session.SessionHelper.manager.setAdministrator();

			QueryResult qr = BaselineHelper.service.getBaselines(baselineable);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof ManagedBaseline) {
					ManagedBaseline baseline = (ManagedBaseline) obj;
					if (onlyNumber)
						list.add(baseline.getNumber());
					else
						list.add(baseline);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (!"".equals(user)) {
				try {
					wt.session.SessionHelper.manager.setPrincipal(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	/**
	 * This method is used to get items in a baseline
	 * @param baseline given baseline
	 * @return vector that contains baselineable items, such as part and document
	 */
	public static Vector getBaselineItems(ManagedBaseline baseline) {
		String user = "";
		Vector vBaselines = new Vector();
		try {
			user = wt.session.SessionHelper.manager.getPrincipal().getName();
			wt.session.SessionHelper.manager.setAdministrator();

			QueryResult qr = BaselineHelper.service.getBaselineItems(baseline);
			while (qr.hasMoreElements()) {
				vBaselines.add(qr.nextElement());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!"".equals(user)) {
				try {
					wt.session.SessionHelper.manager.setPrincipal(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return vBaselines;
	}
	
	/**
	 * Get baselines from a Persistable Item
	 * 
	 * @param ps
	 * @return
	 * @throws WTException
	 */
	public static QueryResult getBaseLineFromPersistable(Persistable ps) throws WTException {
		Class subjectClass = ps.getClass();
		long keyId = ps.getPersistInfo().getObjectIdentifier().getId();
		QuerySpec qs = new QuerySpec();
		QueryResult qr = new QueryResult();
		int index0 = qs.appendClassList(ManagedBaseline.class, true);
		int index1 = qs.appendClassList(BaselineMember.class, false);
		int index2 = qs.appendClassList(subjectClass, false);
		String[] aliases = new String[3];
		aliases[0] = qs.getFromClause().getAliasAt(index0);
		aliases[1] = qs.getFromClause().getAliasAt(index1);
		aliases[2] = qs.getFromClause().getAliasAt(index2);
		TableColumn tc0 = new TableColumn(aliases[0], "ida2a2"); // baseline
																	// key
		TableColumn tc1 = new TableColumn(aliases[1], "ida3a5"); // baseline
																	// number
																	// key for
																	// baseline
		qs.appendWhere(new SearchCondition(tc0, "=", tc1), new int[] { index0,
				index1 });
		qs.appendAnd();
		tc0 = new TableColumn(aliases[2], "ida2a2");
		tc1 = new TableColumn(aliases[1], "ida3b5"); // baseline number key
														// for member
		qs.appendWhere(new SearchCondition(tc0, "=", tc1), new int[] { index2,
				index1 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(subjectClass,
				WTAttributeNameIfc.ID_NAME, SearchCondition.EQUAL, keyId),
				new int[] { index2 });
//		System.out.println("qs    " + qs);
		qr = PersistenceHelper.manager.find((StatementSpec) qs);
		return qr;
	}
}
