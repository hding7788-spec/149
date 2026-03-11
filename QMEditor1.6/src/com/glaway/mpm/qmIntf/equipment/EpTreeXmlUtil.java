package com.glaway.mpm.qmIntf.equipment;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.glaway.mpm.model.EpType;
import com.glaway.mpm.model.Equipment;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;

public class EpTreeXmlUtil {
	private static VaLogger logger = VaLogger.getLogger(EpTreeXmlUtil.class);

	public static EpTreeNode generateEpTreeFromHobby(EpTreeNode rootNode) {
		String epHobby = FileUtil.generateHobbyPath();
		BufferedReader br = FileUtil.getBufferedReaderByDirectPath(epHobby,
				"gbk");
		try {
			String line;
			while ((line = br.readLine()) != null) {
				EpTreeNode epTreeNode = new EpTreeNode(line);
				rootNode.add(epTreeNode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JavaUtil.closeStream(br);
		}
		return rootNode;
	}

	public static EpTree generateEpTree(EpType epType, EpInfoPanel epInfoPanel,NewTechnicsPart frame) {
		EpTreeNode root = new EpTreeNode("设备");
		if (epType != null) {
			parseNode(root, epType.getEpTypes());
			if(epType.getEquipments()!=null)
			for (Equipment equipment : epType.getEquipments()) {
				//logger.debug("equipment= " + equipment);
				EpNode node1 = new EpNode(equipment, true);
				root.add(node1);
			}
		}
		EpTree epTree = new EpTree(root, epInfoPanel, true,frame);
		return epTree;
	}

	public static EpTree generateEpTreeRoot(EpType epType, EpInfoPanel epInfoPanel,NewTechnicsPart frame) {
		EpTreeNode root = new EpTreeNode("设备树");

		EpTreeNode ep = new EpTreeNode("设备");
		EpTreeNode ep2 = new EpTreeNode("设备2");
		ep.add(ep2);
		root.add(ep);

		EpTree epTree = new EpTree(root, epInfoPanel, true,frame);
		return epTree;
	}

	public static void parseNode(EpTreeNode epTreeNode, List<EpType> epType) {
		if (epType != null && epType.size() != 0) {
			for (EpType type : epType) {
				List<EpType> eptype = type.getEpTypes();
				EpTreeNode node = new EpTreeNode(type.getName());
				epTreeNode.add(node);
				parseNode(node,eptype);
				if (type.getEquipments() != null) {
					for (Equipment equipment : type.getEquipments()) {
						//logger.debug("equipment= " + equipment);
						EpNode node1 = new EpNode(equipment, true);
						node.add(node1);
					}
					epTreeNode.add(node);
				}
			}
		}
	}
}