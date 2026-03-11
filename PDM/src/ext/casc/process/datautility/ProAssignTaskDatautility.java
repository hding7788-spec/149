package ext.casc.process.datautility;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.AbstractGuiComponent;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.DateInputComponent;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.TextBox;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.NmTableGUIComponent;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.PersistenceException;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.*;

public class ProAssignTaskDatautility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
        String oid = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
        NmCommandBean commandBean = context.getNmCommandBean();
        HttpSession session = commandBean.getRequest().getSession();
        String taskType = (String)session.getAttribute("TaskType");
        ProcessTask processTask = null;
        if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)
                ||taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
            processTask = ProcessUtil.getProcessTaskByPart((WTPart)object,ProcessConstants.TASK_TYPE_ZZGYRW);

        }
        String zzcjValue = "";
        String fzcjValue = "";
		String state = "";
        if (object instanceof WTPart) {
            WTPart part = (WTPart)object;
            if(!taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)&&!taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)){
              	List<WTPart> parts=this.getAllVersionWtpart(part);
            	for(WTPart wtpart:parts){
					WTDocument doc = getZProcessPlan(wtpart);
					//有正式主工艺
					if (doc != null) {
						state = doc.getState().getState().getDisplay();
						if ("已作废".equals(state)) {
							if (ProcessUtil.isExistProcessTask(wtpart, taskType)) {
								NmTableGUIComponent gui = new NmTableGUIComponent("");
								GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
								guicomponentarrayMain.addGUIComponent(gui);
								return guicomponentarrayMain;
							}
						} else {
							NmTableGUIComponent gui = new NmTableGUIComponent("");
							GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
							guicomponentarrayMain.addGUIComponent(gui);
							return guicomponentarrayMain;
						}
						//无正式主工艺
					} else {
						if (ProcessUtil.isExistProcessTask(wtpart, taskType)) {
							NmTableGUIComponent gui = new NmTableGUIComponent("");
							GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
							guicomponentarrayMain.addGUIComponent(gui);
							return guicomponentarrayMain;
						}
					}

             }
           }


            IBAUtility ibaUtility = new IBAUtility(part);
            zzcjValue = ibaUtility.getIBAValue("ZZCJ");
            if(zzcjValue == null){
                zzcjValue = "";
            }
            fzcjValue = ibaUtility.getIBAValue("FZCJ");
            if(fzcjValue == null){
                fzcjValue = "";
            }
        }
        Object reObj = null;
        Versioned version = null;
        String veroid = null;
        ReferenceFactory rf = new ReferenceFactory();
		QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) object);
        if (qr.hasMoreElements()) {
            version = (Versioned) qr.nextElement();
            veroid = rf.getReference(version).toString();
        }
        veroid = veroid.replaceAll(">", ":");

        WTUser user = (WTUser)SessionHelper.getPrincipal();
        String userOid= PersistenceHelper.getObjectIdentifier((Persistable) user).toString();

        try {
			if (componentId.equals("sign_person")) {//工艺会签人员:工艺组长、工艺员
        		 String persons = "";
                 String personsDis = "";
				String value = "<input type=\"text\"  readonly name=\"" + oid + "_sign_person\"  id=\"" + veroid + "_sign_person\" value=\"" + personsDis
						+ "\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:showHQRY('" + oid + "','" + veroid + "');\"  id=\"" + veroid + "_selecPer\"/><input type=\"hidden\"   name=\""
						+ oid + "_sign_person_value\" id=\"" + veroid + "_sign_person_value\" value=\"" + persons + "\">";
                NmTableGUIComponent gui = new NmTableGUIComponent(value);
                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.addGUIComponent(gui);
                return guicomponentarrayMain;
            }else if ("zhuzhichejian".equals(componentId)) {
            	String oldValue1 = getOldValue(context, userOid+"_"+context.getNmCommandBean().getCompContext()+"___"+oid+"_zhuzhichejian___combobox");

                ArrayList<String> valueList = getValueList();
                ArrayList<String> displayList = getDisplayList();
                ArrayList<String> selectList = getSelectList();
                AbstractGuiComponent gui = new ComboBox(valueList, displayList, selectList);
                ((ComboBox) gui).setId(veroid + "_zhuzhichejian");
                ((ComboBox) gui).setName(oid + "_zhuzhichejian");
                ((ComboBox) gui).setMultiValued(false);
                ((ComboBox) gui).setEditable(true);
                ((ComboBox) gui).setRequired(true);
                ((ComboBox) gui).addJsAction("onChange", "verify1(this)");
				if (processTask != null) {//如果是工艺更改，则显示上一次的主制车间记录
                    ((ComboBox) gui).setSelected(processTask.getZhuzhichejian());
                }else if (!"".equals(zzcjValue)) {
                    ((ComboBox) gui).setSelected(zzcjValue);
                }
                if(!"".equals(oldValue1)){
                	((ComboBox) gui).setSelected(oldValue1);
                }
                reObj = gui;
            } else if ("fuzhichejian".equals(componentId)) {
            	 String oldValue2 = getOldValue(context, userOid+"_"+oid+"_fuzhichejian1");

                // ArrayList<String> valueList = getValueList();
                // ArrayList<String> displayList = getDisplayList();
                // ArrayList<String> selectList = getSelectList();
                // AbstractGuiComponent gui = new ComboBox(valueList, displayList, selectList);
                // ((ComboBox)gui).setId(oid+"_fuzhichejian");
                // ((ComboBox)gui).setName(oid+"_fuzhichejian");
                // ((ComboBox)gui).setMultiSelect(true);
                // ((ComboBox)gui).setSize(4);
                // ((ComboBox)gui).setMultiValued(true);
				// ((ComboBox)gui).setSelected("一车间");

//            AbstractGuiComponent gui = new TextBox();
//            ((TextBox) gui).setId(oid + "_fuzhichejian");
//            ((TextBox) gui).setName(oid + "_fuzhichejian");
//            ((TextBox) gui).setWidth(20);
//            reObj = gui;

                List<String> allList = new ArrayList<String>();
				if (processTask != null) {//如果是工艺更改或临时工艺，则显示上一次选择的辅制车间记录
                    String tempchejian = processTask.getFuzhichejian();
                    if (tempchejian!=null&&tempchejian.contains("&")) {
                        String[] str = tempchejian.split("&");
                        allList = Arrays.asList(str);
                    }else {
                        allList.add(tempchejian);
                    }
                }else {
                    String str[] = fzcjValue.split("-");
                    allList = Arrays.asList(str);
                }

                StringBuffer sb = new StringBuffer();
                ArrayList<String> list = ProcessUtil.getAllCheJian();
                if(!"".equals(oldValue2)){
                	for (String n : list) {
						if (oldValue2.contains(n)) {//如果是工艺更改或临时工艺，则显示上一次选择的辅制车间记录
                            sb.append("<input type=\"checkbox\" checked id=\""+veroid+"_fuzhichejian"+n+"\" name=\""+oid+"_fuzhichejian1\" onChange=\"verify2(this)\" value=\""+n+"\">"+n+";</input>");
                        } else {
                            sb.append("<input type=\"checkbox\" id=\""+veroid+"_fuzhichejian"+n+"\" name=\""+oid+"_fuzhichejian1\" value=\""+n+"\" onChange=\"verify2(this)\">"+n+";</input>");
                        }
                    }
                }else{
	                for (String n : list) {
						if (allList.contains(n)) {//如果是工艺更改或临时工艺，则显示上一次选择的辅制车间记录
	                        sb.append("<input type=\"checkbox\" checked id=\""+veroid+"_fuzhichejian"+n+"\" name=\""+oid+"_fuzhichejian1\" onChange=\"verify2(this)\" value=\""+n+"\">"+n+";</input>");
	                    } else {
	                        sb.append("<input type=\"checkbox\" id=\""+veroid+"_fuzhichejian"+n+"\" name=\""+oid+"_fuzhichejian1\" value=\""+n+"\" onChange=\"verify2(this)\">"+n+";</input>");
	                    }
	                }
                }
                //sb.append("<input type=\"checkbox\" id=\""+oid+"_fuzhichejian1\" name=\""+oid+"_fuzhichejian1\">1;</input><input type=\"checkbox\" id=\""+oid+"_fuzhichejian2\" name=\""+oid+"_fuzhichejian2\">2;</input>");
                //sb.append("<input type=\"checkbox\" id=\""+oid+"_fuzhichejian3\" name=\""+oid+"_fuzhichejian3\">3;</input><input type=\"checkbox\" id=\""+oid+"_fuzhichejian4\" name=\""+oid+"_fuzhichejian4\">4;</input>");
                //sb.append("<input type=\"checkbox\" id=\""+oid+"_fuzhichejian5\" name=\""+oid+"_fuzhichejian5\">5;</input><input type=\"checkbox\" id=\""+oid+"_fuzhichejian6\" name=\""+oid+"_fuzhichejian6\">6;</input>");
                //sb.append("<input type=\"checkbox\" id=\""+oid+"_fuzhichejian7\" name=\""+oid+"_fuzhichejian7\">7;</input><input type=\"checkbox\" id=\""+oid+"_fuzhichejian8\" name=\""+oid+"_fuzhichejian8\">8</input>");

                GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
                guicomponentarrayMain.setValueHidden(false);
                NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
                gui.setRequired(true);
                if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)){
                    gui.setRequired(false);
                }
                guicomponentarrayMain.addGUIComponent(gui);
                guicomponentarrayMain.setRequired(true);
                return guicomponentarrayMain;
            } else if ("jihuawanchengshijian".equals(componentId)) {

            	 String oldValue4 = getOldValue(context,userOid+"_"+context.getNmCommandBean().getCompContext()+"___"+oid+"_jihuawanchengshijian_col_"+oid+"_jihuawanchengshijian___textbox" );

                AbstractGuiComponent gui = new DateInputComponent(componentId, DateInputComponent.ValueType.DATE_ONLY);
                ((DateInputComponent) gui).setId(veroid + "_jihuawanchengshijian");
                ((DateInputComponent) gui).setName(oid + "_jihuawanchengshijian");
                ((DateInputComponent) gui).setColumnName(oid + "_jihuawanchengshijian");
                ((DateInputComponent) gui).setReadOnly(true);
                ((DateInputComponent) gui).setTimeZone(TimeZone.getDefault());
                ((DateInputComponent) gui).addJsAction("onChange", "verify3(this)",DateInputComponent.UI.DATE_UI);
                //((DateInputComponent) gui).setRequired(true);
                if (processTask!=null) {
                    ((DateInputComponent) gui).setValue(processTask.getEndDate());
                }
                if(!"".equals(oldValue4)){
                	((DateInputComponent) gui).setValue(oldValue4);
                }
                reObj = gui;
            }  else if ("cldePlanTime".equals(componentId)) {

           	 String oldValue4 = getOldValue(context,userOid+"_"+context.getNmCommandBean().getCompContext()+"___"+oid+"_cldePlanTime_col_"+oid+"_cldePlanTime___textbox" );

               AbstractGuiComponent gui = new DateInputComponent(componentId, DateInputComponent.ValueType.DATE_ONLY);
               ((DateInputComponent) gui).setId(veroid + "_cldePlanTime");
               ((DateInputComponent) gui).setName(oid + "_cldePlanTime");
               ((DateInputComponent) gui).setColumnName(oid + "_cldePlanTime");
               ((DateInputComponent) gui).setReadOnly(true);
               ((DateInputComponent) gui).setTimeZone(TimeZone.getDefault());
               ((DateInputComponent) gui).addJsAction("onChange", "verify4(this)",DateInputComponent.UI.DATE_UI);
               //((DateInputComponent) gui).setRequired(true);
               if (processTask!=null) {
                   ((DateInputComponent) gui).setValue(processTask.getEndDate());
               }
               if(!"".equals(oldValue4)){
               	((DateInputComponent) gui).setValue(oldValue4);
               }
               reObj = gui;
           } else if ("renwuyaoqiu".equals(componentId)) {
            	 String oldValue3 = getOldValue(context, userOid+"_"+context.getNmCommandBean().getCompContext()+"___"+oid+"_renwuyaoqiu___textbox");
                AbstractGuiComponent gui = new TextBox();
                ((TextBox) gui).setId(oid + "_renwuyaoqiu");
                ((TextBox) gui).setName(oid + "_renwuyaoqiu");
                ((TextBox) gui).setWidth(30);
                if (processTask!=null) {
                    ((TextBox) gui).setValue(processTask.getRenwuyaoqiu());
                }
                if(!"".equals(oldValue3)){
                	 ((TextBox) gui).setValue(oldValue3);
                }
                reObj = gui;
            }else if ("renwuyiju".equals(componentId)) {
            	String oldValue1 = getOldValue(context, userOid+"_"+context.getNmCommandBean().getCompContext()+"___"+oid+"_zhuzhichejian___combobox");

                ArrayList<String> valueList = getRenwuYijuValueList();
                ArrayList<String> displayList = getRenwuYijuDisplayList();
                ArrayList<String> selectList = getRenwuYijuSelectList();
                AbstractGuiComponent gui = new ComboBox(valueList, displayList, selectList);
                ((ComboBox) gui).setId(veroid + "_renwuyiju");
                ((ComboBox) gui).setName(oid + "_renwuyiju");
                ((ComboBox) gui).setMultiValued(false);
                ((ComboBox) gui).setEditable(true);
                ((ComboBox) gui).setRequired(true);
                ((ComboBox) gui).addJsAction("onChange", "verify5(this)");
				if (processTask != null) {//如果是工艺更改，则显示上一次的主制车间记录
                    ((ComboBox) gui).setSelected(processTask.getZhuzhichejian());
                }else if (!"".equals(zzcjValue)) {
                    ((ComboBox) gui).setSelected(zzcjValue);
                }
                if(!"".equals(oldValue1)){
                	((ComboBox) gui).setSelected(oldValue1);
                }
                reObj = gui;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return reObj;
    }

    private ArrayList<String> getRenwuYijuSelectList() {
    	ArrayList<String> resultList = new ArrayList<String>();
		resultList.add("蓝图");
		resultList.add("并行生产");
		resultList.add("返工返修");
		resultList.add("设计更改");
		resultList.add("工艺更改");
		resultList.add("设计通知");
		resultList.add("工艺通知");
		resultList.add("临时生产");
		return resultList;
	}

	private ArrayList<String> getRenwuYijuDisplayList() {
		ArrayList<String> resultList = new ArrayList<String>();
		resultList.add("蓝图");
		resultList.add("并行生产");
		resultList.add("返工返修");
		resultList.add("设计更改");
		resultList.add("工艺更改");
		resultList.add("设计通知");
		resultList.add("工艺通知");
		resultList.add("临时生产");
		return resultList;
	}

	private ArrayList<String> getRenwuYijuValueList() {
		ArrayList<String> resultList = new ArrayList<String>();
		resultList.add("蓝图");
		resultList.add("并行生产");
		resultList.add("返工返修");
		resultList.add("设计更改");
		resultList.add("工艺更改");
		resultList.add("设计通知");
		resultList.add("工艺通知");
		resultList.add("临时生产");
		return resultList;
	}

	private static ArrayList<String> getDisplayList() throws RemoteException {
        return ProcessUtil.getAllCheJian();
    }

    private static ArrayList<String> getValueList() throws RemoteException {
        return ProcessUtil.getAllCheJian();
    }

    private static ArrayList<String> getSelectList() throws RemoteException {
        return ProcessUtil.getAllCheJian();
    }

    private static String getOldValue(ModelContext mc,String key) {
        try {
            NmCommandBean commandBean = mc.getNmCommandBean();
            HttpServletRequest request = commandBean.getRequest();
            WTProperties prop = WTProperties.getLocalProperties();
            String wt_temp = prop.getProperty("wt.temp");
            WTUser user = (WTUser)SessionHelper.getPrincipal();
            File file = new File(wt_temp+File.separator+user.getName()+File.separator+"record.properties");
            if(!file.exists()) {
            	return "";
            }
            InputStream is = new FileInputStream(file);
            if (is != null) {
                Properties pro = new Properties();
                pro.load(is);
                is.close();
                if (pro.get(key)==null) {
                    return "";
                }else {
                    return String.valueOf(pro.get(key));
                }
            }

        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static List<WTPart> getAllVersionWtpart(WTPart part){
    	List<WTPart> list=new ArrayList<WTPart>();
    	try {
    		String s=part.getVersionIdentifier().getValue();
			WTPart[] parts=wt.clients.prodmgmt.WTPartHelper.findPartByNumber(part.getNumber());
            for(WTPart wtpart:parts){
            	if(s.equals(wtpart.getVersionIdentifier().getValue())){
            		list.add(wtpart);
            	}
            }

		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    	return list;

    }

	public static WTDocument getZProcessPlan(WTPart part) throws WTException {
		WTDocument document = null;
		List<WTDocument> docList = WTPartUtil.getDescribedDocumentByPart(part, "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN");
		for (WTDocument doc : docList) {
			String planType = IBAHelper.getIBAValue(doc, "PPLANTYPE");
			String zfFlag = IBAHelper.getIBAValue(doc, "ZFFLAG");
			if ("正式工艺文件".equals(planType) && "Z".equals(zfFlag)) {
				document = doc;
			}
		}
		return document;
	}
}
