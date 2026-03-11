package ext.casc.integrate.pfmea;

import com.glaway.mpm.util.*;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.constants.Constants;
import ext.casc.util.DBConn;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.fc.collections.WTCollection;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartReferenceLink;
import wt.pdmlink.PDMLinkProduct;
import wt.projmgmt.admin.Project2;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfProcess;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PfmeaProcessService {



    /**
     * 创建质量报告
     * @return msg
     */
    public String createQualityReport(String bomid, String reportFile,String fileName,String userName, String isrepeat){
        try {
            fileName = URLDecoder.decode(fileName,"UTF-8");
            userName = URLDecoder.decode(userName,"UTF-8");
            String[] userNameStrs = userName.split("\\$");
            String userN = null;
            if(userNameStrs != null && userNameStrs.length == 2){
                userN = userNameStrs[0];
//                WTUser wtUser = UserUtil.getWTUserByName(userN);
//                if(wtUser != null){
//                    SessionHelper.manager.setPrincipal(wtUser.getAuthenticationName());
//                }else{
//                    return "ERROR-NOUSER";
//                }
            }
            URL url = new URL(reportFile);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] buffer = readInputStream(inputStream);
            //
            /*String file64 = encodeBase64File(PropertiesUtil.getTempPath() + File.separator + "打印台账导入.xls");
            fileName = "打印台账导入.xls";
            byte[] buffer = new BASE64Decoder().decodeBuffer(file64);*/
            //
            WTPart wtPart = WTPartUtil.getPartByOid(Long.valueOf(bomid));
            if(wtPart != null){
                WTDocument lastWTDocumentByName = WTDocumentUtil.getLastWTDocumentByName(fileName);
                if(lastWTDocumentByName != null){
                    if("true".equals(isrepeat)){
                        lastWTDocumentByName = (WTDocument) WorkInProcessUtil.checkout(lastWTDocumentByName);
                        WTDocumentUtil.setPrimaryForDocument(lastWTDocumentByName, fileName, buffer);
                        lastWTDocumentByName = (WTDocument) WorkInProcessUtil.checkin(lastWTDocumentByName);
                        WTUser wtUser = UserUtil.getWTUserByName(userN);
                        if(wtUser!=null){
                           setModifier(lastWTDocumentByName, PersistenceHelper.getObjectIdentifier(wtUser).getId());
                        }
                    }else{
                        return "ERROR-REPEAT";
                    }
                }else{
                    WTContainer wtContainer = wtPart.getContainer();
                    WTDocument document = WTDocumentUtil.createDocument(null, fileName, wtContainer, "Default/PFMEA分析报告", "casc.sast.149.PFMEAREPORT");
                    WTDocumentUtil.setPrimaryForDocument(document, fileName, buffer);
                    document = (WTDocument) PersistenceHelper.manager.refresh(document);
                    ApplicationData app = WTDocumentUtil.getPrimaryByDocument(document);
//                    HashMap<String, WTDocument> data1 = new HashMap<String, WTDocument>();
                    if (userN != null && !userN.isEmpty()) {
                        WTUser wtUser = UserUtil.getWTUserByName(userN);
                        if (wtUser != null) {
                            setModifier(document, PersistenceHelper.getObjectIdentifier(wtUser).getId());
                        }
                    }
                    WTPartReferenceLink wtpartreferencelink = WTPartReferenceLink.newWTPartReferenceLink(wtPart, (WTDocumentMaster) document.getMaster());
                    PersistenceServerHelper.manager.insert(wtpartreferencelink);
                }
//                initiateWfProcess("分析报告签审流程", data1, wtContainer, document);
            }else{
                return "不存在Oid为:" + bomid + "的部件";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "SUCCESS";
    }
    public static void setModifier(Iterated obj, long userid)
			throws  Exception{
		DBConn conn = null;
		try {

			if(obj instanceof WTDocument){
				conn = new DBConn();
				long objid = PersistenceHelper.getObjectIdentifier(obj).getId();
				String sql = "";
				sql = "update WTDocument set idA3B2iterationInfo="+userid+" where ida2a2="+objid;
				conn.executeUpdate(sql);
				conn.commit();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


    /**
     * 将base64字符解码保存文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code, String targetPath,String catalogue)
            throws Exception {
        File file = new File(catalogue);
        if(file.exists()==false){
            file.mkdirs();
        }
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * 将文件转成base64 字符串
     *
     * @param path文件路径
     * @return *
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }

    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainer container,
                                              WTObject persistable) throws Exception {
        return initiateWfProcess(templateName, data, WTContainerRef.newWTContainerRef(container), persistable);
    }

    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainerRef conref,
                                              WTObject persistable) throws Exception {
        try {
            wt.workflow.definer.WfProcessDefinition wfProcessDef = wt.workflow.definer.WfDefinerHelper.service
                    .getProcessDefinition(templateName, conref);
            if (wfProcessDef == null) {
                System.out.println("the woflowtemplate named " + templateName + "doesn't exist");
                return null;
            }
            WTContainer container = conref.getReferencedContainer();
            wt.inf.team.ContainerTeam team = null;
            if (container instanceof PDMLinkProduct) {
                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((PDMLinkProduct) container);
            } else if (container instanceof WTLibrary) {
                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((WTLibrary) container);
            } else if (container instanceof Project2) {
                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((Project2) container);
            }
            wt.workflow.engine.WfProcess wfProcess = wt.workflow.engine.WfEngineHelper.service.createProcess(
                    wfProcessDef, team, conref);
            wfProcess.setName(templateName + "_" + System.currentTimeMillis());
            String user = wt.auth.Authentication.getUserName();
            wt.org.WTUser wtuser = wt.org.OrganizationServicesHelper.manager.getAuthenticatedUser(user);
            wt.org.WTPrincipalReference ref = wt.org.WTPrincipalReference.newWTPrincipalReference(wtuser);
            wfProcess.setCreator(ref);
            wfProcess = WfEngineServerHelper.service.setPrimaryBusinessObject(wfProcess, persistable);
            wt.workflow.engine.ProcessData pData = wfProcess.getContext();
            Iterator keys = data.keySet().iterator();
            while (keys.hasNext()) {
                String paramName = (String) keys.next();
                Object paramValue = data.get(paramName);
                pData.setValue(paramName, paramValue);
            }
            wfProcess = wfProcess.start(pData, 0, true);
            return wfProcess;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

}
