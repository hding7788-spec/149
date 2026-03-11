package ext.casc.workflow.signtrue.zp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

public class SignatureGYZZXMLParser  implements ISignatureParser{
    private Map<String,SignatureRecord> map = new HashMap<String,SignatureRecord>();
    private Map<String,SignatureRecord> map2 = new HashMap<String,SignatureRecord>();
    private Map<String,SignatureRecord> map3 = new HashMap<String,SignatureRecord>();
    public Map<String, SignatureRecord> getMap3() {
		return map3;
	}
	public void setMap3(Map<String, SignatureRecord> map3) {
		this.map3 = map3;
	}
	private Map<String,SignatureRecord> reassignMap2 = new HashMap<String,SignatureRecord>();
    private String version;
    
    public Map<String, SignatureRecord> getMap() {
        return map;
    }
    public void setMap(Map<String, SignatureRecord> map) {
        this.map = map;
    }
    private InputStream is = null;
    public SignatureGYZZXMLParser(InputStream is){
        this.is = is;
        parse ();
    }
    public SignatureGYZZXMLParser(ContentHolder holder){
    	is = SignatureService.getAttachmentsFromPBO(holder,ContentRoleType.SECONDARY, "signature_emps.xml");
        parse (); 
    }
    public boolean hasPrivilege(String useroid,String empoid,WorkItem wi){
    	Object o = null;
    	if(!wi.isReassigned()){
    		o  = map2.get(empoid+"_"+useroid);
    	}else{
    		o  = map2.get(empoid+"_"+SignatureService.getWorkItemOrginalOwnerShip(wi));
    	}
        return o!=null? true:false;
    }
    public boolean hasPrivilege(Persistable obj,WorkItem wi){
    	Object o = null;
		try {
			WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
			String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
			if(!wi.isReassigned()){
				String uoid = currentuser.getPersistInfo().getObjectIdentifier().toString();
	    		o  = map2.get(oid+"_"+uoid);
	    	}else{
	    		o  = map2.get(oid+"_"+SignatureService.getWorkItemOrginalOwnerShip(wi));
	    	}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return o!=null? true:false;
    }
    public SignatureRecord getSignatureRecordByEPMOid(String oid ){
        return map.get(oid);
    }
    public void parse (){
        try { 
        	if(is!=null){
	            SAXReader reader = new SAXReader();
	            Document document =  reader.read(is);
	            Element root = document.getRootElement();
	            version = root.valueOf("@version");
	            for (Iterator i = root.elementIterator( "EMPHQZZ" ); i.hasNext(); ) {
	                Element typeElement = (Element) i.next();
	                String oid = typeElement.valueOf("@oid");
	               // String advise = typeElement.valueOf("@advise");
	                //String result = typeElement.valueOf("@select");
	                String approver = typeElement.valueOf("@approver");
	                String zpr = typeElement.valueOf("@zpr");
	                String approverDisplay = typeElement.valueOf("@approverDisplay");
	                String version = typeElement.valueOf("@version");
	                String reassign = typeElement.valueOf("@reassign");
	                String zhuzhichejian = typeElement.valueOf("@zhuzhichejian");
	                String fuzhichejian = typeElement.valueOf("@fuzhichejian");
	                SignatureRecord record = new SignatureRecord(oid, "", "", approver, approverDisplay,zpr,version,reassign,zhuzhichejian,fuzhichejian);
	                map.put(oid+"_"+zpr, record);
	                map3.put(oid, record);
	                String[] apps =  approver.split(";");
	                for(String app :apps){
	                    if(!"".equals(app))
	                        map2.put(oid+"_"+app.replaceAll("OR:", ""), record);
	                }
	                if(reassign!=null&&!"".equals(reassign)){
	                	apps =  reassign.split(";");
		                for(String app :apps){
		                    if(!"".equals(app))
		                    	reassignMap2.put(oid+"_"+app.replaceAll("OR:", ""), record);
		                }
	                }
	                
	            }
        	}
        }  catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
	@Override
	public boolean hasPrivilege(Persistable obj, WTUser currentuser, WorkItem wi) {
		Object o = null;
		if(currentuser==null){
		    try {
                currentuser = (WTUser)SessionHelper.manager.getPrincipal();
            } catch (WTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
		if(!wi.isReassigned()){
			String uoid = currentuser.getPersistInfo().getObjectIdentifier().toString();
    		o  = map2.get(oid+"_"+uoid);
    	}else{
    		o  = map2.get(oid+"_"+SignatureService.getWorkItemOrginalOwnerShip(wi));
    	}
        return o!=null? true:false;
	}

	@Override
	public boolean hasKRPrivilege(Persistable obj) {
		String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
		SignatureRecord record = map3.get(oid);
		if(record == null)  return false;
		if(record.getZhuzhichejian().contains("研")||record.getFuzhichejian().contains("研")||record.getZhuzhichejian().contains("科瑞")||record.getFuzhichejian().contains("科瑞")){
			return true;
		}
		return false;
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}
