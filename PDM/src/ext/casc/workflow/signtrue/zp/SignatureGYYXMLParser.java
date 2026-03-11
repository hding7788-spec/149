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

public class SignatureGYYXMLParser implements ISignatureParser{
    private Map<String,SignatureRecord> map = new HashMap<String,SignatureRecord>();
    private Map<String,SignatureRecord> map2 = new HashMap<String,SignatureRecord>();
    private Map<String,SignatureRecord> reassignMap2 = new HashMap<String,SignatureRecord>();
    private Map<String,String> map3 = new HashMap<String,String>();
    private Map<String,String> reassignMap3 = new HashMap<String,String>();
    private String version;
    
    public String getVersion() {
		return version;
	}
	public Map<String, SignatureRecord> getMap() {
        return map;
    }
    public void setMap(Map<String, SignatureRecord> map) {
        this.map = map;
    }
    private InputStream is = null;
    public SignatureGYYXMLParser(InputStream is){
        this.is = is;
        parse (); 
    }
    public SignatureGYYXMLParser(ContentHolder holder){
    	is = SignatureService.getAttachmentsFromPBO(holder,ContentRoleType.SECONDARY, "signature_emps2.xml");
        parse (); 
    }
    public boolean hasPrivilege(Persistable obj,WorkItem wi){
    	Object o = null;
		try {
			WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
			if(!wi.isReassigned()){
	    		o  = map2.get(PersistenceHelper.getObjectIdentifier((Persistable) obj).toString()+"_"+currentuser.getPersistInfo()
						.getObjectIdentifier().toString());
	    	}else{
	    		o  = map2.get(PersistenceHelper.getObjectIdentifier((Persistable) obj).toString()+"_"+SignatureService.getWorkItemOrginalOwnerShip(wi));
	    	}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return o!=null? true:false;
    }
    public String getUsersByObjectOid(String oid){
    	return map3.get(oid);
    }
    /**useroid 为当前登录用户 Oid去掉OR:后的字符串，如：wt.org.WTUser:112835
     * empoid  为EMP或者DOC的最新版本OID去扔掉VR：后的字符串，如：wt.epm.EPMDocument:385579
     * @param useroid
     * @param empoid
     * @return
     */
    public boolean hasPrivilege(String useroid,String empoid,WorkItem wi){
    	Object o = null;
    	if(!wi.isReassigned()){
    		o  = map2.get(empoid+"_"+useroid);
    	}else{
    		o  = map2.get(empoid+"_"+SignatureService.getWorkItemOrginalOwnerShip(wi));
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
	            String rootVer = root.valueOf("@version");
	            for (Iterator i = root.elementIterator( "EMPGYY" ); i.hasNext(); ) {
	                Element typeElement = (Element) i.next();
	                String oid = typeElement.valueOf("@oid");
	              //  String advise = typeElement.valueOf("@advise");
	              //  String result = typeElement.valueOf("@select");
	                String approver = typeElement.valueOf("@approver");
	                String zpr = typeElement.valueOf("@zpr");
	                String approverDisplay = typeElement.valueOf("@approverDisplay");
	                version = typeElement.valueOf("@version");
	                String reassign = typeElement.valueOf("@reassign");
	                SignatureRecord record = new SignatureRecord(oid, "", "", approver, approverDisplay,zpr,version,reassign);
	                map.put(oid+"_"+zpr, record);
	                if(map3.containsKey(oid)&&!"".equals(approver)){
	                	String useroid = map3.get(oid).toString();
	                	useroid = useroid+approver;
	                	map3.put(oid, useroid+";");
	                }else{ 
	                	map3.put(oid, approver+";");
	                }
	                String[] apps =  approver.split(";");
	                for(String app :apps){
	                    if(!"".equals(app)&&rootVer.equals(version))
	                        map2.put(oid+"_"+app.replaceAll("OR:", ""), record);
	                }
	                if(reassign!=null&&!"".equals(reassign)){
	                	if(reassignMap3.containsKey(oid)&&!"".equals(reassign)){
		                	String useroid = reassignMap3.get(oid).toString();
		                	useroid = useroid+reassign;
		                	reassignMap3.put(oid, useroid+";");
		                }else{ 
		                	map3.put(oid, reassign+";");
		                }
		                apps =  reassign.split(";");
		                for(String app :apps){
		                    if(!"".equals(app)&&rootVer.equals(version))
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
		if(!wi.isReassigned()){
    		o  = map2.get(PersistenceHelper.getObjectIdentifier((Persistable) obj).toString()+"_"+currentuser.getPersistInfo()
					.getObjectIdentifier().toString());
    	}else{
    		o  = map2.get(PersistenceHelper.getObjectIdentifier((Persistable) obj).toString()+"_"+SignatureService.getWorkItemOrginalOwnerShip(wi));
    	}
        return o!=null? true:false;
	}

	@Override
	public boolean hasKRPrivilege(Persistable obj) {
		return false;
	}
}
