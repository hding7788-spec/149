package com.glaway.mpm.qmIntf.dashboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.glaway.mpm.model.Dashboard;
import com.glaway.mpm.model.DashboardType;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;

public class SDashboardTreeXmlUtil {
	private static VaLogger logger = VaLogger.getLogger(SDashboardTreeXmlUtil.class);

	public static SDashboardTreeNode generateDashboardTreeFromHobby(SDashboardTreeNode rootNode) {
		String epHobby = FileUtil.generateHobbyPath();
		BufferedReader br = FileUtil.getBufferedReaderByDirectPath(epHobby, "gbk");
		try {
			String line;
			while ((line = br.readLine()) != null) {
				SDashboardTreeNode epTreeNode = new SDashboardTreeNode(line);
				rootNode.add(epTreeNode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JavaUtil.closeStream(br);
		}
		return rootNode;
	}

	public static SDashboardTree generateDashboardTree(DashboardType dashboardType, SDashboardInfoPanel dashboardInfoPanel,NewTechnicsPart frame) {
		SDashboardTreeNode root = new SDashboardTreeNode("标准仪器仪表");
		if (dashboardType != null) {
			parseNode(root, dashboardType.getDashboardTypes());
			if(dashboardType.getDashboards()!=null)
			for (Dashboard dashboard : dashboardType.getDashboards()) {
				SDashboardNode node1 = new SDashboardNode(dashboard, true);
				root.add(node1);
			}
		}
		SDashboardTree epTree = new SDashboardTree(root, dashboardInfoPanel, true,frame);
		return epTree;
	}

	public static SDashboardTree generateDashboardTreeRoot(DashboardType dashboardType, SDashboardInfoPanel dashboardInfoPanel,NewTechnicsPart frame) {
		SDashboardTreeNode root = new SDashboardTreeNode("标准仪器仪表树");

		SDashboardTreeNode dashboard = new SDashboardTreeNode("标准仪器仪表");
		SDashboardTreeNode dashboard2 = new SDashboardTreeNode("标准仪器仪表2");
		dashboard.add(dashboard2);
		root.add(dashboard);

		SDashboardTree epTree = new SDashboardTree(root, dashboardInfoPanel, true,frame);
		return epTree;
	}

	public static void parseNode(SDashboardTreeNode dashboardTreeNode, List<DashboardType> dashboardType) {
		if (dashboardType != null && dashboardType.size() != 0) {
			for (DashboardType type : dashboardType) {
				List<DashboardType> dashboardtype = type.getDashboardTypes();
				SDashboardTreeNode node = new SDashboardTreeNode(type.getName());
				if (type.getDashboards() != null) {
					for (Dashboard dashboard : type.getDashboards()) {
						SDashboardNode node1 = new SDashboardNode(dashboard, true);
						node.add(node1);
					}
				}
				dashboardTreeNode.add(node);
				parseNode(node,dashboardtype);
			}
		}
	}
}