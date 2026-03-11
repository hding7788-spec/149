package com.glaway.mpm.qmIntf.resourceTree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Observer;

import javax.swing.tree.TreePath;

import com.glaway.mpm.model.Frock;
import com.glaway.mpm.model.KnifeTool;
import com.glaway.mpm.model.MeasureTool;
import com.glaway.mpm.model.Tool;
import com.glaway.mpm.qmIntf.resourceTree.model.FkNode;
import com.glaway.mpm.qmIntf.resourceTree.model.KtNode;
import com.glaway.mpm.qmIntf.resourceTree.model.MstNode;
import com.glaway.mpm.qmIntf.resourceTree.model.ToolNode;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class ResourceTreeMouseAdapter extends MouseAdapter {
	private ResourceTree resourceTree;
	private CommonObservable observable;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public ResourceTreeMouseAdapter(ResourceTree resourceTree) {
		observable = new CommonObservable(2);
		this.resourceTree = resourceTree;
	}

	public void addObserver(Observer o) {
		observable.addObserver(o);
	}

	public void deleteObservers() {
		observable.deleteObservers();
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = resourceTree.getRowForLocation(x, y);
		TreePath path = resourceTree.getPathForRow(row);

		if (path != null) {
			Object value = path.getLastPathComponent();
			if (value instanceof FkNode) {
				FkNode node = (FkNode) value;
				Frock frock = node.getFrock();
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					observable.setChanged();
					Map<String, String> map = SwitchUtil.toolToHashMap(frock, "frock");
					map.put("toolType", "工装");
					logger.debug("frock map=" + map);
					observable.notifyObservers(map);
				} else if (e.getButton() == 3) {
					logger.debug("frock.getFrockCardNum()= " + frock.getFrockCardNum());
					if (frock.getFrockCardNum() != null && !"".equals(frock.getFrockCardNum().trim())) {
						ResourceTreeMouseAdapter.this.resourceTree.popupMenu
								.show(ResourceTreeMouseAdapter.this.resourceTree, e.getX(), e.getY());
					}
				}
			} else if (value instanceof ToolNode) {
				if (e.getClickCount() == 2) {
					ToolNode node = (ToolNode) value;
					Tool tool = node.getTool();
					observable.setChanged();
					Map<String, String> map = SwitchUtil.javaBeanToHashMap(tool);
					map.put("toolType", "工具");
					logger.debug("tool map=" + map);
					observable.notifyObservers(map);
				}
			} else if (value instanceof KtNode) {
				if (e.getClickCount() == 2) {
					KtNode node = (KtNode) value;
					KnifeTool knifeTool = node.getKnifeTool();
					observable.setChanged();
					Map<String, String> map = SwitchUtil.toolToHashMap(knifeTool, "knifeTool");
					map.put("toolType", "刀具");
					logger.debug("knife tool map=" + map);
					observable.notifyObservers(map);
				}
			} else if (value instanceof MstNode) {
				if (e.getClickCount() == 2) {
					MstNode node = (MstNode) value;
					MeasureTool measureTool = node.getMeasureTool();
					observable.setChanged();
					Map<String, String> map = SwitchUtil.toolToHashMap(measureTool, "measureTool");
					map.put("toolType", "量具");
					logger.debug("measure map=" + map);
					observable.notifyObservers(map);
				}
			}
		}
	}
}