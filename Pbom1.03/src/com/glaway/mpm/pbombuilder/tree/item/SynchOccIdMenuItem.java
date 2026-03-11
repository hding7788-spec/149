package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class SynchOccIdMenuItem extends CmMenuItem{

	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public SynchOccIdMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("刷新联动");
		setIconStr("ebom_update.gif");
		// setEnabled(canEdit?displayValidate(this.currNode):canEdit);
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {

		//只有顶层节点才能设置
		if(node.getParent() == node.getRoot()) {
			return true;
		}

		return false;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		Map<String, String> ebomOccidMap = new HashMap<String, String>();
		List<String> ebomNumberList = new ArrayList<String>();
		CmTree etree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
		List<CmTreeNode> ebomlist = CmCommonStringUtil.getBomNodeList(etree);
		CmLightPart cmLightPart;
		String partNumber;
		String parentNumber = "";
		String[] occStr;
		int count = 1;
		for (CmTreeNode cmTreeNode : ebomlist) {
			cmLightPart = cmTreeNode.getPart();
			partNumber = cmLightPart.getPartNumber();
			if(cmTreeNode.getParent() != null){
				parentNumber = ((CmTreeNode)cmTreeNode.getParent()).getPart().getPartNumber();
			}
			if(parentNumber == null || "".equals(parentNumber)){
				continue;
			}
			if(ebomNumberList.contains(parentNumber + "," + partNumber)){
				count++;
				ebomOccidMap.put(parentNumber + "," + partNumber + "@" + count, cmTreeNode.getOccId() + "&" + cmTreeNode.getOccpath());
			}else{
				count=1;
				ebomNumberList.add(parentNumber + "," + partNumber);
				ebomOccidMap.put(parentNumber + "," + partNumber + "@" + count, cmTreeNode.getOccId() + "&" + cmTreeNode.getOccpath());
			}
		}
		CmTree mtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
		List<CmTreeNode> mbomlist = CmCommonStringUtil.getBomNodeList(mtree);
		List<String> mbomNumberList = new ArrayList<String>();
		count=1;
		for (CmTreeNode cmTreeNode : mbomlist) {
			cmLightPart = cmTreeNode.getPart();
			partNumber = cmLightPart.getPartNumber();
			if(cmTreeNode.getParent() != null){
				parentNumber = ((CmTreeNode)cmTreeNode.getParent()).getPart().getPartNumber();
			}
			if(parentNumber == null || "".equals(parentNumber)){
				continue;
			}
			if(mbomNumberList.contains(parentNumber + "," + partNumber)){
				count++;
				if(ebomOccidMap.containsKey(parentNumber + "," + partNumber + "@" + count)){
					occStr = ebomOccidMap.get(parentNumber + "," + partNumber + "@" + count).split("&");
					cmTreeNode.setOccId(occStr[0]);
					cmTreeNode.setOccpath(occStr[1]);
				}
			}else{
				count=1;
				mbomNumberList.add(parentNumber + "," + partNumber);
				if(ebomOccidMap.containsKey(parentNumber + "," + partNumber + "@" + count)){
					occStr = ebomOccidMap.get(parentNumber + "," + partNumber + "@" + count).split("&");
					cmTreeNode.setOccId(occStr[0]);
					cmTreeNode.setOccpath(occStr[1]);
				}
			}
		}
		mtree.updateUI();
		JOptionPane.showMessageDialog(owner, "刷新成功!");
	}
}
