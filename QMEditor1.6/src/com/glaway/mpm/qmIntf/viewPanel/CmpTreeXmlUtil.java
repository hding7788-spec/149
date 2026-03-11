package com.glaway.mpm.qmIntf.viewPanel;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.visual.log.VaLogger;

/**
 * @author ylshao
 * @Description: CmpTree
 * @date 2012-11-9
 *
 */
public class CmpTreeXmlUtil {
	private static VaLogger logger = VaLogger.getLogger(CmpTreeXmlUtil.class);

	public static CmpTree xmlToCmpTree(Map<List, Map<String, byte[]>> mapData) {
		CmpTree cmpTree = new CmpTree();
		CmpTreeNode rootNode = new CmpTreeNode("cmpRoot", "", "");
		String tmpPath = FileUtil.makeTmpDir("mpm/cad/cmpview/");
		FileUtil.deleteFile(new File(tmpPath));
		for (Entry<List, Map<String, byte[]>> entry : mapData.entrySet()) {
			List list = entry.getKey();
			if (list == null || list.size() != 3) {
				logger.debug("list=====" + list);
				continue;
			}
			String name = (String) list.get(0);
			String dir = name.substring(0, name.lastIndexOf('.'));
			byte[] kbyte = (byte[]) list.get(1);
			String oid = (String) list.get(2);
			CmpTreeNode cmpTreeNode = new CmpTreeNode(dir, name, oid);
			rootNode.add(cmpTreeNode);
			String path = tmpPath + dir + "/";
			File fileDir = new File(path);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			FileUtil.writeBytes(path + name, kbyte);
//			for (Entry<String, byte[]> subEntry : entry.getValue().entrySet()) {
//				String key = subEntry.getKey();
//				byte[] bytes = subEntry.getValue();
//				CmpNode cmpNode = new CmpNode(key);
//				cmpTreeNode.add(cmpNode);
//				FileUtil.writeBytes(path + key + ".gif", bytes);
//			}
		}
		cmpTree.setRoot(rootNode);
		return cmpTree;
	}
}