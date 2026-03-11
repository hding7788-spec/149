package com.glaway.mpm.mvc.builders.processEditor;

import com.glaway.mpm.model.ProcessEditorBean;
import com.glaway.mpm.util.LoadProcessEditorProperties;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.session.SessionHelper;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class ProcessEditorSaveProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
        FormResult formresult = super.doOperation(nmcommandbean, list);
        HttpServletRequest request = nmcommandbean.getRequest();
        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
        String[] userNames = null;
        String[] swts = null;
        String[] jwsRuntimeParameters = null;
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if (key.contains("_userName")) {
                userNames = (String[]) map.get(key);
            }
            if (key.contains("_swt") && !key.contains("old")) {
                swts = (String[]) map.get(key);
            }
            if (key.contains("_jwsRuntimeParameters") && !key.contains("old")) {
                jwsRuntimeParameters = (String[]) map.get(key);
            }
        }
        if (swts == null || swts.length == 0 || jwsRuntimeParameters == null || jwsRuntimeParameters.length == 0) {
            formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), "", null, "保存对象不存在！"));
            return formresult;
        }
        if (userNames != null) {
            for (int i = 0; i < userNames.length; i++) {
                if (("".equals(swts[i]) || null == swts[i]) || ("".equals(jwsRuntimeParameters[i]) || null == jwsRuntimeParameters[i])) {
                    formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), "", null, "存在位数或分配内存为空，保存失败！"));
                    return formresult;
                } else {
                    ProcessEditorBean processEditorBean = new ProcessEditorBean(userNames[i], swts[i], jwsRuntimeParameters[i]);
                    String filePath = LoadProcessEditorProperties.getInstance().getFilePath();
                    setPropertiesValue(filePath, processEditorBean);
                }
            }
        }
        formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), "", null, "保存成功！"));
        return formresult;
    }

    /**
     * 设置properties文件键值
     *
     * @param filepath
     * @param processEditorBean
     */
    private static void setPropertiesValue(String filepath, ProcessEditorBean processEditorBean) {
        FileOutputStream oFile = null;
        InputStream in = null;
        Properties properties;
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            properties = new Properties();
            in = new FileInputStream(file);
            properties.load(in);
            oFile = new FileOutputStream(filepath, Boolean.FALSE);
            //true表示追加打开
            properties.setProperty(processEditorBean.getUserName(), processEditorBean.getSwt().substring(0, processEditorBean.getSwt().lastIndexOf("位")) + "," + processEditorBean.getJwsRuntimeParameters().substring(0, processEditorBean.getJwsRuntimeParameters().lastIndexOf("M")));
            properties.store(oFile, "ProcessEditManager");
            oFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oFile != null) {
                try {
                    oFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

