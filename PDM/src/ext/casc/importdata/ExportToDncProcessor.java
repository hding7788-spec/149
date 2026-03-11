package ext.casc.importdata;

import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.XmlUtility;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.util.IBAUtility;
import org.dom4j.Element;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExportToDncProcessor extends DefaultObjectFormProcessor{

	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
        	System.out.println("--------------ExportToDNC----------START----------");
            if (actionObj instanceof WTDocument) {
                WTDocument doc = (WTDocument) actionObj;
                IBAUtility iba = new IBAUtility(doc);
                String ppNumber = iba.getIBAValue("PPNUMBER");
				if(ppNumber == null || "".equals(ppNumber)){
					ppNumber = doc.getNumber();
				}
                ppNumber = ppNumber.substring(ppNumber.indexOf("/") + 1,ppNumber.length());
                String phaseCode = iba.getIBAValue("PHASE_CODE");
				String version = doc.getVersionInfo().getIdentifier().getValue();
				String technicName;
				if(phaseCode == null || "".equals(phaseCode)){
					technicName = ppNumber +"_"+ version;
				}else {
					technicName = ppNumber +"_"+ phaseCode +"_"+ version;
				}
                technicName = technicName.replace("/", "_");
                technicName = technicName.replace("*", "+");
                // 附件内容解压路径
    			String attachPath = PropertiesUtil.getLocalCodeBase() + File.separator + "temp" + File.separator + "DNC" + File.separator + technicName;
    			// 上传到ftp服务器哪个路径下
    			String ftpPath = "/"+ppNumber;
    			// 地址
    			String addr = "10.125.237.29";
    			// 端口号
    			int port = 21;
    			// 用户名
    			String userName = "pdmftp";
    			// 密码
    			String password = "Aa00000000";
                uploadDncToFTP(doc, attachPath, ftpPath, addr, port, userName, password);
                System.out.println("----------------ExportToDNC----------END-----------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage("操作成功");
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
	}

	/**
	 * 上传以NC_开头，格式为zip的附件至ftp
	 *
	 * @param document   文档对象
	 * @param attachPath 附件内容解压路径
	 * @param ftpPath    上传到ftp服务器哪个路径下
	 * @param addr       地址
	 * @param port       端口号
	 * @param userName   用户名
	 * @param password   密码
	 * @throws Exception
	 */
	public static void uploadDncToFTP(WTDocument document, String attachPath, String ftpPath, String addr, int port, String userName, String password) throws Exception {
		if(document != null) {
			try {
				FilesUtil.delAllFile(attachPath);
				File path = new File(attachPath);
				if(!path.exists()){
					path.mkdirs();
				}
				QueryResult qs = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
				while(qs.hasMoreElements()) {
					ApplicationData data = (ApplicationData) qs.nextElement();
					if("数控程序".equals(data.getDescription())){
						String fileName = data.getFileName();
						String filePath = attachPath + File.separatorChar + fileName;
						InputStream is = ContentServerHelper.service.findContentStream(data);
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(filePath);
							int i = 0;
							byte abyte[] = new byte[8192];
							while((i = is.read(abyte, 0, abyte.length)) >= 0) {
								fos.write(abyte, 0, i);
							}
						} catch(FileNotFoundException e) {
							e.printStackTrace();
						} catch(IOException e) {
							e.printStackTrace();
						} finally {
							try {
								if(null != is) {
									is.close();
								}
								if(null != fos) {
									fos.close();
								}
							} catch(IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				uploadDNCAttach(attachPath, ftpPath, addr, port, userName, password);
				FilesUtil.delAllFile(attachPath);
			} catch(WTException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> getAllDNCAttachPath(Element technicElement, String filePath) {
		List<String> attachPathList = new ArrayList<String>();
		List<Element> procedureList = XmlUtility.getAllSteps(technicElement);
		for (Element procedureElement : procedureList) {
			Element attachElement = XmlUtility.getAttachs(procedureElement);
			List<Element> pattachInfoList = attachElement.elements("PAttachInfo");
			for(Element pattachInfo : pattachInfoList){
				String attachPath = pattachInfo.attributeValue("absolutePath");
				String fileName = attachPath.substring(attachPath.lastIndexOf("/") + 1, attachPath.lastIndexOf("."));
				String geshi = attachPath.substring(attachPath.lastIndexOf(".") + 1, attachPath.length());
				if (fileName.startsWith("NC_") && (geshi.equals("zip") || geshi.equals("rar"))) {
					attachPathList.add(filePath + File.separator + attachPath);
				}
			}
		}
		return attachPathList;

	}

	public static void uploadDNCAttach(String attachPath, String ftpPath, String addr, int port, String userName, String password) {
		// 上传附件内容至ftp
		ext.casc.util.FTP ftp = null;
		try {
			ftp = new ext.casc.util.FTP();
			ftp.connect(ftpPath, addr, port, userName, password);
			File file = new File(attachPath);
			if(file.isDirectory()){
				File[] files = file.listFiles();
				for(File f : files){
					ftp.upload(f);

				}
			}
			System.out.println("上传成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
