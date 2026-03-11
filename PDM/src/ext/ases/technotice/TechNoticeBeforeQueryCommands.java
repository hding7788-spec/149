/**
 * @(#)TechNoticeBeforeQueryCommands.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/3/23
 */
package ext.ases.technotice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.change2.ChangeException2;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.util.WTException;

import com.ptc.core.components.util.PropagationHelper;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.model.NmSimpleOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.ChangeManagementClientHelper;

public class TechNoticeBeforeQueryCommands implements RemoteAccess {
    static Logger logger = LogR.getLogger(ext.ases.technotice.TechNoticeBeforeQueryCommands.class.getName());

    public TechNoticeBeforeQueryCommands() {
    }

    /**
     * Get the list of items to show in the related data table.
     * 
     * <br/>
     * <br/>
     * <b>Supported API: </b>true
     * 
     * @param cb
     *            The command bean for the current context
     * 
     * @return The list of changeables to display in the table.
     */
    public static List<RevisionControlled> getAffectedData(NmCommandBean cb) {
        return getBeforeData(cb);
    }

    /**
     * Convenience method to wrap the logic for the "EnvelopeData" item tables.
     * 
     * <br/>
     * <br/>
     * <b>Supported API: </b>false
     * 
     * @param cb
     *            The command bean for the current client
     * 
     * @return The list of EnvelopeMembers to display in the table.
     */
    private static List<RevisionControlled> getBeforeData(NmCommandBean cb) {
        ComponentMode mode = ChangeManagementClientHelper.getMode(cb);
        if (logger.isDebugEnabled()) {
            logger.debug("the mode is: " + mode);
        }
        List<RevisionControlled> rcs = new ArrayList<RevisionControlled>();
        try {
            if (cb != null && cb.getActionOid() != null) {
                NmOid oid = cb.getActionOid();
                if (!(oid instanceof NmSimpleOid)) {
                    rcs = getBeforeMembers(cb, rcs, mode);
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
     * <br/>
     * <br/>
     * <b>Supported API: </b>false
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
    private static List<RevisionControlled> getBeforeMembers(NmCommandBean cb, List<RevisionControlled> rcs,
            ComponentMode mode) throws WTException {
        NmOid oid = cb.getActionOid();
        logger.debug("processing oid as a standard NmOid");
        if (mode != ComponentMode.CREATE
                || (mode == ComponentMode.CREATE && PropagationHelper.isPropagationSelected(cb))) {
            getMembers((WTDocument) oid.getRefObject(), rcs);
        }
        return rcs;
    }

    /**
     * 
     * <br/>
     * <br/>
     * <b>Supported API: </b>false
     * 
     * @param processEnvelope
     *            The process envelope to query on.
     * @param list
     *            The list of EnvelopeMembers to append to
     * 
     */
    private static final void getMembers(WTDocument wtdoc, List<RevisionControlled> list) throws ChangeException2,
            WTException {
        ArrayList arraylist = null;
        arraylist = TechNoticeHelper.service.getTechNoticeBeforeMembers(wtdoc);
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
    public static Map<WTReference, Persistable> processLinks(ArrayList arraylist, List<RevisionControlled> list)
            throws WTException {
        Map<WTReference, Persistable> refLinkMap = new HashMap<WTReference, Persistable>();
        for (int i = 0; i < arraylist.size(); i++) {
            Persistable p = (Persistable) arraylist.get(i);
            WTReference ref = null;
            if (list != null && p instanceof RevisionControlled) {
                list.add((RevisionControlled) p);
                ref = ChangeManagementClientHelper.getReference(p);
            }
            if (ref != null && !refLinkMap.containsKey(ref)) {
                refLinkMap.put(ref, p);
            }
        }
        return refLinkMap;
    }
}