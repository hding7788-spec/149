package ext.casc.product;

import cn.hutool.core.util.StrUtil;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmObjectHelper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.product.model.ApplicationDataBean;
import ext.casc.util.IBAHelper;
import ext.casc.util.WTUtil;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import wt.content.*;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectVectorIfc;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.folder.Cabinet;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.inf.container.WTContainerRef;
import wt.ixb.handlers.netmarkets.JSPFeedback;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTMessage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtNmObjectHelper {
    private static final String MORE_RESOURCE = "com.ptc.netmarkets.object.objectResource";
    public  URL downloadFolderContentFiles( NmCommandBean cb )
            throws WTException {
        HashMap map = cb.getMap();
        JSPFeedback jfb = (JSPFeedback)map.get("jfb");
        String primary = (String)map.get("primary");
        String signfile = (String)map.get("signfile");
        WTContainerRef containerRef = null;
        String userName = SessionHelper.getPrincipal().getName();
        if (cb.getSharedContextOid() != null)
            containerRef = cb.getSharedContextOid().getContainerRef();
        else
            containerRef = cb.getContainerRef();
        String errorMsg = "";

        File newZip = new File(WTUtil.WTTEMP + File.separator
                + userName + "_" + WTUtil.getDateString() + ".zip");
        boolean isPrimary= (new Boolean(primary)).booleanValue();
        boolean isSignfile = (new Boolean(signfile)).booleanValue();
        List<ApplicationDataBean> downloadADList = new ArrayList<ApplicationDataBean>();
        // Get the objects selected by the user
        ArrayList selectedList = cb.getSelectedInOpener();
        boolean hasError = false;
        List<String> alreadyAddList = new ArrayList<String>();
        Map<String, Integer> nameIndex = new HashMap<String, Integer>();

        InputStream is = null;
        ZipOutputStream zos =null;
        try {
            if (selectedList.size() > 0) {
                zos = new ZipOutputStream(newZip);
                zos.setEncoding("gb2312");
                byte[] buf = new byte[1024];
                for (Object singleObject : selectedList) {
                    if (singleObject instanceof NmContext) {
                        NmOid oid = ((NmContext) singleObject).getTargetOid();
                        Persistable selectObj = oid.getWtRef().getObject();
                        if (selectObj instanceof SubFolder) {
                            SubFolder subfolder = (SubFolder) selectObj;
                            getChildFolderContentsLists(
                                    subfolder,
                                    subfolder.getFolderPath().replaceAll(
                                            "/" + subfolder.getName(), ""),
                                    downloadADList,isPrimary,isSignfile);
                        } else if (selectObj instanceof Cabinet) {
                            Cabinet cab = (Cabinet) selectObj;
                            getCabinetContentsLists(cab, cab.getFolderPath(),
                                    downloadADList,isPrimary,isSignfile);
                        } else if (selectObj instanceof WTDocument) {
                            downloadADList.addAll(getContentsFromContentHolder(
                                    (WTDocument) selectObj, "",isPrimary,isSignfile));

                        } else if (selectObj instanceof EPMDocument) {
                            downloadADList.addAll(getContentsFromContentHolder(
                                    (EPMDocument) selectObj, "",isPrimary,isSignfile));
                        }
                    }
                }


                if (downloadADList.size() <= 0) {
                    hasError = true;
                }
                if (!hasError) {
                    for (ApplicationDataBean adBean : downloadADList) {
                        is = ContentServerHelper.service
                                .findContentStream(adBean.getAd());
                        if (is == null) {
                            continue;
                        }
                        // because the Application file name of EPMDocument's
                        // primary content is always {$CAD_NAME}, so we get the
                        // file name through epm.getCADName().
                       String  adName = adBean.getName();
                        int formatIndex = adName.lastIndexOf(".");
                        // in order to avoid omit some same file name, if the
                        // download list has, add _0 in the end of file name
                        // eg:file_1.ASM
                        if (alreadyAddList.contains(adName)) {
                            if (nameIndex.containsKey(adName)) {
                                Integer index = nameIndex.get(adName) != null ? nameIndex
                                        .get(adName) : 1;
                                nameIndex.put(adName, index + 1);
                                if (formatIndex > 0) {
                                    adName = adName.substring(0, formatIndex)
                                            + "_" + String.valueOf(index + 1)
                                            + adName.substring(formatIndex);
                                } else {
                                    adName += "_" + String.valueOf(index + 1);
                                }
                            } else {
                                nameIndex.put(adName, 1);
                                if (formatIndex > 0) {
                                    adName = adName.substring(0, formatIndex)
                                            + "_1"
                                            + adName.substring(formatIndex);
                                } else {
                                    adName += "_1";
                                }
                            }

                        }
                        alreadyAddList.add(adName);
                        /*
                         * add the folder path to keep folder structure in the
                         * zip file
                         */
                        zos.putNextEntry(new ZipEntry(adBean.getFolderPath()
                                + adName));
                        int len = 0;
                        while ((len = is.read(buf)) >= 0) {
                            zos.write(buf, 0, len);
                        }
                    }
                  /*  FeedbackMessage message = new FeedbackMessage(
                            FeedbackType.SUCCESS,null,null,null,"打包成功");
                    result.addFeedbackMessage(message);
                    result.setStatus(FormProcessingStatus.SUCCESS);
                    result.setJavascript("window.PTC.util.downloadUrl(\""
                            + url.toExternalForm() + "\");");
                    result.setNextAction(FormResultAction.JAVASCRIPT);*/
                } else {
                   /* FeedbackMessage message = new FeedbackMessage(
                            FeedbackType.FAILURE, null, null, null, errorMsg);
                    result.addFeedbackMessage(message);
                    result.setStatus(FormProcessingStatus.SUCCESS);*/
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return NmObjectHelper.constructOutputURL(newZip, newZip.getName());
        /*if (elemObj == null && cb.getPrimaryOid() !=null) elemObj = cb.getPrimaryOid().getRef();
        ObjectVectorIfc objVec = processSelectedObjectsForDocuments( elemObj, selected, subfolders );

        QueryResult qr = new QueryResult(objVec);

        if (qr.size() == 0) {
            // There are no documents to export.
            throw new NmException(MORE_RESOURCE, objectResource.EMPTY_JAR_FILE, null);
        } else {
            File f = exportContentFiles(qr, jfb, savepath, checkout, containerRef);
            return NmObjectHelper.constructOutputURL(f, DEFAULT_ARCHIVE_FILE_NAME);
        }*/
    }

    private List<ApplicationDataBean> getChildFolderContentsLists(
            SubFolder folder, String currentFolderPath,
            List<ApplicationDataBean> subFolderContent,boolean isPrimary,boolean isSignfile) throws WTException {
        // subFolderList.add(folder);
        QueryResult qr = FolderHelper.service.findFolderContents(folder);
        while (qr.hasMoreElements()) {
            Object object = qr.nextElement();
            if (object instanceof SubFolder) {
                getChildFolderContentsLists((SubFolder) object,
                        currentFolderPath, subFolderContent,isPrimary,isSignfile);
            } else if (object instanceof WTDocument) {
                subFolderContent.addAll(getContentsFromContentHolder(
                        (WTDocument) object,
                        getCurrentFolderPath(folder.getFolderPath(),
                                currentFolderPath),isPrimary,isSignfile));
            } else if (object instanceof EPMDocument) {
                subFolderContent.addAll(getContentsFromContentHolder(
                        (EPMDocument) object,
                        getCurrentFolderPath(folder.getFolderPath(),
                                currentFolderPath),isPrimary,isSignfile));
            }
        }
        return subFolderContent;
    }

    /**
     * get children folder lists
     *
     * @return List<SubFolder>
     * @throws WTException
     */
    private List<ApplicationDataBean> getCabinetContentsLists(Cabinet cab,
                                                              String currentFolderPath, List<ApplicationDataBean> subFolderContent, boolean isPrimary, boolean isSignfile)
            throws WTException {
        // subFolderList.add(folder);
        QueryResult qr = FolderHelper.service.findFolderContents(cab);
        while (qr.hasMoreElements()) {
            Object object = qr.nextElement();
            if (object instanceof SubFolder) {
                getChildFolderContentsLists((SubFolder) object,
                        currentFolderPath, subFolderContent,isPrimary,isSignfile);
            } else if (object instanceof WTDocument) {
                subFolderContent.addAll(getContentsFromContentHolder(
                        (WTDocument) object,
                        getCurrentFolderPath(cab.getFolderPath(),
                                currentFolderPath), isPrimary, isSignfile));
            } else if (object instanceof EPMDocument) {
                subFolderContent.addAll(getContentsFromContentHolder(
                        (EPMDocument) object,
                        getCurrentFolderPath(cab.getFolderPath(),
                                currentFolderPath), isPrimary, isSignfile));
            }
        }
        return subFolderContent;
    }


    private String getCurrentFolderPath(String fullFolderPath,
                                        String currentFolderPath) {
        String returnValue = "";
        if (!fullFolderPath.equals(currentFolderPath)) {
            returnValue = fullFolderPath.replace(currentFolderPath + "/", "");
            if (!returnValue.endsWith("/")) {
                returnValue += "/";
            }
        }
        return returnValue;
    }
    /**
     *
     * @param holder
     * @param folderPath
     * @return
     * @throws WTException
     */
    private List<ApplicationDataBean> getContentsFromContentHolder(
            ContentHolder holder, String folderPath,boolean isPrimary,boolean isSignfile) throws WTException {
        List<ApplicationDataBean> contentList = new ArrayList<ApplicationDataBean>();

        QueryResult secondaryqr = ContentHelper.service.getContentsByRole(
                holder, ContentRoleType.SECONDARY);
        if(isPrimary){
            QueryResult qr = ContentHelper.service.getContentsByRole(holder,
                    ContentRoleType.PRIMARY);
            while (qr.hasMoreElements()) {
                ApplicationData ad = (ApplicationData) qr.nextElement();
                ApplicationDataBean bean = new ApplicationDataBean();
                if (ad == null) {
                    continue;
                }
                bean.setAd(ad);
                if (holder instanceof EPMDocument) {
                    bean.setName(((EPMDocument) holder).getCADName());
                } else {
                    bean.setName(ad.getFileName());
                }
                bean.setFolderPath(folderPath);
                contentList.add(bean);
            }
        }
        if(isSignfile) {
            while (secondaryqr.hasMoreElements()) {
                ApplicationData ad = (ApplicationData) secondaryqr.nextElement();
                ApplicationDataBean bean = new ApplicationDataBean();
                if (ad == null) {
                    continue;
                }

                String fileName = ad.getFileName();
                if(fileName.startsWith("Print_")||fileName.contains("SIGNED")){
                    try {
                        fileName = URLDecoder.decode(fileName, "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                    }
                    bean.setAd(ad);
                    bean.setName(fileName);
                    if(holder instanceof WTDocument){
                        WTDocument doc = (WTDocument)holder;
                        String secret = IBAHelper.getIBAStringValue(doc, "SECRET");
                        if(StrUtil.isNotEmpty(secret)) {
                            secret = "（" +  secret + "）";
                        }else {
                            secret = "";
                        }
                        String typeName = TypedUtility.getTypeIdentifier(holder).getTypename();
                        if(typeName.contains("PROCESS_PLAN")){
                            String downloadName = "Print_" + doc.getName() + "_" + doc.getVersionIdentifier().getValue() + secret + ".pdf";
                            downloadName = downloadName.replaceAll("/", "_");
                            bean.setName(downloadName);
                        }
                    }
                    bean.setFolderPath(folderPath);
                    contentList.add(bean);
                }
            }
        }
        return contentList;
    }
}
