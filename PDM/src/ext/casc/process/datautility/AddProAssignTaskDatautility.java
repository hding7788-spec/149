package ext.casc.process.datautility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;

import ext.casc.constants.Constants;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.NmTableGUIComponent;

public class AddProAssignTaskDatautility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
        String oid = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
        ProcessTask processTask = ProcessUtil.getProcessTaskByPart((WTPart) object,ProcessConstants.TASK_TYPE_ZZGYRW);
        if ("taskType".equals(componentId)) {
            if (processTask != null) {
                return processTask.getTaskType();
            }
        } else if ("zhuzhichejian".equals(componentId)) {
            if (processTask != null) {
                return processTask.getZhuzhichejian();
            }
        } else if ("fuzhichejian".equals(componentId)) {
            if (processTask != null) {
                String fuzhichejian = processTask.getFuzhichejian();
                if (fuzhichejian != null && !"".equals(fuzhichejian)) {
                    fuzhichejian = fuzhichejian.replaceAll("&", "-");
                    return fuzhichejian;
                }
            }
        } else if ("jihuawanchengshijian".equals(componentId)) {
            if (processTask != null) {
                return ProcessUtil.formatTime2(processTask.getEndDate().getTime());
            }
        } else if ("renwuyaoqiu".equals(componentId)) {
            if (processTask != null) {
                return processTask.getRenwuyaoqiu();
            }
        } else if ("addchejian".equals(componentId)) {
            String zhuzhichejian = processTask.getZhuzhichejian();
            String fuzhichejian = processTask.getFuzhichejian();
            ArrayList<String> valueList = getAllCheJian(zhuzhichejian,fuzhichejian);
            StringBuffer sb = new StringBuffer();
            for (String n : valueList) {
                sb.append("<input type=\"checkbox\" id=\""+oid+"_addchejian"+n+"\" name=\""+oid+"_addchejian"+n+"\">"+n+";</input>");
            }
            GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
            guicomponentarrayMain.setValueHidden(false);
            NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
            gui.setRequired(true);
            guicomponentarrayMain.addGUIComponent(gui);
            guicomponentarrayMain.setRequired(true);
            return guicomponentarrayMain;
        }
        return "";
    }

    public static ArrayList<String> getAllCheJian(String zhuzhichejian, String fuzhichejian) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
            Set<String> set = new TreeSet<String>();
            set.addAll(map.keySet());
            for (String chejian : set) {
                if ((zhuzhichejian != null && !zhuzhichejian.equals(chejian))
                        && (fuzhichejian != null && !fuzhichejian.contains(chejian))) {
                    list.add(chejian);
                }
            }
            if ((zhuzhichejian != null && !zhuzhichejian.equals(Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN))
                        && (fuzhichejian != null && !fuzhichejian.contains(Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN))) {
                list.add(Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN);
            }

        } catch (WTException e) {
            e.printStackTrace();
        }

        return list;
    }
}
