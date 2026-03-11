/**
 * @(#)EnvelopeQueryCommands.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/19
 */
package ext.ases.envelope;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.change2.ChangeException2;
import wt.enterprise.RevisionControlled;
import wt.fc.BinaryLink;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.WTReference;
import wt.fc.collections.CollectionsHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTList;
import wt.folder.Folder;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.method.MethodLocal;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionReference;

import com.ptc.core.components.forms.FormDispatcher;
import com.ptc.core.components.util.PropagationHelper;
import com.ptc.core.components.util.converters.ComponentConvertUtils;
import com.ptc.core.meta.common.Identifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.core.ui.validation.UIComponentValidationHelper;
import com.ptc.core.ui.validation.UIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.model.NmSimpleOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.object.ActionDelegateHelper;
import com.ptc.windchill.enterprise.change2.ChangeManagementClientHelper;

public class EnvelopeQueryCommands implements RemoteAccess {
	 static Logger logger = LogR.getLogger(ext.ases.envelope.EnvelopeQueryCommands.class.getName());

    public EnvelopeQueryCommands() {
    }
    
    /**
     * Get the list of items to show in the related data table.
     *
     * <br/><br/><b>Supported API: </b>true
     *
     * @param cb
     *            The command bean for the current context
     *
     * @return The list of changeables to display in the table.
     */
    public static List<RevisionControlled> getAffectedData(NmCommandBean cb) {
        return getEnvelopeData(cb);
    }
    
    /**
     * Convenience method to wrap the logic for the "EnvelopeData" item tables.
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
        NmOid oid = cb.getActionOid();
        logger.debug("processing oid as a standard NmOid");
        if (mode != ComponentMode.CREATE || (mode == ComponentMode.CREATE && PropagationHelper.isPropagationSelected(cb))) {
            getMembers((ProcessEnvelope) oid.getRefObject(), rcs);
        }
        return rcs;
    }
    
    /**
     *
     * <br/><br/><b>Supported API: </b>false
     *
     * @param processEnvelope
     *            The process envelope to query on.
     * @param list
     *            The list of EnvelopeMembers to append to
     *
     */
    private static final void getMembers(ProcessEnvelope processEnvelope, List<RevisionControlled> list) throws ChangeException2, WTException {
        ArrayList arraylist = null;        
        arraylist = EnvelopeHelper.service.getAllMembers(processEnvelope);
        WTCollection collection = new WTArrayList();
        processLinks(arraylist, list);
    }
    
      /**
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     *
     * @param arraylist
     *            EnvelopeMembers
     * @param list
     *
     * @return The Map of ref & Persistable.
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
            }
            if (ref != null && !refLinkMap.containsKey(ref)) {
                refLinkMap.put(ref, p);
            }
        }
        return refLinkMap;
    }
}