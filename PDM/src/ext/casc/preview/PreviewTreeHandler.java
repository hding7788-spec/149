/**
 * @(#)EnvelopeTreeHandler.java
 *
 *
 * @author liaojun
 * @version 1.00 2010/1/18
 */
package ext.casc.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.util.WTException;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;


public class PreviewTreeHandler extends TreeHandlerAdapter {

    public PreviewTreeHandler() {
    }


    public static List topList= new ArrayList();
    public static List memberList = new ArrayList();

   /**
    * Get the root node from the command bean,
    * and get a config spec based on the root node
   **/
   public List getRootNodes() throws WTException {

      // TODO Auto-generated method stub
        List result = new ArrayList();
        NmCommandBean nmcommandbean = getModelContext().getNmCommandBean();
        if(nmcommandbean == null) {
            return null;
        }
        NmOid nmoid = nmcommandbean.getPageOid();
        if(nmoid == null) {
            return null;
        } else {
            Object obj = nmoid.getRef();
            if (obj instanceof Preview) {
                 memberList = PreviewUtil.getAllMembers((Preview)obj);
                 result.addAll(memberList);
            }
        }
        return result;
   }

   /**
    * Get the child parents for the given list of parent parts
   **/
   public Map<Object,List> getNodes(List parents) throws WTException {
      Map<Object,List> result = new HashMap<Object,List>();
      return result;
   }



}