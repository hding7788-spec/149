package ext.casc.workflow;

import cn.hutool.core.util.StrUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.workflow.tree.ActivityRecordHelper;
import ext.casc.workflow.tree.GWActivityRecord;
import ext.sast.common.fc.CmPersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyVetoException;
import java.io.*;
import java.util.*;

public class WorkflowSaveWriteInfoProcessor extends DefaultObjectFormProcessor{
    private static String wt_codebase = "";
    private static String source_path = "";

    static{
        try {
            WTProperties prop = WTProperties.getLocalProperties();
            wt_codebase = prop.getProperty("wt.codebase.location");
            source_path = wt_codebase + File.separator + "ext" + File.separator + "casc" + File.separator + "workflow" + File.separator + "temp";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void  process(String workItemOid,String data,ServletRequest request){
    	try {
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            WfProcess process = wfAct.getParentProcess();
            //List<String> oidList = SignatureHelper.getReviewOid(workItemOid);
            //System.out.println("-------oidList:"+oidList);

            InputStream is = WfUtil.getAttachByWfProcess(process);
            File file = new File(source_path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File tempFile = new File(source_path+File.separator+"record.properties");
            if (is == null) {
                is = new FileInputStream(tempFile);
            }
            Properties properties = new Properties();
            properties.load(is);
            is.close();
            Map map = new HashMap();
            String[] ss = data.split("&");
            for(String s:ss){
            	if(!"".equals(s)&&s.contains("=")){
            		String[] ss2 = s.split("=");
            		if(s.endsWith("=")){
            			map.put(ss2[0], "");
            		}else{
            			if(ss2[0].indexOf("_sign_person_valueB")>-1){
            				if(map.get(ss2[0])!=null&&!"".equals(map.get(ss2[0]))){
            					String value = map.get(ss2[0])+";"+ss2[1];
            					map.put(ss2[0], value);
            				}else{
            					map.put(ss2[0], ss2[1]);
            				}
            			}else{
            				map.put(ss2[0], ss2[1]);
            			}
            			//map.put(ss2[0], ss2[1]);

            		}
            	}
            }

            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = String.valueOf(iterator.next());
                if (key.indexOf("_advise")>-1) {
                    String value =(String) map.get(key);
                    properties.setProperty(workItemOid+"_"+key, value);
                } else if (key.indexOf("_sign_person_valueA")>-1) {
                    String value = (String) map.get(key);
                    properties.setProperty(workItemOid+"_"+key, value);
                } else if (key.indexOf("_sign_person_valueB")>-1) {
                    String tempValue = (String)map.get(key);
                    properties.setProperty(workItemOid+"_"+key, tempValue);
                } else if ((key.indexOf("_sign_person")>-1)&&(key.indexOf("_sign_person_valueB")<0)&&(key.indexOf("_sign_person_valueA")<0)) {
                    String value =(String)  map.get(key);
                    properties.setProperty(workItemOid+"_"+key, value);
                } else if (key.indexOf("_select")>-1) {
                    String value =(String)  map.get(key);
                    properties.setProperty(workItemOid+"_"+key, value);
                }
            }

            //重新写入附件
            FileOutputStream fos = new FileOutputStream(tempFile);
            properties.store(fos, "");
            fos.close();

            //保存新附件
            WfUtil.saveAttachToWfProcess(process, source_path,"record.properties");

        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> arg1) throws WTException {
        FormResult form = new FormResult();
        FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据保存完毕！");
        try {
            HttpServletRequest request = commandBean.getRequest();
            String workItemOid = request.getParameter("oid");
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            WfProcess process = wfAct.getParentProcess();
            //List<String> oidList = SignatureHelper.getReviewOid(workItemOid);
            //System.out.println("-------oidList:"+oidList);

            InputStream is = WfUtil.getAttachByWfProcess(process);
            File file = new File(source_path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File tempFile = new File(source_path+File.separator+"record.properties");
            if (is == null) {
                is = new FileInputStream(tempFile);
            }
            Properties properties = new Properties();
            properties.load(is);
            is.close();

            //将新内容添加到附件
            Map map = request.getParameterMap();
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = String.valueOf(iterator.next());
                if (key.indexOf("_advise")>-1) {
                    String value = request.getParameter(key);
                    properties.setProperty(workItemOid+"_"+key, value);
                } else if (key.indexOf("_sign_person_valueA")>-1) {
                    String value = request.getParameter(key);
                    properties.setProperty(workItemOid+"_"+key, value);
                } else if (key.indexOf("_sign_person_valueB")>-1) {
                    String[] values = request.getParameterValues(key);
                    String tempValue = "";
                    for (String value : values) {
                        if (tempValue.equals("")) {
                            tempValue = value;
                        }else {
                            tempValue = tempValue+";"+value;
                        }
                    }
                    properties.setProperty(workItemOid+"_"+key, tempValue);
                } else if ((key.indexOf("_sign_person")>-1)&&(key.indexOf("_sign_person_valueB")<0)&&(key.indexOf("_sign_person_valueA")<0)) {
                    String value = request.getParameter(key);
                    properties.setProperty(workItemOid+"_"+key, value);
                } else if (key.indexOf("_select")>-1) {
                    String value = request.getParameter(key);
                    properties.setProperty(workItemOid+"_"+key, value);
                }
            }

            //重新写入附件
            FileOutputStream fos = new FileOutputStream(tempFile);
            properties.store(fos, "");
            fos.close();

            //保存新附件
            WfUtil.saveAttachToWfProcess(process, source_path,"record.properties");

            form.setStatus(FormProcessingStatus.SUCCESS);
        } catch (WTRuntimeException e) {
            form.setStatus(FormProcessingStatus.FAILURE);
            message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "数据保存失败！");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            form.setStatus(FormProcessingStatus.FAILURE);
            message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "数据保存失败！");
            e.printStackTrace();
        } catch (IOException e) {
            form.setStatus(FormProcessingStatus.FAILURE);
            message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "数据保存失败！");
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            form.setStatus(FormProcessingStatus.FAILURE);
            message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "数据保存失败！");
            e.printStackTrace();
        }
        form.addFeedbackMessage(message);
        form.setNextAction(FormResultAction.NONE);
        return form;
    }


    public void saveRecord(String workItemOid, String data) {
        try {
            if(StrUtil.isNotEmpty(data)) {
                String[] datas = data.split("@!@");
                for(String str : datas) {
                    if(StrUtil.isNotEmpty(str)) {
                        Map<String,String> map = new HashMap();
                        String[] ss = str.split("&");
                        for(String s : ss) {
                            if(!"".equals(s) && s.contains("=")) {
                                String[] ss2 = s.split("=");
                                String key = ss2[0];
                                if(s.endsWith("=")) {
                                    map.put(key, "");
                                } else {
                                    map.put(key, ss2[1]);
                                }
                            }
                        }
                        String id = map.get("id");
                        String advise = map.get("advise");
                        String select = map.get("select");
                        GWActivityRecord record = ActivityRecordHelper.getActivityRecord(workItemOid, id);
                        if(record != null) {
                            try {
                                record.setAdvise(advise);
                                record.setResult(select);
                                CmPersistenceHelper.manager.update(record);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            try {
                                record = new GWActivityRecord();
                                record.setKeyId(UUID.randomUUID().toString());
                                record.setWorkitemOid(workItemOid);
                                record.setVerOid(id);
                                record.setResult(select);
                                record.setAdvise(advise);
                                CmPersistenceHelper.manager.save(record);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch(WTRuntimeException e) {
            e.printStackTrace();
        }
    }
}
