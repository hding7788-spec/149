package com.glaway.mpm.qmIntf.material;

import java.util.List;

import com.glaway.mpm.model.Material;
import com.glaway.mpm.model.MtType;
import com.glaway.mpm.view.NewTechnicsPart;

public class MtTreeXmlUtil {

	public static MtTree generateMtTree(MtType mtType, MtInfoPanel mtInfoPanel,NewTechnicsPart frame) {
		MtTreeNode root = new MtTreeNode("辅料", "辅料");
		if (mtType != null) {
			List<MtType> types = mtType.getMtTypes();
			if (types != null && types.size() != 0) {
				parseNode(root, types);
				if (types == null || types.size() == 0) {
					parseMaterial(root, mtType.getMaterials());
				}
			}
		}
		MtTree mtTree = new MtTree(root, mtInfoPanel,frame);
		return mtTree;
	}

	public static MtTree generateMtTreeRoot(MtType mtType, MtInfoPanel mtInfoPanel, NewTechnicsPart frame) {
		MtTreeNode root = new MtTreeNode("工艺辅料树", "工艺辅料树");

		MtTreeNode mt = new MtTreeNode("辅料", "辅料");
		MtTreeNode mt2 = new MtTreeNode("辅料2", "辅料2");
		mt.add(mt2);
		root.add(mt);

		MtTree mtTree = new MtTree(root, mtInfoPanel,frame);
		mtTree.setRootVisible(false);
		return mtTree;
	}

	public static void parseMaterial(MtTreeNode mtTreeNode, List<Material> materials) {
		if (materials != null) {
			for (Material temp : materials) {
				MtNode mtNode = new MtNode(temp);
				mtTreeNode.add(mtNode);
			}
		}
	}

	public static void parseNode(MtTreeNode mtTreeNode, List<MtType> mtType) {
		if (mtType != null && mtType.size() != 0) {
			for (MtType type : mtType) {
				MtTreeNode treeNode = new MtTreeNode(type.getName(), type.getTypePath());
				parseNode(treeNode, type.getMtTypes());
				mtTreeNode.add(treeNode);
			}
		} else {
			Material mt = new Material("mpm_material_temp_oid", "", "");
			mtTreeNode.add(new MtNode(mt));
		}
	}
}