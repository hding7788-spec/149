package ext.ases.envelope;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.change2.ChangeException2;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.folder.Folder;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import com.ptc.core.components.util.PropagationHelper;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.model.NmSimpleOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.ChangeManagementClientHelper;

import ext.casc.part.PackagedPartHelper;
import ext.casc.util.IBAHelper;

public class SubEnAntecedencyQueryCommands2 implements RemoteAccess{
	 static Logger logger = LogR.getLogger(ext.ases.envelope.SubEnvelopeQueryCommands2.class.getName());
	 
	 public SubEnAntecedencyQueryCommands2(){
		 
	 }
	 
	 public static List<RevisionControlled> getAffectedData(NmCommandBean cb) {
	        return getEnvelopeData(cb);
	    }
	    
	    /**
	     * Convenience method to wrap the logic for the "Related" item tables.
	     *
	     * <br/><br/><b>Supported API: </b>false
	     *
	     * @param cb
	     *            The command bean for the current client
	     *
	     * @return The list of EnvelopeMembers to display in the table.
	     */
	    private static List<RevisionControlled> getEnvelopeData(NmCommandBean cb) {
	        ComponentMode mode = ChangeManagementClientHelper.getMode(cb);
	        if (logger.isDebugEnabled()) {
	            logger.debug("the mode is: " + mode);
	        }
	        List<RevisionControlled> rcs = new ArrayList<RevisionControlled>();
	        try {
	            if (cb != null && cb.getActionOid() != null) {
	                NmOid oid = cb.getActionOid();
	                if (!(oid instanceof NmSimpleOid)) {
	                    rcs = getEnvelopeMembers(cb, rcs, mode);
	                } 
	            }
	        } catch (WTException e) {
	            e.printStackTrace();
	        }
	        if (logger.isDebugEnabled()) {
	            logger.debug("returning " + rcs.size() + " items from the query.");
	        }
	        return rcs;
	    }
	    
	    /**
	     * Convenience method to wrap the logic for getting the EnvelopeMembers for the
	     * "RelatedData" item tables.
	     *
	     * <br/><br/><b>Supported API: </b>false
	     *
	     * @param cb
	     *            The command bean for the current client
	     * @param changeables
	     *            List of related data
	     * @param mode
	     *            The mode of the table CREATE, EDIT or VIEW.
	     *
	     * @return The list of EnvelopeMembers to display in the table.
	     */
	    private static List<RevisionControlled> getEnvelopeMembers(NmCommandBean cb, List<RevisionControlled> rcs, ComponentMode mode) throws WTException {
	        String[] oids = cb.getTextParameterValues("oid");
	        String objoid = "";
	    	for(String oid:oids){
	    		logger.debug("oid is:" + oid);
	    		objoid = oid;
	    	}
	    	NmOid oid1 = cb.getActionOid();
	    	logger.debug("oid1 is:" + oid1);
	    	if((oid1.isA(Folder.class) || oid1.isA(WTContainer.class))) {
	    		logger.debug("enter getFolderEnvelopeMembers");
	    		rcs =  getFolderEnvelopeMembers(cb);
	        }else{
	        	ReferenceFactory rf = new ReferenceFactory();
		    	WTObject wtobject = (WTObject)rf.getReference(objoid).getObject();
		    	logger.debug("wtobject is:" + wtobject);
		        if (mode == ComponentMode.CREATE || (mode == ComponentMode.CREATE && PropagationHelper.isPropagationSelected(cb))) {
		            getMembers(wtobject, rcs);
		        }
	        }
	        return rcs;
	    }
	    
	    private static List<RevisionControlled> getFolderEnvelopeMembers(NmCommandBean cb) throws WTException {
	        logger.debug("oid was a folder");
	        List<RevisionControlled> envelopeMembers = new ArrayList<RevisionControlled>();
	        String[] oids = cb.getTextParameterValues("wizardSelectedObjects");
	        if(oids!=null){
	        	for(String oid:oids){
		    		logger.debug("oid is:" + oid);
		    		oid = oid.substring(oid.lastIndexOf("$")+1,oid.length()-2);
		    		ReferenceFactory rf = new ReferenceFactory();
		    		WTObject wtobject = (WTObject)rf.getReference(oid).getObject();
		    		envelopeMembers.add((RevisionControlled)wtobject);
				}
	        }
	        
	        return envelopeMembers;
	    }
	    
	    private static final void getMembers(WTObject wtobject, List<RevisionControlled> list) throws ChangeException2, WTException {
	        ArrayList<RevisionControlled> arraylist = new ArrayList();
	        String objTemp = null;
	        
	        arraylist.add((RevisionControlled)wtobject);
	        
	    	if(wtobject instanceof WTPart){
	    		WTPart part = (WTPart)wtobject;
	    		String isPackagedPart = IBAHelper.getIBAStringValue(part, "SETMARK");
	    		try {
					List<RevisionControlled> docList = PackagedPartHelper.getPartReleatedDOC(part);
					if ((docList != null)&&(docList.size() > 0)){
						for (RevisionControlled obj:docList){
							LifeCycleManaged lfObject = (LifeCycleManaged) obj;
							objTemp = lfObject.getState().toString();
							if (objTemp.equals("INWORK")) {
								arraylist.add(obj);
							}	
						}		
					}
				} catch (RemoteException e) {
					throw new WTException(e);
				}
				
	    		if(isPackagedPart.equals("是")){
	    			try {
						List<RevisionControlled> bomList = PackagedPartHelper.getPartBOMData(part);
						if((bomList != null)&&(bomList.size()>0)){
							for (RevisionControlled obj:bomList){
								LifeCycleManaged lfObject = (LifeCycleManaged) obj;
								objTemp = lfObject.getState().toString();
								if (objTemp.equals("INWORK")) {
									arraylist.add(obj);
								}	
							}		
						}
					} catch (WTRuntimeException e) {
						throw new WTException(e);
					} catch (RemoteException e) {
						throw new WTException(e);
					}
	    		}
	    	}
	    	
	        processLinks(arraylist, list);
	    }
	    
	      /**
	     * Given a collection of binary links generates a map of binary links and
	     * the role A object references. If the given list of changeables is not
	     * null then the list is populated from the passed in collection of binary
	     * links.
	     *
	     * <BR>
	     * <BR>
	     * <B>Supported API: </B>false
	     *
	     * @param collection
	     *            binary links
	     * @param list
	     *
	     * @return The Map of binary link class attributes.
	     * @throws WTException
	     */
	    public static Map<WTReference, Persistable> processLinks(ArrayList arraylist, List<RevisionControlled> list) throws WTException {
	        Map<WTReference, Persistable> refLinkMap = new HashMap<WTReference, Persistable>();
	        Class<?> linkClass = null;
	        for(int i=0;i<arraylist.size();i++){
	            Persistable p = (Persistable)arraylist.get(i);
	            WTReference ref = null;
	            if(list != null && p instanceof RevisionControlled) {
	                list.add((RevisionControlled)p);
	                ref = ChangeManagementClientHelper.getReference(p);
//	                System.out.println("ref is: "+ref);
	            }

	            if (ref != null && !refLinkMap.containsKey(ref)) {
	                refLinkMap.put(ref, p);
	            }
	        }
	        return refLinkMap;
	    }
}
