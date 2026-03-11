package ext.casc.doc;

import com.glaway.mpm.util.PropertiesUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.List;
import java.util.Map;

public class AddAttachment4SKProcessor extends DefaultObjectFormProcessor {

    private static String tmp_dir = PropertiesUtil.getTempPath() + File.separator;
    static final int BUFFER = 2048;

    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> listBean) throws WTException {
        System.out.println("======================AddAttachment4SKProcessor start======================");
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        FeedbackMessage feedbackMessage = new FeedbackMessage();
        try {
            Object fileMap = nmCommandBean.getMap().get("fileUploadMap");
            String oid = nmCommandBean.getActionOid().getOid().toString();
            WTDocument document = (WTDocument) new ReferenceFactory().getReference(oid).getObject();
            String[] skFileNames = nmCommandBean.getTextParameterValues("skFile");

            if (fileMap != null && skFileNames != null) {
                Map<?, ?> map = (Map<?, ?>) fileMap;
                Object fileObj = map.get("skFile");
                File[] files;
                File file;
                if (fileObj instanceof File[]) {
                    files = (File[]) fileObj;
                    for(int i = 0; i < files.length; i++) {
                        file = files[i];
                        uploadFile(document,file,skFileNames[i]);
                    }
                }else if(fileObj instanceof File){
                    file = (File) fileObj;
                    uploadFile(document,file,skFileNames[0]);
                }

            }
        } catch (Exception e) {
            feedbackMessage.addMessage("添加数控程序失败");
            result.addFeedbackMessage(feedbackMessage);
            result.setStatus(FormProcessingStatus.FAILURE);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        System.out.println("======================AddAttachment4SKProcessor end======================");
        return result;
    }

    private void uploadFile(WTDocument document,File file,String fileName) throws PropertyVetoException, WTException, IOException {
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }
        compressFile(file, fileName);
        // 读取保存在本地的EXCEL文件
        File skFile = new File(tmp_dir + fileName);
        if (skFile.exists()) {
            ContentHolder holder = ContentHelper.service.getContents(document);
            ApplicationData data = ApplicationData.newApplicationData(holder);
            data.setFileName(skFile.getName());
            data.setUploadedFromPath(skFile.getAbsolutePath());
            data.setRole(ContentRoleType.SECONDARY);
            data.setFileSize(skFile.length());
            data.setDescription("数控程序");
            holder = (ContentHolder) PersistenceHelper.manager.refresh(holder);
            ContentServerHelper.service.updateContent(holder, data, skFile.getPath());
            // 删除临时文件
            if (skFile.exists()) {
                skFile.delete();
            }
        } else {
            System.out.println("file is not exist");
        }
    }

    /**
     * 处理上载的文件，将其先保存在服务器端指定路径，然后再对其进行解压。
     */
    public void compressFile(File zipFile, String fileName) {

        File tempFile = new File(tmp_dir + fileName);
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(zipFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(tempFile));

            // 缓冲数组
            byte[] b = new byte[BUFFER * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if (outBuff != null) {
                    outBuff.close();
                }
                if (inBuff != null) {
                    inBuff.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
