package ext.casc.importdata;

import java.io.File;
import java.io.UnsupportedEncodingException;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.util.WTException;

import com.glaway.mpm.util.PropertiesUtil;

import ext.casc.importdata.ExportToDncProcessor;
import ext.casc.util.IBAUtility;

public class ExportToDNC {

	public ExportToDNC(WTObject pbo) throws Exception{
		WTDocument doc = null;
		try {
			if(pbo instanceof WTDocument){
				doc = (WTDocument) pbo;
			}
			if(pbo instanceof WTChangeOrder2){
				QueryResult qr = ChangeHelper2.service.getChangeablesAfter((WTChangeOrder2) pbo);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WTDocument) {
						doc = (WTDocument) obj;
					}
				}
			}
			IBAUtility iba = new IBAUtility(doc);
			String ppNumber = iba.getIBAValue("PPNUMBER");
			if(ppNumber == null || "".equals(ppNumber)){
				ppNumber = doc.getNumber();
			}
			ppNumber = ppNumber.substring(ppNumber.lastIndexOf("/") + 1,ppNumber.length());
			String phaseCode = iba.getIBAValue("PHASE_CODE");
			String version = doc.getVersionInfo().getIdentifier().getValue();
			String technicName = ppNumber + "_" + phaseCode + "_" + version;
			technicName = technicName.replace("/", "_");
			technicName = technicName.replace("*", "+");

			// 附件内容解压路径
			String attachPath = PropertiesUtil.getLocalCodeBase() + File.separator + "temp" + File.separator + "DNC" + File.separator + technicName;
			// 上传到ftp服务器哪个路径下
			String ftpPath = "/数据库基本目录/SEND/Dnc/机床/"+ppNumber;
			ftpPath = new String(ftpPath.getBytes("GBK"), "iso-8859-1");
			// 地址
			String addr = "10.125.237.29";
			// 端口号
			int port = 21;
			// 用户名
			String userName = "pdm";
			// 密码
			String password = "Aa00000000";
			ExportToDncProcessor.uploadDncToFTP(doc, attachPath, ftpPath, addr, port, userName, password);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
