package com.glaway.mpm.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.util.WTException;
import ext.casc.process.util.ProcessUtil;



public class PbomUtil {
    public static String GROUP_GYRWFG_NAME = "会签";
	public static String GROUP_GYHQ_NAME = "会签";
    public static String GROUP_GONGYIZUZHANG_NAME_SPLIT = "_工艺组长_";
    public static Map<String, String> getAllCheJianAndXiangMuBuMapGYZZ(WTContainer wtContainer,String group) {
        Map<String, String> treeMap = new TreeMap<String, String>();
        wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
        try {
            Map<String, List<WTUser>> map = getGroupAndUsersInOrgContainer(group);
            //System.out.println("--------->>>>>map:" + map);
            Set<Entry<String, List<WTUser>>> set = map.entrySet();
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                Entry<String, List<WTUser>> entry = (Entry<String, List<WTUser>>) iterator.next();
                List<WTUser> list = entry.getValue();
                String values = "";
                for (WTUser wtUser : list) {
                    if("".equals(values)){
                        values = rf.getReferenceString(wtUser);
                    } else {
                        values = values + ";" + rf.getReferenceString(wtUser);
                    }
                }
                if (!entry.getValue().isEmpty()) {
                    treeMap.put(entry.getKey(), values);
                }
            }

            //项目部负责人单独从产品团队里的"项目部型号主管"角色里获取
            List<WTUser> xmbUsers = ProcessUtil.getRoleUsersByWTContainer("XIANGMUBUXINGHAOZHUGUANG", wtContainer);
            String values = "";
            for (WTUser wtUser : xmbUsers) {
            	if("".equals(values)){
                    values = rf.getReferenceString(wtUser);
                } else {
                    values = values + ";" + rf.getReferenceString(wtUser);
                }
			}
            treeMap.put("项", values);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return treeMap;
    }
    /**
     * 获取工艺组长组及其所有人员
     *
     * @return Map<String, List<WTUser>> String为车间序号，List<WTUser>为该车间的工艺组长
     * @throws WTException
     */
    public static Map<String, List<WTUser>> getGroupAndUsersInOrgContainer(String deptName) throws WTException {
        Map<String, List<WTUser>> map = new HashMap<String, List<WTUser>>();
        OrgContainer orgContainer = OrgUtil.getOrgContainer();
        List list = OrgUtil.getNodes(orgContainer);
        if (list != null) {
            WTGroup group = null;
            for (int i = 0; i < list.size(); i++) {
                Object object = list.get(i);
                if (object instanceof WTGroup) {
                    group = (WTGroup) object;
                    String groupName = group.getDescription();
                    System.out.println(groupName);
                    if (groupName!=null&&groupName.indexOf(GROUP_GONGYIZUZHANG_NAME_SPLIT) > -1) {
                        String xuhao = groupName.substring(groupName.lastIndexOf("_")+1, groupName.length());
                        List<WTUser> allUsers = new ArrayList<WTUser>();
                        Enumeration enumeration = group.members();
                        while (enumeration.hasMoreElements()) {
                            Object userObject = enumeration.nextElement();
                            if (userObject instanceof WTUser) {
                                allUsers.add((WTUser) userObject);
                            }
                        }
                        map.put(xuhao, allUsers);
                    }
                }
            }
        }
        return map;
    }
    public static Document getDocument(File file) {
		if (!file.exists()) {

			return null;
		}
		Document document = null;
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("GBK");
		try {
			document = saxReader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}
    /**
	 * @Title: getDocument
	 * @Description: 通过字节数组生成Document对象
	 * @param @param xmlContentArray
	 * @param @return
	 * @param @throws Exception
	 * @return Document
	 * @throws
	 */
	public static Document getDocument(byte[] xmlContentArray) throws Exception {
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("GBK");
		Document document = null;
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(xmlContentArray);
			document = saxReader.read(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != inputStream)
				inputStream.close();
			inputStream = null;
			saxReader = null;
		}
		return document;
	}
}
