package com.glaway.mpm.qmIntf.participatePart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.glaway.mpm.qmIntf.viewPanel.cmp.CmpImageNode;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class VaClipboard {

	public static Vector<Map<String,String>> clipboard = new Vector<Map<String,String>>();
	public static Vector<VaTreeNode> clipboard2 = new Vector<VaTreeNode>();
	public static Map<String,List<VaTreeNode>> clipboard3 = new HashMap<String, List<VaTreeNode>>();
	public static Vector<CmpImageNode> clipboardCmpTreeNodes = new Vector<CmpImageNode>();

}
