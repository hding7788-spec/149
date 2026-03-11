package ext.casc.integrate.mes;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;


import ext.casc.doc.CSCDoc;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.util.WTException;

public class MesService {

	public ArrayList<String> getEcnFileUrl(String number,String version){
		ArrayList<String> result = new ArrayList<String>();
		WTDocument doc = null;
		if(version==null||"".equals(version)){
			doc = CSCDoc.getDoc(number);
		}else{
		    doc = CSCDoc.getLatestDocByNumberAndVersion(number,version);
		}
		String fileName = "";
		String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp";
    	File fileDir = new File(filePath);
    	if(!fileDir.exists()){
    		fileDir.mkdirs();
    	}
    	FileOutputStream fos = null;
		if(doc!=null){
			try {
				StringBuilder sb = new StringBuilder();
	            String  tempFilePath = filePath + File.separator;
				String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc, tempFilePath);
				WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
                Iterator it = coll.iterator();
                if (it.hasNext()) {
                	ContentHolder holder = ContentHelper.service.getContents(doc);
    				Vector apps = ContentHelper.getApplicationData(holder);
    				for (Enumeration e = apps.elements(); e.hasMoreElements();) {
    					ApplicationData contentItem = (ApplicationData) e.nextElement();
    					if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
    						continue;// 不是附件
    					if (contentItem.getFileName().startsWith("Print_")) {
    						byte[] bytes = WTDocumentUtil.applicationDataToByte(contentItem);
    						fileName = contentItem.getFileName();
//    						String trueFileName = String.valueOf(System.currentTimeMillis()) + ".pdf";
    						fos = new FileOutputStream(fileDir + File.separator + fileName);
    						fos.write(bytes);
    						fos.flush();
    					}
    				}
                    WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                    sb.append("number=");
                    sb.append(ecn.getNumber());
                    sb.append("|");
                    sb.append("name=");
                    sb.append(ecn.getName());
                    sb.append("|");
                    sb.append("fileName=");
                    sb.append(zipFileName);
                    sb.append("|");
                    sb.append("pdfName=");
                    sb.append(fileName);
//                    sb.append("|");
//                    sb.append("ecnPdfUrl=");
//                    sb.append(getPrintEcnFileUrl(ecn));
                    result.add(sb.toString());

                }else{
                	result.add("message=不存在编号为"+number+"的更改单");
                	return result;
                }
				return result;
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			result.add("message=不存在编号为"+number+"的工艺文件");
			return result;
		}
		return null;
	}

	private String getPrintEcnFileUrl(WTChangeOrder2 ecn) {
		 ContentHolder holder;
		try {
			holder = ContentHelper.service.getContents((ContentHolder) ecn);
			QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			 while (qr.hasMoreElements()) {
                 Object objQr = qr.nextElement();
                 if (objQr instanceof ApplicationData) {
                	 ApplicationData  ad = (ApplicationData) objQr;
                     String adName = ad.getFileName();
                     if(adName.startsWith("Print_"+ecn.getNumber())){
                    	 return ContentHelper.service.getDownloadURL(ecn, ad).toString();
                     }
                 }
             }
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

}
