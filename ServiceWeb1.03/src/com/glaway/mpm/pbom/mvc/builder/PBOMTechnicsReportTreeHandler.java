package com.glaway.mpm.pbom.mvc.builder;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.dom4j.DocumentException;

import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;

import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import ext.casc.constants.Constants;
import ext.casc.integrate.util.BomUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

public class PBOMTechnicsReportTreeHandler extends TreeHandlerAdapter {

    private WTPart part;
    private WTContainer parentContainer = null;
    private String batch;

    public PBOMTechnicsReportTreeHandler(WTPart part, String batch) {
        this.part = part;
        this.parentContainer = part.getContainer();
        this.batch = batch;
    }

    @Override
    public Map<Object, List> getNodes(List parents) throws WTException {
    	List partList = new ArrayList();
    	for (Object object : parents) {
			if(object instanceof WTPart) {
				WTPart wtpart = (WTPart) object;
				if(batch != null && !"".equals(batch)){
					wtpart = getPartByBatch(wtpart, batch);
					if(wtpart == null){
						wtpart = (WTPart) object;
					}
				}
				partList.add(wtpart);
			}
		}

        Map<Object, List> result = new HashMap<Object, List>();
        Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(partList),
                getDefaultConfigSpec());
        WTContainer childContainer = null;
        for (ListIterator i = partList.listIterator(); i.hasNext();) {
            WTPart parent = (WTPart) i.next();
            List children = new ArrayList();

            IBAUtility ibaUtility = new IBAUtility(parent);
            String partType = ibaUtility.getIBAValue("MTYPE");

            //过滤不是自制件、外配套件、带料委外件、不带料委外件类型的零部件
            if (Constants.TYPE_ZIZHIJIAN.equals(partType)
            		||Constants.TYPE_WAIPEITAOJIAN.equals(partType)
            		||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
            		||Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)) {
            	//获取该零部件关联的工艺文档对象
				try {
					List<WTDocument> list = BomUtil.getAllWTDocumentAndReportProcessByAllSameVersionViewPart(parent);
					if(list != null && !list.isEmpty()) {
	            		children.addAll(list);
	            	}
				} catch (PropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	//List<WTDocument> list = getDescribedByWTDocument(parent);

            }

            result.put(parent, children);

//            if (!this.part.equals(parent)) {//新加的部件不需要带出子件
//                continue;
//            }
            Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }

            for (Persistable[] child : branch) {
                Persistable per = child[1];

                if(!(per instanceof WTPart) && !(per instanceof WTPartMaster)) {
                	continue;
                }
                WTPart childPart = null;
                if (per instanceof WTPart) {
                    childPart = (WTPart)per;
                    childPart = WCUtil.getLatestPartByView((Master)childPart.getMaster(), "Manufacturing");
                } else if(per instanceof WTPartMaster) {
                	childPart = WCUtil.getLatestPartByView((Master)per, "Manufacturing");
                }

                if (childPart == null){
                    continue;
                }

                IBAUtility ibaUtility1 = new IBAUtility(childPart);
                String partType1 = ibaUtility1.getIBAValue("MTYPE");
//addby caolei
                //过滤不是自制件、外配套件、带料委外件、不带料委外件类型的零部件
                if (Constants.TYPE_ZIZHIJIAN.equals(partType1)
                		||Constants.TYPE_WAIPEITAOJIAN.equals(partType1)
                		||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType1)
                		||Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType1)) {
                    childContainer = childPart.getContainer();
//addby caolei
                children.add(childPart);
            }
           }
        }
        return result;
    }

    @Override
    public List<Object> getRootNodes() throws WTException {
        List<Object> list = new ArrayList<Object>();
        if(batch != null && !"".equals(batch)){
        	WTPart wtpart = getPartByBatch(part, batch);
			if(wtpart != null){
				part = wtpart;
			}
		}
        list.add(this.part);
        return list;
    }

    protected ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

    public static List<WTDocument> getDescribedByWTDocument(WTPart part) throws WTException {
    	List<WTDocument> list = new ArrayList<WTDocument>();
    	QueryResult qr = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
    	while (qr.hasMoreElements()) {
    		WTDocument document = (WTDocument) qr.nextElement();
    		if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")) {
    			document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
    			list.add(document);
    		}
    	}
    	return list;
    }
    public static WTPart getPartByBatch(WTPart part, String batch){
    	WTPart wtpart = null;
    	try {
			QueryResult allIterations = VersionControlHelper.service.allVersionsFrom((Versioned) part);
			if (allIterations != null) {
				while (allIterations.hasMoreElements()) {
					wtpart = (WTPart) allIterations.nextElement();
					String partBatch = IBAHelper.getIBAValue(wtpart, "BATCH");
					if(partBatch == null){
						partBatch = "";
					}
					String state2 = wtpart.getState().toString();
					String v1 = VersionControlHelper.getVersionIdentifier((Versioned) wtpart).getValue();
					String v2 = VersionControlHelper.getIterationIdentifier((Iterated) wtpart).getValue();
					System.out.println(wtpart.getNumber() + "," + state2 + "," + v1 + "," + v2);
					if (partBatch.equals(batch)) {
						return wtpart;
					} else {
						wtpart = null;
					}
				}
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		} catch (VersionControlException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
    	return wtpart;
    }

}
