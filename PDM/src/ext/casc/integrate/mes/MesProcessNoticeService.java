package ext.casc.integrate.mes;

import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.constants.Constants;
import ext.casc.doc.CSCDoc;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public class MesProcessNoticeService {

	public ArrayList<String> getProcessNotice(String number,String version){
		ArrayList<String> result = new ArrayList<String>();
		WTDocument doc = null;
		if(version==null||"".equals(version)){
			doc = CSCDoc.getDoc(number);
		}else{
		    doc = CSCDoc.getLatestDocByNumberAndVersion(number,version);
		}
		if(doc != null && !"APPROVED".equals(doc.getState().toString())){
			result.add("message=技术通知单(" + number +")未受控");
		}
//		String fileName = "";
		String trueFileName = "";
		String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp";
    	File fileDir = new File(filePath);
    	if(!fileDir.exists()){
    		fileDir.mkdirs();
    	}
    	FileOutputStream fos = null;
		if(doc!=null){
			try {
				String type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
				if(type.contains(Constants.PROCESS_NOTICE_DOCUMENT)){
					ContentHolder holder = ContentHelper.service.getContents(doc);
					Vector apps = ContentHelper.getApplicationData(holder);
					for (Enumeration e = apps.elements(); e.hasMoreElements();) {
						ApplicationData contentItem = (ApplicationData) e.nextElement();
//						String applicationdataRole = contentItem.getRole().toString();
						if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
							continue;// 不是附件
						if (contentItem.getFileName().startsWith("Print_")) {
							byte[] bytes = WTDocumentUtil.applicationDataToByte(contentItem);
//							fileName = contentItem.getFileName();
							trueFileName = String.valueOf(System.currentTimeMillis()) + ".pdf";
							fos = new FileOutputStream(fileDir + File.separator + trueFileName);
							fos.write(bytes);
							fos.flush();
						}
					}
					StringBuilder sb = new StringBuilder();
					sb.append("processNoticeNumber=");
					sb.append(doc.getNumber());
					sb.append("|");
					sb.append("processNoticeName=");
					sb.append(doc.getName());
					sb.append("|");
					sb.append("pdfName=");
					sb.append(trueFileName);
//					sb.append("|");
//					sb.append("fileUrl=");
//					sb.append(getProcessNoticeUrl(doc));
					result.add(sb.toString());
	            }else{
	            	result.add("message=不存在编号为"+number+"的工艺技术通知单");
	            }
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally{
				if(fos != null){
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			result.add("message=不存在编号为"+number+"的工艺技术通知单");
			return result;
		}
		return result;
	}

	private String getProcessNoticeUrl(WTDocument doc) {
		QueryResult qr = null;
		try {
			qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
                 Object objQr = qr.nextElement();
                 if (objQr instanceof ApplicationData) {
                	 ApplicationData  ad = (ApplicationData) objQr;
                     String adName = ad.getFileName();
                     if(adName.startsWith("Print_")){
                    	 return ContentHelper.service.getDownloadURL(doc, ad).toString();
                     }
                 }
             }
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
