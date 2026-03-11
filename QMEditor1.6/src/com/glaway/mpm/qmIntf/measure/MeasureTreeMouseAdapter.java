package com.glaway.mpm.qmIntf.measure;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Observer;

import javax.swing.tree.TreePath;

import com.glaway.mpm.model.Tool;
import com.glaway.mpm.qmIntf.resourceTree.model.ToolNode;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class MeasureTreeMouseAdapter extends MouseAdapter {
	private MeasureTree resourceTree;
	private CommonObservable observable;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public MeasureTreeMouseAdapter(MeasureTree resourceTree) {
		observable = new CommonObservable(7);
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
			if (value instanceof ToolNode) {
				if (e.getClickCount() == 2) {
					ToolNode node = (ToolNode) value;
					Tool tool = node.getTool();
					observable.setChanged();
					Map<String, String> map = SwitchUtil.javaBeanToHashMap(tool);
					map.put("toolType", "量具");
					logger.debug("tool map=" + map);
					observable.notifyObservers(map);
				}
			}
		}
	}
}