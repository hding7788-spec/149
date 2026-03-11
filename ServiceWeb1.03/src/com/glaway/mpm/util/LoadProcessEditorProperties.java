package com.glaway.mpm.util;

import com.glaway.mpm.model.ProcessEditorBean;

import java.io.File;

public class LoadProcessEditorProperties {

    private static LoadProcessEditorProperties instance = null;
    private String filePath = PropertiesUtil.getLocalCodeBase() + File.separator + "processEditor.properties";

    private LoadProcessEditorProperties() {

    }

    public synchronized static LoadProcessEditorProperties getInstance() {
        if (instance == null) {
            instance = new LoadProcessEditorProperties();
        }
        return instance;
    }

    public ProcessEditorBean checkUser(String userName) {
        ProcessEditorBean processEditorBean = null;
        File file = new File(filePath);
        if (file.exists()) {
            String value = PropertiesUtil.getValue(userName, filePath);
            if (value != null && !"".equals(value)) {
                String[] arrTemp = value.split(",");
                processEditorBean = new ProcessEditorBean(userName, arrTemp[0], arrTemp[1]);
            }
        }
        return processEditorBean;
    }

    public String getFilePath() {
        return filePath;
    }
}
