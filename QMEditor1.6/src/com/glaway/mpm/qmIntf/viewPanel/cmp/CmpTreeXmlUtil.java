package com.glaway.mpm.qmIntf.viewPanel.cmp;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



import com.glaway.mpm.qmIntf.viewPanel.tech.TechTreeRootNode;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.view.NewTechnicsPart;

/**
 * @author ylshao
 * @Description: CmpTree
 * @date 2012-11-9
 * 
 */
public class CmpTreeXmlUtil {
	private static VaLogger logger = VaLogger.getLogger();
	
	public static CmpTree xmlToCmpTree(List<List<Object>> listData,
			Map<String, String> numbers) {
		CmpTree cmpTree = new CmpTree(numbers);
		TechTreeRootNode rootNode = new TechTreeRootNode("cmpRoot", null);
		String tmpPath = WorkSpaceUtil.makeCmpTmpDir();
		FileUtil.deleteFile(new File(tmpPath));
		for (List<Object> list : listData) {
			if (list != null && list.size() >= 4) {
				logger.debug("list=====" + list);
				String dir = (String) list.get(0);
				String name = dir.substring(0, dir.lastIndexOf('.'));
				byte[] kbyte = (byte[]) list.get(1);
				String oid = (String) list.get(2);
				// boolean isOriginal = (Boolean) list.get(3);
				String version;
				String modelName;
				// if (NewTechnicsPart.flag) {
				// version = "1.8";
				// modelName = "aaaa";
				// } else {
				version = (String) list.get(4);
				modelName = (String) list.get(5);
				// }

				CmpImageNode cmpTreeNode = new CmpImageNode(modelName, name,
						dir, version, oid);
				rootNode.add(cmpTreeNode);

				// if (NewTechnicsPart.flag) {
				// cmpTreeNode = new CmpImageNode("123",
				// "AL8_000_20131108_147" + "M" + "2b499d", dir,
				// version, "123");
				// rootNode.add(cmpTreeNode);
				// cmpTreeNode = new CmpImageNode("456",
				// "AL8_000_20131108_147" + "M" + "595423", dir,
				// version, "456");
				// rootNode.add(cmpTreeNode);
				// cmpTreeNode = new CmpImageNode("789",
				// "AL8_000_20131108_147" + "M" + "5ac597", dir,
				// version, "789");
				// rootNode.add(cmpTreeNode);
				// cmpTreeNode = new CmpImageNode("0", "AL8_000_20131108_147"
				// + "M" + "1b96e7", dir, version, "0");
				// rootNode.add(cmpTreeNode);
				// cmpTreeNode = new CmpImageNode("789",
				// "AL8_000_20131108_147" + "M" + "1d2c55", dir,
				// version, "789");
				// rootNode.add(cmpTreeNode);
				// cmpTreeNode = new CmpImageNode("02134",
				// "AL8_000_20131108_147" + "M" + "1dfa82", dir,
				// version, "0");
				// rootNode.add(cmpTreeNode);
				// }

				String path = tmpPath + name + "/";
				File fileDir = new File(path);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				FileUtil.writeBytes(path + dir, kbyte);
				// for (Entry<String, byte[]> subEntry :
				// entry.getValue().entrySet()) {
				// String key = subEntry.getKey();
				// byte[] bytes = subEntry.getValue();
				// CmpNode cmpNode = new CmpNode(key);
				// cmpTreeNode.add(cmpNode);
				// FileUtil.writeBytes(path + key + ".gif", bytes);
				// }
			}
		}

		cmpTree.setRoot(rootNode);
		return cmpTree;
	}
}