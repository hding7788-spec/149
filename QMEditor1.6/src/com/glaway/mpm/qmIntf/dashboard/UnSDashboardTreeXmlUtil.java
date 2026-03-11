package com.glaway.mpm.qmIntf.dashboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.glaway.mpm.model.UnSDashboard;
import com.glaway.mpm.model.UnSDashboardType;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;

public class UnSDashboardTreeXmlUtil {
	private static VaLogger logger = VaLogger.getLogger(UnSDashboardTreeXmlUtil.class);

	public static UnSDashboardTreeNode generateDashboardTreeFromHobby(UnSDashboardTreeNode rootNode) {
		String epHobby = FileUtil.generateHobbyPath();
		BufferedReader br = FileUtil.getBufferedReaderByDirectPath(epHobby, "gbk");
		try {
			String line;
			while ((line = br.readLine()) != null) {
				UnSDashboardTreeNode epTreeNode = new UnSDashboardTreeNode(line);
				rootNode.add(epTreeNode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JavaUtil.closeStream(br);
		}
		return rootNode;
	}

	public static UnSDashboardTree generateUnSDashboardTree(UnSDashboardType dashboardType, UnSDashboardInfoPanel dashboardInfoPanel,NewTechnicsPart frame) {
		UnSDashboardTreeNode root = new UnSDashboardTreeNode("非标准仪器仪表");
		if (dashboardType != null) {
			parseNode(root, dashboardType.getDashboardTypes());
			if(dashboardType.getDashboards()!=null)
			for (UnSDashboard dashboard : dashboardType.getDashboards()) {
				UnSDashboardNode node1 = new UnSDashboardNode(dashboard, true);
				root.add(node1);
			}
		}
		UnSDashboardTree epTree = new UnSDashboardTree(root, dashboardInfoPanel, true,frame);
		return epTree;
	}

	public static UnSDashboardTree generateUnSDashboardTreeRoot(UnSDashboardType dashboardType, UnSDashboardInfoPanel dashboardInfoPanel,NewTechnicsPart frame) {
		UnSDashboardTreeNode root = new UnSDashboardTreeNode("非标准仪器仪表树");

		UnSDashboardTreeNode dashboard = new UnSDashboardTreeNode("非标准仪器仪表");
		UnSDashboardTreeNode dashboard2 = new UnSDashboardTreeNode("非标准仪器仪表2");
		dashboard.add(dashboard2);
		root.add(dashboard);

		UnSDashboardTree epTree = new UnSDashboardTree(root, dashboardInfoPanel, true,frame);
		return epTree;
	}

	public static void parseNode(UnSDashboardTreeNode dashboardTreeNode, List<UnSDashboardType> dashboardType) {
		if (dashboardType != null && dashboardType.size() != 0) {
			for (UnSDashboardType type : dashboardType) {
				List<UnSDashboardType> dashboardtype = type.getDashboardTypes();
				UnSDashboardTreeNode node = new UnSDashboardTreeNode(type.getName());
				if (type.getDashboards() != null) {
					for (UnSDashboard dashboard : type.getDashboards()) {
						UnSDashboardNode node1 = new UnSDashboardNode(dashboard, true);
						node.add(node1);
					}

				}
				dashboardTreeNode.add(node);
				parseNode(node,dashboardtype);
			}
		}
	}
}