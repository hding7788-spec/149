package ext.casc.processPlan.processor;

import com.glaway.mpm.util.ReferenceFactory;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.importdata.productRB;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessKnowledge;
import ext.casc.mpm.process.GLProcessParamDefinition;
import ext.casc.mpm.process.GLProcessParamValues;
import ext.casc.mpm.process.GLProcessParams;
import ext.casc.util.*;
import ext.casc.version.VersionCommonHelper;
import ext.sast.common.fc.CmPersistable;
import ext.sast.common.fc.CmPersistenceHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.httpgw.URLFactory;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProcessParamsCommand implements RemoteAccess, Serializable {

    private static final long serialVersionUID = 1L;

    public ProcessParamsCommand() {}

    public static FormResult addProcessParam(NmCommandBean nmcommandbean) throws WTException {
        FormResult formresult = new FormResult(FormProcessingStatus.SUCCESS);
        formresult.setNextAction(FormResultAction.REFRESH_OPENER);
        String gyName = nmcommandbean.getTextParameter("gyName");
        String parameterCategory = nmcommandbean.getTextParameter("parameterCategory");
        String unit = nmcommandbean.getTextParameter("unit");
        String processCategory = nmcommandbean.getTextParameter("processCategory");
        String enumValues = nmcommandbean.getTextParameter("enumValues");
        String knowledgeInferencePara = nmcommandbean.getTextParameter("knowledgeInferencePara");
        String knowledgeOutputPara = nmcommandbean.getTextParameter("knowledgeOutputPara");
        String outputRules = nmcommandbean.getTextParameter("outputRules");
        String knowledgeType = nmcommandbean.getTextParameter("knowledgeType");
        String isCanZhuang = nmcommandbean.getTextParameter("isCanZhuang");
        String isOnlyValue = nmcommandbean.getTextParameter("isOnlyValue");
        String source = nmcommandbean.getTextParameter("source");
        if(Tools.isTrimNull(source)){
            source = "设计";
        }
        if("基础参数".equals(parameterCategory)){
            knowledgeType = "";
        }
        if(!"配套表".equals(knowledgeType)){
            isCanZhuang = "";
        }
        if (!Tools.isNull(gyName)) {
            try {
                String id = GenSequeneUtil.genSeqNumber("GYPARAMS_SEQ",new DecimalFormat("000000000000"));
                GLProcessParams processParams = new GLProcessParams();
                processParams.setKeyId(id+"_"+gyName);
                processParams.setGyNumber(id);
                processParams.setGyName(gyName);
                processParams.setParameterCategory(parameterCategory);
                processParams.setUnit(unit);
                processParams.setProcessCategory(processCategory);
                if(!Tools.isTrimNull(enumValues)){
                    processParams.setEnumValues(enumValues);
                }
                if(!Tools.isTrimNull(knowledgeInferencePara)){
                    processParams.setKnowledgeInferencePara(knowledgeInferencePara);
                }
                if(!Tools.isTrimNull(knowledgeOutputPara)){
                    processParams.setKnowledgeOutputPara(knowledgeOutputPara);
                }
                if(!Tools.isTrimNull(knowledgeType)){
                    processParams.setKnowledgeType(knowledgeType);
                    processParams.setIsCanZhuang(isCanZhuang);
                }

                processParams.setSource(source);
                processParams.setOutputRules(outputRules);
                processParams.setIsOnlyValue(isOnlyValue);
                CmPersistenceHelper.manager.save(processParams);
            } catch (Exception e) {
               e.printStackTrace();
            }

        } else {
            formresult.setStatus(FormProcessingStatus.FAILURE);
            return formresult;
        }
        return formresult;
    }

    public static FormResult deleteProcessParam(NmCommandBean commandbean) throws WTException {
        FormResult form = new FormResult();
        List selected = commandbean.getSelected();
        for (Object obj : selected) {
            NmContext nmContext = (NmContext)obj;
            String oid = nmContext.getTargetOid().toString();
            DBUtil.deleteByTableAndKey("GLProcessParams","gyNumber",oid);
        }
        form.setStatus(FormProcessingStatus.SUCCESS);
        form.setNextAction(FormResultAction.NONE);
        form.setNextAction(FormResultAction.REFRESH_OPENER);
        return form;
    }

    public static FormResult editProcessParam(NmCommandBean nmcommandbean) throws WTException {
        FormResult formresult = new FormResult(FormProcessingStatus.SUCCESS);
        formresult.setNextAction(FormResultAction.REFRESH_OPENER);
        String gyName = nmcommandbean.getTextParameter("gyName");
        String gyNumber = nmcommandbean.getTextParameter("gyNumber");
        String parameterCategory = nmcommandbean.getTextParameter("parameterCategory");
        String unit = nmcommandbean.getTextParameter("unit");
        String processCategory = nmcommandbean.getTextParameter("processCategory");
        String enumValues = nmcommandbean.getTextParameter("enumValues");
        String knowledgeInferencePara = nmcommandbean.getTextParameter("knowledgeInferencePara");
        String knowledgeOutputPara = nmcommandbean.getTextParameter("knowledgeOutputPara");
        String outputRules = nmcommandbean.getTextParameter("outputRules");
        String knowledgeType = nmcommandbean.getTextParameter("knowledgeType");
        String isCanZhuang = nmcommandbean.getTextParameter("isCanZhuang");
        String isOnlyValue = nmcommandbean.getTextParameter("isOnlyValue");
        String source = nmcommandbean.getTextParameter("source");
        if(Tools.isTrimNull(source)){
            source = "设计";
        }
        if("基础参数".equals(parameterCategory)){
            knowledgeType = "";
        }
        if (!Tools.isNull(gyName)&&!Tools.isNull(gyNumber)) {
            try {
                GLProcessParams processParams = GyCsServerHelper.getProcessParamDefinition(gyNumber,false);
                processParams.setGyName(gyName);
                processParams.setParameterCategory(parameterCategory);
                processParams.setUnit(unit);
                processParams.setProcessCategory(processCategory);
                if(!Tools.isTrimNull(enumValues)){
                    processParams.setEnumValues(enumValues);
                }
                if(!Tools.isTrimNull(knowledgeInferencePara)){
                    processParams.setKnowledgeInferencePara(knowledgeInferencePara);
                }
                if(!Tools.isTrimNull(knowledgeOutputPara)){
                    processParams.setKnowledgeOutputPara(knowledgeOutputPara);
                }
                if(!Tools.isTrimNull(knowledgeType)){
                    processParams.setKnowledgeType(knowledgeType);
                    processParams.setIsCanZhuang(isCanZhuang);
                }

                processParams.setOutputRules(outputRules);
                processParams.setSource(source);
                processParams.setIsOnlyValue(isOnlyValue);
                CmPersistenceHelper.manager.save(processParams);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            formresult.setStatus(FormProcessingStatus.FAILURE);
            return formresult;
        }
        return formresult;
    }
    public static FormResult addProcessKnowledge(NmCommandBean nmcommandbean) throws WTException {
        FormResult formresult = new FormResult(FormProcessingStatus.SUCCESS);
        formresult.setNextAction(FormResultAction.REFRESH_OPENER);
        String paramNumber = nmcommandbean.getTextParameter("paramNumber");

        if (!Tools.isNull(paramNumber)) {
            GLProcessParams processParams = GyCsServerHelper.getProcessParamDefinition(paramNumber,false);
            String output = processParams.getKnowledgeOutputPara();
            String input = processParams.getKnowledgeInferencePara();
            String[] outs  = output.split("\\|");
            String[] ins = input.split("\\|");
            GLProcessKnowledge knowledge = new GLProcessKnowledge();
            knowledge.setKeyId(UUID.randomUUID().toString());
            knowledge.setXuHao("1");
            knowledge.setSheetName(processParams.getGyName());
            boolean hasData = false;
            for(int i = 0;i<ins.length;i++) {
                if (!Tools.isNull(ins[i])) {
                    String columnName = "column"+(i+1);
                    String columnValue =  nmcommandbean.getTextParameter(columnName);
                    if (!Tools.isNull(columnValue)) {
                        hasData = true;
                        try {
                            Field field = GLProcessKnowledge.class.getDeclaredField(columnName);
                            field.setAccessible(true);
                            field.set(knowledge, columnValue);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }
            for(int i = 0;i<outs.length;i++) {
                if (!Tools.isNull(outs[i])) {
                    String columnName = "column"+(ins.length+i+1);
                    String columnValue =  nmcommandbean.getTextParameter(columnName);
                    if (!Tools.isNull(columnValue)) {
                        hasData = true;
                        try {
                            Field field = GLProcessKnowledge.class.getDeclaredField(columnName);
                            field.setAccessible(true);
                            field.set(knowledge, columnValue);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if(hasData){
                try {
                    CmPersistenceHelper.manager.save(knowledge);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        } else {
            formresult.setStatus(FormProcessingStatus.FAILURE);
            return formresult;
        }
        return formresult;
    }

    public static FormResult deleteProcessKnowledge(NmCommandBean commandbean) throws WTException {
        FormResult form = new FormResult();
        List selected = commandbean.getSelected();
        for (Object obj : selected) {
            NmContext nmContext = (NmContext)obj;
            String oid = nmContext.getTargetOid().toString();
            DBUtil.deleteByTableAndKey("GLProcessKnowledge",CmPersistable.KEY_ID,oid);
        }
        form.setStatus(FormProcessingStatus.SUCCESS);
        form.setNextAction(FormResultAction.NONE);
        form.setNextAction(FormResultAction.REFRESH_OPENER);
        return form;
    }

    public static FormResult editProcessKnowledge(NmCommandBean nmcommandbean) throws WTException {
        FormResult formresult = new FormResult(FormProcessingStatus.SUCCESS);
        formresult.setNextAction(FormResultAction.REFRESH_OPENER);
        String paramNumber = nmcommandbean.getTextParameter("paramNumber");
        String knowledgeId = nmcommandbean.getTextParameter("knowledgeId");

        if (!Tools.isNull(paramNumber)&&!Tools.isNull(knowledgeId)) {
            Map<String,String> queryParams = new HashMap<String,String>();
            queryParams.put(CmPersistable.KEY_ID, knowledgeId);
            GLProcessKnowledge knowledge = (GLProcessKnowledge)GyCsServerHelper.queryGLObject(GLProcessKnowledge.class, queryParams);

            GLProcessParams processParams = GyCsServerHelper.getProcessParamDefinition(paramNumber,false);
            String output = processParams.getKnowledgeOutputPara();
            String input = processParams.getKnowledgeInferencePara();

            String[] outs  = output.split("\\|");
            String[] ins = input.split("\\|");
            for(int i = 0;i<ins.length;i++) {
                if (!Tools.isNull(ins[i])) {
                    String columnName = "column"+(i+1);
                    String columnValue =  nmcommandbean.getTextParameter(columnName);
                    if (!Tools.isNull(columnValue)) {
                        try {
                            Field field = GLProcessKnowledge.class.getDeclaredField(columnName);
                            field.setAccessible(true);
                            field.set(knowledge, columnValue);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }
            for(int i = 0;i<outs.length;i++) {
                if (!Tools.isNull(outs[i])) {
                    String columnName = "column"+(ins.length+i+1);
                    String columnValue =  nmcommandbean.getTextParameter(columnName);
                    if (!Tools.isNull(columnValue)) {
                        try {
                            Field field = GLProcessKnowledge.class.getDeclaredField(columnName);
                            field.setAccessible(true);
                            field.set(knowledge, columnValue);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            try {
                CmPersistenceHelper.manager.update(knowledge);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            formresult.setStatus(FormProcessingStatus.FAILURE);
            return formresult;
        }
        return formresult;
    }
    private static String MYRESOURCE = "ext.casc.importdata.productRB";

    public static FormResult importProcessKnowledgeData(NmCommandBean cb) throws WTException {
        File temp_file = (File) cb.getRequest().getAttribute("file");
        File temp_xlsFile = (File) cb.getRequest().getAttribute("file2");
        ResourceBundle rb = ResourceBundle.getBundle(MYRESOURCE);
        FormResult form = new FormResult();
        try {
            String returnValue = importData(temp_xlsFile,cb);
            if (returnValue != null && !returnValue.equals("")) {
                String info = "";
                if (returnValue.charAt(0) == '1') {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES2) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";

                } else {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";
                }
                form.setStatus(FormProcessingStatus.FAILURE);
                URLFactory urlfactory = new URLFactory();
                cb.getRequest().getSession().putValue("errorInfo", info);
                String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/importdata/importDataInfo.jsp?";
                form.setURL(url);
                form.setNextAction(FormResultAction.FORWARD);
                return form;
            } else {
                form.setStatus(FormProcessingStatus.SUCCESS);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据导入成功");
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
                return form;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return form;
        } finally {
            if (temp_file != null) temp_file.delete();
        }
    }


    public static String importData(File file,NmCommandBean cb) {
        String result = "";
        String paramNumber = cb.getTextParameter("paramNumber");
        FileInputStream fileInputStream = null;
        if (!Tools.isNull(paramNumber)) {
            try {
                fileInputStream = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fileInputStream);
                Sheet sheet = workbook.getSheetAt(0);
                GLProcessParams processParams = GyCsServerHelper.getProcessParamDefinition(paramNumber, false);
                String output = processParams.getKnowledgeOutputPara();
                String input = processParams.getKnowledgeInferencePara();
                String[] outs = output.split("\\|");
                String[] ins = input.split("\\|");

                int columnCount = outs.length+ins.length+1;

                for (int i = 1; i < sheet.getLastRowNum()+1; i++) {
                    Row row = sheet.getRow(i);
                    boolean needDb = false;
                    GLProcessKnowledge knowledge = new GLProcessKnowledge();
                    knowledge.setXuHao("1");
                    knowledge.setSheetName(processParams.getGyName());
                    for (int j = 0; j < columnCount; j++) {
                        String columnValue = ExcelUtil.getValue(row.getCell(j)).toString().trim();
                        if(j==0) {
                            if (!Tools.isTrimNull(columnValue)) {
                                knowledge.setKeyId(columnValue);
                            } else {
                                knowledge.setKeyId(UUID.randomUUID().toString());
                            }
                        }
                        if(j>0&&!Tools.isTrimNull(columnValue)){
                            needDb = true;
                            try {
                                Field field = GLProcessKnowledge.class.getDeclaredField("column"+j);
                                field.setAccessible(true);
                                field.set(knowledge, columnValue);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (NoSuchFieldException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }
                    if(needDb){
                        CmPersistenceHelper.manager.save(knowledge);
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
                result = e.getLocalizedMessage();
            } finally {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    public static FormResult importParams(NmCommandBean cb) throws WTException {
        File temp_xlsFile = (File) cb.getRequest().getAttribute("file2");
        ResourceBundle rb = ResourceBundle.getBundle(MYRESOURCE);
        FormResult form = new FormResult();
        try {
            String returnValue = importParamsData(temp_xlsFile,cb);
            if (returnValue != null && !returnValue.equals("")) {
                String info = "";
                if (returnValue.charAt(0) == '1') {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES2) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";

                } else {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";
                }
                form.setStatus(FormProcessingStatus.FAILURE);
                URLFactory urlfactory = new URLFactory();
                cb.getRequest().getSession().putValue("errorInfo", info);
                String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/importdata/importDataInfo.jsp?";
                form.setURL(url);
                form.setNextAction(FormResultAction.FORWARD);
                return form;
            } else {
                form.setStatus(FormProcessingStatus.SUCCESS);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据导入成功");
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
                return form;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return form;
        } finally {
            if (temp_xlsFile != null) temp_xlsFile.delete();
        }
    }

    private static String importParamsData(File file, NmCommandBean cb) {
        String result = "";
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row rowTitle =  sheet.getRow(0);

            for (int i = 1; i < sheet.getLastRowNum()+1; i++) {
                Row row = sheet.getRow(i);

                String partNumber = ExcelUtil.getValue(row.getCell(0)).toString().trim();

                String partVersion = ExcelUtil.getValue(row.getCell(2)).toString().trim();
                String templateId = ExcelUtil.getValue(row.getCell(3)).toString().trim();

                for (int j = 4; j < row.getLastCellNum(); j++) {
                    GLProcessParamValues pv = new GLProcessParamValues();
                    String paramValue = ExcelUtil.getValue(row.getCell(j)).toString().trim();
                    String title = ExcelUtil.getValue(rowTitle.getCell(j)).toString();
                    String paramNumber = "";
                    if(title!=null && title.contains("【")){
                        paramNumber = title.substring(title.lastIndexOf("【")+1,title.length()-1);
                    }
                    if(Tools.isNull(paramNumber)) continue;

                    pv.setKeyId(partNumber+"_"+paramNumber);
                    pv.setPartNumber(partNumber);
                    pv.setPartVersion(partVersion);
                    pv.setGyParamNumber(paramNumber);
                    pv.setTemplateId(templateId);
                    GLProcessParamDefinition ppd = GyCsServerHelper.getGLProcessParamDefinition(templateId,paramNumber);
                    if(ppd==null){
                        continue;
                    }
                    pv.setGyParamName(ppd.getGyParamName());
                    pv.setParamValue(paramValue);
                    CmPersistenceHelper.manager.save(pv);
                }
            }
            result = "";
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getLocalizedMessage();
        } finally {
            try {
                fileInputStream.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public static File downloadParamsExcel(HttpServletRequest request) throws WTException {
        NmCommandBean commandBean = (NmCommandBean)request.getAttribute("commandBean");
        //ArrayList list = commandBean.getSelectedInOpener();
       // ArrayList list2 =  commandBean.getSelected();
        ArrayList list =  commandBean.getSelectedOidForPopup();
        //ArrayList list4=  commandBean.getSelectedContextsForPopup();
        List<WTPart> parts = new ArrayList<WTPart>();
        for (Object singleObject : list) {
            if (singleObject instanceof NmOid) {
                NmOid oid = (NmOid)singleObject;
                Persistable selectObj = oid.getWtRef().getObject();
                if(selectObj instanceof WTPart){
                    parts.add((WTPart)selectObj);
                }
            }
        }
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("零件编号");
        titles.add("零件名称");
        titles.add("零件版本");
        titles.add("模版ID(勿删)");
        String templateOid = (String) commandBean.getRequest().getSession().getAttribute("templateOid");
        if(templateOid.startsWith("VR")){
            WTDocument doc = (WTDocument) ReferenceFactory.getObjectbyOid(templateOid);
            templateOid = "OR:wt.doc.WTDocument:"+doc.getPersistInfo().getObjectIdentifier().getId();
        }
        List<GLProcessParamDefinition> processParamDefinitions = GyCsServerHelper.queryGLProcessParamDefinition(templateOid);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            String currentUser =  SessionHelper.getPrincipal().getName();
            WTProperties wtp = WTProperties.getLocalProperties();
            String temp = wtp.getProperty("wt.temp");
            ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
            boolean hasGenTitle = false;
            for(WTPart p:parts){
                ArrayList<String> value = new ArrayList<String>();
                value.add(p.getNumber());
                value.add(p.getName());
                value.add(VersionCommonHelper.getVersion(p));
                value.add(templateOid);

                for (GLProcessParamDefinition definition : processParamDefinitions) {
                    if ("知识参数".equals(definition.getGyParamType())) continue;
                    GLProcessParams params =  GyCsServerHelper.getProcessParamDefinition(definition.getGyParamNumber()) ;
                    String paramName = definition.getGyParamName();
                    if(!Tools.isTrimNull(params.getUnit())){
                        paramName = definition.getGyParamName()+"("+params.getUnit()+")";
                    }
                    if(!hasGenTitle){
                        String titleValue = paramName + "【"+definition.getGyParamNumber()+"】";
                        titles.add(titleValue);
                    }
                    List<GLProcessParamValues> glProcessParamValues = ext.casc.mpm.process.ProcessUtil.queryGLProcessParamValues(p.getNumber(), definition.getGyParamNumber());
                    StringBuilder  paramValue = new StringBuilder("");
                    if (!glProcessParamValues.isEmpty()) {
                        GLProcessParamValues paramValues = glProcessParamValues.get(glProcessParamValues.size() - 1);
                        if (paramValues != null) {
                            if(!StringUtils.isEmpty(paramValues.getParamValue())){
                                paramValue.append(paramValues.getParamValue());
                            }
                        }
                    }
                    value.add(paramValue.toString());
                }
                values.add(value);
                hasGenTitle=true;

            }
            ExcelFileGenerator gen = new ExcelFileGenerator(titles,values);
            File file = new File(temp + File.separator +"工艺参数导出_"+currentUser+"_"+sdf.format(new Date())+".xls");
            gen.expordExcel(new FileOutputStream(file));
            return file;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
