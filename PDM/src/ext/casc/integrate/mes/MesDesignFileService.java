package ext.casc.integrate.mes;

import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.TechnicPreview;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.doc.CSCDoc;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MesDesignFileService {

    public ArrayList<String> getDesignFileUrl(String number, String version) {
        ArrayList<String> result = new ArrayList<String>();
        WTDocument doc = null;
        if (version == null || "".equals(version)) {
            doc = CSCDoc.getDoc(number);
        } else {
            doc = CSCDoc.getLatestDocByNumberAndVersion(number, version);
        }
        if (doc != null) {
            try {
                TechnicPreview technicPreview = new TechnicPreview();
                String url = technicPreview.preview(doc.toString());
                PropertiesUtil propertiesUtil = new PropertiesUtil();
                String httpCodeBase = propertiesUtil.getHttpCodeBase();

                String code;
                boolean flag = false;
                if(System.getProperty("os.name").contains("Windows")){
                    code = "UTF-8";
                    flag = true;
                }else{
                    code = "GBK";
                }
                String afterUrl = url.split("codebase")[1];
                afterUrl = URLEncoder.encode(afterUrl, code);
                String reglex = afterUrl.substring(0, afterUrl.indexOf("temp"));
                afterUrl = afterUrl.replace(reglex, "/");
                if(flag){
                    afterUrl = afterUrl.replace("+", "20%");
                    afterUrl = afterUrl.replace("20%", "%20");//零件中的空格
                }
                url = httpCodeBase + afterUrl;
                String resultUrl = "url=" + url;
                result.add(resultUrl);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            result.add("message=不存在编号为" + number + "的工艺文件");
            return result;
        }
        return result;
    }

    private String getDegignFileUrl(WTDocument wtDocument) {
        QueryResult primaryResult = null;
        String folderPath = "";
        try {
            primaryResult = ContentHelper.service.getContentsByRole(wtDocument, ContentRoleType.PRIMARY);
            while (primaryResult.hasMoreElements()) {
                ContentItem contentitem = (ContentItem) primaryResult.nextElement();
                if (contentitem instanceof ApplicationData) {
                    ApplicationData applicationdata = (ApplicationData) contentitem;
                    folderPath = ContentHelper.getDownloadURL(wtDocument, applicationdata, false).toString();
                }
            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return folderPath;
    }

    private String downloadDocumentPrimaryToTemp(WTDocument doc, String tempPath) throws WTException, PropertyVetoException {
        ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
        if (data == null) {
            return null;
        }
        InputStream is = ContentServerHelper.service.findContentStream(data);
        String appFileName = data.getFileName();
        String fileName = String.valueOf(System.currentTimeMillis()) + "." + appFileName.substring(appFileName.lastIndexOf(".") + 1);
        File file = new File(tempPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(tempPath + File.separatorChar + fileName));
            int i = 0;
            byte abyte[] = new byte[8192];
            while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
                fos.write(abyte, 0, i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileName;
    }
}
