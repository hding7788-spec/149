package ext.casc.integrate.util;

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.util.*;

public class SearchErpDataHelper {
    public static List<Vector<Object>> getApprovedTechnics(HashMap<String, String> map, String pplanType) {
        List<Vector<Object>> list = new ArrayList<Vector<Object>>();
        try {
            WTPart part = (WTPart) Util.getObjectByOid(WTPart.class,map.get("oid"));
            List<Vector<Object>> tempList = getApprovedProcessZip(part, pplanType);
            if (tempList != null) {
                list.addAll(tempList);
            }

        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            }


        return list;
    }

    public static List<Vector<Object>> getApprovedProcessZip(WTPart part,String pplanType)
            throws WTRuntimeException, WTException, PropertyVetoException {
        List<Vector<Object>> list = new ArrayList<Vector<Object>>();
        List<WTDocument> docList= getDescribedDocumentByPart(part);
        WTUser currentUser = (WTUser) SessionHelper.manager.getPrincipal();
        for (WTDocument document : docList) {
            String state =document.getState().getState().toString();
            if("OBSOLESCENCE".equals(state)){
                continue;
            }
            ext.casc.util.IBAHelper helper = new ext.casc.util.IBAHelper();
            String PPLANTYPE = helper.getIBAStringValue(document, "PPLANTYPE");
            if(!pplanType.equals(PPLANTYPE)){
                continue;
            }
            String CLDEZT = helper.getIBAStringValue(document, "CLDEZT");

            if(document.getState().getState().toString().equals("APPROVED")||"已批准".equals(CLDEZT)){
               // collectAppDatas(isEditable, list, currentUser, document);
            }else if(!"space".equals(document.getVersionIdentifier().getValue())){
                //如果存在上一个版本，则取上一个受控版本
                QueryResult qr2 = VersionControlHelper.service.allVersionsOf(document.getMaster());
                int index = 0;
                while (qr2.hasMoreElements()) {
                    WTDocument docVersion = (WTDocument) qr2.nextElement();
                    if(index ==0){
                        index++;
                        continue;
                    }else{
                        CLDEZT = helper.getIBAStringValue(docVersion, "CLDEZT");
                        if (docVersion.getState().getState().toString().equals("APPROVED")||"已批准".equals(CLDEZT)) {
                           // collectAppDatas(isEditable, list, currentUser, docVersion);
                            break;
                        }
                        index ++;
                    }
                }

            }

        }
        return list;
    }

    public static List<WTDocument> getDescribedDocumentByPart(WTPart part) throws WTException {
        List<WTDocument> list = new ArrayList<WTDocument>();
        if (part != null) {
            QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qr = lcs.process(qr);
            while (qr.hasMoreElements()) {
                WTDocument document = (WTDocument) qr.nextElement();
                QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
                if (qr2.hasMoreElements()) {
                    document = (WTDocument) qr2.nextElement();
                    list.add(document);
                }

            }
        }
        return list;
    }
}
