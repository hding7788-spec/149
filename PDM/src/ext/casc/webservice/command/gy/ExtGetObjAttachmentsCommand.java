/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.analysisActivity.bean.AnalysisToSourceLink;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.change2.ChangeException2;
import wt.change2.WTAnalysisActivity;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * 类功能：根据类型和编号获取相应对象的附件zip包
 *
 * @author chenjianhui
 * @date 2024/08/05
 */
@Component
public class ExtGetObjAttachmentsCommand implements WebServiceCommand, InitializingBean {
    private static final Logger LOGGER = LogR.getLogger(ExtGetObjAttachmentsCommand.class.getName());
    // 方法标识
    public static final String METHOD_NAME = "getObjAttachments";

    private static String PARA_TYPE = "type";
    private static String PARA_NUMBER = "number";

    @Override
    public String execute(String params) {
        JSONObject rtnMsgObj = new JSONObject();
        String errorMsg = "";
        String fileName = "";
        JSONObject jparams = null;
        try {
            jparams = new JSONObject(params);
            LOGGER.info("getObjAttachments jparams=" + jparams);
        } catch(JSONException e) {
            errorMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
            LOGGER.error("", e);
        }
        if(StrUtil.isEmpty(errorMsg)) {
            try {
                String type = jparams.optString(PARA_TYPE);
                String number = jparams.optString(PARA_NUMBER);
                if("AnalysisActivity".equals(type)) {
                    WTAnalysisActivity activity = AnalysisUtil.getWTAnalysisActivityByNumber(number);
                    if(activity != null) {
                        Persistable persistable = null;
                        QueryResult qr = PersistenceHelper.manager.navigate(activity, "sourceObject", AnalysisToSourceLink.class, false);
                        if(qr.hasMoreElements()) {
                            AnalysisToSourceLink link = (AnalysisToSourceLink) qr.nextElement();
                            persistable = link.getSourceObject();
                        }
                        if(persistable != null) {
                            if(persistable instanceof ChangePackaged){
                                ChangePackaged packaged = (ChangePackaged) persistable;
                                fileName = getAttachments(packaged);
                            } else if(persistable instanceof ProcessEnvelope) {
                                ProcessEnvelope pe = (ProcessEnvelope) persistable;
                                ArrayList<WTObject> members = ProcessEnvelopeUtil.getAllMembers(pe);
                                for(WTObject obj : members) {
                                    if(obj instanceof WTDocument) {
                                        WTDocument document = (WTDocument) obj;
                                        TypeIdentifier identifier = TypedUtility.getTypeIdentifier(document);
                                        String typeName = identifier.getTypename();
                                        if(typeName.contains("casc.sast.149.TECHNOTICE_DOC")) {
                                            fileName = getAttachments(document);
                                            break;
                                        }
                                    }
                                }
                            }
                        }else {
                            fileName = getAttachments(activity);
                        }
                    } else {
                        errorMsg = "未查询到编号为" + number + "的影响分析单号！";
                    }
                }
            } catch(ChangeException2 e) {
                e.printStackTrace();
            } catch(WTException e) {
                e.printStackTrace();
            }

        }
        try {
            if(errorMsg != null && !"".equals(errorMsg)) {
                rtnMsgObj.put("status", "N");
                rtnMsgObj.put("result", errorMsg);
            } else {
                rtnMsgObj.put("status", "Y");
                rtnMsgObj.put("result", fileName);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return rtnMsgObj.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }

    public String getAttachments(ContentHolder holder) {
        try {
            SessionServerHelper.manager.setAccessEnforced(false);
            String path = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp" + File.separator + "attachments";
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".zip";
            String tempDir = path + File.separator + fileName;
            File zipFile = new File(tempDir);
            ZipOutputStream zos = new ZipOutputStream(zipFile);
            QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
            while(qr.hasMoreElements()){
                ApplicationData ad = (ApplicationData) qr.nextElement();
                String adFileName = ad.getFileName();
                if(adFileName.indexOf("秘密") > -1 || adFileName.indexOf("机密") > -1 || adFileName.endsWith(".xml")){
                    continue;
                }
                InputStream is = ContentServerHelper.service.findContentStream(ad);
                byte[] buf = new byte[1024];
                zos.putNextEntry(new ZipEntry(adFileName));
                zos.setEncoding("gbk");
                int len = 0;
                while((len = is.read(buf)) >= 0) {
                    zos.write(buf, 0, len);
                }
                is.close();
            }
            zos.close();
            return fileName;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(true);
        }
        return "";

    }


}
