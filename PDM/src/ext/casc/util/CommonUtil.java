package ext.casc.util;

import com.glaway.mpm.util.FileUtil;
import wt.content.ApplicationData;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.pdmlink.PDMLinkProduct;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.io.InputStream;

public class CommonUtil implements RemoteAccess {
	
	public static OrgContainer getOrgContainer() throws WTException {
		OrgContainer org = null;
        try {
            QuerySpec qs = new QuerySpec(OrgContainer.class);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements())
                org = (OrgContainer) qr.nextElement();
        } catch (WTException ex) {
            ex.printStackTrace();
        }
        return org;
    }
	 /**
	 * 判断是否是管理员账号
	 * */
	public static Boolean isSiteOrOrgAdmin() {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "isSiteOrOrgAdmin";
			try {
				return (Boolean)RemoteMethodServer.getDefault().invoke(method, CommonUtil.class.getName(), null, null, null);
			} catch (Exception e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
		}
		try{			
			WTPrincipal	principal=SessionHelper.getPrincipal();
			boolean check=false;
			WTContainerRef exchangeRef=WTContainerHelper.service.getExchangeRef();
			check=WTContainerHelper.service.isAdministrator(exchangeRef, principal);
			if (check)
				return Boolean.TRUE;
			WTOrganization org = OrganizationServicesHelper.manager.getOrganization(principal);
			if (org==null)
				return Boolean.FALSE;
			WTContainerRef orgContainerRef = WTContainerHelper.service.getOrgContainerRef(org);
			check=WTContainerHelper.service.isAdministrator(orgContainerRef, principal);
			return Boolean.valueOf(check);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return Boolean.FALSE;
	}
	
	public static boolean isProductManager(PDMLinkProduct prod)
	{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "isProductManager";
			try {				
				return (Boolean)RemoteMethodServer.getDefault().invoke(method, CommonUtil.class.getName(), 
						null, new Class[]{PDMLinkProduct.class}, new Object[]{prod});
			} catch (Exception e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
		}
		try {
			WTPrincipal p = SessionHelper.getPrincipal();
			return prod.getAdministrators().isMember(p);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Boolean.FALSE;
	}
	public static String objectToString(Object obj) {
		if (obj == null) {
			return "";
		} else if ("null".equals(obj)) {
			return "";
		} else {
			return obj.toString();
		}
	}
    /**
   	 * 把applicationData转化成字节数组
   	 *
   	 * @param data
   	 * @return
   	 * @throws WTException
   	 *
   	 */
   	public static byte[] applicationDataToByte(ApplicationData data) throws WTException {
   		InputStream inputStream = ContentServerHelper.service.findContentStream(data);
   		if(inputStream != null){
   			return FileUtil.fileToBytes(inputStream);
   		}
   		return new byte[0];
   	}
}
